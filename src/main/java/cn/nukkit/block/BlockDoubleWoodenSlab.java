package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.WoodType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockDoubleWoodenSlab extends BlockDoubleSlabBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(DOUBLE_WOODEN_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF, CommonBlockProperties.WOOD_TYPE);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDoubleWoodenSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDoubleWoodenSlab(BlockState blockstate) {
        super(blockstate);
    }

    public WoodType getWoodType() {
        return getPropertyValue(CommonBlockProperties.WOOD_TYPE);
    }

    public void setWoodType(WoodType type) {
        setPropertyValue(CommonBlockProperties.WOOD_TYPE, type);
    }

    @Override
    public String getSlabName() {
        return getWoodType().name();
    }

    @Override
    public String getName() {
        return "Double " + getSlabName() + " Wood Slab";
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public String getSingleSlabId() {
        return WOODEN_SLAB;
    }
    @Override
    protected boolean isCorrectTool(Item item) {
        return true;
    }

}