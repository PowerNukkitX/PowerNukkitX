package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPaleOakSlab extends BlockWoodenSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(PALE_OAK_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPaleOakSlab(BlockState blockstate) {
        super(blockstate, PALE_OAK_DOUBLE_SLAB);
    }

    @Override
    public String getSlabName() {
        return "Pale Oak";
    }
}