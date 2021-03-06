package com.ss.rlib.common.network.server.impl;

import com.ss.rlib.common.concurrent.GroupThreadFactory;
import com.ss.rlib.common.network.NetworkConfig;
import com.ss.rlib.common.network.impl.AbstractAsyncNetwork;
import com.ss.rlib.common.network.packet.ReadablePacketRegistry;
import com.ss.rlib.common.network.server.AcceptHandler;
import com.ss.rlib.common.network.server.ServerNetwork;
import com.ss.rlib.common.network.server.client.Client;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.function.Consumer;

/**
 * The base implementation of {@link ServerNetwork}.
 *
 * @author JavaSaBr
 */
public final class DefaultServerNetwork extends AbstractAsyncNetwork implements ServerNetwork {

    /**
     * The asynchronous channel group.
     */
    @NotNull
    private final AsynchronousChannelGroup group;

    /**
     * The asynchronous server socket channel.
     */
    @NotNull
    private final AsynchronousServerSocketChannel channel;

    /**
     * The accept handler.
     */
    @NotNull
    private final AcceptHandler acceptHandler;

    /**
     * The destroyed handler.
     */
    @Nullable
    private Consumer<@NotNull Client> destroyedHandler;

    public DefaultServerNetwork(
            @NotNull NetworkConfig config,
            @NotNull ReadablePacketRegistry packetRegistry,
            @NotNull AcceptHandler acceptHandler
    ) throws IOException {

        super(config, packetRegistry);

        this.group = AsynchronousChannelGroup.withFixedThreadPool(config.getGroupSize(),
                new GroupThreadFactory(config.getGroupName(), config.getThreadClass(), config.getThreadPriority()));

        this.channel = AsynchronousServerSocketChannel.open(group);
        this.acceptHandler = acceptHandler;
    }

    @Override
    public <A> void accept(
            @Nullable A attachment,
            @NotNull CompletionHandler<AsynchronousSocketChannel, ? super A> handler
    ) {
        channel.accept(attachment, handler);
    }

    @Override
    public void bind(@NotNull SocketAddress address) throws IOException {
        channel.bind(address);
        channel.accept(this, acceptHandler);
    }

    @Override
    public void shutdown() {
        group.shutdown();
    }

    @Override
    public void setDestroyedHandler(@Nullable Consumer<@NotNull Client> destroyedHandler) {
        this.destroyedHandler = destroyedHandler;
    }

    @Override
    public void onDestroyed(@NotNull Client client) {
        if (destroyedHandler != null) {
            destroyedHandler.accept(client);
        }
    }

    @Override
    public String toString() {
        return "DefaultServerNetwork{" +
                "group=" + group +
                ", channel=" + channel +
                ", acceptHandler=" + acceptHandler +
                "} " + super.toString();
    }
}
