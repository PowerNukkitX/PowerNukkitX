package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public interface Inventory {

    int MAX_STACK = 64;

    /**
     * 获取该库存大小
     */
    int getSize();

    /**
     * 获取最大库存大小
     */
    int getMaxStackSize();

    /**
     * 设置最大库存大小
     */
    void setMaxStackSize(int size);

    /**
     * 获取该库存的名字
     */
    String getName();

    /**
     * 获取该库存的标题
     */
    String getTitle();

    /**
     * 获取该库存指定索引处的物品
     *
     * @param index the index
     * @return the item
     */
    Item getItem(int index);

    /**
     * 设置该库存指定索引处的物品
     *
     * @param index the index
     * @param item  the item
     * @return the item
     */
    default boolean setItem(int index, Item item) {
        return setItem(index, item, true);
    }

    /**
     * 设置该库存指定索引处的物品
     *
     * @param index the index
     * @param item  the item
     * @param send  是否同时发送数据包
     * @return the item
     */
    boolean setItem(int index, Item item, boolean send);

    /**
     * Now it is only called by {@link cn.nukkit.inventory.transaction.action.SlotChangeAction} and {@link cn.nukkit.inventory.transaction.EnchantTransaction}
     *
     * @param player player that will receive the changes
     * @param index index of the item
     * @param item item to set
     * @return true if the item was set
     */
    @Since("1.19.50-r3")
    @PowerNukkitXOnly
    default boolean setItemByPlayer(Player player, int index, Item item, boolean send) {
        return setItem(index, item, send);
    }

    Item[] addItem(Item... slots);

    boolean canAddItem(Item item);

    Item[] removeItem(Item... slots);

    Map<Integer, Item> getContents();

    void setContents(Map<Integer, Item> items);

    void sendContents(Player player);

    void sendContents(Player... players);

    void sendContents(Collection<Player> players);

    void sendSlot(int index, Player player);

    void sendSlot(int index, Player... players);

    void sendSlot(int index, Collection<Player> players);

    boolean contains(Item item);

    Map<Integer, Item> all(Item item);

    default int first(Item item) {
        return first(item, false);
    }

    int first(Item item, boolean exact);

    int firstEmpty(Item item);

    void decreaseCount(int slot);

    void remove(Item item);

    default boolean clear(int index) {
        return clear(index, true);
    }

    boolean clear(int index, boolean send);

    void clearAll();

    boolean isFull();

    boolean isEmpty();

    Set<Player> getViewers();

    InventoryType getType();

    InventoryHolder getHolder();

    void onOpen(Player who);

    boolean open(Player who);

    void close(Player who);

    void onClose(Player who);

    void onSlotChange(int index, Item before, boolean send);

    @PowerNukkitOnly
    void addListener(InventoryListener listener);

    @PowerNukkitOnly
    void removeListener(InventoryListener listener);
}
