package cn.nukkit.scoreboard.manager;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.displayer.IScoreboardViewer;
import cn.nukkit.scoreboard.scoreboard.IScoreboard;
import cn.nukkit.scoreboard.storage.IScoreboardStorage;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

/**
 * 管理，储存一批计分板 <br>
 * 此接口面向/scoreboard命令，若只是想要显示信息，请直接操作scoreboard对象
 */
@PowerNukkitXOnly
@Since("1.19.30-r1")
public interface IScoreboardManager {
    /**
     * 添加一个计分板
     * @param scoreboard 目标计分板
     * @return 是否添加成功（返回false若计分板已存在或者事件被撤销）
     */
    boolean addScoreboard(IScoreboard scoreboard);

    /**
     * 删除一个计分板
     * @param scoreboard 目标计分板
     * @return 是否删除成功（返回false若计分板已存在或者事件被撤销）
     */
    boolean removeScoreboard(IScoreboard scoreboard);

    /**
     * 删除一个计分板
     * @param objectiveName 目标计分板标识名称
     * @return 是否删除成功（返回false若计分板已存在或者事件被撤销）
     */
    boolean removeScoreboard(String objectiveName);

    /**
     * 获取计分板对象（若存在）
     * @param objectiveName 目标计分板标识名称
     * @return 计分板对象
     */
    @Nullable IScoreboard getScoreboard(String objectiveName);

    /**
     * 获取所有计分板对象
     * @return 所有计分板对象
     */
    Map<String, IScoreboard> getScoreboards();

    /**
     * 检查是否存在指定计分板
     * @param scoreboard 指定计分板
     * @return 是否存在
     */
    boolean containScoreboard(IScoreboard scoreboard);

    /**
     * 检查是否存在指定计分板
     * @param name 指定计分板标识名称
     * @return 是否存在
     */
    boolean containScoreboard(String name);

    /**
     * 获取显示槽位信息
     * @return 显示槽位信息
     */
    Map<DisplaySlot, IScoreboard> getDisplay();

    /**
     * 获取指定显示槽位的计分板（若存在）
     * @param slot 指定槽位
     * @return 计分板对象
     */
    @Nullable IScoreboard getDisplaySlot(DisplaySlot slot);

    /**
     * 设置指定槽位显示计分板
     * 若形参scoreboard为null,则清除指定槽位内容
     * @param slot 指定槽位
     * @param scoreboard 计分板对象
     */
    void setDisplay(DisplaySlot slot, @Nullable IScoreboard scoreboard);

    /**
     * 获取所有观察者
     * @return 所有观察者
     */
    Set<IScoreboardViewer> getViewers();

    /**
     * 添加一个观察者
     * @param viewer 目标观察者
     * @return 是否添加成功
     */
    boolean addViewer(IScoreboardViewer viewer);

    /**
     * 删除一个观察者（若存在）
     * @param viewer 目标观察者
     * @return 是否删除成功
     */
    boolean removeViewer(IScoreboardViewer viewer);

    /**
     * 服务端内部方法
     */
    void onPlayerJoin(Player player);

    /**
     * 服务端内部方法
     */
    void beforePlayerQuit(Player player);

    /**
     * 服务端内部方法
     */
    void onEntityDead(EntityLiving entity);

    /**
     * 获取计分板存储器实例
     * @return 存储器实例
     */
    IScoreboardStorage getStorage();

    /**
     * 通过存储器保存计分板信息
     */
    void save();

    /**
     * 从存储器重新读取计分板信息
     */
    void read();
}
