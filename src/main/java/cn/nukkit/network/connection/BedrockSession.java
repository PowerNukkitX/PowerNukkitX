package cn.nukkit.network.connection;

import cn.nukkit.network.connection.netty.BedrockPacketWrapper;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.types.PacketCompressionAlgorithm;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.crypto.SecretKey;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BedrockSession {
    private static final InternalLogger log = InternalLoggerFactory.getInstance(BedrockSession.class);

    private final AtomicBoolean closed = new AtomicBoolean();
    protected final BedrockPeer peer;
    protected final int subClientId;
    protected boolean logging;
    protected String disconnectReason;
    private final Queue<DataPacket> inbound = PlatformDependent.newSpscQueue();

    public BedrockSession(BedrockPeer peer, int subClientId) {
        this.peer = peer;
        this.subClientId = subClientId;
    }

    protected void checkForClosed() {
        if (this.closed.get()) {
            throw new IllegalStateException("Session has been closed");
        }
    }

    public void sendPacket(@NonNull DataPacket packet) {
        this.peer.sendPacket(this.subClientId, 0, packet);
        this.logOutbound(packet);
    }

    public void sendPacketImmediately(@NonNull DataPacket packet) {
        this.peer.sendPacketImmediately(this.subClientId, 0, packet);
        this.logOutbound(packet);
    }

    public void sendPacketImmediatelyAndCallBack(@NonNull DataPacket packet, Runnable runnable) {
        this.peer.sendPacketImmediatelyAndCallBack(this.subClientId, 0, packet, runnable);
        this.logOutbound(packet);
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
        inbound.add(packet);
    }

    public List<DataPacket> readPackets() {
        DataPacket packet;
        var list = new ArrayList<DataPacket>(this.inbound.size());
        while ((packet = this.inbound.poll()) != null) {
            list.add(packet);
        }
        this.inbound.clear();
        return list;
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

    public void setDisconnectReason(String disconnectReason) {
        this.disconnectReason = disconnectReason;
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
