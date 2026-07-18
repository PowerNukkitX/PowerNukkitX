package org.powernukkitx.level.generator.terra.delegate;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.terra.PNXAdapter;
import org.powernukkitx.level.generator.terra.TerraGenerator;
import org.powernukkitx.math.BlockVector3;
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

public class PNXServerWorld implements ServerWorld {

    private final TerraGenerator generatorWrapper;
    private final BlockManager level;

    public PNXServerWorld(TerraGenerator generatorWrapper, Level level) {
        this.generatorWrapper = generatorWrapper;
        this.level = new BlockManager(level);
    }

    public TerraGenerator generatorWrapper() {
        return generatorWrapper;
    }

    public Level level() {
        return level.getLevel();
    }

    public void apply(IChunk chunk) {
        for(Block b : level.getBlocks()) {
            if(b.getChunk() != chunk) {
                level.getChunk(b.getChunkX(), b.getChunkZ());
            }
        }
        level.applySubChunkUpdate();
    }

    @Override
    public void setBlockState(int x, int y, int z, BlockState blockState, boolean b) {
        var innerBlockState = ((PNXBlockStateDelegate) blockState).innerBlockState();

        if (Objects.equals(innerBlockState.getIdentifier(), BlockID.KELP)) {
            level.setBlockStateAt(x, y, z, innerBlockState);
            level.setBlockStateAt(x, y, z, 1, PNXProtoChunk.water);
            return;
        }

        var ob = level.getBlockAt(x, y, z);
        if (Objects.equals(ob.getId(), BlockID.WATERLILY)
                || Objects.equals(ob.getId(), BlockID.WATER)
                || Objects.equals(ob.getId(), BlockID.FLOWING_WATER)) {

            level.setBlockStateAt(x, y, z, innerBlockState);
            level.setBlockStateAt(x, y, z, 1, PNXProtoChunk.water);
        } else {
            level.setBlockStateAt(x, y, z, innerBlockState);
        }
    }

    @Override
    public Entity spawnEntity(double x, double y, double z, EntityType entityType) {
        String identifier = (String) entityType.getHandle();
        org.powernukkitx.entity.Entity nukkitEntity =
                org.powernukkitx.entity.Entity.createEntity(
                        identifier,
                        new Position(x, y, z, level.getLevel())
                );
        return new PNXEntity(nukkitEntity, this);
    }

    @Override
    public BlockState getBlockState(int x, int y, int z) {
        return PNXAdapter.adapt(level.getBlockAt(x, y, z).getBlockState());
    }

    @Override
    public BlockEntity getBlockEntity(int x, int y, int z) {
        org.powernukkitx.blockentity.BlockEntity blockEntity =
                level.getLevel().getBlockEntity(new BlockVector3(x, y, z));
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
        return level.getLevel();
    }

    @Override
    public Chunk getChunkAt(int x, int z) {
        return new PNXChunkDelegate(this, level.getChunk(x, z));
    }
}
