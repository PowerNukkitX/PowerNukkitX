package org.powernukkitx.entity.mob;

import org.powernukkitx.Player;
import org.powernukkitx.entity.EntitySmite;
import org.powernukkitx.entity.EntityWalkable;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.WalkController;
import org.powernukkitx.entity.ai.evaluator.EntityCheckEvaluator;
import org.powernukkitx.entity.ai.evaluator.RandomSoundEvaluator;
import org.powernukkitx.entity.ai.executor.BowShootExecutor;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.PlaySoundExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EntityBogged extends EntityMob implements EntityWalkable, EntitySmite {
    @Override
    @NotNull
    public String getIdentifier() {
        return BOGGED;
    }

    public EntityBogged(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        if (getItemInHand().isNull()) {
            setItemInHand(Item.get(ItemID.BOW));
        }
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.9f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(16);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.25f);
    }

    @Override
    public String getOriginalName() {
        return "Bogged";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("bogged", "skeleton", "monster", "mob", "undead");
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);
        List<Item> drops = new ArrayList<>();

        int boneAmount = Utils.rand(0, 2 + looting);
        if (boneAmount > 0) {
            drops.add(Item.get(Item.BONE, 0, boneAmount));
        }

        int arrowAmount = Utils.rand(0, 2 + looting);
        if (arrowAmount > 0) {
            drops.add(Item.get(Item.ARROW, 0, arrowAmount));
        }

        float poisonChance = 0.5f - (looting * (1f / 12f));
        if (poisonChance < 0f) {
            poisonChance = 0f;
        }

        if (Utils.rand(0f, 1f) < poisonChance) {
            int poisonAmount = Utils.rand(1, Math.min(1 + looting, 4));
            drops.add(Item.get(Item.ARROW, 27, poisonAmount));
        }

        return drops.toArray(Item.EMPTY_ARRAY);
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
    protected IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(new PlaySoundExecutor(Sound.MOB_SKELETON_SAY), new RandomSoundEvaluator(), 4, 1),
                        new Behavior(new BowShootExecutor(this::getItemInHand, CoreMemoryTypes.ATTACK_TARGET, 0.3f, 15, true, 35, 20), new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET), 3, 1),
                        new Behavior(new BowShootExecutor(this::getItemInHand, CoreMemoryTypes.NEAREST_PLAYER, 0.3f, 15, true, 35, 20), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER), 2, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, true, 10), none(), 1, 1)
                ),
                Set.of(new NearestPlayerSensor(16, 0, 20)),
                Set.of(new WalkController(), new LookController(true, true)),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }
}
