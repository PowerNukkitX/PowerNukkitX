package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * http://minecraft.wiki/w/End_Rod
 *
 * @author PikyCZ
 */
public class BlockEndRod extends BlockTransparent implements Faceable {
    public static final BlockProperties $1 = new BlockProperties(END_ROD, CommonBlockProperties.FACING_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockEndRod() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockEndRod(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "End Rod";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getLightLevel() {
        return 14;
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
    
    public double getMinX() {
        return this.x + 0.4;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMinZ() {
        return this.z + 0.4;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxX() {
        return this.x + 0.6;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxZ() {
        return this.z + 0.6;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getWaterloggingLevel() {
        return 2;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeFlowedInto() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        int[] faces = {0, 1, 3, 2, 5, 4};
        setPropertyValue(CommonBlockProperties.FACING_DIRECTION, faces[player != null ? face.getIndex() : 0]);
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(getPropertyValue(CommonBlockProperties.FACING_DIRECTION) & 0x07);
    }
}
