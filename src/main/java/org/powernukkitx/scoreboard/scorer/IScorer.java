package org.powernukkitx.scoreboard.scorer;

import org.cloudburstmc.protocol.bedrock.data.payload.scoreboard.ScoreInfo;
import org.cloudburstmc.protocol.bedrock.data.payload.scoreboard.ScorePacketEntryAction;
import org.powernukkitx.scoreboard.IScoreboard;
import org.powernukkitx.scoreboard.IScoreboardLine;

public interface IScorer {

    ScorePacketEntryAction getScorerType();

    String getName();

    ScoreInfo toNetworkInfo(IScoreboard scoreboard, IScoreboardLine line);
}
