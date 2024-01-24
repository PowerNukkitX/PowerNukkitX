package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemJungleSign;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.FACING_DIRECTION;


public class BlockJungleWallSign extends BlockWallSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(JUNGLE_WALL_SIGN, FACING_DIRECTION);

    public BlockJungleWallSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockJungleWallSign(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getWallSignId() {
        return JUNGLE_WALL_SIGN;
    }

    @Override
    public String getStandingSignId() {
        return JUNGLE_STANDING_SIGN;
    }

    @Override
    public String getName() {
        return "Jungle Wall Sign";
    }

    @Override
    public Item toItem() {
        return new ItemJungleSign();
    }
}
