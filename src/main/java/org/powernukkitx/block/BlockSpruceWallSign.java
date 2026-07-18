package org.powernukkitx.block;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemSpruceSign;
import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.FACING_DIRECTION;


public class BlockSpruceWallSign extends BlockWallSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(SPRUCE_WALL_SIGN, FACING_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSpruceWallSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSpruceWallSign(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Spruce Wall Sign";
    }

    @Override
    public String getWallSignId() {
        return SPRUCE_WALL_SIGN;
    }

    @Override
    public String getStandingSignId() {
        return SPRUCE_STANDING_SIGN;
    }

    @Override
    public Item toItem() {
        return new ItemSpruceSign();
    }
}
