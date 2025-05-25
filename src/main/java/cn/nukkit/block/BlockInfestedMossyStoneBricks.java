package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockInfestedMossyStoneBricks extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(INFESTED_MOSSY_STONE_BRICKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockInfestedMossyStoneBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockInfestedMossyStoneBricks(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Infested Mossy Stone Bricks";
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
