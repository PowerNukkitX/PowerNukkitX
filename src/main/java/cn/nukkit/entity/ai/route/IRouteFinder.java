package cn.nukkit.entity.ai.route;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.ai.route.data.Node;
import cn.nukkit.math.Vector3;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 此接口抽象了一个寻路器
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface IRouteFinder {
    /**
     * @return boolean 是否正在寻路
     */
    boolean isSearching();

    /**
     * @return boolean 是否完成寻路（找到有效路径）
     */
    boolean isFinished();

    /**
     * @return boolean 寻路是否被中断了
     */
    boolean isInterrupt();

    /**
     * 在调用此方法前，你应该首先尝试寻路，否则此方法始将终返回{@code true}
     *
     * @return 终点是否可到达
     */
    boolean isReachable();

    /**
     * 尝试开始寻路
     *
     * @return 是否成功找到路径
     */
    boolean search();

    /**
     * @return 寻路起点
     */
    Vector3 getStart();

    /**
     * 设置寻路起点，将会导致寻路中断
     *
     * @param vector3 寻路起点
     */
    void setStart(Vector3 vector3);

    /**
     * @return 寻路终点
     */
    Vector3 getTarget();

    /**
     * 设置寻路终点，将会导致寻路中断
     *
     * @param vector3 寻路终点
     */
    void setTarget(Vector3 vector3);

    /**
     * 获取寻路结果
     *
     * @return 一个包含 {@link Node} 的列表 {@link List}，应已排序好，第一项为寻路起点，最后一项为寻路终点，之间的为找到的路径点
     */
    List<Node> getRoute();

    /**
     * @return 是否有下一个节点 {@link Node}
     */
    boolean hasNext();

    /**
     * 获取下一个节点{@link Node}（如果有的话）
     *
     * @return 下一个节点
     */
    @Nullable
    Node next();

    /**
     * @return 当前索引所在位置是否有节点 {@link Node}
     */
    boolean hasCurrentNode();

    /**
     * @return 当前索引位置对应的节点 {@link Node}
     */
    Node getCurrentNode();

    /**
     * @return 当前索引
     */
    int getNodeIndex();

    /**
     * 设置当前索引
     *
     * @param index 索引值
     */
    void setNodeIndex(int index);

    /**
     * @return 指定索引位置的节点 {@link Node}
     */
    Node getNode(int index);
}
