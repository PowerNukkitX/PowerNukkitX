package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityEndGateway;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author PikyCZ
 */
public class BlockEndGateway extends BlockSolid implements BlockEntityHolder<BlockEntityEndGateway> {
    public static final BlockProperties $1 = new BlockProperties(END_GATEWAY);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockEndGateway() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockEndGateway(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "End Gateway";
    }

    @Override
    @NotNull public Class<? extends BlockEntityEndGateway> getBlockEntityClass() {
        return BlockEntityEndGateway.class;
    }

    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getBlockEntityType() {
        return BlockEntity.END_GATEWAY;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        return BlockEntityHolder.setBlockAndCreateEntity(this) != null;
    }
    
    @Override
    /**
     * @deprecated 
     */
    
    public boolean canPassThrough() {
        if (this.getLevel() == null) {
            return false;
        }
        
        if (this.getLevel().getDimension() != Level.DIMENSION_THE_END) {
            return false;
        } else {
            return !getOrCreateBlockEntity().isTeleportCooldown();
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isBreakable(@NotNull Vector3 vector, int layer, @Nullable BlockFace face, @Nullable Item item, @Nullable Player player) {
        return false;
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
        return 3600000;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getLightLevel() {
        return 15;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(BlockID.AIR));
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
    
    public void onEntityCollide(Entity entity) {
        if (this.getLevel() == null) {
            return;
        }
        
        if (this.getLevel().getDimension() != Level.DIMENSION_THE_END) {
            return;
        }
        
        if (entity == null) {
            return;
        }
        
        BlockEntityEndGateway $2 = getOrCreateBlockEntity();
        if (endGateway == null) {
            return;
        }
        
        if (!endGateway.isTeleportCooldown()) {
            endGateway.teleportEntity(entity);
        }
    }
}
