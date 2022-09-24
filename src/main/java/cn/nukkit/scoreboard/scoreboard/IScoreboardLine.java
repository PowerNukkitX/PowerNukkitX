package cn.nukkit.scoreboard.scoreboard;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.network.protocol.SetScorePacket;
import cn.nukkit.scoreboard.scorer.IScorer;

/**
 * 计分板上的单个行 <br>
 * 由{@link IScorer}和分数组成
 */
@PowerNukkitXOnly
@Since("1.19.30-r1")
public interface IScoreboardLine {
    IScorer getScorer();
    long getLineId();
    IScoreboard getScoreboard();
    int getScore();
    boolean setScore(int score);
    default boolean addScore(int addition) {return setScore(getScore() + addition);}

    default boolean removeScore(int reduction) {return setScore(getScore() - reduction);}
    default SetScorePacket.ScoreInfo toNetworkInfo() {
        return getScorer().toNetworkInfo(getScoreboard(), this);
    }
    default void updateScore() {
        getScoreboard().updateScore(this);
    }
}
