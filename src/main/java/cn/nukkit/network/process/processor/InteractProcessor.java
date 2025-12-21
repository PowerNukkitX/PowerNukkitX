package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.custom.CustomEntity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.item.EntityXpOrb;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.event.player.PlayerHackDetectedEvent;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.event.player.PlayerMouseOverEntityEvent;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.InteractPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class InteractProcessor extends DataPacketProcessor<InteractPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull InteractPacket pk) {
        Player player = playerHandle.player;
        if (!player.spawned || !player.isAlive()) {
            return;
        }

        Entity targetEntity = player.level.getEntity(pk.target);

        if (targetEntity == null || !player.isAlive() || !targetEntity.isAlive()) {
            return;
        }

        if (targetEntity instanceof EntityItem || targetEntity instanceof EntityArrow || targetEntity instanceof EntityXpOrb) {
            // Custom entities can interact in the client, so they don't kick players out.
            if (targetEntity instanceof CustomEntity) {
                return;
            }

            PlayerHackDetectedEvent event = new PlayerHackDetectedEvent(player, PlayerHackDetectedEvent.HackType.INVALID_PVE);
            player.getServer().getPluginManager().callEvent(event);

            if(event.isKick())
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
                if (!targetEntity.isRideable() || player.riding == null) {
                    return;
                }
                player.riding.dismountEntity(player);
            }
            case InteractPacket.ACTION_OPEN_INVENTORY -> {
                if (targetEntity.isRideable()) {
                    if(targetEntity.openInventory(player)) return;
                } else if (targetEntity.getId() != player.getId()) {
                    return;
                }
                if (!playerHandle.getInventoryOpen()) {
                    if(player.getTopWindow().isPresent()) return;
                    playerHandle.setInventoryOpen(player.getInventory().open(player));
                } else {
                    player.forceClientCloseInventory();
                    playerHandle.setInventoryOpen(false);
                    log.warn("{} tried to open the inventory while still being open!", player.getName());
                }
            }
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.INTERACT_PACKET;
    }
}
