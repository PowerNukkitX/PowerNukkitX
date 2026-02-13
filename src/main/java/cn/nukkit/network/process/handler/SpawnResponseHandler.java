package cn.nukkit.network.process.handler;

import cn.nukkit.Player;
import cn.nukkit.entity.data.property.EntityProperty;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.BedrockSession;
import cn.nukkit.network.protocol.AvailableEntityIdentifiersPacket;
import cn.nukkit.network.protocol.ItemRegistryPacket;
import cn.nukkit.network.protocol.RequestChunkRadiusPacket;
import cn.nukkit.network.protocol.SetLocalPlayerAsInitializedPacket;
import cn.nukkit.network.protocol.StartGamePacket;
import cn.nukkit.network.protocol.SyncEntityPropertyPacket;
import cn.nukkit.network.protocol.TrimDataPacket;
import cn.nukkit.network.protocol.VoxelShapesPacket;
import cn.nukkit.network.protocol.types.TrimData;
import cn.nukkit.registry.ItemRegistry;
import cn.nukkit.registry.ItemRuntimeIdRegistry;
import cn.nukkit.registry.Registries;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

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

        ItemRegistryPacket itemRegistryPacket = new ItemRegistryPacket();
        var entries = new ObjectOpenHashSet<ItemRegistryPacket.Entry>();

        for(ItemRuntimeIdRegistry.ItemData data : ItemRuntimeIdRegistry.getITEMDATA()) {
            CompoundTag tag = new CompoundTag();

            if (ItemRegistry.getItemComponents().containsCompound(data.identifier())) {
                CompoundTag item_tag = ItemRegistry.getItemComponents().getCompound(data.identifier());
                tag.putCompound("components", item_tag.getCompound("components"));
            }
            else if (Registries.ITEM.getCustomItemDefinition().containsKey(data.identifier())) {
                tag = Registries.ITEM.getCustomItemDefinition().get(data.identifier()).nbt();
            }

            entries.add(new ItemRegistryPacket.Entry(data.identifier(), data.runtimeId(), data.version(), data.componentBased(), tag));
        }

        itemRegistryPacket.setEntries(entries.toArray(ItemRegistryPacket.Entry.EMPTY_ARRAY));
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
        trimDataPacket.materials.addAll(TrimData.trimMaterials);
        trimDataPacket.patterns.addAll(TrimData.trimPatterns);
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

        startPk.entityUniqueId = player.getId();
        startPk.entityRuntimeId = player.getId();
        startPk.playerGamemode = Player.toNetworkGamemode(player.getGamemode());

        startPk.x = (float) player.x;
        startPk.y = (float) (player.isOnGround() ? player.y + player.getEyeHeight() : player.y);//防止在地上生成容易陷进地里
        startPk.z = (float) player.z;
        startPk.yaw = (float) player.yaw;
        startPk.pitch = (float) player.pitch;
        startPk.seed = -1L;
        startPk.dimension = (byte) (player.level.getDimension() & 0xff);
        startPk.worldGamemode = Player.toNetworkGamemode(server.getDefaultGamemode());
        startPk.difficulty = server.getDifficulty();
        var spawn = player.getSafeSpawn();
        startPk.spawnX = spawn.getFloorX();
        startPk.spawnY = spawn.getFloorY();
        startPk.spawnZ = spawn.getFloorZ();
        startPk.hasAchievementsDisabled = true;
        startPk.dayCycleStopTime = -1;
        startPk.rainLevel = 0;
        startPk.lightningLevel = 0;
        startPk.commandsEnabled = player.isEnableClientCommand();
        startPk.gameRules = player.getLevel().getGameRules();
        startPk.levelId = "";
        startPk.worldName = server.getSubMotd();
        startPk.generator = (byte) ((player.getLevel().getDimension() + 1) & 0xff); //0 旧世界 Old world, 1 主世界 Main world, 2 下界 Nether, 3 末地 End
        startPk.serverAuthoritativeMovement = server.getServerAuthoritativeMovement();
        startPk.isInventoryServerAuthoritative = true;//enable item stack request packet
        startPk.blockNetworkIdsHashed = true;//enable blockhash
        // 写入自定义方块数据
        // Write custom block data
        startPk.blockProperties.addAll(Registries.BLOCK.getCustomBlockDefinitionList());
        startPk.playerPropertyData = EntityProperty.getPlayerPropertyCache();
        startPk.setExperiments(server.getExperiments());
        player.dataPacketImmediately(startPk);
    }

    @Override
    public void handle(RequestChunkRadiusPacket pk) {
        player.setViewDistance(Math.max(2, player.getViewDistance()));
    }

    @Override
    public void handle(SetLocalPlayerAsInitializedPacket pk) {
        log.debug("receive SetLocalPlayerAsInitializedPacket for {}", this.player.getPlayerInfo().getUsername());
        handle.onPlayerLocallyInitialized();
    }
}
