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
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.TextFormat;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xtypr
 * @since 2015/12/13
 */
public class SpawnpointCommand extends VanillaCommand {
    public SpawnpointCommand(String name) {
        super(name, "commands.spawnpoint.description");
        this.setPermission("nukkit.command.spawnpoint");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", true, CommandParamType.TARGET, new PlayersNode()),
                CommandParameter.newType("spawnPos", true, CommandParamType.POSITION),
        });
        this.paramTree = new ParamTree(this);
    }

    @Since("1.19.50-r4")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        List<Player> players = sender.isPlayer() ? Collections.singletonList(sender.asPlayer()) : List.of();
        DecimalFormat round2 = new DecimalFormat("##0.00");
        if (list.hasResult(0)) {
            players = list.getResult(0);
            Level level = sender.getPosition().getLevel();
            if (list.hasResult(1)) {
                if (level != null) {
                    Position position = list.getResult(1);
                    if (level.isOverWorld()) {
                        if (position.y < -64) position.y = -64;
                        if (position.y > 320) position.y = 320;
                    } else {
                        if (position.y < 0) position.y = 0;
                        if (position.y > 255) position.y = 255;
                    }
                    for (Player player : players) {
                        player.setSpawn(position);
                    }
                    log.addSuccess("commands.spawnpoint.success.multiple.specific", players.stream().map(Player::getName).collect(Collectors.joining(" ")),
                            round2.format(position.x),
                            round2.format(position.y),
                            round2.format(position.z)).successCount(players.size()).output(true, true);
                    return players.size();
                }
            }
            log.addSyntaxErrors(1).output();
            return 0;
        }
        if (!players.isEmpty()) {
            Position pos = players.get(0).getPosition();
            players.get(0).setSpawn(pos);
            log.addSuccess("commands.spawnpoint.success.single", sender.getName(),
                    round2.format(pos.x),
                    round2.format(pos.y),
                    round2.format(pos.z)).output(true, true);
            return 1;
        } else {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.noTargetMatch"));
            return 0;
        }
    }
}
