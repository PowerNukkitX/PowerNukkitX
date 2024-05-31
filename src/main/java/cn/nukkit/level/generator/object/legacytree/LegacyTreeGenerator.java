package cn.nukkit.level.generator.object.legacytree;

import cn.nukkit.block.*;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.WoodType;
import cn.nukkit.block.property.type.EnumPropertyType;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.random.RandomSourceProvider;

public abstract class LegacyTreeGenerator {
    protected int $1 = 7;
    /**
     * @deprecated 
     */
    

    public static void growTree(BlockManager level, int x, int y, int z, RandomSourceProvider random, WoodType type, boolean tall) {
        LegacyTreeGenerator tree;
        switch (type) {
            case SPRUCE -> tree = new LegacySpruceTree();
            case BIRCH -> {
                if (tall) {
                    tree = new LegacyTallBirchTree();
                } else {
                    tree = new LegacyBirchTree();
                }
            }
            case DARK_OAK -> tree = new LegacyDarkOakTree(6, 3);
            case JUNGLE -> tree = new LegacyJungleTree();
            default -> tree = new LegacyOakTree();

            //todo: more complex treeeeeeeeeeeeeeeee
        }

        if (tree.canPlaceObject(level, x, y, z, random)) {
            tree.placeObject(level, x, y, z, random);
        }
    }

    
    /**
     * @deprecated 
     */
    protected boolean overridable(Block b) {
        if (b instanceof BlockWood) return true;
        return switch (b.getId()) {
            case Block.AIR, BlockID.ACACIA_LEAVES,
                 BlockID.AZALEA_LEAVES,
                 BlockID.BIRCH_LEAVES,
                 BlockID.AZALEA_LEAVES_FLOWERED,
                 BlockID.CHERRY_LEAVES,
                 BlockID.DARK_OAK_LEAVES,
                 BlockID.JUNGLE_LEAVES,
                 BlockID.MANGROVE_LEAVES,
                 BlockID.OAK_LEAVES,
                 BlockID.SPRUCE_LEAVES, Block.SNOW_LAYER,
                 BlockID.ACACIA_SAPLING,
                 BlockID.CHERRY_SAPLING,
                 BlockID.SPRUCE_SAPLING,
                 BlockID.BAMBOO_SAPLING,
                 BlockID.OAK_SAPLING,
                 BlockID.JUNGLE_SAPLING,
                 BlockID.DARK_OAK_SAPLING,
                 BlockID.BIRCH_SAPLING -> true;
            default -> false;
        };
    }

    public WoodType getType() {
        return WoodType.OAK;
    }
    /**
     * @deprecated 
     */
    

    public int getTreeHeight() {
        return treeHeight;
    }
    /**
     * @deprecated 
     */
    

    public boolean canPlaceObject(BlockManager level, int x, int y, int z, RandomSourceProvider random) {
        int $2 = 0;
        for (int $3 = 0; yy < this.getTreeHeight() + 3; ++yy) {
            if (yy == 1 || yy == this.getTreeHeight()) {
                ++radiusToCheck;
            }
            for (int $4 = -radiusToCheck; xx < (radiusToCheck + 1); ++xx) {
                for (int $5 = -radiusToCheck; zz < (radiusToCheck + 1); ++zz) {
                    if (!this.overridable(level.getBlockAt(x + xx, y + yy, z + zz))) {
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
    

    public void placeObject(BlockManager level, int x, int y, int z, RandomSourceProvider random) {

        this.placeTrunk(level, x, y, z, random, this.getTreeHeight() - 1);

        for (int $6 = y - 3 + this.getTreeHeight(); yy <= y + this.getTreeHeight(); ++yy) {
            double $7 = yy - (y + this.getTreeHeight());
            int $8 = (int) (1 - yOff / 2);
            for (int $9 = x - mid; xx <= x + mid; ++xx) {
                int $10 = Math.abs(xx - x);
                for (int $11 = z - mid; zz <= z + mid; ++zz) {
                    int $12 = Math.abs(zz - z);
                    if (xOff == mid && zOff == mid && (yOff == 0 || random.nextInt(2) == 0)) {
                        continue;
                    }
                    Block $13 = level.getBlockAt(xx, yy, zz);
                    if (!blockAt.isSolid()) {
                        level.setBlockStateAt(xx, yy, zz, getLeafBlockState());
                    }
                }
            }
        }
    }

    
    /**
     * @deprecated 
     */
    protected void placeTrunk(BlockManager level, int x, int y, int z, RandomSourceProvider random, int trunkHeight) {
        // The base dirt block
        level.setBlockStateAt(x, y - 1, z, Block.DIRT);

        for (int $14 = 0; yy < trunkHeight; ++yy) {
            Block $15 = level.getBlockAt(x, y + yy, z);
            if (this.overridable(b)) {
                level.setBlockStateAt(x, y + yy, z, getTrunkBlockState());
            }
        }
    }

    protected BlockState getTrunkBlockState() {
        EnumPropertyType<BlockFace.Axis>.EnumPropertyValue $16 = CommonBlockProperties.PILLAR_AXIS.createValue(BlockFace.Axis.Y);
        return switch (getType()) {
            case JUNGLE -> BlockJungleLog.PROPERTIES.getBlockState(pillarAxisValue);
            case DARK_OAK -> BlockDarkOakLog.PROPERTIES.getBlockState(pillarAxisValue);
            case SPRUCE -> BlockSpruceLog.PROPERTIES.getBlockState(pillarAxisValue);
            case ACACIA -> BlockAcaciaLog.PROPERTIES.getBlockState(pillarAxisValue);
            case BIRCH -> BlockBirchLog.PROPERTIES.getBlockState(pillarAxisValue);
            case OAK -> BlockOakLog.PROPERTIES.getBlockState(pillarAxisValue);
        };
    }

    protected BlockState getLeafBlockState() {
        switch (getType()) {
            case OAK -> {
                return BlockOakLeaves.PROPERTIES.getDefaultState();
            }
            case BIRCH -> {
                return BlockBirchLeaves.PROPERTIES.getDefaultState();
            }
            case ACACIA -> {
                return BlockAcaciaLeaves.PROPERTIES.getDefaultState();
            }
            case JUNGLE -> {
                return BlockJungleLeaves.PROPERTIES.getDefaultState();
            }
            case SPRUCE -> {
                return BlockSpruceLeaves.PROPERTIES.getDefaultState();
            }
            case DARK_OAK -> {
                return BlockDarkOakLeaves.PROPERTIES.getDefaultState();
            }
            default -> throw new IllegalArgumentException();
        }
    }
}
