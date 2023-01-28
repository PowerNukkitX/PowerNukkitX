package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntityStructBlock;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.inventory.InventoryOpenEvent;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.ContainerOpenPacket;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@PowerNukkitXOnly
@Since("1.19.60-r1")
public class StructBlockInventory implements Inventory {
    protected final BlockEntityStructBlock holder;

    public StructBlockInventory(BlockEntityStructBlock holder) {
        this.holder = holder;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public int getMaxStackSize() {
        return 0;
    }

    @Override
    public void setMaxStackSize(int size) {

    }

    @Override
    public String getName() {
        return this.holder.getName();
    }

    @Override
    public String getTitle() {
        return this.getName();
    }

    @Override
    public Item getItem(int index) {
        return Item.get(Item.AIR);
    }

    @Override
    public boolean setItem(int index, Item item, boolean send) {
        return false;
    }

    @Override
    public Item[] addItem(Item... slots) {
        return Item.EMPTY_ARRAY;
    }

    @Override
    public boolean canAddItem(Item item) {
        return false;
    }

    @Override
    public Item[] removeItem(Item... slots) {
        return Item.EMPTY_ARRAY;
    }

    @Override
    public Map<Integer, Item> getContents() {
        return Collections.emptyMap();
    }

    @Override
    public void setContents(Map<Integer, Item> items) {

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
    public boolean contains(Item item) {
        return false;
    }

    @Override
    public Map<Integer, Item> all(Item item) {
        return Collections.emptyMap();
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
        return false;
    }

    @Override
    public void clearAll() {

    }

    @Override
    public boolean isFull() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public Set<Player> getViewers() {
        return Collections.emptySet();
    }

    @Override
    public InventoryType getType() {
        return null;
    }

    @Override
    public InventoryHolder getHolder() {
        return (InventoryHolder) this.holder;
    }

    @Override
    public void onOpen(Player who) {
        if (who.isOp() && who.isCreative()) {
            ContainerOpenPacket pk = new ContainerOpenPacket();
            pk.windowId = who.getWindowId(this);
            pk.type = 14;
            InventoryHolder holder = this.getHolder();
            if (holder instanceof Vector3) {
                pk.x = ((Vector3) holder).getFloorX();
                pk.y = ((Vector3) holder).getFloorY();
                pk.z = ((Vector3) holder).getFloorZ();
            } else {
                pk.x = pk.y = pk.z = 0;
            }
            if (holder instanceof Entity) {
                pk.entityId = ((Entity) holder).getId();
            }
            who.dataPacket(pk);
        }
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
        this.onClose(who);
    }

    @Override
    public void onClose(Player who) {
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
    }

    @Deprecated
    @Override
    public void addListener(InventoryListener listener) {
    }

    @Deprecated
    @Override
    public void removeListener(InventoryListener listener) {
    }
}
