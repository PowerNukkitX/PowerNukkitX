package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDoor;
import cn.nukkit.block.BlockSoulFire;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.evaluator.PassByTimeEvaluator;
import cn.nukkit.entity.ai.evaluator.RandomSoundEvaluator;
import cn.nukkit.entity.ai.executor.CrossBowShootExecutor;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.FleeFromTargetExecutor;
import cn.nukkit.entity.ai.executor.MeleeAttackExecutor;
import cn.nukkit.entity.ai.executor.PiglinTradeExecutor;
import cn.nukkit.entity.ai.executor.PiglinTransformExecutor;
import cn.nukkit.entity.ai.executor.PlaySoundExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.BlockSensor;
import cn.nukkit.entity.ai.sensor.NearestEntitySensor;
import cn.nukkit.entity.ai.sensor.NearestPlayerAngryPiglinSensor;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.ai.sensor.NearestTargetEntitySensor;
import cn.nukkit.entity.data.EntityDataTypes;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.inventory.EntityInventoryHolder;
import cn.nukkit.inventory.InventorySlice;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemCrossbow;
import cn.nukkit.item.ItemGoldIngot;
import cn.nukkit.item.ItemPorkchop;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AnimateEntityPacket;
import cn.nukkit.network.protocol.types.LevelSoundEvent;
import cn.nukkit.network.protocol.TakeItemEntityPacket;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class EntityPiglin extends EntityMob implements EntityWalkable {
    @Override
    @NotNull public String getIdentifier() {
        return PIGLIN;
    }

    public EntityPiglin(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(new PiglinTransformExecutor(), all(
                                entity -> entity.getLevel().getDimension() != Level.DIMENSION_NETHER,
                                entity -> !isImmobile(),
                                entity -> !entity.namedTag.getBoolean("IsImmuneToZombification")
                        ), 13, 1),
                        new Behavior(new PiglinTradeExecutor(), all(
                                entity -> !getItemInOffhand().isNull(),
                                entity -> !isAngry(),
                                not(
                                        all(
                                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.LAST_BE_ATTACKED_TIME),
                                                entity -> entity.getLevel().getTick() - getMemoryStorage().get(CoreMemoryTypes.LAST_BE_ATTACKED_TIME) <= 1
                                        )
                                )
                        ), 12, 1),
                        new Behavior(new PlaySoundExecutor(Sound.MOB_PIGLIN_JEALOUS, 0.8f, 1.2f, 0.8f, 0.8f), all(new RandomSoundEvaluator(), entity -> getViewers().values().stream().noneMatch(p -> p.distance(entity) < 8 && likesItem(p.getInventory().getItemInHand()) && p.level.raycastBlocks(p, entity).isEmpty())), 12, 1),
                        new Behavior(new PlaySoundExecutor(Sound.MOB_PIGLIN_ANGRY, 0.8f, 1.2f, 0.8f, 0.8f), all(new RandomSoundEvaluator(), entity -> isAngry()), 11, 1),
                        new Behavior(new PlaySoundExecutor(Sound.MOB_PIGLIN_AMBIENT, 0.8f, 1.2f, 0.8f, 0.8f), all(new RandomSoundEvaluator(), entity -> !isAngry()), 10, 1),
                        new Behavior(new CrossBowShootExecutor(this::getItemInHand, CoreMemoryTypes.ATTACK_TARGET, 0.3f, 15, true, 30, 80), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET),
                                entity -> getItemInHand() instanceof ItemCrossbow
                        ), 9, 1),
                        new Behavior(new CrossBowShootExecutor(this::getItemInHand, CoreMemoryTypes.NEAREST_PLAYER, 0.3f, 15, true, 30, 80), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER),
                                entity -> getMemoryStorage().get(CoreMemoryTypes.NEAREST_PLAYER) instanceof Player player && player.getInventory() != null && !Arrays.stream(player.getInventory().getArmorContents()).anyMatch(item -> !item.isNull() && item.isWearable() && item.getTier() == Item.WEARABLE_TIER_GOLD),
                                entity -> getItemInHand() instanceof ItemCrossbow
                        ), 8, 1),
                        new Behavior(new CrossBowShootExecutor(this::getItemInHand, CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, 0.3f, 15, true, 30, 80), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET),
                                entity -> !entity.namedTag.getBoolean("CannotHunt"),
                                entity -> getItemInHand() instanceof ItemCrossbow,
                                any(
                                        entity -> getMemoryStorage().get(CoreMemoryTypes.LAST_HOGLIN_ATTACK_TIME) == 0,
                                        new PassByTimeEvaluator(CoreMemoryTypes.LAST_HOGLIN_ATTACK_TIME, 6000)
                                )
                        ), 7, 1),
                        new Behavior(new PiglinMeleeAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.5f, 40, true, 30), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET)
                        ), 6, 1),
                        new Behavior(new PiglinMeleeAttackExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.5f, 40, false, 30), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER),
                                entity -> getMemoryStorage().get(CoreMemoryTypes.NEAREST_PLAYER) instanceof Player player && player.getInventory() != null && !Arrays.stream(player.getInventory().getArmorContents()).anyMatch(item -> !item.isNull() && item.isWearable() && item.getTier() == Item.WEARABLE_TIER_GOLD)
                        ), 5, 1),
                        new Behavior(new PiglinMeleeAttackExecutor(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, 0.5f, 40, true, 30), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET),
                                entity -> !entity.namedTag.getBoolean("CannotHunt"),
                                any(
                                        entity -> getMemoryStorage().get(CoreMemoryTypes.LAST_HOGLIN_ATTACK_TIME) == 0,
                                        new PassByTimeEvaluator(CoreMemoryTypes.LAST_HOGLIN_ATTACK_TIME, 6000)
                                )
                        ), 5, 1),
                        new Behavior(new PiglinFleeFromTargetExecutor(CoreMemoryTypes.NEAREST_BLOCK), all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_BLOCK),
                                entity -> getMemoryStorage().get(CoreMemoryTypes.NEAREST_BLOCK) instanceof BlockSoulFire
                        ), 2, 1),
                        new Behavior(new PiglinFleeFromTargetExecutor(CoreMemoryTypes.NEAREST_SHARED_ENTITY), all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_SHARED_ENTITY),
                                entity -> {
                                    if(isBaby()) {
                                        return true;
                                    } else {
                                        Entity entity1 = getMemoryStorage().get(CoreMemoryTypes.NEAREST_SHARED_ENTITY);
                                        if(entity1 instanceof EntityWither || entity1 instanceof EntityWitherSkeleton) return false;
                                        return true;
                                    }
                                }
                        ), 3, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, true, 10), none(), 1, 1)
                ),
                Set.of(new NearestPlayerSensor(40, 0, 20),
                        new NearestTargetEntitySensor<>(0, 16, 20,
                                List.of(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), this::attackTarget),
                        new NearestPlayerAngryPiglinSensor(),
                        new NearestEntitySensor(EntityZombiePigman.class, CoreMemoryTypes.NEAREST_SHARED_ENTITY, 8 , 0),
                        new NearestEntitySensor(EntityZoglin.class, CoreMemoryTypes.NEAREST_SHARED_ENTITY, 8 , 0),
                        new NearestEntitySensor(EntityWither.class, CoreMemoryTypes.NEAREST_SHARED_ENTITY, 8 , 0),
                        new NearestEntitySensor(EntityWitherSkeleton.class, CoreMemoryTypes.NEAREST_SHARED_ENTITY, 8 , 0),
                        new BlockSensor(BlockDoor.class, CoreMemoryTypes.NEAREST_BLOCK, 2, 2, 20),
                        new BlockSensor(BlockSoulFire.class, CoreMemoryTypes.NEAREST_BLOCK, 8, 2, 20)
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
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if(getItemInOffhand().isNull() && !isAngry()) {
            if(item instanceof ItemGoldIngot) {
                if(player.getGamemode() != Player.CREATIVE) player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
                setItemInOffhand(Item.get(Item.GOLD_INGOT));
            }
        }
        return super.onInteract(player, item);
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(16);
        this.diffHandDamage = new float[]{3f, 5f, 7f};
        super.initEntity();
        if(!isBaby()) setItemInHand(Item.get(Utils.rand() ? Item.GOLDEN_SWORD : Item.CROSSBOW));
        if(Utils.rand(0,10) == 0) setHelmet(Item.get(Item.GOLDEN_HELMET));
        if(Utils.rand(0,10) == 0) setChestplate(Item.get(Item.GOLDEN_CHESTPLATE));
        if(Utils.rand(0,10) == 0) setLeggings(Item.get(Item.GOLDEN_LEGGINGS));
        if(Utils.rand(0,10) == 0) setBoots(Item.get(Item.GOLDEN_BOOTS));
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if(currentTick%20 == 0) {
            if(getLevel().getGameRules().getBoolean(GameRule.MOB_GRIEFING)) {
                pickupItems(this);
            }
        }
        return super.onUpdate(currentTick);
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
        return "Piglin";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("piglin", "mob");
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return !this.isBaby();
    }

    public boolean isAngry() {
        return getDataFlag(EntityFlag.ANGRY);
    }

    @Override
    public Item[] getDrops() {
        List<Item> drops = new ArrayList<>();
        if(ThreadLocalRandom.current().nextInt(200) < 17) { // 8.5%
            drops.add(getItemInHand());
            drops.addAll(getArmorInventory().getContents().values());
        }
        drops.addAll(new InventorySlice(getEquipmentInventory(), 1, getEquipmentInventory().getSize()).getContents().values());
        return drops.toArray(Item.EMPTY_ARRAY);
    }

    @Override
    public boolean attackTarget(Entity entity) {
        return switch (entity.getIdentifier()) {
            case Entity.WITHER_SKELETON, Entity.WITHER -> true;
            case Entity.HOGLIN -> {
                if(entity instanceof EntityHoglin hoglin) {
                    if(!hoglin.isBaby()) {
                        if(hoglin.getHealth() - getDiffHandDamage(getServer().getDifficulty()) <= 0) {
                            List<Entity> entities =  Arrays.stream(getLevel().getEntities()).filter(entity1 -> entity1 instanceof EntityPiglin piglin && piglin.distance(this) < 16).toList();
                            AnimateEntityPacket.Animation.AnimationBuilder builder = AnimateEntityPacket.Animation.builder();
                            builder.animation("animation.piglin.celebrate_hunt_special");
                            builder.nextState("r");
                            builder.blendOutTime(1);
                            Entity.playAnimationOnEntities(builder.build(), entities);
                            entities.forEach(entity1 -> entity1.level.addSound(entity1, Sound.MOB_PIGLIN_CELEBRATE));
                        }
                        yield true;
                    }
                }
                yield false;
            }
            default -> false;
        };
    }

    public void pickupItems(Entity entity) {
        if(!isAngry()) {
            if(entity instanceof EntityInventoryHolder holder) {
                for(Entity i : entity.level.getNearbyEntities(entity.getBoundingBox().grow(1, 0.5, 1))) {
                    boolean pickup = false;
                    if(i instanceof EntityItem entityItem) {
                        Item item = entityItem.getItem();
                        if((item.isArmor() || item.isTool()) && item.getTier() == ItemTool.TIER_GOLD) {
                            if(holder.equip(item)) {
                                pickup = true;
                            }
                        } else if(item instanceof ItemPorkchop) {
                            pickup = true;
                        } else if(likesItem(item)) {
                            if(getItemInOffhand().isNull()) {
                                setItemInOffhand(item);
                                pickup = true;
                            }
                        }
                        if(pickup) {
                            TakeItemEntityPacket pk = new TakeItemEntityPacket();
                            pk.entityId = entity.getId();
                            pk.target = i.getId();
                            Server.broadcastPacket(entity.getViewers().values(), pk);
                            i.close();
                        }
                    }
                }
            }
        }
    }

    @Override
    public Integer getExperienceDrops() {
        return Math.toIntExact(isBaby() ? 1 : 5 + (getArmorInventory().getContents().values().stream().filter(Item::isArmor).count() * ThreadLocalRandom.current().nextInt(1, 4)));
    }

    @Override
    public boolean equip(Item item) {
         if((item.getTier() > getItemInHand().getTier() && getItemInHand().getTier() != Item.WEARABLE_TIER_GOLD) || item.getTier() == Item.WEARABLE_TIER_GOLD) {
            this.getEquipmentInventory().addItem(getItemInHand());
            this.setItemInHand(item);
            return true;
        }
        return false;
    }

    protected static class PiglinFleeFromTargetExecutor extends FleeFromTargetExecutor {

        public PiglinFleeFromTargetExecutor(MemoryType<? extends Vector3> memory) {
            super(memory, 0.5f, true, 8);
        }

        @Override
        public void onStart(EntityIntelligent entity) {
            super.onStart(entity);
            if(entity.distance(entity.getMemoryStorage().get(getMemory())) < 8) {
                entity.getLevel().addSound(entity, Sound.MOB_PIGLIN_RETREAT);
            }
        }
    }

    protected static class PiglinMeleeAttackExecutor extends MeleeAttackExecutor {

        public PiglinMeleeAttackExecutor(MemoryType<? extends Entity> memory, float speed, int maxSenseRange, boolean clearDataWhenLose, int coolDown) {
            super(memory, speed, maxSenseRange, clearDataWhenLose, coolDown);
        }

        @Override
        public void onStart(EntityIntelligent entity) {
            super.onStart(entity);
            entity.setDataProperty(EntityDataTypes.TARGET_EID, entity.getMemoryStorage().get(memory).getId());
            entity.setDataFlag(EntityFlag.ANGRY);
            entity.level.addLevelSoundEvent(entity, LevelSoundEvent.ANGRY, -1, Entity.PIGLIN, false, false);
            Arrays.stream(entity.level.getEntities()).filter(entity1 -> entity1 instanceof EntityPiglin && entity1.distance(entity) < 16 && ((EntityPiglin) entity1).getMemoryStorage().isEmpty(CoreMemoryTypes.ATTACK_TARGET)).forEach(entity1 -> ((EntityPiglin) entity1).getMemoryStorage().put(CoreMemoryTypes.ATTACK_TARGET, entity.getMemoryStorage().get(memory)));
            if(entity.getMemoryStorage().get(memory) instanceof EntityHoglin) {
                entity.getMemoryStorage().put(CoreMemoryTypes.LAST_HOGLIN_ATTACK_TIME, entity.getLevel().getTick());
            }
        }

        @Override
        public void onStop(EntityIntelligent entity) {
            super.onStop(entity);
            entity.setDataFlag(EntityFlag.ANGRY, false);
            entity.setDataProperty(EntityDataTypes.TARGET_EID, 0L);
        }

        @Override
        public void onInterrupt(EntityIntelligent entity) {
            super.onInterrupt(entity);
            entity.setDataFlag(EntityFlag.ANGRY, false);
            entity.setDataProperty(EntityDataTypes.TARGET_EID, 0L);
        }
    }

    public static boolean likesItem(Item item) {
        return switch (item.getId()) {
            case Block.BELL,
                 Block.GOLD_BLOCK,
                 Item.CLOCK,
                 Item.ENCHANTED_GOLDEN_APPLE,
                 Block.GILDED_BLACKSTONE,
                 Item.GLISTERING_MELON_SLICE,
                 Item.GOLD_INGOT,
                 Item.GOLD_NUGGET,
                 Block.GOLD_ORE,
                 Item.GOLDEN_APPLE,
                 Item.GOLDEN_AXE,
                 Item.GOLDEN_BOOTS,
                 Item.GOLDEN_CARROT,
                 Item.GOLDEN_CHESTPLATE,
                 Item.GOLDEN_HELMET,
                 Item.GOLDEN_HOE,
                 Item.GOLDEN_HORSE_ARMOR,
                 Item.GOLDEN_LEGGINGS,
                 Item.GOLDEN_PICKAXE,
                 Item.GOLDEN_SHOVEL,
                 Item.GOLDEN_SWORD,
                 Block.LIGHT_WEIGHTED_PRESSURE_PLATE,
                 Block.NETHER_GOLD_ORE,
                 Block.GOLDEN_RAIL,
                 Item.RAW_GOLD -> true;
            default -> false;
        };
    }
}
