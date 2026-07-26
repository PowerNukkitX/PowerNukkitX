package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntityEndGateway;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author PikyCZ
 */
public class BlockEndGateway extends BlockSolid implements BlockEntityHolder<BlockEntityEndGateway> {
    public static final BlockProperties PROPERTIES = new BlockProperties(END_GATEWAY);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockEndGateway() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockEndGateway(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "End Gateway";
    }

    @Override
    @NotNull public Class<? extends BlockEntityEndGateway> getBlockEntityClass() {
        return BlockEntityEndGateway.class;
    }

    @Override
    @NotNull public String getBlockEntityType() {
        return BlockEntity.END_GATEWAY;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        return BlockEntityHolder.setBlockAndCreateEntity(this) != null;
    }
    
    @Override
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
    public boolean isBreakable(@NotNull Vector3 vector, int layer, @Nullable BlockFace face, @Nullable Item item, @Nullable Player player) {
        return false;
    }

    @Override
    public double getHardness() {
        return -1;
    }

    @Override
    public double getResistance() {
        return 3600000;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(BlockID.AIR));
    }
    
    @Override
    public boolean canBePushed() {
        return false;
    }
    
    @Override
    public boolean canBePulled() {
        return false;
    }
    
    @Override
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
        
        BlockEntityEndGateway endGateway = getOrCreateBlockEntity();
        if (endGateway == null) {
            return;
        }
        
        if (!endGateway.isTeleportCooldown()) {
            endGateway.teleportEntity(entity);
        }
    }
}
