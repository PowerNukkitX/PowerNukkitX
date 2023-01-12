package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.tree.node.PlayersNode;
import cn.nukkit.command.utils.CommandLogger;

import java.util.List;
import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class PardonCommand extends VanillaCommand {

    public PardonCommand(String name) {
        super(name, "unban a player");
        this.setPermission("nukkit.command.unban.player");
        this.setAliases(new String[]{"unban"});
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET, new PlayersNode())
        });
        this.paramTree = new ParamTree(this);
    }

    @Since("1.19.50-r4")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        List<Player> players = result.getValue().getResult(0);
        for (var player : players) {
            sender.getServer().getNameBans().remove(player.getName());
            log.addSuccess("commands.unban.success", player.getName());
        }
        log.successCount(players.size()).output(true, true);
        return players.size();
    }
}
