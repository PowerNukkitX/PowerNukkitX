package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import org.jetbrains.annotations.NotNull;

/**
 * @author CreeperFace
 * @since 26. 11. 2016
 */
public class BlockRedSandstone extends BlockSandstone {

    public static final BlockProperties PROPERTIES = new BlockProperties(RED_SANDSTONE, CommonBlockProperties.SAND_STONE_TYPE);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedSandstone() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedSandstone(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return switch(getSandstoneType()) {
            case CUT -> "Cut Red Sandstone";
            case DEFAULT -> "Red Sandstone";
            case HEIROGLYPHS -> "Chiseled Red Sandstone";
            case SMOOTH -> "Smooth Red Sandstone";
        };
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, this.getDamage() & 0x03);
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

}
