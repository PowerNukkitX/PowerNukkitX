package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemSpruceSign;
import org.jetbrains.annotations.NotNull;

public class BlockSpruceStandingSign extends BlockStandingSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(SPRUCE_STANDING_SIGN, CommonBlockProperties.GROUND_SIGN_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSpruceStandingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSpruceStandingSign(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getWallSignId() {
        return BlockSpruceWallSign.PROPERTIES.getIdentifier();
    }

    @Override
    public Item toItem() {
        return new ItemSpruceSign();
    }
}