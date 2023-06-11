package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

@PowerNukkitXOnly
@Since("1.20.0-r2")
public class BlockHangingSign extends BlockSignPost {
    public static final BlockProperties PROPERTIES = new BlockProperties(CommonBlockProperties.FACING_DIRECTION, GROUND_SIGN_DIRECTION, CommonBlockProperties.ATTACHED, CommonBlockProperties.HANGING);

    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
}
