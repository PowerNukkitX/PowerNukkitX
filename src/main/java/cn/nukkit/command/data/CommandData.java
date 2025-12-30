package cn.nukkit.command.data;

import cn.nukkit.network.protocol.types.PermissionLevel;

import java.util.*;

/**
 * Represents the metadata and configuration for a command in PowerNukkitX.
 * <p>
 * This class stores all relevant information about a command, including its description, aliases, overloads,
 * flags, permission level, and subcommands. It is used to define the structure, behavior, and access control
 * for commands registered in the server.
 * <p>
 * Features:
 * <ul>
 *   <li>Stores command description and aliases for localization and auto-completion.</li>
 *   <li>Manages command overloads for supporting multiple argument signatures.</li>
 *   <li>Uses {@link Flag} enum to control command visibility, execution, and behavior.</li>
 *   <li>Defines required permission level for command execution.</li>
 *   <li>Supports chained subcommands for complex command structures.</li>
 *   <li>Implements {@link #clone()} for safe duplication of command metadata.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Instantiate and configure fields to define a command's metadata.</li>
 *   <li>Use {@link #flags} to set command properties such as visibility and cheat status.</li>
 *   <li>Use {@link #overloads} to define multiple argument sets for the command.</li>
 *   <li>Use {@link #subcommands} for advanced command chaining.</li>
 *   <li>Call {@link #clone()} to duplicate the command data safely.</li>
 * </ul>
 * <p>
 * Example:
 * <pre>
 * CommandData data = new CommandData();
 * data.description = "Teleport command";
 * data.permission = PermissionLevel.OPERATOR;
 * data.flags.add(CommandData.Flag.HIDDEN);
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see CommandEnum
 * @see CommandOverload
 * @see Flag
 * @see PermissionLevel
 * @see ChainedSubCommandData
 */
public class CommandData implements Cloneable {
    /**
     * The description of the command, used for help and localization.
     */
    public String description = "description";
    /**
     * The aliases for the command, used for auto-completion and alternative names.
     */
    public CommandEnum aliases = null;
    /**
     * The map of overloads, each representing a different argument signature for the command.
     */
    public Map<String, CommandOverload> overloads = new HashMap<>();
    /**
     * The set of flags controlling command visibility, execution, and behavior.
     */
    public EnumSet<Flag> flags = EnumSet.of(Flag.NOT_CHEAT);
    /**
     * The required permission level to execute the command.
     */
    public PermissionLevel permission = PermissionLevel.ANY;
    /**
     * The list of chained subcommands for advanced command structures.
     */
    public List<ChainedSubCommandData> subcommands = new ArrayList<>();

    /**
     * Creates a clone of this CommandData instance.
     * <p>
     * Returns a new instance with the same field values. If cloning fails, returns a new default CommandData.
     *
     * @return a cloned CommandData instance
     */
    @Override
    public CommandData clone() {
        try {
            return (CommandData) super.clone();
        } catch (Exception e) {
            return new CommandData();
        }
    }

    /**
     * Flags for controlling command behavior, visibility, and execution.
     * <p>
     * Each flag represents a specific property or restriction for the command.
     * Flags can be combined using bitwise operations and stored in the {@link #flags} set.
     *
     * @author PowerNukkitX Project Team
     */
    public enum Flag {
        /** No special behavior. */
        NONE(0x00),
        /** Used for test usage. */
        TEST_USAGE(0x01),
        /** Hidden from command blocks. */
        HIDDEN_FROM_COMMAND_BLOCK(0x02),
        /** Hidden from players. */
        HIDDEN_FROM_PLAYER(0x04),
        /** Hidden from all sources. */
        HIDDEN(0x06),
        /** Hidden from automation systems. */
        HIDDEN_FROM_AUTOMATION(0x08),
        /** Command is removed. */
        REMOVED(0xe),
        /** Local synchronization required. */
        LOCAL_SYNC(0x10),
        /** Execution is disallowed. */
        EXECUTE_DISALLOWED(0x20),
        /** Message type flag. */
        MESSAGE_TYPE(0x40),
        /** Not a cheat command. */
        NOT_CHEAT(0x80),
        /** Command executes asynchronously. */
        ASYNC(0x100),
        /** Editor-specific command. */
        EDITOR(0x200);

        /**
         * The bit value representing the flag.
         */
        public final int bit;

        Flag(int bit) {
            this.bit = bit;
        }
    }
}
