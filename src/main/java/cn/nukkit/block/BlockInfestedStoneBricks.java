package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockInfestedStoneBricks extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(INFESTED_STONE_BRICKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockInfestedStoneBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockInfestedStoneBricks(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Infested Stone Bricks";
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
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

}
