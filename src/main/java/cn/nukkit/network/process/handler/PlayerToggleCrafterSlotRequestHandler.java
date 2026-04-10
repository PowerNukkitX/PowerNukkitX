package cn.nukkit.network.process.handler;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockCrafter;
import cn.nukkit.blockentity.BlockEntityCrafter;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
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