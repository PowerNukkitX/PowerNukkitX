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

    public static final BlockProperties PROPERTIES = new BlockProperties(DROPPER, FACING_DIRECTION, TRIGGERED_BIT);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDropper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDropper(BlockState blockstate) {
        super(blockstate);
    }

    @Override
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
    public String getBlockEntityType() {
        return BlockEntity.DROPPER;
    }

    @Override
    public void dispense() {
        super.dispense();
    }

    @Override
    protected DispenseBehavior getDispenseBehavior(Item item) {
        return new DropperDispenseBehavior();
    }

    @Override
    public double getResistance() {
        return 3.5;
    }

    @Override
    public double getHardness() {
        return 3.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }
}
