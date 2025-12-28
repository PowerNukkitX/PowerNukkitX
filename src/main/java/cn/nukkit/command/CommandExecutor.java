package cn.nukkit.command;

/**
 * Represents a listener for command execution events.
 * <p>
 * Classes implementing this interface can handle commands executed by players, the console, or command blocks.
 * <p>
 * Usage:
 * <ul>
 *   <li>Implement this interface in your plugin or handler class.</li>
 *   <li>Register your CommandExecutor with a command to handle its execution.</li>
 *   <li>Use {@link #onCommand(CommandSender, Command, String, String[])} to define command logic.</li>
 * </ul>
 * <p>
 * The {@code label} parameter is always converted to lower case, while {@code args} preserves the original case.
 * <p>
 * If {@code onCommand} returns {@code false}, the server will send usage information to the sender.
 * <p>
 * To check permissions, use {@link cn.nukkit.command.Command#testPermissionSilent}.
 *
 * @author MagicDroidX (code) @ Nukkit Project
 * @author 粉鞋大妈 (javadoc) @ Nukkit Project
 * @see cn.nukkit.plugin.PluginBase
 * @see cn.nukkit.command.CommandExecutor#onCommand
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
public interface CommandExecutor {
    /**
     * Called when a command is executed.
     * <p>
     * The label is always lower case, while arguments keep their original case.
     * <p>
     * Return {@code true} if the command executed successfully, or {@code false} to send usage information to the sender.
     * <p>
     * To check permissions, use {@link cn.nukkit.command.Command#testPermissionSilent}.
     *
     * @param sender The sender of this command (player, console, etc.)
     * @param command The command being executed
     * @param label The command label (lower case)
     * @param args The command arguments (original case)
     * @return true if the command executed successfully, false otherwise
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    boolean onCommand(CommandSender sender, Command command, String label, String[] args);
}
