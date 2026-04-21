package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBundle;
import cn.nukkit.math.AtomicIntIncrementSupplier;
import cn.nukkit.utils.ItemHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.InternalApi;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerId;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;
import org.cloudburstmc.protocol.bedrock.data.inventory.FullContainerName;
import org.cloudburstmc.protocol.bedrock.packet.InventoryContentPacket;

import java.util.List;
import java.util.Map;

@Slf4j
public class BundleInventory extends BaseInventory {

    private static final int MAX_FILL = 64;
    private static final AtomicIntIncrementSupplier BUNDLE_ID_INCREMENT = new AtomicIntIncrementSupplier(0, 1);

    public BundleInventory(ItemBundle holder) {
        super(holder, ContainerType.NONE, 64);
        this.holder = holder;
        NbtMapBuilder tagBuilder = holder.getOrCreateNamedTag().toBuilder();
        tagBuilder.putInt("bundle_id", BUNDLE_ID_INCREMENT.getAsInt());
        if (!tagBuilder.containsKey("storage_item_component_content")) {
            tagBuilder.putList("storage_item_component_content", NbtType.COMPOUND, new ObjectArrayList<>());
        }

        final NbtMap tag = tagBuilder.build();

        holder.setNamedTag(tag);

        List<NbtMap> list = tag.getList("storage_item_component_content", NbtType.COMPOUND);
        for (NbtMap compound : list) {
            Item item = ItemHelper.read(compound);
            this.setItemInternal(compound.getByte("Slot"), item);
        }
    }

    @Override
    public boolean setItem(int index, Item item) {
        return super.setItem(index, item, true);
    }

    @Override
    public boolean setItem(int index, Item item, boolean send) {
        if (getWeight() + getWeight(item) > MAX_FILL) {
            log.warn("Tried to overfill bundle!");
            return false;
        }
        if (super.setItem(index, item, send)) {
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
        if (super.clear(index, send)) {
            getHolder().saveNBT();
            return true;
        } else return false;
    }

    protected int getWeight(Item item) {
        if (item instanceof ItemBundle bundle) {
            return ((BundleInventory) bundle.getInventory()).getWeight() + 4;
        } else return (MAX_FILL / item.getMaxStackSize()) * item.getCount();
    }

    public int getWeight() {
        int weight = 0;
        for (Item item : getContents().values()) {
            weight += getWeight(item);
        }
        return weight;
    }

    @Override
    public void sendContents(Player... players) {
        final InventoryContentPacket pk = new InventoryContentPacket();
        for (int i = 0; i < this.getSize(); i++) {
            pk.getSlots().add(this.getUnclonedItem(i).toNetwork());
        }
        pk.setStorageItem(this.getHolder().toNetwork());
        pk.setFullContainerName(
                new FullContainerName(ContainerEnumName.DYNAMIC_CONTAINER, this.getHolder().getBundleId())
        );

        for (Player player : players) {
            int id = ContainerId.CONTAINER_ID_REGISTRY;
            if (!player.spawned) {
                this.close(player);
                continue;
            }
            pk.setInventoryId(id);
            player.sendPacket(pk);
        }
    }

    @Override
    public Map<Integer, ContainerEnumName> slotTypeMap() {
        Map<Integer, ContainerEnumName> map = super.slotTypeMap();
        for (int i = 0; i < getSize(); i++) {
            map.put(i, ContainerEnumName.DYNAMIC_CONTAINER);
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
