#!/bin/bash

if [ -z $WORKSPACE ]; then
  echo WORKSPACE is unset
  exit -1
fi

mvn clean install -Dvalidate-format -f api/pom.xml

# 2026-07-27 Make sure we have the Jakarta EE TCK bom - if this goes into Nexus we can move the cloning back to the post-build step where jakartaee-tck is used
#sed -i "s#<module>tck</module>##g" pom.xml
#git diff
git clone https://github.com/eclipse-ee4j/jakartaee-tck
cd $WORKSPACE/jakartaee-tck
if [ -n "$ghprbPullLongDescription" ] ; then
  PLATFORMTCK_PULL=
  set +e
  PLATFORMTCK_PULL=$(echo $ghprbPullLongDescription | grep -o https://github.com/jakartaee/platform-tck/pull/[0-9]* | grep -o [0-9]*)
  set -e
  if [ "$PLATFORMTCK_PULL" != ""  ]; then
    git fetch origin +refs/pull/*:refs/remotes/origin/pull/*
    git checkout origin/pull/$PLATFORMTCK_PULL/head
    #20260709 Make the rebase happy
	git config --global user.email "you@example.com"
	git config --global user.name "Your Name"
    git pull --rebase origin main
  fi
fi
# 2026-06-10 build the boms
cd $WORKSPACE/jakartaee-tck
mvn clean install -f bom/pom.xml
# 2026-08-10 build the connector dependencies
mvn clean install -f tcks/apis/connector-whitebox/pom.xml
mvn clean install -f tcks/apis/connector/pom.xml

## 2026-07-27 it has been noted that when Parsing POMs fails in Jenkins/Maven builds it doesn't fail the build so for now try to re-run the install to see if it can pass really
#cd ${WORKSPACE}
#mvn install -Dvalidate-format

# 2026-07-27 this is expected to be in the root pom before long
cd ${WORKSPACE}
echo Comparing `git branch` to $ghprbTargetBranch in the spec folder for spec changes to check
set +e
git diff --exit-code origin/$ghprbTargetBranch spec/
if [ $? -eq 1 ]; then
  set -e
  mvn clean install -f spec/pom.xml
else
  set -e
fi

# 2026-08-11 can't build the tck till we have the platform tck but can't build the platform tck without the API jar
# 2026-06-11 build the transactions TCK which needs the platform TCK bom
cd $WORKSPACE
mvn clean install -f tck/pom.xml

#Run WildFly against the TCK using it's runner
cd $WORKSPACE
git clone https://github.com/wildfly/wildfly-tck-runners.git
# 2026-08-11 add in the new test for the runner https://github.com/wildfly/wildfly-tck-runners/pull/285/changes
cd $WORKSPACE/wildfly-tck-runners/jakarta-ee-tck-runners/transactions-tck/transactions-tck-runner/
sed -i "s#<include>com/sun/ts/tests/jta/ee/usertransaction/\*\*/\*ServletTest\*.java</include>#<include>com/sun/ts/tests/jta/ee/usertransaction/\*\*/\*ServletTest\*.java</include>\n                                <include>com/sun/ts/tests/jta/ee/readonlyxa/ClientServletTest\*.java</include>#g" pom.xml
# 2026-06-10 To work with https://github.com/jakartaee/platform-tck/pull/2720/changes
cd $WORKSPACE/wildfly-tck-runners/jakarta-ee-tck-runners/transactions-tck/
mvn clean install -Pappclient -P'!download-tck'
failsafe="$WORKSPACE/wildfly-tck-runners/jakarta-ee-tck-runners/transactions-tck/transactions-tck-runner/target/failsafe-reports/failsafe-summary.xml"
completed=$(sed -n 's:.*<completed>\(.*\)</completed>.*:\1:p' "$failsafe")
errors=$(sed -n 's:.*<errors>\(.*\)</errors>.*:\1:p' "$failsafe")
failures=$(sed -n 's:.*<failures>\(.*\)</failures>.*:\1:p' "$failsafe")
echo "********************************************************************************" >> ${WORKSPACE}/summary.txt
echo "Completed running ${completed} tests."                     >> ${WORKSPACE}/summary.txt
echo "Number of Tests Failed   = ${failures}"                   >> ${WORKSPACE}/summary.txt
echo "Number of Tests with Errors = ${errors}"                   >> ${WORKSPACE}/summary.txt
echo "********************************************************************************" >> ${WORKSPACE}/summary.txt
if [ "${failures}" != "0" ];
then
 exit -1;
fi
if [ "${errors}" != "0" ];
then
 exit -1;
fi

cd ${WORKSPACE}/api
SNAPSHOT_VERSION=`mvn -B org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version 2> /dev/null | grep -E '^[0-9]+(\.[0-9]+)+-SNAPSHOT$'`
cd ${WORKSPACE}

#Run GlassFish against the TCK using it's runner
case $SNAPSHOT_VERSION in
  1.*)
    GLASSFISH_BRANCH=5.1.0-BRANCH
    TCK_VERSION=https://download.eclipse.org/ee4j/jakartaee-tck/jakartaee8/promoted/transactions-tck-1.3.0.zip
    #TCK_VERSION=https://download.eclipse.org/ee4j/jakartaee-tck/8.0.1/promoted/jtatck-1.3_latest.zip
    GLASSFISH_MAJOR=5
    #TS_JTE=https://raw.githubusercontent.com/eclipse-ee4j/jakartaee-tck/8.0.1/install/jta/bin/ts.jte
    OVERRIDE_TRANSACTION_API_PROPERTY=javax.transaction-api.version
    ;;
  2.0.0*)
    GLASSFISH_BRANCH=6.x
    TCK_VERSION=https://download.eclipse.org/jakartaee/transactions/2.0/jakarta-transactions-tck-2.0.0.zip
    #TCK_VERSION=https://download.eclipse.org/ee4j/jakartaee-tck/jakartaee9-eftl/staged-900/jakarta-transactions-tck-2.0.0.zip
    GLASSFISH_MAJOR=6
    #TS_JTE=https://raw.githubusercontent.com/eclipse-ee4j/jakartaee-tck/master/install/jta/bin/ts.jte
    OVERRIDE_TRANSACTION_API_PROPERTY=jakarta.transaction-api.version
    ;; 
  2.*)
    GLASSFISH_BRANCH=master
    TCK_VERSION=https://download.eclipse.org/ee4j/jakartaee-tck/jakartaee10/staged/eftl/jakarta-transactions-tck-2.0.2.zip
    GLASSFISH_MAJOR=7
    #TS_JTE=https://raw.githubusercontent.com/eclipse-ee4j/jakartaee-tck/master/install/jta/bin/ts.jte
    OVERRIDE_TRANSACTION_API_PROPERTY=jakarta.transaction-api.version
    ;;    
  *)
    echo "Unknown major version"
    exit -1
