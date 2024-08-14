package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.StoneSlabType;
import org.jetbrains.annotations.NotNull;

public class BlockSandstoneSlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(SANDSTONE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    public BlockSandstoneSlab(BlockState blockState, BlockState doubleSlab) {
        super(blockState, doubleSlab);
    }

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getSlabName() {
        return "Sandstone";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(this.getId());
    }
}