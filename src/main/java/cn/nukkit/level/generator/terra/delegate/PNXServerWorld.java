package cn.nukkit.level.generator.terra.delegate;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.generator.terra.PNXAdapter;
import cn.nukkit.level.generator.terra.TerraGenerator;
import cn.nukkit.math.BlockVector3;
import com.dfsek.terra.api.block.entity.BlockEntity;
import com.dfsek.terra.api.block.state.BlockState;
import com.dfsek.terra.api.config.ConfigPack;
import com.dfsek.terra.api.entity.Entity;
import com.dfsek.terra.api.entity.EntityType;
import com.dfsek.terra.api.world.ServerWorld;
import com.dfsek.terra.api.world.biome.generation.BiomeProvider;
import com.dfsek.terra.api.world.chunk.Chunk;
import com.dfsek.terra.api.world.chunk.generation.ChunkGenerator;

import java.util.Objects;

public record PNXServerWorld(TerraGenerator generatorWrapper, Level level) implements ServerWorld {

    @Override
    public void setBlockState(int i, int i1, int i2, BlockState blockState, boolean b) {
        var innerBlockState = ((PNXBlockStateDelegate) blockState).innerBlockState();
        if (Objects.equals(innerBlockState.getIdentifier(), BlockID.KELP)) {
            level.setBlockStateAt(i, i1, i2, innerBlockState);
            level.setBlockStateAt(i, i1, i2, 1, PNXProtoChunk.water);
        } else {
            var ob = level.getBlockStateAt(i, i1, i2);
            if (Objects.equals(ob.getIdentifier(), BlockID.WATERLILY) ||
                    Objects.equals(ob.getIdentifier(), BlockID.WATER) ||
                    Objects.equals(ob.getIdentifier(), BlockID.FLOWING_WATER)) {
                level.setBlockStateAt(i, i1, i2, innerBlockState);
                level.setBlockStateAt(i, i1, i2, 1, PNXProtoChunk.water);
            } else {
                level.setBlockStateAt(i, i1, i2, innerBlockState);
            }
        }
    }

    @Override
    public Entity spawnEntity(double v, double v1, double v2, EntityType entityType) {
        String identifier = (String) entityType.getHandle();
        cn.nukkit.entity.Entity nukkitEntity = cn.nukkit.entity.Entity.createEntity(identifier, new Position(v, v1, v2, level));
        return new PNXEntity(nukkitEntity, this);
    }

    @Override
    public BlockState getBlockState(int i, int i1, int i2) {
        return PNXAdapter.adapt(level.getBlockStateAt(i, i1, i2));
    }

    @Override
    public BlockEntity getBlockEntity(int i, int i1, int i2) {
        cn.nukkit.blockentity.BlockEntity blockEntity = level.getBlockEntity(new BlockVector3(i, i1, i2));
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
        return level.getSeed();
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
    public Level getHandle() {
        return level;
    }

    @Override
    public Chunk getChunkAt(int i, int i1) {
        return new PNXChunkDelegate(this, level.getChunk(i, i1));
    }
}
