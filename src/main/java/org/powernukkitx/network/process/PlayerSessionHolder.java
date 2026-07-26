package org.powernukkitx.network.process;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.nbt.NbtMap;
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
import org.cloudburstmc.protocol.bedrock.data.payload.pack.PackIdVersion;
import org.cloudburstmc.protocol.bedrock.data.payload.pack.PackInfoData;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.common.DefinitionRegistry;
import org.cloudburstmc.protocol.common.SimpleDefinitionRegistry;
import org.cloudburstmc.protocol.common.util.OptionalBoolean;
import org.jetbrains.annotations.Nullable;
import org.powernukkitx.Player;
import org.powernukkitx.PlayerHandle;
import org.powernukkitx.Server;
import org.powernukkitx.block.customblock.CustomBlockDefinition;
import org.powernukkitx.config.category.network.RateLimitSettings;
import org.powernukkitx.entity.data.property.EntityProperty;
import org.powernukkitx.event.player.PlayerCreationEvent;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.network.process.pack.InternalPackManager;
import org.powernukkitx.registry.ItemRegistry;
import org.powernukkitx.registry.ItemRuntimeIdRegistry;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.registry.VoxelShapeRegistry;
import org.powernukkitx.utils.DefaultCameraAimAssistPresets;
import org.powernukkitx.utils.DefaultCameraPresets;
import org.powernukkitx.utils.RuntimeBlockDefinitionRegistry;

import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author Kaooot
 */
@Data
@Slf4j
public class PlayerSessionHolder {

    private final BedrockServerSession session;
    private SessionState state = SessionState.INITIAL;
    @Setter(AccessLevel.PRIVATE)
    private Player player;
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

