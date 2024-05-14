package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.node.PlayersNode;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.network.protocol.types.SpawnPointType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ClearSpawnPointCommand extends VanillaCommand {

    public ClearSpawnPointCommand(String name) {
        super(name, "commands.clearspawnpoint.description");
        this.setPermission("nukkit.command.clearspawnpoint");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newType("player", true, CommandParamType.TARGET, new PlayersNode()),
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        List<Player> players = sender.isPlayer() ? List.of(sender.asPlayer()) : null;
        if (list.hasResult(0)) players = list.getResult(0);
        if (players == null || players.isEmpty()) {
            log.addNoTargetMatch().output();
            return 0;
        }
        for (Player player : players) {
            player.setSpawn(Server.getInstance().getDefaultLevel().getSpawnLocation(), SpawnPointType.WORLD);
        }
        String players_str = players.stream().map(Player::getName).collect(Collectors.joining(" "));
        if (players.size() > 1) {
            log.addSuccess("commands.clearspawnpoint.success.multiple", players_str);
        } else {
            log.addSuccess("commands.clearspawnpoint.success.single", players_str);
        }
        log.successCount(players.size()).output();
        return players.size();
    }
}
