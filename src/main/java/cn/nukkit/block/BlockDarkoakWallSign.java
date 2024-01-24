package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDarkOakSign;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.FACING_DIRECTION;


public class BlockDarkoakWallSign extends BlockWallSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(DARKOAK_WALL_SIGN, FACING_DIRECTION);

    public BlockDarkoakWallSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDarkoakWallSign(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getWallSignId() {
        return DARKOAK_WALL_SIGN;
    }

    @Override
    public String getStandingSignId() {
        return DARKOAK_STANDING_SIGN;
    }

    @Override
    public Item toItem() {
        return new ItemDarkOakSign();
    }
}
