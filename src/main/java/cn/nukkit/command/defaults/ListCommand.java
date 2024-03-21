package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.lang.TranslationContainer;

import java.util.Map;

/**
 * @author xtypr
 * @since 2015/11/11
 */
public class ListCommand extends VanillaCommand {

    public ListCommand(String name) {
        super(name, "commands.list.description");
        this.setPermission("nukkit.command.list");
        this.commandParameters.clear();
        this.commandParameters.put("default", CommandParameter.EMPTY_ARRAY);
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        StringBuilder online = new StringBuilder();
        int onlineCount = 0;
        for (Player player : sender.getServer().getOnlinePlayers().values()) {
            if (player.isOnline() && (!(sender instanceof Player) || ((Player) sender).canSee(player))) {
                online.append(player.getDisplayName()).append(", ");
                ++onlineCount;
            }
        }
        if (!online.isEmpty()) {
            online = new StringBuilder(online.substring(0, online.length() - 2));
        }
        sender.sendMessage(new TranslationContainer("commands.players.list",
                String.valueOf(onlineCount), String.valueOf(sender.getServer().getMaxPlayers())));
        sender.sendMessage(online.toString());
        return 1;
    }
}
