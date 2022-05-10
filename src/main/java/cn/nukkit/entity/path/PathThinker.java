package cn.nukkit.entity.path;

import org.jetbrains.annotations.NotNull;

/**
 * 能够寻路的对象应该实现此接口，用于给寻路器提供信息
 */
public interface PathThinker {
    /**
     * 计算两点间移动的代价，通常水平移动1格为10，0.5格为5，斜向移动一格为14。
     * 两个点不保证相邻，有可能会发生坠落，攀爬，跳跃导致不相邻。
     * 如果两个点之间是直接不可达的，应返回{@link Long#MAX_VALUE} (9223372036854775807L)。
     *
     * @param from 起点
     * @param to   终点
     * @return 代价
     */
    long calcCost(@NotNull Node from, @NotNull Node to);

    /**
     * 计算这个位置是否能经过，需要考虑的点包括但不限于此处是否有方块阻挡，脚下是否是空气能站立，梯子/藤蔓是否能攀爬等。
     *
     * @param node 要计算可通过性的点
     * @return 是否可通过
     */
    boolean canPassThrough(@NotNull Node node);

    /**
     * @return 搜索形状
     * @see SearchShape
     */
    @NotNull
    SearchShape getSearchShape();
}
