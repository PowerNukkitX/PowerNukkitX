package cn.nukkit.scoreboard.storage;

import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.IScoreboard;

import java.util.Collection;
import java.util.Map;

/**
 * 计分板存储器接口
 */


public interface IScoreboardStorage {
    void saveScoreboard(IScoreboard scoreboard);

    void saveScoreboard(Collection<IScoreboard> scoreboards);

    void saveDisplay(Map<DisplaySlot, IScoreboard> display);

    Map<String, IScoreboard> readScoreboard();

    IScoreboard readScoreboard(String name);

    Map<DisplaySlot, String> readDisplay();

    void removeScoreboard(String name);

    void removeAllScoreboard();

    boolean containScoreboard(String name);
}
