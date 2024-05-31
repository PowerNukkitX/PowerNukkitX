package cn.nukkit.event.scoreboard;

import cn.nukkit.event.HandlerList;
import cn.nukkit.scoreboard.IScoreboard;

public class ScoreboardObjectiveChangeEvent extends ScoreboardEvent{

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final ActionType actionType;
    /**
     * @deprecated 
     */
    

    public ScoreboardObjectiveChangeEvent(IScoreboard scoreboard, ActionType actionType) {
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
