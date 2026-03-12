/*
 * Copyright (c) 2013, 2025 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.enterprise.util.Nonbinding;
import jakarta.interceptor.InterceptorBinding;
import java.lang.annotation.*;

/**
 * <p>
 * The jakarta.transaction.Transactional annotation provides the application the ability to declaratively control
 * transaction boundaries on CDI managed beans, as well as classes defined as managed beans by the Jakarta EE
 * specification, at both the class and method level where method level annotations override those at the class level.
 * </p>
 * <p>
 * See the Jakarta Enterprise Beans specification for restrictions on the use of @Transactional with Jakarta Enterprise
 * Beans.
 * </p>
 * <p>
 * This support is provided via an implementation of CDI interceptors that conduct the necessary suspending, resuming,
 * etc. The Transactional interceptor interposes on business method invocations only and not on lifecycle events.
 * Lifecycle methods are invoked in an unspecified transaction context.
 * </p>
 * <p>
 * If an attempt is made to call any method of the UserTransaction interface from within the scope of a bean or method
 * annotated with @Transactional and a Transactional.TxType other than NOT_SUPPORTED or NEVER, an IllegalStateException
 * must be thrown. The use of the UserTransaction is allowed within life cycle events. The use of the
 * TransactionSynchronizationRegistry is allowed regardless of any @Transactional annotation.
 * </p>
 * <p>
 * The Transactional interceptors must have a priority of Interceptor.Priority.PLATFORM_BEFORE+200. Refer to the
 * Interceptors specification for more details.
 * </p>
 * <p>
 * The TxType element of the annotation indicates whether a bean method is to be executed within a transaction context.
 * TxType.REQUIRED is the default.
 * </p>
 *
 * <p>
 * By default, a Transactional interceptor marks the transaction for rollback when it intercepts an exception assignable
 * to RuntimeException. Checked exceptions are considered <em>application exceptions</em> and do not, by default, cause
 * the interceptor to mark the transaction for rollback.
 * </p>
 * <p>
 * This default behavior can be modified by specifying additional types that cause the interceptor to mark the
 * transaction for rollback and/or types that do not cause the interceptor to mark the transaction for rollback.
 * </p>
 * <ul>
 * <li>If an exception is an instance of a type listed by rollbackOn, and also of Exception, and if the exception is not
 * also an instance of a type listed in dontRollbackOn, the interceptor must mark the transaction for rollback.</li>
 * <li>If an exception is an instance of a type listed by dontRollbackOn, the interceptor must not mark the transaction
 * for rollback, even if the exception is also an instance of RuntimeException or of a type listed in rollbackOn.</li>
 * </ul>
 * <p>
 * If an exception is an instance of Error, the behavior is undefined. A Transactional interceptor is not required to
 * catch Errors, and so portable applications should not depend on the behavior of the interceptor when an Error occurs.
 * </p>
 *
 * @version Jakarta Transactions 2.0
 * @since JTA 1.2
 */
