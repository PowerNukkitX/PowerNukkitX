package cn.nukkit.scoreboard.displayer;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.scoreboard.IScoreboard;
import cn.nukkit.scoreboard.scoreboard.IScoreboardLine;

@PowerNukkitXOnly
@Since("1.19.21-r5")
public interface IScoreboardViewer {
    void display(IScoreboard scoreboard, DisplaySlot slot);
    void hide(DisplaySlot slot);
    void removeLine(IScoreboardLine line);
    void updateLine(IScoreboardLine line);
}
