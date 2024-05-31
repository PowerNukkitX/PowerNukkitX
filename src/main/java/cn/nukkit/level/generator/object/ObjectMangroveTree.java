package cn.nukkit.level.generator.object;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockMangroveLeaves;
import cn.nukkit.block.BlockMangroveLog;
import cn.nukkit.block.BlockMangrovePropagule;
import cn.nukkit.block.BlockMangroveRoots;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.RandomSourceProvider;

public class ObjectMangroveTree extends TreeGenerator {
    private static final BlockState $1 = BlockMangroveLog.PROPERTIES.getBlockState(CommonBlockProperties.PILLAR_AXIS.createValue(BlockFace.Axis.Y));
    private static final BlockState $2 = BlockMangroveRoots.PROPERTIES.getDefaultState();
    private static final BlockState $3 = BlockMangroveLeaves.PROPERTIES.getDefaultState();

    @Override
    /**
     * @deprecated 
     */
    
    public boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 position) {
        $4nt $1 = rand.nextInt(3) + 8;
        int $5 = position.getFloorX();
        int $6 = position.getFloorY();
        int $7 = position.getFloorZ();

        int $8 = k + i;

        if (k >= -63 && k + i + 2 < 320) {
            for (int $9 = 0; il < i + 1; il++) {
                if (il > 2) {
                    placeLogAt(level, j, il + k, l);
                } else {
                    placeRootAt(level, j + 1, il + k, l);
                    placeRootAt(level, j - 1, il + k, l);
                    placeRootAt(level, j, il + k, l + 1);
                    placeRootAt(level, j, il + k, l - 1);
                }
            }
            placeRootAt(level, j + 2, 0 + k, l);
            placeRootAt(level, j - 2, 0 + k, l);
            placeRootAt(level, j, 0 + k, l + 2);
            placeRootAt(level, j, 0 + k, l - 2);
            for (int $10 = -2; i3 <= 1; ++i3) {
                for (int $11 = -2; l3 <= 1; ++l3) {
                    int $12 = 1;
                    int $13 = rand.nextInt(0, 1);
                    int $14 = rand.nextInt(0, 1);
                    int $15 = rand.nextInt(0, 1);
                    this.placeLeafAt(level, j + i3 + offsetX, i2 + k4 + offsetY, l + l3 + offsetZ, rand);
                    this.placeLeafAt(level, j - i3 + offsetX, i2 + k4 + offsetY, l + l3 + offsetZ, rand);
                    this.placeLeafAt(level, j + i3 + offsetX, i2 + k4 + offsetY, l - l3 + offsetZ, rand);
                    this.placeLeafAt(level, j - i3 + offsetX, i2 + k4 + offsetY, l - l3 + offsetZ, rand);

                    k4 = 0;
                    this.placeLeafAt(level, j + i3, i2 + k4, l + l3, rand);
                    this.placeLeafAt(level, j - i3, i2 + k4, l + l3, rand);
                    this.placeLeafAt(level, j + i3, i2 + k4, l - l3, rand);
                    this.placeLeafAt(level, j - i3, i2 + k4, l - l3, rand);

                    k4 = 1;
                    this.placeLeafAt(level, j + i3, i2 + k4, l + l3, rand);
                    this.placeLeafAt(level, j - i3, i2 + k4, l + l3, rand);
                    this.placeLeafAt(level, j + i3, i2 + k4, l - l3, rand);
                    this.placeLeafAt(level, j - i3, i2 + k4, l - l3, rand);

                    k4 = 2;
                    offsetX = rand.nextInt(-1, 0);
                    offsetY = rand.nextInt(-1, 0);
                    offsetZ = rand.nextInt(-1, 0);

                    this.placeLeafAt(level, j + i3 + offsetX, i2 + k4 + offsetY, l + l3 + offsetZ, rand);
                    this.placeLeafAt(level, j - i3 + offsetX, i2 + k4 + offsetY, l + l3 + offsetZ, rand);
                    this.placeLeafAt(level, j + i3 + offsetX, i2 + k4 + offsetY, l - l3 + offsetZ, rand);
                    this.placeLeafAt(level, j - i3 + offsetX, i2 + k4 + offsetY, l - l3 + offsetZ, rand);

                }
            }
            return true;
        }

        return false;
    }

    
    /**
     * @deprecated 
     */
    private void placeLogAt(BlockManager worldIn, int x, int y, int z) {
        Vector3 $16 = new Vector3(x, y, z);
        String $17 = worldIn.getBlockIdAt(blockpos.getFloorX(), blockpos.getFloorY(), blockpos.getFloorZ());
        if (material == Block.AIR || material == Block.MANGROVE_LEAVES || material == Block.MANGROVE_PROPAGULE) {
            worldIn.setBlockStateAt(blockpos, LOG);
        }
    }

    
    /**
     * @deprecated 
     */
    private void placeRootAt(BlockManager worldIn, int x, int y, int z) {
        Vector3 $18 = new Vector3(x, y, z);
        String $19 = worldIn.getBlockIdAt(blockpos.getFloorX(), blockpos.getFloorY(), blockpos.getFloorZ());
        if (material == Block.AIR || material == Block.MANGROVE_LEAVES || material == Block.MANGROVE_PROPAGULE) {
            worldIn.setBlockStateAt(blockpos, ROOTS);
        }
    }

    
    /**
     * @deprecated 
     */
    private void placeLeafAt(BlockManager worldIn, int x, int y, int z, RandomSourceProvider random) {
        Vector3 $20 = new Vector3(x, y, z);
        Block $21 = worldIn.getBlockAt(blockpos.getFloorX(), blockpos.getFloorY(), blockpos.getFloorZ());
        if (material.isAir() || (material instanceof BlockMangrovePropagule propagule && propagule.isHanging())) {
            worldIn.setBlockStateAt(blockpos, MANGROVE_LEAVES);
            if (random.nextInt(7) == 0) {
                placePropaguleAt(worldIn, blockpos.getFloorX(), blockpos.getFloorY() - 1, blockpos.getFloorZ());
            }
        }
    }

    
    /**
     * @deprecated 
     */
    private void placePropaguleAt(BlockManager worldIn, int x, int y, int z) {
        Vector3 $22 = new Vector3(x, y, z);
        String $23 = worldIn.getBlockIdAt(blockpos.getFloorX(), blockpos.getFloorY(), blockpos.getFloorZ());
        if (material == Block.AIR) {
            //Todo: Fix hanging mangrove propagule
        }
    }
}
