package cn.nukkit.entity.passive;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.SpaceMoveController;
import cn.nukkit.entity.ai.evaluator.ProbabilityEvaluator;
import cn.nukkit.entity.ai.executor.SpaceRandomRoamExecutor;
import cn.nukkit.entity.ai.route.finder.impl.SimpleSpaceAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.SwimmingPosEvaluator;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.Set;

/**
 * 所有鱼的基类
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public abstract class EntityFish extends EntitySwimmingAnimal{

    protected IBehaviorGroup behaviorGroup;

    public EntityFish(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    //移除搁浅音效很不对味
    @Override
    public IBehaviorGroup getBehaviorGroup() {
        if (behaviorGroup == null) {
            behaviorGroup = new BehaviorGroup(
                    this.tickSpread,
                    Set.of(),
                    Set.of(
                            new Behavior(new SpaceRandomRoamExecutor(0.2f, 12, 150, false, -1, 20), new ProbabilityEvaluator(5, 10), 1, 1, 25)
                    ),
                    Set.of(),
                    Set.of(new SpaceMoveController(), new LookController(true, true)),
                    new SimpleSpaceAStarRouteFinder(new SwimmingPosEvaluator(), this)
            );
        }
        return behaviorGroup;
    }
}
