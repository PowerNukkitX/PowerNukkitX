package cn.nukkit.scoreboard.interfaces;

import cn.nukkit.scoreboard.Scoreboard;
import cn.nukkit.scoreboard.data.DisplaySlot;

public interface ScoreboardSendable {
    void sendScoreboard(Scoreboard scoreboard, DisplaySlot slot);
    void clearScoreboardSlot(DisplaySlot slot);
}
