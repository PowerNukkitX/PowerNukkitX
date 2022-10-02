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
@Since("1.19.30-r1")
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
