package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.BlockState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.random.RandomSourceProvider;

public class CoralClawFeature extends AbstractCoralFeature {
    public static final String NAME = "minecraft:coral_hang_feature";

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
        if (!placeCoralBlock(chunk, random, x, y, z, coralState)) {
            return false;
        }

        BlockFace clawDirection = BlockFace.Plane.HORIZONTAL.random(random);
        int branches = random.nextInt(2) + 2;
        BlockFace[] possible = new BlockFace[]{clawDirection, clawDirection.rotateY(), clawDirection.rotateYCCW()};

        for (int i = possible.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            BlockFace t = possible[i];
            possible[i] = possible[j];
            possible[j] = t;
        }

        for (int branch = 0; branch < branches; branch++) {
            BlockFace branchDirection = possible[branch];
            int px = x + branchDirection.getXOffset();
            int py = y;
            int pz = z + branchDirection.getZOffset();

            int sidewayLength = random.nextInt(2) + 1;
            int inwayLength;
            BlockFace segmentDirection;
            if (branchDirection == clawDirection) {
                segmentDirection = clawDirection;
                inwayLength = random.nextInt(3) + 2;
            } else {
                py++;
                segmentDirection = random.nextBoolean() ? branchDirection : BlockFace.UP;
                inwayLength = random.nextInt(3) + 3;
            }

            for (int i = 0; i < sidewayLength && placeCoralBlock(chunk, random, px, py, pz, coralState); i++) {
                px += segmentDirection.getXOffset();
                py += segmentDirection.getYOffset();
                pz += segmentDirection.getZOffset();
            }

            px -= segmentDirection.getXOffset();
            py -= segmentDirection.getYOffset();
            pz -= segmentDirection.getZOffset();
            py++;

            for (int i = 0; i < inwayLength; i++) {
                px += clawDirection.getXOffset();
                py += clawDirection.getYOffset();
                pz += clawDirection.getZOffset();

                if (!placeCoralBlock(chunk, random, px, py, pz, coralState)) {
                    break;
                }
                if (random.nextFloat() < 0.25f) {
                    py++;
                }
            }
        }

        return true;
    }
}
