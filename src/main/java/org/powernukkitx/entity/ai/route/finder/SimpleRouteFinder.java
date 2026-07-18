package org.powernukkitx.entity.ai.route.finder;

import org.powernukkitx.entity.ai.route.data.Node;
import org.powernukkitx.entity.ai.route.posevaluator.IPosEvaluator;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for non-asynchronous pathfinding <br/>
 * In PowerNukkitX's mob AI architecture, pathfinding for different entities is parallel rather than asynchronous <br/>
 * So we do not need asynchronous pathfinding
 */


public abstract class SimpleRouteFinder implements IRouteFinder {

    //List used to store pathfinding results
    protected List<Node> nodes = new ArrayList<>();

    //index value
    protected int currentIndex = 0;

    //block evaluator
    @Getter
    protected IPosEvaluator evalPos;

    public SimpleRouteFinder(IPosEvaluator blockEvaluator) {
        this.evalPos = blockEvaluator;
    }

    //add a pathfinding result node
    protected void addNode(Node node) {
        nodes.add(node);
    }

    //add pathfinding result nodes in bulk
    protected void addNode(List<Node> node) {
        nodes.addAll(node);
    }

    //reset pathfinding results
    protected void resetNodes() {
        this.nodes.clear();
    }

    @Override
    public List<Node> getRoute() {
        return new ArrayList<>(this.nodes);
    }

    @Override
    public @Nullable Node getCurrentNode() {
        if (this.hasCurrentNode()) {
            return nodes.get(currentIndex);
        }
        return null;
    }

    @Override
    public boolean hasNext() {
        try {
            if (this.currentIndex + 1 < nodes.size()) {
                return this.nodes.get(this.currentIndex + 1) != null;
            }
        } catch (Exception ignore) {
        }
        return false;
    }

    @Override
    public @Nullable Node next() {
        if (this.hasNext()) {
            return this.nodes.get(++currentIndex);
        }
        return null;
    }

    @Override
    public boolean hasCurrentNode() {
        return currentIndex < this.nodes.size();
    }

    @Override
    public int getNodeIndex() {
        return this.currentIndex;
    }

    @Override
    public void setNodeIndex(int index) {
        this.currentIndex = index;
    }

    @Override
    public @Nullable Node getNode(int index) {
        if (index + 1 < nodes.size()) {
            return this.nodes.get(index);
        }
        return null;
    }
}
