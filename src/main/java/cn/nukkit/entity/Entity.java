package cn.nukkit.entity;

import cn.nukkit.Player;
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
import cn.nukkit.entity.custom.CustomEntity;
import cn.nukkit.entity.data.EntityDataMap;
import cn.nukkit.entity.data.EntityDataType;
import cn.nukkit.entity.data.EntityDataTypes;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.data.PlayerFlag;
import cn.nukkit.entity.data.property.BooleanEntityProperty;
import cn.nukkit.entity.data.property.EntityProperty;
import cn.nukkit.entity.data.property.EnumEntityProperty;
import cn.nukkit.entity.data.property.FloatEntityProperty;
import cn.nukkit.entity.data.property.IntEntityProperty;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.entity.item.EntityArmorStand;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.mob.EntityEnderDragon;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.Event;
import cn.nukkit.event.block.FarmLandDecayEvent;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityPortalEnterEvent.PortalType;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.event.player.PlayerTeleportEvent;
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
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.network.protocol.*;
import cn.nukkit.network.protocol.types.EntityLink;
import cn.nukkit.network.protocol.types.PropertySyncData;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.registry.Registries;
import cn.nukkit.scheduler.Task;
import cn.nukkit.tags.ItemTags;
import cn.nukkit.utils.ChunkException;
import cn.nukkit.utils.Identifier;
import cn.nukkit.utils.PortalHelper;
import cn.nukkit.utils.TextFormat;
import com.google.common.collect.Iterables;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author MagicDroidX
 */
public abstract class Entity extends Location implements Metadatable, EntityID, EntityDataTypes {
    public static final Entity[] EMPTY_ARRAY = new Entity[0];
    protected final EntityDataMap entityDataMap = new EntityDataMap();
    public static AtomicLong entityCount = new AtomicLong(1);
    public final List<Entity> passengers = new ArrayList<>();
    public final AxisAlignedBB offsetBoundingBox = new SimpleAxisAlignedBB(0, 0, 0, 0, 0, 0);
    protected final Map<Integer, Player> hasSpawned = new ConcurrentHashMap<>();
    protected final Map<EffectType, Effect> effects = new ConcurrentHashMap<>();
    /**
     * 这个实体骑在谁身上
     * <p>
     * Who is this entity riding on
     */
    public Entity riding = null;
    public IChunk chunk;
    public List<Block> blocksAround = new ArrayList<>();
    public List<Block> collisionBlocks = new ArrayList<>();
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
    public CompoundTag namedTag;
    public boolean isCollided = false;
    public boolean isCollidedHorizontally = false;
    public boolean isCollidedVertically = false;
    public int noDamageTicks;
    public boolean justCreated;
    public boolean fireProof;
    public boolean invulnerable;
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

    public Entity(IChunk chunk, CompoundTag nbt) {
        if (this instanceof Player) {
            initEntityProperties("minecraft:player");
            return;
        }
        initEntityProperties();
        this.init(chunk, nbt);
    }

    /**
     * 从mc标准实体标识符创建实体，形如(minecraft:sheep)
     * <p>
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
     * 创建一个实体从实体名,名称从{@link Entity#init registerEntities}源代码查询
     * <p>
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
     * 创建一个实体从网络id
     * <p>
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
     * 创建一个实体从网络id
     * <p>
     * Create an entity from the network id
     *
     * @param type  网络ID<br> network id
     * @param chunk the chunk
     * @param nbt   the nbt
     * @param args  the args
     * @return the entity
     */
    @Nullable
    public static Entity createEntity(int type, @NotNull IChunk chunk, @NotNull CompoundTag nbt, @Nullable Object... args) {
        String entityIdentifier = Registries.ENTITY.getEntityIdentifier(type);
        if (entityIdentifier == null) return null;
        return Registries.ENTITY.provideEntity(entityIdentifier, chunk, nbt, args);
    }

    @Nullable
    public static Entity createEntity(@NotNull String identifier, @NotNull IChunk chunk, @NotNull CompoundTag nbt, @Nullable Object... args) {
        Identifier.assertValid(identifier);
        return Registries.ENTITY.provideEntity(identifier, chunk, nbt, args);
    }

    /**
     * 获取指定网络id实体的标识符
     * <p>
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
    public static CompoundTag getDefaultNBT(@NotNull Vector3 pos) {
        return getDefaultNBT(pos, null);
    }

    @NotNull
    public static CompoundTag getDefaultNBT(@NotNull Vector3 pos, @Nullable Vector3 motion) {
        Location loc = pos instanceof Location ? (Location) pos : null;

        if (loc != null) {
            return getDefaultNBT(pos, motion, (float) loc.getYaw(), (float) loc.getPitch());
        }

        return getDefaultNBT(pos, motion, 0, 0);
    }

    /**
     * 获得该实体的默认NBT，带有位置,motion，yaw pitch等信息
     * <p>
     * Get the default NBT of the entity, with information such as position, motion, yaw pitch, etc.
     *
     * @param pos    the pos
     * @param motion the motion
     * @param yaw    the yaw
     * @param pitch  the pitch
     * @return the default nbt
     */
    @NotNull
    public static CompoundTag getDefaultNBT(@NotNull Vector3 pos, @Nullable Vector3 motion, float yaw, float pitch) {
        return new CompoundTag()
                .putList("Pos", new ListTag<DoubleTag>()
                        .add(new DoubleTag(pos.x))
                        .add(new DoubleTag(pos.y))
                        .add(new DoubleTag(pos.z)))
                .putList("Motion", new ListTag<DoubleTag>()
                        .add(new DoubleTag(motion != null ? motion.x : 0))
                        .add(new DoubleTag(motion != null ? motion.y : 0))
                        .add(new DoubleTag(motion != null ? motion.z : 0)))
                .putList("Rotation", new ListTag<FloatTag>()
                        .add(new FloatTag(yaw))
                        .add(new FloatTag(pitch)));
    }

    /**
     * Batch play animation on entity groups<br/>
     * This method is recommended if you need to play the same animation on a large number of entities at the same time, as it only sends packets once for each player, which greatly reduces bandwidth pressure
     * <p>
     * 在实体群上批量播放动画<br/>
     * 若你需要同时在大量实体上播放同一动画，建议使用此方法，因为此方法只会针对每个玩家发送一次包，这能极大地缓解带宽压力
     *
     * @param animation 动画对象 Animation objects
     * @param entities  需要播放动画的实体群 Group of entities that need to play animations
     * @param players   可视玩家 Visible Player
     */
    public static void playAnimationOnEntities(AnimateEntityPacket.Animation animation, Collection<Entity> entities, Collection<Player> players) {
        var pk = new AnimateEntityPacket();
        pk.parseFromAnimation(animation);
        entities.forEach(entity -> pk.entityRuntimeIds.add(entity.getId()));
        Server.broadcastPacket(players, pk);
    }

    /**
     * @see #playAnimationOnEntities(AnimateEntityPacket.Animation, Collection, Collection)
     */
    public static void playAnimationOnEntities(AnimateEntityPacket.Animation animation, Collection<Entity> entities) {
        var viewers = new HashSet<Player>();
        entities.forEach(entity -> {
            viewers.addAll(entity.getViewers().values());
            if (entity.isPlayer) viewers.add((Player) entity);
        });
        playAnimationOnEntities(animation, entities, viewers);
    }

