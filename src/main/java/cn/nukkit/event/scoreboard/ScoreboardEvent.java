package cn.nukkit.event.scoreboard;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.scoreboard.IScoreboard;
import lombok.Getter;

/**
 * 请注意，若计分板不存在于 {@link cn.nukkit.Server}::scoreboardManager中，则此事件不会被调用
 */
@Getter
public abstract class ScoreboardEvent extends Event implements Cancellable {

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final IScoreboard scoreboard;
    /**
     * @deprecated 
     */
    

    public ScoreboardEvent(IScoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public IScoreboard getScoreboard(){
        return this.scoreboard;
    }
}
