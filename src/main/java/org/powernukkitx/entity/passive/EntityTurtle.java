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
import org.powernukkitx.block.BlockTurtleEgg;
import org.powernukkitx.block.property.enums.TurtleEggCount;
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
import org.powernukkitx.entity.ai.executor.BreedingExecutor;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.IBehaviorExecutor;
import org.powernukkitx.entity.ai.executor.LookAtTargetExecutor;
import org.powernukkitx.entity.ai.executor.SpaceRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.TemptExecutor;
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
import org.powernukkitx.event.entity.EntityBlockChangeEvent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.particle.DestroyBlockParticle;
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
        TurtleGoal panic = new TurtleGoal(Goal.PANIC);
        TurtleGoal babyWater = new TurtleGoal(Goal.WATER);
        TurtleGoal water = new TurtleGoal(Goal.WATER);
        TurtleGoal land = new TurtleGoal(Goal.LAND);
        TurtleGoal lay = new TurtleGoal(Goal.LAY);
        TurtleGoal home = new TurtleGoal(Goal.HOME);
        return EntityAI.of(this)
                .behavior(panic)
                        .when(e -> isPanicking() && !isInsideOfWater() && panic.canStart())
                .behavior(new TurtleSwimExecutor(true))
                        .when(e -> isPanicking() && isInsideOfWater())
                .behavior(new FlatRandomRoamExecutor(0.12f, 12, 40, true, 100, false, 10))
                        .when(e -> isPanicking())
                .behavior(babyWater)
                        .when(e -> isBaby() && !isInsideOfWater() && babyWater.canStart())
                .behavior(home)
                        .when(e -> isPregnant() && !wantsToLayEgg && homeDimension == level.getDimension()
                                && home.canGoHome())
                .behavior(lay)
                        .when(e -> wantsToLayEgg && homeDimension == level.getDimension() && lay.canStart())
                .behavior(new TurtleBreedingExecutor())
                        .when(e -> !isBaby() && !isPregnant()
                                && getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE))
                .behavior(new TurtleTemptExecutor())
                        .when(e -> TemptExecutor.hasTemptingPlayer(e, false, 10, TEMPT_ITEMS))
                .behavior(water)
                        .when(e -> !isBaby() && !isInsideOfWater() && water.canStart())
                .behavior(land)
                        .when(e -> !isBaby() && isInsideOfWater() && land.canStart())
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

    private enum Goal { PANIC, WATER, HOME, LAY, LAND }

    private final class TurtleGoal implements IBehaviorExecutor {
        private final Goal goal;
        private Vector3 target;
        private int nextSearch;
        private int diggingTicks;
        private int elapsed;
        private boolean goingHome;

        private TurtleGoal(Goal goal) {
            this.goal = goal;
        }

        private boolean canGoHome() {
            if (!goingHome && ThreadLocalRandom.current().nextInt(700) == 0) goingHome = true;
            return goingHome;
        }

        private boolean canStart() {
            if (target != null && validTarget(target)) return true;
            target = null;
            resetDigging();
            if (level.getTick() < nextSearch) return false;
            nextSearch = level.getTick() + 100;
            target = findTarget();
            return target != null;
        }

        @Override
        public boolean execute(EntityIntelligent entity) {
            elapsed++;
            if (goal == Goal.HOME) {
                if (target == null || elapsed % 40 == 0) target = homePos.add(0.5, 0.5, 0.5);
                if (distanceSquared(target) <= 16) {
                    wantsToLayEgg = true;
                    return false;
                }
            } else {
                if (target != null && !validTarget(target)) {
                    target = null;
                    resetDigging();
                }
                if (target == null && !canStart()) return false;
                double radius = goal == Goal.WATER ? (isBaby() ? 0.1 : 1.5) : goal == Goal.LAY ? 1.5 : 0.5;
                if (distanceSquared(target) <= radius * radius) {
                    if (goal != Goal.LAY && (goal != Goal.WATER || isInsideOfWater())) return false;
                    if (goal == Goal.LAY && !isInsideOfWater() && onGround) return dig();
                }
                if (goal == Goal.LAY) resetDigging();
            }
            if (goal != Goal.HOME && !level.isChunkLoaded(target.getFloorX() >> 4, target.getFloorZ() >> 4)) return false;
            var finder = getBehaviorGroup().getRouteFinder();
            if (goal != Goal.HOME && elapsed > 20 && finder.isFinished() && !finder.isReachable()
                    && target.equals(finder.getTarget())) {
                nextSearch = level.getTick() + 100;
                return false;
            }
            if (getMoveTarget() == null || !getMoveTarget().equals(target)) {
                getBehaviorGroup().setForceUpdateRoute(true);
            }
            setMoveTarget(target);
            setLookTarget(target);
            setMovementSpeed((isInsideOfWater() ? getDefaultUnderWaterSpeed() : 0.1f) * (goal == Goal.PANIC ? 1.2f : 1f));
            if (elapsed % 20 == 0) getBehaviorGroup().setForceUpdateRoute(true);
            return true;
        }

        private boolean dig() {
            setMoveTarget(null);
            setDataFlag(ActorFlags.LAYING_EGG, true);
            if (++diggingTicks % 5 == 0) {
                level.addParticle(new DestroyBlockParticle(target, level.getBlock(target.add(0, -1, 0))));
            }
            if (diggingTicks <= 200) return true;
            Block from = level.getBlock(target);
            BlockTurtleEgg eggs = new BlockTurtleEgg();
            eggs.setEggCount(TurtleEggCount.values()[ThreadLocalRandom.current().nextInt(4)]);
            EntityBlockChangeEvent event = new EntityBlockChangeEvent(EntityTurtle.this, from, eggs);
            getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled() && validTarget(target) && level.setBlock(target, eggs, true, true)) {
                level.addSound(target, Sound.BLOCK_TURTLE_EGG_DROP);
                setPregnant(false);
            }
            nextSearch = level.getTick() + 100;
            return false;
        }

        private Vector3 findTarget() {
            int range = goal == Goal.PANIC ? 10 : goal == Goal.WATER && isBaby() ? 15 : 16;
            int height = goal == Goal.LAY || goal == Goal.PANIC ? 4 : 5;
            Vector3 best = null;
            double nearest = Double.MAX_VALUE;
            for (int dx = -range; dx <= range; dx++) {
                for (int dz = -range; dz <= range; dz++) {
                    int bx = getFloorX() + dx;
                    int bz = getFloorZ() + dz;
                    if (!level.isChunkLoaded(bx >> 4, bz >> 4)) continue;
                    for (int dy = -height; dy <= height; dy++) {
                        double cy = getFloorY() + dy;
                        double distance = (bx + 0.5 - x) * (bx + 0.5 - x)
                                + (cy - y) * (cy - y) + (bz + 0.5 - z) * (bz + 0.5 - z);
                        if (distance >= nearest) continue;
                        Vector3 candidate = new Vector3(bx + 0.5, cy, bz + 0.5);
                        if (validTarget(candidate)) {
                            best = candidate;
                            nearest = distance;
                        }
                    }
                }
            }
            return best;
        }

        private boolean validTarget(Vector3 pos) {
            if (!level.isChunkLoaded(pos.getFloorX() >> 4, pos.getFloorZ() >> 4)) return false;
            Block block = level.getBlock(pos);
            if (goal == Goal.WATER || goal == Goal.PANIC) {
                if (!(block instanceof BlockFlowingWater)) return false;
                double dx = pos.x - x;
                double dz = pos.z - z;
                int stepX = Math.abs(dx) >= Math.abs(dz) ? (dx >= 0 ? 1 : -1) : 0;
                int stepZ = stepX == 0 ? (dz >= 0 ? 1 : -1) : 0;
                return level.getBlock(pos.add(-stepX, 0, -stepZ), false) instanceof BlockFlowingWater;
            }
            if (!block.isAir() || !level.getBlock(pos, 1).isAir()) return false;
            Block below = level.getBlock(pos.add(0, -1, 0));
            return goal == Goal.LAY ? BlockID.SAND.equals(below.getId()) : below.isSolid();
        }

        private void resetDigging() {
            diggingTicks = 0;
            if (goal == Goal.LAY) setDataFlag(ActorFlags.LAYING_EGG, false);
        }

        @Override
        public void onStop(EntityIntelligent entity) {
            target = null;
            elapsed = 0;
            goingHome = false;
            resetDigging();
            setMoveTarget(null);
            setLookTarget(null);
            setMovementSpeed(getMovementSpeedDefault());
        }

        @Override
        public void onInterrupt(EntityIntelligent entity) {
            onStop(entity);
        }
    }

    private static final class TurtleLookExecutor extends LookAtTargetExecutor {
        private TurtleLookExecutor() {
            super(CoreMemoryTypes.NEAREST_PLAYER, 100);
        }

        @Override
        public boolean execute(EntityIntelligent entity) {
            Player player = entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_PLAYER);
            return player != null && player.isAlive() && entity.distanceSquared(player) <= 36
                    && super.execute(entity);
        }

        @Override
        public void onStop(EntityIntelligent entity) {
            super.onStop(entity);
            removeLookTarget(entity);
        }

        @Override
        public void onInterrupt(EntityIntelligent entity) {
            super.onInterrupt(entity);
            removeLookTarget(entity);
        }
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

    private static final class TurtleTemptExecutor extends TemptExecutor {
        private TurtleTemptExecutor() {
            super(1.1f, true, false, false, 10, 2.0f, null, TEMPT_ITEMS);
        }

        @Override
        public boolean execute(@NotNull EntityIntelligent entity) {
            boolean result = super.execute(entity);
            if (result && entity.isInsideOfWater()) {
                entity.setMovementSpeed(entity.getDefaultUnderWaterSpeed() * 1.1f);
            }
            return result;
        }
    }

    private static final class TurtleSwimExecutor extends SpaceRandomRoamExecutor {
        private final float speedMultiplier;
        private final SwimmingPosEvaluator swimPositions = new SwimmingPosEvaluator();

        private TurtleSwimExecutor(boolean panicking) {
            super(panicking ? 0.144f : 0.12f, panicking ? 5 : 30, panicking ? 4 : 15,
                    panicking ? 40 : Integer.MAX_VALUE - 1, true, panicking ? 100 : 160, false, 10);
            speedMultiplier = panicking ? 1.2f : 1;
        }

        @Override
        protected boolean needUpdateTarget(EntityIntelligent entity) {
            return super.needUpdateTarget(entity) || entity.distanceSquared(entity.getMoveTarget()) <= 1;
        }

        @Override
        protected @Nullable Vector3 next(EntityIntelligent entity) {
            var random = ThreadLocalRandom.current();
            for (int i = 0; i < 10; i++) {
                int x = entity.getFloorX() + random.nextInt(-maxRoamRange, maxRoamRange + 1);
                int z = entity.getFloorZ() + random.nextInt(-maxRoamRange, maxRoamRange + 1);
                if (!entity.level.isChunkLoaded(x >> 4, z >> 4)) continue;
                for (int dy = 0; dy <= maxYRoamRange * 2; dy++) {
                    int y = entity.getFloorY() + (dy % 2 == 0 ? dy / 2 : -(dy + 1) / 2);
                    Vector3 pos = new Vector3(x + 0.5, y + 0.5, z + 0.5);
                    if (isSwimTarget(entity, pos)) return pos;
                }
            }
            Vector3 target = null;
            int count = 0;
            for (int dx = -4; dx <= 4; dx++) {
                for (int dz = -4; dz <= 4; dz++) {
                    for (int dy = -2; dy <= 2; dy++) {
                        Vector3 pos = new Vector3(entity.getFloorX() + dx + 0.5,
                                entity.getFloorY() + dy + 0.5, entity.getFloorZ() + dz + 0.5);
                        if (isSwimTarget(entity, pos) && random.nextInt(++count) == 0) target = pos;
                    }
                }
            }
            return target;
        }

        private boolean isSwimTarget(EntityIntelligent entity, Vector3 pos) {
            return entity.distanceSquared(pos) >= 1
                    && entity.level.isChunkLoaded(pos.getFloorX() >> 4, pos.getFloorZ() >> 4)
                    && entity.level.getBlock(pos, false) instanceof BlockFlowingWater
                    && swimPositions.evalPos(entity, pos);
        }

        @Override
        public boolean execute(@NotNull EntityIntelligent entity) {
            durationTick++;
            if (needUpdateTarget(entity) || durationTick > runningTime) {
                Vector3 target = next(entity);
                if (target == null) {
                    stop(entity);
                    return false;
                }
                setRouteTarget(entity, target);
                setLookTarget(entity, target);
                entity.getBehaviorGroup().setForceUpdateRoute(true);
                durationTick = 0;
            }
            entity.setMovementSpeed(entity.getDefaultUnderWaterSpeed() * speedMultiplier);
            return true;
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

    private static final class TurtleBreedingExecutor extends BreedingExecutor {
        private TurtleBreedingExecutor() {
            super(8, 60, 0.1f);
        }

        @Override
        public boolean execute(EntityIntelligent entity) {
            EntityIntelligent spouse = getSpouse(entity);
            if (spouse != null) {
                if (!spouse.isAlive() || spouse.isPregnant()
                        || !spouse.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE)) {
                    onInterrupt(entity);
                    return false;
                }
                if (entity.distanceSquared(spouse) > 9) setBreedingTick(entity, -1);
            }
            boolean result = super.execute(entity);
            if (result && entity.isInsideOfWater()) entity.setMovementSpeed(entity.getDefaultUnderWaterSpeed());
            return result;
        }

        @Override
        protected boolean isValidMatePair(EntityIntelligent a, BreedableComponent ac, EntityIntelligent b, BreedableComponent bc) {
            return a.isAlive() && b.isAlive() && !a.isPregnant() && !b.isPregnant()
                    && super.isValidMatePair(a, ac, b, bc);
        }

        @Override
        protected void breed(EntityIntelligent a, EntityIntelligent b) {
            if (!a.isAlive() || !b.isAlive() || a.distanceSquared(b) > 9 || a.isPregnant() || b.isPregnant()) return;
            super.breed(a, b);
            a.level.dropExpOrb(a, ThreadLocalRandom.current().nextInt(1, 8));
        }
    }
}
