package cn.nukkit.scoreboard.scorer;

import cn.nukkit.network.protocol.SetScorePacket;
import cn.nukkit.scoreboard.data.ScorerType;
import cn.nukkit.scoreboard.IScoreboard;
import cn.nukkit.scoreboard.IScoreboardLine;

/**
 * 计分板追踪对象
 */


public interface IScorer {

    /**
     * 获取追踪对象类型
     * @return 追踪对象类型
     */
    ScorerType getScorerType();

    /**
     * 获取名称
     * @return 追踪对象类型
     */
    String getName();

    /**
     * 内部方法
     * 转换到network信息
     * @param scoreboard 所属计分板
     * @param line 所属行
     * @return network信息
     */
    SetScorePacket.ScoreInfo toNetworkInfo(IScoreboard scoreboard, IScoreboardLine line);
}
