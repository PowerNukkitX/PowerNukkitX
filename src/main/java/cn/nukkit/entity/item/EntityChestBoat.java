package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.inventory.ChestBoatInventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.ItemHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.data.ActorLinkType;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorLink;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;
import org.cloudburstmc.protocol.bedrock.packet.AddActorPacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;


public class EntityChestBoat extends EntityBoat implements InventoryHolder {

    @Override
    public @NotNull String getIdentifier() {
        return CHEST_BOAT;
    }

    protected ChestBoatInventory inventory;

    public EntityChestBoat(IChunk chunk, NbtMap nbt) {
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

        super.mountEntity(player, true);
        return false;
    }

    @Override
    protected BedrockPacket createAddEntityPacket() {
        final AddActorPacket packet = new AddActorPacket();
        packet.setTargetActorID(this.getId());
        packet.setTargetRuntimeID(this.getId());
        packet.setActorType("minecraft:chest_boat");
        packet.setPosition(Vector3f.from(this.x, this.y + this.getBaseOffset(), this.z));
        packet.setVelocity(Vector3f.from(this.motionX, this.motionY, this.motionZ));
        packet.setRotation(Vector2f.from(this.pitch, this.yaw));
        packet.setActorData(this.entityDataMap);

        for (int i = 0; i < this.passengers.size(); i++) {
            packet.getActorLinks().add(
                    new ActorLink(
                            this.getId(),
                            this.passengers.get(i).getId(),
                            i == 0 ? ActorLinkType.RIDING : ActorLinkType.PASSENGER,
                            false,
                            false,
                            0f
                    )
            );
        }
        return packet;
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

        if (this.namedTag.containsKey("Items") && this.namedTag.get("Items") instanceof List<?>) {
            List<NbtMap> inventoryList = this.namedTag.getList("Items", NbtType.COMPOUND);
            for (NbtMap item : inventoryList) {
                this.inventory.setItem(item.getByte("Slot"), ItemHelper.read(item));
            }
        }

        this.entityDataMap.put(ActorDataTypes.CONTAINER_TYPE, (byte) ContainerType.CHEST_BOAT.ordinal());
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
