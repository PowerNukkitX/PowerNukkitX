package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import org.cloudburstmc.protocol.bedrock.packet.InventoryContentPacket;
import org.cloudburstmc.protocol.bedrock.packet.InventorySlotPacket;
import org.cloudburstmc.protocol.bedrock.packet.MobArmorEquipmentPacket;
import org.cloudburstmc.protocol.bedrock.data.inventory.FullContainerName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerSlotType;

import java.util.HashSet;
import java.util.Set;


public class EntityArmorInventory extends BaseInventory {
    private final Entity entity;
    public static final int SLOT_HEAD = 0;
    public static final int SLOT_CHEST = 1;
    public static final int SLOT_LEGS = 2;
    public static final int SLOT_FEET = 3;
    public static final int SLOT_BODY = 4; // This is not part of the armor inventory, but its used for happy ghasts

    /**
     * @param holder an Entity which implements {@link InventoryHolder}.
     * @throws ClassCastException if the entity does not implements {@link InventoryHolder}
     */
    public EntityArmorInventory(InventoryHolder holder) {
        super(holder, InventoryType.INVENTORY, 5);
        this.entity = (Entity) holder;
    }


    public Entity getEntity() {
        return entity;
    }

    @Override
    public int getSize() {
        return 5;
    }


    public Item getHelmet() {
        return this.getItem(SLOT_HEAD);
    }


    public Item getChestplate() {
        return this.getItem(SLOT_CHEST);
    }


    public Item getLeggings() {
        return this.getItem(SLOT_LEGS);
    }


    public Item getBoots() {
        return this.getItem(SLOT_FEET);
    }

    public Item getBody() {
        return this.getItem(SLOT_BODY);
    }


    public boolean setHelmet(Item item) {
        return this.setItem(SLOT_HEAD, item);
    }


    public boolean setChestplate(Item item) {
        return this.setItem(SLOT_CHEST, item);
    }


    public boolean setLeggings(Item item) {
        return this.setItem(SLOT_LEGS, item);
    }


    public boolean setBoots(Item item) {
        return this.setItem(SLOT_FEET, item);
    }

    public boolean setBody(Item item) {
        return this.setItem(SLOT_BODY, item);
    }

    @Override
    public void sendSlot(int index, Player... players) {
        for (Player player : players) {
            this.sendSlot(index, player);
        }
    }

    @Override
    public void sendSlot(int index, Player player) {
        if (player == this.holder) {
            InventorySlotPacket inventorySlotPacket = new InventorySlotPacket();
            int id = player.getWindowId(this);
            inventorySlotPacket.setContainerId(id);
            inventorySlotPacket.setSlot(index);
            inventorySlotPacket.setItem(toNetworkItem(this.getItem(index)));
            inventorySlotPacket.setContainerNameData(new FullContainerName(
                    this.getSlotType(index),
                    id
            ));
            player.dataPacket(inventorySlotPacket);
        } else {
            MobArmorEquipmentPacket mobArmorEquipmentPacket = new MobArmorEquipmentPacket();
            mobArmorEquipmentPacket.setRuntimeEntityId(this.entity.getId());
            mobArmorEquipmentPacket.setHelmet(toNetworkItem(this.getHelmet()));
            mobArmorEquipmentPacket.setChestplate(toNetworkItem(this.getChestplate()));
            mobArmorEquipmentPacket.setLeggings(toNetworkItem(this.getLeggings()));
            mobArmorEquipmentPacket.setBoots(toNetworkItem(this.getBoots()));
            mobArmorEquipmentPacket.setBody(toNetworkItem(this.getBody()));
            player.dataPacket(mobArmorEquipmentPacket);
        }
    }

    @Override
    public void sendContents(Player... players) {
        for (Player player : players) {
            this.sendContents(player);
        }
    }

    @Override
    public void sendContents(Player player) {
        if (player == this.holder) {
            InventoryContentPacket inventoryContentPacket = new InventoryContentPacket();
            int id = player.getWindowId(this);
            inventoryContentPacket.setContainerId(id);
            inventoryContentPacket.setContents(java.util.List.of(
                    toNetworkItem(this.getHelmet()),
                    toNetworkItem(this.getChestplate()),
                    toNetworkItem(this.getLeggings()),
                    toNetworkItem(this.getBoots())
            ));
            inventoryContentPacket.setContainerNameData(new FullContainerName(
                    ContainerSlotType.ARMOR,
                    id
            ));
            player.dataPacket(inventoryContentPacket);
        } else {
            MobArmorEquipmentPacket mobArmorEquipmentPacket = new MobArmorEquipmentPacket();
            mobArmorEquipmentPacket.setRuntimeEntityId(this.entity.getId());
            mobArmorEquipmentPacket.setHelmet(toNetworkItem(this.getHelmet()));
            mobArmorEquipmentPacket.setChestplate(toNetworkItem(this.getChestplate()));
            mobArmorEquipmentPacket.setLeggings(toNetworkItem(this.getLeggings()));
            mobArmorEquipmentPacket.setBoots(toNetworkItem(this.getBoots()));
            mobArmorEquipmentPacket.setBody(toNetworkItem(this.getBody()));
            player.dataPacket(mobArmorEquipmentPacket);
        }
    }

    @Override
    public void onOpen(Player who) {
        this.viewers.add(who);
    }

    @Override
    public void onClose(Player who) {
        this.viewers.remove(who);
    }

    @Override
    public Set<Player> getViewers() {
        Set<Player> viewers = new HashSet<>(this.viewers);
        viewers.addAll(entity.getViewers().values());
        return viewers;
    }
}
