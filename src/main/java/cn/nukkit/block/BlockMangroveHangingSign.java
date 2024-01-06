package cn.nukkit.block;


import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMangroveHangingSign extends BlockHangingSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(MANGROVE_HANGING_SIGN, CommonBlockProperties.ATTACHED_BIT, CommonBlockProperties.FACING_DIRECTION, CommonBlockProperties.GROUND_SIGN_DIRECTION, CommonBlockProperties.HANGING);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMangroveHangingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMangroveHangingSign(BlockState blockstate) {
        super(blockstate);
    }

    public String getName() {
        return "Mangrove Hanging Sign";
    }
}