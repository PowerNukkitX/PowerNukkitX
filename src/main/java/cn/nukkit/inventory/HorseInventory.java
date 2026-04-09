package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.BlockChest;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.components.EquippableComponent;
import cn.nukkit.entity.components.InventoryComponent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Sound;
import cn.nukkit.utils.ItemHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.bedrock.packet.ContainerClosePacket;
import org.cloudburstmc.protocol.bedrock.packet.MobArmorEquipmentPacket;
import org.cloudburstmc.protocol.bedrock.packet.UpdateEquipPacket;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class HorseInventory<T extends EntityCreature & InventoryHolder> extends BaseInventory {
    protected final T equineHolder;
    private boolean revertingSlot = false;
    private final EquippableComponent equippable;

    public HorseInventory(T holder, int size) {
        super(holder, ContainerType.HORSE, Math.max(size, resolveMinSize(holder)));
        this.equineHolder = holder;
        this.equippable = resolveEquippable(holder);
    }

    private static int resolveMinSize(Object holder) {
        EquippableComponent eq = resolveEquippable(holder);
        if (eq == null || eq.slots() == null) return 0;

        int count = 0;
        for (EquippableComponent.Slot s : eq.slots()) {
            if (s != null) count++;
        }
        return Math.max(0, count);
    }

    private static EquippableComponent resolveEquippable(Object holder) {
        if (holder instanceof EntityCreature ec) {
            return ec.getComponentEquippable();
        }
        return null;
    }

    public T getHolder() {
        return this.equineHolder;
    }

    public @Nullable EquippableComponent getEquippableDefinition() {
        return this.equippable;
    }

    private Map<Integer, EquippableComponent.Slot> buildEquippableSlotMap() {
        Map<Integer, EquippableComponent.Slot> map = new HashMap<>();

        int storageIdx = 0;
        for (EquippableComponent.Slot def : getSortedEquippableSlots()) {
            if (storageIdx >= 0 && storageIdx < this.getSize()) {
                map.put(storageIdx, def);
            }
            storageIdx++;
        }

        return map;
    }

    private boolean isEquipSlot(int index, Map<Integer, EquippableComponent.Slot> slotsByIndex) {
        return slotsByIndex.containsKey(index);
    }

    private boolean canEquipIntoSlot(EquippableComponent.Slot def, Item item) {
        if (def == null) return false;
        if (item.isNull()) return true;
        return def.accepts(item.getId());
    }

    @Override
    public void init() {
        Map<Integer, EquippableComponent.Slot> slotsByIndex = buildEquippableSlotMap();
        // Mark equip slots as HORSE_EQUIP
        for (Map.Entry<Integer, EquippableComponent.Slot> e : slotsByIndex.entrySet()) {
            int idx = e.getKey();
            if (idx >= 0 && idx < this.getSize()) {
                slotTypeMap.put(idx, ContainerEnumName.HORSE_EQUIP_CONTAINER);
            }
        }
        // Everything else is a regular slot
        for (int i = 0; i < this.getSize(); i++) {
            if (!slotTypeMap.containsKey(i)) {
                slotTypeMap.put(i, ContainerEnumName.ANVIL_INPUT_CONTAINER);
            }
        }
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        if (revertingSlot) {
            super.onSlotChange(index, before, send);
            return;
        }

        Map<Integer, EquippableComponent.Slot> slotsByIndex = buildEquippableSlotMap();
        boolean equipSlot = isEquipSlot(index, slotsByIndex);

        // Validate equips
        if (equipSlot) {
            EquippableComponent.Slot def = slotsByIndex.get(index);
            Item now = this.getItem(index);

            if (!canEquipIntoSlot(def, now)) {
                revertingSlot = true;
                this.setItem(index, before, send);
                revertingSlot = false;
                return;
            }
        }

        super.onSlotChange(index, before, send);

        if (!equipSlot) return;

        EquippableComponent.Slot def = slotsByIndex.get(index);
        if (def == null) return;
        EquippableComponent.Type type = def.type();
        Item now = this.getItem(index);
        if (now.isNull()) now = Item.AIR;

        if (type == EquippableComponent.Type.SADDLE) {
            boolean hasSaddle = !now.isNull();

            if (!hasSaddle) {
                getHolder().setSaddle(false);
                getHolder().setInputControls(false);

                if (getHolder().hasJumpStrength()) {
                    getHolder().setCanPowerJump(false);
                } else if (getHolder().hasDashAction()) {
                    getHolder().setCanDash(false);
                }
                if (getHolder().hasHome()) getHolder().setHomePosition();
            } else {
                getHolder().getLevel().addLevelSoundEvent(getHolder(), SoundEvent.SADDLE, -1, getHolder().getIdentifier(), false, false);
                getHolder().setSaddle(true);
                getHolder().setInputControls(true);

                if (getHolder().hasJumpStrength()) {
                    getHolder().setCanPowerJump(true);
                } else if (getHolder().hasDashAction()) {
                    getHolder().setCanDash(true);
                }
                if (getHolder().hasHome()) getHolder().setHomePosition();
            }
            return;
        }

        if (type == EquippableComponent.Type.HORSE_ARMOR) {
            Item armor = now.isNull() ? Item.AIR : now;

            if (!armor.isNull()) getHolder().getLevel().addSound(getHolder(), Sound.MOB_HORSE_ARMOR);

            final MobArmorEquipmentPacket mobArmorEquipmentPacket = new MobArmorEquipmentPacket();
            mobArmorEquipmentPacket.setTargetRuntimeID(this.getHolder().getId());
            mobArmorEquipmentPacket.setHead(ItemData.AIR);
            mobArmorEquipmentPacket.setTorso(ItemData.AIR);
            mobArmorEquipmentPacket.setLegs(ItemData.AIR);
            mobArmorEquipmentPacket.setFeet(ItemData.AIR);
            mobArmorEquipmentPacket.setBody(armor.toNetwork());

            Server.broadcastPacket(this.getViewers(), mobArmorEquipmentPacket);
            return;
        }

        if (type == EquippableComponent.Type.NAUTILUS_ARMOR) {
            Item armor = now.isNull() ? Item.AIR : now;

            // TODO: Do Nautilus play a sound on equip armor?

            final MobArmorEquipmentPacket mobArmorEquipmentPacket = new MobArmorEquipmentPacket();
            mobArmorEquipmentPacket.setTargetRuntimeID(this.getHolder().getId());
            mobArmorEquipmentPacket.setHead(ItemData.AIR);
            mobArmorEquipmentPacket.setTorso(ItemData.AIR);
            mobArmorEquipmentPacket.setLegs(ItemData.AIR);
            mobArmorEquipmentPacket.setFeet(ItemData.AIR);
            mobArmorEquipmentPacket.setBody(armor.toNetwork());

            Server.broadcastPacket(this.getViewers(), mobArmorEquipmentPacket);
            return;
        }
    }

    @Override
    public void onClose(Player who) {
        final ContainerClosePacket pk = new ContainerClosePacket();
        pk.setContainerID((byte) who.getWindowId(this));
        pk.setServerInitiatedClose(who.getClosingWindowId() != pk.getContainerID());
        pk.setContainerType(this.getType());
        who.dataPacket(pk);
        super.onClose(who);
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);

        who.dataPacket(createUpdateEquipmentPacket(who));
        sendContents(this.getViewers());
        for (int idx : buildEquippableSlotMap().keySet()) {
            sendSlot(idx, who);
        }
        sendEquippedVisuals(who);
    }

    protected UpdateEquipPacket createUpdateEquipmentPacket(Player who) {
        final List<NbtMap> slots = new ObjectArrayList<>();

        int storageIdx = 0;
        for (EquippableComponent.Slot def : getSortedEquippableSlots()) {
            if (def == null) continue;

            NbtMapBuilder slotTag = NbtMap.builder()
                    .putInt("slotNumber", def.slot())
                    .putList("acceptedItems", NbtType.COMPOUND, buildAcceptedItemsTag(def.acceptedItems()));

            Item equipped = (storageIdx >= 0 && storageIdx < this.getSize()) ? this.getItem(storageIdx) : Item.AIR;
            if (!equipped.isNull()) {
                slotTag.putCompound("item", NbtMap.builder()
                        .putString("Name", equipped.getId())
                        .putShort("Aux", Short.MAX_VALUE)
                        .build()
                );
            }

            slots.add(slotTag.build());
            storageIdx++;
        }

        var nbt = NbtMap.builder().putList("slots", NbtType.COMPOUND, slots).build();

        final UpdateEquipPacket updateEquipPacket = new UpdateEquipPacket();
        updateEquipPacket.setContainerId((short) who.getWindowId(this));
        updateEquipPacket.setType((short) this.getType().getId());
        updateEquipPacket.setEntityUniqueId(this.getHolder().getId());
        updateEquipPacket.setTag(nbt);
        return updateEquipPacket;
    }

    private static List<NbtMap> buildAcceptedItemsTag(Set<String> acceptedItems) {
        final List<NbtMap> list = new ObjectArrayList<>();

        if (acceptedItems == null || acceptedItems.isEmpty()) {
            return list;
        }

        for (String id : acceptedItems) {
            if (id == null) continue;
            String s = id.trim();
            if (s.isEmpty()) continue;

            list.add(NbtMap.builder()
                    .putCompound("slotItem", NbtMap.builder()
                            .putShort("Aux", Short.MAX_VALUE)
                            .putString("Name", s)
                            .build()
                    ).build()
            );
        }

        return list;
    }

    public void sendEquippedVisualsTo(Collection<Player> players) {
        if (equippable == null) return;
        if (players == null || players.isEmpty()) return;

        Item armor = getEquippedItem(EquippableComponent.Type.HORSE_ARMOR);
        if (armor.isNull()) armor = getEquippedItem(EquippableComponent.Type.NAUTILUS_ARMOR);
        if (armor.isNull()) armor = Item.AIR;

        final MobArmorEquipmentPacket pk = new MobArmorEquipmentPacket();
        pk.setTargetRuntimeID(this.getHolder().getId());
        pk.setHead(ItemData.AIR);
        pk.setTorso(ItemData.AIR);
        pk.setLegs(ItemData.AIR);
        pk.setFeet(ItemData.AIR);
        pk.setBody(armor.toNetwork());

        Server.broadcastPacket(players, pk);
    }

    public void sendEquippedVisuals(Player player) {
        if (player == null) return;
        sendEquippedVisualsTo(List.of(player));
    }

    public Item getEquippedItem(EquippableComponent.Type type) {
        if (equippable == null) return Item.AIR;

        int storageIdx = 0;
        for (EquippableComponent.Slot def : getSortedEquippableSlots()) {
            if (def == null) continue;

            if (def.type() == type) {
                if (storageIdx < 0 || storageIdx >= this.getSize()) return Item.AIR;
                Item it = getItem(storageIdx);
                return it.isNull() ? Item.AIR : it;
            }

            storageIdx++;
        }

        return Item.AIR;
    }

    public boolean setEquippedItem(EquippableComponent.Type type, Item item) {
        if (equippable == null) return false;
        if (item == null) item = Item.AIR;

        int storageIdx = 0;
        for (EquippableComponent.Slot def : getSortedEquippableSlots()) {
            if (def.type() == type) {
                if (storageIdx < 0 || storageIdx >= this.getSize()) return false;
                if (!item.isNull() && !def.accepts(item.getId())) return false;

                Item before = this.getItem(storageIdx);

                revertingSlot = true;
                this.setItem(storageIdx, item, false);
                revertingSlot = false;

                this.onSlotChange(storageIdx, before, true);
                return true;
            }
            storageIdx++;
        }

        return false;
    }

    private List<EquippableComponent.Slot> getSortedEquippableSlots() {
        EquippableComponent eq = this.equippable;
        if (eq == null || eq.slots().isEmpty()) return Collections.emptyList();

        ArrayList<EquippableComponent.Slot> defs = new ArrayList<>(eq.slots().size());
        for (EquippableComponent.Slot s : eq.slots()) {
            if (s != null) defs.add(s);
        }
        defs.sort(Comparator.comparingInt(EquippableComponent.Slot::slot));
        return defs;
    }

    public void syncEquippedTo(HorseInventory<?> other) {
        if (other == null) return;

        EquippableComponent eq = this.getEquippableDefinition();
        if (eq == null) return;

        int equipCount = eq.getEquipCount();
        for (int i = 0; i < equipCount; i++) {
            Item it = this.getItem(i);
            other.setItem(i, it, false);
        }
    }

    public void load(List<NbtMap> inventoryTag) {
        if (inventoryTag == null) return;

        EquippableComponent eq = this.getEquippableDefinition();
        int equipCount = eq != null ? eq.getEquipCount() : 0;
        int base = getStorageBaseUiSlot();

        for (int i = 0; i < inventoryTag.size(); i++) {
            NbtMap entry = inventoryTag.get(i);
            int slot = entry.containsKey("Slot") ? (entry.getByte("Slot") & 0xff) : i;

            Item it = ItemHelper.read(entry);
            if (it == null || it.isNull()) continue;

            // 1) Equippable by UI slotNumber
            if (eq != null) {
                EquippableComponent.Type type = eq.getTypeByUiSlot(slot);
                if (type != null && this.setEquippedItem(type, it)) continue;
            }

            // 2) Storage by UI slotNumber
            if (slot >= base) {
                int storageOffset = slot - base;
                int idx = equipCount + storageOffset;
                if (idx >= 0 && idx < this.getSize()) {
                    this.setItem(idx, it, false);
                    continue;
                }
            }

            // 3) Legacy fallback: raw internal index
            if (slot >= 0 && slot < this.getSize()) {
                this.setItem(slot, it, false);
            }
        }
    }

    public List<NbtMap> save(boolean includeStorage) {
        final List<NbtMap> inventoryTag = new ObjectArrayList<>();

        EquippableComponent eq = this.getEquippableDefinition();
        int equipCount = eq != null ? eq.getEquipCount() : 0;

        // Save equips as UI slot numbers
        if (eq != null && eq.slots() != null) {
            List<EquippableComponent.Slot> defs = new ArrayList<>(eq.slots());
            defs.removeIf(Objects::isNull);
            defs.sort(Comparator.comparingInt(EquippableComponent.Slot::slot));

            int storageIdx = 0;
            for (EquippableComponent.Slot def : defs) {
                if (storageIdx >= this.getSize()) break;
                Item it = this.getItem(storageIdx);
                if (it == null) it = Item.AIR;
                inventoryTag.add(ItemHelper.write(it, def.slot()));
                storageIdx++;
            }
        }

        // Save storage as raw indices
        if (includeStorage) {
            int base = getStorageBaseUiSlot();
            int storageIdx = 0;

            for (int idx = equipCount; idx < this.getSize(); idx++) {
                Item it = this.getItem(idx);
                if (it == null || it.isNull()) continue;

                int uiSlot = base + storageIdx;
                inventoryTag.add(ItemHelper.write(it, uiSlot));
                storageIdx++;
            }
        }

        return inventoryTag;
    }

    private int getMaxUiSlotNumber() {
        EquippableComponent eq = this.equippable;
        if (eq == null || eq.slots() == null) return -1;
        int max = -1;
        for (var s : eq.slots()) {
            if (s == null) continue;
            max = Math.max(max, s.slot());
        }
        return max;
    }

    private int getStorageBaseUiSlot() {
        int max = getMaxUiSlotNumber();
        return max + 1;
    }

    public static Item[] getInventoryDrops(HorseInventory<?> inv, Entity holder) {
        InventoryComponent ic = holder.getComponentInventory();
        if (ic != null && ic.isPrivateInventory()) return Item.EMPTY_ARRAY;

        ArrayList<Item> out = new ArrayList<>();

        // Drop equipped items
        EquippableComponent eq = inv.getEquippableDefinition();
        if (eq != null && eq.slots() != null) {
            for (EquippableComponent.Slot s : eq.slots()) {
                if (s == null) continue;
                Item it = inv.getEquippedItem(s.type());
                if (!it.isNull()) out.add(it.clone());
            }
        }

        // Drop chest item + storage contents if the entity is chested
        boolean chested = holder.isChested();
        if (chested) {
            Item chestDrop = new ItemBlock(BlockChest.PROPERTIES.getDefaultState().toBlock(), 0);
            chestDrop.setCount(1);
            out.add(chestDrop);

            int equipCount = (eq != null) ? eq.getEquipCount() : 0;
            for (int idx = equipCount; idx < inv.getSize(); idx++) {
                Item it = inv.getItem(idx);
                if (!it.isNull()) out.add(it.clone());
            }
        }

        return out.isEmpty() ? Item.EMPTY_ARRAY : out.toArray(new Item[0]);
    }
}
