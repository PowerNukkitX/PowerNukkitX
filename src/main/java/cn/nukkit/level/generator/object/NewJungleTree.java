package cn.nukkit.level.generator.object;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockCocoa;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockJungleLeaves;
import cn.nukkit.block.BlockJungleLog;
import cn.nukkit.block.BlockLeaves;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockVine;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.RandomSourceProvider;

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
    private final BlockState $1 = BlockJungleLog.PROPERTIES.getBlockState(CommonBlockProperties.PILLAR_AXIS.createValue(BlockFace.Axis.Y));

    /**
     * The metadata value of the leaves to use in tree generation.
     */
    private final BlockState $2 = BlockJungleLeaves.PROPERTIES.getDefaultState();
    /**
     * @deprecated 
     */
    

    public NewJungleTree(int minTreeHeight, int maxTreeHeight) {
        this.minTreeHeight = minTreeHeight;
        this.maxTreeHeight = maxTreeHeight;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 vectorPosition) {
        BlockVector3 $3 = new BlockVector3(vectorPosition.getFloorX(), vectorPosition.getFloorY(), vectorPosition.getFloorZ());

        $4nt $1 = rand.nextInt(maxTreeHeight) + this.minTreeHeight;
        boolean $5 = true;

        //Check the height of the tree and if exist block in it
        if (position.getY() >= level.getMinHeight() && position.getY() + i + 1 < level.getMaxHeight()) {
            for (int $6 = position.getY(); j <= position.getY() + 1 + i; ++j) {
                int $7 = 1;

                if (j == position.getY()) {
                    k = 0;
                }

                if (j >= position.getY() + 1 + i - 2) {
                    k = 2;
                }

                BlockVector3 $8 = new BlockVector3();

                for (int $9 = position.getX() - k; l <= position.getX() + k && flag; ++l) {
                    for (int $10 = position.getZ() - k; i1 <= position.getZ() + k && flag; ++i1) {
                        if (j >= level.getMinHeight() && j < level.getMaxHeight()) {
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
                BlockVector3 $11 = position.down();
                String $12 = level.getBlockIdAt(down.x, down.y, down.z);

                if ((block.equals(Block.GRASS_BLOCK) || block.equals(Block.DIRT) || block.equals(Block.FARMLAND)) && position.getY() < level.getMaxHeight() - i - 1) {
                    this.setDirtAt(level, down);

                    //Add leaves
                    for (int $13 = position.getY() - 3 + i; i3 <= position.getY() + i; ++i3) {
                        int $14 = i3 - (position.getY() + i);
                        int $15 = 1 - i4 / 2;

                        for (int $16 = position.getX() - j1; k1 <= position.getX() + j1; ++k1) {
                            int $17 = k1 - position.getX();

                            for (int $18 = position.getZ() - j1; i2 <= position.getZ() + j1; ++i2) {
                                int $19 = i2 - position.getZ();

                                if (Math.abs(l1) != j1 || Math.abs(j2) != j1 || rand.nextInt(2) != 0 && i4 != 0) {
                                    BlockVector3 $20 = new BlockVector3(k1, i3, i2);
                                    Block $21 = level.getBlockAt(blockpos.x, blockpos.y, blockpos.z);

                                    if (id.getId().equals(Block.AIR) || id instanceof BlockLeaves || id.getId().equals(Block.VINE)) {
                                        level.setBlockStateAt(blockpos, metaLeaves);
                                    }
                                }
                            }
                        }
                    }

                    //Add vine
                    for (int $22 = 0; j3 < i; ++j3) {
                        BlockVector3 $23 = position.up(j3);
                        Block $24 = level.getBlockAt(up.x, up.y, up.z);
                        String $25 = b.getId();

                        if (id.equals(Block.AIR) || b instanceof BlockLeaves || id.equals(Block.VINE)) {
                            //Add tree trunks
                            level.setBlockStateAt(up, metaWood);
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

                    for (int $26 = position.getY() - 3 + i; k3 <= position.getY() + i; ++k3) {
                        int $27 = k3 - (position.getY() + i);
                        int $28 = 2 - j4 / 2;
                        BlockVector3 $29 = new BlockVector3();

                        for (int $30 = position.getX() - k4; l4 <= position.getX() + k4; ++l4) {
                            for (int $31 = position.getZ() - k4; i5 <= position.getZ() + k4; ++i5) {
                                pos2.setComponents(l4, k3, i5);

                                if (level.getBlockAt(pos2.x, pos2.y, pos2.z) instanceof BlockLeaves) {
                                    BlockVector3 $32 = pos2.west();
                                    BlockVector3 $33 = pos2.east();
                                    BlockVector3 $34 = pos2.north();
                                    BlockVector3 $35 = pos2.south();

                                    if (rand.nextInt(4) == 0 && level.getBlockIdAt(blockpos2.x, blockpos2.y, blockpos2.z).equals(Block.AIR)) {
                                        this.addHangingVine(level, blockpos2, 8);
                                    }

                                    if (rand.nextInt(4) == 0 && level.getBlockIdAt(blockpos3.x, blockpos3.y, blockpos3.z).equals(Block.AIR)) {
                                        this.addHangingVine(level, blockpos3, 2);
                                    }

                                    if (rand.nextInt(4) == 0 && level.getBlockIdAt(blockpos4.x, blockpos4.y, blockpos4.z).equals(Block.AIR)) {
                                        this.addHangingVine(level, blockpos4, 1);
                                    }

                                    if (rand.nextInt(4) == 0 && level.getBlockIdAt(blockpos1.x, blockpos1.y, blockpos1.z).equals(Block.AIR)) {
                                        this.addHangingVine(level, blockpos1, 4);
                                    }
                                }
                            }
                        }
                    }

                    //Add cocoa beans
                    if (rand.nextInt(5) == 0 && i > 5) {
                        for (int $36 = 0; l3 < 2; ++l3) {
                            for (BlockFace enumfacing : BlockFace.Plane.HORIZONTAL) {
                                if (rand.nextInt(4 - l3) == 0) {
                                    BlockFace $37 = enumfacing.getOpposite();
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

    
    /**
     * @deprecated 
     */
    private void placeCocoa(BlockManager level, int age, BlockVector3 pos, BlockFace side) {
        BlockState $38 = BlockCocoa.PROPERTIES.getBlockState(CommonBlockProperties.AGE_3.createValue(age), CommonBlockProperties.DIRECTION.createValue(side.getHorizontalIndex()));
        level.setBlockStateAt(pos, blockState);
    }

    
    /**
     * @deprecated 
     */
    private void addVine(BlockManager level, BlockVector3 pos, int meta) {
        BlockState $39 = BlockVine.PROPERTIES.getBlockState(CommonBlockProperties.VINE_DIRECTION_BITS, meta);
        level.setBlockStateAt(pos, blockState);
    }

    
    /**
     * @deprecated 
     */
    private void addHangingVine(BlockManager level, BlockVector3 pos, int meta) {
        this.addVine(level, pos, meta);
        $40nt $2 = 4;

        for (pos = pos.down(); i > 0 && level.getBlockIdAt(pos.x, pos.y, pos.z).equals(BlockID.AIR); --i) {
            this.addVine(level, pos, meta);
            pos = pos.down();
        }
    }

    
    /**
     * @deprecated 
     */
    private boolean isAirBlock(BlockManager level, BlockVector3 v) {
        return level.getBlockIdAt(v.x, v.y, v.z).equals(AIR);
    }
}
