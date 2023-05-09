package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.FluctuateController;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.evaluator.PassByTimeEvaluator;
import cn.nukkit.entity.ai.evaluator.ProbabilityEvaluator;
import cn.nukkit.entity.ai.executor.*;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestFeedingPlayerSensor;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.item.Item;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.Set;

/**
 * @author BeYkeRYkt (Nukkit Project)
 */
public class EntityMooshroom extends EntityAnimal implements EntityWalkable {

    public static final int NETWORK_ID = 16;

    public EntityMooshroom(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @PowerNukkitOnly
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
                                1, 1
                        )
                ),
                Set.of(
                        new Behavior(new FlatRandomRoamExecutor(0.25f, 12, 40, true, 100, true, 10), new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, 0, 100), 4, 1),
                        new Behavior(new EntityBreedingExecutor<>(EntityMooshroom.class, 16, 100, 0.5f), entity -> entity.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE), 3, 1),
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_FEEDING_PLAYER, 0.25f, true), new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_FEEDING_PLAYER), 2, 1),
                        new Behavior(new LookAtTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 100), new ProbabilityEvaluator(4, 10), 1, 1, 100),
                        new Behavior(new FlatRandomRoamExecutor(0.1f, 12, 100, false, -1, true, 10), (entity -> true), 1, 1)
                ),
                Set.of(new NearestFeedingPlayerSensor(8, 0), new NearestPlayerSensor(8, 0, 20)),
                Set.of(new WalkController(), new LookController(true, true), new FluctuateController()),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    @Override
    public float getWidth() {
        if (isBaby()) {
            return 0.45f;
        }
        return 0.9f;
    }

    @Override
    public float getHeight() {
        if (isBaby()) {
            return 0.65f;
        }
        return 1.3f;
    }

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return "Mooshroom";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.LEATHER), Item.get(Item.RAW_BEEF)};
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(10);
        super.initEntity();
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (super.onInteract(player, item, clickedPos)) {
            return true;
        }

        if (item.getId() == Item.SHEARS && item.useOn(this)) {
            this.close();
            //TODO 不同颜色的牛掉落不同的蘑菇
            this.level.dropItem(this, Item.get(40, 0, 5));
            this.level.addParticleEffect(this.add(0, this.getHeight(), 0), ParticleEffect.LARGE_EXPLOSION_LEVEL);
            EntityCow cow = new EntityCow(this.getChunk(), this.namedTag);
            cow.setPosition(this);
            cow.setRotation(this.yaw, this.pitch);
            cow.spawnToAll();
            this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this.clone(), this.getPosition(), VibrationType.SHEAR));
            return true;
        } else if (item.getId() == Item.BUCKET && item.getDamage() == 0) {
            item.count--;
            player.getInventory().addItem(Item.get(Item.BUCKET, 1));
            return true;
        } else if (item.getId() == Item.BOWL && item.getDamage() == 0) {
            item.count--;
            player.getInventory().addItem(Item.get(Item.MUSHROOM_STEW));
            return true;
        }

        return false;
    }
}
