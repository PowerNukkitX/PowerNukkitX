package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntityShearable;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.FluctuateController;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.BlockCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.PassByTimeEvaluator;
import cn.nukkit.entity.ai.evaluator.ProbabilityEvaluator;
import cn.nukkit.entity.ai.executor.AnimalGrowExecutor;
import cn.nukkit.entity.ai.executor.BreedingExecutor;
import cn.nukkit.entity.ai.executor.EatGrassExecutor;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.LookAtTargetExecutor;
import cn.nukkit.entity.ai.executor.LoveTimeoutExecutor;
import cn.nukkit.entity.ai.executor.TemptExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.components.AgeableComponent;
import cn.nukkit.entity.components.BreedableComponent;
import cn.nukkit.entity.components.HealthComponent;
import cn.nukkit.entity.components.MovementComponent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.Utils;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author BeYkeRYkt (Nukkit Project)
 */
public class EntitySheep extends EntityAnimal implements EntityWalkable, EntityShearable {
    @Override
    @NotNull
    public String getIdentifier() {
        return SHEEP;
    }

    public boolean sheared = false;
    public int color = 0;

    public EntitySheep(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
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

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(8);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.25f);
    }

    @Override
    public String getOriginalName() {
        return "Sheep";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("sheep", "mob");
    }

    @Override
    public @Nullable BreedableComponent getComponentBreedable() {
        return new BreedableComponent(
                null,
                null,
                null,
                null,
                Set.of(
                        BlockID.WHEAT
                ),
                List.of(
                        new BreedableComponent.BreedsWith(EntityID.SHEEP, EntityID.SHEEP)
                ),
                null,
                null,
                null,
                null,
                null,
                null,
                true,
                null,
                false,
                null
        );
    }

    @Override
    public AgeableComponent getComponentAgeable() {
        return new AgeableComponent(
                null,
                1200f,
                List.of(
                        new AgeableComponent.FeedItem(BlockID.WHEAT)
                ),
                null,
                null,
                null
        );
    }

    @Override
    public void initEntity() {
        super.initEntity();

        if (!this.namedTag.containsKey("Color")) {
            this.setColor(randomColor());
        } else {
            this.setColor(this.namedTag.getByte("Color"));
        }

        if (!this.namedTag.containsKey("Sheared")) {
            this.namedTag = this.namedTag.toBuilder().putByte("Sheared", (byte) 0).build();
        } else {
            this.sheared = this.namedTag.getBoolean("Sheared");
        }

        this.setDataFlag(ActorFlags.SHEARED, this.sheared);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag = this.namedTag.toBuilder().putByte("Color", (byte) this.color)
                .putBoolean("Sheared", this.sheared)
                .build();
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (super.onInteract(player, item, clickedPos)) {
            return true;
        }

        if (item instanceof ItemDye dye) {
            this.setColor(dye.getDyeColor().getWoolData());
            return true;
        }

        return item.isShears() && shear();
    }

    public boolean shear() {
        if (sheared || this.isBaby()) {
            return false;
        }

        this.sheared = true;
        this.setDataFlag(ActorFlags.SHEARED, true);

        Item woolItem = this.getWoolItem();
        woolItem.setCount(ThreadLocalRandom.current().nextInt(2) + 1);
        this.level.dropItem(this, woolItem);

        level.addSound(this, Sound.MOB_SHEEP_SHEAR);
        level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.getVector3(), VibrationType.SHEAR));
        return true;
    }

    public void growWool() {
        this.setDataFlag(ActorFlags.SHEARED, false);
        this.sheared = false;
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);

        int muttonAmount = Utils.rand(1, 2 + looting);
        Item mutton = Item.get(
                this.isOnFire() ? Item.COOKED_MUTTON : Item.MUTTON,
                0,
                muttonAmount
        );

        Item woolItem = this.sheared ? Item.AIR : this.getWoolItem();

        return new Item[]{mutton, woolItem};
    }

    public int getColor() {
        return namedTag.getByte("Color");
    }

    public void setColor(int color) {
        this.color = color;
        this.setDataProperty(ActorDataTypes.COLOR_INDEX, (byte) color);
        this.namedTag = this.namedTag.toBuilder().putByte("Color", (byte) this.color).build();
    }

    public Item getWoolItem() {
        return switch (getColor()) {
            case 0 -> Item.get(Block.WHITE_WOOL);
            case 1 -> Item.get(Block.ORANGE_WOOL);
            case 2 -> Item.get(Block.MAGENTA_WOOL);
            case 3 -> Item.get(Block.LIGHT_BLUE_WOOL);
            case 4 -> Item.get(Block.YELLOW_WOOL);
            case 5 -> Item.get(Block.LIME_WOOL);
            case 6 -> Item.get(Block.PINK_WOOL);
            case 7 -> Item.get(Block.GRAY_WOOL);
            case 8 -> Item.get(Block.LIGHT_GRAY_WOOL);
            case 9 -> Item.get(Block.CYAN_WOOL);
            case 10 -> Item.get(Block.PURPLE_WOOL);
            case 11 -> Item.get(Block.BLUE_WOOL);
            case 12 -> Item.get(Block.BROWN_WOOL);
            case 13 -> Item.get(Block.GREEN_WOOL);
            case 14 -> Item.get(Block.RED_WOOL);
            case 15 -> Item.get(Block.BLACK_WOOL);
            default -> throw new IllegalStateException("Unexpected value: " + getColor());
        };
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

    private static final Set<String> TEMPT_ITEMS = Set.of(
            BlockID.WHEAT
    );

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(
                        new Behavior(
                                new LoveTimeoutExecutor(20 * 30),
                                e -> e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE),
                                2, 1
                        ),
                        new Behavior(
                                new AnimalGrowExecutor(),
                                all(
                                        e -> e.isAgeable(),
                                        e -> e.isBaby(),
                                        e -> !e.isGrowthPaused(),
                                        e -> e.getTicksGrowLeft() > 0
                                ),
                                1, 1, 1200
                        )
                ),
                Set.of(
                        new Behavior(
                                new FlatRandomRoamExecutor(0.5f, 12, 40, true, 100, true, 10),
                                new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, 0, 100),
                                6, 1
                        ),
                        new Behavior(
                                new BreedingExecutor(16, 200, 0.25f),
                                all(
                                        e -> !e.isBaby(),
                                        e -> e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE)
                                ),
                                5, 1
                        ),
                        new Behavior(
                                new TemptExecutor(1.25f, TEMPT_ITEMS),
                                all(
                                        e -> !e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE),
                                        e -> TemptExecutor.hasTemptingPlayer(e, false, 10, TEMPT_ITEMS)
                                ),
                                4, 1
                        ),
                        new Behavior(
                                new EatGrassExecutor(40),
                                all(
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
                                                new BlockCheckEvaluator(Block.GRASS_BLOCK, new Vector3(0, -1, 0)),
                                                new BlockCheckEvaluator(Block.TALL_GRASS, Vector3.ZERO)
                                        )
                                ),
                                3, 1, 100
                        ),
                        new Behavior(
                                new LookAtTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 100),
                                new ProbabilityEvaluator(4, 10),
                                1, 1, 100
                        ),
                        new Behavior(
                                new FlatRandomRoamExecutor(0.2f, 12, 100, false, -1, true, 10),
                                (entity -> true),
                                1, 1
                        )
                ),
                Set.of(
                        new NearestPlayerSensor(8, 0, 20)
                ),
                Set.of(
                        new WalkController(),
                        new LookController(true, true),
                        new FluctuateController()
                ),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

}
