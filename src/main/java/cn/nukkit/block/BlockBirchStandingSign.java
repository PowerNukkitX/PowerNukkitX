package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBirchStandingSign extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:birch_standing_sign", CommonBlockProperties.GROUND_SIGN_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBirchStandingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBirchStandingSign(BlockState blockstate) {
        super(blockstate);
    }
}