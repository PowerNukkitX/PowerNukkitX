package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBedrock;
import cn.nukkit.block.BlockSoulSand;
import cn.nukkit.block.BlockWitherSkeletonSkull;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityFlyable;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LiftController;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.SpaceMoveController;
import cn.nukkit.entity.ai.evaluator.DistanceEvaluator;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.PassByTimeEvaluator;
import cn.nukkit.entity.ai.evaluator.RandomSoundEvaluator;
import cn.nukkit.entity.ai.executor.MoveToTargetExecutor;
import cn.nukkit.entity.ai.executor.PlaySoundExecutor;
import cn.nukkit.entity.ai.executor.SpaceRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.WitherDashExecutor;
import cn.nukkit.entity.ai.executor.WitherShootExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleSpaceAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.FlyingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.ai.sensor.NearestTargetEntitySensor;
import cn.nukkit.entity.data.EntityDataTypes;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityExplosionPrimeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.*;
import cn.nukkit.network.protocol.types.LevelSoundEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class EntityWither extends EntityBoss implements EntityFlyable, EntitySmite {

    @Override
    @NotNull public String getIdentifier() {
        return WITHER;
    }

    public EntityWither(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(new PlaySoundExecutor(Sound.MOB_WITHER_AMBIENT), all(
                                new RandomSoundEvaluator(),
                                entity -> age >= 200
                        ), 11, 1),
                        new Behavior(new WitherDashExecutor(CoreMemoryTypes.ATTACK_TARGET, 1f, true, 64, 0), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET),
                                any(
                                        new PassByTimeEvaluator(CoreMemoryTypes.LAST_ATTACK_DASH, 400),
                                        entity -> getDataFlag(EntityFlag.CAN_DASH)
                                ),
                                new DistanceEvaluator(CoreMemoryTypes.ATTACK_TARGET, 65, 3),
                                entity -> getHealth() <= getMaxHealth()/2f,
                                entity -> age >= 200
                        ), 10, 1),
                        new Behavior(new WitherDashExecutor(CoreMemoryTypes.NEAREST_PLAYER, 1f, true, 64, 0), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER),
                                any(
                                        new PassByTimeEvaluator(CoreMemoryTypes.LAST_ATTACK_DASH, 400),
                                        entity -> getDataFlag(EntityFlag.CAN_DASH)
                                ),
                                new DistanceEvaluator(CoreMemoryTypes.NEAREST_PLAYER, 65, 3),
                                entity -> getHealth() <= getMaxHealth()/2f,
                                entity -> age >= 200
                        ), 9, 1),
                        new Behavior(new WitherDashExecutor(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, 1f, true, 64, 0), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET),
                                any(
                                        new PassByTimeEvaluator(CoreMemoryTypes.LAST_ATTACK_DASH, 400),
                                        entity -> getDataFlag(EntityFlag.CAN_DASH)
                                ),
                                new DistanceEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, 65, 3),
                                entity -> getHealth() <= getMaxHealth()/2f,
                                entity -> age >= 200
                        ), 8, 1),
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.7f, true, 64, 16), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET),
                                new DistanceEvaluator(CoreMemoryTypes.ATTACK_TARGET, 65, 17),
                                entity -> age >= 200                        ), 7, 1),
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.7f, true, 64, 16), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER),
                                new DistanceEvaluator(CoreMemoryTypes.NEAREST_PLAYER, 65, 17),
                                entity -> age >= 200
                        ), 6, 1),
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, 0.7f, true, 64, 16), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET),
                                new DistanceEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, 65, 17),
                                entity -> age >= 200
                        ), 5, 1),
                        new Behavior(new WitherShootExecutor(CoreMemoryTypes.ATTACK_TARGET), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET),
                                new PassByTimeEvaluator(CoreMemoryTypes.LAST_ATTACK_TIME, 100),
                                entity -> age >= 200
                        ), 4, 1),
                        new Behavior(new WitherShootExecutor(CoreMemoryTypes.NEAREST_PLAYER), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER),
                                new PassByTimeEvaluator(CoreMemoryTypes.LAST_ATTACK_TIME, 100),
                                entity -> age >= 200
                        ), 3, 1),
                        new Behavior(new WitherShootExecutor(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET),
                                new PassByTimeEvaluator(CoreMemoryTypes.LAST_ATTACK_TIME, 100),
                                entity -> age >= 200
                        ), 2, 1),
                        new Behavior(new SpaceRandomRoamExecutor(0.15f, 12, 100, 20, false, -1, true, 10), entity -> age >= 200, 1, 1)
                ),
                Set.of(new NearestPlayerSensor(64, 0, 20),
                        new NearestTargetEntitySensor<>(0, 64, 20,
                                List.of(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), this::attackTarget)),
                Set.of(new SpaceMoveController(), new LookController(true, true), new LiftController()),
                new SimpleSpaceAStarRouteFinder(new FlyingPosEvaluator(), this),
                this
        );
    }

    private boolean exploded = false;
    private int deathTicks = -1;

    @Override
    public void kill() {
        if(deathTicks == -1) {
            deathTicks = 190;
            getLevel().addLevelSoundEvent(this, LevelSoundEvent.DEATH, -1, Entity.WITHER, false, false);
            EntityEventPacket packet = new EntityEventPacket();
            packet.event = EntityEventPacket.DEATH_ANIMATION;
            packet.eid = getId();
            Server.broadcastPacket(getViewers().values(), packet);
            setImmobile(true);
        } else {
            if (!this.exploded && this.lastDamageCause != null && EntityDamageEvent.DamageCause.SUICIDE != this.lastDamageCause.getCause()) {
                this.exploded = true;
                this.explode();
            }
            super.kill();
        }
    }
    
    @Override
    public void setHealth(float health) {
        float healthBefore = getHealth();
        float halfHealth = getMaxHealth()/2f;
        super.setHealth(health);
        if(health <= halfHealth && healthBefore > halfHealth) {
            if(!isInvulnerable()) {
                this.explode();
                setInvulnerable(176);
            }
        }
    }

    public void setInvulnerable(int ticks) {
        this.setDataProperty(EntityDataTypes.WITHER_INVULNERABLE_TICKS, ticks);
    }

    public boolean isInvulnerable() {
        return getDataProperty(EntityDataTypes.WITHER_INVULNERABLE_TICKS) > 0;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if(age < 200 || deathTicks != -1) return false;
        if(source.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            return false;
        }
        return super.attack(source);
    }

    @Override
    public float getWidth() {
        return 1.0f;
    }

    @Override
    public float getHeight() {
        return 3.0f;
    }

    @Override
    protected DataPacket createAddEntityPacket() {
        AddEntityPacket addEntity = new AddEntityPacket();
        addEntity.type = getNetworkId();
        addEntity.entityUniqueId = this.getId();
        addEntity.entityRuntimeId = this.getId();
        addEntity.yaw = (float) this.yaw;
        addEntity.headYaw = (float) this.yaw;
        addEntity.pitch = (float) this.pitch;
        addEntity.x = (float) this.x;
        addEntity.y = (float) this.y;
        addEntity.z = (float) this.z;
        addEntity.speedX = (float) this.motionX;
        addEntity.speedY = (float) this.motionY;
        addEntity.speedZ = (float) this.motionZ;
        addEntity.entityData = this.entityDataMap;
        addEntity.attributes = new Attribute[]{Attribute.getAttribute(Attribute.MAX_HEALTH).setMaxValue(getMaxDiffHealth()).setValue(getMaxDiffHealth())};
        return addEntity;
    }

    private int getMaxDiffHealth() {
        return switch (this.getServer().getDifficulty()) {
            case 2:
                yield 450;
            case 3:
                yield 600;
            default:
                yield 300;
        };
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(getMaxDiffHealth());
        super.initEntity();
        this.blockBreakSound = Sound.MOB_WITHER_BREAK_BLOCK;
        this.setInvulnerable(200);
        this.setHealth(1);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if(!closed) {
            if(deathTicks != -1) {
                if(deathTicks <= 0) {
                    kill();
                } else deathTicks--;
            }
            if(isInvulnerable()) {
                this.setDataProperty(EntityDataTypes.WITHER_INVULNERABLE_TICKS, getDataProperty(WITHER_INVULNERABLE_TICKS)-1);
            }
            if (this.age == 200) {
                this.explode();
                setHealth(getMaxHealth());
                getLevel().addSound(this, Sound.MOB_WITHER_SPAWN);
            } else if(age < 200) {
                heal(getMaxHealth()/200f);
            }
        }
        return super.onUpdate(currentTick);
    }

    @Override
    public boolean attackTarget(Entity entity) {
        if(entity instanceof EntityWither) return false;
        return entity instanceof EntityIntelligent;
    }

    @Override
    public void addBossbar(Player player) {
        BossEventPacket pkBoss = new BossEventPacket();
        pkBoss.bossEid = this.id;
        pkBoss.type = BossEventPacket.TYPE_SHOW;
        pkBoss.title = this.getName();
        pkBoss.color = 6;
        pkBoss.darkenSky = 1;
        pkBoss.healthPercent = 0;
        player.dataPacket(pkBoss);
    }

    @Override
    public String getOriginalName() {
        return "Wither";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("wither", "skeleton", "monster", "undead", "mob");
    }

    @Override
    public boolean isUndead() {
        return true;
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }

    @Override
    public boolean isBoss() {
        return true;
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.NETHER_STAR)};
    }

    @Override
    public Integer getExperienceDrops() {
        return 50;
    }

    @Override
    public boolean move(double dx, double dy, double dz) {
        if((age%40==0 || getDataFlag(EntityFlag.CAN_DASH) && age > 200)) {
            Block[] blocks = level.getCollisionBlocks(getBoundingBox().grow(1, 1, 1));
            if(blocks.length > 0) {
                if(blockBreakSound != null) level.addSound(this, blockBreakSound);
                for (Block collisionBlock : blocks) {
                    if(!(collisionBlock instanceof BlockBedrock)) {
                        level.breakBlock(collisionBlock);
                    }
                }
            }
        }
        return super.move(dx, dy, dz);
    }

    public static boolean checkAndSpawnWither(Block block) {
        Block check = block;
        BlockFace skullFace = null;
        if(block.getLevel().getGameRules().getBoolean(GameRule.DO_MOB_SPAWNING)) {
            for(BlockFace face : Set.of(BlockFace.UP, BlockFace.NORTH, BlockFace.EAST)) {
                boolean[] skulls = new boolean[5];
                ints:
                for(int i = -2; i<=2; i++) {
                    if(block.getSide(face, i) instanceof BlockWitherSkeletonSkull skull) {
                        skulls[i+2] = true;
                    } else skulls[i+2] = false;
                }
                int inrow = 0;
                for(int i = 0; i < skulls.length; i++) {
                    if(skulls[i]) {
                        inrow++;
                        if(inrow == 2) check = block.getSide(face, i-2);
                    } else if(inrow < 3) {
                        inrow = 0;
                    }
                }
                if(inrow >= 3) {
                    skullFace = face;
                }
            }
            if(skullFace == null) return false;
            if(check instanceof BlockWitherSkeletonSkull) {
                faces:
                for(BlockFace blockFace : BlockFace.values()) {
                    for(int i = 1; i<=2; i++) {
                        if(!(check.getSide(blockFace, i) instanceof BlockSoulSand)) {
                            continue faces;
                        }
                    }
                    faces1:
                    for(BlockFace face : Set.of(BlockFace.UP, BlockFace.NORTH, BlockFace.EAST)) {
                        for(int i = -1; i<=1; i++) {
                            if(!(check.getSide(blockFace).getSide(face, i) instanceof BlockSoulSand)) {
                                continue faces1;
                            }
                        }

                        for(int i = 0; i<=2; i++) {
                            Block location = check.getSide(blockFace, i);
                            location.level.breakBlock(location);
                        }
                        for(int i = -1; i<=1; i++) {
                            Block location = check.getSide(blockFace).getSide(face, i);
                            location.level.breakBlock(location);
                            location.level.breakBlock(location.getSide(blockFace.getOpposite()));

                        }
                        Block pos = check.getSide(blockFace, 2);
                        CompoundTag nbt = new CompoundTag()
                                .putList("Pos", new ListTag<DoubleTag>()
                                        .add(new DoubleTag(pos.x + 0.5))
                                        .add(new DoubleTag(pos.y))
                                        .add(new DoubleTag(pos.z + 0.5)))
                                .putList("Motion", new ListTag<DoubleTag>()
                                        .add(new DoubleTag(0))
                                        .add(new DoubleTag(0))
                                        .add(new DoubleTag(0)))
                                .putList("Rotation", new ListTag<FloatTag>()
                                        .add(new FloatTag(0f))
                                        .add(new FloatTag(0f)));

                        Entity wither = Entity.createEntity(EntityID.WITHER, check.level.getChunk(check.getChunkX(), check.getChunkZ()), nbt);
                        wither.spawnToAll();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void explode() {
        EntityExplosionPrimeEvent ev = new EntityExplosionPrimeEvent(this, 7);
        this.server.getPluginManager().callEvent(ev);
        if (!ev.isCancelled()) {
            Explosion explosion = new Explosion(this, (float) ev.getForce(), this);
            if (ev.isBlockBreaking() && this.level.getGameRules().getBoolean(GameRule.MOB_GRIEFING)) {
                explosion.explodeA();
            }
            explosion.explodeB();
        }
    }
}
