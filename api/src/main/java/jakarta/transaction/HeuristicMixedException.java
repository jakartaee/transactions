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
 * This exception is thrown to report that a heuristic decision was made and
 * that some relevant updates have been committed and others have been
 * rolled back.
 *
 * @version Jakarta Transactions 2.0
 */
public class HeuristicMixedException extends java.lang.Exception  
{
    /**
     * Specify serialVersionUID for backward compatibility
     */
    private static final long serialVersionUID = 2345014349685956666L;

    public HeuristicMixedException()
    {
	super();
    }

    public HeuristicMixedException(String msg)
    {
	super(msg);
    }
}

