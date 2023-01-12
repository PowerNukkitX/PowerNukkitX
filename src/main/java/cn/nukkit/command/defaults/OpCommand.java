package cn.nukkit.command.defaults;

import cn.nukkit.IPlayer;
import cn.nukkit.Player;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.tree.node.PlayersNode;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.utils.TextFormat;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xtypr
 * @since 2015/11/12
 */
public class OpCommand extends VanillaCommand {

    public OpCommand(String name) {
        super(name, "commands.op.description");
        this.setPermission("nukkit.command.op.give");
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
        List<IPlayer> IPlayers = players.stream().map(p -> (IPlayer) p).collect(Collectors.toList());
        if (IPlayers.size() == 0) {
            IPlayers.add(sender.getServer().getOfflinePlayer(result.getValue().getParent().getArgs()[0]));
        }

        for (IPlayer player : players) {
            if (player.isOp()) {
                log.addError("commands.op.failed", player.getName()).output();
            } else {
                player.setOp(true);
                log.addSuccess("commands.op.success", player.getName()).output(true, true);
                if (player instanceof Player player1) {
                    log.outputObjectWhisper(player1, TextFormat.GRAY + "%commands.op.message");
                }
            }
        }
        return players.size();
    }
}
