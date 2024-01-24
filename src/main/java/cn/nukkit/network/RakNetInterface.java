package cn.nukkit.network;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.player.PlayerAsyncCreationEvent;
import cn.nukkit.event.server.QueryRegenerateEvent;
import cn.nukkit.network.connection.BedrockPeer;
import cn.nukkit.network.connection.BedrockPong;
import cn.nukkit.network.connection.BedrockServerSession;
import cn.nukkit.network.connection.netty.initializer.BedrockServerInitializer;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.utils.Utils;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.netty.channel.raknet.RakChannelFactory;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;

import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;

import static cn.nukkit.utils.Utils.dynamic;

@Slf4j
public class RakNetInterface implements SourceInterface {
    private final Map<InetSocketAddress, BedrockServerSession> serverSessionMap = new ConcurrentHashMap<>();
    private final Map<InetAddress, Long> blockIpMap = new HashMap<>();
    private final Channel channel;
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
        } else {
            oclass = NioDatagramChannel.class;
            eventloopgroup = new NioEventLoopGroup(nettyThreadNumber, threadFactory);
        }
        InetSocketAddress bindAddress = new InetSocketAddress(Strings.isNullOrEmpty(this.server.getIp()) ? "127.0.0.1" : this.server.getIp(), this.server.getPort());
        this.channel = new ServerBootstrap()
                .channelFactory(RakChannelFactory.server(oclass))
                .option(RakChannelOption.RAK_ADVERTISEMENT, pong.toByteBuf())
                .group(eventloopgroup)
                .childHandler(new BedrockServerInitializer() {
                    @Override
                    protected void postInitChannel(Channel channel) {
                        /*if (RakNetInterface.this.server.getPropertyBoolean("enable-query", true)) {
                            channel.pipeline().addLast("queryPacketCodec", new QueryPacketCodec())
                                    .addLast("queryPacketHandler", new QueryPacketHandler(address -> RakNetInterface.this.server.getQueryInformation()));
                        }*/
                    }

                    @Override
                    public BedrockServerSession createSession0(BedrockPeer peer, int subClientId) {
                        BedrockServerSession session = new BedrockServerSession(peer, subClientId);
                        InetSocketAddress address = (InetSocketAddress) session.getSocketAddress();
                        try {
                            PlayerAsyncCreationEvent event = new PlayerAsyncCreationEvent(RakNetInterface.this, Player.class, Player.class, address);
                            RakNetInterface.this.server.getPluginManager().callEvent(event);

                            RakNetInterface.this.serverSessionMap.put(event.getSocketAddress(), session);
                            Constructor<? extends Player> constructor = event.getPlayerClass().getConstructor(SourceInterface.class, Integer.class, InetSocketAddress.class);
                            Player player = constructor.newInstance(RakNetInterface.this, subClientId, event.getSocketAddress());
                            RakNetInterface.this.server.addPlayer(address, player);
                            session.setPlayer(player);
                        } catch (Exception e) {
                            log.error("Failed to create player", e);
                            session.disconnect("Internal error");
                            RakNetInterface.this.serverSessionMap.remove(address);
                        }
                        return session;
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
        BedrockServerSession session = this.serverSessionMap.get(player.getRawSocketAddress());
        return session == null ? -1 : (int) session.getPing();
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
    public BedrockServerSession getSession(InetSocketAddress address) {
        return this.serverSessionMap.get(address);
    }

    @Override
    public void close(Player player) {
        this.close(player, "unknown reason");
    }

    @Override
    public void close(Player player, String reason) {
        BedrockServerSession playerSession = this.getSession(player.getRawSocketAddress());
        if (playerSession != null && playerSession.isConnected()) {
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
        Iterator<BedrockServerSession> iterator = this.serverSessionMap.values().iterator();
        while (iterator.hasNext()) {
            BedrockServerSession nukkitSession = iterator.next();
            Player player = nukkitSession.getPlayer();
            if (nukkitSession.getDisconnectReason() != null) {
                player.close(player.getLeaveMessage(), nukkitSession.getDisconnectReason(), false);
                iterator.remove();
            } else {
                nukkitSession.serverTick();
            }
        }
    }

    @Override
    public void shutdown() {
        this.serverSessionMap.values().forEach(session -> {
            if (session.isConnected()) {
                session.disconnect("Shutdown");
            }
        });
        this.channel.close();
    }
}
