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
public class BlockRedstoneRepeaterPowered extends BlockRedstoneRepeater {

    public BlockRedstoneRepeaterPowered() {
        super();
        this.isPowered = true;
    }

    @Override
    public int getId() {
        return POWERED_REPEATER;
    }

    @Override
    public String getName() {
        return "Powered Repeater";
    }

    @Override
    protected Block getPowered() {
        return this;
    }

    @Override
    protected Block getUnpowered() {
        return BlockState.of(BlockID.UNPOWERED_REPEATER, getCurrentState().getDataStorage())
                .getBlock();
    }

    @Override
    public int getLightLevel() {
        return 7;
    }
}
