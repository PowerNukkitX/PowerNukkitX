package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.evaluator.RandomSoundEvaluator;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.PlaySoundExecutor;
import cn.nukkit.entity.ai.executor.PotionThrowExecutor;
import cn.nukkit.entity.ai.executor.UsePotionExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestEntitySensor;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author PikyCZ
 */
public class EntityWitch extends EntityMob implements EntityWalkable {

    @Override
    @NotNull public String getIdentifier() {
        return WITCH;
    }

    public EntityWitch(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(
                        new Behavior(new PlaySoundExecutor(Sound.MOB_WITCH_AMBIENT), new RandomSoundEvaluator(), 2, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, true, 10), none(), 1, 1)
                ),
                Set.of(
                        new Behavior(new UsePotionExecutor(0.3f, 30, 20), all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.LAST_BE_ATTACKED_TIME),
                                entity -> entity.getLevel().getTick() - getMemoryStorage().get(CoreMemoryTypes.LAST_BE_ATTACKED_TIME) <= 1
                        ), 4, 1),
                        new Behavior(new PotionThrowExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.3f, 15, true, 30, 20), new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET), 3, 1),
                        new Behavior(new PotionThrowExecutor(CoreMemoryTypes.NEAREST_GOLEM, 0.3f, 15, true, 30, 20), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_GOLEM), 2, 1),
                        new Behavior(new PotionThrowExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.3f, 15, true, 30, 20), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER), 1, 1)
                ),
                Set.of(
                        new NearestPlayerSensor(16, 0, 20),
                        new NearestEntitySensor(EntityGolem.class, CoreMemoryTypes.NEAREST_GOLEM, 42, 0)
                ),
                Set.of(new WalkController(), new LookController(true, true)),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(26);
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
        return "Witch";
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }

    @Override
    public Item[] getDrops() {
        String itemId = switch (ThreadLocalRandom.current().nextInt(7)) {
            case 0 -> Item.STICK;
            case 1 -> Item.SPIDER_EYE;
            case 2 -> Item.GLOWSTONE_DUST;
            case 3 -> Item.GUNPOWDER;
            case 4 -> Item.REDSTONE;
            case 5 -> Item.SUGAR;
            default -> Item.GLASS_BOTTLE;
        };
        return new Item[] {
                Item.get(itemId,0, ThreadLocalRandom.current().nextInt(5))
        };
    }
}
