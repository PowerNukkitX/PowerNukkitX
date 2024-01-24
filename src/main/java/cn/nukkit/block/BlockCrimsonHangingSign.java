package cn.nukkit.block;


import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCrimsonHangingSign extends BlockHangingSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(CRIMSON_HANGING_SIGN, CommonBlockProperties.ATTACHED_BIT, CommonBlockProperties.FACING_DIRECTION, CommonBlockProperties.GROUND_SIGN_DIRECTION, CommonBlockProperties.HANGING);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonHangingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonHangingSign(BlockState blockstate) {
        super(blockstate);
    }

    public String getName() {
        return "Crimson Hanging Sign";
    }
}