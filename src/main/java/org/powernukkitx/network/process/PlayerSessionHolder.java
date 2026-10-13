package org.powernukkitx.network.process;

import io.netty.channel.Channel;
import io.netty.util.ReferenceCountUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodecHelper;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.data.*;
import org.cloudburstmc.protocol.bedrock.data.camera.CameraAimAssistPresetPacketOperation;
import org.cloudburstmc.protocol.bedrock.data.definitions.ItemDefinition;
import org.cloudburstmc.protocol.bedrock.data.definitions.SimpleItemDefinition;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemVersion;
import org.cloudburstmc.protocol.bedrock.data.payload.ServerTelemetryData;
import org.cloudburstmc.protocol.bedrock.data.payload.common.DimensionType;
import org.cloudburstmc.protocol.bedrock.data.payload.connection.DisconnectPacketMessages;
import org.cloudburstmc.protocol.bedrock.data.payload.editor.ServerEditorConnectionPolicy;
import org.cloudburstmc.protocol.bedrock.data.payload.experiment.Experiments;
import org.cloudburstmc.protocol.bedrock.data.payload.list.PlayerListAddEntry;
import org.cloudburstmc.protocol.bedrock.data.payload.pack.PackIdVersion;
import org.cloudburstmc.protocol.bedrock.data.payload.pack.PackInfoData;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.common.DefinitionRegistry;
import org.cloudburstmc.protocol.common.SimpleDefinitionRegistry;
import org.cloudburstmc.protocol.common.util.OptionalBoolean;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.powernukkitx.Player;
import org.powernukkitx.PlayerHandle;
import org.powernukkitx.Server;
import org.powernukkitx.block.customblock.CustomBlockDefinition;
import org.powernukkitx.config.category.network.RateLimitSettings;
import org.powernukkitx.entity.data.property.EntityProperty;
import org.powernukkitx.event.player.PlayerCreationEvent;
import org.powernukkitx.event.server.PacketSendEvent;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.NukkitMath;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.network.HoldableServerSession;
import org.powernukkitx.network.NetworkConstants;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.network.process.pack.InternalPackManager;
import org.powernukkitx.plugin.InternalPlugin;
import org.powernukkitx.registry.ItemRegistry;
import org.powernukkitx.registry.ItemRuntimeIdRegistry;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.registry.VoxelShapeRegistry;
import org.powernukkitx.utils.DefaultCameraAimAssistPresets;
import org.powernukkitx.utils.DefaultCameraPresets;
import org.powernukkitx.utils.RuntimeBlockDefinitionRegistry;

import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Kaooot
 */
@Data
@Slf4j
public class PlayerSessionHolder {

    private final BedrockServerSession session;
    // Written on the level thread during the spawn handoff and read on the network thread
    private volatile SessionState state = SessionState.INITIAL;
    @Setter(AccessLevel.PRIVATE)
    private volatile Player player;
    private Player.PlayerInfo playerInfo;
    @Setter(AccessLevel.PRIVATE)
    private PlayerHandle playerHandle;
    @Getter(AccessLevel.PRIVATE)
    private final RateLimitSettings rateLimitSettings;
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private long packetRateLimitTimeInMS;
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private long packetRateLimitTimeInTicks;
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private long packetCounter;
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private long packetCounterForTicks;
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private long lastWarnTime;
    private InternalPackManager internalPackManager;

    private static final long WARN_TIME_INTERVAL_IN_MS = 2000L;
    private static final long DISCONNECT_TIMEOUT_SECONDS = 10L;

    private final Object rateLimitLock = new Object();
    private volatile boolean disconnected = false;
    @Getter(AccessLevel.NONE)
    private final AtomicBoolean loginAdmitted = new AtomicBoolean();
    @Getter(AccessLevel.NONE)
    private final AtomicBoolean playerSlotReserved = new AtomicBoolean();
    @Getter(AccessLevel.NONE)
    private final AtomicBoolean playerDataPreloading = new AtomicBoolean();

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Server.PreloadedPlayerData preloadedPlayerData;

    /** Packets that arrived while the player was being created, see {@link #deferUntilSpawned}. */
    @Getter(AccessLevel.NONE)
    private final Queue<BedrockPacket> spawnBacklog = new ConcurrentLinkedQueue<>();

    private static volatile ItemRegistryCache itemRegistryCache;


    /** How often the stored player data is read again while a save of it is still on its way. */
    private static final int MAX_PRELOAD_ATTEMPTS = 5;

    /** Share of a tick a level spends creating joining players before the rest wait for the next tick. */
    private static final int SPAWN_BUDGET_DIVISOR = 5;

    /**
     * Per level: the tick the spawn budget was last counted for, and the nanoseconds spent creating
     * players in it. Only touched by the thread that ticks the level.
     */
    private static final Map<Level, long[]> SPAWN_BUDGETS = Collections.synchronizedMap(new WeakHashMap<>());

    /**
     * The item palette sent to every client, built once instead of per join. Rebuilt when items are
     * registered after it was built. Only the immutable definitions are shared; every client gets a
     * packet of its own, since plugins may change a packet while it is sent.
     */
    private record ItemRegistryCache(int itemDataCount, int customItemCount, NbtMap itemComponents,
                                     List<ItemDefinition> itemDefinitions, DefinitionRegistry<ItemDefinition> definitions) {
        boolean matches() {
            return this.itemDataCount == ItemRuntimeIdRegistry.getITEMDATA().size()
                && this.customItemCount == Registries.ITEM.getCustomItemDefinition().size()
                && this.itemComponents == ItemRegistry.getItemComponents();
        }
    }

