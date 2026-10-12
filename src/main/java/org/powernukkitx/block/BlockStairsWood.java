package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;

/**
 * @author xtypr
 * @since 2015/11/25
 */
public abstract class BlockStairsWood extends BlockStairs {
    public static final BlockDefinition DEFINITION = BlockStairs.DEFINITION.toBuilder()
            .hardness(2)
            .resistance(3)
            .toolType(ItemTool.TYPE_AXE)
            .burnChance(5)
            .burnAbility(20)
            .build();
    public BlockStairsWood(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    public BlockStairsWood(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
    }

    @Override
    public Item[] getDrops(Item item) {
         return new Item[]{
            toItem()
            };
    }
}
