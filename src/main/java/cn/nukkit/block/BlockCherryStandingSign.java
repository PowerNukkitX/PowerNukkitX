package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCherryStandingSign extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:cherry_standing_sign", CommonBlockProperties.GROUND_SIGN_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCherryStandingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCherryStandingSign(BlockState blockstate) {
        super(blockstate);
    }
}