package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDirt;
import cn.nukkit.block.BlockTurtleEgg;
import cn.nukkit.entity.*;
import cn.nukkit.entity.ai.EntityAI;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.FluctuateController;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.evaluator.PassByTimeEvaluator;
import cn.nukkit.entity.ai.evaluator.ProbabilityEvaluator;
import cn.nukkit.entity.ai.executor.AnimalGrowExecutor;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.LookAtTargetExecutor;
import cn.nukkit.entity.ai.executor.TameHorseExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestFeedingPlayerSensor;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.custom.CustomEntity;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.event.block.FarmLandDecayEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityFallEvent;
import cn.nukkit.inventory.HorseInventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.*;
import cn.nukkit.network.protocol.types.EntityLink;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.Utils;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

import static cn.nukkit.network.protocol.SetEntityLinkPacket.TYPE_PASSENGER;

/**
 * @author PikyCZ
 */
public class EntityHorse extends EntityAnimal implements EntityWalkable, EntityVariant, EntityMarkVariant, EntityRideable, EntityOwnable, InventoryHolder, EntityAgeable {

    public static final int NETWORK_ID = 23;
    private static final int[] VARIANTS = {0, 1, 2, 3, 4, 5, 6};
    private static final int[] MARK_VARIANTS = {0, 1, 2, 3, 4};
    private Map<String, Attribute> attributeMap;
    private HorseInventory horseInventory;
    private final AtomicBoolean jumping = new AtomicBoolean(false);

