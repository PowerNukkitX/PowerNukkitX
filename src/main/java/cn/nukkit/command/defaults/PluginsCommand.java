package cn.nukkit.command.defaults;

import cn.nukkit.api.Since;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.TextFormat;

import java.util.Map;

/**
 * @author xtypr
 * @since 2015/11/12
 */
public class PluginsCommand extends Command implements CoreCommand {

    public PluginsCommand(String name) {
        super(name,
                "%nukkit.command.plugins.description",
                "%nukkit.command.plugins.usage",
                new String[]{"pl"}
        );
        this.setPermission("nukkit.command.plugins");
        this.commandParameters.clear();
        this.commandParameters.put("default", CommandParameter.EMPTY_ARRAY);
        this.paramTree = new ParamTree(this);
    }

    @Since("1.19.50-r4")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        this.sendPluginList(sender, log);
        return 1;
    }

    private void sendPluginList(CommandSender sender, CommandLogger log) {
        StringBuilder list = new StringBuilder();
        Map<String, Plugin> plugins = sender.getServer().getPluginManager().getPlugins();
        for (Plugin plugin : plugins.values()) {
            if (list.length() > 0) {
                list.append(TextFormat.WHITE + ", ");
            }
            list.append(plugin.isEnabled() ? TextFormat.GREEN : TextFormat.RED);
            list.append(plugin.getDescription().getFullName());
        }
        log.addMessage("nukkit.command.plugins.success", String.valueOf(plugins.size()), list.toString()).output();
    }
}
