package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.EntityIntelligentHuman;
import cn.nukkit.event.entity.EntityArmorChangeEvent;
import cn.nukkit.event.entity.EntityInventoryChangeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemArmor;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.network.protocol.MobArmorEquipmentPacket;
import cn.nukkit.network.protocol.MobEquipmentPacket;

import java.util.Collection;

/**
 * 这个Inventory是一个hack实现，用来实现{@link EntityIntelligentHuman}的背包实现，它无法被open 和 close，因为虚拟人类不会自己打开物品栏<p>
 * 它的{@link FakeHumanInventory#viewers}永远为空,因为不允许打开它
 */
public class FakeHumanInventory extends BaseInventory {
    protected int itemInHandIndex = 0;

    public FakeHumanInventory(EntityIntelligentHuman player) {
        super(player, InventoryType.PLAYER_INVENTORY);
    }

    /**
     * 判断这个格子位置是否在物品栏(0-9)
     * <br>
     * Determine if this grid position is in the item column (0-9)
     *
     * @param slot 格子位置<br>grid position
     */
    public boolean isHotbarSlot(int slot) {
        return slot >= 0 && slot <= this.getHotbarSize();
    }

    /**
     * 获取{@link EntityIntelligentHuman}的手持物品的索引位置
     * <br>
     * Get the index location of {@link EntityIntelligentHuman}'s handheld items
     */
    public int getHeldItemIndex() {
        return this.itemInHandIndex;
    }

    /**
     * 设置{@link EntityIntelligentHuman}的手持物品的格子位置
     * <br>
     * Setting the grid position of {@link EntityIntelligentHuman}'s handheld items
     *
     * @param slot 索引位置<br>index position
     */
    public void setHeldItemSlot(int slot) {
        if (!isHotbarSlot(slot)) {
            return;
        }
        this.itemInHandIndex = slot;
        this.sendHeldItem(this.getViewers());
    }

    /**
     * 获取{@link EntityIntelligentHuman}的手持物品
     * <br>
     * Get the handheld item of {@link EntityIntelligentHuman}
     */
    public Item getItemInHand() {
        Item item = this.getItem(this.getHeldItemIndex());
        if (item != null) {
            return item;
        } else {
            return new ItemBlock(Block.get(BlockID.AIR), 0, 0);
        }
    }

    /**
     * 设置{@link EntityIntelligentHuman}的手持物品，这个方法会自动刷新客户端显示
     * <br>
     * Get the handheld item of {@link EntityIntelligentHuman}
     */
    public boolean setItemInHand(Item item) {
        return this.setItem(this.getHeldItemIndex(), item);
    }

    /**
     * 发送数据包给客户端，这个方法可以刷新被更改的手持物品显示
     * <br>
     * Sends a packet to the client, this method refreshes the display of altered handheld items
     */
    public void sendHeldItem(Player... players) {
        Item item = this.getItemInHand();

        MobEquipmentPacket pk = new MobEquipmentPacket();
        pk.item = item;
        pk.inventorySlot = pk.hotbarSlot = this.getHeldItemIndex();

        for (Player player : players) {
            pk.eid = this.getHolder().getId();
            player.dataPacket(pk);
        }
    }

    /**
     * 发送数据包给客户端，这个方法可以刷新被更改的手持物品显示
     * <br>
     * Sends a packet to the client, this method refreshes the display of altered handheld items
     */
    public void sendHeldItem(Collection<Player> players) {
        this.sendHeldItem(players.toArray(Player.EMPTY_ARRAY));
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        if (send) {
            if (index >= this.getSize()) {
                this.sendArmorSlot(index, this.getHolder().getViewers().values());
                if (this.getItem(index) instanceof ItemArmor) {
                    this.getHolder().level.getVibrationManager().callVibrationEvent(new VibrationEvent(getHolder(), this.getHolder().clone(), VibrationType.EQUIP));
                }
            } else if (isHotbarSlot(index)) {
                this.sendArmorSlot(index, this.getHolder().getViewers().values());
            }
        }
    }

    /**
     * 获取物品栏大小
     * <br>
     * Get item bar size
     */
    public int getHotbarSize() {
        return 9;
    }

    /**
     * 获取盔甲栏对应索引位置的物品
     * <br>
     * Get the item at the corresponding index position in the armor bar
     *
     * @param index 索引位置(0-4)<br>index position (0-4)
     */
    public Item getArmorItem(int index) {
        return this.getItem(this.getSize() + index);
    }

    /**
     * 设置盔甲栏对应索引位置的物品
     * <br>
     * Setting the item corresponding to the index position of the armor bar
     *
     * @param index 索引位置(0-4)<br>index position (0-4)
     * @param item  要设置的物品<br>item to be set
     */
    public boolean setArmorItem(int index, Item item) {
        return this.setArmorItem(index, item, false);
    }

