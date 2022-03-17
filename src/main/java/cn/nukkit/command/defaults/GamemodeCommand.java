package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.entity.Entity;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.EntitySelector;
import cn.nukkit.utils.TextFormat;

import java.util.List;

/**
 * @author xtypr
 * @since 2015/11/13
 */
public class GamemodeCommand extends VanillaCommand {

    public GamemodeCommand(String name) {
        super(name, "%nukkit.command.gamemode.description", "%commands.gamemode.usage",
                new String[]{"gm"});
        this.setPermission("nukkit.command.gamemode.survival;" +
                "nukkit.command.gamemode.creative;" +
                "nukkit.command.gamemode.adventure;" +
                "nukkit.command.gamemode.spectator;" +
                "nukkit.command.gamemode.other");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("gameMode", CommandParamType.INT),
                CommandParameter.newType("player", true, CommandParamType.TARGET)
        });
        this.commandParameters.put("byString", new CommandParameter[]{
                CommandParameter.newEnum("gameMode", CommandEnum.ENUM_GAMEMODE),
                CommandParameter.newType("player", true, CommandParamType.TARGET)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }

        int gameMode = Server.getGamemodeFromString(args[0]);
        if (gameMode == -1) {
            sender.sendMessage("Unknown game mode");
            return false;
        }

        List<Entity> entities = List.of();
        if (args.length > 1) {
            if (sender.hasPermission("nukkit.command.gamemode.other")) {

                if (EntitySelector.hasArguments(args[1])) {
                    if (sender.isPlayer())
                        entities = EntitySelector.matchEntities(sender, args[1]);
                    else
                        entities = EntitySelector.matchEntities(sender, args[1]);
                } else if(sender.getServer().getPlayer(args[1]) != null){
                    entities = List.of(sender.getServer().getPlayer(args[1]));
                }

                if (entities == null) {
                    return false;
                }

                entities.stream().filter(entity -> entity instanceof Player).toList();
                if (entities.size() == 0) {
                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
                    return false;
                }
            } else {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));
                return false;
            }
        } else if (!(sender instanceof Player)) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }

        if ((gameMode == 0 && !sender.hasPermission("nukkit.command.gamemode.survival")) ||
                (gameMode == 1 && !sender.hasPermission("nukkit.command.gamemode.creative")) ||
                (gameMode == 2 && !sender.hasPermission("nukkit.command.gamemode.adventure")) ||
                (gameMode == 3 && !sender.hasPermission("nukkit.command.gamemode.spectator"))) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));
            return false;
        }

        boolean successExecute = false;
        for (Entity target : entities) {

            if (!((Player) target).setGamemode(gameMode)) {
                sender.sendMessage("Game mode update for " + target.getName() + " failed");
            } else {
                successExecute = true;
                if (target.equals(sender)) {
                    Command.broadcastCommandMessage(sender, new TranslationContainer("commands.gamemode.success.self", Server.getGamemodeString(gameMode)));
                } else {
                    ((Player) target).sendMessage(new TranslationContainer("gameMode.changed", Server.getGamemodeString(gameMode)));
                    Command.broadcastCommandMessage(sender, new TranslationContainer("commands.gamemode.success.other", target.getName(), Server.getGamemodeString(gameMode)));
                }
            }
        }
        if (!successExecute) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
            return false;
        }
        return true;
    }
}
