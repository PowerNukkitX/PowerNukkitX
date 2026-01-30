package cn.nukkit.level.generator.object;

import cn.nukkit.block.*;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.tags.BlockTags;
import cn.nukkit.utils.random.RandomSourceProvider;

import static cn.nukkit.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;

public class ObjectSwampOakTree extends TreeGenerator {

    private final int minTreeHeight;
    private final int maxTreeHeight;

    private final BlockState metaWood = BlockOakLog.PROPERTIES.getBlockState(
            CommonBlockProperties.PILLAR_AXIS.createValue(BlockFace.Axis.Y)
    );
    private final BlockState metaLeaves = BlockOakLeaves.PROPERTIES.getDefaultState();

    public ObjectSwampOakTree(int minTreeHeight, int maxTreeHeight) {
        this.minTreeHeight = minTreeHeight;
        this.maxTreeHeight = maxTreeHeight;
    }

    @Override
    public boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 vectorPosition) {
        BlockVector3 position = new BlockVector3(vectorPosition.getFloorX(), vectorPosition.getFloorY(), vectorPosition.getFloorZ());

        int height = rand.nextInt(maxTreeHeight - minTreeHeight + 1) + minTreeHeight;

        if (position.getY() < level.getMinHeight() || position.getY() + height + 1 >= level.getMaxHeight()) {
            return false;
        }

        BlockVector3 down = position.down();
        Block block = level.getBlockIfCachedOrLoaded(down.x, down.y, down.z);

        if (!block.hasTag(BlockTags.DIRT)) {
            return false;
        }

        this.setDirtAt(level, down);

        for (int y = 0; y < height; y++) {
            BlockVector3 up = position.up(y);
            String id = level.getBlockIdIfCachedOrLoaded(up.x, up.y, up.z);
            if (id.equals(Block.AIR) || id.equals(Block.OAK_LEAVES) || id.equals(Block.VINE) || id.equals(Block.WATER)) {
                level.setBlockStateAt(up, metaWood);
                if(id.equals(Block.WATER)) height++;
            }
        }

        int baseY = position.getY();
        int baseX = position.getX();
        int baseZ = position.getZ();

        for (int yy = baseY - 3 + height; yy <= baseY + height; ++yy) {
            double yOff = yy - (baseY + height);
            int mid = (int) (1 - yOff / 2);

            for (int xx = baseX - mid; xx <= baseX + mid; ++xx) {
                int xOff = Math.abs(xx - baseX);

                for (int zz = baseZ - mid; zz <= baseZ + mid; ++zz) {
                    int zOff = Math.abs(zz - baseZ);

                    if (xOff == mid && zOff == mid && (yOff == 0 || rand.nextInt(2) == 0)) {
                        continue;
                    }

                    Block blockAt = level.getBlockIfCachedOrLoaded(xx, yy, zz);
                    if (!blockAt.isSolid()) {
                        level.setBlockStateAt(xx, yy, zz, metaLeaves);

                        if (rand.nextInt(4) == 0) {
                            this.addHangingVine(level, new BlockVector3(xx, yy, zz), randomVineMeta(rand));
                        }
                    }
                }
            }
        }

        return true;
    }

    private void addVine(BlockManager level, BlockVector3 pos, int meta) {
        BlockState blockState = BlockVine.PROPERTIES.getBlockState(CommonBlockProperties.VINE_DIRECTION_BITS, meta);
        level.setBlockStateAt(pos, blockState);
    }

    private void addHangingVine(BlockManager level, BlockVector3 pos, int meta) {
        BlockVector3 down = pos.down();
        int length = 3 + (int) (Math.random() * 3); // 3–5 Blöcke
        for (int i = 0; i < length; i++) {
            if (level.getBlockIdIfCachedOrLoaded(down.x, down.y, down.z).equals(Block.AIR) && down.y > SEA_LEVEL) {
                this.addVine(level, down, meta);
                down = down.down();
            } else {
                break;
            }
        }
    }

    private int randomVineMeta(RandomSourceProvider rand) {
        int[] metas = {1, 2, 4, 8}; // N, E, S, W
        return metas[rand.nextInt(metas.length-1)];
    }
}
