package cn.nukkit.level.terra.delegate;

import cn.nukkit.Server;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.terra.PNXAdapter;
import com.dfsek.terra.api.block.entity.BlockEntity;
import com.dfsek.terra.api.block.state.BlockState;
import com.dfsek.terra.api.config.ConfigPack;
import com.dfsek.terra.api.entity.Entity;
import com.dfsek.terra.api.entity.EntityType;
import com.dfsek.terra.api.world.ServerWorld;
import com.dfsek.terra.api.world.biome.generation.BiomeProvider;
import com.dfsek.terra.api.world.chunk.Chunk;
import com.dfsek.terra.api.world.chunk.generation.ChunkGenerator;

public record PNXServerWorld(Level level, ChunkManager chunkManager, ChunkGenerator chunkGenerator, ConfigPack configPack) implements ServerWorld {

    @Override
    public void setBlockState(int i, int i1, int i2, BlockState blockState, boolean b) {
        chunkManager.setBlockStateAt(i, i1, i2, ((PNXBlockStateDelegate) blockState).getHandle());
    }

    @Override
    public Entity spawnEntity(double v, double v1, double v2, EntityType entityType) {
        String identifier = (String) entityType.getHandle();
        cn.nukkit.entity.Entity nukkitEntity = cn.nukkit.entity.Entity.createEntity(identifier, new Position(v, v1, v2, level));
        return new PNXEntity(nukkitEntity, this);
    }

    @Override
    public BlockState getBlockState(int i, int i1, int i2) {
        return PNXAdapter.adapt(chunkManager.getBlockStateAt(i, i1, i2));
    }

    @Override
    public BlockEntity getBlockEntity(int i, int i1, int i2) {
        System.err.println("Unsupported getBlockEntity!");
        return null;
    }

    @Override
    public ChunkGenerator getGenerator() {
        return chunkGenerator;
    }

    @Override
    public BiomeProvider getBiomeProvider() {
        return configPack.getBiomeProvider();
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
        return new PNXChunkDelegate(this, chunkManager.getChunk(i, i1));
    }
}
