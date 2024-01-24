package cn.nukkit.block;


import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.HANGING;

public class BlockSoulLantern extends BlockLantern {
    public static final BlockProperties PROPERTIES = new BlockProperties(SOUL_LANTERN, HANGING);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSoulLantern() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSoulLantern(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Soul Lantern";
    }

    @Override
    public int getLightLevel() {
        return 10;
    }

}
