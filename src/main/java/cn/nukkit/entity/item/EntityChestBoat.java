package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.inventory.ChestBoatInventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.types.EntityLink;

import static cn.nukkit.network.protocol.SetEntityLinkPacket.TYPE_PASSENGER;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class EntityChestBoat extends EntityBoat implements InventoryHolder {

    public static final int NETWORK_ID = 218;

    protected ChestBoatInventory inventory;

    public EntityChestBoat(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public String getOriginalName() {
        return "Chest Boat";
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public ChestBoatInventory getInventory() {
        return inventory;
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (player.isSneaking()) {
            player.addWindow(this.inventory);
            return false;
        }

        if (this.passengers.size() >= 1 || getWaterLevel() < -SINKING_DEPTH) {
            return false;
        }

        super.mountEntity(player);
        return false;
    }

    @Override
    protected DataPacket createAddEntityPacket() {
        AddEntityPacket addEntity = new AddEntityPacket();
        addEntity.type = 0;
        addEntity.id = "minecraft:chest_boat";
        addEntity.entityUniqueId = this.getId();
        addEntity.entityRuntimeId = this.getId();
        addEntity.yaw = (float) this.yaw;
        addEntity.headYaw = (float) this.yaw;
        addEntity.pitch = (float) this.pitch;
        addEntity.x = (float) this.x;
        addEntity.y = (float) this.y + getBaseOffset();
        addEntity.z = (float) this.z;
        addEntity.speedX = (float) this.motionX;
        addEntity.speedY = (float) this.motionY;
        addEntity.speedZ = (float) this.motionZ;
        addEntity.metadata = this.dataProperties;

        addEntity.links = new EntityLink[this.passengers.size()];
        for (int i = 0; i < addEntity.links.length; i++) {
            addEntity.links[i] = new EntityLink(this.getId(), this.passengers.get(i).getId(), i == 0 ? EntityLink.TYPE_RIDER : TYPE_PASSENGER, false, false);
        }

        return addEntity;
    }

    @Override
    public void initEntity() {
        super.initEntity();

        this.inventory = new ChestBoatInventory(this);
        if (this.namedTag.contains("Items") && this.namedTag.get("Items") instanceof ListTag) {
            ListTag<CompoundTag> inventoryList = this.namedTag.getList("Items", CompoundTag.class);
            for (CompoundTag item : inventoryList.getAll()) {
                this.inventory.setItem(item.getByte("Slot"), NBTIO.getItemHelper(item));
            }
        }

        this.dataProperties
                .putByte(DATA_CONTAINER_TYPE, InventoryType.CHEST_BOAT.getNetworkType())
                .putInt(DATA_CONTAINER_BASE_SIZE, this.inventory.getSize())
                .putInt(DATA_CONTAINER_EXTRA_SLOTS_PER_STRENGTH, 0);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putList(new ListTag<CompoundTag>("Items"));
        if (this.inventory != null) {
            for (int slot = 0; slot < 27; ++slot) {
                Item item = this.inventory.getItem(slot);
                if (item != null && item.getId() != Item.AIR) {
                    this.namedTag.getList("Items", CompoundTag.class)
                            .add(NBTIO.putItemHelper(item, slot));
                }
            }
        }
    }

    @Since("1.6.0.0-PNX")
    @Override
    protected void dropItem() {
        switch (this.getVariant()) {
            case 0 -> this.level.dropItem(this, Item.get(ItemID.OAK_CHEST_BOAT));
            case 1 -> this.level.dropItem(this, Item.get(ItemID.SPRUCE_CHEST_BOAT));
            case 2 -> this.level.dropItem(this, Item.get(ItemID.BIRCH_CHEST_BOAT));
            case 3 -> this.level.dropItem(this, Item.get(ItemID.JUNGLE_CHEST_BOAT));
            case 4 -> this.level.dropItem(this, Item.get(ItemID.ACACIA_CHEST_BOAT));
            case 5 -> this.level.dropItem(this, Item.get(ItemID.DARK_OAK_CHEST_BOAT));
            case 6 -> this.level.dropItem(this, Item.get(ItemID.MANGROVE_CHEST_BOAT));
            default -> this.level.dropItem(this, Item.get(ItemID.CHEST_BOAT));
        }

        for (Item item : this.inventory.getContents().values()) {
            this.level.dropItem(this, item);
        }
        this.inventory.clearAll();
    }
}
