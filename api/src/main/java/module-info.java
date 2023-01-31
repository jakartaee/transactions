module jakarta.transaction {
    requires java.rmi;
    requires transitive java.transaction.xa;
    requires static transitive jakarta.cdi;
    requires static jakarta.interceptor;

    exports jakarta.transaction;
}