package cn.nukkit.network;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.network.connection.BedrockPeer;
import cn.nukkit.network.connection.BedrockPong;
import cn.nukkit.network.connection.BedrockSession;
import cn.nukkit.network.connection.netty.initializer.BedrockServerInitializer;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.query.codec.QueryPacketCodec;
import cn.nukkit.network.query.handler.QueryPacketHandler;
import cn.nukkit.utils.Utils;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueDatagramChannel;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.netty.channel.raknet.RakChannelFactory;
import org.cloudburstmc.netty.channel.raknet.RakServerChannel;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.jetbrains.annotations.Nullable;
import oshi.SystemInfo;
import oshi.hardware.NetworkIF;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Slf4j
public class Network {
    private final Server server;
    private final LinkedList<NetWorkStatisticData> netWorkStatisticDataList = new LinkedList<>();
    @Nullable
    private final List<NetworkIF> hardWareNetworkInterfaces;
    private final Map<InetSocketAddress, BedrockSession> sessionMap = new ConcurrentHashMap<>();
    private final Map<InetAddress, Long> blockIpMap = new HashMap<>();
    private final RakServerChannel channel;
    private BedrockPong pong;

    public Network(Server server) {
        this(server, Runtime.getRuntime().availableProcessors(), new ThreadFactoryBuilder().setNameFormat("Netty Server IO #%d").build());
    }

    public Network(Server server, int nettyThreadNumber, ThreadFactory threadFactory) {
        this.server = server;
        List<NetworkIF> tmpIfs = null;
        try {
            tmpIfs = new SystemInfo().getHardware().getNetworkIFs();
        } catch (Throwable t) {
            log.warn(Server.getInstance().getLanguage().get("nukkit.start.hardwareMonitorDisabled"));
        } finally {
            this.hardWareNetworkInterfaces = tmpIfs;
        }

        Class<? extends DatagramChannel> oclass;
        EventLoopGroup eventloopgroup;
        if (Epoll.isAvailable()) {
            oclass = EpollDatagramChannel.class;
            eventloopgroup = new EpollEventLoopGroup(nettyThreadNumber, threadFactory);
        } else if (KQueue.isAvailable()) {
            oclass = KQueueDatagramChannel.class;
            eventloopgroup = new KQueueEventLoopGroup(nettyThreadNumber, threadFactory);
        } else {
            oclass = NioDatagramChannel.class;
            eventloopgroup = new NioEventLoopGroup(nettyThreadNumber, threadFactory);
        }
        InetSocketAddress bindAddress = new InetSocketAddress(Strings.isNullOrEmpty(this.server.getIp()) ? "0.0.0.0" : this.server.getIp(), this.server.getPort());

        this.pong = new BedrockPong()
                .edition("MCPE")
                .motd(server.getMotd())
                .subMotd(server.getSubMotd())
                .playerCount(server.getOnlinePlayers().size())
                .maximumPlayerCount(server.getMaxPlayers())
                .serverId(1)
                .gameType(Server.getGamemodeString(server.getDefaultGamemode(), true))
                .nintendoLimited(false)
                .protocolVersion(ProtocolInfo.CURRENT_PROTOCOL)
                .ipv4Port(server.getPort())
                .ipv6Port(server.getPort());

        this.channel = (RakServerChannel) new ServerBootstrap()
                .channelFactory(RakChannelFactory.server(oclass))
                .option(RakChannelOption.RAK_ADVERTISEMENT, pong.toByteBuf())
                .group(eventloopgroup)
                .childHandler(new BedrockServerInitializer() {
                    @Override
                    protected void postInitChannel(Channel channel) {
                        if (Network.this.server.getPropertyBoolean("enable-query", true)) {
                            channel.pipeline().addLast("queryPacketCodec", new QueryPacketCodec())
                                    .addLast("queryPacketHandler", new QueryPacketHandler(address -> Network.this.server.getQueryInformation()));
                        }
                    }

                    @Override
                    protected BedrockPeer createPeer(Channel channel) {
                        return super.createPeer(channel);
                    }

                    @Override
                    public BedrockSession createSession0(BedrockPeer peer, int subClientId) {
                        BedrockSession session = new BedrockSession(peer, subClientId);
                        InetSocketAddress address = (InetSocketAddress) session.getSocketAddress();
                        Network.this.sessionMap.put(address, session);
                        if (blockIpMap.containsKey(address.getAddress())) {
                            session.close("Your IP address has been blocked by this server!");
                            Network.this.sessionMap.remove(address);
                        }
                        return session;
                    }

                    @Override
                    protected void initSession(BedrockSession session) {
                        session.setLogging(true);
                    }
                })
                .bind(bindAddress)
                .syncUninterruptibly()
                .channel();
        this.pong.channel(channel);
    }

    record NetWorkStatisticData(long upload, long download) {
    }

    public void shutdown() {
        this.channel.close();
        this.pong = null;
        this.sessionMap.clear();
        this.netWorkStatisticDataList.clear();
    }

    public double getUpload() {
        return netWorkStatisticDataList.get(1).upload - netWorkStatisticDataList.get(0).upload;
    }

    public double getDownload() {
        return netWorkStatisticDataList.get(1).download - netWorkStatisticDataList.get(0).download;
    }

    public void resetStatistics() {
        long upload = 0;
        long download = 0;
        if (netWorkStatisticDataList.size() > 1) {
            netWorkStatisticDataList.removeFirst();
        }
        if (this.hardWareNetworkInterfaces != null) {
            for (var networkIF : this.hardWareNetworkInterfaces) {
                networkIF.updateAttributes();
                upload += networkIF.getBytesSent();
                download += networkIF.getBytesRecv();
            }
        }
        netWorkStatisticDataList.add(new NetWorkStatisticData(upload, download));
    }

    public void processInterfaces() {
        try {
            this.process();
        } catch (Exception e) {
            log.error(this.server.getLanguage().tr("nukkit.server.networkError", this.getClass().getName(), Utils.getExceptionMessage(e)), e);
        }
    }

    public Server getServer() {
        return server;
    }

    public @Nullable List<NetworkIF> getHardWareNetworkInterfaces() {
        return hardWareNetworkInterfaces;
    }


    public int getNetworkLatency(Player player) {
        var session = this.sessionMap.get(player.getRawSocketAddress());
        return session == null ? -1 : (int) session.getPing();
    }

    public void blockAddress(InetAddress address) {
        blockIpMap.put(address, -1L);
    }

    public void blockAddress(InetAddress address, int timeout) {
        blockIpMap.put(address, (long) timeout);
    }

    public void unblockAddress(InetAddress address) {
        blockIpMap.remove(address);
    }

    public BedrockSession getSession(InetSocketAddress address) {
        return this.sessionMap.get(address);
    }

    public void process() {
        for (BedrockSession session : this.sessionMap.values()) {
            if (session.isDisconnected()) {
                return;
            }
            session.tick();
        }
    }

    public void onSessionDisconnect(InetSocketAddress address) {
        this.sessionMap.remove(address);
    }

    public BedrockPong getPong() {
        return pong;
    }
}
