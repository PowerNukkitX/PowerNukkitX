package cn.nukkit.level.terra.delegate;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.impl.BlockWater;
import cn.nukkit.level.terra.PNXAdapter;
import com.dfsek.terra.api.block.BlockType;
import com.dfsek.terra.api.block.state.BlockState;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public record PNXBlockType(cn.nukkit.block.state.BlockState innerBlockState) implements BlockType {
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
    public cn.nukkit.block.state.BlockState getHandle() {
        return innerBlockState;
    }
}
