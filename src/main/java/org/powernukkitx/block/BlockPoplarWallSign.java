package org.powernukkitx.block;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemPoplarSign;
import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.FACING_DIRECTION;

public class BlockPoplarWallSign extends BlockWallSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(POPLAR_WALL_SIGN, FACING_DIRECTION);

    public BlockPoplarWallSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPoplarWallSign(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getWallSignId() {
        return POPLAR_WALL_SIGN;
    }

    @Override
    public String getStandingSignId() {
        return POPLAR_STANDING_SIGN;
    }

    @Override
    public String getName() {
        return "Poplar Wall Sign";
    }

    @Override
    public Item toItem() {
        return new ItemPoplarSign();
    }
}