esac
# 20251722 https://ci.eclipse.org/glassfish/job/glassfish8_build-and-test_jdk21/configure it looks like 8.0 and not #git checkout master
git clone https://github.com/eclipse-ee4j/glassfish.git
cd glassfish
# 2025-06-04
git checkout 8.0.2
# 2026-0417 seems to be 8.0.0 now and not 8.0
#git checkout 8.0.0
MAVEN_OPTS="-Xmx2500m -Xss768k" mvn -B -e clean install -Pfastest,staging -T4C -Djakarta.transaction-api.version=$SNAPSHOT_VERSION -DskipTests
mv appserver/distributions/glassfish/target/glassfish.zip ..
# 20250721 https://ci.eclipse.org/jakartaee-tck/job/11/job/tck/job/jakarta-transactions-tck-glassfish/configure
cd ..
rm -rf glassfish8
unzip -q glassfish.zip
mvn install:install-file -Dfile=./glassfish.zip -DgroupId=org.glassfish.main.distributions -DartifactId=glassfish -Dversion=8.0.0-X -Dpackaging=zip
# 2026-06-10 build the boms
cd $WORKSPACE/jakartaee-tck/glassfish-runner/bom 
mvn clean install
MVN_STAGING='-Pstaging,snapshots'
PROFILE=full
#2026-06-10 change to accomodate main
TCK_VERSION=12.0.0-SNAPSHOT
#TCK_VERSION=11.0.1-SNAPSHOT
# Slight change from TCK as the path is not $WORKSPACE
cd $WORKSPACE/jakartaee-tck/glassfish-runner/transactions-tck
# 2026-06-10 To work with https://github.com/jakartaee/platform-tck/pull/2720/changes
mvn clean install -Dglassfish.version=8.0.0-X -P$PROFILE $MVN_STAGING -Pappclient -Dtck.test.transactions.version=$TCK_VERSION -Dtck.version=$TCK_VERSION -P '!download-tck'
#mvn clean install -Dglassfish.version=8.0.0-X -P$PROFILE $MVN_STAGING -Pappclient -Dtck.test.transactions.version=$TCK_VERSION -Dtck.version=$TCK_VERSION
# Slight change from TCK as the path is not just $WORKSPACE
failsafe="$WORKSPACE/jakartaee-tck/glassfish-runner/transactions-tck/transactions-tck-run/target/failsafe-reports/failsafe-summary.xml"
completed=$(sed -n 's:.*<completed>\(.*\)</completed>.*:\1:p' "$failsafe")
errors=$(sed -n 's:.*<errors>\(.*\)</errors>.*:\1:p' "$failsafe")
failures=$(sed -n 's:.*<failures>\(.*\)</failures>.*:\1:p' "$failsafe")
echo "********************************************************************************"  >>  ${WORKSPACE}/summary.txt
echo "Completed running ${completed} tests."                                          >>  ${WORKSPACE}/summary.txt
echo "Number of Tests Failed      = ${failures}"                                     >>  ${WORKSPACE}/summary.txt
echo "Number of Tests with Errors = ${errors}"                                      >>  ${WORKSPACE}/summary.txt
echo "********************************************************************************"  >>  ${WORKSPACE}/summary.txt
if [ "${failures}" != "0" ];
then
  exit -1;
