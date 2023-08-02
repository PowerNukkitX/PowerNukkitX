package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;

//todo complete
@PowerNukkitXOnly
@Since("1.20.10-r2")
public class BlockDecoratedPot extends BlockTransparentMeta {
    public static final BlockProperties PROPERTIES = new BlockProperties(CommonBlockProperties.DIRECTION);

    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDecoratedPot() {
    }

    public BlockDecoratedPot(int meta) {
        super(meta);
    }

    public int getId() {
        return DECORATED_POT;
    }

    public String getName() {
        return "Decorated Pot";
    }
}