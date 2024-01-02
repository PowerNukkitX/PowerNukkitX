package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIronDoor;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockIronDoor extends BlockWoodenDoor {
    public static final BlockProperties PROPERTIES = new BlockProperties(IRON_DOOR, CommonBlockProperties.DIRECTION, CommonBlockProperties.DOOR_HINGE_BIT, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockIronDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockIronDoor(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Iron Door Block";
    }

    @Override
    public double getHardness() {
        return 5;
    }

    @Override
    public double getResistance() {
        return 25;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public Item toItem() {
        return new ItemIronDoor();
    }
}