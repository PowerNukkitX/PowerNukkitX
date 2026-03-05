package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityFlyable;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LiftController;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.SpaceMoveController;
import cn.nukkit.entity.ai.evaluator.ProbabilityEvaluator;
import cn.nukkit.entity.ai.executor.AnimalGrowExecutor;
import cn.nukkit.entity.ai.executor.FloatTemptExecutor;
import cn.nukkit.entity.ai.executor.HoverRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.LookAtTargetExecutor;
import cn.nukkit.entity.ai.executor.MoveToTargetExecutor;
import cn.nukkit.entity.ai.executor.TemptExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleSpaceAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.HoveringPosEvaluator;
import cn.nukkit.entity.ai.sensor.ISensor;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.components.AgeableComponent;
import cn.nukkit.entity.components.HomeComponent;
import cn.nukkit.entity.components.RideableComponent;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.data.property.BooleanEntityProperty;
import cn.nukkit.entity.data.property.EntityProperty;
import cn.nukkit.inventory.EntityArmorInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.*;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.Utils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * @author KeksDev
 */
@Slf4j
public class EntityHappyGhast extends EntityAnimal implements EntityFlyable, InventoryHolder {
    private static final String PROP_CAN_MOVE = "minecraft:can_move";
    public static final EntityProperty[] PROPERTIES = new EntityProperty[]{
        new BooleanEntityProperty(PROP_CAN_MOVE, true, true)
    };
    public EntityProperty[] getEntityProperties() { return PROPERTIES; }
    

    @Override
    @NotNull
    public String getIdentifier() {
        return HAPPY_GHAST;
    }

