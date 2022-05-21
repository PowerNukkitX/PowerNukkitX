package cn.nukkit.entity.ai.goal;

import cn.nukkit.entity.EntityIntelligent;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractGoal implements Goal {

    public static final int ORDER_EMERGENCY = 200000000;
    public static final int ORDER_HIGHEST = 300000000;
    public static final int ORDER_HIGH = 400000000;
    public static final int ORDER_MEDIUM_HIGH = 500000000;
    public static final int ORDER_MEDIUM = 600000000;
    public static final int ORDER_MEDIUM_LOW = 700000000;
    public static final int ORDER_LOW = 800000000;
    public static final int ORDER_LOWEST = 900000000;
    public static final int ORDER_DEFAULT = ORDER_MEDIUM;

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
