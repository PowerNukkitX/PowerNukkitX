package cn.nukkit.scoreboard.scoreboard;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.scoreboard.scorer.IScorer;
import lombok.Getter;

@PowerNukkitXOnly
@Since("1.19.21-r5")
@Getter
public class ScoreboardLine implements IScoreboardLine{

    protected static long staticLineId = 0;

    protected IScoreboard scoreboard;
    protected IScorer scorer;
    protected int score;
    protected long lineId;

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
    public void setScore(int score) {
        this.score = score;
        scoreboard.updateLine(this);
    }
}
