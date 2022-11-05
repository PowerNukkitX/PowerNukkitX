package cn.nukkit.scoreboard.data;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

/**
 * 追踪目标显示顺序排序准则，客户端会依照准则根据分数排序所有追踪对象
 */
@PowerNukkitXOnly
@Since("1.19.30-r1")
public enum SortOrder {
    //升序
    ASCENDING,
    //降序
    DESCENDING
}
