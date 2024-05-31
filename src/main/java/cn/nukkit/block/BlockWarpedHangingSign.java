package cn.nukkit.block;


import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWarpedHangingSign extends BlockHangingSign {
    public static final BlockProperties $1 = new BlockProperties(WARPED_HANGING_SIGN, CommonBlockProperties.ATTACHED_BIT, CommonBlockProperties.FACING_DIRECTION, CommonBlockProperties.GROUND_SIGN_DIRECTION, CommonBlockProperties.HANGING);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockWarpedHangingSign() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockWarpedHangingSign(BlockState blockstate) {
        super(blockstate);
    }
    /**
     * @deprecated 
     */
    

    public String getName() {
        return "Warped Hanging Sign";
    }
}