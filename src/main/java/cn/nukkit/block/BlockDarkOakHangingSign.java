package cn.nukkit.block;


import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDarkOakHangingSign extends BlockHangingSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(DARK_OAK_HANGING_SIGN, CommonBlockProperties.ATTACHED_BIT, CommonBlockProperties.FACING_DIRECTION, CommonBlockProperties.GROUND_SIGN_DIRECTION, CommonBlockProperties.HANGING);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDarkOakHangingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDarkOakHangingSign(BlockState blockstate) {
        super(blockstate);
    }

    public String getName() {
        return "Dark Oak Hanging Sign";
    }
}