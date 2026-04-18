package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.block.BlockTurtleEgg;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.evaluator.RandomSoundEvaluator;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.JumpExecutor;
import cn.nukkit.entity.ai.executor.MeleeAttackExecutor;
import cn.nukkit.entity.ai.executor.MoveToTargetExecutor;
import cn.nukkit.entity.ai.executor.NearestBlockIncementExecutor;
import cn.nukkit.entity.ai.executor.PlaySoundExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.MemorizedBlockSensor;
import cn.nukkit.entity.ai.sensor.NearestEntitySensor;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.components.HealthComponent;
import cn.nukkit.entity.components.MovementComponent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.utils.Utils;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author PikyCZ, Buddelbubi
 */
public class EntityZombiePigman extends EntityMob implements EntityWalkable, EntitySmite { //Mojang seems to kept the old name?
    @Override
    @NotNull
    public String getIdentifier() {
        return ZOMBIE_PIGMAN;
    }


    public EntityZombiePigman(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }


    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(
                        new Behavior(new NearestBlockIncementExecutor(), entity -> !getMemoryStorage().isEmpty(CoreMemoryTypes.NEAREST_BLOCK) && getMemoryStorage().get(CoreMemoryTypes.NEAREST_BLOCK) instanceof BlockTurtleEgg, 1, 1)
                ),
                Set.of(
                        new Behavior(new PlaySoundExecutor(Sound.MOB_ZOMBIEPIG_ZPIG, isBaby() ? 1.3f : 0.8f, isBaby() ? 1.7f : 1.2f, 1, 1), new RandomSoundEvaluator(), 7, 1),
                        new Behavior(new JumpExecutor(), all(entity -> !getMemoryStorage().isEmpty(CoreMemoryTypes.NEAREST_BLOCK), entity -> entity.getCollisionBlocks().stream().anyMatch(block -> block instanceof BlockTurtleEgg)), 6, 1, 10),
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_BLOCK, 0.3f, true), new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_BLOCK), 5, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.3f, 40, true, 30), new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET), 4, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.NEAREST_GOLEM, 0.3f, 40, true, 30), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_GOLEM), 3, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.3f, 40, false, 30), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER), 2, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, true, 10), none(), 1, 1)
                ),
                Set.of(
                        new NearestPlayerSensor(40, 0, 0),
                        new NearestEntitySensor(EntityGolem.class, CoreMemoryTypes.NEAREST_GOLEM, 42, 0),
                        new MemorizedBlockSensor(11, 5, 20)
                ),
                Set.of(new WalkController(), new LookController(true, true)),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    @Override
    public double getFloatingForceFactor() {
        return 0;
    }

    @Override
    protected void initEntity() {
        this.diffHandDamage = new float[]{2.5f, 3f, 4.5f};
        super.initEntity();
        getMemoryStorage().put(CoreMemoryTypes.LOOKING_BLOCK, BlockTurtleEgg.class);
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
        return HealthComponent.value(20);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.23f);
    }

    @Override
    public String getOriginalName() {
        return "Zombified Piglin";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("zombie_pigman", "undead", "monster", "mob");
    }

    @Override
    public boolean isUndead() {
        return true;
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return this.getDataFlag(ActorFlags.ANGRY);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        burn(this);
        if (currentTick % 20 == 0) {
            EntityZombie.pickupItems(this);
        }
        return super.onUpdate(currentTick);
    }

    @Override
    public Integer getExperienceDrops() {
        return isBaby() ? 7 : 5;
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);
        List<Item> drops = new ArrayList<>();

        int flesh = Utils.rand(0, 1 + looting);
        if (flesh > 0) {
            drops.add(Item.get(Item.ROTTEN_FLESH, 0, flesh));
        }

        int nuggets = Utils.rand(0, 1 + looting);
        if (nuggets > 0) {
            drops.add(Item.get(Item.GOLD_NUGGET, 0, nuggets));
        }

        Item hand = getEquipmentInventory().getItemInHand();
        if (!hand.isNull() && hand.getId() == Item.WARPED_FUNGUS_ON_A_STICK) {
            drops.add(hand.clone());
        }

        if (weapon != Item.AIR) {
            if (Utils.rand(0, 199) < (5 + looting)) {
                Item sword = Item.get(Item.GOLDEN_SWORD);
                drops.add(sword);
            }

            if (/* TODO: isPiglinBruteZoombified() &&*/ Utils.rand(0, 199) < (5 + looting)) {
                Item axe = Item.get(Item.GOLDEN_AXE);
                drops.add(axe);
            }

            if (hand.getId() == Item.CROSSBOW) {
                drops.add(hand.clone());
            }

            if (Utils.rand(0, 999) < (10 + looting * 5)) {
                drops.add(Item.get(Item.GOLD_INGOT, 0, 1));
            }
        }

        return drops.toArray(Item.EMPTY_ARRAY);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (source.getCause() == EntityDamageEvent.DamageCause.DROWNING) {
            return false;
        }
        return super.attack(source);
    }
}
