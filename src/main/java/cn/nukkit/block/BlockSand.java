package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.SandType;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

/**
 * @author MagicDroidX (Nukkit Project)
 */

public class BlockSand extends BlockFallable {
    public static final BlockProperties $1 = new BlockProperties(SAND, CommonBlockProperties.SAND_TYPE);
    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockSand() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockSand(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0.5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 2.5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return getPropertyValue(CommonBlockProperties.SAND_TYPE) == SandType.NORMAL ? "Sand" : "Red Sand";
    }

}