fi
if [ "${errors}" != "0" ];
then
  exit -1;
fi


#git clone https://github.com/eclipse-ee4j/glassfish.git
#cd glassfish
#git checkout master

#master
# https://github.com/eclipse-ee4j/glassfish/blob/master/Jenkinsfile#L266
#mvn -s ./snapshots/settings.xml clean install -f ./snapshots/pom.xml
#mvn -U clean install -Pstaging,fastest -T2C -D$OVERRIDE_TRANSACTION_API_PROPERTY=$SNAPSHOT_VERSION -DskipTests
              
#cd ${WORKSPACE}
#mv glassfish/appserver/distributions/glassfish/target/glassfish.zip .
#rm -rf glassfish7
#unzip -q glassfish.zip

# Local testing ending up with gfnamejar being jakarta.transaction-api.3.3.jar which is strange as it doesn't happen on the main TCLK job
# GF_URL=http://central.maven.org/maven2/org/glassfish/main/distributions/glassfish/5.1.0/glassfish-5.1.0.zip
# wget -q ${GF_URL} -O glassfish.zip
# unzip -q glassfish.zip
# cd glassfish${GLASSFISH_MAJOR}/glassfish/modules  # updated to reflect glassfish${GLASSFISH_MAJOR} not 5 but not tested    
# for jarfile in api/target/jakarta.transaction-api-$SNAPSHOT_VERSION.jar; do 
#   	echo $(basename $jarfile) | sed -e 's/-RC[0-9][0-9]*//' | sed -e 's/-SNAPSHOT//' | sed -e 's/\\.[0-9][0-9]*//' | sed -e 's/\\.[0-9][0-9]*//' | sed -e 's/-[0-9][0-9]*//' | while IFS= read -r gfnamejar ; do rm -v $gfnamejar; cp -v $jarfile $gfnamejar; done;
# done
# cd -
#else
#  echo "Not building a glassfish as on branch $ghprbTargetBranch"
#fi

#wget -q $TCK_VERSION -O jtatck.zip
#TS_HOME=${WORKSPACE}/transactions-tck
#rm -rf $TS_HOME
#unzip -q ${WORKSPACE}/jtatck.zip

#rm -f ts.jte
#wget $TS_JTE

