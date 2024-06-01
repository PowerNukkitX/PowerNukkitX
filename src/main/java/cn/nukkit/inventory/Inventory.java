package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.api.DoNotModify;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.InventorySlotPacket;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.ApiStatus;
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
     * get the size of inventory
     */
    int getSize();

    /**
     * Get the maximum size allowed to be placed on a single slot, which cannot exceed the limit of the client.
     */
    int getMaxStackSize();

    /**
     * set the maximum size allowed to be placed on a single slot, which cannot exceed the limit of the client.
     */
    void setMaxStackSize(int size);

    /**
     * Get the item at the specified index of the inventory
     *
     * @param index the index from 0 ~ getSize()-1
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
    @ApiStatus.Internal
    @DoNotModify
    default Item getUnclonedItem(int index) {
        //你需要覆写它来实现
        return getItem(index);
    }

    /**
     * @see #setItem(int, Item, boolean)
     */
    default boolean setItem(int index, Item item) {
        return setItem(index, item, true);
    }

    /**
     * Sets the item at the specified index in this inventory
     *
     * @param index the index from 0 ~ getSize()-1
     * @param item  the item
     * @param send  Whether to send sync datapacket for client
     * @return the item
     */
    boolean setItem(int index, Item item, boolean send);

    default boolean setItemByPlayer(Player player, int index, Item item, boolean send) {
        return setItem(index, item, send);
    }

    /**
     * Add some items to the inventory
     *
     * @param slots the items
     * @return the remain items that can't add to the inventory
     */
    Item[] addItem(Item... slots);

    /**
     * @param item the item
     * @return Is there still a space in this inventory that allows this item to be added?
     */
    boolean canAddItem(Item item);

    /**
     * remove some items to the inventory
     */
    Item[] removeItem(Item... slots);

    /**
     * Get all items of the inventory
     * <p>
     * key = slot index (from 0 ~ getSize()-1)
     * <p>
     * value = item
     */
    Map<Integer, Item> getContents();

    /**
     * set all items of the inventory
     * <p>
     * key = slot index (from 0 ~ getSize()-1)
     * <p>
     * value = item
     */
    void setContents(Map<Integer, Item> items);

    /**
     * @see #sendContents(Collection)
     */
    void sendContents(Player player);

    /**
     * @see #sendContents(Collection)
     */
    void sendContents(Player... players);

    /**
     * Sync all changes of item for this inventory to the client
     *
     * @param players the target players to receive the packet
     */
    void sendContents(Collection<Player> players);

    /**
     * @see #sendSlot(int, Collection)
     */
    void sendSlot(int index, Player player);

    /**
     * @see #sendSlot(int, Collection)
     */
    void sendSlot(int index, Player... players);

    /**
     * Sync a change of item for this inventory to the client
     *
     * @param index   the slot index where this item is located
     * @param players the target players to receive the packet
     */
    void sendSlot(int index, Collection<Player> players);

    /**
     * Get the free space size of this item that can be place in this inventory
     */
    int getFreeSpace(Item item);

    /**
     * Whether this item exists in the inventory will detect AUX and NBT
     */
    boolean contains(Item item);


    /**
     * Gets a map of all items in this inventory that match this item
     */
    Map<Integer, Item> all(Item item);


    default int first(Item item) {
        return first(item, false);
    }

    /**
     * Get the first slot index that matching this item located in this inventory
     *
     * @param item  the item
     * @param exact Whether to exact match the item count
     * @return the index of item
     */
    int first(Item item, boolean exact);

    /**
     * The first free space in this inventory
     */
    int firstEmpty(Item item);

    /**
     * Decrease the item count in the slot index.
     */
    void decreaseCount(int slot);


    /**
     * Remove all items matching this item from this inventory
     *
     * @param item the item
     */
    void remove(Item item);

    /**
     * @see #clear(int, boolean)
     */
    default boolean clear(int index) {
        return clear(index, true);
    }

    /**
     * Remove all items in the index
     */
    boolean clear(int index, boolean send);

    /**
     * Remove all items in the inventory
     */
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

    @ApiStatus.Internal
    default void init() {
    }

    /**
     * native slot id <-> network slot id
     */
    @ApiStatus.Internal
    default BiMap<Integer, Integer> networkSlotMap() {
        return HashBiMap.create();
    }

    /**
     * slot id -> ContainerSlotType
     */
    @ApiStatus.Internal
    default Map<Integer, ContainerSlotType> slotTypeMap() {
        return new HashMap<>();
    }

    @ApiStatus.Internal
    default int fromNetworkSlot(int networkSlot) {
        return networkSlotMap().inverse().getOrDefault(networkSlot, networkSlot);
    }

    @ApiStatus.Internal
    default int toNetworkSlot(int nativeSlot) {
        return networkSlotMap().getOrDefault(nativeSlot, nativeSlot);
    }

    @ApiStatus.Internal
    default ContainerSlotType getSlotType(int nativeSlot) {
        return slotTypeMap().get(nativeSlot);
    }
}
