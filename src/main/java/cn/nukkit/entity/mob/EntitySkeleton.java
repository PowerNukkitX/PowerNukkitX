package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.ShootExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;

import java.util.Set;

/**
 * @author PikyCZ
 */
public class EntitySkeleton extends EntityMob implements EntityWalkable, EntitySmite {

    public static final int NETWORK_ID = 34;

    public EntitySkeleton(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(20);
        super.initEntity();
        if (getItemInHand().isNull()) {
            setItemInHand(Item.get(ItemID.BOW));
        }
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.9f;
    }

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return "Skeleton";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.BONE, Item.ARROW)};
    }

    @PowerNukkitOnly
    @Override
    public boolean isUndead() {
        return true;
    }

    @PowerNukkitOnly
    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }

    @PowerNukkitXOnly
    @Override
    public boolean onUpdate(int currentTick) {
        if (this.getLevel().getDimension() == Level.DIMENSION_OVERWORLD)
            if (this.getLevel().getTime() > 0 && this.getLevel().getTime() <= 12000)
                if (!this.hasEffect(Effect.FIRE_RESISTANCE))
                    if (!this.isInsideOfWater())
                        if (!this.isUnderBlock())
                            if (!this.isOnFire())
                                this.setOnFire(1);
        return super.onUpdate(currentTick);
    }

    @Since("1.19.80-r3")
    @Override
    protected IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(new ShootExecutor(this::getItemInHand, CoreMemoryTypes.ATTACK_TARGET, 0.3f, 15, true, 30, 20),
                                entity -> {
                                    if (entity.getMemoryStorage().isEmpty(CoreMemoryTypes.ATTACK_TARGET)) {
                                        return false;
                                    } else {
                                        Entity e = entity.getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET);
                                        if (e instanceof Player player) {
                                            return player.isSurvival() || player.isAdventure();
                                        }
                                        return true;
                                    }
                                }, 3, 1),
                        new Behavior(new ShootExecutor(this::getItemInHand, CoreMemoryTypes.NEAREST_PLAYER, 0.3f, 15, true, 30, 20),
                                entity -> {
                                    if (entity.getMemoryStorage().isEmpty(CoreMemoryTypes.NEAREST_PLAYER)) {
                                        return false;
                                    } else {
                                        Player player = entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_PLAYER);
                                        return player.isSurvival() || player.isAdventure();
                                    }
                                }, 2, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, true, 10), (entity -> true), 1, 1)
                ),
                Set.of(new NearestPlayerSensor(16, 0, 20)),
                Set.of(new WalkController(), new LookController(true, true)),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }
}
