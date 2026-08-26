package org.powernukkitx.entity.ai.route;

import org.powernukkitx.Server;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.route.data.Node;
import org.powernukkitx.entity.ai.route.finder.IRouteFinder;
import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.Vector3;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Keeps the movement direction of an entity in sync with its pathfinder, by submitting pathfinding tasks
 * whenever the route becomes outdated and feeding the resulting nodes back to the entity.
 */
public class RouteUpdater {

    /**
     * Determines how many gt between each path update
     */
    protected static final int ROUTE_UPDATE_CYCLE = 16;//gt

    /**
     * How many gt a submitted, not yet started pathfinding task may wait before it is resubmitted
     */
    protected static final int TASK_TIMEOUT = 8;//gt

    @Getter
    protected final IRouteFinder routeFinder;

    @Getter
    protected RouteFindingManager.RouteFindingTask routeFindingTask;

    /**
     * Records the number of gt elapsed since the last path update
     */
    protected int currentUpdateTick;//gt

    protected long blockChangeCache;

    @Getter
    @Setter
    protected boolean forceUpdate;

    /**
     * @param routeFinder      the pathfinder to drive
     * @param startUpdateTick  staggers the path update timing of each entity, to avoid submitting too many
     *                         path update tasks within a single gt
     */
    public RouteUpdater(IRouteFinder routeFinder, int startUpdateTick) {
        this.routeFinder = routeFinder;
        this.currentUpdateTick = startUpdateTick;
    }

    /**
     * Recalculates the path towards the entity's move target when needed, and hands the next movement
     * direction to the entity.
     *
     * @param entity the entity to move
     */
    public void update(EntityIntelligent entity) {
        currentUpdateTick++;
        boolean reachUpdateCycle = currentUpdateTick >= calcActiveDelay(entity, ROUTE_UPDATE_CYCLE + (entity.level.tickRateOptDelay << 1));
        if (reachUpdateCycle) currentUpdateTick = 0;
        Vector3 target = entity.getMoveTarget();
        if (target == null) {
            //no path target, so clear the path information
            clearMoveDirection(entity);
            return;
        }
        //when the update cycle is reached, start recalculating the new path
        if (forceUpdate || (reachUpdateCycle && shouldUpdateRoute(entity))) {
            submitRouteFinding(entity, target);
        }
        if (routeFindingTask != null && routeFindingTask.getFinished() && !hasNewUnCalMoveTarget(entity)) {
            //if it can no longer move and there is no pathfinding task in progress, clear the path information
            var reachableTarget = routeFinder.getReachableTarget();
            if (reachableTarget != null && entity.floor().equals(reachableTarget.floor())) {
                entity.setMoveTarget(null);
                clearMoveDirection(entity);
                return;
            }
        }
        if (entity.isShouldUpdateMoveDirection() && routeFinder.hasNext()) {
            //if there is a new movement direction, update it
            updateMoveDirection(entity);
            entity.setShouldUpdateMoveDirection(false);
        }
    }

    protected void submitRouteFinding(EntityIntelligent entity, Vector3 target) {
        boolean timedOut = routeFindingTask != null
                && !routeFindingTask.getFinished()
                && !routeFindingTask.getStarted()
                && Server.getInstance().getNextTick() - routeFindingTask.getStartTime() > TASK_TIMEOUT;
        //only submit when this is the first calculation, the previous one finished, or the pending one timed out
        if (routeFindingTask != null && !routeFindingTask.getFinished() && !timedOut) return;
        if (timedOut) routeFindingTask.cancel(true);
        routeFindingTask = new RouteFindingManager.RouteFindingTask(routeFinder, task -> {
            updateMoveDirection(entity);
            entity.setShouldUpdateMoveDirection(false);
            setForceUpdate(false);
            //write the section change record
            cacheSectionBlockChange(entity.level, calPassByChunkSections(routeFinder.getRoute().stream().map(Node::getVector3).toList(), entity.level));
        });
        //clone to prevent potential modification by the pathfinder
        RouteFindingManager.getInstance().submit(routeFindingTask.setStart(entity.clone()).setTarget(target));
    }

