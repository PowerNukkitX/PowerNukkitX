package org.powernukkitx.network.nethernet;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.netty.signaling.ProviderClient;
import org.cloudburstmc.netty.signaling.ProviderDiagnostic;
import org.cloudburstmc.netty.signaling.ProviderStateStore;
import org.cloudburstmc.netty.signaling.ProviderTransport;
import org.cloudburstmc.netty.signaling.ServerStatus;
import org.cloudburstmc.netty.signaling.provider.NativeProviderHostFactory;
import org.cloudburstmc.netty.signaling.provider.ProviderRuntimeConfiguration;
import org.cloudburstmc.netty.signaling.provider.ProviderShutdown;
import org.powernukkitx.Server;
import org.powernukkitx.config.category.network.NetherNetSettings;
import org.powernukkitx.config.category.network.NxsAdvancedSettings;
import org.powernukkitx.config.category.network.NxsSettings;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

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

    private final Server server;
    private final Function<GameOutcomeReporter, ChannelHandler> sessionInitializer;
    private final GameOutcomeReporter gameOutcomes = new GameOutcomeReporter(this::reportGameOutcome);

    private EventLoopGroup group;
    private Channel channel;
    private volatile ProviderClient client;
    private ProviderShutdown shutdown;

    /**
     * @param sessionInitializer builds the session pipeline for admitted peers, wired to report
     *                           game outcomes through the reporter it is handed
     */
    public NetherNetProvider(Server server, Function<GameOutcomeReporter, ChannelHandler> sessionInitializer) {
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
        Path dataPath = Path.of(this.server.getDataPath());

        ProviderRuntimeConfiguration runtime = ProviderRuntimeConfiguration.resolve(
            providerSettings(settings.nxsSettings()), dataPath, address.getAddress().getHostAddress(), udpPort,
            this.server.getMaxPlayers());

        ProviderStateStore store = new ProviderStateStore(runtime.stateDirectory());
        ProviderTransport transport = null;
        try {
            this.group = new NioEventLoopGroup(1, new ThreadFactoryBuilder()
                .setNameFormat("NetherNet Provider - #%d").build());
            ServerBootstrap bootstrap = new ServerBootstrap()
                .group(this.group)
                .childHandler(this.sessionInitializer.apply(this.gameOutcomes));

            NativeProviderHostFactory.Host host = NativeProviderHostFactory.open(bootstrap, runtime)
                .toCompletableFuture().get(30, TimeUnit.SECONDS);

            this.channel = host.channel();
            transport = host.transport();
            host.warnings().forEach(log::warn);

            this.client = new ProviderClient(runtime.clientConfiguration(), store, transport, this::status,
                () -> this.health(runtime.capacity()), NetherNetProvider::diagnostic);
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

        ProviderClient client = this.client;
        client.start().whenComplete((registration, failure) -> {
            if (failure != null) {
                log.error("NetherNet provider registration failed, no players will arrive through it", failure);
                this.close();
                return;
            }
            log.info(registrationMessage(registration));
            // The provider may hand the operator a link, such as one to claim this server
            client.operatorAction().thenAccept(action -> action.ifPresent(value ->
                log.info("{} {}", value.text(), value.url())));
            log.debug("NetherNet client identities are authenticated by external signalling provider {}; "
                + "direct login keys are checked against its admission tickets", runtime.origin().getHost());
        });
    }

    /**
     * Turns the nxs section of the config into what the provider runtime takes. Game outcomes are
     * always on, since the server reports them itself.
     *
     * @throws IOException when the configured location is not one the provider understands
     */
    static ProviderRuntimeConfiguration.Settings providerSettings(NxsSettings nxs) throws IOException {
        NxsAdvancedSettings advanced = nxs.advanced();
        ProviderClient.Location location;
        try {
            location = ProviderClient.Location.of(advanced.location());
        } catch (IllegalArgumentException invalid) {
            throw new IOException("nethernet.nxs.advanced.location needs a two-letter country code, a latitude "
                + "and longitude pair, or both; city is optional", invalid);
        }
        return ProviderRuntimeConfiguration.Settings.builder(nxs.endpoint())
            .token(nxs.token())
            .advertiseAddresses(nxs.advertiseAddresses())
            .assistedJoins(advanced.assistedJoins())
            .connectionChecks(advanced.connectionChecks())
            .stunDiscovery(advanced.stunDiscovery())
            .deregisterOnShutdown(advanced.deregisterOnShutdown())
            .location(location)
            .gameOutcomes(true)
            .build();
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
        return new ServerStatus(this.server.getMotd(), this.server.getSubMotd(), this.server.getMaxPlayers(), 0);
    }

    /**
     * What the provider checks in on between status updates, so it knows whether to keep sending
     * players here.
     */
    private ProviderClient.Health health(int capacity) {
        return new ProviderClient.Health(this.accepting(), capacity,
            new ProviderClient.Software("PowerNukkitX", this.server.getNukkitVersion()),
            new ProviderClient.ConnectedPlayers(this.players(), System.currentTimeMillis()));
    }

    private boolean reportGameOutcome(String ticketId, ProviderClient.GameOutcome outcome, String reason) {
        ProviderClient client = this.client;
        return client != null && client.reportGameOutcome(ticketId, outcome, reason);
    }

    private static void diagnostic(ProviderDiagnostic diagnostic) {
        String message = "NXS: " + diagnostic.message();
        switch (diagnostic.level()) {
            case DEBUG -> log.debug(message);
            case INFO -> log.info(message);
            case WARN -> log.warn(message);
        }
    }

    private static String registrationMessage(ProviderClient.Registration registration) {
        String instanceId = registration.instanceId();
        String address = registration.publicAddress();
        if (address != null) {
            return "Provider address: " + address + " (instance " + instanceId + ")";
        }
        return "Provider instance registered: " + instanceId
            + "; public addresses are managed on its attached Signal Servers";
    }

    private int players() {
        return this.server.getOnlinePlayers().size();
    }

    /**
     * Whether the server has room for more players.
     */
    private boolean accepting() {
        return this.players() < this.server.getMaxPlayers();
    }
}
