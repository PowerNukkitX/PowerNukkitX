package org.powernukkitx.network;

import com.google.common.base.Strings;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
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
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.netty.channel.nethernet.NetherNetChannelFactory;
import org.cloudburstmc.netty.channel.nethernet.config.NetherChannelOption;
import org.cloudburstmc.netty.channel.nethernet.signaling.NetherNetHTTPSignaling;
import org.cloudburstmc.netty.channel.nethernet.signaling.NetherNetServerSignaling.PongData;
import org.cloudburstmc.netty.channel.nethernet.signaling.NetherNetSignaling;
import org.cloudburstmc.netty.util.nethernet.NetherNetLogging;
import org.cloudburstmc.netty.util.nethernet.ServerIdentity;
import org.cloudburstmc.netty.util.nethernet.TokenTrust;
import org.cloudburstmc.netty.util.nethernet.TrustedProxies;
import org.cloudburstmc.netty.channel.raknet.RakChannelFactory;
import org.cloudburstmc.netty.channel.raknet.RakServerChannel;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.cloudburstmc.netty.channel.raknet.config.RakServerCookieMode;
import org.cloudburstmc.protocol.bedrock.BedrockPong;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.netty.codec.packet.BedrockPacketCodec;
import org.cloudburstmc.protocol.bedrock.netty.codec.packet.BedrockPacketCodec_v3;
import org.cloudburstmc.protocol.bedrock.netty.initializer.BedrockServerInitializer;
import org.jetbrains.annotations.Nullable;
import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.config.category.NetworkSettings;
import org.powernukkitx.config.category.network.NetherNetSettings;
import org.powernukkitx.config.category.network.RakNetSettings;
import org.powernukkitx.event.server.ServerBotnetAttackEvent;
import org.powernukkitx.network.nethernet.NetherNetProvider;
import org.powernukkitx.network.nethernet.NetherNetServerInitializer;
import org.powernukkitx.network.nethernet.SignalingService;
import org.powernukkitx.network.nethernet.ServerIdentityProvider;
import org.powernukkitx.network.process.BadPacketHandler;
import org.powernukkitx.network.process.NetworkPacketHandler;
import org.powernukkitx.network.process.NetworkState;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.powernukkitx.network.query.codec.QueryPacketCodec;
import org.powernukkitx.network.query.handler.QueryPacketHandler;
import org.powernukkitx.network.security.BotnetDetector;
import org.powernukkitx.plugin.InternalPlugin;
import org.powernukkitx.utils.Utils;
import oshi.SystemInfo;
import oshi.hardware.NetworkIF;
import tel.schich.libdatachannel.LibDataChannelArchDetect;
import tel.schich.libdatachannel.PeerConnectionConfiguration;

import java.net.BindException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Slf4j
public class Network implements NetworkInterface, SignalingService {
    private static final int GAME_TYPE_SURVIVAL = 0;
    private static final int GAME_TYPE_CREATIVE = 1;
    private static final int GAME_TYPE_ADVENTURE = 2;

    private final Server server;
    private final LinkedList<NetWorkStatisticData> netWorkStatisticDataList = new LinkedList<>();
    private final AtomicReference<List<NetworkIF>> hardWareNetworkInterfaces = new AtomicReference<>(null);
    private final Map<InetSocketAddress, BedrockServerSession> sessionMap = new ConcurrentHashMap<>();
    private final Map<InetAddress, LocalDateTime> blockIpMap = new ConcurrentHashMap<>();
    @Getter
    private final NetworkSettings.TransportType transport;
    private final EventLoopGroup eventLoopGroup;
    private final @Nullable NetherNetHTTPSignaling signaling;
    private final @Nullable Channel channel;
    private final @Nullable Channel queryChannel;
    private final @Nullable NetherNetProvider provider;
    private final ChannelHandler sessionInitializer;
    @Getter(onMethod_ = {@Override})
    private final BotnetDetector botnetDetector;
    private BedrockPong pong;
    @Getter
    @Setter
    private NetworkState state = NetworkState.STARTING;

