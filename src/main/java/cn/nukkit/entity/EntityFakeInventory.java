package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

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

        this.setMaxHealth(1);
        this.setHealth(1);

        this.setDataProperty(NAMETAG_ALWAYS_SHOW, (byte) 0, false);
        this.setDataFlag(EntityFlag.CAN_SHOW_NAME, false);

        this.setDataFlag(EntityFlag.FIRE_IMMUNE, true);
        this.setDataFlag(EntityFlag.NO_AI, true);
        this.setDataFlag(EntityFlag.SILENT, true);
        this.setDataFlag(EntityFlag.HAS_GRAVITY, false);
        this.setDataFlag(EntityFlag.HAS_COLLISION, false);
        this.setDataFlag(EntityFlag.CAN_CLIMB, false);

        if (this.namedTag.contains("ContainerSize")) {
            this.containerSize = this.namedTag.getInt("ContainerSize");
        }
        if (this.namedTag.contains("displayName")) {
            this.displayName = this.namedTag.getString("displayName");
            this.setNameTag(this.displayName);
        }

        this.entityDataMap.put(CONTAINER_TYPE, InventoryType.CONTAINER.getNetworkType());
        this.entityDataMap.put(CONTAINER_SIZE, this.containerSize);
        this.entityDataMap.put(CONTAINER_STRENGTH_MODIFIER, 0);
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
