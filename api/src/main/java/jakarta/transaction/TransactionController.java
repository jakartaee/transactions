/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
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

package jakarta.transaction;

/**
 * Declares methods allowing an application program to explicitly manage
 * transaction boundaries.
 *
 * @since Jakarta Transactions 2.1
 */
public interface TransactionController {

    /**
     * Create a new transaction and associate it with the current thread.
     *
     * @exception IllegalStateException Thrown if the thread is already
     *    associated with a transaction and the {@link TransactionManager}
     *    implementation does not support nested transactions, or if the
     *    transaction manager encounters an unexpected error condition.
     *
     */
    void begin();

    /**
     * Complete the transaction associated with the current thread. When this
     * method completes, the thread is no longer associated with a transaction.
     *
     * @exception SecurityException Thrown to indicate that the thread is
     *    not allowed to commit the transaction.
     *
     * @exception IllegalStateException Thrown if the current thread is not
     *    associated with a transaction, if the transaction has been rolled
     *    back rather than committed, if a heuristic decision was made and
     *    at least some relevant updates have been rolled back, or if the
     *    transaction manager encounters an unexpected error condition. In
     *    the case of a rollback or heuristic decision, the thrown
     *    {@link IllegalStateException} must contain a wrapped
     *    {@linkplain Throwable#getCause cause} of type
     *    {@link RollbackException}, {@link HeuristicMixedException}, or
     *    {@link HeuristicRollbackException}, as appropriate.
     *
    */
    void commit();

    /**
     * Roll back the transaction associated with the current thread. When this
     * method completes, the thread is no longer associated with a transaction.
     *
     * @exception SecurityException Thrown to indicate that the thread is
     *    not allowed to roll back the transaction.
     *
     * @exception IllegalStateException Thrown if the current thread is
     *    not associated with a transaction or if the transaction manager
     *    encounters an unexpected error condition.
     *
     */
    void rollback();

    /**
     * Modify the transaction associated with the current thread such that
     * the only possible outcome of the transaction is to roll back the
     * transaction.
     *
     * @exception IllegalStateException Thrown if the current thread is
     *    not associated with a transaction or if the transaction manager
     *    encounters an unexpected error condition.
     *
     */
    void setRollbackOnly();

    /**
     * Determine whether the transaction associated with the current thread
     * has been marked so that the only possible outcome of the transaction
     * is to roll back the transaction.
     *
     * @return {@code true} if the current transaction has been marked for
     *    rollback, or {@code false} otherwise.
     */
    boolean isRollbackOnly();

    /**
     * Obtain the status of the transaction associated with the current thread.
     *
     * @return The transaction status. If no transaction is associated with
     *    the current thread, this method returns the Status.NoTransaction
     *    value.
     *
     * @exception IllegalStateException Thrown if the transaction manager
     *    encounters an unexpected error condition.
     *
     */
    int getStatus();

    /**
     * Modify the timeout value that is associated with transactions started
     * by the current thread with the {@link #begin} method.
     *
     * <p> If an application has not called this method, the transaction
     * service uses some default value for the transaction timeout.
     *
     * @param seconds The value of the timeout in seconds. If the value is zero,
     *        the transaction service restores the default value. If the value
     *        is negative a SystemException is thrown.
     *
     * @exception IllegalStateException Thrown if the transaction manager
     *    encounters an unexpected error condition.
     *
     */
    void setTimeout(int seconds);

    /**
     * The timeout value that is associated with transactions started by
     * the current thread with the {@link #begin} method.
     *
     * <p> If an application has not called this method, the transaction
     * service uses some default value for the transaction timeout.
     *
     * @return The timeout in seconds
     *
     * @exception IllegalStateException Thrown if the transaction manager
     *    encounters an unexpected error condition.
     *
     */
    int getTimeout();
}
