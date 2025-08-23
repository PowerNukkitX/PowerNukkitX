package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.RandomSoundEvaluator;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.MeleeAttackExecutor;
import cn.nukkit.entity.ai.executor.PlaySoundExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.ai.sensor.NearestTargetEntitySensor;
import cn.nukkit.entity.data.EntityDataTypes;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.types.LevelSoundEvent;
import org.jetbrains.annotations.NotNull;

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
        this.setMaxHealth(24);
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
    public Item[] getDrops() {
        Item axe = Item.get(Item.IRON_AXE);
        axe.setDamage(ThreadLocalRandom.current().nextInt(1, axe.getMaxDurability()));
        return new Item[]{
                axe,
                Item.get(Item.EMERALD, 0, ThreadLocalRandom.current().nextInt(2))
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
            entity.setDataProperty(EntityDataTypes.TARGET_EID, entity.getMemoryStorage().get(memory).getId());
            entity.setDataFlag(EntityFlag.ANGRY);
            entity.level.addLevelSoundEvent(entity, LevelSoundEvent.ANGRY, -1, Entity.VINDICATOR, false, false);
            Arrays.stream(entity.level.getEntities()).filter(entity1 -> entity1 instanceof EntityPiglin && entity1.distance(entity) < 16 && ((EntityPiglin) entity1).getMemoryStorage().isEmpty(CoreMemoryTypes.ATTACK_TARGET)).forEach(entity1 -> ((EntityPiglin) entity1).getMemoryStorage().put(CoreMemoryTypes.ATTACK_TARGET, entity.getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET)));
            if(entity.getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET) instanceof EntityHoglin) {
                entity.getMemoryStorage().put(CoreMemoryTypes.LAST_HOGLIN_ATTACK_TIME, entity.getLevel().getTick());
            }
        }

        @Override
        public void onStop(EntityIntelligent entity) {
            super.onStop(entity);
            entity.setDataFlag(EntityFlag.ANGRY, false);
            entity.setDataProperty(EntityDataTypes.TARGET_EID, 0L);
        }

        @Override
        public void onInterrupt(EntityIntelligent entity) {
            super.onInterrupt(entity);
            entity.setDataFlag(EntityFlag.ANGRY, false);
            entity.setDataProperty(EntityDataTypes.TARGET_EID, 0L);
        }
    }

    public boolean isJohnny() {
        return getNameTag().equals("Johnny") || (namedTag.exist("Johnny") && namedTag.getBoolean("Johnny"));
    }
}
