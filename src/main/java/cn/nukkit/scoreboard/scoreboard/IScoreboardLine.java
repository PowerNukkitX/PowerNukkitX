package cn.nukkit.scoreboard.scoreboard;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.network.protocol.SetScorePacket;
import cn.nukkit.scoreboard.scorer.IScorer;

/**
 * 计分板上的单个行 <br>
 * 由{@link IScorer}和分数组成
 */
@PowerNukkitXOnly
@Since("1.19.30-r1")
public interface IScoreboardLine {

    /**
     * 获取追踪对象
     * @return 追踪对象
     */
    IScorer getScorer();

    /**
     * 获取行id
     * 客户端通过此id标识计分板上的每个行
     * @return 行id
     */
    long getLineId();

    /**
     * 获取此计分行所属的计分板
     * @return 所属计分板
     */
    IScoreboard getScoreboard();

    /**
     * 获取分数
     * @return 分数
     */
    int getScore();

    /**
     * 设置分数
     * @param score 分数
     * @return 是否成功（事件被撤回就会false）
     */
    boolean setScore(int score);

    /**
     * 增加分数
     * @param addition 增加量
     * @return 是否成功（事件被撤回就会false）
     */
    default boolean addScore(int addition) {return setScore(getScore() + addition);}

    /**
     * 减少分数
     * @param reduction 减少量
     * @return 是否成功（事件被撤回就会false）
     */
    default boolean removeScore(int reduction) {return setScore(getScore() - reduction);}

    /**
     * 内部方法
     * 转换到network信息
     * @return network信息
     */
    default SetScorePacket.ScoreInfo toNetworkInfo() {
        return getScorer().toNetworkInfo(getScoreboard(), this);
    }

    /**
     * 内部方法
     * 通知所属计分板对象更新此行信息
     */
    default void updateScore() {
        getScoreboard().updateScore(this);
    }
}
