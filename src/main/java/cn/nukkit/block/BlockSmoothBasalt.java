package cn.nukkit.block;

import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

public class BlockSmoothBasalt extends BlockBasalt {

    public static final BlockProperties PROPERTIES = new BlockProperties(SMOOTH_BASALT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSmoothBasalt() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSmoothBasalt(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Smooth Basalt";
    }

    public BlockFace.Axis getPillarAxis() {
        // ignore
        return null;
    }

    public void setPillarAxis(BlockFace.Axis axis) {
        // ignore
    }
}
