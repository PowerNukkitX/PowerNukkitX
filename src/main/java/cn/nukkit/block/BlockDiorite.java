package cn.nukkit.block;

import cn.nukkit.block.property.enums.StoneType;
import org.jetbrains.annotations.NotNull;

public class BlockDiorite extends BlockStone {
    public static final BlockProperties PROPERTIES = new BlockProperties(DIORITE);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDiorite() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDiorite(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public StoneType stoneType() {
        return StoneType.DIORITE;
    }
}