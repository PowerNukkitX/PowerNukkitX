package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.utils.CommandLogger;

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
        this.paramTree = new ParamTree(this);
    }

    @Since("1.19.50-r4")
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
        if (online.length() > 0) {
            online = new StringBuilder(online.substring(0, online.length() - 2));
        }
        log.addSuccess("commands.players.list", String.valueOf(onlineCount), String.valueOf(sender.getServer().getMaxPlayers()))
                .addSuccess(online.toString())
                .output();
        return 1;
    }
}
