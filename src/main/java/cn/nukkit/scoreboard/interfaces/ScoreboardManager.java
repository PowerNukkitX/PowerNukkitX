package cn.nukkit.scoreboard.interfaces;

public interface ScoreboardManager {
    Scoreboard getScoreboard(String name);
    Scoreboard[] getAllScoreboards(String name);
}
