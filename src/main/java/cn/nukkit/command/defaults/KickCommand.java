package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.node.PlayersNode;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.event.player.PlayerKickEvent;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author xtypr
 * @since 2015/11/11
 */
public class KickCommand extends VanillaCommand {

    public KickCommand(String name) {
        super(name, "commands.kick.description");
        this.setPermission("nukkit.command.kick");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET, new PlayersNode()),
                CommandParameter.newType("reason", true, CommandParamType.MESSAGE)
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        List<Player> players = list.getResult(0);
        players = players.stream().filter(Objects::nonNull).toList();
        if (players.isEmpty()) {
            log.addNoTargetMatch().output();
            return 0;
        }
        String reason = "";
        if (list.hasResult(1)) {
            reason = list.getResult(1);
        }

        for (Player player : players) {
            player.kick(PlayerKickEvent.Reason.KICKED_BY_ADMIN, reason);
            if (reason.length() >= 1) {
                log.addSuccess("commands.kick.success.reason", player.getName(), reason.toString());
            } else {
                log.addSuccess("commands.kick.success", player.getName());
            }
        }
        log.successCount(players.size()).output(true);
        return players.size();
    }
}
