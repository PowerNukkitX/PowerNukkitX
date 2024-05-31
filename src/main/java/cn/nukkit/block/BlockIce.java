package cn.nukkit.block;

import cn.nukkit.event.block.BlockFadeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockIce extends BlockTransparent {
    public static final BlockProperties $1 = new BlockProperties(ICE);
    /**
     * @deprecated 
     */
    

    public BlockIce() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockIce(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Ice";
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 2.5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0.5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getFrictionFactor() {
        return 0.98;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onBreak(Item item) {
        if (level.getDimension() == Level.DIMENSION_NETHER 
                || item.getEnchantmentLevel(Enchantment.ID_SILK_TOUCH) > 0 
                || down().isAir()) {
            return super.onBreak(item);
        }
        
        return level.setBlock(this, Block.get(BlockID.FLOWING_WATER), true);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (level.getBlockLightAt((int) this.x, (int) this.y, (int) this.z) >= 12) {
                BlockFadeEvent $2 = new BlockFadeEvent(this, level.getDimension() == Level.DIMENSION_NETHER ? get(AIR) : get(FLOWING_WATER));
                level.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    level.setBlock(this, event.getNewState(), true);
                }
                return Level.BLOCK_UPDATE_RANDOM;
            }
        }
        return 0;
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBurnChance() {
        return -1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getLightFilter() {
        return 2;
    }
}
