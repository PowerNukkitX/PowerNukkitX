package org.powernukkitx.entity.passive;

import org.powernukkitx.entity.EntitySwimmable;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.DiveController;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.SpaceMoveController;
import org.powernukkitx.entity.ai.executor.SpaceRandomRoamExecutor;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleSpaceAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.SwimmingPosEvaluator;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * @author PetteriM1
 */
public class EntityDolphin extends EntityAnimal implements EntitySwimmable {
    @Override
    @NotNull public String getIdentifier() {
        return DOLPHIN;
    }
    

    public EntityDolphin(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(
                                new SpaceRandomRoamExecutor(0.36f, 12, 1, 80, false, -1, false, 10),
                                entity -> true, 1)
                ),
                Set.of(),
                Set.of(new SpaceMoveController(), new LookController(true, true), new DiveController()),
                new SimpleSpaceAStarRouteFinder(new SwimmingPosEvaluator(), this),
                this
        );
    }


    @Override
    public String getOriginalName() {
        return "Dolphin";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("dolphin", "mob");
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
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(10);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.1f);
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        if (Utils.rand(0f, 1f) >= 0.5f) {
            return Item.EMPTY_ARRAY;
        }

        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);

        int amount = Utils.rand(0, 1 + looting);
        if (amount <= 0) {
            return Item.EMPTY_ARRAY;
        }

        return new Item[]{
                Item.get(this.isOnFire() ? Item.COOKED_COD : Item.COD, 0, amount)
        };
    }

    @Override
    public Integer getExperienceDrops() {
        return 0;
    }
}
