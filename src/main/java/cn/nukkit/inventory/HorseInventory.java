package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.entity.passive.EntityHorse;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.SNBTParser;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.UpdateEquipmentPacket;

import java.io.IOException;
import java.util.List;

public class HorseInventory extends BaseInventory {
    private static final CompoundTag slot0;
    private static final CompoundTag slot1;

    static {
        ListTag<CompoundTag> saddle = new ListTag<CompoundTag>().add(new CompoundTag().putCompound(new CompoundTag("slotItem").putShort("Aux", 32767).putString("Name", "minecraft:saddle")));
        ListTag<CompoundTag> horseArmor = new ListTag<>();
        for (var h : List.of("minecraft:horsearmorleather", "minecraft:horsearmoriron", "minecraft:horsearmorgold", "minecraft:horsearmordiamond")) {
            horseArmor.add(new CompoundTag().putCompound(new CompoundTag("slotItem").putShort("Aux", 32767).putString("Name", h)));
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
    public void onSlotChange(int index, Item before, boolean send) {
        super.onSlotChange(index, before, send);
        if (index == 0) {
            this.getHolder().getLevel().addLevelSoundEvent(this.getHolder(), LevelSoundEventPacket.SOUND_SADDLE);
        }
    }

    @Override
    public void onClose(Player who) {
        super.onClose(who);
        ContainerClosePacket pk = new ContainerClosePacket();
        pk.windowId = who.getWindowId(this);
        pk.wasServerInitiated = who.getClosingWindowId() != pk.windowId;
        who.dataPacket(pk);
    }

    public HorseInventory(EntityHorse holder) {
        super(holder, InventoryType.HORSE);
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        who.dataPacket(createUpdateEquipmentPacket(who));
    }

    @Override
    public EntityHorse getHolder() {
        return (EntityHorse) holder;
    }

    protected UpdateEquipmentPacket createUpdateEquipmentPacket(Player who) {
        var slots = new ListTag<CompoundTag>();
        Item saddle = getSaddle();
        Item horseArmor = getHorseArmor();
        /*if (!saddle.isNull()) {
            slots.add(slot0.clone().putCompound(new CompoundTag("item").putString("Name", saddle.getNamespaceId()).putShort("Aux", 32767)));
        } else slots.add(slot0.clone());
        if (!horseArmor.isNull()) {
            slots.add(slot1.clone().putCompound(new CompoundTag("item").putString("Name", horseArmor.getNamespaceId()).putShort("Aux", 32767)));
        } else slots.add(slot1.clone());
        var nbt = new CompoundTag().putList("slots", slots);*/
        String s = """
                  {
                  "slots": [
                    {
                      "acceptedItems": [
                        {
                          "slotItem": {
                            "Aux": 32767s,
                            "Name": "minecraft:saddle"
                          }
                        }
                      ],
                      "item": {
                        "Aux": 32767s,
                        "Name": "minecraft:saddle"
                      },
                      "slotNumber": 0
                    },
                    {
                      "acceptedItems": [
                        {
                          "slotItem": {
                            "Aux": 32767s,
                            "Name": "minecraft:leather_horse_armor"
                          }
                        },
                        {
                          "slotItem": {
                            "Aux": 32767s,
                            "Name": "minecraft:iron_horse_armor"
                          }
                        },
                        {
                          "slotItem": {
                            "Aux": 32767s,
                            "Name": "minecraft:golden_horse_armor"
                          }
                        },
                        {
                          "slotItem": {
                            "Aux": 32767s,
                            "Name": "minecraft:diamond_horse_armor"
                          }
                        }
                      ],
                      "item": {
                        "Aux": 32767s,
                        "Name": "minecraft:iron_horse_armor"
                      },
                      "slotNumber": 1
                    }
                  ]
                }
                                  """;
        UpdateEquipmentPacket updateEquipmentPacket = new UpdateEquipmentPacket();
        updateEquipmentPacket.windowId = who.getWindowId(this);
        updateEquipmentPacket.windowType = this.getType().getNetworkType();
        updateEquipmentPacket.eid = getHolder().getId();
        CompoundTag parse = SNBTParser.parse(s);
        this.getHolder().namedTag.put("slots", parse.getList("slots"));
        try {
            updateEquipmentPacket.namedtag = NBTIO.writeNetwork(parse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return updateEquipmentPacket;
    }
}
