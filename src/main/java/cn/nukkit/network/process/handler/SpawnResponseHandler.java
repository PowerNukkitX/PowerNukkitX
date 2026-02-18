package cn.nukkit.network.process.handler;

import cn.nukkit.Player;
import cn.nukkit.entity.data.property.EntityProperty;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.BedrockSession;
import cn.nukkit.inventory.request.CraftRecipeActionProcessor;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.data.AuthoritativeMovementMode;
import org.cloudburstmc.protocol.bedrock.data.ChatRestrictionLevel;
import org.cloudburstmc.protocol.bedrock.data.GamePublishSetting;
import org.cloudburstmc.protocol.bedrock.data.GameType;
import org.cloudburstmc.protocol.bedrock.data.SpawnBiomeType;
import org.cloudburstmc.protocol.bedrock.data.WorldType;
import org.cloudburstmc.protocol.bedrock.data.definitions.SimpleItemDefinition;
import org.cloudburstmc.protocol.bedrock.packet.AvailableEntityIdentifiersPacket;
import org.cloudburstmc.protocol.bedrock.packet.ItemComponentPacket;
import org.cloudburstmc.protocol.bedrock.packet.StartGamePacket;
import org.cloudburstmc.protocol.bedrock.packet.SyncEntityPropertyPacket;
import org.cloudburstmc.protocol.bedrock.packet.TrimDataPacket;
import org.cloudburstmc.protocol.bedrock.packet.VoxelShapesPacket;
import cn.nukkit.registry.ItemRegistry;
import cn.nukkit.registry.ItemRuntimeIdRegistry;
import cn.nukkit.registry.Registries;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.packet.RequestChunkRadiusPacket;
import org.cloudburstmc.protocol.bedrock.packet.SetLocalPlayerAsInitializedPacket;
import org.cloudburstmc.protocol.common.PacketSignal;
import org.cloudburstmc.protocol.common.util.OptionalBoolean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

@Slf4j
public class SpawnResponseHandler extends BedrockSessionPacketHandler {
    public SpawnResponseHandler(BedrockSession session) {
        super(session);
        var server = player.getServer();

        log.debug("Sending voxel shapes");
        VoxelShapesPacket voxelShapesPacket = new VoxelShapesPacket();
        voxelShapesPacket.setShapes(new ArrayList<>());
        voxelShapesPacket.setNameMap(new HashMap<>());
        player.dataPacketImmediately(voxelShapesPacket);

        this.startGame();

        log.debug("Sending component items");

        ItemComponentPacket itemRegistryPacket = new ItemComponentPacket();

        for(ItemRuntimeIdRegistry.ItemData data : ItemRuntimeIdRegistry.getITEMDATA()) {
            CompoundTag tag = new CompoundTag();

            if (ItemRegistry.getItemComponents().containsCompound(data.identifier())) {
                CompoundTag item_tag = ItemRegistry.getItemComponents().getCompound(data.identifier());
                tag.putCompound("components", item_tag.getCompound("components"));
            }
            else if (Registries.ITEM.getCustomItemDefinition().containsKey(data.identifier())) {
                tag = Registries.ITEM.getCustomItemDefinition().get(data.identifier()).nbt();
            }

            itemRegistryPacket.getItems().add(new SimpleItemDefinition(data.identifier(), data.runtimeId(), data.componentBased()));
        }

        player.dataPacket(itemRegistryPacket);

        log.debug("Sending actor identifiers");
        player.dataPacket(new AvailableEntityIdentifiersPacket());

        // 注册实体属性
        // Register entity properties
        log.debug("Sending actor properties");
        for (SyncEntityPropertyPacket pk : EntityProperty.getEntityPropertyCache()) {
            player.dataPacket(pk);
        }

        log.debug("Sending biome definitions");
        player.dataPacket(Registries.BIOME.getBiomeDefinitionListPacket());

        log.debug("Sending attributes");
        player.syncAttributes();

        log.debug("Sending available commands");
        this.session.syncAvailableCommands();

        // 发送玩家权限列表
        // Send player permission list
        log.debug("Sending abilities");
        var col = Collections.singleton(player);
        server.getOnlinePlayers().values().forEach(p -> {
            if (p != player) {
                p.getAdventureSettings().sendAbilities(col);
                p.getAdventureSettings().updateAdventureSettings();
            }
        });

        log.debug("Sending effects");
        player.sendPotionEffects(player);

        log.debug("Sending actor metadata");
        player.sendData(player);

        log.debug("Sending inventory");
        this.session.syncInventory();

        log.debug("Sending creative content");
        this.session.syncCreativeContent();

        TrimDataPacket trimDataPacket = new TrimDataPacket();
        trimDataPacket.getMaterials().addAll(CraftRecipeActionProcessor.getTrimMaterials());
        trimDataPacket.getPatterns().addAll(CraftRecipeActionProcessor.getTrimPatterns());
        this.session.sendPacket(trimDataPacket);

        player.setCanClimb(true);
        player.sendMovementSpeed(player.getMovementSpeed());
        log.debug("Sending player list");

        server.addOnlinePlayer(player);
        server.onPlayerCompleteLoginSequence(player);

        if (player.isOp() || player.hasPermission("nukkit.textcolor")) {
            player.setRemoveFormat(false);
        }
    }

