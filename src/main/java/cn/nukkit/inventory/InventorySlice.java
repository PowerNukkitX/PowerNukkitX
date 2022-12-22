package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Since("1.19.50-r3")
@PowerNukkitXOnly
public class InventorySlice implements Inventory {
    @NotNull
    public final Inventory rawInv;
    public int startSlot;
    public int endSlot;

    public InventorySlice(@NotNull Inventory rawInv, int startSlot, int endSlot) {
        this.rawInv = rawInv;
        this.startSlot = startSlot;
        this.endSlot = endSlot;
    }

    public void setStartSlot(int startSlot) {
        this.startSlot = startSlot;
    }

    public void setEndSlot(int endSlot) {
        this.endSlot = endSlot;
    }

    public int getStartSlot() {
        return startSlot;
    }

    public int getEndSlot() {
        return endSlot;
    }

    @Override
    public int getSize() {
        return endSlot - startSlot;
    }

    @Override
    public int getMaxStackSize() {
        return rawInv.getMaxStackSize();
    }

    @Override
    public void setMaxStackSize(int size) {
        rawInv.setMaxStackSize(size);
    }

    @Override
    public String getName() {
        return rawInv.getName();
    }

    @Override
    public String getTitle() {
        return rawInv.getTitle();
    }

    @Override
    public Item getItem(int index) {
        return rawInv.getItem(index + startSlot);
    }

    @Override
    public boolean setItem(int index, Item item, boolean send) {
        return rawInv.setItem(index + startSlot, item, send);
    }

    @Override
    public Item[] addItem(Item... slots) {
        return rawInv.addItem(slots);
    }

    @Override
    public boolean canAddItem(Item item) {
        item = item.clone();
        boolean checkDamage = item.hasMeta();
        boolean checkTag = item.getNamedTag() != null;
        for (int i = startSlot; i < endSlot; ++i) {
            Item slot = rawInv.getItem(i);
            if (item.equals(slot, checkDamage, checkTag)) {
                int diff;
                if ((diff = Math.min(slot.getMaxStackSize(), rawInv.getMaxStackSize()) - slot.getCount()) > 0) {
                    item.setCount(item.getCount() - diff);
                }
            } else if (slot.getId() == Item.AIR) {
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
        var map = new HashMap<Integer, Item>();
        for (int i = startSlot; i < endSlot; i++) {
            map.put(i - startSlot, rawInv.getItem(i));
        }
        return map;
    }

    @Override
    public void setContents(Map<Integer, Item> items) {
        for (Map.Entry<Integer, Item> entry : items.entrySet()) {
            rawInv.setItem(entry.getKey() + startSlot, entry.getValue());
        }
    }

    @Override
    public void sendContents(Player player) {
        rawInv.sendContents(player);
    }

    @Override
    public void sendContents(Player... players) {
        rawInv.sendContents(players);
    }

    @Override
    public void sendContents(Collection<Player> players) {
        rawInv.sendContents(players);
    }

    @Override
    public void sendSlot(int index, Player player) {
        rawInv.sendSlot(index + startSlot, player);
    }

    @Override
    public void sendSlot(int index, Player... players) {
        rawInv.sendSlot(index + startSlot, players);
    }

    @Override
    public void sendSlot(int index, Collection<Player> players) {
        rawInv.sendSlot(index + startSlot, players);
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
        for (int i = startSlot; i < endSlot; ++i) {
            if (rawInv.getItem(i).getId() == Item.AIR) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public void decreaseCount(int slot) {
        rawInv.decreaseCount(slot + startSlot);
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
    public boolean clear(int index, boolean send) {
        return rawInv.clear(index + startSlot, send);
    }

    @Override
    public void clearAll() {
        for (int i = startSlot; i < endSlot; ++i) {
            rawInv.clear(i);
        }
    }

    @Override
    public boolean isFull() {
        for (int i = startSlot; i < endSlot; ++i) {
            if (rawInv.getItem(i).getId() == Item.AIR) {
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

        for (Item item : this.getContents().values()) {
            if (item != null && item.getId() != 0 && item.getCount() > 0) {
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
    public void onOpen(Player who) {
        rawInv.onOpen(who);
    }

    @Override
    public boolean open(Player who) {
        return rawInv.open(who);
    }

    @Override
    public void close(Player who) {
        rawInv.close(who);
    }

    @Override
    public void onClose(Player who) {
        rawInv.onClose(who);
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        rawInv.onSlotChange(index + startSlot, before, send);
    }

    @PowerNukkitOnly
    @Override
    public void addListener(InventoryListener listener) {
        rawInv.addListener(((inventory, oldItem, slot) -> listener.onInventoryChanged(this, oldItem, slot - startSlot)));
    }

    @PowerNukkitOnly
    @Override
    public void removeListener(InventoryListener listener) {
        rawInv.removeListener(((inventory, oldItem, slot) -> listener.onInventoryChanged(this, oldItem, slot - startSlot)));
    }
}
