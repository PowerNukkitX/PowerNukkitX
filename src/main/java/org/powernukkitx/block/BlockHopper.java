package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntityHopper;
import org.powernukkitx.entity.item.EntityItem;
import org.powernukkitx.event.inventory.InventoryMoveItemEvent;
import org.powernukkitx.inventory.ContainerInventory;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.inventory.InventoryHolder;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;
import org.powernukkitx.utils.Faceable;
import org.powernukkitx.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static org.powernukkitx.block.property.CommonBlockProperties.TOGGLE_BIT;

/**
 * @author CreeperFace
 */
public class BlockHopper extends BlockTransparent implements RedstoneComponent, Faceable, BlockEntityHolder<BlockEntityHopper> {
    public static final BlockProperties PROPERTIES = new BlockProperties(HOPPER, CommonBlockProperties.FACING_DIRECTION, TOGGLE_BIT);
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .hardness(3)
            .resistance(24)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canBeActivated(true)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockHopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockHopper(BlockState blockstate) {
        super(blockstate, DEFINITION);
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

        if (this.level.getServer().getSettings().gameplaySettings().enableRedstone()) {
            boolean powered = this.isGettingPower();

            if (powered == this.isEnabled()) {
                this.setEnabled(!powered);
            }
        }

        CompoundTag nbt = new CompoundTag().putList("Items", new ListTag<>(Tag.TAG_Compound));
        if (item.hasCustomBlockData()) {
            for (var entry : item.getCustomBlockData().getEntrySet()) {
                nbt.put(entry.getKey(), entry.getValue().copy());
            }
        }
        return BlockEntityHolder.setBlockAndCreateEntity(this, false, true, nbt) != null;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (isNotActivate(player)) return false;

        BlockEntityHopper blockEntity = getOrCreateBlockEntity();

        return player.addWindow(blockEntity.getInventory()) != -1;
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
        if (!this.level.getServer().getSettings().gameplaySettings().enableRedstone()) {
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
                Inventory inv = holder instanceof org.powernukkitx.inventory.RecipeInventoryHolder recipeInventoryHolder ? recipeInventoryHolder.getProductView() : holder.getInventory();

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
                    //Check if input is possible
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

            for (EntityItem itemEntity : hopperPos.level.getCollidingItemEntities(pickupArea)) {
                if (itemEntity.isDisplayOnly())
                    continue;

                Item item = itemEntity.getItem();

                if (item.isNull() || !hopperInv.canAddItem(item))
                    continue;

                int originalCount = item.getCount();

                if (!hopperInv.callPickupItemEvent(itemEntity))
                    continue;

                Item[] items = hopperInv.addItem(item);

                if (items.length == 0) {
                    itemEntity.close();
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
