package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.BlockTurtleEgg;
import cn.nukkit.entity.Entity;
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
import cn.nukkit.entity.ai.sensor.BlockSensor;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.ai.sensor.NearestTargetEntitySensor;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.passive.EntityTurtle;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.inventory.EntityInventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.TakeItemEntityPacket;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class EntityZombie extends EntityMob implements EntityWalkable, EntitySmite {
    @Override
    @NotNull
    public String getIdentifier() {
        return ZOMBIE;
    }


    public EntityZombie(IChunk chunk, CompoundTag nbt) {
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
                        new Behavior(new PlaySoundExecutor(Sound.MOB_ZOMBIE_SAY, isBaby() ? 1.3f : 0.8f, isBaby() ? 1.7f : 1.2f, 1, 1), new RandomSoundEvaluator(), 7, 1),
                        new Behavior(new JumpExecutor(), all(entity -> !getMemoryStorage().isEmpty(CoreMemoryTypes.NEAREST_BLOCK), entity -> entity.getCollisionBlocks().stream().anyMatch(block -> block instanceof BlockTurtleEgg)), 6, 1, 10),
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_BLOCK, 0.3f, true), new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_BLOCK), 5, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.3f, 40, true, 30), new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET), 4, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.3f, 40, false, 30), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER), 3, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, 0.3f, 40, true, 30), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), 2, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, true, 10), none(), 1, 1)
                ),
                Set.of(
                        new NearestPlayerSensor(40, 0, 0),
                        new NearestTargetEntitySensor<>(0, 16, 20,
                                List.of(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), this::attackTarget),
                        new BlockSensor(BlockTurtleEgg.class, CoreMemoryTypes.NEAREST_BLOCK, 11, 15, 10)
                ),
                Set.of(new WalkController(), new LookController(true, true)),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if(getHealth()-source.getFinalDamage() <= 1) {
            if(source.getCause() == EntityDamageEvent.DamageCause.DROWNING) {
                transform();
                return true;
            }
        }
        return super.attack(source);
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(20);
        this.diffHandDamage = new float[]{2.5f, 3f, 4.5f};
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
        return "Zombie";
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
        //husk not burn
        if (this instanceof EntityHusk) {
            return super.onUpdate(currentTick);
        }
        burn(this);
        if(currentTick%20 == 0) {
            pickupItems(this);
        }
        return super.onUpdate(currentTick);
    }

    @Override
    public double getFloatingForceFactor() {
        return 0.7;
    }

    @Override
    public Item[] getDrops() {
        float drops = ThreadLocalRandom.current().nextFloat(100);
        if (drops < 0.83) {
            return switch (Utils.rand(0, 2)) {
                case 0 -> new Item[]{Item.get(Item.IRON_INGOT, 0, 1), Item.get(Item.ROTTEN_FLESH, 0, Utils.rand(0, 2))};
                case 1 -> new Item[]{Item.get(Item.CARROT, 0, 1), Item.get(Item.ROTTEN_FLESH, 0, Utils.rand(0, 2))};
                default -> new Item[]{Item.get(Item.POTATO, 0, 1), Item.get(Item.ROTTEN_FLESH, 0, Utils.rand(0, 2))};
            };
        }
        return new Item[]{Item.get(Item.ROTTEN_FLESH, 0, Utils.rand(0, 2))};
    }

    @Override
    public boolean attackTarget(Entity entity) {
        return switch (entity.getIdentifier()) {
            case Entity.VILLAGER_V2,
                 Entity.SNOW_GOLEM,
                 Entity.IRON_GOLEM-> true;
            case Entity.TURTLE -> entity instanceof EntityTurtle turtle && !turtle.isBaby();
            default -> false;
        };
    }

    public static void pickupItems(Entity entity) {
        if(entity instanceof EntityInventoryHolder holder) {
            for(Entity i : entity.level.getNearbyEntities(entity.getBoundingBox().grow(1, 0.5, 1))) {
                if(i instanceof EntityItem entityItem) {
                    Item item = entityItem.getItem();
                    if(item.isArmor() || item.isTool()) {
                        if(holder.equip(item)) {
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

    protected void transform() {
        this.close();
        getArmorInventory().getContents().values().forEach(i -> getLevel().dropItem(this, i));
        getEquipmentInventory().getContents().values().forEach(i -> getLevel().dropItem(this, i));
        EntityDrowned drowned = new EntityDrowned(this.getChunk(), this.namedTag);
        drowned.setPosition(this);
        drowned.setRotation(this.yaw, this.pitch);
        drowned.spawnToAll();
        drowned.namedTag.putBoolean("Transformed", true);
        drowned.level.addSound(drowned, Sound.ENTITY_ZOMBIE_CONVERTED_TO_DROWNED);
    }
}
