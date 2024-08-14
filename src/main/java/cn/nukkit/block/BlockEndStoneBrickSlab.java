package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockEndStoneBrickSlab extends BlockSlab {

    public static final BlockProperties PROPERTIES = new BlockProperties(END_STONE_BRICK_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    public BlockEndStoneBrickSlab(BlockState blockState, BlockState doubleSlab) {
        super(blockState, doubleSlab);
    }

    @Override
    public String getSlabName() {
        return "End Stone Brick";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(this.getId());
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 9;
    }
}
