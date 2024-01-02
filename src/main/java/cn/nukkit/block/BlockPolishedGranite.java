package cn.nukkit.block;

import cn.nukkit.block.property.enums.StoneType;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedGranite extends BlockStone {
    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_GRANITE);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedGranite() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedGranite(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public StoneType stoneType() {
        return StoneType.GRANITE_SMOOTH;
    }

    @Override
    public boolean canSilkTouch() {
        return false;
    }
}