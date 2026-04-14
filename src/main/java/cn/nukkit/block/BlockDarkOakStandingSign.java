package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDarkOakSign;
import org.jetbrains.annotations.NotNull;

public class BlockDarkOakStandingSign extends BlockStandingSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(DARKOAK_STANDING_SIGN, CommonBlockProperties.GROUND_SIGN_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDarkOakStandingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDarkOakStandingSign(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getWallSignId() {
        return BlockDarkoakWallSign.PROPERTIES.getIdentifier();
    }

    @Override
    public Item toItem() {
        return new ItemDarkOakSign();
    }
}