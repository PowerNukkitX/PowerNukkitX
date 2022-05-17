package cn.nukkit.entity.ai.goal;

import cn.nukkit.entity.EntityIntelligent;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractGoal implements Goal {
    public static final int ORDER_DEFAULT = 0;
    public static final int ORDER_LOW = 800000000;

    public static final int ORDER_FOLLOW_PATH = ORDER_LOW;

    @NotNull
    protected GoalState goalState = GoalState.NOT_WORKING;

    @Override
    public boolean shouldStart(int currentTick, EntityIntelligent entity) {
        if (goalState == GoalState.JUST_DONE) {
            goalState = GoalState.NOT_WORKING;
        }
        return Goal.super.shouldStart(currentTick, entity);
    }

    @Override
    public void start(int currentTick, EntityIntelligent entity) {
        goalState = GoalState.WORKING;
    }

    @Override
    public void stop(int currentTick, EntityIntelligent entity) {
        goalState = GoalState.JUST_DONE;
    }

    @Override
    public GoalState getState() {
        return goalState;
    }

    @Override
    public int getOrder() {
        return ORDER_DEFAULT;
    }
}
