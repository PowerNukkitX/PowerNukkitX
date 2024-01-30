package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.api.DoNotModify;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.InventorySlotPacket;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
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
     * 获取该库存指定索引处的物品
     *
     * @param index the index
     * @return the item
     */
    @NotNull
    Item getItem(int index);

    /**
     * 获取该库存指定索引处的未克隆的物品<p/>
     * 若调用方保证不会修改此方法返回的Item对象，则使用此方法将降低特定场景下Item::clone()造成的性能开销
     *
     * @param index the index
     * @return the item
     */
    @DoNotModify
    default Item getUnclonedItem(int index) {
        //你需要覆写它来实现
        return getItem(index);
    }

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

    int getFreeSpace(Item item);

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

    void onClose(Player who);

    void close(Player who);

    /**
     * 当执行{@link #setItem(int, Item)}时该方法会被调用，此时物品已经put进slots
     * <p>
     * This method is called when {@link #setItem(int, Item)} is executed, and the item has been put into slots
     *
     * @param index  物品变动的格子索引<br>The grid index of the item's changes
     * @param before 变动前的物品<br>Items before the change
     * @param send   是否发送{@link InventorySlotPacket}到客户端<br>Whether to send {@link InventorySlotPacket} to the client
     */
    void onSlotChange(int index, Item before, boolean send);


    void addListener(InventoryListener listener);


    void removeListener(InventoryListener listener);

    default void init() {
    }

    /**
     * native slot id <-> network slot id
     */
    default BiMap<Integer, Integer> networkSlotMap() {
        return HashBiMap.create();
    }

    /**
     * slot id -> ContainerSlotType
     */
    default Map<Integer, ContainerSlotType> slotTypeMap() {
        return new HashMap<>();
    }

    default int fromNetworkSlot(int networkSlot) {
        return networkSlotMap().inverse().getOrDefault(networkSlot, networkSlot);
    }

    default int toNetworkSlot(int nativeSlot) {
        return networkSlotMap().getOrDefault(nativeSlot, nativeSlot);
    }

    default ContainerSlotType getSlotType(int nativeSlot) {
        return slotTypeMap().get(nativeSlot);
    }
}
