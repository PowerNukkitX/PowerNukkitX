package cn.nukkit.block;


import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBirchHangingSign extends BlockHangingSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(BIRCH_HANGING_SIGN, CommonBlockProperties.ATTACHED_BIT, CommonBlockProperties.FACING_DIRECTION, CommonBlockProperties.GROUND_SIGN_DIRECTION, CommonBlockProperties.HANGING);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBirchHangingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBirchHangingSign(BlockState blockstate) {
        super(blockstate);
    }

    public String getName() {
        return "Birch Hanging Sign";
    }
}