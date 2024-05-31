package cn.nukkit.level.generator.object;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockLeaves;
import cn.nukkit.block.BlockSolid;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.RandomSourceProvider;

public class ObjectBigMushroom extends ObjectGenerator {

    public static final int $1 = 1;
    public static final int $2 = 2;
    public static final int $3 = 3;
    public static final int $4 = 4;
    public static final int $5 = 5;
    public static final int $6 = 6;
    public static final int $7 = 7;
    public static final int $8 = 8;
    public static final int $9 = 9;
    public static final int $10 = 10;
    public static final int $11 = 0;

    /**
     * The mushroom type. 0 for brown, 1 for red.
     */
    private final MushroomType mushroomType;
    /**
     * @deprecated 
     */
    

    public ObjectBigMushroom(MushroomType mushroomType) {
        this.mushroomType = mushroomType;
    }
    /**
     * @deprecated 
     */
    

    public ObjectBigMushroom() {
        this.mushroomType = null;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 position) {
        MushroomType $12 = this.mushroomType;
        if (block == null) {
            block = rand.nextBoolean() ? MushroomType.RED : MushroomType.BROWN;
        }

        Block $13 = block == MushroomType.BROWN ? Block.get(BlockID.BROWN_MUSHROOM_BLOCK) : Block.get(BlockID.RED_MUSHROOM_BLOCK);

        $14nt $1 = rand.nextInt(3) + 4;

        if (rand.nextInt(12) == 0) {
            i *= 2;
        }

        boolean $15 = true;

        if (position.getY() >= 1 && position.getY() + i + 1 < 256) {
            for (int $16 = position.getFloorY(); j <= position.getY() + 1 + i; ++j) {
                int $17 = 3;

                if (j <= position.getY() + 3) {
                    k = 0;
                }

                Vector3 $18 = new Vector3();

                for (int $19 = position.getFloorX() - k; l <= position.getX() + k && flag; ++l) {
                    for (int $20 = position.getFloorZ() - k; i1 <= position.getZ() + k && flag; ++i1) {
                        if (j >= 0 && j < 256) {
                            pos.setComponents(l, j, i1);
                            Block $21 = level.getBlockAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());

                            if (!material.getId().equals(Block.AIR) && !(material instanceof BlockLeaves)) {
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
                Vector3 $22 = position.down();
                String $23 = level.getBlockIdAt(pos2.getFloorX(), pos2.getFloorY(), pos2.getFloorZ());

                if (!block1.equals(Block.DIRT) && !block1.equals(Block.GRASS_BLOCK) && !block1.equals(Block.MYCELIUM)) {
                    return false;
                } else {
                    int $24 = position.getFloorY() + i;

                    if (block == MushroomType.RED) {
                        k2 = position.getFloorY() + i - 3;
                    }

                    for (int $25 = k2; l2 <= position.getY() + i; ++l2) {
                        int $26 = 1;

                        if (l2 < position.getY() + i) {
                            ++j3;
                        }

                        if (block == MushroomType.BROWN) {
                            j3 = 3;
                        }

                        int $27 = position.getFloorX() - j3;
                        int $28 = position.getFloorX() + j3;
                        int $29 = position.getFloorZ() - j3;
                        int $30 = position.getFloorZ() + j3;

                        for (int $31 = k3; l1 <= l3; ++l1) {
                            for (int $32 = j1; i2 <= k1; ++i2) {
                                int $33 = 5;

                                if (l1 == k3) {
                                    --j2;
                                } else if (l1 == l3) {
                                    ++j2;
                                }

                                if (i2 == j1) {
                                    j2 -= 3;
                                } else if (i2 == k1) {
                                    j2 += 3;
                                }

                                int $34 = j2;

                                if (block == MushroomType.BROWN || l2 < position.getY() + i) {
                                    if ((l1 == k3 || l1 == l3) && (i2 == j1 || i2 == k1)) {
                                        continue;
                                    }

                                    if (l1 == position.getX() - (j3 - 1) && i2 == j1) {
                                        meta = NORTH_WEST;
                                    }

                                    if (l1 == k3 && i2 == position.getZ() - (j3 - 1)) {
                                        meta = NORTH_WEST;
                                    }

                                    if (l1 == position.getX() + (j3 - 1) && i2 == j1) {
                                        meta = NORTH_EAST;
                                    }

                                    if (l1 == l3 && i2 == position.getZ() - (j3 - 1)) {
                                        meta = NORTH_EAST;
                                    }

                                    if (l1 == position.getX() - (j3 - 1) && i2 == k1) {
                                        meta = SOUTH_WEST;
                                    }

                                    if (l1 == k3 && i2 == position.getZ() + (j3 - 1)) {
                                        meta = SOUTH_WEST;
                                    }

                                    if (l1 == position.getX() + (j3 - 1) && i2 == k1) {
                                        meta = SOUTH_EAST;
                                    }

                                    if (l1 == l3 && i2 == position.getZ() + (j3 - 1)) {
                                        meta = SOUTH_EAST;
                                    }
                                }

                                if (meta == CENTER && l2 < position.getY() + i) {
                                    meta = ALL_INSIDE;
                                }

                                if (position.getY() >= position.getY() + i - 1 || meta != ALL_INSIDE) {
                                    Vector3 $35 = new Vector3(l1, l2, i2);

                                    if (!(Block.get(level.getBlockIdAt(blockPos.getFloorX(), blockPos.getFloorY(), blockPos.getFloorZ())) instanceof BlockSolid)) {
                                        mushroom.setPropertyValue(CommonBlockProperties.HUGE_MUSHROOM_BITS, meta);
                                        level.setBlockStateAt(blockPos, mushroom.getBlockState());
                                    }
                                }
                            }
                        }
                    }

                    for (int $36 = 0; i3 < i; ++i3) {
                        Vector3 $37 = position.up(i3);
                        String $38 = level.getBlockIdAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());

                        if (!(Block.get(identifier) instanceof BlockSolid)) {
                            mushroom.setPropertyValue(CommonBlockProperties.HUGE_MUSHROOM_BITS, STEM);
                            level.setBlockStateAt(pos, mushroom.getBlockState());
                        }
                    }

                    return true;
                }
            }
        } else {
            return false;
        }
    }

    public enum MushroomType {
        RED,
        BROWN
    }
}
