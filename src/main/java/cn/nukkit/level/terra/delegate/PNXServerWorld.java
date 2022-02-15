package cn.nukkit.level.terra.delegate;

import cn.nukkit.level.ChunkManager;
import com.dfsek.terra.api.block.entity.BlockEntity;
import com.dfsek.terra.api.block.state.BlockState;
import com.dfsek.terra.api.config.ConfigPack;
import com.dfsek.terra.api.entity.Entity;
import com.dfsek.terra.api.entity.EntityType;
import com.dfsek.terra.api.world.ServerWorld;
import com.dfsek.terra.api.world.biome.generation.BiomeProvider;
import com.dfsek.terra.api.world.chunk.Chunk;
import com.dfsek.terra.api.world.chunk.generation.ChunkGenerator;
import com.dfsek.terra.api.world.chunk.generation.ProtoWorld;

public record PNXServerWorld(ChunkManager chunkManager, ChunkGenerator chunkGenerator, ConfigPack configPack) implements ServerWorld {

    @Override
    public void setBlockState(int i, int i1, int i2, BlockState blockState, boolean b) {
        chunkManager.setBlockStateAt(i, i1, i2, ((PNXBlockStateDelegate) blockState).getHandle());
    }

    @Override
    public Entity spawnEntity(double v, double v1, double v2, EntityType entityType) {
        // TODO: 2022/2/14 暂不支持实体
        return null;
    }

    @Override
    public BlockState getBlockState(int i, int i1, int i2) {
        return new PNXBlockStateDelegate(chunkManager.getBlockStateAt(i, i1, i2));
    }

    @Override
    public BlockEntity getBlockEntity(int i, int i1, int i2) {
        return null;
    }

    @Override
    public ChunkGenerator getGenerator() {
        return chunkGenerator;
    }

    @Override
    public BiomeProvider getBiomeProvider() {
        return configPack.getBiomeProvider().caching();
    }

    @Override
    public ConfigPack getPack() {
        return configPack;
    }

    @Override
    public long getSeed() {
        return chunkManager.getSeed();
    }

    @Override
    public int getMaxHeight() {
        return 320;
    }

    @Override
    public int getMinHeight() {
        return -64;
    }

    @Override
    public ChunkManager getHandle() {
        return chunkManager;
    }

    @Override
    public Chunk getChunkAt(int i, int i1) {
        return new PNXChunkDelegate(chunkManager.getChunk(i ,i1), chunkManager, chunkGenerator, configPack);
    }
}
