package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityShearable;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.FluctuateController;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.BlockCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.evaluator.PassByTimeEvaluator;
import cn.nukkit.entity.ai.evaluator.ProbabilityEvaluator;
import cn.nukkit.entity.ai.executor.*;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestFeedingPlayerSensor;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.DyeColor;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author BeYkeRYkt (Nukkit Project)
 */
public class EntitySheep extends EntityAnimal implements EntityWalkable, EntityShearable {

    public static final int NETWORK_ID = 13;
    public boolean sheared = false;
    public int color = 0;

    public EntitySheep(FullChunk chunk, CompoundTag nbt) {
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
                                1, 1
                        )
                ),
                Set.of(
                        new Behavior(new FlatRandomRoamExecutor(0.5f, 12, 40, true, 100, true, 10), new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, 0, 100), 6, 1),
                        new Behavior(new EntityBreedingExecutor<>(EntitySheep.class, 16, 100, 0.5f), entity -> entity.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE), 5, 1),
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_FEEDING_PLAYER, 0.5f, true, 8, 1.5f), new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_FEEDING_PLAYER), 4, 1),
                        new Behavior(new EatGrassExecutor(40), all(
                                any(
                                        all(
                                                entity -> entity instanceof EntityAnimal animal && !animal.isBaby(),
                                                new ProbabilityEvaluator(1, 100)
                                        ),
                                        all(
                                                entity -> entity instanceof EntityAnimal animal && animal.isBaby(),
                                                new ProbabilityEvaluator(43, 50)
                                        )
                                ),
                                any(
                                        new BlockCheckEvaluator(Block.GRASS, new Vector3(0, -1, 0)),
                                        new BlockCheckEvaluator(Block.TALL_GRASS, Vector3.ZERO))),
                                3, 1, 100
                        ),
                        new Behavior(new LookAtTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 100), new ProbabilityEvaluator(4, 10), 1, 1, 100),
                        new Behavior(new FlatRandomRoamExecutor(0.2f, 12, 100, false, -1, true, 10), (entity -> true), 1, 1)
                ),
                Set.of(new NearestFeedingPlayerSensor(8, 0), new NearestPlayerSensor(8, 0, 20)),
                Set.of(new WalkController(), new LookController(true, true), new FluctuateController()),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
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
        return "Sheep";
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(8);
        super.initEntity();

        if (!this.namedTag.contains("Color")) {
            this.setColor(randomColor());
        } else {
            this.setColor(this.namedTag.getByte("Color"));
        }

        if (!this.namedTag.contains("Sheared")) {
            this.namedTag.putByte("Sheared", 0);
        } else {
            this.sheared = this.namedTag.getBoolean("Sheared");
        }

        this.setDataFlag(DATA_FLAGS, DATA_FLAG_SHEARED, this.sheared);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putByte("Color", this.color);
        this.namedTag.putBoolean("Sheared", this.sheared);
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (super.onInteract(player, item, clickedPos)) {
            return true;
        }

        if (item instanceof ItemDye) {
            this.setColor(((ItemDye) item).getDyeColor().getWoolData());
            return true;
        }

        return item.isShears() && shear();
    }

    public boolean shear() {
        if (sheared || this.isBaby()) {
            return false;
        }

        this.sheared = true;
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_SHEARED, true);

        this.level.dropItem(this, Item.get(Item.WOOL, getColor(), ThreadLocalRandom.current().nextInt(2) + 1));

        level.addSound(this, Sound.MOB_SHEEP_SHEAR);
        level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.clone(), VibrationType.SHEAR));
        return true;
    }

    public void growWool() {
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_SHEARED, false);
        this.sheared = false;
    }

    @Override
    public Item[] getDrops() {
        if (this.lastDamageCause instanceof EntityDamageByEntityEvent) {
            return new Item[]{Item.get(((this.isOnFire()) ? Item.COOKED_MUTTON : Item.RAW_MUTTON)), Item.get(Item.WOOL, getColor(), 1)};
        }
        return Item.EMPTY_ARRAY;
    }

    public int getColor() {
        return namedTag.getByte("Color");
    }

    public void setColor(int color) {
        this.color = color;
        this.setDataProperty(new ByteEntityData(DATA_COLOUR, color));
        this.namedTag.putByte("Color", this.color);
    }

    private int randomColor() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double rand = random.nextDouble(1, 100);

        if (rand <= 0.164) {
            return DyeColor.PINK.getWoolData();
        }

        if (rand <= 15) {
            return random.nextBoolean() ? DyeColor.BLACK.getWoolData() : random.nextBoolean() ? DyeColor.GRAY.getWoolData() : DyeColor.LIGHT_GRAY.getWoolData();
        }

        return DyeColor.WHITE.getWoolData();
    }
}
