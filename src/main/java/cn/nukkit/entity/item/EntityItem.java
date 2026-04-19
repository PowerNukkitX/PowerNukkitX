package cn.nukkit.entity.item;

import cn.nukkit.Server;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.components.NameableComponent;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.ItemDespawnEvent;
import cn.nukkit.event.entity.ItemSpawnEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddItemEntityPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.EntityEventPacket;
import lombok.extern.slf4j.Slf4j;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;

/**
 * @author MagicDroidX
 */
@Slf4j
public class EntityItem extends Entity {
    @Override
    @NotNull
    public String getIdentifier() {
        return ITEM;
    }

    protected String owner;
    protected String thrower;
    protected Item item;
    protected int pickupDelay;
    private boolean mergeItems;
    private boolean shouldDespawn;
    private boolean isDisplayOnly;
    private int waterTicks;

    public EntityItem(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public float getWidth() {
        return 0.25f;
    }

    @Override
    public float getLength() {
        return 0.25f;
    }

    @Override
    public float getHeight() {
        return 0.25f;
    }

    @Override
    public float getGravity() {
        if(!getDataFlag(EntityFlag.HAS_GRAVITY)) return 0f;
        return 0.04f;
    }

    @Override
    public float getDrag() {
        return 0.02f;
    }

    @Override
    protected float getBaseOffset() {
        return 0.125f;
    }

    @Override
    public boolean canCollide() {
        return false;
    }

    // items use their own despawn system, so they should be persistent to prevent them from being unloaded when far away from players
    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public NameableComponent getComponentNameable() {
        return DEFAULT_NOT_NAMEABLE;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.setHealthMax(5);
        this.setHealthCurrent(this.namedTag.getShort("Health"));

        if (this.namedTag.contains("Age")) {
            this.age = this.namedTag.getShort("Age");
        }

        if(this.namedTag.contains("ShouldDespawn")) {
            this.shouldDespawn = this.namedTag.getBoolean("ShouldDespawn");
        } else shouldDespawn = true;

        if(this.namedTag.contains("DisplayOnly")) {
            this.isDisplayOnly = this.namedTag.getBoolean("DisplayOnly");
        } else isDisplayOnly = false;

        if (this.namedTag.contains("PickupDelay")) {
            this.pickupDelay = this.namedTag.getShort("PickupDelay");
        }

        if (this.namedTag.contains("Owner")) {
            this.owner = this.namedTag.getString("Owner");
        }

        if (this.namedTag.contains("Thrower")) {
            this.thrower = this.namedTag.getString("Thrower");
        }

        if (!this.namedTag.contains("Item")) {
            this.close();
            return;
        }

        if(this.namedTag.contains("Mergeable")) {
            this.mergeItems = this.namedTag.getBoolean("Mergeable");
        } else mergeItems = true;

        this.item = NBTIO.getItemHelper(this.namedTag.getCompound("Item"));
        this.setDataFlag(EntityFlag.HAS_GRAVITY, true);

        if (this.item.isLavaResistant()) {
            this.setFireImmune(true);
        }

        this.server.getPluginManager().callEvent(new ItemSpawnEvent(this));
    }


    @Override
    public boolean attack(EntityDamageEvent source) {
        if (this.isDisplayOnly()) return false;

        if (item != null && item.isLavaResistant() && (
                source.getCause() == DamageCause.LAVA ||
                        source.getCause() == DamageCause.FIRE ||
                        source.getCause() == DamageCause.FIRE_TICK)) {
            return false;
        }

        return (source.getCause() == DamageCause.VOID ||
                source.getCause() == DamageCause.CONTACT ||
                source.getCause() == DamageCause.FIRE_TICK ||
                (source.getCause() == DamageCause.ENTITY_EXPLOSION ||
                        source.getCause() == DamageCause.BLOCK_EXPLOSION) &&
                        !this.isInsideOfWater() && (this.item == null ||
                        !Objects.equals(this.item.getId(), Item.NETHER_STAR))) && super.attack(source);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        int tickDiff = currentTick - this.lastUpdate;

        if (tickDiff <= 0 && !this.justCreated) {
            return true;
        }

        this.lastUpdate = currentTick;

        if (this.mergeItems && this.age % 60 == 0 && this.onGround && this.getItem() != null && this.isAlive()) {
            if (this.getItem().getCount() < this.getItem().getMaxStackSize()) {
                for (Entity entity : this.getLevel().getNearbyEntities(getBoundingBox().grow(1, 1, 1), this, false)) {
                    if (entity instanceof EntityItem) {
                        if (!entity.isAlive()) {
                            continue;
                        }
                        if(!((EntityItem) entity).mergeItems) continue;
                        Item closeItem = ((EntityItem) entity).getItem();
                        if (!closeItem.equals(getItem(), true, true)) {
                            continue;
                        }
                        if (!entity.isOnGround()) {
                            continue;
                        }
                        int newAmount = this.getItem().getCount() + closeItem.getCount();
                        if (newAmount > this.getItem().getMaxStackSize()) {
                            continue;
                        }
                        entity.close();
                        this.getItem().setCount(newAmount);
                        EntityEventPacket packet = new EntityEventPacket();
                        packet.eid = getId();
                        packet.data = newAmount;
                        packet.event = EntityEventPacket.MERGE_ITEMS;
                        Server.broadcastPacket(this.getViewers().values(), packet);
                    }
                }
            }
        }

        boolean hasUpdate = this.entityBaseTick(tickDiff);

        boolean lavaResistant = fireProof || item != null && item.isLavaResistant();

        if (!lavaResistant && (isInsideOfFire() || isInsideOfLava())) {
            this.kill();
        }

        if (this.isAlive()) {
            if (this.pickupDelay > 0 && this.pickupDelay < 32767) {
                this.pickupDelay -= tickDiff;
                if (this.pickupDelay < 0) {
                    this.pickupDelay = 0;
                }
            }

            String bid = this.level.getBlockIdAt((int) this.x, (int) this.boundingBox.getMaxY(), (int) this.z, 0);

            boolean inWaterBlock =
                    Objects.equals(bid, BlockID.WATER) || Objects.equals(bid, BlockID.FLOWING_WATER)
                            || (Objects.equals(this.level.getBlockIdAt((int) this.x, (int) this.boundingBox.getMaxY(), (int) this.z, 1), BlockID.WATER)
                            || Objects.equals(this.level.getBlockIdAt((int) this.x, (int) this.boundingBox.getMaxY(), (int) this.z, 1), BlockID.FLOWING_WATER));

            boolean inLavaBlock =
                    Objects.equals(this.level.getBlockIdAt((int) this.x, (int) this.boundingBox.getMaxY(), (int) this.z, 0), BlockID.LAVA)
                            || Objects.equals(this.level.getBlockIdAt((int) this.x, (int) this.boundingBox.getMaxY(), (int) this.z, 0), BlockID.FLOWING_LAVA)
                            || Objects.equals(this.level.getBlockIdAt((int) this.x, (int) this.boundingBox.getMaxY(), (int) this.z, 1), BlockID.LAVA)
                            || Objects.equals(this.level.getBlockIdAt((int) this.x, (int) this.boundingBox.getMaxY(), (int) this.z, 1), BlockID.FLOWING_LAVA);

            boolean inLiquid = inWaterBlock || (lavaResistant && inLavaBlock);

            if (inLiquid) {
                this.motionY = this.getGravity() - 0.06;
                applyLiquidFlow();
            } else if (this.isInsideOfWater() || (lavaResistant && this.isInsideOfLava())) {
                this.motionY = this.getGravity() - 0.06;
            } else {
                this.motionY -= this.getGravity();
            }

            if (this.checkObstruction(this.x, this.y, this.z)) {
                hasUpdate = true;
            }

            this.move(this.motionX, this.motionY, this.motionZ);

            if (!inLiquid) {
                double friction = 1 - this.getDrag();
                if (this.onGround && (Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionZ) > 0.00001)) {
                    friction *= this.getLevel().getBlock(this.temporalVector.setComponents(
                            (int) Math.floor(this.x), (int) Math.floor(this.y - 1), (int) Math.floor(this.z)
                    )).getFrictionFactor();
                }
                this.motionX *= friction;
                this.motionY *= 1 - this.getDrag();
                this.motionZ *= friction;

                if (this.onGround) {
                    this.motionY *= -0.5;
                }
            }

            if (inWaterBlock) {
                if (this.waterTicks < 1000) this.waterTicks++;
            } else {
                this.waterTicks = 0;
            }

            this.updateMovement();

            if (!this.shouldDespawn) {
                if (this.age > 0) this.age--;
            } else if (this.isDisplayOnly && this.age > 5980) {
                this.age = 0;
                respawnToAll();
            } else if (this.age > 6000) {
                ItemDespawnEvent ev = new ItemDespawnEvent(this);
                this.server.getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    this.age = 0;
                    respawnToAll();
                } else {
                    this.kill();
                    hasUpdate = true;
                }
            }
        }

