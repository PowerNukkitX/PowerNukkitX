package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockInfestedChiseledStoneBricks extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(INFESTED_CHISELED_STONE_BRICKS);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockInfestedChiseledStoneBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockInfestedChiseledStoneBricks(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Infested Chiseled Stone Bricks";
    }

    @Override
    public double getHardness() {
        return 0.75;
    }

    @Override
    public double getResistance() {
        return 0.75;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

}
