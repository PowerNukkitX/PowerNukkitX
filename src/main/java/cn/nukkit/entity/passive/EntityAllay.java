package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityFlyable;
import cn.nukkit.entity.EntityOwnable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LiftController;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.SpaceMoveController;
import cn.nukkit.entity.ai.evaluator.ConditionalProbabilityEvaluator;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.executor.EntityMoveToOwnerExecutor;
import cn.nukkit.entity.ai.executor.LookAtTargetExecutor;
import cn.nukkit.entity.ai.executor.MoveToTargetExecutor;
import cn.nukkit.entity.ai.executor.SpaceRandomRoamExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleSpaceAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.FlyingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestItemSensor;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.mob.EntityMob;
import cn.nukkit.inventory.EntityInventoryHolder;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventorySlice;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EntityAllay extends EntityMob implements EntityFlyable, EntityOwnable, EntityInventoryHolder {

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
    protected void initEntity() {
        super.initEntity();
        updateMemory();
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
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
                ),
                Set.of(
                        new NearestItemSensor(32, 0 , 20),
                        new NearestPlayerSensor(64, 0, 20)
                ),
                Set.of(new SpaceMoveController(), new LookController(true, true), new LiftController()),
                new SimpleSpaceAStarRouteFinder(new FlyingPosEvaluator(), this),
                this
        );
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
                Item itemInHand = player.getInventory().getItemInHand().clone().clearNamedTag();
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
    public Item[] getDrops() {
        return getInventory().getContents().values().toArray(Item.EMPTY_ARRAY);
    }
}
