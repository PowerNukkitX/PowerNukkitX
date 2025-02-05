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
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.FleeFromTargetExecutor;
import cn.nukkit.entity.ai.executor.MeleeAttackExecutor;
import cn.nukkit.entity.ai.executor.PiglinTradeExecutor;
import cn.nukkit.entity.ai.executor.PlaySoundExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.BlockSensor;
import cn.nukkit.entity.ai.sensor.NearestEntitySensor;
import cn.nukkit.entity.ai.sensor.NearestTargetEntitySensor;
import cn.nukkit.entity.data.EntityDataTypes;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.inventory.EntityInventoryHolder;
import cn.nukkit.inventory.InventorySlice;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemArmor;
import cn.nukkit.item.ItemGoldIngot;
import cn.nukkit.item.ItemPorkchop;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.TakeItemEntityPacket;
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
                        new Behavior(new PlaySoundExecutor(Sound.MOB_PIGLIN_JEALOUS, 0.8f, 1.2f, 0.8f, 0.8f), all(new RandomSoundEvaluator(), entity -> getViewers().values().stream().noneMatch(p -> p.distance(entity) < 8 && likesItem(p.getInventory().getItemInHand()) && p.level.raycastBlocks(p, entity).isEmpty())), 11, 1),
                        new Behavior(new PlaySoundExecutor(Sound.MOB_PIGLIN_ANGRY, 0.8f, 1.2f, 0.8f, 0.8f), all(new RandomSoundEvaluator(), entity -> isAngry()), 10, 1),
                        new Behavior(new PlaySoundExecutor(Sound.MOB_PIGLIN_AMBIENT, 0.8f, 1.2f, 0.8f, 0.8f), all(new RandomSoundEvaluator(), entity -> !isAngry()), 9, 1),
                        new Behavior(new PiglinMeleeAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.3f, 40, true, 30), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET)
                        ), 5, 1),
                        new Behavior(new PiglinMeleeAttackExecutor(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, 0.3f, 40, false, 30), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET),
                                entity -> getMemoryStorage().get(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET) instanceof Player player && player.getInventory() != null && !Arrays.stream(player.getInventory().getArmorContents()).anyMatch(item -> !item.isNull() && item instanceof ItemArmor armor && armor.getTier() == ItemArmor.TIER_GOLD)
                        ), 4, 1),
                        new Behavior(new PiglinFleeFromTargetExecutor(CoreMemoryTypes.NEAREST_BLOCK), all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_BLOCK),
                                entity -> getMemoryStorage().get(CoreMemoryTypes.NEAREST_BLOCK) instanceof BlockSoulFire
                        ), 2, 1),
                        new Behavior(new PiglinFleeFromTargetExecutor(CoreMemoryTypes.NEAREST_SHARED_ENTITY), new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_SHARED_ENTITY), 3, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, true, 10), none(), 1, 1)
                ),
                Set.of(new NearestTargetEntitySensor<>(0, 16, 20,
                        List.of(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), this::attackTarget),
                        new NearestEntitySensor(EntityZombiePigman.class, CoreMemoryTypes.NEAREST_SHARED_ENTITY, 8 , 0),
                        new NearestEntitySensor(EntityZoglin.class, CoreMemoryTypes.NEAREST_SHARED_ENTITY, 8 , 0),
                        new BlockSensor(BlockDoor.class, CoreMemoryTypes.NEAREST_BLOCK, 2, 2, 20),
                        new BlockSensor(BlockSoulFire.class, CoreMemoryTypes.NEAREST_BLOCK, 8, 2, 20)
                ),
                Set.of(new WalkController(), new LookController(true, true)),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
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
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if(currentTick%20 == 0) {
            pickupItems(this);
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
                            entity.level.addSound(entity, Sound.MOB_PIGLIN_CELEBRATE);
                        }
                        yield true;
                    }
                }
                yield false;
            }
            default -> super.attackTarget(entity);
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
    public boolean equip(Item item) {
         if((item.getTier() > getItemInHand().getTier() && getItemInHand().getTier() != ItemArmor.TIER_GOLD) || item.getTier() == ItemArmor.TIER_GOLD) {
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
            System.out.println(entity.getMemoryStorage().get(memory).getId());
            entity.setDataProperty(EntityDataTypes.TARGET_EID, entity.getMemoryStorage().get(memory).getId());
            entity.setDataFlag(EntityFlag.ANGRY);
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
