package org.powernukkitx.blockentity;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockComposter;
import org.powernukkitx.block.BlockHopper;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.event.block.HopperSearchItemEvent;
import org.powernukkitx.event.inventory.InventoryMoveItemEvent;
import org.powernukkitx.inventory.BrewingInventory;
import org.powernukkitx.inventory.HopperInventory;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.inventory.InventoryHolder;
import org.powernukkitx.inventory.RecipeInventoryHolder;
import org.powernukkitx.inventory.SmeltingInventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemPotion;
import org.powernukkitx.item.ItemSplashPotion;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.utils.ItemHelper;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

/**
 * @author CreeperFace
 * @since 8.5.2017
 */
public class BlockEntityHopper extends BlockEntitySpawnable implements BlockEntityInventoryHolder, BlockHopper.IHopper {

    protected HopperInventory inventory;

    public int transferCooldown;

    private AxisAlignedBB pickupArea;

    private boolean disabled;

    private final BlockVector3 temporalVector = new BlockVector3();

    //由容器矿车检测漏斗并通知更新，这样子能大幅优化性能
    @Getter
    @Setter
    private InventoryHolder minecartInvPickupFrom = null;
    @Getter
    @Setter
    private InventoryHolder minecartInvPushTo = null;

    public BlockEntityHopper(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        super.initBlockEntity();
        this.scheduleUpdate();
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        if (this.nbt.contains("TransferCooldown")) {
            this.transferCooldown = this.getNbt().getInt("TransferCooldown");
        } else {
            this.transferCooldown = 8;
        }

        this.inventory = new HopperInventory(this);

        if (!this.nbt.containsList("Items", Tag.TAG_Compound)) {
            this.nbt.putList("Items", new ListTag<>(Tag.TAG_Compound));
        }

        for (int i = 0; i < this.getSize(); i++) {
            this.inventory.setItem(i, this.getItem(i));
        }

        this.pickupArea = generatePickupArea();

        checkDisabled();
    }

    protected SimpleAxisAlignedBB generatePickupArea() {
        return new SimpleAxisAlignedBB(this.x, this.y, this.z, this.x + 1, this.y + 2, this.z + 1);
    }

    protected void checkDisabled() {
        if (getBlock() instanceof BlockHopper blockHopper) {
            disabled = !(blockHopper).isEnabled();
        }
    }

    /**
     * @return How much ticks does it take for the hopper to transfer an item
     */
    public int getCooldownTick() {
        return 8;
    }

    protected boolean checkBlockStateValid(@NotNull BlockState levelBlockState) {
        return levelBlockState.getIdentifier().equals(BlockID.HOPPER);
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.level.getBlockIdAt(this.getFloorX(), this.getFloorY(), this.getFloorZ()).equals(Block.HOPPER);
    }

    @Override
    public String getName() {
        return this.hasName() ? this.getNbt().getString("CustomName") : "Hopper";
    }

    @Override
    public boolean hasName() {
        return this.nbt.contains("CustomName");
    }

