package cn.nukkit.block;


import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockAcaciaHangingSign extends BlockHangingSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(ACACIA_HANGING_SIGN,
            CommonBlockProperties.ATTACHED_BIT,
            CommonBlockProperties.FACING_DIRECTION,
            CommonBlockProperties.GROUND_SIGN_DIRECTION,
            CommonBlockProperties.HANGING
    );
    public BlockAcaciaHangingSign() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockAcaciaHangingSign(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public String getName() {
        return "Acacia Hanging Sign";
    }
}