package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemMangroveSign;
import cn.nukkit.item.ItemPaleOakSign;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.FACING_DIRECTION;


public class BlockPaleOakWallSign extends BlockWallSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(PALE_OAK_WALL_SIGN, FACING_DIRECTION);

    public BlockPaleOakWallSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPaleOakWallSign(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getWallSignId() {
        return PALE_OAK_WALL_SIGN;
    }

    @Override
    public String getStandingSignId() {
        return PALE_OAK_STANDING_SIGN;
    }

    @Override
    public String getName() {
        return "Pale Oak Wall Sign";
    }

    @Override
    public Item toItem() {
        return new ItemPaleOakSign();
    }
}
