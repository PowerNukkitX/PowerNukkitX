package cn.nukkit.event.scoreboard;

import cn.nukkit.event.HandlerList;
import cn.nukkit.scoreboard.scoreboard.IScoreboard;
import lombok.Getter;


public class ScoreboardObjectiveChangeEvent extends ScoreboardEvent{

    @Getter
    private static final HandlerList handlers = new HandlerList();

    private final ActionType actionType;

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
