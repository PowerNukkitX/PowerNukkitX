package org.powernukkitx.entity.passive;

import org.powernukkitx.Player;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityLiving;
import org.powernukkitx.entity.EntityVariant;
import org.powernukkitx.entity.EntityWalkable;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.HoppingController;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.MoveToTargetExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.NearestItemSensor;
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.entity.data.property.EntityProperty;
import org.powernukkitx.entity.data.sulfurcube.SulfurCubeArchetype;
import org.powernukkitx.entity.data.sulfurcube.SulfurCubePhysics;
import org.powernukkitx.entity.item.EntityItem;
import org.powernukkitx.entity.mob.EntityWarden;
import org.powernukkitx.entity.mob.EntityWither;
import org.powernukkitx.entity.mob.EntityZoglin;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.event.entity.EntityExplosionPrimeEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Explosion;
import org.powernukkitx.level.GameRule;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Utils;

import lombok.Getter;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerId;
import org.cloudburstmc.protocol.bedrock.packet.LevelSoundEventPacket;
import org.cloudburstmc.protocol.bedrock.packet.MobEquipmentPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

/**
 * Sulfur cube entity class
 * @author SemihAkin685, xRookieFight
 * @since 29/07/2026
 */
public class EntitySulfurCube extends EntityAnimal implements EntityWalkable, EntityVariant {

    public static final int SIZE_SMALL = 1;
    public static final int SIZE_LARGE = 2;

    public static final String TAG_SIZE = "Size";
    public static final String TAG_ABSORBED_BLOCK = "AbsorbedBlock";
    private static final String TAG_GROWTH_TICKS = "GrowthTicks";
    private static final String TAG_GROWTH_LOCKED = "GrowthLocked";
    private static final String TAG_FUSE = "Fuse";
    private static final String TAG_FROM_BUCKET = "FromBucket";
    private static final String TAG_PICKUP_TIMER = "PickupTimer";

    private static final int GROWTH_TIME_TICKS = 24000;
    private static final int SLIME_BALL_GROWTH_BONUS = 2400;
    private static final int MANUAL_FUSE_TICKS = 120;
    private static final int HEALTH_GROWN = 8;
    private static final int HEALTH_YOUNG = 4;
    private static final int EJECT_PICKUP_COOLDOWN = 100;
    private static final float EXPLOSION_FORCE = 3;

    public static final EntityProperty[] PROPERTIES = {SulfurCubeArchetype.entityProperty()};

    private final SulfurCubePhysics physics = new SulfurCubePhysics(this);

    @Getter
    @Nullable
    private SulfurCubeArchetype archetype;
    private int growthTicks;
    private boolean growthLocked;
    private int fuse = -1;
    private boolean exploding;
    private int pickupTimer;

    public EntitySulfurCube(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return SULFUR_CUBE;
    }

    @Override
    public String getOriginalName() {
        return "Sulfur Cube";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("sulfur_cube", "mob");
    }

