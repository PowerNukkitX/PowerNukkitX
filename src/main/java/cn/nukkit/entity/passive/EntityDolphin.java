package cn.nukkit.entity.passive;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityWaterAnimal;
import cn.nukkit.entity.ai.BehaviorGroup;
import cn.nukkit.entity.ai.IBehaviorGroup;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.controller.SpaceMoveController;
import cn.nukkit.entity.ai.evaluator.PlayerEvaluator;
import cn.nukkit.entity.ai.executor.MoveToTargetExecutor;
import cn.nukkit.entity.ai.memory.NearestPlayerMemory;
import cn.nukkit.entity.ai.route.SimpleSpaceAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.SwimmingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.Set;

/**
 * @author PetteriM1
 */
public class EntityDolphin extends EntityWaterAnimal {

    public static final int NETWORK_ID = 31;

    protected IBehaviorGroup behaviorGroup = new BehaviorGroup(
            Set.of(
                    new Behavior(new MoveToTargetExecutor(NearestPlayerMemory.class),new PlayerEvaluator(),1,1)
            ),
            Set.of(new NearestPlayerSensor(50,0)),
            Set.of(new SpaceMoveController()),
            new SimpleSpaceAStarRouteFinder(new SwimmingPosEvaluator(),this)
    );

    public EntityDolphin(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }


    @Override
    public IBehaviorGroup getBehaviorGroup() {
        return behaviorGroup;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return "Dolphin";
    }

    @Override
    public float getWidth() {
        return 0.9f;
    }

    @Override
    public float getHeight() {
        return 0.6f;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(10);
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.RAW_FISH)};
    }
}
