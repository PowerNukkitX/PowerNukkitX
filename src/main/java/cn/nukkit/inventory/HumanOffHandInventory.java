package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.entity.IHuman;
import cn.nukkit.item.Item;
import org.cloudburstmc.protocol.bedrock.packet.InventoryContentPacket;
import org.cloudburstmc.protocol.bedrock.packet.MobEquipmentPacket;
import org.cloudburstmc.protocol.bedrock.data.inventory.FullContainerName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerSlotType;
import com.google.common.collect.BiMap;

import java.util.Map;

public class HumanOffHandInventory extends BaseInventory {
    public HumanOffHandInventory(IHuman holder) {
        super(holder, InventoryType.INVENTORY, 1);
    }

    @Override
    public void init() {
        BiMap<Integer, Integer> map = super.networkSlotMap();
        map.put(0, 1);

        Map<Integer, ContainerSlotType> map2 = super.slotTypeMap();
        map2.put(0, ContainerSlotType.OFFHAND);
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
        MobEquipmentPacket pk = this.createMobEquipmentPacket(item);

        for (Player player : players) {
            if (player == this.getHolder()) {
                InventoryContentPacket pk2 = new InventoryContentPacket();
                pk2.setContainerId(SpecialWindowId.OFFHAND.getId());
                pk2.setContents(java.util.List.of(toNetworkItem(item)));
                pk2.setContainerNameData(new FullContainerName(
                        ContainerSlotType.OFFHAND,
                        0
                ));
                player.dataPacket(pk2);
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
        MobEquipmentPacket pk = new MobEquipmentPacket();
        pk.setRuntimeEntityId(this.getHolder().getEntity().getId());
        pk.setItem(toNetworkItem(item));
        pk.setInventorySlot(1);
        pk.setHotbarSlot(1);
        pk.setContainerId(SpecialWindowId.OFFHAND.getId());
        return pk;
    }

    @Override
    public IHuman getHolder() {
        return (IHuman) super.getHolder();
    }
}
