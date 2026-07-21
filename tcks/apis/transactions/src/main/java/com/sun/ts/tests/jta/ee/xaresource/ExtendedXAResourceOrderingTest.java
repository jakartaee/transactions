/*
 * Copyright (c) 1997, 2026 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.ts.tests.jta.ee.xaresource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.transaction.xa.CommitPriority;
import jakarta.transaction.xa.ExtendedXAResource;
import jakarta.transaction.xa.PreparePriority;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import org.junit.jupiter.api.Test;

/**
 * TCK tests for the resource-ordering API introduced in Jakarta Transactions 2.2 to address
 * <a href="https://github.com/jakartaee/transactions/issues/58">issue 58</a>.
 *
 * <p>
 * These tests verify:
 * <ul>
 * <li>The default {@link PreparePriority} returned by {@link ExtendedXAResource#getPreparePriority()} is
 * {@link PreparePriority#NORMAL}.</li>
 * <li>The default {@link CommitPriority} returned by {@link ExtendedXAResource#getCommitPriority()} is
 * {@link CommitPriority#NORMAL}.</li>
 * <li>A resource adapter can override {@code getPreparePriority()} to return any {@link PreparePriority} value.</li>
 * <li>A resource adapter can override {@code getCommitPriority()} to return any {@link CommitPriority} value.</li>
 * </ul>
 * </p>
 *
 * <p>
 * The ordering semantics themselves (e.g. that the transaction manager actually calls resources in the correct order)
 * must be verified by integration tests against a real transaction manager implementation.
 * </p>
 */
public class ExtendedXAResourceOrderingTest {

    // -----------------------------------------------------------------------
    // Minimal no-op implementations used across test cases
    // -----------------------------------------------------------------------

    /** An ExtendedXAResource that relies entirely on the default method implementations. */
    private static final ExtendedXAResource DEFAULT_RESOURCE = new MinimalExtendedXAResource();

    /** An ExtendedXAResource that signals it should be prepared last (LRCO). */
    private static final ExtendedXAResource LRCO_RESOURCE = new MinimalExtendedXAResource() {
        @Override
        public PreparePriority getPreparePriority() {
            return PreparePriority.EXCLUSIVE_LAST;
        }
    };

    /** An ExtendedXAResource that signals it should be prepared early (likely read-only). */
    private static final ExtendedXAResource EARLY_PREPARE_RESOURCE = new MinimalExtendedXAResource() {
        @Override
        public PreparePriority getPreparePriority() {
            return PreparePriority.EARLY;
        }
    };

    /** An ExtendedXAResource that signals it should be committed last (e.g. a JMS broker). */
    private static final ExtendedXAResource LATE_COMMIT_RESOURCE = new MinimalExtendedXAResource() {
        @Override
        public CommitPriority getCommitPriority() {
            return CommitPriority.LATE;
        }
    };

    /** An ExtendedXAResource that signals it should be committed first. */
    private static final ExtendedXAResource FIRST_COMMIT_RESOURCE = new MinimalExtendedXAResource() {
        @Override
        public CommitPriority getCommitPriority() {
            return CommitPriority.EXCLUSIVE_FIRST;
        }
    };

    // -----------------------------------------------------------------------
    // Default-value tests
    // -----------------------------------------------------------------------

    /**
     * An {@link ExtendedXAResource} that does not override {@code getPreparePriority()} must return
     * {@link PreparePriority#NORMAL} as the default.
     *
     * @see ExtendedXAResource#getPreparePriority()
     */
    @Test
    public void defaultPreparePriorityIsNormal() {
        assertEquals(PreparePriority.NORMAL, DEFAULT_RESOURCE.getPreparePriority(),
                "Default getPreparePriority() must return NORMAL");
    }

    /**
     * An {@link ExtendedXAResource} that does not override {@code getCommitPriority()} must return
     * {@link CommitPriority#NORMAL} as the default.
     *
     * @see ExtendedXAResource#getCommitPriority()
     */
    @Test
    public void defaultCommitPriorityIsNormal() {
        assertEquals(CommitPriority.NORMAL, DEFAULT_RESOURCE.getCommitPriority(),
                "Default getCommitPriority() must return NORMAL");
    }

    // -----------------------------------------------------------------------
    // Overridden prepare priority tests
    // -----------------------------------------------------------------------