    @Override
    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            this.nbt.remove("CustomName");
            return;
        }

        this.nbt.putString("CustomName", name);
    }

    public boolean isOnTransferCooldown() {
        return this.transferCooldown > 0;
    }

    public void setTransferCooldown(int transferCooldown) {
        this.transferCooldown = transferCooldown;
    }

    public int getSize() {
        return 5;
    }

    protected int getSlotIndex(int index) {
        ListTag<CompoundTag> list = this.getNbt().getList("Items", CompoundTag.class);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getByte("Slot") == index) {
                return i;
            }
        }

        return -1;
    }

    public Item getItem(int index) {
        int i = this.getSlotIndex(index);
        if (i < 0) {
            return Item.AIR;
        } else {
            CompoundTag data = this.getNbt().getList("Items", CompoundTag.class).get(i);
            return ItemHelper.read(data);
        }
    }

    public void setItem(int index, Item item) {
        int i = this.getSlotIndex(index);

        CompoundTag d = ItemHelper.write(item, index);

        final ListTag<CompoundTag> items = this.getNbt().getList("Items", CompoundTag.class);

        if (item.isNull() || item.getCount() <= 0) {
            if (i >= 0) {
                items.remove(i);
            }
        } else if (i < 0) {
            items.add(d);
        } else {
            items.add(i, d);
        }
        this.nbt.putList("Items", items);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.nbt.putList("Items", new ListTag<>(Tag.TAG_Compound));
        for (int index = 0; index < this.getSize(); index++) {
            this.setItem(index, this.inventory.getItem(index));
        }

        this.nbt.putInt("TransferCooldown", this.transferCooldown);
    }

    @Override
    public HopperInventory getInventory() {
        return inventory;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public boolean onUpdate() {
        if (this.closed) {
            return false;
        }

        if (!isBlockEntityValid()) {
            this.close();
            return false;
        }

        if (isOnTransferCooldown()) {
            this.transferCooldown--;
            return true;
        }

        if (disabled) {
            return false;
        }

        Block blockSide = this.getSide(BlockFace.UP).getTickCachedLevelBlock();
        BlockEntity blockEntity = this.level.getBlockEntity(temporalVector.setComponentsAdding(this, BlockFace.UP));

        if (this.getLocation().getLevel() == null) return true;

        boolean changed = pushItems() || pushItemsIntoMinecart();

        HopperSearchItemEvent event = new HopperSearchItemEvent(this, false);
        this.server.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            if (blockEntity instanceof InventoryHolder || blockSide instanceof BlockComposter) {
                changed = pullItems(this, this) || changed;
            } else {
                changed = pullItemsFromMinecart() || pickupItems(this, this, pickupArea) || changed;
            }
        }

        if (changed) {
            this.setTransferCooldown(this.getCooldownTick());
            setDirty();
        }

        return true;
    }

    @Override
    public boolean isObservable() {
        return false;
    }

    public boolean pullItemsFromMinecart() {
        if (this.inventory.isFull()) {
            return false;
        }

        if (getMinecartInvPickupFrom() != null) {
            var inv = getMinecartInvPickupFrom().getInventory();

            for (int i = 0; i < inv.getSize(); i++) {
                Item item = inv.getItem(i);

                if (!item.isNull()) {
                    Item itemToAdd = item.clone();
                    itemToAdd.count = 1;
                    if (!this.inventory.canAddItem(itemToAdd))
                        continue;

                    InventoryMoveItemEvent ev = new InventoryMoveItemEvent(inv, this.inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE);
                    this.server.getPluginManager().callEvent(ev);
                    if (ev.isCancelled())
                        continue;

                    Item[] items = this.inventory.addItem(itemToAdd);
                    if (items.length >= 1)
                        continue;

                    item.count--;
                    inv.setItem(i, item);

                    //归位为null
                    setMinecartInvPickupFrom(null);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void close() {
        if (!closed) {
            for (Player player : new HashSet<>(this.getInventory().getViewers())) {
                player.removeWindow(this.getInventory());
            }
            super.close();
        }
    }

    @Override
    public void onBreak(boolean isSilkTouch) {
        for (Item content : inventory.getContents().values()) {
            level.dropItem(this, content);
        }
        this.inventory.clearAll();
    }


    public boolean pushItemsIntoMinecart() {
        if (getMinecartInvPushTo() != null) {
            Inventory holderInventory = getMinecartInvPushTo().getInventory();

            if (holderInventory.isFull())
                return false;

            for (int i = 0; i < this.inventory.getSize(); i++) {
                Item item = this.inventory.getItem(i);

                if (!item.isNull()) {
                    Item itemToAdd = item.clone();
                    itemToAdd.setCount(1);

                    if (!holderInventory.canAddItem(itemToAdd))
                        continue;

                    InventoryMoveItemEvent ev = new InventoryMoveItemEvent(this.inventory, holderInventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE);
                    this.server.getPluginManager().callEvent(ev);

                    if (ev.isCancelled())
                        continue;

                    Item[] items = holderInventory.addItem(itemToAdd);

                    if (items.length > 0)
                        continue;

                    item.count--;
                    this.inventory.setItem(i, item);

                    //归位为null
                    setMinecartInvPushTo(null);
                    return true;
                }
            }

        }

        return false;
    }

    public boolean pushItems() {
        if (this.inventory.isEmpty()) {
            return false;
        }

        BlockState levelBlockState = getLevelBlockState();
        if (!checkBlockStateValid(levelBlockState)) {
            return false;
        }

        BlockFace side = BlockFace.fromIndex(levelBlockState.getPropertyValue(CommonBlockProperties.FACING_DIRECTION));
        Position sidePos = this.getSide(side);
        Block blockSide = sidePos.getLevelBlock(false);
        if (blockSide.isAir()) return false;
        BlockEntity be = this.level.getBlockEntity(temporalVector.setComponentsAdding(this, side));


        //漏斗应该有主动向被锁住的漏斗推送物品的能力
        if (be instanceof BlockEntityHopper sideHopper && levelBlockState.isDefaultState() && !sideHopper.isDisabled() || !(be instanceof InventoryHolder) && !(blockSide instanceof BlockComposter)) {
            return false;
        }

        InventoryMoveItemEvent event;

        //Fix for furnace inputs
        if (be instanceof BlockEntityFurnace furnace) {
            SmeltingInventory inventory = furnace.getInventory();
            if (inventory.isFull()) {
                return false;
            }

            boolean pushedItem = false;

            for (int i = 0; i < this.inventory.getSize(); i++) {
                Item item = this.inventory.getItem(i);
                if (!item.isNull()) {
                    Item itemToAdd = item.clone();
                    itemToAdd.setCount(1);

                    //Check direction of hopper
                    if (this.getBlock().getPropertyValue(CommonBlockProperties.FACING_DIRECTION) == 0) {
                        Item smelting = inventory.getSmelting();
                        if (smelting.isNull()) {
                            event = new InventoryMoveItemEvent(this.inventory, inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE);
                            this.server.getPluginManager().callEvent(event);

                            if (!event.isCancelled()) {
                                inventory.setSmelting(itemToAdd);
                                item.count--;
                                pushedItem = true;
                            }
                        } else if (inventory.getSmelting().getId().equals(itemToAdd.getId()) && inventory.getSmelting().getDamage() == itemToAdd.getDamage() && inventory.getSmelting().getId().equals(itemToAdd.getId()) && smelting.count < smelting.getMaxStackSize()) {
                            event = new InventoryMoveItemEvent(this.inventory, inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE);
                            this.server.getPluginManager().callEvent(event);

                            if (!event.isCancelled()) {
                                smelting.count++;
                                inventory.setSmelting(smelting);
                                item.count--;
                                pushedItem = true;
                            }
                        }
                    } else if (Registries.FUEL.isFuel(itemToAdd)) {
                        Item fuel = inventory.getFuel();
                        if (fuel.isNull()) {
                            event = new InventoryMoveItemEvent(this.inventory, inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE);
                            this.server.getPluginManager().callEvent(event);

                            if (!event.isCancelled()) {
                                inventory.setFuel(itemToAdd);
                                item.count--;
                                pushedItem = true;
                            }
                        } else if (fuel.getId().equals(itemToAdd.getId()) && fuel.getDamage() == itemToAdd.getDamage() && fuel.getId().equals(itemToAdd.getId()) && fuel.count < fuel.getMaxStackSize()) {
                            event = new InventoryMoveItemEvent(this.inventory, inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE);
                            this.server.getPluginManager().callEvent(event);

                            if (!event.isCancelled()) {
                                fuel.count++;
                                inventory.setFuel(fuel);
                                item.count--;
                                pushedItem = true;
                            }
                        }
                    }

                    if (pushedItem) {
                        this.inventory.setItem(i, item);
                    }
                }
            }

            return pushedItem;
        } else if (be instanceof BlockEntityBrewingStand brewingstand) {
            BrewingInventory inventory = brewingstand.getInventory();
            if (inventory.isFull()) {
                return false;
            }

            boolean pushedItem = false;

            for (int i = 0; i < this.inventory.getSize(); i++) {
                Item item = this.inventory.getItem(i);
                if (!item.isNull()) {
                    Item itemToAdd = item.clone();
                    itemToAdd.setCount(1);

                    //Check direction of hopper
                    if (this.getBlock().getPropertyValue(CommonBlockProperties.FACING_DIRECTION) == 0) {
                        Item ingredient = inventory.getIngredient();
                        if (ingredient.isNull()) {
                            event = new InventoryMoveItemEvent(this.inventory, inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE);
                            this.server.getPluginManager().callEvent(event);

                            if (!event.isCancelled()) {
                                inventory.setIngredient(itemToAdd);
                                item.count--;
                                pushedItem = true;
                            }
                        } else if (ingredient.getId().equals(itemToAdd.getId()) && ingredient.getDamage() == itemToAdd.getDamage() && ingredient.getId().equals(itemToAdd.getId()) && ingredient.count < ingredient.getMaxStackSize()) {
                            event = new InventoryMoveItemEvent(this.inventory, inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE);
                            this.server.getPluginManager().callEvent(event);

                            if (!event.isCancelled()) {
                                ingredient.count++;
                                inventory.setIngredient(ingredient);
                                item.count--;
                                pushedItem = true;
                            }
                        }
                    } else if (itemToAdd instanceof ItemPotion || itemToAdd instanceof ItemSplashPotion) {
                        Inventory productView = brewingstand.getProductView();
                        if (productView.canAddItem(itemToAdd)) {
                            for (int j = 1; j < 4; j++) {
                                if (inventory.getItem(j).isNull()) {
                                    inventory.setItem(j, itemToAdd);
                                    item.count--;
                                    pushedItem = true;
                                    break;
                                }
                            }
                        }
                    }

                    if (pushedItem) {
                        this.inventory.setItem(i, item);
                    }
                }
            }

            return pushedItem;
        } else if (blockSide instanceof BlockComposter composter) {
            if (composter.isFull()) {
                return false;
            }

            for (int i = 0; i < this.inventory.getSize(); i++) {
                Item item = this.inventory.getItem(i);

                if (item.isNull()) {
                    continue;
                }

                Item itemToAdd = item.clone();
                itemToAdd.setCount(1);

                if (!composter.onActivate(item, null, BlockFace.DOWN, 0, 0, 0)) {
                    return false;
                }
                item.count--;
                this.inventory.setItem(i, item);
                return true;
            }
        } else {
            Inventory inventory = be instanceof RecipeInventoryHolder recipeInventoryHolder ? recipeInventoryHolder.getIngredientView() : ((InventoryHolder) be).getInventory();

            if (inventory.isFull()) {
                return false;
            }

            for (int i = 0; i < this.inventory.getSize(); i++) {
                Item item = this.inventory.getItem(i);

                if (!item.isNull()) {
                    Item itemToAdd = item.clone();
                    itemToAdd.setCount(1);

                    if (!inventory.canAddItem(itemToAdd)) {
                        continue;
                    }

                    InventoryMoveItemEvent ev = new InventoryMoveItemEvent(this.inventory, inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE);
                    this.server.getPluginManager().callEvent(ev);

                    if (ev.isCancelled()) {
                        continue;
                    }

                    Item[] items = inventory.addItem(itemToAdd);

                    if (items.length > 0) {
                        continue;
                    }

                    item.count--;
                    this.inventory.setItem(i, item);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag c = super.getSpawnCompound().putBoolean("isMovable", this.isMovable());

        if (this.hasName()) {
            c.put("CustomName", this.nbt.get("CustomName").copy());
        }

        return c;
    }
}
