package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

@PowerNukkitOnly
public class BlockStairsAndesitePolished extends BlockStairs {
    @PowerNukkitOnly
    public BlockStairsAndesitePolished() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockStairsAndesitePolished(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return POLISHED_ANDESITE_STAIRS;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    @PowerNukkitOnly
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public String getName() {
        return "Polished Andesite Stairs";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
