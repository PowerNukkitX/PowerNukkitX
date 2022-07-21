package cn.nukkit.entity.passive;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.controller.WalkingController;
import cn.nukkit.entity.ai.evaluator.PlayerEvaluator;
import cn.nukkit.entity.ai.executor.MoveToTargetExecutor;
import cn.nukkit.entity.ai.memory.NearestPlayerMemory;
import cn.nukkit.entity.ai.route.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.Set;

public class EntityCat extends EntityWalkingAnimal {

    public static final int NETWORK_ID = 75;

    protected IBehaviorGroup behaviorGroup = new BehaviorGroup(
            Set.of(
                    new Behavior(new MoveToTargetExecutor(NearestPlayerMemory.class),new PlayerEvaluator(),1,1)
            ),
            Set.of(new NearestPlayerSensor(50,0)),
            Set.of(new WalkingController()),
            new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(),this)
    );

    @Override
    public IBehaviorGroup getBehaviorGroup() {
        return behaviorGroup;
    }

    public EntityCat(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.3f;
        }
        return 0.6f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.35f;
        }
        return 0.7f;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(10);
    }


    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return "Cat";
    }
}
