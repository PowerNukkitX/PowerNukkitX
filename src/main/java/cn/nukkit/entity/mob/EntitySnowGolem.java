package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFlowingWater;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.AttackCheckEvaluator;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.MeleeAttackExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Set;


public class EntitySnowGolem extends EntityMob implements EntityWalkable {
    @Override
    @NotNull public String getIdentifier() {
        return SNOW_GOLEM;
    }

    public int waterTicks = 0;

    public EntitySnowGolem(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, true, 10), (entity -> true), 1, 1)
                ),
                Set.of(),
                Set.of(new WalkController(), new LookController(true, true)),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    @Override
    public String getOriginalName() {
        return "Snow Golem";
    }

    @Override
    public float getWidth() {
        return 0.4f;
    }

    @Override
    public float getHeight() {
        return 1.8f;
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(4);
        super.initEntity();
    }

    @Override
    public boolean onUpdate(int currentTick) {
        this.waterTicks++;
        if(this.getLevelBlock().isAir()) {
            if(this.getLevelBlock().down().isFullBlock()){
                this.getLevel().setBlock(this, Block.get(Block.SNOW_LAYER));
            }
        }
        if(this.waterTicks >= 20) {
            if((this.level.isRaining() && !this.isUnderBlock()) || this.getLevelBlock() instanceof BlockLiquid) {
                this.attack(new EntityDamageEvent(this, EntityDamageEvent.DamageCause.WEATHER, 1));
            }
            this.waterTicks = 0;
        }
        return super.onUpdate(currentTick);
    }

    @Override
    public Integer getExperienceDrops() {
        return 0;
    }

}
