package org.powernukkitx.network.process.handler;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockCrafter;
import org.powernukkitx.blockentity.BlockEntityCrafter;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.packet.PlayerToggleCrafterSlotRequestPacket;

/**
 * @author Kaooot
 */
public class PlayerToggleCrafterSlotRequestHandler implements PacketHandler<PlayerToggleCrafterSlotRequestPacket> {

    @Override
    public void handle(PlayerToggleCrafterSlotRequestPacket packet, PlayerSessionHolder holder, Server server) {
        Player player = holder.getPlayer();
        if (!player.spawned || !player.isAlive()) {
            return;
        }

        BlockVector3 position = BlockVector3.fromNetwork(packet.getPos());
        player.temporalVector.setComponents(position.x, position.y, position.z);
        if (!player.canInteract(player.temporalVector.add(0.5, 0.5, 0.5), player.isCreative() ? 13 : 7)) {
            return;
        }

        Block block = player.getLevel().getBlock(position.x, position.y, position.z, false);
        if (!(block instanceof BlockCrafter crafter)) {
            return;
        }

        BlockEntityCrafter blockEntity = crafter.getOrCreateBlockEntity();
        int slot = packet.getSlotIndex();
        if (slot < 0 || slot >= blockEntity.getInventory().getSize()) {
            return;
        }

        blockEntity.getInventory().setSlotState(slot, !packet.isDisabled());
    }
}
