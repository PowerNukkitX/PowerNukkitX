package org.powernukkitx.entity.passive;

import org.powernukkitx.Player;
import org.powernukkitx.entity.EntityFlyable;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.LiftController;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.SpaceMoveController;
import org.powernukkitx.entity.ai.evaluator.ConditionalProbabilityEvaluator;
import org.powernukkitx.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import org.powernukkitx.entity.ai.executor.EntityMoveToOwnerExecutor;
import org.powernukkitx.entity.ai.executor.LookAtTargetExecutor;
import org.powernukkitx.entity.ai.executor.MoveToTargetExecutor;
import org.powernukkitx.entity.ai.executor.SpaceRandomRoamExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleSpaceAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.FlyingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.NearestItemSensor;
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.entity.item.EntityItem;
import org.powernukkitx.entity.mob.EntityMob;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.inventory.InventorySlice;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class EntityAllay extends EntityMob implements EntityFlyable {

    @Override
    @NotNull public String getIdentifier() {
        return ALLAY;
    }

    @Getter
    private int lastItemDropTick = -1;
    @Getter
    @Setter
    public int dropCollectCooldown = 60;


    public EntityAllay(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.1f);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        updateMemory();
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return BehaviorGroup.builder(this)
                .behaviors(
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_ITEM, 0.22f, true), new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_ITEM), 5, 1),
                        new Behavior(new EntityMoveToOwnerExecutor(0.4f, true, 64, -1), entity -> {
                            if (this.hasOwner()) {
                                var player = getOwner();
                                var distanceSquared = this.distanceSquared(player);
                                return distanceSquared >= 100;
                            } else return false;
                        }, 4, 1),
                        new Behavior(new LookAtTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 100), new ConditionalProbabilityEvaluator(3, 7, entity -> hasOwner(false), 10),
                                1, 1, 25),
                        new Behavior(new SpaceRandomRoamExecutor(0.15f, 12, 100, 20, false, -1, true, 10), (entity -> true), 1, 1)
                )
                .sensors(
                        new NearestItemSensor(32, 0 , 20),
                        new NearestPlayerSensor(64, 0, 20)
                )
                .controllers(new SpaceMoveController(), new LookController(true, true), new LiftController())
                .routeFinder(new SimpleSpaceAStarRouteFinder(new FlyingPosEvaluator(), this))
                .build();
    }

    @Override
    public float getHeight() {
        return 0.6f;
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public String getOriginalName() {
        return "Allay";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("allay", "mob");
    }

    @Override
    public Integer getExperienceDrops() {
        return 0;
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if(player != null) {
            if(item.isNull()) {
                setOwnerName(null);
                setItemInHand(Item.AIR);
            } else {
                setOwnerName(player.getName());
                Item itemInHand = player.getInventory().getItemInMainHand().clone().clearNamedTag();
                itemInHand.setCount(1);
                setItemInHand(itemInHand);
            }
            updateMemory();
        }
        return super.onInteract(player, item, clickedPos);
    }

    private void updateMemory() {
        Item item = getItemInHand();
        if(item.isNull()) {
            getMemoryStorage().clear(CoreMemoryTypes.LOOKING_ITEM);
        } else getMemoryStorage().put(CoreMemoryTypes.LOOKING_ITEM, item.getClass());
    }

    @Override
    public Inventory getInventory() {
        //0 = hand, 1 = offhand
        return new InventorySlice(getEquipmentInventory(), 2, 3);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if(currentTick%10 == 0) {
            EntityItem nearestItem = getMemoryStorage().get(CoreMemoryTypes.NEAREST_ITEM);
            if(nearestItem != null && !nearestItem.closed) {
                if(nearestItem.distance(this) < 1 && currentTick-lastItemDropTick > dropCollectCooldown) {
                    Item item = nearestItem.getItem();
                    Item currentItem = getInventory().getItem(0).clone();
                    if(getInventory().canAddItem(item)) {
                        if(getInventory().callPickupItemEvent(nearestItem)) {
                            if(currentItem.isNull()) {
                                getInventory().setItem(0, item);
                            } else {
                                item.setCount(item.getCount() + currentItem.getCount());
                                getInventory().setItem(0, item);
                            }
                            this.level.addSound(this, Sound.RANDOM_POP);
                            nearestItem.close();
                        }
                    }
                }
            } else {
                if(hasOwner()) {
                    if(distance(getOwner()) < 2){
                        dropItem(currentTick);
                    }
                }
            }
        }
        return super.onUpdate(currentTick);
    }

    private boolean dropItem(int currentTick) {
        if (!this.isAlive()) {
            return false;
        }
        Item item = getInventory().getItem(0);
        if(item.isNull()) return true;
        Vector3 motion = this.getDirectionVector().multiply(0.4);
        this.level.dropItem(this.add(0, 1.3, 0), item, motion, 40);
        getInventory().clearAll();
        this.lastItemDropTick = currentTick;
        return true;
    }
    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        return getInventory().getContents().values().toArray(Item.EMPTY_ARRAY);
    }
}
