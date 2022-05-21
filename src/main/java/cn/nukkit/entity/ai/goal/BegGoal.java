package cn.nukkit.entity.ai.goal;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.path.AStarPathFinder;
import cn.nukkit.entity.ai.path.Node;
import cn.nukkit.entity.ai.sensor.common.BegInterestSensor;

import java.util.List;

public class BegGoal extends AbstractGoal {

    @Override
    public boolean shouldStart(int currentTick, EntityIntelligent entity) {
        var interestTarget = entity.getMemory(BegInterestSensor.class, List.class);
        return super.shouldStart(currentTick, entity) && interestTarget != null && !interestTarget.isEmpty();
    }

    @Override
    public boolean shouldStop(int currentTick, EntityIntelligent entity) {
        var interestTarget = entity.getMemory(BegInterestSensor.class, List.class);
        return interestTarget == null || interestTarget.isEmpty();
    }

    @Override
    public void execute(int currentTick, EntityIntelligent entity) {
        var interestTarget = entity.getMemory(BegInterestSensor.class, List.class);
        if (interestTarget != null) {
            Entity nearest = null;
            double lenSquared = Double.MAX_VALUE;
            for (var each : interestTarget) {
                if (each instanceof Entity target) {
                    if (nearest == null) {
                        nearest = target;
                    } else {
                        var tmpLen = target.distanceSquared(entity);
                        if (tmpLen < lenSquared) {
                            lenSquared = tmpLen;
                            nearest = target;
                        }
                    }
                }
            }
            if (nearest != null) {
                entity.lookAtTarget = nearest.add(0, nearest.getEyeHeight());
                var pathFinder = new AStarPathFinder();
                var destinationNode = new Node(nearest.x, nearest.y, nearest.z, null);
                destinationNode.setParent(destinationNode);
                var startNode = new Node(entity.x, entity.y, entity.z, destinationNode);
                startNode.setRoot(true);
                pathFinder.setPathThinker(entity);
                pathFinder.setDestination(destinationNode);
                pathFinder.allowDestinationOffset(2);
                pathFinder.setStart(startNode);
                pathFinder.prepareSearch();
                var result = pathFinder.search();
                if (result) {
                    entity.setPathFinder(pathFinder);
                }
            }
        }
    }

    @Override
    public int getOrder() {
        return ORDER_MEDIUM_HIGH;
    }
}
