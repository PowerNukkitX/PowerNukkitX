package cn.nukkit.network.process.handler;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.custom.CustomEntity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.item.EntityXpOrb;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.event.player.PlayerHackDetectedEvent;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.event.player.PlayerMouseOverEntityEvent;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.packet.InteractPacket;

/**
 * @author Kaooot
 */
@Slf4j
public class InteractHandler implements PacketHandler<InteractPacket> {

    @Override
    public void handle(InteractPacket packet, PlayerSessionHolder holder, Server server) {
        final Player player = holder.getPlayer();
        if (!player.spawned || !player.isAlive()) {
            return;
        }

        Entity targetEntity = player.level.getEntity(packet.getTargetRuntimeID());

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

        switch (packet.getAction()) {
            case InteractPacket.Action.INTERACT_UPDATE -> {
                if (packet.getTargetRuntimeID() == 0) {
                    return;
                }
                player.getServer().getPluginManager().callEvent(new PlayerMouseOverEntityEvent(player, targetEntity));
            }
            case InteractPacket.Action.STOP_RIDING -> {
                if (!targetEntity.isRideable() || player.riding == null) {
                    return;
                }
                player.riding.dismountEntity(player);
            }
            case InteractPacket.Action.OPEN_INVENTORY -> {
                if (targetEntity.isRideable()) {
                    if (targetEntity.openInventory(player)) return;

                    // If it was denied because of owner restriction: do nothing
                    if (targetEntity.hasInventory() && targetEntity.isTamed()) {
                        boolean restricted = targetEntity.getComponentInventory().isRestrictedToOwner();
                        if (restricted && !player.getName().equals(targetEntity.getOwnerName())) return;
                    }
                } else if (targetEntity.getId() != player.getId()) {
                    return;
                }
                if (!player.isInventoryOpen()) {
                    if(player.getTopWindow().isPresent()) return;
                    player.setInventoryOpen(player.getInventory().open(player));
                } else {
                    player.forceClientCloseInventory();
                    player.setInventoryOpen(false);
                    log.warn("{} tried to open the inventory while still being open!", player.getName());
                }
            }
        }
    }
}