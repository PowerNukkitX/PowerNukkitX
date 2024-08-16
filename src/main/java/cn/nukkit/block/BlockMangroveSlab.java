package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockMangroveSlab extends BlockWoodenSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(MANGROVE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMangroveSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMangroveSlab(BlockState blockstate) {
        super(blockstate, MANGROVE_DOUBLE_SLAB);
    }

    @Override
    public String getName() {
        return (isOnTop() ? "Upper " : "") + getSlabName() + " Wood Slab";
    }

    @Override
    public String getSlabName() {
        return "Mangrove";
    }

}