package cn.nukkit.network.query.handler;

import cn.nukkit.event.server.QueryRegenerateEvent;
import cn.nukkit.network.query.QueryEventListener;
import cn.nukkit.network.query.enveloped.DirectAddressedQueryPacket;
import cn.nukkit.network.query.packet.HandshakePacket;
import cn.nukkit.network.query.packet.StatisticsPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Timer;

public class QueryPacketHandler extends SimpleChannelInboundHandler<DirectAddressedQueryPacket> {
    private final QueryEventListener listener;
    private final Timer timer;
    private byte[] lastToken;
    private byte[] token;

    public QueryPacketHandler(QueryEventListener listener) {
        this.listener = listener;
        this.timer = new Timer("QueryRegenerationTicker");

        this.token = this.generateToken();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DirectAddressedQueryPacket packet) {
        if (packet.content() instanceof HandshakePacket handshake) {
            handshake.setToken(getTokenString(packet.sender()));
            ctx.writeAndFlush(new DirectAddressedQueryPacket(handshake, packet.sender(), packet.recipient()), ctx.voidPromise());
        }
        if (packet.content() instanceof StatisticsPacket statistics) {
            if (statistics.getToken() != getTokenInt(packet.sender())) {
                return;
            }

            QueryRegenerateEvent data = this.listener.onQuery(packet.sender());

            if (statistics.isFull()) {
                statistics.setPayload(data.getLongQuery());
            } else {
                statistics.setPayload(data.getShortQuery());
            }
            ctx.writeAndFlush(new DirectAddressedQueryPacket(statistics, packet.sender(), packet.recipient()), ctx.voidPromise());
        }
    }

    private byte[] generateToken() {
        byte[] bytes = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);
        return bytes;
    }

    public void refreshToken() {
        this.lastToken = this.token;
        this.token = this.generateToken();
    }

    private String getTokenString(InetSocketAddress socketAddress) {
        return Integer.toString(getTokenInt(socketAddress));

    }

    private int getTokenInt(InetSocketAddress socketAddress) {
        return ByteBuffer.wrap(getToken(socketAddress)).getInt();
    }

    private byte[] getToken(InetSocketAddress socketAddress) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException var3) {
            throw new InternalError("SHA-512 not supported", var3);
        }

        digest.update(socketAddress.toString().getBytes());
        byte[] digested = digest.digest(this.token);
        return Arrays.copyOf(digested, 4);
    }
}
