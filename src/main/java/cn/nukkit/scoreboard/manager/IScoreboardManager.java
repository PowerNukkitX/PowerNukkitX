package cn.nukkit.scoreboard.manager;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.displayer.IScoreboardViewer;
import cn.nukkit.scoreboard.scoreboard.IScoreboard;
import cn.nukkit.scoreboard.storage.IScoreboardStorage;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

/**
 * 管理，储存一批计分板 <br>
 * 此接口面向/scoreboard命令，若只是想要显示信息，请直接操作scoreboard对象
 */
@PowerNukkitXOnly
@Since("1.19.30-r1")
public interface IScoreboardManager {
    boolean addScoreboard(IScoreboard scoreboard);
    boolean removeScoreboard(IScoreboard scoreboard);
    boolean removeScoreboard(String objectiveName);
    IScoreboard getScoreboard(String objectiveName);
    Map<String, IScoreboard> getScoreboards();
    boolean containScoreboard(IScoreboard scoreboard);
    boolean containScoreboard(String name);

    Map<DisplaySlot, IScoreboard> getDisplay();
    IScoreboard getDisplaySlot(DisplaySlot slot);
    void setDisplay(DisplaySlot slot, @Nullable IScoreboard scoreboard);

    Set<IScoreboardViewer> getViewers();
    boolean addViewer(IScoreboardViewer viewer);
    boolean removeViewer(IScoreboardViewer viewer);

    void onPlayerJoin(Player player);
    void beforePlayerQuit(Player player);

    IScoreboardStorage getStorage();
    void save();
    void read();
}
