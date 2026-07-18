package org.powernukkitx.entity.mob;

import org.powernukkitx.Player;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.EntityWalkable;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.WalkController;
import org.powernukkitx.entity.ai.evaluator.EntityCheckEvaluator;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.MeleeAttackExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.ai.sensor.NearestTargetEntitySensor;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class EntityZoglin extends EntityMob implements EntityWalkable {

    @Override
    @NotNull public String getIdentifier() {
        return ZOGLIN;
    }

    public EntityZoglin(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.3f, 40, true, 30), new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET), 3, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.3f, 40, true, 30), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER), 2, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, 0.3f, 40, true, 30), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), 2, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, true, 10), none(), 1, 1)
                ),
                Set.of(
                        new NearestPlayerSensor(40, 0, 20),
                        new NearestTargetEntitySensor<>(0, 16, 20,
                                List.of(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), this::attackTarget)
                ),
                Set.of(new WalkController(), new LookController(true, true)),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.diffHandDamage = new float[]{1f, 1f, 1f};
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.85f;
        }
        return 1.4f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.85f;
        }
        return 1.4f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(40);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.25f);
    }

    @Override
    public float[] getDiffHandDamage() {
        if(isBaby()) {
            return super.getDiffHandDamage();
        } else return new float[] {
                Utils.rand(2.5f, 5f),
                Utils.rand(3f, 8f),
                Utils.rand(4.5f, 12f),
        };
    }

    @Override
    public String getOriginalName() {
        return "Zoglin";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("zoglin", "zoglin_baby", "undead", "monster", "mob");
    }

    @Override
    public boolean isUndead() {
        return true;
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);
        int amount = Utils.rand(1, 3 + looting);

        return new Item[]{
                Item.get(Item.ROTTEN_FLESH, 0, amount)
        };
    }

    @Override
    public boolean attackTarget(Entity entity) {
        return (!(entity instanceof EntityZoglin) && entity instanceof EntityIntelligent);
    }

    @Override
    public Integer getExperienceDrops() {
        return isBaby() ? 0 : Utils.rand(1,3);
    }
}
