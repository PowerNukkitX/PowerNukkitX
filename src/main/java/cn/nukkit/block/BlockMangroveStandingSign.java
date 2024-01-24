package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemMangroveSign;
import org.jetbrains.annotations.NotNull;

public class BlockMangroveStandingSign extends BlockStandingSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(MANGROVE_STANDING_SIGN, CommonBlockProperties.GROUND_SIGN_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMangroveStandingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMangroveStandingSign(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getWallSignId() {
        return BlockMangroveWallSign.PROPERTIES.getIdentifier();
    }

    @Override
    public Item toItem() {
        return new ItemMangroveSign();
    }
}