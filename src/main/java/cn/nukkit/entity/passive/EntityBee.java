package cn.nukkit.entity.passive;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBeehive;
import cn.nukkit.block.BlockFlower;
import cn.nukkit.block.BlockWitherRose;
import cn.nukkit.blockentity.BlockEntityBeehive;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityFlyable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LiftController;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.SpaceMoveController;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.executor.BeeAttackExecutor;
import cn.nukkit.entity.ai.executor.MoveToTargetExecutor;
import cn.nukkit.entity.ai.executor.SpaceRandomRoamExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleSpaceAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.FlyingPosEvaluator;
import cn.nukkit.entity.ai.sensor.BeeMemorizedBlockSensor;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.data.property.BooleanEntityProperty;
import cn.nukkit.entity.data.property.EntityProperty;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Set;


public class EntityBee extends EntityAnimal implements EntityFlyable {
    public static final EntityProperty[] PROPERTIES = new EntityProperty[]{
        new BooleanEntityProperty("minecraft:has_nectar", false, true)
    };
    private static final String PROPERTY_HAS_NECTAR = "minecraft:has_nectar";
    private static final int POLLINATION_REQUIRED_TICKS = 400;

    @Override
    @NotNull public String getIdentifier() {
        return BEE;
    }

    public int dieInTicks = -1;

    private static final int NO_HOME = Integer.MIN_VALUE;
    private int homeHiveX = NO_HOME;
    private int homeHiveY = NO_HOME;
    private int homeHiveZ = NO_HOME;

    // Anti-stuck tracking for movement targets
    private int stuckTicksOnTarget = 0;
    private int lastTargetX = Integer.MIN_VALUE;
    private int lastTargetY = Integer.MIN_VALUE;
    private int lastTargetZ = Integer.MIN_VALUE;


    private int pollinationTicks = 0;
    private boolean pollinating = false;

    private int currentFlowerTicks = 0;
    private int currentFlowerX = Integer.MIN_VALUE;
    private int currentFlowerY = Integer.MIN_VALUE;
    private int currentFlowerZ = Integer.MIN_VALUE;

