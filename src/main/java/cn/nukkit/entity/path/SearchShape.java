package cn.nukkit.entity.path;

import org.jetbrains.annotations.NotNull;

/**
 * 对每一个点周围的相邻点的搜索形状，此迭代器应返回所有应当被搜索的相邻点
 */
public interface SearchShape extends Iterable<Node> {
    /**
     * 设置搜索中心点，此操作应归零迭代器状态。
     *
     * @param node 中心点
     */
    void setCenterNode(@NotNull Node node);
}