    public EntityHorse(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.7f;
        }
        return 1.4f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.8f;
        }
        return 1.6f;
    }

    @Override
    public void initEntity() {
        attributeMap = new HashMap<>();
        if (this.namedTag.containsList("Attributes")) {
            for (var nbt : this.namedTag.getList("Attributes", CompoundTag.class).getAll()) {
                attributeMap.put(nbt.getString("Name"), Attribute.fromNBT(nbt));
            }
        } else {
            for (var attribute : randomizeAttributes()) {
                attributeMap.put(attribute.getName(), attribute);
            }
        }
        this.setMaxHealth((int) Math.ceil(attributeMap.get("minecraft:health").getMaxValue()));
        super.initEntity();

        this.horseInventory = new HorseInventory(this);
        ListTag<CompoundTag> inventoryTag;
        if (this.namedTag.containsList("Inventory")) {
            inventoryTag = this.namedTag.getList("Inventory", CompoundTag.class);
            Item item0 = NBTIO.getItemHelper(inventoryTag.get(0));
            if (item0.isNull()) {
                this.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_SADDLED, false);
                this.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_WASD_CONTROLLED, false);
                this.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_CAN_POWER_JUMP, false);
            } else {
                this.getInventory().setItem(0, item0);
            }
            this.getInventory().setItem(1, NBTIO.getItemHelper(inventoryTag.get(1)));
        } else {
            this.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_SADDLED, false);
            this.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_WASD_CONTROLLED, false);
            this.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_CAN_POWER_JUMP, false);
        }
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_GRAVITY, true);
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_CAN_CLIMB, true);
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_HAS_COLLISION, true);

        if (!hasVariant()) {
            this.setVariant(randomVariant());
        }
        if (!hasMarkVariant()) {
            this.setMarkVariant(randomMarkVariant());
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        ListTag<CompoundTag> inventoryTag = new ListTag<>();
        if (this.getInventory() != null) {
            Item item0 = this.getInventory().getItem(0);
            Item item1 = this.getInventory().getItem(1);
            inventoryTag.add(NBTIO.putItemHelper(item0, 0));
            inventoryTag.add(NBTIO.putItemHelper(item1, 1));
        }
        this.namedTag.putList("Inventory", inventoryTag);

        ListTag<CompoundTag> compoundTagListTag = new ListTag<>();
        for (var attribute : this.attributeMap.values()) {
            compoundTagListTag.add(Attribute.toNBT(attribute));
        }
        this.namedTag.putList("Attributes", compoundTagListTag);
    }

    @Override
    public void setHealth(float health) {
        super.setHealth(health);
        if (this.isAlive()) {
            Attribute attr = this.attributeMap.get("minecraft:health")
                    .setDefaultValue(this.getMaxHealth())
                    .setMaxValue(this.getMaxHealth())
                    .setValue(health > 0 ? (health < getMaxHealth() ? health : getMaxHealth()) : 0);
            UpdateAttributesPacket pk = new UpdateAttributesPacket();
            pk.entries = new Attribute[]{attr};
            pk.entityId = this.id;
            Server.broadcastPacket(this.getViewers().values().toArray(Player.EMPTY_ARRAY), pk);
        }
    }

    @Override
    public void setMaxHealth(int maxHealth) {
        super.setMaxHealth(maxHealth);
        Attribute attr = this.attributeMap.get("minecraft:health")
                .setMaxValue(maxHealth)
                .setDefaultValue(maxHealth)
                .setValue(health > 0 ? (health < getMaxHealth() ? health : getMaxHealth()) : 0);
        if (this.isAlive()) {
            UpdateAttributesPacket pk = new UpdateAttributesPacket();
            pk.entries = new Attribute[]{attr};
            pk.entityId = this.id;
            Server.broadcastPacket(this.getViewers().values().toArray(Player.EMPTY_ARRAY), pk);
        }
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.LEATHER), getHorseArmor(), getSaddle()};
    }

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return "Horse";
    }

    @Override
    public int[] getAllVariant() {
        return VARIANTS;
    }

    @Override
    public int[] getAllMarkVariant() {
        return MARK_VARIANTS;
    }

    public AtomicBoolean getJumping() {
        return jumping;
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(
                        //todo breed
                        new Behavior(
                                new AnimalGrowExecutor(),
                                //todo：Growth rate
                                all(
                                        new PassByTimeEvaluator(CoreMemoryTypes.ENTITY_SPAWN_TIME, 20 * 60 * 20, Integer.MAX_VALUE),
                                        entity -> entity instanceof EntityAnimal animal && animal.isBaby()
                                )
                                , 1, 1, 1200
                        )
                ),
                Set.of(
                        new Behavior(new TameHorseExecutor(0.4f, 12, 40, true, 100, true, 10, 35), all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.RIDER_NAME),
                                e -> !this.hasOwner(false)
                        ), 4, 1),
                        new Behavior(new LookAtTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 100), new ProbabilityEvaluator(4, 10), 1, 1, 100),
                        new Behavior(new FlatRandomRoamExecutor(0.2f, 12, 100, false, -1, true, 10), (entity -> true), 1, 1)
                ),
                Set.of(new NearestFeedingPlayerSensor(8, 0), new NearestPlayerSensor(8, 0, 20)),
                Set.of(new WalkController(), new LookController(true, true), new FluctuateController()),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    @Override
    public void asyncPrepare(int currentTick) {
        if (this.getRider() == null || this.getOwner() == null || this.getSaddle().isNull()) {
            isActive = level.isHighLightChunk(getChunkX(), getChunkZ());
            if (!this.isImmobile()) {
                var behaviorGroup = getBehaviorGroup();
                if (behaviorGroup == null) return;
                behaviorGroup.collectSensorData(this);
                behaviorGroup.evaluateCoreBehaviors(this);
                behaviorGroup.evaluateBehaviors(this);
                behaviorGroup.tickRunningCoreBehaviors(this);
                behaviorGroup.tickRunningBehaviors(this);
                behaviorGroup.updateRoute(this);
                behaviorGroup.applyController(this);
                if (EntityAI.checkDebugOption(EntityAI.DebugOption.BEHAVIOR)) behaviorGroup.debugTick(this);
            }
            this.needsRecalcMovement = this.level.tickRateOptDelay == 1 || ((currentTick + tickSpread) & (this.level.tickRateOptDelay - 1)) == 0;
            this.calculateOffsetBoundingBox();
            if (!this.isImmobile()) {
                handleGravity();
                if (needsRecalcMovement) {
                    handleCollideMovement(currentTick);
                }
                addTmpMoveMotionXZ(previousCollideMotion);
                handleFloatingMovement();
                handleGroundFrictionMovement();
                handlePassableBlockFrictionMovement();
            }
        }
    }

    @Override
    public void fall(float fallDistance) {
        if (this.hasEffect(Effect.SLOW_FALLING)) {
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
            int jumpBoost = this.hasEffect(Effect.JUMP_BOOST) ? (getEffect(Effect.JUMP_BOOST).getAmplifier() + 1) : 0;
            float damage = fallDistance - 3 - jumpBoost - getClientMaxJumpHeight();

            if (damage > 0) {
                this.attack(new EntityDamageEvent(this, EntityDamageEvent.DamageCause.FALL, damage));
            }
        }

        down.onEntityFallOn(this, fallDistance);

        if (fallDistance > 0.75) {//todo: moving these into their own classes (method "onEntityFallOn()")
            if (down.getId() == Block.FARMLAND) {
                if (onPhysicalInteraction(down, false)) {
                    return;
                }
                var farmEvent = new FarmLandDecayEvent(this, down);
                this.server.getPluginManager().callEvent(farmEvent);
                if (farmEvent.isCancelled()) return;
                this.level.setBlock(down, new BlockDirt(), false, true);
                return;
            }

            Block floor = this.level.getTickCachedBlock(floorLocation);
            if (floor instanceof BlockTurtleEgg) {
                if (onPhysicalInteraction(floor, ThreadLocalRandom.current().nextInt(10) >= 3)) {
                    return;
                }
                this.level.useBreakOn(this, null, null, true);
            }
        }
    }

    @Override
    public boolean onUpdate(int currentTick) {
        boolean b = super.onUpdate(currentTick);
        if (currentTick % 2 == 0) {
            if (this.jumping.get() && this.isOnGround()) {
                this.setDataFlag(EntityHorse.DATA_FLAGS, Entity.DATA_FLAG_REARING, false);
                this.jumping.set(false);
            }
        }
        return b;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return super.canCollideWith(entity) && entity != this.getRider();
    }

    public void onPlayerInput(Location clientLoc) {
        if (this.getRider() == null || this.getOwner() == null || this.getSaddle().isNull()) return;
        //每次输入乘骑玩家位置之前都要确保motion为0,避免onGround不更新
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
        this.setMoveTarget(null);
        this.setLookTarget(null);
        this.move(clientLoc.x - this.x, (clientLoc.y - 0.5) - this.y, clientLoc.z - this.z);
        this.yaw = clientLoc.yaw;
        this.headYaw = clientLoc.headYaw;
        broadcastMovement();
    }

    @Nullable
    @Override
    public String getOwnerName() {
        String ownerName = EntityOwnable.super.getOwnerName();
        if (ownerName == null) {
            this.setDataProperty(new ByteEntityData(Entity.DATA_CONTAINER_TYPE, 0));
            this.setDataProperty(new IntEntityData(Entity.DATA_CONTAINER_BASE_SIZE, 0));
        } else {
            //添加两个metadata这个才能交互物品栏
            this.setDataProperty(new ByteEntityData(Entity.DATA_CONTAINER_TYPE, 12));
            this.setDataProperty(new IntEntityData(Entity.DATA_CONTAINER_BASE_SIZE, 2));
        }
        return ownerName;
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        mountEntity(player);
        return true;
    }

    @Override
    public boolean mountEntity(Entity entity) {
        this.getMemoryStorage().put(CoreMemoryTypes.RIDER_NAME, entity.getName());
        super.mountEntity(entity, SetEntityLinkPacket.TYPE_RIDE);
        return true;
    }

    @Override
    public boolean dismountEntity(Entity entity) {
        this.getMemoryStorage().clear(CoreMemoryTypes.RIDER_NAME);
        return super.dismountEntity(entity);
    }

    @Override
    public Vector3f getMountedOffset(Entity entity) {
        return new Vector3f(0, 2.42f, 0);
    }

    @Override
    public HorseInventory getInventory() {
        return horseInventory;
    }

    @Nullable
    public Entity getRider() {
        String name = getMemoryStorage().get(CoreMemoryTypes.RIDER_NAME);
        if (name != null) {
            return Server.getInstance().getPlayerExact(name);
        } else return null;//todo other entity
    }

    public float getClientMaxJumpHeight() {
        return attributeMap.get("minecraft:horse.jump_strength").getValue();
    }

    /**
     * @see HorseInventory#setSaddle(Item)
     */
    public void setSaddle(Item item) {
        this.getInventory().setSaddle(item);
    }

    /**
     * @see HorseInventory#setHorseArmor(Item)
     */
    public void setHorseArmor(Item item) {
        this.getInventory().setHorseArmor(item);
    }

    /**
     * @see HorseInventory#getSaddle()
     */
    public Item getSaddle() {
        return this.getInventory().getSaddle();
    }

    /**
     * @see HorseInventory#getHorseArmor()
     */
    public Item getHorseArmor() {
        return this.getInventory().getHorseArmor();
    }

    /**
     * 播放驯服失败的动画
     * <p>
     * Play an animation of a failed tamer
     */
    public void playTameFailAnimation() {
        this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_MAD, -1, "minecraft:horse", this.isBaby(), false);
        this.setDataFlag(EntityHorse.DATA_FLAGS, EntityHorse.DATA_FLAG_REARING);
    }

    /**
     * 停止播放驯服失败的动画
     * <p>
     * Stop playing the animation that failed to tame
     */
    public void stopTameFailAnimation() {
        this.setDataFlag(EntityHorse.DATA_FLAGS, EntityHorse.DATA_FLAG_REARING, false);
    }

    @Override
    public void spawnTo(Player player) {
        super.spawnTo(player);
        //确保第一次生成时血量更新，单纯在AddEntityPacket中发送属性似乎还不够，还需要UpdateAttributesPacket
        Attribute attr = this.attributeMap.get("minecraft:health")
                .setDefaultValue(this.getMaxHealth())
                .setMaxValue(this.getMaxHealth())
                .setValue(health > 0 ? (health < getMaxHealth() ? health : getMaxHealth()) : 0);
        UpdateAttributesPacket pk = new UpdateAttributesPacket();
        pk.entries = new Attribute[]{attr};
        pk.entityId = this.id;
        player.dataPacket(pk);
    }

    protected float generateRandomMaxHealth() {
        return 15.0F + (float) Utils.rand(0, 8) + (float) Utils.rand(0, 9);
    }

    protected float generateRandomJumpStrength() {
        return (float) (0.4F + Utils.random.nextDouble() * 0.2D + Utils.random.nextDouble() * 0.2D + Utils.random.nextDouble() * 0.2D);
    }

    protected float generateRandomSpeed() {
        return (float) ((0.45F + Utils.random.nextDouble() * 0.3D + Utils.random.nextDouble() * 0.3D + Utils.random.nextDouble() * 0.3D) * 0.25D);
    }

    protected Attribute[] randomizeAttributes() {
        Attribute[] attributes = new Attribute[3];
        attributes[0] = Attribute.getAttribute(Attribute.MOVEMENT_SPEED).setValue(generateRandomSpeed());
        float maxHealth = generateRandomMaxHealth();
        attributes[1] = Attribute.getAttribute(Attribute.MAX_HEALTH).setMinValue(0).setMaxValue(maxHealth).setDefaultValue(maxHealth).setValue(maxHealth);
        attributes[2] = Attribute.getAttribute(Attribute.HORSE_JUMP_STRENGTH).setValue(generateRandomJumpStrength());
        ListTag<CompoundTag> compoundTagListTag = new ListTag<>();
        compoundTagListTag.add(Attribute.toNBT(attributes[0])).add(Attribute.toNBT(attributes[1])).add(Attribute.toNBT(attributes[2]));
        this.namedTag.putList("Attributes", compoundTagListTag);
        return attributes;
    }

    @Override
    protected DataPacket createAddEntityPacket() {
        AddEntityPacket addEntity = new AddEntityPacket();
        addEntity.type = this.getNetworkId();
        addEntity.entityUniqueId = this.getId();
        if (this instanceof CustomEntity customEntity) {
            addEntity.id = customEntity.getDefinition().getStringId();
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
        addEntity.metadata = this.dataProperties;
        addEntity.attributes = this.attributeMap.values().toArray(Attribute.EMPTY_ARRAY);
        addEntity.links = new EntityLink[this.passengers.size()];
        for (int i = 0; i < addEntity.links.length; i++) {
            addEntity.links[i] = new EntityLink(this.getId(), this.passengers.get(i).getId(), i == 0 ? EntityLink.TYPE_RIDER : TYPE_PASSENGER, false, false);
        }

        return addEntity;
    }
}
