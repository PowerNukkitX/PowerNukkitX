package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;

import static cn.nukkit.block.property.CommonBlockProperties.SAND_STONE_TYPE;

/**
 * @author CreeperFace
 * @since 26. 11. 2016
 */
public class BlockRedSandstone extends BlockSandstone {
    public static final BlockProperties PROPERTIES = new BlockProperties(RED_SANDSTONE, SAND_STONE_TYPE);

    public BlockRedSandstone() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedSandstone(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return switch (getSandstoneType()) {
            case CUT -> "Cut Red Sandstone";
            case DEFAULT -> "Red Sandstone";
            case HEIROGLYPHS -> "Chiseled Red Sandstone";
            case SMOOTH -> "Smooth Red Sandstone";
        };
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, getSandstoneType().ordinal());
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

}
