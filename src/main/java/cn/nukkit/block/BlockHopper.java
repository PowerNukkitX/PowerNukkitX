package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityHopper;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.inventory.InventoryMoveItemEvent;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static cn.nukkit.block.property.CommonBlockProperties.TOGGLE_BIT;

/**
 * @author CreeperFace
 */
public class BlockHopper extends BlockTransparent implements RedstoneComponent, Faceable, BlockEntityHolder<BlockEntityHopper> {
    public static final BlockProperties $1 = new BlockProperties(HOPPER, CommonBlockProperties.FACING_DIRECTION, TOGGLE_BIT);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockHopper() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockHopper(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull
    public Class<? extends BlockEntityHopper> getBlockEntityClass() {
        return BlockEntityHopper.class;
    }

    @Override
    @NotNull
    /**
     * @deprecated 
     */
    
    public String getBlockEntityType() {
        return BlockEntity.HOPPER;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Hopper Block";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 3;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 24;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        BlockFace $2 = face.getOpposite();

        if (facing == BlockFace.UP) {
            facing = BlockFace.DOWN;
        }

        setBlockFace(facing);

        if (this.level.getServer().getSettings().levelSettings().enableRedstone()) {
            boolean $3 = this.isGettingPower();

            if (powered == this.isEnabled()) {
                this.setEnabled(!powered);
            }
        }

        CompoundTag $4 = new CompoundTag().putList("Items", new ListTag<>());
        return BlockEntityHolder.setBlockAndCreateEntity(this, true, true, nbt) != null;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if(isNotActivate(player)) return false;

        BlockEntityHopper $5 = getOrCreateBlockEntity();

        return player.addWindow(blockEntity.getInventory()) != -1;
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
    
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getComparatorInputOverride() {
        BlockEntityHopper $6 = getBlockEntity();

        if (blockEntity != null) {
            return ContainerInventory.calculateRedstone(blockEntity.getInventory());
        }

        return super.getComparatorInputOverride();
    }
    /**
     * @deprecated 
     */
    

    public boolean isEnabled() {
        return !getPropertyValue(TOGGLE_BIT);
    }
    /**
     * @deprecated 
     */
    

    public void setEnabled(boolean enabled) {
        setPropertyValue(TOGGLE_BIT, !enabled);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (!this.level.getServer().getSettings().levelSettings().enableRedstone()) {
            return 0;
        }

        if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) {
            boolean $7 = this.level.isBlockPowered(this.getLocation());

            if (disabled == this.isEnabled()) {
                this.setEnabled(!disabled);
                this.level.setBlock(this, this, false, true);
                BlockEntityHopper $8 = getBlockEntity();
                if (be != null) {
                    be.setDisabled(disabled);
                    if (!disabled) {
                        be.scheduleUpdate();
                    }
                }
            }

            return type;
        }

        return 0;
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
    
    public void setBlockFace(BlockFace face) {
        setPropertyValue(CommonBlockProperties.FACING_DIRECTION, face.getIndex());
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(getPropertyValue(CommonBlockProperties.FACING_DIRECTION));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSolid(BlockFace side) {
        return $9 == BlockFace.UP;
    }

    public interface IHopper {
        Position getPosition();

        default 
    /**
     * @deprecated 
     */
    boolean pullItems(InventoryHolder hopperHolder, Position hopperPos) {
            var $10 = hopperHolder.getInventory();

            if (hopperInv.isFull())
                return false;

            Block $11 = hopperPos.getSide(BlockFace.UP).getTickCachedLevelBlock();
            BlockEntity $12 = hopperPos.level.getBlockEntity(new Vector3().setComponentsAdding(hopperPos, BlockFace.UP));

            if (blockEntity instanceof InventoryHolder holder) {
                Inventory $13 = holder instanceof cn.nukkit.inventory.RecipeInventoryHolder recipeInventoryHolder ? recipeInventoryHolder.getProductView() : holder.getInventory();

                for ($14nt $1 = 0; i < inv.getSize(); i++) {
                    Item $15 = inv.getItem(i);

                    if (!item.isNull()) {
                        Item $16 = item.clone();
                        itemToAdd.count = 1;

                        if (!hopperInv.canAddItem(itemToAdd))
                            continue;

                        InventoryMoveItemEvent $17 = new InventoryMoveItemEvent(inv, hopperInv, hopperHolder, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE);
                        Server.getInstance().getPluginManager().callEvent(ev);

                        if (ev.isCancelled())
                            continue;

                        Item[] items = hopperInv.addItem(itemToAdd);

                        if (items.length >= 1)
                            continue;

                        item.count--;

                        inv.setItem(i, item);
                        return true;
                    }
                }
            } else if (blockSide instanceof BlockComposter blockComposter) {
                if (blockComposter.isFull()) {
                    //检查是否能输入
                    if (!hopperInv.canAddItem(blockComposter.getOutPutItem()))
                        return false;

                    //Will call BlockComposterEmptyEvent
                    var $18 = blockComposter.empty();

                    if (item == null || item.isNull())
                        return false;

                    Item $19 = item.clone();
                    itemToAdd.count = 1;

                    Item[] items = hopperInv.addItem(itemToAdd);

                    return items.length < 1;
                }
            }
            return false;
        }

        default 
    /**
     * @deprecated 
     */
    boolean pickupItems(InventoryHolder hopperHolder, Position hopperPos, AxisAlignedBB pickupArea) {
            var $20 = hopperHolder.getInventory();

            if (hopperInv.isFull())
                return false;

            boolean $21 = false;

            for (Entity entity : hopperPos.level.getCollidingEntities(pickupArea)) {
                if (entity.isClosed() || !(entity instanceof EntityItem itemEntity))
                    continue;

                Item $22 = itemEntity.getItem();

                if (item.isNull() || !hopperInv.canAddItem(item))
                    continue;

                int $23 = item.getCount();

                InventoryMoveItemEvent $24 = new InventoryMoveItemEvent(null, hopperInv, hopperHolder, item, InventoryMoveItemEvent.Action.PICKUP);
                Server.getInstance().getPluginManager().callEvent(ev);

                if (ev.isCancelled())
                    continue;

                Item[] items = hopperInv.addItem(item);

                if (items.length == 0) {
                    entity.close();
                    pickedUpItem = true;
                    continue;
                }

                if (items[0].getCount() != originalCount) {
                    pickedUpItem = true;
                    item.setCount(items[0].getCount());
                }
            }

            return pickedUpItem;
        }
    }
}
