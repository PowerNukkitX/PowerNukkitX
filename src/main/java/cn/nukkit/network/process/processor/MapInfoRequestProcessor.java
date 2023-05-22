package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityItemFrame;
import cn.nukkit.event.player.PlayerMapInfoRequestEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemMap;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.MapInfoRequestPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.plugin.InternalPlugin;
import cn.nukkit.plugin.PowerNukkitPlugin;
import cn.nukkit.scheduler.AsyncTask;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;

@Log4j2
public class MapInfoRequestProcessor extends DataPacketProcessor<MapInfoRequestPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull MapInfoRequestPacket pk) {
        Player player = playerHandle.player;
        Item mapItem = null;
        int index = 0;

        for (var entry : player.getInventory().getContents().entrySet()) {
            var item1 = entry.getValue();
            if (item1 instanceof ItemMap && ((ItemMap) item1).getMapId() == pk.mapId) {
                mapItem = item1;
                index = entry.getKey();
            }
        }

        if (mapItem == null) {
            for (Item item1 : player.getInventory().getContents().values()) {
                if (item1 instanceof ItemMap && ((ItemMap) item1).getMapId() == pk.mapId) {
                    mapItem = item1;
                }
            }
        }

        if (mapItem == null) {
            for (BlockEntity be : player.level.getBlockEntities().values()) {
                if (be instanceof BlockEntityItemFrame itemFrame1) {

                    if (itemFrame1.getItem() instanceof ItemMap && ((ItemMap) itemFrame1.getItem()).getMapId() == pk.mapId) {
                        ((ItemMap) itemFrame1.getItem()).sendImage(player);
                        break;
                    }
                }
            }
        }

        if (mapItem != null) {
            PlayerMapInfoRequestEvent event;
            player.getServer().getPluginManager().callEvent(event = new PlayerMapInfoRequestEvent(player, mapItem));

            if (!event.isCancelled()) {
                ItemMap map = (ItemMap) mapItem;
                if (map.trySendImage(player)) {
                    return;
                }

                int finalIndex = index;
                //TODO: 并行计算
                Server.getInstance().getScheduler().scheduleAsyncTask(InternalPlugin.INSTANCE, new AsyncTask() {
                    @Override
                    public void onRun() {
                        map.renderMap(player.getLevel(), (player.getFloorX() / 128) << 7, (player.getFloorZ() / 128) << 7, 10);
                        player.getInventory().setItem(finalIndex, map);
                        map.sendImage(player);
                    }
                });
            }
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.toNewProtocolID(ProtocolInfo.MAP_INFO_REQUEST_PACKET);
    }
}
