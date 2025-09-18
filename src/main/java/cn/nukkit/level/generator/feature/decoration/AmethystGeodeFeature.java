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
        this.random.setSeed(level.getSeed() ^ ((chunk.getX() << 8) ^ chunk.getZ()));
        BlockManager object = new BlockManager(level);
        if (random.nextBoundedInt(100) > 4) {
            return;
        }

        int centerX = random.nextBoundedInt(16);
        int centerZ = random.nextBoundedInt(16);
        int centerY = 12 + random.nextBoundedInt(36);

        int outerRadius = 4 + random.nextBoundedInt(5);
        int calciteThickness = 1;
        int amethystThickness = 1;
        double outerRadiusSq = outerRadius * outerRadius;

        int minX = centerX - outerRadius;
        int maxX = centerX + outerRadius;
        int minZ = centerZ - outerRadius;
        int maxZ = centerZ + outerRadius;
        int minY = Math.max(-58, centerY - outerRadius);
        int maxY = Math.min(30, centerY + outerRadius);
        for (int lx = minX; lx <= maxX; lx++) {
            for (int lz = minZ; lz <= maxZ; lz++) {
                for (int y = minY; y <= maxY; y++) {
                    int worldX = (chunk.getX() << 4) + lx;
                    int worldZ = (chunk.getZ() << 4) + lz;
                    if(level.getBlock(worldX, y, worldZ) instanceof BlockFlowingWater) return;

                    double dx = lx - centerX + 0.5;
                    double dz = lz - centerZ + 0.5;
                    double dy = y - centerY + 0.5;
                    double distSq = dx * dx + dy * dy + dz * dz;

                    if (distSq > outerRadiusSq) continue;

                    double dist = Math.sqrt(distSq);
                    double distFromOuter = outerRadius - dist;

                    if (distFromOuter <= 0.5) {
                        object.setBlockStateAt(worldX, y, worldZ, SMOOTH_BASALT);
                    } else if (distFromOuter <= 0.5 + calciteThickness) {
                        object.setBlockStateAt(worldX, y, worldZ, CALCITE);
                    } else if (distFromOuter <= 0.5 + calciteThickness + amethystThickness) {
                        object.setBlockStateAt(worldX, y, worldZ, AMETHYST_BLOCK);
                        if (random.nextBoundedInt(100) < 6) {
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
        if (y < level.getMinHeight() || y > level.getMaxHeight()) return false;

        BlockState state = level.getBlockIfCachedOrLoaded(x, y, z).getBlockState();
        return state == AMETHYST_BLOCK || state == AMETHYST_BUDDING;
    }

    @Override
    public String name() {
        return NAME;
    }
}
