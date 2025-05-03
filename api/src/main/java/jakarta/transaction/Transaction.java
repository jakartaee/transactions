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

import javax.transaction.xa.XAResource;
import java.lang.IllegalStateException;
import java.lang.SecurityException;

import static jakarta.transaction.Status.*;

/**
 * The Transaction interface allows operations to be performed against
 * the transaction in the target Transaction object. A Transaction
 * object is created corresponding to each global transaction creation.
 * The Transaction object can be used for resource enlistment,
 * synchronization registration, transaction completion, and status
 * query operations.
 *
 * @version Jakarta Transactions 2.0
 */

public interface Transaction {

    /**
     * Complete the transaction represented by this Transaction object.
     *
     * @exception RollbackException Thrown to indicate that
     *    the transaction has been rolled back rather than committed.
     *
     * @exception HeuristicMixedException Thrown to indicate that a heuristic
     *    decision was made and that some relevant updates have been committed
     *    while others have been rolled back.
     *
     * @exception HeuristicRollbackException Thrown to indicate that a
     *    heuristic decision was made and that all relevant updates have been
     *    rolled back.
     *
     * @exception SecurityException Thrown to indicate that the thread is
     *    not allowed to commit the transaction.
     *
     * @exception IllegalStateException Thrown if the transaction in the 
     *    target object is inactive.
     *
     * @exception SystemException Thrown if the transaction manager
     *    encounters an unexpected error condition.
     */
    public void commit() throws RollbackException,
                HeuristicMixedException, HeuristicRollbackException,
                SecurityException, IllegalStateException, SystemException;

    /**
     * Disassociate the resource specified from the transaction associated 
     * with the target Transaction object.
     *
     * @param xaRes The XAResource object associated with the resource 
     *              (connection).
     *
     * @param flag One of the values of TMSUCCESS, TMSUSPEND, or TMFAIL.
     *
     * @exception IllegalStateException Thrown if the transaction in the
     *    target object is inactive.
     *
     * @exception SystemException Thrown if the transaction manager
     *    encounters an unexpected error condition.
     *
     * @return <i>true</i> if the resource was delisted successfully; otherwise
     *	  <i>false</i>.
     *
     */
    public boolean delistResource(XAResource xaRes, int flag)
        throws IllegalStateException, SystemException;

    /**
     * Enlist the resource specified with the transaction associated with the 
     * target Transaction object.
     *
     * @param xaRes The XAResource object associated with the resource 
     *              (connection).
     *
     * @return <i>true</i> if the resource was enlisted successfully; otherwise
     *    <i>false</i>.
     *
     * @exception RollbackException Thrown to indicate that
     *    the transaction has been marked for rollback only.
     *
     * @exception IllegalStateException Thrown if the transaction in the
     *    target object is in the prepared state or the transaction is
     *    inactive.
     *
     * @exception SystemException Thrown if the transaction manager
     *    encounters an unexpected error condition.
     *
     */
    public boolean enlistResource(XAResource xaRes)
        throws RollbackException, IllegalStateException,
        SystemException;

    /**
     * Obtain the status of the transaction associated with the target
     * Transaction object.
     *
     * @return The transaction status. If no transaction is associated with
     *    the target object, this method returns the
     *    Status.NoTransaction value.
     *
     * @exception SystemException Thrown if the transaction manager
     *    encounters an unexpected error condition.
     *
     * @since JTA 2.1
     */
    public Status getCurrentStatus() throws SystemException;

    /**
     * Obtain the status of the transaction associated with the target 
     * Transaction object.
     *
     * @return The transaction status. If no transaction is associated with
     *    the target object, this method returns the 
     *    Status.NoTransaction value.
     *
     * @exception SystemException Thrown if the transaction manager
     *    encounters an unexpected error condition.
     *
     * @deprecated Use {@link #getCurrentStatus()}
     */
    @Deprecated(since = "2.1")
    public int getStatus() throws SystemException;

    /**
     * Register a synchronization object for the transaction currently
     * associated with the target object. The transction manager invokes
     * the beforeCompletion method prior to starting the two-phase transaction
     * commit process. After the transaction is completed, the transaction
     * manager invokes the afterCompletion method.
     *
     * @param sync The Synchronization object for the transaction associated
     *    with the target object.
     *
     * @exception RollbackException Thrown to indicate that
     *    the transaction has been marked for rollback only.
     *
     * @exception IllegalStateException Thrown if the transaction in the
     *    target object is in the prepared state or the transaction is
     *	  inactive.
     *
     * @exception SystemException Thrown if the transaction manager
     *    encounters an unexpected error condition.
     *
     */
    public void registerSynchronization(Synchronization sync)
                throws RollbackException, IllegalStateException,
                SystemException;

