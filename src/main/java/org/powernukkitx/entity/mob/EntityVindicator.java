package org.powernukkitx.entity.mob;

import org.powernukkitx.Player;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.EntityWalkable;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.WalkController;
import org.powernukkitx.entity.ai.evaluator.EntityCheckEvaluator;
import org.powernukkitx.entity.ai.evaluator.RandomSoundEvaluator;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.MeleeAttackExecutor;
import org.powernukkitx.entity.ai.executor.PlaySoundExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.memory.MemoryType;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.ai.sensor.NearestTargetEntitySensor;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Utils;

import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author PikyCZ
 */
public class EntityVindicator extends EntityIllager implements EntityWalkable {

    @Override
    @NotNull public String getIdentifier() {
        return VINDICATOR;
    }

    public EntityVindicator(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(new PlaySoundExecutor(Sound.MOB_VINDICATOR_IDLE, isBaby() ? 1.3f : 0.8f, isBaby() ? 1.7f : 1.2f, 1, 1), new RandomSoundEvaluator(), 7, 1),
                        new Behavior(new VindicatorMeleeAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.5f, 40, true, 30), new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET), 4, 1),
                        new Behavior(new VindicatorMeleeAttackExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.5f, 40, false, 30), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER), 3, 1),
                        new Behavior(new VindicatorMeleeAttackExecutor(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, 0.5f, 40, true, 30), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), 2, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.5f, 12, 100, false, -1, true, 10), none(), 1, 1)
                ),
                Set.of(new NearestTargetEntitySensor<>(0, 16, 20,
                                List.of(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), this::attackTarget),
                        new NearestPlayerSensor(16, 0, 0)
                ),
                Set.of(new WalkController(), new LookController(true, true)),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }


    @Override
    public boolean attackTarget(Entity entity) {
        return switch (entity.getIdentifier()) {
            case Entity.IRON_GOLEM,
                 Entity.SNOW_GOLEM,
                 Entity.VILLAGER,
                 Entity.WANDERING_TRADER -> true;
                 default -> super.attackTarget(entity) || isJohnny();
        };
    }

    @Override
    protected void initEntity() {
        this.diffHandDamage = new float[]{3.5f, 5f, 7.5f};
        super.initEntity();
        setItemInHand(Item.get(Item.IRON_AXE));
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.9f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(24);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.35f);
    }

    @Override
    public String getOriginalName() {
        return "Vindicator";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("vindicator", "monster", "illager", "mob");
    }

    @Override
    public Integer getExperienceDrops() {
        return Math.toIntExact(isBaby() ? 1 : 5 + (getArmorInventory().getContents().values().stream().filter(Item::isArmor).count() * ThreadLocalRandom.current().nextInt(1, 4)));
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);
        Item axe = Item.get(Item.IRON_AXE);
        axe.setDamage(ThreadLocalRandom.current().nextInt(1, axe.getMaxDurability()));
        return new Item[]{
                axe,
                Item.get(Item.EMERALD, 0, Utils.rand(0, 2 + looting))
        };
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }

    protected static class VindicatorMeleeAttackExecutor extends MeleeAttackExecutor {

        public VindicatorMeleeAttackExecutor(MemoryType<? extends Entity> memory, float speed, int maxSenseRange, boolean clearDataWhenLose, int coolDown) {
            super(memory, speed, maxSenseRange, clearDataWhenLose, coolDown);
        }

        @Override
        public void onStart(EntityIntelligent entity) {
            super.onStart(entity);
            entity.setDataProperty(ActorDataTypes.TARGET, entity.getMemoryStorage().get(memory).getId());
            entity.setDataFlag(ActorFlags.ANGRY);
            entity.level.addLevelSoundEvent(entity, SoundEvent.ANGRY, -1, Entity.VINDICATOR, false, false);
            Arrays.stream(entity.level.getEntities()).filter(entity1 -> entity1 instanceof EntityPiglin && entity1.distance(entity) < 16 && ((EntityPiglin) entity1).getMemoryStorage().isEmpty(CoreMemoryTypes.ATTACK_TARGET)).forEach(entity1 -> ((EntityPiglin) entity1).getMemoryStorage().put(CoreMemoryTypes.ATTACK_TARGET, entity.getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET)));
            if(entity.getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET) instanceof EntityHoglin) {
                entity.getMemoryStorage().put(CoreMemoryTypes.LAST_HOGLIN_ATTACK_TIME, entity.getLevel().getTick());
            }
        }

        @Override
        public void onStop(EntityIntelligent entity) {
            super.onStop(entity);
            entity.setDataFlag(ActorFlags.ANGRY, false);
            entity.setDataProperty(ActorDataTypes.TARGET, 0L);
        }

        @Override
        public void onInterrupt(EntityIntelligent entity) {
            super.onInterrupt(entity);
            entity.setDataFlag(ActorFlags.ANGRY, false);
            entity.setDataProperty(ActorDataTypes.TARGET, 0L);
        }
    }

    public boolean isJohnny() {
        return getNameTag().equals("Johnny") || (nbt.contains("Johnny") && getNbt().getBoolean("Johnny"));
    }
}
