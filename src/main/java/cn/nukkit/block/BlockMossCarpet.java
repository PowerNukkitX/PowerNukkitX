package cn.nukkit.block;

import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;


public class BlockMossCarpet extends BlockCarpet {

    public static final BlockProperties PROPERTIES = new BlockProperties(MOSS_CARPET);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMossCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMossCarpet(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Moss Carpet";
    }

    @Override
    public double getResistance() {
        return 0.1;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{toItem()};
    }
}
