package org.powernukkitx.entity.mob;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockBedrock;
import org.powernukkitx.block.BlockEndGateway;
import org.powernukkitx.block.BlockTorch;
import org.powernukkitx.block.property.enums.TorchFacingDirection;
import org.powernukkitx.entity.Attribute;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityFlyable;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.IController;
import org.powernukkitx.entity.ai.controller.LiftController;
import org.powernukkitx.entity.ai.controller.SpaceMoveController;
import org.powernukkitx.entity.ai.evaluator.EntityCheckEvaluator;
import org.powernukkitx.entity.ai.evaluator.MemoryCheckEmptyEvaluator;
import org.powernukkitx.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import org.powernukkitx.entity.ai.evaluator.RandomSoundEvaluator;
import org.powernukkitx.entity.ai.executor.PlaySoundExecutor;
import org.powernukkitx.entity.ai.executor.enderdragon.CircleMovementExecutor;
import org.powernukkitx.entity.ai.executor.enderdragon.PerchingExecutor;
import org.powernukkitx.entity.ai.executor.enderdragon.StrafeExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.data.Node;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleSpaceAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.FlyingPosEvaluator;
import org.powernukkitx.entity.ai.route.posevaluator.IPosEvaluator;
import org.powernukkitx.entity.ai.sensor.NearestEntitySensor;
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.entity.components.NameableComponent;
import org.powernukkitx.entity.item.EntityEnderCrystal;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.BVector3;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector2;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.plugin.InternalPlugin;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorEvent;
import org.cloudburstmc.protocol.bedrock.data.payload.boss.BossBarColor;
import org.cloudburstmc.protocol.bedrock.data.payload.boss.BossBarOverlay;
import org.cloudburstmc.protocol.bedrock.data.payload.boss.BossEventUpdateType;
import org.cloudburstmc.protocol.bedrock.packet.ActorEventPacket;
import org.cloudburstmc.protocol.bedrock.packet.AddActorPacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.BossEventPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static org.powernukkitx.block.property.CommonBlockProperties.TORCH_FACING_DIRECTION;

public class EntityEnderDragon extends EntityBoss implements EntityFlyable {

    @Override
    @NotNull
    public String getIdentifier() {
        return ENDER_DRAGON;
    }

