package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMangroveStandingSign extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:mangrove_standing_sign", CommonBlockProperties.GROUND_SIGN_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMangroveStandingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMangroveStandingSign(BlockState blockstate) {
        super(blockstate);
    }
}