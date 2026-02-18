package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockCrafter;
import cn.nukkit.blockentity.BlockEntityCrafter;
import cn.nukkit.level.Level;
import cn.nukkit.network.process.DataPacketProcessor;
import org.cloudburstmc.protocol.bedrock.packet.ToggleCrafterSlotRequestPacket;
import org.jetbrains.annotations.NotNull;

public class ToggleCrafterSlotRequestPacketProcessor extends DataPacketProcessor<ToggleCrafterSlotRequestPacket> {

    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull ToggleCrafterSlotRequestPacket pk) {

        Level level = playerHandle.player.getLevel();
        var position = pk.getBlockPosition();
        Block block = level.getBlock(position.getX(), position.getY(), position.getZ());
        if (!(block instanceof BlockCrafter crafter)) {
            return;
        }

        BlockEntityCrafter blockEntity = crafter.getOrCreateBlockEntity();
        int slot = pk.getSlot();
        boolean state = !pk.isDisabled();

        blockEntity.getInventory().setSlotState(slot, state);
    }

    @Override
    public Class<ToggleCrafterSlotRequestPacket> getPacketClass() {
        return ToggleCrafterSlotRequestPacket.class;
    }
}
