package cn.nukkit.network.process.handler;

import cn.nukkit.Player;
import cn.nukkit.entity.data.property.EntityProperty;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.network.process.NetworkSession;
import cn.nukkit.network.process.NetworkSessionState;
import cn.nukkit.network.protocol.AvailableEntityIdentifiersPacket;
import cn.nukkit.network.protocol.BiomeDefinitionListPacket;
import cn.nukkit.network.protocol.ItemComponentPacket;
import cn.nukkit.network.protocol.RequestChunkRadiusPacket;
import cn.nukkit.network.protocol.SetLocalPlayerAsInitializedPacket;
import cn.nukkit.network.protocol.StartGamePacket;
import cn.nukkit.network.protocol.SyncEntityPropertyPacket;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.TextFormat;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

@Slf4j
public class PrespawnHandler extends NetworkSessionPacketHandler {
    public PrespawnHandler(NetworkSession session) {
        super(session);
        var server = player.getServer();

        this.startGame();

        //注册实体属性
        for (SyncEntityPropertyPacket pk : EntityProperty.getPacketCache()) {
            player.dataPacket(pk);
        }

        //写入自定义物品数据
        ItemComponentPacket itemComponentPacket = new ItemComponentPacket();
        if (!Registries.ITEM.getCustomItemDefinition().isEmpty()) {
            Int2ObjectOpenHashMap<ItemComponentPacket.Entry> entries = new Int2ObjectOpenHashMap<>();
            int i = 0;
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

        player.dataPacket(new BiomeDefinitionListPacket());
        player.dataPacket(new AvailableEntityIdentifiersPacket());
        player.sendCreativeContents();

        //发送玩家权限列表
        var col = Collections.singleton(player);
        server.getOnlinePlayers().values().forEach(p -> {
            if (p != player) {
                p.getAdventureSettings().sendAbilities(col);
            }
        });

        player.sendPotionEffects(player);
        player.sendData(player);
        player.syncAttributes();
        player.setNameTagVisible(true);
        player.setNameTagAlwaysVisible(true);
        player.setCanClimb(true);

        log.info(server.getLanguage().tr("nukkit.player.logIn",
                TextFormat.AQUA + player.getName() + TextFormat.WHITE,
                player.getAddress(),
                String.valueOf(player.getPort()),
                String.valueOf(player.getId()),
                player.level.getName(),
                String.valueOf(NukkitMath.round(player.x, 4)),
                String.valueOf(NukkitMath.round(player.y, 4)),
                String.valueOf(NukkitMath.round(player.z, 4))));

        if (player.isOp() || player.hasPermission("nukkit.textcolor")) {
            player.setRemoveFormat(false);
        }

        server.addOnlinePlayer(player);
        server.onPlayerCompleteLoginSequence(player);
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
        startPk.worldName = server.getNetwork().getName();
        startPk.generator = (byte) ((player.getLevel().getDimension() + 1) & 0xff); //0 旧世界, 1 主世界, 2 下界, 3末地
        startPk.serverAuthoritativeMovement = server.getServerAuthoritativeMovement();
        startPk.isInventoryServerAuthoritative = true;//enable item stack request packet
        startPk.blockNetworkIdsHashed = true;//enable blockhash
        //写入自定义方块数据
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
        this.session.getMachine().fire(NetworkSessionState.IN_GAME);
    }
}
