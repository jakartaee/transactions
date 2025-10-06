/*
 * Copyright (c) 1997, 2025 Oracle and/or its affiliates. All rights reserved.
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
 * Represents the status of a transaction.
 *
 * @since JTA 2.1
 */
public enum TransactionStatus {
    /**
     * A transaction is associated with the target object, and it is in the active state. An implementation returns this
     * status after a transaction has been started and prior to a Coordinator issuing any prepares, unless the transaction
     * has been marked for rollback.
     */
    ACTIVE,

    /**
     * A transaction is associated with the target object, and it has been marked for rollback, perhaps as a result of a
     * {@link Transaction#setRollbackOnly} operation.
     */
    MARKED_FOR_ROLLBACK,

    /**
     * A transaction is associated with the target object, and it has been prepared. That is, all subordinates have agreed
     * to commit. The target object may be waiting for instructions from a superior as to how to proceed.
     */
    PREPARED,

    /**
     * A transaction is associated with the target object, and it has been committed. It is likely that heuristics exist;
     * otherwise, the transaction would have been destroyed and {@link #NO_TRANSACTION} returned.
     */
    COMMITTED,

    /**
     * A transaction is associated with the target object and the outcome has been determined to be rollback. It is likely
     * that heuristics exist; otherwise, the transaction would have been destroyed and {@link #NO_TRANSACTION} returned.
     */
    ROLLED_BACK,

    /**
     * A transaction is associated with the target object but its current status cannot be determined. This is a transient
     * condition and a subsequent invocation will ultimately return a different status.
     */
    UNKNOWN,

    /**
     * No transaction is currently associated with the target object. This will occur after a transaction has completed.
     */
    NO_TRANSACTION,

    /**
     * A transaction is associated with the target object, and it is in the process of preparing. An implementation returns
     * this status if it has started preparing, but has not yet completed the process. The likely reason for this is that
     * the implementation is probably waiting for responses to prepare from one or more Resources.
     */
    PREPARING,

    /**
     * A transaction is associated with the target object, and it is in the process of committing. An implementation returns
     * this status if it has decided to commit but has not yet completed the committing process. This occurs because the
     * implementation is probably waiting for responses from one or more Resources.
     */
    COMMITTING,

    /**
     * A transaction is associated with the target object, and it is in the process of rolling back. An implementation
     * returns this status if it has decided to rollback but has not yet completed the process. The implementation is
     * probably waiting for responses from one or more Resources.
     */
    ROLLING_BACK;
}