    /**
     * Checks whether the path needs to be updated. This method detects whether the ChunkSections the path passes through have changed
     *
     * @return whether the path needs to be updated
     */
    protected boolean shouldUpdateRoute(EntityIntelligent entity) {
        //this optimization only applies to entities in non-active chunks
        if (entity.isActive()) return true;
        //the endpoint changed or it's the first calculation, so recalculation is needed
        if (routeFinder.getTarget() == null || hasNewUnCalMoveTarget(entity)) return true;
        Set<ChunkSectionVector> passByChunkSections = calPassByChunkSections(routeFinder.getRoute().stream().map(Node::getVector3).toList(), entity.level);
        long total = 0;
        for (ChunkSectionVector section : passByChunkSections) {
            total += getSectionBlockChange(entity.level, section);
        }
        //a Section changed, so recalculation is needed
        return blockChangeCache != total;
    }

    /**
     * Confirms whether the entity has set a new, uncalculated moveTarget by comparing the moveTarget set in the pathfinder with the entity's moveTarget
     *
     * @param entity the entity
     * @return whether a new, uncalculated pathfinding target exists
     */
    protected boolean hasNewUnCalMoveTarget(EntityIntelligent entity) {
        return !entity.getMoveTarget().equals(routeFinder.getTarget());
    }

    /**
     * Caches the section's blockChanges into blockChangeCache
     */
    protected void cacheSectionBlockChange(Level level, Set<ChunkSectionVector> sections) {
        long total = 0;
        for (ChunkSectionVector section : sections) {
            total += getSectionBlockChange(level, section);
        }
        this.blockChangeCache = total;
    }

    /**
     * Returns the blockChanges of the section corresponding to the sectionVector
     */
    protected long getSectionBlockChange(Level level, ChunkSectionVector vector) {
        var chunk = level.getChunk(vector.chunkX, vector.chunkZ);
        return chunk.getSectionBlockChanges(vector.sectionY);
    }

    /**
     * Calculates the ChunkSections that the set of coordinates passes through
     */
    protected Set<ChunkSectionVector> calPassByChunkSections(Collection<Vector3> nodes, Level level) {
        final DimensionData dimensionData = level.getDimensionData();
        var sections = new HashSet<ChunkSectionVector>();
        for (Vector3 node : nodes) {
            final int y = Math.min(dimensionData.getMaxHeight(), Math.max(dimensionData.getMinHeight(), node.getFloorY() - dimensionData.getMinHeight()));
            sections.add(new ChunkSectionVector(node.getChunkX(), y >> 4, node.getChunkZ()));
        }
        return sections;
    }

    protected void updateMoveDirection(EntityIntelligent entity) {
        Vector3 end = entity.getMoveDirectionEnd();
        if (end == null) {
            end = entity.clone();
        }
        var next = routeFinder.next();
        if (next != null) {
            entity.setMoveDirectionStart(end);
            entity.setMoveDirectionEnd(next.getVector3());
        }
    }

    protected void clearMoveDirection(EntityIntelligent entity) {
        entity.setMoveDirectionStart(null);
        entity.setMoveDirectionEnd(null);
    }

    /**
     * Calculates the active entity delay
     *
     * @param entity        the entity
     * @param originalDelay the original delay
     * @return if the entity is inactive, the delay is multiplied by 4, otherwise the original delay is returned
     */
    protected int calcActiveDelay(@NotNull EntityIntelligent entity, int originalDelay) {
        return entity.isActive() ? originalDelay : originalDelay << 2;
    }

    /**
     * Describes the position of a ChunkSection
     */
    protected record ChunkSectionVector(int chunkX, int sectionY, int chunkZ) {
    }
}
