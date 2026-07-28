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
import org.powernukkitx.entity.data.property.EnumEntityProperty;
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

import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.cloudburstmc.protocol.bedrock.packet.MobArmorEquipmentPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

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

    public static final EntityProperty[] PROPERTIES = new EntityProperty[]{
            new EnumEntityProperty("minecraft:sulfur_cube_archetype", new String[]{
                    "none",
                    "bouncy",
                    "regular",
                    "slow_bouncy",
                    "fast_flat",
                    "light",
                    "fast_sliding",
                    "high_resistance",
                    "slow_flat",
                    "slow_sliding",
                    "sticky",
                    "explosive",
                    "hot"
            }, "none", true)
    };

    public enum Archetype {
        BOUNCY("bouncy", -2.0f, 0.9f, 0.3f, 0.01f, 0.105f, true),
        REGULAR("regular", -1.0f, 0.5f, 0.3f, 0.1f, 0.09f, true),
        SLOW_BOUNCY("slow_bouncy", 0.4f, 0.6f, 0.3f, 0.05f, 0.24f, false),
        FAST_FLAT("fast_flat", -1.0f, 0.5f, 0.2f, 0.01f, 0.09f, false),
        LIGHT("light", -1.0f, 1.0f, 0.3f, 1.8f, 0.18f, true),
        FAST_SLIDING("fast_sliding", 0.5f, 0.1f, 0.05f, 0.01f, 0.09f, false),
        HIGH_RESISTANCE("high_resistance", 0.7f, 0.2f, 1.0f, 0.01f, 0.09f, false),
        SLOW_FLAT("slow_flat", 0.5f, 0.4f, 0.4f, 0.1f, 0.105f, false),
        SLOW_SLIDING("slow_sliding", 0.8f, 0.1f, 0.05f, 0.01f, 0.09f, false),
        STICKY("sticky", -2.0f, 0.0f, 2.0f, 0.01f, 0.09f, false),
        EXPLOSIVE("explosive", -1.0f, 0.5f, 0.3f, 0.3f, 0.09f, true),
        HOT("hot", -1.0f, 0.5f, 0.3f, 0.1f, 0.09f, true);

        private final String propertyName;
        private final float knockbackResistance;
        private final float bounciness;
        private final float friction;
        private final float airDrag;
        private final float verticalKick;
        private final boolean floats;

        Archetype(String propertyName, float knockbackResistance, float bounciness, float friction, float airDrag, float verticalKick, boolean floats) {
            this.propertyName = propertyName;
            this.knockbackResistance = knockbackResistance;
            this.bounciness = bounciness;
            this.friction = friction;
            this.airDrag = airDrag;
            this.verticalKick = verticalKick;
            this.floats = floats;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public boolean floats() {
            return floats;
        }

        public float launchMultiplier() {
            return Math.max(0.1f, 1f - knockbackResistance);
        }

        @Nullable
        public static Archetype fromBlockId(@Nullable String blockId) {
            if (blockId == null) return null;
            String id = blockId.startsWith("minecraft:") ? blockId.substring("minecraft:".length()) : blockId;
            if (id.equals("tnt")) return EXPLOSIVE;
            if (id.equals("magma")) return HOT;
            if (id.equals("honeycomb_block")) return STICKY;
            if (id.equals("soul_sand") || id.equals("soul_soil")) return HIGH_RESISTANCE;
            if (id.equals("blue_ice") || id.equals("packed_ice") || id.equals("snow")) return FAST_SLIDING;
            if (id.equals("mycelium") || id.equals("shroomlight")
                    || id.equals("brown_mushroom_block") || id.equals("red_mushroom_block")
                    || id.equals("mushroom_stem")) return SLOW_SLIDING;
            if (id.equals("iron_block") || id.equals("gold_block") || id.equals("netherite_block")
                    || id.equals("copper_block") || id.equals("raw_iron_block")
                    || id.equals("raw_gold_block") || id.equals("raw_copper_block")) return SLOW_FLAT;
            if (id.endsWith("_wool")) return LIGHT;
            if (id.endsWith("coral_block") || id.equals("sponge") || id.equals("wet_sponge")
                    || id.equals("pumpkin") || id.equals("carved_pumpkin") || id.endsWith("froglight")
                    || id.equals("moss_block") || id.equals("pale_moss_block")) return FAST_FLAT;
            if (id.endsWith("_planks") || id.endsWith("_log") || id.endsWith("_wood")
                    || id.endsWith("_stem") && id.startsWith("stripped") || id.equals("bamboo_block")
                    || id.equals("stripped_bamboo_block") || id.equals("crimson_stem")
                    || id.equals("warped_stem") || id.equals("crimson_hyphae") || id.equals("warped_hyphae")) return BOUNCY;
            if (id.equals("dirt") || id.equals("coarse_dirt") || id.equals("rooted_dirt")
                    || id.equals("grass_block") || id.equals("clay") || id.equals("coal_block")
                    || id.endsWith("concrete_powder") || id.equals("sand") || id.equals("red_sand")
                    || id.equals("gravel") || id.equals("mud") || id.equals("packed_mud")) return REGULAR;
            if (id.equals("stone") || id.equals("cobblestone") || id.equals("mossy_cobblestone")
                    || id.equals("deepslate") || id.equals("cobbled_deepslate") || id.equals("tuff")
                    || id.equals("andesite") || id.equals("diorite") || id.equals("granite")
                    || id.equals("calcite") || id.equals("dripstone_block") || id.equals("amethyst_block")
                    || id.equals("blackstone") || id.equals("basalt") || id.equals("smooth_basalt")
                    || id.equals("netherrack") || id.equals("end_stone") || id.equals("obsidian")
                    || id.equals("sandstone") || id.equals("red_sandstone")
                    || id.endsWith("_ore")) return SLOW_BOUNCY;
            return null;
        }
    }

    private int growthTicks;
    private boolean growthLocked;
    private int fuse = -1;
    private boolean exploding;
    private double lastFallSpeed;
    private boolean wasOnGround;
    private int kickCooldown;
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
    public IBehaviorGroup requireBehaviorGroup() {
        return BehaviorGroup.builder(this)
                .behaviors(
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_ITEM, 0.35f, true, 9f, 1f),
                                entity -> canSeekBlocks() && isAbsorbableItemEntity(getMemoryStorage().get(CoreMemoryTypes.NEAREST_ITEM)), 3, 1),
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.35f, true, 10f, 2f),
                                entity -> canSeekBlocks() && isPlayerHoldingAbsorbable(getMemoryStorage().get(CoreMemoryTypes.NEAREST_PLAYER)), 2, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.35f, 12, 100, false, -1, true, 10),
                                entity -> !hasAbsorbedBlock(), 1, 1)
                )
                .sensors(
                        new NearestItemSensor(9, 0, 20),
                        new NearestPlayerSensor(10, 0, 20)
                )
                .controllers(new HoppingController(40), new LookController(true, true))
                .routeFinder(new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this))
                .build();
    }

    private boolean canSeekBlocks() {
        return getVariant() == SIZE_LARGE && !hasAbsorbedBlock() && this.pickupTimer == 0 && this.fuse < 0;
    }

    private boolean isAbsorbableItemEntity(@Nullable EntityItem itemEntity) {
        if (itemEntity == null || !itemEntity.isAlive() || itemEntity.closed) return false;
        Item drop = itemEntity.getItem();
        return drop != null && drop.isBlock() && Archetype.fromBlockId(drop.getId()) != null;
    }

    private boolean isPlayerHoldingAbsorbable(@Nullable Player player) {
        if (player == null || !player.isAlive() || player.closed) return false;
        Item held = player.getInventory().getItemInMainHand();
        return held.isBlock() && Archetype.fromBlockId(held.getId()) != null;
    }

    @Override
    protected void initEntity() {
        if (!this.nbt.contains(TAG_SIZE)) {
            this.nbt.putInt(TAG_SIZE, SIZE_LARGE);
        }

        super.initEntity();

        if (getBehaviorGroup() != null) {
            getMemoryStorage().put(CoreMemoryTypes.VARIANT, this.getNbt().getInt(TAG_SIZE));
        }

        this.growthTicks = this.nbt.getInt(TAG_GROWTH_TICKS);
        this.growthLocked = this.nbt.getBoolean(TAG_GROWTH_LOCKED);
        this.fuse = this.nbt.contains(TAG_FUSE) ? this.nbt.getInt(TAG_FUSE) : -1;
        this.pickupTimer = this.nbt.getInt(TAG_PICKUP_TIMER);

        applyArchetypeProperty();

        if (this.fuse >= 0) {
            this.setDataFlag(ActorFlags.IGNITED, true);
        }

        applyVariantScale();
    }

    private void applyVariantScale() {
        this.setScale(getVariant() == SIZE_LARGE ? 1f : 0.5f);
        recalculateBoundingBox();
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
    public int getVariant() {
        if (getBehaviorGroup() != null) {
            Integer variant = getMemoryStorage().get(CoreMemoryTypes.VARIANT);
            if (variant != null) return variant;
        }

        if (this.nbt.contains(TAG_SIZE)) {
            return this.getNbt().getInt(TAG_SIZE);
        }

        return SIZE_LARGE;
    }

    @Override
    public void setVariant(int variant) {
        this.nbt.putInt(TAG_SIZE, variant);

        if (getBehaviorGroup() != null) {
            getMemoryStorage().put(CoreMemoryTypes.VARIANT, variant);
        }

        applyVariantScale();
    }

    @Override
    public boolean hasVariant() {
        if (getBehaviorGroup() != null && getMemoryStorage().notEmpty(CoreMemoryTypes.VARIANT)) {
            return true;
        }

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

    @Override
    public float getWidth() {
        if (getBehaviorGroup() == null) return 0;
        return 0.98f;
    }

    @Override
    public float getHeight() {
        if (getBehaviorGroup() == null) return 0;
        return 0.98f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(getVariant() == SIZE_LARGE ? 8 : 4);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(getVariant() == SIZE_LARGE ? 0.4f : 0.3f);
    }

    @Override
    public double getFloatingForceFactor() {
        Archetype archetype = getArchetype();
        if (archetype != null && archetype.floats()) {
            return super.getFloatingForceFactor();
        }
        return 0;
    }

    @Override
    public String getOriginalName() {
        return "Sulfur Cube";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("sulfur_cube", "mob");
    }

    public boolean hasAbsorbedBlock() {
        return this.nbt.contains(TAG_ABSORBED_BLOCK);
    }

    @Nullable
    public String getAbsorbedBlock() {
        return hasAbsorbedBlock() ? this.nbt.getString(TAG_ABSORBED_BLOCK) : null;
    }

    @Nullable
    public Archetype getArchetype() {
        return Archetype.fromBlockId(getAbsorbedBlock());
    }

    public void setAbsorbedBlock(@Nullable String blockId) {
        if (blockId == null) {
            this.nbt.remove(TAG_ABSORBED_BLOCK);
        } else {
            this.nbt.putString(TAG_ABSORBED_BLOCK, blockId);
        }
        applyArchetypeProperty();
    }

    private void applyArchetypeProperty() {
        Archetype archetype = getArchetype();
        setEnumEntityProperty(PROPERTIES[0].getIdentifier(), archetype == null ? "none" : archetype.getPropertyName());
        sendData(this.getViewers().values().toArray(Player[]::new));
        sendAbsorbedBlockEquipment(this.getViewers().values().toArray(Player[]::new));
    }

    private void sendAbsorbedBlockEquipment(Player... targets) {
        if (targets.length == 0) return;
        String absorbed = getAbsorbedBlock();
        Item bodyItem = absorbed == null ? Item.AIR : Item.get(absorbed);

        MobArmorEquipmentPacket packet = new MobArmorEquipmentPacket();
        packet.setTargetRuntimeID(this.getId());
        packet.setHead(Item.AIR.toNetwork());
        packet.setTorso(Item.AIR.toNetwork());
        packet.setLegs(Item.AIR.toNetwork());
        packet.setFeet(Item.AIR.toNetwork());
        packet.setBody(bodyItem.toNetwork());
        for (Player target : targets) {
            target.sendPacket(packet);
        }
    }

    @Override
    public void spawnTo(Player player) {
        super.spawnTo(player);
        if (hasAbsorbedBlock()) {
            sendAbsorbedBlockEquipment(player);
        }
    }

    @Override
    public void setRotation(double yaw, double pitch) {
        if (hasAbsorbedBlock()) {
            super.setRotation(0, 0);
            return;
        }
        super.setRotation(yaw, pitch);
    }

    @Override
    public void setRotation(double yaw, double pitch, double headYaw) {
        if (hasAbsorbedBlock()) {
            super.setRotation(0, 0, 0);
            return;
        }
        super.setRotation(yaw, pitch, headYaw);
    }

    public boolean isFromBucket() {
        return this.nbt.getBoolean(TAG_FROM_BUCKET);
    }

    public void setFromBucket(boolean fromBucket) {
        this.nbt.putBoolean(TAG_FROM_BUCKET, fromBucket);
    }

    public void ignite(int fuseTicks) {
        if (getArchetype() != Archetype.EXPLOSIVE || this.fuse >= 0) return;
        this.fuse = fuseTicks;
        this.setDataFlag(ActorFlags.IGNITED, true);
    }

    public boolean isIgnited() {
        return this.fuse >= 0;
    }

    private void explode() {
        this.exploding = true;
        EntityExplosionPrimeEvent event = new EntityExplosionPrimeEvent(this, 4);
        this.server.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            this.exploding = false;
            this.fuse = -1;
            this.setDataFlag(ActorFlags.IGNITED, false);
            return;
        }

        setAbsorbedBlock(null);
        Explosion explosion = new Explosion(this, event.getForce(), this);
        if (event.isBlockBreaking() && this.level.getGameRules().getBoolean(GameRule.MOB_GRIEFING)) {
            explosion.explodeA();
        }
        explosion.explodeB();
        this.close();
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (isAlive() && !this.closed) {
            if (this.kickCooldown > 0) {
                this.kickCooldown--;
            }

            if (this.pickupTimer > 0) {
                this.pickupTimer--;
            }

            checkPlayerKick();

            if (hasAbsorbedBlock()) {
                if (hasMoveDirection()) {
                    setMoveTarget(null);
                    setMoveDirectionStart(null);
                    setMoveDirectionEnd(null);
                }
                if (this.yaw != 0 || this.pitch != 0 || this.headYaw != 0) {
                    this.setRotation(0, 0, 0);
                }
            }

            if (this.fuse >= 0) {
                if (this.fuse == 0) {
                    explode();
                    return true;
                }
                this.fuse--;
            }

            if (getVariant() == SIZE_SMALL && !this.growthLocked && !isImmobile()) {
                this.growthTicks++;
                if (this.growthTicks >= GROWTH_TIME_TICKS) {
                    grow();
                }
            }

            if (currentTick % 10 == 0 && getArchetype() == Archetype.HOT) {
                for (Entity entity : this.level.getNearbyEntitiesSafe(getBoundingBox().grow(0.2, 0.1, 0.2), this)) {
                    if (entity instanceof EntityLiving living && !(entity instanceof EntitySulfurCube)) {
                        living.attack(new EntityDamageByEntityEvent(this, living, EntityDamageEvent.DamageCause.CONTACT, 1f));
                    }
                }
            }

            if (currentTick % 20 == 0 && canSeekBlocks()
                    && this.level.getGameRules().getBoolean(GameRule.MOB_GRIEFING)) {
                absorbNearbyBlockItem();
            }

            Archetype archetype = getArchetype();
            if (archetype != null) {
                if (archetype.bounciness > 0f && this.onGround && !this.wasOnGround && this.lastFallSpeed < -0.3) {
                    double bounce = Math.min(0.5, -this.lastFallSpeed * archetype.bounciness);
                    if (bounce >= 0.2) {
                        this.motionY = bounce;
                    }
                }

                double horizontalSpeed = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
                if (horizontalSpeed > 0.85) {
                    double clamp = 0.85 / horizontalSpeed;
                    this.motionX *= clamp;
                    this.motionZ *= clamp;
                    horizontalSpeed = 0.85;
                }
                if (horizontalSpeed > 0.02) {
                    double factor = this.onGround
                            ? Math.max(0, 1 - archetype.friction * 0.4)
                            : Math.max(0, 1 - archetype.airDrag * 0.05);
                    this.motionX *= factor;
                    this.motionZ *= factor;
                }
            }
            this.wasOnGround = this.onGround;
            this.lastFallSpeed = this.motionY;
        }

        return super.onUpdate(currentTick);
    }

    private void absorbNearbyBlockItem() {
        for (Entity entity : this.level.getNearbyEntitiesSafe(getBoundingBox().grow(1, 0.5, 1), this)) {
            if (!(entity instanceof EntityItem itemEntity) || !itemEntity.isAlive()) continue;
            Item drop = itemEntity.getItem();
            if (drop == null || !drop.isBlock() || Archetype.fromBlockId(drop.getId()) == null) continue;

            setAbsorbedBlock(drop.getId());
            itemEntity.close();
            if (drop.getCount() > 1) {
                Item remainder = drop.clone();
                remainder.setCount(drop.getCount() - 1);
                this.level.dropItem(itemEntity, remainder);
            }
            return;
        }
    }

    private void grow() {
        this.growthTicks = 0;
        setVariant(SIZE_LARGE);
        setMaxHealth(8);
        setHealth(getMaxHealth());
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (hasAbsorbedBlock() && item.isNull() && this.fuse < 0) {
            launchFrom(player, 0.5);
            return true;
        }

        if (getVariant() == SIZE_LARGE) {
            if (Objects.equals(item.getId(), Item.BUCKET) && this.fuse < 0) {
                Item bucket = Item.get(Item.SULFUR_CUBE_BUCKET);
                CompoundTag tag = new CompoundTag();
                tag.putInt("Variant", SIZE_LARGE);
                String absorbed = getAbsorbedBlock();
                if (absorbed != null) {
                    tag.putString(TAG_ABSORBED_BLOCK, absorbed);
                }
                bucket.setNbt(tag);
                player.getInventory().setItemInMainHand(bucket);
                this.close();
                return true;
            }

            if (Objects.equals(item.getId(), Item.SHEARS) && hasAbsorbedBlock() && this.fuse < 0) {
                ejectBlock();
                return true;
            }

            if ((Objects.equals(item.getId(), Item.FLINT_AND_STEEL) || Objects.equals(item.getId(), Item.FIRE_CHARGE))
                    && getArchetype() == Archetype.EXPLOSIVE && this.fuse < 0) {
                ignite(MANUAL_FUSE_TICKS);
                if (!player.isCreative()) {
                    if (Objects.equals(item.getId(), Item.FIRE_CHARGE)) {
                        item.count--;
                    } else {
                        item.setDamage(item.getDamage() + 1);
                    }
                    player.getInventory().setItemInMainHand(item);
                }
                return true;
            }

            if (item.isBlock() && this.fuse < 0 && Archetype.fromBlockId(item.getId()) != null) {
                String previous = getAbsorbedBlock();
                if (previous != null) {
                    this.level.dropItem(this.add(0, getHeight() / 2, 0), Item.get(previous));
                }
                setAbsorbedBlock(item.getId());
                if (!player.isCreative()) {
                    item.count--;
                    player.getInventory().setItemInMainHand(item);
                }
                return true;
            }
        } else {
            if (Objects.equals(item.getId(), Item.SLIME_BALL) && !this.growthLocked) {
                this.growthTicks += SLIME_BALL_GROWTH_BONUS;
                if (!player.isCreative()) {
                    item.count--;
                    player.getInventory().setItemInMainHand(item);
                }
                return true;
            }

            if (Objects.equals(item.getId(), BlockID.GOLDEN_DANDELION) && !this.growthLocked) {
                this.growthLocked = true;
                if (!player.isCreative()) {
                    item.count--;
                    player.getInventory().setItemInMainHand(item);
                }
                return true;
            }
        }

        return super.onInteract(player, item, clickedPos);
    }

    public void ejectBlock() {
        String absorbed = getAbsorbedBlock();
        if (absorbed == null) return;
        this.level.dropItem(this.add(0, getHeight() / 2, 0), Item.get(absorbed));
        setAbsorbedBlock(null);
        this.pickupTimer = 100;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (getArchetype() == Archetype.EXPLOSIVE && this.fuse < 0) {
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

        if (hasAbsorbedBlock() && !isBypassingAbsorption(source)) {
            source.setCancelled(true);
            if (source instanceof EntityDamageByEntityEvent event && event.getDamager() != null) {
                launch(event.getDamager(), source.getDamage() + event.getKnockBack() * 4);
            }
            return false;
        }

        return super.attack(source);
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
            if (damager instanceof EntityWarden || damager instanceof EntityZoglin || damager instanceof EntityWither) {
                return true;
            }
            if (damager instanceof EntityWolf wolf && wolf.isTamed()) {
                return true;
            }
        }

        return false;
    }

    private void launch(Entity damager, float damage) {
        launchFrom(damager, 0.25 + Math.min(damage, 12) * 0.06);
    }

    private void launchFrom(Entity source, double baseStrength) {
        Archetype archetype = getArchetype();
        if (archetype == null) return;

        double dx = this.x - source.x;
        double dz = this.z - source.z;
        double distance = Math.sqrt(dx * dx + dz * dz);
        if (distance < 1e-4) return;

        double strength = baseStrength * archetype.launchMultiplier();
        this.setMotion(new Vector3(
                dx / distance * strength,
                Math.min(0.35, archetype.verticalKick * (1 + strength)),
                dz / distance * strength
        ));
        this.kickCooldown = 10;
    }

    private void checkPlayerKick() {
        if (!hasAbsorbedBlock() || this.kickCooldown > 0 || this.fuse >= 0) return;

        for (Entity entity : this.level.getNearbyEntitiesSafe(getBoundingBox().grow(0.35, 0.25, 0.35), this)) {
            if (!(entity instanceof Player player) || !player.isAlive() || player.isSpectator()) continue;

            double dx = player.x - player.lastX;
            double dz = player.z - player.lastZ;
            double speed = Math.sqrt(dx * dx + dz * dz);

            Archetype archetype = getArchetype();
            if (archetype == null) return;

            double strength = Math.max(0.3, Math.min(speed * 3.5, 1.0)) * archetype.launchMultiplier();
            double dirX;
            double dirZ;
            if (speed > 0.05) {
                dirX = dx / speed;
                dirZ = dz / speed;
            } else {
                double px = this.x - player.x;
                double pz = this.z - player.z;
                double dist = Math.sqrt(px * px + pz * pz);
                if (dist < 1e-4) return;
                dirX = px / dist;
                dirZ = pz / dist;
            }

            this.setMotion(new Vector3(
                    dirX * strength,
                    Math.min(0.35, archetype.verticalKick * (1 + strength)),
                    dirZ * strength
            ));
            this.kickCooldown = 10;
            return;
        }
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        String absorbed = getAbsorbedBlock();
        if (getVariant() == SIZE_LARGE && absorbed != null && !this.exploding) {
            return new Item[]{Item.get(absorbed)};
        }
        return Item.EMPTY_ARRAY;
    }

    @Override
    public Integer getExperienceDrops() {
        if (this.exploding || getVariant() != SIZE_LARGE) {
            return 0;
        }
        return Utils.rand(1, 2);
    }

    @Override
    public void kill() {
        if (!this.isAlive()) {
            return;
        }

        if (getVariant() == SIZE_LARGE && !this.exploding) {
            CompoundTag childNbt = this.getNbt().copy();
            childNbt.remove(TAG_ABSORBED_BLOCK);
            childNbt.remove(TAG_FUSE);
            childNbt.remove(TAG_FROM_BUCKET);
            childNbt.remove("Scale");
            childNbt.remove("Variant");
            childNbt.putInt(TAG_GROWTH_TICKS, 0);
            childNbt.putInt(TAG_SIZE, SIZE_SMALL);
            for (int i = 0; i < 2; i++) {
                EntitySulfurCube cube = new EntitySulfurCube(this.getChunk(), childNbt.copy());
                double angle = Math.toRadians(Utils.rand(0, 359));
                cube.setPosition(this.add(Math.cos(angle) * 0.4, 0.1, Math.sin(angle) * 0.4));
                cube.setRotation(this.yaw, this.pitch);
                cube.setVariant(SIZE_SMALL);
                cube.spawnToAll();
                cube.setMotion(new Vector3(Math.cos(angle) * 0.25, 0.3, Math.sin(angle) * 0.25));
            }
        }
        super.kill();
    }
}
