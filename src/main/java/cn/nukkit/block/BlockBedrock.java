package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static cn.nukkit.block.property.CommonBlockProperties.INFINIBURN_BIT;

/**
 * @author Angelic47 (Nukkit Project)
 * @apiNote Extends BlockSolidMeta instead of BlockSolid only in PowerNukkit
 */

public class BlockBedrock extends BlockSolid {
    public static final BlockProperties $1 = new BlockProperties(BEDROCK, INFINIBURN_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockBedrock() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBedrock(BlockState blockstate) {
        super(blockstate);
    }
    /**
     * @deprecated 
     */
    

    public boolean getBurnIndefinitely() {
        return getPropertyValue(INFINIBURN_BIT);
    }
    /**
     * @deprecated 
     */
    

    public void setBurnIndefinitely(boolean infiniburn) {
        setPropertyValue(INFINIBURN_BIT, infiniburn);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return -1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 18000000;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Bedrock";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isBreakable(@NotNull Vector3 vector, int layer, @Nullable BlockFace face, @Nullable Item item, @Nullable Player player) {
        return player != null && player.isCreative();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBePushed() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBePulled() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canHarvestWithHand() {
        return false;
    }
}
