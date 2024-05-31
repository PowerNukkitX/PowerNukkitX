package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

/**
 * Alias STILL LAVA
 *
 * @author Angelic47 (Nukkit Project)
 */
public class BlockLava extends BlockFlowingLava {
    public static final BlockProperties $1 = new BlockProperties(LAVA, CommonBlockProperties.LIQUID_DEPTH);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockLava() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockLava(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Still Lava";
    }

    @Override
    public BlockLiquid getLiquidWithNewDepth(int depth) {
        return new BlockLava(this.blockstate.setPropertyValue(PROPERTIES, CommonBlockProperties.LIQUID_DEPTH.createValue(depth)));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        return this.getLevel().setBlock(this, this, true, false);
    }
}
