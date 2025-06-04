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
 * This exception indicates that the request carried an invalid transaction
 * context. For example, this exception could be raised if an error
 * occurred when trying to register a resource.
 *
 * @version Jakarta Transactions 2.0
 */
public class InvalidTransactionException extends java.rmi.RemoteException
{
    /**
     * Specify serialVersionUID for backward compatibility
     */
    private static final long serialVersionUID = 3597320220337691496L;

    public InvalidTransactionException()
	{
		super();
	}

	public InvalidTransactionException(String message)
	{
		super(message);
	}

 	public InvalidTransactionException(String message, Throwable cause)
	{
		super(message, cause);
	}
}

