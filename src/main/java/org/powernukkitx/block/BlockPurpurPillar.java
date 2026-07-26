package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.PILLAR_AXIS;

public class BlockPurpurPillar extends BlockSolid {

    public static final BlockProperties PROPERTIES = new BlockProperties(PURPUR_PILLAR, PILLAR_AXIS);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPurpurPillar() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPurpurPillar(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Purpur Pillar";
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
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        this.setPillarAxis(face.getAxis());
        this.getLevel().setBlock(block, this, true, true);
        return true;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    public BlockFace.Axis getPillarAxis() {
        return getPropertyValue(PILLAR_AXIS);
    }

    public void setPillarAxis(BlockFace.Axis axis) {
        setPropertyValue(PILLAR_AXIS, axis);
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this.getProperties().getDefaultState().toBlock());
    }

}
