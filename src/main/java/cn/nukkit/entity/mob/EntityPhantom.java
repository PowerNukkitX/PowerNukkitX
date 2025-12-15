package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityFlyable;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LiftController;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.SpaceMoveController;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.evaluator.PassByTimeEvaluator;
import cn.nukkit.entity.ai.evaluator.RandomSoundEvaluator;
import cn.nukkit.entity.ai.executor.CircleAboveTargetExecutor;
import cn.nukkit.entity.ai.executor.MeleeAttackExecutor;
import cn.nukkit.entity.ai.executor.PlaySoundExecutor;
import cn.nukkit.entity.ai.executor.SpaceRandomRoamExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.entity.ai.route.finder.impl.SimpleSpaceAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.FlyingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.ai.sensor.NearestTargetEntitySensor;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

/**
 * @author PetteriM1
 */
public class EntityPhantom extends EntityMob implements EntityFlyable, EntitySmite {

    @Override
    @NotNull public String getIdentifier() {
        return PHANTOM;
    }

    public EntityPhantom(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(new PlaySoundExecutor(Sound.MOB_PHANTOM_IDLE, 0.8f, 1.2f, 0.8f, 0.8f), all(new RandomSoundEvaluator()), 6, 1),
                        new Behavior(new CircleAboveTargetExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.4f, true), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET),
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.LAST_ATTACK_ENTITY),
                                not(new PassByTimeEvaluator(CoreMemoryTypes.LAST_ATTACK_TIME, Utils.rand(200, 400)))
                        ), 5, 1),
                        new Behavior(new CircleAboveTargetExecutor(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, 0.4f, true), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET),
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.LAST_ATTACK_ENTITY),
                                not(new PassByTimeEvaluator(CoreMemoryTypes.LAST_ATTACK_TIME, Utils.rand(200, 400)))
                        ), 4, 1),
                        new Behavior(new PhantomMeleeAttackExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.5f, 64, false, 30), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER), 3, 1),
                        new Behavior(new PhantomMeleeAttackExecutor(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, 0.5f, 64, false, 30), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), 2, 1),
                        new Behavior(new SpaceRandomRoamExecutor(0.15f, 12, 100, 20, false, -1, true, 10), (entity -> true), 1, 1)
                ),
                Set.of(
                        new NearestPlayerSensor(64, 0, 20),
                        new NearestTargetEntitySensor<>(0, 64, 20,
                                List.of(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), this::attackTarget)
                ),
                Set.of(new SpaceMoveController(), new LookController(true, true), new LiftController()),
                new SimpleSpaceAStarRouteFinder(new FlyingPosEvaluator(), this),
                this
        );
    }

    @Override
    public boolean isEnablePitch() {
        return false;
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(20);
        super.initEntity();
        this.diffHandDamage = new float[]{4f, 6f, 9f};

    }

    @Override
    public float getWidth() {
        return 0.9f;
    }

    @Override
    public float getHeight() {
        return 0.5f;
    }

    @Override
    public String getOriginalName() {
        return "Phantom";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("phantom", "undead", "monster", "mob");
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);

        if (Utils.rand(0, 1) == 0) {
            return Item.EMPTY_ARRAY;
        }

        int amount = Utils.rand(0, 1 + looting);
        if (amount <= 0) {
            return Item.EMPTY_ARRAY;
        }

        return new Item[]{
                Item.get(ItemID.PHANTOM_MEMBRANE, 0, amount)
        };
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
    public boolean onUpdate(int currentTick) {
        burn(this);
        return super.onUpdate(currentTick);
    }

    private static class PhantomMeleeAttackExecutor extends MeleeAttackExecutor {

        public PhantomMeleeAttackExecutor(MemoryType<? extends Entity> memory, float speed, int maxSenseRange, boolean clearDataWhenLose, int coolDown) {
            super(memory, speed, maxSenseRange, clearDataWhenLose, coolDown, 2.5f);
        }

        @Override
        public void onStart(EntityIntelligent entity) {
            super.onStart(entity);
            entity.level.addSound(entity, Sound.MOB_PHANTOM_SWOOP);
        }
    }
}
