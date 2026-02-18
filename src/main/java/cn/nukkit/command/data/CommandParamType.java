package cn.nukkit.command.data;

/**
 * Represents the supported parameter types for commands in PowerNukkitX.
 * <p>
 * This enum defines all possible argument types that can be used in command registration and parsing.
 * Each type is mapped to an integer ID from {@link org.cloudburstmc.protocol.bedrock.packet.AvailableCommandsPacket} for protocol compatibility.
 * <p>
 * Features:
 * <ul>
 *   <li>Supports primitive types (int, float, string, etc.), target selectors, block positions, JSON, and more.</li>
 *   <li>Includes wildcard and specialized types for advanced command scenarios.</li>
 *   <li>Provides backwards compatibility for legacy types (e.g., {@link #TEXT}).</li>
 *   <li>Each type is associated with a protocol ID for serialization and network communication.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Use in {@link cn.nukkit.command.data.CommandParameter} to specify the type of a command argument.</li>
 *   <li>Retrieve the protocol ID using {@link #getId()} for packet serialization.</li>
 * </ul>
 * <p>
 * Example:
 * <pre>
 * CommandParamType type = CommandParamType.INT;
 * int protocolId = type.getId();
 * </pre>
 *
 * @author CreeperFace
 * @see cn.nukkit.command.data.CommandParameter
 * @see org.cloudburstmc.protocol.bedrock.packet.AvailableCommandsPacket
 */
public enum CommandParamType {
    /** Integer argument type. */
    INT(org.cloudburstmc.protocol.bedrock.data.command.CommandParamType.INT),
    /** Floating-point argument type. */
    FLOAT(org.cloudburstmc.protocol.bedrock.data.command.CommandParamType.FLOAT),
    /** Value argument type (generic). */
    VALUE(org.cloudburstmc.protocol.bedrock.data.command.CommandParamType.VALUE),
    /** Wildcard integer argument type. */
    WILDCARD_INT(org.cloudburstmc.protocol.bedrock.data.command.CommandParamType.WILDCARD_INT),
    /** Target selector argument type (e.g., @p, @a). */
    TARGET(org.cloudburstmc.protocol.bedrock.data.command.CommandParamType.TARGET),
    /** Wildcard target selector argument type. */
    WILDCARD_TARGET(org.cloudburstmc.protocol.bedrock.data.command.CommandParamType.WILDCARD_TARGET),
    /** Equipment slot argument type. */
    EQUIPMENT_SLOT(org.cloudburstmc.protocol.bedrock.data.command.CommandParamType.EQUIPMENT_SLOTS),
    /** String argument type. */
    STRING(org.cloudburstmc.protocol.bedrock.data.command.CommandParamType.STRING),
    /** Block position argument type (x y z). */
    BLOCK_POSITION(org.cloudburstmc.protocol.bedrock.data.command.CommandParamType.BLOCK_POSITION),
    /** Position argument type (may include rotation). */
    POSITION(org.cloudburstmc.protocol.bedrock.data.command.CommandParamType.POSITION),
    /** Message argument type (chat message). */
    MESSAGE(org.cloudburstmc.protocol.bedrock.data.command.CommandParamType.MESSAGE),
    /** Raw text argument type. */
    RAWTEXT(org.cloudburstmc.protocol.bedrock.data.command.CommandParamType.TEXT),
    /** JSON argument type. */
    JSON(org.cloudburstmc.protocol.bedrock.data.command.CommandParamType.JSON),
    /** Text argument type (backwards compatibility, same as RAWTEXT). */
    TEXT(org.cloudburstmc.protocol.bedrock.data.command.CommandParamType.TEXT),
    /** Command argument type (sub-command or command name). */
    COMMAND(org.cloudburstmc.protocol.bedrock.data.command.CommandParamType.COMMAND),
    /** File path argument type. */
    FILE_PATH(org.cloudburstmc.protocol.bedrock.data.command.CommandParamType.FILE_PATH),
    /** Operator argument type (e.g., +, -, *, /). */
    OPERATOR(org.cloudburstmc.protocol.bedrock.data.command.CommandParamType.OPERATOR),
    /** Compare operator argument type (e.g., ==, !=, <, >). */
    COMPARE_OPERATOR(org.cloudburstmc.protocol.bedrock.data.command.CommandParamType.COMPARE_OPERATOR),
    /** Full integer range argument type. */
    FULL_INTEGER_RANGE(org.cloudburstmc.protocol.bedrock.data.command.CommandParamType.INT_RANGE_FULL),
    /** Block states argument type (NBT or block data). */
    BLOCK_STATES(org.cloudburstmc.protocol.bedrock.data.command.CommandParamType.BLOCK_STATES);

    /**
     * The protocol ID associated with this parameter type.
     */
    private final org.cloudburstmc.protocol.bedrock.data.command.CommandParamType cloudburstType;

    /**
     * Constructs a CommandParamType with the given protocol ID.
     *
     * @param id the protocol ID for this type
     */
    CommandParamType(org.cloudburstmc.protocol.bedrock.data.command.CommandParamType cloudburstType) {
        this.cloudburstType = cloudburstType;
    }

    /**
     * Gets the protocol ID for this parameter type.
     *
     * @return the protocol ID
     */
    public int getId() {
        return cloudburstType.ordinal();
    }

    public org.cloudburstmc.protocol.bedrock.data.command.CommandParamType getCloudburstType() {
        return cloudburstType;
    }
}
