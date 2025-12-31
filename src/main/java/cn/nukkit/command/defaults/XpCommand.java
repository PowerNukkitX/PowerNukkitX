package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.node.PlayersNode;
import cn.nukkit.command.tree.node.XpLevelNode;
import cn.nukkit.command.utils.CommandLogger;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Snake1999
 * @since 2016/1/22
 */
public class XpCommand extends VanillaCommand {
    public XpCommand(String name) {
        super(name, "commands.xp.description");
        this.setPermission("nukkit.command.xp");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("amount", CommandParamType.INT),
                CommandParameter.newType("player", true, CommandParamType.TARGET, new PlayersNode())
        });
        this.commandParameters.put("level", new CommandParameter[]{
                CommandParameter.newType("level", CommandParamType.STRING, new XpLevelNode()),
                CommandParameter.newType("player", true, CommandParamType.TARGET, new PlayersNode())
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        //  "/xp <amount> [player]"  for adding exp
        //  "/xp <amount>L [player]" for adding exp level
        var list = result.getValue();
        List<Player> players = sender.isPlayer() ? List.of(sender.asPlayer()) : List.of();
        switch (result.getKey()) {
            case "default" -> {
                int amount = list.getResult(0);
                if (amount < 0) {
                    log.addError("commands.xp.failure.widthdrawXp").output();
                    return 0;
                }
                if (list.hasResult(1)) {
                    players = list.getResult(1);
                }
                players = players.stream().filter(Objects::nonNull).toList();
                if (players.isEmpty()) {
                    log.addNoTargetMatch().output();
                    return 0;
                }
                for (Player player : players) {
                    player.addExperience(amount);
                    log.addSuccess("commands.xp.success", String.valueOf(amount), player.getName());
                }
                log.successCount(players.size()).output();
                return players.size();
            }
            case "level" -> {
                int level = list.getResult(0);
                if (list.hasResult(1)) {
                    players = list.getResult(1);
                }
                players = players.stream().filter(Objects::nonNull).toList();
                if (players.isEmpty()) {
                    log.addNoTargetMatch().output();
                    return 0;
                }
                for (Player player : players) {
                    int newLevel = player.getExperienceLevel();
                    newLevel += level;
                    if (newLevel > 24791) newLevel = 24791;
                    if (newLevel < 0) {
                        player.setExperience(0, 0);
                    } else {
                        player.setExperience(player.getExperience(), newLevel, true);
                    }
                    log.addSuccess("commands.xp.success.levels", String.valueOf(level), player.getName());
                }
                log.successCount(players.size()).output();
                return players.size();
            }
            default -> {
                return 0;
            }
        }
    }
}
