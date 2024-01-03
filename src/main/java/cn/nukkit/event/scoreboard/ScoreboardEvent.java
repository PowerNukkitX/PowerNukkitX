package cn.nukkit.event.scoreboard;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.scoreboard.scoreboard.IScoreboard;
import lombok.Getter;

/**
 * 请注意，若计分板不存在于 {@link cn.nukkit.Server}::scoreboardManager中，则此事件不会被调用
 */
@Getter
public abstract class ScoreboardEvent extends Event implements Cancellable {

    @Getter
    private static final HandlerList handlers = new HandlerList();

    private final IScoreboard scoreboard;

    public ScoreboardEvent(IScoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public IScoreboard getScoreboard(){
        return this.scoreboard;
    }
}
