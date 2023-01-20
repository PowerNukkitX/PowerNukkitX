package cn.nukkit.command.defaults;

import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.utils.TextFormat;

import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ReloadCommand extends VanillaCommand {

    public ReloadCommand(String name) {
        super(name, "Reload the server/plugin");
        this.setPermission("nukkit.command.reload");
        this.commandParameters.clear();
        this.commandParameters.put("default", CommandParameter.EMPTY_ARRAY);
        this.commandParameters.put("function", new CommandParameter[]{
                CommandParameter.newEnum("function", false, new String[]{"function"})
        });
        this.commandParameters.put("plugin", new CommandParameter[]{
                CommandParameter.newEnum("plugin", new String[]{"plugin"}),
                CommandParameter.newType("plugin", CommandParamType.STRING)
        });
        this.paramTree = new ParamTree(this);
    }

    @Since("1.19.50-r4")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        switch (result.getKey()) {
            case "default" -> {
                log.addMessage(TextFormat.YELLOW + "%nukkit.command.reload.reloading" + TextFormat.WHITE).output(true, true);
                sender.getServer().reload();
                log.addMessage(TextFormat.YELLOW + "%nukkit.command.reload.reloaded" + TextFormat.WHITE).output(true, true);
                return 1;
            }
            case "function" -> {
                log.addMessage(TextFormat.YELLOW + "%nukkit.command.reload.reloading" + TextFormat.WHITE).output(true, true);
                sender.getServer().getFunctionManager().reload();
                log.addMessage(TextFormat.YELLOW + "%nukkit.command.reload.reloaded" + TextFormat.WHITE).output(true, true);
                return 1;
            }
            case "plugin" -> {
                log.addMessage(TextFormat.YELLOW + "%nukkit.command.reload.reloading" + TextFormat.WHITE).output(true, true);
                var pluginManager = sender.getServer().getPluginManager();
                String str = list.getResult(1);
                var plugin = pluginManager.getPlugin(str);
                //todo: 多语言
                log.addSuccess("Reloading plugin §a" + plugin.getDescription().getName()).output(true, true);
                pluginManager.disablePlugin(plugin);
                pluginManager.getPlugins().remove(plugin.getDescription().getName());
                plugin = pluginManager.loadPlugin(plugin.getFile());
                pluginManager.enablePlugin(plugin);
                log.addSuccess("Plugin §a" + plugin.getDescription().getName() + " §freloaded!").output(true, true);
                log.addSuccess(TextFormat.YELLOW + "%nukkit.command.reload.reloaded" + TextFormat.WHITE).output(true, true);
                return 1;
            }
            default -> {
                return 0;
            }
        }
    }
}
