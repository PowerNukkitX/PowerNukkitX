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
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MapInfoRequestProcessor extends DataPacketProcessor<MapInfoRequestPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull MapInfoRequestPacket pk) {
        Player player = playerHandle.player;
        Item mapItem = null;

        for (Item item1 : player.getOffhandInventory().getContents().values()) {
            if (item1 instanceof ItemMap && ((ItemMap) item1).getMapId() == pk.mapId) {
                mapItem = item1;
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
                Server.getInstance().getScheduler().scheduleAsyncTask(InternalPlugin.INSTANCE, new AsyncTask() {
                    @Override
                    public void onRun() {
                        try {
                            BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
                            Graphics2D graphics = image.createGraphics();

                            int worldX = (player.getFloorX() / 128) << 7;
                            int worldZ = (player.getFloorZ() / 128) << 7;
                            for (int x = 0; x < 128; x++) {
                                for (int z = 0; z < 128; z++) {
                                    graphics.setColor(player.getLevel().getMapColorAt(worldX + x, worldZ + z));
                                    graphics.fillRect(x, z, x + 1, z + 1);
                                }
                            }

                            map.setImage(image);
                            map.sendImage(player);
                        } catch (Exception ex) {
                            player.getServer().getLogger().warning("There was an error while generating map image", ex);
                        }
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
