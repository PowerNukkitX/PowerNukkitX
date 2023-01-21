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
public class DeopCommand extends VanillaCommand {
    public DeopCommand(String name) {
        super(name, "commands.deop.description");
        this.setPermission("nukkit.command.op.take");
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
            if (!player.isOp()) {
                log.addError("Privileges cannot be revoked (revoked or with higher privileges)").output();//no translation in client
                return 0;
            }
            player.setOp(false);
            if (player.isOnline()) {
                log.outputObjectWhisper(player.getPlayer(), TextFormat.GRAY + "%commands.deop.message");
            }
            log.addSuccess("commands.deop.success", player.getName());
        }
        return players.size();
    }
}
