package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemWarpedSign;
import org.jetbrains.annotations.NotNull;

public class BlockWarpedStandingSign extends BlockStandingSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(WARPED_STANDING_SIGN, CommonBlockProperties.GROUND_SIGN_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedStandingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedStandingSign(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getWallSignId() {
        return BlockWarpedWallSign.PROPERTIES.getIdentifier();
    }

    @Override
    public Item toItem() {
        return new ItemWarpedSign();
    }
}