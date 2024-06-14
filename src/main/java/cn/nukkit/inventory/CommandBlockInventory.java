package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntityCommandBlock;
import cn.nukkit.blockentity.BlockEntityNameable;
import cn.nukkit.event.inventory.InventoryOpenEvent;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//implement the command block's ui
public class CommandBlockInventory implements Inventory, BlockEntityInventoryNameable {
    protected final BlockEntityCommandBlock holder;
    protected final Set<Player> viewers;
    private List<InventoryListener> listeners;

    public CommandBlockInventory(BlockEntityCommandBlock holder) {
        this.holder = holder;
        this.viewers = new HashSet<>();
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

    @NotNull
    public Item getItem(int index) {
        return Item.AIR;
    }

    @Override
    public boolean setItem(int index, Item item, boolean send) {
        return false;
    }

    @Override
    public Item[] addItem(Item... slots) {
        return new Item[0];
    }

    @Override
    public boolean canAddItem(Item item) {
        return false;
    }

    @Override
    public Item[] removeItem(Item... slots) {
        return new Item[0];
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
    public int getFreeSpace(Item item) {
        return 0;
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
        return InventoryType.COMMAND_BLOCK;
    }

    @Override
    public BlockEntityCommandBlock getHolder() {
        return this.holder;
    }

    @Override
    public void onOpen(Player who) {
        if (who.isOp() && who.isCreative()) {
            this.viewers.add(who);
            ContainerOpenPacket pk = new ContainerOpenPacket();
            pk.windowId = who.getWindowId(this);
            pk.type = getType().getNetworkType();
            InventoryHolder holder = this.getHolder();
            if (holder instanceof Vector3 vector3) {
                pk.x = vector3.getFloorX();
                pk.y = vector3.getFloorY();
                pk.z = vector3.getFloorZ();
            } else {
                pk.x = pk.y = pk.z = 0;
            }
            who.dataPacket(pk);
        }
    }

    @Override
    public boolean open(Player who) {
        if(who.getWindowId(this)!=-1){//todo hack, ContainerClosePacket no longer triggers for command block and struct block, finding the correct way to close them
            who.removeWindow(this);
        }

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
        ContainerClosePacket pk = new ContainerClosePacket();
        pk.windowId = who.getWindowId(this);
        pk.wasServerInitiated = who.getClosingWindowId() != pk.windowId;
        pk.type = getType();
        who.dataPacket(pk);
        this.viewers.remove(who);
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        if (this.listeners != null) {
            for (InventoryListener listener : listeners) {
                listener.onInventoryChanged(this, before, index);
            }
        }
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
    public BlockEntityNameable getBlockEntityInventoryHolder() {
        return getHolder();
    }
}
