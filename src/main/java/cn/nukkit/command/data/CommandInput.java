package cn.nukkit.command.data;

/**
 * Represents the input parameters for a command in PowerNukkitX.
 * <p>
 * This class is used to define the set of parameters that a command accepts. It is typically used in command
 * registration and metadata to specify the expected arguments for a command. The {@link #parameters} field holds
 * an array of {@link CommandParameter} objects, each describing a single argument's type, name, and properties.
 * <p>
 * Features:
 * <ul>
 *   <li>Stores an array of {@link CommandParameter} objects representing command arguments.</li>
 *   <li>Used in command registration, overloads, and metadata for argument validation and auto-completion.</li>
 *   <li>Supports empty parameter arrays for commands with no arguments.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Instantiate and set the {@link #parameters} field to define command arguments.</li>
 *   <li>Use in {@link CommandOverload} and {@link CommandData} to specify command signatures.</li>
 * </ul>
 * <p>
 * Example:
 * <pre>
 * CommandInput input = new CommandInput();
 * input.parameters = new CommandParameter[] {
 *     CommandParameter.newType("target", false, CommandParamType.PLAYER),
 *     CommandParameter.newType("amount", true, CommandParamType.INT)
 * };
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see CommandParameter
 * @see CommandOverload
 * @see CommandData
 */
public class CommandInput {

    /**
     * The array of parameters accepted by the command.
     * Each {@link CommandParameter} describes a single argument's type, name, and properties.
     * Defaults to {@link CommandParameter#EMPTY_ARRAY} for commands with no arguments.
     */
    public CommandParameter[] parameters = CommandParameter.EMPTY_ARRAY;

}