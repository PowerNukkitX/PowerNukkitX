package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPurpurSlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(PURPUR_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    public BlockPurpurSlab(BlockState blockState, BlockState doubleSlab) {
        super(blockState, doubleSlab);
    }

    @Override
    public String getSlabName() {
        return "Purpur";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(this.getId());
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }
}
