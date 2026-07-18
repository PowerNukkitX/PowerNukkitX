package org.powernukkitx.block;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemMangroveSign;
import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.FACING_DIRECTION;


public class BlockMangroveWallSign extends BlockWallSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(MANGROVE_WALL_SIGN, FACING_DIRECTION);

    public BlockMangroveWallSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMangroveWallSign(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getWallSignId() {
        return MANGROVE_WALL_SIGN;
    }

    @Override
    public String getStandingSignId() {
        return MANGROVE_STANDING_SIGN;
    }

    @Override
    public String getName() {
        return "Mangrove Wall Sign";
    }

    @Override
    public Item toItem() {
        return new ItemMangroveSign();
    }
}
