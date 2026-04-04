package cn.nukkit.entity.ai.executor;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * "Beg" behavior: makes the entity look at the nearest nearby player holding
 * one of the configured beg items, and sets {@link EntityFlag#INTERESTED}
 * while the condition is met.
 *
 * Unlike {@link TemptExecutor}, this does not apply any movement/follow logic;
 * it only locks the look target (optionally considering vertical distance).
 * 
 * @author Curse
 */
public class BegExecutor implements EntityControl, IBehaviorExecutor {
    protected final boolean canBegVertically;
    protected final int withinRadiusSquared;
    protected final Set<String> begItems;

    protected Vector3 oldTarget;
    protected Player lastTarget;
    protected boolean enabledPitch;

    public BegExecutor(Set<String> begItems) {
        this(false, 8, begItems);
    }

    public BegExecutor(boolean canBegVertically, int withinRadius, Set<String> begItems) {
        this.canBegVertically = canBegVertically;
        this.withinRadiusSquared = withinRadius * withinRadius;
        this.begItems = begItems;
    }

    public static boolean hasBeggingPlayer(EntityIntelligent entity, boolean canBegVertically, int withinRadius, Set<String> begItems) {
        if (begItems == null || begItems.isEmpty()) return false;

        int withinRadiusSquared = withinRadius * withinRadius;

        for (Player p : entity.getLevel().getPlayers().values()) {
            if (p == null || !p.isOnline() || !p.isAlive()) continue;

            var item = p.getInventory().getItemInMainHand();
            if (item == null || item.isNull()) continue;

            if (!begItems.contains(item.getId())) continue;

            double d2;
            if (canBegVertically) {
                d2 = entity.distanceSquared(p);
            } else {
                d2 = horizontalDistanceSquared(entity, p);
            }

            if (d2 <= withinRadiusSquared) return true;
        }

        return false;
    }

    protected static double horizontalDistanceSquared(EntityIntelligent entity, Player p) {
        double dx = entity.getX() - p.getX();
        double dz = entity.getZ() - p.getZ();
        return (dx * dx) + (dz * dz);
    }

    protected boolean shouldEnablePitchWhileBegging() {
        return false;
    }

    @Override
    public boolean execute(@NotNull EntityIntelligent entity) {
        if (begItems == null || begItems.isEmpty()) {
            clearBeg(entity);
            return false;
        }

        if (shouldEnablePitchWhileBegging() && !entity.isEnablePitch()) {
            entity.setEnablePitch(true);
            enabledPitch = true;
        }

        Player best = null;
        double bestD2 = Double.MAX_VALUE;

        for (Player p : entity.getLevel().getPlayers().values()) {
            if (p == null || !p.isOnline() || !p.isAlive()) continue;

            var item = p.getInventory().getItemInMainHand();
            if (item == null || item.isNull()) continue;
            if (!begItems.contains(item.getId())) continue;

            double d2 = canBegVertically ? entity.distanceSquared(p) : horizontalDistanceSquared(entity, p);
            if (d2 > withinRadiusSquared) continue;

            if (d2 < bestD2) {
                bestD2 = d2;
                best = p;
            }
        }

        if (best == null) {
            clearBeg(entity);
            return false;
        }

        Location target = best.getLocation();
        if (!target.level.getName().equals(entity.level.getName())) {
            clearBeg(entity);
            return false;
        }

        Vector3 floor = target.floor();
        if (oldTarget != null && floor.equals(oldTarget) && lastTarget == best) {
            setLookTarget(entity, target);
            entity.setDataFlag(EntityFlag.INTERESTED, true);
            return true;
        }

        setLookTarget(entity, target);

        oldTarget = floor;
        lastTarget = best;

        return true;
    }

    protected void clearBeg(EntityIntelligent entity) {
        removeLookTarget(entity);
        entity.setDataFlag(EntityFlag.INTERESTED, false);

        if (enabledPitch) {
            entity.setEnablePitch(false);
            enabledPitch = false;
        }

        oldTarget = null;
        lastTarget = null;
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        clearBeg(entity);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        clearBeg(entity);
    }
}