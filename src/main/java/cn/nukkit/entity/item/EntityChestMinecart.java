package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.MinecartChestInventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.ItemHelper;
import cn.nukkit.utils.MinecartType;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.data.ActorLinkType;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.jetbrains.annotations.NotNull;

import java.util.List;
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

    public EntityChestMinecart(IChunk chunk, NbtMap nbt) {
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
        if (this.namedTag.containsKey("Items") && this.namedTag.get("Items") instanceof List<?>) {
            List<NbtMap> inventoryList = this.namedTag.getList("Items", NbtType.COMPOUND);
            for (NbtMap item : inventoryList) {
                this.inventory.setItem(item.getByte("Slot"), ItemHelper.read(item));
            }
        }

        this.entityDataMap.put(ActorDataTypes.CONTAINER_TYPE, (byte) 10);
        entityDataMap.put(ActorDataTypes.CONTAINER_SIZE, this.inventory.getSize());
        entityDataMap.put(ActorDataTypes.CONTAINER_STRENGTH_MODIFIER, 0);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        final List<NbtMap> serializedItems = new ObjectArrayList<>();
        if (this.inventory != null) {
            for (int slot = 0; slot < 27; ++slot) {
                Item item = this.inventory.getItem(slot);
                if (item != null && !item.isNull()) {
                    serializedItems.add(ItemHelper.write(item, slot));
                }
            }
        }
        this.namedTag = this.namedTag.toBuilder()
                .putList("Items", NbtType.COMPOUND, serializedItems)
                .build();
    }
}
