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

import java.lang.IllegalStateException;
import java.lang.SecurityException;

/**
 * The TransactionManager interface defines the methods that allow an application server to manage transaction
 * boundaries.
 *
 * @version Jakarta Transactions 2.0
 */
public interface TransactionManager {

    /**
     * Create a new transaction and associate it with the current thread.
     *
     * @exception NotSupportedException Thrown if the thread is already associated with a transaction and the Transaction
     * Manager implementation does not support nested transactions.
     *
     * @exception SystemException Thrown if the transaction manager encounters an unexpected error condition.
     *
     */
    public void begin() throws NotSupportedException, SystemException;

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
     *
     */
    public void commit() throws RollbackException,
            HeuristicMixedException, HeuristicRollbackException, SecurityException,
            IllegalStateException, SystemException;

    /**
     * Obtain the status of the transaction associated with the current thread.
     *
     * @return The transaction status. If no transaction is associated with the current thread, this method returns the
     * Status.NoTransaction value.
     *
     * @exception SystemException Thrown if the transaction manager encounters an unexpected error condition.
     *
     */
    public int getStatus() throws SystemException;

    /**
     * Get the transaction object that represents the transaction context of the calling thread.
     *
     * @return the <code>Transaction</code> object representing the transaction associated with the calling thread.
     *
     * @exception SystemException Thrown if the transaction manager encounters an unexpected error condition.
     *
     */
    public Transaction getTransaction() throws SystemException;

    /**
     * Resume the transaction context association of the calling thread with the transaction represented by the supplied
     * Transaction object. When this method returns, the calling thread is associated with the transaction context
     * specified.
     *
     * @param tobj The <code>Transaction</code> object that represents the transaction to be resumed.
     *
     * @exception InvalidTransactionException Thrown if the parameter transaction object contains an invalid transaction.
     *
     * @exception IllegalStateException Thrown if the thread is already associated with another transaction.
     *
     * @exception SystemException Thrown if the transaction manager encounters an unexpected error condition.
     */
    public void resume(Transaction tobj)
            throws InvalidTransactionException, IllegalStateException,
            SystemException;

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
    public void rollback() throws IllegalStateException, SecurityException,
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
    public void setRollbackOnly() throws IllegalStateException, SystemException;

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
    public void setTransactionTimeout(int seconds) throws SystemException;

    /**
     * Suspend the transaction currently associated with the calling thread and return a Transaction object that represents
     * the transaction context being suspended. If the calling thread is not associated with a transaction, the method
     * returns a null object reference. When this method returns, the calling thread is not associated with a transaction.
     *
     * @return Transaction object representing the suspended transaction.
     *
     * @exception SystemException Thrown if the transaction manager encounters an unexpected error condition.
     *
     */
    public Transaction suspend() throws SystemException;
}
