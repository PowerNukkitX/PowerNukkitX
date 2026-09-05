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
import org.powernukkitx.item.Item;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

/**
 * Base class for all fish.
 */
public abstract class EntityFish extends EntityAnimal implements EntitySwimmable {

    public EntityFish(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    //removing the stranded sound effect feels off
    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return BehaviorGroup.builder(this)
                .behaviors(
                        new Behavior(
                                new SpaceRandomRoamExecutor(0.36f, 12, 1, 80, false, -1, false, 10),
                                entity -> true, 1)
                )
                .controllers(new SpaceMoveController(), new LookController(true, true), new DiveController())
                .routeFinder(new SimpleSpaceAStarRouteFinder(new SwimmingPosEvaluator(), this))
                .build();
    }

    /**
     * Rolls the bone every fish shares, a 25% chance raised by 1% per looting level, giving
     * one bone plus a bonus of one to two per looting level.
     *
     * @param drops  the drop list to add the bone to
     * @param weapon the weapon the fish was killed with
     */
    protected void addBoneDrop(@NotNull List<Item> drops, @NotNull Item weapon) {
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);
        if (Utils.rand(0f, 1f) < 0.25f + (looting * 0.01f)) {
            drops.add(Item.get(Item.BONE, 0, Utils.rand(1 + looting, 1 + (looting * 2))));
        }
    }
}
