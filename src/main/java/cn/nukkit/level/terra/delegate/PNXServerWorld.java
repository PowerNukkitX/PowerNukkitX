package cn.nukkit.level.terra.delegate;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Position;
import cn.nukkit.level.terra.TerraGenerator;
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

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public record PNXServerWorld(TerraGenerator generatorWrapper, ChunkManager manager) implements ServerWorld {

    @Override
    public void setBlockState(int i, int i1, int i2, BlockState blockState, boolean b) {
        manager.setBlockStateAt(i, i1, i2, ((PNXBlockStateDelegate) blockState).getHandle());
    }

    @Override
    public Entity spawnEntity(double v, double v1, double v2, EntityType entityType) {
        String identifier = (String) entityType.getHandle();
        cn.nukkit.entity.Entity nukkitEntity = cn.nukkit.entity.Entity.createEntity(identifier, new Position(v, v1, v2, generatorWrapper.getLevel()));
        return new PNXEntity(nukkitEntity, this);
    }

    @Override
    public BlockState getBlockState(int i, int i1, int i2) {
        return PNXAdapter.adapt(manager.getBlockStateAt(i, i1, i2));
    }

    @Override
    public BlockEntity getBlockEntity(int i, int i1, int i2) {
        System.err.println("Unsupported getBlockEntity!");
        return null;
    }

    @Override
    public ChunkGenerator getGenerator() {
        return generatorWrapper.getHandle();
    }

    @Override
    public BiomeProvider getBiomeProvider() {
        return generatorWrapper.getBiomeProvider();
    }

    @Override
    public ConfigPack getPack() {
        return generatorWrapper.getConfigPack();
    }

    @Override
    public long getSeed() {
        return manager.getSeed();
    }

    @Override
    public int getMaxHeight() {
        return generatorWrapper.getDimensionData().getMaxHeight();
    }

    @Override
    public int getMinHeight() {
        return generatorWrapper.getDimensionData().getMinHeight();
    }

    @Override
    public ChunkManager getHandle() {
        return manager;
    }

    @Override
    public Chunk getChunkAt(int i, int i1) {
        return new PNXChunkDelegate(this, manager.getChunk(i, i1));
    }
}
