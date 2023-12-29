package cn.nukkit.block;

import cn.nukkit.block.property.enums.StoneType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockStone extends BlockSolid{
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:stone");

    /*
    public static final int GRANITE = 1;

    public static final int POLISHED_GRANITE = 2;

    public static final int DIORITE = 3;

    public static final int POLISHED_DIORITE = 4;

    public static final int POLISHED_ANDESITE = 6;
    */

    public BlockStone() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStone(BlockState blockState) {
        super(blockState);
    }

    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }


    public StoneType stoneType() {
        return StoneType.STONE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= getToolTier()) {
            return new Item[]{
                    StoneType.STONE.equals(stoneType())
                            ? Item.getItemBlock(BlockID.COBBLESTONE)
                            : toItem()
            };
        } else {
            return Item.EMPTY_ARRAY;
        }
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }
}
