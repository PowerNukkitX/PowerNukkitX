package cn.nukkit.level.terra.delegate;

import cn.nukkit.block.BlockWater;
import cn.nukkit.level.terra.PNXAdapter;
import com.dfsek.terra.api.block.BlockType;
import com.dfsek.terra.api.block.state.BlockState;

public record PNXBlockType(cn.nukkit.blockstate.BlockState innerBlockState) implements BlockType {
    @Override
    public BlockState getDefaultState() {
        return PNXAdapter.adapt(innerBlockState);
    }

    @Override
    public boolean isSolid() {
        return innerBlockState.getBlock().isSolid();
    }

    @Override
    public boolean isWater() {
        return innerBlockState.getBlock() instanceof BlockWater;
    }

    @Override
    public cn.nukkit.blockstate.BlockState getHandle() {
        return innerBlockState;
    }
}
