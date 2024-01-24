package cn.nukkit.block;

import cn.nukkit.block.property.enums.StoneType;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedDiorite extends BlockStone {
    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_DIORITE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedDiorite() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedDiorite(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public StoneType stoneType() {
        return StoneType.DIORITE_SMOOTH;
    }

    @Override
    public boolean canSilkTouch() {
        return false;
    }
}