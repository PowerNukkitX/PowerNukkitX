package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockPackedIce extends BlockIce {
    public static final BlockProperties PROPERTIES = new BlockProperties(PACKED_ICE);
    public static final BlockDefinition DEFINITION = BlockIce.DEFINITION.toBuilder()
            .toolType(ItemTool.TYPE_PICKAXE)
            .burnChance(0)
            .isTransparent(false)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPackedIce() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPackedIce(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    public BlockPackedIce(BlockState blockstate, BlockDefinition definition) {
        super(blockstate, definition);
    }

    @Override
    public String getName() {
        return "Packed Ice";
    }

    
    @Override
    public int onUpdate(int type) {
        return 0; //not being melted
    }

    
    @Override
    public boolean onBreak(Item item) {
        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true); //no water
        return true;
    }

    @Override
    public int getLightFilter() {
        return 15;
    }
}