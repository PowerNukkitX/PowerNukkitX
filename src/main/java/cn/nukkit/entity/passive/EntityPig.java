package cn.nukkit.entity.passive;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.AllMatchEvaluator;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.evaluator.PassByTimeEvaluator;
import cn.nukkit.entity.ai.evaluator.ProbabilityEvaluator;
import cn.nukkit.entity.ai.executor.*;
import cn.nukkit.entity.ai.memory.*;
import cn.nukkit.entity.ai.route.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestBeggingPlayerSensor;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.Set;

/**
 * @author BeYkeRYkt (Nukkit Project)
 */
public class EntityPig extends EntityWalkingAnimal {

    public static final int NETWORK_ID = 12;

    private final IBehaviorGroup behaviorGroup = new BehaviorGroup(
            this.tickSpread,
            Set.of(
                    //用于刷新InLove状态的核心行为
                    new Behavior(
                            new InLoveExecutor(400),
                            new AllMatchEvaluator(
                                    new PassByTimeEvaluator<>(PlayerBreedingMemory.class,0,400),
                                    new PassByTimeEvaluator<>(InLoveMemory.class,6000,Integer.MAX_VALUE,true)
                            ),
                            1,1
                    )
            ),
            Set.of(
                    new Behavior(new RandomRoamExecutor(0.5f, 12, 40, true,100,true,10), new PassByTimeEvaluator<>(AttackMemory.class,0,100), 6, 1),
                    new Behavior(new EntityBreedingExecutor<>(EntityCow.class,16,100,0.5f), entity -> entity.getMemoryStorage().get(InLoveMemory.class).isInLove(),5,1),
                    new Behavior(new MoveToTargetExecutor(NearestBeggingPlayerMemory.class, 0.3f), new MemoryCheckNotEmptyEvaluator(NearestBeggingPlayerMemory.class), 4, 1),
                    new Behavior(new LookAtTargetExecutor(NearestPlayerMemory.class,100), new ProbabilityEvaluator(4,10), 1, 1,100),
                    new Behavior(new RandomRoamExecutor(0.15f, 12, 100, false,-1,true,10), (entity -> true), 1, 1)
            ),
            Set.of(new NearestBeggingPlayerSensor(8, 0), new NearestPlayerSensor(8, 0,20)),
            Set.of(new WalkController(), new LookController(true, true)),
            new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this)
    );

    public EntityPig(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup getBehaviorGroup() {
        return behaviorGroup;
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.45f;
        }
        return 0.9f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.45f;
        }
        return 0.9f;
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
        return "Pig";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(((this.isOnFire()) ? Item.COOKED_PORKCHOP : Item.RAW_PORKCHOP))};
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public boolean isBreedingItem(Item item) {
        int id = item.getId();

        return id == Item.CARROT || id == Item.POTATO || id == Item.BEETROOT;
    }
}
