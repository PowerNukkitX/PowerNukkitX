package cn.nukkit.event.command;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.scoreboard.scoreboard.Scoreboard;

public abstract class ScoreboardEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private Scoreboard scoreboard;

    public ScoreboardEvent(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Scoreboard getScoreboard(){
        return this.scoreboard;
    }
}
