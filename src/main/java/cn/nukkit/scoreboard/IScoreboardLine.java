package cn.nukkit.scoreboard;

import cn.nukkit.scoreboard.scorer.IScorer;
import org.cloudburstmc.protocol.bedrock.data.ScoreInfo;

public interface IScoreboardLine {

    IScorer getScorer();

    long getLineId();

    IScoreboard getScoreboard();

    int getScore();

    boolean setScore(int score);

    default boolean addScore(int addition) {
        return setScore(getScore() + addition);
    }

    default boolean removeScore(int reduction) {
        return setScore(getScore() - reduction);
    }

    default ScoreInfo toNetworkInfo() {
        return getScorer().toNetworkInfo(getScoreboard(), this);
    }

    default void updateScore() {
        getScoreboard().updateScore(this);
    }
}