    /**
     * A resource adapter implementing LRCO must be able to return {@link PreparePriority#EXCLUSIVE_LAST} from
     * {@code getPreparePriority()}.
     */
    @Test
    public void preparePriorityExclusiveLastCanBeReturned() {
        assertEquals(PreparePriority.EXCLUSIVE_LAST, LRCO_RESOURCE.getPreparePriority(),
                "LRCO resource must return EXCLUSIVE_LAST from getPreparePriority()");
    }

    /**
     * A resource adapter that signals it is likely read-only must be able to return {@link PreparePriority#EARLY} from
     * {@code getPreparePriority()}.
     */
    @Test
    public void preparePriorityEarlyCanBeReturned() {
        assertEquals(PreparePriority.EARLY, EARLY_PREPARE_RESOURCE.getPreparePriority(),
                "Likely-read-only resource must return EARLY from getPreparePriority()");
    }

    // -----------------------------------------------------------------------
    // Overridden commit priority tests
    // -----------------------------------------------------------------------

    /**
     * A resource adapter (e.g. a JMS broker) that must commit after other resources must be able to return
     * {@link CommitPriority#LATE} from {@code getCommitPriority()}.
     */
    @Test
    public void commitPriorityLateCanBeReturned() {
        assertEquals(CommitPriority.LATE, LATE_COMMIT_RESOURCE.getCommitPriority(),
                "JMS-style resource must return LATE from getCommitPriority()");
    }

    /**
     * A resource adapter that must commit before all others must be able to return {@link CommitPriority#EXCLUSIVE_FIRST}
     * from {@code getCommitPriority()}.
     */
    @Test
    public void commitPriorityExclusiveFirstCanBeReturned() {
        assertEquals(CommitPriority.EXCLUSIVE_FIRST, FIRST_COMMIT_RESOURCE.getCommitPriority(),
                "First-commit resource must return EXCLUSIVE_FIRST from getCommitPriority()");
    }

    // -----------------------------------------------------------------------
    // Enum completeness tests — guard against accidental enum value removal
    // -----------------------------------------------------------------------

    /**
     * {@link PreparePriority} must define exactly the three values: EARLY, NORMAL, EXCLUSIVE_LAST.
     */
    @Test
    public void preparePriorityEnumValues() {
        PreparePriority[] values = PreparePriority.values();
        assertEquals(3, values.length, "PreparePriority must have exactly 3 values");
        assertEquals(PreparePriority.EARLY, values[0]);
        assertEquals(PreparePriority.NORMAL, values[1]);
        assertEquals(PreparePriority.EXCLUSIVE_LAST, values[2]);
    }

    /**
     * {@link CommitPriority} must define exactly the three values: EXCLUSIVE_FIRST, NORMAL, LATE.
     */
    @Test
    public void commitPriorityEnumValues() {
        CommitPriority[] values = CommitPriority.values();
        assertEquals(3, values.length, "CommitPriority must have exactly 3 values");
        assertEquals(CommitPriority.EXCLUSIVE_FIRST, values[0]);
        assertEquals(CommitPriority.NORMAL, values[1]);
        assertEquals(CommitPriority.LATE, values[2]);
    }

    // -----------------------------------------------------------------------
    // Helper — minimal no-op XAResource + ExtendedXAResource implementation
    // -----------------------------------------------------------------------

    /**
     * A do-nothing base implementation of {@link ExtendedXAResource} used to test the interface's default methods without
     * requiring a real resource manager.
     */
    private static class MinimalExtendedXAResource implements ExtendedXAResource {

        @Override
        public boolean setReadOnly(Xid xid) throws XAException {
            return false;
        }

        @Override
        public void commit(Xid xid, boolean onePhase) throws XAException {
        }

        @Override
        public void end(Xid xid, int flags) throws XAException {
        }

        @Override
        public void forget(Xid xid) throws XAException {
        }

        @Override
        public int getTransactionTimeout() throws XAException {
            return 0;
        }

        @Override
        public boolean isSameRM(XAResource xaResource) throws XAException {
            return this == xaResource;
        }

        @Override
        public int prepare(Xid xid) throws XAException {
            return XAResource.XA_OK;
        }

        @Override
        public Xid[] recover(int flag) throws XAException {
            return new Xid[0];
        }

        @Override
        public void rollback(Xid xid) throws XAException {
        }

        @Override
        public boolean setTransactionTimeout(int seconds) throws XAException {
            return false;
        }

        @Override
        public void start(Xid xid, int flags) throws XAException {
        }
    }
}
