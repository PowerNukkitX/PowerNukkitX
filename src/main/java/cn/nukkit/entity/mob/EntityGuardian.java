package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntitySwimmable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.DiveController;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.SpaceMoveController;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.PassByTimeEvaluator;
import cn.nukkit.entity.ai.evaluator.RandomSoundEvaluator;
import cn.nukkit.entity.ai.executor.FleeFromTargetExecutor;
import cn.nukkit.entity.ai.executor.GuardianAttackExecutor;
import cn.nukkit.entity.ai.executor.PlaySoundExecutor;
import cn.nukkit.entity.ai.executor.SpaceRandomRoamExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleSpaceAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.SwimmingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.ai.sensor.NearestTargetEntitySensor;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author PikyCZ
 */
public class EntityGuardian extends EntityMob implements EntitySwimmable {

    @Override
    @NotNull public String getIdentifier() {
        return GUARDIAN;
    }

    public EntityGuardian(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(new PlaySoundExecutor(Sound.MOB_GUARDIAN_AMBIENT, 0.8f, 1.2f, 1, 1), all(entity -> isInsideOfWater(), new RandomSoundEvaluator()), 6, 1, 1, true),
                        new Behavior(new PlaySoundExecutor(Sound.MOB_GUARDIAN_LAND_IDLE, 0.8f, 1.2f, 1, 1), all(entity -> !isInsideOfWater(), new RandomSoundEvaluator()), 5, 1, 1, true),
                        new Behavior(new FleeFromTargetExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.5f, true, 9), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET),
                                new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, 0, 100)
                        ), 4, 1),
                        new Behavior(new GuardianAttackExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.3f, 15, true, 60, 40), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER),
                                entity -> entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_PLAYER) != null && !entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_PLAYER).isBlocking(),
                                entity -> entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_PLAYER) != null && getLevel().raycastBlocks(entity, entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_PLAYER)).stream().allMatch(Block::isTransparent)
                        ), 3, 1),
                        new Behavior(new GuardianAttackExecutor(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, 0.3f, 15, true, 60, 40), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), 2, 1),
                        new Behavior(new SpaceRandomRoamExecutor(0.36f, 12, 1, 80, false, -1, false, 10), none(), 1, 1)
                ),
                Set.of(new NearestPlayerSensor(40, 0, 20),
                        new NearestTargetEntitySensor<>(0, 16, 20,
                                List.of(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), this::attackTarget)
                ),
                Set.of(new SpaceMoveController(), new LookController(true, true), new DiveController()),
                new SimpleSpaceAStarRouteFinder(new SwimmingPosEvaluator(), this),
                this
        );
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if(source.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
            return false;
        }
        if(super.attack(source)) {
            if(source instanceof EntityDamageByEntityEvent e) {
                e.getDamager().attack(new EntityDamageByEntityEvent(this, source.getEntity(), EntityDamageEvent.DamageCause.THORNS, getServer().getDifficulty() == 3 ? 2 : 3));
            }
            return true;
        }
        return false;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(30);
        this.diffHandDamage = new float[]{4f, 6f, 9f};
        super.initEntity();
    }

    @Override
    public String getOriginalName() {
        return "Guardian";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("guardian", "monster", "mob");
    }

    @Override
    public float getWidth() {
        return 0.85f;
    }

    @Override
    public float getHeight() {
        return 0.85f;
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        int secondLoot = ThreadLocalRandom.current().nextInt(6);
        return new Item[]{
                Item.get(Item.PRISMARINE_SHARD, 0, Utils.rand(0, 2)),
                ThreadLocalRandom.current().nextInt(1000) <= 25 ? Item.get(Item.COD, 0, 1) : Item.AIR,
                secondLoot <= 2 ? Item.get(Item.COD, 0, Utils.rand(0, 1)) : Item.AIR,
                secondLoot > 2 && secondLoot <= 4 ? Item.get(Item.PRISMARINE_CRYSTALS, 0, Utils.rand(0, 1)) : Item.AIR
        };
    }

    @Override
    public Integer getExperienceDrops() {
        return 10;
    }

    @Override
    public boolean attackTarget(Entity entity) {
        return switch (entity.getIdentifier()) {
            case SQUID, GLOW_SQUID, AXOLOTL -> true;
            default -> false;
        };
    }
}
