package cn.nukkit.event.scoreboard;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.event.HandlerList;
import cn.nukkit.scoreboard.scoreboard.IScoreboard;
import cn.nukkit.scoreboard.scoreboard.IScoreboardLine;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

@PowerNukkitXOnly
@Since("1.19.30-r2")
@Getter
@Setter
public class ScoreboardLineChangeEvent extends ScoreboardEvent {

    private static final HandlerList handlers = new HandlerList();

    @Nullable
    private final IScoreboardLine line;
    private int newValue;
    private int oldValue;
    private final ActionType actionType;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public ScoreboardLineChangeEvent(IScoreboard scoreboard, @Nullable IScoreboardLine line, int newValue, int oldValue){
        this(scoreboard,line,newValue,oldValue,ActionType.SCORE_CHANGE);
    }

    public ScoreboardLineChangeEvent(IScoreboard scoreboard, @Nullable IScoreboardLine line, int newValue, int oldValue, ActionType actionType) {
        super(scoreboard);
        this.line = line;
        this.newValue = newValue;
        this.oldValue = oldValue;
        this.actionType = actionType;
    }

    public enum ActionType{
        SCORE_CHANGE,
        REMOVE_LINE,
        REMOVE_ALL_LINES,
        ADD_LINE
    }
}
