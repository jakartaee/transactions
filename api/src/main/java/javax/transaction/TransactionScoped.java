/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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

package javax.transaction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.context.NormalScope;

/**
 * <p>The javax.transaction.TransactionScoped annotation provides the ability to
 * specify a standard CDI scope to define bean instances whose lifecycle is
 * scoped to the currently active Jakarta Transactions transaction. This annotation has no effect
 * on classes which have non-contextual references such those defined as managed
 * beans by the Jakarta EE specification.</p>
 * The transaction scope is active when the return from a call to
 * <code>UserTransaction.getStatus</code> or
 * <code>TransactionManager.getStatus</code>
 * is one of the following states:
 * <ul>
 * <li>Status.STATUS_ACTIVE
 * <li>Status.STATUS_MARKED_ROLLBACK
 * <li>Status.STATUS_PREPARED
 * <li>Status.STATUS_UNKNOWN
 * <li>Status.STATUS_PREPARING
 * <li>Status.STATUS_COMMITTING
 * <li>Status.STATUS_ROLLING_BACK
 * </ul>
 * <p>It is not intended that the term "active" as defined here in relation to the
 * TransactionScoped annotation should also apply to its use in relation to
 * transaction context, lifecycle, etc. mentioned elsewhere in this
 * specification. The object with this annotation will be associated with the
 * current active Jakarta Transactions transaction when the object is used. This association must
 * be retained through any transaction suspend or resume calls as well as any
 * <code>Synchronization.beforeCompletion</code> callbacks. Any
 * <code>Synchronization.afterCompletion</code> methods will be invoked in an undefined
 * context. The way in which the Jakarta Transactions transaction is begun and completed
 * (for example via UserTransaction, Transactional interceptor, etc.) is of no consequence.
 * The contextual references used across different Jakarta Transactions transactions are distinct.
 * Refer to the CDI specification for more details on contextual references.
 * A <code>javax.enterprise.context.ContextNotActiveException</code>
 * will be thrown if an object with this annotation is used when the
 * transaction context is not active.</p>
 *
 *  @since 1.2
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@NormalScope(passivating=true)
public @interface TransactionScoped {
}
