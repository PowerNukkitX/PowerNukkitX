package cn.nukkit.blockentity;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.impl.BlockComposter;
import cn.nukkit.block.impl.BlockHopper;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.event.block.HopperSearchItemEvent;
import cn.nukkit.event.inventory.InventoryMoveItemEvent;
import cn.nukkit.inventory.*;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.player.Player;
import java.util.HashSet;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

/**
 * @author CreeperFace
 * @since 8.5.2017
 */
public class BlockEntityHopper extends BlockEntitySpawnable
        implements InventoryHolder, BlockEntityContainer, BlockEntityNameable, BlockHopper.IHopper {

    protected HopperInventory inventory;

    public int transferCooldown;

    private AxisAlignedBB pickupArea;

    private boolean disabled;

    private final BlockVector3 temporalVector = new BlockVector3();

    // 由容器矿车检测漏斗并通知更新，这样子能大幅优化性能
    @Getter
    @Setter
    @PowerNukkitXOnly
    @Since("1.19.60-r1")
    private InventoryHolder minecartInvPickupFrom = null;

    @Getter
    @Setter
    @PowerNukkitXOnly
    @Since("1.19.60-r1")
    private InventoryHolder minecartInvPushTo = null;

    public BlockEntityHopper(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        super.initBlockEntity();
        this.scheduleUpdate();
    }

    @Since("1.19.60-r1")
    @Override
    public void loadNBT() {
        super.loadNBT();
        if (this.namedTag.contains("TransferCooldown")) {
            this.transferCooldown = this.namedTag.getInt("TransferCooldown");
        } else {
            this.transferCooldown = 8;
        }

        this.inventory = new HopperInventory(this);

        if (!this.namedTag.contains("Items") || !(this.namedTag.get("Items") instanceof ListTag)) {
            this.namedTag.putList(new ListTag<CompoundTag>("Items"));
        }

        for (int i = 0; i < this.getSize(); i++) {
            this.inventory.setItem(i, this.getItem(i));
        }

        this.pickupArea = generatePickupArea();

        checkDisabled();
    }

    @Since("1.20.0-r1")
    @PowerNukkitXOnly
    protected SimpleAxisAlignedBB generatePickupArea() {
        return new SimpleAxisAlignedBB(this.x(), this.y(), this.z(), this.x() + 1, this.y() + 2, this.z() + 1);
    }

    @Since("1.20.0-r1")
    @PowerNukkitXOnly
    protected void checkDisabled() {
        if (getBlock() instanceof BlockHopper blockHopper) {
            disabled = !(blockHopper).isEnabled();
        }
    }

    /**
     * @return How much ticks does it take for the hopper to transfer an item
     */
    @Since("1.20.0-r1")
    @PowerNukkitXOnly
    public int getCooldownTick() {
        return 8;
    }

    @Since("1.20.0-r1")
    @PowerNukkitXOnly
    protected boolean checkBlockStateValid(@NotNull BlockState levelBlockState) {
        return levelBlockState.getBlockId() == BlockID.HOPPER_BLOCK;
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getLevel().getBlockIdAt(this.getFloorX(), this.getFloorY(), this.getFloorZ()) == Block.HOPPER_BLOCK;
    }

    @Override
    public String getName() {
        return this.hasName() ? this.namedTag.getString("CustomName") : "Hopper";
    }

    @Override
    public boolean hasName() {
        return this.namedTag.contains("CustomName");
    }

    @Override
    public void setName(String name) {
        if (name == null || name.equals("")) {
            this.namedTag.remove("CustomName");
            return;
        }

        this.namedTag.putString("CustomName", name);
    }

    public boolean isOnTransferCooldown() {
        return this.transferCooldown > 0;
    }

    public void setTransferCooldown(int transferCooldown) {
        this.transferCooldown = transferCooldown;
    }

    @Override
    public int getSize() {
        return 5;
    }

    protected int getSlotIndex(int index) {
        ListTag<CompoundTag> list = this.namedTag.getList("Items", CompoundTag.class);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getByte("Slot") == index) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public Item getItem(int index) {
        int i = this.getSlotIndex(index);
        if (i < 0) {
            return new ItemBlock(Block.get(BlockID.AIR), 0, 0);
        } else {
            CompoundTag data = (CompoundTag) this.namedTag.getList("Items").get(i);
            return NBTIO.getItemHelper(data);
        }
    }

    @Override
    public void setItem(int index, Item item) {
        int i = this.getSlotIndex(index);

        CompoundTag d = NBTIO.putItemHelper(item, index);

        if (item.getId() == Item.AIR || item.getCount() <= 0) {
            if (i >= 0) {
                this.namedTag.getList("Items").getAll().remove(i);
            }
        } else if (i < 0) {
            (this.namedTag.getList("Items", CompoundTag.class)).add(d);
        } else {
            (this.namedTag.getList("Items", CompoundTag.class)).add(i, d);
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putList(new ListTag<CompoundTag>("Items"));
        for (int index = 0; index < this.getSize(); index++) {
            this.setItem(index, this.inventory.getItem(index));
        }

        this.namedTag.putInt("TransferCooldown", this.transferCooldown);
    }

    @Override
    public HopperInventory getInventory() {
        return inventory;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isDisabled() {
        return disabled;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public boolean onUpdate() {
        if (this.closed) {
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
        BlockEntity blockEntity =
                this.getLevel().getBlockEntity(temporalVector.setComponentsAdding(this, BlockFace.UP));

        boolean changed = pushItems() || pushItemsIntoMinecart();

        HopperSearchItemEvent event = new HopperSearchItemEvent(this, false);
        event.call();
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

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isObservable() {
        return false;
    }

    @PowerNukkitXOnly
    @Since("1.19.21-r2")
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
                    if (!this.inventory.canAddItem(itemToAdd)) continue;

                    InventoryMoveItemEvent event = new InventoryMoveItemEvent(
                            inv, this.inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE);
                    event.call();
                    if (event.isCancelled()) continue;

                    Item[] items = this.inventory.addItem(itemToAdd);
                    if (items.length >= 1) continue;

                    item.count--;
                    inv.setItem(i, item);

                    // 归位为null
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
    public void onBreak() {
        for (Item content : inventory.getContents().values()) {
            getLevel().dropItem(this, content);
        }
        this.inventory.clearAll();
    }

    @PowerNukkitXOnly
    @Since("1.19.21-r3")
    public boolean pushItemsIntoMinecart() {
        if (getMinecartInvPushTo() != null) {
            Inventory holderInventory = getMinecartInvPushTo().getInventory();

            if (holderInventory.isFull()) return false;

            for (int i = 0; i < this.inventory.getSize(); i++) {
                Item item = this.inventory.getItem(i);

                if (!item.isNull()) {
                    Item itemToAdd = item.clone();
                    itemToAdd.setCount(1);

                    if (!holderInventory.canAddItem(itemToAdd)) continue;

                    InventoryMoveItemEvent event = new InventoryMoveItemEvent(
                            this.inventory,
                            holderInventory,
                            this,
                            itemToAdd,
                            InventoryMoveItemEvent.Action.SLOT_CHANGE);
                    event.call();

                    if (event.isCancelled()) continue;

                    Item[] items = holderInventory.addItem(itemToAdd);

                    if (items.length > 0) continue;

                    item.count--;
                    this.inventory.setItem(i, item);

                    // 归位为null
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

        BlockFace side = levelBlockState.getPropertyValue(CommonBlockProperties.FACING_DIRECTION);
        Block blockSide = this.getSide(side).getTickCachedLevelBlock();
        BlockEntity be = this.getLevel().getBlockEntity(temporalVector.setComponentsAdding(this, side));

        // 漏斗应该有主动向被锁住的漏斗推送物品的能力
        if (be instanceof BlockEntityHopper sideHopper && levelBlockState.isDefaultState() && !sideHopper.isDisabled()
                || !(be instanceof InventoryHolder) && !(blockSide instanceof BlockComposter)) {
            return false;
        }

        InventoryMoveItemEvent event;

        // Fix for furnace inputs
        if (be instanceof BlockEntityFurnace) {
            BlockEntityFurnace furnace = (BlockEntityFurnace) be;
            FurnaceInventory inventory = furnace.getInventory();
            if (inventory.isFull()) {
                return false;
            }

            boolean pushedItem = false;

            for (int i = 0; i < this.inventory.getSize(); i++) {
                Item item = this.inventory.getItem(i);
                if (!item.isNull()) {
                    Item itemToAdd = item.clone();
                    itemToAdd.setCount(1);

                    // Check direction of hopper
                    if (this.getBlock().getDamage() == 0) {
                        Item smelting = inventory.getSmelting();
                        if (smelting.isNull()) {
                            event = new InventoryMoveItemEvent(
                                    this.inventory,
                                    inventory,
                                    this,
                                    itemToAdd,
                                    InventoryMoveItemEvent.Action.SLOT_CHANGE);
                            event.call();

                            if (!event.isCancelled()) {
                                inventory.setSmelting(itemToAdd);
                                item.count--;
                                pushedItem = true;
                            }
                        } else if (inventory.getSmelting().getId() == itemToAdd.getId()
                                && inventory.getSmelting().getDamage() == itemToAdd.getDamage()
                                && inventory.getSmelting().getNamespaceId().equals(itemToAdd.getNamespaceId())
                                && smelting.count < smelting.getMaxStackSize()) {
                            event = new InventoryMoveItemEvent(
                                    this.inventory,
                                    inventory,
                                    this,
                                    itemToAdd,
                                    InventoryMoveItemEvent.Action.SLOT_CHANGE);
                            event.call();

                            if (!event.isCancelled()) {
                                smelting.count++;
                                inventory.setSmelting(smelting);
                                item.count--;
                                pushedItem = true;
                            }
                        }
                    } else if (Fuel.isFuel(itemToAdd)) {
                        Item fuel = inventory.getFuel();
                        if (fuel.isNull()) {
                            event = new InventoryMoveItemEvent(
                                    this.inventory,
                                    inventory,
                                    this,
                                    itemToAdd,
                                    InventoryMoveItemEvent.Action.SLOT_CHANGE);
                            event.call();

                            if (!event.isCancelled()) {
                                inventory.setFuel(itemToAdd);
                                item.count--;
                                pushedItem = true;
                            }
                        } else if (fuel.getId() == itemToAdd.getId()
                                && fuel.getDamage() == itemToAdd.getDamage()
                                && fuel.getNamespaceId().equals(itemToAdd.getNamespaceId())
                                && fuel.count < fuel.getMaxStackSize()) {
                            event = new InventoryMoveItemEvent(
                                    this.inventory,
                                    inventory,
                                    this,
                                    itemToAdd,
                                    InventoryMoveItemEvent.Action.SLOT_CHANGE);
                            event.call();

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

        } else if (blockSide instanceof BlockComposter) {
            BlockComposter composter = (BlockComposter) blockSide;
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

                if (!composter.onActivate(item)) {
                    return false;
                }
                item.count--;
                this.inventory.setItem(i, item);
                return true;
            }
        } else {
            Inventory inventory = be instanceof RecipeInventoryHolder recipeInventoryHolder
                    ? recipeInventoryHolder.getIngredientView()
                    : ((InventoryHolder) be).getInventory();

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

                    InventoryMoveItemEvent event1 = new InventoryMoveItemEvent(
                            this.inventory, inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE);
                    event1.call();

                    if (event1.isCancelled()) {
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
        CompoundTag c = new CompoundTag()
                .putString("id", BlockEntity.HOPPER)
                .putInt("x", (int) this.x())
                .putInt("y", (int) this.y())
                .putInt("z", (int) this.z());

        if (this.hasName()) {
            c.put("CustomName", this.namedTag.get("CustomName"));
        }

        return c;
    }

    @Override
    public Position getPosition() {
        return this.getBlock();
    }
}
