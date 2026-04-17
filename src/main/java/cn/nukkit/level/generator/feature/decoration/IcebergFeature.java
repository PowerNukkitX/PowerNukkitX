package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockPackedIce;
import cn.nukkit.block.BlockSnow;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockWater;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateFeature;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.utils.random.NukkitRandom;

import java.util.HashSet;
import java.util.Set;

import static cn.nukkit.block.BlockID.BLUE_ICE;
import static cn.nukkit.block.BlockID.FLOWING_WATER;
import static cn.nukkit.block.BlockID.ICE;
import static cn.nukkit.block.BlockID.PACKED_ICE;
import static cn.nukkit.block.BlockID.AIR;
import static cn.nukkit.block.BlockID.SNOW;
import static cn.nukkit.block.BlockID.SNOW_LAYER;
import static cn.nukkit.block.BlockID.WATER;
import static cn.nukkit.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;

public class IcebergFeature extends GenerateFeature {

    public static final String NAME = "minecraft:frozen_ocean_after_surface_ice_feature";

    private static final BlockState STATE_MAIN = BlockPackedIce.PROPERTIES.getDefaultState();
    private static final BlockState STATE_SNOW = BlockSnow.PROPERTIES.getDefaultState();
    private static final BlockState STATE_WATER = BlockWater.PROPERTIES.getDefaultState();
    private static final BlockState STATE_AIR = BlockAir.STATE;
    private final NukkitRandom nukkitRandom = new NukkitRandom();

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        nukkitRandom.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ) ^ NAME.hashCode());

        int originX = (chunkX << 4) + nextIntExclusive(16);
        int originZ = (chunkZ << 4) + nextIntExclusive(16);
        int originY = SEA_LEVEL + 1;
        if (!isValidIcebergBiome(level.getBiomeId(originX, SEA_LEVEL, originZ))) {
            return;
        }
        if (nextIntExclusive(8) != 0) {
            return;
        }

        boolean snowOnTop = nukkitRandom.nextDouble() > 0.7;
        double shapeAngle = nukkitRandom.nextDouble() * 2.0 * Math.PI;
        int shapeEllipseA = 11 - nextIntExclusive(5);
        int shapeEllipseC = 3 + nextIntExclusive(3);
        boolean isEllipse = nukkitRandom.nextDouble() > 0.7;

        int overWaterHeight = isEllipse ? nextIntExclusive(10) + 8 : nextIntExclusive(20) + 6;
        if (!isEllipse && nukkitRandom.nextDouble() > 0.72) {
            overWaterHeight += nextIntExclusive(12) + 6;
        }
        if (!isEllipse && nukkitRandom.nextDouble() > 0.93) {
            overWaterHeight += nextIntExclusive(18) + 8;
        }

        int underWaterHeight = Math.min(overWaterHeight + nextIntExclusive(14), 30);
        int width = Math.max(4, (int) Math.round(overWaterHeight * (0.48 + nukkitRandom.nextDouble() * 0.18))
                + nextIntExclusive(4) - nextIntExclusive(3));
        int a = isEllipse ? shapeEllipseA : width;

        BlockManager manager = new BlockManager(level);
        Set<Long> localIceberg = new HashSet<>();

        for (int xo = -a; xo < a; xo++) {
            for (int zo = -a; zo < a; zo++) {
                for (int yOff = 0; yOff < overWaterHeight; yOff++) {
                    int radius = isEllipse
                            ? heightDependentRadiusEllipse(yOff, overWaterHeight, width)
                            : heightDependentRadiusRound(yOff, overWaterHeight, width);
                    if (isEllipse || xo < radius) {
                        generateIcebergBlock(
                                manager, originX, originY, originZ, overWaterHeight, xo, yOff, zo, radius, a,
                                isEllipse, shapeEllipseC, shapeAngle, snowOnTop, localIceberg
                        );
                    }
                }
            }
        }

        for (int xo = -a; xo < a; xo++) {
            for (int zo = -a; zo < a; zo++) {
                for (int yOff = -1; yOff > -underWaterHeight; yOff--) {
                    int newA = isEllipse ? (int) Math.ceil(a * (1.0F - (float) Math.pow(yOff, 2.0) / (underWaterHeight * 8.0F))) : a;
                    int radius = heightDependentRadiusSteep(-yOff, underWaterHeight, width);
                    if (xo < radius) {
                        generateIcebergBlock(
                                manager, originX, originY, originZ, underWaterHeight, xo, yOff, zo, radius, newA,
                                isEllipse, shapeEllipseC, shapeAngle, snowOnTop, localIceberg
                        );
                    }
                }
            }
        }

        smooth(localIceberg, manager, originX, originY, originZ, width, overWaterHeight, isEllipse, shapeEllipseA);

        boolean doCutOut = isEllipse ? nukkitRandom.nextDouble() > 0.1 : nukkitRandom.nextDouble() > 0.7;
        if (doCutOut) {
            generateCutOut(manager, width, overWaterHeight, originX, originY, originZ, isEllipse, shapeEllipseA, shapeAngle, shapeEllipseC);
        }

        generateBlueIce(manager, originX, originZ);
        queueObject(chunk, manager);
    }

    private void generateBlueIce(BlockManager manager, int originX, int originZ) {
        int x = originX + nextIntExclusive(8) - nextIntExclusive(8);
        int z = originZ + nextIntExclusive(8) - nextIntExclusive(8);
        int y = SEA_LEVEL - 2;

        if (y > SEA_LEVEL - 1) {
            return;
        }

        String originState = getId(manager, x, y, z);
        String belowState = getId(manager, x, y - 1, z);
        if (!isWaterState(originState) && !isWaterState(belowState)) {
            return;
        }

        boolean foundPackedIce = false;
        if (getId(manager, x + 1, y, z).equals(PACKED_ICE)) foundPackedIce = true;
        if (getId(manager, x - 1, y, z).equals(PACKED_ICE)) foundPackedIce = true;
        if (getId(manager, x, y, z + 1).equals(PACKED_ICE)) foundPackedIce = true;
        if (getId(manager, x, y, z - 1).equals(PACKED_ICE)) foundPackedIce = true;
        if (getId(manager, x, y + 1, z).equals(PACKED_ICE)) foundPackedIce = true;

        if (!foundPackedIce) {
            return;
        }

        setBlock(manager, x, y, z, cn.nukkit.block.BlockBlueIce.PROPERTIES.getDefaultState());

        for (int i = 0; i < 200; i++) {
            int yOff = nextIntExclusive(5) - nextIntExclusive(6);
            int xzDiff = 3;
            if (yOff < 2) {
                xzDiff += yOff / 2;
            }

            if (xzDiff < 1) {
                continue;
            }

            int px = x + nextIntExclusive(xzDiff) - nextIntExclusive(xzDiff);
            int py = y + yOff;
            int pz = z + nextIntExclusive(xzDiff) - nextIntExclusive(xzDiff);
            String placeState = getId(manager, px, py, pz);

            if (placeState.equals(AIR) || isWaterState(placeState) || placeState.equals(PACKED_ICE) || placeState.equals(ICE)) {
                if (hasAdjacentBlueIce(manager, px, py, pz)) {
                    setBlock(manager, px, py, pz, cn.nukkit.block.BlockBlueIce.PROPERTIES.getDefaultState());
                }
            }
        }
    }

    private void generateCutOut(
            BlockManager manager,
            int width,
            int height,
            int originX,
            int originY,
            int originZ,
            boolean isEllipse,
            int shapeEllipseA,
            double shapeAngle,
            int shapeEllipseC
    ) {
        int randomSignX = nukkitRandom.nextBoolean() ? -1 : 1;
        int randomSignZ = nukkitRandom.nextBoolean() ? -1 : 1;
        int xOff = nextIntExclusive(Math.max(width / 2 - 2, 1));
        if (nukkitRandom.nextBoolean()) {
            xOff = width / 2 + 1 - nextIntExclusive(Math.max(width - width / 2 - 1, 1));
        }

        int zOff = nextIntExclusive(Math.max(width / 2 - 2, 1));
        if (nukkitRandom.nextBoolean()) {
            zOff = width / 2 + 1 - nextIntExclusive(Math.max(width - width / 2 - 1, 1));
        }

        if (isEllipse) {
            xOff = nextIntExclusive(Math.max(shapeEllipseA - 5, 1));
            zOff = xOff;
        }

        int localOriginX = randomSignX * xOff;
        int localOriginZ = randomSignZ * zOff;
        double angle = isEllipse ? shapeAngle + (Math.PI / 2) : nukkitRandom.nextDouble() * 2.0 * Math.PI;

        for (int yOff = 0; yOff < height - 3; yOff++) {
            int radius = heightDependentRadiusRound(yOff, height, width);
            carve(manager, radius, yOff, originX, originY, originZ, false, angle, localOriginX, localOriginZ, shapeEllipseA, shapeEllipseC);
        }

        for (int yOff = -1; yOff > -height + nextIntExclusive(5); yOff--) {
            int radius = heightDependentRadiusSteep(-yOff, height, width);
            carve(manager, radius, yOff, originX, originY, originZ, true, angle, localOriginX, localOriginZ, shapeEllipseA, shapeEllipseC);
        }
    }

    private void carve(
            BlockManager manager,
            int radius,
            int yOff,
            int originX,
            int originY,
            int originZ,
            boolean underWater,
            double angle,
            int localOriginX,
            int localOriginZ,
            int shapeEllipseA,
            int shapeEllipseC
    ) {
        int a = radius + 1 + shapeEllipseA / 3;
        int c = Math.min(radius - 3, 3) + shapeEllipseC / 2 - 1;
        if (c <= 0) {
            return;
        }

        for (int xo = -a; xo < a; xo++) {
            for (int zo = -a; zo < a; zo++) {
                double signedDist = signedDistanceEllipse(xo, zo, localOriginX, localOriginZ, a, c, angle);
                if (signedDist < 0.0) {
                    int x = originX + xo;
                    int y = originY + yOff;
                    int z = originZ + zo;
                    String state = getId(manager, x, y, z);
                    if (isIcebergState(state) || state.equals(SNOW)) {
                        if (underWater) {
                            setBlock(manager, x, y, z, STATE_WATER);
                        } else {
                            setBlock(manager, x, y, z, STATE_AIR);
                            removeFloatingSnowLayer(manager, x, y, z);
                        }
                    }
                }
            }
        }
    }

    private void removeFloatingSnowLayer(BlockManager manager, int x, int y, int z) {
        if (getId(manager, x, y + 1, z).equals(SNOW_LAYER)) {
            setBlock(manager, x, y + 1, z, STATE_AIR);
        }
    }

    private void generateIcebergBlock(
            BlockManager manager,
            int originX,
            int originY,
            int originZ,
            int height,
            int xo,
            int yOff,
            int zo,
            int radius,
            int a,
            boolean isEllipse,
            int shapeEllipseC,
            double shapeAngle,
            boolean snowOnTop,
            Set<Long> localIceberg
    ) {
        double signedDist = isEllipse
                ? signedDistanceEllipse(xo, zo, 0, 0, a, getEllipseC(yOff, height, shapeEllipseC), shapeAngle)
                : signedDistanceCircle(xo, zo, 0, 0, radius);
        if (signedDist < 0.0) {
            int x = originX + xo;
            int y = originY + yOff;
            int z = originZ + zo;
            double compareVal = isEllipse ? -0.5 : -6 - nextIntExclusive(3);
            if (signedDist > compareVal && nukkitRandom.nextDouble() > 0.9) {
                return;
            }
            setIcebergBlock(manager, x, y, z, height - yOff, height, isEllipse, snowOnTop, localIceberg);
        }
    }

    private void setIcebergBlock(
            BlockManager manager,
            int x,
            int y,
            int z,
            int hDiff,
            int height,
            boolean isEllipse,
            boolean snowOnTop,
            Set<Long> localIceberg
    ) {
        String state = getId(manager, x, y, z);
        if (state.equals(AIR) || state.equals(SNOW) || state.equals(ICE) || state.equals(WATER) || state.equals(FLOWING_WATER)) {
            boolean randomness = !isEllipse || nukkitRandom.nextDouble() > 0.05;
            int divisor = isEllipse ? 3 : 2;
            if (snowOnTop && !state.equals(WATER) && !state.equals(FLOWING_WATER)
                    && hDiff <= nextIntExclusive(Math.max(1, height / divisor)) + (int) (height * 0.6F) && randomness) {
                setBlock(manager, x, y, z, STATE_SNOW);
                localIceberg.add(posKey(x, y, z));
            } else {
                setBlock(manager, x, y, z, STATE_MAIN);
                localIceberg.add(posKey(x, y, z));
            }
        }
    }

    private int getEllipseC(int yOff, int height, int shapeEllipseC) {
        int c = shapeEllipseC;
        if (yOff > 0 && height - yOff <= 3) {
            c = shapeEllipseC - (4 - (height - yOff));
        }
        return c;
    }

    private double signedDistanceCircle(int xo, int zo, int originX, int originZ, int radius) {
        float off = 10.0F * clamp(nukkitRandom.nextFloat(), 0.2F, 0.8F) / Math.max(1, radius);
        return off + Math.pow(xo - originX, 2.0) + Math.pow(zo - originZ, 2.0) - Math.pow(radius, 2.0);
    }

    private double signedDistanceEllipse(int xo, int zo, int originX, int originZ, int a, int c, double angle) {
        return Math.pow(((xo - originX) * Math.cos(angle) - (zo - originZ) * Math.sin(angle)) / Math.max(1, a), 2.0)
                + Math.pow(((xo - originX) * Math.sin(angle) + (zo - originZ) * Math.cos(angle)) / Math.max(1, c), 2.0)
                - 1.0;
    }

    private int heightDependentRadiusRound(int yOff, int height, int width) {
        float k = 3.5F - nukkitRandom.nextFloat();
        float scale = (1.0F - (float) Math.pow(yOff, 2.0) / (height * k)) * width;
        if (height > 15 + nextIntExclusive(5)) {
            int tempYOff = yOff < 3 + nextIntExclusive(6) ? yOff / 2 : yOff;
            scale = (1.0F - tempYOff / (height * k * 0.4F)) * width;
        }
        return (int) Math.ceil(scale / 2.0F);
    }

    private int heightDependentRadiusEllipse(int yOff, int height, int width) {
        float scale = (1.0F - (float) Math.pow(yOff, 2.0) / (height * 1.0F)) * width;
        return (int) Math.ceil(scale / 2.0F);
    }

    private int heightDependentRadiusSteep(int yOff, int height, int width) {
        float k = 1.0F + nukkitRandom.nextFloat() / 2.0F;
        float scale = (1.0F - yOff / (height * k)) * width;
        return (int) Math.ceil(scale / 2.0F);
    }

    private boolean isIcebergState(String id) {
        return id.equals(PACKED_ICE) || id.equals(SNOW) || id.equals(BLUE_ICE);
    }

    private boolean hasAdjacentBlueIce(BlockManager manager, int x, int y, int z) {
        return getId(manager, x + 1, y, z).equals(BLUE_ICE)
                || getId(manager, x - 1, y, z).equals(BLUE_ICE)
                || getId(manager, x, y, z + 1).equals(BLUE_ICE)
                || getId(manager, x, y, z - 1).equals(BLUE_ICE)
                || getId(manager, x, y + 1, z).equals(BLUE_ICE)
                || getId(manager, x, y - 1, z).equals(BLUE_ICE);
    }

    private boolean isWaterState(String id) {
        return id.equals(WATER) || id.equals(FLOWING_WATER);
    }

    private boolean isValidIcebergBiome(int biomeId) {
        return biomeId == BiomeID.FROZEN_OCEAN
                || biomeId == BiomeID.DEEP_FROZEN_OCEAN
                || biomeId == BiomeID.LEGACY_FROZEN_OCEAN;
    }

    private boolean belowIsAir(Set<Long> mask, int x, int y, int z) {
        return !mask.contains(posKey(x, y - 1, z));
    }

    private void smooth(Set<Long> mask, BlockManager manager, int originX, int originY, int originZ, int width, int height, boolean isEllipse, int shapeEllipseA) {
        int a = isEllipse ? shapeEllipseA : width / 2;
        int smoothingPasses = 3;
        for (int pass = 0; pass < smoothingPasses; pass++) {
            Set<Long> toRemove = new HashSet<>();

            for (int xOff = -a; xOff <= a; xOff++) {
                for (int zOff = -a; zOff <= a; zOff++) {
                    for (int yOff = 0; yOff <= height; yOff++) {
                        int x = originX + xOff;
                        int y = originY + yOff;
                        int z = originZ + zOff;
                        long key = posKey(x, y, z);
                        if (!mask.contains(key)) {
                            continue;
                        }
                        if (belowIsAir(mask, x, y, z)) {
                            toRemove.add(key);
                            long above = posKey(x, y + 1, z);
                            if (mask.contains(above)) {
                                toRemove.add(above);
                            }
                        } else {
                            int counter = 0;
                            if (!mask.contains(posKey(x - 1, y, z))) counter++;
                            if (!mask.contains(posKey(x + 1, y, z))) counter++;
                            if (!mask.contains(posKey(x, y, z - 1))) counter++;
                            if (!mask.contains(posKey(x, y, z + 1))) counter++;
                            if (counter >= 3) {
                                toRemove.add(key);
                            }
                        }
                    }
                }
            }

            if (toRemove.isEmpty()) {
                break;
            }

            for (long key : toRemove) {
                int x = (int) (key >> 42);
                int y = (int) ((key >> 21) & 0x1FFFFF);
                int z = (int) (key & 0x1FFFFF);
                if (y >= 0x100000) y -= 0x200000;
                if (z >= 0x100000) z -= 0x200000;
                setBlock(manager, x, y, z, STATE_AIR);
                mask.remove(key);
            }
        }
    }

    private String getId(BlockManager manager, int x, int y, int z) {
        return manager.getBlockIfCachedOrLoaded(x, y, z).getId();
    }

    private void setBlock(BlockManager manager, int x, int y, int z, BlockState state) {
        Level level = manager.getLevel();
        if (y < level.getMinHeight() || y > level.getMaxHeight()) {
            return;
        }
        manager.setBlockStateAt(x, y, z, state);
    }

    private static float clamp(float value, float min, float max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    private int nextIntExclusive(int boundExclusive) {
        if (boundExclusive <= 1) {
            return 0;
        }
        return nukkitRandom.nextRange(0, boundExclusive - 1);
    }

    private long posKey(int x, int y, int z) {
        return ((long) x << 42) ^ (((long) y & 0x1FFFFF) << 21) ^ ((long) z & 0x1FFFFF);
    }

    @Override
    public String name() {
        return NAME;
    }
}
