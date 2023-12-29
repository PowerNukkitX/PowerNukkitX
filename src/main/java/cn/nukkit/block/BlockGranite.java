package cn.nukkit.block;

import cn.nukkit.block.property.enums.StoneType;
import org.jetbrains.annotations.NotNull;

public class BlockGranite extends BlockStone {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:granite");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGranite() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGranite(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public StoneType stoneType() {
        return StoneType.GRANITE;
    }
}