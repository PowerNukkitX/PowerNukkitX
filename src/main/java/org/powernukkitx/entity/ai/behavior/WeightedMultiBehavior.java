package org.powernukkitx.entity.ai.behavior;

import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A group consisting of multiple behaviors {@link IBehavior} (note the distinction with behavior groups {@link IBehaviorGroup})<br>
 * Before calling the method {@link #execute(EntityIntelligent)}, the evaluation function of this object must be called to confirm which behavior is activated<br>
 * During evaluation, all contained child behaviors are evaluated<br>
 * After filtering out the behaviors that return success, the group with the highest priority is selected<br>
 * If there are still multiple behaviors at this point, one of them is randomly selected for execution based on the return value of the {@link IBehavior#getWeight()} method of the behavior
 */


@Getter
public class WeightedMultiBehavior extends AbstractBehavior {
    /**
     * The priority of this group. In Behavior Group, getting the priority will return this value to refer to the priority of the entire group
     */
    protected final int priority;
    protected Set<IBehavior> behaviors;
    @Setter
    protected IBehavior currentBehavior;

    public WeightedMultiBehavior(int priority, IBehavior... behaviors) {
        this.priority = priority;
        this.behaviors = Set.of(behaviors);
    }

    public WeightedMultiBehavior(int priority, Set<IBehavior> behaviors) {
        this.priority = priority;
        this.behaviors = behaviors;
    }

    @Override
    public boolean evaluate(EntityIntelligent entity) {
        var result = evaluateBehaviors(entity);
        if (result.isEmpty()) {
            return false;
        }
        if (result.size() == 1) {
            setCurrentBehavior(result.iterator().next());
            return true;
        }
        //pick a behavior based on Weight
        int totalWeight = 0;
        for (IBehavior behavior : result) {
            totalWeight += behavior.getWeight();
        }
        int random = ThreadLocalRandom.current().nextInt(totalWeight + 1);
        for (IBehavior behavior : result) {
            random -= behavior.getWeight();
            if (random <= 0) {
                setCurrentBehavior(behavior);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        if (currentBehavior == null) {
            return false;
        }
        return currentBehavior.execute(entity);
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        if (currentBehavior == null) {
            return;
        }
        currentBehavior.onInterrupt(entity);
        currentBehavior.setBehaviorState(BehaviorState.STOP);
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        if (currentBehavior == null) {
            return;
        }
        currentBehavior.onStart(entity);
        currentBehavior.setBehaviorState(BehaviorState.ACTIVE);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        if (currentBehavior == null) {
            return;
        }
        currentBehavior.onStop(entity);
        currentBehavior.setBehaviorState(BehaviorState.STOP);
    }

    /**
     * @param entity the entity
     * @return the set of highest-priority behaviors that evaluated successfully (including the evaluation results)
     */
    protected Set<IBehavior> evaluateBehaviors(EntityIntelligent entity) {
        //stores the behaviors that evaluated successfully (priority not yet filtered)
        var evalSucceed = new HashSet<IBehavior>();
        int highestPriority = Integer.MIN_VALUE;
        for (IBehavior behavior : behaviors) {
            if (behavior.evaluate(entity)) {
                evalSucceed.add(behavior);
                if (behavior.getPriority() > highestPriority) {
                    highestPriority = behavior.getPriority();
                }
            }
        }
        //return empty if there are no evaluation results
        if (evalSucceed.isEmpty()) {
            return evalSucceed;
        }
        //filter out the lower-priority behaviors
        var result = new HashSet<IBehavior>();
        for (IBehavior entry : evalSucceed) {
            if (entry.getPriority() == highestPriority) {
                result.add(entry);
            }
        }
        return result;
    }
}
