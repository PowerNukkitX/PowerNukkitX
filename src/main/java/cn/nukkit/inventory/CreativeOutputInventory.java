package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class CreativeOutputInventory implements Inventory {
    private Item item;
    private Player player;

    public CreativeOutputInventory(Player player) {
        this.player = player;
    }

    @Override
    public Map<Integer, ContainerSlotType> slotTypeMap() {
        Map<Integer, ContainerSlotType> map = Inventory.super.slotTypeMap();
        map.put(0, ContainerSlotType.CREATED_OUTPUT);
        return map;
    }

    @Override
    public BiMap<Integer, Integer> networkSlotMap() {
        HashBiMap<Integer, Integer> res = HashBiMap.create();
        res.put(0, 50);
        return res;
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public void setMaxStackSize(int size) {
    }

    @Override
    public @NotNull Item getItem(int index) {
        return item;
    }

    @Override
    public boolean setItem(int index, @NotNull Item item, boolean send) {
        this.item = item;
        return true;
    }

    public boolean setItem(@NotNull Item item) {
        return setItem(0, item, false);
    }

    @Override
    public Item[] addItem(@NotNull Item... slots) {
        Preconditions.checkNotNull(slots[0]);
        this.item = slots[0];
        return new Item[]{item};
    }

    @Override
    public boolean canAddItem(Item item) {
        return false;
    }

    @Override
    public Item[] removeItem(Item... slots) {
        item = Item.AIR;
        return Item.EMPTY_ARRAY;
    }

    @Override
    public Map<Integer, Item> getContents() {
        return Map.of(0, item);
    }

    @Override
    public void setContents(Map<Integer, Item> items) {
        item = items.get(0);
    }

    @Override
    public void sendContents(Player player) {

    }

    @Override
    public void sendContents(Player... players) {

    }

    @Override
    public void sendContents(Collection<Player> players) {

    }

    @Override
    public void sendSlot(int index, Player player) {

    }

    @Override
    public void sendSlot(int index, Player... players) {

    }

    @Override
    public void sendSlot(int index, Collection<Player> players) {

    }

    @Override
    public int getFreeSpace(Item item) {
        return 0;
    }

    @Override
    public boolean contains(Item item) {
        return false;
    }

    @Override
    public Map<Integer, Item> all(Item item) {
        return null;
    }

    @Override
    public int first(Item item, boolean exact) {
        return 0;
    }

    @Override
    public int firstEmpty(Item item) {
        return 0;
    }

    @Override
    public void decreaseCount(int slot) {

    }

    @Override
    public void remove(Item item) {

    }

    @Override
    public boolean clear(int index, boolean send) {
        item = Item.AIR;
        return true;
    }

    @Override
    public void clearAll() {
        item = Item.AIR;
    }

    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Set<Player> getViewers() {
        return null;
    }

    @Override
    public InventoryType getType() {
        return InventoryType.NONE;
    }

    @Override
    public Player getHolder() {
        return player;
    }

    @Override
    public void onOpen(Player who) {

    }

    @Override
    public boolean open(Player who) {
        return false;
    }

    @Override
    public void onClose(Player who) {

    }

    @Override
    public void close(Player who) {

    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {

    }

    @Override
    public void addListener(InventoryListener listener) {

    }

    @Override
    public void removeListener(InventoryListener listener) {

    }
}