    private void startGame() {
        var server = player.getServer();
        var startPk = new StartGamePacket();
        var spawn = player.getSafeSpawn();

        startPk.setUniqueEntityId(player.getId());
        startPk.setRuntimeEntityId(player.getId());
        startPk.setPlayerGameType(GameType.from(Player.toNetworkGamemode(player.getGamemode())));
        startPk.setPlayerPosition(Vector3f.from((float) player.x, (float) (player.isOnGround() ? player.y + player.getEyeHeight() : player.y), (float) player.z));
        startPk.setRotation(Vector2f.from((float) player.pitch, (float) player.yaw));
        startPk.setSeed(-1L);
        startPk.setDimensionId(player.level.getDimension());
        startPk.setGeneratorId((player.getLevel().getDimension() + 1) & 0xff);
        startPk.setLevelGameType(GameType.from(Player.toNetworkGamemode(server.getDefaultGamemode())));
        startPk.setDifficulty(server.getDifficulty());
        startPk.setDefaultSpawn(Vector3i.from(spawn.getFloorX(), spawn.getFloorY(), spawn.getFloorZ()));
        startPk.setSpawnBiomeType(SpawnBiomeType.DEFAULT);
        startPk.setCustomBiomeName("plains");
        startPk.setAchievementsDisabled(true);
        startPk.setDayCycleStopTime(-1);
        startPk.setRainLevel(0);
        startPk.setLightningLevel(0);
        startPk.setCommandsEnabled(player.isEnableClientCommand());
        startPk.setLevelId("");
        startPk.setLevelName(server.getSubMotd());
        startPk.setAuthoritativeMovementMode(AuthoritativeMovementMode.SERVER_WITH_REWIND);
        startPk.setInventoriesServerAuthoritative(true);
        startPk.setBlockNetworkIdsHashed(true);
        startPk.setPlayerPropertyData(NbtMap.EMPTY);
        startPk.setExperimentsPreviouslyToggled(false);
        startPk.setForceExperimentalGameplay(OptionalBoolean.empty());
        startPk.setMultiplayerGame(true);
        startPk.setBroadcastingToLan(true);
        startPk.setXblBroadcastMode(GamePublishSetting.PUBLIC);
        startPk.setPlatformBroadcastMode(GamePublishSetting.PUBLIC);
        startPk.setServerId("");
        startPk.setWorldId("");
        startPk.setScenarioId("");
        startPk.setOwnerId("");
        startPk.setPremiumWorldTemplateId("00000000-0000-0000-0000-000000000000");
        startPk.setWorldTemplateId(new UUID(0, 0));
        startPk.setEditorWorldType(WorldType.NON_EDITOR);
        startPk.setChatRestrictionLevel(ChatRestrictionLevel.NONE);
        startPk.setLevelName(server.getSubMotd());
        startPk.setServerAuthoritativeBlockBreaking(true);
        startPk.setServerEngine("PowerNukkitX");
        startPk.getExperiments().addAll(server.getExperiments());
        player.dataPacketImmediately(startPk);
    }

    @Override
    public PacketSignal handle(RequestChunkRadiusPacket pk) {
        player.setViewDistance(Math.max(2, player.getViewDistance()));
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetLocalPlayerAsInitializedPacket pk) {
        log.debug("receive SetLocalPlayerAsInitializedPacket for {}", this.player.getName());
        handle.onPlayerLocallyInitialized();
        return PacketSignal.HANDLED;
    }
}
