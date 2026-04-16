package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.BlockState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.random.RandomSourceProvider;

public class CoralTreeFeature extends AbstractCoralFeature {
    public static final String NAME = "minecraft:coral_tree_feature";

    @Override
    public int getBase() {
        return 1;
    }

    @Override
    public int getRandom() {
        return 0;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    protected boolean placeFeature(IChunk chunk, RandomSourceProvider random, int x, int y, int z, BlockState coralState) {
        int py = y;
        int trunkHeight = random.nextInt(3) + 1;

        for (int i = 0; i < trunkHeight; i++) {
            if (!placeCoralBlock(chunk, random, x, py, z, coralState)) {
                return true;
            }
            py++;
        }

        int topY = py;
        int branches = random.nextInt(3) + 2;
        BlockFace[] directions = BlockFace.getHorizontals();
        for (int i = directions.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            BlockFace t = directions[i];
            directions[i] = directions[j];
            directions[j] = t;
        }

        for (int i = 0; i < branches; i++) {
            BlockFace direction = directions[i];
            int px = x + direction.getXOffset();
            int pz = z + direction.getZOffset();
            int branchHeight = random.nextInt(5) + 2;
            int segmentLength = 0;
            int by = topY;

            for (int j = 0; j < branchHeight && placeCoralBlock(chunk, random, px, by, pz, coralState); j++) {
                segmentLength++;
                by++;
                if (j == 0 || (segmentLength >= 2 && random.nextFloat() < 0.25f)) {
                    px += direction.getXOffset();
                    pz += direction.getZOffset();
                    segmentLength = 0;
                }
            }
        }

        return true;
    }
}
