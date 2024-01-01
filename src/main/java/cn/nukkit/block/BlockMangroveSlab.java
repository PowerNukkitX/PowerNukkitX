package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockMangroveSlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(MANGROVE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMangroveSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMangroveSlab(BlockState blockstate) {
        super(blockstate, MANGROVE_DOUBLE_SLAB);
    }

    @Override
    public String getName() {
        return (isOnTop() ? "Upper " : "") + getSlabName() + " Wood Slab";
    }

    @Override
    public String getSlabName() {
        return "Mangrove";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(getId());
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

}