    /**
     * 获取该实体的标识符
     * <p>
     * Get the identifier of the entity
     *
     * @return the identifier
     */
    @NotNull
    public abstract String getIdentifier();

    /**
     * 实体高度
     * <p>
     * entity Height
     *
     * @return the height
     */
    public float getHeight() {
        return 0;
    }


    public float getCurrentHeight() {
        if (isSwimming()) {
            return getSwimmingHeight();
        } else {
            return getHeight();
        }
    }

    public float getEyeHeight() {
        return getCurrentHeight() / 2 + 0.1f;
    }

    public float getWidth() {
        return 0;
    }

    public float getLength() {
        return 0;
    }

    protected double getStepHeight() {
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

    /**
     * 实体初始化顺序，先初始化Entity类字段->Entity构造函数->进入init方法->调用initEntity方法->子类字段初始化->子类构造函数
     * <p>
     * 用于初始化实体的NBT和实体字段的方法
     * <p>
     * Entity initialization order, first initialize the Entity class field->Entity constructor->Enter the init method->Call the init Entity method-> subclass field initialization-> subclass constructor
     * <p>
     * The method used to initialize the NBT and entity fields of the entity
     */
    protected void initEntity() {
        if (!(this instanceof Player)) {
            if (this.namedTag.contains("uuid")) {
                this.entityUniqueId = UUID.fromString(this.namedTag.getString("uuid"));
            } else {
                this.entityUniqueId = UUID.randomUUID();
            }
        }

        if (this.namedTag.contains("ActiveEffects")) {
            ListTag<CompoundTag> effects = this.namedTag.getList("ActiveEffects", CompoundTag.class);
            for (CompoundTag e : effects.getAll()) {
                Effect effect = Effect.get(e.getByte("Id"));
                if (effect == null) {
                    continue;
                }

                effect.setAmplifier(e.getByte("Amplifier")).setDuration(e.getInt("Duration")).setVisible(e.getBoolean("ShowParticles"));

                this.addEffect(effect);
            }
        }

        if (this.namedTag.contains("CustomName")) {
            this.setNameTag(this.namedTag.getString("CustomName"));
            if (this.namedTag.contains("CustomNameVisible")) {
                this.setNameTagVisible(this.namedTag.getBoolean("CustomNameVisible"));
            }
            if (this.namedTag.contains("CustomNameAlwaysVisible")) {
                this.setNameTagAlwaysVisible(this.namedTag.getBoolean("CustomNameAlwaysVisible"));
            }
        }

        if (this.namedTag.contains("Attributes")) {
            ListTag<CompoundTag> attributes = this.namedTag.getList("Attributes", CompoundTag.class);
            for (var nbt : attributes.getAll()) {
                Attribute attribute = Attribute.fromNBT(nbt);
                this.attributes.put(attribute.getId(), attribute);
            }
        }
        this.entityDataMap.getOrCreateFlags();
        this.entityDataMap.put(AIR_SUPPLY, this.namedTag.getShort("Air"));
        this.entityDataMap.put(AIR_SUPPLY_MAX, 400);
        this.entityDataMap.put(NAME, "");
        this.entityDataMap.put(LEASH_HOLDER, -1);
        this.entityDataMap.put(SCALE, 1f);
        this.entityDataMap.put(HEIGHT, this.getHeight());
        this.entityDataMap.put(WIDTH, this.getWidth());
        this.entityDataMap.put(STRUCTURAL_INTEGRITY, (int) this.getHealth());
        this.sendData(this.hasSpawned.values().toArray(Player.EMPTY_ARRAY), entityDataMap);
        this.setDataFlags(EnumSet.of(EntityFlag.CAN_CLIMB, EntityFlag.BREATHING, EntityFlag.HAS_COLLISION, EntityFlag.HAS_GRAVITY));
    }

    protected final void init(IChunk chunk, CompoundTag nbt) {
        if ((chunk == null || chunk.getProvider() == null)) {
            throw new ChunkException("Invalid garbage Chunk given to Entity");
        }
        this.id = Entity.entityCount.getAndIncrement();
        this.isPlayer = this instanceof Player;
        this.temporalVector = new Vector3();
        this.justCreated = true;
        this.namedTag = nbt;
        this.chunk = chunk;
        this.setLevel(chunk.getProvider().getLevel());
        this.server = chunk.getProvider().getLevel().getServer();
        this.boundingBox = new SimpleAxisAlignedBB(0, 0, 0, 0, 0, 0);

        ListTag<DoubleTag> posList = this.namedTag.getList("Pos", DoubleTag.class);
        ListTag<FloatTag> rotationList = this.namedTag.getList("Rotation", FloatTag.class);
        ListTag<DoubleTag> motionList = this.namedTag.getList("Motion", DoubleTag.class);
        this.setPositionAndRotation(
                this.temporalVector.setComponents(
                        posList.get(0).data,
                        posList.get(1).data,
                        posList.get(2).data
                ),
                rotationList.get(0).data,
                rotationList.get(1).data
        );

        this.setMotion(this.temporalVector.setComponents(
                motionList.get(0).data,
                motionList.get(1).data,
                motionList.get(2).data
        ));

        if (!this.namedTag.contains("FallDistance")) {
            this.namedTag.putFloat("FallDistance", 0);
        }
        this.fallDistance = this.namedTag.getFloat("FallDistance");
        this.highestPosition = this.y + this.namedTag.getFloat("FallDistance");

        if (!this.namedTag.contains("Fire") || this.namedTag.getShort("Fire") > 32767) {
            this.namedTag.putShort("Fire", 0);
        }
        this.fireTicks = this.namedTag.getShort("Fire");

        if (!this.namedTag.contains("Air")) {
            this.namedTag.putShort("Air", 300);
        }
        if (!this.namedTag.contains("OnGround")) {
            this.namedTag.putBoolean("OnGround", false);
        }
        this.onGround = this.namedTag.getBoolean("OnGround");

        if (!this.namedTag.contains("Invulnerable")) {
            this.namedTag.putBoolean("Invulnerable", false);
        }
        this.invulnerable = this.namedTag.getBoolean("Invulnerable");

        if (!this.namedTag.contains("Scale")) {
            this.namedTag.putFloat("Scale", 1);
        }
        this.scale = this.namedTag.getFloat("Scale");

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
                this.lastUpdate = this.server.getTick();
            }
        } catch (Exception e) {
            this.close(false);
            throw e;
        }
    }

    public boolean hasCustomName() {
        return !this.getNameTag().isEmpty();
    }

    public String getNameTag() {
        return this.getDataProperty(NAME, "");
    }

    public void setNameTag(String name) {
        this.setDataProperty(NAME, name);
    }

    public boolean isNameTagVisible() {
        return this.getDataFlag(EntityFlag.CAN_SHOW_NAME);
    }

    public void setNameTagVisible(boolean value) {
        this.setDataFlag(EntityFlag.CAN_SHOW_NAME, value);
    }

    public boolean isNameTagAlwaysVisible() {
        return this.getDataProperty(NAMETAG_ALWAYS_SHOW, (byte) 0) == 1;
    }

    public void setNameTagAlwaysVisible(boolean value) {
        this.setDataProperty(NAMETAG_ALWAYS_SHOW, value ? 1 : 0);
    }

    public String getScoreTag() {
        return this.getDataProperty(SCORE, "");
    }

    public void setScoreTag(String score) {
        this.setDataProperty(SCORE, score);
    }

    public boolean isSneaking() {
        return this.getDataFlag(EntityFlag.SNEAKING);
    }

    public void setSneaking(boolean value) {
        this.setDataFlag(EntityFlag.SNEAKING, value);
    }

    public boolean isSwimming() {
        return this.getDataFlag(EntityFlag.SWIMMING);
    }

    public void setSwimming(boolean value) {
        if (isSwimming() == value) {
            return;
        }
        this.setDataFlag(EntityFlag.SWIMMING, value);
        if (Float.compare(getSwimmingHeight(), getHeight()) != 0) {
            recalculateBoundingBox(true);
        }
    }

    public boolean isSprinting() {
        return this.getDataFlag(EntityFlag.SPRINTING);
    }

    public void setSprinting(boolean value) {
        this.setDataFlag(EntityFlag.SPRINTING, value);
    }

    public boolean isGliding() {
        return this.getDataFlag(EntityFlag.GLIDING);
    }

    public void setGliding(boolean value) {
        this.setDataFlag(EntityFlag.GLIDING, value);
    }

    public boolean isImmobile() {
        return this.getDataFlag(EntityFlag.NO_AI);
    }

    public void setImmobile(boolean value) {
        this.setDataFlag(EntityFlag.NO_AI, value);
    }

    public boolean canClimb() {
        return this.getDataFlag(EntityFlag.CAN_CLIMB);
    }

    public void setCanClimb(boolean value) {
        this.setDataFlag(EntityFlag.CAN_CLIMB, value);
    }

    public boolean canClimbWalls() {
        return this.getDataFlag(EntityFlag.WALL_CLIMBING);
    }

    public void setCanClimbWalls(boolean value) {
        this.setDataFlag(EntityFlag.WALL_CLIMBING, value);
    }

    public float getScale() {
        return this.scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
        this.setDataProperty(SCALE, this.scale);
        this.recalculateBoundingBox();
    }

    public float getSwimmingHeight() {
        return getHeight();
    }

    public List<Entity> getPassengers() {
        return passengers;
    }

    public Entity getPassenger() {
        return Iterables.getFirst(this.passengers, null);
    }

    public boolean isPassenger(Entity entity) {
        return this.passengers.contains(entity);
    }

    public boolean isControlling(Entity entity) {
        return this.passengers.indexOf(entity) == 0;
    }

    public boolean hasControllingPassenger() {
        return !this.passengers.isEmpty() && isControlling(this.passengers.get(0));
    }

    public Entity getRiding() {
        return riding;
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
                MobEffectPacket packet = new MobEffectPacket();
                packet.eid = player.getId();
                packet.effectId = effect.getId();
                packet.eventId = MobEffectPacket.EVENT_REMOVE;
                player.dataPacket(packet);
            }

            effect.remove(this);
            effects.remove(type);

            this.recalculateEffectColor();
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
            MobEffectPacket packet = new MobEffectPacket();
            packet.eid = player.getId();
            packet.effectId = effect.getId();
            packet.amplifier = effect.getAmplifier();
            packet.particles = effect.isVisible();
            packet.duration = effect.getDuration();
            if (oldEffect != null) {
                packet.eventId = MobEffectPacket.EVENT_MODIFY;
            } else {
                packet.eventId = MobEffectPacket.EVENT_ADD;
            }

            player.dataPacket(packet);
        }

        effect.add(this);
        effects.put(effect.getType(), effect);

        this.recalculateEffectColor();
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
        if (this.getEntityDataMap().get(HEIGHT) != entityHeight) {
            change = true;
            this.getEntityDataMap().put(HEIGHT, entityHeight);
        }
        if (this.getEntityDataMap().get(WIDTH) != this.getWidth()) {
            change = true;
            this.getEntityDataMap().put(WIDTH, this.getWidth());
        }
        if (send && change) {
            sendData(this.hasSpawned.values().toArray(Player.EMPTY_ARRAY), this.entityDataMap.copy(WIDTH, HEIGHT));
        }
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
                    EFFECT_COLOR, (r << 16) + (g << 8) + b,
                    EFFECT_AMBIENCE, ambient ? 1 : 0
            ));
        } else {
            setDataProperties(Map.of(
                    EFFECT_COLOR, 0,
                    EFFECT_AMBIENCE, 0
            ));
        }
    }

    public void saveNBT() {
        if (!(this instanceof Player)) {
            this.namedTag.putString("identifier", this.getIdentifier());
            if (!this.getNameTag().isEmpty()) {
                this.namedTag.putString("CustomName", this.getNameTag());
                this.namedTag.putBoolean("CustomNameVisible", this.isNameTagVisible());
                this.namedTag.putBoolean("CustomNameAlwaysVisible", this.isNameTagAlwaysVisible());
            } else {
                this.namedTag.remove("CustomName");
                this.namedTag.remove("CustomNameVisible");
                this.namedTag.remove("CustomNameAlwaysVisible");
            }
            if (this.entityUniqueId == null) {
                this.entityUniqueId = UUID.randomUUID();
            }
            this.namedTag.putString("uuid", this.entityUniqueId.toString());
        }

        this.namedTag.putList("Pos", new ListTag<DoubleTag>()
                .add(new DoubleTag(this.x))
                .add(new DoubleTag(this.y))
                .add(new DoubleTag(this.z))
        );

        this.namedTag.putList("Motion", new ListTag<DoubleTag>()
                .add(new DoubleTag(this.motionX))
                .add(new DoubleTag(this.motionY))
                .add(new DoubleTag(this.motionZ))
        );

        this.namedTag.putList("Rotation", new ListTag<FloatTag>()
                .add(new FloatTag((float) this.yaw))
                .add(new FloatTag((float) this.pitch))
        );

        this.namedTag.putFloat("FallDistance", this.fallDistance);
        this.namedTag.putShort("Fire", this.fireTicks);
        this.namedTag.putShort("Air", this.getDataProperty(AIR_SUPPLY, (short) 0));
        this.namedTag.putBoolean("OnGround", this.onGround);
        this.namedTag.putBoolean("Invulnerable", this.invulnerable);
        this.namedTag.putFloat("Scale", this.scale);

        if (!this.effects.isEmpty()) {
            ListTag<CompoundTag> list = new ListTag<>();
            for (Effect effect : this.effects.values()) {
                list.add(new CompoundTag()
                        .putByte("Id", effect.getId())
                        .putByte("Amplifier", effect.getAmplifier())
                        .putInt("Duration", effect.getDuration())
                        .putBoolean("Ambient", false)
                        .putBoolean("ShowParticles", effect.isVisible())
                );
            }

            this.namedTag.putList("ActiveEffects", list);
        } else {
            this.namedTag.remove("ActiveEffects");
        }

        if (!this.attributes.isEmpty()) {
            ListTag<CompoundTag> attributes = new ListTag<>();
            for (var attribute : this.attributes.values()) {
                CompoundTag nbt = Attribute.toNBT(attribute);
                attributes.add(nbt);
            }
            this.namedTag.putList("Attributes", attributes);
        } else {
            this.namedTag.remove("Attributes");
        }
    }

    /**
     * The name that English name of the type of this entity.
     */
    public String getOriginalName() {
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
     * 将这个实体在客户端生成，让该玩家可以看到它
     * <p>
     * Spawn this entity on the client side so that the player can see it
     *
     * @param player the player
     */
    public void spawnTo(Player player) {

        if (!this.hasSpawned.containsKey(player.getLoaderId()) && this.chunk != null && player.getUsedChunks().contains(Level.chunkHash(this.chunk.getX(), this.chunk.getZ()))) {
            this.hasSpawned.put(player.getLoaderId(), player);
            player.dataPacket(createAddEntityPacket());
        }

        if (this.riding != null) {
            this.riding.spawnTo(player);

            SetEntityLinkPacket pkk = new SetEntityLinkPacket();
            pkk.vehicleUniqueId = this.riding.getId();
            pkk.riderUniqueId = this.getId();
            pkk.type = EntityLink.Type.RIDER;
            pkk.immediate = 1;

            player.dataPacket(pkk);
        }
    }

    protected DataPacket createAddEntityPacket() {
        AddEntityPacket addEntity = new AddEntityPacket();
        addEntity.type = this.getNetworkId();
        addEntity.entityUniqueId = this.getId();
        if (this instanceof CustomEntity) {
            addEntity.id = this.getIdentifier();
        }
        addEntity.entityRuntimeId = this.getId();
        addEntity.yaw = (float) this.yaw;
        addEntity.headYaw = (float) this.yaw;
        addEntity.pitch = (float) this.pitch;
        addEntity.x = (float) this.x;
        addEntity.y = (float) this.y + this.getBaseOffset();
        addEntity.z = (float) this.z;
        addEntity.speedX = (float) this.motionX;
        addEntity.speedY = (float) this.motionY;
        addEntity.speedZ = (float) this.motionZ;
        addEntity.entityData = this.entityDataMap;

        addEntity.links = new EntityLink[this.passengers.size()];
        for (int i = 0; i < addEntity.links.length; i++) {
            addEntity.links[i] = new EntityLink(this.getId(), this.passengers.get(i).getId(), i == 0 ? EntityLink.Type.RIDER : EntityLink.Type.PASSENGER, false, false);
        }

        return addEntity;
    }

    public Map<Integer, Player> getViewers() {
        return hasSpawned;
    }

    public void sendPotionEffects(Player player) {
        for (Effect effect : effects.values()) {
            if (effect.getId() != null) {
                MobEffectPacket packet = new MobEffectPacket();
                packet.eid = this.getId();
                packet.effectId = effect.getId();
                packet.amplifier = effect.getAmplifier();
                packet.particles = effect.isVisible();
                packet.duration = effect.getDuration();
                packet.eventId = MobEffectPacket.EVENT_ADD;
                player.dataPacket(packet);
            }
        }
    }

    public void sendData(Player player) {
        this.sendData(player, null);
    }

    public void sendData(Player player, EntityDataMap data) {
        SetEntityDataPacket pk = new SetEntityDataPacket();
        pk.eid = this.getId();
        pk.entityData = data == null ? this.entityDataMap : data;
        pk.syncedProperties = this.propertySyncData();

        player.dataPacket(pk);
    }

    public void sendData(Player[] players) {
        this.sendData(players, null);
    }

    public void sendData(Player[] players, EntityDataMap data) {
        SetEntityDataPacket pk = new SetEntityDataPacket();
        pk.eid = this.getId();
        pk.entityData = data == null ? this.entityDataMap : data;
        pk.syncedProperties = this.propertySyncData();

        for (Player player : players) {
            if (player == this) {
                continue;
            }
            player.dataPacket(pk);
        }
        if (this instanceof Player player) {
            player.dataPacket(pk);
        }
    }

    public void despawnFrom(Player player) {
        if (this.hasSpawned.containsKey(player.getLoaderId())) {
            RemoveEntityPacket pk = new RemoveEntityPacket();
            pk.eid = this.getId();
            player.dataPacket(pk);
            this.hasSpawned.remove(player.getLoaderId());
        }
    }

    /**
     * 当一个实体被攻击时(即接受一个实体伤害事件 这个事件可以是由其他实体攻击导致，也可能是自然伤害)调用.
     * <p>
     * Called when an entity is attacked (i.e. receives an entity damage event. This event can be caused by an attack by another entity, or it can be a natural damage).
     *
     * @param source 记录伤害源的事件<br>Record the event of the source of the attack
     * @return 是否攻击成功<br>Whether the attack was successful
     */
    public boolean attack(EntityDamageEvent source) {
        //火焰保护附魔实现
        if (hasEffect(EffectType.FIRE_RESISTANCE)
                && (source.getCause() == DamageCause.FIRE
                || source.getCause() == DamageCause.FIRE_TICK
                || source.getCause() == DamageCause.LAVA)) {
            return false;
        }

        //水生生物免疫溺水
        if (this instanceof EntitySwimmable swimmable && !swimmable.canDrown() && source.getCause() == DamageCause.DROWNING)
            return false;

        //飞行生物免疫摔伤
        if (this instanceof EntityFlyable flyable && !flyable.hasFallingDamage() && source.getCause() == DamageCause.FALL)
            return false;

        //事件回调函数
        getServer().getPluginManager().callEvent(source);
        if (source.isCancelled()) {
            return false;
        }

        // Make fire aspect to set the target in fire before dealing any damage so the target is in fire on death even if killed by the first hit
        if (source instanceof EntityDamageByEntityEvent) {
            Enchantment[] enchantments = ((EntityDamageByEntityEvent) source).getWeaponEnchantments();
            if (enchantments != null) {
                for (Enchantment enchantment : enchantments) {
                    enchantment.doAttack(((EntityDamageByEntityEvent) source).getDamager(), this);
                }
            }
        }

        //吸收伤害实现
        if (this.absorption > 0) {  // Damage Absorption
            this.setAbsorption(Math.max(0, this.getAbsorption() + source.getDamage(EntityDamageEvent.DamageModifier.ABSORPTION)));
        }

        //修改最后一次伤害
        setLastDamageCause(source);

        //计算血量
        float newHealth = getHealth() - source.getFinalDamage();

        //only player
        if (newHealth < 1 && this instanceof Player player) {
            if (source.getCause() != DamageCause.VOID && source.getCause() != DamageCause.SUICIDE) {
                boolean totem = false;
                boolean isOffhand = false;
                if (player.getOffhandInventory().getItem(0) instanceof ItemTotemOfUndying) {
                    totem = true;
                    isOffhand = true;
                } else if (player.getInventory().getItemInHand() instanceof ItemTotemOfUndying) {
                    totem = true;
                }
                //复活图腾实现
                if (totem) {
                    this.getLevel().addLevelEvent(this, LevelEventPacket.EVENT_SOUND_TOTEM_USED);
                    this.getLevel().addParticleEffect(this, ParticleEffect.TOTEM);

                    this.extinguish();
                    this.removeAllEffects();
                    this.setHealth(1);

                    this.addEffect(Effect.get(EffectType.REGENERATION).setDuration(800).setAmplifier(1));
                    this.addEffect(Effect.get(EffectType.FIRE_RESISTANCE).setDuration(800));
                    this.addEffect(Effect.get(EffectType.ABSORPTION).setDuration(100).setAmplifier(1));

                    EntityEventPacket pk = new EntityEventPacket();
                    pk.eid = this.getId();
                    pk.event = EntityEventPacket.CONSUME_TOTEM;
                    player.dataPacket(pk);

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

        setHealth(newHealth);

        if (!(this instanceof EntityArmorStand)) {
            this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(attacker, this.clone(), VibrationType.ENTITY_DAMAGE));
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

    public void heal(EntityRegainHealthEvent source) {
        this.server.getPluginManager().callEvent(source);
        if (source.isCancelled()) {
            return;
        }
        this.setHealth(this.getHealth() + source.getAmount());
    }

    public void heal(float amount) {
        this.heal(new EntityRegainHealthEvent(this, amount, EntityRegainHealthEvent.CAUSE_REGEN));
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        if (this.health == health) {
            return;
        }

        if (health < 1) {
            if (this.isAlive()) {
                this.kill();
            }
        } else if (health <= this.getMaxHealth() || health < this.health) {
            this.health = health;
        } else {
            this.health = this.getMaxHealth();
        }

        setDataProperty(STRUCTURAL_INTEGRITY, (int) this.health);
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

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
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

        if (riding != null && !riding.isAlive() && riding instanceof EntityRideable entityRideable) {
            entityRideable.dismountEntity(this);
        }
        updatePassengers();

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

        if (this.y < (level.getMinHeight() - 18) && this.isAlive()) {
            if (this instanceof Player player) {
                if (!player.isCreative()) this.attack(new EntityDamageEvent(this, DamageCause.VOID, 10));
            } else {
                this.attack(new EntityDamageEvent(this, DamageCause.VOID, 10));
                hasUpdate = true;
            }
        }

        if (this.fireTicks > 0) {
            if (this.fireProof) {
                this.fireTicks -= 4 * tickDiff;
                if (this.fireTicks < 0) {
                    this.fireTicks = 0;
                }
            } else {
                if (!this.hasEffect(EffectType.FIRE_RESISTANCE) && ((this.fireTicks % 20) == 0 || tickDiff > 20)) {
                    this.attack(new EntityDamageEvent(this, DamageCause.FIRE_TICK, 1));
                }
                this.fireTicks -= tickDiff;
            }
            if (this.fireTicks <= 0) {
                this.extinguish();
            } else if (!this.fireProof && (!(this instanceof Player player) || !player.isSpectator())) {
                this.setDataFlag(EntityFlag.ON_FIRE);
                hasUpdate = true;
            }
        }

        if (this.noDamageTicks > 0) {
            this.noDamageTicks -= tickDiff;
            if (this.noDamageTicks < 0) {
                this.noDamageTicks = 0;
            }
        }

        if (this.inPortalTicks == 80) {//handle portal teleport
            EntityPortalEnterEvent ev = new EntityPortalEnterEvent(this, PortalType.NETHER);
            getServer().getPluginManager().callEvent(ev);//call event

            if (!ev.isCancelled() && (level.getDimension() == Level.DIMENSION_OVERWORLD || level.getDimension() == Level.DIMENSION_NETHER)) {
                Position newPos = PortalHelper.convertPosBetweenNetherAndOverworld(this);
                if (newPos != null) {
                    Position nearestPortal = PortalHelper.getNearestValidPortal(newPos);
                    if (nearestPortal != null) {
                        teleport(nearestPortal.add(0.5, 0, 0.5), PlayerTeleportEvent.TeleportCause.NETHER_PORTAL);
                    } else {
                        final Position finalPos = newPos.add(1.5, 1, 1.5);
                        if (teleport(finalPos, PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)) {
                            server.getScheduler().scheduleDelayedTask(new Task() {
                                @Override
                                public void onRun(int currentTick) {
                                    // dirty hack to make sure chunks are loaded and generated before spawning
                                    // player
                                    inPortalTicks = 81;
                                    teleport(finalPos, PlayerTeleportEvent.TeleportCause.NETHER_PORTAL);
                                    PortalHelper.spawnPortal(newPos);
                                }
                            }, 5);
                        }
                    }
                }
            }
        }
        this.age += tickDiff;
        this.ticksLived += tickDiff;

        return hasUpdate;
    }

    public void updateMovement() {
        //这样做是为了向后兼容旧插件
        if (!enableHeadYaw()) {
            this.headYaw = this.yaw;
        }
        double diffPosition = (this.x - this.lastX) * (this.x - this.lastX) + (this.y - this.lastY) * (this.y - this.lastY) + (this.z - this.lastZ) * (this.z - this.lastZ);
        double diffRotation = (enableHeadYaw() ? (this.headYaw - this.lastHeadYaw) * (this.headYaw - this.lastHeadYaw) : 0) + (this.yaw - this.lastYaw) * (this.yaw - this.lastYaw) + (this.pitch - this.lastPitch) * (this.pitch - this.lastPitch);

        double diffMotion = (this.motionX - this.lastMotionX) * (this.motionX - this.lastMotionX) + (this.motionY - this.lastMotionY) * (this.motionY - this.lastMotionY) + (this.motionZ - this.lastMotionZ) * (this.motionZ - this.lastMotionZ);

        if (diffPosition > 0.0001 || diffRotation > 1.0) { //0.2 ** 2, 1.5 ** 2
            if (diffPosition > 0.0001) {
                if (this.isOnGround()) {
                    this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this instanceof EntityProjectile projectile ? projectile.shootingEntity : this, this.clone(), VibrationType.STEP));
                } else if (this.isTouchingWater()) {
                    this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this instanceof EntityProjectile projectile ? projectile.shootingEntity : this, this.clone(), VibrationType.SWIM));
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
     * 增加运动 (仅发送数据包，如果需要请使用{@link #setMotion})
     * <p>
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

    /*
     * 请注意此方法仅向客户端发motion包，并不会真正的将motion数值加到实体的motion(x|y|z)上<p/>
     * 如果你想在实体的motion基础上增加，请直接将要添加的motion数值加到实体的motion(x|y|z)上，像这样：<p/>
     * entity.motionX += vector3.x;<p/>
     * entity.motionY += vector3.y;<p/>
     * entity.motionZ += vector3.z;<p/>
     * 对于玩家实体，你不应该使用此方法！
     */
    public void addMotion(double motionX, double motionY, double motionZ) {
        SetEntityMotionPacket pk = new SetEntityMotionPacket();
        pk.eid = this.getId();
        pk.motionX = (float) motionX;
        pk.motionY = (float) motionY;
        pk.motionZ = (float) motionZ;

        Server.broadcastPacket(this.hasSpawned.values(), pk);
    }


    protected void broadcastMovement(boolean tp) {
        var pk = new MoveEntityAbsolutePacket();
        pk.eid = this.getId();
        pk.x = this.x;
        pk.y = this.y + this.getBaseOffset();
        pk.z = this.z;
        pk.headYaw = yaw;
        pk.pitch = pitch;
        pk.yaw = yaw;
        pk.teleport = tp;
        pk.onGround = this.onGround;
        Server.broadcastPacket(hasSpawned.values(), pk);
    }

    @Override
    public Vector3 getDirectionVector() {
        return super.getDirectionVector();
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

    public boolean mountEntity(Entity entity) {
        return mountEntity(entity, EntityLink.Type.RIDER);
    }

    /**
     * Mount an Entity from a/into vehicle
     *
     * @param entity The target Entity
     * @return {@code true} if the mounting successful
     */
    public boolean mountEntity(Entity entity, EntityLink.Type mode) {
        Objects.requireNonNull(entity, "The target of the mounting entity can't be null");

        if (isPassenger(entity) || entity.riding != null && !entity.riding.dismountEntity(entity, false)) {
            return false;
        }

        // Entity entering a vehicle
        EntityVehicleEnterEvent ev = new EntityVehicleEnterEvent(entity, this);
        server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return false;
        }

        broadcastLinkPacket(entity, mode);

        // Add variables to entity
        entity.riding = this;
        entity.setDataFlag(EntityFlag.RIDING);
        passengers.add(entity);

        entity.setSeatPosition(getMountedOffset(entity));
        updatePassengerPosition(entity);
        return true;
    }

    public boolean dismountEntity(Entity entity) {
        return this.dismountEntity(entity, true);
    }

    public boolean dismountEntity(Entity entity, boolean sendLinks) {
        // Run the events
        EntityVehicleExitEvent ev = new EntityVehicleExitEvent(entity, this);
        server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            int seatIndex = this.passengers.indexOf(entity);
            if (seatIndex == 0) {
                this.broadcastLinkPacket(entity, EntityLink.Type.RIDER);
            } else if (seatIndex != -1) {
                this.broadcastLinkPacket(entity, EntityLink.Type.PASSENGER);
            }
            return false;
        }

        if (sendLinks) {
            broadcastLinkPacket(entity, EntityLink.Type.REMOVE);
        }

        // refresh the entity
        entity.riding = null;
        entity.setDataFlag(EntityFlag.RIDING, false);
        passengers.remove(entity);

        entity.setSeatPosition(new Vector3f());
        updatePassengerPosition(entity);

        if (entity instanceof Player player) {
            player.resetFallDistance();
        }
        return true;
    }

    protected void broadcastLinkPacket(Entity rider, EntityLink.Type type) {
        SetEntityLinkPacket pk = new SetEntityLinkPacket();
        pk.vehicleUniqueId = getId();         // To the?
        pk.riderUniqueId = rider.getId(); // From who?
        pk.type = type;
        pk.riderInitiated = type != EntityLink.Type.REMOVE;
        Server.broadcastPacket(this.hasSpawned.values(), pk);
    }

    public void updatePassengers() {
        if (this.passengers.isEmpty()) {
            return;
        }

        for (Entity passenger : new ArrayList<>(this.passengers)) {
            if (!passenger.isAlive()) {
                dismountEntity(passenger);
                continue;
            }

            updatePassengerPosition(passenger);
        }
    }

    protected void updatePassengerPosition(Entity passenger) {
        passenger.setPosition(this.add(passenger.getSeatPosition().asVector3()));
    }

    public Vector3f getSeatPosition() {
        return this.getDataProperty(SEAT_OFFSET);
    }

    public void setSeatPosition(Vector3f pos) {
        this.setDataProperty(SEAT_OFFSET, pos);
    }

    public Vector3f getMountedOffset(Entity entity) {
        return new Vector3f(0, getHeight() * 0.75f);
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

    public float getAbsorption() {
        return absorption;
    }

    public void setAbsorption(float absorption) {
        if (absorption != this.absorption) {
            this.absorption = absorption;
        }
    }

    public void syncAttribute(Attribute attribute) {
        UpdateAttributesPacket pk = new UpdateAttributesPacket();
        pk.entries = new Attribute[]{attribute};
        pk.entityId = this.getId();
        Server.broadcastPacket(this.getViewers().values(), pk);
    }

    public void syncAttributes() {
        UpdateAttributesPacket pk = new UpdateAttributesPacket();
        pk.entries = this.attributes.values().stream().filter(Attribute::isSyncable).toArray(Attribute[]::new);
        pk.entityId = this.getId();
        Server.broadcastPacket(this.getViewers().values(), pk);
    }


    public boolean canBePushed() {
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
        this.setDataFlag(EntityFlag.ON_FIRE, false);
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

    protected void updateFallState(boolean onGround) {
        if (onGround) {
            fallDistance = (float) (this.highestPosition - this.y);

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

    public void fall(float fallDistance) {//todo: check why @param fallDistance always less than the real distance
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
            float damage = fallDistance - 3.255f - jumpBoost;

            if (damage > 0) {
                if (!this.isSneaking()) {
                    if (!(this instanceof EntityItem item) ||
                            !ItemTags.getTagSet(item.getIdentifier()).contains(ItemTags.WOOL)) {
                        this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.clone(), VibrationType.HIT_GROUND));
                    }
                }
                this.attack(new EntityDamageEvent(this, DamageCause.FALL, damage));
            }
        }

        down.onEntityFallOn(this, fallDistance);

        if (fallDistance > 0.75) {//todo: moving these into their own classes (method "onEntityFallOn()")
            if (Block.FARMLAND.equals(down.getId())) {
                if (onPhysicalInteraction(down, false)) {
                    return;
                }
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

    public boolean onInteract(Player player, Item item) {
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

        return true;
    }

    @NotNull
    public Position getPosition() {
        return new Position(this.x, this.y, this.z, this.level);
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
        double y = this.y + this.getEyeHeight();
        Block block = this.level.getBlock(
                this.temporalVector.setComponents(
                        NukkitMath.floorDouble(this.x),
                        NukkitMath.floorDouble(y),
                        NukkitMath.floorDouble(this.z))
        );

        AxisAlignedBB bb = block.getBoundingBox();

        return bb != null && block.isSolid() && !block.isTransparent() && bb.intersectsWith(this.getBoundingBox());

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
    public boolean move(double dx, double dy, double dz) {
        if (dx == 0 && dz == 0 && dy == 0) {
            this.onGround = !this.getPosition().setComponents(this.down()).getTickCachedLevelBlock().canPassThrough();
            return true;
        }


        this.ySize *= 0.4F;

        double movX = dx;
        double movY = dy;
        double movZ = dz;

        AxisAlignedBB axisalignedbb = this.boundingBox.clone();

        var list = this.noClip ? AxisAlignedBB.EMPTY_LIST : this.level.fastCollisionCubes(this, this.boundingBox.addCoord(dx, dy, dz), false);

        for (AxisAlignedBB bb : list) {
            dy = bb.calculateYOffset(this.boundingBox, dy);
        }

        this.boundingBox.offset(0, dy, 0);

        boolean fallingFlag = (this.onGround || (dy != movY && movY < 0));

        for (AxisAlignedBB bb : list) {
            dx = bb.calculateXOffset(this.boundingBox, dx);
        }

        this.boundingBox.offset(dx, 0, 0);

        for (AxisAlignedBB bb : list) {
            dz = bb.calculateZOffset(this.boundingBox, dz);
        }

        this.boundingBox.offset(0, 0, dz);

        if (this.getStepHeight() > 0 && fallingFlag && this.ySize < 0.05 && (movX != dx || movZ != dz)) {
            double cx = dx;
            double cy = dy;
            double cz = dz;
            dx = movX;
            dy = this.getStepHeight();
            dz = movZ;

            AxisAlignedBB axisalignedbb1 = this.boundingBox.clone();

            this.boundingBox.setBB(axisalignedbb);

            list = this.level.fastCollisionCubes(this, this.boundingBox.addCoord(dx, dy, dz), false);

            for (AxisAlignedBB bb : list) {
                dy = bb.calculateYOffset(this.boundingBox, dy);
            }

            this.boundingBox.offset(0, dy, 0);

            for (AxisAlignedBB bb : list) {
                dx = bb.calculateXOffset(this.boundingBox, dx);
            }

            this.boundingBox.offset(dx, 0, 0);

            for (AxisAlignedBB bb : list) {
                dz = bb.calculateZOffset(this.boundingBox, dz);
            }

            this.boundingBox.offset(0, 0, dz);

            this.boundingBox.offset(0, 0, dz);

            if ((cx * cx + cz * cz) >= (dx * dx + dz * dz)) {
                dx = cx;
                dy = cy;
                dz = cz;
                this.boundingBox.setBB(axisalignedbb1);
            } else {
                this.ySize += 0.5F;
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

        //TODO: vehicle collision events (first we need to spawn them!)
        return true;
    }

    protected void checkGroundState(double movX, double movY, double movZ, double dx, double dy, double dz) {
        if (this.noClip) {
            this.isCollidedVertically = false;
            this.isCollidedHorizontally = false;
            this.isCollided = false;
            this.onGround = false;
        } else {
            this.isCollidedVertically = movY != dy;
            this.isCollidedHorizontally = (movX != dx || movZ != dz);
            this.isCollided = (this.isCollidedHorizontally || this.isCollidedVertically);
            this.onGround = (movY != dy && movY < 0);
        }
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

        setDataFlagExtend(EntityFlag.IN_SCAFFOLDING, scaffolding);

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
                        setDataFlagExtend(EntityFlag.OVER_SCAFFOLDING, true);
                        break outerScaffolding;
                    }
                }
            }
        }

        if (endPortal) {//handle endPortal teleport
            if (!inEndPortal) {
                inEndPortal = true;
                if (this.getRiding() == null && this.getPassengers().isEmpty() && !(this instanceof EntityEnderDragon)) {
                    EntityPortalEnterEvent ev = new EntityPortalEnterEvent(this, PortalType.END);
                    getServer().getPluginManager().callEvent(ev);

                    if (!ev.isCancelled() && (level.getDimension() == Level.DIMENSION_OVERWORLD || level.getDimension() == Level.DIMENSION_THE_END)) {
                        final Position newPos = PortalHelper.moveToTheEnd(this);
                        if (newPos != null) {
                            if (newPos.getLevel().getDimension() == Level.DIMENSION_THE_END) {
                                if (teleport(newPos.add(0.5, 1, 0.5), PlayerTeleportEvent.TeleportCause.END_PORTAL)) {
                                    server.getScheduler().scheduleDelayedTask(new Task() {
                                        @Override
                                        public void onRun(int currentTick) {
                                            // dirty hack to make sure chunks are loaded and generated before spawning player
                                            teleport(newPos.add(0.5, 1, 0.5), PlayerTeleportEvent.TeleportCause.END_PORTAL);
                                            BlockEndPortal.spawnObsidianPlatform(newPos);
                                        }
                                    }, 5);
                                }
                            } else {
                                if (teleport(newPos, PlayerTeleportEvent.TeleportCause.END_PORTAL)) {
                                    server.getScheduler().scheduleDelayedTask(new Task() {
                                        @Override
                                        public void onRun(int currentTick) {
                                            // dirty hack to make sure chunks are loaded and generated before spawning player
                                            teleport(newPos, PlayerTeleportEvent.TeleportCause.END_PORTAL);
                                        }
                                    }, 5);
                                }
                            }
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

    public void setDataProperties(Map<EntityDataType<?>, Object> maps) {
        setDataProperties(maps, true);
    }

    public void setDataProperties(Map<EntityDataType<?>, Object> maps, boolean send) {
        EntityDataMap sendMap = new EntityDataMap();
        for (var e : maps.entrySet()) {
            EntityDataType<?> key = e.getKey();
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

    public boolean setDataProperty(EntityDataType<?> key, Object value) {
        return setDataProperty(key, value, true);
    }

    public boolean setDataProperty(EntityDataType<?> key, Object value, boolean send) {
        if (this.getEntityDataMap().containsKey(key) && this.getEntityDataMap().get(key).equals(value)) {
            return false;
        }

        this.getEntityDataMap().put(key, value);
        if (send) {
            EntityDataMap map = new EntityDataMap();
            map.put(key, value);
            this.sendData(this.hasSpawned.values().toArray(Player.EMPTY_ARRAY), map);
        }
        return true;
    }

    public EntityDataMap getEntityDataMap() {
        return this.entityDataMap;
    }

    @NotNull
    public <T> T getDataProperty(EntityDataType<T> key) {
        return this.getEntityDataMap().get(key);
    }

    @NotNull
    public <T> T getDataProperty(EntityDataType<T> key, T d) {
        return this.getEntityDataMap().getOrDefault(key, d);
    }

    public void setDataFlag(EntityFlag entityFlag) {
        this.setDataFlag(entityFlag, true);
    }

    public void setDataFlag(EntityFlag entityFlag, boolean value) {
        if (this.getEntityDataMap().existFlag(entityFlag) ^ value) {
            this.getEntityDataMap().setFlag(entityFlag, value);
            EntityDataMap entityDataMap = new EntityDataMap();
            entityDataMap.put(EntityDataTypes.FLAGS, this.getEntityDataMap().getFlags());
            sendData(this.hasSpawned.values().toArray(Player.EMPTY_ARRAY), entityDataMap);
        }
    }

    public void setDataFlags(EnumSet<EntityFlag> entityFlags) {
        this.getEntityDataMap().put(FLAGS, entityFlags);
        sendData(this.hasSpawned.values().toArray(Player.EMPTY_ARRAY), entityDataMap);
    }

    public void setDataFlagExtend(EntityFlag entityFlag) {
        this.setDataFlag(entityFlag, true);
    }

    public void setDataFlagExtend(EntityFlag entityFlag, boolean value) {
        if (this.getEntityDataMap().existFlag(entityFlag) ^ value) {
            EnumSet<EntityFlag> entityFlags = this.getEntityDataMap().getOrDefault(EntityDataTypes.FLAGS_2, EnumSet.noneOf(EntityFlag.class));
            if (value) {
                entityFlags.add(entityFlag);
            } else {
                entityFlags.remove(entityFlag);
            }
            this.getEntityDataMap().put(EntityDataTypes.FLAGS_2, entityFlags);
            EntityDataMap entityDataMap = new EntityDataMap();
            entityDataMap.put(EntityDataTypes.FLAGS_2, entityFlags);
            sendData(this.hasSpawned.values().toArray(Player.EMPTY_ARRAY), entityDataMap);
        }
    }

    public void setDataFlagsExtend(EnumSet<EntityFlag> entityFlags) {
        this.getEntityDataMap().put(FLAGS_2, entityFlags);
        sendData(this.hasSpawned.values().toArray(Player.EMPTY_ARRAY), entityDataMap);
    }

    public boolean getDataFlag(EntityFlag id) {
        return this.getEntityDataMap().getOrCreateFlags().contains(id);
    }

    public void setPlayerFlag(PlayerFlag entityFlag) {
        byte flags = this.getEntityDataMap().getOrDefault(EntityDataTypes.PLAYER_FLAGS, (byte) 0);
        flags ^= (byte) (1 << entityFlag.getValue());
        this.setDataProperty(EntityDataTypes.PLAYER_FLAGS, flags);
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
        return this.getDataFlag(EntityFlag.DAMAGE_NEARBY_MOBS);
    }


    public void setSpinAttacking(boolean value) {
        this.setDataFlag(EntityFlag.DAMAGE_NEARBY_MOBS, value);
    }


    public void setSpinAttacking() {
        this.setSpinAttacking(true);
    }


    public boolean isNoClip() {
        return noClip;
    }


    public void setNoClip(boolean noClip) {
        this.noClip = noClip;
        this.setDataFlag(EntityFlag.HAS_COLLISION, noClip);
    }


    public boolean isBoss() {
        return false;
    }


    public void addTag(String tag) {
        this.namedTag.putList("Tags", this.namedTag.getList("Tags", StringTag.class).add(new StringTag(tag)));
    }


    public void removeTag(String tag) {
        ListTag<StringTag> tags = this.namedTag.getList("Tags", StringTag.class);
        tags.remove(new StringTag(tag));
        this.namedTag.putList("Tags", tags);
    }


    public boolean containTag(String tag) {
        return this.namedTag.getList("Tags", StringTag.class).getAll().stream().anyMatch(t -> t.data.equals(tag));
    }


    public List<StringTag> getAllTags() {
        return this.namedTag.getList("Tags", StringTag.class).getAll();
    }


    public float getFreezingEffectStrength() {
        return this.getDataProperty(FREEZING_EFFECT_STRENGTH);
    }


    public void setFreezingEffectStrength(float strength) {
        if (strength < 0 || strength > 1)
            throw new IllegalArgumentException("Freezing Effect Strength must be between 0 and 1");
        this.setDataProperty(FREEZING_EFFECT_STRENGTH, strength);
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
        this.setDataProperty(AMBIENT_SOUND_INTERVAL, interval);
    }


    public void setAmbientSoundIntervalRange(float range) {
        this.setDataProperty(AMBIENT_SOUND_INTERVAL, range);
    }


    public void setAmbientSoundEvent(Sound sound) {
        this.setAmbientSoundEventName(sound.getSound());
    }


    public void setAmbientSoundEventName(String eventName) {
        this.setDataProperty(AMBIENT_SOUND_EVENT_NAME, eventName);
    }


    public void playAnimation(AnimateEntityPacket.Animation animation) {
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
    public void playAnimation(AnimateEntityPacket.Animation animation, Collection<Player> players) {
        var pk = new AnimateEntityPacket();
        pk.parseFromAnimation(animation);
        pk.entityRuntimeIds.add(this.getId());
        Server.broadcastPacket(players, pk);
    }


    public void playActionAnimation(AnimatePacket.Action action, float rowingTime) {
        var viewers = new HashSet<>(this.getViewers().values());
        if (this.isPlayer) viewers.add((Player) this);
        playActionAnimation(action, rowingTime, viewers);
    }

    /**
     * Play the action animation of this entity to a specified group of players
     * <p>
     * 向指定玩家群体播放此实体的action动画
     *
     * @param action     the action
     * @param rowingTime the rowing time
     * @param players    可视玩家 Visible Player
     */
    public void playActionAnimation(AnimatePacket.Action action, float rowingTime, Collection<Player> players) {
        var pk = new AnimatePacket();
        pk.action = action;
        pk.rowingTime = rowingTime;
        pk.eid = this.getId();
        Server.broadcastPacket(players, pk);
    }

    private boolean validateAndSetIntProperty(String identifier, int value) {
        if (!intProperties.containsKey(identifier)) return false;
        intProperties.put(identifier, value);
        return true;
    }


    public final boolean setIntEntityProperty(String identifier, int value) {
        return validateAndSetIntProperty(identifier, value);
    }


    public final boolean setBooleanEntityProperty(String identifier, boolean value) {
        return validateAndSetIntProperty(identifier, value ? 1 : 0);
    }


    public final boolean setFloatEntityProperty(String identifier, float value) {
        if (!floatProperties.containsKey(identifier)) return false;
        floatProperties.put(identifier, value);
        return true;
    }


    public final boolean setEnumEntityProperty(String identifier, String value) {
        if (!intProperties.containsKey(identifier)) return false;
        List<EntityProperty> entityPropertyList = EntityProperty.getEntityProperty(this.getIdentifier().toString());

        for (EntityProperty property : entityPropertyList) {
            if (!identifier.equals(property.getIdentifier()) ||
                    !(property instanceof EnumEntityProperty enumProperty)) {
                continue;
            }
            int index = enumProperty.findIndex(value);

            if (index >= 0) {
                intProperties.put(identifier, index);
                return true;
            }
            return false;
        }
        return false;
    }


    private void initEntityProperties() {
        if (this.getIdentifier() != null) {
            initEntityProperties(this.getIdentifier().toString());
        }
    }


    private void initEntityProperties(String entityIdentifier) {
        List<EntityProperty> entityPropertyList = EntityProperty.getEntityProperty(entityIdentifier);
        if (entityPropertyList.isEmpty()) return;

        for (EntityProperty property : entityPropertyList) {
            final String identifier = property.getIdentifier();

            if (property instanceof FloatEntityProperty floatProperty) {
                floatProperties.put(identifier, floatProperty.getDefaultValue());
            } else if (property instanceof IntEntityProperty intProperty) {
                intProperties.put(identifier, intProperty.getDefaultValue());
            } else if (property instanceof BooleanEntityProperty booleanProperty) {
                intProperties.put(identifier, booleanProperty.getDefaultValue() ? 1 : 0);
            } else if (property instanceof EnumEntityProperty enumProperty) {
                intProperties.put(identifier, enumProperty.findIndex(enumProperty.getDefaultValue()));
            }
        }
    }


    private PropertySyncData propertySyncData() {
        Collection<Integer> intValues = intProperties.values();
        int[] intArray = new int[intValues.size()];
        int i = 0;
        for (Integer value : intValues) {
            intArray[i++] = value;
        }

        Collection<Float> floatValues = floatProperties.values();
        float[] floatArray = new float[floatValues.size()];
        i = 0;
        for (Float value : floatValues) {
            floatArray[i++] = value;
        }

        return new PropertySyncData(intArray, floatArray);
    }

    public Map<Integer, Attribute> getAttributes() {
        return attributes;
    }

    public boolean isInitialized() {
        return initialized;
    }
}
