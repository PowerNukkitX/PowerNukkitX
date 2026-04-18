package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBubbleColumn;
import cn.nukkit.block.BlockEndPortal;
import cn.nukkit.block.BlockFence;
import cn.nukkit.block.BlockFire;
import cn.nukkit.block.BlockFlowingLava;
import cn.nukkit.block.BlockFlowingWater;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockTurtleEgg;
import cn.nukkit.blockentity.BlockEntityPistonArm;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.components.*;
import cn.nukkit.entity.custom.CustomEntity;
import cn.nukkit.entity.custom.CustomEntityComponents;
import cn.nukkit.entity.custom.CustomEntityDefinition;
import cn.nukkit.entity.data.property.BooleanEntityProperty;
import cn.nukkit.entity.data.property.EntityProperty;
import cn.nukkit.entity.data.property.EnumEntityProperty;
import cn.nukkit.entity.data.property.FloatEntityProperty;
import cn.nukkit.entity.data.property.IntEntityProperty;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.entity.item.EntityArmorStand;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.item.EntityVehicle;
import cn.nukkit.entity.mob.EntityBoss;
import cn.nukkit.entity.mob.EntityEnderDragon;
import cn.nukkit.entity.passive.EntityNpc;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.Event;
import cn.nukkit.event.block.FarmLandDecayEvent;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityPortalEnterEvent.PortalType;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTotemOfUndying;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.particle.ExplodeParticle;
import cn.nukkit.level.particle.HappyVillagerParticle;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.MathHelper;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.metadata.Metadatable;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.registry.EntityRegistry;
import cn.nukkit.registry.Registries;
import cn.nukkit.scheduler.Task;
import cn.nukkit.tags.ItemTags;
import cn.nukkit.utils.ChunkException;
import cn.nukkit.utils.Identifier;
import cn.nukkit.utils.PortalHelper;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.utils.Utils;
import com.google.common.collect.Iterables;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.data.ActorLinkType;
import org.cloudburstmc.protocol.bedrock.data.ActorSwingSource;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataMap;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataType;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorEvent;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorLink;
import org.cloudburstmc.protocol.bedrock.data.actor.MoveActorAbsoluteData;
import org.cloudburstmc.protocol.bedrock.data.actor.PropertySyncData;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.random.RandomGenerator;

/**
 * @author MagicDroidX
 */
@Slf4j
public abstract class Entity extends Location implements Metadatable, EntityID {
    public static final Entity[] EMPTY_ARRAY = new Entity[0];
    protected final ActorDataMap entityDataMap = new ActorDataMap();
    public static AtomicLong entityCount = new AtomicLong(1);
    public final List<Entity> passengers = new ArrayList<>();
    public final AxisAlignedBB offsetBoundingBox = new SimpleAxisAlignedBB(0, 0, 0, 0, 0, 0);
    protected final Map<Integer, Player> hasSpawned = new ConcurrentHashMap<>();
    protected final Map<EffectType, Effect> effects = new ConcurrentHashMap<>();
    /**
     * Who is this entity riding on
     */
    public static final String NBT_RIDING_UUID = "RidingUUID";
    public final float SEATED_FACTOR = 0.75308642f;
    public Entity riding = null;
    private int passengerCount = 0;
    protected int restoreMountTries = 0;
    protected Vector3f seatRawOffset;

    /**
     * The entity is this targeting to
     */
    public Entity targetEntity = null;

    /**
     * Tolerance factor to compute onGround state
     */
    private static final double GROUND_PROBE_DEPTH = 0.06d; // ~1/16 block
    private static final double GROUND_EPS = 1.0e-4;        // tolerant eps for BB rounding

    public final static float DEFAULT_SPEED = 0.1f;
    public final static float DEFAULT_FLYING_SPEED = 0.03f;
    public final static float DEFAULT_UNDER_WATER_SPEED = 0.07f;
    public final static float DEFAULT_LAVA_MOVEMENT_SPEED = 0.05f;
    protected float movementSpeed = (this instanceof EntityLiving) ? DEFAULT_SPEED : 0;
    protected float flyingSpeed = (this instanceof EntityLiving) ? DEFAULT_FLYING_SPEED : 0;
    protected float lavaMovementSpeed = (this instanceof EntityLiving) ? DEFAULT_LAVA_MOVEMENT_SPEED : 0;

    protected static final String NBT_SOUND_VARIANT = "sound_variant";

    public final static int DEFAULT_HEALTH = 20;

    public IChunk chunk;
    public List<Block> blocksAround = new ArrayList<>();
    public List<Block> collisionBlocks = new ArrayList<>();
    public List<Block> stepOnBlocks = new ArrayList<>();
    protected Set<Vector3> lastStepOnBlocks = new HashSet<>();
    public double lastX;
    public double lastY;
    public double lastZ;
    public boolean firstMove = true;
    public double motionX;
    public double motionY;
    public double motionZ;
    public int deadTicks = 0;
    /**
     * temporalVector，its value has no meaning
     */
    public Vector3 temporalVector;
    public double lastMotionX;
    public double lastMotionY;
    public double lastMotionZ;
    public double lastPitch;
    public double lastYaw;
    public double lastHeadYaw;
    public double pitchDelta;
    public double yawDelta;
    public double headYawDelta;
    public double entityCollisionReduction = 0; // Higher than 0.9 will result a fast collisions
    public AxisAlignedBB boundingBox;
    public boolean onGround;
    public boolean positionChanged;
    public boolean motionChanged;
    public float fallDistance = 0;
    public int ticksLived = 0;
    public int lastUpdate;
    public int fireTicks = 0;
    public int inPortalTicks = 0;
    public int freezingTicks = 0;//0 - 140
    public float scale = 1;
    public NbtMap namedTag;
    public boolean isCollided = false;
    public boolean isCollidedHorizontally = false;
    public boolean isCollidedVertically = false;
    public int noDamageTicks;
    public boolean justCreated;
    public boolean fireProof;
    public boolean invulnerable;
    public boolean despawnable;
    protected int lastPlayerNearbyTick = 0;
    public double highestPosition;
    public boolean closed = false;
    public boolean noClip = false;
    /**
     * spawned by server
     * <p>
     * player's UUID is sent by client,so this value cannot be used in Player
     */
    protected UUID entityUniqueId;
    /**
     * runtime id (changed after you restart the server)
     */
    protected volatile long id;
    protected EntityDamageEvent lastDamageCause = null;
    protected int age = 0;
    protected float health = 20;
    protected float absorption = 0;
    /**
     * Player do not use
     */
    protected float ySize = 0;
    protected boolean inEndPortal;
    protected Server server;
    protected boolean isPlayer = this instanceof Player;
    private int maxHealth = 20;
    protected String name;
    private volatile boolean initialized;
    protected volatile boolean saveWithChunk = true;
    private final Map<String, Integer> intProperties = new LinkedHashMap<>();
    private final Map<String, Float> floatProperties = new LinkedHashMap<>();
    protected final Map<Integer, Attribute> attributes = new HashMap<>();

    protected static final int DEFAULT_SOFT_DESPAWN_DISTANCE = 74;
    protected static final int DEFAULT_HARD_DESPAWN_DISTANCE = 128;
    protected static final int DEFAULT_SOFT_DESPAWN_GRACE_TICKS = 20 * 45;

    /**
     * Entity growth
     */
    public static final String TAG_ENTITY_BIRTH_DATE = "entity_birth_date";
    public static final String TAG_ENTITY_GROW_LEFT = "entity_grow_left";
    public static final String TAG_ENTITY_GROW_PAUSED = "entity_grow_paused";
    public static final String TAG_ENTITY_GROW_LAST_SYNC = "entity_grow_last_sync";
    protected int ticksGrowLeft = -1;
    protected boolean growDirty = false;

    /**
     * Nameable Entity
     */
    protected static final NameableComponent DEFAULT_NAMEABLE = new NameableComponent(true, false);
    protected static final NameableComponent DEFAULT_NOT_NAMEABLE = new NameableComponent(false, false);

    /**
     * Entity Boostable ticks
     */
    protected int boostableTicks = -1;

    private String idConvertToName() {
        var path = getIdentifier().split(":")[1];
        StringBuilder result = new StringBuilder();
        String[] parts = path.split("_");
        for (String part : parts) {
            if (!part.isEmpty()) {
                result.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(" ");
            }
        }
        this.name = result.toString().trim().intern();
        return this.name;
    }

    public Entity(IChunk chunk, NbtMap nbt) {
        initEntityProperties(this.getIdentifier());
        if (!(this instanceof Player)) {
            this.init(chunk, nbt);
        }
    }

    /**
     * Create an entity from the entity identifier, like (minecraft:sheep)
     *
     * @param identifier the identifier
     * @param pos        the pos
     * @param args       the args
     * @return the entity
     */
    @Nullable
    public static Entity createEntity(Identifier identifier, @NotNull Position pos, @Nullable Object... args) {
        return createEntity(identifier.toString(), Objects.requireNonNull(pos.getChunk()), getDefaultNBT(pos), args);
    }

    /**
     * Create an entity from entity name, name from {@link Entity#init registerEntities} source code query
     *
     * @param name the name
     * @param pos  the pos
     * @param args the args
     * @return the entity
     */
    @Nullable
    public static Entity createEntity(@NotNull String name, @NotNull Position pos, @Nullable Object... args) {
        return createEntity(name, Objects.requireNonNull(pos.getChunk()), getDefaultNBT(pos), args);
    }

    /**
     * Create an entity from the network id
     *
     * @param type 网络ID<br> network id
     * @param pos  the pos
     * @param args the args
     * @return the entity
     */
    @Nullable
    public static Entity createEntity(int type, @NotNull Position pos, @Nullable Object... args) {
        String entityIdentifier = Registries.ENTITY.getEntityIdentifier(type);
        if (entityIdentifier == null) return null;
        return createEntity(entityIdentifier, Objects.requireNonNull(pos.getChunk()), getDefaultNBT(pos), args);
    }

    /**
     * Create an entity from the network id
     *
     * @param type  网络ID<br> network id
     * @param chunk the chunk
     * @param nbt   the nbt
     * @param args  the args
     * @return the entity
     */
    @Nullable
    public static Entity createEntity(int type, @NotNull IChunk chunk, @NotNull NbtMap nbt, @Nullable Object... args) {
        String entityIdentifier = Registries.ENTITY.getEntityIdentifier(type);
        if (entityIdentifier == null) return null;
        return Registries.ENTITY.provideEntity(entityIdentifier, chunk, nbt, args);
    }

    @Nullable
    public static Entity createEntity(@NotNull String identifier, @NotNull IChunk chunk, @NotNull NbtMap nbt, @Nullable Object... args) {
        Identifier.assertValid(identifier);
        return Registries.ENTITY.provideEntity(identifier, chunk, nbt, args);
    }

    /**
     * Get the identifier of the specified network id entity
     *
     * @return the identifier
     */
    @Nullable
    public static Identifier getIdentifier(int networkID) {
        String entityIdentifier = Registries.ENTITY.getEntityIdentifier(networkID);
        if (entityIdentifier == null) return null;
        return new Identifier(entityIdentifier);
    }

    /**
     * @see #getDefaultNBT(Vector3, Vector3, float, float)
     */
    @NotNull
    public static NbtMap getDefaultNBT(@NotNull Vector3 pos) {
        return getDefaultNBT(pos, null);
    }

    @NotNull
    public static NbtMap getDefaultNBT(@NotNull Vector3 pos, @Nullable Vector3 motion) {
        Location loc = pos instanceof Location ? (Location) pos : null;

        if (loc != null) {
            return getDefaultNBT(pos, motion, (float) loc.getYaw(), (float) loc.getPitch());
        }

        return getDefaultNBT(pos, motion, 0, 0);
    }

    /**
     * Get the default NBT of the entity, with information such as position, motion, yaw pitch, etc.
     *
     * @param pos    the pos
     * @param motion the motion
     * @param yaw    the yaw
     * @param pitch  the pitch
     * @return the default nbt
     */
    @NotNull
    public static NbtMap getDefaultNBT(@NotNull Vector3 pos, @Nullable Vector3 motion, float yaw, float pitch) {
        return NbtMap.builder()
                .putList("Pos", NbtType.DOUBLE, Arrays.asList(pos.x, pos.y, pos.z))
                .putList("Motion", NbtType.DOUBLE, Arrays.asList(
                        motion != null ? motion.x : 0,
                        motion != null ? motion.y : 0,
                        motion != null ? motion.z : 0
                ))
                .putList("Rotation", NbtType.FLOAT, Arrays.asList(yaw, pitch))
                .build();
    }

    /**
     * Batch play animation on entity groups<br/>
     * This method is recommended if you need to play the same animation on a large number of entities at the same time,
     * as it only sends packets once for each player, which greatly reduces bandwidth pressure
     *
     * @param animation Animation objects
     * @param entities  Group of entities that need to play animations
     * @param players   Visible Player
     */
    public static void playAnimationOnEntities(EntityAnimation animation, Collection<Entity> entities, Collection<Player> players) {
        var pk = animation.toNetwork();
        entities.forEach(entity -> pk.getRuntimeIds().add(entity.getId()));
        Server.broadcastPacket(players, pk);
    }

    /**
     * @see #playAnimationOnEntities(EntityAnimation, Collection, Collection)
     */
    public static void playAnimationOnEntities(EntityAnimation animation, Collection<Entity> entities) {
        var viewers = new HashSet<Player>();
        entities.forEach(entity -> {
            viewers.addAll(entity.getViewers().values());
            if (entity.isPlayer) viewers.add((Player) entity);
        });
        playAnimationOnEntities(animation, entities, viewers);
    }

    /**
     * Get the identifier of the entity
     *
     * @return the identifier
     */
    @NotNull
    public abstract String getIdentifier();

    public float getCurrentHeight() {
        if (isSwimming()) {
            return getSwimmingHeight();
        } else if (isSneaking()) {
            return getSneakingHeight();
        } else if (isCrawling()) {
            return getCrawlingHeight();
        } else if (this.riding != null) {
            return getSeatingHeight();
        } else {
            return getHeight();
        }
    }

    public float getEyeHeight() {
        return getCurrentHeight() * 0.90f;
    }

    /**
     * Entity Width
     *
     * @return the width
     */
    public float getWidth() {
        if (!hasCollision()) return 0f;
        if (isCustomEntity()) {
            return meta().getCollisionBox(CustomEntityComponents.COLLISION_BOX).width();
        }
        return 0;
    }

    /**
     * Entity Default Height
     *
     * @return the default height
     */
    public float getHeight() {
        if (!hasCollision()) return 0f;
        if (isCustomEntity()) {
            return meta().getCollisionBox(CustomEntityComponents.COLLISION_BOX).height();
        }
        return 0;
    }

    public float getKnockbackResistance() {
        if (isCustomEntity()) {
            return meta().getFloat(CustomEntityComponents.KNOCKBACK_RESISTANCE, 0f);
        }
        return 0f;
    }

    public float getLength() {
        return 0;
    }

    protected double getStepHeight() {
        return 0;
    }

    protected double getStepHeightControlled() {
        return 0;
    }

    protected double getStepHeightJumpPrevented() {
        return 0;
    }

    public boolean canCollide() {
        return true;
    }

    protected float getGravity() {
        return 0;
    }

    protected float getDrag() {
        return 0;
    }

    protected float getBaseOffset() {
        return 0;
    }

    public int getFrostbiteInjury() {
        return 1;
    }

    public boolean isPersistent() {
        return !this.despawnable;
    }

    public void setPersistent(boolean persistent) {
        this.despawnable = !persistent;
        this.namedTag = namedTag.toBuilder().putBoolean("Persistent", persistent).build();
    }

    public boolean isInvulnerable() {
        return invulnerable;
    }

