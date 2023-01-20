package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.tree.node.PlayersNode;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.utils.TextFormat;

import java.util.List;
import java.util.Map;

/**
 * @author xtypr
 * @since 2015/11/13
 */
public class GamemodeCommand extends VanillaCommand {

    public GamemodeCommand(String name) {
        super(name, "commands.gamemode.description", "",
                new String[]{"gm"});
        this.setPermission("nukkit.command.gamemode.survival;" +
                "nukkit.command.gamemode.creative;" +
                "nukkit.command.gamemode.adventure;" +
                "nukkit.command.gamemode.spectator;" +
                "nukkit.command.gamemode.other");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("gameMode", CommandParamType.INT),
                CommandParameter.newType("player", true, CommandParamType.TARGET, new PlayersNode())
        });
        this.commandParameters.put("byString", new CommandParameter[]{
                CommandParameter.newEnum("gameMode", CommandEnum.ENUM_GAMEMODE),
                CommandParameter.newType("player", true, CommandParamType.TARGET, new PlayersNode())
        });
        this.paramTree = new ParamTree(this);
    }

    @Since("1.19.50-r4")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        int gameMode = -1;
        List<Player> players;
        switch (result.getKey()) {
            case "default" -> gameMode = list.getResult(0);
            case "byString" -> {
                String str = list.getResult(0);
                gameMode = Server.getGamemodeFromString(str);
            }
        }
        if (gameMode < 0 || gameMode > 3) {
            log.addError("commands.gamemode.fail.invalid", String.valueOf(gameMode)).output();
            return 0;
        }
        if (list.hasResult(1)) {
            if (sender.hasPermission("nukkit.command.gamemode.other")) {
                players = list.getResult(1);
            } else {
                log.addMessage(TextFormat.RED + "%nukkit.command.generic.permission").output();
                return 0;
            }
        } else {
            players = sender.isPlayer() ? List.of(sender.asPlayer()) : List.of();
        }

        if ((gameMode == 0 && !sender.hasPermission("nukkit.command.gamemode.survival")) ||
                (gameMode == 1 && !sender.hasPermission("nukkit.command.gamemode.creative")) ||
                (gameMode == 2 && !sender.hasPermission("nukkit.command.gamemode.adventure")) ||
                (gameMode == 3 && !sender.hasPermission("nukkit.command.gamemode.spectator"))) {
            log.addMessage(TextFormat.RED + "%nukkit.command.generic.permission").output();
            return 0;
        }

        for (Player target : players) {
            target.setGamemode(gameMode);
            if (target.equals(sender.asPlayer())) {
                log.addSuccess("commands.gamemode.success.self", Server.getGamemodeString(gameMode));
            } else {
                log.outputObjectWhisper(target, "gameMode.changed", Server.getGamemodeString(gameMode));
                log.addSuccess("commands.gamemode.success.other", Server.getGamemodeString(gameMode), target.getName());
            }
        }
        log.successCount(players.size()).output(true, true);
        return players.size();
    }
}
