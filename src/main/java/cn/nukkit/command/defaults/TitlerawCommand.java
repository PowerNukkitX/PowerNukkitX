package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.command.utils.RawText;
import cn.nukkit.utils.TextFormat;

import java.util.List;
import java.util.Map;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class TitlerawCommand extends VanillaCommand {

    public TitlerawCommand(String name) {
        super(name, "commands.titleraw.description");
        this.setPermission("nukkit.command.titleraw");
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
                CommandParameter.newType("titleJson", CommandParamType.RAWTEXT)
        });
        this.commandParameters.put("times", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("times", new CommandEnum("TitleTimes", "times")),
                CommandParameter.newType("fadeIn", CommandParamType.INT),
                CommandParameter.newType("stay", CommandParamType.INT),
                CommandParameter.newType("fadeOut", CommandParamType.INT)
        });
        this.paramTree = new ParamTree(this);
    }

    @Since("1.19.50-r4")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        List<Player> players = list.getResult(0);

        switch (result.getKey()) {
            case "clear" -> {
                for (Player player : players) {
                    player.clearTitle();
                    log.addMessage(TextFormat.WHITE + "%nukkit.command.title.clear", player.getName());
                }
                log.output();
                return 1;
            }
            case "reset" -> {
                for (Player player : players) {
                    player.resetTitleSettings();
                    log.addMessage(TextFormat.WHITE + "%nukkit.command.title.reset", player.getName());
                }
                log.output();
                return 1;
            }
            case "set" -> {
                String titleLocation = list.getResult(1);
                RawText rawText = list.getResult(2);
                switch (titleLocation) {
                    case "title" -> {
                        for (Player player : players) {
                            player.setRawTextTitle(rawText);
                            log.addSuccess("commands.titleraw.success");
                        }
                        log.output();
                    }
                    case "subtitle" -> {
                        for (Player player : players) {
                            player.setRawTextSubTitle(rawText);
                            log.addSuccess("commands.titleraw.success");
                        }
                        log.output();
                    }
                    case "actionbar" -> {
                        for (Player player : players) {
                            player.setRawTextActionBar(rawText);
                            log.addSuccess("commands.titleraw.success");
                        }
                        log.output();
                    }
                    default -> {
                        log.addMessage("commands.generic.usage", "\n" + this.getCommandFormatTips());
                        return 0;
                    }
                }
                return 1;
            }
            case "times" -> {
                int fadeIn = list.getResult(2);
                int stay = list.getResult(3);
                int fadeOut = list.getResult(4);
                for (var player : players) {
                    log.addMessage(TextFormat.WHITE + "%nukkit.command.title.times.success", String.valueOf(fadeIn), String.valueOf(stay), String.valueOf(fadeOut), player.getName());
                }
                log.output();
                return 1;
            }
            default -> {
                return 0;
            }
        }
    }
}
