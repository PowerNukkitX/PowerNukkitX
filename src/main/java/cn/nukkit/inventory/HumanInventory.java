package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.IHuman;
import cn.nukkit.event.entity.EntityArmorChangeEvent;
import cn.nukkit.event.entity.EntityInventoryChangeEvent;
import cn.nukkit.event.player.PlayerItemHeldEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemArmor;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import cn.nukkit.network.protocol.InventoryContentPacket;
import cn.nukkit.network.protocol.InventorySlotPacket;
import cn.nukkit.network.protocol.MobArmorEquipmentPacket;
import cn.nukkit.network.protocol.MobEquipmentPacket;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.Range;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 0-8 物品栏<br>
 * 9-35 背包<br>
 * 36-39 盔甲栏<br>
 * 想获取副手库存请用{@link HumanOffHandInventory}<br>
 * <p>
 * 0-8 hotbar<br>
 * 9-35 inventory<br>
 * 36-39 Armor Inventory<br>
 * To obtain the off-hand inventory, please use {@link HumanOffHandInventory}<br>
 *
 * @author MagicDroidX (Nukkit Project)
 */
public class HumanInventory extends BaseInventory {
    protected int itemInHandIndex = 0;

    public static final int ARMORS_INDEX = 36;

    public HumanInventory(IHuman human) {
        super(human, InventoryType.INVENTORY, 40);
    }//9+27+4

    protected InventorySlice armorInventory;

    @Override
    public void init() {
        Map<Integer, ContainerSlotType> map = super.slotTypeMap();
        for (int i = 0; i < 9; i++) {
            map.put(i, ContainerSlotType.HOTBAR);
        }
        for (int i = 9; i < 36; i++) {
            map.put(i, ContainerSlotType.INVENTORY);
        }
        armorInventory = new InventorySlice(this, HumanInventory.ARMORS_INDEX, this.getSize()) {
            {
                HashMap<Integer, ContainerSlotType> map = new HashMap<>();
                BiMap<Integer, Integer> biMap = HashBiMap.create();
                for (int i = 0; i < 4; i++) {
                    map.put(i, ContainerSlotType.ARMOR);
                    biMap.put(i, i);
                }
                this.setNetworkMapping(map, biMap);
            }
        };
    }

    /**
     * Called when a client equips a hotbar inventorySlot. This method should not be used by plugins.
     * This method will call PlayerItemHeldEvent.
     *
     * @param slot hotbar slot Number of the hotbar slot to equip.
     * @return boolean if the equipment change was successful, false if not.
     */
    public boolean equipItem(int slot) {
        if (!isHotbarSlot(slot)) {
            this.sendContents((Player) this.getHolder());
            return false;
        }

        if (this.getHolder() instanceof Player player) {
            PlayerItemHeldEvent ev = new PlayerItemHeldEvent(player, this.getItem(slot), slot);
            this.getHolder().getLevel().getServer().getPluginManager().callEvent(ev);

            if (ev.isCancelled()) {
                this.sendContents(this.getViewers());
                return false;
            }

            if (player.fishing != null) {
                if (!(this.getItem(slot).equals(player.fishing.rod))) {
                    player.stopFishing(false);
                }
            }
        }

        this.setHeldItemIndex(slot, false);
        return true;
    }

    public boolean isHotbarSlot(int slot) {
        return slot >= 0 && slot < this.getHotbarSize();
    }

    public int getHeldItemIndex() {
        return this.itemInHandIndex;
    }

    public void setHeldItemIndex(int index) {
        setHeldItemIndex(index, true);
    }

    public void setHeldItemIndex(int index, boolean send) {
        if (index >= 0 && index < this.getHotbarSize()) {
            this.itemInHandIndex = index;

            if (this.getHolder() instanceof Player && send) {
                this.sendHeldItem((Player) this.getHolder());
            }

            this.sendHeldItem(this.getHolder().getEntity().getViewers().values());
        }
    }

    public Item getItemInHand() {
        return this.getItem(this.getHeldItemIndex());
    }

    public boolean setItemInHand(Item item) {
        return this.setItem(this.getHeldItemIndex(), item);
    }

    public boolean setItemInHand(Item item, boolean send) {
        return this.setItem(this.getHeldItemIndex(), item, send);
    }

    public void setHeldItemSlot(int slot) {
        if (!isHotbarSlot(slot)) {
            return;
        }

        this.itemInHandIndex = slot;

        if (this.getHolder() instanceof Player) {
            this.sendHeldItem((Player) this.getHolder());
        }

        this.sendHeldItem(this.getViewers());
    }

    public void sendHeldItem(Player... players) {
        Item item = this.getItemInHand();

        MobEquipmentPacket pk = new MobEquipmentPacket();
        pk.item = item;
        pk.inventorySlot = pk.hotbarSlot = this.getHeldItemIndex();

        for (Player player : players) {
            pk.eid = this.getHolder().getEntity().getId();
            if (player.equals(this.getHolder())) {
                pk.eid = player.getId();
                this.sendSlot(this.getHeldItemIndex(), player);
            }

            player.dataPacket(pk);
        }
    }

