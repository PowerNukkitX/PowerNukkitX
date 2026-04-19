package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.*;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateFeature;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.random.NukkitRandom;

import static cn.nukkit.block.property.CommonBlockProperties.MINECRAFT_BLOCK_FACE;

public class AmethystGeodeFeature extends GenerateFeature {

    protected static final BlockState SMOOTH_BASALT = BlockSmoothBasalt.PROPERTIES.getDefaultState();
    protected static final BlockState CALCITE = BlockCalcite.PROPERTIES.getDefaultState();
    protected static final BlockState AMETHYST_BLOCK = BlockAmethystBlock.PROPERTIES.getDefaultState();
    protected static final BlockState AMETHYST_BUDDING = BlockBuddingAmethyst.PROPERTIES.getDefaultState();
    protected static final BlockProperties AMETHYST_CLUSTER = BlockAmethystCluster.PROPERTIES;

    public static final String NAME = "minecraft:overworld_amethyst_geode_feature";

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        Level level = chunk.getLevel();
        this.random.setSeed(level.getSeed() ^ (((long) chunk.getX() << 8) ^ chunk.getZ()));
        BlockManager object = new BlockManager(level);
        // Vanilla: each chunk has a 1/24 chance to attempt a geode.
        if (random.nextBoundedInt(24) != 0) {
            return;
        }

        int centerX = random.nextBoundedInt(16);
        int centerZ = random.nextBoundedInt(16);

        int outerRadius = 4 + random.nextBoundedInt(5);
        int calciteThickness = 1;
        int amethystThickness = 1;
        double outerRadiusSq = outerRadius * outerRadius;
        boolean hasCrack = random.nextBoundedInt(100) < 95;
        double crackAngle = random.nextBoundedInt(360) * Math.PI / 180.0;
        double crackDirX = Math.cos(crackAngle);
        double crackDirZ = Math.sin(crackAngle);
        double crackHalfWidth = 1.5;
        double crackHalfHeight = 2.0;

        int geodeMinY = Math.max(level.getMinHeight(), -58);
        int geodeMaxY = Math.min(level.getMaxHeight() - 1, 30);
        int minCenterY = geodeMinY + outerRadius;
        int maxCenterY = geodeMaxY - outerRadius;
        if (minCenterY > maxCenterY) {
            return;
        }
        int centerY = minCenterY + random.nextBoundedInt(maxCenterY - minCenterY + 1);

        int minX = centerX - outerRadius;
        int maxX = centerX + outerRadius;
        int minZ = centerZ - outerRadius;
        int maxZ = centerZ + outerRadius;
        int minY = centerY - outerRadius;
        int maxY = centerY + outerRadius;
        for (int lx = minX; lx <= maxX; lx++) {
            for (int lz = minZ; lz <= maxZ; lz++) {
                for (int y = minY; y <= maxY; y++) {
                    int worldX = (chunk.getX() << 4) + lx;
                    int worldZ = (chunk.getZ() << 4) + lz;

                    double dx = lx - centerX + 0.5;
                    double dz = lz - centerZ + 0.5;
                    double dy = y - centerY + 0.5;
                    double distSq = dx * dx + dy * dy + dz * dz;

                    if (distSq > outerRadiusSq) continue;

                    if (hasCrack) {
                        double along = dx * crackDirX + dz * crackDirZ;
                        double side = Math.abs(dx * -crackDirZ + dz * crackDirX);
                        if (along > 0.0 && along <= outerRadius + 1.0 && side <= crackHalfWidth && Math.abs(dy) <= crackHalfHeight) {
                            object.setBlockStateAt(worldX, y, worldZ, BlockAir.STATE);
                            continue;
                        }
                    }

                    double dist = Math.sqrt(distSq);
                    double distFromOuter = outerRadius - dist;

                    if (distFromOuter <= 0.5) {
                        object.setBlockStateAt(worldX, y, worldZ, SMOOTH_BASALT);
                    } else if (distFromOuter <= 0.5 + calciteThickness) {
                        object.setBlockStateAt(worldX, y, worldZ, CALCITE);
                    } else if (distFromOuter <= 0.5 + calciteThickness + amethystThickness) {
                        object.setBlockStateAt(worldX, y, worldZ, AMETHYST_BLOCK);
                        // Vanilla: 8.3% of inner layer becomes budding amethyst.
                        if (random.nextBoundedInt(1000) < 83) {
                            object.setBlockStateAt(worldX, y, worldZ, AMETHYST_BUDDING);
                        }
                    } else {
                        object.setBlockStateAt(worldX, y, worldZ, BlockAir.STATE);

                        BlockFace touchesAmethystShell = null;
                        if (isAmethystShell(object, worldX + 1, y, worldZ)) touchesAmethystShell = BlockFace.WEST;
                        if (isAmethystShell(object, worldX - 1, y, worldZ)) touchesAmethystShell = BlockFace.EAST;
                        if (isAmethystShell(object, worldX, y + 1, worldZ)) touchesAmethystShell = BlockFace.DOWN;
                        if (isAmethystShell(object, worldX, y - 1, worldZ)) touchesAmethystShell = BlockFace.UP;
                        if (isAmethystShell(object, worldX, y, worldZ + 1)) touchesAmethystShell = BlockFace.NORTH;
                        if (isAmethystShell(object, worldX, y, worldZ - 1)) touchesAmethystShell = BlockFace.SOUTH;

                        if (touchesAmethystShell != null) {
                            if (random.nextBoundedInt(100) < 18) {
                                object.setBlockStateAt(worldX, y, worldZ, AMETHYST_CLUSTER.getBlockState(MINECRAFT_BLOCK_FACE.createValue(touchesAmethystShell)));
                            }
                        }
                    }
                }
            }
        }
        queueObject(chunk, object);
    }

    private boolean isAmethystShell(BlockManager level, int x, int y, int z) {
        if (y < level.getMinHeight() || y >= level.getMaxHeight()) return false;

        BlockState state = level.getBlockIfCachedOrLoaded(x, y, z).getBlockState();
        return state.equals(AMETHYST_BLOCK) || state.equals(AMETHYST_BUDDING);
    }

    @Override
    public String name() {
        return NAME;
    }
}
