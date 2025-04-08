module jakarta.transaction {
    requires java.rmi;
    requires transitive java.transaction.xa;
    requires transitive jakarta.cdi;
    requires jakarta.interceptor;

    exports jakarta.transaction;
    exports jakarta.transaction.xa;
}