package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCobbledDeepslateSlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(COBBLED_DEEPSLATE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCobbledDeepslateSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCobbledDeepslateSlab(BlockState blockstate) {
        super(blockstate, COBBLED_DEEPSLATE_DOUBLE_SLAB);
    }

    @Override
    public String getSlabName() {
        return "Cobbled Deepslate";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return getId().equals(slab.getId());
    }
}