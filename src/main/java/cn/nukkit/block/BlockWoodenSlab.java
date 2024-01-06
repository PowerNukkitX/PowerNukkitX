package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.WoodType;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockWoodenSlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(WOODEN_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF, CommonBlockProperties.WOOD_TYPE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWoodenSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWoodenSlab(BlockState blockstate) {
        super(blockstate, DOUBLE_WOODEN_SLAB);
    }

    @Override
    public String getName() {
        return (isOnTop()? "Upper " : "") + getSlabName() + " Wood Slab";
    }

    @Override
    public String getSlabName() {
        return getWoodType().name();
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(getId()) && slab.getPropertyValue(CommonBlockProperties.WOOD_TYPE).equals(getWoodType());
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    public WoodType getWoodType() {
        return getPropertyValue(CommonBlockProperties.WOOD_TYPE);
    }

    public void setWoodType(WoodType type) {
        setPropertyValue(CommonBlockProperties.WOOD_TYPE, type);
    }
}