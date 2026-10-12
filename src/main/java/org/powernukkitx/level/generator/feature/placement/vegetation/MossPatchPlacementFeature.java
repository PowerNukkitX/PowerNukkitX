package org.powernukkitx.level.generator.feature.placement.vegetation;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockMossBlock;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.generator.feature.placement.FeaturePlacementContext;
import org.powernukkitx.level.generator.feature.placement.PlacementFeature;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.BlockVector3;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.powernukkitx.block.BlockID.ANDESITE;
import static org.powernukkitx.block.BlockID.CAVE_VINES;
import static org.powernukkitx.block.BlockID.CAVE_VINES_BODY_WITH_BERRIES;
import static org.powernukkitx.block.BlockID.CAVE_VINES_HEAD_WITH_BERRIES;
import static org.powernukkitx.block.BlockID.COARSE_DIRT;
import static org.powernukkitx.block.BlockID.DEEPSLATE;
import static org.powernukkitx.block.BlockID.DIORITE;
import static org.powernukkitx.block.BlockID.DIRT;
import static org.powernukkitx.block.BlockID.DIRT_WITH_ROOTS;
import static org.powernukkitx.block.BlockID.GRANITE;
import static org.powernukkitx.block.BlockID.GRASS_BLOCK;
import static org.powernukkitx.block.BlockID.MOSS_BLOCK;
import static org.powernukkitx.block.BlockID.MYCELIUM;
import static org.powernukkitx.block.BlockID.PODZOL;
import static org.powernukkitx.block.BlockID.POLISHED_ANDESITE;
import static org.powernukkitx.block.BlockID.POLISHED_DIORITE;
import static org.powernukkitx.block.BlockID.POLISHED_GRANITE;
import static org.powernukkitx.block.BlockID.STONE;
import static org.powernukkitx.block.BlockID.TUFF;

/**
 * Native floor moss vegetation patch.
 *
 * @author Curse
 */
public final class MossPatchPlacementFeature implements PlacementFeature {
    private static final int VERTICAL_RANGE = 5;
    private static final float EDGE_COLUMN_CHANCE = 0.3f;
    private static final float VEGETATION_CHANCE = 0.8f;

    private static final BlockState MOSS = BlockMossBlock.PROPERTIES.getDefaultState();

    private static final Set<String> REPLACEABLE = Set.of(
            DIRT,
            COARSE_DIRT,
            PODZOL,
            DIRT_WITH_ROOTS,
            GRASS_BLOCK,
            MYCELIUM,
            STONE,
            GRANITE,
            POLISHED_GRANITE,
            DIORITE,
            POLISHED_DIORITE,
            ANDESITE,
            POLISHED_ANDESITE,
            DEEPSLATE,
            TUFF,
            MOSS_BLOCK,
            CAVE_VINES,
            CAVE_VINES_BODY_WITH_BERRIES,
            CAVE_VINES_HEAD_WITH_BERRIES
    );

    private static final PlacementFeature VEGETATION = MossVegetationPlacementFeature.feature();
    private static final MossPatchPlacementFeature INSTANCE = new MossPatchPlacementFeature();

    private MossPatchPlacementFeature() {
    }

    /**
     * Returns the native moss vegetation patch.
     */
    public static PlacementFeature feature() {
        return INSTANCE;
    }

    @Override
    public boolean place(FeaturePlacementContext context) {
        int originX = context.getX();
        int originY = context.getY();
        int originZ = context.getZ();

        int radiusX = 5 + context.getRandom().nextExclusiveInt(4);
        int radiusZ = 5 + context.getRandom().nextExclusiveInt(4);
        List<BlockVector3> groundPositions = new ArrayList<>();

        for (int dx = -radiusX; dx <= radiusX; dx++) {
            boolean xEdge = dx == -radiusX || dx == radiusX;

            for (int dz = -radiusZ; dz <= radiusZ; dz++) {
                boolean zEdge = dz == -radiusZ || dz == radiusZ;
                boolean corner = xEdge && zEdge;
                boolean edge = xEdge || zEdge;

                if (corner || edge && context.getRandom().nextFloat() > EDGE_COLUMN_CHANCE) {
                    continue;
                }

                int x = originX + dx;
                int y = originY;
                int z = originZ + dz;

                for (int step = 0; isAir(context, x, y, z) && step < VERTICAL_RANGE; step++) {
                    y--;
                }

                for (int step = 0; !isAir(context, x, y, z) && step < VERTICAL_RANGE; step++) {
                    y++;
                }

                if (y <= context.getGenerationContext().getLevel().getMinHeight()
                        || y >= context.getGenerationContext().getLevel().getMaxHeight()
                        || !isAir(context, x, y, z)) {
                    continue;
                }

                int groundY = y - 1;
                Block ground = context.getRoot().getBlockIfCachedOrLoaded(x, groundY, z);
                if (!ground.isSideFull(BlockFace.UP) || !placeGround(context, x, groundY, z)) {
                    continue;
                }

                groundPositions.add(new BlockVector3(x, groundY, z));
            }
        }

        if (groundPositions.size() == 0) {
            context.setPosition(originX, originY, originZ);
            return false;
        }

        for (BlockVector3 ground : groundPositions) {
            if (context.getRandom().nextFloat() < VEGETATION_CHANCE) {
                context.setPosition(ground.getX(), ground.getY() + 1, ground.getZ());
                VEGETATION.place(context);
            }
        }

        BlockVector3 output = groundPositions.get(groundPositions.size() - 1);
        context.setPosition(output.getX(), output.getY(), output.getZ());
        return true;
    }

    private static boolean placeGround(FeaturePlacementContext context, int x, int y, int z) {
        BlockState state = context.getRoot().getBlockStateIfCachedOrLoaded(x, y, z);
        String identifier = state.getIdentifier();

        if (MOSS_BLOCK.equals(identifier)) {
            return true;
        }
        if (!REPLACEABLE.contains(identifier)) {
            return false;
        }

        context.getRoot().setBlockStateAtUnchecked(x, y, z, MOSS);
        return true;
    }

    private static boolean isAir(FeaturePlacementContext context, int x, int y, int z) {
        return context.getRoot().getBlockStateIfCachedOrLoaded(x, y, z) == BlockAir.STATE;
    }
}
