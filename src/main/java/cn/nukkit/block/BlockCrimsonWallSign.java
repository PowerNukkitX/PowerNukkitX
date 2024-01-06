package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemCrimsonSign;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.FACING_DIRECTION;


public class BlockCrimsonWallSign extends BlockWallSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(CRIMSON_WALL_SIGN, FACING_DIRECTION);

    public BlockCrimsonWallSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonWallSign(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getWallSignId() {
        return CRIMSON_WALL_SIGN;
    }

    @Override
    public String getStandingSignId() {
        return CRIMSON_STANDING_SIGN;
    }

    @Override
    public Item toItem() {
        return new ItemCrimsonSign();
    }
}
