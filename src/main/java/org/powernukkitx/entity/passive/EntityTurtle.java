package org.powernukkitx.entity.passive;

import it.unimi.dsi.fastutil.Pair;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockFlowingWater;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.EntitySwimmable;
import org.powernukkitx.entity.EntityWalkable;
import org.powernukkitx.entity.ai.EntityAI;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.ConditionalController;
import org.powernukkitx.entity.ai.controller.DiveController;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.SpaceMoveController;
import org.powernukkitx.entity.ai.controller.WalkController;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.TemptExecutor;
import org.powernukkitx.entity.ai.executor.turtle.TurtleBreedingExecutor;
import org.powernukkitx.entity.ai.executor.turtle.TurtleGoalExecutor;
import org.powernukkitx.entity.ai.executor.turtle.TurtleGoalExecutor.Goal;
import org.powernukkitx.entity.ai.executor.turtle.TurtleLookExecutor;
import org.powernukkitx.entity.ai.executor.turtle.TurtleSwimExecutor;
import org.powernukkitx.entity.ai.executor.turtle.TurtleTemptExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleSpaceAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.SwimmingPosEvaluator;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.components.AgeableComponent;
import org.powernukkitx.entity.components.BreedableComponent;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.HomeComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.math.VectorMath;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.utils.Utils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author PetteriM1
 */
public class EntityTurtle extends EntityAnimal implements EntitySwimmable, EntityWalkable {
    private static final HomeComponent HOME_COMPONENT = new HomeComponent(true, null, null, null);

    private Vector3 homePos;
    private int homeDimension;
    private boolean wantsToLayEgg;
    private int lastPanicTick = -1000;
    private int lastPlayerAttackTick = -1000;

    @Override
    protected void initEntity() {
        super.initEntity();
        ListTag<FloatTag> home = nbt.getList("HomePos", FloatTag.class);
        homePos = home.size() == 3
                ? new Vector3(home.get(0).data, home.get(1).data, home.get(2).data)
                : new Vector3(getFloorX(), getFloorY(), getFloorZ());
        nbt.putDouble("HomeX", homePos.x).putDouble("HomeY", homePos.y).putDouble("HomeZ", homePos.z);
        homeDimension = nbt.getInt("HomeDimensionId", level.getDimension());
        wantsToLayEgg = nbt.getBoolean("WantsToLayEgg");
        setPregnant(nbt.getBoolean("IsPregnant") || wantsToLayEgg);
        if (!nbt.contains("identifier") && !nbt.contains("HomePos") && !nbt.contains(TAG_ENTITY_GROW_LEFT)
                && !nbt.contains("IsPregnant") && ThreadLocalRandom.current().nextInt(10) == 0) {
            setBaby(true);
        }
    }

