package cn.nukkit.level.generator.feature.ore;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDeepslate;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockStone;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateFeature;
import cn.nukkit.level.generator.Normal;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.math.MathHelper;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.random.NukkitRandom;

public abstract class OreGeneratorFeature extends GenerateFeature {

    protected static final BlockState STONE = BlockStone.PROPERTIES.getDefaultState();
    protected static final BlockState DEEPSLATE = BlockDeepslate.PROPERTIES.getDefaultState();

    public abstract BlockState getState(BlockState original);
    public abstract int getClusterCount();
    public abstract int getClusterSize();
    public abstract int getMinHeight();
    public abstract int getMaxHeight();

    public float getSkipAir() {
        return 0;
    }

    public ConcentrationType getConcentration() {
        return ConcentrationType.UNIFORM;
    }

    public boolean isRare() {
        return false;
    }

    public boolean canBeReplaced(BlockState state) {
        return state == STONE || state == DEEPSLATE;
    }

    @Override
    public final void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        NukkitRandom random = new NukkitRandom(Level.chunkHash(chunkX, chunkZ) * level.getSeed());
        int sx = chunkX << 4;
        int ex = sx + 15;
        int sz = chunkZ << 4;
        int ez = sz + 15;
        BlockManager manager = new BlockManager(level);
        for (int i = 0; i < (this.isRare() ? (random.identical().nextInt(getClusterCount()) == 0 ? 1 : 0) : getClusterCount()); i++) {
            BlockManager object = new BlockManager(level);
            int maxY = Math.min(this.getMaxHeight(), level.getMaxHeight());
            int minY = Math.max(this.getMinHeight(), level.getMinHeight());
            int x = NukkitMath.randomRange(random, sx, ex);
            int z = NukkitMath.randomRange(random, sz, ez);
            int y = switch (getConcentration()) {
                case TRIANGLE -> NukkitMath.randomRangeTriangle(random, minY, maxY);
                default -> minY + random.nextBoundedInt((maxY - minY) + 1);
            };

            BlockState original = level.getBlockStateAt(x, y, z);
            if (!canBeReplaced(original)) {
                continue;
            }
            if (this.getClusterSize() == 1) {
                object.setBlockStateAt(x, y, z, getState(original));
            } else {
                spawn(object, new NukkitRandom(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ) ^ x + y + z), x, y, z);
            }
            boolean skip = false;
            if(getSkipAir() != 0) {
                boolean air = object.getBlocks().stream().anyMatch(block -> level.getBlock(block).isAir());
                if(air) {
                    skip = random.identical().nextFloat() < getSkipAir();
                }
            }
            if(!skip) {
                for (Block block : object.getBlocks()) {
                    if (block.getChunk().isGenerated()) {
                        manager.setBlockStateAt(block.asBlockVector3(), block.getBlockState());
                    } else {
                        IChunk nextChunk = block.getChunk();
                        long chunkHash = Level.chunkHash(nextChunk.getX(), nextChunk.getZ());
                        ((Normal) context.getGenerator()).getChunkPlacementQueue(chunkHash).setBlockStateAt(block.asBlockVector3(), block.getBlockState());
                    }
                }
            }
        }
        manager.applySubChunkUpdate();
    }

    protected void spawn(BlockManager level, NukkitRandom rand, int x, int y, int z) {

        float piScaled = rand.nextFloat() * (float) Math.PI;
        double scaleMaxX = (float) (x + 8) + MathHelper.sin(piScaled) * (float) getClusterSize() / 8.0F;
        double scaleMinX = (float) (x + 8) - MathHelper.sin(piScaled) * (float) getClusterSize() / 8.0F;
        double scaleMaxZ = (float) (z + 8) + MathHelper.cos(piScaled) * (float) getClusterSize() / 8.0F;
        double scaleMinZ = (float) (z + 8) - MathHelper.cos(piScaled) * (float) getClusterSize() / 8.0F;
        double scaleMaxY = y + rand.nextBoundedInt(3) - 2;
        double scaleMinY = y + rand.nextBoundedInt(3) - 2;

        for (int i = 0; i < getClusterSize(); ++i) {
            float sizeIncr = (float) i / (float) getClusterSize();
            double scaleX = scaleMaxX + (scaleMinX - scaleMaxX) * (double) sizeIncr;
            double scaleY = scaleMaxY + (scaleMinY - scaleMaxY) * (double) sizeIncr;
            double scaleZ = scaleMaxZ + (scaleMinZ - scaleMaxZ) * (double) sizeIncr;
            double randSizeOffset = rand.nextDouble() * (double) getClusterSize() / 16.0D;
            double randVec1 = (double) (MathHelper.sin((float) Math.PI * sizeIncr) + 1.0F) * randSizeOffset + 1.0D;
            double randVec2 = (double) (MathHelper.sin((float) Math.PI * sizeIncr) + 1.0F) * randSizeOffset + 1.0D;
            int minX = MathHelper.floor(scaleX - randVec1 / 2.0D);
            int minY = MathHelper.floor(scaleY - randVec2 / 2.0D);
            int minZ = MathHelper.floor(scaleZ - randVec1 / 2.0D);
            int maxX = MathHelper.floor(scaleX + randVec1 / 2.0D);
            int maxY = MathHelper.floor(scaleY + randVec2 / 2.0D);
            int maxZ = MathHelper.floor(scaleZ + randVec1 / 2.0D);

            for (int xSeg = minX; xSeg <= maxX; ++xSeg) {
                double xVal = ((double) xSeg + 0.5D - scaleX) / (randVec1 / 2.0D);

                if (xVal * xVal < 1.0D) {
                    for (int ySeg = minY; ySeg <= maxY; ++ySeg) {
                        double yVal = ((double) ySeg + 0.5D - scaleY) / (randVec2 / 2.0D);

                        if (xVal * xVal + yVal * yVal < 1.0D) {
                            for (int zSeg = minZ; zSeg <= maxZ; ++zSeg) {
                                double zVal = ((double) zSeg + 0.5D - scaleZ) / (randVec1 / 2.0D);

                                if (xVal * xVal + yVal * yVal + zVal * zVal < 1.0D) {
                                    BlockState original = level.getLevel().getBlockStateAt(xSeg, ySeg, zSeg);
                                    if(canBeReplaced(original)) {
                                        level.setBlockStateAt(xSeg, ySeg, zSeg, getState(original));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public enum ConcentrationType {
        UNIFORM,
        TRIANGLE
    }

}
