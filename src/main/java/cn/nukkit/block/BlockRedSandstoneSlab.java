package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockRedSandstoneSlab extends BlockSlab {

    public static final BlockProperties PROPERTIES = new BlockProperties(RED_SANDSTONE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    public BlockRedSandstoneSlab(BlockState blockState, BlockState doubleSlab) {
        super(blockState, doubleSlab);
    }

    @Override
    public String getSlabName() {
        return "Red Sandstone";
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
