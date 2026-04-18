package cn.nukkit.network.process;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.block.customblock.CustomBlockDefinition;
import cn.nukkit.entity.data.property.EntityProperty;
import cn.nukkit.event.player.PlayerCreationEvent;
import cn.nukkit.network.protocol.types.TrimData;
import cn.nukkit.registry.ItemRegistry;
import cn.nukkit.registry.ItemRuntimeIdRegistry;
import cn.nukkit.registry.Registries;
import cn.nukkit.registry.VoxelShapeRegistry;
import cn.nukkit.utils.DefaultCameraAimAssistPresets;
import cn.nukkit.utils.DefaultCameraPresets;
import cn.nukkit.utils.RuntimeBlockDefinitionRegistry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.data.*;
import org.cloudburstmc.protocol.bedrock.data.camera.CameraAimAssistPresetPacketOperation;
import org.cloudburstmc.protocol.bedrock.data.definitions.ItemDefinition;
import org.cloudburstmc.protocol.bedrock.data.definitions.SimpleItemDefinition;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemVersion;
import org.cloudburstmc.protocol.bedrock.data.payload.connection.DisconnectPacketMessages;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.common.DefinitionRegistry;
import org.cloudburstmc.protocol.common.SimpleDefinitionRegistry;
import org.cloudburstmc.protocol.common.util.OptionalBoolean;
import org.jetbrains.annotations.Nullable;

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
    private Player player;
    private Player.PlayerInfo playerInfo;
    private PlayerHandle playerHandle;

    public void sendPlayStatus(PlayStatus status) {
        final PlayStatusPacket packet = new PlayStatusPacket();
        packet.setStatus(status);
        this.session.sendPacketImmediately(packet);
    }

    public void disconnect(DisconnectFailReason reason) {
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
                .map(resourcePack -> new ResourcePacksInfoPacket.Entry(
                        resourcePack.getPackId(),
                        resourcePack.getPackVersion(),
                        resourcePack.getPackSize(),
                        resourcePack.getEncryptionKey(),
                        resourcePack.getSubPackName(),
                        resourcePack.getEncryptionKey(),
                        resourcePack.usesScript(),
                        resourcePack.isRaytracingCapable(),
                        resourcePack.isAddonPack(),
                        resourcePack.cdnUrl()
                ))
                .toList()
        );
        infoPacket.setResourcePackRequired(server.getForceResources());
        infoPacket.setWorldTemplateUUID(new UUID(0L, 0L));
        infoPacket.setWorldTemplateVersion("0.0.0");
        infoPacket.setForceDisableVibrantVisuals(!server.allowVibrantVisuals());
        this.session.sendPacketImmediately(infoPacket);
    }

    public void sendBeforeSpawn(Server server) {
        this.doPlayerCreation(this.playerInfo);
        this.setState(SessionState.BEFORE_SPAWN);
        this.session.sendPacketImmediately(VoxelShapeRegistry.getPACKET());
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

        final TrimDataPacket trimDataPacket = new TrimDataPacket();
        trimDataPacket.getTrimMaterialList().addAll(TrimData.trimMaterials);
        trimDataPacket.getTrimPatternList().addAll(TrimData.trimPatterns);
        this.session.sendPacketImmediately(trimDataPacket);

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

        this.playerHandle.doFirstSpawn();
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
        packet.getSettings().getSpawnSettings().setDimension(Dimension.from(this.player.level.getDimension()));
        packet.getSettings().getSpawnSettings().setType(SpawnBiomeType.DEFAULT);
        packet.getSettings().getSpawnSettings().setUserDefinedBiomeName("plains");
        packet.getSettings().setGeneratorType(GeneratorType.OVERWORLD);
        packet.getSettings().setGameType(GameType.from(Player.toNetworkGamemode(server.getDefaultGamemode())));
        packet.getSettings().setHardcoreModeEnabled(false);
        packet.getSettings().setGameDifficulty(Difficulty.from(server.getDifficulty()));
        packet.getSettings().setDefaultSpawnBlockPosition(this.player.getSafeSpawn().toNetwork().toInt());
        packet.getSettings().setAchievementsDisabled(true);
        packet.getSettings().setEditorWorldType(EditorWorldType.NON_EDITOR);
        packet.getSettings().setCreatedInEditor(false);
        packet.getSettings().setExportedFromEditor(false);
        packet.getSettings().setDayCycleStopTime(-1);
        packet.getSettings().setEducationEditionOffer(EducationEditionOffer.NONE);
        packet.getSettings().setAreEducationFeaturesEnabled(false);
        packet.getSettings().setEducationProductionId("");
        packet.getSettings().setRainLevel(0f);
        packet.getSettings().setLightningLevel(0f);
        packet.getSettings().setHasConfirmedPlatformLockedContent(false);
        packet.getSettings().setWasMultiplayerIntendedToBeEnabled(true);
        packet.getSettings().setWasLANBroadcastingIntendedToBeEnabled(false);
        packet.getSettings().setXboxLiveBroadcastSetting(GamePublishSetting.PUBLIC);
        packet.getSettings().setPlatformBroadcastSetting(GamePublishSetting.PUBLIC);
        packet.getSettings().setCommandsEnabled(this.player.isEnableClientCommand());
        packet.getSettings().setTexturePacksRequired(server.getForceResources());
        packet.getSettings().getRuleData().getRulesList().addAll(this.player.getLevel().getGameRules().toNetwork());
        packet.getSettings().getExperiments().addAll(server.getExperiments());
        packet.getSettings().setWereAnyExperimentsEverToggled(!server.getExperiments().isEmpty());
        packet.getSettings().setHasBonusChestEnabled(false);
        packet.getSettings().setStartWithMapEnabled(false);
        packet.getSettings().setPlayerPermissions(this.player.isOp() ? PlayerPermissionLevel.OPERATOR : PlayerPermissionLevel.MEMBER);
        packet.getSettings().setServerChunkTickRange(4);
        packet.getSettings().setHasLockedBehaviorPack(false);
        packet.getSettings().setHasLockedResourcePack(false);
        packet.getSettings().setFromLockedWorldTemplate(false);
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
        packet.getSettings().setForceExperimentalGameplay(OptionalBoolean.empty());
        packet.getSettings().setChatRestrictionLevel(ChatRestrictionLevel.NONE);
        packet.getSettings().setDisablePlayerInteractions(false);

        packet.setLevelId("");
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
        packet.setServerID("");
        packet.setScenarioID("");
        packet.setWorldID("");
        packet.setOwnerID("");
        this.player.dataPacketImmediately(packet);
    }

    private void sendItemRegistry() {
        final ItemRegistryPacket itemRegistryPacket = new ItemRegistryPacket();
        final List<ItemDefinition> itemDefinitions = new ObjectArrayList<>();

        for (ItemRuntimeIdRegistry.ItemData data : ItemRuntimeIdRegistry.getITEMDATA()) {
            NbtMapBuilder tag = NbtMap.builder();
            if (ItemRegistry.getItemComponents().containsKey(data.identifier())) {
                NbtMap itemTag = ItemRegistry.getItemComponents().getCompound(data.identifier());
                tag.putCompound("components", itemTag.getCompound("components"));
            } else if (Registries.ITEM.getCustomItemDefinition().containsKey(data.identifier())) {
                tag = Registries.ITEM.getCustomItemDefinition().get(data.identifier()).nbt().toBuilder();
            }
            final NbtMap components = tag.build();
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

        this.player.dataPacketImmediately(itemRegistryPacket);
    }

    public void doPlayerCreation(Player.PlayerInfo playerInfo) {
        log.debug("Creating player");

        this.player = this.createPlayer(playerInfo);
        if (this.player == null) {
            this.session.close("Failed to create player");
            return;
        }
        this.playerHandle = new PlayerHandle(this.player);
        Server.getInstance().onPlayerLogin((InetSocketAddress) this.session.getSocketAddress(), player);
        this.playerHandle.processLogin();
        // The reason why teleport player to their position is for gracefully client-side spawn,
        // although we need some hacks, It is definitely a fairly worthy trade.
        this.player.setImmobile(true); //TODO: HACK: fix client-side falling pre-spawn
    }

    private @Nullable Player createPlayer(Player.PlayerInfo playerInfo) {
        try {
            PlayerCreationEvent event = new PlayerCreationEvent(Player.class);
            Server.getInstance().getPluginManager().callEvent(event);
            Constructor<? extends Player> constructor = event.getPlayerClass().getConstructor(BedrockServerSession.class, Player.PlayerInfo.class);
            return constructor.newInstance(this.session, playerInfo);
        } catch (Exception e) {
            log.error("Failed to create player", e);
        }
        return null;
    }
}