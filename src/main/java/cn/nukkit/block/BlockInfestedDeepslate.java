package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockInfestedDeepslate extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(INFESTED_DEEPSLATE, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockInfestedDeepslate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockInfestedDeepslate(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Infested Deepslate";
    }
    
    @Override
    public double getHardness() {
        return 1.5;
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
