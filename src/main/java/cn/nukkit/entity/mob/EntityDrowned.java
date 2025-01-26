package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.block.BlockTurtleEgg;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.entity.EntitySwimmable;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.DiveController;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.DistanceEvaluator;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.RandomSoundEvaluator;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.JumpExecutor;
import cn.nukkit.entity.ai.executor.MeleeAttackExecutor;
import cn.nukkit.entity.ai.executor.MoveToTargetExecutor;
import cn.nukkit.entity.ai.executor.NearestBlockIncementExecutor;
import cn.nukkit.entity.ai.executor.PlaySoundExecutor;
import cn.nukkit.entity.ai.executor.TridentThrowExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestBlockSensor;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.inventory.EntityInventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTrident;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author PetteriM1, Buddelbubi
 */
public class EntityDrowned extends EntityMob implements EntitySwimmable, EntityWalkable, EntitySmite {

    @Override
    @NotNull public String getIdentifier() {
        return DROWNED;
    }

    public EntityDrowned(IChunk chunk, CompoundTag nbt) {
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
                        new Behavior(new PlaySoundExecutor(Sound.MOB_DROWNED_SAY_WATER), all(new RandomSoundEvaluator(), entity -> isInsideOfWater()), 9, 1),
                        new Behavior(new PlaySoundExecutor(Sound.MOB_DROWNED_SAY), all(new RandomSoundEvaluator(), entity -> !isInsideOfWater()), 8, 1),
                        new Behavior(new JumpExecutor(), all(entity -> !getMemoryStorage().isEmpty(CoreMemoryTypes.NEAREST_BLOCK), entity -> entity.getCollisionBlocks().stream().anyMatch(block -> block instanceof BlockTurtleEgg)), 7, 1, 10),
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_BLOCK, 0.3f, true), new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_BLOCK), 6, 1),
                        new Behavior(new TridentThrowExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.3f, 15, true, 30, 20), all(new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET), new DistanceEvaluator(CoreMemoryTypes.ATTACK_TARGET, 40, 3), entity -> getItemInHand().getId().equals(Item.TRIDENT)), 5, 1),
                        new Behavior(new TridentThrowExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.3f, 15, false, 30, 20), all(new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER), new DistanceEvaluator(CoreMemoryTypes.NEAREST_PLAYER, 40, 3), entity -> getItemInHand().getId().equals(Item.TRIDENT)), 4, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.3f, 40, true, 30), new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET), 3, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.3f, 40, false, 30), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER), 2, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, false, 10), none(), 1, 1)
                ),
                Set.of(
                        new NearestPlayerSensor(40, 0, 0),
                        new NearestBlockSensor(11, 5, 20)
                ),
                Set.of(new WalkController(), new LookController(true, true)),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    public double getFloatingForceFactor() {
        if(any(
                new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET),
                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER),
                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_BLOCK)
        ).evaluate(this)) {
            if (hasWaterAt(this.getFloatingHeight())) {
                return 1.3;
            }
            return 0.7;
        }
        return 0;
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(20);
        this.diffHandDamage = new float[]{2.5f, 3f, 4.5f};
        super.initEntity();
        getMemoryStorage().put(CoreMemoryTypes.LOOKING_BLOCK, BlockTurtleEgg.class);
        getMemoryStorage().put(CoreMemoryTypes.ENABLE_DIVE_FORCE, true);
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
        return "Drowned";
    }

    @Override
    public Item[] getDrops() {
        Item trident = Item.AIR;
        if(getItemInHand() instanceof ItemTrident) {
            EntityDamageEvent event = getLastDamageCause();
            int lootingLevel = 0;
            if(event instanceof EntityDamageByEntityEvent entityEvent) {
                if(entityEvent.getDamager() instanceof EntityInventoryHolder holder) {
                    lootingLevel = holder.getItemInHand().getEnchantmentLevel(Enchantment.ID_LOOTING);
                }
            }
            if(ThreadLocalRandom.current().nextInt(1, 100) < Math.min(37, 25+lootingLevel)) {
                trident = Item.get(Item.TRIDENT);
            }
        }
        return new Item[]{
                Item.get(Item.ROTTEN_FLESH),
                trident
        };
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
        if(currentTick%20 == 0) {
            EntityZombie.pickupItems(this);
        }
        return super.onUpdate(currentTick);
    }

    @Override
    public Integer getExperienceDrops() {
        return isBaby() ? 7 : 5;
    }
}
