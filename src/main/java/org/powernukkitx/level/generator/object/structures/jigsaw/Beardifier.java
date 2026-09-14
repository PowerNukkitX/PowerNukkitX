package org.powernukkitx.level.generator.object.structures.jigsaw;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.generator.object.structures.StructureHelper;
import org.powernukkitx.level.generator.object.structures.utils.BoundingBox;
import org.powernukkitx.math.BlockVector3;

import java.util.List;

/**
 * Shared density calculation used to blend structure bounding boxes into the
 * surrounding terrain.
 */
public final class Beardifier {

    public static final int KERNEL_RADIUS = 12;

    private static final int KERNEL_SIZE = KERNEL_RADIUS * 2;
    private static final double[] KERNEL = createKernel();

    private Beardifier() {
    }

    /**
     * Calculates the beard density contribution for a position relative to a
     * structure bounding box.
     *
     * @param dx horizontal distance from the bounding box on the x-axis
     * @param dy vertical distance from the bounding box
     * @param dz horizontal distance from the bounding box on the z axis
     * @param yToGround vertical offset from the bottom of the bounding box
     * @return the density contribution, or {@code 0} outside the kernel
     */
    public static double getContribution(int dx, int dy, int dz, int yToGround) {
        int xi = dx + KERNEL_RADIUS;
        int yi = dy + KERNEL_RADIUS;
        int zi = dz + KERNEL_RADIUS;
        if (!isInKernelRange(xi) || !isInKernelRange(yi) || !isInKernelRange(zi)) {
            return 0.0;
        }

        double dyWithOffset = yToGround + 0.5;
        double distanceSqr = dx * (double) dx + dyWithOffset * dyWithOffset + dz * (double) dz;
        double value = -dyWithOffset / Math.sqrt(distanceSqr / 2.0) / 2.0;
        return value * KERNEL[zi * KERNEL_SIZE * KERNEL_SIZE + xi * KERNEL_SIZE + yi];
    }

    /**
     * Traverses the complete beard kernel around structure boxes. Callers only
     * decide what a density contribution does to a block.
     */
    public static void apply(StructureHelper helper, List<TerrainAdaptationPiece> pieces,
                             ColumnProcessor processor) {
        BlockVector3 origin = helper.getOrigin();
        int minHeight = helper.getMinHeight();
        int maxHeight = helper.getMaxHeight() - 1;

        for (TerrainAdaptationPiece piece : pieces) {
            BoundingBox box = piece.boundingBox().moved(origin.getX(), origin.getY(), origin.getZ());
            int groundY = origin.getY() + piece.groundY();
            int minY = Math.max(minHeight, groundY - KERNEL_RADIUS);
            int maxY = Math.min(maxHeight, groundY + KERNEL_RADIUS);

            for (int x = box.x0 - KERNEL_RADIUS; x <= box.x1 + KERNEL_RADIUS; x++) {
                for (int z = box.z0 - KERNEL_RADIUS; z <= box.z1 + KERNEL_RADIUS; z++) {
                    int dx = distanceToRange(x, box.x0, box.x1);
                    int dz = distanceToRange(z, box.z0, box.z1);
                    if (dx >= KERNEL_RADIUS || dz >= KERNEL_RADIUS) {
                        continue;
                    }

                    processor.beginColumn(helper, box, x, z);
                    for (int y = minY; y <= maxY; y++) {
                        if (helper.isCached(new BlockVector3(x, y, z))) {
                            continue;
                        }

                        int dy = Math.abs(y - groundY);
                        if (dy >= KERNEL_RADIUS) {
                            continue;
                        }

                        double contribution = getContribution(dx, dy, dz, y - groundY) * 0.8;
                        processor.process(helper, box, x, y, z, contribution);
                    }
                    processor.endColumn(helper, box, x, z);
                }
            }
        }
    }

    /**
     * Terrain-adaptation data in coordinates relative to the structure helper.
     * The density kernel is centered at {@code groundY}; its highest positive
     * fill contribution is therefore one block below that level.
     */
    public record TerrainAdaptationPiece(BoundingBox boundingBox, int groundY) {

