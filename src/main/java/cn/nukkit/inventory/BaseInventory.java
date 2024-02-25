package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityInventoryChangeEvent;
import cn.nukkit.event.inventory.InventoryCloseEvent;
import cn.nukkit.event.inventory.InventoryOpenEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.network.protocol.InventoryContentPacket;
import cn.nukkit.network.protocol.InventorySlotPacket;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class BaseInventory implements Inventory {
    protected final Int2ObjectOpenHashMap<Item> slots = new Int2ObjectOpenHashMap<>();
    protected final InventoryType type;
    protected final Set<Player> viewers = new HashSet<>();
    protected final int size;
    protected int maxStackSize = Inventory.MAX_STACK;
    protected InventoryHolder holder;
    protected List<InventoryListener> listeners;
    protected Map<Integer, ContainerSlotType> slotTypeMap;
    protected BiMap<Integer, Integer> networkSlotMap;

    public BaseInventory(InventoryHolder holder, InventoryType type, int size) {
        this.holder = holder;
        this.type = type;
        this.size = size;
        this.slotTypeMap = new HashMap<>();
        this.networkSlotMap = HashBiMap.create();
        init();
    }

    @Override
    public Map<Integer, ContainerSlotType> slotTypeMap() {
        return this.slotTypeMap;
    }

    @Override
    public BiMap<Integer, Integer> networkSlotMap() {
        return this.networkSlotMap;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public int getMaxStackSize() {
        return maxStackSize;
    }

    @NotNull
    @Override
    public Item getItem(int index) {
        return this.slots.containsKey(index) ? this.slots.get(index).clone() : Item.AIR;
    }

    @Override
    public Item getUnclonedItem(int index) {
        return this.slots.getOrDefault(index, Item.AIR);
    }

    @Override
    public Map<Integer, Item> getContents() {
        return new HashMap<>(this.slots);
    }

    @Override
    public void setContents(Map<Integer, Item> items) {
        if (items.size() > this.size) {
            TreeMap<Integer, Item> newItems = new TreeMap<>();
            for (Map.Entry<Integer, Item> entry : items.entrySet()) {
                newItems.put(entry.getKey(), entry.getValue());
            }
            items = newItems;
            newItems = new TreeMap<>();
            int i = 0;
            for (Map.Entry<Integer, Item> entry : items.entrySet()) {
                newItems.put(entry.getKey(), entry.getValue());
                i++;
                if (i >= this.size) {
                    break;
                }
            }
            items = newItems;
        }

        for (int i = 0; i < this.size; ++i) {
            if (!items.containsKey(i)) {
                if (this.slots.containsKey(i)) {
                    this.clear(i);
                }
            } else {
                if (!this.setItem(i, items.get(i))) {
                    this.clear(i);
                }
            }
        }
    }

    @ApiStatus.Internal
    public void setItemInternal(int index, Item item) {
        this.slots.put(index, item);
    }

    public boolean setItem(int index, Item item, boolean send) {
        if (index < 0 || index >= this.size) {
            return false;
        } else if (item.isNull()) {
            return this.clear(index, send);
        }

        item = item.clone();
        InventoryHolder holder = this.getHolder();
        if (holder instanceof Entity) {
            EntityInventoryChangeEvent ev = new EntityInventoryChangeEvent((Entity) holder, this.getItem(index), item, index);
            Server.getInstance().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                this.sendSlot(index, this.getViewers());
                return false;
            }

            item = ev.getNewItem();
        }

        if (holder instanceof BlockEntity) {
            ((BlockEntity) holder).setDirty();
        }

        Item old = this.getUnclonedItem(index);
        this.slots.put(index, item);
        this.onSlotChange(index, old, send);

        return true;
    }

    @Override
    public boolean contains(Item item) {
        int count = Math.max(1, item.getCount());
        boolean checkDamage = item.hasMeta() && item.getDamage() >= 0;
        boolean checkTag = item.getCompoundTag() != null;
        for (Item i : this.getContents().values()) {
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
        for (Map.Entry<Integer, Item> entry : this.getContents().entrySet()) {
            if (item.equals(entry.getValue(), checkDamage, checkTag)) {
                slots.put(entry.getKey(), entry.getValue());
            }
        }

        return slots;
    }

    @Override
    public void remove(Item item) {
        boolean checkDamage = item.hasMeta();
        boolean checkTag = item.getCompoundTag() != null;
        for (Map.Entry<Integer, Item> entry : this.getContents().entrySet()) {
            if (item.equals(entry.getValue(), checkDamage, checkTag)) {
                this.clear(entry.getKey());
            }
        }
    }

    @Override
    public int first(Item item, boolean exact) {
        int count = Math.max(1, item.getCount());
        boolean checkDamage = item.hasMeta();
        boolean checkTag = item.getCompoundTag() != null;
        for (Map.Entry<Integer, Item> entry : this.getContents().entrySet()) {
            if (item.equals(entry.getValue(), checkDamage, checkTag) && (entry.getValue().getCount() == count || (!exact && entry.getValue().getCount() > count))) {
                return entry.getKey();
            }
        }

        return -1;
    }

    @Override
    public int firstEmpty(Item item) {
        for (int i = 0; i < this.size; ++i) {
            if (this.getUnclonedItem(i).isNull()) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public void decreaseCount(int slot) {
        Item item = this.getUnclonedItem(slot);

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
        boolean checkBlock = item.isBlock();
        boolean checkTag = item.getCompoundTag() != null;
        for (int i = 0; i < this.getSize(); ++i) {
            Item slot = this.getUnclonedItem(i);
            if (item.equals(slot, checkDamage, checkBlock, checkTag)) {
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
            if (!slot.isNull()) {
                //todo: clone only if necessary
                itemSlots.add(slot.clone());
            }
        }

        //使用FastUtils的IntArrayList提高性能
        IntList emptySlots = new IntArrayList(this.getSize());

        for (int i = 0; i < this.getSize(); ++i) {
            //获取未克隆Item对象
            Item item = this.getUnclonedItem(i);
            if (item.isNull() || item.getCount() <= 0) {
                emptySlots.add(i);
            }

            //使用迭代器而不是新建一个ArrayList
            for (Iterator<Item> iterator = itemSlots.iterator(); iterator.hasNext(); ) {
                Item slot = iterator.next();
                if (slot.equals(item)) {
                    int maxStackSize = Math.min(this.getMaxStackSize(), item.getMaxStackSize());
                    if (item.getCount() < maxStackSize) {
                        int amount = Math.min(maxStackSize - item.getCount(), slot.getCount());
                        amount = Math.min(amount, this.getMaxStackSize());
                        if (amount > 0) {
                            //在需要clone时再clone
                            item = item.clone();
                            slot.setCount(slot.getCount() - amount);
                            item.setCount(item.getCount() + amount);
                            this.setItem(i, item);
                            if (slot.getCount() <= 0) {
                                iterator.remove();
                            }
                        }
                    }
                }
            }
            if (itemSlots.isEmpty()) {
                break;
            }
        }

        if (!itemSlots.isEmpty() && !emptySlots.isEmpty()) {
            for (int slotIndex : emptySlots) {
                if (!itemSlots.isEmpty()) {
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
        }

        return itemSlots.toArray(Item.EMPTY_ARRAY);
    }

    @Override
    public Item[] removeItem(Item... slots) {
        List<Item> itemSlots = new ArrayList<>();
        for (Item slot : slots) {
            if (!slot.isNull()) {
                itemSlots.add(slot.clone());
            }
        }

        for (int i = 0; i < this.size; ++i) {
            Item item = this.getUnclonedItem(i);
            if (item.isNull() || item.getCount() <= 0) {
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
        if (this.slots.containsKey(index)) {
            Item item = Item.AIR;
            Item old = this.slots.get(index);
            InventoryHolder holder = this.getHolder();
            if (holder instanceof Entity) {
                EntityInventoryChangeEvent ev = new EntityInventoryChangeEvent((Entity) holder, old, item, index);
                Server.getInstance().getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    this.sendSlot(index, this.getViewers());
                    return false;
                }
                item = ev.getNewItem();
            }

            if (!item.isNull()) {
                this.slots.put(index, item.clone());
            } else {
                this.slots.remove(index);
            }

            this.onSlotChange(index, old, send);
        }

        return true;
    }

    @Override
    public void clearAll() {
        for (Integer index : this.getContents().keySet()) {
            this.clear(index);
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
        InventoryCloseEvent ev = new InventoryCloseEvent(this, who);
        who.getServer().getPluginManager().callEvent(ev);
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

        if (before.getId() == ItemID.LODESTONE_COMPASS || getUnclonedItem(index).getId() == ItemID.LODESTONE_COMPASS) {
            if (holder instanceof Player) {
                ((Player) holder).updateTrackingPositions(true);
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
            pk.slots[i] = this.getUnclonedItem(i);
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
        if (this.slots.size() < this.getSize()) {
            return false;
        }

        for (Item item : this.slots.values()) {
            if (item == null || item.isNull() || item.getCount() < item.getMaxStackSize() || item.getCount() < this.getMaxStackSize()) {
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

        for (Item item : this.slots.values()) {
            if (item != null && !item.isNull()) {
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
        int space = (this.getSize() - this.slots.size()) * maxStackSize;

        for (Item slot : this.getContents().values()) {
            if (slot.isNull()) {
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
        pk.slot = toNetworkSlot(index);
        pk.item = this.getUnclonedItem(index);

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
}
