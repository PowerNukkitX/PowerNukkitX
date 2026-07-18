package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

/**
 * @author xtypr
 * @since 2015/12/2
 */
public class BlockObsidian extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(OBSIDIAN);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .resistance(6000)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_DIAMOND)
            .canBePushed(false)
            .canBePulled(false)
            .build();

    public BlockObsidian() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockObsidian(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "Obsidian";
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public double getHardness() {
        return 35; //TODO Should be 50 but the break time calculation is broken
    }

    
    @Override
    public boolean onBreak(Item item) {
        //destroy the nether portal
        Block[] nearby = new Block[]{
                this.up(), this.down(),
                this.north(), south(),
                this.west(), this.east(),
        };
        for (Block aNearby : nearby) {
            if (aNearby != null && aNearby.getId().equals(PORTAL)) {
                aNearby.onBreak(item);
            }
        }
        return super.onBreak(item);
    }

    @Override
    public void afterRemoval(Block newBlock, boolean update) {
        if (update) {
            onBreak(Item.AIR);
        }
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
