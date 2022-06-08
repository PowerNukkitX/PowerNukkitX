package cn.nukkit.block;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.utils.BlockColor;

public class BlockPressurePlateMangrove extends BlockPressurePlateWood {
    public BlockPressurePlateMangrove(int meta) {
        super(meta);
        this.onPitch = 0.8f;
        this.offPitch = 0.7f;
    }

    public BlockPressurePlateMangrove() {
        this(0);
    }

    @Override
    public String getName() {
        return "Mangrove Pressure Plate";
    }

    @Override
    public int getId() {
        return MANGROVE_PRESSURE_PLATE;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public double getHardness() {
        return 0.5D;
    }

    @Override
    public double getResistance() {
        return 0.5D;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWNISH_RED;
    }

    @Override
    protected int computeRedstoneStrength() {
        AxisAlignedBB bb = getCollisionBoundingBox();

        for (Entity entity : this.level.getCollidingEntities(bb)) {
            if (entity.doesTriggerPressurePlate()) {
                return 15;
            }
        }

        return 0;
    }
}
