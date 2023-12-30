package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightGrayTerracotta extends BlockTerracotta {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:light_gray_terracotta");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightGrayTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightGrayTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}