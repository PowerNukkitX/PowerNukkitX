package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.level.Level;

import java.util.Map;

/**
 * @author xtypr
 * @since 2015/11/11
 */
public class TimeCommand extends VanillaCommand {

    public TimeCommand(String name) {
        super(name, "commands.time.description");
        this.setPermission("nukkit.command.time.add;" +
                "nukkit.command.time.set;" +
                "nukkit.command.time.start;" +
                "nukkit.command.time.stop");
        this.commandParameters.clear();
        this.commandParameters.put("1arg", new CommandParameter[]{
                CommandParameter.newEnum("mode", new CommandEnum("TimeMode", "query", "start", "stop"))
        });
        this.commandParameters.put("add", new CommandParameter[]{
                CommandParameter.newEnum("mode", new CommandEnum("TimeModeAdd", "add")),
                CommandParameter.newType("amount", CommandParamType.INT)
        });
        this.commandParameters.put("setAmount", new CommandParameter[]{
                CommandParameter.newEnum("mode", false, new CommandEnum("TimeModeSet", "set")),
                CommandParameter.newType("amount", CommandParamType.INT)
        });
        this.commandParameters.put("setTime", new CommandParameter[]{
                CommandParameter.newEnum("mode", new CommandEnum("TimeModeSet", "set")),
                CommandParameter.newEnum("time", new CommandEnum("TimeSpec", "day", "night", "midnight", "noon", "sunrise", "sunset"))
        });
        this.paramTree = new ParamTree(this);
    }

    @Since("1.19.50-r4")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        switch (result.getKey()) {
            case "1arg" -> {
                String mode = list.getResult(0);
                if ("start".equals(mode)) {
                    if (!sender.hasPermission("nukkit.command.time.start")) {
                        log.addMessage("nukkit.command.generic.permission").output();
                        return 0;
                    }
                    for (Level level : sender.getServer().getLevels().values()) {
                        level.checkTime();
                        level.startTime();
                        level.checkTime();
                    }
                    log.addSuccess("Restarted the time").output(true, true);
                } else if ("stop".equals(mode)) {
                    if (!sender.hasPermission("nukkit.command.time.stop")) {
                        log.addMessage("nukkit.command.generic.permission").output();
                        return 0;
                    }
                    for (Level level : sender.getServer().getLevels().values()) {
                        level.checkTime();
                        level.stopTime();
                        level.checkTime();
                    }
                    log.addSuccess("Stopped the time").output(true, true);
                } else if ("query".equals(mode)) {
                    if (!sender.hasPermission("nukkit.command.time.query")) {
                        log.addMessage("nukkit.command.generic.permission").output();
                        return 0;
                    }
                    Level level;
                    if (sender instanceof Player) {
                        level = ((Player) sender).getLevel();
                    } else {
                        level = sender.getServer().getDefaultLevel();
                    }
                    log.addSuccess("commands.time.query.gametime", String.valueOf(level.getTime())).output(true, true);
                }
                return 1;
            }
            case "add" -> {
                if (!sender.hasPermission("nukkit.command.time.add")) {
                    log.addMessage("nukkit.command.generic.permission").output();
                    return 0;
                }
                int value = list.getResult(1);
                if (value < 0) {
                    log.addNumTooSmall(1, 0).output();
                    return 0;
                }
                for (Level level : sender.getServer().getLevels().values()) {
                    level.checkTime();
                    level.setTime(level.getTime() + value);
                    level.checkTime();
                }
                log.addSuccess("commands.time.added", String.valueOf(value)).output(true, true);
                return 1;
            }
            case "setAmount" -> {
                if (!sender.hasPermission("nukkit.command.time.set")) {
                    log.addMessage("nukkit.command.generic.permission").output();
                    return 0;
                }
                int value = list.getResult(1);
                if (value < 0) {
                    log.addNumTooSmall(1, 0).output();
                    return 0;
                }
                for (Level level : sender.getServer().getLevels().values()) {
                    level.checkTime();
                    level.setTime(value);
                    level.checkTime();
                }
                log.addSuccess("commands.time.set", String.valueOf(value)).output(true, true);
                return 1;
            }
            case "setTime" -> {
                if (!sender.hasPermission("nukkit.command.time.set")) {
                    log.addMessage("nukkit.command.generic.permission").output();
                    return 0;
                }
                int value = 0;
                String str = list.getResult(1);
                if ("day".equals(str)) {
                    value = Level.TIME_DAY;
                } else if ("night".equals(str)) {
                    value = Level.TIME_NIGHT;
                } else if ("midnight".equals(str)) {
                    value = Level.TIME_MIDNIGHT;
                } else if ("noon".equals(str)) {
                    value = Level.TIME_NOON;
                } else if ("sunrise".equals(str)) {
                    value = Level.TIME_SUNRISE;
                } else if ("sunset".equals(str)) {
                    value = Level.TIME_SUNSET;
                }
                for (Level level : sender.getServer().getLevels().values()) {
                    level.checkTime();
                    level.setTime(value);
                    level.checkTime();
                }
                log.addSuccess("commands.time.set", String.valueOf(value)).output(true, true);
                return 1;
            }
            default -> {
                return 0;
            }
        }
    }
}
