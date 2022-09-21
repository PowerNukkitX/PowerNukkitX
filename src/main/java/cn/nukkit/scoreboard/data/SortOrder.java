package cn.nukkit.scoreboard.data;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

/**
 * 计分项排序准则，客户端会依照准则根据计分项分数排序计分板内容
 */
@PowerNukkitXOnly
@Since("1.19.21-r5")
public enum SortOrder {
    ASCENDING,
    DESCENDING
}
