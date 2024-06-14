package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedBlackstoneBrickDoubleSlab extends BlockPolishedBlackstoneDoubleSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_BLACKSTONE_BRICK_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedBlackstoneBrickDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedBlackstoneBrickDoubleSlab(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getSlabName() {
        return "Polished Blackstone Brick";
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public BlockState getSingleSlab() {
        return BlockPolishedBlackstoneBrickSlab.PROPERTIES.getDefaultState();
    }
}