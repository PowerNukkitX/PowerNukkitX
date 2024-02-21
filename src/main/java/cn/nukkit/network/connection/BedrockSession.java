package cn.nukkit.network.connection;

import cn.nukkit.network.connection.netty.BedrockBatchWrapper;
import cn.nukkit.network.connection.netty.BedrockPacketWrapper;
import cn.nukkit.network.connection.netty.codec.packet.BedrockPacketCodec;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.NetworkSettingsPacket;
import cn.nukkit.network.protocol.types.PacketCompressionAlgorithm;
import cn.nukkit.utils.ByteBufVarInt;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.crypto.SecretKey;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public abstract class BedrockSession {
    private static final InternalLogger log = InternalLoggerFactory.getInstance(BedrockSession.class);

    private final AtomicBoolean closed = new AtomicBoolean();
    protected final BedrockPeer peer;
    protected final int subClientId;
    protected boolean logging;
    protected String disconnectReason;
    private final Queue<DataPacket> inbound = PlatformDependent.newSpscQueue();
    private final AtomicBoolean nettyThreadOwned = new AtomicBoolean(false);
    private final AtomicReference<Consumer<DataPacket>> consumer = new AtomicReference<>(null);

    public BedrockSession(BedrockPeer peer, int subClientId) {
        this.peer = peer;
        this.subClientId = subClientId;
    }

    public void setNettyThreadOwned(boolean immediatelyHandle) {
        this.nettyThreadOwned.set(immediatelyHandle);
    }

    public boolean isNettyThreadOwned() {
        return this.nettyThreadOwned.get();
    }

    public void setPacketConsumer(Consumer<DataPacket> consumer) {
        this.consumer.set(consumer);
    }

    protected void checkForClosed() {
        if (this.closed.get()) {
            throw new IllegalStateException("Session has been closed");
        }
    }

    public void flush() {
        this.peer.flush();
    }

    public void sendPacket(@NonNull DataPacket packet) {
        this.peer.sendPacket(this.subClientId, 0, packet);
        this.logOutbound(packet);
    }

    public void sendPacketImmediately(@NonNull DataPacket packet) {
        this.peer.sendPacketImmediately(this.subClientId, 0, packet);
        this.logOutbound(packet);
    }

    public void sendNetworkSettingsPacket(@NonNull NetworkSettingsPacket pk) {
        //TODO WTF
        ByteBufAllocator alloc = this.peer.channel.alloc();
        ByteBuf buf1 = alloc.buffer(16);
        ByteBuf header = alloc.ioBuffer(5);
        BedrockPacketWrapper msg = new BedrockPacketWrapper(0, subClientId, 0, pk, null);
        try {
            BedrockPacketCodec bedrockPacketCodec = this.peer.channel.pipeline().get(BedrockPacketCodec.class);
            DataPacket packet = msg.getPacket();
            msg.setPacketId(packet.pid());
            bedrockPacketCodec.encodeHeader(buf1, msg);
            packet.tryEncode();
            buf1.writeBytes(packet.getBuffer());

            BedrockBatchWrapper batch = BedrockBatchWrapper.newInstance();
            CompositeByteBuf buf2 = alloc.compositeDirectBuffer(2);
            ByteBufVarInt.writeUnsignedInt(header, buf1.readableBytes());
            buf2.addComponent(true, header);
            buf2.addComponent(true, buf1);
            batch.setCompressed(buf2);
            this.peer.channel.writeAndFlush(batch);
        } catch (Throwable t) {
            log.error("Error send", t);
        } finally {
            msg.release();
        }
    }

    public void flushSendBuffer() {
        this.peer.flushSendQueue();
    }

    public BedrockPeer getPeer() {
        return peer;
    }

    public void setCompression(PacketCompressionAlgorithm algorithm) {
        if (isSubClient()) {
            throw new IllegalStateException("The compression algorithm can only be set by the primary session");
        }
        this.peer.setCompression(algorithm);
    }

    public void enableEncryption(SecretKey key) {
        if (isSubClient()) {
            throw new IllegalStateException("Encryption can only be enabled by the primary session");
        }
        this.peer.enableEncryption(key);
    }

    public void close(String reason) {
        checkForClosed();

        if (isSubClient()) {
            // FIXME: Do sub-clients send a server-bound DisconnectPacket?
        } else {
            // Primary sub-client controls the connection
            this.peer.close(reason);
        }
    }

    protected void onClose() {
        if (!this.closed.compareAndSet(false, true)) {
            return;
        }
        this.peer.removeSession(this);
    }

    protected void onPacket(BedrockPacketWrapper wrapper) {
        DataPacket packet = wrapper.getPacket();
        if (this.nettyThreadOwned.get()) {
            var c = this.consumer.get();
            if (c != null) {
                c.accept(packet);
            }
        } else {
            inbound.add(packet);
        }
    }

    public void tick() {
        DataPacket packet;
        var c = this.consumer.get();
        while ((packet = this.inbound.poll()) != null) {
            if (c != null) {
                c.accept(packet);
            }
        }
    }

    protected void logOutbound(DataPacket packet) {/*
        if (log.isTraceEnabled() && this.logging) {
            log.trace("Outbound {}{}: {}", this.getSocketAddress(), this.subClientId, packet);
        }*/
    }

    protected void logInbound(DataPacket packet) {/*
        if (log.isTraceEnabled() && this.logging) {
            log.trace("Inbound {}{}: {}", this.getSocketAddress(), this.subClientId, packet);
        }*/
    }

    public SocketAddress getSocketAddress() {
        return peer.getSocketAddress();
    }

    public boolean isSubClient() {
        return this.subClientId != 0;
    }

    public boolean isLogging() {
        return logging;
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    public String getDisconnectReason() {
        return disconnectReason;
    }

    public boolean isDisconnected() {
        return this.closed.get();
    }

    public final void disconnect() {
        disconnect("disconnect.disconnected");
    }

    public final void disconnect(String reason) {
        this.disconnect(reason, false);
    }

    public abstract void disconnect(String reason, boolean hideReason);

    public boolean isConnected() {
        return !this.closed.get();
    }

    public long getPing() {
        return peer.getPing();
    }
}
