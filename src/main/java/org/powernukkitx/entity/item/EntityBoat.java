package org.powernukkitx.entity.item;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockBubbleColumn;
import org.powernukkitx.block.BlockFlowingWater;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityHuman;
import org.powernukkitx.entity.components.RideableComponent;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.event.vehicle.VehicleMoveEvent;
import org.powernukkitx.event.vehicle.VehicleUpdateEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.level.GameRule;
import org.powernukkitx.level.Location;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.Vector2;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.math.Vector3f;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.protocol.bedrock.data.ActorLinkType;
import org.cloudburstmc.protocol.bedrock.data.InputInteractionModel;
import org.cloudburstmc.protocol.bedrock.data.InputMode;
import org.cloudburstmc.protocol.bedrock.data.PlayerAuthInputData;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorLink;
import org.cloudburstmc.protocol.bedrock.packet.AddActorPacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Set;

/**
 * @author yescallop
 * @since 2016/2/13
 */
public class EntityBoat extends EntityVehicle {
    @Override
    @NotNull
    public String getIdentifier() {
        return BOAT;
    }

    public static final double SINKING_DEPTH = 0.3;
    private static final double EQUILIBRIUM = -0.01;
    private double bobbingPhase = 0;
    //paddle animation states
    private float paddleTimeLeft = 0f;
    private float paddleTimeRight = 0f;
    public int woodID;
    protected boolean sinking = true;
    private int ticksInWater;

    /**
     * How long a whirlpool holds the boat before it stops floating and gets dragged under.
     */
    private static final int WHIRLPOOL_RESIST_TICKS = 60;

    /**
     * How long the whirlpool has held the boat.
     */
    private int whirlpoolTicks;
    private boolean overWhirlpool;
    private boolean draggedUnder;

    public EntityBoat(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);

        this.setHealthMax(40);
        this.setHealthCurrent(40);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        final CompoundTag nbtMap = this.getNbt();
        if (nbtMap.contains("Variant")) {
            woodID = nbtMap.getInt("Variant");
        } else if (nbtMap.contains("woodID")) {
            woodID = nbtMap.getByte("woodID");
        }