    public void setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
        this.namedTag = this.namedTag.toBuilder().putBoolean("Invulnerable", invulnerable).build();
    }

    /**
     * Entity initialization order, first initialize the Entity class field->Entity constructor->Enter the init method->Call the init Entity method-> subclass field initialization-> subclass constructor
     * <p>
     * The method used to initialize the NBT and entity fields of the entity
     */
    protected void initEntity() {
        // =========================================================
        // Load or generate UUID for non-player entities
        // =========================================================
        if (!(this instanceof Player)) {
            if (this.namedTag.containsKey("uuid")) {
                this.entityUniqueId = UUID.fromString(this.namedTag.getString("uuid"));
            } else {
                this.entityUniqueId = UUID.randomUUID();
            }
        }

        // =========================================================
        // Initialize entity data defaults first
        // =========================================================
        this.entityDataMap.getOrCreateFlags();
        this.entityDataMap.put(ActorDataTypes.AIR_SUPPLY, this.namedTag.getShort("Air"));
        this.entityDataMap.put(ActorDataTypes.AIR_SUPPLY_MAX, (short) 400);
        this.entityDataMap.put(ActorDataTypes.NAME, "");
        this.entityDataMap.put(ActorDataTypes.LEASH_HOLDER, -1L);
        this.entityDataMap.put(ActorDataTypes.SCALE, 1f);
        this.entityDataMap.put(ActorDataTypes.HEIGHT, this.getHeight());
        this.entityDataMap.put(ActorDataTypes.WIDTH, this.getWidth());
        this.entityDataMap.put(ActorDataTypes.STRUCTURAL_INTEGRITY, (int) this.getHealthCurrent());

        // =========================================================
        // Load Effects from NBT
        // =========================================================
        if (this.namedTag.containsKey("ActiveEffects")) {
            List<NbtMap> effects = this.namedTag.getList("ActiveEffects", NbtType.COMPOUND);
            for (NbtMap e : effects) {
                Effect effect = Effect.get(e.getByte("Id"));
                if (effect == null) continue;

                effect.setAmplifier(e.getByte("Amplifier"))
                        .setDuration(e.getInt("Duration"))
                        .setVisible(e.getBoolean("ShowParticles"));

                this.addEffect(effect);
            }
        }

        // =========================================================
        // Load Custom name from NBT
        // =========================================================
        if (this.namedTag.containsKey("CustomName")) {
            String name = this.namedTag.getString("CustomName");
            if (name != null) {
                this.setNameTag(name);
            }
            if (this.namedTag.containsKey("CustomNameVisible")) {
                this.setNameTagVisible(this.namedTag.getBoolean("CustomNameVisible"));
            }
            if (this.namedTag.containsKey("CustomNameAlwaysVisible")) {
                this.setNameTagAlwaysVisible(this.namedTag.getBoolean("CustomNameAlwaysVisible"));
            }
        }

        // =========================================================
        // Load Attributes from NBT
        // =========================================================
        if (this.namedTag.containsKey("Attributes")) {
            List<NbtMap> attributes = this.namedTag.getList("Attributes", NbtType.COMPOUND);
            for (var nbt : attributes) {
                Attribute attribute = Attribute.fromNBT(nbt);
                this.attributes.put(attribute.getId(), attribute);
            }
        }
        this.applyInitialHealth();
        this.applyInitialRideJumpStrength();
        this.applyInitialMovementSpeed();

        // =========================================================
        // Send initial data + default flags
        // =========================================================
        this.sendData(this.hasSpawned.values().toArray(Player.EMPTY_ARRAY), entityDataMap);
        if (this.isFireImmune()) {
            this.setFireImmune(true);
        }
        this.setDataFlags(EnumSet.of(
                ActorFlags.CAN_WALK,
                ActorFlags.CAN_CLIMB,
                ActorFlags.BREATHING,
                ActorFlags.HAS_COLLISION,
                ActorFlags.HAS_GRAVITY
        ));

        this.applyInitialInputControlFlags();
        this.applyInitialPowerJumpFlags();
    }

    protected final void init(IChunk chunk, NbtMap nbt) {
        if ((chunk == null || chunk.getProvider() == null)) {
            throw new ChunkException("Invalid garbage Chunk given to Entity");
        }
        this.id = Entity.entityCount.getAndIncrement();
        this.isPlayer = this instanceof Player;
        this.temporalVector = new Vector3();
        this.justCreated = true;
        this.namedTag = nbt;

        // Restore entity properties from NBT (overwriting defaults)
        if (this.namedTag.containsKey("IntProperties")) {
            NbtMap intProps = this.namedTag.getCompound("IntProperties");
            for (Map.Entry<String, Object> entry : intProps.entrySet()) {
                String key = entry.getKey();
                if (entry.getValue() instanceof Number number) {
                    this.intProperties.put(key, number.intValue());
                }
            }
        }

        if (this.namedTag.containsKey("FloatProperties")) {
            NbtMap floatProps = this.namedTag.getCompound("FloatProperties");
            for (Map.Entry<String, Object> entry : floatProps.entrySet()) {
                String key = entry.getKey();
                if (entry.getValue() instanceof Number number) {
                    this.floatProperties.put(key, number.floatValue());
                }
            }
        }

        this.chunk = chunk;
        this.setLevel(chunk.getProvider().getLevel());
        this.server = chunk.getProvider().getLevel().getServer();
        this.boundingBox = new SimpleAxisAlignedBB(0, 0, 0, 0, 0, 0);

        List<Double> posList = this.namedTag.getList("Pos", NbtType.DOUBLE);
        List<Float> rotationList = this.namedTag.getList("Rotation", NbtType.FLOAT);
        List<Double> motionList = this.namedTag.getList("Motion", NbtType.DOUBLE);
        this.setPositionAndRotation(
                this.temporalVector.setComponents(
                        posList.get(0),
                        posList.get(1),
                        posList.get(2)
                ),
                rotationList.get(0),
                rotationList.get(1)
        );

        this.setMotion(this.temporalVector.setComponents(
                motionList.get(0),
                motionList.get(1),
                motionList.get(2)
        ));

        if (!this.namedTag.containsKey("FallDistance")) {
            this.namedTag = this.namedTag.toBuilder().putFloat("FallDistance", 0).build();
        }
        this.fallDistance = this.namedTag.getFloat("FallDistance");
        this.highestPosition = this.y + this.namedTag.getFloat("FallDistance");

        if (!this.namedTag.containsKey("Fire") || this.namedTag.getShort("Fire") > 32767) {
            this.namedTag = this.namedTag.toBuilder().putShort("Fire", (short) 0).build();
        }
        this.fireTicks = this.namedTag.getShort("Fire");

        if (!this.namedTag.containsKey("Air")) {
            this.namedTag = this.namedTag.toBuilder().putShort("Air", (short) 300).build();
        }
        if (!this.namedTag.containsKey("OnGround")) {
            this.namedTag = this.namedTag.toBuilder().putBoolean("OnGround", false).build();
        }
        this.onGround = this.namedTag.getBoolean("OnGround");

        if (!this.namedTag.containsKey("Invulnerable")) {
            this.namedTag = this.namedTag.toBuilder().putBoolean("Invulnerable", false).build();
        }
        this.invulnerable = this.namedTag.getBoolean("Invulnerable");

        if (!this.namedTag.containsKey("Scale")) {
            this.namedTag = this.namedTag.toBuilder().putFloat("Scale", 1).build();
        }
        this.scale = this.namedTag.getFloat("Scale");
        if (!this.namedTag.containsKey("Despawnable")) {
            boolean persistent =
                    (isCustomEntity() && meta().getBoolean(CustomEntityComponents.PERSISTENT, false)) ||
                            this.namedTag.getBoolean("Persistent");

            this.namedTag = this.namedTag.toBuilder().putBoolean("Despawnable", !persistent).build();
        }
        this.despawnable = this.namedTag.getBoolean("Despawnable");
        try {
            this.initEntity();
            if (this.initialized) {
                // We've already initialized this entity
                return;
            }
            this.initialized = true;

            this.chunk.addEntity(this);
            this.level.addEntity(this);

            EntitySpawnEvent event = new EntitySpawnEvent(this);
            this.server.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                this.close(false);
            } else {
                this.scheduleUpdate();
                this.lastUpdate = this.level.getTick();
            }
        } catch (Exception e) {
            this.close(false);
            throw e;
        }
    }

    public Set<String> typeFamily() {
        if (isCustomEntity()) {
            return meta().getStringSet(CustomEntityComponents.TYPE_FAMILY);
        }
        return Set.of();
    }

    public final boolean isFamily(String family) {
        return family != null && !family.isEmpty() && typeFamily().contains(family);
    }

    public final boolean isAnyFamily(Collection<String> families) {
        if (families == null || families.isEmpty()) return false;
        Set<String> mine = typeFamily();
        for (String f : families) if (mine.contains(f)) return true;
        return false;
    }

    public final boolean isAllFamilies(Collection<String> families) {
        return families == null || families.isEmpty() || typeFamily().containsAll(families);
    }

    public String getScoreTag() {
        return this.getDataProperty(ActorDataTypes.SCORE, "");
    }

    public void setScoreTag(String score) {
        this.setDataProperty(ActorDataTypes.SCORE, score);
    }

    public boolean isSneaking() {
        return this.getDataFlag(ActorFlags.SNEAKING);
    }

    public void setSneaking(boolean value) {
        boolean changed = this.getEntityDataMap().getOrCreateFlags().contains(ActorFlags.SNEAKING) ^ value;

        if (changed) {
            this.getEntityDataMap().setFlag(ActorFlags.SNEAKING, value);
        }

        recalculateBoundingBox(false);
        float newHeight = (float) this.getEntityDataMap().getOrDefault(ActorDataTypes.HEIGHT, getCurrentHeight());

        if (changed) {
            ActorDataMap delta = new ActorDataMap();
            delta.put(ActorDataTypes.FLAGS, this.getEntityDataMap().getFlags());
            delta.putType(ActorDataTypes.HEIGHT, newHeight);
            sendData(this.hasSpawned.values().toArray(Player.EMPTY_ARRAY), delta);
        } else {
            ActorDataMap delta = new ActorDataMap();
            delta.putType(ActorDataTypes.HEIGHT, newHeight);
            sendData(this.hasSpawned.values().toArray(Player.EMPTY_ARRAY), delta);
        }
    }

    public boolean isSwimming() {
        return this.getDataFlag(ActorFlags.SWIMMING);
    }

    public void setSwimming(boolean value) {
        if (isSwimming() == value) {
            return;
        }
        this.setDataFlag(ActorFlags.SWIMMING, value);
        if (Float.compare(getSwimmingHeight(), getHeight()) != 0) {
            recalculateBoundingBox(true);
        }
    }

    public boolean isSprinting() {
        return this.getDataFlag(ActorFlags.SPRINTING);
    }

    public void setSprinting(boolean value) {
        this.setDataFlag(ActorFlags.SPRINTING, value);
    }

    public boolean isCrawling() {
        return this.getDataFlag(ActorFlags.CRAWLING);
    }

    public void setCrawling(boolean value) {
        this.setDataFlag(ActorFlags.CRAWLING, value);
    }

    public boolean isGliding() {
        return this.getDataFlag(ActorFlags.GLIDING);
    }

    public void setGliding(boolean value) {
        this.setDataFlag(ActorFlags.GLIDING, value);
    }

    public boolean isImmobile() {
        return this.getDataFlag(ActorFlags.NO_AI);
    }

    public void setImmobile(boolean value) {
        this.setDataFlag(ActorFlags.NO_AI, value);
    }

    public boolean canClimb() {
        return this.getDataFlag(ActorFlags.CAN_CLIMB);
    }

    public void setCanClimb(boolean value) {
        this.setDataFlag(ActorFlags.CAN_CLIMB, value);
    }

    public boolean canClimbWalls() {
        return this.getDataFlag(ActorFlags.WALL_CLIMBING);
    }

    public void setCanClimbWalls(boolean value) {
        this.setDataFlag(ActorFlags.WALL_CLIMBING, value);
    }

    public float getScale() {
        return this.scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
        this.setDataProperty(ActorDataTypes.SCALE, this.scale);
        this.recalculateBoundingBox();
    }

    public float getSwimmingHeight() {
        return getHeight();
    }

    public float getSneakingHeight() {
        return getHeight();
    }

    public float getCrawlingHeight() {
        return getHeight();
    }

    public float getSeatingHeight() {
        return getHeight() * SEATED_FACTOR;
    }

    public Map<EffectType, Effect> getEffects() {
        return effects;
    }

    public void removeAllEffects() {
        for (Effect effect : effects.values()) {
            this.removeEffect(effect.getType());
        }
    }

    public void removeEffect(EffectType type) {
        if (effects.containsKey(type)) {

            Effect effect = effects.get(type);

            EntityEffectRemoveEvent event = new EntityEffectRemoveEvent(this, effect);
            Server.getInstance().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return;
            }

            if (this instanceof Player player && effect.getId() != null) {
                final MobEffectPacket packet = new MobEffectPacket();
                packet.setTargetRuntimeID(player.getId());
                packet.setEffectID(effect.getId());
                packet.setEvent(MobEffectPacket.Event.REMOVE);
                player.dataPacket(packet);
            }

            effect.remove(this);
            effects.remove(type);

            this.recalculateEffectColor();
            this.setDataProperty(ActorDataTypes.VISIBLE_MOB_EFFECTS, computeVisibleMobEffects());
            if (this instanceof EntityLiving) ((EntityLiving) this).recalcMovementSpeedFromEffects();
        }
    }

    public Effect getEffect(EffectType type) {
        return effects.getOrDefault(type, null);
    }

    public boolean hasEffect(EffectType type) {
        return effects.containsKey(type);
    }

    public void addEffect(Effect effect) {
        if (effect == null) {
            return;
        }

        Effect oldEffect = this.getEffect(effect.getType());

        EntityEffectUpdateEvent event = new EntityEffectUpdateEvent(this, oldEffect, effect);
        Server.getInstance().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        if (oldEffect != null && (
                Math.abs(effect.getAmplifier()) < Math.abs(oldEffect.getAmplifier()) ||
                        (Math.abs(effect.getAmplifier()) == Math.abs(oldEffect.getAmplifier()) &&
                                effect.getDuration() < oldEffect.getDuration())
        )) {
            return;
        }

        if (this instanceof Player player && effect.getId() != null) {
            final MobEffectPacket packet = new MobEffectPacket();
            packet.setTargetRuntimeID(player.getId());
            packet.setEffectID(effect.getId());
            packet.setEffectAmplifier(effect.getAmplifier());
            packet.setShowParticles(effect.isVisible());
            packet.setEffectDurationTicks(effect.getDuration());
            packet.setEvent(
                    oldEffect != null ? MobEffectPacket.Event.MODIFY : MobEffectPacket.Event.ADD
            );
            player.dataPacket(packet);
        }

        effect.add(this);
        effects.put(effect.getType(), effect);

        this.recalculateEffectColor();
        this.setDataProperty(ActorDataTypes.VISIBLE_MOB_EFFECTS, computeVisibleMobEffects());
        if (this instanceof EntityLiving) ((EntityLiving) this).recalcMovementSpeedFromEffects();
    }

    public void recalculateBoundingBox() {
        this.recalculateBoundingBox(true);
    }

    public void recalculateBoundingBox(boolean send) {
        float entityHeight = getCurrentHeight();
        float height = entityHeight * this.scale;
        double radius = (this.getWidth() * this.scale) / 2d;
        this.boundingBox.setBounds(
                x - radius,
                y,
                z - radius,

                x + radius,
                y + height,
                z + radius
        );

        boolean change = false;
        if (!this.getEntityDataMap().containsKey(ActorDataTypes.HEIGHT) || this.getEntityDataMap().get(ActorDataTypes.HEIGHT) != entityHeight) {
            change = true;
            this.getEntityDataMap().put(ActorDataTypes.HEIGHT, entityHeight);
        }
        if (!this.getEntityDataMap().containsKey(ActorDataTypes.WIDTH) || this.getEntityDataMap().get(ActorDataTypes.WIDTH) != this.getWidth()) {
            change = true;
            this.getEntityDataMap().put(ActorDataTypes.WIDTH, this.getWidth());
        }
        if (send && change) {
            sendData(this.hasSpawned.values().toArray(Player.EMPTY_ARRAY), this.copyEntityData(ActorDataTypes.WIDTH, ActorDataTypes.HEIGHT));
        }
    }

    private ActorDataMap copyEntityData(ActorDataType<?>... types) {
        final ActorDataMap map = new ActorDataMap();
        for (ActorDataType<?> type : types) {
            if (this.entityDataMap.containsKey(type)) {
                map.put(type, this.entityDataMap.get(type));
            }
        }
        return map;
    }

    protected void recalculateEffectColor() {
        int[] color = new int[3];
        int count = 0;
        boolean ambient = true;
        for (Effect effect : effects.values()) {
            if (effect.isVisible()) {
                Color effectColor = effect.getColor();
                color[0] += effectColor.getRed() * effect.getLevel();
                color[1] += effectColor.getGreen() * effect.getLevel();
                color[2] += effectColor.getBlue() * effect.getLevel();
                count += effect.getLevel();
                if (!effect.isAmbient()) {
                    ambient = false;
                }
            }
        }

        if (count > 0) {
            int r = (color[0] / count) & 0xff;
            int g = (color[1] / count) & 0xff;
            int b = (color[2] / count) & 0xff;
            setDataProperties(Map.of(
                    ActorDataTypes.EFFECT_COLOR, (r << 16) + (g << 8) + b,
                    ActorDataTypes.EFFECT_AMBIENCE, ambient ? (byte) 1 : 0
            ));
        } else {
            setDataProperties(Map.of(
                    ActorDataTypes.EFFECT_COLOR, 0,
                    ActorDataTypes.EFFECT_AMBIENCE, (byte) 0
            ));
        }
    }

    private long computeVisibleMobEffects() {
        if (effects == null || effects.isEmpty()) return 0L;

        ArrayList<Effect> list = new ArrayList<>(effects.values());
        list.sort((a, b) -> Integer.compare(
                a.getType() != null && a.getType().id() != null ? a.getType().id() : -1,
                b.getType() != null && b.getType().id() != null ? b.getType().id() : -1
        ));

        long data = 0L;
        int packed = 0;

        for (Effect e : list) {
            if (packed >= 8) break;
            if (e == null || e.getType() == null || e.getType().id() == null) continue;
            if (!e.isVisible()) continue;

            int id = e.getType().id();
            if (id < 0 || id > 63) continue;

            int ambient = e.isAmbient() ? 1 : 0;
            int slotByte = (id & 0x3F) | (ambient << 6);
            data |= ((long) (slotByte & 0xFF)) << (packed * 8);
            packed++;
        }
        return data;
    }

    public void saveNBT() {
        final NbtMapBuilder builder = this.namedTag.toBuilder();
        if (!(this instanceof Player)) {
            builder.putString("identifier", this.getIdentifier());
            if (!this.getNameTag().isEmpty()) {
                builder.putString("CustomName", this.getNameTag())
                        .putBoolean("CustomNameVisible", this.isNameTagVisible())
                        .putBoolean("CustomNameAlwaysVisible", this.isNameTagAlwaysVisible());
            } else {
                builder.remove("CustomName");
                builder.remove("CustomNameVisible");
                builder.remove("CustomNameAlwaysVisible");
            }
            if (this.entityUniqueId == null) {
                this.entityUniqueId = UUID.randomUUID();
            }
            builder.putString("uuid", this.entityUniqueId.toString());
        }

        builder.putList("Pos", NbtType.DOUBLE, Arrays.asList(this.x, this.y, this.z));
        builder.putList("Motion", NbtType.DOUBLE, Arrays.asList(this.motionX, this.motionY, this.motionZ));
        builder.putList("Rotation", NbtType.FLOAT, Arrays.asList((float) this.yaw, (float) this.pitch));
        builder.putFloat("FallDistance", this.fallDistance)
                .putShort("Fire", (short) this.fireTicks)
                .putShort("Air", this.getDataProperty(ActorDataTypes.AIR_SUPPLY, (short) 0))
                .putBoolean("OnGround", this.onGround)
                .putBoolean("Invulnerable", this.invulnerable)
                .putBoolean("Despawnable", this.despawnable)
                .putFloat("Scale", this.scale);

        if (!this.effects.isEmpty()) {
            final List<NbtMap> list = new ObjectArrayList<>();
            for (Effect effect : this.effects.values()) {
                list.add(NbtMap.builder()
                        .putByte("Id", effect.getId().byteValue())
                        .putByte("Amplifier", (byte) effect.getAmplifier())
                        .putInt("Duration", effect.getDuration())
                        .putBoolean("Ambient", false)
                        .putBoolean("ShowParticles", effect.isVisible())
                        .build()
                );
            }

            builder.putList("ActiveEffects", NbtType.COMPOUND, list);
        } else {
            builder.remove("ActiveEffects");
        }

        if (!this.attributes.isEmpty()) {
            List<NbtMap> attributes = new ObjectArrayList<>();
            for (var attribute : this.attributes.values()) {
                NbtMap nbt = Attribute.toNBT(attribute);
                attributes.add(nbt);
            }
            builder.putList("Attributes", NbtType.COMPOUND, attributes);
        } else {
            builder.remove("Attributes");
        }

        // Save intProperties & boolProperties
        NbtMapBuilder intProps = NbtMap.builder();
        for (Map.Entry<String, Integer> entry : intProperties.entrySet()) {
            intProps.putInt(entry.getKey(), entry.getValue());
        }
        builder.putCompound("IntProperties", intProps.build());

        // Save floatProperties
        NbtMapBuilder floatProps = NbtMap.builder();
        for (Map.Entry<String, Float> entry : floatProperties.entrySet()) {
            floatProps.putFloat(entry.getKey(), entry.getValue());
        }
        builder.putCompound("FloatProperties", floatProps.build());
    }

    /**
     * The pretty name of this entity.
     */
    public String getOriginalName() {
        if (isCustomEntity()) {
            String n = meta().getString(CustomEntityComponents.ORIGINAL_NAME, "");
            if (!n.isEmpty()) return n;
        }
        return name == null ? idConvertToName() : name;
    }

    /**
     * Similar to {@link #getName()}, but if the name is blank or empty it returns the static name instead.
     */
    public final String getVisibleName() {
        String name = getName();
        if (!TextFormat.clean(name).trim().isEmpty()) {
            return name;
        } else {
            return getOriginalName();
        }
    }

    /**
     * The current name used by this entity in the name tag, or the static name if the entity don't have nametag.
     */
    @NotNull
    public String getName() {
        if (this.hasCustomName()) {
            return this.getNameTag();
        } else {
            return this.getOriginalName();
        }
    }

    /**
     * Spawn this entity on the client side so that the player can see it
     *
     * @param player the player
     */
    public void spawnTo(Player player) {
        if (this.closed) return;
        log.info("spawn {} to {}", this.getName(), player.getName());
        if (!this.hasSpawned.containsKey(player.getLoaderId()) && this.chunk != null && player.getUsedChunks().contains(Level.chunkHash(this.chunk.getX(), this.chunk.getZ()))) {
            this.hasSpawned.put(player.getLoaderId(), player);
            player.dataPacket(createAddEntityPacket());
        }

        if (this.riding != null) {
            this.riding.spawnTo(player);

            final SetActorLinkPacket setActorLinkPacket = new SetActorLinkPacket();
            setActorLinkPacket.setLink(
                    new ActorLink(
                            this.riding.getId(),
                            this.getId(),
                            ActorLinkType.RIDING,
                            true,
                            false,
                            0f
                    )
            );
            player.dataPacket(setActorLinkPacket);
        }
    }

    protected BedrockPacket createAddEntityPacket() {
        final AddActorPacket addActorPacket = new AddActorPacket();
        if (!this.isPlayer && !this.attributes.isEmpty()) {
            addActorPacket.getAttributesList().addAll(
                    this.attributes.values().stream()
                            .map(Attribute::toNetwork)
                            .toList()
            );
        }

        addActorPacket.setActorData(this.entityDataMap);

        int controlSeat = getControllingSeatIndex();
        for (int i = 0; i < this.passengers.size(); i++) {
            final ActorLinkType actorLinkType = (i == controlSeat ? ActorLinkType.RIDING : ActorLinkType.PASSENGER);
            final ActorLink actorLink = new ActorLink(
                    this.getId(),
                    this.passengers.get(i).getId(),
                    actorLinkType,
                    false,
                    false,
                    0f
            );
            addActorPacket.getActorLinks().add(actorLink);
        }
        addActorPacket.setTargetActorID(this.getId());
        addActorPacket.setTargetRuntimeID(this.getId());
        addActorPacket.setActorType(this.getIdentifier());
        addActorPacket.setPosition(this.getPosition().toNetwork());
        addActorPacket.setVelocity(org.cloudburstmc.math.vector.Vector3f.from(this.motionX, this.motionY, this.motionZ));
        addActorPacket.setRotation(Vector2f.from(this.pitch, this.yaw));
        addActorPacket.setHeadRotation((float) this.headYaw);
        addActorPacket.setBodyRotation((float) this.yaw);
        final PropertySyncData propertySyncData = this.getClientSyncProperties();
        addActorPacket.getSyncedProperties().getIntProperties().addAll(propertySyncData.getIntProperties());
        addActorPacket.getSyncedProperties().getFloatProperties().addAll(propertySyncData.getFloatProperties());
        return addActorPacket;
    }

    public Map<Integer, Player> getViewers() {
        return hasSpawned;
    }

    public void sendPotionEffects(Player player) {
        for (Effect effect : effects.values()) {
            if (effect.getId() != null) {
                final MobEffectPacket packet = new MobEffectPacket();
                packet.setTargetRuntimeID(this.getId());
                packet.setEffectID(effect.getId());
                packet.setEffectAmplifier(effect.getAmplifier());
                packet.setShowParticles(effect.isVisible());
                packet.setEffectDurationTicks(effect.getDuration());
                packet.setEvent(MobEffectPacket.Event.ADD);
                player.dataPacket(packet);
            }
        }
    }

    public void sendData(Player player) {
        this.sendData(player, null);
    }

    public void sendData(Player player, ActorDataMap data) {
        final SetActorDataPacket packet = new SetActorDataPacket();
        packet.setActorData(data == null ? this.entityDataMap : data);
        packet.setTargetRuntimeID(this.getId());
        packet.getSyncedProperties().getFloatProperties().addAll(this.propertySyncData().getFloatProperties());
        packet.getSyncedProperties().getIntProperties().addAll(this.propertySyncData().getIntProperties());

        player.dataPacket(packet);
    }

    public void sendData(Player[] players) {
        this.sendData(players, null);
    }

    public void sendData(Player[] players, ActorDataMap data) {
        final SetActorDataPacket packet = new SetActorDataPacket();
        packet.setActorData(data == null ? this.entityDataMap : data);
        packet.setTargetRuntimeID(this.getId());
        packet.getSyncedProperties().getFloatProperties().addAll(this.propertySyncData().getFloatProperties());
        packet.getSyncedProperties().getIntProperties().addAll(this.propertySyncData().getIntProperties());

        for (Player player : players) {
            if (player == this) {
                continue;
            }
            player.dataPacket(packet);
        }
        if (this instanceof Player player) {
            player.dataPacket(packet);
        }
    }

    public void despawnFrom(Player player) {
        if (this.hasSpawned.containsKey(player.getLoaderId())) {
            final RemoveActorPacket packet = new RemoveActorPacket();
            packet.setTargetActorID(this.getId());
            player.dataPacket(packet);
            this.hasSpawned.remove(player.getLoaderId());
        }
    }

    /**
     * Called when an entity is attacked (i.e. receives an entity damage event. This event can be caused by an attack by another entity, or it can be a natural damage).
     *
     * @param source Record the event of the source of the attack
     * @return Whether the attack was successful
     */
    public boolean attack(EntityDamageEvent source) {
        // Fire Protection enchantment implemented
        if ((hasEffect(EffectType.FIRE_RESISTANCE)
                || !this.level.gameRules.getBoolean(GameRule.FIRE_DAMAGE))
                && (source.getCause() == DamageCause.FIRE
                || source.getCause() == DamageCause.FIRE_TICK
                || source.getCause() == DamageCause.LAVA)) {
            return false;
        }

        // Aquatic creatures are immune to drowning
        if (this instanceof EntitySwimmable swimmable && !swimmable.canDrown() && source.getCause() == DamageCause.DROWNING)
            return false;

        // Flying creatures are immune to falls
        if (this instanceof EntityFlyable flyable && !flyable.hasFallingDamage() && source.getCause() == DamageCause.FALL)
            return false;

        // Event callback function
        getServer().getPluginManager().callEvent(source);
        if (source.isCancelled()) {
            return false;
        }

        // Make fire aspect to set the target in fire before dealing any damage so the target is in fire on death even if killed by the first hit
        if (source instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) {
            Enchantment[] enchantments = entityDamageByEntityEvent.getWeaponEnchantments();
            if (enchantments != null) {
                for (Enchantment enchantment : enchantments) {
                    enchantment.doAttack(entityDamageByEntityEvent);
                }
            }
        }

        // Damage absorption implementation
        if (this.absorption > 0) {
            this.setAbsorption(Math.max(0, this.getAbsorption() + source.getDamage(EntityDamageEvent.DamageModifier.ABSORPTION)));
        }

        // Modify the last damage
        setLastDamageCause(source);

        // Calculating blood volume
        float newHealth = getHealthCurrent() - source.getFinalDamage();

        // Only player
        if (newHealth < 1 && this instanceof Player player) {
            if (source.getCause() != DamageCause.VOID && source.getCause() != DamageCause.SUICIDE) {
                boolean totem = false;
                boolean isOffhand = false;
                if (player.getOffhandInventory().getItem(0) instanceof ItemTotemOfUndying) {
                    totem = true;
                    isOffhand = true;
                } else if (player.getInventory().getItemInMainHand() instanceof ItemTotemOfUndying) {
                    totem = true;
                }
                // Resurrection Totem Implementation
                if (totem) {
                    this.getLevel().addLevelEvent(this, LevelEvent.SOUND_TOTEM_USED);
                    this.getLevel().addParticleEffect(this, ParticleEffect.TOTEM);

                    this.extinguish();
                    this.removeAllEffects();
                    this.setHealthCurrent(1);

                    this.addEffect(Effect.get(EffectType.REGENERATION).setDuration(800).setAmplifier(1));
                    this.addEffect(Effect.get(EffectType.FIRE_RESISTANCE).setDuration(800));
                    this.addEffect(Effect.get(EffectType.ABSORPTION).setDuration(100).setAmplifier(1));

                    final ActorEventPacket actorEventPacket = new ActorEventPacket();
                    actorEventPacket.setTargetRuntimeID(this.getId());
                    actorEventPacket.setType(ActorEvent.TALISMAN_ACTIVATE);
                    player.dataPacket(actorEventPacket);

                    if (isOffhand) {
                        player.getOffhandInventory().clear(0, true);
                    } else {
                        player.getInventory().clear(player.getInventory().getHeldItemIndex(), true);
                    }

                    source.setCancelled(true);
                    return false;
                }
            }
        }

        Entity attacker = source instanceof EntityDamageByEntityEvent ? ((EntityDamageByEntityEvent) source).getDamager() : null;

        setHealthCurrent(newHealth);

        if (!(this instanceof EntityArmorStand)) {
            this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(attacker, this.getVector3(), VibrationType.ENTITY_DAMAGE));
        }

        return true;
    }

    public boolean attack(float damage) {
        return this.attack(new EntityDamageEvent(this, DamageCause.CUSTOM, damage));
    }

    public int getAge() {
        return this.age;
    }

    public int getNetworkId() {
        return Registries.ENTITY.getEntityNetworkId(getIdentifier());
    }

    public boolean isAlive() {
        return this.health > 0;
    }

    public boolean isClosed() {
        return closed;
    }

    public EntityDamageEvent getLastDamageCause() {
        return lastDamageCause;
    }

    public void setLastDamageCause(EntityDamageEvent type) {
        this.lastDamageCause = type;
    }

    public boolean isNpc() {
        return this instanceof EntityNpc;
    }

    public boolean isArmorStand() {
        return this instanceof EntityArmorStand;
    }

    public boolean canCollideWith(Entity entity) {
        return !this.justCreated && this != entity && !this.noClip;
    }

    /**
     * Whether the entity is persisted to disk
     *
     * @return the boolean
     */
    public boolean canBeSavedWithChunk() {
        return saveWithChunk;
    }


    /**
     * Set this entity is persisted to disk
     *
     * @param saveWithChunk value
     */
    public void setCanBeSavedWithChunk(boolean saveWithChunk) {
        this.saveWithChunk = saveWithChunk;
    }

    protected boolean checkObstruction(double x, double y, double z) {
        if (this.level.fastCollisionCubes(this, this.getBoundingBox(), false).isEmpty() || this.noClip) {
            return false;
        }

        int i = NukkitMath.floorDouble(x);
        int j = NukkitMath.floorDouble(y);
        int k = NukkitMath.floorDouble(z);

        double diffX = x - i;
        double diffY = y - j;
        double diffZ = z - k;

        if (!this.level.getBlock(i, j, k).isTransparent()) {
            boolean flag = this.level.getBlock(i - 1, j, k).isTransparent();
            boolean flag1 = this.level.getBlock(i + 1, j, k).isTransparent();
            boolean flag2 = this.level.getBlock(i, j - 1, k).isTransparent();
            boolean flag3 = this.level.getBlock(i, j + 1, k).isTransparent();
            boolean flag4 = this.level.getBlock(i, j, k - 1).isTransparent();
            boolean flag5 = this.level.getBlock(i, j, k + 1).isTransparent();

            int direction = -1;
            double limit = 9999;

            if (flag) {
                limit = diffX;
                direction = 0;
            }

            if (flag1 && 1 - diffX < limit) {
                limit = 1 - diffX;
                direction = 1;
            }

            if (flag2 && diffY < limit) {
                limit = diffY;
                direction = 2;
            }

            if (flag3 && 1 - diffY < limit) {
                limit = 1 - diffY;
                direction = 3;
            }

            if (flag4 && diffZ < limit) {
                limit = diffZ;
                direction = 4;
            }

            if (flag5 && 1 - diffZ < limit) {
                direction = 5;
            }

            double force = ThreadLocalRandom.current().nextDouble() * 0.2 + 0.1;

            if (direction == 0) {
                this.motionX = -force;

                return true;
            }

            if (direction == 1) {
                this.motionX = force;

                return true;
            }

            if (direction == 2) {
                this.motionY = -force;

                return true;
            }

            if (direction == 3) {
                this.motionY = force;

                return true;
            }

            if (direction == 4) {
                this.motionZ = -force;

                return true;
            }

            if (direction == 5) {
                this.motionZ = force;

                return true;
            }
        }

        return false;
    }

    public boolean entityBaseTick() {
        return this.entityBaseTick(1);
    }

    public boolean entityBaseTick(int tickDiff) {
        if (!getServer().isRunning()) return true;
        if (!this.isAlive()) {
            if (this instanceof EntityCreature) {
                this.deadTicks += tickDiff;
                if (this.deadTicks >= 15) {
                    //apply death smoke cloud only if it is a creature
                    var aabb = this.getBoundingBox();
                    for (double x = aabb.getMinX(); x <= aabb.getMaxX(); x += 0.5) {
                        for (double z = aabb.getMinZ(); z <= aabb.getMaxZ(); z += 0.5) {
                            for (double y = aabb.getMinY(); y <= aabb.getMaxY(); y += 0.5) {
                                this.getLevel().addParticle(new ExplodeParticle(new Vector3(x, y, z)));
                            }
                        }
                    }
                    this.despawnFromAll();
                    if (!this.isPlayer) {
                        this.close();
                    }
                }
                return this.deadTicks < 15;
            } else {
                this.despawnFromAll();
                if (!this.isPlayer) {
                    this.close();
                }
            }
        }
        if (!this.isPlayer) {
            this.blocksAround = null;
            this.collisionBlocks = null;
        }
        this.justCreated = false;
        this.stepOnBlocks = null;

        if (riding != null && !riding.isAlive() && riding.isRideable()) {
            riding.dismountEntity(this);
        }
        updatePassengers();

        // Boostable timer tickdown
        if (this.boostableTicks > -1 && this.isBoostable()) {
            this.boostableTicks -= tickDiff;
            if (this.boostableTicks <= 0) {
                this.boostableTicks = -1;
            }
        }

        if (!this.effects.isEmpty()) {
            for (Effect effect : this.effects.values()) {
                if (effect.canTick()) {
                    effect.apply(this, 1);
                }
                effect.setDuration(effect.getDuration() - tickDiff);

                if (effect.getDuration() <= 0) {
                    this.removeEffect(effect.getType());
                }
            }
        }

        boolean hasUpdate = false;

        this.checkBlockCollision();
        this.checkBlockStepOn();

        if (this.y < (level.getMinHeight() - 18) && this.isAlive()) {
            if (this instanceof Player player) {
                if (!player.isCreative()) this.attack(new EntityDamageEvent(this, DamageCause.VOID, 10));
            } else {
                this.attack(new EntityDamageEvent(this, DamageCause.VOID, 10));
                hasUpdate = true;
            }
        }

        if (this.fireTicks > 0 && this.hasEffect(EffectType.FIRE_RESISTANCE)) fireTicks = 0;
        if (this.fireTicks > 0) {
            if (this.fireProof) {
                this.fireTicks -= 4 * tickDiff;
                if (this.fireTicks < 0) {
                    this.fireTicks = 0;
                }
            } else {
                if (((this.fireTicks % 20) == 0 || tickDiff > 20)) {
                    this.attack(new EntityDamageEvent(this, DamageCause.FIRE_TICK, 1));
                }
                this.fireTicks -= tickDiff;
            }
            if (this.fireTicks <= 0) {
                this.extinguish();
            } else if (!this.fireProof && (!(this instanceof Player player) || !player.isSpectator())) {
                this.setDataFlag(ActorFlags.ON_FIRE);
                hasUpdate = true;
            }
        }

        if (this.noDamageTicks > 0) {
            this.noDamageTicks -= tickDiff;
            if (this.noDamageTicks < 0) {
                this.noDamageTicks = 0;
            }
        }

        if (this.inPortalTicks == 80) { // Handle portal teleport
            EntityPortalEnterEvent ev = new EntityPortalEnterEvent(this, PortalType.NETHER);
            getServer().getPluginManager().callEvent(ev);

            if (!ev.isCancelled() && (level.getDimension() == Level.DIMENSION_OVERWORLD || level.getDimension() == Level.DIMENSION_NETHER)) {
                Position newPos = PortalHelper.convertPosBetweenNetherAndOverworld(this);
                if (newPos != null) {
                    IChunk destChunk = newPos.getChunk();
                    if (!destChunk.isGenerated()) {
                        newPos.getLevel().syncGenerateChunk(destChunk.getX(), destChunk.getZ());
                        newPos = PortalHelper.convertPosBetweenNetherAndOverworld(this);
                    }
                    if (newPos != null) {
                        // Use Optional for safer portal search
                        Optional<Position> nearestPortalOpt = PortalHelper.getNearestValidPortal(newPos);
                        if (nearestPortalOpt.isPresent()) {
                            teleport(nearestPortalOpt.get().add(0.5, 0, 0.5), PlayerTeleportEvent.TeleportCause.NETHER_PORTAL);
                        } else {
                            final Position finalPos = newPos.add(1.5, 1, 1.5);
                            inPortalTicks = 81;
                            PortalHelper.spawnPortal(newPos);
                            teleport(finalPos, PlayerTeleportEvent.TeleportCause.NETHER_PORTAL);
                        }
                    } else {
                        getServer().getLogger().warning("Failed to calculate new Nether position for portal teleport.");
                    }
                } else {
                    getServer().getLogger().warning("Failed to convert position between Nether and Overworld for portal teleport.");
                }
            }
        }
        this.age += tickDiff;
        this.ticksLived += tickDiff;

        // Auto-despawn mobs
        if (!this.isPersistent() && this.isAlive()) {
            final int tickNow = this.level.getTick();
            final int softDistSq = DEFAULT_SOFT_DESPAWN_DISTANCE * DEFAULT_SOFT_DESPAWN_DISTANCE;
            final int hardDistSq = DEFAULT_HARD_DESPAWN_DISTANCE * DEFAULT_HARD_DESPAWN_DISTANCE;

            Player nearest = null;
            double nearestSq = Double.MAX_VALUE;

            if (!this.level.getPlayers().isEmpty()) {
                for (Player p : this.level.getPlayers().values()) {
                    if (!p.isOnline() || p.isSpectator()) continue;
                    double dsq = p.distanceSquared(this);
                    if (dsq < nearestSq) {
                        nearestSq = dsq;
                        nearest = p;
                    }
                }
            }

            // Hard distance -> immediate despawn
            if (nearest == null || nearestSq > hardDistSq) {
                this.despawnFromAll();
                this.close();
                return hasUpdate;
            }

            // Soft distance -> start/consume grace
            if (nearestSq <= softDistSq) {
                this.lastPlayerNearbyTick = tickNow;
            } else {
                if (this.lastPlayerNearbyTick == 0) {
                    this.lastPlayerNearbyTick = tickNow;
                } else if ((tickNow - this.lastPlayerNearbyTick) >= DEFAULT_SOFT_DESPAWN_GRACE_TICKS) {
                    this.despawnFromAll();
                    this.close();
                    return hasUpdate;
                }
            }
        }

        return hasUpdate;
    }

    public void updateMovement() {
        // This is done for backward compatibility with older plugins.
        if (isImmobile()) return; //Do not move when immobile

        if (!enableHeadYaw()) {
            this.headYaw = this.yaw;
        }
        double diffPosition = (this.x - this.lastX) * (this.x - this.lastX) + (this.y - this.lastY) * (this.y - this.lastY) + (this.z - this.lastZ) * (this.z - this.lastZ);
        double diffRotation = (enableHeadYaw() ? (this.headYaw - this.lastHeadYaw) * (this.headYaw - this.lastHeadYaw) : 0) + (this.yaw - this.lastYaw) * (this.yaw - this.lastYaw) + (this.pitch - this.lastPitch) * (this.pitch - this.lastPitch);

        double diffMotion = (this.motionX - this.lastMotionX) * (this.motionX - this.lastMotionX) + (this.motionY - this.lastMotionY) * (this.motionY - this.lastMotionY) + (this.motionZ - this.lastMotionZ) * (this.motionZ - this.lastMotionZ);

        if (diffPosition > 0.0001 || diffRotation > 1.0) { //0.2 ** 2, 1.5 ** 2
            if (diffPosition > 0.0001) {
                if (this.isOnGround()) {
                    this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this instanceof EntityProjectile projectile ? projectile.shootingEntity : this, this.getVector3(), VibrationType.STEP));
                } else if (this.isTouchingWater()) {
                    this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this instanceof EntityProjectile projectile ? projectile.shootingEntity : this, this.getVector3(), VibrationType.SWIM));
                }
            }

            this.addMovement(this.x, this.isPlayer ? this.y : this.y + this.getBaseOffset(), this.z, this.yaw, this.pitch, this.headYaw);

            this.lastX = this.x;
            this.lastY = this.y;
            this.lastZ = this.z;

            this.lastPitch = this.pitch;
            this.lastYaw = this.yaw;
            this.lastHeadYaw = this.headYaw;

            this.positionChanged = true;
        } else {
            this.positionChanged = false;
        }

        if (diffMotion > 0.0025 || (diffMotion > 0.0001 && this.getMotion().lengthSquared() <= 0.0001)) { //0.05 ** 2
            this.lastMotionX = this.motionX;
            this.lastMotionY = this.motionY;
            this.lastMotionZ = this.motionZ;

            this.addMotion(this.motionX, this.motionY, this.motionZ);
        }
    }


    public boolean enableHeadYaw() {
        return false;
    }

    /**
     * Add motion (just sending packet will not make the entity actually move, use {@link #setMotion} if needed)
     *
     * @param x       x
     * @param y       y
     * @param z       z
     * @param yaw     左右旋转
     * @param pitch   上下旋转
     * @param headYaw headYaw
     */
    public void addMovement(double x, double y, double z, double yaw, double pitch, double headYaw) {
        this.level.addEntityMovement(this, x, y, z, yaw, pitch, headYaw);
    }

    /**
     * Please note that this method only sends a motion packet to the client and does not actually add the motion value to the entity's motion(x|y|z).<p/>
     * If you want to add to the entity's motion, please directly add the motion value to the entity's motion(x|y|z), like this:<p/>
     * entity.motionX += vector3.x;<p/>
     * entity.motionY += vector3.y;<p/>
     * entity.motionZ += vector3.z;<p/>
     * You should not use this method for player entities!
     */
    public void addMotion(double motionX, double motionY, double motionZ) {
        final SetActorMotionPacket packet = new SetActorMotionPacket();
        packet.setTargetRuntimeID(this.getId());
        packet.setMotion(org.cloudburstmc.math.vector.Vector3f.from(motionX, motionY, motionZ));

        Server.broadcastPacket(this.hasSpawned.values(), packet);
    }


    protected void broadcastMovement(boolean tp) {
        final MoveActorAbsoluteData moveData = new MoveActorAbsoluteData();
        moveData.setActorRuntimeID(this.getId());
        moveData.setOnGround(this.onGround);
        moveData.setTeleported(tp);
        moveData.setPos(this.getPosition().toNetwork());
        moveData.setRotation(org.cloudburstmc.math.vector.Vector3f.from(this.pitch, this.yaw, this.yaw));

        final MoveActorAbsolutePacket packet = new MoveActorAbsolutePacket();
        packet.setMoveData(moveData);

        Server.broadcastPacket(hasSpawned.values(), packet);
    }

    public Vector2 getDirectionPlane() {
        return (new Vector2((float) (-Math.cos(Math.toRadians(this.yaw) - Math.PI / 2)), (float) (-Math.sin(Math.toRadians(this.yaw) - Math.PI / 2)))).normalize();
    }

    public BlockFace getHorizontalFacing() {
        return BlockFace.fromHorizontalIndex(NukkitMath.floorDouble((this.yaw * 4.0F / 360.0F) + 0.5D) & 3);
    }

    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        int tickDiff = currentTick - this.lastUpdate;
        if (tickDiff <= 0) {
            return false;
        }
        this.lastUpdate = currentTick;

        boolean hasUpdate = this.entityBaseTick(tickDiff);

        if (!this.isImmobile()) {
            this.updateMovement();
        }

        return hasUpdate;
    }


    /// ///////////////////////////////////////////
    /// /////////// RIDEABLE APIS /////////////////
    /// ///////////////////////////////////////////

    public boolean onRiderInput(Player rider, PlayerAuthInputPacket pk) {
        return false;
    }

    /**
     * Return true if getComponentRideable() is not null.
     */
    public boolean isRideable() {
        return getComponentRideable() != null;
    }

    public boolean isRiding() {
        return this.riding != null;
    }

    public List<Entity> getPassengers() {
        return passengers;
    }

    @SuppressWarnings("null")
    public Entity getPassenger() {
        return Iterables.getFirst(this.passengers, null);
    }

    public boolean isPassenger(Entity entity) {
        return this.passengers.contains(entity);
    }

    public boolean isControlling(Entity entity) {
        Entity rider = getRider();
        return rider != null && rider == entity;
    }

    public boolean hasControllingPassenger() {
        return getRider() != null;
    }

    public Entity getRiding() {
        return riding;
    }

    public Entity getRider() {
        if (this.passengers == null || this.passengers.isEmpty()) return null;

        int seat = this.getControllingSeatIndex();
        if (seat < 0) seat = 0;

        Entity rider = this.passengers.get(seat);
        if (rider == null || !rider.isAlive()) return null;

        return rider;
    }

    /**
     * Returns whether this entity can accept passengers (rideable/vehicle).
     * <p>
     * Default: false
     * Custom entities: true only if minecraft:rideable is present.
     */
    public boolean canBeSaddled() {
        if (isCustomEntity()) {
            return meta().has(CustomEntityComponents.PNX_CAN_BE_SADDLED);
        }
        return false;
    }

    public boolean requireSaddleToMount() {
        return false;
    }

    public boolean canBeChested() {
        return false;
    }

    public boolean isChested() {
        return this.getDataFlag(ActorFlags.CHESTED);
    }

    public void setChest(boolean chested) {
        this.setDataFlag(ActorFlags.CHESTED, chested);
    }

    public boolean isEquine() {
        return false;
    }

    /**
     * Play an animation failed to tame
     */
    public void equinePlayTameFailAnimation() {
        this.getLevel().addLevelSoundEvent(this, SoundEvent.MAD, -1, "minecraft:horse", this.isBaby(), false);
        this.setDataFlag(ActorFlags.STANDING);
    }

    /**
     * Stop playing animation failed to tame
     */
    public void equineStopTameFailAnimation() {
        this.setDataFlag(ActorFlags.STANDING, false);
    }

    /**
     * Gets the maximum number of passengers this entity can hold.
     * <p>
     * Default: 1
     */
    public int getSeatCount() {
        RideableComponent r = getComponentRideable();
        return r != null ? Math.max(1, r.seatCount()) : 1;
    }

    /**
     * Gets the maximum number of passengers this entity can hold.
     * <p>
     * Default: 1
     */
    public String getInteractText() {
        RideableComponent r = getComponentRideable();
        return r != null ? r.interactText() : "action.interact.ride.horse";
    }

    /**
     * Returns passenger max width. If 0, ignored.
     */
    public float getPassengerMaxWidth() {
        RideableComponent r = getComponentRideable();
        return r != null ? r.passengerMaxWidth() : 0.0f;
    }

    /**
     * Returns allowed rider family types (Bedrock rideable family_types).
     */
    public Set<String> getAllowedRiderFamilyTypes() {
        RideableComponent r = getComponentRideable();
        return r != null ? r.familyTypes() : Collections.emptySet();
    }

    /**
     * Checks whether a rider entity is allowed to ride this entity.
     * <p>
     * Default rules:
     * - if allowed set is empty => allow
     * - if allowed contains "player" => allow Player
     * - otherwise rider.typeFamily must intersect allowed set
     */
    public boolean isAllowedRider(Entity rider) {
        Set<String> allowed = getAllowedRiderFamilyTypes();
        if (allowed == null || allowed.isEmpty()) return true;

        if (rider instanceof Player) {
            return allowed.contains("player");
        }

        // typeFamily() exists on many entities (you already use it)
        Set<String> fam = rider.typeFamily();
        if (fam == null || fam.isEmpty()) return false;

        for (String f : fam) {
            if (f == null) continue;
            if (allowed.contains(f)) return true;
        }
        return false;
    }

    public int getControllingSeatIndex() {
        RideableComponent r = getComponentRideable();
        return r != null ? r.controllingSeat() : 0;
    }

    /**
     * Returns the {@link RideableComponent} associated with this entity.
     * <p>
     * The rideable component defines seat configuration, rider interaction rules,
     * and which seat controls entity movement.
     * </p>
     *
     * @return the {@link RideableComponent} if defined for this custom entity,
     * or {@code null} if the entity is not custom or does not have
     * the {@code minecraft:rideable} component.
     */
    public @Nullable RideableComponent getComponentRideable() {
        if (!isCustomEntity()) return null;
        return meta().getRideableComponent(CustomEntityComponents.RIDEABLE);
    }

    /**
     * Return the default base mounted offset for a passenger.
     */
    public Vector3f getMountedOffset(Entity passenger) {
        if (passenger.isPlayer) {
            float standingEyeHeight = passenger.getHeight() * 0.9f;
            float mountedOffsetY = standingEyeHeight * SEATED_FACTOR;
            return new Vector3f(0f, mountedOffsetY, 0f);
        }
        float mountedOffsetY = -passenger.getHeight() * 0.24691358f;
        return new Vector3f(0f, mountedOffsetY, 0f);
    }

    /**
     * Set ride to allow input controls.
     */
    public boolean setInputControls(boolean enabled) {
        if (enabled && !this.canEnableWASDControls()) return false;

        switch (getInputControlType()) {
            case GROUND: {
                this.setDataFlag(ActorFlags.WASD_CONTROLLED, enabled);
                this.setDataFlag(ActorFlags.WASD_FREE_CAMERA_CONTROLLED, false);

                if (enabled) {
                    this.setDataFlag(ActorFlags.DOES_SERVER_AUTH_ONLY_DISMOUNT, false);
                    this.setDataFlag(ActorFlags.CAN_USE_VERTICAL_MOVEMENT_ACTION, false);
                }
                return true;
            }
            case AIR: {
                this.setDataFlag(ActorFlags.WASD_FREE_CAMERA_CONTROLLED, enabled);
                this.setDataFlag(ActorFlags.WASD_CONTROLLED, false);

                if (enabled) {
                    this.setDataFlag(ActorFlags.DOES_SERVER_AUTH_ONLY_DISMOUNT, true);
                    this.setDataFlag(ActorFlags.CAN_USE_VERTICAL_MOVEMENT_ACTION, true);
                } else {
                    this.setDataFlag(ActorFlags.DOES_SERVER_AUTH_ONLY_DISMOUNT, false);
                    this.setDataFlag(ActorFlags.CAN_USE_VERTICAL_MOVEMENT_ACTION, false);
                }
                return true;
            }
            case WATER: {
                this.setDataFlag(ActorFlags.WASD_FREE_CAMERA_CONTROLLED, enabled);
                this.setDataFlag(ActorFlags.WASD_CONTROLLED, false);
                this.setDataFlag(ActorFlags.CAN_USE_VERTICAL_MOVEMENT_ACTION, false);
                this.setDataFlag(ActorFlags.DOES_SERVER_AUTH_ONLY_DISMOUNT, false);
                return true;
            }
            default:
                return false;
        }
    }

    public boolean canEnableWASDControls() {
        return this.isRideable() && (!this.canBeSaddled() || this.isSaddled());
    }

    public boolean rideCanJump() {
        return this.isRideable() && (this.getRideJumpStrength() > 0f && this.getDataFlag(ActorFlags.CAN_POWER_JUMP));
    }

    public boolean rideHasVerticalMove() {
        return this.isRideable() && this.isAirControlled();
    }

    public boolean isSaddled() {
        return this.getDataFlag(ActorFlags.SADDLED);
    }

    public boolean setSaddle(boolean saddled) {
        if (!this.isRideable()) return false;
        if (!this.canBeSaddled()) return false;

        boolean current = this.getDataFlag(ActorFlags.SADDLED);
        if (current == saddled) return false;

        this.setDataFlag(ActorFlags.SADDLED, saddled);
        return true;
    }

    public boolean removeSaddle() {
        if (!this.isRideable()) return false;
        if (!this.canBeSaddled()) return false;
        if (!this.getDataFlag(ActorFlags.SADDLED)) return false;

        this.setDataFlag(ActorFlags.SADDLED, false);
        return true;
    }

    public boolean setCanPowerJump(boolean enabled) {
        if (enabled && !this.isRideable()) return false;
        this.setDataFlag(ActorFlags.CAN_POWER_JUMP, enabled);
        return true;
    }

    public boolean canPowerJump() {
        return this.getDataFlag(ActorFlags.CAN_POWER_JUMP);
    }

    public boolean setCanDash(boolean enabled) {
        if (enabled && !this.isRideable()) return false;
        this.setDataFlag(ActorFlags.CAN_DASH, enabled);
        return true;
    }

    public void setDashCooldown(boolean value) {
        this.setDataFlag(ActorFlags.HAS_DASH_COOLDOWN, value);
    }

    public boolean hasDashCooldown() {
        return this.getDataFlag(ActorFlags.HAS_DASH_COOLDOWN);
    }

    public boolean canDash() {
        return this.getDataFlag(ActorFlags.CAN_DASH);
    }

    /**
     * Returns runtime state of entity has any WASD controls
     */
    public boolean hasWASDControls() {
        return this.isGroundControlled() || this.isAirControlled();
    }

    /**
     * Returns runtime state of entity if is ground controlled
     */
    public boolean isGroundControlled() {
        return this.getDataFlag(ActorFlags.WASD_CONTROLLED);
    }

    /**
     * Returns runtime state of entity if is air controlled
     */
    public boolean isAirControlled() {
        return this.getDataFlag(ActorFlags.WASD_FREE_CAMERA_CONTROLLED);
    }

    /**
     * Define the default input control type
     */
    public RideableComponent.InputType getInputControlType() {
        return null;
    }


    /// ///////////////////////////////////////////
    /// //////////// MOUNT CHAIN //////////////////
    /// ///////////////////////////////////////////

    public boolean mountEntity(Entity entity) {
        return mountEntity(entity, false);
    }

    /**
     * @deprecated Use {@link #mountEntity(Entity, boolean)}. Link type is derived from seat index.
     */
    @Deprecated
    public boolean mountEntity(Entity entity, ActorLinkType mode) {
        boolean ok = mountEntity(entity, false); // not rider initiated by default
        if (!ok || entity.riding != this) return ok;

        // Preserve legacy intent:
        if (mode == ActorLinkType.RIDING) {
            forcePassengerToSeat(entity, 0);
        } else if (mode == ActorLinkType.PASSENGER) {
            forcePassengerToPassengerSeat(entity);
        }
        // refresh seats/positions/links after forcing
        updatePassengers(true, false);
        return true;
    }

    /**
     * INTERNAL LEGACY COMPATIBILITY HELPER.
     * <p>
     * Forces a passenger to occupy a given seat index (list index).
     * <p>
     * This exists ONLY to preserve behavior of deprecated
     * {@link #mountEntity(Entity, ActorLinkType)} and MUST NOT be exposed
     * to plugins or subclasses.
     * <p>
     * Does not broadcast or reposition passengers; caller must invoke
     * {@link #updatePassengers(boolean, boolean)} afterwards.
     *
     * @return true if passenger order changed
     * Planned removal: after 6 months (>= 2026-08-19).
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    private boolean forcePassengerToSeat(Entity entity, int seatIndex) {
        if (entity == null) return false;
        int cur = passengers.indexOf(entity);
        if (cur < 0) return false;

        int maxSeat = Math.max(0, passengers.size() - 1);
        int dst = Math.max(0, Math.min(seatIndex, maxSeat));
        if (cur == dst) return false;

        passengers.remove(cur);
        passengers.add(dst, entity);
        return true;
    }

    /**
     * INTERNAL LEGACY COMPATIBILITY HELPER.
     * <p>
     * Forces a passenger to be placed in a non-rider seat (index >= 1),
     * preserving old PASSENGER semantics from
     * {@link #mountEntity(Entity, ActorLinkType)}.
     *
     * @return true if passenger order changed
     * Planned removal: after 6 months (>= 2026-08-19).
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    private boolean forcePassengerToPassengerSeat(Entity entity) {
        if (entity == null) return false;

        int cur = passengers.indexOf(entity);
        if (cur < 0) return false;

        if (cur != 0) return false;
        if (passengers.size() < 2) return false;

        passengers.remove(0);
        passengers.add(1, entity);
        return true;
    }

    public boolean mountEntity(@NotNull Entity entity, boolean riderInitiated) {
        if (!isRideable() || entity.isSneaking()) return false;

        if (isPassenger(entity) || (entity.riding != null && !entity.riding.dismountEntity(entity, false))) {
            return false;
        }

        int seatCount = Math.max(1, getSeatCount());
        if (passengers.size() >= seatCount) return false;

        if (!isAllowedRider(entity)) return false;

        float maxW = getPassengerMaxWidth();
        if (maxW > 0f && entity.getWidth() > maxW) return false;

        EntityVehicleEnterEvent ev = new EntityVehicleEnterEvent(entity, this);
        server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) return false;

        entity.riding = this;
        entity.setDataFlag(ActorFlags.RIDING, true);
        passengers.add(entity);

        if (!this.isPlayer && !(entity instanceof Player)) {
            entity.namedTag = entity.namedTag.toBuilder().putString(NBT_RIDING_UUID, this.getUniqueId().toString()).build();
        }

        updatePassengers(true, riderInitiated);

        return true;
    }

    protected void updatePassengerPosition(Entity passenger) {
        Vector3f seat = passenger.getSeatPosition();
        if (seat == null) seat = new Vector3f(0, getHeight() * SEATED_FACTOR, 0);

        double yawRad = Math.toRadians(this.yaw);
        double cos = Math.cos(yawRad);
        double sin = Math.sin(yawRad);

        // rotate local seat X/Z into world X/Z
        double ox = seat.x * cos - seat.z * sin;
        double oz = seat.x * sin + seat.z * cos;

        passenger.setPosition(new Vector3(
                this.x + ox,
                this.y + seat.y,
                this.z + oz
        ));
    }

    public void updatePassengers(boolean sendLinks, boolean riderInitiated) {
        if (passengers.isEmpty()) {
            refreshRideMemory();
            return;
        }

        for (Entity passenger : new ArrayList<>(passengers)) {
            if (!passenger.isAlive()) dismountEntity(passenger, sendLinks);
        }
        if (passengers.isEmpty()) {
            refreshRideMemory();
            return;
        }

        boolean reordered = reorderPassengers(passengers);
        boolean countChanged = (passengers.size() != passengerCount);
        passengerCount = passengers.size();

        if (reordered || riderInitiated || countChanged) {
            applySeatOffsets();
        }
        refreshRideMemory();

        for (Entity p : passengers) {
            updatePassengerPosition(p);
        }

        if (sendLinks || reordered) {
            broadcastLinksForAllPassengers(riderInitiated);
        }
    }

    public void updatePassengers(boolean sendLinks) {
        updatePassengers(sendLinks, false);
    }

    public void updatePassengers() {
        updatePassengers(false, false);
    }

    protected boolean reorderPassengers(List<Entity> passengers) {
        if (passengers.size() < 2) return false;

        int controlSeat = getControllingSeatIndex();
        if (controlSeat < 0) controlSeat = 0;
        if (controlSeat >= passengers.size()) controlSeat = passengers.size() - 1;

        Entity current = passengers.get(controlSeat);

        // Only reorder if controlling seat is not a player and there is a player aboard
        if (current instanceof Player) return false;

        int bestIdx = -1;
        for (int i = 0; i < passengers.size(); i++) {
            if (passengers.get(i) instanceof Player) {
                bestIdx = i;
                break;
            }
        }
        if (bestIdx == -1 || bestIdx == controlSeat) return false;

        Entity best = passengers.remove(bestIdx);
        if (bestIdx < controlSeat) controlSeat--;
        passengers.add(controlSeat, best);
        return true;
    }

    protected void refreshRideMemory() {
        if (!(this instanceof EntityIntelligent ei)) return;

        Entity controller = null;
        int controlSeat = getControllingSeatIndex();
        if (controlSeat < 0) controlSeat = 0;
        if (controlSeat < passengers.size()) controller = passengers.get(controlSeat);

        if (controller != null && controller.isAlive()) {
            ei.getMemoryStorage().put(CoreMemoryTypes.RIDER_NAME, controller.getName());
        } else {
            ei.getMemoryStorage().put(CoreMemoryTypes.RIDER_NAME, null);
        }
    }

    /**
     * Resolves the seat offset to apply to a passenger for a specific seat index.
     */
    public @Nullable RideableComponent.Seat getRideSeatFor(int seatIndex) {
        RideableComponent r = getComponentRideable();
        if (r == null) return null;

        List<RideableComponent.Seat> seats = r.seats();
        if (seats == null || seats.isEmpty()) return null;

        if (seatIndex < 0 || seatIndex >= seats.size()) return null;
        return seats.get(seatIndex);
    }

    public Vector3f getSeatOffsetFor(int seatIndex, Entity passenger) {
        Vector3f seat = null;

        RideableComponent.Seat sm = getRideSeatFor(seatIndex);
        if (sm != null) seat = sm.position();
        if (seat == null) seat = new Vector3f(0f, 0f, 0f);

        Vector3f base = getMountedOffset(passenger);

        return new Vector3f(
                base.x + seat.x,
                base.y + seat.y,
                base.z + seat.z
        );
    }

    protected void applySeatOffsets() {
        for (int i = 0; i < passengers.size(); i++) {
            Entity p = passengers.get(i);

            p.setSeatPosition(getSeatOffsetFor(i, p));

            RideableComponent.Seat sm = getRideSeatFor(i);
            if (sm == null) continue;

            Vector3f raw = sm.position();
            if (raw == null) raw = new Vector3f(0f, 0f, 0f);
            p.seatRawOffset = raw;

            Float tpc = sm.thirdPersonCameraRadius();
            if (tpc != null) p.setSeatThirdPersonCameraRadius(tpc);

            Float relax = sm.cameraRelaxDistanceSmoothing();
            if (relax != null) p.setSeatCameraRelaxDistanceSmoothing(relax);

            Float lock = sm.lockRiderRotationDegrees();
            if (lock != null) p.setSeatLockRiderRotationDegrees(lock);

            Float rot = sm.rotateRiderByDegrees();
            if (rot != null) p.setSeatRotateRiderByDegrees(rot);
        }
    }


    /// ///////////////////////////////////////////
    /// ////////// DISMOUNT CHAIN /////////////////
    /// ///////////////////////////////////////////

    public boolean dismountEntity(Entity entity) {
        return this.dismountEntity(entity, true);
    }

    public boolean dismountEntity(Entity entity, boolean sendLinks) {
        int seatIndex = passengers.indexOf(entity);

        EntityVehicleExitEvent ev = new EntityVehicleExitEvent(entity, this);
        server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            if (seatIndex == 0) {
                broadcastLinkPacket(entity, ActorLinkType.RIDING);
            } else if (seatIndex != -1) {
                broadcastLinkPacket(entity, ActorLinkType.PASSENGER);
            }
            return false;
        }

        if (entity instanceof Player p) clearSeatData(p);
        if (sendLinks) {
            broadcastLinkPacket(entity, ActorLinkType.NONE, false);
        }

        entity.riding = null;
        entity.setDataFlag(ActorFlags.RIDING, false);
        entity.setSeatPosition(new Vector3f());
        entity.seatRawOffset = null;
        passengers.remove(entity);

        // Dismount placement 
        // TODO: need a few improvements when dismounting default, it will must select safe place
        Vector3 dismount = resolveDismountPosition(entity);
        if (entity instanceof Player p) {
            p.teleport(dismount);
            p.resetFallDistance();
        } else {
            entity.setPosition(dismount);
        }

        // Remaining passengers may need reordering/offsets
        updatePassengers(sendLinks, false);

        if (entity instanceof Player p) p.resetFallDistance();
        return true;
    }

    public RideableComponent.DismountMode getDismountMode() {
        RideableComponent r = getComponentRideable();
        return r != null ? r.dismountMode() : RideableComponent.DismountMode.DEFAULT;
    }

    protected Vector3 resolveDismountPosition(Entity passenger) {
        RideableComponent.DismountMode mode = getDismountMode();

        double cx = this.x;
        double cz = this.z;

        switch (mode) {
            case ON_TOP_CENTER: {
                double y = this.y + this.getHeight() + 0.01;
                return new Vector3(cx, y, cz);
            }
            case DEFAULT:
            default: {
                Vector3 found = findValidGroundDismountAround(passenger);
                if (found != null) return found;
                return new Vector3(cx, this.y + 0.5, cz);
            }
        }
    }

    protected Vector3 findValidGroundDismountAround(Entity passenger) {
        final double r = Math.max(0.6, (this.getWidth() * 0.5) + 0.3);

        final double[][] offsets = new double[][]{
                {r, 0}, {-r, 0}, {0, r}, {0, -r},
                {r, r}, {r, -r}, {-r, r}, {-r, -r}
        };

        for (double[] o : offsets) {
            double px = this.x + o[0];
            double pz = this.z + o[1];
            int baseY = (int) Math.floor(this.y);
            Vector3 pos = findGroundSpotAt(px, baseY, pz, passenger);
            if (pos != null) return pos;
        }
        return null;
    }

    protected Vector3 findGroundSpotAt(double x, int y, double z, Entity passenger) {
        final int bx = (int) Math.floor(x);
        final int bz = (int) Math.floor(z);

        final int minStandY = (int) Math.floor(this.y + 0.001);

        for (int dy = 2; dy >= -3; dy--) {
            int fy = y + dy;
            if (fy < minStandY) continue;

            Block ground = this.level.getBlock(bx, fy - 1, bz);
            if (ground == null || !ground.isSolid()) continue;
            if (ground.up().isSolid()) continue;
            if (!hasSpaceToDismount(x, fy, z, passenger)) continue;
            return new Vector3(x, fy + 0.01, z);
        }
        return null;
    }

    protected boolean hasSpaceToDismount(double x, int y, double z, Entity passenger) {
        Block feet = this.level.getBlock((int) Math.floor(x), y, (int) Math.floor(z));
        Block head = this.level.getBlock((int) Math.floor(x), y + 1, (int) Math.floor(z));

        if (feet != null && feet.isSolid()) return false;
        if (head != null && head.isSolid()) return false;

        return true;
    }

    public void clearSeatData(Player passenger) {
        passenger.setDataProperty(ActorDataTypes.SEAT_CAMERA_RELAX_DISTANCE_SMOOTHING, 0.0f, false);
        passenger.setDataProperty(ActorDataTypes.SEAT_LOCK_PASSENGER_ROTATION_DEGREES, 0.0f, false);
        passenger.setDataProperty(ActorDataTypes.SEAT_THIRD_PERSON_CAMERA_RADIUS, 0.0f, false);
        passenger.setDataProperty(ActorDataTypes.SEAT_ROTATION_OFFSET_DEGREES, 0.0f, false);
        passenger.setDataProperty(ActorDataTypes.SEAT_ROTATION_OFFSET, false, true);
        passenger.sendData(passenger);
    }


    /// ///////////////////////////////////////////
    /// ///// PASSENGER SEAT META SYNC ////////////
    /// ///////////////////////////////////////////

    public Vector3f getSeatPosition() {
        return Vector3f.fromNetwork(this.getDataProperty(ActorDataTypes.SEAT_OFFSET));
    }

    public void setSeatPosition(Vector3f pos) {
        this.setDataProperty(ActorDataTypes.SEAT_OFFSET, pos);
    }

    public @Nullable Float getSeatThirdPersonCameraRadius() {
        return this.getDataProperty(ActorDataTypes.SEAT_THIRD_PERSON_CAMERA_RADIUS);
    }

    public void setSeatThirdPersonCameraRadius(@Nullable Float v) {
        if (v == null) return;
        this.setDataProperty(ActorDataTypes.SEAT_THIRD_PERSON_CAMERA_RADIUS, v);
    }

    public @Nullable Float getSeatCameraRelaxDistanceSmoothing() {
        return this.getDataProperty(ActorDataTypes.SEAT_CAMERA_RELAX_DISTANCE_SMOOTHING);
    }

    public void setSeatCameraRelaxDistanceSmoothing(@Nullable Float v) {
        if (v == null) return;
        this.setDataProperty(ActorDataTypes.SEAT_CAMERA_RELAX_DISTANCE_SMOOTHING, v);
    }

    public @Nullable Float getSeatLockRiderRotationDegrees() {
        return this.getDataProperty(ActorDataTypes.SEAT_LOCK_PASSENGER_ROTATION_DEGREES);
    }

    public void setSeatLockRiderRotationDegrees(@Nullable Float v) {
        if (v == null) return;
        this.setDataProperty(ActorDataTypes.SEAT_LOCK_PASSENGER_ROTATION_DEGREES, v);
    }

    public @Nullable Float getSeatRotateRiderByDegrees() {
        return this.getDataProperty(ActorDataTypes.SEAT_ROTATION_OFFSET_DEGREES);
    }

    public void setSeatRotateRiderByDegrees(@Nullable Float v) {
        if (v == null) return;
        this.setDataProperty(ActorDataTypes.SEAT_ROTATION_OFFSET_DEGREES, v);
    }


    /// ///////////////////////////////////////////
    /// //////// UPDATE LINK PACKETS //////////////
    /// ///////////////////////////////////////////

    protected void broadcastLinkPacket(Entity rider, ActorLinkType type) {
        broadcastLinkPacket(rider, type, type != ActorLinkType.NONE);
    }

    protected void broadcastLinkPacket(Entity rider, ActorLinkType type, boolean riderInitiated) {
        final SetActorLinkPacket packet = new SetActorLinkPacket();
        packet.setLink(
                new ActorLink(
                        this.getId(),
                        rider.getId(),
                        type,
                        false,
                        riderInitiated,
                        0f
                )
        );
        Server.broadcastPacket(this.hasSpawned.values(), packet);
    }

    protected void broadcastLinksForAllPassengers(boolean riderInitiated) {
        int controlSeat = getControllingSeatIndex();

        for (int i = 0; i < passengers.size(); i++) {
            Entity p = passengers.get(i);
            broadcastLinkPacket(p, i == controlSeat ? ActorLinkType.RIDING : ActorLinkType.PASSENGER, riderInitiated);
        }
    }

    //////////////////////////////////////////////
    /////////// FINISH RIDABLE APIs //////////////
    //////////////////////////////////////////////


    /**
     * Returns the item identifier that can control this entity when held by a rider.
     * <p>
     * This corresponds to the {@code minecraft:item_controllable} component and
     * defines which item allows the rider to steer or trigger special movement
     * behavior (for example, warped fungus on a stick for striders).
     * </p>
     *
     * @return the controlling item identifier, or {@code null} if the entity is
     * not custom or no controllable item is defined.
     */
    public @Nullable String getItemControllable() {
        if (!isCustomEntity()) return null;

        CustomEntityDefinition.Meta.ItemControllable ic = meta().getItemControllable(CustomEntityComponents.ITEM_CONTROLLABLE);
        if (ic == null) return null;

        String v = ic.controlItems();
        return (v == null || v.isBlank()) ? null : v;
    }

    /**
     * Returns the {@link EquippableComponent} associated with this entity.
     * <p>
     * The equippable component defines which equipment slots the entity supports
     * and what items can be equipped into those slots.
     * </p>
     *
     * @return the {@link EquippableComponent} if defined for this custom entity,
     * or {@code null} if the entity is not custom or does not define the
     * {@code minecraft:equippable} component.
     */
    public @Nullable EquippableComponent getComponentEquippable() {
        if (!isCustomEntity()) return null;
        return meta().getEquippableComponent(CustomEntityComponents.EQUIPPABLE);
    }

    /**
     * Returns the {@code minecraft:health} component definition used by this entity.
     *
     * <p>This method defines the initial health model of the entity, including fixed
     * health values and ranged health values used for genetics/randomized initialization.</p>
     *
     * <p>Example (fixed value):</p>
     * <pre>{@code
     * @Override
     * public HealthComponent getComponentHealth() {
     *     return HealthComponent.value(8);
     * }
     * }</pre>
     *
     * <p>Example (range/genetics):</p>
     * <pre>{@code
     * @Override
     * public HealthComponent getComponentHealth() {
     *     return HealthComponent.range(15, 30);
     * }
     * }</pre>
     *
     * @return The resolved health component definition for this entity.
     */
    public HealthComponent getComponentHealth() {
        if (isCustomEntity() && meta().has(CustomEntityComponents.HEALTH)) {
            HealthComponent ht = meta().getDefinitionHealthComponent(CustomEntityComponents.HEALTH);
            if (ht != null) return ht;
        }
        return HealthComponent.defaults();
    }

    /**
     * Applies the initial health attribute state for a non-player entity.
     *
     * <p>This method is intended to run only during initial entity setup. If the entity
     * already has a persisted {@link Attribute#HEALTH} attribute loaded from NBT, the
     * existing values are preserved and no reroll is performed.</p>
     *
     * <p>When the resolved {@link HealthComponent} defines a range, two random values are
     * rolled to generate the entity genetics, which are then stored as
     * {@code defaultMinimum} and {@code defaultMaximum}. The higher value is also used as
     * the initial runtime max health and current health.</p>
     *
     * <p>When the resolved {@link HealthComponent} defines a fixed value, the fixed value
     * is used as the initial runtime max health, default health, and current health.
     */
    protected void applyInitialHealth() {
        if (this.isPlayer) return;

        // 0) Respect persisted NBT (never overwrite / never reroll)
        Attribute existing = this.attributes.get(Attribute.HEALTH);
        if (existing != null) return;

        // 1) Resolve component health
        HealthComponent health = getComponentHealth();
        if (health == null) return;

        RandomGenerator rnd = RandomGenerator.getDefault();

        float newDefaultMin = 0f;
        float newDefaultMax;
        float resolvedMaxHealth;

        if (health.hasRange()) {
            int value1 = health.resolveSpawnValue(rnd);
            int value2 = health.resolveSpawnValue(rnd);

            newDefaultMin = Math.min(value1, value2);
            newDefaultMax = Math.max(value1, value2);
            resolvedMaxHealth = newDefaultMax;
        } else {
            resolvedMaxHealth = Math.max(1, health.resolveSpawnValue(rnd));
            newDefaultMax = resolvedMaxHealth;
        }

        // 2) Persist attribute
        Attribute attr = Attribute.getAttribute(Attribute.HEALTH);
        if (attr == null) return;

        attr.setMinValue(0f);
        attr.setMaxValue(resolvedMaxHealth);

        attr.setDefaultMaximum(newDefaultMax);
        attr.setDefaultMinimum(newDefaultMin);

        attr.setDefaultValue(resolvedMaxHealth);
        attr.setValue(resolvedMaxHealth);

        this.attributes.put(attr.getId(), attr);
    }

    /**
     * Sets the current runtime maximum health of the entity.
     *
     * @param maxHealth The new runtime maximum health value.
     */
    public void setHealthMax(int maxHealth) {
        setMaxHealth(maxHealth);
        return;
    }

    /**
     * @deprecated Since 2.0.0 (2026-02-19).
     * Naming was standardized to {@link #setHealthMax(int)} so health-related methods are grouped
     * consistently under the {@code getHealth...}/{@code setHealth...} naming pattern.
     * <p>
     * Planned removal: after 6 months (>= 2026-08-19). <p>
     * Obs: When removing this method the logic under this must be moved to {@link #setHealthMax(int)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public void setMaxHealth(int maxHealth) {
        if (this.isPlayer) {
            this.maxHealth = maxHealth;
            return;
        }

        Attribute attr = this.getAttributes().get(Attribute.HEALTH);
        if (attr == null) {
            attr = Attribute.getAttribute(Attribute.HEALTH);
            if (attr == null) return;
        }

        float v = (float) maxHealth;
        attr.setMaxValue(v);
        this.attributes.put(attr.getId(), attr);
    }

    /**
     * Returns the current runtime maximum health of the entity.
     *
     * @return The current runtime maximum health.
     */
    public int getHealthMax() {
        return getMaxHealth();
    }

    /**
     * @deprecated Since 2.0.0 (2026-02-19).
     * Naming was standardized to {@link #getHealthMax()} so health-related methods are grouped
     * consistently under the {@code getHealth...}/{@code setHealth...} naming pattern.
     * <p>
     * Planned removal: after 6 months (>= 2026-08-19). <p>
     * Obs: When removing this method the logic under this must be moved to {@link #getHealthMax()}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public int getMaxHealth() {
        if (this.isPlayer) return maxHealth;

        Attribute attr = this.getAttributes().get(Attribute.HEALTH);
        if (attr != null) return (int) attr.getMaxValue();

        HealthComponent health = getComponentHealth();
        if (health != null) {
            if (health.isFixed()) {
                Integer v = health.value();
                if (v != null && v > 0) return v;
            }

            if (health.hasRange()) {
                Integer max = health.rangeMax();
                if (max != null && max > 0) return max;
            }
        }

        return DEFAULT_HEALTH;
    }

    /**
     * Returns the default/base health value of the entity.
     *
     * <p>For players, this returns the player max health field.</p>
     *
     * <p>For non-player entities, this first reads the persisted/runtime
     * {@link Attribute#HEALTH} default value. If the attribute is not yet initialized, the
     * value is derived from the resolved {@link HealthComponent} definition.</p>
     *
     * @return The default/base health value.
     */
    public int getHealthDefault() {
        if (this.isPlayer) return maxHealth;

        Attribute attr = this.getAttributes().get(Attribute.HEALTH);
        if (attr != null) return (int) attr.getDefaultValue();

        HealthComponent health = getComponentHealth();
        if (health != null) {
            if (health.isFixed()) {
                Integer v = health.value();
                if (v != null && v > 0) return v;
            }

            if (health.hasRange()) {
                Integer max = health.rangeMax();
                if (max != null && max > 0) return max;
            }
        }

        return DEFAULT_HEALTH;
    }

    /**
     * Sets the current runtime health of the entity.
     *
     * <p>If the provided health is less than {@code 1}, the entity is killed if still alive.
     * Otherwise, the value is clamped so it does not exceed the current runtime maximum
     * health.</p>
     *
     * @param health The new current health value.
     */
    public void setHealthCurrent(float health) {
        setHealth(health);
        return;
    }

    /**
     * @deprecated Since 2.0.0 (2026-02-19).
     * Naming was standardized to {@link #setHealthCurrent(float)} so health-related
     * methods follow the {@code setHealth...} naming pattern and are easier to
     * discover through API filtering.
     * <p>
     * Planned removal: after 6 months (>= 2026-08-19). <p>
     * Obs: When removing this method the logic under this must be moved to {@link #setHealthCurrent(float)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public void setHealth(float health) {
        if (this.health == health) return;

        if (health < 1) {
            if (this.isAlive()) this.kill();
        } else if (health <= this.getHealthMax() || health < this.health) {
            this.health = health;
        } else {
            this.health = this.getHealthMax();
        }

        Attribute attr = this.attributes.get(Attribute.HEALTH);
        if (attr != null) attr.setValue(this.health, true);
        setDataProperty(ActorDataTypes.STRUCTURAL_INTEGRITY, (int) this.health);
    }

    /**
     * Returns the current runtime health of the entity.
     *
     * <p>This value represents the entity live health state used for damage, healing,
     * death handling, and runtime persistence.</p>
     *
     * @return The current runtime health value.
     */
    public float getHealthCurrent() {
        return getHealth();
    }

    /**
     * @deprecated Since 2.0.0 (2026-02-19).
     * Naming was standardized to {@link #getHealthCurrent()} so health-related methods are grouped
     * consistently under the {@code getHealth...}/{@code setHealth...} naming pattern.
     * <p>
     * Planned removal: after 6 months (>= 2026-08-19). <p>
     * Obs: When removing this method the logic under this must be moved to {@link #getHealthCurrent()}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public float getHealth() {
        return health;
    }

    /**
     * Returns the default minimum health genetics value of the entity.
     *
     * <p>If a persisted {@link Attribute#HEALTH} attribute exists, this reads the stored
     * default minimum directly from the attribute.</p>
     *
     * <p>If the attribute is not yet initialized, the value is derived from the resolved
     * {@link HealthComponent}. Fixed health definitions return {@code 0}, while ranged
     * definitions return the configured lower bound.</p>
     *
     * @return The default minimum health genetics value, or {@code 0} when not applicable.
     */
    public float getHealthDefaultMin() {
        Attribute attr = this.getAttributes().get(Attribute.HEALTH);
        if (attr != null) {
            float v = attr.getDefaultMinimum();
            if (Float.isFinite(v) && v >= 0f) return v;
        }

        HealthComponent health = getComponentHealth();
        if (health == null) return 0f;

        if (health.hasRange()) {
            Integer min = health.rangeMin();
            if (min != null && min > 0) return min.floatValue();
        }

        return 0f;
    }

    /**
     * Returns the default maximum health genetics value of the entity.
     *
     * <p>If a persisted {@link Attribute#HEALTH} attribute exists, this reads the stored
     * default maximum directly from the attribute.</p>
     *
     * <p>If the attribute is not yet initialized, the value is derived from the resolved
     * {@link HealthComponent}. Ranged definitions return the configured upper bound, while
     * fixed definitions return the fixed configured value.</p>
     *
     * @return The default maximum health genetics value.
     */
    public float getHealthDefaultMax() {
        Attribute attr = this.getAttributes().get(Attribute.HEALTH);
        if (attr != null) {
            float v = attr.getDefaultMaximum();
            if (Float.isFinite(v) && v > 0f) return v;
        }

        HealthComponent health = getComponentHealth();
        if (health == null) return DEFAULT_HEALTH;

        if (health.hasRange()) {
            Integer max = health.rangeMax();
            if (max != null && max > 0) return max.floatValue();
        }

        if (health.isFixed()) {
            Integer value = health.value();
            if (value != null && value > 0) return value.floatValue();
        }

        return DEFAULT_HEALTH;
    }

    /**
     * Heals the entity using the provided regain health event.
     *
     * @param source The regain health event describing the heal operation.
     */
    public void heal(EntityRegainHealthEvent source) {
        this.server.getPluginManager().callEvent(source);
        if (source.isCancelled()) {
            return;
        }
        this.setHealthCurrent(this.getHealthCurrent() + source.getAmount());
    }

    /**
     * Heals the entity by the specified amount using
     * {@link EntityRegainHealthEvent#CAUSE_REGEN}.
     *
     * @param amount The amount of health to restore.
     */
    public void heal(float amount) {
        this.heal(new EntityRegainHealthEvent(this, amount, EntityRegainHealthEvent.CAUSE_REGEN));
    }

    /**
     * Retrieves the {@link HorseJumpStrengthComponent} definition for this entity.
     *
     * <p>This component defines the horse jump strength configuration used when
     * initializing {@link Attribute#HORSE_JUMP_STRENGTH}. It may represent either
     * a fixed jump strength value or a spawn-time range used to generate genetic
     * variation.</p>
     *
     * <p>The component is only available for custom entities that define
     * {@code minecraft:horse_jump_strength} in their entity metadata.</p>
     *
     * @return the horse jump strength component definition for this entity, or
     * {@code null} if the entity does not define this component.
     */
    protected @Nullable HorseJumpStrengthComponent getComponentHorseJumpStrength() {
        if (isCustomEntity() && meta().has(CustomEntityComponents.HORSE_JUMP_STRENGTH)) {
            HorseJumpStrengthComponent js = meta().getDefinitionJumpStrengthComponent(CustomEntityComponents.HORSE_JUMP_STRENGTH);
            if (js != null) return js;
        }
        return null;
    }

    /**
     * Applies the initial horse jump strength attribute state for a non-player entity.
     *
     * <p>This method is intended to run only during initial entity setup. If the entity
     * already has a persisted {@link Attribute#HORSE_JUMP_STRENGTH} attribute loaded
     * from NBT, the existing values are preserved and no reroll is performed.</p>
     *
     * <p>The jump strength configuration is read from the entity's
     * {@link HorseJumpStrengthComponent}. When the component defines a range,
     * two random values are rolled to generate the entity genetics. These values
     * are stored as {@code defaultMinimum} and {@code defaultMaximum}, representing
     * the genetic jump strength envelope for the entity.</p>
     *
     * <p>The higher of the two rolled values is used as the initial runtime jump
     * strength and is stored as both the attribute default and current value.</p>
     *
     * <p>When the component defines a fixed value, that value is used as the
     * entity's default and current horse jump strength.</p>
     *
     * <p>The resulting values are persisted in the
     * {@link Attribute#HORSE_JUMP_STRENGTH} attribute so that the generated
     * genetics remain consistent across saves.</p>
     */
    protected void applyInitialRideJumpStrength() {
        if (this.isPlayer) return;

        // 0) Respect persisted NBT (never overwrite / never reroll)
        Attribute existing = this.attributes.get(Attribute.HORSE_JUMP_STRENGTH);
        if (existing != null) return;

        // 1) Read meta range
        HorseJumpStrengthComponent jumpStrength = getComponentHorseJumpStrength();
        if (jumpStrength == null) return;

        RandomGenerator rnd = RandomGenerator.getDefault();

        float newDefaultMin = 0f;
        float newDefaultMax;
        float resolvedJumpStrength;

        // 2) Build per-entity genetics
        if (jumpStrength.hasRange()) {
            float value1 = jumpStrength.resolveSpawnValue(rnd);
            float value2 = jumpStrength.resolveSpawnValue(rnd);

            newDefaultMin = Math.min(value1, value2);
            newDefaultMax = Math.max(value1, value2);
            resolvedJumpStrength = newDefaultMax;
        } else {
            resolvedJumpStrength = jumpStrength.resolveSpawnValue(rnd);
            newDefaultMax = resolvedJumpStrength;
        }

        Attribute attr = Attribute.getAttribute(Attribute.HORSE_JUMP_STRENGTH);
        if (attr == null) return;

        // 3) Persist attribute
        attr.setMinValue(0f);
        attr.setMaxValue(resolvedJumpStrength);

        attr.setDefaultMaximum(newDefaultMax);
        attr.setDefaultMinimum(newDefaultMin);

        attr.setDefaultValue(resolvedJumpStrength);
        attr.setValue(resolvedJumpStrength);

        this.attributes.put(attr.getId(), attr);
    }

    /**
     * Returns the current runtime horse jump strength for this entity.
     *
     * <p>This value represents the effective jump strength currently stored in
     * {@link Attribute#HORSE_JUMP_STRENGTH}. If the entity does not have this
     * attribute, {@code 0.0f} is returned.</p>
     *
     * @return the current horse jump strength.
     */
    public float getRideJumpStrength() {
        Attribute attr = this.getAttributes().get(Attribute.HORSE_JUMP_STRENGTH);
        return attr != null ? attr.getValue() : 0.0f;
    }

    /**
     * Returns the current runtime horse jump strength for this entity.
     *
     * <p>This is an alias of {@link #getRideJumpStrength()}.</p>
     *
     * @return the current horse jump strength.
     */
    public float getHorseJumpStrength() {
        return getRideJumpStrength();
    }

    /**
     * Returns the default minimum horse jump strength for this entity.
     *
     * <p>This value represents the lower bound of the jump strength genetics
     * envelope generated during entity initialization. It corresponds to
     * {@link Attribute#getDefaultMinimum()} of
     * {@link Attribute#HORSE_JUMP_STRENGTH}.</p>
     *
     * <p>If the entity does not have a horse jump strength attribute or the
     * value is invalid, {@code 0.0f} is returned.</p>
     *
     * @return the default minimum horse jump strength (genetics bound).
     */
    public float getRideJumpStrengthDefaultMin() {
        Attribute attr = this.getAttributes().get(Attribute.HORSE_JUMP_STRENGTH);
        if (attr != null) {
            float v = attr.getDefaultMinimum();
            if (Float.isFinite(v) && v > 0f) return v;
        }
        return 0.0f;
    }

    /**
     * Returns the default maximum horse jump strength for this entity.
     *
     * <p>This value represents the upper bound of the jump strength genetics
     * envelope generated during entity initialization. It corresponds to
     * {@link Attribute#getDefaultMaximum()} of
     * {@link Attribute#HORSE_JUMP_STRENGTH}.</p>
     *
     * <p>If the entity does not have a horse jump strength attribute or the
     * value is invalid, {@code 0.0f} is returned.</p>
     *
     * @return the default maximum horse jump strength (genetics bound).
     */
    public float getRideJumpStrengthDefaultMax() {
        Attribute attr = this.getAttributes().get(Attribute.HORSE_JUMP_STRENGTH);
        if (attr != null) {
            float v = attr.getDefaultMaximum();
            if (Float.isFinite(v) && v > 0f) return v;
        }
        return 0.0f;
    }

    /**
     * Returns the {@link DashActionComponent} defined for this custom entity.
     * <p>
     * This corresponds to the {@code minecraft:dash} action component and
     * defines dash-related behavior such as cooldowns and movement boosts.
     * </p>
     *
     * @return the {@link DashActionComponent} if defined and not empty,
     * or {@code null} if the entity is not custom or the component
     * is not present.
     */
    public @Nullable DashActionComponent getComponentDashAction() {
        if (isCustomEntity() && meta().has(CustomEntityComponents.DASH_ACTION)) {
            var dash = meta().getDefinitionDashActionComponent(CustomEntityComponents.DASH_ACTION);
            if (dash != null && !dash.isEmpty()) return dash;
        }

        return null;
    }

    /**
     * Returns the {@link BoostableComponent} defined for this custom entity.
     * <p>
     * This corresponds to the {@code minecraft:boostable} component and
     * controls temporary speed boosts that can be triggered while riding
     * the entity.
     * </p>
     *
     * @return the {@link BoostableComponent} if defined and not empty,
     * or {@code null} if the entity is not custom or the component
     * is not present.
     */
    public @Nullable BoostableComponent getComponentBoostable() {
        if (isCustomEntity() && meta().has(CustomEntityComponents.BOOSTABLE)) {
            var boost = meta().getDefinitionBoostableComponent(CustomEntityComponents.BOOSTABLE);
            if (boost != null && !boost.isEmpty()) return boost;
        }

        return null;
    }

    /**
     * Checks whether this entity has the {@code minecraft:boostable} component.
     *
     * @return {@code true} if the entity defines a boostable component,
     * otherwise {@code false}.
     */
    public boolean isBoostable() {
        return getComponentBoostable() != null;
    }

    public int getBoostableTicks() {
        return this.boostableTicks;
    }

    public boolean isBoosting() {
        return this.boostableTicks > 0;
    }

    /**
     * Starts boost using duration in seconds.
     *
     * @param durationSeconds Boost duration in seconds.
     */
    public void setBoostableDuration(float durationSeconds) {
        if (!Float.isFinite(durationSeconds) || durationSeconds <= 0f) {
            this.boostableTicks = -1;
            return;
        }
        this.boostableTicks = Math.max(1, (int) (durationSeconds * 20f));
    }

    /**
     * Stops the current boost immediately.
     */
    public void clearBoostable() {
        this.boostableTicks = -1;
    }

    /**
     * Returns the {@link InventoryComponent} defined for this custom entity.
     * <p>
     * This corresponds to the {@code minecraft:inventory} component and defines
     * the inventory container attached to the entity.
     * </p>
     *
     * @return the {@link InventoryComponent} if defined and not empty,
     * or {@code null} if the entity is not custom or the component
     * is not present.
     */
    public @Nullable InventoryComponent getComponentInventory() {
        if (isCustomEntity() && meta().has(CustomEntityComponents.INVENTORY)) {
            var inv = meta().getDefinitionInventoryComponent(CustomEntityComponents.INVENTORY);
            if (inv != null && !inv.isEmpty()) return inv;
        }

        return null;
    }

    /**
     * Checks whether this entity defines an inventory component.
     *
     * @return {@code true} if the entity has an {@link InventoryComponent},
     * otherwise {@code false}.
     */
    public boolean hasInventory() {
        return getComponentInventory() != null;
    }

    public void updateInventoryFlags() {
        if (!hasInventory()) {
            this.setDataProperty(ActorDataTypes.CONTAINER_TYPE, (byte) 0);
            this.setDataProperty(ActorDataTypes.CONTAINER_SIZE, 0);
            this.setDataProperty(ActorDataTypes.CONTAINER_STRENGTH_MODIFIER, 0);
            this.setDataFlag(ActorFlags.CONTAINER_IS_PRIVATE, false);
            return;
        }

        if ((this instanceof EntityLiving ei) && ei.isTamed()) {
            this.setDataProperty(ActorDataTypes.CONTAINER_TYPE, (byte) getComponentInventory().typeId());
            this.setDataProperty(ActorDataTypes.CONTAINER_SIZE, getComponentInventory().size());
            this.setDataProperty(ActorDataTypes.CONTAINER_STRENGTH_MODIFIER, getComponentInventory().strengthModifier());
            this.setDataFlag(ActorFlags.CONTAINER_IS_PRIVATE, getComponentInventory().isRestrictedToOwner());
            return;
        }

        if (this instanceof EntityVehicle) {
            this.setDataProperty(ActorDataTypes.CONTAINER_TYPE, (byte) getComponentInventory().typeId());
            this.setDataProperty(ActorDataTypes.CONTAINER_SIZE, getComponentInventory().size());
            this.setDataProperty(ActorDataTypes.CONTAINER_STRENGTH_MODIFIER, 0);
            this.setDataFlag(ActorFlags.CONTAINER_IS_PRIVATE, false);
        }
    }

    /**
     * Returns the {@link HomeComponent} defined for this custom entity.
     * <p>
     * This corresponds to the {@code minecraft:home} component and defines
     * the entity's home location behavior and movement restrictions.
     * </p>
     *
     * @return the {@link HomeComponent} if defined and present,
     * or {@code null} if the entity is not custom or the component
     * is not configured.
     */
    public @Nullable HomeComponent getComponentHome() {
        if (isCustomEntity() && meta().has(CustomEntityComponents.HOME)) {
            HomeComponent hm = meta().getDefinitionHomeComponent(CustomEntityComponents.HOME);
            if (hm != null && hm.isPresent()) return hm;
        }
        return null;
    }

    /**
     * Checks whether this entity defines a home component.
     *
     * @return {@code true} if the entity has a {@link HomeComponent},
     * otherwise {@code false}.
     */
    public boolean hasHome() {
        return getComponentHome() != null;
    }

    /**
     * Returns the {@link BreedableComponent} defined for this custom entity.
     * <p>
     * This corresponds to the {@code minecraft:breedable} component and defines
     * breeding rules such as allowed breeding items, cooldowns, and offspring behavior.
     * </p>
     *
     * @return the {@link BreedableComponent} if defined and not empty,
     * or {@code null} if the entity is not custom or the component
     * is not configured.
     */
    public @Nullable BreedableComponent getComponentBreedable() {
        if (isCustomEntity() && meta().has(CustomEntityComponents.BREEDABLE)) {
            BreedableComponent bd = meta().getDefinitionBreedableComponent(CustomEntityComponents.BREEDABLE);
            if (bd != null && !bd.isEmpty()) return bd;
        }
        return null;
    }

    /**
     * Checks whether this entity supports breeding behavior.
     *
     * @return {@code true} if the entity defines a valid {@link BreedableComponent},
     * otherwise {@code false}.
     */
    public boolean isBreedable() {
        BreedableComponent breedable = getComponentBreedable();
        return breedable != null && !breedable.isEmpty();
    }

    /**
     * Checks whether this entity is currently marked as pregnant.
     * <p>
     * This value is stored in the entity AI memory under
     * {@link CoreMemoryTypes#IS_PREGNANT}.
     * </p>
     *
     * @return {@code true} if the entity is pregnant, otherwise {@code false}.
     */
    public boolean isPregnant() {
        if (!(this instanceof EntityIntelligent ei)) return false;
        return ei.getMemoryStorage().get(CoreMemoryTypes.IS_PREGNANT);
    }

    /**
     * Sets the pregnancy state of this entity.
     * <p>
     * This updates both the AI memory and the {@link ActorFlags#IS_PREGNANT}
     * entity data flag for client synchronization.
     * </p>
     *
     * @param value {@code true} to mark the entity as pregnant, otherwise {@code false}.
     */
    public void setPregnant(boolean value) {
        if (!(this instanceof EntityIntelligent ei)) return;
        ei.getMemoryStorage().put(CoreMemoryTypes.IS_PREGNANT, value);
        ei.setDataFlag(ActorFlags.IS_PREGNANT, value);
    }

    /**
     * Checks whether this entity is currently angry.
     * <p>
     * This state is stored in the entity AI memory under
     * {@link CoreMemoryTypes#IS_ANGRY}.
     * </p>
     *
     * @return {@code true} if the entity is angry, otherwise {@code false}.
     */
    public boolean isAngry() {
        if (!(this instanceof EntityIntelligent ei)) return false;
        return ei.getMemoryStorage().get(CoreMemoryTypes.IS_ANGRY);
    }

    /**
     * Sets the angry state of this entity.
     * <p>
     * This updates both the AI memory and the {@link ActorFlags#ANGRY}
     * entity data flag for client synchronization.
     * </p>
     *
     * @param value {@code true} to mark the entity as angry, otherwise {@code false}.
     */
    public void setAngry(boolean value) {
        if (!(this instanceof EntityIntelligent ei)) return;
        ei.getMemoryStorage().put(CoreMemoryTypes.IS_ANGRY, value);
        ei.setDataFlag(ActorFlags.ANGRY, value);
    }

    /**
     * Marks this entity as angry and assigns a specific attack target.
     * <p>
     * The target is stored in the AI memory under
     * {@link CoreMemoryTypes#ATTACK_TARGET}, and the angry state is enabled.
     * </p>
     *
     * @param entity the entity that becomes the attack target.
     */
    public void setAngryOnTarget(Entity entity) {
        if (!(this instanceof EntityIntelligent ei)) return;
        ei.getMemoryStorage().put(CoreMemoryTypes.ATTACK_TARGET, entity);
        ei.getMemoryStorage().put(CoreMemoryTypes.IS_ANGRY, true);
        ei.setDataFlag(ActorFlags.ANGRY, true);
    }

    /**
     * Returns the {@link NameableComponent} defined for this custom entity.
     * <p>
     * This corresponds to the {@code minecraft:nameable} component and controls
     * whether the entity can be renamed with name tags.
     * </p>
     *
     * @return the {@link NameableComponent} if defined and not empty,
     * otherwise a default nameable configuration.
     */
    public NameableComponent getComponentNameable() {
        if (isCustomEntity() && meta().has(CustomEntityComponents.NAMEABLE)) {
            NameableComponent nm = meta().getDefinitionNameableComponent(CustomEntityComponents.NAMEABLE);
            if (nm != null && !nm.isEmpty()) return nm;
        }
        return DEFAULT_NAMEABLE;
    }

    /**
     * Checks whether this entity can be renamed using a name tag.
     *
     * @return {@code true} if name tag renaming is allowed, otherwise {@code false}.
     */
    public boolean isNameable() {
        return getComponentNameable().resolvedAllowNameTagRenaming();
    }

    public boolean hasCustomName() {
        return !this.getNameTag().isEmpty();
    }

    public String getNameTag() {
        return this.getDataProperty(ActorDataTypes.NAME, "");
    }

    public void setNameTag(String name) {
        this.setDataProperty(ActorDataTypes.NAME, name);
    }

    public boolean isNameTagVisible() {
        return this.getDataFlag(ActorFlags.CAN_SHOW_NAME);
    }

    public void setNameTagVisible(boolean value) {
        this.setDataFlag(ActorFlags.CAN_SHOW_NAME, value);
    }

    public boolean isNameTagAlwaysVisible() {
        return this.getDataProperty(ActorDataTypes.NAMETAG_ALWAYS_SHOW, (byte) 0) == 1;
    }

    public void setNameTagAlwaysVisible(boolean value) {
        this.setDataProperty(ActorDataTypes.NAMETAG_ALWAYS_SHOW, value ? (byte) 1 : 0);
    }

    /**
     * Returns the {@link HealableComponent} defined for this custom entity.
     * <p>
     * This corresponds to the {@code minecraft:healable} component and defines
     * which items can restore health to the entity and how healing is applied.
     * </p>
     *
     * @return the {@link HealableComponent} if defined and not empty,
     * or {@code null} if the entity is not custom or the component
     * is not configured.
     */
    public HealableComponent getComponentHealable() {
        if (isCustomEntity() && meta().has(CustomEntityComponents.HEALABLE)) {
            HealableComponent hl = meta().getDefinitionHealableComponent(CustomEntityComponents.HEALABLE);
            if (hl != null && !hl.isEmpty()) return hl;
        }
        return null;
    }

    /**
     * Checks whether this entity supports healing via items.
     *
     * @return {@code true} if the entity defines a valid {@link HealableComponent},
     * otherwise {@code false}.
     */
    public boolean isHealable() {
        HealableComponent healable = getComponentHealable();
        return healable != null && !healable.isEmpty();
    }

    /**
     * Returns the {@link AgeableComponent} defined for this custom entity.
     * <p>
     * This corresponds to the {@code minecraft:ageable} component and defines
     * growth behavior such as baby/adult state transitions and growth timing.
     * </p>
     *
     * @return the {@link AgeableComponent} if defined and not empty,
     * or {@code null} if the entity is not custom or the component
     * is not configured.
     */
    public AgeableComponent getComponentAgeable() {
        if (isCustomEntity() && meta().has(CustomEntityComponents.AGEABLE)) {
            AgeableComponent hl = meta().getDefinitionAgeableComponent(CustomEntityComponents.AGEABLE);
            if (hl != null && !hl.isEmpty()) return hl;
        }
        return null;
    }

    /**
     * Checks whether this entity supports age and growth behavior.
     *
     * @return {@code true} if the entity defines a valid {@link AgeableComponent},
     * otherwise {@code false}.
     */
    public boolean isAgeable() {
        AgeableComponent ageable = getComponentAgeable();
        return ageable != null && !ageable.isEmpty();
    }

    /**
     * Checks whether the growth process of this entity is currently paused.
     * <p>
     * Growth can be paused only for baby entities and is stored in the entity
     * NBT using {@link #TAG_ENTITY_GROW_PAUSED}.
     * </p>
     *
     * @return {@code true} if growth is paused, otherwise {@code false}.
     */
    public final boolean isGrowthPaused() {
        if (!isAgeable() || !isBaby()) return false;
        return this.namedTag.containsKey(TAG_ENTITY_GROW_PAUSED) && this.namedTag.getBoolean(TAG_ENTITY_GROW_PAUSED);
    }

    /**
     * Checks whether this entity is currently a baby.
     *
     * @return {@code true} if the {@link ActorFlags#BABY} data flag is set,
     * otherwise {@code false}.
     */
    public boolean isBaby() {
        return ((Entity) this).getDataFlag(ActorFlags.BABY);
    }

    /**
     * Sets whether this entity is a baby.
     * <p>
     * Updates the {@link ActorFlags#BABY} data flag and applies the corresponding scale.
     * For ageable entities, this also initializes or clears growth-related NBT
     * (birth date, remaining growth ticks, and pause state) and marks growth data as dirty.
     * </p>
     *
     * @param value {@code true} to set as baby, {@code false} to set as adult.
     */
    public void setBaby(boolean value) {
        this.setDataFlag(ActorFlags.BABY, value);
        this.setScale(value ? 0.5f : 1f);

        if (this.isAgeable()) {
            if (!value) {
                this.namedTag.remove(TAG_ENTITY_GROW_LEFT);
                this.namedTag.remove(TAG_ENTITY_GROW_PAUSED);
                ticksGrowLeft = -1;
                growDirty = true;
            } else {
                if (!this.namedTag.containsKey(Entity.TAG_ENTITY_BIRTH_DATE)) {
                    long nowSec = System.currentTimeMillis() / 1000L;
                    this.namedTag = this.namedTag.toBuilder().putLong(Entity.TAG_ENTITY_BIRTH_DATE, nowSec).build();
                }
                ticksGrowLeft = -1;
                growDirty = true;
            }
        }
    }

    /**
     * Sets whether growth is paused for this entity.
     * <p>
     * This applies only to baby, ageable entities and is stored in NBT using
     * {@link #TAG_ENTITY_GROW_PAUSED}. Growth data is marked as dirty after updating.
     * </p>
     *
     * @param paused {@code true} to pause growth, {@code false} to resume growth.
     */
    public final void setGrowthPaused(boolean paused) {
        if (!isAgeable() || !isBaby()) return;

        if (paused) {
            this.namedTag = this.namedTag.toBuilder().putBoolean(TAG_ENTITY_GROW_PAUSED, true).build();
        } else {
            this.namedTag.remove(TAG_ENTITY_GROW_PAUSED);
        }
        growDirty = true;
    }

    public int getBabyGrowTotalTicks() {
        AgeableComponent ageable = getComponentAgeable();
        if (ageable == null || ageable.isEmpty()) return -1;

        float d = ageable.resolvedDuration();
        if (d == -1.0f) return -1;

        int total = (int) (d * 20f);
        if (total < 0) total = 0;
        return total;
    }

    public final int getTicksGrowLeft() {
        if (!isAgeable() || !isBaby()) return -1;
        ensureGrowLoaded();
        return ticksGrowLeft;
    }

    public final void reduceGrowLeft(int ticks) {
        if (!isAgeable() || !isBaby() || isGrowthPaused()) return;

        int total = getBabyGrowTotalTicks();
        if (total == -1) return;

        ensureGrowLoaded();
        if (ticksGrowLeft <= 0) return;

        ticksGrowLeft = Math.max(0, ticksGrowLeft - Math.max(0, ticks));
        growDirty = true;
    }

    protected final void ensureGrowLoaded() {
        if (!isAgeable() || !isBaby()) return;
        if (ticksGrowLeft >= 0) return;

        int total = getBabyGrowTotalTicks();
        if (total == -1) {
            this.namedTag.remove(TAG_ENTITY_GROW_LEFT);
            ticksGrowLeft = -1;
            growDirty = false;
            return;
        }

        int left = this.namedTag.containsKey(TAG_ENTITY_GROW_LEFT) ? this.namedTag.getInt(TAG_ENTITY_GROW_LEFT) : total;

        if (left < 0 || left > total) left = total;
        ticksGrowLeft = left;
    }

    public final void babyFeedGrowBoost(Item item) {
        if (!isAgeable() || !isBaby() || isGrowthPaused()) return;
        if (item.isNull()) return;

        AgeableComponent ageable = getComponentAgeable();
        if (ageable == null || ageable.isEmpty()) return;

        int total = getBabyGrowTotalTicks();
        if (total == -1) return;

        ensureGrowLoaded();
        if (ticksGrowLeft <= 0) return;

        String id = item.getId();

        Float growth = null;
        for (AgeableComponent.FeedItem fi : ageable.resolvedFeedItems()) {
            if (fi == null || fi.isEmpty()) continue;
            if (id.equals(fi.item())) {
                growth = fi.resolvedGrowth();
                break;
            }
        }

        total = Math.max(1, total);

        if (growth == null) return;

        int reduce = (int) Math.floor(growth * total);
        reduce = Math.max(1, reduce);

        ticksGrowLeft = Math.max(0, ticksGrowLeft - reduce);
        growDirty = true;
        this.playBabyGrowthParticle();

        if (ticksGrowLeft == 0) {
            setBaby(false);
        }
    }

    protected final void restoreBabyStateFromNbt() {
        if (!isAgeable()) return;

        int total = getBabyGrowTotalTicks();
        if (total == -1) {
            namedTag.remove(TAG_ENTITY_GROW_LEFT);
            namedTag.remove(TAG_ENTITY_GROW_PAUSED);
            ticksGrowLeft = -1;
            growDirty = false;
            setDataFlag(ActorFlags.BABY, false, false);
            setScale(1f);
            return;
        }

        if (!namedTag.containsKey(TAG_ENTITY_GROW_LEFT)) {
            setDataFlag(ActorFlags.BABY, false, false);
            setScale(1f);
            ticksGrowLeft = -1;
            return;
        }

        int left = namedTag.getInt(TAG_ENTITY_GROW_LEFT);

        if (left < 0) left = 0;
        if (left > total) left = total;

        boolean baby = left > 0;

        setDataFlag(ActorFlags.BABY, baby, false);
        setScale(baby ? 0.5f : 1f);

        ticksGrowLeft = left;

        if (!baby) {
            namedTag.remove(TAG_ENTITY_GROW_PAUSED);
        }
    }

    public final void playBabyGrowthParticle() {
        for (int i = 0; i < 6; i++) {
            this.level.addParticle(
                    new HappyVillagerParticle(
                            this.add(
                                    Utils.rand(-0.3, 0.3),
                                    Utils.rand(0.3, 1.0),
                                    Utils.rand(-0.3, 0.3)
                            )
                    )
            );
        }
    }


    public boolean canSit() {
        return (this instanceof EntityCanSit);
    }

    public boolean isSitting() {
        if (!(this instanceof EntityIntelligent ei)) return false;
        return ei.getMemoryStorage().get(CoreMemoryTypes.IS_SITTING);
    }

    /**
     * Retrieves the {@link MovementComponent} definition for this entity.
     *
     * <p>This component defines the movement speed configuration used when initializing
     * the entity's {@link Attribute#MOVEMENT_SPEED}. It may represent either a fixed
     * movement speed or a spawn-time range used to generate genetic variation.</p>
     *
     * @return the movement component definition for this entity, or {@code null}
     * if the entity does not define a movement component.
     */
    protected @Nullable MovementComponent getComponentMovement() {
        if (isCustomEntity() && meta().has(CustomEntityComponents.MOVEMENT)) {
            MovementComponent mv = meta().getDefinitionMovementComponent(CustomEntityComponents.MOVEMENT);
            if (mv != null) return mv;
        }
        return null;
    }

    /**
     * Applies the initial movement speed attribute state for a non-player entity.
     *
     * <p>This method is intended to run only during initial entity setup. If the entity
     * already has a persisted {@link Attribute#MOVEMENT_SPEED} attribute loaded from NBT,
     * the existing values are preserved and no reroll is performed.</p>
     *
     * <p>When the resolved {@link MovementComponent} defines a range, two random values are
     * rolled to generate the entity genetics. These values are stored as
     * {@code defaultMinimum} and {@code defaultMaximum}, representing the genetic movement
     * envelope for the entity. The higher value is used as the initial runtime movement
     * speed and stored as both the attribute default and current value.</p>
     *
     * <p>When the resolved {@link MovementComponent} defines a fixed value, that value is
     * used as the entity's default and current movement speed.</p>
     *
     * <p>The resolved movement speed is stored in the {@link Attribute#MOVEMENT_SPEED}
     * attribute and cached into {@link #movementSpeed} for fast runtime access by
     * physics and AI systems.</p>
     */
    protected void applyInitialMovementSpeed() {
        if (this.isPlayer) return;

        // 0) If NBT already has it, respect it
        Attribute existing = this.attributes.get(Attribute.MOVEMENT_SPEED);
        if (existing != null) {
            this.movementSpeed = existing.getDefaultValue();
            return;
        }

        MovementComponent movement = getComponentMovement();
        if (movement == null) return;

        RandomGenerator rnd = RandomGenerator.getDefault();

        float newDefaultMin = 0f;
        float newDefaultMax;
        float resolvedSpeed;

        if (movement.hasRange()) {
            float value1 = movement.resolveSpawnValue(rnd);
            float value2 = movement.resolveSpawnValue(rnd);

            newDefaultMin = Math.min(value1, value2);
            newDefaultMax = Math.max(value1, value2);
            resolvedSpeed = newDefaultMax;
        } else {
            resolvedSpeed = movement.resolveSpawnValue(rnd);
            newDefaultMax = resolvedSpeed;
        }

        Attribute attr = Attribute.getAttribute(Attribute.MOVEMENT_SPEED);
        if (attr == null) return;

        attr.setMinValue(0f);
        attr.setMaxValue(resolvedSpeed);

        attr.setDefaultMaximum(newDefaultMax);
        attr.setDefaultMinimum(newDefaultMin);

        attr.setDefaultValue(resolvedSpeed);
        attr.setValue(resolvedSpeed);

        this.attributes.put(attr.getId(), attr);
        this.movementSpeed = resolvedSpeed;
    }

    /**
     * Returns the current runtime movement speed for this entity.
     *
     * <p>This value represents the effective movement speed currently used by
     * physics and AI systems. It reflects the resolved movement speed stored
     * during entity initialization and may differ from the configured default
     * if modified dynamically at runtime.</p>
     *
     * @return the current movement speed.
     */
    public float getMovementSpeed() {
        return this.movementSpeed;
    }

    /**
     * Returns the default movement speed configured for this entity.
     *
     * <p>This method resolves the base movement speed used by the entity,
     * considering both the {@link MovementComponent} configuration and any
     * persisted {@link Attribute#MOVEMENT_SPEED} attribute values.</p>
     *
     * @return the resolved default movement speed for the entity.
     */
    public float getMovementSpeedDefault() {
        return getDefaultSpeed();
    }

    /**
     * @deprecated Since 2.0.0 (2026-02-19).
     * Naming was standardized to {@link #getMovementSpeedDefault()} so movement-related methods are grouped
     * consistently under the {@code getMovement...}/{@code setMovement...} naming pattern.
     * <p>
     * Planned removal: after 6 months (>= 2026-08-19). <p>
     * Obs: When removing this method the logic under this must be moved to {@link #getMovementSpeedDefault()}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public float getDefaultSpeed() {
        if (this.isPlayer) return DEFAULT_SPEED;

        MovementComponent mv = getComponentMovement();
        if (mv != null) {
            if (mv.isFixed()) {
                Float v = mv.value();
                if (v != null) return v;
            }

            if (mv.hasRange()) {
                Attribute attr = this.getAttributes().get(Attribute.MOVEMENT_SPEED);
                if (attr != null) return attr.getDefaultValue();

                Float max = mv.rangeMax();
                if (max != null) return max;
            }
        }

        Attribute attr = this.getAttributes().get(Attribute.MOVEMENT_SPEED);
        if (attr != null) return attr.getDefaultValue();

        return 0f;
    }

    /**
     * Returns the default minimum movement speed for this entity.
     *
     * <p>This value represents the lower bound of the movement genetics
     * envelope generated during entity initialization. It corresponds to
     * {@link Attribute#getDefaultMinimum()} of {@link Attribute#MOVEMENT_SPEED}.</p>
     *
     * <p>If the entity does not have a movement attribute or the value is
     * invalid, {@code 0f} is returned.</p>
     *
     * @return the default minimum movement speed (genetics bound).
     */
    public float getMovementSpeedDefaultMin() {
        Attribute attr = this.getAttributes().get(Attribute.MOVEMENT_SPEED);
        if (attr != null) {
            float v = attr.getDefaultMinimum();
            if (Float.isFinite(v) && v > 0f) return v;
        }
        return 0f;
    }

    /**
     * Returns the default maximum movement speed for this entity.
     *
     * <p>This value represents the upper bound of the movement genetics
     * envelope generated during entity initialization. It corresponds to
     * {@link Attribute#getDefaultMaximum()} of {@link Attribute#MOVEMENT_SPEED}.</p>
     *
     * <p>If the entity does not have a movement attribute or the value is
     * invalid, the entity's resolved default movement speed is returned.</p>
     *
     * @return the default maximum movement speed (genetics bound).
     */
    public float getMovementSpeedDefaultMax() {
        Attribute attr = this.getAttributes().get(Attribute.MOVEMENT_SPEED);
        if (attr != null) {
            float v = attr.getDefaultMaximum();
            if (Float.isFinite(v) && v > 0f) return v;
        }
        return getDefaultSpeed();
    }

    /**
     * Returns the default flying speed for this entity.
     *
     * <p>This value represents the baseline movement speed used when the
     * entity is flying. It is independent of the ground movement speed
     * defined by {@link MovementComponent}.</p>
     *
     * @return the default flying speed.
     */
    public float getDefaultFlyingSpeed() {
        return DEFAULT_FLYING_SPEED;
    }

    /**
     * Returns the default underwater movement speed for this entity.
     *
     * <p>This value represents the baseline movement speed used when the
     * entity moves through water.</p>
     *
     * @return the default underwater movement speed.
     */
    public float getDefaultUnderWaterSpeed() {
        return DEFAULT_UNDER_WATER_SPEED;
    }

    /**
     * Returns the default movement speed for this entity while inside lava.
     *
     * <p>This value represents the baseline movement speed used when the
     * entity moves through lava.</p>
     *
     * @return the default lava movement speed.
     */
    public float getDefaultLavaMovementSpeed() {
        return DEFAULT_LAVA_MOVEMENT_SPEED;
    }

    /**
     * @deprecated Movement multipliers should be implemented in behavior executors.
     *
     * <p>
     * This method is kept for backward compatibility only.
     * Bedrock entity definitions do not store movement multipliers;
     * speed scaling is controlled by runtime behaviors such as
     * follow, tempt, boost, or rider input.
     * </p>
     *
     * <p>
     * This field is currently used as a server-side tuning knob for
     * legacy entities and will be removed once all movement behaviors
     * implement proper runtime multipliers.
     * </p>
     * <p>
     * Planned removal: after behavior parity is complete (>= 2026-09-05).
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public float getSpeedMultiplier() {
        if (isCustomEntity()) {
            Float sm = meta().getSpeedMultiplier(CustomEntityComponents.DEFAULT_MOVEMENT_MULTIPLIER);
            if (sm != null) return sm;
        }
        return 1.0f;
    }

    protected void applyInitialPowerJumpFlags() {
        if (this.isPlayer) return;
        if (!isRideable()) return;
        if (!hasJumpStrength()) return;

        this.setDataFlag(ActorFlags.CAN_POWER_JUMP, true);
        this.entityDataMap.put(ActorDataTypes.CHARGE_AMOUNT, (byte) 0);
    }

    public boolean hasGroundInputControlsMeta() {
        if (!isCustomEntity()) return false;
        return meta().has(CustomEntityComponents.INPUT_GROUND_CONTROLLED);
    }

    public boolean hasAirInputControlsMeta() {
        if (!isCustomEntity()) return false;
        return meta().has(CustomEntityComponents.INPUT_AIR_CONTROLLED);
    }

    protected boolean hasAnyInputControlMeta() {
        return hasGroundInputControlsMeta() || hasAirInputControlsMeta();
    }

    protected void applyInitialInputControlFlags() {
        if (this.isPlayer) return;
        if (!isRideable()) return;
        if (!hasAnyInputControlMeta()) return;

        if (hasAirInputControlsMeta()) {
            this.setDataFlag(ActorFlags.WASD_FREE_CAMERA_CONTROLLED, true, false);
            this.setDataFlag(ActorFlags.WASD_CONTROLLED, false, false);

            this.setDataFlag(ActorFlags.DOES_SERVER_AUTH_ONLY_DISMOUNT, true, false);
            this.setDataFlag(ActorFlags.CAN_USE_VERTICAL_MOVEMENT_ACTION, true, true);
        } else {
            this.setDataFlag(ActorFlags.WASD_CONTROLLED, true, false);
            this.setDataFlag(ActorFlags.WASD_FREE_CAMERA_CONTROLLED, false, false);

            this.setDataFlag(ActorFlags.DOES_SERVER_AUTH_ONLY_DISMOUNT, false, false);
            this.setDataFlag(ActorFlags.CAN_USE_VERTICAL_MOVEMENT_ACTION, false, true);
        }
    }

    /**
     * Relative offset used for attaching follower entities (fishing hook, lead, etc).
     */
    public Vector3f getAttachmentOffset(Entity follower) {
        return new Vector3f(0f, getHeight() * SEATED_FACTOR, 0f);
    }

    /**
     * Returns the {@link TameableComponent} defined for this custom entity.
     * <p>
     * This corresponds to the {@code minecraft:tameable} component and defines
     * taming behavior such as required items and taming conditions.
     * </p>
     *
     * @return the {@link TameableComponent} if defined and not empty,
     * or {@code null} if the entity is not custom or the component
     * is not configured.
     */
    public TameableComponent getComponentTameable() {
        if (isCustomEntity() && meta().has(CustomEntityComponents.TAMEABLE)) {
            TameableComponent tm = meta().getDefinitionTameableComponent(CustomEntityComponents.TAMEABLE);
            if (tm != null && !tm.isEmpty()) return tm;
        }
        return null;
    }

    /**
     * Checks whether this entity supports taming behavior.
     *
     * @return {@code true} if the entity defines a valid {@link TameableComponent},
     * otherwise {@code false}.
     */
    public boolean isTameable() {
        TameableComponent tameable = getComponentTameable();
        return tameable != null && !tameable.isEmpty();
    }

    /**
     * Checks whether this entity is currently tamed.
     *
     * @return {@code true} if the {@link ActorFlags#TAMED} data flag is set,
     * otherwise {@code false}.
     */
    public boolean isTamed() {
        return this.getDataFlag(ActorFlags.TAMED);
    }

    /**
     * Sets the tamed state of this entity.
     * <p>
     * This updates the {@link ActorFlags#TAMED} data flag and stores the
     * corresponding value in the entity NBT.
     * </p>
     *
     * @param value {@code true} to mark the entity as tamed, otherwise {@code false}.
     */
    public void setTamed(boolean value) {
        this.setDataFlag(ActorFlags.TAMED, value);
        this.namedTag = this.namedTag.toBuilder().putBoolean("Tamed", true).build();
    }

    /**
     * Returns the owner name of this entity.
     * <p>
     * The owner name is stored in the AI memory under
     * {@link CoreMemoryTypes#OWNER_NAME}.
     * </p>
     *
     * @return the owner's player name, or {@code null} if no owner is assigned.
     */
    public String getOwnerName() {
        if (!(this instanceof EntityIntelligent ei)) return null;
        return ei.getMemoryStorage().get(CoreMemoryTypes.OWNER_NAME);
    }

    /**
     * Sets the owner name for this entity.
     *
     * @param playerName the name of the player that owns the entity,
     *                   or {@code null} to clear the owner.
     */
    public void setOwnerName(@Nullable String playerName) {
        if (this instanceof EntityIntelligent ei) {
            ei.getMemoryStorage().put(CoreMemoryTypes.OWNER_NAME, playerName);
        }
    }

    public void onTameSuccess(Player player) {
        this.setOwnerName(player.getName());
        this.setTamed(true);

        final ActorEventPacket packet = new ActorEventPacket();
        packet.setTargetRuntimeID(this.getId());
        packet.setType(ActorEvent.TAMING_SUCCEEDED);

        player.dataPacket(packet);

        this.saveNBT();
    }

    public void onTameFail(Player player) {
        final ActorEventPacket packet = new ActorEventPacket();
        packet.setTargetRuntimeID(this.getId());
        packet.setType(ActorEvent.TAMING_FAILED);

        player.dataPacket(packet);
    }

    /**
     * Returns the owner player of this entity, if available.
     * <p>
     * The owner is primarily retrieved from the AI memory under
     * {@link CoreMemoryTypes#OWNER}. If the stored reference is not available
     * or the player is offline, the method attempts to resolve the owner using
     * the stored owner name.
     * </p>
     *
     * @return the owning {@link Player} if found and online,
     * otherwise {@code null}.
     */
    @Nullable
    public Player getOwner() {
        if (!(this instanceof EntityIntelligent ei)) return null;

        var owner = ei.getMemoryStorage().get(CoreMemoryTypes.OWNER);
        if (owner == null || !owner.isOnline()) {
            var ownerName = getOwnerName();
            if (ownerName == null) return null;
            owner = this.getServer().getPlayerExact(ownerName);
        }

        return owner;
    }

    public boolean hasOwner() {
        return hasOwner(true);
    }

    public boolean hasOwner(boolean checkOnline) {
        if (checkOnline) {
            return getOwner() != null;
        } else {
            return getOwnerName() != null;
        }
    }

    public final void scheduleUpdate() {
        this.level.updateEntities.put(this.getId(), this);
    }

    public boolean isOnFire() {
        return this.fireTicks > 0;
    }

    public void setOnFire(int seconds) {
        int ticks = seconds * 20;
        if (ticks > this.fireTicks) {
            this.fireTicks = ticks;
        }
    }

    /**
     * Checks whether this entity is immune to fire damage.
     *
     * @return {@code true} if the entity is fire immune, otherwise {@code false}.
     */
    public boolean isFireImmune() {
        return fireProof;
    }

    /**
     * Sets whether this entity is immune to fire damage.
     * <p>
     * This updates both the internal fire-proof state and the
     * {@link ActorFlags#FIRE_IMMUNE} data flag for client synchronization.
     * </p>
     *
     * @param isFireImmune {@code true} to make the entity immune to fire,
     *                     otherwise {@code false}.
     */
    public void setFireImmune(boolean isFireImmune) {
        this.fireProof = isFireImmune;
        this.setDataFlag(ActorFlags.FIRE_IMMUNE, isFireImmune);
    }

    public float getAbsorption() {
        return absorption;
    }

    public void setAbsorption(float absorption) {
        this.absorption = absorption;
        Attribute attribute = this.attributes.computeIfAbsent(Attribute.ABSORPTION, Attribute::getAttribute);
        attribute.setValue(absorption);
        this.syncAttribute(attribute);
    }

    public void syncAttribute(Attribute attribute) {
        final UpdateAttributesPacket packet = new UpdateAttributesPacket();
        packet.setRuntimeID(this.getId());
        packet.getAttributeList().add(attribute.toNetwork());

        Server.broadcastPacket(this.getViewers().values(), packet);
    }

    public void syncAttributes() {
        final UpdateAttributesPacket packet = new UpdateAttributesPacket();
        packet.setRuntimeID(this.getId());
        packet.getAttributeList().addAll(
                this.attributes.values().stream()
                        .filter(Attribute::isSyncable)
                        .map(Attribute::toNetwork)
                        .toList()
        );
        Server.broadcastPacket(this.getViewers().values(), packet);
    }

    public String[] getSoundVariants() {
        return new String[0];
    }

    public String getRandomSoundVariant() {
        String[] variants = this.getSoundVariants();
        if (variants.length == 0) {
            return null;
        }
        return variants[Utils.rand(0, variants.length - 1)];
    }

    protected void initSoundVariantProperty() {
        String soundVariant;
        if (this.namedTag.containsKey(NBT_SOUND_VARIANT)) {
            soundVariant = this.namedTag.getString(NBT_SOUND_VARIANT);
        } else {
            soundVariant = this.getRandomSoundVariant();
            this.namedTag = this.namedTag.toBuilder().putString(NBT_SOUND_VARIANT, soundVariant).build();
        }
        if (soundVariant == null) return;
        this.setEnumEntityProperty("minecraft:sound_variant", soundVariant);
    }

    /**
     * @deprecated Use {@link #canBePushedByEntities()} and/or {@link #canBePushedByPiston()} instead. <p>
     * If custom entitye use simpleBuilder.pusable() to define.
     */
    @Deprecated
    public boolean canBePushed() {
        return canBePushedByPiston();
    }

    public boolean canBePushedByEntities() {
        if (isCustomEntity()) {
            return meta().getPushable(CustomEntityComponents.PUSHABLE).isPushable();
        }
        return true;
    }

    public boolean canBePushedByPiston() {
        if (isCustomEntity()) {
            return meta().getPushable(CustomEntityComponents.PUSHABLE).isPushableByPiston();
        }
        return true;
    }

    public boolean hasCollision() {
        if (isCustomEntity()) {
            return meta().getPhysics(CustomEntityComponents.PHYSICS).hasCollision();
        }
        return true;
    }

    public boolean hasGravity() {
        if (isCustomEntity()) {
            return meta().getPhysics(CustomEntityComponents.PHYSICS).hasGravity();
        }
        return true;
    }

    public boolean pushTowardsClosestSpace() {
        if (isCustomEntity()) {
            return meta().getPhysics(CustomEntityComponents.PHYSICS).pushTowardsClosestSpace();
        }
        return true;
    }

    public BlockFace getDirection() {
        double rotation = this.yaw % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }
        if ((0 <= rotation && rotation < 45) || (315 <= rotation && rotation < 360)) {
            return BlockFace.SOUTH;
        } else if (45 <= rotation && rotation < 135) {
            return BlockFace.WEST;
        } else if (135 <= rotation && rotation < 225) {
            return BlockFace.NORTH;
        } else if (225 <= rotation && rotation < 315) {
            return BlockFace.EAST;
        } else {
            return null;
        }
    }

    public void extinguish() {
        this.fireTicks = 0;
        this.setDataFlag(ActorFlags.ON_FIRE, false);
    }

    public boolean canTriggerWalking() {
        return true;
    }

    public void resetFallDistance() {
        if (this.level != null) {
            this.highestPosition = this.level.getMinHeight();
        } else {
            this.highestPosition = 0;
        }
    }

    public void updateFallDistance() {
        this.fallDistance = (float) (this.highestPosition - this.y);
    }

    protected void updateFallState(boolean onGround) {
        if (onGround) {
            this.updateFallDistance();
            if (fallDistance > 0) {
                // check if we fell into at least 1 block of water
                var lb = this.getLevelBlock();
                var lb2 = this.getLevelBlockAtLayer(1);
                if (this instanceof EntityLiving &&
                        this.riding == null &&
                        !(lb instanceof BlockFlowingWater ||
                                lb instanceof BlockFence ||
                                (lb2 instanceof BlockFlowingWater && lb2.getMaxY() == 1d))) {
                    this.fall(fallDistance);
                }
                this.resetFallDistance();
            }
        }
    }

    public AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }

    public void fall(float fallDistance) { //todo: check why @param fallDistance always less than the real distance
        if (this.hasEffect(EffectType.SLOW_FALLING)) {
            return;
        }

        Location floorLocation = this.floor();
        Block down = this.level.getBlock(floorLocation.down());

        EntityFallEvent event = new EntityFallEvent(this, down, fallDistance);
        this.server.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        fallDistance = event.getFallDistance();

        if ((!this.isPlayer || level.getGameRules().getBoolean(GameRule.FALL_DAMAGE)) && down.useDefaultFallDamage()) {
            int jumpBoost = this.hasEffect(EffectType.JUMP_BOOST) ? this.getEffect(EffectType.JUMP_BOOST).getLevel() : 0;
            boolean rideable = this.isRideable();
            boolean isRideJumping = rideable && (this instanceof EntityPhysical ef) && ef.isRideJumping();
            float jumpReduction = this.canPowerJump() ? this.getRideJumpStrength() : 0f;
            float damage = fallDistance - 3.255f - jumpBoost - jumpReduction;

            if (damage > 0) {
                if (!this.isSneaking()) {
                    if (!(this instanceof EntityItem item) ||
                            !ItemTags.getTagSet(item.getIdentifier()).contains(ItemTags.WOOL)) {
                        this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.getVector3(), VibrationType.HIT_GROUND));
                    }
                }
                if (!rideable || !isRideJumping) {
                    this.attack(new EntityDamageEvent(this, DamageCause.FALL, damage));
                }
            }
        }

        down.onEntityFallOn(this, fallDistance);

        if (fallDistance > 0.75) { //todo: moving these into their own classes (method "onEntityFallOn()")
            if (Block.FARMLAND.equals(down.getId())) {
                if (onPhysicalInteraction(down, false)) {
                    return;
                }
                if (this instanceof EntityFlyable) return;
                var farmEvent = new FarmLandDecayEvent(this, down);
                this.server.getPluginManager().callEvent(farmEvent);
                if (farmEvent.isCancelled()) return;
                this.level.setBlock(down, Block.get(Block.DIRT), false, true);
                return;
            }

            Block floor = this.level.getBlock(floorLocation);

            if (floor instanceof BlockTurtleEgg) {
                if (onPhysicalInteraction(floor, ThreadLocalRandom.current().nextInt(10) >= 3)) {
                    return;
                }
                this.level.useBreakOn(this, null, null, true);
            }
        }
    }

    protected boolean onPhysicalInteraction(Block block, boolean cancelled) {
        Event ev;

        if (this instanceof Player) {
            ev = new PlayerInteractEvent((Player) this, null, block, null, Action.PHYSICAL);
        } else {
            ev = new EntityInteractEvent(this, block);
        }

        ev.setCancelled(cancelled);

        this.server.getPluginManager().callEvent(ev);
        if (this instanceof Player player) new PlayerHandle(player).setInteract();
        return ev.isCancelled();
    }

    public void handleLavaMovement() {
        //todo
    }

    public void moveFlying(float strafe, float forward, float friction) {
        // This is special for Nukkit! :)
        float speed = strafe * strafe + forward * forward;
        if (speed >= 1.0E-4F) {
            speed = MathHelper.sqrt(speed);
            if (speed < 1.0F) {
                speed = 1.0F;
            }
            speed = friction / speed;
            strafe *= speed;
            forward *= speed;
            float nest = MathHelper.sin((float) (this.yaw * 3.1415927F / 180.0F));
            float place = MathHelper.cos((float) (this.yaw * 3.1415927F / 180.0F));
            this.motionX += strafe * place - forward * nest;
            this.motionZ += forward * place + strafe * nest;
        }
    }

    public void onCollideWithPlayer(EntityHuman entityPlayer) {

    }

    public void applyEntityCollision(Entity entity) {
        if (entity.riding != this && !entity.passengers.contains(this)) {
            double dx = entity.x - this.x;
            double dy = entity.z - this.z;
            double dz = NukkitMath.getDirection(dx, dy);

            if (dz >= 0.009999999776482582D) {
                dz = MathHelper.sqrt((float) dz);
                dx /= dz;
                dy /= dz;
                double d3 = 1.0D / dz;

                if (d3 > 1.0D) {
                    d3 = 1.0D;
                }

                dx *= d3;
                dy *= d3;
                dx *= 0.05000000074505806;
                dy *= 0.05000000074505806;
                dx *= 1F + entityCollisionReduction;

                if (this.riding == null) {
                    motionX -= dx;
                    motionZ -= dy;
                }
            }
        }
    }

    public void onStruckByLightning(Entity entity) {
        if (this.attack(new EntityDamageByEntityEvent(entity, this, DamageCause.LIGHTNING, 5))) {
            if (this.fireTicks < 8 * 20) {
                this.setOnFire(8);
            }
        }
    }


    public void onPushByPiston(BlockEntityPistonArm piston) {

    }

    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        return onInteract(player, item);
    }

    /**
     * return true if opening inventory, otherwise players inventory will be opnened. <p>
     * If inventory is restricted to owner no inventory UI is opened
     */
    public boolean openInventory(Player player) {
        if (!this.hasInventory()) return false;
        boolean isRestricted = this.getComponentInventory().restrictToOwner();

        if (this.isTamed()) {
            if (isRestricted && !player.getName().equals(getOwnerName())) return false;
            if (this instanceof InventoryHolder io) {
                player.addWindow(io.getInventory());
                return true;
            }
        }
        return false;
    }

    public boolean onInteract(Player player, Item item) {
        this.setPersistent(true);
        return false;
    }

    protected boolean switchLevel(Level targetLevel) {
        if (this.closed) {
            return false;
        }

        if (this.isValid()) {
            EntityLevelChangeEvent ev = new EntityLevelChangeEvent(this, this.level, targetLevel);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            }

            this.level.removeEntity(this);
            if (this.chunk != null) {
                this.chunk.removeEntity(this);
            }
            this.despawnFromAll();
        }

        this.setLevel(targetLevel);
        this.level.addEntity(this);
        this.chunk = null;
        this.lastUpdate = level.getTick();
        this.blocksAround = null;
        this.collisionBlocks = null;
        return true;
    }

    @Override
    @NotNull
    public Location getLocation() {
        return new Location(this.x, this.y, this.z, this.yaw, this.pitch, this.headYaw, this.level);
    }


    public boolean isTouchingWater() {
        return hasWaterAt(0) || hasWaterAt(this.getEyeHeight());
    }

    public boolean isInsideOfWater() {
        return hasWaterAt(this.getEyeHeight());
    }


    public boolean isUnderBlock() {
        int x = this.getFloorX();
        int y = this.getFloorY();
        int z = this.getFloorZ();
        for (int i = y + 1; i <= this.getLevel().getMaxHeight(); i++) {
            if (!this.getLevel().getBlock(x, i, z).isAir()) {
                return true;
            }
        }
        return false;
    }


    public boolean hasWaterAt(float height) {
        return hasWaterAt(height, false);
    }


    protected boolean hasWaterAt(float height, boolean tickCached) {
        double y = this.y + height;
        Block block = tickCached ?
                this.level.getTickCachedBlock(this.temporalVector.setComponents(NukkitMath.floorDouble(this.x), NukkitMath.floorDouble(y), NukkitMath.floorDouble(this.z))) :
                this.level.getBlock(this.temporalVector.setComponents(NukkitMath.floorDouble(this.x), NukkitMath.floorDouble(y), NukkitMath.floorDouble(this.z)));

        boolean layer1 = false;
        Block block1 = tickCached ? block.getTickCachedLevelBlockAtLayer(1) : block.getLevelBlockAtLayer(1);
        if (!(block instanceof BlockBubbleColumn) && (
                block instanceof BlockFlowingWater
                        || (layer1 = block1 instanceof BlockFlowingWater))) {
            BlockFlowingWater water = (BlockFlowingWater) (layer1 ? block1 : block);
            double f = (block.y + 1) - (water.getFluidHeightPercent() - 0.1111111);
            return y < f;
        }

        return false;
    }

    public boolean isInsideOfSolid() {
        double probeBaseY = this.y;

        if (this.riding != null && this.seatRawOffset != null) {
            probeBaseY = this.riding.y + this.seatRawOffset.y;
        }

        double y = probeBaseY + this.getEyeHeight();
        Block block = this.level.getBlock(
                this.temporalVector.setComponents(
                        NukkitMath.floorDouble(this.x),
                        NukkitMath.floorDouble(y),
                        NukkitMath.floorDouble(this.z))
        );

        AxisAlignedBB bb = block.getBoundingBox();
        return bb != null && block.isSolid() && !block.isTransparent() && bb.isVectorInside(this.x, y, this.z);
    }

    public boolean isInsideOfFire() {
        for (Block block : this.getCollisionBlocks()) {
            if (block instanceof BlockFire) {
                return true;
            }
        }

        return false;
    }

    public <T extends Block> boolean collideWithBlock(Class<T> classType) {
        for (Block block : this.getCollisionBlocks()) {
            if (classType.isInstance(block)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInsideOfLava() {
        for (Block block : this.getCollisionBlocks()) {
            if (block instanceof BlockFlowingLava) {
                return true;
            }
        }

        return false;
    }

    public boolean isOnLadder() {
        Block b = this.getLevelBlock();

        return Block.LADDER.equals(b.getId());
    }

    /**
     * Player do not use
     */
    private static final float Y_SIZE_DAMPING = 0.4F;
    private static final float Y_SIZE_THRESHOLD = 0.05F;
    private static final float Y_SIZE_BOOST = 0.5F;

    public boolean move(double dx, double dy, double dz) {
        if (isImmobile()) return true; //Do not move when immobile

        if (dx == 0 && dz == 0 && dy == 0) {
            this.onGround = !this.getPosition().setComponents(this.down()).getTickCachedLevelBlock().canPassThrough();
            return true;
        }

        this.ySize *= Y_SIZE_DAMPING;

        double movX = dx;
        double movY = dy;
        double movZ = dz;

        AxisAlignedBB originalBB = this.boundingBox.clone();

        var list = this.noClip ? AxisAlignedBB.EMPTY_LIST : this.level.fastCollisionCubes(this, this.boundingBox.addCoord(dx, dy, dz), false);

        Vec3 collisionOffsets = applyCollisionOffsets(dx, dy, dz, this.boundingBox, list);
        dx = collisionOffsets.x;
        dy = collisionOffsets.y;
        dz = collisionOffsets.z;

        boolean fallingFlag = (this.onGround || (dy != movY && movY < 0));

        boolean tryStep = this.getStepHeight() > 0 && fallingFlag && this.ySize < Y_SIZE_THRESHOLD && (movX != dx || movZ != dz);

        if (tryStep) {
            double cx = dx;
            double cy = dy;
            double cz = dz;
            dx = movX;
            dy = this.getStepHeight();
            dz = movZ;

            AxisAlignedBB steppedBB = this.boundingBox.clone();
            this.boundingBox.setBB(originalBB);

            list = this.level.fastCollisionCubes(this, this.boundingBox.addCoord(dx, dy, dz), false);
            Vec3 stepOffsets = applyCollisionOffsets(dx, dy, dz, this.boundingBox, list);
            dx = stepOffsets.x;
            dy = stepOffsets.y;
            dz = stepOffsets.z;

            double movedLenOld = cx * cx + cz * cz;
            double movedLenNew = dx * dx + dz * dz;

            if (movedLenOld >= movedLenNew) {
                dx = cx;
                dy = cy;
                dz = cz;
                this.boundingBox.setBB(steppedBB);
            } else {
                this.ySize += Y_SIZE_BOOST;
            }
        }

        this.x = (this.boundingBox.getMinX() + this.boundingBox.getMaxX()) / 2;
        this.y = this.boundingBox.getMinY() - this.ySize;
        this.z = (this.boundingBox.getMinZ() + this.boundingBox.getMaxZ()) / 2;

        this.checkChunks();
        this.checkGroundState(movX, movY, movZ, dx, dy, dz);
        this.updateFallState(this.onGround);

        if (movX != dx) {
            this.motionX = 0;
        }

        if (movY != dy) {
            this.motionY = 0;
        }

        if (movZ != dz) {
            this.motionZ = 0;
        }

        // TODO: vehicle collision events (first we need to spawn them!)
        return true;
    }

    private Vec3 applyCollisionOffsets(double dx, double dy, double dz, AxisAlignedBB box, List<AxisAlignedBB> list) {
        for (AxisAlignedBB bb : list) {
            dy = bb.calculateYOffset(box, dy);
        }
        box.offset(0, dy, 0);

        for (AxisAlignedBB bb : list) {
            dx = bb.calculateXOffset(box, dx);
        }
        box.offset(dx, 0, 0);

        for (AxisAlignedBB bb : list) {
            dz = bb.calculateZOffset(box, dz);
        }
        box.offset(0, 0, dz);

        return new Vec3(dx, dy, dz);
    }

    private static class Vec3 {
        public double x;
        public double y;
        public double z;

        public Vec3(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    protected void checkGroundState(double movX, double movY, double movZ, double dx, double dy, double dz) {
        if (this.noClip) {
            this.isCollidedVertically = false;
            this.isCollidedHorizontally = false;
            this.isCollided = false;
            this.onGround = false;
            return;
        }

        this.isCollidedVertically = movY != dy;
        this.isCollidedHorizontally = (movX != dx || movZ != dz);
        this.isCollided = (this.isCollidedHorizontally || this.isCollidedVertically);

        boolean onGroundByCollision = (movY < 0 && movY != dy);

        boolean onGroundBySupport = false;
        if (!onGroundByCollision && movY <= 0.0d && dy == movY) {
            onGroundBySupport = hasSolidSupportBelow();
        }

        this.onGround = onGroundByCollision || onGroundBySupport;
    }

    private boolean hasSolidSupportBelow() {
        if (this.noClip || this.boundingBox == null || this.level == null) return false;

        AxisAlignedBB bb = this.boundingBox;
        double feetY = bb.getMinY();

        AxisAlignedBB below = new SimpleAxisAlignedBB(
                bb.getMinX(), feetY - GROUND_PROBE_DEPTH, bb.getMinZ(),
                bb.getMaxX(), feetY + GROUND_EPS, bb.getMaxZ()
        );

        var cubes = this.level.fastCollisionCubes(this, below, false);
        if (cubes.isEmpty()) return false;

        for (AxisAlignedBB c : cubes) {
            double top = c.getMaxY();
            double d = feetY - top;
            if (d >= -GROUND_EPS && d <= GROUND_PROBE_DEPTH) return true;
        }
        return false;
    }

    public List<Block> getBlocksAround() {
        if (this.blocksAround == null) {
            int minX = NukkitMath.floorDouble(this.boundingBox.getMinX());
            int minY = NukkitMath.floorDouble(this.boundingBox.getMinY());
            int minZ = NukkitMath.floorDouble(this.boundingBox.getMinZ());
            int maxX = NukkitMath.ceilDouble(this.boundingBox.getMaxX());
            int maxY = NukkitMath.ceilDouble(this.boundingBox.getMaxY());
            int maxZ = NukkitMath.ceilDouble(this.boundingBox.getMaxZ());

            this.blocksAround = new ArrayList<>();

            for (int z = minZ; z <= maxZ; ++z) {
                for (int x = minX; x <= maxX; ++x) {
                    for (int y = minY; y <= maxY; ++y) {
                        Block block = this.level.getBlock(this.temporalVector.setComponents(x, y, z));
                        this.blocksAround.add(block);
                    }
                }
            }
        }

        return this.blocksAround;
    }


    public List<Block> getTickCachedBlocksAround() {
        if (this.blocksAround == null) {
            int minX = NukkitMath.floorDouble(this.boundingBox.getMinX());
            int minY = NukkitMath.floorDouble(this.boundingBox.getMinY());
            int minZ = NukkitMath.floorDouble(this.boundingBox.getMinZ());
            int maxX = NukkitMath.ceilDouble(this.boundingBox.getMaxX());
            int maxY = NukkitMath.ceilDouble(this.boundingBox.getMaxY());
            int maxZ = NukkitMath.ceilDouble(this.boundingBox.getMaxZ());

            this.blocksAround = new ArrayList<>();

            for (int z = minZ; z <= maxZ; ++z) {
                for (int x = minX; x <= maxX; ++x) {
                    for (int y = minY; y <= maxY; ++y) {
                        this.blocksAround.add(this.level.getTickCachedBlock(this.temporalVector.setComponents(x, y, z)));
                    }
                }
            }
        }

        return this.blocksAround;
    }

    public List<Block> getCollisionBlocks() {
        if (this.collisionBlocks == null) {
            this.collisionBlocks = new ArrayList<>();

            for (Block b : getBlocksAround()) {
                if (b.collidesWithBB(this.getBoundingBox(), true)) {
                    this.collisionBlocks.add(b);
                }
            }
        }

        return this.collisionBlocks;
    }


    public List<Block> getTickCachedCollisionBlocks() {
        if (this.collisionBlocks == null) {
            this.collisionBlocks = new ArrayList<>();

            for (Block b : getTickCachedBlocksAround()) {
                if (b.collidesWithBB(this.getBoundingBox(), true)) {
                    this.collisionBlocks.add(b);
                }
            }
        }

        return this.collisionBlocks;
    }

    protected List<Block> getBlocksUnderEntity() {
        List<Block> blocks = new ArrayList<>();
        if (!this.isOnGround()) {
            return blocks;
        }

        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        double epsilon = 0.01;
        int blockX = NukkitMath.floorDouble(x);
        int blockY = NukkitMath.floorDouble(y - epsilon);
        int blockZ = NukkitMath.floorDouble(z);

        Block blockBelow = this.level.getBlock(blockX, blockY, blockZ);

        if (blockBelow.hasEntityStepSensor()) {
            blocks.add(blockBelow);
        }

        return blocks;
    }

    public List<Block> getTickCachedStepOnBlocks() {
        if (this.stepOnBlocks == null) {
            this.stepOnBlocks = this.getBlocksUnderEntity();
        }
        return this.stepOnBlocks;
    }

    protected void checkBlockStepOn() {
        List<Block> currentStepOnBlocks = this.getTickCachedStepOnBlocks();
        Set<Vector3> currentPositions = new HashSet<>();

        // On Step ON
        for (Block block : currentStepOnBlocks) {
            Vector3 pos = new Vector3(block.getX(), block.getY(), block.getZ());
            currentPositions.add(pos);

            if (!lastStepOnBlocks.contains(pos)) {
                block.onEntityStepOn(this);
            }
        }
        // On Step OFF
        for (Vector3 oldPos : lastStepOnBlocks) {
            if (!currentPositions.contains(oldPos)) {
                Block oldBlock = this.level.getBlock(oldPos.getFloorX(), oldPos.getFloorY(), oldPos.getFloorZ());
                oldBlock.onEntityStepOff(this);
            }
        }

        lastStepOnBlocks = currentPositions;
    }

    /**
     * Returns whether this entity can be moved by currents in liquids.
     *
     * @return boolean
     */
    public boolean canBeMovedByCurrents() {
        return true;
    }

    protected void checkBlockCollision() {
        if (this.noClip) {
            return;
        }

        boolean needsRecalcCurrent = true;
        if (this instanceof EntityPhysical entityPhysical) {
            needsRecalcCurrent = entityPhysical.needsRecalcMovement;
        }

        Vector3 vector = new Vector3(0, 0, 0);
        boolean portal = false;
        boolean scaffolding = false;
        boolean endPortal = false;
        for (var block : this.getTickCachedCollisionBlocks()) {
            switch (block.getId()) {
                case Block.PORTAL -> portal = true;
                case BlockID.SCAFFOLDING -> scaffolding = true;
                case BlockID.END_PORTAL -> endPortal = true;
            }

            block.onEntityCollide(this);
            block.getTickCachedLevelBlockAtLayer(1).onEntityCollide(this);
            if (needsRecalcCurrent)
                block.addVelocityToEntity(this, vector);
        }

        setDataFlag(ActorFlags.IN_SCAFFOLDING, scaffolding);

        if (Math.abs(this.y % 1) > 0.125) {
            int minX = NukkitMath.floorDouble(boundingBox.getMinX());
            int minZ = NukkitMath.floorDouble(boundingBox.getMinZ());
            int maxX = NukkitMath.ceilDouble(boundingBox.getMaxX());
            int maxZ = NukkitMath.ceilDouble(boundingBox.getMaxZ());
            int Y = (int) y;

            outerScaffolding:
            for (int i = minX; i <= maxX; i++) {
                for (int j = minZ; j <= maxZ; j++) {
                    Location location = new Location(i, Y, j, level);
                    if (BlockID.SCAFFOLDING.equals(location.getLevelBlock(false).getId())) {
                        setDataFlag(ActorFlags.OVER_SCAFFOLDING, true);
                        break outerScaffolding;
                    }
                }
            }
        }

        if (endPortal) { // Handle End portal teleport
            if (!inEndPortal) {
                inEndPortal = true;
                if (this.getRiding() == null && this.getPassengers().isEmpty() && !(this instanceof EntityEnderDragon)) {
                    EntityPortalEnterEvent ev = new EntityPortalEnterEvent(this, PortalType.END);
                    getServer().getPluginManager().callEvent(ev);

                    if (!ev.isCancelled() && (level.getDimension() == Level.DIMENSION_OVERWORLD || level.getDimension() == Level.DIMENSION_THE_END)) {
                        final Position newPos = PortalHelper.convertPosBetweenEndAndOverworld(this);
                        if (newPos != null) {
                            if (newPos.getLevel().getDimension() == Level.DIMENSION_THE_END) {
                                if (teleport(newPos.add(0.5, 1, 0.5), PlayerTeleportEvent.TeleportCause.END_PORTAL)) {
                                    newPos.getLevel().getScheduler().scheduleDelayedTask(new Task() {
                                        @Override
                                        public void onRun(int currentTick) {
                                            // Ensure chunks are loaded and generated before spawning player
                                            teleport(newPos.add(0.5, 1, 0.5), PlayerTeleportEvent.TeleportCause.END_PORTAL);
                                            BlockEndPortal.spawnObsidianPlatform(newPos);
                                        }
                                    }, 5);
                                }
                            } else {
                                if (teleport(newPos, PlayerTeleportEvent.TeleportCause.END_PORTAL)) {
                                    newPos.getLevel().getScheduler().scheduleDelayedTask(new Task() {
                                        @Override
                                        public void onRun(int currentTick) {
                                            // Ensure chunks are loaded and generated before spawning player
                                            teleport(newPos, PlayerTeleportEvent.TeleportCause.END_PORTAL);
                                        }
                                    }, 5);
                                }
                            }
                        } else {
                            getServer().getLogger().warning("Failed to convert position between End and Overworld for portal teleport.");
                        }
                    }
                }
            }
        } else {
            inEndPortal = false;
        }

        if (portal) {
            if (this.inPortalTicks <= 80) {
                // 81 means the server won't try to teleport
                this.inPortalTicks++;
            }
        } else {
            this.inPortalTicks = 0;
        }

        if (needsRecalcCurrent)
            if (vector.lengthSquared() > 0) {
                vector = vector.normalize();
                double d = 0.018d;
                var dx = vector.x * d;
                var dy = vector.y * d;
                var dz = vector.z * d;
                this.motionX += dx;
                this.motionY += dy;
                this.motionZ += dz;
                if (this instanceof EntityPhysical entityPhysical) {
                    entityPhysical.previousCurrentMotion.x = dx;
                    entityPhysical.previousCurrentMotion.y = dy;
                    entityPhysical.previousCurrentMotion.z = dz;
                }
            } else {
                if (this instanceof EntityPhysical entityPhysical) {
                    entityPhysical.previousCurrentMotion.x = 0;
                    entityPhysical.previousCurrentMotion.y = 0;
                    entityPhysical.previousCurrentMotion.z = 0;
                }
            }
        else ((EntityPhysical) this).addPreviousLiquidMovement();
    }

    public boolean setPositionAndRotation(Vector3 pos, double yaw, double pitch) {
        this.setRotation(yaw, pitch);
        return this.setPosition(pos);
    }

    /**
     * Sets position and rotation.
     *
     * @param pos     the pos
     * @param yaw     the yaw
     * @param pitch   the pitch
     * @param headYaw the head yaw
     * @return 切换地图失败会返回false
     */
    public boolean setPositionAndRotation(Vector3 pos, double yaw, double pitch, double headYaw) {
        this.setRotation(yaw, pitch, headYaw);
        return this.setPosition(pos);
    }

    public void setRotation(double yaw, double pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.scheduleUpdate();
    }


    public void setRotation(double yaw, double pitch, double headYaw) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.headYaw = headYaw;
        this.scheduleUpdate();
    }

    /**
     * Whether the entity can activate pressure plates.
     * Used for {@link cn.nukkit.entity.passive.EntityBat}s only.
     *
     * @return triggers pressure plate
     */
    public boolean doesTriggerPressurePlate() {
        return true;
    }

    public boolean canPassThrough() {
        return true;
    }

    protected void checkChunks() {
        if (this.chunk == null || (this.chunk.getX() != ((int) this.x >> 4)) || this.chunk.getZ() != ((int) this.z >> 4)) {
            if (this.chunk != null) {
                this.chunk.removeEntity(this);
            }
            this.chunk = this.level.getChunk((int) this.x >> 4, (int) this.z >> 4, true);

            if (!this.justCreated) {
                Map<Integer, Player> newChunk = this.level.getChunkPlayers((int) this.x >> 4, (int) this.z >> 4);
                for (Player player : new ArrayList<>(this.hasSpawned.values())) {
                    if (!newChunk.containsKey(player.getLoaderId())) {
                        this.despawnFrom(player);
                    } else {
                        newChunk.remove(player.getLoaderId());
                    }
                }

                for (Player player : newChunk.values()) {
                    this.spawnTo(player);
                }
            }

            if (this.chunk == null) {
                return;
            }

            this.chunk.addEntity(this);
        }
    }

    public boolean setPosition(Vector3 pos) {
        if (this.closed) {
            return false;
        }

        if (pos instanceof Position && ((Position) pos).level != null && ((Position) pos).level != this.level) {
            if (!this.switchLevel(((Position) pos).getLevel())) {
                return false;
            }
        }

        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;

        this.recalculateBoundingBox(false); // Don't need to send BB height/width to client on position change

        this.checkChunks();

        return true;
    }

    public Vector3 getMotion() {
        return new Vector3(this.motionX, this.motionY, this.motionZ);
    }

    /**
     * 设置一个运动向量(会使得实体移动这个向量的距离，非精准移动)<p/>
     * <p>
     * Set a motion vector (will make the entity move the distance of this vector, not move precisely)
     *
     * @param motion 运动向量<br>a motion vector
     * @return boolean
     */
    public boolean setMotion(Vector3 motion) {
        if (!this.justCreated) {
            EntityMotionEvent ev = new EntityMotionEvent(this, motion);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            }
        }

        this.motionX = motion.x;
        this.motionY = motion.y;
        this.motionZ = motion.z;

        if (!this.justCreated && !this.isImmobile()) {
            this.updateMovement();
        }

        return true;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void kill() {
        this.health = 0;
        this.scheduleUpdate();

        for (Entity passenger : new ArrayList<>(this.passengers)) {
            dismountEntity(passenger);
        }
    }

    public boolean teleport(Vector3 pos) {
        return this.teleport(pos, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public boolean teleport(Vector3 pos, PlayerTeleportEvent.TeleportCause cause) {
        return this.teleport(Location.fromObject(pos, this.level, this.yaw, this.pitch, this.headYaw), cause);
    }

    public boolean teleport(Position pos) {
        return this.teleport(pos, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public boolean teleport(Position pos, PlayerTeleportEvent.TeleportCause cause) {
        return this.teleport(Location.fromObject(pos, pos.level, this.yaw, this.pitch, this.headYaw), cause);
    }

    public boolean teleport(Location location) {
        return this.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    /**
     * Teleport the entity to another location
     *
     * @param location the another location
     * @param cause    the teleported cause
     * @return the boolean
     */
    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause) {
        double yaw = location.yaw;
        double pitch = location.pitch;

        Location from = this.getLocation();
        Location to = location;
        if (cause != null) {
            EntityTeleportEvent ev = new EntityTeleportEvent(this, from, to, cause);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            }
            to = ev.getTo();
        }

        final Entity currentRide = getRiding();
        if (currentRide != null && !currentRide.dismountEntity(this)) {
            return false;
        }

        this.positionChanged = true;
        this.ySize = 0;

        this.setMotion(this.temporalVector.setComponents(0, 0, 0));

        if (this.setPositionAndRotation(to, yaw, pitch)) {
            this.resetFallDistance();
            this.onGround = !this.noClip;
            this.updateMovement();
            return true;
        }

        return false;
    }

    /**
     * return runtime id (changed after restart the server),the id is incremental number
     */
    public long getId() {
        return this.id;
    }


    /**
     * Gets unique id(UUID)
     */
    public UUID getUniqueId() {
        return this.entityUniqueId;
    }

    public void respawnToAll() {
        Player[] players = this.hasSpawned.values().toArray(Player.EMPTY_ARRAY);
        this.hasSpawned.clear();

        for (Player player : players) {
            this.spawnTo(player);
        }
    }

    public void spawnToAll() {
        if (this.chunk == null || this.closed) {
            return;
        }

        for (Player player : this.level.getChunkPlayers(this.chunk.getX(), this.chunk.getZ()).values()) {
            if (player.isOnline()) {
                this.spawnTo(player);
            }
        }
    }

    public void despawnFromAll() {
        for (Player player : new ArrayList<>(this.hasSpawned.values())) {
            this.despawnFrom(player);
        }
    }

    public void close() {
        close(true);
    }

    private void close(boolean despawn) {
        if (!this.closed) {
            this.closed = true;

            if (despawn) {
                try {
                    EntityDespawnEvent event = new EntityDespawnEvent(this);

                    this.server.getPluginManager().callEvent(event);

                    if (event.isCancelled()) {
                        this.closed = false;
                        return;
                    }
                } catch (Throwable e) {
                    this.closed = false;
                    throw e;
                }
            }

            try {
                this.despawnFromAll();
            } finally {
                try {
                    if (this.chunk != null) {
                        this.chunk.removeEntity(this);
                    }
                } finally {
                    if (this.level != null) {
                        this.level.removeEntity(this);
                    }
                }
            }
        }
    }

    public void setDataProperties(Map<ActorDataType<?>, Object> maps) {
        setDataProperties(maps, true);
    }

    public void setDataProperties(Map<ActorDataType<?>, Object> maps, boolean send) {
        ActorDataMap sendMap = new ActorDataMap();
        for (var e : maps.entrySet()) {
            ActorDataType<?> key = e.getKey();
            Object value = e.getValue();
            if (this.getEntityDataMap().containsKey(key) && this.getEntityDataMap().get(key).equals(value)) {
                continue;
            }
            this.getEntityDataMap().put(key, value);
            sendMap.put(key, value);
        }
        if (send) {
            this.sendData(this.hasSpawned.values().toArray(Player.EMPTY_ARRAY), sendMap);
        }
    }

    public boolean setDataProperty(ActorDataType<?> key, Object value) {
        return setDataProperty(key, value, true);
    }

    public boolean setDataProperty(ActorDataType<?> key, Object value, boolean send) {
        if (this.getEntityDataMap().containsKey(key) && this.getEntityDataMap().get(key).equals(value)) {
            return false;
        }

        this.getEntityDataMap().put(key, value);
        if (send) {
            ActorDataMap map = new ActorDataMap();
            map.put(key, value);
            this.sendData(this.hasSpawned.values().toArray(Player.EMPTY_ARRAY), map);
        }
        return true;
    }

    public ActorDataMap getEntityDataMap() {
        return this.entityDataMap;
    }

    @NotNull
    public <T> T getDataProperty(ActorDataType<T> key) {
        return this.getEntityDataMap().get(key);
    }

    @NotNull
    public <T> T getDataProperty(ActorDataType<T> key, T d) {
        return (T) this.getEntityDataMap().getOrDefault(key, d);
    }

    public void setDataFlag(ActorFlags entityFlag) {
        this.setDataFlag(entityFlag, true);
    }

    public void setDataFlag(ActorFlags entityFlag, boolean value) {
        setDataFlag(entityFlag, value, true);
    }

    public void setDataFlag(ActorFlags entityFlag, boolean value, boolean send) {
        if (this.getEntityDataMap().getOrCreateFlags().contains(entityFlag) ^ value) {
            this.getEntityDataMap().setFlag(entityFlag, value);
            if (send) {
                sendData(this.hasSpawned.values().toArray(Player.EMPTY_ARRAY), this.getEntityDataMap());
            }
        }
    }

    public void setDataFlags(EnumSet<ActorFlags> entityFlags) {
        this.getEntityDataMap().putFlags(entityFlags);
        sendData(this.hasSpawned.values().toArray(Player.EMPTY_ARRAY), this.getEntityDataMap());
    }

    public boolean getDataFlag(ActorFlags id) {
        return this.getEntityDataMap().getOrCreateFlags().contains(id);
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        this.server.getEntityMetadata().setMetadata(this, metadataKey, newMetadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        return this.server.getEntityMetadata().getMetadata(this, metadataKey);
    }

    @Override
    public MetadataValue getMetadata(String metadataKey, Plugin plugin) {
        return this.server.getEntityMetadata().getMetadata(this, metadataKey, plugin);
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        return this.server.getEntityMetadata().hasMetadata(this, metadataKey);
    }

    @Override
    public boolean hasMetadata(String metadataKey, Plugin plugin) {
        return this.server.getEntityMetadata().hasMetadata(this, metadataKey, plugin);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        this.server.getEntityMetadata().removeMetadata(this, metadataKey, owningPlugin);
    }

    public Server getServer() {
        return server;
    }

    public boolean isUndead() {
        return false;
    }

    public boolean isInEndPortal() {
        return inEndPortal;
    }

    public boolean isPreventingSleep(Player player) {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Entity other = (Entity) obj;
        return this.getId() == other.getId();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = (int) (29 * hash + this.getId());
        return hash;
    }

    public boolean isSpinAttacking() {
        return this.getDataFlag(ActorFlags.DAMAGE_NEARBY_MOBS);
    }

    public void setSpinAttacking(boolean value) {
        this.setDataFlag(ActorFlags.DAMAGE_NEARBY_MOBS, value);
    }

    public void setSpinAttacking() {
        this.setSpinAttacking(true);
    }

    public boolean isNoClip() {
        return noClip;
    }

    public void setNoClip(boolean noClip) {
        this.noClip = noClip;
        this.setDataFlag(ActorFlags.HAS_COLLISION, noClip);
    }

    public boolean isBoss() {
        return this instanceof EntityBoss;
    }

    /**
     * Add a tag to the entity
     */
    public void addTag(String tag) {
        final List<String> tags = this.namedTag.getList("Tags", NbtType.STRING);
        tags.add(tag);
        this.namedTag = this.namedTag.toBuilder().putList("Tags", NbtType.STRING, tags).build();
    }

    /**
     * Remove tag from entity if exist
     */
    public void removeTag(String tag) {
        List<String> tags = this.namedTag.getList("Tags", NbtType.STRING);
        tags.remove(tag);
        this.namedTag = this.namedTag.toBuilder().putList("Tags", NbtType.STRING, tags).build();
    }

    /**
     * @deprecated Use {@link #hasTag(String)} instead.
     */
    @Deprecated
    public boolean containTag(String tag) {
        return hasTag(tag);
    }

    /**
     * @return true if entity has a string tag
     */
    public boolean hasTag(String tag) {
        return this.namedTag.getList("Tags", NbtType.STRING).stream().anyMatch(t -> t.equals(tag));
    }

    /**
     * @return List of tags for the entity
     */
    public List<String> getAllTags() {
        return this.namedTag.getList("Tags", NbtType.STRING);
    }

    public float getFreezingEffectStrength() {
        return this.getDataProperty(ActorDataTypes.FREEZING_EFFECT_STRENGTH);
    }

    public void setFreezingEffectStrength(float strength) {
        if (strength < 0 || strength > 1)
            throw new IllegalArgumentException("Freezing Effect Strength must be between 0 and 1");
        this.setDataProperty(ActorDataTypes.FREEZING_EFFECT_STRENGTH, strength);
    }

    public int getFreezingTicks() {
        return this.freezingTicks;
    }

    public void setFreezingTicks(int ticks) {
        this.freezingTicks = Math.max(0, Math.min(ticks, 140));
        setFreezingEffectStrength(ticks / 140f);
    }

    public void addFreezingTicks(int increments) {
        if (freezingTicks + increments < 0) this.freezingTicks = 0;
        else if (freezingTicks + increments > 140) this.freezingTicks = 140;
        else this.freezingTicks += increments;
        setFreezingEffectStrength(this.freezingTicks / 140f);
    }

    public void setAmbientSoundInterval(float interval) {
        this.setDataProperty(ActorDataTypes.AMBIENT_SOUND_INTERVAL, interval);
    }

    public void setAmbientSoundIntervalRange(float range) {
        this.setDataProperty(ActorDataTypes.AMBIENT_SOUND_INTERVAL, range);
    }

    public void setAmbientSoundEvent(Sound sound) {
        this.setAmbientSoundEventName(sound.getSound());
    }

    public void setAmbientSoundEventName(String eventName) {
        this.setDataProperty(ActorDataTypes.AMBIENT_SOUND_EVENT_NAME, eventName);
    }

    public void playAnimation(EntityAnimation animation) {
        var viewers = new HashSet<>(this.getViewers().values());
        if (this.isPlayer) viewers.add((Player) this);
        playAnimation(animation, viewers);
    }

    /**
     * Play the animation of this entity to a specified group of players
     * <p>
     * 向指定玩家群体播放此实体的动画
     *
     * @param animation 动画对象 Animation objects
     * @param players   可视玩家 Visible Player
     */
    public void playAnimation(EntityAnimation animation, Collection<Player> players) {
        var pk = animation.toNetwork();
        pk.getRuntimeIds().add(this.getId());
        Server.broadcastPacket(players, pk);
    }

    public void playActionAnimation(AnimatePacket.Action action, ActorSwingSource swingSource) {
        var viewers = new HashSet<>(this.getViewers().values());
        if (this.isPlayer) viewers.add((Player) this);
        playActionAnimation(action, swingSource, viewers);
    }

    /**
     * Play the action animation of this entity to a specified group of players
     * <p>
     * 向指定玩家群体播放此实体的action动画
     *
     * @param action      the action
     * @param swingSource the swing source
     * @param players     可视玩家 Visible Player
     */
    public void playActionAnimation(AnimatePacket.Action action, ActorSwingSource swingSource, Collection<Player> players) {
        var pk = new AnimatePacket();
        pk.setAction(action);
        pk.setTargetRuntimeID(this.getId());
        pk.setSwingSource(swingSource);
        Server.broadcastPacket(players, pk);
    }

    public double getLookingAngleAt(Vector3 location) {
        double anglePosition = Math.abs(Math.toDegrees(Math.atan2(location.x - this.x, location.z - this.z)));
        double angleVector = Math.abs(Math.toDegrees(Math.atan2(this.getDirectionVector().x, this.getDirectionVector().z)));
        return Math.abs(anglePosition - angleVector);
    }

    public double getLookingAngleAtPitch(Vector3 location) {
        double anglePosition = Math.abs(Math.toDegrees(Math.atan2(location.y - (this.y + getEyeHeight()), 0)));
        double angleVector = Math.abs(getPitch());
        return Math.abs(Math.abs(anglePosition - angleVector) - 90);
    }

    public boolean isLookingAt(Vector3 location, double tolerance, boolean checkRaycast) {
        return getLookingAngleAt(location) <= tolerance && getLookingAngleAtPitch(location) <= tolerance && (!checkRaycast || getLevel().raycastBlocks(location, this.add(0, getEyeHeight(), 0)).isEmpty());
    }

    private boolean validateAndSetIntProperty(String identifier, int value) {
        if (!intProperties.containsKey(identifier)) return false;

        IntEntityProperty intProperty = getTypedEntityProperty(identifier, IntEntityProperty.class);
        if (intProperty == null) return false;

        if (value < intProperty.getMinValue() || value > intProperty.getMaxValue()) {
            return false;
        }

        intProperties.put(identifier, value);
        return true;
    }

    private boolean validateAndSetBooleanProperty(String identifier, boolean value) {
        if (!intProperties.containsKey(identifier)) return false;

        BooleanEntityProperty booleanProperty = getTypedEntityProperty(identifier, BooleanEntityProperty.class);
        if (booleanProperty == null) return false;

        intProperties.put(identifier, value ? 1 : 0);
        return true;
    }

    private boolean validateAndSetFloatProperty(String identifier, float value) {
        if (!floatProperties.containsKey(identifier)) return false;

        FloatEntityProperty floatProperty = getTypedEntityProperty(identifier, FloatEntityProperty.class);
        if (floatProperty == null) return false;

        if (value < floatProperty.getMinValue() || value > floatProperty.getMaxValue()) {
            return false;
        }

        floatProperties.put(identifier, value);
        return true;
    }

    public final boolean setIntEntityProperty(String identifier, int value) {
        boolean change = validateAndSetIntProperty(identifier, value);

        if (change) {
            IntEntityProperty prop = getTypedEntityProperty(identifier, IntEntityProperty.class);
            if (prop != null && prop.isClientSync()) {
                this.sendData(this.getViewers().values().toArray(new Player[0]));
            }
        }
        return change;
    }

    public final boolean setBooleanEntityProperty(String identifier, boolean value) {
        boolean change = validateAndSetBooleanProperty(identifier, value);

        if (change) {
            BooleanEntityProperty property = getTypedEntityProperty(identifier, BooleanEntityProperty.class);
            if (property != null && property.isClientSync()) {
                this.sendData(this.getViewers().values().toArray(new Player[0]));
            }
        }
        return change;
    }

    public final boolean setFloatEntityProperty(String identifier, float value) {
        boolean change = validateAndSetFloatProperty(identifier, value);

        if (change) {
            FloatEntityProperty property = getTypedEntityProperty(identifier, FloatEntityProperty.class);
            if (property != null && property.isClientSync()) {
                this.sendData(this.getViewers().values().toArray(new Player[0]));
            }
        }

        return change;
    }

    public final boolean setEnumEntityProperty(String identifier, String value) {
        if (!intProperties.containsKey(identifier)) return false;

        EnumEntityProperty property = getTypedEntityProperty(identifier, EnumEntityProperty.class);
        if (property != null) {
            int index = property.findIndex(value);
            if (index >= 0) {
                intProperties.put(identifier, index);
                if (property.isClientSync()) {
                    this.sendData(this.getViewers().values().toArray(new Player[0]));
                }
                return true;
            }
            return false;
        }
        return false;
    }

    public PropertySyncData getClientSyncProperties() {
        final List<EntityProperty> propertyDefs = EntityProperty.getEntityProperty(this.getIdentifier());
        final List<Integer> ints = new IntArrayList();
        final List<Float> floats = new FloatArrayList();
        for (final EntityProperty property : propertyDefs) {
            Object value;
            if (this.shouldSyncIntProperty(property) && (value = this.getIntPropertyValue(property)) != null) {
                ints.add((int) value);
                continue;
            }
            if (this.shouldSyncFloatProperty(property) && (value = this.getFloatPropertyValue(property)) != null) {
                floats.add((float) value);
            }
        }
        final List<org.cloudburstmc.protocol.bedrock.data.actor.IntEntityProperty> intProperties = new ObjectArrayList<>();
        for (Integer anInt : ints) {
            intProperties.add(
                    new org.cloudburstmc.protocol.bedrock.data.actor.IntEntityProperty(
                            ints.indexOf(anInt),
                            anInt
                    )
            );
        }
        final List<org.cloudburstmc.protocol.bedrock.data.actor.FloatEntityProperty> floatProperties = new ObjectArrayList<>();
        for (Float aFloat : floats) {
            floatProperties.add(
                    new org.cloudburstmc.protocol.bedrock.data.actor.FloatEntityProperty(
                            floats.indexOf(aFloat),
                            aFloat
                    )
            );
        }
        final PropertySyncData syncData = new PropertySyncData();
        syncData.getIntProperties().addAll(intProperties);
        syncData.getFloatProperties().addAll(floatProperties);
        return syncData;
    }

    private boolean shouldSyncIntProperty(EntityProperty prop) {
        if (!prop.isClientSync()) return false;
        return (prop instanceof IntEntityProperty)
                || (prop instanceof BooleanEntityProperty)
                || (prop instanceof EnumEntityProperty);
    }

    private Integer getIntPropertyValue(EntityProperty prop) {
        Integer val = this.intProperties.get(prop.getIdentifier());
        if (prop instanceof BooleanEntityProperty && val == null) return 0;
        return val;
    }

    private boolean shouldSyncFloatProperty(EntityProperty prop) {
        return prop.isClientSync() && (prop instanceof FloatEntityProperty);
    }

    private Float getFloatPropertyValue(EntityProperty prop) {
        return this.floatProperties.get(prop.getIdentifier());
    }

    @SuppressWarnings("unchecked")
    protected <T extends EntityProperty> T getTypedEntityProperty(String identifier, Class<T> type) {
        List<EntityProperty> propertyList = EntityProperty.getEntityProperty(this.getIdentifier());
        if (propertyList == null) return null;

        for (EntityProperty property : propertyList) {
            if (identifier.equals(property.getIdentifier()) && type.isInstance(property)) {
                return (T) property;
            }
        }

        return null;
    }

    public final int getIntEntityProperty(String identifier) {
        return intProperties.get(identifier);
    }

    public final boolean getBooleanEntityProperty(String identifier) {
        return intProperties.get(identifier) == 1;
    }

    public final float getFloatEntityProperty(String identifier) {
        return floatProperties.get(identifier);
    }

    public final String getEnumEntityProperty(String identifier) {
        List<EntityProperty> entityPropertyList = EntityProperty.getEntityProperty(this.getIdentifier());

        for (EntityProperty property : entityPropertyList) {
            if (!identifier.equals(property.getIdentifier()) ||
                    !(property instanceof EnumEntityProperty enumProperty)) {
                continue;
            }
            return enumProperty.getEnums()[intProperties.get(identifier)];
        }
        return null;
    }

    private void initEntityProperties(String entityIdentifier) {
        List<EntityProperty> entityPropertyList = EntityProperty.getEntityProperty(entityIdentifier);
        if (entityPropertyList.isEmpty()) return;

        for (EntityProperty property : entityPropertyList) {
            final String identifier = property.getIdentifier();

            switch (property) {
                case FloatEntityProperty floatProperty -> {
                    if (!floatProperties.containsKey(identifier)) {
                        floatProperties.put(identifier, floatProperty.getDefaultValue());
                    }
                }
                case IntEntityProperty intProperty -> {
                    if (!intProperties.containsKey(identifier)) {
                        intProperties.put(identifier, intProperty.getDefaultValue());
                    }
                }
                case BooleanEntityProperty booleanProperty -> {
                    if (!intProperties.containsKey(identifier)) {
                        intProperties.put(identifier, booleanProperty.getDefaultValue() ? 1 : 0);
                    }
                }
                case EnumEntityProperty enumProperty -> {
                    if (!intProperties.containsKey(identifier)) {
                        intProperties.put(identifier, enumProperty.findIndex(enumProperty.getDefaultValue()));
                    }
                }
                default -> {
                }
            }
        }
    }

    private PropertySyncData propertySyncData() {
        final List<Integer> ints = new IntArrayList(intProperties.values());
        final List<Float> floats = new FloatArrayList(floatProperties.values());
        final List<org.cloudburstmc.protocol.bedrock.data.actor.IntEntityProperty> intProperties = new ObjectArrayList<>();
        for (Integer anInt : ints) {
            intProperties.add(
                    new org.cloudburstmc.protocol.bedrock.data.actor.IntEntityProperty(
                            ints.indexOf(anInt),
                            anInt
                    )
            );
        }
        final List<org.cloudburstmc.protocol.bedrock.data.actor.FloatEntityProperty> floatProperties = new ObjectArrayList<>();
        for (Float aFloat : floats) {
            floatProperties.add(
                    new org.cloudburstmc.protocol.bedrock.data.actor.FloatEntityProperty(
                            floats.indexOf(aFloat),
                            aFloat
                    )
            );
        }
        final PropertySyncData syncData = new PropertySyncData();
        syncData.getIntProperties().addAll(intProperties);
        syncData.getFloatProperties().addAll(floatProperties);
        return syncData;
    }

    public Map<Integer, Attribute> getAttributes() {
        return attributes;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public boolean isCustomEntity() {
        return this instanceof CustomEntity;
    }

    @Nullable
    public CustomEntityDefinition getCustomEntityDefinition() {
        if (!isCustomEntity()) return null;
        return EntityRegistry.getCustomEntityDefinitionById(getIdentifier());
    }

    @NotNull
    protected CustomEntityDefinition.Meta meta() {
        CustomEntityDefinition def = getCustomEntityDefinition();
        return def == null ? new CustomEntityDefinition.Meta() : CustomEntityDefinition.metaOf(def.id());
    }

    public boolean hasJumpStrength() {
        Attribute existing = this.attributes.get(Attribute.HORSE_JUMP_STRENGTH);
        return existing != null;
    }

    public boolean hasDashAction() {
        return getComponentDashAction() != null;
    }

}