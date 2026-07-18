package org.powernukkitx.level.generator.terra.delegate;

import org.powernukkitx.block.BlockFlowingWater;
import org.powernukkitx.level.generator.terra.PNXAdapter;
import com.dfsek.terra.api.block.BlockType;
import com.dfsek.terra.api.block.state.BlockState;

public record PNXBlockType(org.powernukkitx.block.BlockState innerBlockState) implements BlockType {
    @Override
    public BlockState getDefaultState() {
        return PNXAdapter.adapt(innerBlockState);
    }

    @Override
    public boolean isSolid() {
        return innerBlockState.toBlock().isSolid();
    }

    @Override
    public boolean isWater() {
        return innerBlockState.toBlock() instanceof BlockFlowingWater;
    }

    @Override
    public org.powernukkitx.block.BlockState getHandle() {
        return innerBlockState;
    }
}
