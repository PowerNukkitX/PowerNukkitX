package org.powernukkitx.network.process.handler;

import org.powernukkitx.Server;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockCrafter;
import org.powernukkitx.blockentity.BlockEntityCrafter;
import org.powernukkitx.level.Level;
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
        Level level = holder.getPlayer().getLevel();
        BlockVector3 position = BlockVector3.fromNetwork(packet.getPos());
        Block block = level.getBlock(position.asVector3());
        if (!(block instanceof BlockCrafter crafter)) {
            return;
        }

        BlockEntityCrafter blockEntity = crafter.getOrCreateBlockEntity();
        int slot = packet.getSlotIndex();
        boolean state = !packet.isDisabled();

        blockEntity.getInventory().setSlotState(slot, state);
    }
}