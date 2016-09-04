package rlib.util.dictionary;

import rlib.concurrent.lock.AsyncReadSyncWriteLock;
import rlib.concurrent.lock.LockFactory;

/**
 * Реализация конкурентного словаря с примитивным ключем int, где миханизм синхранизации выступают
 * атомарные счетчики.
 *
 * @author JavaSaBr
 */
public class ConcurrentAtomicIntegerDictionary<V> extends ConcurrentReentrantReadWriteLockIntegerDictionary<V> {

    public ConcurrentAtomicIntegerDictionary() {
        super();
    }

    public ConcurrentAtomicIntegerDictionary(float loadFactor, int initCapacity) {
        super(loadFactor, initCapacity);
    }

    public ConcurrentAtomicIntegerDictionary(float loadFactor) {
        super(loadFactor);
    }

    public ConcurrentAtomicIntegerDictionary(int initCapacity) {
        super(initCapacity);
    }

    @Override
    protected AsyncReadSyncWriteLock createLocker() {
        return LockFactory.newPrimitiveAtomicARSWLock();
    }
}
