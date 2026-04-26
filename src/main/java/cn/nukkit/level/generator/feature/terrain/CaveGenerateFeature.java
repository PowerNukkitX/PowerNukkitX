package cn.nukkit.level.generator.feature.terrain;

import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockLava;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateFeature;
import cn.nukkit.math.MathHelper;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BlockTags;
import cn.nukkit.utils.random.RandomSourceProvider;

import java.util.Set;

import static cn.nukkit.level.Level.CHUNK_SIZE;

public class CaveGenerateFeature extends GenerateFeature {

    public static final String NAME = "minecraft:overworld_cave";
    protected static final int LAVA_LEVEL_OFFSET = 8;
    protected static final Set<String> LIQUID_BLOCK_IDS = Set.of(BlockID.WATER, BlockID.FLOWING_WATER, BlockID.LAVA, BlockID.FLOWING_LAVA);
    protected static final String GRASS_BLOCK_ID = BlockID.GRASS_BLOCK;
    protected static final Set<String> DIRT_BLOCK_IDS = BlockTags.getBlockSet(BlockTags.DIRT);
    protected static final BlockState LAVA_STATE = BlockLava.PROPERTIES.getDefaultState();

    protected final int carvingRangeChunks = 8;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        this.random.setSeed(level.getSeed());
        long xSeed = this.random.nextLong();
        long zSeed = this.random.nextLong();

