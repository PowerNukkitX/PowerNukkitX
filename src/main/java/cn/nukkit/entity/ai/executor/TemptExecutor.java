package cn.nukkit.entity.ai.executor;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Tempt behavior that makes an entity follow the nearest player holding
 * one of the configured tempt items.
 *
 * The entity moves toward the player, stops within a configurable distance,
 * and sets {@link EntityFlag#TEMPTED} while active. Optional mechanics include
 * vertical temptation, rider checks, scare reactions to sudden player movement,
 * and periodic tempt sounds.
 * 
 * @author Curse
 */
public class TemptExecutor implements EntityControl, IBehaviorExecutor {
    protected final float speedMultiplier;
    protected final boolean canTemptVertically;
    protected final boolean canTemptWhileRidden;
    protected final boolean canGetScared;
    protected final int withinRadiusSquared;
    protected final int stopDistanceSquared;
    protected final Set<String> temptItems;
    protected final TemptSound temptSound;

    protected Vector3 oldTarget;
    protected Player lastTarget;
    protected boolean changedSpeed;

    protected int nextSoundTick = -1;
    protected boolean enabledPitch;

    protected double lastScarePX;
    protected double lastScarePY;
    protected double lastScarePZ;
    protected int lastScareSampleTick = -1;
    protected Player lastScarePlayer;

    protected int scaredFleeUntilTick = -1;
    protected int scaredWaitUntilTick = -1;
    protected int nextRandomScareCheckTick = 0;
    protected Vector3 scaredFleeTarget;
    protected Vector3 scaredFleeTargetFloor;

    public TemptExecutor(float speedMultiplier, Set<String> temptItems) {
        this(
                speedMultiplier,
                false,
                false,
                false,
                10,
                2.0f,
                null,
                temptItems
        );
    }

    /**
     * @param temptSound nullable (null = no sounds)
     */
    public TemptExecutor(float speedMultiplier,
                         boolean canTemptVertically,
                         boolean canTemptWhileRidden,
                         boolean canGetScared,
                         int withinRadius,
                         float stopDistance,
                         TemptSound temptSound,
                         Set<String> temptItems) {

        this.speedMultiplier = speedMultiplier <= 0 ? 1.0f : speedMultiplier;
        this.canTemptVertically = canTemptVertically;
        this.canTemptWhileRidden = canTemptWhileRidden;
        this.canGetScared = canGetScared;

        this.withinRadiusSquared = withinRadius * withinRadius;
        this.stopDistanceSquared = (int) Math.ceil(stopDistance * stopDistance);

        this.temptItems = temptItems;
        this.temptSound = temptSound;
    }

    public static record TemptSound(String name, float minSeconds, float maxSeconds) {
        public TemptSound {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("TemptSound name cannot be null/empty");
            }

            if (!Float.isFinite(minSeconds) || minSeconds < 0f) minSeconds = 0f;
            if (!Float.isFinite(maxSeconds) || maxSeconds < 0f) maxSeconds = minSeconds;

            if (maxSeconds < minSeconds) {
                float t = minSeconds;
                minSeconds = maxSeconds;
                maxSeconds = t;
            }
        }
    }

    public static boolean hasTemptingPlayer(EntityIntelligent entity, boolean canTemptVertically, int withinRadius, Set<String> temptItems) {
        if (temptItems == null || temptItems.isEmpty()) return false;

        int withinRadiusSquared = withinRadius * withinRadius;

        for (Player p : entity.getLevel().getPlayers().values()) {
            if (p == null || !p.isOnline() || !p.isAlive()) continue;

            var item = p.getInventory().getItemInMainHand();
            if (item == null || item.isNull()) continue;

            if (!temptItems.contains(item.getId())) continue;

            double d2;
            if (canTemptVertically) {
                d2 = entity.distanceSquared(p);
            } else {
                double dx = entity.getX() - p.getX();
                double dz = entity.getZ() - p.getZ();
                d2 = (dx * dx) + (dz * dz);
            }

            if (d2 <= withinRadiusSquared) return true;
        }

        return false;
    }

    public boolean hasTemptingPlayer(EntityIntelligent entity) {
        return hasTemptingPlayer(entity, this.canTemptVertically, (int) Math.sqrt(this.withinRadiusSquared), this.temptItems);
    }

    protected double horizontalDistanceSquared(EntityIntelligent entity, Player p) {
        double dx = entity.getX() - p.getX();
        double dz = entity.getZ() - p.getZ();
        return (dx * dx) + (dz * dz);
    }

    protected boolean isScaringPlayer(Player p, int tick) {
        if (p == null) return false;

        boolean sneaking = false;
        try { sneaking = p.isSneaking(); } catch (Throwable ignored) {}

        if (sneaking) {
            lastScarePlayer = p;
            lastScarePX = p.getX();
            lastScarePY = p.getY();
            lastScarePZ = p.getZ();
            lastScareSampleTick = tick;
            return false;
        }

        if (lastScarePlayer != p || lastScareSampleTick < 0) {
            lastScarePlayer = p;
            lastScarePX = p.getX();
            lastScarePY = p.getY();
            lastScarePZ = p.getZ();
            lastScareSampleTick = tick;
            return false;
        }

        int dt = tick - lastScareSampleTick;
        if (dt <= 0) return false;

        double x = p.getX();
        double y = p.getY();
        double z = p.getZ();

        double dx = x - lastScarePX;
        double dy = y - lastScarePY;
        double dz = z - lastScarePZ;

        lastScarePX = x;
        lastScarePY = y;
        lastScarePZ = z;
        lastScareSampleTick = tick;

        double inv = 1.0 / dt;
        dx *= inv;
        dy *= inv;
        dz *= inv;

        double h = Math.sqrt((dx * dx) + (dz * dz));
        boolean moving = h >= 0.01;

        boolean jumping = false;
        try { jumping = !p.isOnGround(); } catch (Throwable ignored) {}
        if (!jumping) jumping = dy >= 0.02;

        boolean sprint = false;
        try { sprint = p.isSprinting(); } catch (Throwable ignored) {}

        return sprint || jumping || moving;
    }

    protected boolean isWithinRandomScareBand(double bestD2) {
        return bestD2 >= (3.0 * 3.0) && bestD2 <= (6.0 * 6.0);
    }

    protected boolean shouldRandomlyScare(EntityIntelligent entity, Player best, double bestD2, int tick) {
        if (tick < nextRandomScareCheckTick) return false;
        nextRandomScareCheckTick = tick + 10;

        if (!isWithinRandomScareBand(bestD2)) return false;
        if (isScaringPlayer(best, tick)) return false;

        boolean sneaking = false;
        try { sneaking = best.isSneaking(); } catch (Throwable ignored) {}

        double chance = 0.14;
        if (sneaking) chance *= 0.25;

        return ThreadLocalRandom.current().nextDouble() < chance;
    }

    protected void beginScare(EntityIntelligent entity, Player best, double bestD2, int tick) {
        int fleeTicks = ThreadLocalRandom.current().nextInt(25, 41);
        int waitTicks = ThreadLocalRandom.current().nextInt(15, 31);

        scaredFleeUntilTick = tick + fleeTicks;
        scaredWaitUntilTick = scaredFleeUntilTick + waitTicks;

        double px = best.getX();
        double pz = best.getZ();

        double ex = entity.getX();
        double ez = entity.getZ();

        double vx = ex - px;
        double vz = ez - pz;

        double len = Math.sqrt((vx * vx) + (vz * vz));
        if (len < 1e-6) {
            double a = ThreadLocalRandom.current().nextDouble() * Math.PI * 2.0;
            vx = Math.cos(a);
            vz = Math.sin(a);
            len = 1.0;
        } else {
            vx /= len;
            vz /= len;
        }

        double currentDist = Math.sqrt(bestD2);

        double desiredDist = ThreadLocalRandom.current().nextDouble(10.5, 13.5);
        if (desiredDist < currentDist + 2.0) desiredDist = currentDist + 2.0;

        double tx = px + (vx * desiredDist);
        double tz = pz + (vz * desiredDist);

        scaredFleeTarget = new Vector3(tx, entity.getY(), tz);
        scaredFleeTargetFloor = scaredFleeTarget.floor();

        removeLookTarget(entity);
        entity.setDataFlag(EntityFlag.TEMPTED, true);
        entity.getBehaviorGroup().setForceUpdateRoute(true);

        float scaredSpeed = entity.getMovementSpeedDefault() * 1.8f;
        if (entity.getMovementSpeed() != scaredSpeed) {
            entity.setMovementSpeed(scaredSpeed);
            changedSpeed = true;
        }
    }

    protected boolean handleScaredState(EntityIntelligent entity, int tick) {
        if (scaredWaitUntilTick != -1 && tick < scaredWaitUntilTick && tick >= scaredFleeUntilTick) {
            removeRouteTarget(entity);
            removeLookTarget(entity);
            entity.setDataFlag(EntityFlag.TEMPTED, true);
            return true;
        }

        if (scaredFleeUntilTick != -1 && tick < scaredFleeUntilTick) {
            if (scaredFleeTarget != null) {
                Location fleeLoc = new Location(scaredFleeTarget.x, scaredFleeTarget.y, scaredFleeTarget.z, entity.level);

                if (oldTarget == null || !oldTarget.equals(scaredFleeTargetFloor)) {
                    setRouteTarget(entity, fleeLoc);
                    oldTarget = scaredFleeTargetFloor;
                    entity.getBehaviorGroup().setForceUpdateRoute(true);
                }

                setLookTarget(entity, fleeLoc);
            }

            entity.setDataFlag(EntityFlag.TEMPTED, true);
            return true;
        }

        if (scaredWaitUntilTick != -1 && tick >= scaredWaitUntilTick) {
            scaredFleeUntilTick = -1;
            scaredWaitUntilTick = -1;
            scaredFleeTarget = null;
            scaredFleeTargetFloor = null;

            oldTarget = null;
            lastTarget = null;
        }

        return false;
    }

    protected void maybePlayTemptSound(EntityIntelligent entity) {
        if (temptSound == null) return;

        int tick = entity.getLevel().getTick();
        if (nextSoundTick != -1 && tick < nextSoundTick) return;

        int minT = (int) Math.ceil(temptSound.minSeconds() * 20f);
        int maxT = (int) Math.ceil(temptSound.maxSeconds() * 20f);
        if (maxT < minT) maxT = minT;

        int delay = (minT == maxT) ? minT : ThreadLocalRandom.current().nextInt(minT, maxT + 1);
        nextSoundTick = tick + delay;

        try {
            entity.getLevel().addSound(entity.getLocation(), temptSound.name());
        } catch (Throwable ignored) {
        }
    }

    protected boolean shouldEnablePitchWhileTempting() {
        return false;
    }

    protected float resolveDesiredSpeed(Entity entity) {
        return entity.getMovementSpeedDefault() * speedMultiplier;
    }

    @Override
    public boolean execute(@NotNull EntityIntelligent entity) {
        if (temptItems == null || temptItems.isEmpty()) {
            clearTempt(entity);
            return false;
        }

        if (shouldEnablePitchWhileTempting() && !entity.isEnablePitch()) {
            entity.setEnablePitch(true);
            enabledPitch = true;
        }

        if (!canTemptWhileRidden) {
            try {
                if (entity.getPassengers() != null && !entity.getPassengers().isEmpty()) {
                    clearTempt(entity);
                    return false;
                }
            } catch (Throwable ignored) {
            }
        }

        int tick = entity.getLevel().getTick();

        if (canGetScared && handleScaredState(entity, tick)) {
            return true;
        }

        Player best = null;
        double bestD2 = Double.MAX_VALUE;

        for (Player p : entity.getLevel().getPlayers().values()) {
            if (p == null || !p.isOnline() || !p.isAlive()) continue;

            var item = p.getInventory().getItemInMainHand();
            if (item.isNull() || !temptItems.contains(item.getId())) continue;

            double d2 = canTemptVertically ? entity.distanceSquared(p) : horizontalDistanceSquared(entity, p);
            if (d2 > withinRadiusSquared) continue;

            if (d2 < bestD2) {
                bestD2 = d2;
                best = p;
            }
        }

        if (best == null) {
            clearTempt(entity);
            return false;
        }

        Location target = best.getLocation();
        if (!target.level.getName().equals(entity.level.getName())) {
            clearTempt(entity);
            return false;
        }

        if (canGetScared) {
            int scaredDistanceSquared = stopDistanceSquared + (2 * 2);

            boolean closeEnoughToScare = bestD2 <= scaredDistanceSquared;
            boolean movingScare = closeEnoughToScare && isScaringPlayer(best, tick);
            boolean randomScare = shouldRandomlyScare(entity, best, bestD2, tick);

            if (movingScare || randomScare) {
                beginScare(entity, best, bestD2, tick);
                return true;
            }
        }

        if (bestD2 <= stopDistanceSquared) {
            removeRouteTarget(entity);
            setLookTarget(entity, target);
            entity.setDataFlag(EntityFlag.TEMPTED, true);

            maybePlayTemptSound(entity);

            lastTarget = best;
            oldTarget = target.floor();
            return true;
        }

        Vector3 floor = target.floor();
        if (oldTarget != null && floor.equals(oldTarget) && lastTarget == best) {
            setLookTarget(entity, target);
            entity.setDataFlag(EntityFlag.TEMPTED, true);

            maybePlayTemptSound(entity);

            return true;
        }

        setRouteTarget(entity, target);
        setLookTarget(entity, target);
        entity.setDataFlag(EntityFlag.TEMPTED, true);

        maybePlayTemptSound(entity);

        if (oldTarget == null || !oldTarget.equals(floor) || lastTarget != best) {
            entity.getBehaviorGroup().setForceUpdateRoute(true);
            oldTarget = floor;
            lastTarget = best;
        }

        float desired = resolveDesiredSpeed(entity);
        if (entity.getMovementSpeed() != desired) {
            entity.setMovementSpeed(desired);
            changedSpeed = true;
        }

        return true;
    }

    protected void clearTempt(EntityIntelligent entity) {
        removeRouteTarget(entity);
        removeLookTarget(entity);
        entity.setDataFlag(EntityFlag.TEMPTED, false);

        if (enabledPitch) {
            entity.setEnablePitch(false);
            enabledPitch = false;
        }

        if (changedSpeed) {
            entity.setMovementSpeed(entity.getMovementSpeedDefault());
            changedSpeed = false;
        }

        oldTarget = null;
        lastTarget = null;
        nextSoundTick = -1;

        scaredFleeUntilTick = -1;
        scaredWaitUntilTick = -1;
        scaredFleeTarget = null;
        scaredFleeTargetFloor = null;
        nextRandomScareCheckTick = 0;

        lastScarePlayer = null;
        lastScareSampleTick = -1;
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        clearTempt(entity);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        clearTempt(entity);
    }
}