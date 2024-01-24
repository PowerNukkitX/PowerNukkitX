package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemWarpedSign;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.FACING_DIRECTION;


public class BlockWarpedWallSign extends BlockWallSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(WARPED_WALL_SIGN, FACING_DIRECTION);

    public BlockWarpedWallSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedWallSign(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getWallSignId() {
        return WARPED_WALL_SIGN;
    }

    @Override
    public String getStandingSignId() {
        return WARPED_STANDING_SIGN;
    }

    @Override
    public String getName() {
        return "Warped Wall Sign";
    }

    @Override
    public Item toItem() {
        return new ItemWarpedSign();
    }
}
