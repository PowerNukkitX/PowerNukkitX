package cn.nukkit.level.terra.delegate;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.terra.PNXAdapter;
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
        var oid = chunk.getBlockId(i, i1, i2);
        if (oid == BlockID.WATERLILY || oid == BlockID.STILL_WATER || oid == BlockID.FLOWING_WATER) {
            chunk.setBlockStateAt(i, i1, i2, ((PNXBlockStateDelegate) blockState).getHandle());
            chunk.setBlockAtLayer(i, i1, i2, 1, oid);
        } else chunk.setBlockStateAt(i, i1, i2, ((PNXBlockStateDelegate) blockState).getHandle());
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
