package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDandelion extends BlockFlower {
    public static final BlockProperties PROPERTIES = new BlockProperties(DANDELION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDandelion() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDandelion(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public Block getUncommonFlower() {
        return get(RED_TULIP);
    }
}