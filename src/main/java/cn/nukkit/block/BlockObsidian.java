package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

/**
 * @author xtypr
 * @since 2015/12/2
 */
public class BlockObsidian extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(OBSIDIAN);

    public BlockObsidian() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockObsidian(BlockState blockState) {
        super(blockState);
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
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_DIAMOND;
    }

    @Override
    public double getHardness() {
        return 35; //TODO Should be 50 but the break time calculation is broken
    }

    @Override
    public double getResistance() {
        return 6000;
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
    public boolean canBePushed() {
        return false;
    }

    @Override
    public  boolean canBePulled() {
        return false;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