    public EntityEnderDragon(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(
                        new Behavior(new PlaySoundExecutor(Sound.MOB_ENDERDRAGON_GROWL), new RandomSoundEvaluator(), 2, 1)
                ),
                Set.of(
                        new Behavior(new PerchingExecutor(), entity -> getMemoryStorage().get(CoreMemoryTypes.FORCE_PERCHING), 5, 1),
                        new Behavior(new StrafeExecutor(), all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_PLAYER),
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER),
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.LAST_ENDER_CRYSTAL_DESTROY)
                        ), 4, 1),
                        new Behavior(new CircleMovementExecutor(CoreMemoryTypes.STAY_NEARBY, 1f, true, 82, 12, 5), all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.STAY_NEARBY),
                                new MemoryCheckEmptyEvaluator(CoreMemoryTypes.NEAREST_SHARED_ENTITY)
                        ), 3, 1),
                        new Behavior(new CircleMovementExecutor(CoreMemoryTypes.STAY_NEARBY, 1f, true, 48, 8, 4), all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.STAY_NEARBY),
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_SHARED_ENTITY)
                        ), 2, 1)
                ),
                Set.of(
                        new NearestPlayerSensor(512, 0, 20),
                        new NearestEntitySensor(EntityEnderCrystal.class, CoreMemoryTypes.NEAREST_SHARED_ENTITY, 192, 0, 10)
                ),
                Set.of(new SpaceMoveController(), new LookController(), new LiftController()),
                new EnderDragonRouteFinder(new EnderDragonPosEvaluator(), this),
                this
        );
    }

    @Override
    protected BedrockPacket createAddEntityPacket() {
        final AddActorPacket pk = new AddActorPacket();
        pk.getAttributesList().add(
                Attribute.getAttribute(Attribute.HEALTH).setMaxValue(200).setValue(200).toNetwork()
        );
        pk.setActorData(this.getActorDataMap());
        pk.setTargetActorID(this.getId());
        pk.setTargetRuntimeID(this.getId());
        pk.setActorType("minecraft:ender_dragon");
        pk.setPosition(org.cloudburstmc.math.vector.Vector3f.from(this.x, this.y, this.z));
        pk.setVelocity(org.cloudburstmc.math.vector.Vector3f.from(this.motionX, this.motionY, this.motionZ));
        pk.setRotation(org.cloudburstmc.math.vector.Vector2f.from(this.pitch, this.yaw));
        return pk;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (deathTicks != -1) return false;
        switch (source.getCause()) {
            case SUFFOCATION,
                 MAGIC -> {
                return false;
            }
        }
        return super.attack(source);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        //Hack -> Ensures that Ender Dragon is always ticked.
        getLevel().getScheduler().scheduleTask(InternalPlugin.INSTANCE, this::scheduleUpdate);
        if (deathTicks != -1) {
            if (deathTicks <= 0) {
                kill();
            } else deathTicks--;
            return true;
        }
        if (currentTick % 2 == 0) {
            if (currentTick % ((toHorizontal().distance(Vector2.ZERO) < 1) ? 10 : 20) == 0) {
                getLevel().addLevelSoundEvent(this, SoundEvent.FLAP, -1, this.getIdentifier(), false, false);
            }
            for (Entity e : this.getLevel().getEntities()) {
                if (e instanceof EntityEnderCrystal) {
                    if (e.distance(this) <= 28) {
                        float health = this.getHealthCurrent();
                        if (health < this.getHealthMax() && health != 0) {
                            this.heal(0.2f);
                        }
                    }
                }
            }
        }
        return super.onUpdate(currentTick);
    }

    private int deathTicks = -1;

    @Override
    public void kill() {
        if (deathTicks == -1) {
            deathTicks = 190;
            getLevel().addLevelSoundEvent(this, SoundEvent.DEATH, -1, getIdentifier(), false, false);
            final ActorEventPacket packet = new ActorEventPacket();
            packet.setTargetRuntimeID(this.getId());
            packet.setType(ActorEvent.DRAGON_START_DEATH_ANIM);
            Server.broadcastPacket(getViewers().values(), packet);
            setImmobile(true);
        } else {
            super.kill();
            close();
            if(this.getLevel().getDimension() == Level.DIMENSION_THE_END) {
                if (!isRevived()) {
                    int y = getLevel().getHighestBlockAt(Vector2.ZERO);
                    getLevel().setBlock(new Vector3(0, y + 1, 0), Block.get(Block.DRAGON_EGG));
                    for (BlockFace face : BlockFace.getHorizontals()) {
                        Block torch = BlockTorch.PROPERTIES.getBlockState(TORCH_FACING_DIRECTION.createValue(TorchFacingDirection.getByTorchDirection(face))).toBlock();
                        getLevel().setBlock(new Vector3(0, y - 1, 0).getSide(face), torch);
                    }
                }

                for (int y = getLevel().getMinHeight(); y < getLevel().getHighestBlockAt(0, 0); y++) {
                    if (getLevel().getBlock(0, y, 0) instanceof BlockBedrock) {
                        for (int i = -2; i <= 2; i++) {
                            for (int j = -1; j <= 1; j++) {
                                if (!(i == 0 && j == 0)) {
                                    getLevel().setBlock(new Vector3(i, y + 1, j), Block.get(Block.END_PORTAL));
                                    getLevel().setBlock(new Vector3(j, y + 1, i), Block.get(Block.END_PORTAL));
                                }
                            }
                        }
                        break;
                    }
                }

                for (int i = 0; i < 20; i++) {
                    Vector3 origin = Vector3.ZERO;
                    double angleIncrement = 360.0 / 20;
                    double angle = Math.toRadians(i * angleIncrement);
                    double particleX = origin.getX() + Math.cos(angle) * 96;
                    double particleZ = origin.getZ() + Math.sin(angle) * 96;
                    Block dest = getLevel().getBlock(new Vector3(particleX, 75, particleZ));
                    if (!(dest instanceof BlockEndGateway)) {
                        Arrays.stream(BlockFace.values()).forEach(face -> getLevel().setBlock(dest.up().getSide(face), Block.get(Block.BEDROCK)));
                        Arrays.stream(BlockFace.values()).forEach(face -> getLevel().setBlock(dest.down().getSide(face), Block.get(Block.BEDROCK)));
                        getLevel().setBlock(dest, Block.get(Block.END_GATEWAY));
                        break;
                    }
                }
            }
        }
    }

    @Override
    public float getWidth() {
        return 13;
    }

    @Override
    public float getHeight() {
        return 4;
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(200);
    }

    @Override
    public NameableComponent getComponentNameable() {
        return DEFAULT_NOT_NAMEABLE;
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.3f);
    }

    @Override
    public void initEntity() {
        this.diffHandDamage = new float[]{6f, 10f, 15f};
        super.initEntity();
        getMemoryStorage().put(CoreMemoryTypes.STAY_NEARBY, new Vector3(0, 84, 0));
        isActive = false;
        noClip = true;
    }

    @Override
    protected boolean applyNameTag(@NotNull Player player, @NotNull Item item) {
        return false;
    }

    @Override
    public String getOriginalName() {
        return "Ender Dragon";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("dragon", "mob");
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
    public void addBossbar(Player player) {
        final BossEventPacket bossEventPacket = new BossEventPacket();
        bossEventPacket.setTargetActorID(this.getId());
        bossEventPacket.setEventType(BossEventUpdateType.ADD);
        bossEventPacket.setName(this.getName());
        bossEventPacket.setHealthPercent(health / getHealthMax());
        bossEventPacket.setOverlay(BossBarOverlay.PROGRESS);
        bossEventPacket.setColor(BossBarColor.PURPLE);
        bossEventPacket.setPlayerID(player.getId());
        player.sendPacket(bossEventPacket);
    }

    @Override
    public Integer getExperienceDrops() {
        return isRevived() ? 500 : 12000;
    }

    private class EnderDragonRouteFinder extends SimpleSpaceAStarRouteFinder {

        public EnderDragonRouteFinder(IPosEvaluator blockEvaluator, EntityIntelligent entity) {
            super(blockEvaluator, entity);
        }

        @Override
        public boolean search() {
            boolean superRes = super.search();
            if (superRes && getMemoryStorage().notEmpty(CoreMemoryTypes.MOVE_TARGET)) {
                this.nodes = new ArrayList<>(Collections.singleton(nodes.getFirst()));
                nodes.add(new Node(getMemoryStorage().get(CoreMemoryTypes.MOVE_TARGET), null, 0, 0));
            }
            return superRes;
        }
    }

    private class EnderDragonPosEvaluator extends FlyingPosEvaluator {
        protected boolean isPassable(EntityIntelligent entity, Vector3 vector3) {
            return true;
        }
    }

    @Override
    public boolean move(double dx, double dy, double dz) {
        boolean superRes = super.move(dx, dy, dz);
        if (superRes) {
            Arrays.stream(getLevel().getCollisionBlocks(getBoundingBox())).filter(this::canBreakBlock).forEach(block -> getLevel().breakBlock(block));
        }
        return superRes;
    }

    public boolean isRevived() {
        if (this.nbt.contains("Revived")) {
            return this.getNbt().getBoolean("Revived");
        } else return false;
    }

    protected class LookController implements IController {
        @Override
        public boolean control(EntityIntelligent entity) {
            Vector3 target = entity.getMemoryStorage().get(CoreMemoryTypes.LOOK_TARGET);
            if (target == null) return false;
            var toPlayerVector = new Vector3(entity.x - target.x, entity.y - target.y, entity.z - target.z).normalize();
            entity.setHeadYaw(BVector3.getYawFromVector(toPlayerVector));
            entity.setYaw(BVector3.getYawFromVector(toPlayerVector));
            entity.setPitch(BVector3.getPitchFromVector(toPlayerVector));
            return true;
        }
    }
}
