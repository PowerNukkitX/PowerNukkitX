package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDarkPrismarineSlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(DARK_PRISMARINE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    public BlockDarkPrismarineSlab(BlockState blockState, BlockState doubleSlab) {
        super(blockState, doubleSlab);
    }

    @Override
    public String getSlabName() {
        return "Dark Prismarine Slab";
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
