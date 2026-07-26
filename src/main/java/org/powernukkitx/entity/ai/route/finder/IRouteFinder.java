package org.powernukkitx.entity.ai.route.finder;

import org.powernukkitx.entity.ai.route.data.Node;
import org.powernukkitx.math.Vector3;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This interface abstracts a pathfinder
 */


public interface IRouteFinder {
    /**
     * @return boolean whether it is currently searching
     */
    boolean isSearching();

    /**
     * @return boolean whether pathfinding is finished (a valid path was found)
     */
    boolean isFinished();

    /**
     * @return boolean whether pathfinding was interrupted
     */
    boolean isInterrupt();

    /**
     * Before calling this method, you should first attempt to search, otherwise this method will always return {@code true}
     *
     * @return whether the target is reachable
     */
    boolean isReachable();

    /**
     * Attempt to start pathfinding
     *
     * @return whether a path was successfully found
     */
    boolean search();

    /**
     * @return the pathfinding start point
     */
    Vector3 getStart();

    /**
     * Set the pathfinding start point, which will interrupt the current search
     *
     * @param vector3 the pathfinding start point
     */
    void setStart(Vector3 vector3);

    /**
     * @return the pathfinding target point
     */
    Vector3 getTarget();

    /**
     * Set the pathfinding target point, which will interrupt the current search
     *
     * @param vector3 the pathfinding target point
     */
    void setTarget(Vector3 vector3);

    /**
     * @return the reachable target point
     */
    Vector3 getReachableTarget();

    /**
     * Get the pathfinding result
     *
     * @return a {@link List} containing {@link Node}s, which should already be sorted, with the first item being the start point, the last item being the target point, and the ones in between being the path points found
     */
    List<Node> getRoute();

    /**
     * @return whether there is a next {@link Node}
     */
    boolean hasNext();

    /**
     * Get the next {@link Node} (if there is one)
     *
     * @return the next node
     */
    @Nullable
    Node next();

    /**
     * @return whether there is a {@link Node} at the current index position
     */
    boolean hasCurrentNode();

    /**
     * @return the {@link Node} at the current index position
     */
    Node getCurrentNode();

    /**
     * @return the current index
     */
    int getNodeIndex();

    /**
     * Set the current index
     *
     * @param index the index value
     */
    void setNodeIndex(int index);

    /**
     * @return the {@link Node} at the specified index position
     */
    Node getNode(int index);
}
