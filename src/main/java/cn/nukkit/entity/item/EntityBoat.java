package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFlowingWater;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.components.RideableComponent;
import cn.nukkit.entity.data.EntityDataTypes;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.vehicle.VehicleMoveEvent;
import cn.nukkit.event.vehicle.VehicleUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.PlayerAuthInputPacket;
import cn.nukkit.network.protocol.types.AuthInputAction;
import cn.nukkit.network.protocol.types.AuthInteractionModel;
import cn.nukkit.network.protocol.types.EntityLink;
import cn.nukkit.network.protocol.types.InputMode;

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

    public EntityBoat(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);

        this.setHealthMax(40);
        this.setHealthCurrent(40);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        if (this.namedTag.contains("Variant")) {
            woodID = this.namedTag.getInt("Variant");
        } else if (this.namedTag.contains("woodID")) {
            woodID = this.namedTag.getByte("woodID");
        }

        this.setDataFlag(EntityFlag.HAS_GRAVITY);
        this.setDataFlag(EntityFlag.STACKABLE);
        this.entityDataMap.put(VARIANT, woodID);
        this.entityDataMap.put(IS_BUOYANT, true);
        this.entityDataMap.put(BUOYANCY_DATA, "{\"apply_gravity\":true,\"base_buoyancy\":1.0,\"big_wave_probability\":0.02999999932944775,\"big_wave_speed\":10.0,\"drag_down_on_buoyancy_removed\":0.0,\"liquid_blocks\":[\"minecraft:water\",\"minecraft:flowing_water\"],\"simulate_waves\":true}");
        this.entityDataMap.put(AIR_SUPPLY, 300);
        this.entityDataMap.put(OWNER_EID, -1);
        this.entityDataMap.put(ROW_TIME_LEFT, 0);
        this.entityDataMap.put(ROW_TIME_RIGHT, 0);
        this.entityDataMap.put(CONTROLLING_RIDER_SEAT_INDEX, 0);
        this.entityDataMap.put(DATA_LIFETIME_TICKS, -1);
        this.entityDataMap.put(NAMETAG_ALWAYS_SHOW, -1);
        this.entityDataMap.put(AMBIENT_SOUND_INTERVAL, 8F);
        this.entityDataMap.put(AMBIENT_SOUND_INTERVAL_RANGE, 16F);
        this.entityDataMap.put(AMBIENT_SOUND_EVENT_NAME, "ambient");
        this.entityDataMap.put(FALL_DAMAGE_MULTIPLIER, 1F);
        setDataFlag(EntityFlag.COLLIDABLE);
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

    @Override public float getHeight() { return 0.455f; }
    @Override public float getWidth()  { return 1.4f; }
    @Override protected float getDrag() { return 0.02f; }
    @Override protected float getGravity() { return 0.04f; }
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
    protected DataPacket createAddEntityPacket() {
        AddEntityPacket addEntity = new AddEntityPacket();
        addEntity.type = 0;
        addEntity.id = "minecraft:boat";
        addEntity.entityUniqueId = this.getId();
        addEntity.entityRuntimeId = this.getId();
        addEntity.yaw = (float) this.yaw;
        addEntity.headYaw = (float) this.yaw;
        addEntity.pitch = (float) this.pitch;
        addEntity.x = (float) this.x;
        addEntity.y = (float) this.y + getBaseOffset();
        addEntity.z = (float) this.z;
        addEntity.speedX = (float) this.motionX;
        addEntity.speedY = (float) this.motionY;
        addEntity.speedZ = (float) this.motionZ;
        addEntity.entityData = this.entityDataMap;

        addEntity.links = new EntityLink[this.passengers.size()];
        for (int i = 0; i < addEntity.links.length; i++) {
            addEntity.links[i] = new EntityLink(this.getId(), this.passengers.get(i).getId(), i == 0 ? EntityLink.Type.RIDER : EntityLink.Type.PASSENGER, false, false, 0f);
        }

        return addEntity;
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

    private boolean updateBoat(int tickDiff) {
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

        if (inWater) {
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

        move(this.motionX, this.motionY, this.motionZ);

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
        this.namedTag.putInt("Variant", this.woodID); // Correct way in Bedrock Edition
        this.namedTag.putByte("woodID", this.woodID); // Compatibility with Cloudburst Nukkit
    }

    public int getVariant() {
        return this.woodID;
    }

    public void setVariant(int variant) {
        this.woodID = variant;
        this.entityDataMap.put(VARIANT, variant);
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
            this.setDataProperty(EntityDataTypes.ROW_TIME_LEFT,  paddleTimeLeft);
            this.setDataProperty(EntityDataTypes.ROW_TIME_RIGHT, paddleTimeRight);
            this.sendData(this.getViewers().values().toArray(Player.EMPTY_ARRAY));
        }

        return true;
    }

    private Vector2 getClientBoatInput(PlayerAuthInputPacket pk) {
        boolean isMobileAndClassicMovement = pk.getInputMode() == InputMode.TOUCH
                && pk.getInteractionModel() == AuthInteractionModel.CLASSIC;

        if (isMobileAndClassicMovement) {
            boolean left = pk.getInputData().contains(AuthInputAction.PADDLE_LEFT);
            boolean right = pk.getInputData().contains(AuthInputAction.PADDLE_RIGHT);

            if (left && right) {
                return new Vector2(0, 1);
            }

            return new Vector2(1, 0).multiply(left ? -1 : right ? 1 : 0);
        }

        return pk.motion;
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
