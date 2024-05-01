package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.*;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.AttackCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.MeleeAttackExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.ai.sensor.NearestTargetEntitySensor;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author PikyCZ
 */
public class EntityWitherSkeleton extends EntityMob implements EntityWalkable, EntitySmite {

    @Override
    @NotNull public String getIdentifier() {
        return WITHER_SKELETON;
    }

    public EntityWitherSkeleton(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(
                        new Behavior(
                                entity -> {
                                    var storage = getMemoryStorage();
                                    if (storage.notEmpty(CoreMemoryTypes.ATTACK_TARGET)) return false;
                                    Entity attackTarget = null;
                                    if (storage.notEmpty(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET) && storage.get(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET).isAlive()) {
                                        attackTarget = storage.get(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET);
                                    }
                                    storage.put(CoreMemoryTypes.ATTACK_TARGET, attackTarget);
                                    return false;
                                },
                                entity -> true, 20
                        )
                ),
                Set.of(
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.3f, 40, true, 10, Effect.get(EffectType.WITHER).setDuration(200)), all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.ATTACK_TARGET),
                                new AttackCheckEvaluator()
                        ), 3, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.3f, 40, false, 10, Effect.get(EffectType.WITHER).setDuration(200)), all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_PLAYER),
                                entity -> {
                                    if (entity.getMemoryStorage().isEmpty(CoreMemoryTypes.NEAREST_PLAYER))
                                        return true;
                                    Player player = entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_PLAYER);
                                    return player.isSurvival() || player.isAdventure();
                                }
                        ), 2, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, true, 10), (entity -> true), 1, 1)
                ),
                Set.of(new NearestPlayerSensor(40, 0, 20),
                        new NearestTargetEntitySensor<>(0, 16, 20,
                                List.of(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), this::attackTarget)
                ),
                Set.of(new WalkController(), new LookController(true, true)),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }


    //凋零骷髅会攻击距离他16格范围内的玩家、雪傀儡、小海龟、铁傀儡、猪灵或猪灵蛮兵
    @Override
    public boolean attackTarget(Entity entity) {
        return switch (entity.getIdentifier()) {
            case EntityID.SNOW_GOLEM, EntityID.IRON_GOLEM,
                    EntityID.TURTLE, EntityID.PIGLIN,
                    EntityID.PIGLIN_BRUTE -> true;
            default -> false;
        };
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(20);
        this.diffHandDamage = new float[]{5f, 8f, 12f};
        super.initEntity();
        // 判断凋零骷髅是否手持石剑如果没有就给它石剑
        if (this.getItemInHand() != Item.get(Item.STONE_SWORD)) {
            this.setItemInHand(Item.get(Item.STONE_SWORD));
        }
        // 设置凋零骷髅空闲状态播放空闲声音
        this.setDataProperty(AMBIENT_SOUND_EVENT_NAME, LevelSoundEventPacket.SOUND_AMBIENT);
    }

    @Override
    public float getWidth() {
        return 0.7f;
    }

    @Override
    public float getHeight() {
        return 2.4f;
    }

    @Override
    public String getOriginalName() {
        return "Wither Skeleton";
    }

    @Override
    public boolean isUndead() {
        return true;
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }

    //掉落剑的概率为8.5% 掉落头的概率为2.5%
    @Override
    public Item[] getDrops() {
        List<Item> drops = new ArrayList<>();
        drops.add(Item.get(Item.BONE, 0, Utils.rand(0, 2)));
        if (Utils.rand(0, 2) == 0) {
            drops.add(Item.get(Item.COAL, 0, 1));
        }
        //掉落石剑的概率为8.5%
        if (Utils.rand(0, 200) <= 17) {
            drops.add(Item.get(Item.STONE_SWORD, Utils.rand(0, 131), 1));
        }
        //掉落头的概率为2.5%
        if (Utils.rand(0, 40) == 1) {
            drops.add(Item.get(BlockID.SKULL, 1, 1));
        }
        return drops.toArray(Item.EMPTY_ARRAY);
    }
}
