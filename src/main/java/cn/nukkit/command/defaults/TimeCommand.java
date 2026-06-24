package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.level.Level;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;

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
        this.commandParameters.put("timeModeQuery", new CommandParameter[]{
                CommandParameter.newEnum("mode", new CommandEnum("TimeModeQuery", "query")),
                CommandParameter.newEnum("mode", new CommandEnum("TimeQuery", "daytime", "gametime", "day"))
        });
        this.commandParameters.put("timeModeOther", new CommandParameter[]{
                CommandParameter.newEnum("mode", new CommandEnum("TimeMode", "start", "stop"))
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        final Level level = sender.getLocation().getLevel();
        switch (result.getKey()) {
            case "add" -> {
                if (!sender.hasPermission("nukkit.command.time.add")) {
                    log.addMessage("nukkit.command.generic.permission").output();
                    return 0;
                }
                final int value = list.getResult(1);
                level.checkTime();
                level.setTime(level.getTime() + value);
                level.checkTime();
                log.addSuccess("commands.time.added", String.valueOf(value)).output(true);
                return 1;
            }
            case "setAmount" -> {
                if (!sender.hasPermission("nukkit.command.time.set")) {
                    log.addMessage("nukkit.command.generic.permission").output();
                    return 0;
                }
                final int value = list.getResult(1);
                level.checkTime();
                level.setTime(value);
                level.checkTime();
                log.addSuccess("commands.time.set", String.valueOf(value)).output(true);
                return 1;
            }
            case "setTime" -> {
                if (!sender.hasPermission("nukkit.command.time.set")) {
                    log.addMessage("nukkit.command.generic.permission").output();
                    return 0;
                }
                final String str = list.getResult(1);
                final int value = switch (str) {
                    case "day" -> Level.TIME_DAY;
                    case "night" -> Level.TIME_NIGHT;
                    case "midnight" -> Level.TIME_MIDNIGHT;
                    case "noon" -> Level.TIME_NOON;
                    case "sunrise" -> Level.TIME_SUNRISE;
                    case "sunset" -> Level.TIME_SUNSET;
                    case null, default -> 0;
                };
                level.checkTime();
                level.setTime(level.getTime() - level.getDayTime() + value);
                level.checkTime();
                log.addSuccess("commands.time.set", String.valueOf(value)).output(true);
                return 1;
            }
            case "timeModeQuery" -> {
                if (!sender.hasPermission("nukkit.command.time.query")) {
                    log.addMessage("nukkit.command.generic.permission").output();
                    return 0;
                }
                final String mode = list.getResult(1);
                switch (mode) {
                    case "daytime" -> log.addSuccess("commands.time.query.daytime", String.valueOf(level.getDayTime())).output(true);
                    case "gametime" -> log.addSuccess("commands.time.query.gametime", String.valueOf(level.getTime())).output(true);
                    case "day" -> log.addSuccess("commands.time.query.day", String.valueOf(level.getDay())).output(true);
                }
                return 1;
            }
            case "timeModeOther" -> {
                final String mode = list.getResult(0);
                if ("start".equals(mode)) {
                    if (!sender.hasPermission("nukkit.command.time.start")) {
                        log.addMessage("nukkit.command.generic.permission").output();
                        return 0;
                    }
                    level.checkTime();
                    level.startTime();
                    level.checkTime();
                    log.addSuccess("Restarted the time").output(true);
                } else if ("stop".equals(mode)) {
                    if (!sender.hasPermission("nukkit.command.time.stop")) {
                        log.addMessage("nukkit.command.generic.permission").output();
                        return 0;
                    }
                    level.checkTime();
                    level.stopTime();
                    level.checkTime();
                    log.addSuccess("Stopped the time").output(true);
                } else if ("query".equals(mode)) {
                    if (!sender.hasPermission("nukkit.command.time.query")) {
                        log.addMessage("nukkit.command.generic.permission").output();
                        return 0;
                    }
                    log.addSuccess("commands.time.query.gametime", String.valueOf(level.getTime())).output(true);
                }
                return 1;
            }
            default -> {
                return 0;
            }
        }
    }
}
