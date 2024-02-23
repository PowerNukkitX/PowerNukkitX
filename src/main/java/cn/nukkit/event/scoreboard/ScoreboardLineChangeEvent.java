package cn.nukkit.event.scoreboard;

import cn.nukkit.event.HandlerList;
import cn.nukkit.scoreboard.IScoreboard;
import cn.nukkit.scoreboard.IScoreboardLine;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;


@Getter
@Setter
public class ScoreboardLineChangeEvent extends ScoreboardEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private @Nullable final IScoreboardLine line;
    private int newValue;
    private int oldValue;
    private final ActionType actionType;

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
