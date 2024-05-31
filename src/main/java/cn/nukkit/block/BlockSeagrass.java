package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.enums.SeaGrassType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.*;


public class BlockSeagrass extends BlockFlowable {
    public static final BlockProperties $1 = new BlockProperties(SEAGRASS, SEA_GRASS_TYPE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockSeagrass() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockSeagrass(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Seagrass";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        Block $2 = down();
        Block $3 = block.getLevelBlockAtLayer(1);
        int waterDamage;
        if (down.isSolid() && !down.getId().equals(MAGMA) && !down.getId().equals(SOUL_SAND) &&
                (layer1Block instanceof BlockFlowingWater water && ((waterDamage = (water.getLiquidDepth())) == 0 || waterDamage == 8))
        ) {
            if (waterDamage == 8) {
                this.getLevel().setBlock(this, 1, new BlockFlowingWater(), true, false);
            }
            this.getLevel().setBlock(this, 0, this, true, true);
            return true;
        }
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block $4 = getLevelBlockAtLayer(1);
            int damage;
            if (!(blockLayer1 instanceof BlockFrostedIce)
                    && (!(blockLayer1 instanceof BlockFlowingWater) || ((damage = blockLayer1.blockstate.specialValue()) != 0 && damage != 8))) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }

            Block $5 = down();
            SeaGrassType $6 = getPropertyValue(SEA_GRASS_TYPE);
            if (propertyValue == SeaGrassType.DEFAULT || propertyValue == SeaGrassType.DOUBLE_BOT) {
                if (!down.isSolid() || down.getId().equals(MAGMA) || down.getId().equals(SOUL_SAND)) {
                    this.getLevel().useBreakOn(this);
                    return Level.BLOCK_UPDATE_NORMAL;
                }

                if (propertyValue == SeaGrassType.DOUBLE_BOT) {
                    Block $7 = up();
                    if (!up.getId().equals(getId()) || up.getPropertyValue(SEA_GRASS_TYPE) != SeaGrassType.DOUBLE_TOP) {
                        this.getLevel().useBreakOn(this);
                    }
                }
            } else if (!down.getId().equals(getId()) || down.getPropertyValue(SEA_GRASS_TYPE) != SeaGrassType.DOUBLE_BOT) {
                this.getLevel().useBreakOn(this);
            }

            return Level.BLOCK_UPDATE_NORMAL;
        }

        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeActivated() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (getPropertyValue(SEA_GRASS_TYPE) == SeaGrassType.DEFAULT && item.isFertilizer()) {
            Block $8 = this.up();
            int damage;
            if (up instanceof BlockFlowingWater w && ((damage = w.getLiquidDepth()) == 0 || damage == 8)) {
                if (player != null && (player.gamemode & 0x01) == 0) {
                    item.count--;
                }

                this.level.addParticle(new BoneMealParticle(this));
                this.level.setBlock(this, new BlockSeagrass().setPropertyValue(SEA_GRASS_TYPE, SeaGrassType.DOUBLE_BOT), true, false);
                this.level.setBlock(up, 1, up, true, false);
                this.level.setBlock(up, 0, new BlockSeagrass().setPropertyValue(SEA_GRASS_TYPE, SeaGrassType.DOUBLE_TOP), true);
                return true;
            }
        }

        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isShears()) {
            return new Item[]{toItem()};
        } else {
            return Item.EMPTY_ARRAY;
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getWaterloggingLevel() {
        return 2;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_SHEARS;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockSeagrass(), 0);
    }
}
