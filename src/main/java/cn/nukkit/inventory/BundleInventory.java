package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBundle;
import cn.nukkit.math.AtomicIntIncrementSupplier;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.InventoryContentPacket;
import cn.nukkit.network.protocol.types.inventory.FullContainerName;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.InternalApi;

import java.util.Map;

@Slf4j
public class BundleInventory extends BaseInventory {

    private static final int MAX_FILL = 64;

    private static AtomicIntIncrementSupplier BUNDLE_ID_INCREMENT = new AtomicIntIncrementSupplier(0, 1);

    public BundleInventory(ItemBundle holder) {
        super(holder, InventoryType.NONE, 64);
        this.holder = holder;
        CompoundTag tag = holder.getNamedTag();
        tag.putInt("bundle_id", BUNDLE_ID_INCREMENT.getAsInt());
        if (!tag.containsList("storage_item_component_content")) {
            tag.putList("storage_item_component_content", new ListTag<CompoundTag>());
        }

        ListTag<CompoundTag> list = (ListTag<CompoundTag>) tag.getList("storage_item_component_content");
        for (CompoundTag compound : list.getAll()) {
            Item item = NBTIO.getItemHelper(compound);
            this.setItemInternal(compound.getByte("Slot"), item);
        }
    }

    @Override
    public boolean setItem(int index, Item item) {
        return super.setItem(index, item, true);
    }
    @Override
    public boolean setItem(int index, Item item, boolean send) {
        if(getWeight() + getWeight(item) > MAX_FILL) {
            log.warn("Tried to overfill bundle!");
            return false;
        }
        if(super.setItem(index, item, send)) {
            getHolder().saveNBT();
            return true;
        }
        return false;
    }

    @Override
    public boolean clear(int index) {
        return super.clear(index, true);
    }

    @Override
    public boolean clear(int index, boolean send) {
        if(super.clear(index, send)) {
            getHolder().saveNBT();
            return true;
        } else return false;
    }

    protected int getWeight(Item item) {
        if(item instanceof ItemBundle bundle) {
            return ((BundleInventory) bundle.getInventory()).getWeight() + 4;
        } else return (MAX_FILL/item.getMaxStackSize()) * item.getCount();
    }

    public int getWeight() {
        int weight = 0;
        for(Item item : getContents().values()) {
            weight += getWeight(item);
        }
        return weight;
    }

    @Override
    public void sendContents(Player... players) {
        InventoryContentPacket pk = new InventoryContentPacket();
        pk.slots = new Item[this.getSize()];
        pk.storageItem = getHolder();
        pk.fullContainerName = new FullContainerName(ContainerSlotType.DYNAMIC_CONTAINER, getHolder().getBundleId());
        for (int i = 0; i < this.getSize(); ++i) {
            pk.slots[i] = this.getUnclonedItem(i);
        }

        for (Player player : players) {
            int id = SpecialWindowId.CONTAINER_ID_REGISTRY.getId();
            if (id == -1 || !player.spawned) {
                this.close(player);
                continue;
            }
            pk.inventoryId = id;
            player.dataPacket(pk);
        }
    }

    @Override
    public Map<Integer, ContainerSlotType> slotTypeMap() {
        Map<Integer, ContainerSlotType> map = super.slotTypeMap();
        for(int i = 0; i < getSize(); i++) {
            map.put(i, ContainerSlotType.DYNAMIC_CONTAINER);
        }
        return map;
    }

    public ItemBundle getHolder() {
        return (ItemBundle) holder;
    }

    @InternalApi
    public void setHolder(ItemBundle bundle) {
        holder = bundle;
    }
}
