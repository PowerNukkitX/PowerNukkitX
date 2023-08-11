package cn.nukkit.block.impl;

import cn.nukkit.block.BlockTransparentMeta;
import cn.nukkit.block.property.BlockProperties;
import cn.nukkit.block.property.CommonBlockProperties;

public class BlockDecoratedPot extends BlockTransparentMeta {
    public static final BlockProperties PROPERTIES = new BlockProperties(CommonBlockProperties.DIRECTION);

    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDecoratedPot() {}

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
