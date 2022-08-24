package cn.nukkit.level.terra.delegate;

import cn.nukkit.Server;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.terra.PNXAdapter;
import com.dfsek.terra.api.block.state.BlockState;
import com.dfsek.terra.api.world.chunk.generation.ProtoChunk;
import org.iq80.leveldb.table.Block;
import org.jetbrains.annotations.NotNull;

public record PNXProtoChunk(BaseFullChunk chunk) implements ProtoChunk {
    @Override
    public int getMaxHeight() {
        return 384;
    }

    @Override
    public void setBlock(int i, int i1, int i2, @NotNull BlockState blockState) {
        var ob = chunk.getBlockState(i, i1, i2);
        var innerBlockState = ((PNXBlockStateDelegate) blockState).getHandle();
        if (innerBlockState.getBlockId() == BlockID.BLOCK_KELP){
            chunk.setBlockStateAt(i, i1, i2, innerBlockState);
            chunk.setBlockAtLayer(i, i1, i2, 1, BlockID.STILL_WATER);
        } else if (ob.getBlockId() == BlockID.WATERLILY || ob.getBlockId() == BlockID.STILL_WATER || ob.getBlockId() == BlockID.FLOWING_WATER) {
            chunk.setBlockStateAt(i, i1, i2, innerBlockState);
            chunk.setBlockStateAt(i, i1, i2, 1, ob);
        } else chunk.setBlockStateAt(i, i1, i2, innerBlockState);
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
