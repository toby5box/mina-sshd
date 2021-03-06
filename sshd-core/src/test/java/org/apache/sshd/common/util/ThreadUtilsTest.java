/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.sshd.common.util;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.sshd.util.BaseTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:dev@mina.apache.org">Apache MINA SSHD Project</a>
 */
public class ThreadUtilsTest extends BaseTest {
    public ThreadUtilsTest() {
        super();
    }

    @Test
    public void testProtectExecutorServiceShutdown() {
        for (boolean shutdownOnExit : new boolean[] { true, false }) {
            Assert.assertNull("Unexpected instance for shutdown=" + shutdownOnExit, ThreadUtils.protectExecutorServiceShutdown(null, shutdownOnExit));
        }

        ExecutorService service=Executors.newSingleThreadExecutor();
        try {
            Assert.assertSame("Unexpected wrapped instance", service, ThreadUtils.protectExecutorServiceShutdown(service, true));
            
            ExecutorService wrapped=ThreadUtils.protectExecutorServiceShutdown(service, false);
            try {
                Assert.assertNotSame("No wrapping occurred", service, wrapped);

                wrapped.shutdown();
                Assert.assertTrue("Wrapped service not shutdown", wrapped.isShutdown());
                Assert.assertFalse("Protected service is shutdown", service.isShutdown());
                
                Collection<?>   running=wrapped.shutdownNow();
                Assert.assertTrue("Non-empty runners list", running.isEmpty());
                Assert.assertTrue("Wrapped service not shutdownNow", wrapped.isShutdown());
                Assert.assertFalse("Protected service is shutdownNow", service.isShutdown());
            } finally {
                wrapped.shutdownNow();  // just in case
            }
        } finally {
            service.shutdownNow();  // just in case...
        }
    }
}