    @Override
    protected void initEntity() {
        if (!this.nbt.contains(TAG_SIZE)) {
            this.nbt.putInt(TAG_SIZE, SIZE_LARGE);
        }

        super.initEntity();

        this.growthTicks = this.nbt.getInt(TAG_GROWTH_TICKS);
        this.growthLocked = this.nbt.getBoolean(TAG_GROWTH_LOCKED);
        this.fuse = this.nbt.contains(TAG_FUSE) ? this.nbt.getInt(TAG_FUSE) : -1;
        this.pickupTimer = this.nbt.getInt(TAG_PICKUP_TIMER);

        syncSwallowedBlock();
        applyVariantAppearance();

        if (isIgnited()) {
            setDataFlag(ActorFlags.IGNITED, true);
            setDataProperty(ActorDataTypes.FUSE_TIME, this.fuse);
        }

        if (isFromBucket()) {
            setPersistent(true);
        }
        
        this.recalculateBoundingBox();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.nbt.putInt(TAG_SIZE, getVariant());
        this.nbt.putInt(TAG_GROWTH_TICKS, this.growthTicks);
        this.nbt.putBoolean(TAG_GROWTH_LOCKED, this.growthLocked);
        this.nbt.putInt(TAG_FUSE, this.fuse);
        this.nbt.putInt(TAG_PICKUP_TIMER, this.pickupTimer);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return BehaviorGroup.builder(this)
                .behaviors(
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_ITEM, 0.35f, true, 9f, 1f),
                                entity -> canSwallow() && isSwallowable(getMemoryStorage().get(CoreMemoryTypes.NEAREST_ITEM)), 3, 1),
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.35f, true, 10f, 2f),
                                entity -> canSwallow() && isOfferingSwallowable(getMemoryStorage().get(CoreMemoryTypes.NEAREST_PLAYER)), 2, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.35f, 12, 100, false, -1, true, 10),
                                entity -> !hasSwallowedBlock(), 1, 1)
                )
                .sensors(
                        new NearestItemSensor(9, 0, 20),
                        new NearestPlayerSensor(10, 0, 20)
                )
                .controllers(new HoppingController(40), new LookController(true, true))
                .routeFinder(new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this))
                .build();
    }

    @Override
    public int getVariant() {
        return this.nbt.contains(TAG_SIZE) ? this.nbt.getInt(TAG_SIZE) : SIZE_LARGE;
    }

    @Override
    public void setVariant(int variant) {
        this.nbt.putInt(TAG_SIZE, variant);
        applyVariantAppearance();
    }

    @Override
    public boolean hasVariant() {
        return this.nbt.contains(TAG_SIZE);
    }

    @Override
    public int[] getAllVariant() {
        return new int[]{SIZE_SMALL, SIZE_LARGE};
    }

    @Override
    public int randomVariant() {
        return SIZE_LARGE;
    }

    public boolean isFullGrown() {
        return getVariant() == SIZE_LARGE;
    }

    private void applyVariantAppearance() {
        setDataFlag(ActorFlags.BABY, !isFullGrown());
    }

    @Override
    public float getWidth() {
        return getBehaviorGroup() == null ? 0 : 0.98f;
    }

    @Override
    public float getHeight() {
        return getBehaviorGroup() == null ? 0 : 0.98f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(variantHealth());
    }

    private int variantHealth() {
        return isFullGrown() ? HEALTH_GROWN : HEALTH_YOUNG;
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(isFullGrown() ? 0.4f : 0.3f);
    }

    public boolean hasSwallowedBlock() {
        return this.nbt.contains(TAG_ABSORBED_BLOCK);
    }

    @Nullable
    public String getSwallowedBlock() {
        return hasSwallowedBlock() ? this.nbt.getString(TAG_ABSORBED_BLOCK) : null;
    }

    public void setSwallowedBlock(@Nullable String blockId) {
        if (Objects.equals(getSwallowedBlock(), blockId)) {
            return;
        }
        applySwallowedBlock(blockId);
        playCubeSound(blockId == null ? SoundEvent.EJECT_BLOCK : SoundEvent.ABSORB_BLOCK);
    }

    /**
     * Swaps the swallowed block without the swallow/spit sound, for state that is being restored rather
     * than produced by something the cube just did.
     */
    private void applySwallowedBlock(@Nullable String blockId) {
        if (blockId == null) {
            this.nbt.remove(TAG_ABSORBED_BLOCK);
        } else {
            this.nbt.putString(TAG_ABSORBED_BLOCK, blockId);
        }
        syncSwallowedBlock();
    }

    public void ejectBlock() {
        String absorbed = getSwallowedBlock();
        if (absorbed == null) return;
        this.level.dropItem(this.add(0, getHeight() / 2, 0), Item.get(absorbed));
        setSwallowedBlock(null);
        this.pickupTimer = EJECT_PICKUP_COOLDOWN;
    }

    private void syncSwallowedBlock() {
        this.archetype = SulfurCubeArchetype.forSwallowed(getSwallowedBlock());
        setEnumEntityProperty(PROPERTIES[0].getIdentifier(),
                this.archetype == null ? SulfurCubeArchetype.NONE : this.archetype.getPropertyName());

        Player[] viewers = getViewers().values().toArray(Player[]::new);
        sendData(viewers);
        sendSwallowedBlock(viewers);
    }

    private void playCubeSound(SoundEvent sound) {
        LevelSoundEventPacket packet = new LevelSoundEventPacket();
        packet.setSoundEvent(sound);
        packet.setPosition(this.toNetwork());
        packet.setData(-1);
        packet.setActorIdentifier(getIdentifier());
        packet.setActorUniqueId(getId());
        packet.setBaby(!isFullGrown());
        packet.setGlobal(false);
        this.level.addChunkPacket(getFloorX() >> 4, getFloorZ() >> 4, packet);
    }

    private void sendSwallowedBlock(Player... targets) {
        if (targets.length == 0) return;
        String absorbed = getSwallowedBlock();

        MobEquipmentPacket packet = new MobEquipmentPacket();
        packet.setTargetRuntimeID(runtimeId());
        packet.setItem((absorbed == null ? Item.AIR : Item.get(absorbed)).toNetwork());
        packet.setSlot(0);
        packet.setSelectedSlot(0);
        packet.setContainerId(ContainerId.INVENTORY);
        for (Player target : targets) {
            target.sendPacket(packet);
        }
    }

    @Override
    public void spawnTo(Player player) {
        super.spawnTo(player);
        if (hasSwallowedBlock()) {
            sendSwallowedBlock(player);
        }
    }

    @Override
    public void setRotation(double yaw, double pitch) {
        if (hasSwallowedBlock()) {
            super.setRotation(0, 0);
            return;
        }
        super.setRotation(yaw, pitch);
    }

    @Override
    public void setRotation(double yaw, double pitch, double headYaw) {
        if (hasSwallowedBlock()) {
            super.setRotation(0, 0, 0);
            return;
        }
        super.setRotation(yaw, pitch, headYaw);
    }

    private boolean canSwallow() {
        return isFullGrown() && !hasSwallowedBlock() && this.pickupTimer == 0 && !isIgnited();
    }

    private boolean isSwallowable(@Nullable EntityItem itemEntity) {
        if (itemEntity == null || !itemEntity.isAlive() || itemEntity.closed) return false;
        return SulfurCubeArchetype.fromItem(itemEntity.getItem()) != null;
    }

    private boolean isOfferingSwallowable(@Nullable Player player) {
        if (player == null || !player.isAlive() || player.closed) return false;
        return SulfurCubeArchetype.fromItem(player.getInventory().getItemInMainHand()) != null;
    }

    private void swallowNearbyItem() {
        for (Entity entity : this.level.getNearbyEntitiesSafe(getBoundingBox().grow(1, 0.5, 1), this)) {
            if (!(entity instanceof EntityItem itemEntity) || !itemEntity.isAlive()) continue;
            Item drop = itemEntity.getItem();
            if (SulfurCubeArchetype.fromItem(drop) == null) continue;

            setSwallowedBlock(drop.getId());
            itemEntity.close();
            if (drop.getCount() > 1) {
                Item remainder = drop.clone();
                remainder.setCount(drop.getCount() - 1);
                this.level.dropItem(itemEntity, remainder);
            }
            return;
        }
    }

    public boolean isFromBucket() {
        return this.nbt.getBoolean(TAG_FROM_BUCKET);
    }

    public void setFromBucket(boolean fromBucket) {
        this.nbt.putBoolean(TAG_FROM_BUCKET, fromBucket);
    }

    /**
     * Snapshot of everything a bucket has to carry over, so a cube survives the round trip unchanged.
     */
    public CompoundTag writeBucketTag() {
        CompoundTag tag = new CompoundTag()
                .putInt(TAG_SIZE, getVariant())
                .putFloat("Health", getHealthCurrent())
                .putInt(TAG_GROWTH_TICKS, this.growthTicks)
                .putBoolean(TAG_GROWTH_LOCKED, this.growthLocked)
                .putInt(TAG_PICKUP_TIMER, this.pickupTimer);

        String swallowed = getSwallowedBlock();
        if (swallowed != null) {
            tag.putString(TAG_ABSORBED_BLOCK, swallowed);
        }
        if (hasCustomName()) {
            tag.putString("CustomName", getNameTag());
            tag.putBoolean("CustomNameVisible", isNameTagVisible());
        }
        return tag;
    }

    public void readBucketTag(@Nullable CompoundTag tag) {
        setFromBucket(true);
        setPersistent(true);
        if (tag == null) {
            return;
        }

        setVariant(tag.contains(TAG_SIZE) ? tag.getInt(TAG_SIZE) : SIZE_LARGE);
        setHealthMax(variantHealth());
        setHealthCurrent(tag.contains("Health")
                ? Math.min(tag.getFloat("Health"), getHealthMax()) : getHealthMax());
        this.growthTicks = tag.getInt(TAG_GROWTH_TICKS);
        this.growthLocked = tag.getBoolean(TAG_GROWTH_LOCKED);
        this.pickupTimer = tag.getInt(TAG_PICKUP_TIMER);
        if (tag.contains(TAG_ABSORBED_BLOCK)) {
            applySwallowedBlock(tag.getString(TAG_ABSORBED_BLOCK));
        }
        if (tag.contains("CustomName")) {
            setNameTag(tag.getString("CustomName"));
            setNameTagVisible(tag.getBoolean("CustomNameVisible"));
        }
    }

    public boolean isIgnited() {
        return this.fuse >= 0;
    }

    public void ignite(int fuseTicks) {
        if (this.archetype != SulfurCubeArchetype.EXPLOSIVE || isIgnited()) return;
        this.fuse = fuseTicks;
        setDataFlag(ActorFlags.IGNITED, true);
        setDataProperty(ActorDataTypes.FUSE_TIME, this.fuse);
    }

    private void explode() {
        this.exploding = true;
        EntityExplosionPrimeEvent event = new EntityExplosionPrimeEvent(this, EXPLOSION_FORCE);
        this.server.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            this.exploding = false;
            this.fuse = -1;
            setDataFlag(ActorFlags.IGNITED, false);
            setDataProperty(ActorDataTypes.FUSE_TIME, 0);
            return;
        }

        setSwallowedBlock(null);
        if (this.level.getGameRules().getBoolean(GameRule.TNT_EXPLODES)) {
            Explosion explosion = new Explosion(this, event.getForce(), this);
            if (event.isBlockBreaking() && this.level.getGameRules().getBoolean(GameRule.MOB_GRIEFING)) {
                explosion.explodeA();
            }
            explosion.explodeB();
        }
        close();
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (this.archetype != null && item.isNull() && !isIgnited()) {
            physics.launch(this.archetype, player, 1f);
            return true;
        }

        Boolean handled = isFullGrown() ? interactGrown(player, item) : interactYoung(item);
        return handled != null ? handled : super.onInteract(player, item, clickedPos);
    }

    @Nullable
    private Boolean interactGrown(Player player, Item item) {
        if (isIgnited()) return null;

        if (Objects.equals(item.getId(), Item.BUCKET)) {
            scoopIntoBucket(player, item);
            // The scoop already swapped the empty bucket for a filled one. Reporting the interaction as
            // handled would make InventoryTransactionHandler consume the held stack a second time.
            return false;
        }
        if (Objects.equals(item.getId(), Item.SHEARS) && hasSwallowedBlock()) {
            ejectBlock();
            return true;
        }
        if (this.archetype == SulfurCubeArchetype.EXPLOSIVE
                && (Objects.equals(item.getId(), Item.FLINT_AND_STEEL) || Objects.equals(item.getId(), Item.FIRE_CHARGE))) {
            ignite(MANUAL_FUSE_TICKS);
            return true;
        }
        if (SulfurCubeArchetype.fromItem(item) != null) {
            String previous = getSwallowedBlock();
            if (previous != null) {
                this.level.dropItem(this.add(0, getHeight() / 2, 0), Item.get(previous));
            }
            setSwallowedBlock(item.getId());
            return true;
        }
        return null;
    }

    @Nullable
    private Boolean interactYoung(Item item) {
        if (this.growthLocked) return null;

        if (Objects.equals(item.getId(), Item.SLIME_BALL)) {
            this.growthTicks += SLIME_BALL_GROWTH_BONUS;
            return true;
        }
        if (Objects.equals(item.getId(), BlockID.GOLDEN_DANDELION)) {
            this.growthLocked = true;
            return true;
        }
        return null;
    }

    private void scoopIntoBucket(Player player, Item bucketInHand) {
        Item filled = Item.get(Item.SULFUR_CUBE_BUCKET);
        filled.setNbt(writeBucketTag());

        if (!player.isCreative()) {
            bucketInHand.count--;
            player.getInventory().setItemInMainHand(bucketInHand);
        }
        for (Item leftover : player.getInventory().addItem(filled)) {
            player.dropItem(leftover);
        }
        close();
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (isIgnited() && !isSelfInflicted(source)) {
            source.setCancelled(true);
            return false;
        }

        if (this.archetype == SulfurCubeArchetype.EXPLOSIVE && !isIgnited()) {
            switch (source.getCause()) {
                case FIRE, FIRE_TICK, LAVA -> ignite(MANUAL_FUSE_TICKS);
                case BLOCK_EXPLOSION, ENTITY_EXPLOSION -> {
                    ignite(Utils.rand(15, 44));
                    source.setCancelled(true);
                    return false;
                }
                default -> {
                }
            }
        }

        if (hasSwallowedBlock() && !isBypassingAbsorption(source)) {
            source.setCancelled(true);
            if (this.archetype != null && source instanceof EntityDamageByEntityEvent event) {
                physics.launch(this.archetype, event.getDamager(), source.getDamage());
            }
            return false;
        }

        return super.attack(source);
    }

    private boolean isSelfInflicted(EntityDamageEvent source) {
        return switch (source.getCause()) {
            case VOID, SUICIDE -> true;
            default -> false;
        };
    }

    private boolean isBypassingAbsorption(EntityDamageEvent source) {
        switch (source.getCause()) {
            case FIRE, FIRE_TICK, LAVA, SUFFOCATION, WITHER, VOID, SUICIDE -> {
                return true;
            }
            default -> {
            }
        }

        if (source instanceof EntityDamageByEntityEvent event) {
            Entity damager = event.getDamager();
            return damager instanceof EntityWarden
                    || damager instanceof EntityZoglin
                    || damager instanceof EntityWither
                    || (damager instanceof EntityWolf wolf && wolf.isTamed());
        }

        return false;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (isAlive() && !this.closed) {
            if (this.pickupTimer > 0) {
                this.pickupTimer--;
            }

            if (hasSwallowedBlock()) {
                stopPathfinding();
                if (this.archetype != null) {
                    physics.shoveFromPlayers(this.archetype);
                }
            }

            if (isIgnited()) {
                if (this.fuse == 0) {
                    explode();
                    return true;
                }
                this.fuse--;
                if (this.fuse % 5 == 0) {
                    setDataProperty(ActorDataTypes.FUSE_TIME, this.fuse);
                }
            }

            if (!isFullGrown() && !this.growthLocked && !isImmobile() && ++this.growthTicks >= GROWTH_TIME_TICKS) {
                grow();
            }

            if (currentTick % 10 == 0 && this.archetype == SulfurCubeArchetype.HOT) {
                scorchNeighbours();
            }

            if (currentTick % 20 == 0 && canSwallow()
                    && this.level.getGameRules().getBoolean(GameRule.MOB_GRIEFING)) {
                swallowNearbyItem();
            }

            if (this.archetype != null) {
                physics.damp(this.archetype);
            }
        }

        return super.onUpdate(currentTick);
    }

    private void stopPathfinding() {
        if (hasMoveDirection()) {
            setMoveTarget(null);
            setMoveDirectionStart(null);
            setMoveDirectionEnd(null);
        }
        if (this.yaw != 0 || this.pitch != 0 || this.headYaw != 0) {
            setRotation(0, 0, 0);
        }
    }

    private void scorchNeighbours() {
        for (Entity entity : this.level.getNearbyEntitiesSafe(getBoundingBox().grow(0.2, 0.1, 0.2), this)) {
            if (entity instanceof EntityLiving living && !(entity instanceof EntitySulfurCube)) {
                living.attack(new EntityDamageByEntityEvent(this, living, EntityDamageEvent.DamageCause.CONTACT, 1f));
            }
        }
    }

    private void grow() {
        this.growthTicks = 0;
        setVariant(SIZE_LARGE);
        setHealthMax(variantHealth());
        setHealthCurrent(getHealthMax());
    }

    @Override
    public double getGroundFrictionFactor() {
        if (this.archetype == null || !this.onGround) {
            return super.getGroundFrictionFactor();
        }
        return this.archetype.getGroundKeepPerTick();
    }

    @Override
    public double getFloatingForceFactor() {
        if (this.archetype == null) return super.getFloatingForceFactor();
        return this.archetype.floats() ? super.getFloatingForceFactor() : 0;
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        String absorbed = getSwallowedBlock();
        if (isFullGrown() && absorbed != null && !this.exploding) {
            return new Item[]{Item.get(absorbed)};
        }
        return Item.EMPTY_ARRAY;
    }

    @Override
    public Integer getExperienceDrops() {
        return this.exploding || !isFullGrown() ? 0 : Utils.rand(1, 2);
    }

    @Override
    public void kill() {
        if (!isAlive()) {
            return;
        }

        if (isFullGrown() && !this.exploding) {
            splitIntoChildren();
        }
        super.kill();
    }

    private void splitIntoChildren() {
        CompoundTag childNbt = getNbt().copy();
        childNbt.remove(TAG_ABSORBED_BLOCK);
        childNbt.remove(TAG_FUSE);
        childNbt.remove(TAG_FROM_BUCKET);
        childNbt.remove("Scale");
        childNbt.remove("Variant");
        childNbt.putInt(TAG_GROWTH_TICKS, 0);
        childNbt.putInt(TAG_SIZE, SIZE_SMALL);

        for (int i = 0; i < 2; i++) {
            EntitySulfurCube cube = new EntitySulfurCube(getChunk(), childNbt.copy());
            double angle = Math.toRadians(Utils.rand(0, 359));
            cube.setPosition(this.add(Math.cos(angle) * 0.4, 0.1, Math.sin(angle) * 0.4));
            cube.setRotation(this.yaw, this.pitch);
            cube.setVariant(SIZE_SMALL);
            cube.spawnToAll();
            cube.setMotion(new Vector3(Math.cos(angle) * 0.25, 0.3, Math.sin(angle) * 0.25));
        }
    }
}
