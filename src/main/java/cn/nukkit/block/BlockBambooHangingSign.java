package cn.nukkit.block;


import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBambooHangingSign extends BlockHangingSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(BAMBOO_HANGING_SIGN, CommonBlockProperties.ATTACHED_BIT, CommonBlockProperties.FACING_DIRECTION, CommonBlockProperties.GROUND_SIGN_DIRECTION, CommonBlockProperties.HANGING);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBambooHangingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBambooHangingSign(BlockState blockstate) {
        super(blockstate);
    }

    public String getName() {
        return "Bamboo Hanging Sign";
    }
}