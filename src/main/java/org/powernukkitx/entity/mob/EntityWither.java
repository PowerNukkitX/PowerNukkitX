package org.powernukkitx.entity.mob;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockBedrock;
import org.powernukkitx.block.BlockSoulSand;
import org.powernukkitx.block.BlockWitherSkeletonSkull;
import org.powernukkitx.entity.Attribute;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityFlyable;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.EntitySmite;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.LiftController;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.SpaceMoveController;
import org.powernukkitx.entity.ai.evaluator.DistanceEvaluator;
import org.powernukkitx.entity.ai.evaluator.EntityCheckEvaluator;
import org.powernukkitx.entity.ai.evaluator.PassByTimeEvaluator;
import org.powernukkitx.entity.ai.evaluator.RandomSoundEvaluator;
import org.powernukkitx.entity.ai.executor.MoveToTargetExecutor;
import org.powernukkitx.entity.ai.executor.PlaySoundExecutor;
import org.powernukkitx.entity.ai.executor.SpaceRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.WitherDashExecutor;
import org.powernukkitx.entity.ai.executor.WitherShootExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleSpaceAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.FlyingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.ai.sensor.NearestTargetEntitySensor;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.event.entity.EntityExplosionPrimeEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Explosion;
import org.powernukkitx.level.GameRule;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.DoubleTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.ListTag;

