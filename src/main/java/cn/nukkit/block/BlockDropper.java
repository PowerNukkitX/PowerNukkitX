package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityDropper;
import cn.nukkit.dispenser.DispenseBehavior;
import cn.nukkit.dispenser.DropperDispenseBehavior;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.FACING_DIRECTION;
import static cn.nukkit.block.property.CommonBlockProperties.TRIGGERED_BIT;


public class BlockDropper extends BlockDispenser {

    public static final BlockProperties $1 = new BlockProperties(DROPPER, FACING_DIRECTION, TRIGGERED_BIT);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockDropper() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockDropper(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Dropper";
    }

    @Override
    @NotNull
    public Class<? extends BlockEntityDropper> getBlockEntityClass() {
        return BlockEntityDropper.class;
    }

    @Override
    @NotNull
    /**
     * @deprecated 
     */
    
    public String getBlockEntityType() {
        return BlockEntity.DROPPER;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void dispense() {
        super.dispense();
    }

    @Override
    protected DispenseBehavior getDispenseBehavior(Item item) {
        return new DropperDispenseBehavior();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 3.5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 3.5;
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
    
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }
}