    /**
     * Reserves one of the {@link LoginService} slots for this session while its login is verified,
     * or queues the login until a slot frees up, see {@link LoginService#admit}. The slot is freed by
     * {@link #releaseLogin(Server)}, or when the session closes at the latest, and a queued login is
     * dropped from the queue when the session closes first.
     *
     * @param onAdmitted run on the session's event loop once a queued login got its slot; not run
     *                   when the login was admitted right away or refused
     * @return whether the login holds a slot now, waits for one, or was refused
     */
    public LoginService.Admission admitLogin(Server server, Runnable onAdmitted) {
        if (this.loginAdmitted.get()) {
            return LoginService.Admission.ADMITTED;
        }
        final Channel channel = this.session.getPeer().getChannel();
        final Runnable resume = () -> {
            this.markLoginAdmitted(server);
            try {
                channel.eventLoop().execute(() -> {
                    if (this.session.isConnected()) {
                        onAdmitted.run();
                    } else {
                        this.releaseLogin(server);
                    }
                });
            } catch (RejectedExecutionException e) {
                this.releaseLogin(server);
            }
        };
        final LoginService.Admission admission = server.getLoginService().admit(this.remoteAddress(), resume);
        if (admission == LoginService.Admission.ADMITTED) {
            this.markLoginAdmitted(server);
        } else if (admission == LoginService.Admission.QUEUED) {
            channel.closeFuture().addListener(_ -> server.getLoginService().cancelWaiting(resume));
        }
        return admission;
    }

    private void markLoginAdmitted(Server server) {
        this.loginAdmitted.set(true);
        this.session.getPeer().getChannel().closeFuture().addListener(_ -> this.releaseLogin(server));
    }

    /**
     * Frees the login slot taken by {@link #admitLogin(Server, Runnable)}, if any. Safe to call more than once.
     */
    public void releaseLogin(Server server) {
        if (this.loginAdmitted.compareAndSet(true, false)) {
            server.getLoginService().release(this.remoteAddress());
        }
    }

    /**
     * Takes one of the {@code max-players} slots for this session until its player is online, see
     * {@link LoginService#tryReservePlayerSlot(int, int)}. The slot is freed when the session
     * closes at the latest.
     *
     * @return {@code false} when the server is full
     */
    public boolean tryReservePlayerSlot(Server server) {
        if (this.playerSlotReserved.get()) {
            return true;
        }
        if (!server.getLoginService().tryReservePlayerSlot(server.getOnlinePlayers().size(), server.getMaxPlayers())) {
            return false;
        }
        this.playerSlotReserved.set(true);
        this.session.getPeer().getChannel().closeFuture().addListener(_ -> this.releasePlayerSlot(server));
        return true;
    }

    /**
     * Frees the slot taken by {@link #tryReservePlayerSlot(Server)}, if any. Safe to call more than once.
     */
    public void releasePlayerSlot(Server server) {
        if (this.playerSlotReserved.compareAndSet(true, false)) {
            server.getLoginService().releasePlayerSlot();
        }
    }

    private @Nullable InetAddress remoteAddress() {
        return this.session.getSocketAddress() instanceof InetSocketAddress inet ? inet.getAddress() : null;
    }

    public InternalPackManager getInternalPackManager() {
        if (this.internalPackManager == null) {
            this.internalPackManager = new InternalPackManager(this.session, Server.getInstance());
        }
        return this.internalPackManager;
    }

    public boolean checkRateLimits() {
        if (!this.rateLimitSettings.rateLimitEnabled()) {
            return true;
        }
        synchronized (this.rateLimitLock) {
            long now = System.currentTimeMillis();
            if (this.packetRateLimitTimeInMS <= now) {
                this.packetRateLimitTimeInMS = now + 1000L;
                this.packetCounter = 0L;
            }
            if (this.packetRateLimitTimeInTicks <= now) {
                this.packetRateLimitTimeInTicks = now + 50L;
                this.packetCounterForTicks = 0L;
            }
            this.packetCounter++;
            this.packetCounterForTicks++;
            if (this.packetCounter > this.rateLimitSettings.maxInboundPacketsPerSecond() && !this.disconnected) {
                log.warn(
                    "{}: exceeded the limit for the maximum packets per second (limit: {}, received: {}) ",
                    this.player != null ? this.player.getName() : this.session.getSocketAddress(),
                    this.rateLimitSettings.maxInboundPacketsPerSecond(),
                    this.packetCounter
                );
                this.disconnect(DisconnectFailReason.UNKNOWN); // flooding
                return false;
            }
            if (this.packetCounterForTicks > this.rateLimitSettings.maxPacketsPerTick()) {
                if (now - this.lastWarnTime >= WARN_TIME_INTERVAL_IN_MS) {
                    log.warn(
                        "{}: exceeded the limit for the maximum packets per tick (limit: {}, received: {}, excess: {}) ",
                        this.player != null ? this.player.getName() : this.session.getSocketAddress(),
                        this.rateLimitSettings.maxPacketsPerTick(),
                        this.packetCounterForTicks,
                        this.packetCounterForTicks - this.rateLimitSettings.maxPacketsPerTick()
                    );
                    this.lastWarnTime = now;
                }
                return false;
            }
        }
        return true;
    }

