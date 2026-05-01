package cn.nukkit.level.generator.feature.terrain;

import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.MathHelper;
import cn.nukkit.utils.random.RandomSourceProvider;

import static cn.nukkit.level.Level.CHUNK_SIZE;

public class CanyonCarverFeature extends CaveGenerateFeature {

    public static final String NAME = "minecraft:canyon_carver";

    private static final float CANYON_PROBABILITY = 0.01F;
    private static final int MIN_CANYON_Y = 10;
    private static final int MAX_CANYON_Y = 67;
    private static final float Y_SCALE = 3.0F;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    protected void carveChunk(RandomSourceProvider random, int sourceChunkX, int sourceChunkZ, IChunk chunk) {
        if (random.nextFloat() > CANYON_PROBABILITY) {
            return;
        }

        int minY = chunk.getDimensionData().getMinHeight();
        int maxY = chunk.getDimensionData().getMaxHeight();
        int lavaLevel = minY + LAVA_LEVEL_OFFSET;

        double x = sourceChunkX * CHUNK_SIZE + random.nextInt(CHUNK_SIZE);
        double y = MathHelper.clamp(MIN_CANYON_Y + random.nextInt(MAX_CANYON_Y - MIN_CANYON_Y + 1), minY + 1, maxY - 1);
        double z = sourceChunkZ * CHUNK_SIZE + random.nextInt(CHUNK_SIZE);

        float horizontalRotation = random.nextFloat() * ((float) Math.PI * 2.0F);
        float verticalRotation = random.nextFloat() * 0.25F - 0.125F;
        float thickness = 2.0F + random.nextFloat() * 4.0F;
        int maxDistance = this.carvingRangeChunks * CHUNK_SIZE - CHUNK_SIZE;
        int distance = (int) (maxDistance * (0.75F + random.nextFloat() * 0.25F));

        carveCanyon(
                random,
                chunk,
                x,
                y,
                z,
                thickness,
                horizontalRotation,
                verticalRotation,
                distance,
                minY,
                maxY,
                lavaLevel
        );
    }

    private void carveCanyon(
            RandomSourceProvider random,
            IChunk chunk,
            double x,
            double y,
            double z,
            float thickness,
            float horizontalRotation,
            float verticalRotation,
            int distance,
            int minY,
            int maxY,
            int lavaLevel
    ) {
        float[] widthFactorPerHeight = initWidthFactors(random, minY, maxY);
        float xRota = 0.0F;
        float yRota = 0.0F;
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        double centerX = chunkX * CHUNK_SIZE + 8;
        double centerZ = chunkZ * CHUNK_SIZE + 8;

        for (int currentStep = 0; currentStep < distance; currentStep++) {
            double horizontalRadius = 1.5D + MathHelper.sin((float) currentStep * (float) Math.PI / distance) * thickness;
            double verticalRadius = updateVerticalRadius(random, horizontalRadius * Y_SCALE, distance, currentStep);
            horizontalRadius *= 0.75F + random.nextFloat() * 0.25F;

            float xzCos = MathHelper.cos(verticalRotation);
            float ySin = MathHelper.sin(verticalRotation);
            x += MathHelper.cos(horizontalRotation) * xzCos;
            y += ySin;
            z += MathHelper.sin(horizontalRotation) * xzCos;

            verticalRotation *= 0.7F;
            verticalRotation += xRota * 0.05F;
            horizontalRotation += yRota * 0.05F;
            xRota *= 0.8F;
            yRota *= 0.5F;
            xRota += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
            yRota += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;

            if (random.nextInt(4) == 0) {
                continue;
            }

            if (!canReach(centerX, centerZ, x, z, currentStep, distance, thickness)) {
                return;
            }

            int xFrom = MathHelper.floor(x - horizontalRadius) - chunkX * CHUNK_SIZE - 1;
            int xTo = MathHelper.floor(x + horizontalRadius) - chunkX * CHUNK_SIZE + 1;
            int yFrom = MathHelper.floor(y - verticalRadius) - 1;
            int yTo = MathHelper.floor(y + verticalRadius) + 1;
            int zFrom = MathHelper.floor(z - horizontalRadius) - chunkZ * CHUNK_SIZE - 1;
            int zTo = MathHelper.floor(z + horizontalRadius) - chunkZ * CHUNK_SIZE + 1;

            if (xFrom < 0) xFrom = 0;
            if (xTo > CHUNK_SIZE) xTo = CHUNK_SIZE;
            if (zFrom < 0) zFrom = 0;
            if (zTo > CHUNK_SIZE) zTo = CHUNK_SIZE;
            if (yFrom < minY + 1) yFrom = minY + 1;
            if (yTo > maxY - 1) yTo = maxY - 1;
            if (xFrom >= xTo || zFrom >= zTo || yFrom >= yTo) {
                continue;
            }

            if (hasLiquid(chunk, xFrom, xTo, yFrom, yTo, zFrom, zTo, maxY)) {
                continue;
            }

            carveEllipsoid(chunk, x, y, z, horizontalRadius, verticalRadius, xFrom, xTo, yFrom, yTo, zFrom, zTo, widthFactorPerHeight, minY, lavaLevel);
        }
    }

    private float[] initWidthFactors(RandomSourceProvider random, int minY, int maxY) {
        int depth = maxY - minY + 1;
        float[] widthFactorPerHeight = new float[depth];
        float widthFactor = 1.0F;
        for (int yIndex = 0; yIndex < depth; yIndex++) {
            if (yIndex == 0 || random.nextInt(3) == 0) {
                widthFactor = 1.0F + random.nextFloat() * random.nextFloat();
            }
            widthFactorPerHeight[yIndex] = widthFactor * widthFactor;
        }
        return widthFactorPerHeight;
    }

    private double updateVerticalRadius(RandomSourceProvider random, double verticalRadius, int distance, int currentStep) {
        float verticalMultiplier = 1.0F - Math.abs(0.5F - currentStep / (float) distance) * 2.0F;
        float factor = 1.0F + 0.0F * verticalMultiplier;
        return factor * verticalRadius * (0.75F + random.nextFloat() * 0.25F);
    }

    private void carveEllipsoid(
            IChunk chunk,
            double x,
            double y,
            double z,
            double horizontalRadius,
            double verticalRadius,
            int xFrom,
            int xTo,
            int yFrom,
            int yTo,
            int zFrom,
            int zTo,
            float[] widthFactorPerHeight,
            int minY,
            int lavaLevel
    ) {
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        int chunkXBlock = chunkX * CHUNK_SIZE;
        int chunkZBlock = chunkZ * CHUNK_SIZE;
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
                    int yIndex = yy - minY;
                    if (yIndex <= 0 || yIndex >= widthFactorPerHeight.length) {
                        continue;
                    }
                    double yd = (yy - 0.5D - y) * invVerticalRadius;
                    double shape = horizontalSq * widthFactorPerHeight[yIndex - 1] + yd * yd / 6.0D;
                    if (shape >= 1.0D) {
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
}
