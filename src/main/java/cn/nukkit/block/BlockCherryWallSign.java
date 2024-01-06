package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemCherrySign;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.FACING_DIRECTION;


public class BlockCherryWallSign extends BlockWallSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHERRY_WALL_SIGN, FACING_DIRECTION);

    public BlockCherryWallSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCherryWallSign(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getWallSignId() {
        return CHERRY_WALL_SIGN;
    }

    @Override
    public String getStandingSignId() {
        return CHERRY_STANDING_SIGN;
    }

    @Override
    public Item toItem() {
        return new ItemCherrySign();
    }
}
