package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockStandingSign extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:standing_sign", CommonBlockProperties.GROUND_SIGN_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStandingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStandingSign(BlockState blockstate) {
        super(blockstate);
    }
}