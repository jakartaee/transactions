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
 * This exception indicates that the transaction associated with processing
 * of the request has been rolled back, or it has been marked to roll back. 
 * Thus the requested operation either could not be performed or was not 
 * performed because further computation on behalf of the transaction would be
 * fruitless.
 */
public class TransactionRolledbackException extends java.rmi.RemoteException 
{
	    
    /**
     * Specify serialVersionUID for backward compatibility
     */
    private static final long serialVersionUID = -3142798139623020577L;
    
    public TransactionRolledbackException()
	{
		super();
	}

	public TransactionRolledbackException(String msg)
	{
		super(msg);
	}
}

