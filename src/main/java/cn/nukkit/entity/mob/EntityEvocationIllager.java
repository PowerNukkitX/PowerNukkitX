package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.DistanceEvaluator;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.MemoryCheckEmptyEvaluator;
import cn.nukkit.entity.ai.evaluator.PassByTimeEvaluator;
import cn.nukkit.entity.ai.evaluator.RandomSoundEvaluator;
import cn.nukkit.entity.ai.executor.DoNothingExecutor;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.FleeFromTargetExecutor;
import cn.nukkit.entity.ai.executor.LookAtTargetExecutor;
import cn.nukkit.entity.ai.executor.PlaySoundExecutor;
import cn.nukkit.entity.ai.executor.evocation.ColorConversionExecutor;
import cn.nukkit.entity.ai.executor.evocation.FangCircleExecutor;
import cn.nukkit.entity.ai.executor.evocation.FangLineExecutor;
import cn.nukkit.entity.ai.executor.evocation.VexSummonExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestTargetEntitySensor;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.passive.EntitySheep;
import cn.nukkit.item.Item;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.entity.ai.memory.CoreMemoryTypes.LAST_MAGIC;

/**
 * @author PikyCZ
 */
public class EntityEvocationIllager extends EntityIllager implements EntityWalkable {
    public EntityEvocationIllager(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    @NotNull public String getIdentifier() {
        return EntityID.EVOCATION_ILLAGER;
    }

    @Override
    protected IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(new PlaySoundExecutor(Sound.MOB_EVOCATION_ILLAGER_AMBIENT), new RandomSoundEvaluator(), 10, 1),
                        new Behavior(new FleeFromTargetExecutor(CoreMemoryTypes.NEAREST_SHARED_ENTITY, 0.5f, true, 9), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SHARED_ENTITY),
                                new DistanceEvaluator(CoreMemoryTypes.NEAREST_SHARED_ENTITY, 8),
                                any(
                                        new MemoryCheckEmptyEvaluator(LAST_MAGIC),
                                        entity -> entity.getMemoryStorage().get(LAST_MAGIC) == SPELL.NONE
                                )
                        ), 9, 1),
                        new Behavior(new FleeFromTargetExecutor(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, 0.5f, true, 11), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET),
                                new DistanceEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, 10),
                                any(
                                        new MemoryCheckEmptyEvaluator(LAST_MAGIC),
                                        entity -> entity.getMemoryStorage().get(LAST_MAGIC) == SPELL.NONE
                                )
                        ), 8, 1),
                        new Behavior(new ColorConversionExecutor(), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET),
                                new PassByTimeEvaluator(CoreMemoryTypes.LAST_CONVERSION, 100),
                                new PassByTimeEvaluator(CoreMemoryTypes.LAST_ATTACK_TIME, 40),
                                entity -> {
                                    for(Entity entity1 : entity.getLevel().getNearbyEntities(entity.getBoundingBox().grow(16, 16, 16))) {
                                        if(entity1 instanceof EntitySheep entitySheep) {
                                            if(entitySheep.getColor() == DyeColor.BLUE.getWoolData()) {
                                                return true;
                                            }
                                        }
                                    }
                                    return false;
                                },
                                any(
                                        new MemoryCheckEmptyEvaluator(LAST_MAGIC),
                                        entity -> entity.getMemoryStorage().get(LAST_MAGIC) == SPELL.NONE,
                                        entity -> entity.getMemoryStorage().get(LAST_MAGIC) == SPELL.COLOR_CONVERSION
                                ),
                                entity -> entity.getLevel().getGameRules().getBoolean(GameRule.MOB_GRIEFING)
                        ), 7, 1),
                        new Behavior(new VexSummonExecutor(), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET),
                                new PassByTimeEvaluator(CoreMemoryTypes.LAST_ATTACK_SUMMON, 340),
                                new PassByTimeEvaluator(CoreMemoryTypes.LAST_ATTACK_TIME, 40),
                                entity -> {
                                    int count = 0;
                                    for(Entity entity1 : entity.getLevel().getNearbyEntities(entity.getBoundingBox().grow(15, 15, 15))) {
                                        if(entity1 instanceof EntityVex) count++;
                                    }
                                    return count < 8;
                                },
                                any(
                                        new MemoryCheckEmptyEvaluator(LAST_MAGIC),
                                        entity -> entity.getMemoryStorage().get(LAST_MAGIC) == SPELL.NONE,
                                        entity -> entity.getMemoryStorage().get(LAST_MAGIC) == SPELL.SUMMON
                                )
                        ), 6, 1),
                        new Behavior(new FangCircleExecutor(), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET),
                                new PassByTimeEvaluator(CoreMemoryTypes.LAST_ATTACK_CAST, 100),
                                new PassByTimeEvaluator(CoreMemoryTypes.LAST_ATTACK_TIME, 40),
                                new DistanceEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, 3),
                                any(
                                        new MemoryCheckEmptyEvaluator(LAST_MAGIC),
                                        entity -> entity.getMemoryStorage().get(LAST_MAGIC) == SPELL.NONE,
                                        entity -> entity.getMemoryStorage().get(LAST_MAGIC) == SPELL.CAST_CIRLCE
                                )
                        ), 5, 1),
                        new Behavior(new FangLineExecutor(), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET),
                                new PassByTimeEvaluator(CoreMemoryTypes.LAST_ATTACK_CAST, 100),
                                new PassByTimeEvaluator(CoreMemoryTypes.LAST_ATTACK_TIME, 40),
                                any(
                                        new MemoryCheckEmptyEvaluator(LAST_MAGIC),
                                        entity -> entity.getMemoryStorage().get(LAST_MAGIC) == SPELL.NONE,
                                        entity -> entity.getMemoryStorage().get(LAST_MAGIC) == SPELL.CAST_LINE
                                )
                                ), 4, 1),
                        new Behavior(new LookAtTargetExecutor(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, 1), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET),
                                entity -> !entity.getDataFlag(EntityFlag.CASTING)
                        ), 3, 1),
                        new Behavior(new DoNothingExecutor(), entity -> entity.getDataFlag(EntityFlag.CASTING), 2, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, true, 10), none(), 1, 1)
                ),
                Set.of(
                        new NearestTargetEntitySensor<>(0, 16, 20,
                                List.of(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), this::attackTarget),
                        new NearestTargetEntitySensor<>(0, 16, 20,
                                List.of(CoreMemoryTypes.NEAREST_SHARED_ENTITY), entity -> entity instanceof EntityCreaking)
                ),
                Set.of(new WalkController(), new LookController(true, true)),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    @Override
    public void spawnTo(Player player) {
        super.spawnTo(player);
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(24);
        super.initEntity();
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
        return "Evoker";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("evocation_illager", "monster", "illager", "mob");
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }

    @Override
    public Integer getExperienceDrops() {
        return 10;
    }

    @Override
    public Item[] getDrops() {
        return new Item[] {
                Item.get(Item.TOTEM_OF_UNDYING),
                Item.get(Item.EMERALD, 0, ThreadLocalRandom.current().nextInt(2))
        };
    }


    public enum SPELL {
        NONE,
        CAST_LINE,
        CAST_CIRLCE,
        SUMMON,
        COLOR_CONVERSION
    }

}
