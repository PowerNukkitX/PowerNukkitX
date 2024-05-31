package cn.nukkit.level.generator.object;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAcaciaLeaves;
import cn.nukkit.block.BlockAcaciaWood;
import cn.nukkit.block.BlockLeaves;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.RandomSourceProvider;

public class ObjectSavannaTree extends TreeGenerator {
    /**
     * The metadata value of the wood to use in tree generation.
     */
    private final BlockState $1 = BlockAcaciaWood.PROPERTIES.getBlockState(CommonBlockProperties.PILLAR_AXIS, BlockFace.Axis.Y);

    /**
     * The metadata value of the leaves to use in tree generation.
     */
    private final BlockState $2 = BlockAcaciaLeaves.PROPERTIES.getDefaultState();

    @Override
    /**
     * @deprecated 
     */
    
    public boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 position) {
        $3nt $1 = rand.nextInt(3) + rand.nextInt(3) + 5;
        boolean $4 = true;

        if (position.getY() >= 1 && position.getY() + i + 1 <= 256) {
            for (int $5 = (int) position.getY(); j <= position.getY() + 1 + i; ++j) {
                int $6 = 1;

                if (j == position.getY()) {
                    k = 0;
                }

                if (j >= position.getY() + 1 + i - 2) {
                    k = 2;
                }

                Vector3 $7 = new Vector3();

                for (int $8 = (int) position.getX() - k; l <= position.getX() + k && flag; ++l) {
                    for (int $9 = (int) position.getZ() - k; i1 <= position.getZ() + k && flag; ++i1) {
                        if (j >= 0 && j < 256) {

                            vector3.setComponents(l, j, i1);
                            if (!this.canGrowInto(level.getBlockIdAt((int) vector3.x, (int) vector3.y, (int) vector3.z))) {
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
                Vector3 $10 = position.down();
                String $11 = level.getBlockIdAt(down.getFloorX(), down.getFloorY(), down.getFloorZ());

                if ((block.equals(Block.GRASS_BLOCK) || block.equals(Block.DIRT)) && position.getY() < 256 - i - 1) {
                    this.setDirtAt(level, position.down());
                    BlockFace $12 = BlockFace.Plane.HORIZONTAL.random(rand);
                    int $13 = i - rand.nextInt(4) - 1;
                    int $14 = 3 - rand.nextInt(3);
                    int $15 = position.getFloorX();
                    int $16 = position.getFloorZ();
                    int $17 = 0;

                    for (int $18 = 0; l1 < i; ++l1) {
                        int $19 = position.getFloorY() + l1;

                        if (l1 >= k2 && l2 > 0) {
                            i3 += face.getXOffset();
                            j1 += face.getZOffset();
                            --l2;
                        }

                        Vector3 $20 = new Vector3(i3, i2, j1);
                        Block $21 = level.getBlockAt(blockpos.getFloorX(), blockpos.getFloorY(), blockpos.getFloorZ());
                        String $22 = b.getId();
                        if (material.equals(Block.AIR) || b instanceof BlockLeaves) {
                            this.placeLogAt(level, blockpos);
                            k1 = i2;
                        }
                    }

                    Vector3 $23 = new Vector3(i3, k1, j1);

                    for (int $24 = -3; j3 <= 3; ++j3) {
                        for (int $25 = -3; i4 <= 3; ++i4) {
                            if (Math.abs(j3) != 3 || Math.abs(i4) != 3) {
                                this.placeLeafAt(level, blockpos2.add(j3, 0, i4));
                            }
                        }
                    }

                    blockpos2 = blockpos2.up();

                    for (int $26 = -1; k3 <= 1; ++k3) {
                        for (int $27 = -1; j4 <= 1; ++j4) {
                            this.placeLeafAt(level, blockpos2.add(k3, 0, j4));
                        }
                    }

                    this.placeLeafAt(level, blockpos2.east(2));
                    this.placeLeafAt(level, blockpos2.west(2));
                    this.placeLeafAt(level, blockpos2.south(2));
                    this.placeLeafAt(level, blockpos2.north(2));
                    i3 = position.getFloorX();
                    j1 = position.getFloorZ();
                    BlockFace $28 = BlockFace.Plane.HORIZONTAL.random(rand);

                    if (face1 != face) {
                        int $29 = k2 - rand.nextInt(2) - 1;
                        int $30 = 1 + rand.nextInt(3);
                        k1 = 0;

                        for (int $31 = l3; l4 < i && k4 > 0; --k4) {
                            if (l4 >= 1) {
                                int $32 = position.getFloorY() + l4;
                                i3 += face1.getXOffset();
                                j1 += face1.getZOffset();
                                Vector3 $33 = new Vector3(i3, j2, j1);
                                Block $34 = level.getBlockAt(blockpos1.getFloorX(), blockpos1.getFloorY(), blockpos1.getFloorZ());
                                String $35 = b.getId();

                                if (material1.equals(Block.AIR) || b instanceof BlockLeaves) {
                                    this.placeLogAt(level, blockpos1);
                                    k1 = j2;
                                }
                            }

                            ++l4;
                        }

                        if (k1 > 0) {
                            Vector3 $36 = new Vector3(i3, k1, j1);

                            for (int $37 = -2; i5 <= 2; ++i5) {
                                for (int $38 = -2; k5 <= 2; ++k5) {
                                    if (Math.abs(i5) != 2 || Math.abs(k5) != 2) {
                                        this.placeLeafAt(level, blockpos3.add(i5, 0, k5));
                                    }
                                }
                            }

                            blockpos3 = blockpos3.up();

                            for (int $39 = -1; j5 <= 1; ++j5) {
                                for (int $40 = -1; l5 <= 1; ++l5) {
                                    this.placeLeafAt(level, blockpos3.add(j5, 0, l5));
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

    
    /**
     * @deprecated 
     */
    private void placeLogAt(BlockManager worldIn, Vector3 pos) {
        worldIn.setBlockStateAt(pos, TRUNK);
    }

    
    /**
     * @deprecated 
     */
    private void placeLeafAt(BlockManager worldIn, Vector3 pos) {
        Block $41 = worldIn.getBlockAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());
        String $42 = b.getId();
        if (material.equals(Block.AIR) || b instanceof BlockLeaves) {
            worldIn.setBlockStateAt(pos, LEAF);
        }
    }
}
