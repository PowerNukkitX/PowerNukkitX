package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockRedNetherBrickSlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(RED_NETHER_BRICK_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    public BlockRedNetherBrickSlab(BlockState blockState, BlockState doubleSlab) {
        super(blockState, doubleSlab);
    }

    @Override
    public String getSlabName() {
        return "Red Nether Brick";
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
