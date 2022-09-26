package cn.nukkit.scoreboard.scoreboard;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.data.SortOrder;
import cn.nukkit.scoreboard.displayer.IScoreboardViewer;
import cn.nukkit.scoreboard.scorer.IScorer;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 计分板对象
 * 可被发送到任何实现了{@link IScoreboardViewer}接口的对象上
 */
@PowerNukkitXOnly
@Since("1.19.30-r1")
public interface IScoreboard {
    /**
     * @return 此计分板的标识名称
     */
    String getObjectiveName();

    /**
     * @return 此计分板的显示名称
     */
    String getDisplayName();

    /**
     * @return 此计分板的 “准则” (eg: dummy)
     */
    String getCriteriaName();

    /**
     * @return 此计分板的排序规则
     */
    SortOrder getSortOrder();

    /**
     * 设置计分板的排序规则
     * @param order 排序规则
     */
    void setSortOrder(SortOrder order);

    /**
     * @return 此计分板的所有观察者
     */
    Set<IScoreboardViewer> getAllViewers();

    /**
     * @param slot 目标槽位
     * @return 此计分板目标槽位的观察者
     */
    Set<IScoreboardViewer> getViewers(DisplaySlot slot);

    /**
     * 删除此计分板目标槽位中的某个观察者
     * @param viewer 目标观察者
     * @param slot 目标槽位
     * @return 是否删除成功
     */
    boolean removeViewer(IScoreboardViewer viewer, DisplaySlot slot);

    /**
     * 向此计分板目标槽位中添加一个观察者
     * @param viewer 目标观察者
     * @param slot 目标槽位
     * @return 是否添加成功
     */
    boolean addViewer(IScoreboardViewer viewer, DisplaySlot slot);

    /**
     * 检查此计分板目标槽位中是否有特定观察者
     * @param viewer 目标观察者
     * @param slot 目标槽位
     * @return 是否存在
     */
    boolean containViewer(IScoreboardViewer viewer, DisplaySlot slot);

    /**
     * @return 此计分板的所有行
     */
    Map<IScorer, IScoreboardLine> getLines();

    /**
     * 获取追踪对象在此计分板上对应的行（如果存在）
     * @param scorer 追踪对象
     * @return 对应行
     */
    @Nullable IScoreboardLine getLine(IScorer scorer);

    /**
     * 为此计分板添加一个行
     * @param line 目标行
     * @return 是否添加成功
     */
    boolean addLine(IScoreboardLine line);

    /**
     * 为此计分板添加一个行
     * @param scorer 追踪对象
     * @param score 分数
     * @return 是否添加成功
     */
    boolean addLine(IScorer scorer, int score);

    /**
     * 为插件提供的便捷的计分板显示接口
     * @param text FakeScorer的名称
     * @param score 分数
     * @return 是否添加成功
     */
    boolean addLine(String text, int score);

    /**
     * 删除追踪对象在此计分板上对应的行（如果存在）
     * @param scorer 目标追踪对象
     * @return 是否删除成功
     */
    boolean removeLine(IScorer scorer);

    /**
     * 删除计分板所有行
     * @param send 是否发送到观察者
     * @return 是否删除成功
     */
    boolean removeAllLine(boolean send);

    /**
     * 检查追踪对象在此计分板上是否有记录
     * @param scorer 目标追踪对象
     * @return 是否存在
     */
    boolean containLine(IScorer scorer);

    /**
     * 向所有观察者发送新的分数 <br>
     * @param update 需要更新的行
     */
    void updateScore(IScoreboardLine update);

    /**
     * 向所有观察者重新发送此计分板以及行信息 <br>
     * 例如当对计分板进行了大量的更改后，调用此方法 <br>
     * 可节省大量带宽
     */
    void resend();

    /**
     * 为插件提供的快捷接口 <br>
     * 按照List顺序设置计分板的内容 (使用FakeScorer作为追踪对象) <br>
     * 会覆盖之前的所有行 <br>
     * @param lines 需要设置的字符串内容
     */
    void setLines(List<String> lines);

    /**
     * 按照List顺序设置计分板的内容 <br>
     * 会覆盖之前的所有行 <br>
     * @param lines 需要设置的行内容
     */
    void setLines(Collection<IScoreboardLine> lines);
}
