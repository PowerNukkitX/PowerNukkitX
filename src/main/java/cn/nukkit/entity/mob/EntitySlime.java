package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityVariant;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.HoppingController;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.MeleeAttackExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.ai.sensor.NearestTargetEntitySensor;
import cn.nukkit.entity.passive.EntityFrog;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

/**
 * @author PikyCZ
 */
public class EntitySlime extends EntityMob implements EntityWalkable, EntityVariant {

    @Override
    @NotNull public String getIdentifier() {
        return SLIME;
    }

    public static final int SIZE_SMALL = 1;
    public static final int SIZE_MEDIUM = 2;
    public static final int SIZE_BIG = 4;

    public EntitySlime(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.3f, 40, true, 30), new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET), 3, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, 0.3f, 40, false, 30), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), 2, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, true, 10), none(), 1, 1)
                ),
                Set.of(new NearestTargetEntitySensor<>(0, 16, 20,
                        List.of(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), this::attackTarget)),
                Set.of(new HoppingController(40), new LookController(true, true)),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        if (!hasVariant()) {
            this.setVariant(randomVariant());
        }

        if (getVariant() == SIZE_BIG) {
            this.diffHandDamage = new float[] {3, 4, 6};
        } else if (getVariant() == SIZE_MEDIUM) {
            this.diffHandDamage = new float[] {2, 2, 3};
        } else {
            this.diffHandDamage = new float[] {0, 0, 0};
        }

        if (getVariant() == SIZE_BIG) {
            this.setMaxHealth(16);
        } else if (getVariant() == SIZE_MEDIUM) {
            this.setMaxHealth(4);
        } else if (getVariant() == SIZE_SMALL) {
            this.setMaxHealth(1);
        }
        recalculateBoundingBox();
    }

    @Override
    public double getFloatingForceFactor() {
        return 0;
    }

    @Override
    public float getWidth() {
        if(getBehaviorGroup() == null) return 0;
        return 0.51f + getVariant() * 0.51f;
    }

    @Override
    public float getHeight() {
        if(getBehaviorGroup() == null) return 0;
        return 0.51f + getVariant() * 0.51f;
    }

    @Override
    public String getOriginalName() {
        return "Slime";
    }

    @Override
    public Item[] getDrops() {
        if(getVariant() == SIZE_SMALL) {
            if(getLastDamageCause() != null) {
                if(lastDamageCause instanceof EntityDamageByEntityEvent event) {
                    if(event.getDamager() instanceof EntityFrog frog) {
                        return new Item[]{Item.get(Item.SLIME_BALL)};
                    }
                }
            }
            return new Item[] {Item.get(Item.SLIME_BALL, Utils.rand(0,3))};
        }
        return Item.EMPTY_ARRAY;
    }

    @Override
    public Integer getExperienceDrops() {
        return getVariant();
    }

    @Override
    public int[] getAllVariant() {
        return new int[]{1, 2, 4};
    }

    private int getSmaller() {
        return switch (getVariant()) {
            case 4 -> 2;
            default -> getVariant()-1;
        };
    }

    @Override
    public void kill() {
        if(getVariant() != SIZE_SMALL) {
            for(int i = 1; i < Utils.rand(2, 5); i++) {
                EntitySlime slime = new EntitySlime(this.getChunk(), this.namedTag);
                slime.setPosition(this.add(Utils.rand(-0.5, 0.5), 0, Utils.rand(-0.5, 0.5)));
                slime.setRotation(this.yaw, this.pitch);
                slime.setVariant(getSmaller());
                slime.spawnToAll();
            }
        }
        super.kill();
    }

    @Override
    public boolean attackTarget(Entity entity) {
        return super.attackTarget(entity) || entity instanceof EntityGolem;
    }
}
