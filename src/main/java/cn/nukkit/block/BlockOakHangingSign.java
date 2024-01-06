package cn.nukkit.block;


import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockOakHangingSign extends BlockHangingSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(OAK_HANGING_SIGN, CommonBlockProperties.ATTACHED_BIT, CommonBlockProperties.FACING_DIRECTION, CommonBlockProperties.GROUND_SIGN_DIRECTION, CommonBlockProperties.HANGING);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOakHangingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOakHangingSign(BlockState blockstate) {
        super(blockstate);
    }

    public String getName() {
        return "Oak Hanging Sign";
    }
}