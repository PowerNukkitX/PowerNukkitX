package cn.nukkit.scoreboard.scoreboard;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.event.command.ScoreboardLineChangeEvent;
import cn.nukkit.scoreboard.scorer.IScorer;
import lombok.Getter;

@PowerNukkitXOnly
@Since("1.19.30-r1")
@Getter
public class ScoreboardLine implements IScoreboardLine{

    protected static long staticLineId = 0;

    protected final IScoreboard scoreboard;
    protected final IScorer scorer;
    protected final long lineId;
    protected int score;

    public ScoreboardLine(IScoreboard scoreboard, IScorer scorer) {
        this(scoreboard, scorer, 0);
    }

    public ScoreboardLine(IScoreboard scoreboard, IScorer scorer, int score) {
        this.scoreboard = scoreboard;
        this.scorer = scorer;
        this.score = score;
        this.lineId = ++staticLineId;
    }

    @Override
    public boolean setScore(int score) {
        if (scoreboard.shouldCallEvent()) {
            var event = new ScoreboardLineChangeEvent(scoreboard, this, score, this.score, ScoreboardLineChangeEvent.ActionType.SCORE_CHANGE);
            Server.getInstance().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return false;
            }
            score = event.getNewValue();
        }
        this.score = score;
        updateScore();
        return true;
    }
}
