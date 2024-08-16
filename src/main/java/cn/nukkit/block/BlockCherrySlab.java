package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockCherrySlab extends BlockWoodenSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHERRY_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCherrySlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCherrySlab(BlockState blockstate) {
        super(blockstate, CHERRY_DOUBLE_SLAB);
    }

    @Override
    public String getSlabName() {
        return "Cherry";
    }
}