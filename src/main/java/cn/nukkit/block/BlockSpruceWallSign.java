package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemSpruceSign;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.FACING_DIRECTION;


public class BlockSpruceWallSign extends BlockWallSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(SPRUCE_WALL_SIGN, FACING_DIRECTION);

    public BlockSpruceWallSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSpruceWallSign(BlockState blockState) {
        super(blockState);
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
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
    public String getName() {
        return "Spruce Wall Sign";
    }

    @Override
    public Item toItem() {
        return new ItemSpruceSign();
    }
}