    public void sendPlayStatus(PlayStatus status) {
        final PlayStatusPacket packet = new PlayStatusPacket();
        packet.setStatus(status);
        this.session.sendPacketImmediately(packet);
    }

    public void disconnect(DisconnectFailReason reason) {
        this.disconnect(reason, Registries.DISCONNECT_REASON.get(reason));
    }

    public void disconnect(DisconnectFailReason reason, String message) {
        if (this.disconnected) {
            return;
        }
        this.disconnected = true;

        final DisconnectPacket packet = new DisconnectPacket();
        packet.setReason(reason);
        packet.setMessages(new DisconnectPacketMessages(message, ""));

        this.session.sendPacketImmediately(packet);
        // Same idea as BedrockServerSession#disconnect, which we can't use since it drops the reason:
        // let the client read the message and leave first, then close whatever is still open.
        this.session.getPeer().getChannel().eventLoop().schedule(() -> {
            if (this.session.isConnected()) {
                this.session.close(message);
            }
        }, DISCONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        SocketAddress socketAddress = this.getSession().getSocketAddress();

        if (socketAddress instanceof InetSocketAddress address) {
            InetAddress inetAddress = address.getAddress();

            log.debug("[Network session disconnected: {}:{}] - {}",
                inetAddress != null ? inetAddress.getHostAddress() : address.getHostString(),
                address.getPort(),
                message
            );
        } else {
            log.debug("[Network session disconnected] - {}", message);
        }
    }

    public void sendResourcePacksInfo(Server server) {
        final ResourcePacksInfoPacket infoPacket = new ResourcePacksInfoPacket();

        for (final var resourcePack : server.getResourcePackManager().getResourceStack()) {
            final PackInfoData packInfoData = new PackInfoData();
            final PackIdVersion packIdVersion = new PackIdVersion();

            packIdVersion.setPackUUID(resourcePack.getPackId());
            packIdVersion.setPackVersion(resourcePack.getPackVersion());

            packInfoData.setPackIdVersion(packIdVersion);
            packInfoData.setPackSize(resourcePack.getPackSize());

            final String encryptionKey = resourcePack.getEncryptionKey();
            packInfoData.setContentKey(encryptionKey);
            packInfoData.setSubpackName(resourcePack.getSubPackName());
            packInfoData.setContentIdentity(
                encryptionKey.isEmpty() ? "" : resourcePack.getPackId().toString()
            );
            packInfoData.setHasScripts(resourcePack.usesScript());
            packInfoData.setRayTracingCapable(resourcePack.isRaytracingCapable());
            packInfoData.setAddonPack(resourcePack.isAddonPack());
            packInfoData.setCdnUrl(resourcePack.cdnUrl());

            infoPacket.getResourcePacks().add(packInfoData);
        }

        infoPacket.setResourcePackRequired(server.getForceResources());

        final PackIdVersion packIdVersion = new PackIdVersion();
        packIdVersion.setPackUUID(new UUID(0L, 0L));
        packIdVersion.setPackVersion("0.0.0");

        infoPacket.setWorldTemplateIdAndVersion(packIdVersion);
        infoPacket.setForceDisableVibrantVisuals(!server.allowVibrantVisuals());

        this.session.sendPacketImmediately(infoPacket);
    }

    /**
     * Starts creating the player once the client has accepted the resource packs.
     * <p>
     * Called on the network thread. The stored player data is read on the player data thread of
     * the {@link LoginService}, the chunk the player stands in is loaded on the chunk IO executor,
     * and everything that touches the world then runs on the thread of the level the player joins,
     * see {@link #sendBeforeSpawn(Server)}. None of it blocks the network thread, which serves many
     * other sessions.
     */
    public void beginSpawn(Server server) {
        this.setState(SessionState.SPAWNING);
        final UUID uuid = Server.uuidFromXUID(Server.resolveXuid(this.playerInfo, server.getProxyAuthProvider()));
        server.beginPlayerDataPreload(uuid);
        this.playerDataPreloading.set(true);
        this.session.getPeer().getChannel().closeFuture().addListener(_ -> this.endPlayerDataPreload(server, uuid));
        this.schedulePreload(server, uuid, 1);
    }

    private void schedulePreload(Server server, UUID uuid, int attempt) {
        try {
            server.getLoginService().executePlayerData(() -> this.preloadAndSchedule(server, uuid, attempt));
        } catch (RejectedExecutionException e) {
            this.endPlayerDataPreload(server, uuid);
            this.disconnect(DisconnectFailReason.UNKNOWN, "Server is shutting down");
        }
    }

    private void endPlayerDataPreload(Server server, UUID uuid) {
        if (this.playerDataPreloading.compareAndSet(true, false)) {
            server.endPlayerDataPreload(uuid);
        }
    }

    private void preloadAndSchedule(Server server, UUID uuid, int attempt) {
        if (!this.session.isConnected()) {
            this.endPlayerDataPreload(server, uuid);
            return;
        }

        Server.PreloadedPlayerData preloaded = null;
        boolean failed = false;
        try {
            preloaded = server.preloadPlayerData(uuid);
        } catch (Exception e) {
            // processLogin reads storage itself when nothing was preloaded
            log.warn("Failed to preload player data for {}", uuid, e);
            failed = true;
        }
        if (preloaded == null && !failed && attempt < MAX_PRELOAD_ATTEMPTS) {
            server.getScheduler().scheduleDelayedTask(InternalPlugin.INSTANCE,
                () -> this.schedulePreload(server, uuid, attempt + 1), 1);
            return;
        }

        final String levelName = preloaded != null ? preloaded.levelName() : null;
        final Level storedLevel = levelName != null ? server.getLevelByName(levelName) : null;
        final Level level = storedLevel != null ? storedLevel : server.getDefaultLevel();
        final Vector3 position = storedLevel != null ? preloaded.position() : null;
        final Server.PreloadedPlayerData data = preloaded;

        final CompletableFuture<?> spawnChunk = position != null
            ? level.getChunkAsync(NukkitMath.floorDouble(position.x) >> 4, NukkitMath.floorDouble(position.z) >> 4)
                .exceptionally(error -> {
                    log.debug("Could not load the spawn chunk of {} ahead of time", uuid, error);
                    return null;
                })
            : CompletableFuture.completedFuture(null);
        spawnChunk.thenRun(() -> level.getScheduler().scheduleTask(InternalPlugin.INSTANCE,
            () -> this.spawnWithinBudget(server, level, () -> this.completeSpawn(server, uuid, data, attempt))));
    }

    /**
     * Runs {@code spawn} on the level's thread unless the level has already spent its share of this
     * tick on creating players, in which case it waits for the next tick. A burst of joins is spread
     * over several ticks instead of stalling one; the first spawn of a tick always runs.
     */
    private void spawnWithinBudget(Server server, Level level, Runnable spawn) {
        final long[] budget = SPAWN_BUDGETS.computeIfAbsent(level, _ -> new long[]{Long.MIN_VALUE, 0L});
        final long tick = level.getTick();
        if (budget[0] != tick) {
            budget[0] = tick;
            budget[1] = 0L;
        } else if (budget[1] >= server.getNanosPerTick() / SPAWN_BUDGET_DIVISOR) {
            level.getScheduler().scheduleDelayedTask(InternalPlugin.INSTANCE,
                () -> this.spawnWithinBudget(server, level, spawn), 1);
            return;
        }
        final long start = System.nanoTime();
        try {
            spawn.run();
        } finally {
            budget[1] += System.nanoTime() - start;
        }
    }

    /**
     * Whether the player is being created on the level thread right now.
     */
    public boolean isSpawning() {
        final SessionState current = this.state;
        return current == SessionState.SPAWNING || current == SessionState.BEFORE_SPAWN;
    }

    /**
     * Holds a packet that arrived while the player was being created, and handles it on the
     * level thread once the creation is done, in arrival order.
     */
    void deferUntilSpawned(BedrockPacket packet) {
        this.spawnBacklog.add(packet);
        // The creation may have finished, and drained the backlog, while this packet was added
        if (!this.isSpawning()) {
            final Player created = this.player;
            if (created != null) {
                created.getLevel().getScheduler().scheduleTask(InternalPlugin.INSTANCE, this::drainSpawnBacklog);
            } else {
                this.spawnBacklog.clear();
            }
        }
    }

    private void drainSpawnBacklog() {
        final Player created = this.player;
        if (created == null || !created.isConnected()
                || !(this.session.getPacketHandler() instanceof NetworkPacketHandler handler)) {
            this.spawnBacklog.clear();
            return;
        }
        BedrockPacket packet;
        while ((packet = this.spawnBacklog.poll()) != null) {
            try {
                handler.processInbound(packet);
            } catch (Exception e) {
                log.error("Error handling packet {} for player {}", packet.getClass().getSimpleName(), created.getName(), e);
            }
        }
    }

    /**
     * Runs on the level thread: creates the player and sends everything the client needs before it
     * can spawn. A disconnect that happened meanwhile is handled here, because the network thread
     * leaves a session in {@link SessionState#SPAWNING} alone.
     */
    private void completeSpawn(Server server, UUID uuid, Server.PreloadedPlayerData preloaded, int attempt) {
        if (!this.session.isConnected()) {
            this.endPlayerDataPreload(server, uuid);
            return;
        }
        if (preloaded != null && !server.isPreloadCurrent(preloaded) && attempt < MAX_PRELOAD_ATTEMPTS) {
            this.schedulePreload(server, uuid, attempt + 1);
            return;
        }

        this.preloadedPlayerData = preloaded;
        // Off the event loop, so the order of the login burst has to be kept by holding it back;
        // see HoldableServerSession. Released below whatever happens
        final HoldableServerSession holdable = this.session instanceof HoldableServerSession h ? h : null;
        if (holdable != null) {
            holdable.holdOutbound();
        }
        try {
            this.sendBeforeSpawn(server);
        } catch (Exception e) {
            log.error("Failed to spawn a player from {}", this.session.getSocketAddress(), e);
            if (this.player != null) {
                this.player.close("Failed to create player");
            } else {
                this.session.close("Failed to create player");
            }
        } finally {
            this.preloadedPlayerData = null;
            this.endPlayerDataPreload(server, uuid);
            if (holdable != null) {
                holdable.releaseOutbound();
            }
        }

        try {
            this.drainSpawnBacklog();
        } finally {
            this.setState(SessionState.CHUNKS);
        }
        final Player created = this.player;
        if (created != null && !this.session.isConnected()) {
            created.close("Disconnected");
        }
        this.drainSpawnBacklog();
    }

    public void sendBeforeSpawn(Server server) {
        this.doPlayerCreation();
        if (this.player == null || !this.player.isConnected()) {
            // Refused by a plugin or failed to load; closing has already been taken care of
            return;
        }
        this.setState(SessionState.BEFORE_SPAWN);

        server.sendSleepingPlayersStatusImmediately(Set.of(this.player));

        final PlayerListPacket playerListPacket = new PlayerListPacket();
        final PlayerListAddEntry playerListEntry = new PlayerListAddEntry();

        playerListEntry.setUuid(this.player.getUniqueId());
        playerListEntry.setActorUniqueID(this.player.uniqueIdLong());
        playerListEntry.setPlayerName(this.player.getName());
        playerListEntry.setXblXUID(this.player.getXUID());
        playerListEntry.setPlatformOnlineID("");
        playerListEntry.setBuildPlatform(BuildPlatform.UNKNOWN);
        playerListEntry.setSerializedSkin(this.player.getSkin().toSerializedSkin());
        playerListEntry.setPlayerColor(this.player.getLocatorBarColor().getRGB());

        playerListPacket.getEntries().add(playerListEntry);
        this.player.sendPacketImmediately(playerListPacket);

        this.player.getLevel().sendTime(this.player);

        this.sendEncoded(StaticPacket.VOXEL_SHAPES);
        this.sendEncoded(StaticPacket.JIGSAW_STRUCTURES);
        this.sendStartGame(server);

        for (SyncActorPropertyPacket syncActorPropertyPacket : EntityProperty.getEntityPropertyCache()) {
            this.session.sendPacketImmediately(syncActorPropertyPacket);
        }

        this.sendItemRegistry();
        this.player.sendInitialSpawnPosition();

        final SetDifficultyPacket difficultyPacket = new SetDifficultyPacket();
        difficultyPacket.setDifficulty(server.getDifficulty());
        this.player.sendPacketImmediately(difficultyPacket);

        final SetCommandsEnabledPacket commandsEnabledPacket = new SetCommandsEnabledPacket();
        commandsEnabledPacket.setCommandsEnabled(this.player.isEnableClientCommand());
        this.player.sendPacketImmediately(commandsEnabledPacket);

        this.player.getAdventureSettings().updateAdventureSettingsImmediately();

        final GameRulesChangedPacket gameRulesChangedPacket = new GameRulesChangedPacket();
        gameRulesChangedPacket.getRulesData().getRulesList().addAll(
            this.player.getLevel().getGameRules().toNetwork()
        );
        this.player.sendPacketImmediately(gameRulesChangedPacket);

        server.addOnlinePlayer(this.player);
        this.releasePlayerSlot(server);
        server.onPlayerCompleteLoginSequence(this.player);

        this.runInOutboundOrder(() -> {
            this.session.getPeer().getCodecHelper().setBlockDefinitions(new RuntimeBlockDefinitionRegistry());
            this.session.getPeer().getCodecHelper().setCameraPresetDefinitions(DefaultCameraPresets.getDefinitions());
        });

        this.sendEncoded(StaticPacket.BIOME_DEFINITIONS);
        this.sendEncoded(StaticPacket.ACTOR_IDENTIFIERS);

        final PlayerFogPacket playerFogPacket = new PlayerFogPacket();
        playerFogPacket.getFogStack().addAll(this.player.getFogStack());
        this.session.sendPacketImmediately(playerFogPacket);

        // FeatureRegistryPacket not used by PNX.

        final CameraPresetsPacket cameraPresetsPacket = new CameraPresetsPacket();
        cameraPresetsPacket.getCameraPresets().addAll(DefaultCameraPresets.getAll());
        this.session.sendPacketImmediately(cameraPresetsPacket);

        final CameraAimAssistPresetsPacket cameraAimAssistPresetsPacket = new CameraAimAssistPresetsPacket();
        cameraAimAssistPresetsPacket.getCategoryDefinitions().addAll(DefaultCameraAimAssistPresets.getAllCategories());
        cameraAimAssistPresetsPacket.getPresets().addAll(DefaultCameraAimAssistPresets.getAllPresets());
        cameraAimAssistPresetsPacket.setOperation(CameraAimAssistPresetPacketOperation.ADD_TO_EXISTING);
        this.session.sendPacketImmediately(cameraAimAssistPresetsPacket);

        final CameraSplinePacket cameraSplinePacket = new CameraSplinePacket();
        this.session.sendPacketImmediately(cameraSplinePacket);

        this.player.sendInitialLoginAttributes();

        final Collection<Player> collection = Set.of(this.player);
        for (Player value : server.getOnlinePlayers().values()) {
            if (value != this.player) {
                value.getAdventureSettings().sendAbilities(collection);
                value.getAdventureSettings().updateAdventureSettings();
            }
        }

        this.player.sendPotionEffects(this.player);
        this.player.sendData(this.player);
        this.player.syncCreativeContent();

        this.session.sendPacketImmediately(Registries.TRIM.buildPacket());

        this.player.syncInventory();
        this.sendEncoded(StaticPacket.CRAFTING_DATA);

        this.player.setCanClimb(true);
        this.player.setMovementSpeed(this.player.getMovementSpeed());
    }

    private void sendStartGame(Server server) {
        final StartGamePacket packet = new StartGamePacket();
        packet.setEntityID(this.player.uniqueIdLong());
        packet.setRuntimeID(this.player.runtimeId());
        packet.setGameType(GameType.from(Player.toNetworkGamemode(this.player.getGamemode())));
        packet.setPosition(this.player.getInitialSpawnPosition());
        packet.setRotation(Vector2f.from(this.player.getPitch(), this.player.getYaw()));

        packet.getSettings().setSeed(-1L);
        packet.getSettings().getSpawnSettings().setDimension(DimensionType.from(this.player.level.getDimension()));
        packet.getSettings().getSpawnSettings().setType(SpawnBiomeType.DEFAULT);
        packet.getSettings().getSpawnSettings().setUserDefinedBiomeName("minecraft:plains");
        packet.getSettings().setGeneratorType(GeneratorType.OVERWORLD);
        packet.getSettings().setGameType(GameType.from(Player.toNetworkGamemode(server.getDefaultGamemode())));
        packet.getSettings().setHardcore(false);
        packet.getSettings().setGameDifficulty(Difficulty.from(server.getDifficulty()));
        packet.getSettings().setDefaultSpawnBlockPosition(this.player.getSafeSpawn().toNetwork().toInt());
        packet.getSettings().setAchievementsDisabled(true);
        packet.getSettings().setEditorWorldType(EditorWorldType.NON_EDITOR);
        packet.getSettings().setCreatedInEditor(false);
        packet.getSettings().setExportedFromEditor(false);
        packet.getSettings().setDayCycleStopTime((int) this.player.getLevel().getTime());
        packet.getSettings().setEducationEditionOffer(EducationEditionOffer.NONE);
        packet.getSettings().setEducationFeaturesEnabled(false);
        packet.getSettings().setEducationProductID("");
        packet.getSettings().setRainLevel(0f);
        packet.getSettings().setLightningLevel(0f);
        packet.getSettings().setHasConfirmedPlatformLockedContent(false);
        packet.getSettings().setMultiplayerGameIntent(true);
        packet.getSettings().setLanBroadcastIntent(false);
        packet.getSettings().setXboxLiveBroadcastSetting(GamePublishSetting.PUBLIC);
        packet.getSettings().setPlatformBroadcastSetting(GamePublishSetting.PUBLIC);
        packet.getSettings().setCommandsEnabled(this.player.isEnableClientCommand());
        packet.getSettings().setTexturePacksRequired(server.getForceResources());
        packet.getSettings().getRuleData().getRulesList().addAll(this.player.getLevel().getGameRules().toNetwork());
        packet.getSettings().setExperiments(new Experiments());
        packet.getSettings().getExperiments().getToggles().addAll(server.getExperiments());
        packet.getSettings().setWereAnyExperimentsEverToggled(!server.getExperiments().isEmpty());
        packet.getSettings().setHasBonusChestEnabled(false);
        packet.getSettings().setStartWithMapEnabled(false);
        packet.getSettings().setPlayerPermissions(this.player.isOp() ? PlayerPermissionLevel.OPERATOR : PlayerPermissionLevel.MEMBER);
        packet.getSettings().setServerChunkTickRange(4);
        packet.getSettings().setHasLockedBehaviorPack(false);
        packet.getSettings().setHasLockedResourcePack(false);
        packet.getSettings().setFromLockedTemplate(false);
        packet.getSettings().setUseMsaGamertagsOnly(false);
        packet.getSettings().setFromWorldTemplate(false);
        packet.getSettings().setWorldTemplateOptionLocked(false);
        packet.getSettings().setOnlySpawnV1Villagers(false);
        packet.getSettings().setPersonaDisabled(false);
        packet.getSettings().setCustomSkinsDisabled(false);
        packet.getSettings().setEmoteChatMuted(server.getSettings().gameplaySettings().muteEmoteAnnouncements());
        packet.getSettings().setBaseGameVersion("*");
        packet.getSettings().setLimitedWorldWidth(16);
        packet.getSettings().setLimitedWorldDepth(16);
        packet.getSettings().setNetherType(false);
        packet.getSettings().setEduSharedUriResource(EduSharedUriResource.EMPTY);
        packet.getSettings().setOverrideForceExperimentalGameplay(OptionalBoolean.empty());
        packet.getSettings().setChatRestrictionLevel(ChatRestrictionLevel.NONE);
        packet.getSettings().setDisablePlayerInteractions(false);
        packet.getSettings().setServerEditorConnectionPolicy(ServerEditorConnectionPolicy.VANILLA_ONLY);

        packet.setLevelID("");
        packet.setLevelName(server.getSubMotd());
        packet.setTemplateContentIdentity("");
        packet.setTrial(false);
        packet.setMovementSettings(new SyncedPlayerMovementSettings(
                ServerAuthMovementMode.SERVER_AUTHORITATIVE_V3,
                0,
                true
            )
        );
        packet.setLevelCurrentTime(this.player.getLevel().getCurrentTick());
        packet.setEnchantmentSeed(this.player.getEnchantmentSeed());

        packet.getBlockProperties().addAll(Registries.BLOCK.getDataDrivenProperties());
        for (final CustomBlockDefinition definition : Registries.BLOCK.getCustomBlockDefinitionList()) {
            packet.getBlockProperties().add(definition.toNetwork());
        }
        packet.setMultiplayerCorrelationId("");
        packet.setEnableItemStackNetManager(true);
        packet.setServerVersion("");
        packet.setPlayerPropertyData(EntityProperty.getPlayerPropertyCache());
        packet.setWorldTemplateID(new UUID(0L, 0L));
        packet.setBlockNetworkIdsAreHashes(true);
        packet.setServerTelemetryData(new ServerTelemetryData());
        this.player.sendPacketImmediately(packet);
    }

    private void sendItemRegistry() {
        final ItemRegistryCache cache = itemRegistryCache();
        this.runInOutboundOrder(() -> this.session.getPeer().getCodecHelper().setItemDefinitions(cache.definitions()));
        this.sendEncoded(StaticPacket.ITEM_REGISTRY);
    }

    /**
     * The packets of the login sequence that are the same for every player, each with the
     * {@link EncodedPacketCache} it is sent from and what identifies its current content there, see
     * {@link EncodedPacketCache#get}. Sending and encoding ahead of time, see
     * {@link #warmEncodedPackets()}, both go through the same constant, so they always agree on the
     * content, and sending one allocates nothing beyond the packet that goes out.
     */
    private enum StaticPacket {
        VOXEL_SHAPES(true, VoxelShapeRegistry::getPACKET),
        JIGSAW_STRUCTURES(true, Registries.STRUCTURE::getJigsawStructureData),
        ITEM_REGISTRY(true, PlayerSessionHolder::itemRegistryCache, () -> 0L, () -> null, PlayerSessionHolder::itemRegistryPacket),
        BIOME_DEFINITIONS(false, () -> Registries.BIOME, Registries.BIOME::getBiomeDefinitionListFingerprint, () -> null,
            Registries.BIOME::getBiomeDefinitionListPacket),
        ACTOR_IDENTIFIERS(false, Registries.ENTITY::getTag, () -> 0L, () -> null, PlayerSessionHolder::actorIdentifiersPacket),
        CRAFTING_DATA(true, Registries.RECIPE::getCraftingPacket, () -> 0L, PlayerSessionHolder::itemRegistryCache,
            Registries.RECIPE::getCraftingPacket),
        CREATIVE_CONTENT(true, () -> Registries.CREATIVE, PlayerSessionHolder::creativeContentFingerprint,
            PlayerSessionHolder::itemRegistryCache, PlayerSessionHolder::creativeContentPacket);

        private final EncodedPacketCache cache = new EncodedPacketCache(NetworkConstants.CODEC);
        /** Whether the packet goes through the player, firing {@link PacketSendEvent}, when it is sent the regular way. */
        private final boolean fireEvent;
        private final Supplier<?> key;
        private final LongSupplier stamp;
        private final Supplier<?> dependency;
        private final Supplier<? extends BedrockPacket> packet;

        /** For a packet that is its own key, kept until a registry replaces it. */
        StaticPacket(boolean fireEvent, Supplier<? extends BedrockPacket> packet) {
            this(fireEvent, packet, () -> 0L, () -> null, packet);
        }

        StaticPacket(boolean fireEvent, Supplier<?> key, LongSupplier stamp, Supplier<?> dependency,
                     Supplier<? extends BedrockPacket> packet) {
            this.fireEvent = fireEvent;
            this.key = key;
            this.stamp = stamp;
            this.dependency = dependency;
            this.packet = packet;
        }

        BedrockPacket encoded() {
            return this.cache.get(this.key.get(), this.stamp.getAsLong(), this.dependency.get(), this.packet,
                PlayerSessionHolder::prepareEncodingHelper);
        }
    }

    private static ItemRegistryPacket itemRegistryPacket() {
        final ItemRegistryPacket packet = new ItemRegistryPacket();
        packet.getItemData().addAll(itemRegistryCache().itemDefinitions());
        return packet;
    }

    private static AvailableActorIdentifiersPacket actorIdentifiersPacket() {
        final AvailableActorIdentifiersPacket packet = new AvailableActorIdentifiersPacket();
        packet.setActorInfoList(Registries.ENTITY.getTag());
        return packet;
    }

    private static long creativeContentFingerprint() {
        return EncodedPacketCache.identityFingerprint(Registries.CREATIVE.getCreativeGroups(), Registries.CREATIVE.getCreativeItemData());
    }

    private static CreativeContentPacket creativeContentPacket() {
        final CreativeContentPacket packet = new CreativeContentPacket();
        packet.getGroups().addAll(Registries.CREATIVE.getCreativeGroups());
        packet.getEntries().addAll(Registries.CREATIVE.getCreativeItemData());
        return packet;
    }

    /**
     * Encodes every packet of the login sequence that is the same for all players, so the first
     * players to join do not pay for it. Meant to run once the registries are complete, after the
     * plugins have been enabled; anything registered later is encoded again when first sent.
     * Safe to call from any thread, and concurrently with logins.
     */
    @ApiStatus.Internal
    public static void warmEncodedPackets() {
        for (StaticPacket packet : StaticPacket.values()) {
            try {
                if (packet.key.get() != null) {
                    ReferenceCountUtil.release(packet.encoded());
                }
            } catch (Exception e) {
                log.warn("Could not encode the {} login packet ahead of time", packet, e);
            }
        }
    }

    /**
     * Sends a packet that is the same for every player, from its {@link EncodedPacketCache} when
     * nothing can change it on the way out, and the regular way otherwise.
     */
    private void sendEncoded(StaticPacket packet) {
        if (trySendEncoded(this.player, packet)) {
            return;
        }
        if (packet.fireEvent) {
            this.player.sendPacketImmediately(packet.packet.get());
        } else {
            this.session.sendPacketImmediately(packet.packet.get());
        }
    }

    /**
     * Whether packets for {@code player} may come from an {@link EncodedPacketCache}: it is no
     * plugin's subclass of {@link Player}, whose send methods may do more than send; no plugin
     * listens to {@link PacketSendEvent}, which could change a packet on its way out; and its
     * session speaks the codec the caches encode with.
     */
    private static boolean canSendEncoded(Player player) {
        return player.getClass() == Player.class && PacketSendEvent.getHandlers().isEmpty()
            && player.getSession().getCodec() == NetworkConstants.CODEC;
    }

    private static boolean trySendEncoded(Player player, StaticPacket packet) {
        if (!canSendEncoded(player) || packet.key.get() == null) {
            return false;
        }
        if (player.getSession().isConnected()) {
            player.getSession().sendPacketImmediately(packet.encoded());
        }
        return true;
    }

    /**
     * Sends the creative inventory content from its {@link EncodedPacketCache}, encoding it again
     * whenever its groups or entries change.
     *
     * @return {@code false} when the content has to be sent the regular way instead, see
     * {@link Player#syncCreativeContent()}
     */
    @ApiStatus.Internal
    public static boolean trySendCreativeContent(Player player) {
        return trySendEncoded(player, StaticPacket.CREATIVE_CONTENT);
    }

    private static void prepareEncodingHelper(BedrockCodecHelper helper) {
        helper.setItemDefinitions(itemRegistryCache().definitions());
        helper.setBlockDefinitions(new RuntimeBlockDefinitionRegistry());
        helper.setCameraPresetDefinitions(DefaultCameraPresets.getDefinitions());
        if (Server.getInstance().getSettings().debugSettings().disableEncodingLimits()) {
            helper.setEncodingSettings(EncodingSettings.UNLIMITED);
        }
    }

    /**
     * Applies a codec helper change at the current point of the outbound stream, so packets sent
     * before it are still encoded without it. See {@link HoldableServerSession#runInOrder}.
     * <p>
     * Only a {@link HoldableServerSession} keeps that order when called off the session's event
     * loop. Every transport PowerNukkitX binds creates one; a session of another kind gets the
     * change applied right away.
     */
    private void runInOutboundOrder(Runnable action) {
        if (this.session instanceof HoldableServerSession holdable) {
            holdable.runInOrder(action);
        } else {
            action.run();
        }
    }

    private static ItemRegistryCache itemRegistryCache() {
        ItemRegistryCache cache = itemRegistryCache;
        if (cache != null && cache.matches()) {
            return cache;
        }
        synchronized (PlayerSessionHolder.class) {
            cache = itemRegistryCache;
            if (cache != null && cache.matches()) {
                return cache;
            }
            final int itemDataCount = ItemRuntimeIdRegistry.getITEMDATA().size();
            final int customItemCount = Registries.ITEM.getCustomItemDefinition().size();
            final NbtMap itemComponents = ItemRegistry.getItemComponents();
            final List<ItemDefinition> itemDefinitions = new ObjectArrayList<>();

            for (ItemRuntimeIdRegistry.ItemData data : ItemRuntimeIdRegistry.getITEMDATA()) {
                CompoundTag tag = new CompoundTag();
                if (itemComponents.containsKey(data.identifier())) {
                    NbtMap itemTag = itemComponents.getCompound(data.identifier());
                    tag.putCompound("components", CompoundTag.fromNetwork(itemTag.getCompound("components")));
                } else if (Registries.ITEM.getCustomItemDefinition().containsKey(data.identifier())) {
                    tag = Registries.ITEM.getCustomItemDefinition().get(data.identifier()).nbt();
                }
                final NbtMap components = tag.toNetwork();
                itemDefinitions.add(
                    new SimpleItemDefinition(
                        data.identifier(),
                        data.runtimeId(),
                        ItemVersion.from(data.version()),
                        data.componentBased(),
                        components
                    )
                );
            }

            // Shared by every session: the definitions and the registry are only read once built
            final DefinitionRegistry<ItemDefinition> itemDefinitionRegistry = new SimpleDefinitionRegistry.Builder<ItemDefinition>()
                .addAll(itemDefinitions)
                .build();

            cache = new ItemRegistryCache(itemDataCount, customItemCount, itemComponents,
                List.copyOf(itemDefinitions), itemDefinitionRegistry);
            itemRegistryCache = cache;
            return cache;
        }
    }

    public void doPlayerCreation() {
        log.debug("Creating player");

        this.player = this.createPlayer(playerInfo);
        if (this.player == null) {
            this.session.close("Failed to create player");
            return;
        }
        this.playerHandle = new PlayerHandle(this.player);
        this.player.setPlayerHandle(this.playerHandle);
        this.player.setPreloadedPlayerData(this.preloadedPlayerData);
        if (this.session.getPacketHandler() instanceof NetworkPacketHandler networkPacketHandler) {
            this.player.setInboundProcessor(networkPacketHandler::processInbound);
        }
        Server.getInstance().onPlayerLogin((InetSocketAddress) this.session.getSocketAddress(), player);
        if (!this.player.isConnected()) {
            return;
        }
        this.playerHandle.processLogin();
    }

    private @Nullable Player createPlayer(Player.PlayerInfo playerInfo) {
        try {
            PlayerCreationEvent event = new PlayerCreationEvent(Player.class, playerInfo);
            Server.getInstance().getPluginManager().callEvent(event);
            Constructor<? extends Player> constructor = event.getPlayerClass().getConstructor(BedrockServerSession.class, Player.PlayerInfo.class);
            return constructor.newInstance(this.session, playerInfo);
        } catch (Exception e) {
            log.error("Failed to create player", e);
        }
        return null;
    }
}
