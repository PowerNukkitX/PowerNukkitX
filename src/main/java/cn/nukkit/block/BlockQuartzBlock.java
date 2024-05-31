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
    public static final BlockProperties $1 = new BlockProperties(QUARTZ_BLOCK, CommonBlockProperties.CHISEL_TYPE, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockQuartzBlock() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockQuartzBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0.8;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 4;
    }

    @Override
    /**
     * @deprecated 
     */
    
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
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (this.getChiselType() != ChiselType.DEFAULT) {
            this.setPillarAxis(face.getAxis());
        }
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, getChiselType().ordinal(), 1);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canHarvestWithHand() {
        return false;
    }

    public BlockFace.Axis getPillarAxis() {
        return getPropertyValue(PILLAR_AXIS);
    }
    /**
     * @deprecated 
     */
    

    public void setPillarAxis(BlockFace.Axis axis) {
        setPropertyValue(PILLAR_AXIS, axis);
    }

    public ChiselType getChiselType() {
        return getPropertyValue(CHISEL_TYPE);
    }
    /**
     * @deprecated 
     */
    

    public void setChiselType(ChiselType type) {
        setPropertyValue(CHISEL_TYPE, type);
    }
}