    public Network(Server server) {
        this(server, Runtime.getRuntime().availableProcessors(), new ThreadFactoryBuilder().setNameFormat("Netty Server IO #%d").build());
    }

    @SuppressWarnings("deprecation")
    public Network(Server server, int nettyThreadNumber, ThreadFactory threadFactory) {
        this.server = server;
        var bns = server.getSettings().networkSettings().botnetSettings();
        this.botnetDetector = bns.detectionEnabled()
            ? new BotnetDetector(bns.suspiciousThreshold(), bns.minSuspiciousIps(), bns.minScore())
            : null;
        server.getScheduler().scheduleTask(InternalPlugin.INSTANCE, () -> {
            List<NetworkIF> tmpIfs = null;
            try {
                tmpIfs = new SystemInfo().getHardware().getNetworkIFs();
            } catch (Throwable t) {
                log.warn(Server.getInstance().getLanguage().get("nukkit.start.hardwareMonitorDisabled"));
            }
            hardWareNetworkInterfaces.set(tmpIfs);
        }, true);

        final NetworkSettings net = server.getSettings().networkSettings();
        this.transport = net.resolvedTransport();
        final BedrockCodec codec = NetworkConstants.CODEC;
        final InetSocketAddress bindAddress = new InetSocketAddress(
            Strings.isNullOrEmpty(this.server.getIp()) ? "0.0.0.0" : this.server.getIp(), this.server.getPort());

        this.pong = new BedrockPong()
            .edition("MCPE")
            .motd(server.getMotd())
            .subMotd(server.getSubMotd())
            .playerCount(server.getOnlinePlayers().size())
            .maximumPlayerCount(server.getMaxPlayers())
            .serverId(UUID.randomUUID().getMostSignificantBits())
            .gameType(Server.getGamemodeString(server.getDefaultGamemode(), true))
            .nintendoLimited(false)
            .protocolVersion(codec.getProtocolVersion())
            .version(codec.getMinecraftVersion())
            .ipv4Port(server.getPort())
            .ipv6Port(server.getPort());

        if (this.transport == NetworkSettings.TransportType.RAKNET) {
            Class<? extends DatagramChannel> oclass;
            if (Epoll.isAvailable()) {
                oclass = EpollDatagramChannel.class;
                @SuppressWarnings("deprecation")
                var group = new EpollEventLoopGroup(nettyThreadNumber, threadFactory);
                this.eventLoopGroup = group;
            } else if (KQueue.isAvailable()) {
                oclass = KQueueDatagramChannel.class;
                @SuppressWarnings("deprecation")
                var group = new KQueueEventLoopGroup(nettyThreadNumber, threadFactory);
                this.eventLoopGroup = group;
            } else {
                oclass = NioDatagramChannel.class;
                @SuppressWarnings("deprecation")
                var group = new NioEventLoopGroup(nettyThreadNumber, threadFactory);
                this.eventLoopGroup = group;
            }

            checkPortAvailable(bindAddress);

            this.signaling = null;
            this.provider = null;
            // Query rides the RakNet datagram channel, so it needs no listener of its own
            this.queryChannel = null;
            this.sessionInitializer = this.rakSessionInitializer(net);

            final RakNetSettings rak = net.rakNetSettings();
            this.channel = new ServerBootstrap()
                .channelFactory(RakChannelFactory.server(oclass))
                .option(RakChannelOption.RAK_ADVERTISEMENT, getAdvertisement())
                .option(RakChannelOption.RAK_SUPPORTED_PROTOCOLS, new int[]{codec.getRaknetProtocolVersion()})
                .option(RakChannelOption.RAK_PACKET_LIMIT, rak.packetLimit())
                .option(RakChannelOption.RAK_SERVER_COOKIE_MODE, parseCookieMode(rak.cookieMode()))
                .childOption(RakChannelOption.RAK_AUTO_FLUSH, rak.autoFlush())
                .childOption(RakChannelOption.RAK_FLUSH_INTERVAL, rak.flushInterval())
                .childOption(RakChannelOption.RAK_MAX_QUEUED_BYTES, rak.maxQueuedBytes())
                .group(this.eventLoopGroup)
                .childHandler(this.sessionInitializer)
                .bind(bindAddress)
                .syncUninterruptibly()
                .channel();

            log.info("RakNet listening on udp/{}", bindAddress.getPort());
            return;
        }

        // Signalling binds NIO channels of its own, so the group has to be NIO. Nothing hot runs on
        // it: the media is carried by the native ICE and DTLS stack on its own threads.
        this.eventLoopGroup = new NioEventLoopGroup(nettyThreadNumber, threadFactory);

        final NetherNetSettings settings = net.netherNetSettings();

        this.initNative(settings);

        final NetherNetSettings.SignalingMode mode = settings.resolvedSignalingMode();
        final boolean queryEnabled = server.getSettings().networkSettings().enableQuery();
        final int icePort = this.icePort(bindAddress, settings);
        // Query keeps the UDP side of the listener port, so ICE can only have it when query is off
        final boolean iceOnListenerPort = icePort <= 0 && !queryEnabled;
        if (mode.binds() && icePort <= 0 && !iceOnListenerPort) {
            log.warn("Query holds udp/{}, so NetherNet media falls back to an ephemeral port per peer. "
                + "Set network-settings.nethernet.udpPort to give it a fixed one", bindAddress.getPort());
        }

        this.sessionInitializer =
            new NetherNetServerInitializer(codec.getRaknetProtocolVersion(), log.isDebugEnabled()) {
                @Override
                protected void initSession(BedrockServerSession session) {
                    Network.this.initSession(session);
                }
            };

        if (mode.binds()) {
            this.signaling = this.buildSignaling(settings, bindAddress, icePort, iceOnListenerPort, mode.builtin());

            final InetSocketAddress signalingAddress = settings.signalingPort() > 0
                ? new InetSocketAddress(bindAddress.getAddress(), settings.signalingPort())
                : bindAddress;

            this.channel = new ServerBootstrap()
                .group(this.eventLoopGroup)
                .channelFactory(NetherNetChannelFactory.server(this.signaling))
                .option(NetherChannelOption.NETHER_SERVER_RTC_HANDSHAKE_TIMEOUT_SECONDS, settings.handshakeTimeout())
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel channel) {
                        if (icePort <= 0) {
                            return;
                        }
                        // Runs before the first connection, so every peer sees the pinned port
                        ChannelConfig options = channel.config();
                        options.setOption(NetherChannelOption.NETHER_PEER_CONNECTION_CONFIG,
                            pinIce(options.getOption(NetherChannelOption.NETHER_PEER_CONNECTION_CONFIG),
                                bindAddress.getAddress(), icePort));
                    }
                })
                .childHandler(this.sessionInitializer)
                .bind(signalingAddress)
                .syncUninterruptibly()
                .channel();

            if (mode.builtin()) {
                log.info("NetherNet signalling listening on tcp/{}{}", signalingAddress.getPort(),
                    icePort > 0 ? ", with WebRTC on udp/" + icePort : "");
            } else {
                log.info("NetherNet is bound but serves no endpoint, offers must arrive through the signalling API");
            }
        } else {
            this.signaling = null;
            this.channel = null;
        }

        this.provider = mode.nxs()
            ? this.startProvider(settings, bindAddress, this.providerPort(bindAddress, settings, icePort, queryEnabled))
            : null;

        this.queryChannel = this.bindQuery(bindAddress);
    }

    /**
     * The Bedrock pipeline as RakNet carries it: the stock initializer, with the debug packet codec
     * and the bad-packet guard swapped in the way this server wants them.
     */
    private ChannelHandler rakSessionInitializer(NetworkSettings net) {
        final boolean query = net.enableQuery();
        return new BedrockServerInitializer() {
            @Override
            protected void postInitChannel(Channel channel) {
                channel.config().setOption(
                    RakChannelOption.RAK_METRICS,
                    new RakNetNetworkMetrics()
                );

                if (query) {
                    channel.pipeline().addLast("queryPacketCodec", new QueryPacketCodec())
                        .addLast("queryPacketHandler", new QueryPacketHandler(
                            sender -> Network.this.server.getQueryInformation()));
                }
            }

            @Override
            protected void initSession(BedrockServerSession session) {
                session.getPeer().getChannel().pipeline().replace(
                    BedrockPacketCodec.NAME,
                    BedrockPacketCodec.NAME,
                    new BedrockPacketCodec_v3(log.isDebugEnabled())
                );
                Network.this.initSession(session);
            }
        };
    }

    // Verifies the UDP port is free before RakNet binds, so the server refuses to start when another process holds it.
    private void checkPortAvailable(InetSocketAddress address) {
        try (DatagramSocket testSocket = new DatagramSocket(null)) {
            testSocket.setReuseAddress(false);
            testSocket.bind(address);
        } catch (BindException e) {
            throw new IllegalStateException("Server port " + address.getPort() + " is already in use by another process. Startup aborted.", e);
        } catch (SocketException e) {
            throw new IllegalStateException("Failed to verify availability of server port " + address.getPort() + ".", e);
        }
    }

    /**
     * Resolves the configured RakNet connection-cookie mode, falling back to {@link RakServerCookieMode#ACTIVE}
     * (the RakNet default) for unknown or invalid values.
     */
    private RakServerCookieMode parseCookieMode(String mode) {
        try {
            RakServerCookieMode parsed = RakServerCookieMode.valueOf(mode.trim().toUpperCase(Locale.ROOT));
            if (parsed != RakServerCookieMode.INVALID) {
                return parsed;
            }
        } catch (IllegalArgumentException ignored) {
        }
        log.warn("Invalid network cookie-mode '{}', falling back to ACTIVE. Valid values: ACTIVE, OFFLOADED, OFFLOADED_PSK, OFF", mode);
        return RakServerCookieMode.ACTIVE;
    }

    /**
     * Retrieves the RakNet advertisement, which is what the client shows in its server list.
     */
    private ByteBuf getAdvertisement() {
        if (this.state == NetworkState.STARTING || this.state == NetworkState.STOPPING) {
            return Unpooled.EMPTY_BUFFER;
        }
        return this.pong.toByteBuf();
    }

    /**
     * Registers with the provider, which then admits players onto a port of its own. A provider
     * that cannot be reached leaves the rest of the server alone, the same way signalling does.
     */
    private @Nullable NetherNetProvider startProvider(NetherNetSettings settings, InetSocketAddress address,
                                                      int udpPort) {
        if (udpPort <= 0) {
            log.error("NetherNet provider registration needs a fixed nethernet.udpPort, "
                + "no players will arrive through it");
            return null;
        }
        NetherNetProvider provider = new NetherNetProvider(this.server, this.sessionInitializer);
        try {
            provider.start(settings, address, udpPort);
            return provider;
        } catch (Throwable t) {
            provider.close();
            log.error("Unable to register with the NetherNet provider, no players will arrive through it", t);
            return null;
        }
    }

    /**
     * The port the provider hands out. It must be fixed, so the listener port stands in when
     * nothing else holds its UDP side.
     */
    private int providerPort(InetSocketAddress listener, NetherNetSettings settings, int icePort,
                             boolean queryEnabled) {
        if (icePort > 0) {
            return icePort;
        }
        return queryEnabled ? 0 : listener.getPort();
    }

    /**
     * Loads the ICE and DTLS native built for this platform, and the identity clients pin.
     */
    private void initNative(NetherNetSettings settings) {
        try {
            LibDataChannelArchDetect.initialize();
            NetherNetLogging.setNativeLogLevel(settings.nativeLogLevel());
            ServerIdentityProvider.identity(this.server);
        } catch (Throwable t) {
            throw new IllegalStateException("Unable to initialise NetherNet. The native ICE and DTLS library "
                + "may be missing for this platform", t);
        }
    }

    private NetherNetHTTPSignaling buildSignaling(NetherNetSettings settings, InetSocketAddress address, int icePort,
                                                  boolean iceOnListenerPort, boolean serveHttp) {
        try {
            ServerIdentity identity = ServerIdentityProvider.identity(this.server);
            log.info("NetherNet identifies this operator to players as {}", ServerIdentityProvider.domain(this.server));

            return new NetherNetHTTPSignaling.Builder()
                .setIdentity(identity)
                .setServeHttp(serveHttp)
                .setTrustedProxies(TrustedProxies.parse(settings.trustedProxies()))
                .setProxyProtocol(settings.proxyProtocol())
                .setAdvertisedAddresses(advertisedAddresses(address, settings))
                .setIceServers(iceServers(settings))
                // A dedicated media port is pinned by the channel initialiser instead
                .setIceOnLocalPort(icePort <= 0 && iceOnListenerPort)
                .setTokenTrust(TokenTrust.ANY)
                .setMotdProvider((host, client) -> Network.this.advertisement())
                .setPlayerFilter((host, player) -> Network.this.acceptsConnections())
                .build();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to configure NetherNet signalling", e);
        }
    }

    /**
     * Query rides its own datagram channel: the listener port is TCP for signalling, so its UDP
     * side is free.
     */
    private @Nullable Channel bindQuery(InetSocketAddress address) {
        if (!this.server.getSettings().networkSettings().enableQuery()) {
            return null;
        }
        return new Bootstrap()
            .group(this.eventLoopGroup)
            .channel(NioDatagramChannel.class)
            .handler(new ChannelInitializer<>() {
                @Override
                protected void initChannel(Channel channel) {
                    channel.pipeline()
                        .addLast("queryPacketCodec", new QueryPacketCodec())
                        .addLast("queryPacketHandler", new QueryPacketHandler(
                            sender -> Network.this.server.getQueryInformation()));
                }
            })
            .bind(address)
            .syncUninterruptibly()
            .channel();
    }

    private void initSession(BedrockServerSession session) {
        session.getPeer().getChannel().pipeline().addAfter(
            BedrockPacketCodec.NAME,
            "badPacketHandler",
            new BadPacketHandler(session)
        );

        final InetSocketAddress address = (InetSocketAddress) session.getSocketAddress();
        if (this.getState() == NetworkState.STARTING || this.getState() == NetworkState.STOPPING) {
            return;
        }
        if (isAddressBlocked(address)) {
            session.close("Your IP address has been blocked by this server!");
            return;
        }

        session.setCodec(NetworkConstants.CODEC);
        session.setPacketHandler(new NetworkPacketHandler(this.server,
            new PlayerSessionHolder(
                session,
                this.server.getSettings().networkSettings().rateLimitSettings()
            )
        ));
        final Channel sessionChannel = session.getPeer().getChannel();
        this.sessionMap.put(address, session);
        sessionChannel.closeFuture().addListener(future -> {
            if (!this.sessionMap.remove(address, session)) {
                this.sessionMap.values().remove(session);
            }
        });
    }

    /**
     * The configured STUN and TURN servers, as one entry carrying every URL. Credentials belong in
     * the URL, which is the only place the configuration has to put them.
     */
    private static List<NetherNetSignaling.IceServerInfo> iceServers(NetherNetSettings settings) {
        List<String> urls = settings.iceServers();
        if (urls.isEmpty()) {
            return List.of();
        }
        return List.of(new NetherNetSignaling.IceServerInfo.Builder().setUrls(List.copyOf(urls)).build());
    }

    /**
     * A dedicated media port multiplexes every peer over one socket. Without one, ICE gathers on
     * the listener port when query is not holding its UDP side, and on ephemeral ports otherwise.
     */
    /**
     * The addresses clients are told to connect to. Falling back to the bind address keeps a server
     * bound to one public address from offering the internal addresses of its machine.
     */
    private static Set<String> advertisedAddresses(InetSocketAddress address, NetherNetSettings settings) {
        if (!settings.advertiseAddresses().isEmpty()) {
            return Set.copyOf(settings.advertiseAddresses());
        }
        InetAddress bound = address.getAddress();
        if (bound == null || bound.isAnyLocalAddress()) {
            return Set.of();
        }
        return Set.of(bound.getHostAddress());
    }

    private int icePort(InetSocketAddress listener, NetherNetSettings settings) {
        int port = settings.udpPort();
        if (port == listener.getPort()) {
            return 0;
        }
        return Math.max(port, 0);
    }

    private static PeerConnectionConfiguration pinIce(PeerConnectionConfiguration config, InetAddress host, int port) {
        // A wildcard bind is left unset so ICE keeps gathering on every interface
        if (host != null && !host.isAnyLocalAddress()) {
            config = config.withBindAddress(host);
        }
        return config
            .withEnableIceUdpMux(true)
            .withPortRangeBegin(port)
            .withPortRangeEnd(port);
    }

    record NetWorkStatisticData(long upload, long download) {
    }

    public void shutdown() {
        if (this.provider != null) {
            this.provider.close();
        }
        if (this.channel != null) {
            this.channel.close();
        }
        if (this.queryChannel != null) {
            this.queryChannel.close();
        }
        this.eventLoopGroup.shutdownGracefully();
        this.pong = null;
        this.sessionMap.clear();
        this.netWorkStatisticDataList.clear();
    }

    public double getUpload() {
        if (netWorkStatisticDataList.size() < 2) return 0;
        return netWorkStatisticDataList.get(1).upload - netWorkStatisticDataList.get(0).upload;
    }

    public double getDownload() {
        if (netWorkStatisticDataList.size() < 2) return 0;
        return netWorkStatisticDataList.get(1).download - netWorkStatisticDataList.get(0).download;
    }

    public void resetStatistics() {
        long upload = 0;
        long download = 0;
        if (netWorkStatisticDataList.size() > 1) {
            netWorkStatisticDataList.removeFirst();
        }
        if (this.getHardWareNetworkInterfaces() != null) {
            for (var networkIF : this.getHardWareNetworkInterfaces()) {
                networkIF.updateAttributes();
                upload += networkIF.getBytesSent();
                download += networkIF.getBytesRecv();
            }
        }
        netWorkStatisticDataList.add(new NetWorkStatisticData(upload, download));
    }

    /**
     * process tick for all network interfaces.
     */
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
        return hardWareNetworkInterfaces.get();
    }


    /**
     * Get network latency for specific player.
     *
     * @param player the player
     * @return the network latency
     */
    public int getNetworkLatency(Player player) {
        return -1;
    }

    @Override
    public NetworkPressure getNetworkPressure(Player player) {
        if (this.transport != NetworkSettings.TransportType.RAKNET) {
            return NetworkPressure.UNKNOWN;
        }

        final var metrics = player.getSession().getPeer().getChannel().config().getOption(RakChannelOption.RAK_METRICS);
        return metrics instanceof RakNetNetworkMetrics networkMetrics
            ? networkMetrics.getNetworkPressure()
            : NetworkPressure.UNKNOWN;
    }

    /**
     * Block an address forever.
     *
     * @param address the address
     */
    public void blockAddress(InetAddress address) {
        blockIpMap.put(address, LocalDateTime.of(9999, 1, 1, 0, 0));
    }

    /**
     * Block an address.
     *
     * @param address the address
     * @param timeout the timeout,unit millisecond
     */
    public void blockAddress(InetAddress address, int timeout) {
        blockIpMap.put(address, LocalDateTime.now().plus(timeout, ChronoUnit.MILLIS));
    }

    /**
     * Recover an address of banned.
     *
     * @param address the address
     */
    public void unblockAddress(InetAddress address) {
        blockIpMap.remove(address);
    }

    /**
     * Get a session of player.
     *
     * @param address the address of session
     * @return the session
     */
    public BedrockServerSession getSession(InetSocketAddress address) {
        return this.sessionMap.get(address);
    }

    /**
     * Replace session address.
     * <p>
     * handle a scenario that the player from proxy
     *
     * @param oldAddress the old address
     * @param newAddress the new address,usually the IP of the proxy
     * @param newSession original session
     */
    @Override
    public void replaceSessionAddress(InetSocketAddress oldAddress, InetSocketAddress newAddress, BedrockServerSession newSession) {
        if (!this.sessionMap.containsKey(oldAddress))
            return;

        if (isAddressBlocked(newAddress))
            return;

        onSessionDisconnect(oldAddress);
        this.sessionMap.put(newAddress, newSession);
    }

    /**
     * whether the address is blocked
     */
    public boolean isAddressBlocked(InetSocketAddress address) {
        LocalDateTime until = this.blockIpMap.get(address.getAddress());
        return until != null && LocalDateTime.now().isBefore(until);
    }

    /**
     * A function of tick for network session
     */
    public void process() {
        if (botnetDetector != null) {
            var bns = server.getSettings().networkSettings().botnetSettings();
            botnetDetector.tick().ifPresent(report -> {
                ServerBotnetAttackEvent event = new ServerBotnetAttackEvent(
                    report, bns.autoBlock(), bns.autoBlockDurationSeconds());
                server.getPluginManager().callEvent(event);
                if (event.isAutoBlock()) {
                    int durationMs = event.getBlockDurationSeconds() * 1000;
                    for (var ip : event.getSuspiciousAddresses()) {
                        blockAddress(ip, durationMs);
                    }
                }
            });
        }
    }

    /**
     * call on session disconnect.
     */
    public void onSessionDisconnect(InetSocketAddress address) {
        this.sessionMap.remove(address);
    }

    /**
     * Gets the advertisement clients read before they connect.
     */
    public BedrockPong getPong() {
        return pong;
    }

    @Override
    public void updatePong(BedrockPong pong) {
        this.pong = pong;
        if (this.channel instanceof RakServerChannel rakChannel) {
            rakChannel.config().setAdvertisement(this.getAdvertisement());
        }
        if (this.signaling != null) {
            this.signaling.setAdvertisementData(this.advertisement());
        }
    }

    @Override
    public boolean acceptsConnections() {
        return this.state != NetworkState.STARTING && this.state != NetworkState.STOPPING;
    }

    @Override
    public CompletableFuture<String> acceptOffer(String networkId, String offer, InetSocketAddress clientAddress) {
        NetherNetHTTPSignaling signaling = this.signaling;
        if (signaling == null) {
            return CompletableFuture.failedFuture(new IllegalStateException("NetherNet is not bound"));
        }
        return signaling.acceptOffer(networkId, offer, clientAddress, null);
    }

    /**
     * The status document a client reads before it offers. Built per request, so plugins that
     * rewrite the MOTD through {@link #updatePong(BedrockPong)} are reflected immediately.
     */
    @Override
    public PongData advertisement() {
        BedrockPong current = this.pong;
        if (current == null) {
            return PongData.DEFAULT;
        }
        return new PongData.Builder()
            .setServerName(current.motd())
            .setProtocol(current.protocolVersion())
            .setVersion(current.version())
            .setLevelName(current.subMotd())
            .setPlayerCount(current.playerCount())
            .setMaxPlayerCount(current.maximumPlayerCount())
            .setGameType(gameType(current.gameType()))
            .setOnlineAuth(this.server.getSettings().baseSettings().xboxAuth())
            // Offline mode means the server signs its own chains rather than the auth service
            .setSelfSignedAuth(!this.server.getSettings().baseSettings().xboxAuth())
            .build();
    }

    private static int gameType(String name) {
        if (name == null) {
            return GAME_TYPE_SURVIVAL;
        }
        return switch (name.toLowerCase(Locale.ROOT)) {
            case "creative" -> GAME_TYPE_CREATIVE;
            case "adventure" -> GAME_TYPE_ADVENTURE;
            default -> GAME_TYPE_SURVIVAL;
        };
    }
}
