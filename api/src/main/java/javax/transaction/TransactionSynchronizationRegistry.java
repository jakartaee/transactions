/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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

package javax.transaction;

/**
 * This interface is intended for use by system level application server
 * components such as persistence managers, resource adapters, as well as
 * Jakarta Enterprise Beans and Web application components. This provides the ability to
 * register synchronization objects with special ordering semantics,
 * associate resource objects with the current transaction, get the
 * transaction context of the current transaction, get current transaction
 * status, and mark the current transaction for rollback.
 *
 * This interface is implemented by the application server by a
 * stateless service object. The same object can be used by any number of
 * components with thread safety.
 *
 * <P>In standard application server environments, an instance
 * implementing this interface can be looked up by a standard name via JNDI.
 * The standard name is java:comp/TransactionSynchronizationRegistry.
 *
 * @since 1.1
 */
public interface TransactionSynchronizationRegistry {

    /**
     * Return an opaque object to represent the transaction bound to the
     * current thread at the time this method is called. This object
     * overrides hashCode and equals to allow its use as the key in a
     * hashMap for use by the caller. If there is no transaction currently
     * active, return null.
     * 
     * <P>This object will return the same hashCode and compare equal to
     * all other objects returned by calling this method
     * from any component executing in the same transaction context in the
     * same application server.
     * 
     * <P>The toString method returns a String that might be usable by a
     * human reader to usefully understand the transaction context. The
     * toString result is otherwise not defined. Specifically, there is no
     * forward or backward compatibility guarantee of the results of
     * toString.
     * 
     * <P>The object is not necessarily serializable, and has no defined
     * behavior outside the virtual machine whence it was obtained.
     *
     * @return an opaque object representing the transaction bound to the 
     * current thread at the time this method is called.
     *
     * @since 1.1
     */
    Object getTransactionKey();
    
    /**
     * Add or replace an object in the Map of resources being managed for
     * the transaction bound to the current thread at the time this 
     * method is called. The supplied key should be of an caller-
     * defined class so as not to conflict with other users. The class
     * of the key must guarantee that the hashCode and equals methods are
     * suitable for use as keys in a map. The key and value are not examined
     * or used by the implementation. The general contract of this method
     * is that of {@link java.util.Map#put(Object, Object)} for a Map that
     * supports non-null keys and null values. For example, 
     * if there is already an value associated with the key, it is replaced 
     * by the value parameter. 
     *
     * @param key the key for the Map entry.
     * @param value the value for the Map entry.
     * @exception IllegalStateException if no transaction is active.
     * @exception NullPointerException if the parameter key is null.
     *
     * @since 1.1
     */
    void putResource(Object key, Object value);
    
    /**
     * Get an object from the Map of resources being managed for
     * the transaction bound to the current thread at the time this 
     * method is called. The key should have been supplied earlier
     * by a call to putResouce in the same transaction. If the key 
     * cannot be found in the current resource Map, null is returned.
     * The general contract of this method
     * is that of {@link java.util.Map#get(Object)} for a Map that
     * supports non-null keys and null values. For example, 
     * the returned value is null if there is no entry for the parameter
     * key or if the value associated with the key is actually null.
     *
     * @param key the key for the Map entry.
     * @return the value associated with the key.
     * @exception IllegalStateException if no transaction is active.
     * @exception NullPointerException if the parameter key is null.
     *
     * @since 1.1
     */
    Object getResource(Object key);

    /**
     * Register a Synchronization instance with special ordering
     * semantics. Its beforeCompletion will be called after all 
     * SessionSynchronization beforeCompletion callbacks and callbacks 
     * registered directly with the Transaction, but before the 2-phase
     * commit process starts. Similarly, the afterCompletion
     * callback will be called after 2-phase commit completes but before
     * any SessionSynchronization and Transaction afterCompletion callbacks.
     * 
     * <P>The beforeCompletion callback will be invoked in the transaction
     * context of the transaction bound to the current thread at the time 
     * this method is called. 
     * Allowable methods include access to resources,
     * e.g. Connectors. No access is allowed to "user components" (e.g. timer
     * services or bean methods), as these might change the state of data
     * being managed by the caller, and might change the state of data that
     * has already been flushed by another caller of 
     * registerInterposedSynchronization. 
     * The general context is the component
     * context of the caller of registerInterposedSynchronization.
     * 
     * <P>The afterCompletion callback will be invoked in an undefined 
     * context. No access is permitted to "user components"
     * as defined above. Resources can be closed but no transactional
     * work can be performed with them.
     * 
     * <P>If this method is invoked without an active transaction context, an
     * IllegalStateException is thrown.
     *
     * <P>If this method is invoked after the two-phase commit processing has
     * started, an IllegalStateException is thrown.
     * 
     * @param sync the Synchronization instance.
     * @exception IllegalStateException if no transaction is active.
     *
     * @since 1.1
     */
    void registerInterposedSynchronization(Synchronization sync);

    /**
     * Return the status of the transaction bound to the
     * current thread at the time this method is called.
     * This is the result of executing TransactionManager.getStatus() in
     * the context of the transaction bound to the current thread at the time
     * this method is called.
     *
     * @return the status of the transaction bound to the current thread 
     * at the time this method is called.
     *
     * @since 1.1
     */
    int getTransactionStatus();

    /**
     * Set the rollbackOnly status of the transaction bound to the
     * current thread at the time this method is called.
     *
     * @exception IllegalStateException if no transaction is active.
     *
     * @since 1.1
     */
    void setRollbackOnly();

    /**
     * Get the rollbackOnly status of the transaction bound to the
     * current thread at the time this method is called.
     *
     * @return the rollbackOnly status.
     * @exception IllegalStateException if no transaction is active.
     *
     * @since 1.1
     */
    boolean getRollbackOnly();
}
