package cn.nukkit.command;

import cn.nukkit.plugin.Plugin;

/**
 * Interface for commands that are associated with a specific plugin.
 * <p>
 * Implement this interface in your command class to indicate that the command is owned by a plugin.
 * This allows the command system to retrieve the plugin instance responsible for the command, enabling
 * features such as permission management, plugin lifecycle integration, and command organization.
 * <p>
 * Usage:
 * <ul>
 *   <li>Implement this interface in your custom command class.</li>
 *   <li>Return the owning plugin instance from {@link #getPlugin()}.</li>
 *   <li>Used by {@link PluginCommand} and other plugin-based command implementations.</li>
 * </ul>
 * <p>
 * Example:
 * <pre>
 * public class MyPluginCommand extends Command implements PluginIdentifiableCommand {
 *     private final Plugin plugin;
 *     public MyPluginCommand(String name, Plugin plugin) {
 *         super(name);
 *         this.plugin = plugin;
 *     }
 *     @Override
 *     public Plugin getPlugin() {
 *         return plugin;
 *     }
 * }
 * </pre>
 *
 * @author MagicDroidX (Nukkit Project)
 * @see PluginCommand
 * @see cn.nukkit.plugin.Plugin
 */
public interface PluginIdentifiableCommand {
    /**
     * Returns the plugin that owns this command.
     *
     * @return the owning plugin instance
     */
    Plugin getPlugin();
}