import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorEvent;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.cloudburstmc.protocol.bedrock.data.payload.boss.BossBarColor;
import org.cloudburstmc.protocol.bedrock.data.payload.boss.BossBarOverlay;
import org.cloudburstmc.protocol.bedrock.data.payload.boss.BossEventUpdateType;
import org.cloudburstmc.protocol.bedrock.packet.ActorEventPacket;
import org.cloudburstmc.protocol.bedrock.packet.AddActorPacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.BossEventPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class EntityWither extends EntityBoss implements EntityFlyable, EntitySmite {

    @Override
    @NotNull
    public String getIdentifier() {
        return WITHER;
    }

    public EntityWither(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return BehaviorGroup.builder(this)
                .behaviors(
                        new Behavior(new PlaySoundExecutor(Sound.MOB_WITHER_AMBIENT), all(
                                new RandomSoundEvaluator(),
                                entity -> age >= 200
                        ), 11, 1),
                        new Behavior(new WitherDashExecutor(CoreMemoryTypes.ATTACK_TARGET, 1f, true, 64, 0), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET),
                                any(
                                        new PassByTimeEvaluator(CoreMemoryTypes.LAST_ATTACK_DASH, 400),
                                        entity -> getDataFlag(ActorFlags.CAN_DASH)
                                ),
                                new DistanceEvaluator(CoreMemoryTypes.ATTACK_TARGET, 65, 3),
                                entity -> getHealthCurrent() <= getHealthMax() / 2f,
                                entity -> age >= 200
                        ), 10, 1),
                        new Behavior(new WitherDashExecutor(CoreMemoryTypes.NEAREST_PLAYER, 1f, true, 64, 0), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER),
                                any(
                                        new PassByTimeEvaluator(CoreMemoryTypes.LAST_ATTACK_DASH, 400),
                                        entity -> getDataFlag(ActorFlags.CAN_DASH)
                                ),
                                new DistanceEvaluator(CoreMemoryTypes.NEAREST_PLAYER, 65, 3),
                                entity -> getHealthCurrent() <= getHealthMax() / 2f,
                                entity -> age >= 200
                        ), 9, 1),
                        new Behavior(new WitherDashExecutor(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, 1f, true, 64, 0), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET),
                                any(
                                        new PassByTimeEvaluator(CoreMemoryTypes.LAST_ATTACK_DASH, 400),
                                        entity -> getDataFlag(ActorFlags.CAN_DASH)
                                ),
                                new DistanceEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, 65, 3),
                                entity -> getHealthCurrent() <= getHealthMax() / 2f,
                                entity -> age >= 200
                        ), 8, 1),
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.7f, true, 64, 16), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET),
                                new DistanceEvaluator(CoreMemoryTypes.ATTACK_TARGET, 65, 17),
                                entity -> age >= 200), 7, 1),
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
                )
                .sensors(new NearestPlayerSensor(64, 0, 20),
                        new NearestTargetEntitySensor<>(0, 64, 20,
                                List.of(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), this::attackTarget))
                .controllers(new SpaceMoveController(), new LookController(true, true), new LiftController())
                .routeFinder(new SimpleSpaceAStarRouteFinder(new FlyingPosEvaluator(), this))
                .build();
    }

    private boolean exploded = false;
    private int deathTicks = -1;

    @Override
    public void kill() {
        if (deathTicks == -1) {
            deathTicks = 190;
            getLevel().addLevelSoundEvent(this, SoundEvent.DEATH, -1, Entity.WITHER, false, false);
            final ActorEventPacket packet = new ActorEventPacket();
            packet.setTargetRuntimeID(this.getId());
            packet.setType(ActorEvent.DEATH);
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
    public void setHealthCurrent(float health) {
        float healthBefore = getHealthCurrent();
        float halfHealth = getHealthMax() / 2f;
        super.setHealthCurrent(health);
        if (health <= halfHealth && healthBefore > halfHealth) {
            if (!isInvulnerable()) {
                this.explode();
                setInvulnerable(176);
            }
        }
    }

    public void setInvulnerable(int ticks) {
        this.setDataProperty(ActorDataTypes.INV, ticks);
    }

    public boolean isInvulnerable() {
        return getDataProperty(ActorDataTypes.INV) > 0;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (age < 200 || deathTicks != -1) return false;
        if (source.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
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
    public HealthComponent getComponentHealth() {
        int diffHealth = getMaxDiffHealth();
        return HealthComponent.value(diffHealth);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.25f);
    }

    @Override
    protected BedrockPacket createAddEntityPacket() {
        final AddActorPacket packet = new AddActorPacket();
        packet.getAttributesList().add(
                Attribute.getAttribute(Attribute.HEALTH).setMaxValue(getMaxDiffHealth()).setValue(getMaxDiffHealth()).toNetwork()
        );
        packet.setActorData(this.getActorDataMap());
        packet.setTargetActorID(this.getId());
        packet.setTargetRuntimeID(this.getId());
        packet.setActorType("minecraft:wither");
        packet.setPosition(org.cloudburstmc.math.vector.Vector3f.from(this.x, this.y, this.z));
        packet.setVelocity(org.cloudburstmc.math.vector.Vector3f.from(this.motionX, this.motionY, this.motionZ));
        packet.setRotation(org.cloudburstmc.math.vector.Vector2f.from(this.pitch, this.yaw));
        return packet;
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
        super.initEntity();
        this.blockBreakSound = Sound.MOB_WITHER_BREAK_BLOCK;
        this.setInvulnerable(200);
        this.setHealthCurrent(1);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (!closed) {
            if (deathTicks != -1) {
                if (deathTicks <= 0) {
                    kill();
                } else deathTicks--;
            }
            if (isInvulnerable()) {
                this.setDataProperty(ActorDataTypes.INV, getDataProperty(ActorDataTypes.INV) - 1);
            }
            if (this.age == 200) {
                this.explode();
                setHealthCurrent(getHealthMax());
                getLevel().addSound(this, Sound.MOB_WITHER_SPAWN);
            } else if (age < 200) {
                heal(getHealthMax() / 200f);
            }
        }
        return super.onUpdate(currentTick);
    }

    @Override
    public boolean attackTarget(Entity entity) {
        if (entity instanceof EntityWither) return false;
        return entity instanceof EntityIntelligent;
    }

    @Override
    public void addBossbar(Player player) {
        final BossEventPacket bossEventPacket = new BossEventPacket();
        bossEventPacket.setTargetActorID(this.getId());
        bossEventPacket.setEventType(BossEventUpdateType.ADD);
        bossEventPacket.setName(this.getName());
        bossEventPacket.setHealthPercent(0f);
        bossEventPacket.setOverlay(BossBarOverlay.PROGRESS);
        bossEventPacket.setColor(BossBarColor.PURPLE);
        bossEventPacket.setPlayerID(player.getId());
        bossEventPacket.setDarkenScreen(1);
        bossEventPacket.setColor(BossBarColor.REBECCA_PURPLE);
        player.sendPacket(bossEventPacket);
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
    public Item[] getDrops(@NotNull Item weapon) {
        return new Item[]{Item.get(Item.NETHER_STAR)};
    }

    @Override
    public Integer getExperienceDrops() {
        return 50;
    }

    @Override
    public boolean move(double dx, double dy, double dz) {
        if ((age % 40 == 0 || getDataFlag(ActorFlags.CAN_DASH) && age > 200)) {
            Block[] blocks = level.getCollisionBlocks(getBoundingBox().grow(1, 1, 1));
            if (blocks.length > 0) {
                if (blockBreakSound != null) level.addSound(this, blockBreakSound);
                for (Block collisionBlock : blocks) {
                    if (!(collisionBlock instanceof BlockBedrock)) {
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
        if (block.getLevel().getGameRules().getBoolean(GameRule.DO_MOB_SPAWNING)) {
            for (BlockFace face : Set.of(BlockFace.UP, BlockFace.NORTH, BlockFace.EAST)) {
                boolean[] skulls = new boolean[5];
                for (int i = -2; i <= 2; i++) {
                    skulls[i + 2] = block.getSide(face, i) instanceof BlockWitherSkeletonSkull;
                }
                int inrow = 0;
                for (int i = 0; i < skulls.length; i++) {
                    if (skulls[i]) {
                        inrow++;
                        if (inrow == 2) check = block.getSide(face, i - 2);
                    } else if (inrow < 3) {
                        inrow = 0;
                    }
                }
                if (inrow >= 3) {
                    skullFace = face;
                }
            }
            if (skullFace == null) return false;
            if (check instanceof BlockWitherSkeletonSkull) {
                faces:
                for (BlockFace blockFace : BlockFace.values()) {
                    for (int i = 1; i <= 2; i++) {
                        if (!(check.getSide(blockFace, i) instanceof BlockSoulSand)) {
                            continue faces;
                        }
                    }
                    faces1:
                    for (BlockFace face : Set.of(BlockFace.UP, BlockFace.NORTH, BlockFace.EAST)) {
                        for (int i = -1; i <= 1; i++) {
                            if (!(check.getSide(blockFace).getSide(face, i) instanceof BlockSoulSand)) {
                                continue faces1;
                            }
                        }

                        for (int i = 0; i <= 2; i++) {
                            Block location = check.getSide(blockFace, i);
                            location.level.breakBlock(location);
                        }
                        for (int i = -1; i <= 1; i++) {
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
