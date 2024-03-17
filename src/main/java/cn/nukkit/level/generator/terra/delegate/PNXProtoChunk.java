package cn.nukkit.level.generator.terra.delegate;

import cn.nukkit.block.BlockFlowingWater;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.terra.PNXAdapter;
import com.dfsek.terra.api.block.state.BlockState;
import com.dfsek.terra.api.world.chunk.generation.ProtoChunk;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record PNXProtoChunk(IChunk chunk) implements ProtoChunk {
    public static cn.nukkit.block.BlockState water = BlockFlowingWater.PROPERTIES.getDefaultState();

    @Override
    public int getMaxHeight() {
        return chunk.getDimensionData().getMaxHeight();
    }

    @Override
    public void setBlock(int i, int i1, int i2, @NotNull BlockState blockState) {
        var innerBlockState = ((PNXBlockStateDelegate) blockState).innerBlockState();
        if (Objects.equals(innerBlockState.getIdentifier(), BlockID.KELP)) {
            chunk.setBlockState(i, i1, i2, innerBlockState);
            chunk.setBlockState(i, i1, i2, water, 1);
        } else {
            var ob = chunk.getBlockState(i, i1, i2);
            if (Objects.equals(ob.getIdentifier(), BlockID.WATERLILY) ||
                    Objects.equals(ob.getIdentifier(), BlockID.WATER) ||
                    Objects.equals(ob.getIdentifier(), BlockID.FLOWING_WATER)) {
                chunk.setBlockState(i, i1, i2, innerBlockState);
                chunk.setBlockState(i, i1, i2, water, 1);
            } else {
                chunk.setBlockState(i, i1, i2, innerBlockState);
            }
        }
    }

    @Override
    public @NotNull
    BlockState getBlock(int i, int i1, int i2) {
        return PNXAdapter.adapt(chunk.getBlockState(i, i1, i2));
    }

    @Override
    public Object getHandle() {
        return chunk;
    }
}
