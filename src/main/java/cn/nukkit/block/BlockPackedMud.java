package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPackedMud extends BlockSolid {
    public static final BlockProperties $1 = new BlockProperties(PACKED_MUD);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockPackedMud() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockPackedMud(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Packed Mud";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 3;
    }
}
