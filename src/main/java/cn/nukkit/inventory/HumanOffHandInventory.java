package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.entity.IHuman;
import cn.nukkit.item.Item;
import com.google.common.collect.BiMap;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;
import org.cloudburstmc.protocol.bedrock.data.inventory.FullContainerName;
import org.cloudburstmc.protocol.bedrock.packet.InventoryContentPacket;
import org.cloudburstmc.protocol.bedrock.packet.MobEquipmentPacket;

import java.util.Map;

public class HumanOffHandInventory extends BaseInventory {
    public HumanOffHandInventory(IHuman holder) {
        super(holder, ContainerType.INVENTORY, 1);
    }

    @Override
    public void init() {
        BiMap<Integer, Integer> map = super.networkSlotMap();
        map.put(0, 1);

        Map<Integer, ContainerEnumName> map2 = super.slotTypeMap();
        map2.put(0, ContainerEnumName.OFFHAND_CONTAINER);
    }

    public void setItem(Item item) {
        setItem(0, item);
    }

    public void setItem(Item item, boolean send) {
        setItem(0, item, send);
    }

    @Override
    public boolean setItem(int index, Item item) {
        return super.setItem(0, item);
    }

    @Override
    public boolean setItem(int index, Item item, boolean send) {
        return super.setItem(0, item, send);
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        IHuman holder = this.getHolder();
        if (holder instanceof Player player) {
            if (!player.spawned) return;
            if (send) {
                this.sendContents(this.getViewers());
                this.sendContents(holder.getEntity().getViewers().values());
            }
        }
    }

    @Override
    public void sendContents(Player... players) {
        Item item = this.getItem(0);
        final MobEquipmentPacket pk = this.createMobEquipmentPacket(item);

        for (Player player : players) {
            if (player == this.getHolder()) {
                final InventoryContentPacket inventoryContentPacket = new InventoryContentPacket();
                inventoryContentPacket.setInventoryId(SpecialWindowId.OFFHAND.getId());
                inventoryContentPacket.getSlots().add(item.toNetwork());
                inventoryContentPacket.setFullContainerName(
                        new FullContainerName(
                                ContainerEnumName.OFFHAND_CONTAINER,
                                null
                        )
                );
                player.dataPacket(inventoryContentPacket);
                player.dataPacket(pk);
            } else {
                player.dataPacket(pk);
            }
        }
    }

    @Override
    public void sendSlot(int index, Player... players) {
        sendContents(players);
    }

    private MobEquipmentPacket createMobEquipmentPacket(Item item) {
        final int slot = 1;
        final MobEquipmentPacket packet = new MobEquipmentPacket();
        packet.setTargetRuntimeID(this.getHolder().getEntity().getId());
        packet.setItem(item.toNetwork());
        packet.setSlot(slot);
        packet.setSelectedSlot(slot);
        packet.setContainerId(SpecialWindowId.OFFHAND.getId());
        return packet;
    }

    @Override
    public IHuman getHolder() {
        return (IHuman) super.getHolder();
    }
}
