package cn.nukkit.level.generator.terra.delegate;

import cn.nukkit.block.BlockFlowingWater;
import cn.nukkit.level.generator.terra.PNXAdapter;
import com.dfsek.terra.api.block.BlockType;
import com.dfsek.terra.api.block.state.BlockState;

public record PNXBlockType(cn.nukkit.block.BlockState innerBlockState) implements BlockType {
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
    public cn.nukkit.block.BlockState getHandle() {
        return innerBlockState;
    }
}
