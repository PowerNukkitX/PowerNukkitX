package cn.nukkit.network.protocol.types;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;


public record PropertySyncData(int[] intProperties, float[] floatProperties) {
}