    public EntityHappyGhast(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    private EntityArmorInventory armorInventory;
    private volatile boolean playerOnTopCached;
    private volatile boolean hasPassengers;
    private Vector3 frozenAnchorPos;
    private int dismountUnlockDelayTicks = 0;

    @Override
    public String getOriginalName() {
        return "Happy Ghast";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("happy_ghast", "mob");
    }

    @Override
    public AgeableComponent getAgeable() {
        return new AgeableComponent(
                null,
                1200f,
                List.of(
                    new AgeableComponent.FeedItem(ItemID.SNOWBALL)
                ),
                null,
                null,
                null
        );
    }

    @Override
    public @Nullable HomeComponent getHome() {
        return new HomeComponent(
                    32,
                    HomeComponent.RestrictionType.RANDOM_MOVEMENT
                );
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        setDataFlag(EntityFlag.COLLIDABLE, true); //allow standing on them
        this.armorInventory = new EntityArmorInventory(this);
        if (this.namedTag.contains("Armor")) {
            ListTag<CompoundTag> armorList = this.namedTag.getList("Armor", CompoundTag.class);
            for (CompoundTag armorTag : armorList.getAll()) {
                int slot = armorTag.getByte("Slot");
                var item = NBTIO.getItemHelper(armorTag);

                this.armorInventory.setItem(slot, item);
                if (!item.isNull()) this.setInputControls(true);
            }
        }

        // Init home memory
        if (this.namedTag.contains("HomeX")) {
            this.initHome();
        } else {
            this.setHomePosition();
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        if (this.armorInventory != null) {
            ListTag<CompoundTag> armorTag = new ListTag<>();
            for (int i = 0; i < 5; i++) {
                armorTag.add(NBTIO.putItemHelper(this.armorInventory.getItem(i), i));
            }
            this.namedTag.putList("Armor", armorTag);
        }
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public float getDefaultSpeed() {
        if (this.isBaby()) return 0.03f;
        return 0.016f;
    }

    @Override
    public float getDefaultFlyingSpeed() {
        if (this.isBaby()) return 0.0833333f;
        return 0.016f;
    }

    @Override
    public boolean isRideable() {
        if (this.isHarnessed()) return true;
        return false;
    }

    @Override
    public RideableComponent getRideableData() {
        if (this.isHarnessed()) return new RideableComponent(
                0,
                true,
                RideableComponent.DismountMode.ON_TOP_CENTER,
                Set.of("player"),
                "action.interact.ride.horse",
                0.0f,
                false,
                false,
                4,
                List.of(
                        new RideableComponent.Seat(
                            0,
                            4,
                            new Vector3f( 0.0f, 3.8f,  1.7f),
                            181.0f,
                            null,
                            8.0f,
                            6.0f
                        ),
                        new RideableComponent.Seat(
                            1,
                            4,
                            new Vector3f(-1.7f, 3.8f,  0.0f),
                            181.0f,
                            null,
                            8.0f,
                            6.0f
                        ),
                        new RideableComponent.Seat(
                            2,
                            4,
                            new Vector3f( 0.0f, 3.8f, -1.7f),
                            181.0f,
                            null,
                            8.0f,
                            6.0f
                        ),
                        new RideableComponent.Seat(
                            3,
                            4,
                            new Vector3f( 1.7f, 3.8f,  0.0f),
                            181.0f,
                            null,
                            8.0f,
                            6.0f
                        )
                )
        );
        return null;
    }

    @Override
    public RideableComponent.InputType getInputControlType() {
        return RideableComponent.InputType.AIR;
    }

    @Override
    public float getKnockbackResistance() {
        if (this.canMove()) return 0.0f;
        return 1.0f;
    }

    @Override
    public boolean canBePushedByEntities() {
        if (this.canMove()) return true;
        return false;
    }

    @Override
    public boolean canBePushedByPiston() {
        if (this.canMove()) return true;
        return false;
    }

    @Override
    public float getWidth() {
        return isBaby() ? 0.95f : 4f;
    }

    @Override
    public float getHeight() {
        return isBaby() ? 0.95f : 4f;
    }

     @Override
    public boolean hasGravity() {
        return false;
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        return new Item[]{getHarness()};
    }

    @Override
    public Integer getExperienceDrops() {
        return Utils.rand(1, 3);
    }

    @Override
    public Inventory getInventory() {
        return armorInventory;
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        boolean superResult = super.onInteract(player, item, clickedPos);
        if (superResult) return true;

        if (this.isBaby()) return false;

        if (!item.isNull()) {
            if (item instanceof ItemHarness armor && armorInventory.getBody().isNull()) {
                armorInventory.setBody(armor);
                armorInventory.sendContents(player);
                this.setInputControls(true);
                this.setHomePosition();
                return true;

            } else if (item instanceof ItemShears && !armorInventory.getBody().isNull()) {
                Item body = armorInventory.getBody();
                if (player.getInventory().canAddItem(body)) {
                    player.getInventory().addItem(body);
                } else {
                    this.getLevel().dropItem(clickedPos, body);
                }
                armorInventory.setBody(Item.AIR);
                armorInventory.sendContents(player);
                this.setInputControls(false);
                this.setHomePosition();
                return true;
            }
        }

        if (this.isHarnessed() && mountEntity(player, true)) {
            this.hasPassengers = true;
            return false;
        }

        return false;
    }

    @Override
    public boolean dismountEntity(Entity entity, boolean sendLinks) {
        boolean result = super.dismountEntity(entity, sendLinks);

        if (result && this.passengers.isEmpty()) {
            this.setHomePosition();
            this.hasPassengers = false;
            this.dismountUnlockDelayTicks = 10;
        }
        return result;
    }

    @Override
    public void spawnTo(Player player) {
        super.spawnTo(player);
        armorInventory.sendContents(player);
    }

    public boolean isHarnessed() {
        return this.armorInventory != null && !this.armorInventory.getBody().isNull();
    }

    public int roamDistance() {
        if (this.isHarnessed() || this.isBaby()) return 32;
        return 64;
    }

    public Item getHarness() {
        return armorInventory.getBody();
    }

    public boolean canMove() {
        return this.getBooleanEntityProperty(PROP_CAN_MOVE);
    }

    public void setCanMove(boolean value) {
        if (this.getBooleanEntityProperty(PROP_CAN_MOVE) == value) return;
        this.setBooleanEntityProperty(PROP_CAN_MOVE, value);
    }

    private static float snapYawToCardinal(double yaw) {
        double y = yaw % 360f;
        if (y < 0f) y += 360f;
        return Math.round(y / 90f) * 90f;
    }

    private void applyMobilityFromTopPresence(boolean playerOnTop) {
        boolean mounted = this.hasPassengers;
        boolean shouldMove = mounted || !playerOnTop;

        if (!shouldMove) {
            if (this.frozenAnchorPos == null) {
                this.frozenAnchorPos = new Vector3(this.getX(), this.getY(), this.getZ());
            }

            // Snap yaw to cardinal
            float snapped = snapYawToCardinal(this.getYaw());
            this.setRotation(snapped, 0f);
            this.setHeadYaw(snapped);
            this.setPitch(0f);

            // Clear AI intentions
            this.setMoveTarget(null);
            this.setLookTarget(null);

            // Kill direction vectors used by controllers
            this.getMemoryStorage().clear(CoreMemoryTypes.MOVE_DIRECTION_START);
            this.getMemoryStorage().clear(CoreMemoryTypes.MOVE_DIRECTION_END);
            this.getMemoryStorage().put(CoreMemoryTypes.SHOULD_UPDATE_MOVE_DIRECTION, false);

            // Hard pin to anchor
            this.setMotion(new Vector3(0, 0, 0));
            this.setMovementSpeed(0f);
            this.setPosition(this.frozenAnchorPos);

            this.getBehaviorGroup().setForceUpdateRoute(true);
            this.setCanMove(false);
            return;
        }

        // Mobile again
        if (this.canMove()) return;

        this.frozenAnchorPos = null;
        this.setCanMove(true);

        this.setMoveTarget(null);
        this.setLookTarget(null);

        this.getMemoryStorage().clear(CoreMemoryTypes.MOVE_DIRECTION_START);
        this.getMemoryStorage().clear(CoreMemoryTypes.MOVE_DIRECTION_END);
        this.getMemoryStorage().put(CoreMemoryTypes.SHOULD_UPDATE_MOVE_DIRECTION, true);

        this.getBehaviorGroup().setForceUpdateRoute(true);
        this.setMovementSpeed(this.getDefaultSpeed());
    }


    @Override
    public boolean onUpdate(int currentTick) {
        boolean updated = super.onUpdate(currentTick);
        boolean mounted = this.passengers != null && !this.passengers.isEmpty();
        this.hasPassengers = mounted;
        boolean standingOnTop = false;

        if (!mounted) {
            final double topY = this.getY() + this.getHeight();

            final double half = this.getWidth() * 0.5;
            final double pad = 0.10;

            final double minY = topY - 0.35;
            final double maxY = topY + 2.20;

            AxisAlignedBB box = new SimpleAxisAlignedBB(
                this.getX() - half + pad,
                minY,
                this.getZ() - half + pad,
                this.getX() + half - pad,
                maxY,
                this.getZ() + half - pad
            );

            for (Entity e : this.level.getNearbyEntitiesSafe(box, this)) {
                if (!(e instanceof Player p) || !p.isAlive()) continue;

                AxisAlignedBB pbb = p.getBoundingBox();
                if (pbb == null) continue;

                double feetY = pbb.getMinY();

                if (feetY >= topY - 0.45 && feetY <= topY + 2.00) {
                    standingOnTop = true;
                    break;
                }
            }
        }

        this.playerOnTopCached = mounted || standingOnTop;
        return updated;
    }

    private static final Set<String> TEMPT_ITEMS = Set.of(
        ItemID.SNOWBALL,
        ItemID.HARNESS_BLACK,
        ItemID.HARNESS_BLUE,
        ItemID.HARNESS_BROWN,
        ItemID.HARNESS_CYAN,
        ItemID.HARNESS_GRAY,
        ItemID.HARNESS_GREEN,
        ItemID.HARNESS_LIGHT_BLUE,
        ItemID.HARNESS_LIGHT_GRAY,
        ItemID.HARNESS_LIME,
        ItemID.HARNESS_MAGENTA,
        ItemID.HARNESS_ORANGE,
        ItemID.HARNESS_PINK,
        ItemID.HARNESS_PURPLE,
        ItemID.HARNESS_RED,
        ItemID.HARNESS_WHITE,
        ItemID.HARNESS_YELLOW
    );

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(
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
                    new Behavior( // Return home if too far
                        new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_BLOCK, this.getDefaultFlyingSpeed() * 8f, true),
                        entity -> {
                            EntityHappyGhast g = (EntityHappyGhast) entity;

                            if (!g.canMove()) return false;
                            if (g.hasPassengers) return false;

                            Block home = entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_BLOCK);
                            if (home == null) return false;

                            double max = g.roamDistance();
                            return entity.distanceSquared(home) > (max * max);
                        },
                        4, 1
                    ),
                    new Behavior(
                        new FloatTemptExecutor(true, 16, 7.0f, TEMPT_ITEMS),
                            entity -> {
                                EntityHappyGhast g = (EntityHappyGhast) entity;
                                if (!g.canMove()) return false;
                                if (g.hasPassengers) return false;
                                if (g.dismountUnlockDelayTicks > 0) return false;
                                return TemptExecutor.hasTemptingPlayer(entity, true, 16, TEMPT_ITEMS);
                            },
                        3, 1
                    ),
                    new Behavior( // Hover roam near home
                        new HoverRandomRoamExecutor(
                            this.getDefaultFlyingSpeed(),
                            roamDistance(),
                            10, // TODO: Supposed to be 16, as we dont have leashable reduced a bit to not lost it
                            (this.isBaby()) ? -1 : 0,
                            (this.isBaby()) ? 1 : 6,
                            (this.isBaby()) ? 4 : 14,
                            160
                        ),
                        entity -> {
                            EntityHappyGhast g = (EntityHappyGhast) entity;
                            if (!g.canMove()) return false;
                            if (g.hasPassengers) return false;
                            return true;
                        },
                        2, 1
                    ),
                    new Behavior( // Look at nearest player
                        new LookAtTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 100),
                            all(
                                new ProbabilityEvaluator(4, 10),
                                e -> ((EntityHappyGhast) e).getMemoryStorage().notEmpty(CoreMemoryTypes.NEAREST_PLAYER),
                                e -> {
                                    EntityHappyGhast h = (EntityHappyGhast) e;
                                    Player p = h.getMemoryStorage().get(CoreMemoryTypes.NEAREST_PLAYER);
                                    return p != null && !h.isPassenger(p);
                                },
                                e -> {
                                    EntityHappyGhast g = (EntityHappyGhast) e;
                                    return !g.hasPassengers && g.dismountUnlockDelayTicks <= 0;
                                }
                            ),
                        1, 1, 100
                    )
                ),
                Set.of(
                    new NearestPlayerSensor(56, 0, 20),
                    // Ensure HOME memory exists + stays valid
                    new ISensor() {
                        @Override
                        public void sense(EntityIntelligent entity) {
                            EntityHappyGhast g = (EntityHappyGhast) entity;
                            if (entity.getMemoryStorage().isEmpty(CoreMemoryTypes.NEAREST_BLOCK)) g.setHomePosition();
                        }

                        @Override
                        public int getPeriod() {
                            return 60;
                        }
                    },
                    new ISensor() {
                        @Override
                        public void sense(EntityIntelligent entity) {
                            EntityHappyGhast g = (EntityHappyGhast) entity;
                            if (g.dismountUnlockDelayTicks > 0 && !g.hasPassengers) g.dismountUnlockDelayTicks--;
                            g.applyMobilityFromTopPresence(g.playerOnTopCached);
                        }

                        @Override
                        public int getPeriod() {
                            return 1;
                        }
                    }
                ),
                Set.of(
                    new SpaceMoveController(),
                    new LookController(
                        () -> this.canMove() && !this.hasPassengers && this.dismountUnlockDelayTicks <= 0,
                        () -> this.canMove() && !this.hasPassengers && this.dismountUnlockDelayTicks <= 0
                    ),
                    new LiftController()
                ),
                new SimpleSpaceAStarRouteFinder(new HoveringPosEvaluator(), this),
                this
        );
    }

}
