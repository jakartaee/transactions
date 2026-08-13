/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.sun.ts.tests.jta.ee.readonlyxa;

import java.net.URL;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import com.sun.ts.tests.common.base.ServiceEETest;
import tck.arquillian.porting.lib.spi.TestArchiveProcessor;
import tck.arquillian.protocol.common.TargetVehicle;

@ExtendWith(ArquillianExtension.class)
@Tag("platform")
@Tag("connector_resourcedef_servlet_optional")
@Tag("tck-javatest")

@TestMethodOrder(MethodOrderer.MethodName.class)
public class ClientServletTest extends Client {
    @TargetsContainer("tck-javatest")
    @OverProtocol("javatest")
    @Deployment(name = "servlet_resourcedefs", order = 2)
    public static EnterpriseArchive createDeployment(@ArquillianResource TestArchiveProcessor archiveProcessor) {
        // War
        // the war with the correct archive name
        WebArchive servlet_resourcedefs_web = ShrinkWrap.create( WebArchive.class, "servlet_resourcedefs_web.war");
        // The class files: the readonlyxa tests plus the EJBLite servlet
        // vehicle machinery, so the test logic executes in-server (CDI
        // injection, JNDI and the Transactional interceptor are available).
        // Whitebox classes are deliberately NOT packaged here: the war links
        // to the rar module's copies so the Client's ConnectorStatus toggle
        // reaches the same statics the resource adapter reads.
        servlet_resourcedefs_web.addClasses(
                com.sun.ts.tests.connector.resourceDefs.servlet.Client.class,
                com.sun.ts.tests.common.vehicle.VehicleRunnerFactory.class,
                com.sun.ts.tests.common.vehicle.VehicleRunnable.class,
                com.sun.ts.tests.common.vehicle.VehicleClient.class,
                com.sun.ts.tests.common.vehicle.ejbliteshare.EJBLiteClientIF.class,
                com.sun.ts.tests.common.vehicle.ejbliteshare.ReasonableStatus.class,
                com.sun.ts.tests.ejb30.common.lite.NumberEnum.class,
                com.sun.ts.tests.ejb30.common.helper.Helper.class,
                com.sun.ts.tests.ejb30.common.lite.EJBLiteClientBase.class,
                com.sun.ts.tests.ejb30.common.lite.NumberIF.class,
                com.sun.ts.lib.harness.Fault.class,
                com.sun.ts.tests.common.base.EETest.class,
                com.sun.ts.lib.harness.SetupException.class,
                ServiceEETest.class,
                com.sun.ts.tests.jta.ee.transactional.Helper.class,
                ReadOnlyTestBean.class,
                EJBLiteServletVehicle.class,
                HttpServletDelegate.class,
                Client.class,
                ClientServletTest.class
        );
        // The web.xml descriptor
        URL warResURL = ClientServletTest.class.getResource( "servlet_resourcedefs_web.xml");
        servlet_resourcedefs_web.addAsWebInfResource(warResURL, "web.xml");
        // The sun-web.xml descriptor
        warResURL = ClientServletTest.class.getResource( "servlet_resourcedefs_web.war.sun-web.xml");
        servlet_resourcedefs_web.addAsWebInfResource(warResURL, "sun-web.xml");
        // beans.xml with discovery-mode=all so ReadOnlyTestBean is a CDI bean
        warResURL = ClientServletTest.class.getResource("/vehicle/ejbliteservlet/beans.xml");
        if (warResURL != null) {
            servlet_resourcedefs_web.addAsWebInfResource(warResURL, "beans.xml");
        }

        // Call the archive processor
        archiveProcessor.processWebArchive( servlet_resourcedefs_web, ClientServletTest.class, warResURL);

        // RAR
        // the rar with the correct archive name
        JavaArchive conn_resourcedefs_jar = ShrinkWrap.create(JavaArchive.class, "resouredef.jar");
        // The class files: the complete adapter1/whitebox/util packages. The
        // adapter's @ConnectionDefinition references TSEISDataSource and
        // TSEISConnection, whose implementations pull in the rest of the
        // whitebox resource manager; a partial class list fails annotation
        // scanning at deployment (ClassNotFoundException). Only
        // CRDResourceAdapterImpl carries @Connector, so packaging the whole
        // whitebox package adds no competing connector annotations.
        conn_resourcedefs_jar.addPackages(false,
                "com.sun.ts.tests.common.connector.embedded.adapter1",
                "com.sun.ts.tests.common.connector.util",
                "com.sun.ts.tests.common.connector.whitebox"
        );
        JavaArchive conn_resourcedefs_rar = ShrinkWrap.create(JavaArchive.class, "whitebox-rd.rar");
        conn_resourcedefs_rar.add(conn_resourcedefs_jar, "/", ZipExporter.class);
        // The whitebox-xa deployment descriptor (metadata-complete): the CRD
        // adapter's outbound connections do not support XA (its
        // createManagedConnection builds TSManagedConnection with
        // supportsXA=false), so the read-only XA handshake can never happen
        // through it. The whitebox-xa connection definition activates
        // XAManagedConnectionFactory instead, whose managed connections
        // expose the ExtendedXAResource-implementing XAResourceImpl.
        conn_resourcedefs_rar.addAsManifestResource(
                ClientServletTest.class.getResource("whitebox-xa-ra.xml"), "ra.xml");

        // Ear
        EnterpriseArchive servlet_resourcedefs_ear = ShrinkWrap.create(EnterpriseArchive.class, "servlet_resourcedefs.ear");

        // Any libraries added to the ear

        // The component jars built by the package target
        servlet_resourcedefs_ear.addAsModule(servlet_resourcedefs_web);
        servlet_resourcedefs_ear.addAsModule(conn_resourcedefs_rar);




        // The application.xml descriptor: pins the war's context-root to the
        // ear base name, which the EJBLite web vehicle client uses as the
        // request URL
        URL earResURL = ClientServletTest.class.getResource( "servlet_resourcedefs_application.xml");
        servlet_resourcedefs_ear.addAsManifestResource(earResURL, "application.xml");
        // The sun-application.xml descriptor
        earResURL = ClientServletTest.class.getResource( "servlet_resourcedefs.ear.sun-application.xml");
        servlet_resourcedefs_ear.addAsManifestResource(earResURL, "sun-application.xml");
        // Call the archive processor
        archiveProcessor.processEarArchive( servlet_resourcedefs_ear, ClientServletTest.class, earResURL);
        return servlet_resourcedefs_ear;
    }

    @Test
    @Override
    @TargetVehicle("ejbliteservlet")
    public void testInsertWithReadOnlyXAResource() throws Exception {
        super.testInsertWithReadOnlyXAResource();
    }

    @Test
    @Override
    @TargetVehicle("ejbliteservlet")
    public void testInsertWithNonReadOnlyXAResource() throws Exception {
        super.testInsertWithNonReadOnlyXAResource();
    }
}