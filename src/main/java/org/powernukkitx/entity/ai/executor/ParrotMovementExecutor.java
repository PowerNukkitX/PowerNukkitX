package org.powernukkitx.entity.ai.executor;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockLeaves;
import org.powernukkitx.block.BlockLog;
import org.powernukkitx.tags.BlockTags;
import org.jetbrains.annotations.Nullable;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.EntityLiving;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.entity.passive.EntityParrot;
import org.powernukkitx.entity.ai.route.posevaluator.ParrotPosEvaluator;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

/** Selects flight destinations using the wild and tame parrot behavior priorities. */
public class ParrotMovementExecutor implements IBehaviorExecutor, EntityControl {
    private final ParrotPosEvaluator positions = new ParrotPosEvaluator();
    private Entity following;
    private boolean followingOwner;
    private boolean roaming;
    private boolean wasPanicking;
    private int nextSearch;
    private int nextDestination;
    private int lookUntil;
    private Player lookingAt;
    private Level lastLevel;

    @Override
    public boolean execute(@NotNull EntityIntelligent entity) {
        if (!(entity instanceof EntityParrot parrot)) return false;
        int tick = entity.level.getTick();
        if (lastLevel != entity.level) {
            lastLevel = entity.level;
            nextSearch = tick;
            nextDestination = tick;
            lookUntil = tick;
            following = null;
            lookingAt = null;
            roaming = false;
            wasPanicking = false;
            stop(entity);
        }
        if (parrot.isRiding()) {
            stop(entity);
            return true;
        }
        if (parrot.isTouchingWater()) {
            followingOwner = false;
            move(parrot, parrot.add(0, 2, 0), 0.4f);
            return true;
        }
        if (parrot.isPanicking()) {
            if (!wasPanicking) nextDestination = tick;
            wasPanicking = true;
            roaming = false;
            followingOwner = false;
            parrot.setMovementSpeed(0.5f);
            if (tick >= nextDestination || parrot.getMoveTarget() == null) {
                chooseFlight(parrot, tick, 0.5f, true);
            }
            return true;
        }
        wasPanicking = false;
        if (parrot.isSitting() || parrot.isDancing()) {
            roaming = false;
            stop(entity);
            lookAtPlayer(parrot, tick);
            return true;
        }
        Player owner = parrot.getOwner();
        if (parrot.isTamed() && owner != null && owner.getLevel() == parrot.level && owner.isAlive() && owner.isOnline()) {
            double distance = parrot.distanceSquared(owner);
            if (distance >= 25) followingOwner = true;
            if (distance <= 1) followingOwner = false;
            if (followingOwner) {
                move(parrot, owner.getPosition(), 0.4f);
                lookAtPlayer(parrot, tick);
                return true;
            }
        } else {
            followingOwner = false;
        }
        if (tick >= nextSearch) {
            following = findFollowTarget(parrot);
            nextSearch = tick + 20;
        }
        if (!validTarget(parrot, following)) following = null;
        if (parrot.isTamed()) {
            if (following instanceof Player player && parrot.canRideShoulder(player)) {
                if (parrot.distanceSquared(player) >= 4) move(parrot, player.getPosition(), 0.4f);
                else stop(entity);
            } else {
                stop(entity);
            }
        } else {
            if (roaming && (parrot.getMoveTarget() == null || tick >= nextDestination)) {
                roaming = false;
                stop(parrot);
            }
            if (!roaming && ThreadLocalRandom.current().nextInt(120) == 0) {
                chooseFlight(parrot, tick, 0.4f, false);
                roaming = parrot.getMoveTarget() != null;
            }
            if (!roaming && tick >= nextDestination) {
                if (following != null && parrot.distanceSquared(following) > 9) {
                    move(parrot, following.getPosition(), 0.4f);
                } else {
                    stop(parrot);
                }
                nextDestination = tick + 10;
            }
        }
        lookAtPlayer(parrot, tick);
        return true;
    }

    private Entity findFollowTarget(EntityParrot parrot) {
        Entity nearest = null;
        double best = parrot.isTamed() ? 256 : 400;
        for (Entity candidate : parrot.level.fastNearbyEntities(parrot.getBoundingBox().grow(20, 20, 20), parrot)) {
            if (!validTarget(parrot, candidate)) continue;
            double distance = parrot.distanceSquared(candidate);
            if (distance < best) {
                nearest = candidate;
                best = distance;
            }
        }
        return nearest;
    }

    private boolean validTarget(EntityParrot parrot, Entity candidate) {
        if (candidate == null || candidate.closed || !candidate.isAlive() || candidate.level != parrot.level
                || parrot.distanceSquared(candidate) > 400) return false;
        if (parrot.isTamed()) return candidate instanceof Player player && parrot.canRideShoulder(player);
        return candidate instanceof EntityLiving && !(candidate instanceof Player) && !(candidate instanceof EntityParrot)
                && !candidate.getDataFlag(org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags.INVISIBLE);
    }

