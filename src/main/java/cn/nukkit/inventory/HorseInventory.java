package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.passive.EntityHorse;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Sound;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.MobArmorEquipmentPacket;
import cn.nukkit.network.protocol.UpdateEquipmentPacket;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;

import java.io.IOException;
import java.util.List;


public class HorseInventory extends BaseInventory {
    private static final CompoundTag slot0;
    private static final CompoundTag slot1;

    public HorseInventory(EntityHorse holder) {
        super(holder, InventoryType.HORSE, 2);
    }

    static {
        ListTag<CompoundTag> saddle = new ListTag<CompoundTag>().add(new CompoundTag().putCompound("slotItem", new CompoundTag()
                .putShort("Aux", Short.MAX_VALUE)
                .putString("Name", ItemID.SADDLE)));
        ListTag<CompoundTag> horseArmor = new ListTag<>();
        for (var h : List.of(ItemID.LEATHER_HORSE_ARMOR, ItemID.IRON_HORSE_ARMOR, ItemID.GOLDEN_HORSE_ARMOR, ItemID.DIAMOND_HORSE_ARMOR)) {
            horseArmor.add(new CompoundTag().putCompound("slotItem", new CompoundTag().putShort("Aux", Short.MAX_VALUE).putString("Name", h)));
        }
        slot0 = new CompoundTag().putList("acceptedItems", saddle).putInt("slotNumber", 0);
        slot1 = new CompoundTag().putList("acceptedItems", horseArmor).putInt("slotNumber", 1);
    }

    public void setSaddle(Item item) {
        this.setItem(0, item);
    }

    public void setHorseArmor(Item item) {
        this.setItem(1, item);
    }

    public Item getSaddle() {
        return this.getItem(0);
    }

    public Item getHorseArmor() {
        return this.getItem(1);
    }

    @Override
    public void init() {
        slotTypeMap.put(0, ContainerSlotType.HORSE_EQUIP);
        slotTypeMap.put(1, ContainerSlotType.HORSE_EQUIP);
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        super.onSlotChange(index, before, send);
        if (index == 0) {
            if (this.getSaddle().isNull()) {
                this.getHolder().setDataFlag(EntityFlag.SADDLED, false);
                this.getHolder().setDataFlag(EntityFlag.WASD_CONTROLLED, false);
                this.getHolder().setDataFlag(EntityFlag.CAN_POWER_JUMP, false);
            } else {
                this.getHolder().getLevel().addLevelSoundEvent(this.getHolder(), LevelSoundEventPacket.SOUND_SADDLE, -1, this.getHolder().getIdentifier(), false, false);
                this.getHolder().setDataFlag(EntityFlag.SADDLED);
                this.getHolder().setDataFlag(EntityFlag.WASD_CONTROLLED);
                this.getHolder().setDataFlag(EntityFlag.CAN_POWER_JUMP);
            }
        } else if (index == 1) {
            if (!this.getHorseArmor().isNull()) {
                this.getHolder().getLevel().addSound(this.getHolder(), Sound.MOB_HORSE_ARMOR);
            }
            MobArmorEquipmentPacket mobArmorEquipmentPacket = new MobArmorEquipmentPacket();
            mobArmorEquipmentPacket.eid = this.getHolder().getId();
            mobArmorEquipmentPacket.slots = new Item[]{Item.AIR, this.getHorseArmor(), Item.AIR, Item.AIR};
            Server.broadcastPacket(this.getViewers(), mobArmorEquipmentPacket);
        }
    }

    @Override
    public void onClose(Player who) {
        ContainerClosePacket pk = new ContainerClosePacket();
        pk.windowId = who.getWindowId(this);
        pk.wasServerInitiated = who.getClosingWindowId() != pk.windowId;
        pk.type = getType();
        who.dataPacket(pk);
        super.onClose(who);
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        who.dataPacket(createUpdateEquipmentPacket(who));
        sendContents(this.getViewers());
    }

    @Override
    public EntityHorse getHolder() {
        return (EntityHorse) holder;
    }

    protected UpdateEquipmentPacket createUpdateEquipmentPacket(Player who) {
        var slots = new ListTag<CompoundTag>();
        Item saddle = getSaddle();
        Item horseArmor = getHorseArmor();
        if (!saddle.isNull()) {
            slots.add(slot0.copy().putCompound("item", new CompoundTag().putString("Name", saddle.getId()).putShort("Aux", Short.MAX_VALUE)));
        } else slots.add(slot0.copy());
        if (!horseArmor.isNull()) {
            slots.add(slot1.copy().putCompound("item", new CompoundTag().putString("Name", horseArmor.getId()).putShort("Aux", Short.MAX_VALUE)));
        } else slots.add(slot1.copy());
        var nbt = new CompoundTag().putList("slots", slots);
        UpdateEquipmentPacket updateEquipmentPacket = new UpdateEquipmentPacket();
        updateEquipmentPacket.windowId = who.getWindowId(this);
        updateEquipmentPacket.windowType = this.getType().getNetworkType();
        updateEquipmentPacket.eid = getHolder().getId();
        try {
            updateEquipmentPacket.namedtag = NBTIO.writeNetwork(nbt);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return updateEquipmentPacket;
    }
}
