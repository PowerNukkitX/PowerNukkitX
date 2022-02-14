package cn.nukkit.level.terra.delegate;

import cn.nukkit.level.format.generic.BaseFullChunk;
import com.dfsek.terra.api.block.state.BlockState;
import com.dfsek.terra.api.world.chunk.generation.ProtoChunk;
import org.jetbrains.annotations.NotNull;

public record PNXProtoChunk(BaseFullChunk chunk) implements ProtoChunk {
    @Override
    public int getMaxHeight() {
        return 384;
    }

    @Override
    public void setBlock(int i, int i1, int i2, @NotNull BlockState blockState) {
        chunk.setBlockState(i, i1, i2, ((PNXBlockStateDelegate) blockState).getHandle());
    }

    @Override
    public @NotNull
    BlockState getBlock(int i, int i1, int i2) {
        return new PNXBlockStateDelegate(chunk.getBlockState(i, i1, i2));
    }

    @Override
    public Object getHandle() {
        return chunk;
    }
}
