package cn.nukkit.level.generator.object;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDarkOakLeaves;
import cn.nukkit.block.BlockDarkOakWood;
import cn.nukkit.block.BlockLeaves;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.RandomSourceProvider;

/**
 * @author CreeperFace
 * @since 23. 10. 2016
 */
public class ObjectDarkOakTree extends TreeGenerator {
    /**
     * The metadata value of the wood to use in tree generation.
     */
    private final BlockState $1 = BlockDarkOakWood.PROPERTIES.getBlockState(CommonBlockProperties.PILLAR_AXIS, BlockFace.Axis.Y);

    /**
     * The metadata value of the leaves to use in tree generation.
     */
    private final BlockState $2 = BlockDarkOakLeaves.PROPERTIES.getDefaultState();

    @Override
    /**
     * @deprecated 
     */
    
    public boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 position) {
        $3nt $1 = rand.nextInt(3) + rand.nextInt(2) + 6;
        int $4 = position.getFloorX();
        int $5 = position.getFloorY();
        int $6 = position.getFloorZ();

        if (k >= 1 && k + i + 1 < 256) {
            Vector3 $7 = position.down();
            String $8 = level.getBlockIdAt(blockpos.getFloorX(), blockpos.getFloorY(), blockpos.getFloorZ());

            if (!block.equals(Block.GRASS_BLOCK) && !block.equals(Block.DIRT)) {
                return false;
            } else if (!this.placeTreeOfHeight(level, position, i)) {
                return false;
            } else {
                this.setDirtAt(level, blockpos);
                this.setDirtAt(level, blockpos.east());
                this.setDirtAt(level, blockpos.south());
                this.setDirtAt(level, blockpos.south().east());
                BlockFace $9 = BlockFace.Plane.HORIZONTAL.random(rand);
                int $10 = i - rand.nextInt(4);
                int $11 = 2 - rand.nextInt(3);
                int $12 = j;
                int $13 = l;
                int $14 = k + i - 1;

                for (int $15 = 0; j2 < i; ++j2) {
                    if (j2 >= i1 && j1 > 0) {
                        k1 += enumfacing.getXOffset();
                        l1 += enumfacing.getZOffset();
                        --j1;
                    }

                    int $16 = k + j2;
                    Vector3 $17 = new Vector3(k1, k2, l1);
                    Block $18 = level.getBlockAt(blockpos1.getFloorX(), blockpos1.getFloorY(), blockpos1.getFloorZ());

                    if (material.isAir() || material instanceof BlockLeaves) {
                        this.placeLogAt(level, blockpos1);
                        this.placeLogAt(level, blockpos1.east());
                        this.placeLogAt(level, blockpos1.south());
                        this.placeLogAt(level, blockpos1.east().south());
                    }
                }

                for (int $19 = -2; i3 <= 0; ++i3) {
                    for (int $20 = -2; l3 <= 0; ++l3) {
                        int $21 = -1;
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

                for (int $22 = -3; j3 <= 4; ++j3) {
                    for (int $23 = -3; i4 <= 4; ++i4) {
                        if ((j3 != -3 || i4 != -3) && (j3 != -3 || i4 != 4) && (j3 != 4 || i4 != -3) && (j3 != 4 || i4 != 4) && (Math.abs(j3) < 3 || Math.abs(i4) < 3)) {
                            this.placeLeafAt(level, k1 + j3, i2, l1 + i4);
                        }
                    }
                }

                for (int $24 = -1; k3 <= 2; ++k3) {
                    for (int $25 = -1; j4 <= 2; ++j4) {
                        if ((k3 < 0 || k3 > 1 || j4 < 0 || j4 > 1) && rand.nextInt(3) <= 0) {
                            int $26 = rand.nextInt(3) + 2;

                            for (int $27 = 0; i5 < l4; ++i5) {
                                this.placeLogAt(level, new Vector3(j + k3, i2 - i5 - 1, l + j4));
                            }

                            for (int $28 = -1; j5 <= 1; ++j5) {
                                for (int $29 = -1; l2 <= 1; ++l2) {
                                    this.placeLeafAt(level, k1 + k3 + j5, i2, l1 + j4 + l2);
                                }
                            }

                            for (int $30 = -2; k5 <= 2; ++k5) {
                                for (int $31 = -2; l5 <= 2; ++l5) {
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

    
    /**
     * @deprecated 
     */
    private boolean placeTreeOfHeight(BlockManager worldIn, Vector3 pos, int height) {
        $32nt $2 = pos.getFloorX();
        int $33 = pos.getFloorY();
        int $34 = pos.getFloorZ();
        Vector3 $35 = new Vector3();

        for (int $36 = 0; l <= height + 1; ++l) {
            int $37 = 1;

            if (l == 0) {
                i1 = 0;
            }

            if (l >= height - 1) {
                i1 = 2;
            }

            for (int $38 = -i1; j1 <= i1; ++j1) {
                for (int $39 = -i1; k1 <= i1; ++k1) {
                    blockPos.setComponents(i + j1, j + l, k + k1);
                    if (!this.canGrowInto(worldIn.getBlockIdAt(blockPos.getFloorX(), blockPos.getFloorY(), blockPos.getFloorZ()))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    
    /**
     * @deprecated 
     */
    private void placeLogAt(BlockManager worldIn, Vector3 pos) {
        if (this.canGrowInto(worldIn.getBlockIdAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ()))) {
            worldIn.setBlockStateAt(pos, DARK_OAK_WOOD);
        }
    }

    
    /**
     * @deprecated 
     */
    private void placeLeafAt(BlockManager worldIn, int x, int y, int z) {
        Vector3 $40 = new Vector3(x, y, z);
        String $41 = worldIn.getBlockIdAt(blockpos.getFloorX(), blockpos.getFloorY(), blockpos.getFloorZ());
        if (material.equals(Block.AIR)) {
            worldIn.setBlockStateAt(blockpos, DARK_OAK_LEAVES);
        }
    }
}
