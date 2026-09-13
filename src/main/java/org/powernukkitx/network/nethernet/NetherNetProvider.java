package org.powernukkitx.network.nethernet;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.netty.signalling.ProviderClient;
import org.cloudburstmc.netty.signalling.ProviderStateStore;
import org.cloudburstmc.netty.signalling.ProviderTransport;
import org.cloudburstmc.netty.signalling.ServerStatus;
import org.cloudburstmc.netty.signalling.provider.NativeProviderHostFactory;
import org.cloudburstmc.netty.signalling.provider.ProviderHostFactory;
import org.cloudburstmc.netty.signalling.provider.ProviderRuntimeConfiguration;
import org.cloudburstmc.netty.signalling.provider.ProviderRuntimeObservations;
import org.cloudburstmc.netty.signalling.provider.ProviderShutdown;
import org.powernukkitx.Server;
import org.powernukkitx.config.category.network.NetherNetSettings;
import org.powernukkitx.config.category.network.NxsSettings;
import org.powernukkitx.network.NetworkConstants;

import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Registers the server with an NXS provider, which then hands it players.
 * <p>
 * Nothing is served here. The provider admits a peer and the server receives it on a UDP port of
 * its own, so this is the other half of the builtin signalling endpoint rather than a variation on
 * it, and the two can run together.
 *
 * @author irrelevantdev (WaterdogPE Project)
 * @since 12/09/2026
 */
@Slf4j
public class NetherNetProvider implements AutoCloseable {

    private static final String LABEL = "PowerNukkitX";
    private static final Set<String> LOCAL_ORIGINS = Set.of("127.0.0.1", "localhost", "[::1]");

    private final Server server;
    private final ChannelHandler sessionInitializer;

    private EventLoopGroup group;
    private Channel channel;
    private ProviderClient client;
    private ProviderShutdown shutdown;

    public NetherNetProvider(Server server, ChannelHandler sessionInitializer) {
        this.server = server;
        this.sessionInitializer = sessionInitializer;
    }

    /**
     * @param settings the NetherNet settings, for the provider section
     * @param address  the address the server listens on
     * @param udpPort  the fixed UDP port peers are handed, which the provider cannot advertise if
     *                 it is ephemeral
     */
    public void start(NetherNetSettings settings, InetSocketAddress address, int udpPort) throws Exception {
        NxsSettings nxs = settings.nxsSettings();
        Path dataPath = Path.of(this.server.getDataPath());

        ProviderRuntimeConfiguration runtime = ProviderRuntimeConfiguration.resolve(
            new ProviderRuntimeConfiguration.Settings(nxs.endpoint(), nxs.token(),
                nxs.advertiseAddresses(), nxs.data()),
            dataPath, address.getAddress().getHostAddress(), udpPort,
            this.server.getMaxPlayers(), LABEL);

        ProviderStateStore store = new ProviderStateStore(runtime.stateDirectory());
        ProviderTransport transport = null;
        try {
            this.group = new NioEventLoopGroup(1, new ThreadFactoryBuilder()
                .setNameFormat("NetherNet Provider - #%d").build());
            ServerBootstrap bootstrap = new ServerBootstrap()
                .group(this.group)
                .childHandler(this.sessionInitializer);

            ProviderHostFactory.Host host = new NativeProviderHostFactory()
                .open(bootstrap, new InetSocketAddress(runtime.bindAddress(), runtime.udpPort()),
                    Map.of("stateDirectory", runtime.stateDirectory().toAbsolutePath().toString(),
                        "profile", runtime.profile(),
                        "advertisedEndpoints", runtime.encodedAdvertisedEndpoints(),
                        "localDevelopment",
                        Boolean.toString(LOCAL_ORIGINS.contains(runtime.origin().getHost()))))
                .toCompletableFuture().get(30, TimeUnit.SECONDS);

            this.channel = host.channel();
            transport = host.transport();
            host.warnings().forEach(log::warn);

            this.client = new ProviderClient(runtime.clientConfiguration(), store, transport, this::status,
                () -> ProviderRuntimeObservations.health(this.players(), runtime.capacity(),
                    System.currentTimeMillis(), this.server.getNukkitVersion(), this.accepting()),
                log::warn);
            // The client owns the store and the transport from here
            store = null;
            transport = null;
            this.shutdown = new ProviderShutdown(this.client::stop, this::closeNetwork, log::warn);
        } finally {
            if (transport != null) {
                transport.close();
            }
            if (store != null) {
                store.close();
            }
        }

        this.client.start().whenComplete((registration, failure) -> {
            if (failure != null) {
                log.error("NetherNet provider registration failed, no players will arrive through it", failure);
                this.close();
                return;
            }
            log.info(ProviderRuntimeObservations.registrationMessage(registration));
            log.info(ProviderRuntimeObservations.delegatedIdentityMessage(runtime.origin()));
        });
    }

    @Override
    public void close() {
        ProviderShutdown shutdown = this.shutdown;
        this.shutdown = null;
        if (shutdown != null) {
            // Closes the client, which drains at the provider before the endpoint goes away
            shutdown.close();
            return;
        }
        this.closeNetwork();
    }

    private void closeNetwork() {
        if (this.channel != null) {
            this.channel.close();
            this.channel = null;
        }
        if (this.group != null) {
            this.group.shutdownGracefully();
            this.group = null;
        }
        this.client = null;
    }

    /**
     * What the provider shows for this server, which is the same thing a player sees in the list.
     */
    private ServerStatus status() {
        return new ServerStatus(this.server.getMotd(), NetworkConstants.CODEC.getProtocolVersion(),
            NetworkConstants.CODEC.getMinecraftVersion(), this.server.getSubMotd(), this.players(),
            this.server.getMaxPlayers(), 0);
    }

    private int players() {
        return this.server.getOnlinePlayers().size();
    }

    /**
     * Whether the server wants more players, which is separate from whether it is healthy: a full
     * server is working perfectly and simply has nowhere to put them.
     */
    private boolean accepting() {
        return this.players() < this.server.getMaxPlayers();
    }
}
