package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.PILLAR_AXIS;

public class BlockBasalt extends BlockSolid {

    public static final BlockProperties $1 = new BlockProperties(BASALT,PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockBasalt() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBasalt(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Basalt";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 1.25;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 4.2;
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
    
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
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

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        setPillarAxis(face.getAxis());
        getLevel().setBlock(block, this, true, true);
        return true;
    }

}
