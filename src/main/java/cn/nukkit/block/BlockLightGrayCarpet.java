package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightGrayCarpet extends BlockCarpet {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:light_gray_carpet");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightGrayCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightGrayCarpet(BlockState blockstate) {
        super(blockstate);
    }
}