package cn.nukkit.entity.ai.route;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.Vector3;

import java.util.ArrayList;

/**
 * 寻路器接口
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
     * 是否可到达终点
     * 在调用此方法前，你应该首先尝试寻路，否则此方法始将终返回true
     */
    boolean isReachable();

    /**
     * 尝试寻路
     *
     * @return boolean 是否成功找到路径
     */
    boolean search();

    /**
     * 获取起始点
     */
    Vector3 getStart();

    /**
     * 设置寻路起点，将会导致寻路中断
     */
    void setStart(Vector3 vector3);

    /**
     * 获取终点
     */
    Vector3 getTarget();

    /**
     * 设置寻路终点，将会导致寻路中断
     */
    void setTarget(Vector3 vector3);

    /**
     * 获取寻路结果
     */
    ArrayList<Node> getRoute();

    /**
     * 是否有下一个节点
     */
    boolean hasNext();

    /**
     * 下一个节点（如果有的话）
     */
    Node next();

    /**
     * 当前索引位置是否有节点
     */
    boolean hasCurrentNode();

    /**
     * 获取当前节点
     */
    Node getCurrentNode();

    /**
     * 获取当前节点索引
     */
    int getNodeIndex();

    /**
     * 设置当前索引位置
     */
    void setNodeIndex(int index);
}