    /**
     * 设置盔甲栏对应索引位置的物品
     * <br>
     * Setting the item corresponding to the index position of the armor bar
     *
     * @param index             索引位置(0-4)<br>index position (0-4)
     * @param item              要设置的物品<br>item to be set
     * @param ignoreArmorEvents 是否忽略盔甲更新事件<br>Whether to ignore armor update events
     */
    public boolean setArmorItem(int index, Item item, boolean ignoreArmorEvents) {
        return this.setItem(this.getSize() + index, item, ignoreArmorEvents);
    }

    /**
     * 获取盔甲栏中头盔位置对应的物品
     * <br>
     * Get the item corresponding to the helmet position in the armor bar
     */
    public Item getHelmet() {
        return this.getItem(this.getSize());
    }

    /**
     * 获取盔甲栏中胸甲位置对应的物品
     * <br>
     * Get the item corresponding to the breastplate position in the armor bar
     */
    public Item getChestplate() {
        return this.getItem(this.getSize() + 1);
    }

    /**
     * 获取盔甲栏中裤腿位置对应的物品
     * <br>
     * Get the item corresponding to the pants leg position in the armor bar
     */
    public Item getLeggings() {
        return this.getItem(this.getSize() + 2);
    }

    /**
     * 获取盔甲栏中鞋子位置对应的物品
     * <br>
     * Get the item corresponding to the shoe position in the armor bar
     */
    public Item getBoots() {
        return this.getItem(this.getSize() + 3);
    }

    /**
     * 设置盔甲栏中头盔位置对应的物品
     * <br>
     * Setting the item corresponding to the helmet position in the armor bar
     *
     * @param helmet the helmet
     * @return the helmet
     */
    public boolean setHelmet(Item helmet) {
        return this.setItem(this.getSize(), helmet);
    }

    /**
     * 设置盔甲栏中胸甲位置对应的物品
     * <br>
     * Setting the item corresponding to the breastplate position in the armor bar
     *
     * @param chestplate the chestplate
     * @return the chestplate
     */
    public boolean setChestplate(Item chestplate) {
        return this.setItem(this.getSize() + 1, chestplate);
    }

    /**
     * 设置盔甲栏中裤腿位置对应的物品
     * <br>
     * Setting the item corresponding to the pants leg position in the armor bar
     *
     * @param leggings the leggings
     * @return the leggings
     */
    public boolean setLeggings(Item leggings) {
        return this.setItem(this.getSize() + 2, leggings);
    }

    /**
     * 设置盔甲栏中鞋子位置对应的物品
     * <br>
     * Setting the item corresponding to the shoe position in the armor bar
     *
     * @param boots the boots
     * @return the boots
     */
    public boolean setBoots(Item boots) {
        return this.setItem(this.getSize() + 3, boots);
    }

    @Override
    public boolean setItem(int index, Item item) {
        return setItem(index, item, true, false);
    }

    @Override
    public boolean setItem(int index, Item item, boolean send) {
        return setItem(index, item, send, false);
    }

