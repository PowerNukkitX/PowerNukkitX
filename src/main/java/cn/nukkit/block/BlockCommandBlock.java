package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityCommandBlock;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static cn.nukkit.block.property.CommonBlockProperties.CONDITIONAL_BIT;
import static cn.nukkit.block.property.CommonBlockProperties.FACING_DIRECTION;

//special thanks to wode
public class BlockCommandBlock extends BlockSolid implements Faceable, BlockEntityHolder<BlockEntityCommandBlock> {
    public static final BlockProperties $1 = new BlockProperties(COMMAND_BLOCK, CONDITIONAL_BIT, FACING_DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockCommandBlock() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockCommandBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Impulse Command Block";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 6000000;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBePushed() {
        return false;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(get(AIR));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isBreakable(@NotNull Vector3 vector, int layer, @Nullable BlockFace face, @Nullable Item item, @Nullable Player player) {
        return player != null && player.isCreative();
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(getPropertyValue(FACING_DIRECTION));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setBlockFace(BlockFace face) {
        setPropertyValue(FACING_DIRECTION, face.getIndex());
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (player != null) {
            if (!player.isCreative())
                return false;
            if (Math.abs(player.getFloorX() - this.x) < 2 && Math.abs(player.getFloorZ() - this.z) < 2) {
                double $2 = player.y + player.getEyeHeight();
                if (y - this.y > 2) {
                    this.setBlockFace(BlockFace.UP);
                } else if (this.y - y > 0) {
                    this.setBlockFace(BlockFace.DOWN);
                } else {
                    this.setBlockFace(player.getHorizontalFacing().getOpposite());
                }
            } else {
                this.setBlockFace(player.getHorizontalFacing().getOpposite());
            }
        } else {
            this.setBlockFace(BlockFace.DOWN);
        }
        return BlockEntityHolder.setBlockAndCreateEntity(this, true, true) != null;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeActivated() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (player != null) {
            Item $3 = player.getInventory().getItemInHand();
            if (player.isSneaking() && !(itemInHand.isTool() || itemInHand.isNull())) {
                return false;
            }
            BlockEntityCommandBlock $4 = this.getOrCreateBlockEntity();
            tile.spawnTo(player);
            player.addWindow(tile.getInventory());
        }
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) {
            BlockEntityCommandBlock $5 = this.getBlockEntity();
            if (tile == null)
                return super.onUpdate(type);
            if (this.isGettingPower()) {
                if (!tile.isPowered()) {
                    tile.setPowered();
                    tile.trigger();
                }
            } else {
                tile.setPowered(false);
            }
        }
        return super.onUpdate(type);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getComparatorInputOverride() {
        return Math.min(this.getOrCreateBlockEntity().getSuccessCount(), 0xf);
    }

    @Override
    @NotNull
    public Class<? extends BlockEntityCommandBlock> getBlockEntityClass() {
        return BlockEntityCommandBlock.class;
    }

    @Override
    @NotNull
    /**
     * @deprecated 
     */
    
    public String getBlockEntityType() {
        return BlockEntity.COMMAND_BLOCK;
    }
}
