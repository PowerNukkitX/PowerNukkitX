package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityInventoryChangeEvent;
import cn.nukkit.event.inventory.InventoryOpenEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemID;
import cn.nukkit.network.protocol.InventoryContentPacket;
import cn.nukkit.network.protocol.InventorySlotPacket;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class BaseInventory implements Inventory {
    protected final Item[] slots;
    protected final InventoryType type;
    protected int maxStackSize = Inventory.MAX_STACK;
    protected int size;
    protected final String name;
    protected final Set<Player> viewers = new HashSet<>();
    protected InventoryHolder holder;
    protected List<InventoryListener> listeners;

    public BaseInventory(InventoryHolder holder, InventoryType type) {
        this.holder = holder;

        this.type = type;

        this.size = this.type.getSize();

        this.name = this.type.getName();

        this.slots = new Item[size];
        Arrays.fill(slots, Item.AIR);
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public int getMaxStackSize() {
        return maxStackSize;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    @NotNull
    public Item getItem(int index) {
        Preconditions.checkArgument(index >= 0 && index < slots.length);
        return this.slots[index].clone();
    }

    @Override
    @ApiStatus.Internal
    public Item getItemUnsafe(int index) {
        return this.slots[index];
    }

    @Override
    @ApiStatus.Internal
    public Item[] getContents() {
        return this.slots;
    }

    @Override
    public void setContents(Map<Integer, Item> items) {
        for (var entry : items.entrySet()) {
            Item item = entry.getValue();
            if (item == null || item.isNull()) {
                this.clear(entry.getKey());
            } else {
                if (!this.setItem(entry.getKey(), entry.getValue())) {
                    this.clear(entry.getKey());
                }
            }
        }
    }

    @Override
    public boolean setItem(int index, Item item, boolean send) {
        if (checkIndex(index)) {
            return false;
        } else if (item.isNull() || item.getCount() <= 0) {
            return this.clear(index, send);
        }

        InventoryHolder holder = this.getHolder();
        if (holder instanceof Entity) {
            item = item.clone();
            EntityInventoryChangeEvent ev = new EntityInventoryChangeEvent((Entity) holder, this.getItem(index), item, index);
            Server.getInstance().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                this.sendSlot(index, this.getViewers());
                return false;
            }

            item = ev.getNewItem();
        }

        if (holder instanceof BlockEntity blockEntity) {
            blockEntity.setDirty();
        }

        Item old = this.getItemUnsafe(index);
        this.slots[index] = item.clone();
        this.onSlotChange(index, old, send);
        return true;
    }

    @Override
    public boolean contains(Item item) {
        int count = Math.max(1, item.getCount());
        boolean checkDamage = item.hasMeta() && item.getDamage() >= 0;
        boolean checkTag = item.getCompoundTag() != null;
        for (Item i : this.getContents()) {
            if (item.equals(i, checkDamage, checkTag)) {
                count -= i.getCount();
                if (count <= 0) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public Map<Integer, Item> all(Item item) {
        Map<Integer, Item> slots = new HashMap<>();
        boolean checkDamage = item.hasMeta() && item.getDamage() >= 0;
        boolean checkTag = item.getCompoundTag() != null;
        for (int i = 0; i < size; i++) {
            Item slot = this.slots[i];
            if (item.equals(slot, checkDamage, checkTag)) {
                slots.put(i, slot.clone());
            }
        }
        return slots;
    }

    @Override
    public void remove(Item item) {
        if (item.isNull()) return;
        boolean checkDamage = item.hasMeta();
        boolean checkTag = item.getCompoundTag() != null;
        for (int i = 0; i < size; i++) {
            Item slot = this.slots[i];
            if (item.equals(slot, checkDamage, checkTag)) {
                this.clear(i);
            }
        }
    }

    @Override
    public int first(Item item, boolean exact) {
        int count = Math.max(1, item.getCount());
        boolean checkDamage = item.hasMeta();
        boolean checkTag = item.getCompoundTag() != null;
        for (int i = 0; i < size; i++) {
            Item slot = this.slots[i];
            if (item.equals(slot, checkDamage, checkTag) && (slot.getCount() == count || (!exact && slot.getCount() > count))) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int firstEmpty(Item item) {
        for (int i = 0; i < this.size; ++i) {
            if (this.getItemUnsafe(i).isNull()) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public void decreaseCount(int slot) {
        Item item = this.getItemUnsafe(slot);

        if (item.getCount() > 0) {
            item = item.clone();
            item.count--;
            this.setItem(slot, item);
        }
    }

    @Override
    public boolean canAddItem(Item item) {
        item = item.clone();
        boolean checkDamage = item.hasMeta();
        boolean checkTag = item.getCompoundTag() != null;
        for (int i = 0; i < this.getSize(); ++i) {
            Item slot = this.getItemUnsafe(i);
            if (item.equals(slot, checkDamage, checkTag)) {
                int diff;
                if ((diff = Math.min(slot.getMaxStackSize(), this.getMaxStackSize()) - slot.getCount()) > 0) {
                    item.setCount(item.getCount() - diff);
                }
            } else if (slot.isNull()) {
                item.setCount(item.getCount() - Math.min(slot.getMaxStackSize(), this.getMaxStackSize()));
            }

            if (item.getCount() <= 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Item[] addItem(Item... slots) {
        List<Item> itemSlots = new ArrayList<>();
        for (Item slot : slots) {
            if (slot.isNull()) {
                continue;
            }
            itemSlots.add(slot.clone());
        }

        //使用FastUtils的IntArrayList提高性能
        IntList emptySlots = new IntArrayList(this.getSize());

        for (int i = 0; i < this.getSize(); ++i) {
            //获取未克隆Item对象
            Item item = this.getItemUnsafe(i);
            if (item.isNull()) {
                emptySlots.add(i);
            }

            //使用迭代器而不是新建一个ArrayList
            for (Iterator<Item> iterator = itemSlots.iterator(); iterator.hasNext(); ) {
                Item slot = iterator.next();
                if (!slot.equals(item)) {
                    continue;
                }
                int maxStackSize = Math.min(this.getMaxStackSize(), item.getMaxStackSize());
                if (item.getCount() >= maxStackSize) {
                    continue;
                }
                int amount = Math.min(maxStackSize - item.getCount(), slot.getCount());
                amount = Math.min(amount, this.getMaxStackSize());
                if (amount <= 0) {
                    continue;
                }
                //在需要clone时再clone
                item = item.clone();
                slot.setCount(slot.getCount() - amount);
                item.setCount(item.getCount() + amount);
                this.setItem(i, item);
                if (slot.getCount() <= 0) {
                    iterator.remove();
                }
            }
            if (itemSlots.isEmpty()) {
                break;
            }
        }

        if (!itemSlots.isEmpty() && !emptySlots.isEmpty()) {
            for (int slotIndex : emptySlots) {
                if (itemSlots.isEmpty()) {
                    continue;
                }
                Item slot = itemSlots.get(0);
                int maxStackSize = Math.min(slot.getMaxStackSize(), this.getMaxStackSize());
                int amount = Math.min(maxStackSize, slot.getCount());
                amount = Math.min(amount, this.getMaxStackSize());
                slot.setCount(slot.getCount() - amount);
                Item item = slot.clone();
                item.setCount(amount);
                this.setItem(slotIndex, item);
                if (slot.getCount() <= 0) {
                    itemSlots.remove(slot);
                }
            }
        }

        return itemSlots.toArray(Item.EMPTY_ARRAY);
    }

    @Override
    public Item[] removeItem(Item... slots) {
        List<Item> itemSlots = new ArrayList<>();
        for (Item slot : slots) {
            if (slot.isNull()) {
                continue;
            }
            itemSlots.add(slot.clone());
        }

        for (int i = 0; i < this.size; ++i) {
            Item item = this.getItemUnsafe(i);
            if (item.isNull()) {
                continue;
            }

            for (Iterator<Item> iterator = itemSlots.iterator(); iterator.hasNext(); ) {
                Item slot = iterator.next();
                if (slot.equals(item, item.hasMeta(), item.getCompoundTag() != null)) {
                    item = item.clone();
                    int amount = Math.min(item.getCount(), slot.getCount());
                    slot.setCount(slot.getCount() - amount);
                    item.setCount(item.getCount() - amount);
                    this.setItem(i, item);
                    if (slot.getCount() <= 0) {
                        iterator.remove();
                    }

                }
            }

            if (itemSlots.isEmpty()) {
                break;
            }
        }

        return itemSlots.toArray(Item.EMPTY_ARRAY);
    }

    @Override
    public boolean clear(int index, boolean send) {
        Item item = new ItemBlock(Block.get(BlockID.AIR), null, 0);
        Item old = this.slots[index];
        InventoryHolder holder = this.getHolder();
        if (holder instanceof Entity entity) {
            EntityInventoryChangeEvent ev = new EntityInventoryChangeEvent(entity, old, item, index);
            Server.getInstance().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                this.sendSlot(index, this.getViewers());
                return false;
            }
            item = ev.getNewItem();
        }

        if (item.isNull()) {
            this.slots[index] = item.clone();
        } else {
            this.slots[index] = Item.AIR;
        }

        this.onSlotChange(index, old, send);

        return true;
    }

    @Override
    public void clearAll() {
        for (int i = 0; i < size; i++) {
            if (slots[i] != Item.AIR) {
                this.clear(i);
            }
        }
    }

    @Override
    public Set<Player> getViewers() {
        return viewers;
    }

    @Override
    public InventoryHolder getHolder() {
        return holder;
    }

    @Override
    public void setMaxStackSize(int maxStackSize) {
        this.maxStackSize = maxStackSize;
    }

    @Override
    public boolean open(Player who) {
        //if (this.viewers.contains(who)) return false;
        InventoryOpenEvent ev = new InventoryOpenEvent(this, who);
        who.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return false;
        }
        this.onOpen(who);

        return true;
    }

    @Override
    public void close(Player who) {
        this.onClose(who);
    }

    @Override
    public void onOpen(Player who) {
        this.viewers.add(who);
    }

    @Override
    public void onClose(Player who) {
        this.viewers.remove(who);
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        if (send) {
            this.sendSlot(index, this.getViewers());
        }

        if (holder instanceof BlockEntity) {
            ((BlockEntity) holder).setDirty();
        }

        if (ItemID.LODESTONE_COMPASS.equals(before.getId()) ||
                ItemID.LODESTONE_COMPASS.equals(getItemUnsafe(index).getId())) {
            if (holder instanceof Player player) {
                player.updateTrackingPositions(true);
            }

            getViewers().forEach(p -> p.updateTrackingPositions(true));
        }

        if (this.listeners != null) {
            for (InventoryListener listener : listeners) {
                listener.onInventoryChanged(this, before, index);
            }
        }
    }

    @Override
    public void sendContents(Player player) {
        this.sendContents(new Player[]{player});
    }

    @Override
    public void sendContents(Player... players) {
        InventoryContentPacket pk = new InventoryContentPacket();
        pk.slots = new Item[this.getSize()];
        for (int i = 0; i < this.getSize(); ++i) {
            pk.slots[i] = this.getItemUnsafe(i);
        }

        for (Player player : players) {
            int id = player.getWindowId(this);
            if (id == -1 || !player.spawned) {
                this.close(player);
                continue;
            }
            pk.inventoryId = id;
            player.dataPacket(pk);
        }
    }

    @Override
    public boolean isFull() {
        long count = getFillSize();
        if (count < this.getSize()) {
            return false;
        }

        for (Item item : this.slots) {
            if (item.getCount() < this.getMaxStackSize()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isEmpty() {
        if (this.getMaxStackSize() <= 0) {
            return false;
        }
        for (Item item : this.slots) {
            if (!item.isNull()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检测指定物品能在该库存所能存放的空余数量
     *
     * @param item 要检测的物品
     * @return 所能存放的空余数量
     */
    @Override
    public int getFreeSpace(Item item) {
        int maxStackSize = Math.min(item.getMaxStackSize(), this.getMaxStackSize());
        int space = (this.getSize() - getFillSize()) * maxStackSize;

        for (Item slot : this.getContents()) {
            if (slot == null || slot.isNull()) {
                space += maxStackSize;
                continue;
            }

            if (slot.equals(item, true, true)) {
                space += maxStackSize - slot.getCount();
            }
        }

        return space;
    }

    @Override
    public void sendContents(Collection<Player> players) {
        this.sendContents(players.toArray(Player.EMPTY_ARRAY));
    }

    @Override
    public void sendSlot(int index, Player player) {
        this.sendSlot(index, new Player[]{player});
    }

    @Override
    public void sendSlot(int index, Player... players) {
        InventorySlotPacket pk = new InventorySlotPacket();
        pk.slot = index;
        pk.item = this.getItemUnsafe(index);

        for (Player player : players) {
            int id = player.getWindowId(this);
            if (id == -1) {
                this.close(player);
                continue;
            }
            pk.inventoryId = id;
            player.dataPacket(pk);
        }
    }

    @Override
    public void sendSlot(int index, Collection<Player> players) {
        this.sendSlot(index, players.toArray(Player.EMPTY_ARRAY));
    }

    @Override
    public void addListener(InventoryListener listener) {
        if (this.listeners == null) {
            this.listeners = new ArrayList<>();
        }

        this.listeners.add(listener);
    }

    @Override
    public void removeListener(InventoryListener listener) {
        if (this.listeners != null) {
            this.listeners.remove(listener);
        }
    }

    @Override
    public InventoryType getType() {
        return type;
    }

    protected boolean checkIndex(int index) {
        return index < 0 || index >= slots.length;
    }

    protected int getFillSize() {
        return (int) Arrays.stream(this.slots).filter(i -> !i.isNull()).count();
    }
}
