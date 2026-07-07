package org.powernukkitx.entity;

import org.powernukkitx.Player;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.inventory.InventoryHolder;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;

import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;
import org.jetbrains.annotations.NotNull;

import java.util.Set;


public class EntityFakeInventory extends Entity implements InventoryHolder {

    public EntityFakeInventory(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return EntityID.FAKE_INVENTORY;
    }

    private int containerSize;
    private String displayName;
    private Inventory inventory = null;

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void initEntity() {
        super.initEntity();

        this.setHealthMax(1);
        this.setHealthCurrent(1);

        this.setDataProperty(ActorDataTypes.NAMETAG_ALWAYS_SHOW, (byte) 0, false);
        this.setDataFlag(ActorFlags.CAN_SHOW_NAME, false);

        this.setDataFlag(ActorFlags.FIRE_IMMUNE, true);
        this.setDataFlag(ActorFlags.NO_AI, true);
        this.setDataFlag(ActorFlags.SILENT, true);
        this.setDataFlag(ActorFlags.HAS_GRAVITY, false);
        this.setDataFlag(ActorFlags.HAS_COLLISION, false);
        this.setDataFlag(ActorFlags.CAN_CLIMB, false);

        final CompoundTag nbtMap = this.getNbt();
        if (nbtMap.containsInt("ContainerSize")) {
            this.containerSize = nbtMap.getInt("ContainerSize");
        }
        if (nbtMap.containsString("displayName")) {
            this.displayName = nbtMap.getString("displayName");
            this.setNameTag(this.displayName);
        }

        this.actorDataMap.put(ActorDataTypes.CONTAINER_TYPE, (byte) ContainerType.CONTAINER.getId());
        this.actorDataMap.put(ActorDataTypes.CONTAINER_SIZE, this.containerSize);
        this.actorDataMap.put(ActorDataTypes.CONTAINER_STRENGTH_MODIFIER, 0);
    }

    public int getContainerSize() {
        return containerSize;
    }

    @Override
    public String getOriginalName() {
        return displayName;
    }

    @Override
    public boolean canCollide() {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBeMovedByCurrents() {
        return false;
    }

    @Override
    public float getGravity() {
        return 0f;
    }

    @Override
    public void setOnFire(int seconds) {
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return false;
    }

    @Override
    public void kill() {
        this.close();
    }

    @Override
    public boolean onUpdate(int tick) {
        if (this.closed) return false;

        if (this.motionX != 0 || this.motionY != 0 || this.motionZ != 0) {
            this.setMotion(Vector3.ZERO);
        }

        return super.onUpdate(tick);
    }

    @Override
    public boolean canBeSavedWithChunk() {
        return false;
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 pos) {
        return false;
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("mob", "pnx", "fake_inventory");
    }
}
