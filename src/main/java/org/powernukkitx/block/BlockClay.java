package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemClayBall;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

/**
 * @author Nukkit Project Team
 */
public class BlockClay extends BlockSolid implements Natural {
    public static final BlockProperties PROPERTIES = new BlockProperties(CLAY);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(0.6)
            .resistance(3)
            .toolType(ItemTool.TYPE_SHOVEL)
            .build();

    public BlockClay() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockClay(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "Clay Block";
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public Item[] getDrops(Item item) {
        Item clayBall = new ItemClayBall();
        clayBall.setCount(4);
        return new Item[]{
                clayBall
        };
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }
}
