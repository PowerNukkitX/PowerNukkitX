package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.command.EntitySelector;
import cn.nukkit.utils.TextFormat;

import java.util.Collections;
import java.util.List;

/**
 * @author Tee7even
 */
public class TitleCommand extends VanillaCommand {
    public TitleCommand(String name) {
        super(name, "commands.title.description");
        this.setPermission("nukkit.command.title");

        this.commandParameters.clear();
        this.commandParameters.put("clear", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("clear", new CommandEnum("TitleClear", "clear"))
        });
        this.commandParameters.put("reset", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("reset", new CommandEnum("TitleReset", "reset"))
        });
        this.commandParameters.put("set", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("titleLocation", new CommandEnum("TitleSet", "title", "subtitle", "actionbar")),
                CommandParameter.newType("titleText", CommandParamType.MESSAGE)
        });
        this.commandParameters.put("times", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("times", new CommandEnum("TitleTimes", "times")),
                CommandParameter.newType("fadeIn", CommandParamType.INT),
                CommandParameter.newType("stay", CommandParamType.INT),
                CommandParameter.newType("fadeOut", CommandParamType.INT)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }
        List<Player> players = EntitySelector.hasArguments(args[0]) ? EntitySelector.matchEntities(sender, args[0]).stream().filter(e -> e instanceof Player).map(e -> (Player) e).toList() : null;
        if (players == null) players = Collections.singletonList(Server.getInstance().getPlayerExact(args[0]));
        if (players == null) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
            return true;
        }

        for (Player player : players) {
            if (args.length == 2) {
                switch (args[1].toLowerCase()) {
                    case "clear":
                        player.clearTitle();
                        sender.sendMessage(new TranslationContainer("nukkit.command.title.clear", player.getName()));
                        break;
                    case "reset":
                        player.resetTitleSettings();
                        sender.sendMessage(new TranslationContainer("nukkit.command.title.reset", player.getName()));
                        break;
                    default:
                        sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
                        return false;
                }
            } else if (args.length == 3) {
                switch (args[1].toLowerCase()) {
                    case "title":
                        player.sendTitle(args[2]);
                        sender.sendMessage(new TranslationContainer("nukkit.command.title.title",
                                TextFormat.clean(args[2]), player.getName()));
                        break;
                    case "subtitle":
                        player.setSubtitle(args[2]);
                        sender.sendMessage(new TranslationContainer("nukkit.command.title.subtitle", TextFormat.clean(args[2]), player.getName()));
                        break;
                    case "actionbar":
                        player.sendActionBar(args[2]);
                        sender.sendMessage(new TranslationContainer("nukkit.command.title.actionbar", new String[]{TextFormat.clean(args[2]), player.getName()}));
                        break;
                    default:
                        sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
                        return false;
                }
            } else if (args.length == 5) {
                if (args[1].toLowerCase().equals("times")) {
                    try {
                        sender.sendMessage(new TranslationContainer("nukkit.command.title.times.success",
                                args[2], args[3], args[4], player.getName()));
                    } catch (NumberFormatException exception) {
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%nukkit.command.title.times.fail"));
                    }
                } else {
                    sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
                    return false;
                }
            } else {
                sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
                return false;
            }
        }

        return true;
    }
}
