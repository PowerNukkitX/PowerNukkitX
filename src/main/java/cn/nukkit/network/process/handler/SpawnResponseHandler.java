package cn.nukkit.network.process.handler;

import cn.nukkit.Player;
import cn.nukkit.entity.data.property.EntityProperty;
import cn.nukkit.network.connection.BedrockSession;
import cn.nukkit.network.protocol.AvailableEntityIdentifiersPacket;
import cn.nukkit.network.protocol.BiomeDefinitionListPacket;
import cn.nukkit.network.protocol.ItemComponentPacket;
import cn.nukkit.network.protocol.RequestChunkRadiusPacket;
import cn.nukkit.network.protocol.SetLocalPlayerAsInitializedPacket;
import cn.nukkit.network.protocol.StartGamePacket;
import cn.nukkit.network.protocol.SyncEntityPropertyPacket;
import cn.nukkit.network.protocol.TrimDataPacket;
import cn.nukkit.network.protocol.types.TrimData;
import cn.nukkit.registry.Registries;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

@Slf4j
public class SpawnResponseHandler extends BedrockSessionPacketHandler {
    public SpawnResponseHandler(BedrockSession session) {
        super(session);
        var server = player.getServer();

        this.startGame();

        // 写入自定义物品数据
        // Write custom item data
        log.debug("Sending component items");
        var itemComponentPacket = new ItemComponentPacket();
        if (!Registries.ITEM.getCustomItemDefinition().isEmpty()) {
            var entries = new Int2ObjectOpenHashMap<ItemComponentPacket.Entry>();
            var i = 0;
            for (var entry : Registries.ITEM.getCustomItemDefinition().entrySet()) {
                try {
                    entries.put(i, new ItemComponentPacket.Entry(entry.getKey(), entry.getValue().nbt()));
                    i++;
                } catch (Exception e) {
                    log.error("ItemComponentPacket encoding error", e);
                }
            }
            itemComponentPacket.setEntries(entries.values().toArray(ItemComponentPacket.Entry.EMPTY_ARRAY));
        }
        player.dataPacket(itemComponentPacket);

        log.debug("Sending actor identifiers");
        player.dataPacket(new AvailableEntityIdentifiersPacket());

        // 注册实体属性
        // Register entity attributes
        log.debug("Sending actor properties");
        for (SyncEntityPropertyPacket pk : EntityProperty.getPacketCache()) {
            player.dataPacket(pk);
        }

        log.debug("Sending biome definitions");
        player.dataPacket(new BiomeDefinitionListPacket());

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

        log.debug("Sending crafting data");
        this.session.syncCraftingData();

        TrimDataPacket trimDataPacket = new TrimDataPacket();
        trimDataPacket.materials.addAll(TrimData.trimMaterials);
        trimDataPacket.patterns.addAll(TrimData.trimPatterns);
        this.session.sendPacket(trimDataPacket);

        player.setNameTagVisible(true);
        player.setNameTagAlwaysVisible(true);
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
        startPk.worldName = player.getLevelName();
        startPk.generator = (byte) ((player.getLevel().getDimension() + 1) & 0xff); //0 旧世界 Old world, 1 主世界 Main world, 2 下界 Nether, 3 末地 End
        startPk.serverAuthoritativeMovement = server.getServerAuthoritativeMovement();
        startPk.isInventoryServerAuthoritative = true;//enable item stack request packet
        startPk.blockNetworkIdsHashed = true;//enable blockhash
        // 写入自定义方块数据
        // Write custom block data
        startPk.blockProperties.addAll(Registries.BLOCK.getCustomBlockDefinitionList());
        startPk.playerPropertyData = EntityProperty.getPlayerPropertyCache();
        player.dataPacketImmediately(startPk);
    }

    @Override
    public void handle(RequestChunkRadiusPacket pk) {
        player.setViewDistance(Math.max(2, Math.min(pk.radius, player.getViewDistance())));
    }

    @Override
    public void handle(SetLocalPlayerAsInitializedPacket pk) {
        log.debug("receive SetLocalPlayerAsInitializedPacket for {}", this.player.getPlayerInfo().getUsername());
        handle.onPlayerLocallyInitialized();
    }
}
