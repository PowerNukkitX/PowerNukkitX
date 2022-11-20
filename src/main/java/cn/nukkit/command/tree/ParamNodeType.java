package cn.nukkit.command.tree;

/**
 * 基本上和{@link cn.nukkit.command.data.CommandParamType}一一对应,但是多一个枚举类型
 */
public enum ParamNodeType {
    INT,
    FLOAT,
    VALUE,
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
