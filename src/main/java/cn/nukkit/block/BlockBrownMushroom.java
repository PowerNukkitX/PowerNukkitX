package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBrownMushroom extends BlockMushroom {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:brown_mushroom");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrownMushroom() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrownMushroom(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getLightLevel() {
        return 1;
    }

    @Override
    protected int getType() {
        return 0;
    }
}