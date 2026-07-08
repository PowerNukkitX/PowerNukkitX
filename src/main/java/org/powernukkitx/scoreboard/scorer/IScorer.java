package org.powernukkitx.scoreboard.scorer;

import org.powernukkitx.scoreboard.IScoreboard;
import org.powernukkitx.scoreboard.IScoreboardLine;
import org.cloudburstmc.protocol.bedrock.data.ScoreInfo;

public interface IScorer {

    ScoreInfo.IdentityDefinitionType getScorerType();

    String getName();

    ScoreInfo toNetworkInfo(IScoreboard scoreboard, IScoreboardLine line);
}