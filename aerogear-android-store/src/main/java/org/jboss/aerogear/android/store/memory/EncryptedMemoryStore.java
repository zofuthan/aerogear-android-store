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
package org.jboss.aerogear.android.store.memory;

import org.jboss.aerogear.AeroGearCrypto;
import org.jboss.aerogear.android.core.ReadFilter;
import org.jboss.aerogear.android.store.generator.IdGenerator;
import org.jboss.aerogear.android.store.Store;
import org.jboss.aerogear.android.security.InvalidKeyException;
import org.jboss.aerogear.android.security.util.CryptoUtils;
import org.jboss.aerogear.crypto.RandomUtils;
import org.jboss.aerogear.crypto.keys.PrivateKey;
import org.jboss.aerogear.crypto.password.Pbkdf2;

import java.io.Serializable;
import java.security.spec.InvalidKeySpecException;
import java.util.Collection;
import java.util.List;

public class EncryptedMemoryStore<T> implements Store<T> {

    private final MemoryStore<byte[]> memoryStore;
    private final CryptoUtils<T> cryptoUtils;

    public EncryptedMemoryStore(IdGenerator idGenerator, String passphrase, Class<T> modelClass) {
        memoryStore = new MemoryStore(idGenerator);

        byte[] iv = RandomUtils.randomBytes();
        byte[] salt = RandomUtils.randomBytes();
        byte[] rawPassword = new byte[0];

        try {
            Pbkdf2 pbkdf2 = AeroGearCrypto.pbkdf2();
            rawPassword = pbkdf2.encrypt(passphrase, salt);
        } catch (InvalidKeySpecException e) {
        }

        PrivateKey privateKey = new PrivateKey(rawPassword);
        cryptoUtils = new CryptoUtils<T>(privateKey, iv, modelClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<T> readAll() throws InvalidKeyException {
        Collection<byte[]> encryptedCollection = memoryStore.readAll();
        return cryptoUtils.decrypt(encryptedCollection);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T read(Serializable id) throws InvalidKeyException {
        byte[] encryptedItem = memoryStore.read(id);
        if (encryptedItem == null) {
            return null;
        } else {
            return cryptoUtils.decrypt(encryptedItem);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> readWithFilter(ReadFilter filter) throws InvalidKeyException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(T item) {
        Serializable idValue = memoryStore.getOrGenerateIdValue(item);
        memoryStore.save(idValue, cryptoUtils.encrypt(item));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(Collection<T> items) {
        for (T item : items) {
            save(item);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        memoryStore.reset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(Serializable id) {
        memoryStore.remove(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return memoryStore.isEmpty();
    }

}
