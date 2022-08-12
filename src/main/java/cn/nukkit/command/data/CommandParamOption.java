package cn.nukkit.command.data;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.19.20-r2")
public enum CommandParamOption {
    SUPPRESS_ENUM_AUTOCOMPLETION,
    HAS_SEMANTIC_CONSTRAINT,
    ENUM_AS_CHAINED_COMMAND
}
