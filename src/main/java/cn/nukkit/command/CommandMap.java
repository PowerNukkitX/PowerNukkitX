package cn.nukkit.command;

import java.util.List;

/**
 * Represents the command registry and execution system for the server.
 * <p>
 * The CommandMap interface defines methods for registering, executing, retrieving, and unregistering commands.
 * It supports registering multiple commands at once, handling label conflicts, annotation-based command registration,
 * and clearing plugin commands. Implementations manage the mapping between command names/aliases and Command objects.
 * <p>
 * Usage:
 * <ul>
 *   <li>Register commands using {@link #registerAll(String, List)}, {@link #register(String, Command)}, or {@link #register(String, Command, String)}.</li>
 *   <li>Register annotation-based commands with {@link #registerSimpleCommands(Object)}.</li>
 *   <li>Execute commands using {@link #executeCommand(CommandSender, String)}.</li>
 *   <li>Retrieve commands with {@link #getCommand(String)}.</li>
 *   <li>Unregister commands using {@link #unregister(String...)}.</li>
 *   <li>Clear all plugin commands with {@link #clearCommands()}.</li>
 * </ul>
 * <p>
 * Label conflicts: If a command label is already registered, registration returns false and the command can only be accessed
 * using the fallback prefix (e.g., fallbackPrefix:label).
 * <p>
 * Thread safety and implementation details depend on the concrete implementation.
 *
 * @author MagicDroidX (Nukkit Project)
 */
public interface CommandMap {
    /**
     * Registers all commands in the provided list with the given fallback prefix.
     * <p>
     * The fallback prefix is used to distinguish commands when labels conflict.
     *
     * @param fallbackPrefix the prefix to use for label conflicts
     * @param commands the list of commands to register
     */
    void registerAll(String fallbackPrefix, List<? extends Command> commands);

    /**
     * Registers a command with the given fallback prefix.
     * <p>
     * Returns false if the label is already registered; in that case, the command can only be accessed
     * using fallbackPrefix:label.
     *
     * @param fallbackPrefix the prefix to use for label conflicts
     * @param command the command to register
     * @return true if registration succeeded, false if the label is already registered
     */
    boolean register(String fallbackPrefix, Command command);

    /**
     * Registers a command with the given fallback prefix and label.
     * <p>
     * Returns false if the label is already registered; in that case, the command can only be accessed
     * using fallbackPrefix:label.
     *
     * @param fallbackPrefix the prefix to use for label conflicts
     * @param command the command to register
     * @param label the label to register the command under
     * @return true if registration succeeded, false if the label is already registered
     */
    boolean register(String fallbackPrefix, Command command, String label);

    /**
     * Registers annotation-based commands from the given object.
     * <p>
     * Scans the object for command annotations and registers the corresponding commands.
     *
     * @param object the object containing annotated command methods
     */
    void registerSimpleCommands(Object object);

    /**
     * Executes a command from the given sender and command line.
     * <p>
     * Returns 0 for failed execution, 1 or greater for successful execution.
     *
     * @param sender the sender executing the command
     * @param cmdLine the full command line to execute
     * @return 0 if execution failed, >=1 if successful
     */
    int executeCommand(CommandSender sender, String cmdLine);

    /**
     * Clears all plugin commands from the registry.
     * <p>
     * Typically used when unloading plugins or resetting the command map.
     */
    void clearCommands();

    /**
     * Retrieves a command by its name or alias.
     * <p>
     * Returns the Command object if found, or null otherwise.
     *
     * @param name the name or alias of the command
     * @return the Command object, or null if not found
     */
    Command getCommand(String name);

    /**
     * Unregisters the specified commands by name.
     * <p>
     * Removes the commands from the registry so they can no longer be executed.
     *
     * @param commands the names of commands to unregister
     */
    void unregister(String... commands);
}
