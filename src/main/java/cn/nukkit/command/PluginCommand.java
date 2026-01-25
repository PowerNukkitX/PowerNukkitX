package cn.nukkit.command;

import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.plugin.Plugin;

import java.util.Map;

/**
 * Represents a command registered by a plugin, allowing custom command logic and permission handling.
 * <p>
 * This class is used to define commands that are owned by a plugin and can be executed by players, the console,
 * or other command senders. It supports custom executors, permission checks, usage messages, and integration
 * with the plugin lifecycle.
 * <p>
 * Features:
 * <ul>
 *   <li>Binds the command to a specific plugin instance.</li>
 *   <li>Allows custom command logic via {@link CommandExecutor}.</li>
 *   <li>Supports usage messages and permission checks.</li>
 *   <li>Integrates with the plugin's enabled/disabled state.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Instantiate with a name and plugin owner, optionally with a description.</li>
 *   <li>Set a custom executor using {@link #setExecutor(CommandExecutor)}.</li>
 *   <li>Override {@link #execute(CommandSender, String, String[])} for custom logic, or delegate to the executor.</li>
 *   <li>Use {@link #getPlugin()} to retrieve the owning plugin.</li>
 * </ul>
 * <p>
 * The command will only execute if the owning plugin is enabled and the sender has permission.
 * If execution fails and a usage message is set, the sender will receive usage instructions.
 *
 * @param <T> the type of the owning plugin
 * @author MagicDroidX (Nukkit Project)
 * @see Command
 * @see CommandExecutor
 * @see Plugin
 */
public class PluginCommand<T extends Plugin> extends Command implements PluginIdentifiableCommand {
    /**
     * The plugin that owns this command.
     */
    private final T owningPlugin;

    /**
     * The executor responsible for handling command logic.
     */
    private CommandExecutor executor;

    /**
     * Constructs a PluginCommand with a name and owning plugin.
     * Sets the executor to the plugin and usage message to empty.
     *
     * @param name the command name
     * @param owner the owning plugin
     */
    public PluginCommand(String name, T owner) {
        super(name);
        this.owningPlugin = owner;
        this.executor = owner;
        this.usageMessage = "";
    }

    /**
     * Constructs a PluginCommand with a name, description, and owning plugin.
     * Sets the executor to the plugin.
     *
     * @param name the command name
     * @param description the command description
     * @param owner the owning plugin
     */
    public PluginCommand(String name, String description, T owner) {
        super(name, description);
        this.owningPlugin = owner;
        this.executor = owner;
    }

    /**
     * Executes the command with parsed parameters and logging.
     * Returns 0 if the plugin is disabled, 1 otherwise.
     *
     * @param sender the command sender
     * @param commandLabel the command label
     * @param result the parsed command result
     * @param log the command logger
     * @return 0 if the plugin is disabled, 1 otherwise
     */
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        if (!this.owningPlugin.isEnabled()) {
            return 0;
        }
        return 1;
    }

    /**
     * Executes the command with the given sender, label, and arguments.
     * Checks if the plugin is enabled and the sender has permission, then delegates to the executor.
     * Sends usage instructions if execution fails and a usage message is set.
     *
     * @param sender the command sender
     * @param commandLabel the command label
     * @param args the command arguments
     * @return true if the command executed successfully, false otherwise
     */
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.owningPlugin.isEnabled()) {
            return false;
        }

        if (!this.testPermission(sender)) {
            return false;
        }

        boolean success = this.executor.onCommand(sender, this, commandLabel, args);

        if (!success && !this.usageMessage.isEmpty()) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
        }

        return success;
    }

    /**
     * Gets the executor responsible for handling command logic.
     *
     * @return the command executor
     */
    public CommandExecutor getExecutor() {
        return executor;
    }

    /**
     * Sets the executor responsible for handling command logic.
     * If null, defaults to the owning plugin.
     *
     * @param executor the command executor to set
     */
    public void setExecutor(CommandExecutor executor) {
        this.executor = (executor != null) ? executor : this.owningPlugin;
    }

    /**
     * Gets the plugin that owns this command.
     *
     * @return the owning plugin
     */
    @Override
    public T getPlugin() {
        return this.owningPlugin;
    }
}