@Inherited
@InterceptorBinding
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Transactional {

    /**
     * The TxType element of the Transactional annotation indicates whether a bean method is to be executed within a
     * transaction context.
     *
     * @return TxType element
     */
    TxType value() default TxType.REQUIRED;

    /**
     * The TxType element of the annotation indicates whether a bean method is to be executed within a transaction context
     * where the values provide the following corresponding behavior.
     *
     * @version Jakarta Transactions 2.0
     */
    public enum TxType {
        /**
         * <p>
         * If called outside a transaction context, the interceptor must begin a new Jakarta Transactions transaction, the
         * managed bean method execution must then continue inside this transaction context, and the transaction must be
         * completed by the interceptor.
         * </p>
         * <p>
         * If called inside a transaction context, the managed bean method execution must then continue inside this transaction
         * context.
         * </p>
         */
        REQUIRED,

        /**
         * <p>
         * If called outside a transaction context, the interceptor must begin a new Jakarta Transactions transaction, the
         * managed bean method execution must then continue inside this transaction context, and the transaction must be
         * completed by the interceptor.
         * </p>
         * <p>
         * If called inside a transaction context, the current transaction context must be suspended, a new Jakarta Transactions
         * transaction will begin, the managed bean method execution must then continue inside this transaction context, the
         * transaction must be completed, and the previously suspended transaction must be resumed.
         * </p>
         */
        REQUIRES_NEW,

        /**
         * <p>
         * If called outside a transaction context, a TransactionalException with a nested TransactionRequiredException must be
         * thrown.
         * </p>
         * <p>
         * If called inside a transaction context, managed bean method execution will then continue under that context.
         * </p>
         */
        MANDATORY,

        /**
         * <p>
         * If called outside a transaction context, managed bean method execution must then continue outside a transaction
         * context.
         * </p>
         * <p>
         * If called inside a transaction context, the managed bean method execution must then continue inside this transaction
         * context.
         * </p>
         */
        SUPPORTS,

        /**
         * <p>
         * If called outside a transaction context, managed bean method execution must then continue outside a transaction
         * context.
         * </p>
         * <p>
         * If called inside a transaction context, the current transaction context must be suspended, the managed bean method
         * execution must then continue outside a transaction context, and the previously suspended transaction must be resumed
         * by the interceptor that suspended it after the method execution has completed.
         * </p>
         */
        NOT_SUPPORTED,

        /**
         * <p>
         * If called outside a transaction context, managed bean method execution must then continue outside a transaction
         * context.
         * </p>
         * <p>
         * If called inside a transaction context, a TransactionalException with a nested InvalidTransactionException must be
         * thrown.
         * </p>
         */
        NEVER
    }

    /**
     * <p>
     * Specifies a list of class and interface types which cause the interceptor to mark the transaction for rollback
     * when an exception assignable to one of the listed types is intercepted.
     * </p>
     * <ul>
     * <li>If an exception is an instance of one of the listed types, and also of Exception, and if the exception is not
     * also an instance of a type listed in dontRollbackOn, the interceptor must mark the transaction for rollback.</li>
     * <li>If an exception is an instance of RuntimeException, and if the exception is not also an instance of a type
     * listed in dontRollbackOn, the interceptor must mark the transaction for rollback.</li>
     * <li>If an exception is an instance of one of the listed types, and also of Error, the behavior is undefined. The
     * interceptor is not required to catch Errors, and so portable applications should not depend on the behavior of
     * the interceptor when an Error occurs.</li>
     * </ul>
     *
     * @return A list of Class objects representing the class and interface types.
     */
    @Nonbinding
    Class[] rollbackOn() default {};

    /**
     * <p>
     * Specifies a list of class and interface types which do not cause the interceptor to mark the transaction for
     * rollback when an exception assignable to one of the listed types is intercepted. If an exception is an instance
     * of one of the listed types, the interceptor must not mark the transaction for rollback, even if the exception is
     * also an instance of RuntimeException or of a type listed in rollbackOn.
     * </p>
     *
     * @return A list of Class objects representing the class and interface types.
     */
    @Nonbinding
    Class[] dontRollbackOn() default {};

    /**
     * The {@code readOnly} element can be set to indicate that the transaction must be read-only. A read-only transaction
     * may fail early if a non-read action is executed. A read-only transaction may only run in a read-only transaction
     * context, and similarly, a non-read-only transaction may only run in a non-read-only transaction context. As defined
     * by the semantics of the configured {@code TxType}, a newly started transaction must be read-only if the
     * {@code readOnly} element is {@code true}, and be non-read-only if the {@code readOnly} element is {@code false}.
     * <p>
     * If called inside a non-compatible transaction context, a TransactionalException with a nested
     * InvalidTransactionException must be thrown.
     * </p>
     *
     * @return The readOnly flag
     * @since 2.1
     */
    @Nonbinding
    public boolean readOnly() default false;

}
