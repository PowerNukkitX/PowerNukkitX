package cn.nukkit.block;


import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWarpedHangingSign extends BlockHangingSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(WARPED_HANGING_SIGN, CommonBlockProperties.ATTACHED_BIT, CommonBlockProperties.FACING_DIRECTION, CommonBlockProperties.GROUND_SIGN_DIRECTION, CommonBlockProperties.HANGING);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedHangingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedHangingSign(BlockState blockstate) {
        super(blockstate);
    }

    public String getName() {
        return "Warped Hanging Sign";
    }
}