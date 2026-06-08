/*
 * Copyright (c) 1997, 2026 Oracle and/or its affiliates. All rights reserved.
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

import java.lang.IllegalStateException;
import java.lang.SecurityException;

/**
 * The UserTransaction interface defines the methods that allow an application to explicitly manage transaction
 * boundaries.
 *
 * @version Jakarta Transactions 2.0
 */
public interface UserTransaction {

    /**
     * Create a new transaction and associate it with the current thread.
     *
     * @exception NotSupportedException Thrown if the thread is already associated with a transaction and the Transaction
     * Manager implementation does not support nested transactions.
     *
     * @exception SystemException Thrown if the transaction manager encounters an unexpected error condition.
     *
     */
    void begin() throws NotSupportedException, SystemException;

    /**
     * <p>
     * Create a new transaction, indicate whether the transaction will be allowed to commit, and associate the transaction
     * with the current thread.
     * </p>
     *
     * <p>
     * A resource manager might be able to optimize its participation in a transaction by restricting usage to read-only
     * access when the application indicates the transaction will never commit.
     * </p>
     *
     * <p>
     * A value of {@code true} for {@code allowCommit} permits the transaction to be resolved by the {@link commit()}
     * operation. This is the same behavior offered by the {@link #begin()} method.
     * </p>
     *
     * <p>
     * A value of {@code false} restricts transaction resolution such that the only possible outcome is to
     * {@linkplain #rollback() roll back} the transaction. The transaction {@linkplain #getStatus() status} does not
     * transition to {@link Status#STATUS_MARKED_ROLLBACK} unless {@link #setRollbackOnly()} is invoked on the transaction.
     * Prior to that point, resource managers must continue to permit read-only operations within the transaction. Some
     * resource managers might also permit write operations that will ultimately roll back.
     * </p>
     *
     * @param allowCommit controls whether the transaction is permitted to commit.
     *
     * @exception NotSupportedException Thrown if the thread is already associated with a transaction and the Transaction
     * Manager implementation does not support nested transactions.
     *
     * @exception SystemException Thrown if the transaction manager encounters an unexpected error condition.
     * @since 2.1
     */
    void begin(boolean allowCommit) throws NotSupportedException, SystemException;

    /**
     * Complete the transaction associated with the current thread. When this method completes, the thread is no longer
     * associated with a transaction.
     *
     * @exception RollbackException Thrown to indicate that the transaction has been rolled back rather than committed.
     *
     * @exception HeuristicMixedException Thrown to indicate that a heuristic decision was made and that some relevant
     * updates have been committed while others have been rolled back.
     *
     * @exception HeuristicRollbackException Thrown to indicate that a heuristic decision was made and that all relevant
     * updates have been rolled back.
     *
     * @exception SecurityException Thrown to indicate that the thread is not allowed to commit the transaction.
     *
     * @exception IllegalStateException Thrown if the current thread is not associated with a transaction.
     *
     * @exception SystemException Thrown if the transaction manager encounters an unexpected error condition.
     */
    void commit() throws RollbackException,
            HeuristicMixedException, HeuristicRollbackException, SecurityException,
            IllegalStateException, SystemException;

    /**
     * Roll back the transaction associated with the current thread. When this method completes, the thread is no longer
     * associated with a transaction.
     *
     * @exception SecurityException Thrown to indicate that the thread is not allowed to roll back the transaction.
     *
     * @exception IllegalStateException Thrown if the current thread is not associated with a transaction.
     *
     * @exception SystemException Thrown if the transaction manager encounters an unexpected error condition.
     *
     */
    void rollback() throws IllegalStateException, SecurityException,
            SystemException;

    /**
     * Modify the transaction associated with the current thread such that the only possible outcome of the transaction is
     * to roll back the transaction.
     *
     * @exception IllegalStateException Thrown if the current thread is not associated with a transaction.
     *
     * @exception SystemException Thrown if the transaction manager encounters an unexpected error condition.
     *
     */
    void setRollbackOnly() throws IllegalStateException, SystemException;

    /**
     * Obtain the status of the transaction associated with the current thread.
     *
     * @return The transaction status. If no transaction is associated with the current thread, this method returns the
     * Status.NoTransaction value.
     *
     * @exception SystemException Thrown if the transaction manager encounters an unexpected error condition.
     *
     */
    int getStatus() throws SystemException;

    /**
     * Modify the timeout value that is associated with transactions started by the current thread with the begin method.
     *
     * <p>
     * If an application has not called this method, the transaction service uses some default value for the transaction
     * timeout.
     *
     * @param seconds The value of the timeout in seconds. If the value is zero, the transaction service restores the
     * default value. If the value is negative a SystemException is thrown.
     *
     * @exception SystemException Thrown if the transaction manager encounters an unexpected error condition.
     *
     */
    void setTransactionTimeout(int seconds) throws SystemException;

    /**
     * Indicates if the transaction bound to the current thread is effectively read-only because the transaction was started
     * with a value of {@code false} for {@link Transactional#allowCommit()}, {@link UserTransaction#begin(boolean)}, or
     * {@link TransactionManager#begin(boolean)}, indicating that the transaction will not commit.
     *
     * @return The transaction read-only value. If no transaction is associated with the current thread, this method returns
     * {@code false}.
     *
     * @exception SystemException Thrown if the transaction manager encounters an unexpected error condition.
     * @since 2.1
     */
    public boolean isReadOnly() throws SystemException;
}
