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

package jakarta.transaction.xa;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

/**
 * Extended XAResource for additional capabilities such as read-only mode and completion-phase ordering hints.
 *
 * <p>
 * A resource adapter may implement this interface in addition to {@link XAResource} to signal to the transaction
 * manager that it requires specific ordering during the prepare or commit phases of the two-phase commit protocol, or
 * that it supports read-only mode.
 * </p>
 *
 * @version Jakarta Transactions 2.2
 * @see PreparePriority
 * @see CommitPriority
 */
public interface ExtendedXAResource extends XAResource {
    /**
     * <p>
     * Puts this {@code XAResource} instance into read-only mode for the transaction of the given xid.
     * </p>
     *
     * <p>
     * If the {@code XAResource} was put into read-only mode for the transaction of the given xid successfully, the method
     * returns {@code true}; otherwise {@code false}. If a resource manager does not support explicitly setting the
     * {@code XAResource} into read-only mode, this method returns {@code false}, in which case the {@code XAResource} will
     * be {@linkplain #rollback(Xid) rolled back} when the transaction ends.
     * </p>
     *
     * <p>
     * The transaction manager can invoke this method on a {@code XAResource} at most once for a given {@code Xid}. If the
     * transaction manager invokes this method for an {@code Xid}, it must do so prior to invoking {@link #start(Xid, int)}.
     * </p>
     *
     * @param xid A global transaction identifier for which this resource shall be set into read-only mode.
     *
     * @return {@code true} if the {@code XAResource} was put into read-only mode successfully; otherwise {@code false}.
     *
     * @exception XAException An error has occurred. Possible exception values are XAER_RMERR, XAER_RMFAIL, or XAER_INVAL.
     */
    boolean setReadOnly(Xid xid) throws XAException;

    /**
     * Returns the prepare-phase ordering preference for this resource.
     *
     * <p>
     * The transaction manager must call this method after invoking {@link #end(Xid, int)} and before starting the prepare
     * phase, so that the resource manager has had the opportunity to determine whether the work performed is likely to be
     * read-only.
     * </p>
     *
     * <p>
     * If this resource returns {@link PreparePriority#EXCLUSIVE_LAST} and a second resource with the same priority is
     * already enlisted in the transaction, the transaction manager must throw {@link jakarta.transaction.SystemException}
     * during enlistment of the second such resource via {@link jakarta.transaction.Transaction#enlistResource}.
     * </p>
     *
     * @return the {@link PreparePriority} for this resource; never {@code null}.
     * @since Jakarta Transactions 2.2
     */
    default PreparePriority getPreparePriority() {
        return PreparePriority.NORMAL;
    }

    /**
     * Returns the commit-phase ordering preference for this resource.
     *
     * <p>
     * The transaction manager must commit a resource returning {@link CommitPriority#EXCLUSIVE_FIRST} before all other
     * enlisted resources, and must commit a resource returning {@link CommitPriority#LATE} after all resources with
     * {@link CommitPriority#NORMAL} or {@link CommitPriority#EXCLUSIVE_FIRST} priority.
     * </p>
     *
     * <p>
     * If this resource returns {@link CommitPriority#EXCLUSIVE_FIRST} and a second resource with the same priority is
     * already enlisted in the transaction, the transaction manager must throw {@link jakarta.transaction.SystemException}
     * during enlistment of the second such resource via {@link jakarta.transaction.Transaction#enlistResource}.
     * </p>
     *
     * @return the {@link CommitPriority} for this resource; never {@code null}.
     * @since Jakarta Transactions 2.2
     */
    default CommitPriority getCommitPriority() {
        return CommitPriority.NORMAL;
    }
}
