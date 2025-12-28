package cn.nukkit.command.data;

import static cn.nukkit.network.protocol.AvailableCommandsPacket.*;

/**
 * Represents the supported parameter types for commands in PowerNukkitX.
 * <p>
 * This enum defines all possible argument types that can be used in command registration and parsing.
 * Each type is mapped to an integer ID from {@link cn.nukkit.network.protocol.AvailableCommandsPacket} for protocol compatibility.
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
 * @see cn.nukkit.network.protocol.AvailableCommandsPacket
 */
public enum CommandParamType {
    /** Integer argument type. */
    INT(ARG_TYPE_INT),
    /** Floating-point argument type. */
    FLOAT(ARG_TYPE_FLOAT),
    /** Value argument type (generic). */
    VALUE(ARG_TYPE_VALUE),
    /** Wildcard integer argument type. */
    WILDCARD_INT(ARG_TYPE_WILDCARD_INT),
    /** Target selector argument type (e.g., @p, @a). */
    TARGET(ARG_TYPE_TARGET),
    /** Wildcard target selector argument type. */
    WILDCARD_TARGET(ARG_TYPE_WILDCARD_TARGET),
    /** Equipment slot argument type. */
    EQUIPMENT_SLOT(ARG_TYPE_EQUIPMENT_SLOT),
    /** String argument type. */
    STRING(ARG_TYPE_STRING),
    /** Block position argument type (x y z). */
    BLOCK_POSITION(ARG_TYPE_BLOCK_POSITION),
    /** Position argument type (may include rotation). */
    POSITION(ARG_TYPE_POSITION),
    /** Message argument type (chat message). */
    MESSAGE(ARG_TYPE_MESSAGE),
    /** Raw text argument type. */
    RAWTEXT(ARG_TYPE_RAWTEXT),
    /** JSON argument type. */
    JSON(ARG_TYPE_JSON),
    /** Text argument type (backwards compatibility, same as RAWTEXT). */
    TEXT(ARG_TYPE_RAWTEXT),
    /** Command argument type (sub-command or command name). */
    COMMAND(ARG_TYPE_COMMAND),
    /** File path argument type. */
    FILE_PATH(ARG_TYPE_FILE_PATH),
    /** Operator argument type (e.g., +, -, *, /). */
    OPERATOR(ARG_TYPE_OPERATOR),
    /** Compare operator argument type (e.g., ==, !=, <, >). */
    COMPARE_OPERATOR(ARG_TYPE_COMPARE_OPERATOR),
    /** Full integer range argument type. */
    FULL_INTEGER_RANGE(ARG_TYPE_FULL_INTEGER_RANGE),
    /** Block states argument type (NBT or block data). */
    BLOCK_STATES(ARG_TYPE_BLOCK_STATES);

    /**
     * The protocol ID associated with this parameter type.
     */
    private final int id;

    /**
     * Constructs a CommandParamType with the given protocol ID.
     *
     * @param id the protocol ID for this type
     */
    CommandParamType(int id) {
        this.id = id;
    }

    /**
     * Gets the protocol ID for this parameter type.
     *
     * @return the protocol ID
     */
    public int getId() {
        return id;
    }
}
