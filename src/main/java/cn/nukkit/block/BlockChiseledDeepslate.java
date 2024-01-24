package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockChiseledDeepslate extends BlockCobbledDeepslate {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHISELED_DEEPSLATE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockChiseledDeepslate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockChiseledDeepslate(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Chiseled Deepslate";
    }
}