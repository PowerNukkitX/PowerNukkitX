package cn.nukkit.block;


import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockAcaciaHangingSign extends BlockHangingSign {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:acacia_hanging_sign",
            CommonBlockProperties.ATTACHED_BIT,
            CommonBlockProperties.FACING_DIRECTION,
            CommonBlockProperties.GROUND_SIGN_DIRECTION,
            CommonBlockProperties.HANGING
    );

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockAcaciaHangingSign() {
        super(PROPERTIES.getDefaultState());
    }

    public String getName() {
        return "Acacia Hanging Sign";
    }
}