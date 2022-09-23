package cn.nukkit.scoreboard.displayer;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.scoreboard.IScoreboard;
import cn.nukkit.scoreboard.scoreboard.IScoreboardLine;

/**
 * 计分板观察者 (eg: Player)
 * 此接口用于抽象服务端到客户端的协议层方法
 */
@PowerNukkitXOnly
@Since("1.19.30-r1")
public interface IScoreboardViewer {
    void display(IScoreboard scoreboard, DisplaySlot slot);
    void hide(DisplaySlot slot);
    void removeScoreboard(IScoreboard scoreboard);
    void removeLine(IScoreboardLine line);
    void updateScore(IScoreboardLine line);
}