    private final Object rateLimitLock = new Object();
    private boolean disconnected = false;

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
                return false; // excess is dropped
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
        this.disconnected = true;
        this.disconnect(reason, Registries.DISCONNECT_REASON.get(reason));
    }

    public void disconnect(DisconnectFailReason reason, String message) {
        final DisconnectPacket packet = new DisconnectPacket();
        packet.setReason(reason);
        packet.setMessages(new DisconnectPacketMessages(message, ""));
        this.session.sendPacketImmediately(packet);
    }

    public void sendResourcePacksInfo(Server server) {
        final ResourcePacksInfoPacket infoPacket = new ResourcePacksInfoPacket();
        infoPacket.getResourcePacks().addAll(Arrays.stream(server.getResourcePackManager().getResourceStack())
            .map(resourcePack -> {
                final PackInfoData packInfoData = new PackInfoData();
                final PackIdVersion packIdVersion = new PackIdVersion();
                packIdVersion.setPackUUID(resourcePack.getPackId());
                packIdVersion.setPackVersion(resourcePack.getPackVersion());

                packInfoData.setPackIdVersion(packIdVersion);
                packInfoData.setPackSize(resourcePack.getPackSize());
                packInfoData.setContentKey(resourcePack.getEncryptionKey());
                packInfoData.setSubpackName(resourcePack.getSubPackName());
                packInfoData.setContentIdentity(!resourcePack.getEncryptionKey().isEmpty() ? resourcePack.getPackId().toString() : "");
                packInfoData.setHasScripts(resourcePack.usesScript());
                packInfoData.setRayTracingCapable(resourcePack.isRaytracingCapable());
                packInfoData.setAddonPack(resourcePack.isAddonPack());
                packInfoData.setCdnUrl(resourcePack.cdnUrl());
                return packInfoData;
            })
            .toList()
        );
        infoPacket.setResourcePackRequired(server.getForceResources());
        final PackIdVersion packIdVersion = new PackIdVersion();
        packIdVersion.setPackUUID(new UUID(0L, 0L));
        packIdVersion.setPackVersion("0.0.0");
        infoPacket.setWorldTemplateIdAndVersion(packIdVersion);
        infoPacket.setForceDisableVibrantVisuals(!server.allowVibrantVisuals());
        this.session.sendPacketImmediately(infoPacket);
    }

    public void sendBeforeSpawn(Server server) {
        this.doPlayerCreation();
        this.setState(SessionState.BEFORE_SPAWN);
        this.player.sendPacketImmediately(VoxelShapeRegistry.getPACKET());
        this.sendStartGame(server);
        this.sendItemRegistry();

        this.session.getPeer().getCodecHelper().setBlockDefinitions(new RuntimeBlockDefinitionRegistry());

        final AvailableActorIdentifiersPacket availableActorIdentifiersPacket = new AvailableActorIdentifiersPacket();
        availableActorIdentifiersPacket.setActorInfoList(Registries.ENTITY.getTag());
        this.session.sendPacketImmediately(availableActorIdentifiersPacket);

        for (SyncActorPropertyPacket syncActorPropertyPacket : EntityProperty.getEntityPropertyCache()) {
            this.session.sendPacketImmediately(syncActorPropertyPacket);
        }

        this.session.sendPacketImmediately(Registries.BIOME.getBiomeDefinitionListPacket());
        this.player.syncAttributes();
        this.player.syncAvailableCommands();

        final Collection<Player> collection = Set.of(this.player);
        for (Player value : server.getOnlinePlayers().values()) {
            if (value != this.player) {
                value.getAdventureSettings().sendAbilities(collection);
                value.getAdventureSettings().updateAdventureSettings();
            }
        }

        this.player.sendPotionEffects(this.player);
        this.player.sendData(this.player);
        this.player.syncInventory();
        this.player.syncCreativeContent();
        this.player.sendAttributes();

        this.session.sendPacketImmediately(Registries.TRIM.buildPacket());

        this.player.setCanClimb(true);
        this.player.setMovementSpeed(this.player.getMovementSpeed());

        server.addOnlinePlayer(this.player);
        server.onPlayerCompleteLoginSequence(this.player);
        this.player.setImmobile(false);

        final PlayerFogPacket playerFogPacket = new PlayerFogPacket();
        playerFogPacket.getFogStack().addAll(this.player.getFogStack());
        this.session.sendPacketImmediately(playerFogPacket);

        final CameraPresetsPacket cameraPresetsPacket = new CameraPresetsPacket();
        cameraPresetsPacket.getCameraPresets().addAll(DefaultCameraPresets.getAll());
        this.session.sendPacketImmediately(cameraPresetsPacket);

        final CameraAimAssistPresetsPacket cameraAimAssistPresetsPacket = new CameraAimAssistPresetsPacket();
        cameraAimAssistPresetsPacket.getCategoryDefinitions().addAll(DefaultCameraAimAssistPresets.getAllCategories());
        cameraAimAssistPresetsPacket.getPresets().addAll(DefaultCameraAimAssistPresets.getAllPresets());
        cameraAimAssistPresetsPacket.setOperation(CameraAimAssistPresetPacketOperation.SET);
        this.session.sendPacketImmediately(cameraAimAssistPresetsPacket);
    }

    private void sendStartGame(Server server) {
        final StartGamePacket packet = new StartGamePacket();
        packet.setEntityID(this.player.getId());
        packet.setRuntimeID(this.player.getId());
        packet.setGameType(GameType.from(Player.toNetworkGamemode(this.player.getGamemode())));
        packet.setPosition(Vector3f.from(
            this.player.x,
            (this.player.isOnGround() ? this.player.y + this.player.getEyeHeight() : this.player.y),
            this.player.z
        ));
        packet.setRotation(Vector2f.from(this.player.getYaw(), this.player.getPitch()));

        packet.getSettings().setSeed(-1L);
        packet.getSettings().getSpawnSettings().setDimension(DimensionType.from(this.player.level.getDimension()));
        packet.getSettings().getSpawnSettings().setType(SpawnBiomeType.DEFAULT);
        packet.getSettings().getSpawnSettings().setUserDefinedBiomeName("plains");
        packet.getSettings().setGeneratorType(GeneratorType.OVERWORLD);
        packet.getSettings().setGameType(GameType.from(Player.toNetworkGamemode(server.getDefaultGamemode())));
        packet.getSettings().setHardcore(false);
        packet.getSettings().setGameDifficulty(Difficulty.from(server.getDifficulty()));
        packet.getSettings().setDefaultSpawnBlockPosition(this.player.getSafeSpawn().toNetwork().toInt());
        packet.getSettings().setAchievementsDisabled(true);
        packet.getSettings().setEditorWorldType(EditorWorldType.NON_EDITOR);
        packet.getSettings().setCreatedInEditor(false);
        packet.getSettings().setExportedFromEditor(false);
        packet.getSettings().setDayCycleStopTime(-1);
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
        packet.getBlockProperties().addAll(
            Registries.BLOCK.getCustomBlockDefinitionList()
                .stream()
                .map(CustomBlockDefinition::toNetwork)
                .toList()
        );
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
        final ItemRegistryPacket itemRegistryPacket = new ItemRegistryPacket();
        final List<ItemDefinition> itemDefinitions = new ObjectArrayList<>();

        for (ItemRuntimeIdRegistry.ItemData data : ItemRuntimeIdRegistry.getITEMDATA()) {
            CompoundTag tag = new CompoundTag();
            if (ItemRegistry.getItemComponents().containsKey(data.identifier())) {
                NbtMap itemTag = ItemRegistry.getItemComponents().getCompound(data.identifier());
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
                    !components.isEmpty(),
                    components
                )
            );
        }
        itemRegistryPacket.getItemData().addAll(itemDefinitions);

        final DefinitionRegistry<ItemDefinition> itemDefinitionRegistry = new SimpleDefinitionRegistry.Builder<ItemDefinition>()
            .addAll(itemDefinitions)
            .build();
        this.session.getPeer().getCodecHelper().setItemDefinitions(itemDefinitionRegistry);

        this.player.sendPacketImmediately(itemRegistryPacket);
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
        if (this.session.getPacketHandler() instanceof NetworkPacketHandler networkPacketHandler) {
            this.player.setInboundProcessor(networkPacketHandler::processInbound);
        }
        Server.getInstance().onPlayerLogin((InetSocketAddress) this.session.getSocketAddress(), player);
        this.playerHandle.processLogin();
        // The reason why teleport player to their position is for gracefully client-side spawn,
        // although we need some hacks, It is definitely a fairly worthy trade.
        this.player.setImmobile(true); //TODO: HACK: fix client-side falling pre-spawn
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
