package cn.nukkit.network.protocol.types;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.19.40-r1")
public record PropertySyncData(int[] intProperties, float[] floatProperties) {
}
