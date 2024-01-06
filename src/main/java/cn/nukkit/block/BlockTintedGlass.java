package cn.nukkit.block;

import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

public class BlockTintedGlass extends BlockGlass {
    public static final BlockProperties PROPERTIES = new BlockProperties(TINTED_GLASS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTintedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTintedGlass(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Tinted Glass";
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[] { toItem() };
    }

    @Override
    public boolean canSilkTouch() {
        return false;
    }
}