package cn.nukkit.command.data;

/**
 * Represents a command overload definition for PowerNukkitX commands.
 * <p>
 * This class is used to define a specific set of input parameters and chaining behavior for a command.
 * Each overload can specify its own argument signature and whether it supports chaining with other commands.
 * <p>
 * Features:
 * <ul>
 *   <li>Stores a {@link CommandInput} instance describing the argument signature for the overload.</li>
 *   <li>Indicates whether this overload supports chaining with other commands via the {@link #chaining} flag.</li>
 *   <li>Used in {@link CommandData#overloads} to support multiple argument sets for a single command.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Instantiate and configure the {@link #input} field to define argument types and properties.</li>
 *   <li>Set {@link #chaining} to true if the overload should support command chaining.</li>
 *   <li>Use in command registration and metadata to provide flexible command signatures.</li>
 * </ul>
 * <p>
 * Example:
 * <pre>
 * CommandOverload overload = new CommandOverload();
 * overload.input.parameters = new CommandParameter[] {
 *     CommandParameter.newType("target", false, CommandParamType.PLAYER)
 * };
 * overload.chaining = true;
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see CommandInput
 * @see CommandData
 */
public class CommandOverload {

    /**
     * The input parameters for this command overload.
     * Defines the argument signature for the overload.
     */
    public CommandInput input = new CommandInput();

    /**
     * Indicates whether this overload supports chaining with other commands.
     */
    public boolean chaining;
}
