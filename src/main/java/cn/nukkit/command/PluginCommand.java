package cn.nukkit.command;

import cn.nukkit.api.Since;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.plugin.Plugin;

import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class PluginCommand<T extends Plugin> extends Command implements PluginIdentifiableCommand {

    private final T owningPlugin;

    private CommandExecutor executor;

    public PluginCommand(String name, T owner) {
        super(name);
        this.owningPlugin = owner;
        this.executor = owner;
        this.usageMessage = "";
    }

    @Since("1.19.50-r4")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        if (!this.owningPlugin.isEnabled()) {
            return 0;
        }
        return 1;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.owningPlugin.isEnabled()) {
            return false;
        }

        if (!this.testPermission(sender)) {
            return false;
        }

        boolean success = this.executor.onCommand(sender, this, commandLabel, args);

        if (!success && !this.usageMessage.equals("")) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
        }

        return success;
    }

    public CommandExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(CommandExecutor executor) {
        this.executor = (executor != null) ? executor : this.owningPlugin;
    }

    @Override
    public T getPlugin() {
        return this.owningPlugin;
    }
}