    public void sendHeldItem(Collection<Player> players) {
        this.sendHeldItem(players.toArray(Player.EMPTY_ARRAY));
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        IHuman holder = this.getHolder();
        if (holder instanceof Player && !((Player) holder).spawned) {
            return;
        }

        if (index >= ARMORS_INDEX) {
            this.sendArmorSlot(index, this.getViewers());
            this.sendArmorSlot(index, this.getHolder().getEntity().getViewers().values());
            if (this.getItem(index) instanceof ItemArmor) {
                this.getHolder().getEntity().level.getVibrationManager().callVibrationEvent(new VibrationEvent(getHolder(), this.getHolder().getEntity(), VibrationType.EQUIP));
            }
        } else {
            super.onSlotChange(index, before, send);
        }
    }

    public int getHotbarSize() {
        return 9;
    }

    public Item getArmorItem(int index) {
        return this.getItem(ARMORS_INDEX + index);
    }

    public boolean setArmorItem(@Range(from = 0, to = 3) int index, Item item) {
        return this.setArmorItem(index, item, false);
    }

    public boolean setArmorItem(@Range(from = 0, to = 3) int index, Item item, boolean ignoreArmorEvents) {
        return this.setItem(ARMORS_INDEX + index, item, ignoreArmorEvents);
    }

    public InventorySlice getArmorInventory() {
        return armorInventory;
    }

    public Item getHelmet() {
        return this.getItem(ARMORS_INDEX);
    }

    public Item getChestplate() {
        return this.getItem(ARMORS_INDEX + 1);
    }

    public Item getLeggings() {
        return this.getItem(ARMORS_INDEX + 2);
    }

    public Item getBoots() {
        return this.getItem(ARMORS_INDEX + 3);
    }

    public boolean setHelmet(Item helmet) {
        return this.setItem(ARMORS_INDEX, helmet);
    }

    public boolean setChestplate(Item chestplate) {
        return this.setItem(ARMORS_INDEX + 1, chestplate);
    }

    public boolean setLeggings(Item leggings) {
        return this.setItem(ARMORS_INDEX + 2, leggings);
    }

    public boolean setBoots(Item boots) {
        return this.setItem(ARMORS_INDEX + 3, boots);
    }

    @Override
    public boolean setItem(@Range(from = 0, to = 39) int index, Item item) {
        return setItem(index, item, true, false);
    }

    @Override
    public boolean setItem(@Range(from = 0, to = 39) int index, Item item, boolean send) {
        return setItem(index, item, send, false);
    }

