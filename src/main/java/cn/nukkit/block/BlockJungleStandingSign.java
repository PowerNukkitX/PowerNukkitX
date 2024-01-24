package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemJungleSign;
import org.jetbrains.annotations.NotNull;

public class BlockJungleStandingSign extends BlockStandingSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(JUNGLE_STANDING_SIGN, CommonBlockProperties.GROUND_SIGN_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockJungleStandingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockJungleStandingSign(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getWallSignId() {
        return BlockJungleWallSign.PROPERTIES.getIdentifier();
    }

    @Override
    public Item toItem() {
        return new ItemJungleSign();
    }
}