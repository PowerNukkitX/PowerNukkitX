package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.MeleeAttackExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;

import java.util.Set;

/**
 * @author Dr. Nick Doran
 * @since 4/23/2017
 */
public class EntityZombie extends EntityMob implements EntityWalkable, EntitySmite {

    public static final int NETWORK_ID = 32;

    public EntityZombie(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.3f, 40, true, 10), all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.ATTACK_TARGET),
                                entity -> !entity.getMemoryStorage().notEmpty(CoreMemoryTypes.ATTACK_TARGET) || !(entity.getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET) instanceof Player player) || player.isSurvival()
                        ), 3, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.3f, 40, false, 10), all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_PLAYER),
                                entity -> {
                                    if (entity.getMemoryStorage().isEmpty(CoreMemoryTypes.NEAREST_PLAYER))
                                        return true;
                                    Player player = entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_PLAYER);
                                    return player.isSurvival();
                                }
                        ), 2, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, true, 10), (entity -> true), 1, 1)
                ),
                Set.of(new NearestPlayerSensor(40, 0, 20)),
                Set.of(new WalkController(), new LookController(true, true)),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this)
        );
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(20);
        this.diffHandDamage = new float[]{2.5f, 3f, 4.5f};
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

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return "Zombie";
    }

    @PowerNukkitOnly
    @Override
    public boolean isUndead() {
        return true;
    }

    @PowerNukkitOnly
    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }

    @PowerNukkitXOnly
    @Override
    public boolean onUpdate(int currentTick) {
        if (this.getLevel().getDimension() == Level.DIMENSION_OVERWORLD)
            if (this.getLevel().getTime() > 0 && this.getLevel().getTime() <= 12000)
                if (!this.hasEffect(Effect.FIRE_RESISTANCE))
                    if (!this.isInsideOfWater())
                        if (!this.isUnderBlock())
                            if (!this.isOnFire())
                                this.setOnFire(1);
        return super.onUpdate(currentTick);
    }

    @Since("1.19.50-r4")
    @Override
    public double getFloatingForceFactor() {
        return 0.7;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        var result = super.attack(source);
        if (source instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) {
            //更新仇恨目标
            getMemoryStorage().put(CoreMemoryTypes.ATTACK_TARGET, entityDamageByEntityEvent.getDamager());
        }
        return result;
    }
}
