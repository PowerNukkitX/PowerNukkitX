package cn.nukkit.level.generator.material;

import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockLava;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockStone;
import cn.nukkit.block.BlockWater;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.densityfunction.DensityCommon;
import cn.nukkit.level.generator.densityfunction.DensityFunction;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.random.NukkitRandom;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Aquifer {
    public static final double FLOWING_UPDATE_SIMILARITY = similarity(100, 144);

    private static final int X_RANGE = 10;
    private static final int Y_RANGE = 9;
    private static final int Z_RANGE = 10;
    private static final int Y_SPACING = 12;
    private static final int SURFACE_LEVEL_Y_OFFSET = 8;
    private static final int WAY_BELOW_MIN_Y = -1_000_000;
    private static final int[][] SURFACE_SAMPLING_OFFSETS_IN_CHUNKS = new int[][]{
            {0, 0}, {-2, -1}, {-1, -1}, {0, -1}, {1, -1}, {-3, 0}, {-2, 0}, {-1, 0}, {1, 0}, {-2, 1}, {-1, 1}, {0, 1}, {1, 1}
    };

    private final DensityFunction barrierNoise;
    private final DensityFunction fluidLevelFloodednessNoise;
    private final DensityFunction fluidLevelSpreadNoise;
    private final DensityFunction lavaNoise;
    private final DensityFunction erosion;
    private final DensityFunction depth;
    private final DensityFunction preliminarySurfaceDensity;
    private final DensityFunction preliminarySurfaceUpperBound;
    private final FluidStatus[] aquiferCache;
    private final long[] aquiferLocationCache;
    private final FluidPicker globalFluidPicker;
    private final int skipSamplingAboveY;
    private final int minY;
    private final int maxY;
    private final int minGridX;
    private final int minGridY;
    private final int minGridZ;
    private final int gridSizeX;
    private final int gridSizeZ;
    private final long randomSeed;
    private final int fallbackSurfaceLevel;
    private final int preliminarySurfaceLowerBound;
    private final int preliminarySurfaceCellHeight;
    private final CachedPointContext cachedPointContext;
    private final Map<Long, Integer> preliminarySurfaceLevelCache;
    private boolean shouldScheduleFluidUpdate;

    public static FluidPicker overworldFluidPicker(int seaLevel) {
        final BlockState water = BlockWater.PROPERTIES.getDefaultState();
        final BlockState lava = BlockLava.PROPERTIES.getDefaultState();
        final int lavaLevel = -54;
        final int lavaThreshold = Math.min(lavaLevel, seaLevel);
        return (x, y, z) -> y < lavaThreshold ? new FluidStatus(lavaLevel, lava) : new FluidStatus(seaLevel, water);
    }

    public Aquifer(
            IChunk chunk,
            Level level,
            DensityCommon.ChunkCache chunkCache,
            DensityFunction barrierNoise,
            DensityFunction fluidLevelFloodednessNoise,
            DensityFunction fluidLevelSpreadNoise,
            DensityFunction lavaNoise,
            DensityFunction erosion,
            DensityFunction depth,
            DensityFunction preliminarySurfaceDensity,
            DensityFunction preliminarySurfaceUpperBound,
            int preliminarySurfaceLowerBound,
            int preliminarySurfaceCellHeight,
            int minBlockY,
            int yBlockSize,
            FluidPicker globalFluidPicker
    ) {
        this.barrierNoise = barrierNoise;
        this.fluidLevelFloodednessNoise = fluidLevelFloodednessNoise;
        this.fluidLevelSpreadNoise = fluidLevelSpreadNoise;
        this.lavaNoise = lavaNoise;
        this.erosion = erosion;
        this.depth = depth;
        this.preliminarySurfaceDensity = preliminarySurfaceDensity;
        this.preliminarySurfaceUpperBound = preliminarySurfaceUpperBound;
        this.preliminarySurfaceLowerBound = preliminarySurfaceLowerBound;
        this.preliminarySurfaceCellHeight = preliminarySurfaceCellHeight;
        this.globalFluidPicker = globalFluidPicker;
        this.randomSeed = level.getSeed() ^ 0x4f9939f508L;
        this.cachedPointContext = new CachedPointContext(chunkCache);
        this.preliminarySurfaceLevelCache = new LinkedHashMap<>(64, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, Integer> eldest) {
                return size() > 64;
            }
        };
        this.fallbackSurfaceLevel = globalFluidPicker.computeFluid(0, 0, 0).fluidLevel();
        this.minY = minBlockY;
        this.maxY = minBlockY + yBlockSize - 1;

        int minBlockX = chunk.getX() << 4;
        int maxBlockX = minBlockX + 15;
        int minBlockZ = chunk.getZ() << 4;
        int maxBlockZ = minBlockZ + 15;

        this.minGridX = gridX(minBlockX - 5);
        int maxGridX = gridX(maxBlockX - 5) + 1;
        this.gridSizeX = maxGridX - this.minGridX + 1;
        this.minGridY = gridY(minBlockY + 1) - 1;
        int maxGridY = gridY(minBlockY + yBlockSize + 1) + 1;
        int gridSizeY = maxGridY - this.minGridY + 1;
        this.minGridZ = gridZ(minBlockZ - 5);
        int maxGridZ = gridZ(maxBlockZ - 5) + 1;
        this.gridSizeZ = maxGridZ - this.minGridZ + 1;

        int totalGridSize = this.gridSizeX * gridSizeY * this.gridSizeZ;
        this.aquiferCache = new FluidStatus[totalGridSize];
        this.aquiferLocationCache = new long[totalGridSize];
        Arrays.fill(this.aquiferLocationCache, Long.MAX_VALUE);
        int maxAdjustedSurfaceLevel = this.adjustSurfaceLevel(
                this.maxPreliminarySurfaceLevel(
                        fromGridX(this.minGridX, 0),
                        fromGridZ(this.minGridZ, 0),
                        fromGridX(maxGridX, 9),
                        fromGridZ(maxGridZ, 9)
                )
        );
        int skipSamplingAboveGridY = gridY(maxAdjustedSurfaceLevel + 12) + 1;
        this.skipSamplingAboveY = fromGridY(skipSamplingAboveGridY, 11) - 1;
    }

    public @Nullable BlockState computeSubstance(DensityFunction.FunctionContext context, double density) {
        if (density > 0.0) {
            this.shouldScheduleFluidUpdate = false;
            return null;
        }

        int posX = context.blockX();
        int posY = context.blockY();
        int posZ = context.blockZ();
        FluidStatus globalFluid = this.globalFluidPicker.computeFluid(posX, posY, posZ);
        BlockState globalAtPos = globalFluid.at(posY);

        if (posY > this.skipSamplingAboveY) {
            this.shouldScheduleFluidUpdate = false;
            return globalAtPos;
        }

        if (isLava(globalAtPos)) {
            this.shouldScheduleFluidUpdate = false;
            return BlockLava.PROPERTIES.getDefaultState();
        }

        int xAnchor = gridX(posX - 5);
        int yAnchor = gridY(posY + 1);
        int zAnchor = gridZ(posZ - 5);

        int distanceSqr1 = Integer.MAX_VALUE;
        int distanceSqr2 = Integer.MAX_VALUE;
        int distanceSqr3 = Integer.MAX_VALUE;
        int distanceSqr4 = Integer.MAX_VALUE;
        int closestIndex1 = 0;
        int closestIndex2 = 0;
        int closestIndex3 = 0;
        int closestIndex4 = 0;

        for (int x1 = 0; x1 <= 1; x1++) {
            for (int y1 = -1; y1 <= 1; y1++) {
                for (int z1 = 0; z1 <= 1; z1++) {
                    int spacedGridX = xAnchor + x1;
                    int spacedGridY = yAnchor + y1;
                    int spacedGridZ = zAnchor + z1;
                    int index = this.getIndex(spacedGridX, spacedGridY, spacedGridZ);
                    long existingLocation = this.aquiferLocationCache[index];
                    long location;
                    if (existingLocation != Long.MAX_VALUE) {
                        location = existingLocation;
                    } else {
                        NukkitRandom random = new NukkitRandom(mixSeed(this.randomSeed, spacedGridX, spacedGridY, spacedGridZ));
                        location = pack(
                                fromGridX(spacedGridX, random.nextInt(X_RANGE)),
                                fromGridY(spacedGridY, random.nextInt(Y_RANGE)),
                                fromGridZ(spacedGridZ, random.nextInt(Z_RANGE))
                        );
                        this.aquiferLocationCache[index] = location;
                    }

                    int dx = unpackX(location) - posX;
                    int dy = unpackY(location) - posY;
                    int dz = unpackZ(location) - posZ;
                    int newDistance = dx * dx + dy * dy + dz * dz;
                    if (distanceSqr1 >= newDistance) {
                        closestIndex4 = closestIndex3;
                        closestIndex3 = closestIndex2;
                        closestIndex2 = closestIndex1;
                        closestIndex1 = index;
                        distanceSqr4 = distanceSqr3;
                        distanceSqr3 = distanceSqr2;
                        distanceSqr2 = distanceSqr1;
                        distanceSqr1 = newDistance;
                    } else if (distanceSqr2 >= newDistance) {
                        closestIndex4 = closestIndex3;
                        closestIndex3 = closestIndex2;
                        closestIndex2 = index;
                        distanceSqr4 = distanceSqr3;
                        distanceSqr3 = distanceSqr2;
                        distanceSqr2 = newDistance;
                    } else if (distanceSqr3 >= newDistance) {
                        closestIndex4 = closestIndex3;
                        closestIndex3 = index;
                        distanceSqr4 = distanceSqr3;
                        distanceSqr3 = newDistance;
                    } else if (distanceSqr4 >= newDistance) {
                        closestIndex4 = index;
                        distanceSqr4 = newDistance;
                    }
                }
            }
        }

        FluidStatus closestStatus1 = this.getAquiferStatus(closestIndex1);
        double similarity12 = similarity(distanceSqr1, distanceSqr2);
        BlockState fluidState = closestStatus1.at(posY);
        if (similarity12 <= 0.0) {
            if (similarity12 >= FLOWING_UPDATE_SIMILARITY) {
                FluidStatus closestStatus2 = this.getAquiferStatus(closestIndex2);
                this.shouldScheduleFluidUpdate = !closestStatus1.equals(closestStatus2);
            } else {
                this.shouldScheduleFluidUpdate = false;
            }
            return fluidState;
        }

        if (isWater(fluidState) && isLava(this.globalFluidPicker.computeFluid(posX, posY - 1, posZ).at(posY - 1))) {
            this.shouldScheduleFluidUpdate = true;
            return fluidState;
        }

        double[] barrierNoiseValue = new double[]{Double.NaN};
        FluidStatus closestStatus2 = this.getAquiferStatus(closestIndex2);
        double barrier12 = similarity12 * this.calculatePressure(context, barrierNoiseValue, closestStatus1, closestStatus2);
        if (density + barrier12 > 0.0) {
            this.shouldScheduleFluidUpdate = false;
            return BlockStone.PROPERTIES.getDefaultState();
        }

        FluidStatus closestStatus3 = this.getAquiferStatus(closestIndex3);
        double similarity13 = similarity(distanceSqr1, distanceSqr3);
        if (similarity13 > 0.0) {
            double barrier13 = similarity12 * similarity13 * this.calculatePressure(context, barrierNoiseValue, closestStatus1, closestStatus3);
            if (density + barrier13 > 0.0) {
                this.shouldScheduleFluidUpdate = false;
                return BlockStone.PROPERTIES.getDefaultState();
            }
        }

        double similarity23 = similarity(distanceSqr2, distanceSqr3);
        if (similarity23 > 0.0) {
            double barrier23 = similarity12 * similarity23 * this.calculatePressure(context, barrierNoiseValue, closestStatus2, closestStatus3);
            if (density + barrier23 > 0.0) {
                this.shouldScheduleFluidUpdate = false;
                return BlockStone.PROPERTIES.getDefaultState();
            }
        }

        boolean mayFlow12 = !closestStatus1.equals(closestStatus2);
        boolean mayFlow23 = similarity23 >= FLOWING_UPDATE_SIMILARITY && !closestStatus2.equals(closestStatus3);
        boolean mayFlow13 = similarity13 >= FLOWING_UPDATE_SIMILARITY && !closestStatus1.equals(closestStatus3);
        if (!mayFlow12 && !mayFlow23 && !mayFlow13) {
            this.shouldScheduleFluidUpdate = similarity13 >= FLOWING_UPDATE_SIMILARITY
                    && similarity(distanceSqr1, distanceSqr4) >= FLOWING_UPDATE_SIMILARITY
                    && !closestStatus1.equals(this.getAquiferStatus(closestIndex4));
        } else {
            this.shouldScheduleFluidUpdate = true;
        }

        return fluidState;
    }

    public boolean shouldScheduleFluidUpdate() {
        return this.shouldScheduleFluidUpdate;
    }

    private int getIndex(int gridX, int gridY, int gridZ) {
        int x = gridX - this.minGridX;
        int y = gridY - this.minGridY;
        int z = gridZ - this.minGridZ;
        return (y * this.gridSizeZ + z) * this.gridSizeX + x;
    }

    private double calculatePressure(
            DensityFunction.FunctionContext context,
            double[] barrierNoiseValue,
            FluidStatus statusClosest1,
            FluidStatus statusClosest2
    ) {
        int posY = context.blockY();
        BlockState type1 = statusClosest1.at(posY);
        BlockState type2 = statusClosest2.at(posY);
        if ((!isLava(type1) || !isWater(type2)) && (!isWater(type1) || !isLava(type2))) {
            int fluidYDiff = Math.abs(statusClosest1.fluidLevel - statusClosest2.fluidLevel);
            if (fluidYDiff == 0) {
                return 0.0;
            }

            double averageFluidY = 0.5 * (statusClosest1.fluidLevel + statusClosest2.fluidLevel);
            double howFarAboveAverageFluidPoint = posY + 0.5 - averageFluidY;
            double baseValue = fluidYDiff / 2.0;
            double topBias = 0.0;
            double furthestRocksFromTopBias = 2.5;
            double furthestHolesFromTopBias = 1.5;
            double bottomBias = 3.0;
            double furthestRocksFromBottomBias = 10.0;
            double furthestHolesFromBottomBias = 3.0;
            double distanceFromBarrierEdgeTowardsMiddle = baseValue - Math.abs(howFarAboveAverageFluidPoint);
            double gradient;
            if (howFarAboveAverageFluidPoint > 0.0) {
                double centerPoint = topBias + distanceFromBarrierEdgeTowardsMiddle;
                if (centerPoint > 0.0) {
                    gradient = centerPoint / furthestHolesFromTopBias;
                } else {
                    gradient = centerPoint / furthestRocksFromTopBias;
                }
            } else {
                double centerPoint = bottomBias + distanceFromBarrierEdgeTowardsMiddle;
                if (centerPoint > 0.0) {
                    gradient = centerPoint / furthestHolesFromBottomBias;
                } else {
                    gradient = centerPoint / furthestRocksFromBottomBias;
                }
            }

            double amplitude = 2.0;
            double noiseValue;
            if (!(gradient < -amplitude) && !(gradient > amplitude)) {
                double currentNoiseValue = barrierNoiseValue[0];
                if (Double.isNaN(currentNoiseValue)) {
                    double barrierNoise = this.barrierNoise.compute(context);
                    barrierNoiseValue[0] = barrierNoise;
                    noiseValue = barrierNoise;
                } else {
                    noiseValue = currentNoiseValue;
                }
            } else {
                noiseValue = 0.0;
            }

            return amplitude * (noiseValue + gradient);
        }
        return 2.0;
    }

    private FluidStatus getAquiferStatus(int index) {
        FluidStatus oldStatus = this.aquiferCache[index];
        if (oldStatus != null) {
            return oldStatus;
        }
        long location = this.aquiferLocationCache[index];
        FluidStatus status = this.computeFluid(unpackX(location), unpackY(location), unpackZ(location));
        this.aquiferCache[index] = status;
        return status;
    }

    private FluidStatus computeFluid(int x, int y, int z) {
        FluidStatus globalFluid = this.globalFluidPicker.computeFluid(x, y, z);
        int lowestPreliminarySurface = Integer.MAX_VALUE;
        int topOfAquiferCell = y + Y_SPACING;
        int bottomOfAquiferCell = y - Y_SPACING;
        boolean surfaceAtCenterIsUnderGlobalFluidLevel = false;

        for (int[] offset : SURFACE_SAMPLING_OFFSETS_IN_CHUNKS) {
            int sampleX = x + (offset[0] << 4);
            int sampleZ = z + (offset[1] << 4);
            int preliminarySurfaceLevel = preliminarySurfaceLevel(sampleX, sampleZ);
            int adjustedSurfaceLevel = this.adjustSurfaceLevel(preliminarySurfaceLevel);
            boolean start = offset[0] == 0 && offset[1] == 0;
            if (start && bottomOfAquiferCell > adjustedSurfaceLevel) {
                return globalFluid;
            }

            boolean topOfAquiferCellPokesAboveSurface = topOfAquiferCell > adjustedSurfaceLevel;
            if (topOfAquiferCellPokesAboveSurface || start) {
                FluidStatus globalFluidAtSurface = this.globalFluidPicker.computeFluid(sampleX, adjustedSurfaceLevel, sampleZ);
                if (!isAir(globalFluidAtSurface.at(adjustedSurfaceLevel))) {
                    if (start) {
                        surfaceAtCenterIsUnderGlobalFluidLevel = true;
                    }
                    if (topOfAquiferCellPokesAboveSurface) {
                        return globalFluidAtSurface;
                    }
                }
            }
            lowestPreliminarySurface = Math.min(lowestPreliminarySurface, preliminarySurfaceLevel);
        }

        int fluidSurfaceLevel = this.computeSurfaceLevel(x, y, z, globalFluid, lowestPreliminarySurface, surfaceAtCenterIsUnderGlobalFluidLevel);
        return new FluidStatus(fluidSurfaceLevel, this.computeFluidType(x, y, z, globalFluid, fluidSurfaceLevel));
    }

    private int adjustSurfaceLevel(int preliminarySurfaceLevel) {
        return preliminarySurfaceLevel + SURFACE_LEVEL_Y_OFFSET;
    }

    private int computeSurfaceLevel(
            int x,
            int y,
            int z,
            FluidStatus globalFluid,
            int lowestPreliminarySurface,
            boolean surfaceAtCenterIsUnderGlobalFluidLevel
    ) {
        CachedPointContext context = this.cachedPointContext.set(x, y, z);
        double partiallyFloodedness;
        double fullyFloodedness;
        if (isDeepDarkRegion(this.erosion, this.depth, context)) {
            partiallyFloodedness = -1.0;
            fullyFloodedness = -1.0;
        } else {
            int distanceBelowSurface = lowestPreliminarySurface + SURFACE_LEVEL_Y_OFFSET - y;
            double floodednessFactor = surfaceAtCenterIsUnderGlobalFluidLevel ? clampedMap(distanceBelowSurface, 0.0, 64.0, 1.0, 0.0) : 0.0;
            double floodednessNoiseValue = NukkitMath.clamp(this.fluidLevelFloodednessNoise.compute(context), -1.0, 1.0);
            double fullyFloodedThreshold = map(floodednessFactor, 1.0, 0.0, -0.3, 0.8);
            double partiallyFloodedThreshold = map(floodednessFactor, 1.0, 0.0, -0.8, 0.4);
            partiallyFloodedness = floodednessNoiseValue - partiallyFloodedThreshold;
            fullyFloodedness = floodednessNoiseValue - fullyFloodedThreshold;
        }

        if (fullyFloodedness > 0.0) {
            return globalFluid.fluidLevel;
        }
        if (partiallyFloodedness > 0.0) {
            return this.computeRandomizedFluidSurfaceLevel(x, y, z, lowestPreliminarySurface);
        }
        return WAY_BELOW_MIN_Y;
    }

    private int computeRandomizedFluidSurfaceLevel(int x, int y, int z, int lowestPreliminarySurface) {
        int fluidLevelCellX = Math.floorDiv(x, 16);
        int fluidLevelCellY = Math.floorDiv(y, 40);
        int fluidLevelCellZ = Math.floorDiv(z, 16);
        int fluidCellMiddleY = fluidLevelCellY * 40 + 20;
        double fluidLevelSpread = this.fluidLevelSpreadNoise
                .compute(this.cachedPointContext.set(fluidLevelCellX, fluidLevelCellY, fluidLevelCellZ))
                * 10.0;
        int fluidLevelSpreadQuantized = quantize(fluidLevelSpread, 3);
        int targetFluidSurfaceLevel = fluidCellMiddleY + fluidLevelSpreadQuantized;
        return Math.min(lowestPreliminarySurface, targetFluidSurfaceLevel);
    }

    private BlockState computeFluidType(int x, int y, int z, FluidStatus globalFluid, int fluidSurfaceLevel) {
        BlockState fluidType = globalFluid.fluidType;
        if (fluidSurfaceLevel <= -10 && fluidSurfaceLevel != WAY_BELOW_MIN_Y && !isLava(globalFluid.fluidType)) {
            int fluidTypeCellX = Math.floorDiv(x, 64);
            int fluidTypeCellY = Math.floorDiv(y, 40);
            int fluidTypeCellZ = Math.floorDiv(z, 64);
            double lavaNoiseValue = this.lavaNoise.compute(this.cachedPointContext.set(fluidTypeCellX, fluidTypeCellY, fluidTypeCellZ));
            if (Math.abs(lavaNoiseValue) > 0.3) {
                fluidType = BlockLava.PROPERTIES.getDefaultState();
            }
        }
        return fluidType;
    }

    private int preliminarySurfaceLevel(int worldX, int worldZ) {
        long key = (((long) worldX) << 32) ^ (worldZ & 0xFFFFFFFFL);
        Integer cached = this.preliminarySurfaceLevelCache.get(key);
        if (cached != null) {
            return cached;
        }

        int lowerY = Math.max(this.minY, this.preliminarySurfaceLowerBound);
        int upperY = (int) Math.floor(this.preliminarySurfaceUpperBound.compute(this.cachedPointContext.set(worldX, 0, worldZ)));
        upperY = Math.min(this.maxY, Math.floorDiv(upperY, this.preliminarySurfaceCellHeight) * this.preliminarySurfaceCellHeight);

        int result = lowerY;
        if (upperY > lowerY) {
            for (int y = upperY; y >= lowerY; y -= this.preliminarySurfaceCellHeight) {
                if (this.preliminarySurfaceDensity.compute(this.cachedPointContext.set(worldX, y, worldZ)) > 0.0d) {
                    result = y;
                    break;
                }
            }
        }
        this.preliminarySurfaceLevelCache.put(key, result);
        return result;
    }

    private int maxPreliminarySurfaceLevel(int minX, int minZ, int maxX, int maxZ) {
        int maxSurface = Integer.MIN_VALUE;
        for (int x = minX; x <= maxX; x += 4) {
            for (int z = minZ; z <= maxZ; z += 4) {
                maxSurface = Math.max(maxSurface, preliminarySurfaceLevel(x, z));
            }
        }
        return maxSurface == Integer.MIN_VALUE ? 63 : maxSurface;
    }

    private static int gridX(int blockCoord) {
        return blockCoord >> 4;
    }

    private static int fromGridX(int gridCoord, int blockOffset) {
        return (gridCoord << 4) + blockOffset;
    }

    private static int gridY(int blockCoord) {
        return Math.floorDiv(blockCoord, 12);
    }

    private static int fromGridY(int gridCoord, int blockOffset) {
        return gridCoord * 12 + blockOffset;
    }

    private static int gridZ(int blockCoord) {
        return blockCoord >> 4;
    }

    private static int fromGridZ(int gridCoord, int blockOffset) {
        return (gridCoord << 4) + blockOffset;
    }

    private static long pack(int x, int y, int z) {
        return ((long) (x & 0x3FFFFFF) << 38) | ((long) (z & 0x3FFFFFF) << 12) | (long) (y & 0xFFF);
    }

    private static int unpackX(long packed) {
        return (int) (packed << 0 >> 38);
    }

    private static int unpackY(long packed) {
        return (int) (packed << 52 >> 52);
    }

    private static int unpackZ(long packed) {
        return (int) (packed << 26 >> 38);
    }

    private static boolean isDeepDarkRegion(DensityFunction erosion, DensityFunction depth, DensityFunction.FunctionContext context) {
        return erosion.compute(context) < -0.225 && depth.compute(context) > 0.9;
    }

    static double similarity(int distanceSqr1, int distanceSqr2) {
        return 1.0 - (distanceSqr2 - distanceSqr1) / 25.0;
    }

    static double clampedMap(double value, double inMin, double inMax, double outMin, double outMax) {
        if (inMin == inMax) {
            return value < inMin ? outMin : outMax;
        }
        double t = NukkitMath.clamp((value - inMin) / (inMax - inMin), 0.0, 1.0);
        return outMin + (outMax - outMin) * t;
    }

    static double map(double value, double inMin, double inMax, double outMin, double outMax) {
        if (inMin == inMax) {
            return outMin;
        }
        double t = (value - inMin) / (inMax - inMin);
        return outMin + (outMax - outMin) * t;
    }

    static int quantize(double value, int step) {
        return (int) Math.floor(value / step) * step;
    }

    static long mixSeed(long seed, int x, int y, int z) {
        long mixed = seed;
        mixed ^= (long) x * 341873128712L;
        mixed ^= (long) y * 132897987541L;
        mixed ^= (long) z * 42317861L;
        mixed ^= (mixed >>> 33);
        mixed *= 0xff51afd7ed558ccdL;
        mixed ^= (mixed >>> 33);
        mixed *= 0xc4ceb9fe1a85ec53L;
        mixed ^= (mixed >>> 33);
        return mixed;
    }

    static boolean isLava(BlockState state) {
        return state.getIdentifier().equals(BlockLava.PROPERTIES.getIdentifier());
    }

    static boolean isWater(BlockState state) {
        return state.getIdentifier().equals(BlockWater.PROPERTIES.getIdentifier());
    }

    static boolean isAir(BlockState state) {
        return state == BlockAir.STATE || state.getIdentifier().equals(BlockAir.PROPERTIES.getIdentifier());
    }

    public interface FluidPicker {
        FluidStatus computeFluid(int blockX, int blockY, int blockZ);
    }

    public record FluidStatus(int fluidLevel, BlockState fluidType) {
        BlockState at(int blockY) {
            return blockY < this.fluidLevel ? this.fluidType : BlockAir.STATE;
        }
    }

    private static final class CachedPointContext implements DensityCommon.ChunkCacheContext {
        private final DensityCommon.ChunkCache chunkCache;
        private int blockX;
        private int blockY;
        private int blockZ;

        private CachedPointContext(DensityCommon.ChunkCache chunkCache) {
            this.chunkCache = chunkCache;
        }

        private CachedPointContext set(int blockX, int blockY, int blockZ) {
            this.blockX = blockX;
            this.blockY = blockY;
            this.blockZ = blockZ;
            return this;
        }

        @Override
        public int blockX() {
            return this.blockX;
        }

        @Override
        public int blockY() {
            return this.blockY;
        }

        @Override
        public int blockZ() {
            return this.blockZ;
        }

        @Override
        public DensityCommon.ChunkCache densityChunkCache() {
            return this.chunkCache;
        }
    }
}
