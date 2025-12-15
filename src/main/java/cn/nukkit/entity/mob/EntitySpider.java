package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityArthropod;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.RandomSoundEvaluator;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.FleeFromTargetExecutor;
import cn.nukkit.entity.ai.executor.MeleeAttackExecutor;
import cn.nukkit.entity.ai.executor.PlaySoundExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestEntitySensor;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.ai.sensor.NearestTargetEntitySensor;
import cn.nukkit.entity.passive.EntityArmadillo;
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
public class EntitySpider extends EntityMob implements EntityWalkable, EntityArthropod {
    @Override
    @NotNull public String getIdentifier() {
        return SPIDER;
    }
    

    public EntitySpider(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(new PlaySoundExecutor(Sound.MOB_SPIDER_SAY), new RandomSoundEvaluator(), 6, 1),
                        new Behavior(new FleeFromTargetExecutor(CoreMemoryTypes.NEAREST_SHARED_ENTITY, 0.3f, true, 9), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SHARED_ENTITY), 5, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.3f, 40, true, 30), new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET), 4, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, 0.3f, 40, true, 30), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), 3, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.3f, 40, false, 30), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER), 2, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, true, 10), none(), 1, 1)
                ),
                Set.of(
                        new NearestPlayerSensor(40, 0, 20),
                        new NearestTargetEntitySensor<>(0, 16, 20,
                                List.of(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), this::attackTarget),
                        new NearestEntitySensor(EntityArmadillo.class, CoreMemoryTypes.NEAREST_SHARED_ENTITY, 42, 0)
                ),
                Set.of(new WalkController(), new LookController(true, true)),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(16);
        this.diffHandDamage = new float[]{2.5f, 3f, 4.5f};
        super.initEntity();
    }

    @Override
    public float getWidth() {
        return 1.4f;
    }

    @Override
    public float getHeight() {
        return 0.9f;
    }

    @Override
    public String getOriginalName() {
        return "Spider";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("spider", "monster", "mob", "arthropod");
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);
        List<Item> drops = new ArrayList<>();

        float stringChance = 0.70f + (0.10f * looting);
        stringChance = Math.min(stringChance, 1.0f);

        if (Utils.rand(0f, 1f) < stringChance) {
            int amount = Utils.rand(1, 2 + looting);
            drops.add(Item.get(Item.STRING, 0, amount));
        }

        float eyeChance = 0.50f + (0.05f * looting);
        eyeChance = Math.min(eyeChance, 1.0f);

        if (Utils.rand(0f, 1f) < eyeChance) {
            int amount = Utils.rand(1, 1 + looting);
            drops.add(Item.get(Item.SPIDER_EYE, 0, amount));
        }

        return drops.toArray(Item.EMPTY_ARRAY);
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }

    @Override
    public boolean attackTarget(Entity entity) {
        return entity instanceof EntityGolem;
    }
}
