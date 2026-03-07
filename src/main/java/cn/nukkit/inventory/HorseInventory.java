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
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.MobArmorEquipmentPacket;
import cn.nukkit.network.protocol.UpdateEquipmentPacket;
import cn.nukkit.network.protocol.types.LevelSoundEvent;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;
import org.jetbrains.annotations.Nullable;


public class HorseInventory<T extends EntityCreature & InventoryHolder> extends BaseInventory {
    protected final T equineHolder;
    private boolean revertingSlot = false;
    private final EquippableComponent equippable;

    public HorseInventory(T holder, int size) {
        super(holder, InventoryType.HORSE, Math.max(size, resolveMinSize(holder)));
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
                slotTypeMap.put(idx, ContainerSlotType.HORSE_EQUIP);
            }
        }
        // Everything else is a regular slot
        for (int i = 0; i < this.getSize(); i++) {
            if (!slotTypeMap.containsKey(i)) {
                slotTypeMap.put(i, ContainerSlotType.ANVIL_INPUT);
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
                getHolder().getLevel().addLevelSoundEvent(getHolder(), LevelSoundEvent.SADDLE, -1, getHolder().getIdentifier(), false, false);
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

            MobArmorEquipmentPacket mobArmorEquipmentPacket = new MobArmorEquipmentPacket();
            mobArmorEquipmentPacket.eid = getHolder().getId();
            mobArmorEquipmentPacket.slots = new Item[]{Item.AIR, armor, Item.AIR, Item.AIR};
            mobArmorEquipmentPacket.body = armor;

            Server.broadcastPacket(this.getViewers(), mobArmorEquipmentPacket);
            return;
        }

        if (type == EquippableComponent.Type.NAUTILUS_ARMOR) {
            Item armor = now.isNull() ? Item.AIR : now;

            // TODO: Do Nautilus play a sound on equip armor?

            MobArmorEquipmentPacket mobArmorEquipmentPacket = new MobArmorEquipmentPacket();
            mobArmorEquipmentPacket.eid = getHolder().getId();
            mobArmorEquipmentPacket.slots = new Item[]{Item.AIR, armor, Item.AIR, Item.AIR};
            mobArmorEquipmentPacket.body = armor;

            Server.broadcastPacket(this.getViewers(), mobArmorEquipmentPacket);
            return;
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
        for (int idx : buildEquippableSlotMap().keySet()) {
            sendSlot(idx, who);
        }
        sendEquippedVisuals(who);
    }

    protected UpdateEquipmentPacket createUpdateEquipmentPacket(Player who) {
        var slots = new ListTag<CompoundTag>();

        int storageIdx = 0;
        for (EquippableComponent.Slot def : getSortedEquippableSlots()) {
            if (def == null) continue;

            CompoundTag slotTag = new CompoundTag()
                    .putInt("slotNumber", def.slot())
                    .putList("acceptedItems", buildAcceptedItemsTag(def.acceptedItems()));

            Item equipped = (storageIdx >= 0 && storageIdx < this.getSize()) ? this.getItem(storageIdx) : Item.AIR;
            if (!equipped.isNull()) {
                slotTag.putCompound("item", new CompoundTag()
                        .putString("Name", equipped.getId())
                        .putShort("Aux", Short.MAX_VALUE));
            }

            slots.add(slotTag);
            storageIdx++;
        }

        var nbt = new CompoundTag().putList("slots", slots);

        UpdateEquipmentPacket updateEquipmentPacket = new UpdateEquipmentPacket();
        updateEquipmentPacket.windowId = who.getWindowId(this);
        updateEquipmentPacket.windowType = this.getType().getNetworkType();
        updateEquipmentPacket.eid = getHolder().getId();
        try {
            updateEquipmentPacket.namedtag = NBTIO.writeNetwork(nbt);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return updateEquipmentPacket;
    }

    private static ListTag<CompoundTag> buildAcceptedItemsTag(Set<String> acceptedItems) {
        ListTag<CompoundTag> list = new ListTag<>();

        if (acceptedItems == null || acceptedItems.isEmpty()) {
            return list;
        }

        for (String id : acceptedItems) {
            if (id == null) continue;
            String s = id.trim();
            if (s.isEmpty()) continue;

            list.add(new CompoundTag().putCompound("slotItem", new CompoundTag()
                    .putShort("Aux", Short.MAX_VALUE)
                    .putString("Name", s)));
        }

        return list;
    }

    public void sendEquippedVisualsTo(Collection<Player> players) {
        if (equippable == null) return;
        if (players == null || players.isEmpty()) return;

        Item armor = getEquippedItem(EquippableComponent.Type.HORSE_ARMOR);
        if (armor.isNull()) armor = getEquippedItem(EquippableComponent.Type.NAUTILUS_ARMOR);
        if (armor.isNull()) armor = Item.AIR;

        MobArmorEquipmentPacket pk = new MobArmorEquipmentPacket();
        pk.eid = getHolder().getId();
        pk.slots = new Item[]{Item.AIR, armor, Item.AIR, Item.AIR};
        pk.body = armor;

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

    public void load(ListTag<CompoundTag> inventoryTag) {
        if (inventoryTag == null) return;

        EquippableComponent eq = this.getEquippableDefinition();
        int equipCount = eq != null ? eq.getEquipCount() : 0;
        int base = getStorageBaseUiSlot();

        for (int i = 0; i < inventoryTag.size(); i++) {
            CompoundTag entry = inventoryTag.get(i);
            int slot = entry.contains("Slot") ? (entry.getByte("Slot") & 0xff) : i;

            Item it = NBTIO.getItemHelper(entry);
            if (it == null || it.isNull()) continue;

            // 1) Equippable by UI slotNumber
            if (eq != null) {
                EquippableComponent.Type type = eq.getTypeByUiSlot(slot);
                if (type != null) {
                    if (this.setEquippedItem(type, it)) continue;
                }
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

    public ListTag<CompoundTag> save(boolean includeStorage) {
        ListTag<CompoundTag> inventoryTag = new ListTag<>();

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
                inventoryTag.add(NBTIO.putItemHelper(it, def.slot()));
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
                inventoryTag.add(NBTIO.putItemHelper(it, uiSlot));
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