    /**
     * Rollback the transaction represented by this Transaction object.
     *
     * @exception IllegalStateException Thrown if the transaction in the
     *    target object is in the prepared state or the transaction is
     *    inactive.
     *
     * @exception SystemException Thrown if the transaction manager
     *    encounters an unexpected error condition.
     *
     */
	public void rollback() throws IllegalStateException, SystemException;

    /**
     * Modify the transaction associated with the target object such that
     * the only possible outcome of the transaction is to roll back the
     * transaction.
     *
     * @exception IllegalStateException Thrown if the target object is
     *    not associated with any transaction.
     *
     * @exception SystemException Thrown if the transaction manager
     *    encounters an unexpected error condition.
     *
     */
    public void setRollbackOnly() throws IllegalStateException,
        SystemException;

    /**
     * Represents the status of a transaction.
     *
     * @since JTA 2.1
     */
    enum Status {
        /**
         * A transaction is associated with the target object, and it is in the
         * active state. An implementation returns this status after a
         * transaction has been started and prior to a Coordinator issuing
         * any prepares, unless the transaction has been marked for rollback.
         */
        ACTIVE,

        /**
         * A transaction is associated with the target object, and it has been
         * marked for rollback, perhaps as a result of a {@link #setRollbackOnly}
         * operation.
         */
        MARKED_FOR_ROLLBACK,

        /**
         * A transaction is associated with the target object, and it has been
         * prepared. That is, all subordinates have agreed to commit. The
         * target object may be waiting for instructions from a superior as to
         * how to proceed.
         */
        PREPARED,

        /**
         * A transaction is associated with the target object, and it has been
         * committed. It is likely that heuristics exist; otherwise, the
         * transaction would have been destroyed and {@link #NO_TRANSACTION}
         * returned.
         */
        COMMITTED,

        /**
         * A transaction is associated with the target object and the outcome
         * has been determined to be rollback. It is likely that heuristics
         * exist; otherwise, the transaction would have been destroyed and
         * {@link #NO_TRANSACTION} returned.
         */
        ROLLED_BACK,

        /**
         * A transaction is associated with the target object but its current
         * status cannot be determined. This is a transient condition and a
         * subsequent invocation will ultimately return a different status.
         */
        UNKNOWN,

        /**
         * No transaction is currently associated with the target object.
         * This will occur after a transaction has completed.
         */
        NO_TRANSACTION,

        /**
         * A transaction is associated with the target object, and it is in
         * the process of preparing. An implementation returns this status if
         * it has started preparing, but has not yet completed the process.
         * The likely reason for this is that the implementation is probably
         * waiting for responses to prepare from one or more Resources.
         */
        PREPARING,

        /**
         * A transaction is associated with the target object, and it is in the
         * process of committing. An implementation returns this status if it
         * has decided to commit but has not yet completed the committing
         * process. This occurs because the implementation is probably waiting
         * for responses from one or more Resources.
         */
        COMMITTING,

        /**
         * A transaction is associated with the target object, and it is in the
         * process of rolling back. An implementation returns this status if it
         * has decided to rollback but has not yet completed the process. The
         * implementation is probably waiting for responses from one or more
         * Resources.
         */
        ROLLING_BACK;

        /**
         * The equivalent legacy integer-valued code defined by {@link jakarta.transaction.Status}.
         */
        public int code() {
            switch (this) {
                case ACTIVE: return STATUS_ACTIVE;
                case MARKED_FOR_ROLLBACK: return STATUS_MARKED_ROLLBACK;
                case PREPARED: return STATUS_PREPARED;
                case COMMITTED: return STATUS_COMMITTED;
                case ROLLED_BACK: return STATUS_ROLLEDBACK;
                case UNKNOWN: return STATUS_UNKNOWN;
                case NO_TRANSACTION: return STATUS_NO_TRANSACTION;
                case PREPARING: return STATUS_PREPARING;
                case COMMITTING: return STATUS_COMMITTING;
                case ROLLING_BACK: return STATUS_ROLLING_BACK;
                default: throw new IllegalStateException(); // impossible
            }
        }
    }

}
