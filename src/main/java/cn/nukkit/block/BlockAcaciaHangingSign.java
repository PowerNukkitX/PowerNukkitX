package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.*;

public class BlockAcaciaHangingSign extends BlockHangingSign {
    public static final BlockProperties $1 = new BlockProperties(ACACIA_HANGING_SIGN, ATTACHED_BIT, FACING_DIRECTION, GROUND_SIGN_DIRECTION, HANGING);
    /**
     * @deprecated 
     */
    
    public BlockAcaciaHangingSign() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockAcaciaHangingSign(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public String getName() {
        return "Acacia Hanging Sign";
    }
}