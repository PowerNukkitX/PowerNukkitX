package cn.nukkit.level.generator.object;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLeaves;
import cn.nukkit.block.BlockPaleHangingMoss;
import cn.nukkit.block.BlockPaleOakLeaves;
import cn.nukkit.block.BlockPaleOakLog;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.RandomSourceProvider;

public class ObjectPaleOakTree extends TreeGenerator {
    /**
     * The metadata value of the wood to use in tree generation.
     */
    private final BlockState PALE_OAK_LOG = BlockPaleOakLog.PROPERTIES.getBlockState(CommonBlockProperties.PILLAR_AXIS, BlockFace.Axis.Y);

    /**
     * The metadata value of the leaves to use in tree generation.
     */
    private final BlockState PALE_OAK_LEAVES = BlockPaleOakLeaves.PROPERTIES.getDefaultState();

    /**
     * The metadata value of the leaves to use in tree generation.
     */
    @Override
    public boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 position) {
        int i = rand.nextInt(3) + rand.nextInt(2) + 6;
        int j = position.getFloorX();
        int k = position.getFloorY();
        int l = position.getFloorZ();

        if (k >= 1 && k + i + 1 < 256) {
            Vector3 blockpos = position.down();
            String block = level.getBlockIdAt(blockpos.getFloorX(), blockpos.getFloorY(), blockpos.getFloorZ());

            if (!block.equals(Block.GRASS_BLOCK) && !block.equals(Block.DIRT)) {
                return false;
            } else if (!this.placeTreeOfHeight(level, position, i)) {
                return false;
            } else {
                this.setDirtAt(level, blockpos);
                this.setDirtAt(level, blockpos.east());
                this.setDirtAt(level, blockpos.south());
                this.setDirtAt(level, blockpos.south().east());
                BlockFace enumfacing = BlockFace.Plane.HORIZONTAL.random(rand);
                int i1 = i - rand.nextInt(4);
                int j1 = 2 - rand.nextInt(3);
                int k1 = j;
                int l1 = l;
                int i2 = k + i - 1;

                for (int j2 = 0; j2 < i; ++j2) {
                    if (j2 >= i1 && j1 > 0) {
                        k1 += enumfacing.getXOffset();
                        l1 += enumfacing.getZOffset();
                        --j1;
                    }

                    int k2 = k + j2;
                    Vector3 blockpos1 = new Vector3(k1, k2, l1);
                    Block material = level.getBlockAt(blockpos1.getFloorX(), blockpos1.getFloorY(), blockpos1.getFloorZ());

                    if (material.isAir() || material instanceof BlockLeaves) {
                        this.placeLogAt(level, blockpos1);
                        this.placeLogAt(level, blockpos1.east());
                        this.placeLogAt(level, blockpos1.south());
                        this.placeLogAt(level, blockpos1.east().south());
                    }
                }

                for (int i3 = -2; i3 <= 0; ++i3) {
                    for (int l3 = -2; l3 <= 0; ++l3) {
                        int k4 = -1;
                        this.placeLeafAt(level, k1 + i3, i2 + k4, l1 + l3);
                        this.placeLeafAt(level, 1 + k1 - i3, i2 + k4, l1 + l3);
                        this.placeLeafAt(level, k1 + i3, i2 + k4, 1 + l1 - l3);
                        this.placeLeafAt(level, 1 + k1 - i3, i2 + k4, 1 + l1 - l3);

                        if ((i3 > -2 || l3 > -1) && (i3 != -1 || l3 != -2)) {
                            k4 = 1;
                            this.placeLeafAt(level, k1 + i3, i2 + k4, l1 + l3);
                            this.placeLeafAt(level, 1 + k1 - i3, i2 + k4, l1 + l3);
                            this.placeLeafAt(level, k1 + i3, i2 + k4, 1 + l1 - l3);
                            this.placeLeafAt(level, 1 + k1 - i3, i2 + k4, 1 + l1 - l3);
                        }
                    }
                }

                if (rand.nextBoolean()) {
                    this.placeLeafAt(level, k1, i2 + 2, l1);
                    this.placeLeafAt(level, k1 + 1, i2 + 2, l1);
                    this.placeLeafAt(level, k1 + 1, i2 + 2, l1 + 1);
                    this.placeLeafAt(level, k1, i2 + 2, l1 + 1);
                }

                for (int j3 = -3; j3 <= 4; ++j3) {
                    for (int i4 = -3; i4 <= 4; ++i4) {
                        if ((j3 != -3 || i4 != -3) && (j3 != -3 || i4 != 4) && (j3 != 4 || i4 != -3) && (j3 != 4 || i4 != 4) && (Math.abs(j3) < 3 || Math.abs(i4) < 3)) {
                            this.placeLeafAt(level, k1 + j3, i2, l1 + i4);
                        }
                    }
                }

                for (int k3 = -1; k3 <= 2; ++k3) {
                    for (int j4 = -1; j4 <= 2; ++j4) {
                        if ((k3 < 0 || k3 > 1 || j4 < 0 || j4 > 1) && rand.nextInt(3) <= 0) {
                            int l4 = rand.nextInt(3) + 2;

                            for (int i5 = 0; i5 < l4; ++i5) {
                                this.placeLogAt(level, new Vector3(j + k3, i2 - i5 - 1, l + j4));
                            }

                            for (int j5 = -1; j5 <= 1; ++j5) {
                                for (int l2 = -1; l2 <= 1; ++l2) {
                                    this.placeLeafAt(level, k1 + k3 + j5, i2, l1 + j4 + l2);
                                }
                            }

                            for (int k5 = -2; k5 <= 2; ++k5) {
                                for (int l5 = -2; l5 <= 2; ++l5) {
                                    if (Math.abs(k5) != 2 || Math.abs(l5) != 2) {
                                        this.placeLeafAt(level, k1 + k3 + k5, i2 - 1, l1 + j4 + l5);
                                    }
                                }
                            }
                        }
                    }
                }

                return true;
            }
        } else {
            return false;
        }
    }

    private boolean placeTreeOfHeight(BlockManager worldIn, Vector3 pos, int height) {
        int i = pos.getFloorX();
        int j = pos.getFloorY();
        int k = pos.getFloorZ();
        Vector3 blockPos = new Vector3();

        for (int l = 0; l <= height + 1; ++l) {
            int i1 = 1;

            if (l == 0) {
                i1 = 0;
            }

            if (l >= height - 1) {
                i1 = 2;
            }

            for (int j1 = -i1; j1 <= i1; ++j1) {
                for (int k1 = -i1; k1 <= i1; ++k1) {
                    blockPos.setComponents(i + j1, j + l, k + k1);
                    if (!this.canGrowInto(worldIn.getBlockIdAt(blockPos.getFloorX(), blockPos.getFloorY(), blockPos.getFloorZ()))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private void placeLogAt(BlockManager worldIn, Vector3 pos) {
        if (this.canGrowInto(worldIn.getBlockIdAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ()))) {
            worldIn.setBlockStateAt(pos, PALE_OAK_LOG);
        }
    }

    private void placeLeafAt(BlockManager worldIn, int x, int y, int z) {
        Vector3 blockpos = new Vector3(x, y, z);
        String material = worldIn.getBlockIdAt(blockpos.getFloorX(), blockpos.getFloorY(), blockpos.getFloorZ());
        if (material.equals(Block.AIR)) {
            worldIn.setBlockStateAt(blockpos, PALE_OAK_LEAVES);
            NukkitRandom random = new NukkitRandom(worldIn.getSeed()+x+y+z);
            if (random.nextInt(2) == 0) {
                int depth = random.nextInt(1, 6);
                for(int i = 1; i < depth; i++) {
                    Vector3 pos = new Vector3(x, y-i, z);
                    if(worldIn.getBlockAt(pos).isAir()) {
                        if(i==depth-1){
                            worldIn.setBlockStateAt(pos, BlockPaleHangingMoss.PROPERTIES.getBlockState(CommonBlockProperties.TIP, true));
                        } else worldIn.setBlockStateAt(pos, BlockPaleHangingMoss.PROPERTIES.getBlockState(CommonBlockProperties.TIP, false));
                    } else break;
                }
            }
        }
    }
}