    public EntityBee(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public boolean hasHomeHive() {
        return this.homeHiveY != NO_HOME;
    }

    public void setHomeHive(BlockEntityBeehive beehive) {
        if (beehive == null || beehive.getBlock() == null) {
            homeHiveX = homeHiveY = homeHiveZ = NO_HOME;
            return;
        }
        Block block = beehive.getBlock();
        this.homeHiveX = block.getFloorX();
        this.homeHiveY = block.getFloorY();
        this.homeHiveZ = block.getFloorZ();
    }

    public Block getHomeHiveBlock() {
        if (!hasHomeHive() || this.level == null) return null;

        Block block = this.level.getBlock(homeHiveX, homeHiveY, homeHiveZ);
        if (!(block instanceof BlockBeehive)) {
            return null;
        }
        return block;
    }

    public boolean isPollinating() {
        return pollinating;
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(
                                new BeeAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.7f, 33, true, 20),
                                all(
                                        new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET),
                                        entity -> hasSting()
                                ),
                                6,
                                1
                        ),
                        new Behavior(
                                new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_BLOCK, 0.32f, true),
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_BLOCK),
                                4,
                                1
                        ),
                        new Behavior(
                                new SpaceRandomRoamExecutor(0.15f, 12, 100, 20, false, -1, true, 10),
                                (entity -> !(entity instanceof EntityBee bee) || !bee.isPollinating()),
                                1,
                                1
                        )
                ),
                Set.of(
                        new BeeMemorizedBlockSensor(32, 6, 20)
                ),
                Set.of(new SpaceMoveController(), new LookController(true, true), new LiftController()),
                new SimpleSpaceAStarRouteFinder(new FlyingPosEvaluator(), this),
                this
        );
    }

    public boolean hasSting() {
        return dieInTicks == -1;
    }

    public boolean hasNectar() {
        return this.getBooleanEntityProperty(PROPERTY_HAS_NECTAR);
    }

    public void setNectar(boolean hasNectar) {
        this.setBooleanEntityProperty(PROPERTY_HAS_NECTAR, hasNectar);
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.275f;
        }
        return 0.55f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.25f;
        }
        return 0.5f;
    }

    public boolean isAngry() {
        return getMemoryStorage().get(CoreMemoryTypes.IS_ANGRY);
    }

    public void setAngry(boolean angry) {
        getMemoryStorage().put(CoreMemoryTypes.IS_ANGRY, angry);
        setDataFlag(EntityFlag.ANGRY, angry);
    }

    public void setAngry(Entity entity) {
        setAngry(true);
        getMemoryStorage().put(CoreMemoryTypes.ATTACK_TARGET, entity);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (source.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
            if (ticksLived < 10) {
                source.setCancelled();
                return false;
            }
        }
        if (source instanceof EntityDamageByEntityEvent event) {
            for (Entity entity : getLevel().getCollidingEntities(this.getBoundingBox().grow(4, 4, 4))) {
                if (entity instanceof EntityBee bee && bee.hasSting()) {
                    bee.setAngry(event.getDamager());
                }
            }
        }
        return super.attack(source);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (!hasSting() && isAlive()) {
            dieInTicks--;
            if (dieInTicks < 0) {
                kill();
            }
        }

        if (currentTick % 20 == 0 && hasSting()) {
            boolean searchHive = shouldSearchBeehive();

            // Search a hive and know home, steer towards it first
            if (searchHive) {
                Block homeHive = getHomeHiveBlock();
                if (homeHive != null) {
                    double dx = (homeHive.getFloorX() + 0.5) - this.x;
                    double dy = (homeHive.getFloorY() + 0.5) - this.y;
                    double dz = (homeHive.getFloorZ() + 0.5) - this.z;
                    double distSq = dx * dx + dy * dy + dz * dz;

                    // If home hive is too far, forget it so bee can re-home
                    if (distSq > 128 * 128) {
                        this.homeHiveX = this.homeHiveY = this.homeHiveZ = NO_HOME;
                    } else {
                        // Push home hive into NEAREST_BLOCK so MoveToTarget can start flying back
                        this.getMemoryStorage().put(CoreMemoryTypes.NEAREST_BLOCK, homeHive);
                    }
                }
            }

            getMemoryStorage().put(CoreMemoryTypes.LOOKING_BLOCK, searchHive ? BlockBeehive.class : BlockFlower.class);
            Class<? extends Block> blockClass = this.getMemoryStorage().get(CoreMemoryTypes.LOOKING_BLOCK);
            Block nearestBlock = this.getMemoryStorage().get(CoreMemoryTypes.NEAREST_BLOCK);

            // Anti-stuck: if we chase the same target too long, clear it and let sensors pick another
            if (nearestBlock != null && blockClass != null) {
                int tx = nearestBlock.getFloorX();
                int ty = nearestBlock.getFloorY();
                int tz = nearestBlock.getFloorZ();

                if (tx == lastTargetX && ty == lastTargetY && tz == lastTargetZ) {
                    stuckTicksOnTarget += 20;
                } else {
                    lastTargetX = tx;
                    lastTargetY = ty;
                    lastTargetZ = tz;
                    stuckTicksOnTarget = 0;
                }

                int timeoutTicks = blockClass.isAssignableFrom(BlockFlower.class) ? 120 : 400;
                if (stuckTicksOnTarget > timeoutTicks) {
                    this.getMemoryStorage().clear(CoreMemoryTypes.NEAREST_BLOCK);
                    stuckTicksOnTarget = 0;
                    lastTargetX = lastTargetY = lastTargetZ = Integer.MIN_VALUE;
                }
            } else {
                stuckTicksOnTarget = 0;
                lastTargetX = lastTargetY = lastTargetZ = Integer.MIN_VALUE;
            }

            if (blockClass == null) {
                return super.onUpdate(currentTick);
            }

            if (blockClass.isAssignableFrom(BlockFlower.class)) {
                Block[] collisions = level.getCollisionBlocks(getBoundingBox().grow(1.5, 1.5, 1.5), false, true);
                BlockFlower flower = (BlockFlower) Arrays.stream(collisions)
                        .filter(block -> block instanceof BlockFlower)
                        .findAny()
                        .orElse(null);

                if (flower instanceof BlockWitherRose) {
                    pollinationTicks = 0;
                    pollinating = false;
                    currentFlowerTicks = 0;
                    currentFlowerX = currentFlowerY = currentFlowerZ = Integer.MIN_VALUE;
                    this.kill();
                } else if (flower != null) {
                    int fx = flower.getFloorX();
                    int fy = flower.getFloorY();
                    int fz = flower.getFloorZ();

                    // Track how long to stay on flower
                    if (fx == currentFlowerX && fy == currentFlowerY && fz == currentFlowerZ) {
                        currentFlowerTicks += 20;
                    } else {
                        currentFlowerX = fx;
                        currentFlowerY = fy;
                        currentFlowerZ = fz;
                        currentFlowerTicks = 20;
                    }

                    boolean stillPollinatingHere = !this.hasNectar() && currentFlowerTicks <= 120; // 6s on same flower
                    if (stillPollinatingHere) {
                        pollinating = true;

                        // Park above the flower
                        double px = fx + 0.5;
                        double py = fy + 0.8;
                        double pz = fz + 0.5;
                        this.setLookTarget(new Vector3(px, py, pz));
                        this.setMoveTarget(new Vector3(px, py, pz));

                        if (!this.hasNectar()) {
                            pollinationTicks += 20;
                            if (pollinationTicks >= POLLINATION_REQUIRED_TICKS) {
                                this.setNectar(true);
                                this.getLevel().addSound(this, Sound.MOB_BEE_POLLINATE);
                                pollinationTicks = 0;
                            }
                        }
                    } else {
                        // Time on this flower exceeded and still no nectar pick a new flower
                        pollinating = false;
                        this.getMemoryStorage().clear(CoreMemoryTypes.NEAREST_BLOCK);
                        currentFlowerTicks = 0;
                        currentFlowerX = currentFlowerY = currentFlowerZ = Integer.MIN_VALUE;
                    }
                } else {
                    // No flower under bee
                    pollinating = false;
                    if (this.hasNectar()) pollinationTicks = 0;
                    currentFlowerTicks = 0;
                    currentFlowerX = currentFlowerY = currentFlowerZ = Integer.MIN_VALUE;
                }
            } else if (blockClass.isAssignableFrom(BlockBeehive.class)) {
                pollinating = false;
                Arrays.stream(level.getCollisionBlocks(getBoundingBox().grow(1.5, 1.5, 1.5), false, true))
                        .filter(block -> {
                            if (block instanceof BlockBeehive hive) {
                                BlockEntityBeehive hiveEntity = hive.getOrCreateBlockEntity();
                                int occupants = hiveEntity.getOccupantsCount();
                                boolean canEnter = occupants < 3;
                                return canEnter;
                            }
                            return false;
                        })
                        .findAny().ifPresent(block -> {
                            BlockEntityBeehive hiveEntity = ((BlockBeehive) block).getOrCreateBlockEntity();
                            hiveEntity.addOccupant(this);
                        });
            }
        }
        return super.onUpdate(currentTick);
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(10);
        super.initEntity();

        if (this.namedTag.contains("HomeHiveX")) {
            this.homeHiveX = this.namedTag.getInt("HomeHiveX");
            this.homeHiveY = this.namedTag.getInt("HomeHiveY");
            this.homeHiveZ = this.namedTag.getInt("HomeHiveZ");
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        if (hasHomeHive()) {
            this.namedTag.putInt("HomeHiveX", homeHiveX);
            this.namedTag.putInt("HomeHiveY", homeHiveY);
            this.namedTag.putInt("HomeHiveZ", homeHiveZ);
        } else {
            this.namedTag.remove("HomeHiveX");
            this.namedTag.remove("HomeHiveY");
            this.namedTag.remove("HomeHiveZ");
        }
    }

    public void nectarDelivered(BlockEntityBeehive blockEntityBeehive) {
        this.setNectar(false);
        if (blockEntityBeehive != null) {
            setHomeHive(blockEntityBeehive);
        }
    }

    public void leftBeehive(BlockEntityBeehive blockEntityBeehive) {
        if (blockEntityBeehive != null) {
            setHomeHive(blockEntityBeehive);
        }
    }

    public boolean shouldSearchBeehive() {
        return this.hasNectar() || getLevel().isRaining() || !getLevel().isDay();
    }

    @Override
    public String getOriginalName() {
        return "Bee";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("bee", "mob", "arthropod", "pacified");
    }

    @Override
    public boolean isPersistent() {
        return true;
    }
}
