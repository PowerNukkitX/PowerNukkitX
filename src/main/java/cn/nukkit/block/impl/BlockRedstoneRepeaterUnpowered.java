package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockRedstoneRepeater;
import cn.nukkit.block.state.BlockState;

/**
 * @author CreeperFace
 * @since 10.4.2017
 */
@PowerNukkitDifference(
        info = "Extends BlockRedstoneRepeater instead of BlockRedstoneDiode only in PowerNukkit",
        since = "1.4.0.0-PN")
public class BlockRedstoneRepeaterUnpowered extends BlockRedstoneRepeater {

    public BlockRedstoneRepeaterUnpowered() {
        super();
        this.isPowered = false;
    }

    @Override
    public int getId() {
        return UNPOWERED_REPEATER;
    }

    @Override
    public String getName() {
        return "Unpowered Repeater";
    }

    @Override
    protected Block getPowered() {
        return BlockState.of(BlockID.POWERED_REPEATER, getCurrentState().getDataStorage())
                .getBlock();
    }

    @Override
    protected Block getUnpowered() {
        return this;
    }
}
