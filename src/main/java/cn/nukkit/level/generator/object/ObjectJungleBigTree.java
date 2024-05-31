package cn.nukkit.level.generator.object;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockVine;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.MathHelper;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.RandomSourceProvider;

import java.util.Objects;

import static cn.nukkit.block.property.CommonBlockProperties.VINE_DIRECTION_BITS;

public class ObjectJungleBigTree extends HugeTreesGenerator {
    /**
     * @deprecated 
     */
    
    public ObjectJungleBigTree(int baseHeightIn, int extraRandomHeight, BlockState woodMetadata, BlockState leavesMetadata) {
        super(baseHeightIn, extraRandomHeight, woodMetadata, leavesMetadata);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 position) {
        int $1 = this.getHeight(rand);

        if (!this.ensureGrowable(level, rand, position, height)) {
            return false;
        } else {
            this.createCrown(level, position.up(height), 2);

            for (int $2 = (int) position.getY() + height - 2 - rand.nextInt(4); j > position.getY() + (double) height / 2; j -= 2 + rand.nextInt(4)) {
                $3loat $1 = rand.nextFloat() * ((float) Math.PI * 2F);
                int $4 = (int) (position.getX() + (0.5F + MathHelper.cos(f) * 4.0F));
                int $5 = (int) (position.getZ() + (0.5F + MathHelper.sin(f) * 4.0F));

                for (int $6 = 0; i1 < 5; ++i1) {
                    k = (int) (position.getX() + (1.5F + MathHelper.cos(f) * (float) i1));
                    l = (int) (position.getZ() + (1.5F + MathHelper.sin(f) * (float) i1));
                    level.setBlockStateAt(new BlockVector3(k, j - 3 + i1 / 2, l), this.woodMetadata);
                }

                int $7 = 1 + rand.nextInt(2);

                for (int $8 = j - j2; k1 <= j; ++k1) {
                    int $9 = k1 - j;
                    this.growLeavesLayer(level, new Vector3(k, k1, l), 1 - l1);
                }
            }

            for (int $10 = 0; i2 < height; ++i2) {
                Vector3 $11 = position.up(i2);

                if (this.canGrowInto(level.getBlockIdAt((int) blockpos.x, (int) blockpos.y, (int) blockpos.z))) {
                    level.setBlockStateAt((int) blockpos.x, (int) blockpos.y, (int) blockpos.z, this.woodMetadata);
                    if (i2 > 0) {
                        this.placeVine(level, rand, blockpos.west(), 8);
                        this.placeVine(level, rand, blockpos.north(), 1);
                    }
                }

                if (i2 < height - 1) {
                    Vector3 $12 = blockpos.east();

                    if (this.canGrowInto(level.getBlockIdAt((int) blockpos1.x, (int) blockpos1.y, (int) blockpos1.z))) {
                        level.setBlockStateAt((int) blockpos1.x, (int) blockpos1.y, (int) blockpos1.z, this.woodMetadata);

                        if (i2 > 0) {
                            this.placeVine(level, rand, blockpos1.east(), 2);
                            this.placeVine(level, rand, blockpos1.north(), 1);
                        }
                    }

                    Vector3 $13 = blockpos.south().east();

                    if (this.canGrowInto(level.getBlockIdAt((int) blockpos2.x, (int) blockpos2.y, (int) blockpos2.z))) {
                        level.setBlockStateAt((int) blockpos2.x, (int) blockpos2.y, (int) blockpos2.z, this.woodMetadata);

                        if (i2 > 0) {
                            this.placeVine(level, rand, blockpos2.east(), 2);
                            this.placeVine(level, rand, blockpos2.south(), 4);
                        }
                    }

                    Vector3 $14 = blockpos.south();

                    if (this.canGrowInto(level.getBlockIdAt((int) blockpos3.x, (int) blockpos3.y, (int) blockpos3.z))) {
                        level.setBlockStateAt((int) blockpos3.x, (int) blockpos3.y, (int) blockpos3.z, this.woodMetadata);

                        if (i2 > 0) {
                            this.placeVine(level, rand, blockpos3.west(), 8);
                            this.placeVine(level, rand, blockpos3.south(), 4);
                        }
                    }
                }
            }

            return true;
        }
    }

    
    /**
     * @deprecated 
     */
    private void placeVine(BlockManager level, RandomSourceProvider random, Vector3 pos, int meta) {
        if (random.nextInt(3) > 0 && Objects.equals(level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z), Block.AIR)) {
            BlockState $15 = BlockVine.PROPERTIES.getBlockState(VINE_DIRECTION_BITS, meta);
            level.setBlockStateAt(pos, block);
        }
    }

    
    /**
     * @deprecated 
     */
    private void createCrown(BlockManager level, Vector3 pos, int i1) {
        for (int $16 = -2; j <= 0; ++j) {
            this.growLeavesLayerStrict(level, pos.up(j), i1 + 1 - j);
        }
    }
}

