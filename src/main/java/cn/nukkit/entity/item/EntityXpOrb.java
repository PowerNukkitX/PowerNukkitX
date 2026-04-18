package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.components.NameableComponent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.level.format.IChunk;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

/**
 * @author xtypr
 * @since 2015/12/26
 */
public class EntityXpOrb extends Entity {
    @Override
    @NotNull
    public String getIdentifier() {
        return XP_ORB;
    }

    /**
     * Split sizes used for dropping experience orbs.
     */
    public static final int[] ORB_SPLIT_SIZES = {2477, 1237, 617, 307, 149, 73, 37, 17, 7, 3, 1}; //This is indexed biggest to smallest so that we can return as soon as we found the biggest value.
    public Player closestPlayer = null;
    private int age;
    private int pickupDelay;
    private int exp;

    public EntityXpOrb(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    /**
     * Returns the largest size of normal XP orb that will be spawned for the specified amount of XP. Used to split XP
     * up into multiple orbs when an amount of XP is dropped.
     */
    public static int getMaxOrbSize(int amount) {
        for (int split : ORB_SPLIT_SIZES) {
            if (amount >= split) {
                return split;
            }
        }

        return 1;
    }

    /**
     * Splits the specified amount of XP into an array of acceptable XP orb sizes.
     */
    public static List<Integer> splitIntoOrbSizes(int amount) {
        List<Integer> result = new IntArrayList();

        while (amount > 0) {
            int size = getMaxOrbSize(amount);
            result.add(size);
            amount -= size;
        }

        return result;
    }


    @Override
    public float getWidth() {
        return 0.25f;
    }

    @Override
    public float getLength() {
        return 0.25f;
    }

    @Override
    public float getHeight() {
        return 0.25f;
    }

    @Override
    protected float getGravity() {
        return 0.04f;
    }

    @Override
    protected float getDrag() {
        return 0.02f;
    }

    @Override
    public boolean canCollide() {
        return false;
    }

    // xp orbs use their own despawn system, so they should be persistent to prevent them from being unloaded when far away from players
    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public NameableComponent getComponentNameable() {
        return DEFAULT_NOT_NAMEABLE;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        setHealthMax(5);
        setHealthCurrent(5);

        if (namedTag.containsKey("Health")) {
            this.setHealthCurrent(namedTag.getShort("Health"));
        }
        if (namedTag.containsKey("Age")) {
            this.age = namedTag.getShort("Age");
        }
        if (namedTag.containsKey("PickupDelay")) {
            this.pickupDelay = namedTag.getShort("PickupDelay");
        }
        if (namedTag.containsKey("Value")) {
            this.exp = namedTag.getShort("Value");
        }

        if (this.exp <= 0) {
            this.exp = 1;
        }

        this.entityDataMap.put(ActorDataTypes.VALUE, this.exp);

        //call event item spawn event
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return (source.getCause() == DamageCause.VOID ||
                source.getCause() == DamageCause.FIRE_TICK ||
                (source.getCause() == DamageCause.ENTITY_EXPLOSION ||
                        source.getCause() == DamageCause.BLOCK_EXPLOSION) &&
                        !this.isInsideOfWater()) && super.attack(source);
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

        boolean hasUpdate = entityBaseTick(tickDiff);
        if (this.isAlive()) {

            if (this.pickupDelay > 0 && this.pickupDelay < 32767) { //Infinite delay
                this.pickupDelay -= tickDiff;
                if (this.pickupDelay < 0) {
                    this.pickupDelay = 0;
                }
            }/* else { // Done in Player#checkNearEntities
                for (Entity entity : this.level.getCollidingEntities(this.boundingBox, this)) {
                    if (entity instanceof Player) {
                        if (((Player) entity).pickupEntity(this, false)) {
                            return true;
                        }
                    }
                }
            }*/

            this.motionY -= this.getGravity();

            if (this.checkObstruction(this.x, this.y, this.z)) {
                hasUpdate = true;
            }

            if (this.closestPlayer == null || this.closestPlayer.distanceSquared(this) > 64.0D) {
                this.closestPlayer = null;
                double closestDistance = 0.0D;
                for (Player p : this.getViewers().values()) {
                    if (!p.isSpectator() && p.spawned && p.isAlive()) {
                        double d = p.distanceSquared(this);
                        if (d <= 64.0D && (this.closestPlayer == null || d < closestDistance)) {
                            this.closestPlayer = p;
                            closestDistance = d;
                        }
                    }
                }
            }

            if (this.closestPlayer != null && (this.closestPlayer.isSpectator() || !this.closestPlayer.spawned || !this.closestPlayer.isAlive())) {
                this.closestPlayer = null;
            }

            if (this.closestPlayer != null) {
                double dX = (this.closestPlayer.x - this.x) / 8.0D;
                double dY = (this.closestPlayer.y + (double) this.closestPlayer.getEyeHeight() / 2.0D - this.y) / 8.0D;
                double dZ = (this.closestPlayer.z - this.z) / 8.0D;
                double d = Math.sqrt(dX * dX + dY * dY + dZ * dZ);
                double diff = 1.0D - d;

                if (diff > 0.0D) {
                    diff = diff * diff;
                    this.motionX += dX / d * diff * 0.1D;
                    this.motionY += dY / d * diff * 0.1D;
                    this.motionZ += dZ / d * diff * 0.1D;
                }
            }

            this.move(this.motionX, this.motionY, this.motionZ);

            double friction = 1d - this.getDrag();

            if (this.onGround && (Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionZ) > 0.00001)) {
                friction = this.getLevel().getBlock(this.temporalVector.setComponents((int) Math.floor(this.x), (int) Math.floor(this.y - 1), (int) Math.floor(this.z))).getFrictionFactor() * friction;
            }

            this.motionX *= friction;
            this.motionY *= 1 - this.getDrag();
            this.motionZ *= friction;

            if (this.onGround) {
                this.motionY *= -0.5;
            }

            this.updateMovement();

            if (this.age > 6000) {
                this.kill();
                hasUpdate = true;
            }

        }

        return hasUpdate || !this.onGround || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag = this.namedTag.toBuilder()
                .putShort("Health", (short) getHealthCurrent())
                .putShort("Age", (short) age)
                .putShort("PickupDelay", (short) pickupDelay)
                .putShort("Value", (short) exp)
                .build();
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        if (exp <= 0) {
            throw new IllegalArgumentException("XP amount must be greater than 0, got " + exp);
        }
        this.exp = exp;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    public int getPickupDelay() {
        return pickupDelay;
    }

    public void setPickupDelay(int pickupDelay) {
        this.pickupDelay = pickupDelay;
    }

    @Override
    public String getOriginalName() {
        return "Experience Orb";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("inanimate");
    }
}
