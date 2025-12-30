package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityAgeable;
import cn.nukkit.entity.EntityCanAttack;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityVariant;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.FluctuateController;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.DistanceEvaluator;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.IBehaviorEvaluator;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.evaluator.PassByTimeEvaluator;
import cn.nukkit.entity.ai.evaluator.ProbabilityEvaluator;
import cn.nukkit.entity.ai.evaluator.RandomSoundEvaluator;
import cn.nukkit.entity.ai.executor.EntityBreedingExecutor;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.FleeFromTargetExecutor;
import cn.nukkit.entity.ai.executor.InLoveExecutor;
import cn.nukkit.entity.ai.executor.LookAtTargetExecutor;
import cn.nukkit.entity.ai.executor.MeleeAttackExecutor;
import cn.nukkit.entity.ai.executor.MoveToTargetExecutor;
import cn.nukkit.entity.ai.executor.PlaySoundExecutor;
import cn.nukkit.entity.ai.executor.panda.EatingExecutor;
import cn.nukkit.entity.ai.executor.panda.LayingExecutor;
import cn.nukkit.entity.ai.executor.panda.RollExecutor;
import cn.nukkit.entity.ai.executor.panda.ShakeExecutor;
import cn.nukkit.entity.ai.executor.panda.SittingExecutor;
import cn.nukkit.entity.ai.executor.panda.SneezingExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestFeedingPlayerSensor;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.ai.sensor.NearestTargetEntitySensor;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.mob.EntityEnderDragon;
import cn.nukkit.entity.mob.EntityGhast;
import cn.nukkit.entity.mob.EntityMagmaCube;
import cn.nukkit.entity.mob.EntityMob;
import cn.nukkit.entity.mob.EntityPhantom;
import cn.nukkit.entity.mob.EntityShulker;
import cn.nukkit.entity.mob.EntitySlime;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.inventory.EntityEquipmentInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.TakeItemEntityPacket;
import cn.nukkit.utils.Utils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class EntityPanda extends EntityAnimal implements EntityWalkable, EntityCanAttack, EntityVariant, InventoryHolder {
    @Override
    @NotNull public String getIdentifier() {
        return PANDA;
    }

    @Getter
    protected float[] diffHandDamage;
    private final static int[] VARIANTS = new int[] {0, 1, 2, 3, 4, 5, 6};

    public static final int NORMAL = 0;
    public static final int LAZY = 1;
    public static final int WORRIED = 2;
    public static final int PLAYFUL = 3;
    public static final int BROWN = 4;
    public static final int WEAK = 5;
    public static final int AGRESSIVE = 6;

    private EntityEquipmentInventory inventory = new EntityEquipmentInventory(this);

    public EntityPanda(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(
                        //用于刷新InLove状态的核心行为
                        new Behavior(entity -> {
                            entity.getMemoryStorage().put(CoreMemoryTypes.ATTACK_TARGET, ((EntityDamageByEntityEvent) entity.getMemoryStorage().get(CoreMemoryTypes.BE_ATTACKED_EVENT)).getDamager());
                            entity.setDataFlag(EntityFlag.ANGRY, true);
                            return true;
                        }, all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.BE_ATTACKED_EVENT),
                                entity -> entity.getMemoryStorage().get(CoreMemoryTypes.BE_ATTACKED_EVENT) instanceof EntityDamageByEntityEvent,
                                new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, 0, 1),
                                entity -> getServer().getDifficulty() != 0
                        ), 3, 1),
                        new Behavior(
                                new InLoveExecutor(400),
                                all(
                                        new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_FEED_TIME, 0, 400),
                                        new PassByTimeEvaluator(CoreMemoryTypes.LAST_IN_LOVE_TIME, 6000, Integer.MAX_VALUE)
                                ),
                                2, 1, 1, false
                        ),
                        new Behavior(new PlaySoundExecutor(Sound.MOB_PANDA_IDLE_AGGRESSIVE), all(new RandomSoundEvaluator(), entity -> getVariant() == AGRESSIVE), 1, 1),
                        new Behavior(new PlaySoundExecutor(Sound.MOB_PANDA_IDLE_WORRIED), all(new RandomSoundEvaluator(), entity -> getVariant() == WORRIED, entity -> getLevel().isThundering()), 1, 1),
                        new Behavior(new PlaySoundExecutor(Sound.MOB_PANDA_IDLE), new RandomSoundEvaluator(), 1, 1)
                ),
                Set.of(
                        new Behavior(new PandaAttackEecutor(), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET),
                                new DistanceEvaluator(CoreMemoryTypes.ATTACK_TARGET, 16)
                        ), 14, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.4f, 12, 40, true, 100, true, 10), new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, 0, 100), 13, 1),
                        new Behavior(new EntityBreedingExecutor<>(EntityPanda.class, 16, 100, 0.5f), entity -> entity.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE), 12, 1),
                        new Behavior(new EatingExecutor(), entity -> !getInventory().isEmpty(), 11, 1),
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_ITEM, 0.4f, true), new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_ITEM), 10, 1),
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_FEEDING_PLAYER, 0.4f, true), new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_FEEDING_PLAYER), 9, 1),
                        new Behavior(new RollExecutor(), all(
                                any(
                                        entity -> getVariant() == PLAYFUL,
                                        EntityAgeable::isBaby
                                ),
                                new ProbabilityEvaluator(1, getVariant() == PLAYFUL ? 1300 : 16000)
                        ), 8, 1),
                        new Behavior(new ShakeExecutor(), all(
                                entity -> getVariant() == WORRIED,
                                entity -> getLevel().isThundering()
                        ), 7, 1),
                        new Behavior(new FleeFromTargetExecutor(CoreMemoryTypes.NEAREST_SHARED_ENTITY, 0.4f, true, 2), all(
                                entity -> getVariant() == WORRIED,
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_SHARED_ENTITY),
                                new DistanceEvaluator(CoreMemoryTypes.NEAREST_SHARED_ENTITY, 2.1)
                        ), 6,1,  1),
                        new Behavior(new SneezingExecutor(), all(
                                EntityAgeable::isBaby,
                                new ProbabilityEvaluator(1, getVariant() == WEAK ? 500 : 6000)
                        ), 5, 1, 1),
                        new Behavior(new LayingExecutor(10), all(
                                entity -> getVariant() == LAZY,
                                new PandaSittingEvaluator(30)
                        ), 4,2,  1),
                        new Behavior(new SittingExecutor(10), new PandaSittingEvaluator(30), 3,2,  1),
                        new Behavior(new LookAtTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 100), new ProbabilityEvaluator(2, 10), 7, 1, 100),
                        new Behavior(new FlatRandomRoamExecutor(0.2f, 12, 100, false, -1, true, 10), (entity -> true), 1, 1)
                ),
                Set.of(new NearestFeedingPlayerSensor(16, 0), new NearestPlayerSensor(16, 0, 20), new NearestTargetEntitySensor<>(0, 16, 20,
                        List.of(CoreMemoryTypes.NEAREST_SHARED_ENTITY), entity -> (entity instanceof EntityMob mob && !(mob instanceof EntitySlime) && !(mob instanceof EntityMagmaCube) && !(mob instanceof EntityGhast) && !(mob instanceof EntityShulker) && !(mob instanceof EntityPhantom) && !(mob instanceof EntityEnderDragon)) || entity instanceof Player),
                        new NearestItemSensor(16,0)),
                Set.of(new WalkController(), new LookController(true, true), new FluctuateController()),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    public int getVariant() {
        return isInitialized() ? getMemoryStorage().get(CoreMemoryTypes.VARIANT) : 0;
    }

    @Override
    public float getWidth() {
        return 1.7f;
    }

    @Override
    public float getHeight() {
        return 1.5f;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if(currentTick % 20 == 0) {
            for (Entity entity : this.level.getNearbyEntities(this.boundingBox, this)) {
                if(entity == null) continue;
                if (!entity.isAlive() || !this.isAlive()) {
                    continue;
                }
                if(entity instanceof EntityItem entityItem) {
                    Item item = entityItem.getItem();
                    if(item.getId().equals(Block.BAMBOO)) {
                        getInventory().addItem(item);
                        TakeItemEntityPacket pk = new TakeItemEntityPacket();
                        pk.entityId = getId();
                        pk.target = entityItem.getId();
                        Server.broadcastPacket(getViewers().values(), pk);
                        entityItem.close();
                    }
                }
            }
        }
        return super.onUpdate(currentTick);
    }

    @Override
    public void initEntity() {
        this.diffHandDamage = new float[]{4f, 6f, 9f};
        super.initEntity();
        if (!hasVariant()) {
            this.setVariant(randomVariant());
        }
        getMemoryStorage().put(CoreMemoryTypes.LAST_SITTING_CHECK, getLevel().getTick());
        this.setMaxHealth(getVariant() == WEAK ? 10 : 20);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if(super.attack(source)) {
            if(source instanceof EntityDamageByEntityEvent event) {
                if(event.getDamager() instanceof Player player) {
                    Arrays.stream(getLevel().getCollidingEntities(getBoundingBox().grow(10, 5, 10))).filter(entity -> entity instanceof EntityPanda panda && panda.getVariant() == AGRESSIVE).map(entity -> (EntityPanda) entity).forEach(entity -> {
                                entity.getMemoryStorage().put(CoreMemoryTypes.ATTACK_TARGET, player);
                                entity.setDataFlag(EntityFlag.ANGRY, true);
                            }
                    );
                }
            }
            return true;
        } else return false;
    }

    @Override
    public String getOriginalName() {
        return "Panda";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("panda" , "panda_aggressive", "mob");
    }

    @Override
    public boolean isBreedingItem(Item item) {
        return item.getId().equals(Block.BAMBOO);
    }

    @Override
    public boolean attackTarget(Entity entity) {
        return false;
    }

    @Override
    public int[] getAllVariant() {
        return VARIANTS;
    }

    @Override
    public int randomVariant() {
        int random = Utils.rand(0, 16);
        if(random < 5) return NORMAL;
        if(random < 6) return LAZY;
        if(random < 7) return WORRIED;
        if(random < 8) return PLAYFUL;
        if(random < 10) return BROWN;
        if(random < 15) return WEAK;
        if(random < 16) return AGRESSIVE;
        return 0;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        return new Item[] {
          Item.get(Block.BAMBOO, 0, Utils.rand(0, 3))
        };
    }

    private class PandaAttackEecutor extends MeleeAttackExecutor {

        public PandaAttackEecutor() {
            super(CoreMemoryTypes.ATTACK_TARGET, 0.5f, 16, true, 20);
        }

        @Override
        protected void playAttackAnimation(EntityIntelligent entity) {
            super.playAttackAnimation(entity);
            if(entity instanceof EntityVariant variant) {
                if(variant.getVariant() != AGRESSIVE) {
                    stop(entity);
                }
                entity.getLevel().addSound(entity, Sound.MOB_PANDA_BITE);
            }
        }

        public void stop(EntityIntelligent entity) {
            entity.getMemoryStorage().clear(CoreMemoryTypes.ATTACK_TARGET);
            entity.setDataFlag(EntityFlag.ANGRY, false);
        }

        @Override
        public void onStop(EntityIntelligent entity) {
            super.onStop(entity);
            stop(entity);
        }

        @Override
        public void onInterrupt(EntityIntelligent entity) {
            super.onInterrupt(entity);
            stop(entity);
        }
    }

    public class PandaSittingEvaluator implements IBehaviorEvaluator {

        private final int cooldown;

        public PandaSittingEvaluator(int cooldown) {
            this.cooldown = cooldown*20;
        }

        @Override
        public boolean evaluate(EntityIntelligent entity) {
            boolean evaluate = any(
                    all(
                            entity1 -> {
                                if(getLevel().getTick() - getMemoryStorage().get(CoreMemoryTypes.LAST_SITTING_CHECK) > cooldown) {
                                    getMemoryStorage().put(CoreMemoryTypes.LAST_SITTING_CHECK, getLevel().getTick());
                                    return true;
                                } else return false;
                            },
                            new ProbabilityEvaluator(3, 10)
                    ),
                    entity1 -> getDataFlag(EntityFlag.SITTING)
            ).evaluate(entity);
            return evaluate;
        }
    }

    private class NearestItemSensor extends cn.nukkit.entity.ai.sensor.NearestItemSensor {

        public NearestItemSensor(double range, double minRange) {
            super(range, minRange);
        }

        @Override
        public void sense(EntityIntelligent entity) {

            EntityItem item = null;
            double rangeSquared = this.range * this.range;
            double minRangeSquared = this.minRange * this.minRange;
            //寻找范围内最近的玩家
            for (Entity e : entity.getLevel().getEntities()) {
                if(e instanceof EntityItem entityItem) {
                    if(entityItem.getItem().getId().equals(Block.BAMBOO)) {
                        if (entity.distanceSquared(e) <= rangeSquared && entity.distanceSquared(e) >= minRangeSquared) {
                            if (item == null) {
                                item = entityItem;
                            } else {
                                if (entity.distanceSquared(entityItem) < entity.distanceSquared(item)) {
                                    item = entityItem;
                                }
                            }
                        }
                    }
                }
            }
            entity.getMemoryStorage().put(CoreMemoryTypes.NEAREST_ITEM, item);
        }
    }

}
