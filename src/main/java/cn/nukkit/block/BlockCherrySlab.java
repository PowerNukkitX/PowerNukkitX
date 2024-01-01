package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockCherrySlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHERRY_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCherrySlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCherrySlab(BlockState blockstate) {
        super(blockstate, CHERRY_DOUBLE_SLAB);
    }

    @Override
    public String getSlabName() {
        return "Cherry";
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

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }
}