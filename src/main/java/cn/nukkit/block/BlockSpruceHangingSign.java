package cn.nukkit.block;


import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockSpruceHangingSign extends BlockHangingSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(SPRUCE_HANGING_SIGN, CommonBlockProperties.ATTACHED_BIT, CommonBlockProperties.FACING_DIRECTION, CommonBlockProperties.GROUND_SIGN_DIRECTION, CommonBlockProperties.HANGING);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSpruceHangingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSpruceHangingSign(BlockState blockstate) {
        super(blockstate);
    }

    public String getName() {
        return "Spruce Hanging Sign";
    }
}