package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockAcaciaStandingSign extends BlockStandingSign {
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

    @Override
    protected String getStandingSignId() {
        return PROPERTIES.getIdentifier();
    }

    @Override
    public String getWallSignId() {
        return BlockAcaciaWallSign.PROPERTIES.getIdentifier();
    }
}