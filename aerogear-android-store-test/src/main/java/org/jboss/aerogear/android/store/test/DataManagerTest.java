/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.aerogear.android.store.test;


import org.jboss.aerogear.android.core.ConfigurationProvider;
import org.jboss.aerogear.android.store.test.util.PatchedActivityInstrumentationTestCase;
import org.jboss.aerogear.android.store.DataManager;
import org.jboss.aerogear.android.store.Store;
import org.jboss.aerogear.android.store.StoreConfiguration;
import org.jboss.aerogear.android.store.memory.MemoryStoreConfiguration;

public class DataManagerTest extends PatchedActivityInstrumentationTestCase<MainActivity> {

    public DataManagerTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testCreateStore() {
        Store store = DataManager
                .config("foo1", MemoryStoreConfiguration.class)
                .store();

        assertNotNull("store could not be null", store);
    }

    public void testGetStore() {
        DataManager
                .config("foo2", MemoryStoreConfiguration.class)
                .store();

        Store store = DataManager.getStore("foo2");

        assertNotNull("store could not be null", store);
    }

    public void testCreateMoreThanOneStoreInDataManager() {
        DataManager
                .config("foo4", MemoryStoreConfiguration.class)
                .store();

        DataManager
                .config("foo5", MemoryStoreConfiguration.class)
                .store();

        Store store1 = DataManager.getStore("foo4");
        Store store2 = DataManager.getStore("foo5");

        assertNotNull("store could not be null", store1);
        assertNotNull("store could not be null", store2);
    }

    public void testAddNewProvider() {

        DataManager.registerConfigurationProvider(StubStoreConfiguration.class, new DummyStoreConfigProvider());
        StubStoreConfiguration config = DataManager.config("test", StubStoreConfiguration.class);
        assertNotNull(config);

    }

    private static final class DummyStoreConfigProvider implements ConfigurationProvider<StubStoreConfiguration> {
        @Override
        public StubStoreConfiguration newConfiguration() {
            return new StubStoreConfiguration();
        }
    }

    private static class StubStoreConfiguration extends StoreConfiguration<StubStoreConfiguration> {
        public StubStoreConfiguration() {
        }

        @Override
        protected Store buildStore() {
            return null;
        }
    }

}
