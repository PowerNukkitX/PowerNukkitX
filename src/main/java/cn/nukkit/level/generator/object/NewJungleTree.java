package cn.nukkit.level.generator.object;

import cn.nukkit.block.*;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.OldLeafType;
import cn.nukkit.block.property.enums.WoodType;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.RandomSource;

/**
 * @author CreeperFace
 * @since 26. 10. 2016
 */
public class NewJungleTree extends TreeGenerator {

    /**
     * The minimum height of a generated tree.
     */
    private final int minTreeHeight;

    private final int maxTreeHeight;

    /**
     * The metadata value of the wood to use in tree generation.
     */
    private final Block metaWood = BlockWood.PROPERTIES.getBlockState(CommonBlockProperties.WOOD_TYPE, WoodType.JUNGLE).toBlock();

    /**
     * The metadata value of the leaves to use in tree generation.
     */
    private final Block metaLeaves = BlockLeaves.PROPERTIES.getBlockState(CommonBlockProperties.OLD_LEAF_TYPE, OldLeafType.JUNGLE).toBlock();

    public NewJungleTree(int minTreeHeight, int maxTreeHeight) {
        this.minTreeHeight = minTreeHeight;
        this.maxTreeHeight = maxTreeHeight;
    }

    @Override
    public boolean generate(BlockManager level, RandomSource rand, Vector3 vectorPosition) {
        BlockVector3 position = new BlockVector3(vectorPosition.getFloorX(), vectorPosition.getFloorY(), vectorPosition.getFloorZ());

        int i = rand.nextInt(maxTreeHeight) + this.minTreeHeight;
        boolean flag = true;

        if (position.getY() >= 1 && position.getY() + i + 1 <= 256) {
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
                        if (j >= 0 && j < 256) {
                            pos2.setComponents(l, j, i1);
                            if (!this.canGrowInto(level.getBlockIdAt(pos2.x, pos2.y, pos2.z))) {
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
                String block = level.getBlockIdAt(down.x, down.y, down.z);

                if ((block == Block.GRASS || block == Block.DIRT || block == Block.FARMLAND) && position.getY() < 256 - i - 1) {
                    this.setDirtAt(level, down);
                    int k2 = 3;
                    int l2 = 0;

                    for (int i3 = position.getY() - 3 + i; i3 <= position.getY() + i; ++i3) {
                        int i4 = i3 - (position.getY() + i);
                        int j1 = 1 - i4 / 2;

                        for (int k1 = position.getX() - j1; k1 <= position.getX() + j1; ++k1) {
                            int l1 = k1 - position.getX();

                            for (int i2 = position.getZ() - j1; i2 <= position.getZ() + j1; ++i2) {
                                int j2 = i2 - position.getZ();

                                if (Math.abs(l1) != j1 || Math.abs(j2) != j1 || rand.nextInt(2) != 0 && i4 != 0) {
                                    BlockVector3 blockpos = new BlockVector3(k1, i3, i2);
                                    String id = level.getBlockIdAt(blockpos.x, blockpos.y, blockpos.z);

                                    if (id == Block.AIR || id == Block.LEAVES || id == Block.VINE) {
                                        level.setBlockAt(blockpos, metaLeaves);
                                    }
                                }
                            }
                        }
                    }

                    for (int j3 = 0; j3 < i; ++j3) {
                        BlockVector3 up = position.up(j3);
                        String id = level.getBlockIdAt(up.x, up.y, up.z);

                        if (id == Block.AIR || id == Block.LEAVES || id == Block.VINE) {
                            level.setBlockAt(up, metaWood);
                            if (j3 > 0) {
                                if (rand.nextInt(3) > 0 && isAirBlock(level, position.add(-1, j3, 0))) {
                                    this.addVine(level, position.add(-1, j3, 0), 8);
                                }

                                if (rand.nextInt(3) > 0 && isAirBlock(level, position.add(1, j3, 0))) {
                                    this.addVine(level, position.add(1, j3, 0), 2);
                                }

                                if (rand.nextInt(3) > 0 && isAirBlock(level, position.add(0, j3, -1))) {
                                    this.addVine(level, position.add(0, j3, -1), 1);
                                }

                                if (rand.nextInt(3) > 0 && isAirBlock(level, position.add(0, j3, 1))) {
                                    this.addVine(level, position.add(0, j3, 1), 4);
                                }
                            }
                        }
                    }

                    for (int k3 = position.getY() - 3 + i; k3 <= position.getY() + i; ++k3) {
                        int j4 = k3 - (position.getY() + i);
                        int k4 = 2 - j4 / 2;
                        BlockVector3 pos2 = new BlockVector3();

                        for (int l4 = position.getX() - k4; l4 <= position.getX() + k4; ++l4) {
                            for (int i5 = position.getZ() - k4; i5 <= position.getZ() + k4; ++i5) {
                                pos2.setComponents(l4, k3, i5);

                                if (level.getBlockIdAt(pos2.x, pos2.y, pos2.z) == Block.LEAVES) {
                                    BlockVector3 blockpos2 = pos2.west();
                                    BlockVector3 blockpos3 = pos2.east();
                                    BlockVector3 blockpos4 = pos2.north();
                                    BlockVector3 blockpos1 = pos2.south();

                                    if (rand.nextInt(4) == 0 && level.getBlockIdAt(blockpos2.x, blockpos2.y, blockpos2.z) == Block.AIR) {
                                        this.addHangingVine(level, blockpos2, 8);
                                    }

                                    if (rand.nextInt(4) == 0 && level.getBlockIdAt(blockpos3.x, blockpos3.y, blockpos3.z) == Block.AIR) {
                                        this.addHangingVine(level, blockpos3, 2);
                                    }

                                    if (rand.nextInt(4) == 0 && level.getBlockIdAt(blockpos4.x, blockpos4.y, blockpos4.z) == Block.AIR) {
                                        this.addHangingVine(level, blockpos4, 1);
                                    }

                                    if (rand.nextInt(4) == 0 && level.getBlockIdAt(blockpos1.x, blockpos1.y, blockpos1.z) == Block.AIR) {
                                        this.addHangingVine(level, blockpos1, 4);
                                    }
                                }
                            }
                        }
                    }

                    if (rand.nextInt(5) == 0 && i > 5) {
                        for (int l3 = 0; l3 < 2; ++l3) {
                            for (BlockFace enumfacing : BlockFace.Plane.HORIZONTAL) {
                                if (rand.nextInt(4 - l3) == 0) {
                                    BlockFace enumfacing1 = enumfacing.getOpposite();
                                    this.placeCocoa(level, rand.nextInt(3), position.add(enumfacing1.getXOffset(), i - 5 + l3, enumfacing1.getZOffset()), enumfacing);
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

    private void placeCocoa(BlockManager level, int age, BlockVector3 pos, BlockFace side) {
        BlockState blockState = BlockCocoa.PROPERTIES.getBlockState(CommonBlockProperties.AGE_3.createValue(age), CommonBlockProperties.DIRECTION.createValue(side.getHorizontalIndex()));
        level.setBlockStateAt(pos, blockState);
    }

    private void addVine(BlockManager level, BlockVector3 pos, int meta) {
        BlockState blockState = BlockVine.PROPERTIES.getBlockState(CommonBlockProperties.VINE_DIRECTION_BITS, meta);
        level.setBlockStateAt(pos, blockState);
    }

    private void addHangingVine(BlockManager level, BlockVector3 pos, int meta) {
        this.addVine(level, pos, meta);
        int i = 4;

        for (pos = pos.down(); i > 0 && level.getBlockIdAt(pos.x, pos.y, pos.z) == Block.AIR; --i) {
            this.addVine(level, pos, meta);
            pos = pos.down();
        }
    }

    private boolean isAirBlock(BlockManager level, BlockVector3 v) {
        return level.getBlockIdAt(v.x, v.y, v.z) == AIR;
    }
}
