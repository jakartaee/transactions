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
 * The transaction manager supports a synchronization mechanism that allows the interested party to be notified before
 * and after the transaction completes. Using the registerSynchronization method, the application server registers a
 * Synchronization object for the transaction currently associated with the target Transaction object.
 *
 * @version Jakarta Transactions 2.0
 */
public interface Synchronization {

    /**
     * The beforeCompletion method is called by the transaction manager prior to the start of the two-phase transaction
     * commit process. This call is executed with the transaction context of the transaction that is being committed.
     */
    public void beforeCompletion();

    /**
     * This method is called by the transaction manager after the transaction is committed or rolled back.
     *
     * @param status The status of the transaction completion.
     */
    public void afterCompletion(int status);
}
