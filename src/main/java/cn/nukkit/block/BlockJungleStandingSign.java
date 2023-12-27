package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockJungleStandingSign extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:jungle_standing_sign", CommonBlockProperties.GROUND_SIGN_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockJungleStandingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockJungleStandingSign(BlockState blockstate) {
        super(blockstate);
    }
}