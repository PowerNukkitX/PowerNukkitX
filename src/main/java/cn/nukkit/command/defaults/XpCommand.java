package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.api.Since;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.tree.node.PlayersNode;
import cn.nukkit.command.tree.node.XpLevelNode;
import cn.nukkit.command.utils.CommandLogger;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Snake1999
 * @since 2016/1/22
 */
public class XpCommand extends Command {
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
        this.paramTree = new ParamTree(this);
    }

    @Since("1.19.50-r4")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        //  "/xp <amount> [player]"  for adding exp
        //  "/xp <amount>L [player]" for adding exp level
        var list = result.getValue();
        List<Player> players = sender.isPlayer() ? Collections.singletonList(sender.asPlayer()) : null;
        switch (result.getKey()) {
            case "default" -> {
                int amount = list.getResult(0);
                if (amount < 0) {
                    log.addNumTooSmall(0, 0).output();
                    return 0;
                }
                if (list.hasResult(1)) {
                    players = list.getResult(1);
                }
                if (players == null) {
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
                if (players == null) {
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
                    if (level > 0) {
                        log.addSuccess("commands.xp.success.levels", String.valueOf(level), player.getName());
                    } else {
                        log.addSuccess("commands.xp.success.levels.minus", String.valueOf(-level), player.getName());
                    }
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
