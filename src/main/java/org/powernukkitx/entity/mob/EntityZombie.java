package org.powernukkitx.entity.mob;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.block.BlockTurtleEgg;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntitySmite;
import org.powernukkitx.entity.EntityWalkable;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.WalkController;
import org.powernukkitx.entity.ai.evaluator.EntityCheckEvaluator;
import org.powernukkitx.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import org.powernukkitx.entity.ai.evaluator.RandomSoundEvaluator;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.JumpExecutor;
import org.powernukkitx.entity.ai.executor.MeleeAttackExecutor;
import org.powernukkitx.entity.ai.executor.MoveToTargetExecutor;
import org.powernukkitx.entity.ai.executor.NearestBlockIncementExecutor;
import org.powernukkitx.entity.ai.executor.PlaySoundExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.BlockSensor;
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.ai.sensor.NearestTargetEntitySensor;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.entity.item.EntityItem;
import org.powernukkitx.entity.passive.EntityTurtle;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.event.entity.EntityTransformEvent;
import org.powernukkitx.inventory.EntityInventoryHolder;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Utils;

import org.cloudburstmc.protocol.bedrock.packet.TakeItemActorPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        if (getHealthCurrent() - source.getFinalDamage() <= 1) {
            if (source.getCause() == EntityDamageEvent.DamageCause.DROWNING) {
                if(transform()) return true;
            }
        }
        return super.attack(source);
    }

    @Override
    protected void initEntity() {
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
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(20);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        float ageMovement = this.isBaby() ? 0.35f : 0.23f;
        return MovementComponent.value(ageMovement);
    }

    @Override
    public String getOriginalName() {
        return "Zombie";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("zombie", "undead", "monster", "mob");
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
        if (currentTick % 20 == 0) {
            pickupItems(this);
        }
        return super.onUpdate(currentTick);
    }

    @Override
    public double getFloatingForceFactor() {
        return 0.7;
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);
        List<Item> drops = new ArrayList<>();

        int flesh = Utils.rand(0, 3 + looting);
        if (flesh > 0) {
            drops.add(Item.get(Item.ROTTEN_FLESH, 0, flesh));
        }

        float rareChance = (1f / 120f) + ((1f / 300f) * looting);
        if (Utils.rand(0f, 1f) < rareChance) {
            int roll = Utils.rand(0, 3);
            switch (roll) {
                case 0 -> drops.add(Item.get(Item.IRON_INGOT));
                case 1 -> drops.add(Item.get(Item.CARROT));
                case 2 -> drops.add(Item.get(Item.POTATO));
            }
        }

        return drops.toArray(Item.EMPTY_ARRAY);
    }

    @Override
    public boolean attackTarget(Entity entity) {
        return switch (entity.getIdentifier()) {
            case Entity.VILLAGER_V2,
                 Entity.SNOW_GOLEM,
                 Entity.IRON_GOLEM -> true;
            case Entity.TURTLE -> entity instanceof EntityTurtle turtle && !turtle.isBaby();
            default -> false;
        };
    }

    public static void pickupItems(Entity entity) {
        if (entity instanceof EntityInventoryHolder holder) {
            for (Entity i : entity.level.getNearbyEntities(entity.getBoundingBox().grow(1, 0.5, 1))) {
                if (i instanceof EntityItem entityItem) {
                    Item item = entityItem.getItem();
                    if (item.isArmor() || item.isTool()) {
                        if (!holder.getInventory().callPickupItemEvent(entityItem)) {
                            continue;
                        }
                        if (holder.equip(item)) {
                            final TakeItemActorPacket pk = new TakeItemActorPacket();
                            pk.setActorRuntimeID(entity.getId());
                            pk.setItemRuntimeID(i.getId());
                            Server.broadcastPacket(entity.getViewers().values(), pk);
                            i.close();
                        }
                    }
                }
            }
        }
    }

    protected boolean transform() {
        this.saveNBT();
        Entity drowned = new EntityDrowned(this.getChunk(), this.getNbt().copy().remove("Health"));
        EntityTransformEvent event = new EntityTransformEvent(this, drowned);
        server.getPluginManager().callEvent(event);
        if(event.isCancelled()) {
            drowned.close();
            return false;
        } else {
            this.close();
            getArmorInventory().getContents().values().forEach(i -> getLevel().dropItem(this, i));
            getEquipmentInventory().getContents().values().forEach(i -> getLevel().dropItem(this, i));
            drowned.spawnToAll();
            drowned.getNbt().putBoolean("Transformed", true);
            drowned.level.addSound(drowned, Sound.ENTITY_ZOMBIE_CONVERTED_TO_DROWNED);
            return true;
        }
    }
}
