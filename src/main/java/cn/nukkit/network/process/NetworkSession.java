package cn.nukkit.network.process;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.data.CommandDataVersions;
import cn.nukkit.event.player.PlayerCreationEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.network.connection.BedrockServerSession;
import cn.nukkit.network.process.handler.HandshakePacketHandler;
import cn.nukkit.network.process.handler.InGamePacketHandler;
import cn.nukkit.network.process.handler.LoginHandler;
import cn.nukkit.network.process.handler.ResourcePackHandler;
import cn.nukkit.network.process.handler.SessionStartHandler;
import cn.nukkit.network.process.handler.SpawnResponseHandler;
import cn.nukkit.network.protocol.AvailableCommandsPacket;
import cn.nukkit.network.protocol.CreativeContentPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.DisconnectPacket;
import cn.nukkit.network.protocol.NetworkSettingsPacket;
import cn.nukkit.network.protocol.PacketHandler;
import cn.nukkit.network.protocol.PlayStatusPacket;
import cn.nukkit.network.protocol.SetCommandsEnabledPacket;
import cn.nukkit.player.info.PlayerInfo;
import cn.nukkit.registry.Registries;
import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class NetworkSession {
    private final @NotNull BedrockServerSession session;

    private final @NotNull Server server = Server.getInstance();
    private Player player;
    private PlayerHandle handle;
    private PlayerInfo info;

    private final @NotNull StateMachine<NetworkSessionState, NetworkSessionState> machine;
    protected @Nullable PacketHandler packetHandler;

    protected AtomicBoolean disconnected = new AtomicBoolean(false);
    private InetSocketAddress address;

    public void onPlayerCreated(@NotNull Player player) {
        this.player = player;
        this.handle = new PlayerHandle(player);
        this.server.onPlayerLogin(address, player);
    }

    public NetworkSession(BedrockServerSession session) {
        this.session = session;
        // from session start to login sequence complete, netty threads own the session
        this.session.setNettyThreadOwned(true);

        this.session.setPacketConsumer((pk) -> {
            try {
                this.handleDataPacket(pk);
            } catch (Exception e) {
                log.error("An error occurred whilst handling {} for {}", pk.getClass().getSimpleName(), this.session.getSocketAddress().toString(), e);
                this.disconnect("packet processing error");
            }
        });

        this.address = (InetSocketAddress) this.session.getSocketAddress();
        log.debug("creating session {}", session.getPeer().getSocketAddress().toString());
        var cfg = new StateMachineConfig<NetworkSessionState, NetworkSessionState>();

        cfg.configure(NetworkSessionState.START)
                .onExit(this::onSessionStartSuccess)
                .permit(NetworkSessionState.LOGIN, NetworkSessionState.LOGIN);

        cfg.configure(NetworkSessionState.LOGIN).onEntry(() -> this.setPacketHandler(new LoginHandler(this, (info) -> {
                    this.info = info;
                })))
                .onExit(this::onServerLoginSuccess)
                .permitIf(NetworkSessionState.ENCRYPTION, NetworkSessionState.ENCRYPTION, () -> Server.getInstance().enabledNetworkEncryption)
                .permit(NetworkSessionState.RESOURCE_PACK, NetworkSessionState.RESOURCE_PACK);

        cfg.configure(NetworkSessionState.ENCRYPTION)
                .onEntry(() -> this.setPacketHandler(new HandshakePacketHandler(this)))
                .permit(NetworkSessionState.RESOURCE_PACK, NetworkSessionState.RESOURCE_PACK);

        cfg.configure(NetworkSessionState.RESOURCE_PACK)
                .onEntry(() -> this.setPacketHandler(new ResourcePackHandler(this)))
                .permit(NetworkSessionState.PRE_SPAWN, NetworkSessionState.PRE_SPAWN);

        cfg.configure(NetworkSessionState.PRE_SPAWN)
                .onEntry(() -> {
                    // now the main thread owns the session
                    this.session.setNettyThreadOwned(false);

                    log.debug("Creating player");

                    var player = this.createPlayer();
                    if (player == null) {
                        this.disconnect("Failed to crate player");
                        return;
                    }
                    this.onPlayerCreated(player);
                    player.processLogin();
                    this.setPacketHandler(new SpawnResponseHandler(this));
                    // The reason why teleport player to their position is for gracefully client-side spawn,
                    // although we need some hacks, It is definitely a fairly worthy trade.
                    this.player.setImmobile(true); //TODO: HACK: fix client-side falling pre-spawn
                })
                .onExit(this::onClientSpawned)
                .permit(NetworkSessionState.IN_GAME, NetworkSessionState.IN_GAME);

        cfg.configure(NetworkSessionState.IN_GAME)
                .onEntry(() -> this.setPacketHandler(new InGamePacketHandler(this)))
                .onExit(this::onServerDeath)
                .permit(NetworkSessionState.DEATH, NetworkSessionState.DEATH);

        cfg.configure(NetworkSessionState.DEATH)
                //.onEntry(()->this.setPacketHandler(new DeathHandler()))
                .onExit(this::onClientRespawn)
                .permit(NetworkSessionState.IN_GAME, NetworkSessionState.IN_GAME);

        machine = new StateMachine<>(NetworkSessionState.START, cfg);
        this.setPacketHandler(new SessionStartHandler(this));
    }

    public void notifyTerrainReady() {
        log.debug("Sending spawn notification, waiting for spawn response");
        var state = this.machine.getState();
        if (!state.equals(NetworkSessionState.PRE_SPAWN)) {
            throw new IllegalStateException("attempt to notifyTerrainReady when the state is " + state.name());
        }
        handle.doFirstSpawn();
    }

    public void flushSendBuffer() {
        this.session.flushSendBuffer();
    }

    public void sendPacketImmediately(DataPacket packet) {
        DataPacketSendEvent ev = new DataPacketSendEvent(this.player, packet);
        this.server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }/*
        if (log.isTraceEnabled() && !server.isIgnoredPacket(packet.getClass())) {
            log.trace("Immediate Outbound {}: {}", this.player.getName(), packet);
        }*/
        this.session.sendPacketImmediately(packet);
    }

    public void sendDataPacket(DataPacket packet) {
        DataPacketSendEvent ev = new DataPacketSendEvent(this.player, packet);
        this.server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }
        if (log.isTraceEnabled() && !server.isIgnoredPacket(packet.getClass())) {
            log.trace("Outbound {}: {}", this.player.getName(), packet);
        }
        this.session.sendPacket(packet);
    }

    public void sendPlayStatus(int status, boolean immediate) {
        PlayStatusPacket pk = new PlayStatusPacket();
        pk.status = status;
        if (immediate) {
            this.sendPacketImmediately(pk);
        } else {
            this.sendDataPacket(pk);
        }
    }

    public void onSessionStartSuccess() {
        log.debug("Waiting for login packet");
    }

    private @Nullable Player createPlayer() {
        try {
            PlayerCreationEvent event = new PlayerCreationEvent(Player.class);
            this.server.getPluginManager().callEvent(event);
            Constructor<? extends Player> constructor = event.getPlayerClass().getConstructor(NetworkSession.class, PlayerInfo.class);
            return constructor.newInstance(this, this.info);
        } catch (Exception e) {
            log.error("Failed to create player", e);
        }
        return null;
    }

    private void onServerLoginSuccess() {
        log.debug("Login completed");
        this.sendPlayStatus(PlayStatusPacket.LOGIN_SUCCESS, false);
    }

    private void onClientSpawned() {
        log.debug("Received spawn response, entering in-game phase");
        player.setImmobile(false); //TODO: HACK: we set this during the spawn sequence to prevent the client sending junk movements

        handle.onPlayerLocallyInitialized();
    }

    private void onServerDeath() {

    }

    private void onClientRespawn() {

    }

    public void handleDataPacket(DataPacket packet) {
        if (packet instanceof DisconnectPacket d) {
            this.session.close(d.message);
            return;
        }
        DataPacketReceiveEvent ev = new DataPacketReceiveEvent(this.player, packet);
        this.server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }
        if (this.packetHandler != null) {
            if (this.packetHandler instanceof InGamePacketHandler i) {
                i.managerHandle(packet);
            } else {
                /*var method = this.packetHandler.getClass().getMethod("handle", packet.getClass());
                method.setAccessible(true);
                method.invoke(this.packetHandler, packet);*/
                packet.handle(this.packetHandler);
            }
        }
    }

    public void tick() {
        if (this.session.isDisconnected()) {
            this.disconnect(this.session.getDisconnectReason());
            return;
        }
        this.session.tick();
    }

    public boolean isDisconnected() {
        return disconnected.get();
    }

    public void disconnect(String reason) {
        if (!this.disconnected.compareAndExchange(false, true)) {
            return;
        }
        this.setPacketHandler(null);
        if (this.player != null) {
            this.player.close(reason);
        }
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public String getAddressString() {
        return address.getAddress().getHostAddress();
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    public long getPing() {
        return this.session.getPing();
    }

    @ApiStatus.Internal
    public void sendNetworkSettingsPacket(@NonNull NetworkSettingsPacket pk) {
        this.session.sendNetworkSettingsPacket(pk);
    }

    public void syncAvailableCommands() {
        /*if (!spawned) {
            return;
        }*/
        AvailableCommandsPacket pk = new AvailableCommandsPacket();
        Map<String, CommandDataVersions> data = new HashMap<>();
        int count = 0;
        for (Command command : this.server.getCommandMap().getCommands().values()) {
            if (!command.testPermissionSilent(this.player) || !command.isRegistered() || command.isServerSideOnly()) {
                continue;
            }
            ++count;
            CommandDataVersions data0 = command.generateCustomCommandData(this.player);
            data.put(command.getName(), data0);
        }
        if (count > 0) {
            //TODO: structure checking
            pk.commands = data;
            this.sendDataPacket(pk);
        }
    }

    public void syncCraftingData() {
        this.sendDataPacket(Registries.RECIPE.getCraftingPacket());
    }

    public void syncCreativeContent() {
        var pk = new CreativeContentPacket();
        pk.entries = Registries.CREATIVE.getCreativeItems();
        this.sendDataPacket(pk);
    }

    public void syncInventory() {
        player.getInventory().sendHeldItem(player);

        player.getInventory().sendContents(player);
        player.getInventory().sendArmorContents(player);
        player.getCursorInventory().sendContents(player);
        player.getOffhandInventory().sendContents(player);
        player.getEnderChestInventory().sendContents(player);
    }

    public void setEnableClientCommand(boolean enable) {
        var pk = new SetCommandsEnabledPacket();
        pk.enabled = enable;
        this.sendDataPacket(pk);
        if (enable) {
            this.syncAvailableCommands();
        }
    }


    public @NotNull BedrockServerSession getSession() {
        return this.session;
    }

    public @NotNull Server getServer() {
        return this.server;
    }

    public Player getPlayer() {
        return this.player;
    }

    public PlayerHandle getHandle() {
        return this.handle;
    }

    public @NotNull StateMachine<NetworkSessionState, NetworkSessionState> getMachine() {
        return this.machine;
    }

    public void setPacketHandler(@Nullable final PacketHandler packetHandler) {
        this.packetHandler = packetHandler;
    }
}
