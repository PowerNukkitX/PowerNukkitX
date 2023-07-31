package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.node.PlayersNode;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.player.GameMode;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;
import java.util.List;
import java.util.Map;

/**
 * @author xtypr
 * @since 2015/11/13
 */
public class GamemodeCommand extends VanillaCommand {

    public GamemodeCommand(String name) {
        super(name, "commands.gamemode.description", "", new String[] {"gm"});
        this.setPermission("nukkit.command.gamemode.survival;" + "nukkit.command.gamemode.creative;"
                + "nukkit.command.gamemode.adventure;"
                + "nukkit.command.gamemode.spectator;"
                + "nukkit.command.gamemode.other");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[] {
            CommandParameter.newType("gameMode", CommandParamType.INT),
            CommandParameter.newType("player", true, CommandParamType.TARGET, new PlayersNode())
        });
        this.commandParameters.put("byString", new CommandParameter[] {
            CommandParameter.newEnum("gameMode", CommandEnum.ENUM_GAMEMODE),
            CommandParameter.newType("player", true, CommandParamType.TARGET, new PlayersNode())
        });
        this.enableParamTree();
    }

    @Override
    public int execute(
            CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        List<Player> players;
        GameMode gamemode = null;
        switch (result.getKey()) {
            case "default" -> gamemode = GameMode.fromOrdinal(list.getResult(0));
            case "byString" -> gamemode = GameMode.fromString(list.getResult(0));
        }
        if (gamemode == null) {
            log.addError("commands.gamemode.fail.invalid", list.getResult(0).toString())
                    .output();
            return 0;
        }
        if (list.hasResult(1)) {
            if (sender.hasPermission("nukkit.command.gamemode.other")) {
                players = list.getResult(1);
            } else {
                log.addMessage(TextFormat.RED + "%nukkit.command.generic.permission")
                        .output();
                return 0;
            }
        } else {
            players = sender.isPlayer() ? List.of(sender.asPlayer()) : List.of();
        }

        if (players.isEmpty()) {
            log.addNoTargetMatch().output();
            return 0;
        }

        if ((gamemode == GameMode.SURVIVAL && !sender.hasPermission("nukkit.command.gamemode.survival"))
                || (gamemode == GameMode.CREATIVE && !sender.hasPermission("nukkit.command.gamemode.creative"))
                || (gamemode == GameMode.ADVENTURE && !sender.hasPermission("nukkit.command.gamemode.adventure"))
                || (gamemode == GameMode.SPECTATOR && !sender.hasPermission("nukkit.command.gamemode.spectator"))) {
            log.addMessage(TextFormat.RED + "%nukkit.command.generic.permission")
                    .output();
            return 0;
        }

        for (Player target : players) {
            target.setGamemode(gamemode);
            if (target.equals(sender.asPlayer())) {
                log.addSuccess("commands.gamemode.success.self", gamemode.getTranslatableName());
            } else {
                log.outputObjectWhisper(target, "gameMode.changed", gamemode.getTranslatableName());
                log.addSuccess("commands.gamemode.success.other", gamemode.getTranslatableName(), target.getName());
            }
        }
        log.successCount(players.size()).output(true);
        return players.size();
    }
}
