package cn.nukkit.entity.mob;

import cn.nukkit.block.BlockCreakingHeart;
import cn.nukkit.block.BlockTurtleEgg;
import cn.nukkit.blockentity.BlockEntityCreakingHeart;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.evaluator.RandomSoundEvaluator;
import cn.nukkit.entity.ai.executor.DoNothingExecutor;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.JumpExecutor;
import cn.nukkit.entity.ai.executor.MeleeAttackExecutor;
import cn.nukkit.entity.ai.executor.MoveToTargetExecutor;
import cn.nukkit.entity.ai.executor.NearestBlockIncementExecutor;
import cn.nukkit.entity.ai.executor.PlaySoundExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestBlockSensor;
import cn.nukkit.entity.ai.sensor.NearestEntitySensor;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EntityCreaking extends EntityMob {

    @Override @NotNull public String getIdentifier() {
        return CREAKING;
    }

    @Setter
    protected BlockEntityCreakingHeart creakingHeart;

    public EntityCreaking(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(
                ),
                Set.of(
                       // new Behavior(new DoNothingExecutor(), entity -> getViewers().values().stream().anyMatch(player -> player.isLookingAt(entity, 70)), 5, 1),
                        new Behavior(new PlaySoundExecutor(Sound.MOB_CREAKING_AMBIENT), new RandomSoundEvaluator(), 4, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.3f, 40, true, 30), new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET), 3, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.3f, 40, false, 30), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER), 2, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, true, 10), none(), 1, 1)
                ),
                Set.of(
                        new NearestPlayerSensor(40, 0, 0)
                ),
                Set.of(new WalkController(), new LookController(true, true)),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(1);
        this.diffHandDamage = new float[]{2.5f, 3, 4.5f};

        if(namedTag.containsCompound("creakingHeart")) {
            CompoundTag tag = namedTag.getCompound("creakingHeart");
            Vector3 vec = new Vector3(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
            if(getLevel().getBlock(vec, true) instanceof BlockCreakingHeart heart) {
                heart.getOrCreateBlockEntity().setLinkedCreaking(this);
            }
        }
        super.initEntity();
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if(source.getCause() == EntityDamageEvent.DamageCause.CUSTOM)
            return super.attack(source);
        return !source.isCancelled();
    }

    @Override
    public void kill() {
        super.kill();
        if(creakingHeart != null && creakingHeart.isBlockEntityValid()) {
            creakingHeart.setLinkedCreaking(null);
        }
    }

    @Override
    public void saveNBT() {
        if(creakingHeart != null) {
            CompoundTag tag = new CompoundTag();
            tag.putInt("x", creakingHeart.getFloorX());
            tag.putInt("y", creakingHeart.getFloorY());
            tag.putInt("z", creakingHeart.getFloorZ());
            this.namedTag.putCompound("creakingHeart", tag);
        }
        super.saveNBT();
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if(!(!getLevel().isDay() || getLevel().isRaining() || getLevel().isThundering())) {
            this.kill();
        }
        return super.onUpdate(currentTick);
    }

    @Override
    public float getHeight() {
        return 2.5F;
    }

    @Override
    public float getWidth() {
        return 1F;
    }

    @Override
    public Integer getExperienceDrops() {
        return 0;
    }

    @Override
    public void updateMovement() {
        super.updateMovement();
        if(creakingHeart != null && creakingHeart.isBlockEntityValid()) {
            creakingHeart.getHeart().updateAroundRedstone(BlockFace.UP, BlockFace.DOWN);
        } else kill();
    }
}
