package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.inventory.ChestBoatInventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import org.cloudburstmc.protocol.bedrock.packet.AddEntityPacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityLinkData;

import java.util.Set;

import org.jetbrains.annotations.NotNull;


public class EntityChestBoat extends EntityBoat implements InventoryHolder {

    @Override
    public @NotNull String getIdentifier() {
        return CHEST_BOAT;
    }

    protected ChestBoatInventory inventory;

    public EntityChestBoat(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public String getOriginalName() {
        return "Chest Boat";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("boat", "inanimate");
    }

    @Override
    public ChestBoatInventory getInventory() {
        return inventory;
    }

    @Override
    public boolean openInventory(Player player) {
        player.addWindow(getInventory());
        return true;
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (player.isSneaking()) {
            player.addWindow(this.inventory);
            return false;
        }

        if (!this.passengers.isEmpty() || getWaterLevel() < -SINKING_DEPTH) {
            return false;
        }

        super.mountEntity(player);
        return false;
    }

    @Override
    protected BedrockPacket createAddEntityPacket() {
        AddEntityPacket addEntity = new AddEntityPacket();
        addEntity.setEntityType(0);
        addEntity.setIdentifier("minecraft:chest_boat");
        addEntity.setUniqueEntityId(this.getId());
        addEntity.setRuntimeEntityId(this.getId());
        addEntity.setRotation(org.cloudburstmc.math.vector.Vector2f.from((float) this.yaw, (float) this.pitch));
        addEntity.setHeadRotation((float) this.yaw);
        addEntity.setBodyRotation((float) this.yaw);
        addEntity.setPosition(org.cloudburstmc.math.vector.Vector3f.from((float) this.x, (float) this.y + getBaseOffset(), (float) this.z));
        addEntity.setMotion(org.cloudburstmc.math.vector.Vector3f.from((float) this.motionX, (float) this.motionY, (float) this.motionZ));
        addEntity.setMetadata(toCloudburstMetadata(this.entityDataMap));

        for (int i = 0; i < this.passengers.size(); i++) {
            addEntity.getEntityLinks().add(new EntityLinkData(this.getId(), this.passengers.get(i).getId(), i == 0 ? EntityLinkData.Type.RIDER : EntityLinkData.Type.PASSENGER, false, false, 0f));
        }

        return addEntity;
    }

    @Override
    public String getInteractButtonText(Player player) {
        if (player.isSneaking()) {
            return "action.interact.opencontainer";
        }
        return "action.interact.ride.boat";
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

        this.entityDataMap.put(CONTAINER_TYPE, InventoryType.CHEST_BOAT.getNetworkType());
        entityDataMap.put(CONTAINER_SIZE, this.inventory.getSize());
        entityDataMap.put(CONTAINER_STRENGTH_MODIFIER, 0);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putList("Items", new ListTag<CompoundTag>());
        if (this.inventory != null) {
            for (int slot = 0; slot < 27; ++slot) {
                Item item = this.inventory.getItem(slot);
                if (item != null && !item.isNull()) {
                    this.namedTag.getList("Items", CompoundTag.class)
                            .add(NBTIO.putItemHelper(item, slot));
                }
            }
        }
    }

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
