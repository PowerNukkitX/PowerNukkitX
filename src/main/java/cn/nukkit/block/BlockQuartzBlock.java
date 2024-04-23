package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.ChiselType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.CHISEL_TYPE;
import static cn.nukkit.block.property.CommonBlockProperties.PILLAR_AXIS;

public class BlockQuartzBlock extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(QUARTZ_BLOCK, CommonBlockProperties.CHISEL_TYPE, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockQuartzBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockQuartzBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public double getHardness() {
        return 0.8;
    }

    @Override
    public double getResistance() {
        return 4;
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "Quartz Block",
                "Chiseled Quartz Block",
                "Quartz Pillar",
                "Quartz Smooth"
        };

        return names[getChiselType().ordinal()];
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (this.getChiselType() != ChiselType.DEFAULT) {
            this.setPillarAxis(face.getAxis());
        }
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, getChiselType().ordinal(), 1);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    public BlockFace.Axis getPillarAxis() {
        return getPropertyValue(PILLAR_AXIS);
    }

    public void setPillarAxis(BlockFace.Axis axis) {
        setPropertyValue(PILLAR_AXIS, axis);
    }

    public ChiselType getChiselType() {
        return getPropertyValue(CHISEL_TYPE);
    }

    public void setChiselType(ChiselType type) {
        setPropertyValue(CHISEL_TYPE, type);
    }
}