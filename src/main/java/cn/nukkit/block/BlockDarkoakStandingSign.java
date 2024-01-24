package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDarkOakSign;
import org.jetbrains.annotations.NotNull;

public class BlockDarkoakStandingSign extends BlockStandingSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(DARKOAK_STANDING_SIGN, CommonBlockProperties.GROUND_SIGN_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDarkoakStandingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDarkoakStandingSign(BlockState blockstate) {
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