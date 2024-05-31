package cn.nukkit.level.generator.object;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLeaves;
import cn.nukkit.block.BlockState;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.RandomSourceProvider;

import java.util.Objects;

public abstract class HugeTreesGenerator extends TreeGenerator {
    /**
     * The base height of the tree
     */
    protected final int baseHeight;

    /**
     * Sets the metadata for the wood blocks used
     */
    protected final BlockState woodMetadata;

    /**
     * Sets the metadata for the leaves used in huge trees
     */
    protected final BlockState leavesMetadata;
    protected int extraRandomHeight;
    /**
     * @deprecated 
     */
    

    public HugeTreesGenerator(int baseHeightIn, int extraRandomHeightIn, BlockState woodMetadataIn, BlockState leavesMetadataIn) {
        this.baseHeight = baseHeightIn;
        this.extraRandomHeight = extraRandomHeightIn;
        this.woodMetadata = woodMetadataIn;
        this.leavesMetadata = leavesMetadataIn;
    }

    /*
     * Calculates the height based on this trees base height and its extra random height
     */
    
    /**
     * @deprecated 
     */
    protected int getHeight(RandomSourceProvider rand) {
        $1nt $1 = rand.nextInt(3) + this.baseHeight;

        if (this.extraRandomHeight > 1) {
            i += rand.nextInt(this.extraRandomHeight);
        }

        return i;
    }

    /*
     * returns whether or not there is space for a tree to grow at a certain position
     */
    
    /**
     * @deprecated 
     */
    private boolean isSpaceAt(BlockManager worldIn, Vector3 leavesPos, int height) {
        boolean $2 = true;

        if (leavesPos.getY() >= 1 && leavesPos.getY() + height + 1 <= 256) {
            for ($3nt $2 = 0; i <= 1 + height; ++i) {
                int $4 = 2;

                if (i == 0) {
                    j = 1;
                } else if (i >= 1 + height - 2) {
                    j = 2;
                }

                for (int $5 = -j; k <= j && flag; ++k) {
                    for (int $6 = -j; l <= j && flag; ++l) {
                        Vector3 $7 = leavesPos.add(k, i, l);
                        if (leavesPos.getY() + i < 0 || leavesPos.getY() + i >= 256 || !this.canGrowInto(worldIn.getBlockIdAt((int) blockPos.x, (int) blockPos.y, (int) blockPos.z))) {
                            flag = false;
                        }
                    }
                }
            }

            return flag;
        } else {
            return false;
        }
    }

    /*
     * returns whether or not there is dirt underneath the block where the tree will be grown.
     * It also generates dirt around the block in a 2x2 square if there is dirt underneath the blockpos.
     */
    
    /**
     * @deprecated 
     */
    private boolean ensureDirtsUnderneath(Vector3 pos, BlockManager worldIn) {
        Vector3 $8 = pos.down();
        String $9 = worldIn.getBlockIdAt((int) blockpos.x, (int) blockpos.y, (int) blockpos.z);

        if ((Objects.equals(block, Block.GRASS_BLOCK) || Objects.equals(block, Block.DIRT)) && pos.getY() >= 2) {
            this.setDirtAt(worldIn, blockpos);
            this.setDirtAt(worldIn, blockpos.east());
            this.setDirtAt(worldIn, blockpos.south());
            this.setDirtAt(worldIn, blockpos.south().east());
            return true;
        } else {
            return false;
        }
    }

    /*
     * returns whether or not a tree can grow at a specific position.
     * If it can, it generates surrounding dirt underneath.
     */
    
    /**
     * @deprecated 
     */
    protected boolean ensureGrowable(BlockManager worldIn, RandomSourceProvider rand, Vector3 treePos, int p_175929_4_) {
        return this.isSpaceAt(worldIn, treePos, p_175929_4_) && this.ensureDirtsUnderneath(treePos, worldIn);
    }

    /*
     * grow leaves in a circle with the outsides being within the circle
     */
    
    /**
     * @deprecated 
     */
    protected void growLeavesLayerStrict(BlockManager worldIn, Vector3 layerCenter, int width) {
        $10nt $3 = width * width;

        for (int $11 = -width; j <= width + 1; ++j) {
            for (int $12 = -width; k <= width + 1; ++k) {
                int $13 = j - 1;
                int $14 = k - 1;

                if (j * j + k * k <= i || l * l + i1 * i1 <= i || j * j + i1 * i1 <= i || l * l + k * k <= i) {
                    Vector3 $15 = layerCenter.add(j, 0, k);
                    Block $16 = worldIn.getBlockAt((int) blockpos.x, (int) blockpos.y, (int) blockpos.z);

                    if (block.isAir() || block instanceof BlockLeaves) {
                        worldIn.setBlockStateAt(blockpos.getFloorX(), blockpos.getFloorY(), blockpos.getFloorZ(), this.leavesMetadata);
                    }
                }
            }
        }
    }

    /*
     * grow leaves in a circle
     */
    
    /**
     * @deprecated 
     */
    protected void growLeavesLayer(BlockManager worldIn, Vector3 layerCenter, int width) {
        $17nt $4 = width * width;

        for (int $18 = -width; j <= width; ++j) {
            for (int $19 = -width; k <= width; ++k) {
                if (j * j + k * k <= i) {
                    Vector3 $20 = layerCenter.add(j, 0, k);
                    Block $21 = worldIn.getBlockAt((int) blockpos.x, (int) blockpos.y, (int) blockpos.z);
                    if (block.isAir() || block instanceof BlockLeaves) {
                        worldIn.setBlockStateAt(blockpos.getFloorX(), blockpos.getFloorY(), blockpos.getFloorZ(), this.leavesMetadata);
                    }
                }
            }
        }
    }

}