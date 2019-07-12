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

/**
 *
 * The TransactionalException thrown from the Transactional interceptors
 *  implementation contains the original exception as its nested exception
 *  and is a RuntimeException, therefore, by default any
 *  transaction that was started as a result of a Transactional annotation
 *  earlier in the call stream will be marked for rollback as a result of
 *  the TransactionalException being thrown by the Transactional interceptor
 *  of the second bean. For example if a transaction is begun as a result of
 *  a call to a bean annotated with Transactional(TxType.REQUIRED) and this
 *  bean in turn calls a second bean annotated with
 *  Transactional(TxType.NEVER), the transaction begun by the first bean
 *  will be marked for rollback.
 *
 * @since 1.2
 */
public class TransactionalException extends RuntimeException {
    /**
     * Specify serialVersionUID for backward compatibility
     */
    private static final long serialVersionUID = -8196645329560986417L;

    public TransactionalException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
