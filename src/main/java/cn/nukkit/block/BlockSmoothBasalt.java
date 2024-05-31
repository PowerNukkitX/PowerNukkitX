package cn.nukkit.block;

import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

public class BlockSmoothBasalt extends BlockBasalt {

    public static final BlockProperties $1 = new BlockProperties(SMOOTH_BASALT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockSmoothBasalt() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockSmoothBasalt(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Smooth Basalt";
    }

    public BlockFace.Axis getPillarAxis() {
        // ignore
        return null;
    }
    /**
     * @deprecated 
     */
    

    public void setPillarAxis(BlockFace.Axis axis) {
        // ignore
    }
}