#HARNESS_REPORT_DIR="\${ts.home}/../JTreport"
#HARNESS_WORK_DIR="\${ts.home}/../JTwork"
# https://github.com/eclipse-ee4j/jakartaee-tck/blob/bf0ea3fbfcc1fbccdb60a295636f3deec5075a0e/docker/jtatck.sh
#sed -i "s#^webServerHome=.*#webServerHome=${WORKSPACE}/glassfish${GLASSFISH_MAJOR}/glassfish#g" ${TS_HOME}/bin/ts.jte
#sed -i "s#jta.classes=.*#jta.classes=\${jtaJarClasspath}\${pathsep}\${webServerHome}/lib/bootstrap/glassfish-jul-extension.jar\${pathsep}\\\#g" ${TS_HOME}/bin/ts.jte
#sed -i "s#jta.classes=.*#jta.classes=\${jtaJarClasspath}\${pathsep}\${webServerHome}/modules/internal-api.jar\${pathsep}\\\#g" ${TS_HOME}/bin/ts.jte
#sed -i "s#^jta.classes=${jtaJarClasspath}${pathsep}#jta.classes=\${jtaJarClasspath}\${pathsep}\${webServerHome}/lib/bootstrap/glassfish-jdk-extensions.jar\${pathsep}\${webServerHome}/lib/bootstrap/simple-glassfish-api.jar\${pathsep}\${webServerHome}/modules/acc-config.jar\${pathsep}\${webServerHome}/modules/admin-core.jar\${pathsep}\${webServerHome}/modules/admin-util.jar\${pathsep}\${webServerHome}/modules/amx-core.jar\${pathsep}\${webServerHome}/modules/amx-jakartaee.jar\${pathsep}\${webServerHome}/modules/angus-activation.jar\${pathsep}\${webServerHome}/modules/angus-mail.jar\${pathsep}\${webServerHome}/modules/annotation-framework.jar\${pathsep}\${webServerHome}/modules/ant.jar\${pathsep}\${webServerHome}/modules/antlr.jar\${pathsep}\${webServerHome}/modules/aopalliance-repackaged.jar\${pathsep}\${webServerHome}/modules/api-exporter.jar\${pathsep}\${webServerHome}/modules/appclient-connector.jar\${pathsep}\${webServerHome}/modules/appclient.security.jar\${pathsep}\${webServerHome}/modules/appclient-server-core.jar\${pathsep}\${webServerHome}/modules/asm-analysis.jar\${pathsep}\${webServerHome}/modules/asm-commons.jar\${pathsep}\${webServerHome}/modules/asm.jar\${pathsep}\${webServerHome}/modules/asm-tree.jar\${pathsep}\${webServerHome}/modules/asm-util.jar\${pathsep}\${webServerHome}/modules/backup.jar\${pathsep}\${webServerHome}/modules/cdi-api-fragment.jar\${pathsep}\${webServerHome}/modules/classmate.jar\${pathsep}\${webServerHome}/modules/class-model.jar\${pathsep}\${webServerHome}/modules/cluster-admin.jar\${pathsep}\${webServerHome}/modules/cluster-common.jar\${pathsep}\${webServerHome}/modules/cluster-ssh.jar\${pathsep}\${webServerHome}/modules/cmp-ejb-mapping.jar\${pathsep}\${webServerHome}/modules/cmp-enhancer.jar\${pathsep}\${webServerHome}/modules/cmp-generator-database.jar\${pathsep}\${webServerHome}/modules/cmp-internal-api.jar\${pathsep}\${webServerHome}/modules/cmp-model.jar\${pathsep}\${webServerHome}/modules/cmp-support-ejb.jar\${pathsep}\${webServerHome}/modules/cmp-support-sqlstore.jar\${pathsep}\${webServerHome}/modules/cmp-utility.jar\${pathsep}\${webServerHome}/modules/com.ibm.jbatch.container.jar\${pathsep}\${webServerHome}/modules/com.ibm.jbatch.spi.jar\${pathsep}\${webServerHome}/modules/command-logger.jar\${pathsep}\${webServerHome}/modules/commons-codec.jar\${pathsep}\${webServerHome}/modules/common-util.jar\${pathsep}\${webServerHome}/modules/concurrent-connector.jar\${pathsep}\${webServerHome}/modules/concurrent-impl.jar\${pathsep}\${webServerHome}/modules/config-api.jar\${pathsep}\${webServerHome}/modules/config-types.jar\${pathsep}\${webServerHome}/modules/connectors-admin.jar\${pathsep}\${webServerHome}/modules/connectors-inbound-runtime.jar\${pathsep}\${webServerHome}/modules/connectors-internal-api.jar\${pathsep}\${webServerHome}/modules/connectors-runtime.jar\${pathsep}\${webServerHome}/modules/console-cluster-plugin.jar\${pathsep}\${webServerHome}/modules/console-common-full-plugin.jar\${pathsep}\${webServerHome}/modules/console-common.jar\${pathsep}\${webServerHome}/modules/console-community-branding-plugin.jar\${pathsep}\${webServerHome}/modules/console-concurrent-plugin.jar\${pathsep}\${webServerHome}/modules/console-corba-plugin.jar\${pathsep}\${webServerHome}/modules/console-ejb-lite-plugin.jar\${pathsep}\${webServerHome}/modules/console-ejb-plugin.jar\${pathsep}\${webServerHome}/modules/console-jca-plugin.jar\${pathsep}\${webServerHome}/modules/console-jdbc-plugin.jar\${pathsep}\${webServerHome}/modules/console-jms-plugin.jar\${pathsep}\${webServerHome}/modules/console-jts-plugin.jar\${pathsep}\${webServerHome}/modules/console-plugin-service.jar\${pathsep}\${webServerHome}/modules/console-web-plugin.jar\${pathsep}\${webServerHome}/modules/container-common.jar\${pathsep}\${webServerHome}/modules/dataprovider.jar\${pathsep}\${webServerHome}/modules/dbschema.jar\${pathsep}\${webServerHome}/modules/deployment-admin.jar\${pathsep}\${webServerHome}/modules/deployment-autodeploy.jar\${pathsep}\${webServerHome}/modules/deployment-common.jar\${pathsep}\${webServerHome}/modules/deployment-jakartaee-core.jar\${pathsep}\${webServerHome}/modules/deployment-jakartaee-full.jar\${pathsep}\${webServerHome}/modules/dol.jar\${pathsep}\${webServerHome}/modules/ejb-client.jar\${pathsep}\${webServerHome}/modules/ejb-container.jar\${pathsep}\${webServerHome}/modules/ejb-full-container.jar\${pathsep}\${webServerHome}/modules/ejb-internal-api.jar\${pathsep}\${webServerHome}/modules/ejb.security.jar\${pathsep}\${webServerHome}/modules/entitybean-container.jar\${pathsep}\${webServerHome}/modules/epicyro.jar\${pathsep}\${webServerHome}/modules/exousia.jar\${pathsep}\${webServerHome}/modules/expressly.jar\${pathsep}\${webServerHome}/modules/flashlight-extra-jdk-packages.jar\${pathsep}\${webServerHome}/modules/flashlight-framework.jar\${pathsep}\${webServerHome}/modules/gf-admingui-connector.jar\${pathsep}\${webServerHome}/modules/gf-client-module.jar\${pathsep}\${webServerHome}/modules/gf-connectors-connector.jar\${pathsep}\${webServerHome}/modules/gf-ejb-connector.jar\${pathsep}\${webServerHome}/modules/gf-jms-connector.jar\${pathsep}\${webServerHome}/modules/gf-jms-injection.jar\${pathsep}\${webServerHome}/modules/gf-jpa-connector.jar\${pathsep}\${webServerHome}/modules/gf-load-balancer-connector.jar\${pathsep}\${webServerHome}/modules/gf-restadmin-connector.jar\${pathsep}\${webServerHome}/modules/gf-web-connector.jar\${pathsep}\${webServerHome}/modules/gf-weld-connector.jar\${pathsep}\${webServerHome}/modules/glassfish-api.jar\${pathsep}\${webServerHome}/modules/glassfish-batch-commands.jar\${pathsep}\${webServerHome}/modules/glassfish-batch-connector.jar\${pathsep}\${webServerHome}/modules/glassfish-corba-csiv2-idl.jar\${pathsep}\${webServerHome}/modules/glassfish-corba-internal-api.jar\${pathsep}\${webServerHome}/modules/glassfish-corba-omgapi.jar\${pathsep}\${webServerHome}/modules/glassfish-corba-orb.jar\${pathsep}\${webServerHome}/modules/glassfish-ee-api.jar\${pathsep}\${webServerHome}/modules/glassfish-extra-jre-packages.jar\${pathsep}\${webServerHome}/modules/glassfish-grizzly-extra-all.jar\${pathsep}\${webServerHome}/modules/glassfish-mbeanserver.jar\${pathsep}\${webServerHome}/modules/glassfish-naming.jar\${pathsep}\${webServerHome}/modules/glassfish-oracle-jdbc-driver-packages.jar\${pathsep}\${webServerHome}/modules/glassfish-osgi-bootstrap.jar\${pathsep}\${webServerHome}/modules/gmbal-api-only.jar\${pathsep}\${webServerHome}/modules/gmbal.jar\${pathsep}\${webServerHome}/modules/gms-adapter.jar\${pathsep}\${webServerHome}/modules/gms-bootstrap.jar\${pathsep}\${webServerHome}/modules/ha-api.jar\${pathsep}\${webServerHome}/modules/ha-file-store.jar\${pathsep}\${webServerHome}/modules/ha-shoal-cache-bootstrap.jar\${pathsep}\${webServerHome}/modules/ha-shoal-cache-store.jar\${pathsep}\${webServerHome}/modules/hibernate-validator-cdi.jar\${pathsep}\${webServerHome}/modules/hibernate-validator.jar\${pathsep}\${webServerHome}/modules/hk2-api.jar\${pathsep}\${webServerHome}/modules/hk2-config-generator.jar\${pathsep}\${webServerHome}/modules/hk2-core.jar\${pathsep}\${webServerHome}/modules/hk2-extras.jar\${pathsep}\${webServerHome}/modules/hk2.jar\${pathsep}\${webServerHome}/modules/hk2-locator.jar\${pathsep}\${webServerHome}/modules/hk2-runlevel.jar\${pathsep}\${webServerHome}/modules/hk2-utils.jar\${pathsep}\${webServerHome}/modules/internal-api.jar\${pathsep}\${webServerHome}/modules/jackson-annotations.jar\${pathsep}\${webServerHome}/modules/jackson-core.jar\${pathsep}\${webServerHome}/modules/jackson-databind.jar\${pathsep}\${webServerHome}/modules/jackson-module-jakarta-xmlbind-annotations.jar\${pathsep}\${webServerHome}/modules/jakarta.activation-api.jar\${pathsep}\${webServerHome}/modules/jakarta.annotation-api.jar\${pathsep}\${webServerHome}/modules/jakarta.authentication-api.jar\${pathsep}\${webServerHome}/modules/jakarta.authorization-api.jar\${pathsep}\${webServerHome}/modules/jakarta.batch-api.jar\${pathsep}\${webServerHome}/modules/jakartaee-kernel.jar\${pathsep}\${webServerHome}/modules/jakarta.ejb-api.jar\${pathsep}\${webServerHome}/modules/jakarta.el-api.jar\${pathsep}\${webServerHome}/modules/jakarta.enterprise.cdi-api.jar\${pathsep}\${webServerHome}/modules/jakarta.enterprise.concurrent-api.jar\${pathsep}\${webServerHome}/modules/jakarta.enterprise.concurrent.jar\${pathsep}\${webServerHome}/modules/jakarta.enterprise.lang-model.jar\${pathsep}\${webServerHome}/modules/jakarta.faces.jar\${pathsep}\${webServerHome}/modules/jakarta.inject-api.jar\${pathsep}\${webServerHome}/modules/jakarta.interceptor-api.jar\${pathsep}\${webServerHome}/modules/jakarta.jms-api.jar\${pathsep}\${webServerHome}/modules/jakarta.json-api.jar\${pathsep}\${webServerHome}/modules/jakarta.json.bind-api.jar\${pathsep}\${webServerHome}/modules/jakarta.mail-api.jar\${pathsep}\${webServerHome}/modules/jakarta.mvc-api.jar\${pathsep}\${webServerHome}/modules/jakarta.persistence-api.jar\${pathsep}\${webServerHome}/modules/jakarta.resource-api.jar\${pathsep}\${webServerHome}/modules/jakarta.security.enterprise-api.jar\${pathsep}\${webServerHome}/modules/jakarta.security.enterprise.jar\${pathsep}\${webServerHome}/modules/jakarta.servlet-api.jar\${pathsep}\${webServerHome}/modules/jakarta.servlet.jsp-api.jar\${pathsep}\${webServerHome}/modules/jakarta.servlet.jsp.jstl-api.jar\${pathsep}\${webServerHome}/modules/jakarta.transaction-api.jar\${pathsep}\${webServerHome}/modules/jakarta.validation-api.jar\${pathsep}\${webServerHome}/modules/jakarta.websocket-api.jar\${pathsep}\${webServerHome}/modules/jakarta.websocket-client-api.jar\${pathsep}\${webServerHome}/modules/jakarta.ws.rs-api.jar\${pathsep}\${webServerHome}/modules/jakarta.xml.bind-api.jar\${pathsep}\${webServerHome}/modules/javassist.jar\${pathsep}\${webServerHome}/modules/jaxb-osgi.jar\${pathsep}\${webServerHome}/modules/jboss-classfilewriter.jar\${pathsep}\${webServerHome}/modules/jboss-logging.jar\${pathsep}\${webServerHome}/modules/jcip-annotations.jar\${pathsep}\${webServerHome}/modules/jdbc-admin.jar\${pathsep}\${webServerHome}/modules/jdbc-config.jar\${pathsep}\${webServerHome}/modules/jdbc-runtime.jar\${pathsep}\${webServerHome}/modules/jersey-bean-validation.jar\${pathsep}\${webServerHome}/modules/jersey-cdi1x.jar\${pathsep}\${webServerHome}/modules/jersey-cdi1x-servlet.jar\${pathsep}\${webServerHome}/modules/jersey-cdi1x-transaction.jar\${pathsep}\${webServerHome}/modules/jersey-client.jar\${pathsep}\${webServerHome}/modules/jersey-common.jar\${pathsep}\${webServerHome}/modules/jersey-container-grizzly2-http.jar\${pathsep}\${webServerHome}/modules/jersey-container-servlet-core.jar\${pathsep}\${webServerHome}/modules/jersey-container-servlet.jar\${pathsep}\${webServerHome}/modules/jersey-ejb-component-provider.jar\${pathsep}\${webServerHome}/modules/jersey-entity-filtering.jar\${pathsep}\${webServerHome}/modules/jersey-hk2.jar\${pathsep}\${webServerHome}/modules/jersey-media-jaxb.jar\${pathsep}\${webServerHome}/modules/jersey-media-json-binding.jar\${pathsep}\${webServerHome}/modules/jersey-media-json-jackson.jar\${pathsep}\${webServerHome}/modules/jersey-media-json-jettison.jar\${pathsep}\${webServerHome}/modules/jersey-media-json-processing.jar\${pathsep}\${webServerHome}/modules/jersey-media-multipart.jar\${pathsep}\${webServerHome}/modules/jersey-media-sse.jar\${pathsep}\${webServerHome}/modules/jersey-mp-rest-client.jar\${pathsep}\${webServerHome}/modules/jersey-mvc-connector.jar\${pathsep}\${webServerHome}/modules/jersey-mvc.jar\${pathsep}\${webServerHome}/modules/jersey-mvc-jsp.jar\${pathsep}\${webServerHome}/modules/jersey-proxy-client.jar\${pathsep}\${webServerHome}/modules/jersey-server.jar\${pathsep}\${webServerHome}/modules/jettison.jar\${pathsep}\${webServerHome}/modules/jms-admin.jar\${pathsep}\${webServerHome}/modules/jms-core.jar\${pathsep}\${webServerHome}/modules/jpa-container.jar\${pathsep}\${webServerHome}/modules/jsch.jar\${pathsep}\${webServerHome}/modules/jsf-connector.jar\${pathsep}\${webServerHome}/modules/jsftemplating.jar\${pathsep}\${webServerHome}/modules/jspcaching-connector.jar\${pathsep}\${webServerHome}/modules/jsr109-impl.jar\${pathsep}\${webServerHome}/modules/jstl-connector.jar\${pathsep}\${webServerHome}/modules/jta.jar\${pathsep}\${webServerHome}/modules/jts.jar\${pathsep}\${webServerHome}/modules/kernel.jar\${pathsep}\${webServerHome}/modules/krazo-core.jar\${pathsep}\${webServerHome}/modules/krazo-jersey.jar\${pathsep}\${webServerHome}/modules/launcher.jar\${pathsep}\${webServerHome}/modules/libpam4j.jar\${pathsep}\${webServerHome}/modules/load-balancer-admin.jar\${pathsep}\${webServerHome}/modules/logging.jar\${pathsep}\${webServerHome}/modules/mail-connector.jar\${pathsep}\${webServerHome}/modules/mail-runtime.jar\${pathsep}\${webServerHome}/modules/management-api.jar\${pathsep}\${webServerHome}/modules/metro-glue.jar\${pathsep}\${webServerHome}/modules/microprofile-config-api.jar\${pathsep}\${webServerHome}/modules/microprofile-config.jar\${pathsep}\${webServerHome}/modules/microprofile-connectors.jar\${pathsep}\${webServerHome}/modules/microprofile-jwt-auth-api.jar\${pathsep}\${webServerHome}/modules/microprofile-jwt-auth.jar\${pathsep}\${webServerHome}/modules/microprofile-rest-client-api.jar\${pathsep}\${webServerHome}/modules/mimepull.jar\${pathsep}\${webServerHome}/modules/monitoring-core.jar\${pathsep}\${webServerHome}/modules/nimbus-jose-jwt.jar\${pathsep}\${webServerHome}/modules/nucleus-grizzly-all.jar\${pathsep}\${webServerHome}/modules/nucleus-resources.jar\${pathsep}\${webServerHome}/modules/orb-connector.jar\${pathsep}\${webServerHome}/modules/orb-enabler.jar\${pathsep}\${webServerHome}/modules/orb-iiop.jar\${pathsep}\${webServerHome}/modules/org.apache.aries.spifly.dynamic.bundle.jar\${pathsep}\${webServerHome}/modules/org.apache.felix.bundlerepository.jar\${pathsep}\${webServerHome}/modules/org.eclipse.persistence.asm.jar\${pathsep}\${webServerHome}/modules/org.eclipse.persistence.core.jar\${pathsep}\${webServerHome}/modules/org.eclipse.persistence.dbws.jar\${pathsep}\${webServerHome}/modules/org.eclipse.persistence.jpa.jar\${pathsep}\${webServerHome}/modules/org.eclipse.persistence.jpa.jpql.jar\${pathsep}\${webServerHome}/modules/org.eclipse.persistence.jpa.modelgen.processor.jar\${pathsep}\${webServerHome}/modules/org.eclipse.persistence.oracle.jar\${pathsep}\${webServerHome}/modules/osgi-adapter.jar\${pathsep}\${webServerHome}/modules/osgi-cli-remote.jar\${pathsep}\${webServerHome}/modules/osgi-container.jar\${pathsep}\${webServerHome}/modules/osgi-resource-locator.jar\${pathsep}\${webServerHome}/modules/parsson.jar\${pathsep}\${webServerHome}/modules/parsson-media.jar\${pathsep}\${webServerHome}/modules/persistence-common.jar\${pathsep}\${webServerHome}/modules/pfl-basic.jar\${pathsep}\${webServerHome}/modules/pfl-basic-tools.jar\${pathsep}\${webServerHome}/modules/pfl-dynamic.jar\${pathsep}\${webServerHome}/modules/pfl-tf.jar\${pathsep}\${webServerHome}/modules/pfl-tf-tools.jar\${pathsep}\${webServerHome}/modules/reactive-streams.jar\${pathsep}\${webServerHome}/modules/resources-connector.jar\${pathsep}\${webServerHome}/modules/resources-runtime.jar\${pathsep}\${webServerHome}/modules/rest-client.jar\${pathsep}\${webServerHome}/modules/rest-service.jar\${pathsep}\${webServerHome}/modules/rmic.jar\${pathsep}\${webServerHome}/modules/scattered-archive-api.jar\${pathsep}\${webServerHome}/modules/schema2beans.jar\${pathsep}\${webServerHome}/modules/security-ee.jar\${pathsep}\${webServerHome}/modules/security.jar\${pathsep}\${webServerHome}/modules/security-services.jar\${pathsep}\${webServerHome}/modules/shoal-cache.jar\${pathsep}\${webServerHome}/modules/shoal-gms-api.jar\${pathsep}\${webServerHome}/modules/shoal-gms-impl.jar\${pathsep}\${webServerHome}/modules/soap-tcp.jar\${pathsep}\${webServerHome}/modules/soteria.spi.bean.decorator.weld.jar\${pathsep}\${webServerHome}/modules/ssl-impl.jar\${pathsep}\${webServerHome}/modules/stats77.jar\${pathsep}\${webServerHome}/modules/stax2-api.jar\${pathsep}\${webServerHome}/modules/transaction-internal-api.jar\${pathsep}\${webServerHome}/modules/tyrus-client.jar\${pathsep}\${webServerHome}/modules/tyrus-container-glassfish-cdi.jar\${pathsep}\${webServerHome}/modules/tyrus-container-glassfish-ejb.jar\${pathsep}\${webServerHome}/modules/tyrus-container-grizzly-client.jar\${pathsep}\${webServerHome}/modules/tyrus-container-servlet.jar\${pathsep}\${webServerHome}/modules/tyrus-core.jar\${pathsep}\${webServerHome}/modules/tyrus-server.jar\${pathsep}\${webServerHome}/modules/tyrus-spi.jar\${pathsep}\${webServerHome}/modules/war-util.jar\${pathsep}\${webServerHome}/modules/wasp.jar\${pathsep}\${webServerHome}/modules/web-cli.jar\${pathsep}\${webServerHome}/modules/web-core.jar\${pathsep}\${webServerHome}/modules/web-embed-api.jar\${pathsep}\${webServerHome}/modules/web-glue.jar\${pathsep}\${webServerHome}/modules/web-gui-plugin-common.jar\${pathsep}\${webServerHome}/modules/web-ha.jar\${pathsep}\${webServerHome}/modules/web-naming.jar\${pathsep}\${webServerHome}/modules/websecurity.jar\${pathsep}\${webServerHome}/modules/webservices-api-osgi.jar\${pathsep}\${webServerHome}/modules/webservices-connector.jar\${pathsep}\${webServerHome}/modules/webservices-extra-jdk-packages.jar\${pathsep}\${webServerHome}/modules/webservices-osgi.jar\${pathsep}\${webServerHome}/modules/webservices.security.jar\${pathsep}\${webServerHome}/modules/web-sse.jar\${pathsep}\${webServerHome}/modules/weld-integration-fragment.jar\${pathsep}\${webServerHome}/modules/weld-integration.jar\${pathsep}\${webServerHome}/modules/weld-osgi-bundle.jar\${pathsep}\${webServerHome}/modules/woodstox-core.jar\${pathsep}\${webServerHome}/modules/work-management.jar\${pathsep}\${webServerHome}/modules/xmlsec.jar\${pathsep}\${webServerHome}/modules/yasson.jar\${pathsep}#g" ${TS_HOME}/bin/ts.jte
#sed -i "s/^report\\.dir=\\/tmp\\/JTreport/$(echo report\\.dir=${HARNESS_REPORT_DIR} | sed -e 's/\\\\/\\\\\\\\/g; s/\\//\\\\\\//g;')/g" ${TS_HOME}/bin/ts.jte
#sed -i "s/^work\\.dir=\\/tmp\\/JTwork/$(echo work\\.dir=${HARNESS_WORK_DIR} | sed -e 's/\\\\/\\\\\\\\/g; s/\\//\\\\\\//g;')/g" ${TS_HOME}/bin/ts.jte
#yes | cp -rfv ${WORKSPACE}/ts.jte ${TS_HOME}/bin/ts.jte
#cd ${TS_HOME}/bin
#ant config.vi
#cd ${TS_HOME}/src/com/sun/ts/tests/
#ant deploy
#ant runclient | tee run.log

#cat run.log | sed -e '1,/Completed running/d' > summary.txt
#PASSED_COUNT=`head -1 summary.txt | tail -1 | sed 's/.*=\\s\\(.*\\)/\\1/'`
#FAILED_COUNT=`head -2 summary.txt | tail -1 | sed 's/.*=\\s\\(.*\\)/\\1/'`
#ERROR_COUNT=`head -3 summary.txt | tail -1 | sed 's/.*=\\s\\(.*\\)/\\1/'`
#echo ERROR_COUNT=${ERROR_COUNT}
#echo FAILED_COUNT=${FAILED_COUNT}
#echo PASSED_COUNT=${PASSED_COUNT}
#if [ "$ERROR_COUNT" != "0" ];
#then
#  exit -1;
#fi

#if [ "$FAILED_COUNT" != "0" ];
#then
#  exit -1;
#fi
