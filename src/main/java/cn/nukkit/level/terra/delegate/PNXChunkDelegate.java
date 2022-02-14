package cn.nukkit.level.terra.delegate;

import cn.nukkit.level.format.generic.BaseFullChunk;
import com.dfsek.terra.api.block.state.BlockState;
import com.dfsek.terra.api.world.ServerWorld;
import com.dfsek.terra.api.world.chunk.Chunk;
import com.dfsek.terra.api.world.chunk.generation.ProtoChunk;
import org.jetbrains.annotations.NotNull;

public record PNXChunkDelegate(BaseFullChunk chunk) implements Chunk {
    @Override
    public void setBlock(int i, int i1, int i2, BlockState blockState, boolean b) {
        setBlock(i, i1, i2, blockState);
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
    public int getX() {
        return 0;
    }

    @Override
    public int getZ() {
        return 0;
    }

    @Override
    public ServerWorld getWorld() {
        return null;
    }

    @Override
    public Object getHandle() {
        return chunk;
    }
}
