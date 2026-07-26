package org.powernukkitx.entity.item;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.inventory.InventoryHolder;
import org.powernukkitx.inventory.MinecartChestInventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;
import org.powernukkitx.utils.ItemHelper;
import org.powernukkitx.utils.MinecartType;
import org.cloudburstmc.protocol.bedrock.data.ActorLinkType;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @author Snake1999
 * @since 2016/1/30
 */
public class EntityChestMinecart extends EntityMinecartAbstract implements InventoryHolder {

    @Override
    public @NotNull String getIdentifier() {
        return CHEST_MINECART;
    }

    protected MinecartChestInventory inventory;

    public EntityChestMinecart(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        setDisplayBlock(Block.get(Block.CHEST), false);
    }

    @Override
    public String getOriginalName() {
        return getType().getName();
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("minecart", "inanimate");
    }

    @Override
    public MinecartType getType() {
        return MinecartType.valueOf(1);
    }

    @Override
    public void dropItem() {
        for (Item item : this.inventory.getContents().values()) {
            this.level.dropItem(this, item);
        }

        if (this.lastDamageCause instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) {
            Entity damager = entityDamageByEntityEvent.getDamager();
            if (damager instanceof Player player && player.isCreative()) {
                return;
            }
        }
        this.level.dropItem(this, Item.get(Item.CHEST_MINECART));
    }

    @Override
    public void kill() {
        super.kill();
        this.inventory.clearAll();
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean mountEntity(Entity entity, ActorLinkType mode) {
        return false;
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        player.addWindow(this.inventory);
        return false; // If true, the count of items player has in hand decreases
    }

    @Override
    public MinecartChestInventory getInventory() {
        return inventory;
    }

    @Override
    public void initEntity() {
        super.initEntity();

        this.inventory = new MinecartChestInventory(this);
        final CompoundTag nbtMap = this.getNbt();
        if (nbtMap.containsList("Items")) {
            ListTag<CompoundTag> inventoryList = nbtMap.getList("Items", CompoundTag.class);
            for (CompoundTag item : inventoryList.getAll()) {
                this.inventory.setItem(item.getByte("Slot"), ItemHelper.read(item));
            }
        }

        this.actorDataMap.put(ActorDataTypes.CONTAINER_TYPE, (byte) 10);
        actorDataMap.put(ActorDataTypes.CONTAINER_SIZE, this.inventory.getSize());
        actorDataMap.put(ActorDataTypes.CONTAINER_STRENGTH_MODIFIER, 0);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        final ListTag<CompoundTag> serializedItems = new ListTag<>(Tag.TAG_Compound);
        if (this.inventory != null) {
            for (int slot = 0; slot < 27; ++slot) {
                Item item = this.inventory.getItem(slot);
                if (item != null && !item.isNull()) {
                    serializedItems.add(ItemHelper.write(item, slot));
                }
            }
        }
        this.nbt.putList("Items", serializedItems);
    }
}
