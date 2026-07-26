package org.powernukkitx.level.generator.feature.decoration;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockGlowLichen;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockLiquid;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.feature.CountGenerateFeature;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.utils.random.RandomSourceProvider;


/**
 * @author Buddelbubi
 * @since 2026/05/10
 * @see <a href="https://github.com/misode/mcmeta/blob/data/data/minecraft/worldgen/placed_feature/glow_lichen.json">Source</a>
 */
public class GlowLichenFeature extends CountGenerateFeature {

    public static final String NAME = "minecraft:underground_glow_lichen_feature";

    private static final int MAX_ABSOLUTE_Y = 256;
    private static final int MAX_SURFACE_RELATIVE_Y = -13;

    @Override
    public int getBase() {
        return 52;
    }

    @Override
    public int getRandom() {
        return 26;
    }

    @Override
    public void populate(ChunkGenerateContext context, RandomSourceProvider random) {
        IChunk chunk = context.getChunk();
        Level level = chunk.getLevel();
        int minY = level.getMinHeight();
        int maxY = Math.min(MAX_ABSOLUTE_Y, level.getMaxHeight() - 1);
        if (minY > maxY) {
            return;
        }

        BlockManager manager = new BlockManager(level);
        int x = (chunk.getX() << 4) + random.nextInt(14) + 1;
        int z = (chunk.getZ() << 4) + random.nextInt(14) + 1;
        int y = random.nextInt(minY, maxY);

        if (y > getOceanFloorHeight(manager, x, z, minY, maxY) + MAX_SURFACE_RELATIVE_Y) {
            return;
        }

        BlockFace face = placeAt(manager, x, y, z, random, null);
        if (face == null) {
            return;
        }

        int placed = 1;
        int target = 1 + random.nextInt(3);
        int currentX = x;
        int currentY = y;
        int currentZ = z;
        BlockFace currentFace = face;
        for (int attempts = 0; attempts < 8 && placed < target; attempts++) {
            BlockFace[] edges = currentFace.getEdges().toArray(new BlockFace[0]);
            BlockFace edge = edges[random.nextInt(edges.length - 1)];
            int nextX = currentX + edge.getXOffset();
            int nextY = currentY + edge.getYOffset();
            int nextZ = currentZ + edge.getZOffset();
            BlockFace nextFace = placeAt(manager, nextX, nextY, nextZ, random, currentFace);
            if (nextFace == null) {
                continue;
            }
            placed++;
            currentX = nextX;
            currentY = nextY;
            currentZ = nextZ;
            currentFace = nextFace;
        }

        queueObject(chunk, manager);
    }

    private int getOceanFloorHeight(BlockManager manager, int x, int z, int minY, int maxY) {
        for (int y = maxY; y >= minY; y--) {
            Block block = manager.getBlockIfCachedOrLoaded(x, y, z);
            if (!block.isAir() && !(block instanceof BlockLiquid)) {
                return y;
            }
        }
        return minY;
    }

    private BlockFace placeAt(BlockManager manager, int x, int y, int z, RandomSourceProvider random, BlockFace preferredFace) {
        Block block = manager.getBlockIfCachedOrLoaded(x, y, z);
        if (!block.isAir() && !block.getId().equals(BlockID.GLOW_LICHEN)) {
            return null;
        }

        int currentBits = 0;
        if (block instanceof BlockGlowLichen) {
            currentBits = block.getPropertyValue(CommonBlockProperties.MULTI_FACE_DIRECTION_BITS);
        }

        BlockFace[] faces = BlockFace.values();
        int startIndex = preferredFace == null ? random.nextInt(faces.length - 1) : preferredFace.getIndex();
        for (int i = 0; i < faces.length; i++) {
            BlockFace face = faces[(startIndex + i) % faces.length];
            Block support = manager.getBlockIfCachedOrLoaded(
                    x + face.getXOffset(),
                    y + face.getYOffset(),
                    z + face.getZOffset()
            );
            if (!support.isSolid(face.getOpposite())) {
                continue;
            }
            int bit = 1 << face.getDUSWNEIndex();
            if ((currentBits & bit) != 0) {
                continue;
            }
            BlockState state = BlockGlowLichen.PROPERTIES.getBlockState(
                    CommonBlockProperties.MULTI_FACE_DIRECTION_BITS.createValue(currentBits | bit)
            );
            manager.setBlockStateAt(x, y, z, state);
            return face;
        }
        return null;
    }

    @Override
    public String name() {
        return NAME;
    }
}
