package cn.nukkit.network.process;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.customblock.CustomBlockDefinition;
import cn.nukkit.entity.data.property.EntityProperty;
import cn.nukkit.network.protocol.types.TrimData;
import cn.nukkit.registry.ItemRegistry;
import cn.nukkit.registry.ItemRuntimeIdRegistry;
import cn.nukkit.registry.Registries;
import cn.nukkit.registry.VoxelShapeRegistry;
import cn.nukkit.utils.RuntimeBlockDefinitionRegistry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Data;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.data.*;
import org.cloudburstmc.protocol.bedrock.data.definitions.ItemDefinition;
import org.cloudburstmc.protocol.bedrock.data.definitions.SimpleItemDefinition;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemVersion;
import org.cloudburstmc.protocol.bedrock.data.payload.connection.DisconnectPacketMessages;
import org.cloudburstmc.protocol.bedrock.packet.AvailableActorIdentifiersPacket;
import org.cloudburstmc.protocol.bedrock.packet.DisconnectPacket;
import org.cloudburstmc.protocol.bedrock.packet.ItemRegistryPacket;
import org.cloudburstmc.protocol.bedrock.packet.PlayStatusPacket;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePacksInfoPacket;
import org.cloudburstmc.protocol.bedrock.packet.StartGamePacket;
import org.cloudburstmc.protocol.bedrock.packet.SyncActorPropertyPacket;
import org.cloudburstmc.protocol.bedrock.packet.TrimDataPacket;
import org.cloudburstmc.protocol.common.DefinitionRegistry;
import org.cloudburstmc.protocol.common.SimpleDefinitionRegistry;
import org.cloudburstmc.protocol.common.util.OptionalBoolean;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author Kaooot
 */
@Data
public class PlayerSessionHolder {

    private final BedrockServerSession session;
    private SessionState state = SessionState.INITIAL;
    private Player player;

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
        infoPacket.setWorldTemplateVersion("");
        infoPacket.setForceDisableVibrantVisuals(!server.allowVibrantVisuals());
        this.session.sendPacketImmediately(infoPacket);
    }

    public void sendBeforeSpawn(Server server) {
        this.setState(SessionState.BEFORE_SPAWN);
        this.session.sendPacketImmediately(VoxelShapeRegistry.getPACKET());
        this.sendStartGame();
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

        final TrimDataPacket trimDataPacket = new TrimDataPacket();
        trimDataPacket.getTrimMaterialList().addAll(TrimData.trimMaterials);
        trimDataPacket.getTrimPatternList().addAll(TrimData.trimPatterns);
        this.session.sendPacketImmediately(trimDataPacket);

        this.player.setCanClimb(true);
        this.player.setMovementSpeed(this.player.getMovementSpeed());

        server.addOnlinePlayer(this.player);
        server.onPlayerCompleteLoginSequence(this.player);

        this.player.doFirstSpawn();
    }

    private void sendStartGame() {
        Server server = this.player.getServer();
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
        packet.getSettings().setGeneratorType(GeneratorType.UNDEFINED);
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
        packet.getSettings().setWasMultiplayerIntendedToBeEnabled(false);
        packet.getSettings().setWasLANBroadcastingIntendedToBeEnabled(false);
        packet.getSettings().setXboxLiveBroadcastSetting(GamePublishSetting.PUBLIC);
        packet.getSettings().setPlatformBroadcastSetting(GamePublishSetting.PUBLIC);
        packet.getSettings().setCommandsEnabled(this.player.isEnableClientCommand());
        packet.getSettings().setTexturePacksRequired(server.getForceResources());
        packet.getSettings().getRuleData().getRulesList().addAll(this.player.getLevel().getGameRules().toNetwork());
        packet.getSettings().getExperiments().addAll(server.getExperiments());
        packet.getSettings().setHasBonusChestEnabled(false);
        packet.getSettings().setStartWithMapEnabled(false);
        packet.getSettings().setPlayerPermissions(this.player.isOp() ? PlayerPermissionLevel.OPERATOR : PlayerPermissionLevel.MEMBER);
        packet.getSettings().setServerChunkTickRange(4);
        packet.getSettings().setHasLockedBehaviorPack(false);
        packet.getSettings().setHasLockedResourcePack(false);
        packet.getSettings().setFromLockedWorldTemplate(false);
        packet.getSettings().setUseMsaGamertagsOnly(true);
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
                        40,
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
            itemDefinitions.add(
                    new SimpleItemDefinition(
                            data.identifier(),
                            data.runtimeId(),
                            ItemVersion.from(data.version()),
                            data.componentBased(),
                            tag.build()
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
}