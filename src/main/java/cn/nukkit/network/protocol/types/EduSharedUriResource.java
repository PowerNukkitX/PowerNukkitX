package cn.nukkit.network.protocol.types;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;


public record EduSharedUriResource(String buttonName, String linkUri) {
    public static final EduSharedUriResource EMPTY = new EduSharedUriResource("", "");
}
