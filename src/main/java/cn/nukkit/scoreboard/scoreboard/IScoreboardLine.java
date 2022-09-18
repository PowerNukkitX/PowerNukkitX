package cn.nukkit.scoreboard.scoreboard;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.network.protocol.SetScorePacket;
import cn.nukkit.scoreboard.scorer.IScorer;

@PowerNukkitXOnly
@Since("1.19.21-r5")
public interface IScoreboardLine {
    IScorer getScorer();
    long getLineId();
    IScoreboard getScoreboard();
    int getScore();
    void setScore(int score);
    default void addScore(int addition) {setScore(getScore() + addition);}

    default void removeScore(int reduction) {setScore(getScore() - reduction);}
    default SetScorePacket.ScoreInfo toNetworkInfo() {
        return getScorer().toNetworkInfo(getScoreboard(), this);
    }
}
