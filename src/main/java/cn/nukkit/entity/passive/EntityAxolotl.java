package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.block.BlockFlowingWater;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCanAttack;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntitySwimmable;
import cn.nukkit.entity.EntityVariant;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.ConditionalController;
import cn.nukkit.entity.ai.controller.DiveController;
import cn.nukkit.entity.ai.controller.FluctuateController;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.SpaceMoveController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.DistanceEvaluator;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.evaluator.PassByTimeEvaluator;
import cn.nukkit.entity.ai.evaluator.ProbabilityEvaluator;
import cn.nukkit.entity.ai.evaluator.RandomSoundEvaluator;
import cn.nukkit.entity.ai.executor.EntityBreedingExecutor;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.InLoveExecutor;
import cn.nukkit.entity.ai.executor.LookAtTargetExecutor;
import cn.nukkit.entity.ai.executor.MeleeAttackExecutor;
import cn.nukkit.entity.ai.executor.MoveToTargetExecutor;
import cn.nukkit.entity.ai.executor.PlaySoundExecutor;
import cn.nukkit.entity.ai.executor.SpaceRandomRoamExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.ConditionalAStarRouteFinder;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.finder.impl.SimpleSpaceAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.SwimmingPosEvaluator;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.BlockSensor;
import cn.nukkit.entity.ai.sensor.NearestFeedingPlayerSensor;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.ai.sensor.NearestTargetEntitySensor;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;
import it.unimi.dsi.fastutil.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class EntityAxolotl extends EntityAnimal implements EntitySwimmable, EntityVariant, EntityCanAttack {
    @Override
    @NotNull public String getIdentifier() {
        return AXOLOTL;
    }

    private final static int[] VARIANTS = new int[] {0, 1, 2, 3, 4};

    private final static float[] DIFF_DAMAGE = new float[] {2, 2, 2};

    public EntityAxolotl(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(
                        //用于刷新InLove状态的核心行为
                        new Behavior(
                                new InLoveExecutor(400),
                                all(
                                        new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_FEED_TIME, 0, 400),
                                        new PassByTimeEvaluator(CoreMemoryTypes.LAST_IN_LOVE_TIME, 6000, Integer.MAX_VALUE)
                                ),
                                1, 1, 1, false
                        ),
                        new Behavior(entity -> {
                            setMoveTarget(getMemoryStorage().get(CoreMemoryTypes.NEAREST_BLOCK));
                            return true;
                        }, all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_BLOCK),
                                entity -> !isInsideOfWater(),
                                not(new DistanceEvaluator(CoreMemoryTypes.NEAREST_BLOCK, 9))
                        ), 1, 1)
                ),
                Set.of(
                        new Behavior(new PlaySoundExecutor(Sound.MOB_AXOLOTL_SPLASH), all(
                                entity -> getAirTicks() == 399
                        ), 7, 1),
                        new Behavior(new PlaySoundExecutor(Sound.MOB_AXOLOTL_IDLE_WATER), all(new RandomSoundEvaluator(), entity -> isInsideOfWater()), 7, 1),
                        new Behavior(new PlaySoundExecutor(Sound.MOB_AXOLOTL_IDLE), all(new RandomSoundEvaluator(), entity -> !isInsideOfWater()), 6, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, 0.3f, 17, true, 30), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), 5, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.4f, 12, 40, true, 100, true, 10), new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, 0, 100), 4, 1),
                        new Behavior(new EntityBreedingExecutor<>(EntityAxolotl.class, 16, 100, 0.5f), entity -> entity.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE), 3, 1),
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_FEEDING_PLAYER, 0.4f, true), new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_FEEDING_PLAYER), 2, 1),
                        new Behavior(new LookAtTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 100), new ProbabilityEvaluator(4, 10), 1, 1, 100),
                        new Behavior(new FlatRandomRoamExecutor(0.2f, 12, 100, false, -1, false, 10), entity -> !entity.isInsideOfWater(), 1, 1),
                        new Behavior(new SpaceRandomRoamExecutor(0.36f, 12, 1, 80, false, -1, false, 10), Entity::isInsideOfWater, 1, 1)
                ),
                Set.of(
                        new NearestFeedingPlayerSensor(8, 0),
                        new NearestPlayerSensor(8, 0, 20),
                        new NearestTargetEntitySensor<>(0, 16, 20,
                                List.of(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), this::attackTarget),
                        new BlockSensor(BlockFlowingWater.class, CoreMemoryTypes.NEAREST_BLOCK, 16, 5, 10),
                        entity -> {
                            if(getLevel().getTick() % 20 == 0) {
                                Entity lastAttack = getMemoryStorage().get(CoreMemoryTypes.LAST_ATTACK_ENTITY);
                                if(lastAttack != null) {
                                    if(!lastAttack.isAlive()) {
                                        if(lastAttack instanceof EntityIntelligent intelligent) {
                                            if(intelligent.getLastDamageCause() instanceof EntityDamageByEntityEvent event) {
                                                if(event.getDamager() instanceof Player player) {
                                                    player.removeEffect(EffectType.MINING_FATIGUE);
                                                    player.addEffect(Effect.get(EffectType.REGENERATION).setDuration((player.hasEffect(EffectType.REGENERATION) ? player.getEffect(EffectType.REGENERATION).getDuration() : 0) + 100));
                                                }
                                            }
                                        }
                                        getMemoryStorage().clear(CoreMemoryTypes.LAST_ATTACK_ENTITY);
                                    }
                                }
                            }
                        }
                ),
                Set.of(new LookController(true, true),
                        new ConditionalController(Pair.of(Entity::isInsideOfWater, new DiveController()), Pair.of(Entity::isInsideOfWater, new SpaceMoveController()), Pair.of(entity -> !entity.isInsideOfWater(), new WalkController()), Pair.of(entity -> !entity.isInsideOfWater(), new FluctuateController()))),
                new ConditionalAStarRouteFinder(this,
                        Pair.of(ent -> !ent.isInsideOfWater(), new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this)),
                        Pair.of(Entity::isInsideOfWater, new SimpleSpaceAStarRouteFinder(new SwimmingPosEvaluator(), this))),
                this
        );
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if(Objects.equals(item.getId(), Item.WATER_BUCKET)) {
            Item bucket = Item.get(Item.AXOLOTL_BUCKET);
            CompoundTag tag = new CompoundTag();
            tag.putInt("Variant", getVariant());
            bucket.setCompoundTag(tag);
            player.getInventory().setItemInHand(bucket);
            this.close();
        }
        return super.onInteract(player, item, clickedPos);
    }

    @Override
    public float getHeight() {
        return 0.42f;
    }

    @Override
    public float getWidth() {
        return 0.75f;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if(source.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION && getLevelBlock().canPassThrough()) {
            if(getAirTicks() > -5600 || getLevel().isRaining() || getLevel().isThundering()) return false;
        }
        return super.attack(source);
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(14);
        super.initEntity();
        if(!hasVariant()) {
            setVariant(randomVariant());
        }
    }

    @Override
    public String getOriginalName() {
        return "Axolotl";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("axolotl", "mob");
    }

    @Override
    public boolean isBreedingItem(Item item) {
        return item.getId().equals(Item.TROPICAL_FISH_BUCKET);
    }

    @Override
    protected boolean useBreedingItem(Player player, Item item) {
        getMemoryStorage().put(CoreMemoryTypes.LAST_FEED_PLAYER, player);
        getMemoryStorage().put(CoreMemoryTypes.LAST_BE_FEED_TIME, getLevel().getTick());
        sendBreedingAnimation(item);
        return player.getInventory().setItemInHand(Item.get(Item.WATER_BUCKET));
    }

    @Override
    public int[] getAllVariant() {
        return VARIANTS;
    }

    @Override
    public int randomVariant() {
        if(Utils.rand(0, 1200) == 0) return VARIANTS[VARIANTS.length-1];
        return VARIANTS[Utils.rand(0, VARIANTS.length-2)];
    }

    @Override
    public float[] getDiffHandDamage() {
        return DIFF_DAMAGE;
    }

    @Override
    public Integer getExperienceDrops() {
        return 1;
    }

    @Override
    public boolean attackTarget(Entity entity) {
        return switch (entity.getIdentifier()) {
            case Entity.COD,
                 Entity.ELDER_GUARDIAN,
                 Entity.GLOW_SQUID,
                 Entity.GUARDIAN,
                 Entity.PUFFERFISH,
                 Entity.SALMON,
                 Entity.TADPOLE,
                 Entity.TROPICALFISH,
                 Entity.DROWNED -> true;
            default -> false;
        };
    }
}
