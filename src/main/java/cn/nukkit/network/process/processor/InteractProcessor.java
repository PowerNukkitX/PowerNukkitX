package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityRideable;
import cn.nukkit.entity.custom.CustomEntity;
import cn.nukkit.entity.item.*;
import cn.nukkit.entity.passive.EntityHorse;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.event.player.PlayerMouseOverEntityEvent;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.InteractPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@Log4j2
public class InteractProcessor extends DataPacketProcessor<InteractPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull InteractPacket pk) {
        Player player = playerHandle.player;
        if (!player.spawned || !player.isAlive()) {
            return;
        }

        if (pk.action != InteractPacket.ACTION_MOUSEOVER || pk.target != 0) {
            player.craftingType = Player.CRAFTING_SMALL;
            //player.resetCraftingGridType();
        }


        Entity targetEntity = player.level.getEntity(pk.target);

        if (targetEntity == null || !player.isAlive() || !targetEntity.isAlive()) {
            return;
        }

        if (targetEntity instanceof EntityItem || targetEntity instanceof EntityArrow || targetEntity instanceof EntityXPOrb) {
            // 自定义实体在客户端中可以互动, 所以不踢出玩家
            if (targetEntity instanceof CustomEntity) {
                return;
            }
            player.kick(PlayerKickEvent.Reason.INVALID_PVE, "Attempting to interact with an invalid entity");
            log.warn(player.getServer().getLanguage().tr("nukkit.player.invalidEntity", player.getName()));
            return;
        }

        switch (pk.action) {
            case InteractPacket.ACTION_MOUSEOVER -> {
                if (pk.target == 0) {
                    return;
                }
                player.getServer().getPluginManager().callEvent(new PlayerMouseOverEntityEvent(player, targetEntity));
            }
            case InteractPacket.ACTION_VEHICLE_EXIT -> {
                if (!(targetEntity instanceof EntityRideable) || player.riding == null) {
                    return;
                }
                ((EntityRideable) player.riding).dismountEntity(player);
            }
            case InteractPacket.ACTION_OPEN_INVENTORY -> {
                if (targetEntity instanceof EntityRideable) {
                    if (targetEntity instanceof EntityChestBoat chestBoat) {
                        player.addWindow(chestBoat.getInventory());
                        return;
                    } else if (targetEntity instanceof EntityHorse horse) {
                        player.addWindow(horse.getInventory());
                        return;
                    }
                    if (!(targetEntity instanceof EntityBoat || targetEntity instanceof EntityMinecartEmpty)) {
                        return;
                    }
                } else if (targetEntity.getId() != player.getId()) {
                    return;
                }
                if (!playerHandle.getInventoryOpen()) {
                    player.getInventory().open(player);
                    playerHandle.setInventoryOpen(true);
                }
            }
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.toNewProtocolID(ProtocolInfo.INTERACT_PACKET);
    }
}
