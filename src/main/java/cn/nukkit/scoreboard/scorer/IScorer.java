package cn.nukkit.scoreboard.scorer;

import cn.nukkit.scoreboard.IScoreboard;
import cn.nukkit.scoreboard.IScoreboardLine;
import org.cloudburstmc.protocol.bedrock.data.ScoreInfo;

public interface IScorer {

    ScoreInfo.IdentityDefinitionType getScorerType();

    String getName();

    ScoreInfo toNetworkInfo(IScoreboard scoreboard, IScoreboardLine line);
}