    /**
     * Advances growth and love duration independently of movement AI.
     *
     * @param tickDiff elapsed game ticks
     * @return whether entity state changed
     */
    @Override
    public boolean entityBaseTick(int tickDiff) {
        boolean updated = super.entityBaseTick(tickDiff);
        if (!isAlive()) return updated;
        if (isBaby() && !isGrowthPaused()) {
            reduceGrowLeft(Math.max(0, tickDiff));
            if (getTicksGrowLeft() == 0) setBaby(false);
            updated = true;
        }
        if (getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE)
                && level.getTick() - getMemoryStorage().get(CoreMemoryTypes.LAST_IN_LOVE_TIME) >= 600) {
            getMemoryStorage().put(CoreMemoryTypes.IS_IN_LOVE, false);
            setDataFlag(ActorFlags.IN_LOVE, false);
            updated = true;
        }
        return updated;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        nbt.putList("HomePos", new ListTag<FloatTag>()
                .add(new FloatTag((float) homePos.x))
                .add(new FloatTag((float) homePos.y))
                .add(new FloatTag((float) homePos.z)));
        nbt.putInt("HomeDimensionId", homeDimension);
        nbt.putBoolean("IsPregnant", isPregnant());
        nbt.putBoolean("WantsToLayEgg", wantsToLayEgg);
    }

    /**
     * Returns the baby render scale used by Bedrock's legacy turtle model.
     *
     * @return the baby render scale
     */
    @Override
    public float getBabyScale() {
        return 0.16f;
    }

    /**
     * Applies the age state and drops a scute when a living baby becomes an adult.
     *
     * @param value whether this turtle is a baby
     */
    @Override
    public void setBaby(boolean value) {
        boolean growingUp = !value && isBaby() && isAlive();
        super.setBaby(value);
        if (growingUp) level.dropItem(this, Item.get(ItemID.TURTLE_SCUTE));
    }

    /**
     * Updates pregnancy and clears the pending laying state when pregnancy is removed.
     *
     * @param value whether this turtle carries eggs
     */
    @Override
    public void setPregnant(boolean value) {
        super.setPregnant(value);
        if (!value) wantsToLayEgg = false;
    }

    @Override
    public Integer getExperienceDrops() {
        boolean killedByPlayer = getLastDamageCause() instanceof EntityDamageByEntityEvent event
                && event.getDamager() instanceof Player;
        return !isBaby() && (killedByPlayer || level.getTick() - lastPlayerAttackTick <= 100)
                ? ThreadLocalRandom.current().nextInt(1, 4) : 0;
    }

    @Override
    public double getFloatingForceFactor() {
        return 1;
    }

    @Override
    public double getPassableBlockFrictionFactor() {
        return isInsideOfWater() ? 0.9 : super.getPassableBlockFrictionFactor();
    }

    @Override
    public float getEyeHeight() {
        return getHeight() * getScale() * 0.9f;
    }

    @Override
    public float getFootHeight() {
        return getHeight() * getScale() / 2 - 0.1f;
    }

    @Override
    protected void calculateOffsetBoundingBox() {
        if (offsetBoundingBox == null) return;
        double radius = getWidth() * getScale() * 0.5;
        offsetBoundingBox.setMinX(x - radius);
        offsetBoundingBox.setMaxX(x + radius);
        offsetBoundingBox.setMinY(y);
        offsetBoundingBox.setMaxY(y + getHeight() * getScale());
        offsetBoundingBox.setMinZ(z - radius);
        offsetBoundingBox.setMaxZ(z + radius);
    }

    @Override
    public float getDefaultUnderWaterSpeed() {
        return isBaby() ? 0.06f : 0.12f;
    }

    @Override
    protected boolean tryGrow(Player player, Item item) {
        if (isBaby() && BlockID.GOLDEN_DANDELION.equals(item.getId())) {
            boolean reset = isGrowthPaused();
            if (reset) setBreedingAge(-getBabyGrowTotalTicks());
            setGrowthPaused(!reset);
            level.addLevelSoundEvent(this, reset ? SoundEvent.RESET_GROWTH : SoundEvent.PAUSE_GROWTH);
            return true;
        }
        return super.tryGrow(player, item);
    }

    @Override
    protected boolean tryBreed(Player player, Item item, int currentTick) {
        return !isPregnant() && !wantsToLayEgg && super.tryBreed(player, item, currentTick);
    }

    @Override
    @NotNull public String getIdentifier() {
        return TURTLE;
    }
    

    public EntityTurtle(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public String getOriginalName() {
        return "Turtle";
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        if (isBaby()) return Item.EMPTY_ARRAY;
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);
        int seagrass = Utils.rand(0, 2 + looting);
        if (seagrass == 0) {
            return Item.EMPTY_ARRAY;
        }

        return new Item[]{
                Item.get(BlockID.SEAGRASS, 0, seagrass)
        };
    }

    @Override
    public Set<String> typeFamily() {
        if (this.isBaby()) {
            return Set.of("aquatic", "turtle", "baby_turtle", "mob");
        }
        return Set.of("aquatic", "turtle", "mob");
    }

    @Override
    public @Nullable BreedableComponent getComponentBreedable() {
        return new BreedableComponent(
                null,
                null,
                null,
                null,
                Set.of(
                    BlockID.SEAGRASS
                ),
                List.of(
                    new BreedableComponent.BreedsWith(EntityID.TURTLE, EntityID.TURTLE)
                ),
                true,
                null,
                null,
                null,
                null,
                null,
                null,
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
                    new AgeableComponent.FeedItem(BlockID.SEAGRASS)
                ),
                Set.of(BlockID.GOLDEN_DANDELION),
                Set.of(BlockID.GOLDEN_DANDELION),
                Set.of(
                    ItemID.TURTLE_SCUTE
                )
        );
    }

    @Override
    public float getWidth() {
        return isBaby() ? 4f : 1.2f;
    }

    @Override
    public float getHeight() {
        return isBaby() ? 1.333333f : 0.4f;
    }

    @Override
    public HomeComponent getComponentHome() {
        return HOME_COMPONENT;
    }

    /**
     * Returns the birthplace block when it is loaded in this turtle's current dimension.
     *
     * @return the birthplace block, or null when it cannot be read without loading a chunk
     */
    @Override
    public @Nullable Block getHomePosition() {
        if (homePos == null || homeDimension != level.getDimension()
                || !level.isChunkLoaded(homePos.getFloorX() >> 4, homePos.getFloorZ() >> 4)) return null;
        return level.getBlock(homePos, false);
    }

    /**
     * Makes the current position the birthplace used for future egg laying.
     */
    @Override
    public void setHomePosition() {
        setHomePos(this);
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(30);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.1f);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if(source.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION && getLevelBlock().canPassThrough()) {
            return false;
        }
        if (source.getCause() == EntityDamageEvent.DamageCause.LIGHTNING) {
            source.setDamage(source.getDamage() * 2000f);
        }
        boolean damaged = super.attack(source);
        if (damaged) {
            lastPanicTick = level.getTick();
            if (source instanceof EntityDamageByEntityEvent event && event.getDamager() instanceof Player) {
                lastPlayerAttackTick = level.getTick();
            }
        }
        return damaged;
    }

    /**
     * Applies a signed breeding age. Negative values initialize the remaining baby growth ticks.
     *
     * @param ticks negative ticks until adulthood, or zero/positive for an adult
     */
    public void setBreedingAge(int ticks) {
        setBaby(ticks < 0);
        if (ticks < 0) {
            nbt.putInt(TAG_ENTITY_GROW_LEFT, (int) Math.min(24000L, -(long) ticks));
            ensureGrowLoaded();
        }
    }

    /**
     * Sets the birthplace used when returning to lay eggs, in this turtle's current dimension.
     *
     * @param pos birthplace; copied so subsequent caller mutations have no effect
     */
    public void setHomePos(@NotNull Vector3 pos) {
        homePos = new Vector3(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());
        homeDimension = level.getDimension();
        nbt.putDouble("HomeX", homePos.x).putDouble("HomeY", homePos.y).putDouble("HomeZ", homePos.z);
    }

    private static final Set<String> TEMPT_ITEMS = Set.of(
        BlockID.SEAGRASS
    );

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        TurtleGoalExecutor panic = new TurtleGoalExecutor(Goal.PANIC);
        TurtleGoalExecutor babyWater = new TurtleGoalExecutor(Goal.WATER);
        TurtleGoalExecutor water = new TurtleGoalExecutor(Goal.WATER);
        TurtleGoalExecutor land = new TurtleGoalExecutor(Goal.LAND);
        TurtleGoalExecutor lay = new TurtleGoalExecutor(Goal.LAY);
        TurtleGoalExecutor home = new TurtleGoalExecutor(() -> homePos.add(0.5, 0.5, 0.5), () -> wantsToLayEgg = true);
        return EntityAI.of(this)
                .behavior(panic)
                        .when(e -> isPanicking() && !isInsideOfWater() && panic.canStart(e))
                .behavior(new TurtleSwimExecutor(true))
                        .when(e -> isPanicking() && isInsideOfWater())
                .behavior(new FlatRandomRoamExecutor(0.12f, 12, 40, true, 100, false, 10))
                        .when(e -> isPanicking())
                .behavior(babyWater)
                        .when(e -> isBaby() && !isInsideOfWater() && babyWater.canStart(e))
                .behavior(home)
                        .when(e -> isPregnant() && !wantsToLayEgg && homeDimension == level.getDimension()
                                && home.canGoHome())
                .behavior(lay)
                        .when(e -> wantsToLayEgg && homeDimension == level.getDimension() && lay.canStart(e))
                .behavior(new TurtleBreedingExecutor())
                        .when(e -> !isBaby() && !isPregnant()
                                && getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE))
                .behavior(new TurtleTemptExecutor(TEMPT_ITEMS))
                        .when(e -> TemptExecutor.hasTemptingPlayer(e, false, 10, TEMPT_ITEMS))
                .behavior(water)
                        .when(e -> !isBaby() && !isInsideOfWater() && water.canStart(e))
                .behavior(land)
                        .when(e -> !isBaby() && isInsideOfWater() && land.canStart(e))
                .behavior(new TurtleSwimExecutor(false))
                        .when(e -> isInsideOfWater())
                .behavior(new TurtleLookExecutor())
                        .when(e -> getMemoryStorage().notEmpty(CoreMemoryTypes.NEAREST_PLAYER)
                                && ThreadLocalRandom.current().nextInt(100) < 2)
                        .evaluateOnce()
                .behavior(new FlatRandomRoamExecutor(0.1f, 12, 100, false, 100, true, 10))
                        .when(e -> !isInsideOfWater())
                .sensors(
                    new NearestPlayerSensor(6, 0, 20)
                )
                .controllers(
                    new TurtleLookController(),
                    new ConditionalController(
                        Pair.of(entity -> entity.isInsideOfWater(), new DiveController()),
                        Pair.of(entity -> entity.isTouchingWater(), new TurtleSwimController()),
                        Pair.of(entity -> !entity.isTouchingWater(), new WalkController())
                    )
                )
                .routeFinder(new TurtleRouteFinder(this))
                .build();
    }

    private boolean isPanicking() {
        return isOnFire() || level.getTick() - lastPanicTick <= 100;
    }

    private static final class TurtleSwimController extends SpaceMoveController {
        @Override
        public boolean control(EntityIntelligent entity) {
            Vector3 direction = entity.getMoveDirectionEnd();
            if (direction == null) return false;
            if (direction.distanceSquared(entity) < 0.000001) {
                needNewDirection(entity);
                return false;
            }
            return super.control(entity);
        }
    }

    private static final class TurtleLookController extends LookController {
        private TurtleLookController() {
            super(true, true);
        }

        @Override
        public boolean control(EntityIntelligent entity) {
            double yaw = entity.getYaw();
            boolean result = super.control(entity);
            double turn = Math.IEEEremainder(entity.getYaw() - yaw, 360);
            entity.setYaw(yaw + Math.max(-5, Math.min(5, turn)));
            return result;
        }
    }

    private static final class TurtleRouteFinder extends SimpleSpaceAStarRouteFinder {
        private TurtleRouteFinder(EntityTurtle turtle) {
            super(new TurtleSwimmingPosEvaluator(), turtle);
        }

        @Override
        protected boolean hasBarrier(Vector3 from, Vector3 to) {
            for (Vector3 pos : VectorMath.getPassByVector3(from, to)) {
                if (!evalPos(pos)) return true;
            }
            return false;
        }
    }

    private static final class TurtleSwimmingPosEvaluator extends SwimmingPosEvaluator {
        private final WalkingPosEvaluator walking = new WalkingPosEvaluator();

        @Override
        public boolean evalPos(@NotNull EntityIntelligent entity, @NotNull Vector3 pos) {
            if (!entity.level.isChunkLoaded(pos.getFloorX() >> 4, pos.getFloorZ() >> 4)) return false;
            Block below = entity.level.getTickCachedBlock(pos.getFloorX(), pos.getFloorY() - 1, pos.getFloorZ(), false);
            if (super.evalPos(entity, pos)) return true;
            if (below == null) return false;
            if (!(below instanceof BlockFlowingWater)) return walking.evalStandingBlock(entity, below);
            if (!entity.level.getTickCachedBlock(pos).isAir() || !isPassable(entity, pos)) return false;
            double radius = entity.getWidth() * entity.getScale() * 0.5;
            for (int dx = -1; dx <= 1; dx += 2) {
                for (int dz = -1; dz <= 1; dz += 2) {
                    Block support = entity.level.getTickCachedBlock((int) Math.floor(pos.x + dx * radius),
                            pos.getFloorY() - 1, (int) Math.floor(pos.z + dz * radius), false);
                    if (support != null && !(support instanceof BlockFlowingWater)
                            && walking.evalStandingBlock(entity, support)) return true;
                }
            }
            return false;
        }
    }
}
