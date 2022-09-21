package cn.nukkit.scoreboard.scorer;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.network.protocol.SetScorePacket;
import cn.nukkit.scoreboard.data.ScorerType;
import cn.nukkit.scoreboard.scoreboard.IScoreboard;
import cn.nukkit.scoreboard.scoreboard.IScoreboardLine;

/**
 * 计分板追踪对象
 */
@PowerNukkitXOnly
@Since("1.19.21-r5")
public interface IScorer {
    ScorerType getScorerType();

    String getName();

    SetScorePacket.ScoreInfo toNetworkInfo(IScoreboard scoreboard, IScoreboardLine line);
}
