package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.trim.ItemTrimPatternType;

/**
 * @author glorydark
 * @date {2023/8/25} {12:13}
 */
@PowerNukkitXOnly
@Since("1.20.30-r2")
public interface ItemTrimPattern {

    ItemTrimPatternType getPattern();
}
