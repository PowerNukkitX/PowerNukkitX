package cn.nukkit.scoreboard.interfaces;

import cn.nukkit.scoreboard.Scoreboard;
import cn.nukkit.scoreboard.data.DisplaySlot;

import java.util.Map;

public interface ScoreboardStorage {
    void saveScoreboard(Scoreboard scoreboard);
    void saveScoreboard(Scoreboard[] scoreboards);
    void saveDisplay(Map<DisplaySlot,String> display);
    Map<String, Scoreboard> readScoreboard(AbstractScoreboardManager manager);
    Scoreboard readScoreboard(String name, AbstractScoreboardManager manager);
    Map<DisplaySlot,String> readDisplay();
    void removeScoreboard(String name);
    boolean containScoreboard(String name);
}
