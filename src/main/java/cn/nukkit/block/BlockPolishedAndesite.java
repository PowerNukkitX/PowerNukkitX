package cn.nukkit.block;

import cn.nukkit.block.property.enums.StoneType;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedAndesite extends BlockStone {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:polished_andesite");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedAndesite() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedAndesite(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public StoneType stoneType() {
        return StoneType.ANDESITE_SMOOTH;
    }

    @Override
    public boolean canSilkTouch() {
        return false;
    }
}