    private boolean setItem(int index, Item item, boolean send, boolean ignoreArmorEvents) {
        if (index < 0 || index >= this.size) {
            return false;
        } else if (item.isNull() || item.getCount() <= 0) {
            return this.clear(index);
        }
        //Armor change
        if (!ignoreArmorEvents && index >= this.getSize()) {
            EntityArmorChangeEvent ev = new EntityArmorChangeEvent(this.getHolder(), this.getItem(index), item, index);
            Server.getInstance().getPluginManager().callEvent(ev);
            if (ev.isCancelled() && this.getHolder() != null) {
                this.sendArmorSlot(index, this.getHolder().getViewers().values());
                return false;
            }
            item = ev.getNewItem();
        } else {
            EntityInventoryChangeEvent ev = new EntityInventoryChangeEvent(this.getHolder(), this.getItem(index), item, index);
            Server.getInstance().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                this.sendSlot(index, this.getViewers());
                return false;
            }
            item = ev.getNewItem();
        }
        Item old = this.getItem(index);
        this.slots[index] = item.clone();
        this.onSlotChange(index, old, send);
        return true;
    }

    @Override
    public boolean clear(int index, boolean send) {
        if (checkIndex(index)) return false;
        Item item = new ItemBlock(Block.get(BlockID.AIR), null, 0);
        Item old = this.slots[index];
        if (index >= this.getSize() && index < this.size) {
            EntityArmorChangeEvent ev = new EntityArmorChangeEvent(this.getHolder(), old, item, index);
            Server.getInstance().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                if (index >= this.size) {
                    this.sendArmorSlot(index, this.getViewers());
                } else {
                    this.sendSlot(index, this.getViewers());
                }
                return false;
            }
            item = ev.getNewItem();
        } else {
            EntityInventoryChangeEvent ev = new EntityInventoryChangeEvent(this.getHolder(), old, item, index);
            Server.getInstance().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                if (index >= this.size) {
                    this.sendArmorSlot(index, this.getViewers());
                } else {
                    this.sendSlot(index, this.getViewers());
                }
                return false;
            }
            item = ev.getNewItem();
        }

        if (!item.isNull()) {
            this.slots[index] = item.clone();
        } else {
            this.slots[index] = Item.AIR;
        }

        this.onSlotChange(index, old, send);
        return true;
    }

    /**
     * @return 获取盔甲栏0-4格的物品
     */
    public Item[] getArmorContents() {
        Item[] armor = new Item[4];
        for (int i = 0; i < 4; i++) {
            armor[i] = this.getItem(this.getSize() + i);
        }

        return armor;
    }

    @Override
    public void clearAll() {
        int limit = this.getSize() + 4;
        for (int index = 0; index < limit; ++index) {
            this.clear(index);
        }
        getHolder().getOffhandInventory().clearAll();
    }

    /**
     * 刷新指定玩家看到的该inventory对应实体盔甲栏显示
     *
     * @param player 指定玩家
     */
    public void sendArmorContents(Player player) {
        this.sendArmorContents(new Player[]{player});
    }

    /**
     * 刷新指定玩家看到的该inventory对应实体盔甲栏显示
     *
     * @param players 指定的玩家数组
     */
    public void sendArmorContents(Player[] players) {
        Item[] armor = this.getArmorContents();

        MobArmorEquipmentPacket pk = new MobArmorEquipmentPacket();
        pk.eid = this.getHolder().getId();
        pk.slots = armor;
        pk.tryEncode();

        for (Player player : players) {
            player.dataPacket(pk);
        }
    }

    /**
     * 设置全部盔甲栏的物品
     *
     * @param items 要设置的物品，分布对应盔甲栏的格子
     */
    public void setArmorContents(Item[] items) {
        if (items.length < 4) {
            Item[] newItems = new Item[4];
            System.arraycopy(items, 0, newItems, 0, items.length);
            items = newItems;
        }

        for (int i = 0; i < 4; ++i) {
            if (items[i] == null) {
                items[i] = new ItemBlock(Block.get(BlockID.AIR), null, 0);
            }

            if (items[i].isNull()) {
                this.clear(this.getSize() + i);
            } else {
                this.setItem(this.getSize() + i, items[i]);
            }
        }
    }

    /**
     * @see #sendArmorContents(Player[])
     */
    public void sendArmorContents(Collection<Player> players) {
        this.sendArmorContents(players.toArray(Player.EMPTY_ARRAY));
    }

    /**
     * 对指定玩家更新指定格子处的盔甲栏物品显示
     *
     * @param index  指定的格子
     * @param player 指定的玩家
     */
    public void sendArmorSlot(int index, Player player) {
        this.sendArmorSlot(index, new Player[]{player});
    }

    /**
     * 对指定玩家更新指定格子处的盔甲栏物品显示
     *
     * @param index   指定的格子
     * @param players 指定的玩家数组
     */
    public void sendArmorSlot(int index, Player[] players) {
        Item[] armor = this.getArmorContents();

        MobArmorEquipmentPacket pk = new MobArmorEquipmentPacket();
        pk.eid = this.getHolder().getId();
        pk.slots = armor;
        pk.tryEncode();

        for (Player player : players) {
            player.dataPacket(pk);
        }
    }

    /**
     * @see #sendArmorSlot(int, Player[])
     */
    public void sendArmorSlot(int index, Collection<Player> players) {
        this.sendArmorSlot(index, players.toArray(Player.EMPTY_ARRAY));
    }

    @Override
    public EntityIntelligentHuman getHolder() {
        return (EntityIntelligentHuman) super.getHolder();
    }

    //non
    @Override
    public void sendContents(Player player) {
    }

    @Override
    public void sendContents(Collection<Player> players) {
    }

    @Override
    public void sendContents(Player[] players) {
    }

    @Override
    public void sendSlot(int index, Player player) {
    }

    @Override
    public void sendSlot(int index, Collection<Player> players) {
    }

    @Override
    public void sendSlot(int index, Player... players) {
    }

    @Override
    public void close(Player who) {
    }

    @Override
    public void onOpen(Player who) {
    }

    @Override
    public void onClose(Player who) {
    }

    @Override
    public boolean open(Player who) {
        return false;
    }
}
