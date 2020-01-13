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

/**
 * NotSupportedException exception indicates that the request cannot be
 * executed because the operation is not a supported feature. For example, 
 * because nested transactions are not supported, the Transaction Manager 
 * throws this exception when a calling thread
 * that is already associated with a transaction attempts to start a new 
 * transaction. (A nested transaction occurs when a thread is already
 * associated with one transaction and attempts to start a second 
 * transaction.)
 */
public class NotSupportedException extends java.lang.Exception 
{
        /**
         * Specify serialVersionUID for backward compatibility
         */
        private static final long serialVersionUID = 56870312332816390L;

        public NotSupportedException()
	{
		super();
	}

	public NotSupportedException(String msg)
	{
		super(msg);
	}
}