        public static TerrainAdaptationPiece atBoundingBoxFloor(BoundingBox boundingBox) {
            return new TerrainAdaptationPiece(boundingBox, boundingBox.y0);
        }
    }

    public interface ColumnProcessor {
        default void beginColumn(StructureHelper helper, BoundingBox box, int x, int z) {
        }

        void process(StructureHelper helper, BoundingBox box, int x, int y, int z, double contribution);

        default void endColumn(StructureHelper helper, BoundingBox box, int x, int z) {
        }
    }

    public static ColumnProcessor surface(BlockState top, BlockState mid, double threshold) {
        return surface((level, x, y, z) -> new SurfaceMaterials(top, mid), threshold);
    }

    public static ColumnProcessor surface(SurfaceProvider provider, double threshold) {
        return new ColumnProcessor() {
            private SurfaceMaterials materials;
            private int highestFilledY;

            @Override
            public void beginColumn(StructureHelper helper, BoundingBox box, int x, int z) {
                materials = provider.get(helper.getLevel(), x, box.y0, z);
                highestFilledY = Integer.MIN_VALUE;
            }

            @Override
            public void process(StructureHelper helper, BoundingBox box, int x, int y, int z,
                                double contribution) {
                if (materials == null || contribution <= threshold) return;
                Block current = helper.getLevel().getBlock(x, y, z);
                if (current.canBeReplaced() || !current.isSolid()) {
                    setAbsolute(helper, x, y, z, materials.mid());
                    highestFilledY = Math.max(highestFilledY, y);
                }
            }

            @Override
            public void endColumn(StructureHelper helper, BoundingBox box, int x, int z) {
                if (materials != null && highestFilledY != Integer.MIN_VALUE) {
                    setAbsolute(helper, x, highestFilledY, z, materials.top());
                }
            }
        };
    }

    public static ColumnProcessor carveAndFill(BlockState carve, double carveThreshold,
                                               BlockState fill, double fillThreshold) {
        return (helper, box, x, y, z, contribution) -> {
            if (contribution < carveThreshold) {
                setAbsolute(helper, x, y, z, carve);
            } else if (contribution > fillThreshold) {
                Block current = helper.getLevel().getBlock(x, y, z);
                if (current.canBeReplaced() || !current.isSolid()) {
                    setAbsolute(helper, x, y, z, fill);
                }
            }
        };
    }

    private static void setAbsolute(StructureHelper helper, int x, int y, int z, BlockState state) {
        BlockVector3 origin = helper.getOrigin();
        helper.setBlockStateAt(x - origin.getX(), y - origin.getY(), z - origin.getZ(), state);
    }

    @FunctionalInterface
    public interface SurfaceProvider {
        SurfaceMaterials get(Level level, int x, int y, int z);
    }

    public record SurfaceMaterials(BlockState top, BlockState mid) {
    }

    private static int distanceToRange(int value, int min, int max) {
        return Math.max(0, Math.max(min - value, value - max));
    }

    private static boolean isInKernelRange(int index) {
        return index >= 0 && index < KERNEL_SIZE;
    }

    private static double[] createKernel() {
        double[] kernel = new double[KERNEL_SIZE * KERNEL_SIZE * KERNEL_SIZE];
        for (int zi = 0; zi < KERNEL_SIZE; zi++) {
            for (int xi = 0; xi < KERNEL_SIZE; xi++) {
                for (int yi = 0; yi < KERNEL_SIZE; yi++) {
                    int dx = xi - KERNEL_RADIUS;
                    double dy = yi - KERNEL_RADIUS + 0.5;
                    int dz = zi - KERNEL_RADIUS;
                    double distanceSqr = dx * (double) dx + dy * dy + dz * (double) dz;
                    kernel[zi * KERNEL_SIZE * KERNEL_SIZE + xi * KERNEL_SIZE + yi] = Math.exp(-distanceSqr / 16.0);
                }
            }
        }
        return kernel;
    }
}
