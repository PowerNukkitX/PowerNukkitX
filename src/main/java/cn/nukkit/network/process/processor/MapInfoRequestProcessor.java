package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityItemFrame;
import cn.nukkit.event.player.PlayerMapInfoRequestEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemFilledMap;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.MapInfoRequestPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.plugin.InternalPlugin;
import cn.nukkit.scheduler.AsyncTask;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class MapInfoRequestProcessor extends DataPacketProcessor<MapInfoRequestPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull MapInfoRequestPacket pk) {
        Player player = playerHandle.player;
        Item mapItem = null;
        int index = 0;
        var offhand = false;

        for (var entry : player.getOffhandInventory().getContents().entrySet()) {
            var item1 = entry.getValue();
            if (checkMapItemValid(item1, pk)) {
                mapItem = item1;
                index = entry.getKey();                    
                offhand = true;
            }
        }

        if (mapItem == null) {
            for (var entry : player.getInventory().getContents().entrySet()) {
                var item1 = entry.getValue();
                if (checkMapItemValid(item1, pk)) {
                    mapItem = item1;
                    index = entry.getKey();
                }
            }
        }

        if (mapItem == null) {
            for (BlockEntity be : player.level.getBlockEntities().values()) {
                if (be instanceof BlockEntityItemFrame itemFrame && checkMapItemValid(itemFrame.getItem(), pk)) {
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
                //TODO: 并行计算
                Server.getInstance().getScheduler().scheduleAsyncTask(InternalPlugin.INSTANCE, new AsyncTask() {
                    @Override
                    public void onRun() {
                        map.renderMap(player.getLevel(), (player.getFloorX() / 128) << 7, (player.getFloorZ() / 128) << 7, 1);
                        if (finalOffhand) {
                            if (checkMapItemValid(player.getOffhandInventory().getUnclonedItem(finalIndex), pk))
                                player.getOffhandInventory().setItem(finalIndex, map);
                        } else {
                            if (checkMapItemValid(player.getInventory().getUnclonedItem(finalIndex), pk))
                                player.getInventory().setItem(finalIndex, map);
                        }
                        map.sendImage(player);
                    }
                });
            }
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.MAP_INFO_REQUEST_PACKET;
    }

    protected boolean checkMapItemValid(Item item, MapInfoRequestPacket pk) {
        return item instanceof ItemFilledMap itemMap && itemMap.getMapId() == pk.mapId;
    }
}
