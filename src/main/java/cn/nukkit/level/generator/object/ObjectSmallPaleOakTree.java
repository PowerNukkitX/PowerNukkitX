package cn.nukkit.level.generator.object;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLeaves;
import cn.nukkit.block.BlockPaleHangingMoss;
import cn.nukkit.block.BlockPaleOakLeaves;
import cn.nukkit.block.BlockPaleOakWood;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.RandomSourceProvider;

public class ObjectSmallPaleOakTree extends TreeGenerator {

    /**
     * The minimum height of a generated tree.
     */
    private final int minTreeHeight;

    private final int maxTreeHeight;

    /**
     * The metadata value of the wood to use in tree generation.
     */
    private final BlockState metaWood = BlockPaleOakWood.PROPERTIES.getBlockState(CommonBlockProperties.PILLAR_AXIS.createValue(BlockFace.Axis.Y));

    /**
     * The metadata value of the leaves to use in tree generation.
     */
    private final BlockState metaLeaves = BlockPaleOakLeaves.PROPERTIES.getDefaultState();

    public ObjectSmallPaleOakTree(int minTreeHeight, int maxTreeHeight) {
        this.minTreeHeight = minTreeHeight;
        this.maxTreeHeight = maxTreeHeight;
    }

    @Override
    public boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 vectorPosition) {
        BlockVector3 position = new BlockVector3(vectorPosition.getFloorX(), vectorPosition.getFloorY(), vectorPosition.getFloorZ());

        int i = rand.nextInt(maxTreeHeight) + this.minTreeHeight;
        boolean flag = true;

        //Check the height of the tree and if exist block in it
        if (position.getY() >= level.getMinHeight() && position.getY() + i + 1 < level.getMaxHeight()) {
            for (int j = position.getY(); j <= position.getY() + 1 + i; ++j) {
                int k = 1;

                if (j == position.getY()) {
                    k = 0;
                }

                if (j >= position.getY() + 1 + i - 2) {
                    k = 2;
                }

                BlockVector3 pos2 = new BlockVector3();

                for (int l = position.getX() - k; l <= position.getX() + k && flag; ++l) {
                    for (int i1 = position.getZ() - k; i1 <= position.getZ() + k && flag; ++i1) {
                        if (j >= level.getMinHeight() && j < level.getMaxHeight()) {
                            pos2.setComponents(l, j, i1);
                            if (!this.canGrowInto(level.getBlockIdIfCachedOrLoaded(pos2.x, pos2.y, pos2.z))) {
                                flag = false;
                            }
                        } else {
                            flag = false;
                        }
                    }
                }
            }

            if (!flag) {
                return false;
            } else {
                BlockVector3 down = position.down();
                String block = level.getBlockIdIfCachedOrLoaded(down.x, down.y, down.z);

                if ((block.equals(Block.GRASS_BLOCK) || block.equals(Block.DIRT) || block.equals(Block.FARMLAND)) && position.getY() < level.getMaxHeight() - i - 1) {
                    this.setDirtAt(level, down);

                    //Add leaves
                    for (int i3 = position.getY() - 3 + i; i3 <= position.getY() + i; ++i3) {
                        int i4 = i3 - (position.getY() + i);
                        int j1 = 1 - i4 / 2;

                        for (int k1 = position.getX() - j1; k1 <= position.getX() + j1; ++k1) {
                            int l1 = k1 - position.getX();

                            for (int i2 = position.getZ() - j1; i2 <= position.getZ() + j1; ++i2) {
                                int j2 = i2 - position.getZ();

                                if (Math.abs(l1) != j1 || Math.abs(j2) != j1 || rand.nextInt(2) != 0 && i4 != 0) {
                                    BlockVector3 blockpos = new BlockVector3(k1, i3, i2);
                                    Block id = level.getBlockIfCachedOrLoaded(blockpos.x, blockpos.y, blockpos.z);

                                    if (id.getId().equals(Block.AIR) || id instanceof BlockLeaves || id.getId().equals(Block.PALE_HANGING_MOSS)) {
                                        level.setBlockStateAt(blockpos, metaLeaves);
                                        NukkitRandom random = new NukkitRandom(level.getSeed()+blockpos.x+blockpos.y+blockpos.z);
                                        if (random.nextInt(2) == 0) {
                                            int depth = random.nextInt(1, 6);
                                            for(int j = 1; j < depth; i++) {
                                                Vector3 pos = new Vector3(blockpos.x, blockpos.y-i, blockpos.z);
                                                if(level.getBlockIfCachedOrLoaded(pos).isAir()) {
                                                    if(i==depth-1){
                                                        level.setBlockStateAt(pos, BlockPaleHangingMoss.PROPERTIES.getBlockState(CommonBlockProperties.TIP, true));
                                                    } else level.setBlockStateAt(pos, BlockPaleHangingMoss.PROPERTIES.getBlockState(CommonBlockProperties.TIP, false));
                                                } else break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }
}
