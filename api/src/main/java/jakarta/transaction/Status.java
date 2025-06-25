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
 * The Status interface defines static variables used for transaction status codes.
 *
 * @version Jakarta Transactions 2.0
 */

public interface Status {
    /**
     * A transaction is associated with the target object and it is in the active state. An implementation returns this
     * status after a transaction has been started and prior to a Coordinator issuing any prepares, unless the transaction
     * has been marked for rollback.
     */
    public final static int STATUS_ACTIVE = 0;

    /**
     * A transaction is associated with the target object and it has been marked for rollback, perhaps as a result of a
     * setRollbackOnly operation.
     */
    public final static int STATUS_MARKED_ROLLBACK = 1;

    /**
     * A transaction is associated with the target object and it has been prepared. That is, all subordinates have agreed to
     * commit. The target object may be waiting for instructions from a superior as to how to proceed.
     */
    public final static int STATUS_PREPARED = 2;

    /**
     * A transaction is associated with the target object and it has been committed. It is likely that heuristics exist;
     * otherwise, the transaction would have been destroyed and NoTransaction returned.
     */
    public final static int STATUS_COMMITTED = 3;

    /**
     * A transaction is associated with the target object and the outcome has been determined to be rollback. It is likely
     * that heuristics exist; otherwise, the transaction would have been destroyed and NoTransaction returned.
     */
    public final static int STATUS_ROLLEDBACK = 4;

    /**
     * A transaction is associated with the target object but its current status cannot be determined. This is a transient
     * condition and a subsequent invocation will ultimately return a different status.
     */
    public final static int STATUS_UNKNOWN = 5;

    /**
     * No transaction is currently associated with the target object. This will occur after a transaction has completed.
     */
    public final static int STATUS_NO_TRANSACTION = 6;

    /**
     * A transaction is associated with the target object and it is in the process of preparing. An implementation returns
     * this status if it has started preparing, but has not yet completed the process. The likely reason for this is that
     * the implementation is probably waiting for responses to prepare from one or more Resources.
     */
    public final static int STATUS_PREPARING = 7;

    /**
     * A transaction is associated with the target object and it is in the process of committing. An implementation returns
     * this status if it has decided to commit but has not yet completed the committing process. This occurs because the
     * implementation is probably waiting for responses from one or more Resources.
     */
    public final static int STATUS_COMMITTING = 8;

    /**
     * A transaction is associated with the target object and it is in the process of rolling back. An implementation
     * returns this status if it has decided to rollback but has not yet completed the process. The implementation is
     * probably waiting for responses from one or more Resources.
     */
    public final static int STATUS_ROLLING_BACK = 9;
}
