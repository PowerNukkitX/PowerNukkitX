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
    /**
     * 在指定槽位显示计分板
     * @param scoreboard 目标计分板
     * @param slot 目标槽位
     */
    void display(IScoreboard scoreboard, DisplaySlot slot);

    /**
     * 清除指定槽位的显示内容
     * @param slot 目标槽位
     */
    void hide(DisplaySlot slot);

    /**
     * 通知观察者计分板已删除（若计分板在任意显示槽位中，则会一并清除槽位）
     * @param scoreboard 目标计分板
     */
    void removeScoreboard(IScoreboard scoreboard);

    /**
     * 通知观察者指定计分板上的指定行已删除
     * @param line 目标行
     */
    void removeLine(IScoreboardLine line);

    /**
     * 向观察者发送指定行的新分数
     * @param line 目标行
     */
    void updateScore(IScoreboardLine line);
}
