package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

@PowerNukkitOnly
public class BlockStairsDioritePolished extends BlockStairs {
    @PowerNukkitOnly
    public BlockStairsDioritePolished() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockStairsDioritePolished(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return POLISHED_DIORITE_STAIRS;
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
    public String getName() {
        return "Polished Diorite Stairs";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.QUARTZ_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
