package com.ss.rlib.common.network.impl;

import static java.nio.ByteBuffer.allocate;
import static java.nio.ByteBuffer.allocateDirect;
import static java.nio.ByteOrder.LITTLE_ENDIAN;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.network.AsyncNetwork;
import com.ss.rlib.common.network.NetworkConfig;
import com.ss.rlib.common.network.packet.ReadablePacketRegistry;
import com.ss.rlib.common.util.pools.PoolFactory;
import org.jetbrains.annotations.NotNull;
import com.ss.rlib.common.util.pools.Pool;

import java.nio.ByteBuffer;
import java.util.function.Function;

/**
 * The base implementation of {@link AsyncNetwork}.
 *
 * @author JavaSaBr
 */
public abstract class AbstractAsyncNetwork implements AsyncNetwork {

    protected static final Logger LOGGER = LoggerManager.getLogger(AsyncNetwork.class);

    /**
     * The pool with read buffers.
     */
    @NotNull
    protected final Pool<ByteBuffer> readBufferPool;

    /**
     * The pool with wait buffers.
     */
    @NotNull
    protected final Pool<ByteBuffer> waitBufferPool;

    /**
     * The pool with write buffers.
     */
    @NotNull
    protected final Pool<ByteBuffer> writeBufferPool;

    /**
     * The readable packet registry.
     */
    @NotNull
    protected final ReadablePacketRegistry registry;

    /**
     * The network config.
     */
    @NotNull
    protected final NetworkConfig config;

    protected AbstractAsyncNetwork(@NotNull NetworkConfig config, @NotNull ReadablePacketRegistry registry) {
        this.config = config;
        this.registry = registry;
        this.readBufferPool = PoolFactory.newConcurrentAtomicARSWLockPool(ByteBuffer.class);
        this.waitBufferPool = PoolFactory.newConcurrentAtomicARSWLockPool(ByteBuffer.class);
        this.writeBufferPool = PoolFactory.newConcurrentAtomicARSWLockPool(ByteBuffer.class);
    }

    @Override
    public @NotNull ReadablePacketRegistry getPacketRegistry() {
        return registry;
    }

    @Override
    public @NotNull NetworkConfig getConfig() {
        return config;
    }

    @Override
    public @NotNull ByteBuffer takeReadBuffer() {
        ByteBuffer buffer = readBufferPool.take(config, readBufferFactory());
        buffer.clear();
        return buffer;
    }

    /**
     * Get the read buffers factory.
     *
     * @return the read buffers factory.
     */
    protected @NotNull Function<NetworkConfig, ByteBuffer> readBufferFactory() {
        return conf -> (conf.isDirectByteBuffer() ?
                allocateDirect(conf.getReadBufferSize()) : allocate(conf.getReadBufferSize())).order(LITTLE_ENDIAN);
    }

    @Override
    public @NotNull ByteBuffer takeWaitBuffer() {
        ByteBuffer buffer = writeBufferPool.take(config, waitBufferFactory());
        buffer.clear();
        return buffer;
    }

    /**
     * Get the wait buffers factory.
     *
     * @return the wait buffers factory.
     */
    protected @NotNull Function<NetworkConfig, ByteBuffer> waitBufferFactory() {
        return conf -> (conf.isDirectByteBuffer() ?
                allocateDirect(conf.getReadBufferSize() * 2) : allocate(conf.getReadBufferSize() * 2)).order(LITTLE_ENDIAN);
    }

    @Override
    public @NotNull ByteBuffer takeWriteBuffer() {
        ByteBuffer buffer = writeBufferPool.take(config, writeBufferFactory());
        buffer.clear();
        return buffer;
    }

    /**
     * Get the write buffers factory.
     *
     * @return the write buffers factory.
     */
    protected Function<NetworkConfig, ByteBuffer> writeBufferFactory() {
        return conf -> (conf.isDirectByteBuffer() ?
                allocateDirect(conf.getWriteBufferSize()) : allocate(conf.getWriteBufferSize())).order(LITTLE_ENDIAN);
    }

    @Override
    public void putReadBuffer(@NotNull ByteBuffer buffer) {
        readBufferPool.put(buffer);
    }

    @Override
    public void putWaitBuffer(@NotNull ByteBuffer buffer) {
        waitBufferPool.put(buffer);
    }

    @Override
    public void putWriteBuffer(@NotNull ByteBuffer buffer) {
        writeBufferPool.put(buffer);
    }

    @Override
    public String toString() {
        return "AbstractAsynchronousNetwork{" +
                "config=" + config +
                '}';
    }
}
