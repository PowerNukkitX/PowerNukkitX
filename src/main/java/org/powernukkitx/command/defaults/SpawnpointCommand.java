package org.powernukkitx.command.defaults;

import org.powernukkitx.Player;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.command.tree.ParamList;
import org.powernukkitx.command.tree.node.PlayersNode;
import org.powernukkitx.command.utils.CommandLogger;
import org.powernukkitx.lang.TranslationContainer;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.utils.TextFormat;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;

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
                CommandParameter.newType("player", true, CommandParamType.SELECTION, new PlayersNode()),
                CommandParameter.newType("spawnPos", true, CommandParamType.POSITION),
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        List<Player> players = sender.isPlayer() ? Collections.singletonList(sender.asPlayer()) : List.of();
        DecimalFormat round2 = new DecimalFormat("##0.00");
        if (list.hasResult(0)) {
            players = list.getResult(0);
            if (players.isEmpty()) {
                log.addNoTargetMatch().output();
                return 0;
            }
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
                        player.setSpawn(position, Player.SpawnPointType.PLAYER);
                    }
                    log.addSuccess("commands.spawnpoint.success.multiple.specific", players.stream().map(p -> p.getViewableName(sender)).collect(Collectors.joining(" ")),
                            round2.format(position.x),
                            round2.format(position.y),
                            round2.format(position.z)).successCount(players.size()).output(true);
                    return players.size();
                }
            }
            log.addSyntaxErrors(1).output();
            return 0;
        }
        if (!players.isEmpty()) {
            Player player = players.getFirst();
            Position pos = player.getPosition();
            player.setSpawn(pos, Player.SpawnPointType.PLAYER);
            log.addSuccess("commands.spawnpoint.success.single", player.getViewableName(sender),
                    round2.format(pos.x),
                    round2.format(pos.y),
                    round2.format(pos.z)).output(true);
            return 1;
        } else {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.noTargetMatch"));
            return 0;
        }
    }
}