        for (int sourceChunkX = chunkX - this.carvingRangeChunks; sourceChunkX <= chunkX + this.carvingRangeChunks; sourceChunkX++) {
            for (int sourceChunkZ = chunkZ - this.carvingRangeChunks; sourceChunkZ <= chunkZ + this.carvingRangeChunks; sourceChunkZ++) {
                long carvingSeed = (long) sourceChunkX * xSeed ^ (long) sourceChunkZ * zSeed ^ level.getSeed();
                this.random.setSeed(carvingSeed);
                carveChunk(this.random, sourceChunkX, sourceChunkZ, chunk);
            }
        }
    }

    protected void carveChunk(RandomSourceProvider random, int sourceChunkX, int sourceChunkZ, IChunk chunk) {
        if (random.nextFloat() > getCaveProbability()) {
            return;
        }

        int minY = chunk.getDimensionData().getMinHeight();
        int maxY = chunk.getDimensionData().getMaxHeight();
        int caveMinY = MathHelper.clamp(minY + LAVA_LEVEL_OFFSET, minY + 1, maxY - 1);
        int caveMaxY = MathHelper.clamp(getCaveMaxY(), caveMinY, maxY - 1);
        int lavaLevel = minY + LAVA_LEVEL_OFFSET;
        int maxDistance = this.carvingRangeChunks * CHUNK_SIZE - CHUNK_SIZE;
        int caveCount = random.nextInt(random.nextInt(random.nextInt(getCaveBound()) + 1) + 1);

        for (int cave = 0; cave < caveCount; cave++) {
            double x = sourceChunkX * CHUNK_SIZE + random.nextInt(CHUNK_SIZE);
            double y = random.nextInt(caveMinY, caveMaxY + 1);
            double z = sourceChunkZ * CHUNK_SIZE + random.nextInt(CHUNK_SIZE);
            double horizontalRadiusMultiplier = 0.7D + random.nextFloat() * 0.7D;
            double verticalRadiusMultiplier = 0.8D + random.nextFloat() * 0.5D;
            double floorLevel = -1.0D + random.nextFloat() * 0.6D;

            int tunnels = 1;
            if (random.nextInt(4) == 0) {
                double yScale = 0.1D + random.nextFloat() * 0.8D;
                float thickness = 1.0F + random.nextFloat() * 6.0F;
                createRoom(chunk, x, y, z, thickness, yScale, floorLevel, minY, maxY, lavaLevel);
                tunnels += random.nextInt(4);
            }

            for (int i = 0; i < tunnels; i++) {
                float horizontalRotation = random.nextFloat() * ((float) Math.PI * 2.0F);
                float verticalRotation = (random.nextFloat() - 0.5F) / 4.0F;
                float thickness = getThickness(random);
                int distance = maxDistance - random.nextInt(maxDistance / 4);
                createTunnel(
                        random.nextLong(),
                        chunk,
                        x,
                        y,
                        z,
                        horizontalRadiusMultiplier,
                        verticalRadiusMultiplier,
                        thickness,
                        horizontalRotation,
                        verticalRotation,
                        0,
                        distance,
                        getYScale(),
                        floorLevel,
                        minY,
                        maxY,
                        lavaLevel
                );
            }
        }
    }

    protected float getCaveProbability() {
        return 0.15F;
    }

    protected int getCaveBound() {
        return 15;
    }

    protected int getCaveMaxY() {
        return 180;
    }

    protected float getThickness(RandomSourceProvider random) {
        float thickness = random.nextFloat() * 2.0F + random.nextFloat();
        if (random.nextInt(10) == 0) {
            thickness *= random.nextFloat() * random.nextFloat() * 3.0F + 1.0F;
        }
        return thickness;
    }

    protected double getYScale() {
        return 1.0D;
    }

    protected void createRoom(
            IChunk chunk,
            double x,
            double y,
            double z,
            float thickness,
            double yScale,
            double floorLevel,
            int minY,
            int maxY,
            int lavaLevel
    ) {
        double horizontalRadius = 1.5D + MathHelper.sin((float) (Math.PI / 2.0D)) * thickness;
        double verticalRadius = horizontalRadius * yScale;
        carveEllipsoid(chunk, x + 1.0, y, z, horizontalRadius, verticalRadius, floorLevel, minY, maxY, lavaLevel);
    }

    protected void createTunnel(
            long tunnelSeed,
            IChunk chunk,
            double x,
            double y,
            double z,
            double horizontalRadiusMultiplier,
            double verticalRadiusMultiplier,
            float thickness,
            float horizontalRotation,
            float verticalRotation,
            int step,
            int dist,
            double yScale,
            double floorLevel,
            int minY,
            int maxY,
            int lavaLevel
    ) {
        RandomSourceProvider random = RandomSourceProvider.create(tunnelSeed);
        int splitPoint = random.nextInt(dist / 2) + dist / 4;
        boolean steep = random.nextInt(6) == 0;
        float yRota = 0.0F;
        float xRota = 0.0F;
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        double centerX = chunkX * CHUNK_SIZE + 8;
        double centerZ = chunkZ * CHUNK_SIZE + 8;

        for (int currentStep = step; currentStep < dist; currentStep++) {
            double horizontalRadius = 1.5D + MathHelper.sin((float) Math.PI * currentStep / dist) * thickness;
            double verticalRadius = horizontalRadius * yScale;
            float cosX = MathHelper.cos(verticalRotation);
            x += MathHelper.cos(horizontalRotation) * cosX;
            y += MathHelper.sin(verticalRotation);
            z += MathHelper.sin(horizontalRotation) * cosX;
            verticalRotation *= steep ? 0.92F : 0.7F;
            verticalRotation += xRota * 0.1F;
            horizontalRotation += yRota * 0.1F;
            xRota *= 0.9F;
            yRota *= 0.75F;
            xRota += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
            yRota += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;

            if (currentStep == splitPoint && thickness > 1.0F) {
                createTunnel(
                        random.nextLong(),
                        chunk,
                        x,
                        y,
                        z,
                        horizontalRadiusMultiplier,
                        verticalRadiusMultiplier,
                        random.nextFloat() * 0.5F + 0.5F,
                        horizontalRotation - (float) (Math.PI / 2.0D),
                        verticalRotation / 3.0F,
                        currentStep,
                        dist,
                        1.0D,
                        floorLevel,
                        minY,
                        maxY,
                        lavaLevel
                );
                createTunnel(
                        random.nextLong(),
                        chunk,
                        x,
                        y,
                        z,
                        horizontalRadiusMultiplier,
                        verticalRadiusMultiplier,
                        random.nextFloat() * 0.5F + 0.5F,
                        horizontalRotation + (float) (Math.PI / 2.0D),
                        verticalRotation / 3.0F,
                        currentStep,
                        dist,
                        1.0D,
                        floorLevel,
                        minY,
                        maxY,
                        lavaLevel
                );
                return;
            }

            if (random.nextInt(4) != 0) {
                if (!canReach(centerX, centerZ, x, z, currentStep, dist, thickness)) {
                    return;
                }
                carveEllipsoid(
                        chunk,
                        x,
                        y,
                        z,
                        horizontalRadius * horizontalRadiusMultiplier,
                        verticalRadius * verticalRadiusMultiplier,
                        floorLevel,
                        minY,
                        maxY,
                        lavaLevel
                );
            }
        }
    }

    protected boolean canReach(double centerX, double centerZ, double x, double z, int currentStep, int distance, float thickness) {
        double dx = x - centerX;
        double dz = z - centerZ;
        double remaining = distance - currentStep;
        double maxReach = thickness + 2.0F + CHUNK_SIZE;
        return dx * dx + dz * dz - remaining * remaining <= maxReach * maxReach;
    }

    protected boolean hasLiquid(IChunk chunk, int xFrom, int xTo, int yFrom, int yTo, int zFrom, int zTo, int maxY) {
        for (int xx = xFrom; xx < xTo; xx++) {
            for (int zz = zFrom; zz < zTo; zz++) {
                for (int yy = yTo + 1; yy >= yFrom - 1; yy--) {
                    if (yy >= maxY) {
                        continue;
                    }
                    if (LIQUID_BLOCK_IDS.contains(chunk.getBlockState(xx, yy, zz).getIdentifier())) {
                        return true;
                    }
                    if (yy != yFrom - 1 && xx != xFrom && xx != xTo - 1 && zz != zFrom && zz != zTo - 1) {
                        yy = yFrom;
                    }
                }
            }
        }
        return false;
    }

    protected void carveEllipsoid(
            IChunk chunk,
            double x,
            double y,
            double z,
            double horizontalRadius,
            double verticalRadius,
            double floorLevel,
            int minY,
            int maxY,
            int lavaLevel
    ) {
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        int chunkXBlock = chunkX * CHUNK_SIZE;
        int chunkZBlock = chunkZ * CHUNK_SIZE;
        int xFrom = MathHelper.floor(x - horizontalRadius) - chunkXBlock - 1;
        int xTo = MathHelper.floor(x + horizontalRadius) - chunkXBlock + 1;
        int yFrom = MathHelper.floor(y - verticalRadius) - 1;
        int yTo = MathHelper.floor(y + verticalRadius) + 1;
        int zFrom = MathHelper.floor(z - horizontalRadius) - chunkZBlock - 1;
        int zTo = MathHelper.floor(z + horizontalRadius) - chunkZBlock + 1;

        if (xFrom < 0) xFrom = 0;
        if (xTo > CHUNK_SIZE) xTo = CHUNK_SIZE;
        if (zFrom < 0) zFrom = 0;
        if (zTo > CHUNK_SIZE) zTo = CHUNK_SIZE;
        if (yFrom < minY + 1) yFrom = minY + 1;
        if (yTo > maxY - 1) yTo = maxY - 1;
        if (xFrom >= xTo || zFrom >= zTo || yFrom >= yTo) {
            return;
        }

        if (hasLiquid(chunk, xFrom, xTo, yFrom, yTo, zFrom, zTo, maxY)) {
            return;
        }

        double invHorizontalRadius = 1.0D / horizontalRadius;
        double invVerticalRadius = 1.0D / verticalRadius;
        for (int xx = xFrom; xx < xTo; xx++) {
            double xd = (xx + chunkXBlock + 0.5D - x) * invHorizontalRadius;
            double xdSq = xd * xd;
            for (int zz = zFrom; zz < zTo; zz++) {
                double zd = (zz + chunkZBlock + 0.5D - z) * invHorizontalRadius;
                double horizontalSq = xdSq + zd * zd;
                if (horizontalSq >= 1.0D) {
                    continue;
                }

                boolean grassFound = false;
                for (int yy = yTo; yy > yFrom; yy--) {
                    double yd = (yy - 0.5D - y) * invVerticalRadius;
                    if (yd <= floorLevel || horizontalSq + yd * yd >= 1.0D) {
                        continue;
                    }

                    BlockState currentState = chunk.getBlockState(xx, yy, zz);
                    if (GRASS_BLOCK_ID.equals(currentState.getIdentifier())) {
                        grassFound = true;
                    }

                    if (yy <= lavaLevel) {
                        if (currentState != LAVA_STATE) {
                            chunk.setBlockState(xx, yy, zz, LAVA_STATE);
                        }
                    } else {
                        if (currentState != BlockAir.STATE) {
                            chunk.setBlockState(xx, yy, zz, BlockAir.STATE);
                        }
                        restoreSurfaceIfNeeded(chunk, xx, yy - 1, zz, grassFound);
                    }
                }
            }
        }
    }

    protected void restoreSurfaceIfNeeded(IChunk chunk, int x, int y, int z, boolean grassFound) {
        if (!grassFound) {
            return;
        }

        BlockState belowState = chunk.getBlockState(x, y, z);
        if (!DIRT_BLOCK_IDS.contains(belowState.getIdentifier())) {
            return;
        }

        BiomeDefinition definition = Registries.BIOME.get(chunk.getBiomeId(x, y, z));
        BlockState topBlock = Registries.BLOCKSTATE.get(definition.data.chunkGenData.get().surfaceMaterial.get().topBlock);
        chunk.setBlockState(x, y, z, topBlock);
    }
}
