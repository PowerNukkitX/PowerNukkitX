package cn.nukkit.command.tree;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.19.50-r4")
public enum ParamNodeType {
    INT,
    FLOAT,
    WILDCARD_INT,
    TARGET,
    WILDCARD_TARGET,
    EQUIPMENT_SLOT,
    STRING,
    BLOCK_POSITION,
    POSITION,
    MESSAGE,
    RAWTEXT,
    JSON,
    TEXT,
    COMMAND,
    FILE_PATH,
    OPERATOR,
    COMPARE_OPERATOR,
    FULL_INTEGER_RANGE,
    BLOCK_STATES,
    ENUM
}
