package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockAcaciaSlab extends BlockWoodenSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(ACACIA_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockAcaciaSlab(BlockState blockstate) {
        super(blockstate, ACACIA_DOUBLE_SLAB);
    }

    @Override
    public String getSlabName() {
        return "Acacia";
    }
}