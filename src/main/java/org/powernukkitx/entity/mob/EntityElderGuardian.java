package org.powernukkitx.entity.mob;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntitySwimmable;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.DiveController;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.SpaceMoveController;
import org.powernukkitx.entity.ai.evaluator.EntityCheckEvaluator;
import org.powernukkitx.entity.ai.evaluator.PassByTimeEvaluator;
import org.powernukkitx.entity.ai.evaluator.RandomSoundEvaluator;
import org.powernukkitx.entity.ai.executor.FleeFromTargetExecutor;
import org.powernukkitx.entity.ai.executor.GuardianAttackExecutor;
import org.powernukkitx.entity.ai.executor.PlaySoundExecutor;
import org.powernukkitx.entity.ai.executor.SpaceRandomRoamExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleSpaceAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.SwimmingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.ai.sensor.NearestTargetEntitySensor;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.entity.effect.Effect;
import org.powernukkitx.entity.effect.EffectType;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Utils;
import org.cloudburstmc.math.vector.Vector3f;

import org.cloudburstmc.protocol.bedrock.data.LevelEvent;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.cloudburstmc.protocol.bedrock.packet.LevelEventPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author PikyCZ
 */
public class EntityElderGuardian extends EntityMob implements EntitySwimmable {

    @Override
    @NotNull
    public String getIdentifier() {
        return ELDER_GUARDIAN;
    }

    public EntityElderGuardian(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(new PlaySoundExecutor(Sound.MOB_ELDERGUARDIAN_IDLE, 0.8f, 1.2f, 1, 1), all(entity -> isInsideOfWater(), new RandomSoundEvaluator()), 6, 1, 1, true),
                        new Behavior(new PlaySoundExecutor(Sound.MOB_GUARDIAN_LAND_IDLE, 0.8f, 1.2f, 1, 1), all(entity -> !isInsideOfWater(), new RandomSoundEvaluator()), 5, 1, 1, true),
                        new Behavior(new FleeFromTargetExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.5f, true, 9), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET),
                                new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, 0, 100)
                        ), 4, 1),
                        new Behavior(new GuardianAttackExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.3f, 15, true, 60, 40), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER),
                                entity -> entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_PLAYER) != null && !entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_PLAYER).isBlocking(),
                                entity -> entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_PLAYER) != null && getLevel().raycastBlocks(entity, entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_PLAYER)).stream().allMatch(Block::isTransparent)
                        ), 3, 1),
                        new Behavior(new GuardianAttackExecutor(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, 0.3f, 15, true, 60, 40), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), 2, 1),
                        new Behavior(new SpaceRandomRoamExecutor(0.36f, 12, 1, 80, false, -1, false, 10), none(), 1, 1)
                ),
                Set.of(new NearestPlayerSensor(40, 0, 20),
                        new NearestTargetEntitySensor<>(0, 16, 20,
                                List.of(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), this::attackTarget)
                ),
                Set.of(new SpaceMoveController(), new LookController(true, true), new DiveController()),
                new SimpleSpaceAStarRouteFinder(new SwimmingPosEvaluator(), this),
                this
        );
    }


    @Override
    protected void initEntity() {
        super.initEntity();
        this.diffHandDamage = new float[]{5f, 8f, 12f};
        this.setDataFlag(ActorFlags.ELDER, true);
    }

    @Override
    public float getWidth() {
        return 1.99f;
    }

    @Override
    public float getHeight() {
        return 1.99f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(80);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.3f);
    }

    @Override
    public String getOriginalName() {
        return "Elder Guardian";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("guardian_elder", "monster", "mob");
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        int secondLoot = ThreadLocalRandom.current().nextInt(6);
        return new Item[]{
                Item.get(Item.PRISMARINE_SHARD, 0, Utils.rand(0, 2)),
                Item.get(Block.WET_SPONGE, 0, 1),
                ThreadLocalRandom.current().nextInt(100) < 20 ? Item.get(Item.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, 0, 1) : Item.AIR,
                ThreadLocalRandom.current().nextInt(1000) < 25 ? Item.get(Item.COD, 0, 1) : Item.AIR,
                secondLoot <= 2 ? Item.get(Item.COD, 0, Utils.rand(0, 1)) : Item.AIR,
                secondLoot > 2 && secondLoot <= 4 ? Item.get(Item.PRISMARINE_CRYSTALS, 0, Utils.rand(0, 1)) : Item.AIR
        };
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (source.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
            return false;
        }
        if (super.attack(source)) {
            if (source instanceof EntityDamageByEntityEvent e) {
                e.getDamager().attack(new EntityDamageByEntityEvent(this, source.getEntity(), EntityDamageEvent.DamageCause.THORNS, getServer().getDifficulty() == 3 ? 2 : 3));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (!this.closed && this.isAlive()) {
            for (Player p : this.getViewers().values()) {
                if (p.locallyInitialized && p.getGamemode() % 2 == 0 && p.distance(this) < 50 && !p.hasEffect(EffectType.MINING_FATIGUE)) {
                    p.addEffect(Effect.get(EffectType.MINING_FATIGUE).setAmplifier(2).setDuration(6000));
                    final LevelEventPacket pk = new LevelEventPacket();
                    pk.setType(LevelEvent.PARTICLE_SOUND_GUARDIAN_GHOST);
                    pk.setPosition(Vector3f.from(this.x, this.y, this.z));
                    p.sendPacket(pk);
                }
            }
        }
        return super.onUpdate(currentTick);
    }

    @Override
    public Integer getExperienceDrops() {
        return 10;
    }

    @Override
    public boolean attackTarget(Entity entity) {
        return switch (entity.getIdentifier()) {
            case SQUID, GLOW_SQUID, AXOLOTL -> true;
            default -> false;
        };
    }
}
