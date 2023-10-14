package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.trim.ItemTrimMaterialType;

/**
 * @author glorydark
 * @date {2023/8/25} {12:13}
 */
@PowerNukkitXOnly
@Since("1.20.30-r2")
public interface ItemTrimMaterial {

    ItemTrimMaterialType getMaterial();
}
