package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDarkoakStandingSign extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:darkoak_standing_sign", CommonBlockProperties.GROUND_SIGN_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDarkoakStandingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDarkoakStandingSign(BlockState blockstate) {
        super(blockstate);
    }
}