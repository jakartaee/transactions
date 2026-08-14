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
 * Indicates the relative ordering preference for the prepare phase of the two-phase commit protocol applied to a given
 * {@link ExtendedXAResource}.
 *
 * <p>
 * The transaction manager is required to respect the {@link #EXCLUSIVE_LAST} ordering constraint and should respect
 * {@link #EARLY} ordering where possible.
 * </p>
 *
 * <p>
 * {@link #EARLY} is intended for resources that are likely to vote {@code XA_RDONLY}, allowing the transaction manager
 * to detect a potential single-resource scenario early and apply a one-phase commit optimisation.
 * </p>
 *
 * <p>
 * {@link #EXCLUSIVE_LAST} supports the <em>Last Resource Commit Optimisation</em> (LRCO), in which a non-XA resource
 * participates by mapping {@code prepare} to a real commit and {@code commit} to a no-op. Because this resource
 * effectively commits during the prepare phase, it must be prepared last, after all genuine XA resources have
 * successfully prepared.
 * </p>
 *
 * @see ExtendedXAResource#getPreparePriority()
 * @since Jakarta Transactions 2.2
 */
public enum PreparePriority {

    /**
     * The resource should be prepared before resources with {@link #NORMAL} or {@link #EXCLUSIVE_LAST} priority. Resources
     * with {@code EARLY} priority may be prepared concurrently with each other.
     *
     * <p>
     * Intended for resources that are likely to vote {@code XA_RDONLY}, enabling an early read-only detection optimisation.
     * </p>
     */
    EARLY,

    /**
     * Default prepare priority. The transaction manager determines the prepare order among resources with {@code NORMAL}
     * priority.
     */
    NORMAL,

    /**
     * The resource must be prepared after all resources with {@link #EARLY} or {@link #NORMAL} priority. At most one
     * {@code EXCLUSIVE_LAST} resource may be enlisted in a single transaction; if a second such resource is enlisted, the
     * transaction manager must throw {@link jakarta.transaction.SystemException}.
     *
     * <p>
     * Intended for Last Resource Commit Optimisation (LRCO) resources that do not support a true prepare phase.
     * </p>
     */
    EXCLUSIVE_LAST
}
