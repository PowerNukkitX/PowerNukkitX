package cn.nukkit.event.command;

import cn.nukkit.event.HandlerList;
import cn.nukkit.scoreboard.Scoreboard;

public class ScoreboardObjectiveChangeEvent extends ScoreboardEvent{

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final ActionType actionType;

    public ScoreboardObjectiveChangeEvent(Scoreboard scoreboard, ActionType actionType) {
        super(scoreboard);
        this.actionType = actionType;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public enum ActionType{
        ADD,
        REMOVE
    }
}
