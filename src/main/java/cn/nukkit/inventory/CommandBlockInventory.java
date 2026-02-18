package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntityCommandBlock;
import cn.nukkit.blockentity.BlockEntityNameable;
import cn.nukkit.event.inventory.InventoryOpenEvent;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;
import org.cloudburstmc.protocol.bedrock.packet.ContainerClosePacket;
import org.cloudburstmc.protocol.bedrock.packet.ContainerOpenPacket;
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
            pk.setId((byte) who.getWindowId(this));
            pk.setType(containerTypeOf(getType()));
            InventoryHolder holder = this.getHolder();
            if (holder instanceof Vector3 vector3) {
                pk.setBlockPosition(Vector3i.from(vector3.getFloorX(), vector3.getFloorY(), vector3.getFloorZ()));
            } else {
                pk.setBlockPosition(Vector3i.ZERO);
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
        byte id = (byte) who.getWindowId(this);
        pk.setId(id);
        pk.setServerInitiated(who.getClosingWindowId() != id);
        pk.setType(containerTypeOf(getType()));
        who.dataPacket(pk);
        this.viewers.remove(who);
    }

    private static ContainerType containerTypeOf(InventoryType inventoryType) {
        int networkType = inventoryType.getNetworkType();
        return networkType >= 0 ? ContainerType.from(networkType) : ContainerType.NONE;
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