    private void chooseFlight(EntityParrot parrot, int tick, float speed, boolean fleeing) {
        var random = ThreadLocalRandom.current();
        Vector3 target = !fleeing && random.nextFloat() >= 0.001f ? findTreeLanding(parrot) : null;
        for (int i = 0; target == null && i < 10; i++) {
            Vector3 candidate = parrot.add(random.nextInt(-15, 16), random.nextInt(-1, 2), random.nextInt(-15, 16));
            if (!parrot.level.isChunkLoaded(candidate.getFloorX() >> 4, candidate.getFloorZ() >> 4)) continue;
            if (fleeing && parrot.getLastDamageCause() instanceof EntityDamageByEntityEvent damage
                    && damage.getDamager().level == parrot.level
                    && candidate.distanceSquared(damage.getDamager()) < parrot.distanceSquared(damage.getDamager())) continue;
            if (candidate != null && safePosition(parrot, candidate)) {
                target = candidate;
                break;
            }
        }
        if (target != null) move(parrot, target, speed);
        else stop(parrot);
        nextDestination = tick + (fleeing ? 20 : random.nextInt(40, 101));
    }

    /**
     * Searches the local six by six by twelve volume for a perch above leaves or logs.
     * @param parrot the bird looking for a perch
     * @return a free landing position, or null when no tree is available
     */
    protected @Nullable Vector3 findTreeLanding(@NotNull EntityParrot parrot) {
        for (int x = parrot.getFloorX() - 3; x < parrot.getFloorX() + 3; x++) {
            for (int z = parrot.getFloorZ() - 3; z < parrot.getFloorZ() + 3; z++) {
                if (!parrot.level.isChunkLoaded(x >> 4, z >> 4)) continue;
                for (int y = Math.max(parrot.level.getMinHeight() + 1, parrot.getFloorY() - 6);
                     y < Math.min(parrot.level.getMaxHeight() - 1, parrot.getFloorY() + 6); y++) {
                    if (x == parrot.getFloorX() && y == parrot.getFloorY() && z == parrot.getFloorZ()) continue;
                    Block below = parrot.level.getTickCachedBlock(x, y - 1, z, 0, false);
                    if (below == null || !(below instanceof BlockLeaves || below instanceof BlockLog || below.hasTag(BlockTags.LOG))) continue;
                    Block at = parrot.level.getTickCachedBlock(x, y, z, 0, false);
                    Block above = parrot.level.getTickCachedBlock(x, y + 1, z, 0, false);
                    if (at == null || above == null || !at.getId().equals(Block.AIR) || !above.getId().equals(Block.AIR)) continue;
                    Vector3 landing = new Vector3(x + 0.5, y, z + 0.5);
                    if (safePosition(parrot, landing)) return landing;
                }
            }
        }
        return null;
    }

    private boolean safePosition(EntityParrot parrot, Vector3 candidate) {
        return positions.evalPos(parrot, candidate);
    }

    private void lookAtPlayer(EntityParrot parrot, int tick) {
        if (tick >= lookUntil) {
            lookUntil = tick + ThreadLocalRandom.current().nextInt(20, 41);
            lookingAt = null;
            double best = 36;
            for (Player player : parrot.level.getPlayers().values()) {
                double distance = parrot.distanceSquared(player);
                if (player.isOnline() && player.isAlive() && !player.isSpectator() && distance < best) {
                    lookingAt = player;
                    best = distance;
                }
            }
        }
        if (lookingAt != null && lookingAt.isOnline() && lookingAt.isAlive()
                && lookingAt.level == parrot.level && parrot.distanceSquared(lookingAt) <= 36) {
            setLookTarget(parrot, lookingAt.add(0, lookingAt.getEyeHeight(), 0));
        }
    }

    private void move(EntityParrot parrot, Vector3 target, float speed) {
        Vector3 previous = parrot.getMoveTarget();
        if (previous == null || previous.distanceSquared(target) > 1) {
            setRouteTarget(parrot, target);
            parrot.getBehaviorGroup().setForceUpdateRoute(true);
        }
        setLookTarget(parrot, target);
        parrot.setMovementSpeed(speed);
    }

    private void stop(EntityIntelligent entity) {
        followingOwner = false;
        removeRouteTarget(entity);
        removeLookTarget(entity);
        entity.setShouldUpdateMoveDirection(true);
        entity.setMoveDirectionStart(null);
        entity.setMoveDirectionEnd(null);
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        stop(entity);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        stop(entity);
    }
}
