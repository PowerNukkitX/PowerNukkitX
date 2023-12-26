package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockStandingBanner extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:standing_banner", CommonBlockProperties.GROUND_SIGN_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStandingBanner() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStandingBanner(BlockState blockstate) {
        super(blockstate);
    }
}