    private boolean setItem(@Range(from = 0, to = 39) int index, Item item, boolean send, boolean ignoreArmorEvents) {
        if (index < 0 || index >= this.size) {
            return false;
        } else if (item.isNull()) {
            return this.clear(index);
        }

        //Armor change
        if (!ignoreArmorEvents && index >= ARMORS_INDEX) {
            EntityArmorChangeEvent ev = new EntityArmorChangeEvent(this.getHolder().getEntity(), this.getItem(index), item, index);
            Server.getInstance().getPluginManager().callEvent(ev);
            if (ev.isCancelled() && this.getHolder() != null) {
                this.sendArmorSlot(index, this.getViewers());
                return false;
            }
            item = ev.getNewItem();
        } else {
            EntityInventoryChangeEvent ev = new EntityInventoryChangeEvent(this.getHolder().getEntity(), this.getItem(index), item, index);
            Server.getInstance().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                this.sendSlot(index, this.getViewers());
                return false;
            }
            item = ev.getNewItem();
        }
        Item old = this.getItem(index);
        this.slots.put(index, item.clone());
        this.onSlotChange(index, old, send);
        return true;
    }

    @Override
    public boolean clear(int index, boolean send) {
        if (this.slots.containsKey(index)) {
            Item item = Item.AIR;
            Item old = this.slots.get(index);
            if (index >= ARMORS_INDEX && index < this.size) {
                EntityArmorChangeEvent ev = new EntityArmorChangeEvent(this.getHolder().getEntity(), old, item, index);
                Server.getInstance().getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    this.sendSlot(index, this.getViewers());
                    return false;
                }
                item = ev.getNewItem();
            } else if (index < ARMORS_INDEX) {
                EntityInventoryChangeEvent ev = new EntityInventoryChangeEvent(this.getHolder().getEntity(), old, item, index);
                Server.getInstance().getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    this.sendSlot(index, this.getViewers());
                    return false;
                }
                item = ev.getNewItem();
            } else {
                return false;
            }

            if (!item.isNull()) {
                this.slots.put(index, item.clone());
            } else {
                this.slots.remove(index);
            }

            this.onSlotChange(index, old, send);
        }

        return true;
    }

    public Item[] getArmorContents() {
        Item[] armor = new Item[4];
        for (int i = 0; i < 4; i++) {
            armor[i] = this.getItem(ARMORS_INDEX + i);
        }

        return armor;
    }

    @Override
    public void clearAll() {
        for (int index = 0; index < getSize(); ++index) {
            this.clear(index);
        }
        getHolder().getOffhandInventory().clearAll();
    }

    public void sendArmorContents(Player player) {
        this.sendArmorContents(new Player[]{player});
    }

    public void sendArmorContents(Player[] players) {
        Item[] armor = this.getArmorContents();

        MobArmorEquipmentPacket pk = new MobArmorEquipmentPacket();
        pk.eid = this.getHolder().getEntity().getId();
        pk.slots = armor;

        for (Player player : players) {
            if (player.equals(this.getHolder())) {
                InventoryContentPacket pk2 = new InventoryContentPacket();
                pk2.inventoryId = SpecialWindowId.ARMOR.getId();
                pk2.slots = armor;
                player.dataPacket(pk2);
            } else {
                player.dataPacket(pk);
            }
        }
    }

    public void setArmorContents(Item[] items) {
        if (items.length < 4) {
            Item[] newItems = new Item[4];
            System.arraycopy(items, 0, newItems, 0, items.length);
            items = newItems;
        }

        for (int i = 0; i < 4; ++i) {
            if (items[i] == null) {
                items[i] = Item.AIR;
            }

            if (items[i].isNull()) {
                this.clear(ARMORS_INDEX + i);
            } else {
                this.setItem(ARMORS_INDEX + i, items[i]);
            }
        }
    }

    public void sendArmorContents(Collection<Player> players) {
        this.sendArmorContents(players.toArray(Player.EMPTY_ARRAY));
    }

    public void sendArmorSlot(int index, Player player) {
        this.sendArmorSlot(index, new Player[]{player});
    }

    public void sendArmorSlot(int index, Player[] players) {
        Item[] armor = this.getArmorContents();

        MobArmorEquipmentPacket pk = new MobArmorEquipmentPacket();
        pk.eid = this.getHolder().getEntity().getId();
        pk.slots = armor;

        for (Player player : players) {
            if (player.equals(this.getHolder())) {
                InventorySlotPacket pk2 = new InventorySlotPacket();
                pk2.inventoryId = SpecialWindowId.ARMOR.getId();
                pk2.slot = index - ARMORS_INDEX;
                pk2.item = this.getItem(index);
                player.dataPacket(pk2);
            } else {
                player.dataPacket(pk);
            }
        }
    }

    public void sendArmorSlot(int index, Collection<Player> players) {
        this.sendArmorSlot(index, players.toArray(Player.EMPTY_ARRAY));
    }

    @Override
    public void sendContents(Player player) {
        this.sendContents(new Player[]{player});
    }

    @Override
    public void sendContents(Collection<Player> players) {
        this.sendContents(players.toArray(Player.EMPTY_ARRAY));
    }

    @Override
    public void sendContents(Player... players) {
        InventoryContentPacket pk = new InventoryContentPacket();
        int inventoryAndHotBarSize = this.getSize() - 4;
        pk.slots = new Item[inventoryAndHotBarSize];
        for (int i = 0; i < inventoryAndHotBarSize; ++i) {
            pk.slots[i] = this.getItem(i);
        }

        for (Player player : players) {
            int id = player.getWindowId(this);
            if (id == -1 || !player.spawned) {
                if (this.getHolder() != player) this.close(player);
                continue;
            }
            pk.inventoryId = id;
            player.dataPacket(pk);

        }
    }

    @Override
    public void sendSlot(int index, Player player) {
        this.sendSlot(index, new Player[]{player});
    }

    @Override
    public void sendSlot(int index, Collection<Player> players) {
        this.sendSlot(index, players.toArray(Player.EMPTY_ARRAY));
    }

    @Override
    public void sendSlot(int index, Player... players) {
        InventorySlotPacket pk = new InventorySlotPacket();
        pk.slot = index;
        pk.item = this.getItem(index).clone();

        for (Player player : players) {
            if (player.equals(this.getHolder())) {
                pk.inventoryId = SpecialWindowId.PLAYER.getId();
                player.dataPacket(pk);
            } else {
                int id = player.getWindowId(this);
                if (id == -1) {
                    this.close(player);
                    continue;
                }
                pk.inventoryId = id;
                player.dataPacket(pk);
            }
        }
    }

    @Override
    public IHuman getHolder() {
        return (IHuman) super.getHolder();
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        if (who.spawned) {
            ContainerOpenPacket pk = new ContainerOpenPacket();
            pk.windowId = who.getWindowId(this);
            pk.type = this.getType().getNetworkType();
            pk.x = who.getFloorX();
            pk.y = who.getFloorY();
            pk.z = who.getFloorZ();
            pk.entityId = who.getId();
            who.dataPacket(pk);
        }
    }

    @Override
    public void onClose(Player who) {
        ContainerClosePacket pk = new ContainerClosePacket();
        pk.windowId = who.getWindowId(this);
        pk.wasServerInitiated = who.getClosingWindowId() != pk.windowId;
        who.dataPacket(pk);
        // player can never stop viewing their own inventory
        if (who != holder) {
            super.onClose(who);
        }
    }
}
