package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCrimsonStandingSign extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:crimson_standing_sign", CommonBlockProperties.GROUND_SIGN_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonStandingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonStandingSign(BlockState blockstate) {
        super(blockstate);
    }
}