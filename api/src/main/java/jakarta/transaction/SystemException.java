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
 * The SystemException is thrown by the transaction manager to 
 * indicate that it has encountered an unexpected error condition
 * that prevents future transaction services from proceeding.
 *
 * @version Jakarta Transactions 2.0
 */
public class SystemException extends java.lang.Exception {

    /**
     * Specify serialVersionUID for backward compatibility
     */
    private static final long serialVersionUID = 839699079412719325L;

    /**
     * The error code with which to create the SystemException.
     *
     * @serial The error code for the exception
     */
    public int errorCode;

    public SystemException()
    {
    	super();
    }    
    
    /**
     * Create a SystemException with a given string.
     *
     * @param s The string message for the exception
     */
    public SystemException(String s)
    {
    	super(s);
    }
    
    /**
     * Create a SystemException with a given error code.
     *
     * @param errcode The error code for the exception
     */
    public SystemException(int errcode)
    {
    	super();
    	errorCode = errcode;
    }


}
