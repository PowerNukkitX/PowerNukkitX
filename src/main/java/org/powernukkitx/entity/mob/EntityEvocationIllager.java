package org.powernukkitx.entity.mob;

import org.powernukkitx.Player;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.entity.EntityWalkable;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.WalkController;
import org.powernukkitx.entity.ai.evaluator.DistanceEvaluator;
import org.powernukkitx.entity.ai.evaluator.EntityCheckEvaluator;
import org.powernukkitx.entity.ai.evaluator.MemoryCheckEmptyEvaluator;
import org.powernukkitx.entity.ai.evaluator.PassByTimeEvaluator;
import org.powernukkitx.entity.ai.evaluator.RandomSoundEvaluator;
import org.powernukkitx.entity.ai.executor.DoNothingExecutor;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.FleeFromTargetExecutor;
import org.powernukkitx.entity.ai.executor.LookAtTargetExecutor;
import org.powernukkitx.entity.ai.executor.PlaySoundExecutor;
import org.powernukkitx.entity.ai.executor.evocation.ColorConversionExecutor;
import org.powernukkitx.entity.ai.executor.evocation.FangCircleExecutor;
import org.powernukkitx.entity.ai.executor.evocation.FangLineExecutor;
import org.powernukkitx.entity.ai.executor.evocation.VexSummonExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.NearestTargetEntitySensor;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.entity.passive.EntitySheep;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.GameRule;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.DyeColor;
import org.powernukkitx.utils.Utils;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

import static org.powernukkitx.entity.ai.memory.CoreMemoryTypes.LAST_MAGIC;

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
        return BehaviorGroup.builder(this)
                .behaviors(
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
                                entity -> !entity.getDataFlag(ActorFlags.CASTING)
                        ), 3, 1),
                        new Behavior(new DoNothingExecutor(), entity -> entity.getDataFlag(ActorFlags.CASTING), 2, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, true, 10), none(), 1, 1)
                )
                .sensors(
                        new NearestTargetEntitySensor<>(0, 16, 20,
                                List.of(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), this::attackTarget),
                        new NearestTargetEntitySensor<>(0, 16, 20,
                                List.of(CoreMemoryTypes.NEAREST_SHARED_ENTITY), entity -> entity instanceof EntityCreaking)
                )
                .controllers(new WalkController(), new LookController(true, true))
                .routeFinder(new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this))
                .build();
    }

    @Override
    public void spawnTo(Player player) {
        super.spawnTo(player);
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
        return MovementComponent.value(0.5f);
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
    public Item[] getDrops(@NotNull Item weapon) {
        int lootingLevel = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);
        return new Item[] {
                Item.get(Item.TOTEM_OF_UNDYING),
                Item.get(Item.EMERALD, 0, Utils.rand(0, 2 + lootingLevel))
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
