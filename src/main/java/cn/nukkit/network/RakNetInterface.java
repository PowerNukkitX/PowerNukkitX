package cn.nukkit.network;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.server.QueryRegenerateEvent;
import cn.nukkit.network.connection.BedrockPeer;
import cn.nukkit.network.connection.BedrockPong;
import cn.nukkit.network.connection.BedrockServerSession;
import cn.nukkit.network.connection.netty.initializer.BedrockServerInitializer;
import cn.nukkit.network.process.NetworkSession;
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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;

@Slf4j
public class RakNetInterface implements SourceInterface {
    private final Map<InetSocketAddress, NetworkSession> serverSessionMap = new ConcurrentHashMap<>();
    private final Map<InetAddress, Long> blockIpMap = new HashMap<>();
    private final RakServerChannel channel;
    private final Server server;
    private BedrockPong pong;
    private Network network;

    public RakNetInterface(Server server) {
        this(server, Runtime.getRuntime().availableProcessors(), new ThreadFactoryBuilder().setNameFormat("Netty Server IO #%d").build());
    }

    public RakNetInterface(Server server, int nettyThreadNumber, ThreadFactory threadFactory) {
        this.pong = new BedrockPong()
                .edition("MCPE")
                .motd(server.getMotd())
                .subMotd(server.getSubMotd())
                .playerCount(server.getOnlinePlayers().size())
                .maximumPlayerCount(server.getMaxPlayers())
                .gameType(Server.getGamemodeString(server.getDefaultGamemode(), true))
                .protocolVersion(ProtocolInfo.CURRENT_PROTOCOL)
                .ipv4Port(server.getPort())
                .ipv6Port(server.getPort());

        this.server = server;
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
        this.channel = (RakServerChannel) new ServerBootstrap()
                .channelFactory(RakChannelFactory.server(oclass))
                .option(RakChannelOption.RAK_ADVERTISEMENT, pong.toByteBuf())
                .group(eventloopgroup)
                .childHandler(new BedrockServerInitializer() {
                    @Override
                    protected void postInitChannel(Channel channel) {
                        if (RakNetInterface.this.server.getPropertyBoolean("enable-query", true)) {
                            channel.pipeline().addLast("queryPacketCodec", new QueryPacketCodec())
                                    .addLast("queryPacketHandler", new QueryPacketHandler(address -> RakNetInterface.this.server.getQueryInformation()));
                        }
                    }

                    @Override
                    protected BedrockPeer createPeer(Channel channel) {
                        return super.createPeer(channel);
                    }

                    @Override
                    public BedrockServerSession createSession0(BedrockPeer peer, int subClientId) {
                        BedrockServerSession s = new BedrockServerSession(peer, subClientId);
                        var session = new NetworkSession(s);
                        InetSocketAddress address = (InetSocketAddress) session.getSession().getSocketAddress();
                        RakNetInterface.this.serverSessionMap.put(address, session);
                        if (blockIpMap.containsKey(address.getAddress())) {
                            session.disconnect("Your IP address has been blocked by this server!");
                            RakNetInterface.this.serverSessionMap.remove(address);
                        }
                        return s;
                    }

                    @Override
                    protected void initSession(BedrockServerSession session) {
                        session.setLogging(true);
                    }
                })
                .bind(bindAddress)
                .syncUninterruptibly()
                .channel();
    }


    @Override
    public int getNetworkLatency(Player player) {
        var session = this.serverSessionMap.get(player.getRawSocketAddress());
        return session == null ? -1 : (int) session.getSession().getPing();
    }

    @Override
    public void blockAddress(InetAddress address) {
        blockIpMap.put(address, -1L);
    }

    @Override
    public void blockAddress(InetAddress address, int timeout) {
        blockIpMap.put(address, (long) timeout);
    }

    @Override
    public void unblockAddress(InetAddress address) {
        blockIpMap.remove(address);
    }

    @Override
    public void setNetwork(Network network) {
        this.network = network;
    }

    @Override
    public NetworkSession getSession(InetSocketAddress address) {
        return this.serverSessionMap.get(address);
    }

    @Override
    public void close(Player player) {
        this.close(player, "unknown reason");
    }

    @Override
    public void close(Player player, String reason) {
        var playerSession = this.getSession(player.getRawSocketAddress());
        if (playerSession != null) {
            playerSession.disconnect(reason);
        }
    }

    @Override
    public void setName(String name) {
        QueryRegenerateEvent info = this.server.getQueryInformation();
        String[] names = name.split("!@#");  //Split double names within the program
        String motd = Utils.rtrim(names[0].replace(";", "\\;"), '\\');
        String subMotd = names.length > 1 ? Utils.rtrim(names[1].replace(";", "\\;"), '\\') : "";
        this.pong = new BedrockPong()
                .edition("MCPE")
                .motd(motd)
                .subMotd(subMotd)
                .playerCount(info.getPlayerCount())
                .maximumPlayerCount(info.getMaxPlayerCount())
                .gameType(Server.getGamemodeString(this.server.getDefaultGamemode(), true))
                .protocolVersion(ProtocolInfo.CURRENT_PROTOCOL)
                .version(info.getVersion())
                .ipv4Port(server.getPort());
    }

    @Override
    public void process() {
        var iterator = this.serverSessionMap.values().iterator();
        while (iterator.hasNext()) {
            var session = iterator.next();
            session.tick();
            if (session.isDisconnected()) {
                iterator.remove();
            }
        }
    }

    @Override
    public void shutdown() {
        this.serverSessionMap.values().forEach(session -> {
            session.disconnect("Shutdown");
        });
        this.channel.close();
    }
}
