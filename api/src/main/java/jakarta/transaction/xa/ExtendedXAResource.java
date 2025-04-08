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

package jakarta.transaction.xa;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;

/**
 * Extended XAResource that can report additional capabilities.
 *
 * @version Jakarta Transactions 2.1
 */
public interface ExtendedXAResource extends XAResource
{
	/**
	 * Obtain whether the XAResource supports a read-only mode.
	 *
	 * @return value indicating if the XAResource supports a read-only mode.
	 *
	 * @exception XAException An error has occurred. Possible exception
	 * values are XAER_RMERR and XAER_RMFAIL.
	 */
	boolean isReadOnly() throws XAException;

	/**
	 * Sets the read-only state for the current transaction for this <CODE>XAResource</CODE>
	 * instance. Once set, this read-only value is effective until
	 * <code>setReadOnly</code> is invoked again with a different
	 * value.
	 *
	 * If the read-only state was updated successfully, the method returns
	 * <i>true</i>; otherwise <i>false</i>. If a resource manager does not
	 * support explicitly setting the transaction read-only value, this method
	 * returns <i>false</i>.
	 *
	 * @param readOnly The value indicating the read-only state.
	 *
	 * @return <i>true</i> if the transaction read-only value is set successfully;
	 *       otherwise <i>false</i>.
	 *
	 * @exception XAException An error has occurred. Possible exception values
	 * are XAER_RMERR, XAER_RMFAIL, or XAER_INVAL.
	 */
	boolean setReadOnly(boolean readOnly) throws XAException;
}

