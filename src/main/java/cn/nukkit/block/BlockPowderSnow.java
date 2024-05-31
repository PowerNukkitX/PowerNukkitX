package cn.nukkit.block;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntitySmallFireball;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;


public class BlockPowderSnow extends BlockTransparent {
    public static final BlockProperties $1 = new BlockProperties(POWDER_SNOW);
    /**
     * @deprecated 
     */
    

    public BlockPowderSnow() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockPowderSnow(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Powder Snow";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0.25;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 0.1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSolid() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isTransparent() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canPassThrough() {
        return true;
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onProjectileHit(@NotNull Entity projectile, @NotNull Position position, @NotNull Vector3 motion) {
        if (projectile instanceof EntitySmallFireball) {
            this.getLevel().useBreakOn(this);
            return true;
        }
        return false;
    }
}