        return hasUpdate || !this.onGround || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001;
    }

    private void applyLiquidFlow() {
        cn.nukkit.block.Block liquidBlock = this.level.getBlock((int) this.x, (int) Math.floor(this.y), (int) this.z);
        if (!(liquidBlock instanceof cn.nukkit.block.BlockLiquid)) {
            return;
        }

        cn.nukkit.math.Vector3 flow = ((cn.nukkit.block.BlockLiquid) liquidBlock).getFlowVector();
        double len = Math.sqrt(flow.x * flow.x + flow.z * flow.z);
        if (len > 0) {
            flow = new cn.nukkit.math.Vector3(flow.x / len, 0, flow.z / len);
        }

        double accelWater = 0.0015;
        double accelIce = 0.041;
        double capWaterXZ = 0.035;
        double capIceXZ = 0.22;
        double dragXZ = 0.92;
        double dragY = 0.90;

        cn.nukkit.block.Block under = this.level.getBlock((int) this.x, (int) Math.floor(this.y) - 1, (int) this.z);
        boolean overIce = Objects.equals(under.getId(), BlockID.ICE)
                || Objects.equals(under.getId(), BlockID.FROSTED_ICE)
                || Objects.equals(under.getId(), BlockID.PACKED_ICE)
                || Objects.equals(under.getId(), BlockID.BLUE_ICE);

        double accel = overIce ? accelIce : accelWater;
        this.motionX += flow.x * accel;
        this.motionZ += flow.z * accel;

        this.motionY -= this.getGravity() * 0.20;
        int surfaceY = (int) Math.floor(this.boundingBox.getMaxY());
        int scanY = surfaceY;
        for (int i = 0; i < 3; i++) {
            String idUp = this.level.getBlockIdAt((int) this.x, scanY + 1, (int) this.z, 0);
            String idUpWL = this.level.getBlockIdAt((int) this.x, scanY + 1, (int) this.z, 1);
            boolean isWaterUp = Objects.equals(idUp, BlockID.WATER) || Objects.equals(idUp, BlockID.FLOWING_WATER)
                    || Objects.equals(idUpWL, BlockID.WATER) || Objects.equals(idUpWL, BlockID.FLOWING_WATER);
            if (!isWaterUp) {
                surfaceY = scanY + 1;
                break;
            }
            scanY++;
        }

        double topY = this.boundingBox.getMaxY();
        double depth = surfaceY - topY;
        double targetDepth = 0.35;
        double hysteresis = 0.10;
        double k = 0.20;
        double buoyBase = 0.010;
        double buoyMaxExtra = 0.030;

        if (this.waterTicks < 15) {
            this.motionY -= 0.005;
        }

        double force = k * (depth - targetDepth);
        if (depth > targetDepth + hysteresis) {
            double buoyancy = buoyBase + Math.min((depth - targetDepth) * 0.06, buoyMaxExtra);
            this.motionY += force + buoyancy;
            this.motionY *= 0.92;
        } else if (depth < targetDepth - hysteresis) {
            this.motionY += force - 0.006;
            this.motionY *= 0.92;
        } else {
            this.motionY += force * 0.5 + buoyBase * 0.5;
            this.motionY *= 0.94;
        }

        this.motionX *= dragXZ;
        this.motionZ *= dragXZ;
        this.motionY *= dragY;

        double capXZ = overIce ? capIceXZ : capWaterXZ;
        if (this.motionX > capXZ) this.motionX = capXZ;
        if (this.motionX < -capXZ) this.motionX = -capXZ;
        if (this.motionZ > capXZ) this.motionZ = capXZ;
        if (this.motionZ < -capXZ) this.motionZ = -capXZ;

        if (this.motionY > 0.06) this.motionY = 0.06;
        if (this.motionY < -0.08) this.motionY = -0.08;
    }

    @Override
    public void setOnFire(int seconds) {
        if (this.isDisplayOnly()) return;
        if (item != null && item.isLavaResistant()) {
            return;
        }
        super.setOnFire(seconds);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        if (this.item != null) { // Yes, a item can be null... I don't know what causes this, but it can happen.
            this.namedTag.putCompound("Item", NBTIO.putItemHelper(this.item, -1));
            this.namedTag.putShort("Health", (int) this.getHealthCurrent());
            this.namedTag.putShort("Age", this.age);
            this.namedTag.putShort("PickupDelay", this.pickupDelay);
            this.namedTag.putBoolean("ShouldDespawn", this.shouldDespawn);
            this.namedTag.putBoolean("DisplayOnly", this.isDisplayOnly);

            if (this.owner != null) {
                this.namedTag.putString("Owner", this.owner);
            }

            if (this.thrower != null) {
                this.namedTag.putString("Thrower", this.thrower);
            }

            this.namedTag.putBoolean("Mergeable", this.mergeItems);
        }
    }

    @Override
    public String getOriginalName() {
        return "Item";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("item", "inanimate");
    }

    @Override
    @NotNull
    public String getName() {
        if (this.hasCustomName()) {
            return getNameTag();
        }
        if (item == null) {
            return getOriginalName();
        }
        return item.count + "x " + this.item.getDisplayName();
    }

    public Item getItem() {
        return item;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    public int getPickupDelay() {
        return pickupDelay;
    }

    public void setPickupDelay(int pickupDelay) {
        this.pickupDelay = pickupDelay;
    }

    public void setDisplayOnly(boolean isDisplayOnly) {
        this.isDisplayOnly = isDisplayOnly;
    }

    public boolean isDisplayOnly() {
        return isDisplayOnly;
    }

    @Override
    public String getOwnerName() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getThrower() {
        return thrower;
    }

    public void setThrower(String thrower) {
        this.thrower = thrower;
    }

    @Override
    public DataPacket createAddEntityPacket() {
        AddItemEntityPacket addEntity = new AddItemEntityPacket();
        addEntity.entityUniqueId = this.getId();
        addEntity.entityRuntimeId = this.getId();
        addEntity.x = (float) this.x;
        addEntity.y = (float) this.y + this.getBaseOffset();
        addEntity.z = (float) this.z;
        addEntity.speedX = (float) this.motionX;
        addEntity.speedY = (float) this.motionY;
        addEntity.speedZ = (float) this.motionZ;
        addEntity.entityData = this.entityDataMap;
        addEntity.item = this.getItem();
        return addEntity;
    }

    @Override
    public boolean doesTriggerPressurePlate() {
        return true;
    }
}
