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
 * RollbackException exception is thrown when the transaction has been 
 * marked for rollback only or the transaction has been rolled back
 * instead of committed. This is a local exception thrown by methods 
 * in the <code>UserTransaction</code>, <code>Transaction</code>, and 
 * <code>TransactionManager</code> interfaces.
 */
public class RollbackException extends java.lang.Exception 
{
        /**
         * Specify serialVersionUID for backward compatibility
         */
        private static final long serialVersionUID = 4151607774785285395L;
        
	public RollbackException()
	{
		super();
	}

	public RollbackException(String msg)
	{
		super(msg);
	}
}

