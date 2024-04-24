package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.enums.ChiselType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.CHISEL_TYPE;
import static cn.nukkit.block.property.CommonBlockProperties.PILLAR_AXIS;

public class BlockPurpurBlock extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(PURPUR_BLOCK, CHISEL_TYPE, PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPurpurBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPurpurBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "Purpur Block",
                "",
                "Purpur Pillar",
                ""
        };

        return names[this.blockstate.specialValue() & 0x03];
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

    @Override
    public Item toItem() {
        return new ItemBlock(this.getProperties().getBlockState(CHISEL_TYPE.createValue(getPropertyValue(CHISEL_TYPE))).toBlock());
    }
}