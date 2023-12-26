package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockAcaciaStandingSign extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:acacia_standing_sign", CommonBlockProperties.GROUND_SIGN_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockAcaciaStandingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockAcaciaStandingSign(BlockState blockstate) {
        super(blockstate);
    }
}