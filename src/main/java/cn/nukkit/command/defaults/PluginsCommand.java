package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.lang.TranslationContainer;

import java.util.Map;

/**
 * @author Ayrz
 * @since update 2025/12/14
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
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        this.sendPluginList(sender);
        return 1;
    }

    private void sendPluginList(CommandSender sender) {
        StringBuilder list = new StringBuilder();
        Map<String, Plugin> plugins = sender.getServer().getPluginManager().getPlugins();
        
        for (Plugin plugin : plugins.values()) {
            if (list.length() > 0) {
                list.append(TextFormat.WHITE + ", ");
            }
            list.append(plugin.isEnabled() ? TextFormat.GREEN : TextFormat.RED);
            list.append(plugin.getDescription().getFullName());
        }

        sender.sendMessage(new TranslationContainer("nukkit.command.plugins.success", new String[]{
            String.valueOf(plugins.size()), 
            list.toString()
        }));
    }
}
