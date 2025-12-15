package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.RandomSoundEvaluator;
import cn.nukkit.entity.ai.executor.BowShootExecutor;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.PlaySoundExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestEntitySensor;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author PikyCZ
 */
public class EntityStray extends EntityMob implements EntityWalkable, EntitySmite {

    @Override
    @NotNull public String getIdentifier() {
        return STRAY;
    }

    public EntityStray(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(20);
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

    @Override
    public String getOriginalName() {
        return "Stray";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("stray", "skeleton", "monster", "mob", "undead");
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);
        List<Item> drops = new ArrayList<>();

        float boneChance = 0.66f + (0.12f * looting);
        boneChance = Math.min(boneChance, 1.0f);

        if (Utils.rand(0f, 1f) < boneChance) {
            int amount = Utils.rand(1, 2 + looting);
            drops.add(Item.get(Item.BONE, 0, amount));
        }

        float arrowChance = 0.55f + (0.10f * looting);
        arrowChance = Math.min(arrowChance, 1.0f);

        if (Utils.rand(0f, 1f) < arrowChance) {
            int amount = Utils.rand(1, 2 + looting);
            drops.add(Item.get(Item.ARROW, 0, amount));
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
    public boolean onUpdate(int currentTick) {
        burn(this);
        return super.onUpdate(currentTick);
    }

    @Override
    protected IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(new PlaySoundExecutor(Sound.MOB_SKELETON_SAY), new RandomSoundEvaluator(), 5, 1),
                        new Behavior(new BowShootExecutor(this::getItemInHand, CoreMemoryTypes.ATTACK_TARGET, 0.3f, 15, true, 30, 20), new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET), 4, 1),
                        new Behavior(new BowShootExecutor(this::getItemInHand, CoreMemoryTypes.NEAREST_GOLEM, 0.3f, 15, true, 30, 20), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_GOLEM), 3, 1),
                        new Behavior(new BowShootExecutor(this::getItemInHand, CoreMemoryTypes.NEAREST_PLAYER, 0.3f, 15, true, 30, 20), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER), 2, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, true, 10), none(), 1, 1)
                ),
                Set.of(
                        new NearestPlayerSensor(16, 0, 20),
                        new NearestEntitySensor(EntityGolem.class, CoreMemoryTypes.NEAREST_GOLEM, 42, 0)
                ),
                Set.of(new WalkController(), new LookController(true, true)),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

}
