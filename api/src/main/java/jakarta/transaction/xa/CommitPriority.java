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

/**
 * Indicates the relative ordering preference for the commit phase of the two-phase commit protocol applied to a given
 * {@link ExtendedXAResource}.
 *
 * <p>
 * The transaction manager must respect the {@link #EXCLUSIVE_FIRST} and {@link #LATE} ordering constraints.
 * </p>
 *
 * <p>
 * A typical use case is a transaction that spans a relational database (DB) and a message broker (JMS). Marking the JMS
 * resource {@link #LATE} ensures that messages are not made visible to downstream consumers before the corresponding
 * database rows have been committed and their locks released, avoiding the race condition described in
 * <a href="https://github.com/jakartaee/transactions/issues/58">issue 58</a>.
 * </p>
 *
 * @see ExtendedXAResource#getCommitPriority()
 * @since Jakarta Transactions 2.2
 */
public enum CommitPriority {

    /**
     * The resource must be committed before all resources with {@link #NORMAL} or {@link #LATE} priority. At most one
     * {@code EXCLUSIVE_FIRST} resource may be enlisted in a single transaction; if a second such resource is enlisted, the
     * transaction manager must throw {@link jakarta.transaction.SystemException}.
     */
    EXCLUSIVE_FIRST,

    /**
     * Default commit priority. The transaction manager determines the commit order among resources with {@code NORMAL}
     * priority.
     */
    NORMAL,

    /**
     * The resource should be committed after all resources with {@link #EXCLUSIVE_FIRST} or {@link #NORMAL} priority.
     * Resources with {@code LATE} priority may be committed concurrently with each other.
     */
    LATE
}
