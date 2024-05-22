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
    public static final BlockProperties PROPERTIES = new BlockProperties(HOPPER, CommonBlockProperties.FACING_DIRECTION, TOGGLE_BIT);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockHopper() {
        this(PROPERTIES.getDefaultState());
    }

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
    public String getBlockEntityType() {
        return BlockEntity.HOPPER;
    }

    @Override
    public String getName() {
        return "Hopper Block";
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 24;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        BlockFace facing = face.getOpposite();

        if (facing == BlockFace.UP) {
            facing = BlockFace.DOWN;
        }

        setBlockFace(facing);

        if (this.level.getServer().getSettings().levelSettings().enableRedstone()) {
            boolean powered = this.isGettingPower();

            if (powered == this.isEnabled()) {
                this.setEnabled(!powered);
            }
        }

        CompoundTag nbt = new CompoundTag().putList("Items", new ListTag<>());
        return BlockEntityHolder.setBlockAndCreateEntity(this, true, true, nbt) != null;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if(isNotActivate(player)) return false;

        BlockEntityHopper blockEntity = getOrCreateBlockEntity();

        return player.addWindow(blockEntity.getInventory()) != -1;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        BlockEntityHopper blockEntity = getBlockEntity();

        if (blockEntity != null) {
            return ContainerInventory.calculateRedstone(blockEntity.getInventory());
        }

        return super.getComparatorInputOverride();
    }

    public boolean isEnabled() {
        return !getPropertyValue(TOGGLE_BIT);
    }

    public void setEnabled(boolean enabled) {
        setPropertyValue(TOGGLE_BIT, !enabled);
    }

    @Override
    public int onUpdate(int type) {
        if (!this.level.getServer().getSettings().levelSettings().enableRedstone()) {
            return 0;
        }

        if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) {
            boolean disabled = this.level.isBlockPowered(this.getLocation());

            if (disabled == this.isEnabled()) {
                this.setEnabled(!disabled);
                this.level.setBlock(this, this, false, true);
                BlockEntityHopper be = getBlockEntity();
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
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(CommonBlockProperties.FACING_DIRECTION, face.getIndex());
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(getPropertyValue(CommonBlockProperties.FACING_DIRECTION));
    }

    @Override
    public boolean isSolid(BlockFace side) {
        return side == BlockFace.UP;
    }

    public interface IHopper {
        Position getPosition();

        default boolean pullItems(InventoryHolder hopperHolder, Position hopperPos) {
            var hopperInv = hopperHolder.getInventory();

            if (hopperInv.isFull())
                return false;

            Block blockSide = hopperPos.getSide(BlockFace.UP).getTickCachedLevelBlock();
            BlockEntity blockEntity = hopperPos.level.getBlockEntity(new Vector3().setComponentsAdding(hopperPos, BlockFace.UP));

            if (blockEntity instanceof InventoryHolder holder) {
                Inventory inv = holder instanceof cn.nukkit.inventory.RecipeInventoryHolder recipeInventoryHolder ? recipeInventoryHolder.getProductView() : holder.getInventory();

                for (int i = 0; i < inv.getSize(); i++) {
                    Item item = inv.getItem(i);

                    if (!item.isNull()) {
                        Item itemToAdd = item.clone();
                        itemToAdd.count = 1;

                        if (!hopperInv.canAddItem(itemToAdd))
                            continue;

                        InventoryMoveItemEvent ev = new InventoryMoveItemEvent(inv, hopperInv, hopperHolder, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE);
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
                    var item = blockComposter.empty();

                    if (item == null || item.isNull())
                        return false;

                    Item itemToAdd = item.clone();
                    itemToAdd.count = 1;

                    Item[] items = hopperInv.addItem(itemToAdd);

                    return items.length < 1;
                }
            }
            return false;
        }

        default boolean pickupItems(InventoryHolder hopperHolder, Position hopperPos, AxisAlignedBB pickupArea) {
            var hopperInv = hopperHolder.getInventory();

            if (hopperInv.isFull())
                return false;

            boolean pickedUpItem = false;

            for (Entity entity : hopperPos.level.getCollidingEntities(pickupArea)) {
                if (entity.isClosed() || !(entity instanceof EntityItem itemEntity))
                    continue;

                Item item = itemEntity.getItem();

                if (item.isNull() || !hopperInv.canAddItem(item))
                    continue;

                int originalCount = item.getCount();

                InventoryMoveItemEvent ev = new InventoryMoveItemEvent(null, hopperInv, hopperHolder, item, InventoryMoveItemEvent.Action.PICKUP);
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
