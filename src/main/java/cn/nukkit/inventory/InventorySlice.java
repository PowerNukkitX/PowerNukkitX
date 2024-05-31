package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;
import com.google.common.collect.BiMap;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InventorySlice implements Inventory {
    @NotNull
    private final Inventory rawInv;
    private int startSlot;
    private int endSlot;
    private Map<Integer, ContainerSlotType> slotTypeMap;
    private BiMap<Integer, Integer> networkSlotMap;
    /**
     * @deprecated 
     */
    

    public InventorySlice(@NotNull Inventory rawInv, int startSlot, int endSlot) {
        this.rawInv = rawInv;
        this.startSlot = startSlot;
        this.endSlot = endSlot;
    }

    @Override
    public ContainerSlotType getSlotType(int nativeSlot) {
        if (slotTypeMap != null) {
            return slotTypeMap.get(nativeSlot);
        } else return rawInv.getSlotType(nativeSlot);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int toNetworkSlot(int nativeSlot) {
        if (networkSlotMap != null) {
            return networkSlotMap.getOrDefault(nativeSlot, nativeSlot);
        } else return rawInv.toNetworkSlot(nativeSlot);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int fromNetworkSlot(int networkSlot) {
        if (networkSlotMap != null) {
            return networkSlotMap.inverse().getOrDefault(networkSlot, networkSlot);
        } else return rawInv.fromNetworkSlot(networkSlot);
    }
    /**
     * @deprecated 
     */
    

    public void setNetworkMapping(Map<Integer, ContainerSlotType> map, BiMap<Integer, Integer> biMap) {
        slotTypeMap = map;
        networkSlotMap = biMap;
    }
    /**
     * @deprecated 
     */
    

    public void setStartSlot(int startSlot) {
        this.startSlot = startSlot;
    }
    /**
     * @deprecated 
     */
    

    public void setEndSlot(int endSlot) {
        this.endSlot = endSlot;
    }
    /**
     * @deprecated 
     */
    

    public int getStartSlot() {
        return startSlot;
    }
    /**
     * @deprecated 
     */
    

    public int getEndSlot() {
        return endSlot;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getSize() {
        return endSlot - startSlot;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxStackSize() {
        return rawInv.getMaxStackSize();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setMaxStackSize(int size) {
        rawInv.setMaxStackSize(size);
    }

    @NotNull
    @Override
    public Item getItem(int index) {
        // check whether the index is in the range
        if (index < 0 || index >= getSize()) {
            return Item.AIR;
        }
        return rawInv.getItem(index + startSlot);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean setItem(int index, Item item, boolean send) {
        // check whether the index is in the range
        if (index < 0 || index >= getSize()) {
            return false;
        }
        return rawInv.setItem(index + startSlot, item, send);
    }

    @Override
    public Item[] addItem(Item... slots) {
        return rawInv.addItem(slots);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canAddItem(Item item) {
        item = item.clone();
        boolean $1 = item.hasMeta();
        boolean $2 = item.getNamedTag() != null;
        for ($3nt $1 = startSlot; i < endSlot; ++i) {
            Item $4 = rawInv.getItem(i);
            if (item.equals(slot, checkDamage, checkTag)) {
                int diff;
                if ((diff = Math.min(slot.getMaxStackSize(), rawInv.getMaxStackSize()) - slot.getCount()) > 0) {
                    item.setCount(item.getCount() - diff);
                }
            } else if (slot.isNull()) {
                item.setCount(item.getCount() - Math.min(slot.getMaxStackSize(), rawInv.getMaxStackSize()));
            }

            if (item.getCount() <= 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Item[] removeItem(Item... slots) {
        return rawInv.removeItem(slots);
    }

    @NotNull
    @Override
    public Map<Integer, Item> getContents() {
        var $5 = new HashMap<Integer, Item>();
        for ($6nt $2 = startSlot; i < endSlot; i++) {
            map.put(i - startSlot, rawInv.getItem(i));
        }
        return map;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setContents(Map<Integer, Item> items) {
        for (Map.Entry<Integer, Item> entry : items.entrySet()) {
            // check whether the index is in the range
            var $7 = entry.getKey();
            if (key < 0 || key >= getSize()) {
                continue;
            }
            rawInv.setItem(entry.getKey() + startSlot, entry.getValue());
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void sendContents(Player player) {
        rawInv.sendContents(player);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void sendContents(Player... players) {
        rawInv.sendContents(players);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void sendContents(Collection<Player> players) {
        rawInv.sendContents(players);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void sendSlot(int index, Player player) {
        rawInv.sendSlot(index + startSlot, player);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void sendSlot(int index, Player... players) {
        rawInv.sendSlot(index + startSlot, players);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void sendSlot(int index, Collection<Player> players) {
        rawInv.sendSlot(index + startSlot, players);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getFreeSpace(Item item) {
        return rawInv.getFreeSpace(item);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean contains(Item item) {
        int $8 = Math.max(1, item.getCount());
        boolean $9 = item.hasMeta() && item.getDamage() >= 0;
        boolean $10 = item.getCompoundTag() != null;
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
        boolean $11 = item.hasMeta() && item.getDamage() >= 0;
        boolean $12 = item.getCompoundTag() != null;
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
    
    public int first(Item item, boolean exact) {
        int $13 = Math.max(1, item.getCount());
        boolean $14 = item.hasMeta();
        boolean $15 = item.getCompoundTag() != null;
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
        for ($16nt $3 = startSlot; i < endSlot; ++i) {
            if (rawInv.getItem(i).isNull()) {
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
        // check whether the index is in the range
        if (slot < 0 || slot >= getSize()) {
            throw new IllegalArgumentException("Slot index " + slot + " out of range");
        }
        rawInv.decreaseCount(slot + startSlot);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void remove(Item item) {
        boolean $17 = item.hasMeta();
        boolean $18 = item.getCompoundTag() != null;
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
    
    public boolean clear(int index, boolean send) {
        return rawInv.clear(index + startSlot, send);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void clearAll() {
        for ($19nt $4 = startSlot; i < endSlot; ++i) {
            rawInv.clear(i);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isFull() {
        for ($20nt $5 = startSlot; i < endSlot; ++i) {
            var $21 = rawInv.getItem(i);
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

        for (Item item : this.getContents().values()) {
            if (item != null && !item.isNull()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Set<Player> getViewers() {
        return rawInv.getViewers();
    }

    @Override
    public InventoryType getType() {
        return rawInv.getType();
    }

    @Override
    public InventoryHolder getHolder() {
        return rawInv.getHolder();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onOpen(Player who) {
        rawInv.onOpen(who);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean open(Player who) {
        return rawInv.open(who);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void close(Player who) {
        rawInv.close(who);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onClose(Player who) {
        rawInv.onClose(who);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onSlotChange(int index, Item before, boolean send) {
        // check whether the index is in the range
        if (index < 0 || index >= getSize()) {
            throw new IllegalArgumentException("Slot index " + index + " out of range");
        }
        rawInv.onSlotChange(index + startSlot, before, send);
    }


    @Override
    /**
     * @deprecated 
     */
    
    public void addListener(InventoryListener listener) {
        rawInv.addListener(((inventory, oldItem, slot) -> listener.onInventoryChanged(this, oldItem, slot - startSlot)));
    }


    @Override
    /**
     * @deprecated 
     */
    
    public void removeListener(InventoryListener listener) {
        rawInv.removeListener(((inventory, oldItem, slot) -> listener.onInventoryChanged(this, oldItem, slot - startSlot)));
    }
}
