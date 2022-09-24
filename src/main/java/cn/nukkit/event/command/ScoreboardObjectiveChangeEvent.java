package cn.nukkit.event.command;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.event.HandlerList;
import cn.nukkit.scoreboard.scoreboard.IScoreboard;
import cn.nukkit.scoreboard.scoreboard.Scoreboard;

@PowerNukkitXOnly
@Since("1.19.30-r2")
public class ScoreboardObjectiveChangeEvent extends ScoreboardEvent{

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

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
