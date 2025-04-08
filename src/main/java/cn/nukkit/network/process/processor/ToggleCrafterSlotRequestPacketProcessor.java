package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockCrafter;
import cn.nukkit.blockentity.BlockEntityCrafter;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3f;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ToggleCrafterSlotRequestPacket;
import org.jetbrains.annotations.NotNull;

public class ToggleCrafterSlotRequestPacketProcessor extends DataPacketProcessor<ToggleCrafterSlotRequestPacket> {

    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull ToggleCrafterSlotRequestPacket pk) {
        Level level = playerHandle.player.getLevel();
        Vector3f position = pk.getBlockPosition();

        Block block = level.getBlock(position.asVector3());
        if (!(block instanceof BlockCrafter crafter)) {
            return;
        }

        BlockEntityCrafter blockEntity = crafter.getOrCreateBlockEntity();
        int slot = pk.slot;
        boolean state = !pk.isDisabled();

        blockEntity.setSlotState(slot, state);
    }

    @Override
    public int getPacketId() {
        return 0;
    }
}
