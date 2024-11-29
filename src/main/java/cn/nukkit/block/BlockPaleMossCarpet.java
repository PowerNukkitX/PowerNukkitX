package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;


public class BlockPaleMossCarpet extends BlockMossCarpet {

    public static final BlockProperties PROPERTIES = new BlockProperties(PALE_MOSS_CARPET,
            CommonBlockProperties.PALE_MOSS_CARPET_SIDE_EAST,
            CommonBlockProperties.PALE_MOSS_CARPET_SIDE_NORTH,
            CommonBlockProperties.PALE_MOSS_CARPET_SIDE_SOUTH,
            CommonBlockProperties.PALE_MOSS_CARPET_SIDE_WEST,
            CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPaleMossCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPaleMossCarpet(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Pale Moss Carpet";
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
