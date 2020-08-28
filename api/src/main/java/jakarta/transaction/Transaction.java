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

package jakarta.transaction;

import javax.transaction.xa.XAResource;
import java.lang.IllegalStateException;
import java.lang.SecurityException;

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
     */
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

}
