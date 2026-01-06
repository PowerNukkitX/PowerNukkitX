package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFlowingWater;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.EntitySwimmable;
import cn.nukkit.entity.data.EntityDataType;
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
import cn.nukkit.math.MathHelper;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.AnimatePacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.PlayerAuthInputPacket;
import cn.nukkit.network.protocol.types.AuthInputAction;
import cn.nukkit.network.protocol.types.AuthInteractionModel;
import cn.nukkit.network.protocol.types.EntityLink;
import cn.nukkit.network.protocol.types.InputMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
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

    public static final Vector3f RIDER_PLAYER_OFFSET = new Vector3f(0, 1.02001f, 0);
    public static final Vector3f RIDER_OFFSET = new Vector3f(0, -0.2f, 0);

    public static final Vector3f PASSENGER_OFFSET = new Vector3f(-0.6f);
    public static final Vector3f RIDER_PASSENGER_OFFSET = new Vector3f(0.2f);

    public static final int RIDER_INDEX = 0;
    public static final int PASSENGER_INDEX = 1;
    public static final double SINKING_DEPTH = 0.3;
    private static final double EQUILIBRIUM = -0.02;
    private double bobbingPhase = 0;
    //paddle animation states
    private float paddleTimeLeft = 0f;
    private float paddleTimeRight = 0f;

    private final Set<Entity> ignoreCollision = new HashSet<>(2);
    public int woodID;
    protected boolean sinking = true;
    private int ticksInWater;

    public EntityBoat(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);

        this.setMaxHealth(40);
        this.setHealth(40);
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
    }


    @Override public float getHeight() { return 0.5f; }
    @Override public float getWidth()  { return 1.3f; }
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
    public void close() {
        super.close();

        for (Entity linkedEntity : this.passengers) {
            linkedEntity.riding = null;
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
            addEntity.links[i] = new EntityLink(this.getId(), this.passengers.get(i).getId(), i == 0 ? EntityLink.Type.RIDER : EntityLink.Type.PASSENGER, false, false);
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

        hasUpdated = computeBuoyancy(waterDiff);
        if (!hasControllingPassenger()) {
            ignoreCollision.removeIf(ignored -> !ignored.isValid() || ignored.isClosed() || !ignored.isAlive() || !ignored.getBoundingBox().intersectsWith(getBoundingBox().grow(0.5, 0.5, 0.5)));
        }

        moveBoat();
        updateMovement();

        hasUpdated = hasUpdated || positionChanged;
        if (waterDiff >= -SINKING_DEPTH) {
            if (ticksInWater != 0) {
                ticksInWater = 0;
                hasUpdated = true;
            }
            //hasUpdated = collectCollidingEntities() || hasUpdated;
        } else {
            hasUpdated = true;
            ticksInWater += tickDiff;
            if (ticksInWater >= 3 * 20) {
                for (int i = passengers.size() - 1; i >= 0; i--) {
                    dismountEntity(passengers.get(i));
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
        move(this.motionX, this.motionY, this.motionZ);

        if (this.isCollidedHorizontally) {
            this.motionX *= -0.3;
            this.motionZ *= -0.3;
        }

        if (this.onGround) {
            this.motionX *= 0.95;
            this.motionZ *= 0.95;
            if (this.motionY < 0) {
                this.motionY *= 0.65;
            }
        }

        double friction;
        if (isBoatInWater()) {
            friction = 0.94;
            this.motionY *= 0.95;
        } else {
            friction = 1 - this.getDrag();
            if (this.onGround && (Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionZ) > 0.00001)) {
                friction *= this.getLevel().getBlock(this.temporalVector.setComponents((int) Math.floor(this.x), (int) Math.floor(this.y - 1), (int) Math.floor(this.z))).getFrictionFactor();
            }
        }

        this.motionX *= friction;
        this.motionZ *= friction;

        Location from = new Location(lastX, lastY, lastZ, lastYaw, lastPitch, level);
        Location to = new Location(this.x, this.y, this.z, this.yaw, this.pitch, level);

        if (!from.equals(to)) {
            this.getServer().getPluginManager().callEvent(new VehicleMoveEvent(this, from, to));
        }
    }

    private boolean collectCollidingEntities() {
        if (this.passengers.size() >= 2) {
            return false;
        }

        for (Entity entity : this.level.getCollidingEntities(this.boundingBox.grow(0.20000000298023224, 0.0D, 0.20000000298023224), this)) {
            if (entity.riding != null || !(entity instanceof EntityLiving) || entity instanceof Player || entity instanceof EntitySwimmable || isPassenger(entity)) {
                continue;
            }

            this.mountEntity(entity);

            if (this.passengers.size() >= 2) {
                break;
            }
        }

        return true;
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
            double kP = 0.08;
            double kD = 0.6;

            double correctionForce = -error * kP - motionY * kD;
            motionY += correctionForce;
            bobbingPhase += 0.08;
            double waveForce = Math.sin(bobbingPhase) * 0.003;
            motionY += waveForce;
            motionY = Math.max(-0.08, Math.min(0.08, motionY));
        }

        fallDistance = 0;
        return oldMotionY != motionY;
    }

    @Override
    public void updatePassengers() {
        updatePassengers(false);
    }

    public void updatePassengers(boolean sendLinks) {
        if (this.passengers.isEmpty()) {
            return;
        }

        for (Entity passenger : new ArrayList<>(passengers)) {
            if (!passenger.isAlive()) {
                dismountEntity(passenger);
            }
        }

        Entity ent;

        if (passengers.size() == 1) {
            (ent = this.passengers.get(0)).setSeatPosition(getMountedOffset(ent));
            super.updatePassengerPosition(ent);

            if (sendLinks) {
                broadcastLinkPacket(ent, EntityLink.Type.RIDER);
            }
        } else if (passengers.size() == 2) {
            if (!((ent = passengers.get(0)) instanceof Player)) { //swap
                Entity passenger2 = passengers.get(1);

                if (passenger2 instanceof Player) {
                    this.passengers.set(0, passenger2);
                    this.passengers.set(1, ent);

                    ent = passenger2;
                }
            }

            ent.setSeatPosition(getMountedOffset(ent).add(RIDER_PASSENGER_OFFSET));
            super.updatePassengerPosition(ent);
            if (sendLinks) {
                broadcastLinkPacket(ent, EntityLink.Type.RIDER);
            }

            (ent = this.passengers.get(1)).setSeatPosition(getMountedOffset(ent).add(PASSENGER_OFFSET));

            super.updatePassengerPosition(ent);

            if (sendLinks) {
                broadcastLinkPacket(ent, EntityLink.Type.PASSENGER);
            }

            //float yawDiff = ent.getId() % 2 == 0 ? 90 : 270;
            //ent.setRotation(this.yaw + yawDiff, ent.pitch);
            //ent.updateMovement();
        } else {
            for (Entity passenger : passengers) {
                super.updatePassengerPosition(passenger);
            }
        }
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
    public boolean mountEntity(Entity entity) {
        boolean player = !this.passengers.isEmpty() && this.passengers.get(0) instanceof Player;
        EntityLink.Type mode = EntityLink.Type.PASSENGER;

        if (!player && (entity instanceof Player || this.passengers.isEmpty())) {
            mode = EntityLink.Type.RIDER;
        }

        return super.mountEntity(entity, mode);
    }

    @Override
    public boolean mountEntity(Entity entity, EntityLink.Type mode) {
        boolean r = super.mountEntity(entity, mode);
        if (entity.riding == this) {
            updatePassengers(true);

            entity.setDataProperty(SEAT_LOCK_RIDER_ROTATION, true);
            entity.setDataProperty(SEAT_LOCK_RIDER_ROTATION_DEGREES, 90);
            entity.setDataProperty(SEAT_HAS_ROTATION, this.passengers.indexOf(entity) != 1);
            entity.setDataProperty(SEAT_ROTATION_OFFSET_DEGREES, -90);
            entity.setRotation(yaw, entity.pitch);
            entity.updateMovement();
        }
        return r;
    }

    @Override
    protected void updatePassengerPosition(Entity passenger) {
        updatePassengers();
    }

    @Override
    public boolean dismountEntity(Entity entity, boolean sendLinks) {
        boolean r = super.dismountEntity(entity, sendLinks);

        updatePassengers();
        entity.setDataProperty(SEAT_LOCK_RIDER_ROTATION, false);
        if (entity instanceof EntityHuman) {
            ignoreCollision.add(entity);
        }

        return r;
    }

    @Override
    public boolean isControlling(Entity entity) {
        return entity instanceof Player && this.passengers.indexOf(entity) == 0;
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (this.passengers.size() >= 2 || getWaterLevel() < -SINKING_DEPTH) {
            return false;
        }

        super.mountEntity(player);
        return super.onInteract(player, item, clickedPos);
    }

    @Override
    public Vector3f getMountedOffset(Entity entity) {
        return entity instanceof Player ? RIDER_PLAYER_OFFSET : RIDER_OFFSET;
    }

    public void onPaddle(AnimatePacket.Action animation, float value) {
        EntityDataType<Float> propertyId = animation == AnimatePacket.Action.ROW_RIGHT ? ROW_TIME_RIGHT : ROW_TIME_LEFT;

        if (Float.compare(getDataProperty(propertyId), value) != 0) {
            this.setDataProperty(propertyId, value);
        }
    }

    @Override
    public void applyEntityCollision(Entity entity) {
        if (this.riding == null && !hasControllingPassenger() && entity.riding != this
                && !entity.passengers.contains(this) && !ignoreCollision.contains(entity)) {
            if (!entity.boundingBox.intersectsWith(this.boundingBox.grow(0.20000000298023224, -0.1, 0.20000000298023224))
                    || entity instanceof Player && ((Player) entity).isSpectator()) {
                return;
            }

            double diffX = entity.x - this.x;
            double diffZ = entity.z - this.z;

            double direction = NukkitMath.getDirection(diffX, diffZ);

            if (direction >= 0.009999999776482582D) {
                direction = Math.sqrt(direction);
                diffX /= direction;
                diffZ /= direction;

                double d3 = Math.min(1 / direction, 1);

                diffX *= d3;
                diffZ *= d3;
                diffX *= 0.05000000074505806;
                diffZ *= 0.05000000074505806;
                diffX *= 1 + entityCollisionReduction;
                diffZ *= 1 + entityCollisionReduction;

                if (this.riding == null) {
                    motionX -= diffX;
                    motionZ -= diffZ;
                }
            }
        }
    }

    @Override
    public boolean canPassThrough() {
        return false;
    }


    @Override
    public void kill() {
        if (!isAlive()) {
            return;
        }
        super.kill();

        if (this.lastDamageCause instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) {
            Entity damager = entityDamageByEntityEvent.getDamager();
            if (damager instanceof Player player && player.isCreative()) {
                return;
            }
        }

        if (level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
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
        float acceleration = 0.0F;

        boolean isMobileAndClassicMovement = pk.getInputMode() == InputMode.TOUCH && pk.getInteractionModel() == AuthInteractionModel.CLASSIC;
        Vector2 input;
        if (isMobileAndClassicMovement) {
            // Press both left and right to move forward and press 1 to turn the boat.
            boolean left = pk.getInputData().contains(AuthInputAction.PADDLE_LEFT), right = pk.getInputData().contains(AuthInputAction.PADDLE_RIGHT);
            if (left && right) {
                input = new Vector2(0, 1);
            } else {
                input = new Vector2(1,0).multiply(left ? -1 : right ? 1 : 0);
            }
        } else {
            input = pk.motion;
        }
        boolean up = input.getY() > 0;
        boolean down = input.getY() < 0;
        boolean left = input.getX() > 0;
        boolean right = input.getX() < 0;

        if (right != left && !up && !down) acceleration += 0.005F;

        if (up) acceleration += 0.04F;
        if (down) acceleration -= 0.005F;

        double yaw = this.getYaw() - 90;
        Vector3 motion = new Vector3(MathHelper.sin((-yaw * 0.017453292F)) * acceleration, 0, MathHelper.cos((yaw * 0.017453292F)) * acceleration);

        this.setMotion(this.getMotion().add(motion));
        float animationSpeed = (float) Math.max(0.01, Math.min(0.08,
                Math.sqrt(motionX * motionX + motionZ * motionZ) * 0.05));
        double turnRate = NukkitMath.min((Math.abs(Math.toDegrees(Math.atan2(pk.motion.y, pk.motion.x)) - 90) / 90d) * 4, 4);

        if (up) {
            paddleTimeLeft  += animationSpeed;
            paddleTimeRight += animationSpeed;
            if (left)  this.yaw -= turnRate;
            if (right) this.yaw += turnRate;
        } else if (down) {
            paddleTimeLeft  -= animationSpeed;
            paddleTimeRight -= animationSpeed;
            if (left)  this.yaw -= turnRate;
            if (right) this.yaw += turnRate;
        } else {
            double yawRad = Math.toRadians(pk.yaw);
            double pz = Math.cos(yawRad);
            double fz = Math.sin(yawRad);
            if (left && !right) applyTurn(true,  turnRate, acceleration, pz, fz, animationSpeed);
            if (right && !left) applyTurn(false, turnRate, acceleration, pz, fz, animationSpeed);
            if (!left)  paddleTimeLeft = 0;
            if (!right) paddleTimeRight = 0;
        }
        this.setDataProperty(EntityDataTypes.ROW_TIME_RIGHT, paddleTimeLeft);
        this.setDataProperty(EntityDataTypes.ROW_TIME_LEFT,  paddleTimeRight);

        this.headYaw = this.yaw;
        broadcastMovement(false);
        this.sendData(this.getViewers().values().toArray(Player.EMPTY_ARRAY));
        return true;
    }

    private boolean isWaterBlock(Block b) {
        return b instanceof BlockFlowingWater
                || b.getId().equals(Block.WATER)
                || b.getId().equals(Block.FLOWING_WATER)
                || (b.getLevelBlockAtLayer(1) instanceof BlockFlowingWater);
    }

    private boolean isBoatInWater() {
        int minX = (int) Math.floor(this.boundingBox.getMinX());
        int maxX = (int) Math.floor(this.boundingBox.getMaxX());
        int minZ = (int) Math.floor(this.boundingBox.getMinZ());
        int maxZ = (int) Math.floor(this.boundingBox.getMaxZ());
        int y = (int) Math.floor(this.boundingBox.getMinY() + 0.05);

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                if (isWaterBlock(level.getBlock(x, y, z))) {
                    return true;
                }
            }
        }
        return false;
    }

    private void applyTurn(boolean left, double turnRate, double accel, double pz, double fz, float animationSpeed) {
        this.yaw += left ? -turnRate : turnRate;
        this.motionX += pz * (accel * 0.6);
        this.motionZ += fz * (accel * 0.6);
        if (left) {
            paddleTimeLeft += animationSpeed;
            paddleTimeRight = 0f;
        } else {
            paddleTimeRight += animationSpeed;
            paddleTimeLeft = 0f;
        }
    }
}