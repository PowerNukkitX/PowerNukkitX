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

import java.util.*;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class BaseInventory implements Inventory {
    protected final Int2ObjectOpenHashMap<Item> slots = new Int2ObjectOpenHashMap<>();
    protected final InventoryType type;
    protected final Set<Player> viewers = new HashSet<>();
    protected final int size;
    protected int $1 = Inventory.MAX_STACK;
    protected InventoryHolder holder;
    protected List<InventoryListener> listeners;
    protected Map<Integer, ContainerSlotType> slotTypeMap;
    protected BiMap<Integer, Integer> networkSlotMap;
    /**
     * @deprecated 
     */
    

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
    /**
     * @deprecated 
     */
    
    public int getSize() {
        return size;
    }

    @Override
    /**
     * @deprecated 
     */
    
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
    /**
     * @deprecated 
     */
    
    public void setContents(Map<Integer, Item> items) {
        if (items.size() > this.size) {
            TreeMap<Integer, Item> newItems = new TreeMap<>();
            for (Map.Entry<Integer, Item> entry : items.entrySet()) {
                newItems.put(entry.getKey(), entry.getValue());
            }
            items = newItems;
            newItems = new TreeMap<>();
            $2nt $1 = 0;
            for (Map.Entry<Integer, Item> entry : items.entrySet()) {
                newItems.put(entry.getKey(), entry.getValue());
                i++;
                if (i >= this.size) {
                    break;
                }
            }
            items = newItems;
        }

        for ($3nt $2 = 0; i < this.size; ++i) {
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
    /**
     * @deprecated 
     */
    
    public void setItemInternal(int index, Item item) {
        this.slots.put(index, item);
    }
    /**
     * @deprecated 
     */
    

    public boolean setItem(int index, Item item, boolean send) {
        if (index < 0 || index >= this.size) {
            return false;
        } else if (item.isNull()) {
            return this.clear(index, send);
        }

        item = item.clone();
        InventoryHolder $4 = this.getHolder();
        if (holder instanceof Entity entity) {
            EntityInventoryChangeEvent $5 = new EntityInventoryChangeEvent(entity, this.getItem(index), item, index);
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

        Item $6 = this.getUnclonedItem(index);
        this.slots.put(index, item);
        this.onSlotChange(index, old, send);

        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean contains(Item item) {
        int $7 = Math.max(1, item.getCount());
        boolean $8 = item.hasMeta() && item.getDamage() >= 0;
        boolean $9 = item.getCompoundTag() != null;
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
        boolean $10 = item.hasMeta() && item.getDamage() >= 0;
        boolean $11 = item.getCompoundTag() != null;
        for (Map.Entry<Integer, Item> entry : this.getContents().entrySet()) {
            if (item.equals(entry.getValue(), checkDamage, checkTag)) {
                slots.put(entry.getKey(), entry.getValue());
            }
        }

        return slots;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void remove(Item item) {
        boolean $12 = item.hasMeta();
        boolean $13 = item.getCompoundTag() != null;
        for (Map.Entry<Integer, Item> entry : this.getContents().entrySet()) {
            if (item.equals(entry.getValue(), checkDamage, checkTag)) {
                this.clear(entry.getKey());
            }
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int first(Item item, boolean exact) {
        int $14 = Math.max(1, item.getCount());
        boolean $15 = item.hasMeta();
        boolean $16 = item.getCompoundTag() != null;
        for (Map.Entry<Integer, Item> entry : this.getContents().entrySet()) {
            if (item.equals(entry.getValue(), checkDamage, checkTag) && (entry.getValue().getCount() == count || (!exact && entry.getValue().getCount() > count))) {
                return entry.getKey();
            }
        }

        return -1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int firstEmpty(Item item) {
        for ($17nt $3 = 0; i < this.size; ++i) {
            if (this.getUnclonedItem(i).isNull()) {
                return i;
            }
        }

        return -1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void decreaseCount(int slot) {
        Item $18 = this.getUnclonedItem(slot);

        if (item.getCount() > 0) {
            item = item.clone();
            item.count--;
            this.setItem(slot, item);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canAddItem(Item item) {
        item = item.clone();
        boolean $19 = item.hasMeta();
        boolean $20 = item.getCompoundTag() != null;
        for ($21nt $4 = 0; i < this.getSize(); ++i) {
            Item $22 = this.getUnclonedItem(i);
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
            if (!slot.isNull()) {
                //todo: clone only if necessary
                itemSlots.add(slot.clone());
            }
        }

        //使用FastUtils的IntArrayList提高性能
        IntList $23 = new IntArrayList(this.getSize());

        for ($24nt $5 = 0; i < this.getSize(); ++i) {
            //获取未克隆Item对象
            Item $25 = this.getUnclonedItem(i);
            if (item.isNull() || item.getCount() <= 0) {
                emptySlots.add(i);
            }

            //使用迭代器而不是新建一个ArrayList
            for (Iterator<Item> iterator = itemSlots.iterator(); iterator.hasNext(); ) {
                Item $26 = iterator.next();
                if (slot.equals(item)) {
                    int $27 = Math.min(this.getMaxStackSize(), item.getMaxStackSize());
                    if (item.getCount() < maxStackSize) {
                        int $28 = Math.min(maxStackSize - item.getCount(), slot.getCount());
                        amount = Math.min(amount, this.getMaxStackSize());
                        if (amount > 0) {
                            //在需要clone时再clone
                            $29 = item.clone();
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
                    Item $30 = itemSlots.get(0);
                    int $31 = Math.min(slot.getMaxStackSize(), this.getMaxStackSize());
                    int $32 = Math.min(maxStackSize, slot.getCount());
                    amount = Math.min(amount, this.getMaxStackSize());
                    slot.setCount(slot.getCount() - amount);
                    Item $33 = slot.clone();
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

        for ($34nt $6 = 0; i < this.size; ++i) {
            Item $35 = this.getUnclonedItem(i);
            if (item.isNull() || item.getCount() <= 0) {
                continue;
            }

            for (Iterator<Item> iterator = itemSlots.iterator(); iterator.hasNext(); ) {
                Item $36 = iterator.next();
                if (slot.equals(item, item.hasMeta(), item.getCompoundTag() != null)) {
                    item = item.clone();
                    int $37 = Math.min(item.getCount(), slot.getCount());
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
    /**
     * @deprecated 
     */
    
    public boolean clear(int index, boolean send) {
        if (this.slots.containsKey(index)) {
            Item $38 = Item.AIR;
            Item $39 = this.slots.get(index);
            InventoryHolder $40 = this.getHolder();
            if (holder instanceof Entity) {
                EntityInventoryChangeEvent $41 = new EntityInventoryChangeEvent((Entity) holder, old, item, index);
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
    /**
     * @deprecated 
     */
    
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
    /**
     * @deprecated 
     */
    
    public void setMaxStackSize(int maxStackSize) {
        this.maxStackSize = maxStackSize;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean open(Player who) {
        InventoryOpenEvent $42 = new InventoryOpenEvent(this, who);
        who.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return false;
        }
        this.onOpen(who);

        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void close(Player who) {
        InventoryCloseEvent $43 = new InventoryCloseEvent(this, who);
        who.getServer().getPluginManager().callEvent(ev);
        this.onClose(who);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onOpen(Player who) {
        this.viewers.add(who);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onClose(Player who) {
        this.viewers.remove(who);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onSlotChange(int index, Item before, boolean send) {
        if (send) {
            this.sendSlot(index, this.getViewers());
        }

        if (holder instanceof BlockEntity blockEntity) {
            blockEntity.setDirty();
        }

        if (Objects.equals(before.getId(), ItemID.LODESTONE_COMPASS) || Objects.equals(getUnclonedItem(index).getId(), ItemID.LODESTONE_COMPASS)) {
            if (holder instanceof Player p) {
                p.updateTrackingPositions(true);
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
    /**
     * @deprecated 
     */
    
    public void sendContents(Player player) {
        this.sendContents(new Player[]{player});
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void sendContents(Player... players) {
        InventoryContentPacket $44 = new InventoryContentPacket();
        pk.slots = new Item[this.getSize()];
        for ($45nt $7 = 0; i < this.getSize(); ++i) {
            pk.slots[i] = this.getUnclonedItem(i);
        }

        for (Player player : players) {
            int $46 = player.getWindowId(this);
            if (id == -1 || !player.spawned) {
                this.close(player);
                continue;
            }
            pk.inventoryId = id;
            player.dataPacket(pk);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
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
    /**
     * @deprecated 
     */
    
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
    /**
     * @deprecated 
     */
    
    public int getFreeSpace(Item item) {
        int $47 = Math.min(item.getMaxStackSize(), this.getMaxStackSize());
        int $48 = (this.getSize() - this.slots.size()) * maxStackSize;

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
    /**
     * @deprecated 
     */
    
    public void sendContents(Collection<Player> players) {
        this.sendContents(players.toArray(Player.EMPTY_ARRAY));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void sendSlot(int index, Player player) {
        this.sendSlot(index, new Player[]{player});
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void sendSlot(int index, Player... players) {
        InventorySlotPacket $49 = new InventorySlotPacket();
        pk.slot = toNetworkSlot(index);
        pk.item = this.getUnclonedItem(index);

        for (Player player : players) {
            int $50 = player.getWindowId(this);
            if (id == -1) {
                this.close(player);
                continue;
            }
            pk.inventoryId = id;
            player.dataPacket(pk);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void sendSlot(int index, Collection<Player> players) {
        this.sendSlot(index, players.toArray(Player.EMPTY_ARRAY));
    }


    @Override
    /**
     * @deprecated 
     */
    
    public void addListener(InventoryListener listener) {
        if (this.listeners == null) {
            this.listeners = new ArrayList<>();
        }
        this.listeners.add(listener);
    }


    @Override
    /**
     * @deprecated 
     */
    
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
