package cn.nukkit.network.process.handler;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityItemFrame;
import cn.nukkit.event.player.PlayerMapInfoRequestEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemFilledMap;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import cn.nukkit.plugin.InternalPlugin;
import cn.nukkit.scheduler.AsyncTask;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.packet.MapInfoRequestPacket;

/**
 * @author Kaooot
 */
@Slf4j
public class MapInfoRequestHandler implements PacketHandler<MapInfoRequestPacket> {

    @Override
    public void handle(MapInfoRequestPacket packet, PlayerSessionHolder holder, Server server) {
        Player player = holder.getPlayer();


        if (packet.getMapUniqueID() <= 0) {
            log.debug("Player {} sent an invalid map id {}", player.getName(), packet.getMapUniqueID());
            return;
        }

        if (!player.isAlive() || player.level == null) {
            log.debug("Player {} tried to request map info while dead or without a level loaded", player.getName());
            return;
        }

        Item mapItem = null;
        int index = 0;
        var offhand = false;

        for (var entry : player.getOffhandInventory().getContents().entrySet()) {
            var item1 = entry.getValue();
            if (checkMapItemValid(item1, packet)) {
                mapItem = item1;
                index = entry.getKey();
                offhand = true;
            }
        }

        if (mapItem == null) {
            for (var entry : player.getInventory().getContents().entrySet()) {
                var item1 = entry.getValue();
                if (checkMapItemValid(item1, packet)) {
                    mapItem = item1;
                    index = entry.getKey();
                }
            }
        }

        if (mapItem == null) {
            for (BlockEntity be : player.level.getBlockEntities().values()) {
                if (be instanceof BlockEntityItemFrame itemFrame && checkMapItemValid(itemFrame.getItem(), packet)) {
                    ((ItemFilledMap) itemFrame.getItem()).sendImage(player);
                    break;
                }
            }
        }

        if (mapItem != null) {
            PlayerMapInfoRequestEvent event;
            player.getServer().getPluginManager().callEvent(event = new PlayerMapInfoRequestEvent(player, mapItem));

            if (!event.isCancelled()) {
                ItemFilledMap map = (ItemFilledMap) mapItem;
                if (map.trySendImage(player)) {
                    return;
                }

                final int finalIndex = index;
                final boolean finalOffhand = offhand;
                player.getLevel().getScheduler().scheduleAsyncTask(InternalPlugin.INSTANCE, new AsyncTask() {
                    @Override
                    public void onRun() {
                        int zoom = Math.max(1, map.getMapScale());
                        int mapSize = 128 * zoom;
                        int halfMapSize = mapSize >> 1;
                        int startX = Math.floorDiv(player.getFloorX() + halfMapSize, mapSize) * mapSize - halfMapSize;
                        int startZ = Math.floorDiv(player.getFloorZ() + halfMapSize, mapSize) * mapSize - halfMapSize;
                        map.renderMap(player.getLevel(), startX, startZ, zoom);
                        if (finalOffhand) {
                            if (checkMapItemValid(player.getOffhandInventory().getUnclonedItem(finalIndex), packet))
                                player.getOffhandInventory().setItem(finalIndex, map);
                        } else {
                            if (checkMapItemValid(player.getInventory().getUnclonedItem(finalIndex), packet))
                                player.getInventory().setItem(finalIndex, map);
                        }
                        map.sendImage(player);
                    }
                });
            }
        }
    }

    protected boolean checkMapItemValid(Item item, MapInfoRequestPacket pk) {
        return item instanceof ItemFilledMap itemMap && itemMap.getMapId() == pk.getMapUniqueID();
    }
}