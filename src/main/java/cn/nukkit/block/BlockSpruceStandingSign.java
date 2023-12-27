package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockSpruceStandingSign extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:spruce_standing_sign", CommonBlockProperties.GROUND_SIGN_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSpruceStandingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSpruceStandingSign(BlockState blockstate) {
        super(blockstate);
    }
}