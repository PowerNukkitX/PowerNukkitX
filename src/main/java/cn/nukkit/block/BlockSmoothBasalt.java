package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;


public class BlockSmoothBasalt extends BlockBasalt {


    public static final BlockProperties PROPERTIES = CommonBlockProperties.EMPTY_PROPERTIES;

    @Override
    public String getName() {
        return "Smooth Basalt";
    }

    @Override
    public int getId() {
        return 632;
    }


    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }


    public BlockFace.Axis getPillarAxis() {
        // ignore
        return null;
    }


    public void setPillarAxis(BlockFace.Axis axis) {
        // ignore
    }
}
