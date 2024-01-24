package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.AxisAlignedBB;
import org.jetbrains.annotations.NotNull;

public class BlockStonePressurePlate extends BlockPressurePlateBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(STONE_PRESSURE_PLATE, CommonBlockProperties.REDSTONE_SIGNAL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStonePressurePlate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStonePressurePlate(BlockState blockstate) {
        super(blockstate);
        this.onPitch = 0.6f;
        this.offPitch = 0.5f;
    }

    @Override
    public String getName() {
        return "Stone Pressure Plate";
    }

    @Override
    public double getHardness() {
        return 0.5D;
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    protected int computeRedstoneStrength() {
        AxisAlignedBB bb = getCollisionBoundingBox();

        for (Entity entity : this.level.getCollidingEntities(bb)) {
            if (entity instanceof EntityLiving && entity.doesTriggerPressurePlate()) {
                return 15;
            }
        }

        return 0;
    }
}