        this.setDataFlag(ActorFlags.HAS_GRAVITY);
        this.setDataFlag(ActorFlags.STACKABLE);
        this.actorDataMap.put(ActorDataTypes.VARIANT, woodID);
        this.actorDataMap.put(ActorDataTypes.STRUCTURAL_INTEGRITY, 40);
        this.actorDataMap.put(ActorDataTypes.IS_BUOYANT, true);
        this.actorDataMap.put(ActorDataTypes.BUOYANCY_DATA, "{\"apply_gravity\":true,\"base_buoyancy\":1.0,\"big_wave_probability\":0.02999999932944775,\"big_wave_speed\":10.0,\"can_auto_step_from_liquid\":false,\"drag_down_on_buoyancy_removed\":0.0,\"liquid_blocks\":[\"minecraft:water\",\"minecraft:flowing_water\"],\"movement_type\":\"waves\"}");
        this.actorDataMap.put(ActorDataTypes.AIR_SUPPLY, (short) 300);
        this.actorDataMap.put(ActorDataTypes.OWNER, -1L);
        this.actorDataMap.put(ActorDataTypes.ROW_TIME_LEFT, 0f);
        this.actorDataMap.put(ActorDataTypes.ROW_TIME_RIGHT, 0f);
        this.actorDataMap.put(ActorDataTypes.CONTROLLING_RIDER_SEAT_INDEX, (byte) 0);
        this.actorDataMap.put(ActorDataTypes.DATA_LIFETIME_TICKS, -1);
        this.actorDataMap.put(ActorDataTypes.NAMETAG_ALWAYS_SHOW, (byte) -1);
        this.actorDataMap.put(ActorDataTypes.AMBIENT_SOUND_INTERVAL, 8F);
        this.actorDataMap.put(ActorDataTypes.AMBIENT_SOUND_INTERVAL_RANGE, 16F);
        this.actorDataMap.put(ActorDataTypes.AMBIENT_SOUND_EVENT_NAME, "ambient");
        this.actorDataMap.put(ActorDataTypes.FALL_DAMAGE_MULTIPLIER, 1F);
        setDataFlag(ActorFlags.COLLIDABLE);
        entityCollisionReduction = -0.5;
        this.lastX = this.x;
        this.lastY = this.y;
        this.lastZ = this.z;
        this.lastYaw = this.yaw;
        this.lastPitch = this.pitch;
        this.lastHeadYaw = this.headYaw;
        this.lastMotionX = this.motionX;
        this.lastMotionY = this.motionY;
        this.lastMotionZ = this.motionZ;
    }


    @Override public float getHeight() { return 0.5f; }
    @Override public float getWidth()  { return 1.3f; }
    @Override protected float getDrag() { return 0.02f; }
    @Override protected float getDefaultGravity() { return 0.04f; }
    @Override public float getBaseOffset() { return 0.37f; }


    @Override
    public String getInteractButtonText(Player player) {
        return "action.interact.ride.boat";
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (invulnerable) {
            return false;
        } else {
            source.setDamage(source.getDamage() * 2);

            boolean attack = super.attack(source);

            if (isAlive()) {
                performHurtAnimation();
            }

            return attack;
        }
    }

    @Override
    protected BedrockPacket createAddEntityPacket() {
        final AddActorPacket packet = new AddActorPacket();
        packet.setTargetActorID(this.getId());
        packet.setTargetRuntimeID(this.getId());
        packet.setActorType("minecraft:boat");
        packet.setPosition(org.cloudburstmc.math.vector.Vector3f.from(this.x, this.y + this.getBaseOffset(), this.z));
        packet.setVelocity(org.cloudburstmc.math.vector.Vector3f.from(this.motionX, this.motionY, this.motionZ));
        packet.setRotation(Vector2f.from(this.pitch, this.yaw));
        packet.setActorData(this.actorDataMap);

        for (int i = 0; i < this.passengers.size(); i++) {
            packet.getActorLinks().add(
                    new ActorLink(
                            this.getId(),
                            this.passengers.get(i).getId(),
                            i == 0 ? ActorLinkType.RIDING : ActorLinkType.PASSENGER,
                            false,
                            false,
                            0f
                    )
            );
        }
        return packet;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        int tickDiff = currentTick - this.lastUpdate;

        if (tickDiff <= 0 && !this.justCreated) {
            return true;
        }

        this.lastUpdate = currentTick;
        boolean hasUpdate = this.entityBaseTick(tickDiff);

        if (this.isAlive()) {
            hasUpdate = this.updateBoat(tickDiff) || hasUpdate;
        }

        return hasUpdate || !this.onGround || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001;
    }

    /**
     * Called by {@link BlockBubbleColumn} on every tick the boat sits in a column. Only a
     * whirlpool (a downward column) sinks a boat; an upward column just lets it keep floating on the water it fills.
     *
     * @param dragsDown whether the column is a whirlpool (magma drags down, soul sand pushes up)
     */
    public void onBubbleColumn(boolean dragsDown) {
        this.overWhirlpool = dragsDown;
    }

    /**
     * Whether a whirlpool has finished shaking the boat and is now dragging it under. While this is true the boat is
     * empty (the rider was ejected when it flipped), so {@link BlockBubbleColumn} does the sinking.
     */
    public boolean isDraggedUnder() {
        return this.draggedUnder;
    }

    /**
     * Drives the whirlpool timer. The boat floats for {@link #WHIRLPOOL_RESIST_TICKS}; when that runs out it is
     * dragged under and the rider is ejected at that instant.
     */
    private void tickWhirlpool() {
        if (this.draggedUnder) {
            // Latched until the boat has finished sinking - once it is out of the water it can float again
            if (!isBoatInWater()) {
                this.draggedUnder = false;
                this.whirlpoolTicks = 0;
            }
        } else if (this.overWhirlpool) {
            if (++this.whirlpoolTicks >= WHIRLPOOL_RESIST_TICKS) {
                this.draggedUnder = true;
                for (final Entity passenger : List.copyOf(passengers)) {
                    dismountEntity(passenger, true, true);
                }
            }
        } else {
            this.whirlpoolTicks = 0;
        }
        // Consumed: BlockBubbleColumn sets it again this tick if the boat is still over a whirlpool
        this.overWhirlpool = false;
    }

    private boolean updateBoat(int tickDiff) {
        tickWhirlpool();

        // The rolling amplitude
        if (getRollingAmplitude() > 0) {
            setRollingAmplitude(getRollingAmplitude() - 1);
        }

        // A killer task
        if (this.level != null) {
            if (y < this.level.getMinHeight() - 16) {
                kill();
                return false;
            }
        } else if (y < -16) {
            kill();
            return false;
        }

        boolean hasUpdated;
        double waterDiff = getWaterLevel();
        boolean inWater = isBoatInWater();

        if (this.draggedUnder) {
            // BlockBubbleColumn#onEntityCollide is dragging the boat down the column; buoyancy must not float it up
            sinking = true;
            hasUpdated = true;
        } else if (inWater) {
            hasUpdated = computeBuoyancy(waterDiff);
        } else {
            double oldMotionY = motionY;
            sinking = false;

            if (motionY > 0.0d) motionY = 0.0d;

            if (this.onGround) {
                if (motionY < 0.0d) {
                    motionY = 0.0d;
                }

                fallDistance = 0.0f;
            } else {
                motionY -= getGravity();
            }

            hasUpdated = oldMotionY != motionY;
        }

        moveBoat();
        updateMovement();

        hasUpdated = hasUpdated || positionChanged;
        if (waterDiff >= -SINKING_DEPTH) {
            if (ticksInWater != 0) {
                ticksInWater = 0;
                hasUpdated = true;
            }
        } else {
            hasUpdated = true;
            ticksInWater += tickDiff;

            if (ticksInWater >= 3 * 20) {
                for (int i = passengers.size() - 1; i >= 0; i--) {
                    dismountEntity(passengers.get(i), true, false);
                }
            }
        }
        this.getServer().getPluginManager().callEvent(new VehicleUpdateEvent(this));

        return hasUpdated;
    }

    @Override
    public void updateMovement() {
        if (passengers.isEmpty()) {
            double drag = isBoatInWater() ? 0.75d : 0.25d;
            motionX *= drag;
            motionZ *= drag;
            if (Math.abs(motionX) < 0.00001d) motionX = 0d;
            if (Math.abs(motionZ) < 0.00001d) motionZ = 0d;
        } else {
            double riderMotionX = this.x - this.lastX;
            double riderMotionZ = this.z - this.lastZ;
            double motionSquared = (riderMotionX * riderMotionX) + (riderMotionZ * riderMotionZ);
            if (motionSquared > 0.00001d && motionSquared <= 9d
                    && Double.isFinite(riderMotionX) && Double.isFinite(riderMotionZ)) {
                this.motionX = riderMotionX;
                this.motionZ = riderMotionZ;
            }
        }
        super.updateMovement();
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return super.canCollideWith(entity) && !isPassenger(entity);
    }

    @Override
    public boolean canDoInteraction() {
        return passengers.size() < 2;
    }

    private void moveBoat() {
        checkObstruction(this.x, this.y, this.z);

        Location from = new Location(lastX, lastY, lastZ, lastYaw, lastPitch, level);

        if(passengers.isEmpty()) {
            move(this.motionX, this.motionY, this.motionZ);
        }

        Location to = new Location(this.x, this.y, this.z, this.yaw, this.pitch, level);

        if (!from.equals(to)) {
            this.getServer().getPluginManager().callEvent(new VehicleMoveEvent(this, from, to));
        }
    }

    private boolean computeBuoyancy(double waterDiff) {
        if (waterDiff == Double.MAX_VALUE) {
            motionY -= getGravity();
            return true;
        }

        double oldMotionY = motionY;
        sinking = waterDiff <= -SINKING_DEPTH;

        if (sinking) {
            motionY += 0.04;
        } else {
            double error = waterDiff - EQUILIBRIUM;
            double kP = 0.035;
            double kD = 0.82;
            double correctionForce = -error * kP - motionY * kD;
            motionY += correctionForce;
            bobbingPhase += 0.035;
            double waveForce = Math.sin(bobbingPhase) * 0.0008d;
            motionY += waveForce;
            motionY = Math.max(-0.025d, Math.min(0.025d, motionY));
        }
        fallDistance = 0;

        return oldMotionY != motionY;
    }

    @Override
    public @Nullable RideableComponent getComponentRideable() {
        float seatY = this.woodID == 7 ? 0.1f : -0.2f; // Bamboo raft have a slight different Y offset
        return new RideableComponent(
            0,
            true,
            RideableComponent.DismountMode.DEFAULT,
            Set.of(),
            "action.interact.ride.boat",
            1.375f,
            true,
            false,
            2,
            List.of(
                new RideableComponent.Seat(0, 1, new Vector3f(0.0f, seatY, 0.0f), 90f, -90f, null, null),
                new RideableComponent.Seat(1, 2, new Vector3f(-0.6f, seatY, 0.0f), 90f, -90f, null, null)
            )
        );
    }

    public double getWaterLevel() {
        double maxY = this.boundingBox.getMinY() + getBaseOffset();
        AxisAlignedBB.BBConsumer<Double> consumer = new AxisAlignedBB.BBConsumer<Double>() {

            private double diffY = Double.MAX_VALUE;

            @Override
            public void accept(int x, int y, int z) {
                Block block = EntityBoat.this.level.getBlock(EntityBoat.this.temporalVector.setComponents(x, y, z));

                if (block instanceof BlockFlowingWater || ((block = block.getLevelBlockAtLayer(1)) instanceof BlockFlowingWater)) {
                    double level = block.getMaxY();

                    diffY = Math.min(maxY - level, diffY);
                }
            }

            @Override
            public Double get() {
                return diffY;
            }
        };

        this.boundingBox.forEach(consumer);

        return consumer.get();
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        double waterLevel = getWaterLevel();

        if (this.passengers.size() >= 2 || waterLevel < -SINKING_DEPTH) {
            return false;
        }

        return mountEntity(player, true);
    }

    @Override
    public boolean canPassThrough() {
        return false;
    }

    @Override
    public void kill() {
        if (!this.isAlive()) {
            return;
        }

        super.kill();

        if (this.lastDamageCause instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) {
            Entity damager = entityDamageByEntityEvent.getDamager();

            if (damager instanceof Player player && player.isCreative()) {
                return;
            }
        }

        if (this.level != null && this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
            dropItem();
        }
    }

    protected void dropItem() {
        this.level.dropItem(this, Item.get(ItemID.BOAT, this.woodID));
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.nbt.putInt("Variant", this.woodID)
                .putByte("woodID", (byte) this.woodID); // compatibility cb nukkit
    }

    public int getVariant() {
        return this.woodID;
    }

    public void setVariant(int variant) {
        this.woodID = variant;
        this.actorDataMap.put(ActorDataTypes.VARIANT, variant);
    }

    @Override
    public String getOriginalName() {
        return "Boat";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("boat", "inanimate");
    }

    @Override
    public void onCollideWithPlayer(EntityHuman entityPlayer) {
        super.onCollideWithPlayer(entityPlayer);
        //TODO: Implement boat push mechanics when player collides
    }

    @Override
    public boolean onRiderInput(Player player, PlayerAuthInputPacket pk) {
        Vector2 input = getClientBoatInput(pk);

        boolean up = input.getY() > 0;
        boolean down = input.getY() < 0;
        boolean left = input.getX() > 0;
        boolean right = input.getX() < 0;

        float oldLeft = paddleTimeLeft;
        float oldRight = paddleTimeRight;

        updateClientOnlyPaddles(up, down, left, right);

        if (Float.compare(oldLeft, paddleTimeLeft) != 0 || Float.compare(oldRight, paddleTimeRight) != 0) {
            this.setDataProperty(ActorDataTypes.ROW_TIME_LEFT,  paddleTimeLeft);
            this.setDataProperty(ActorDataTypes.ROW_TIME_RIGHT, paddleTimeRight);
            this.sendData(this.getViewers().values().toArray(Player.EMPTY_ARRAY));
        }

        return true;
    }

    private Vector2 getClientBoatInput(PlayerAuthInputPacket pk) {
        boolean isMobileAndClassicMovement = pk.getInputMode() == InputMode.TOUCH
                && pk.getNewInteractionModel() == InputInteractionModel.CLASSIC;

        if (isMobileAndClassicMovement) {
            boolean left = pk.getInputData().contains(PlayerAuthInputData.PADDLING_LEFT);
            boolean right = pk.getInputData().contains(PlayerAuthInputData.PADDLING_RIGHT);

            if (left && right) {
                return new Vector2(0, 1);
            }

            return new Vector2(1, 0).multiply(left ? -1 : right ? 1 : 0);
        }

        Vector2f moveVector = pk.getMoveVector();
        return new Vector2(moveVector.getX(), moveVector.getY());
    }

    private void updateClientOnlyPaddles(boolean up, boolean down, boolean left, boolean right) {
        float animationSpeed = 0.04f;

        if (up) {
            paddleTimeLeft += animationSpeed;
            paddleTimeRight += animationSpeed;
            return;
        }

        if (down) {
            paddleTimeLeft -= animationSpeed;
            paddleTimeRight -= animationSpeed;
            return;
        }

        if (left && !right) {
            paddleTimeLeft += animationSpeed;
            paddleTimeRight = 0f;
            return;
        }

        if (right && !left) {
            paddleTimeRight += animationSpeed;
            paddleTimeLeft = 0f;
            return;
        }
        this.setDataProperty(ActorDataTypes.ROW_TIME_RIGHT, paddleTimeLeft);
        this.setDataProperty(ActorDataTypes.ROW_TIME_LEFT, paddleTimeRight);

        paddleTimeLeft = 0f;
        paddleTimeRight = 0f;
    }

    private boolean isWaterBlock(Block b) {
        return b instanceof BlockFlowingWater
                || b.getId().equals(Block.WATER)
                || b.getId().equals(Block.FLOWING_WATER)
                || (b.getLevelBlockAtLayer(1) instanceof BlockFlowingWater);
    }

    protected boolean isBoatInWater() {
        int minX = (int) Math.floor(this.boundingBox.getMinX());
        int maxX = (int) Math.floor(this.boundingBox.getMaxX());
        int minZ = (int) Math.floor(this.boundingBox.getMinZ());
        int maxZ = (int) Math.floor(this.boundingBox.getMaxZ());
        int y = (int) Math.floor(this.boundingBox.getMinY() - 0.10d);

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                Block block = level.getBlock(x, y, z);

                if (isWaterBlock(block)) {
                    return true;
                }
            }
        }

        return false;
    }
}
