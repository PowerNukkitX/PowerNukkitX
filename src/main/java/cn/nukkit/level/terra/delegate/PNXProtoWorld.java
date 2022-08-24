package cn.nukkit.level.terra.delegate;

import cn.nukkit.Server;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Position;
import cn.nukkit.level.terra.PNXAdapter;
import com.dfsek.terra.api.block.entity.BlockEntity;
import com.dfsek.terra.api.block.state.BlockState;
import com.dfsek.terra.api.config.ConfigPack;
import com.dfsek.terra.api.entity.Entity;
import com.dfsek.terra.api.entity.EntityType;
import com.dfsek.terra.api.world.ServerWorld;
import com.dfsek.terra.api.world.biome.generation.BiomeProvider;
import com.dfsek.terra.api.world.chunk.generation.ChunkGenerator;
import com.dfsek.terra.api.world.chunk.generation.ProtoWorld;

public record PNXProtoWorld(ServerWorld serverWorld, int centerChunkX, int centerChunkZ) implements ProtoWorld {

    @Override
    public int centerChunkX() {
        return centerChunkX;
    }

    @Override
    public int centerChunkZ() {
        return centerChunkZ;
    }

    @Override
    public ServerWorld getWorld() {
        return serverWorld;
    }

    @Override
    public void setBlockState(int i, int i1, int i2, BlockState blockState, boolean b) {
        if (blockState instanceof PNXBlockStateDelegate pnxBlockState) {
            var chunkManager = getHandle();
            var oid = chunkManager.getBlockIdAt(i, i1, i2);
            if (oid == BlockID.WATERLILY || oid == BlockID.STILL_WATER || oid == BlockID.FLOWING_WATER) {
                chunkManager.setBlockStateAt(i, i1, i2, pnxBlockState.getHandle());
                chunkManager.setBlockAtLayer(i, i1, i2, 1, oid);
            } else chunkManager.setBlockStateAt(i, i1, i2, pnxBlockState.getHandle());
        }
    }

    @Override
    public Entity spawnEntity(double v, double v1, double v2, EntityType entityType) {
        String identifier = (String) entityType.getHandle();
        cn.nukkit.entity.Entity nukkitEntity = cn.nukkit.entity.Entity.createEntity(identifier, new Position(v, v1, v2, Server.getInstance().getDefaultLevel()));
        return new PNXEntity(nukkitEntity, serverWorld);
    }

    @Override
    public BlockState getBlockState(int i, int i1, int i2) {
        return PNXAdapter.adapt(getHandle().getBlockStateAt(i, i1, i2));
    }

    @Override
    public BlockEntity getBlockEntity(int i, int i1, int i2) {
        return null;
    }

    @Override
    public ChunkGenerator getGenerator() {
        return serverWorld.getGenerator();
    }

    @Override
    public BiomeProvider getBiomeProvider() {
        return serverWorld.getBiomeProvider();
    }

    @Override
    public ConfigPack getPack() {
        return serverWorld.getPack();
    }

    @Override
    public long getSeed() {
        return getHandle().getSeed();
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
        return (ChunkManager) serverWorld.getHandle();
    }
}
