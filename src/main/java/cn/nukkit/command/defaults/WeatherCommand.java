package cn.nukkit.command.defaults;

import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;

import java.util.Map;

/**
 * @author Angelic47 (Nukkit Project)
 */
public class WeatherCommand extends VanillaCommand {

    public WeatherCommand(String name) {
        super(name, "commands.weather.description", "commands.weather.usage");
        this.setPermission("nukkit.command.weather");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newEnum("type", new CommandEnum("WeatherType", "clear", "rain", "thunder")),
                CommandParameter.newType("duration", true, CommandParamType.INT)
        });
        this.paramTree = new ParamTree(this);
    }

    @Since("1.19.50-r4")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        String weather = list.getResult(0);
        Level level = sender.getPosition().level;
        int seconds;
        if (list.hasResult(1)) {
            seconds = list.getResult(1);
        } else {
            seconds = 600 * 20;
        }
        switch (weather) {
            case "clear" -> {
                level.setRaining(false);
                level.setThundering(false);
                level.setRainTime(seconds * 20);
                level.setThunderTime(seconds * 20);
                log.addSuccess("commands.weather.clear").output(true, true);
                return 1;
            }
            case "rain" -> {
                level.setRaining(true);
                level.setRainTime(seconds * 20);
                log.addSuccess("commands.weather.rain").output(true, true);
                return 1;
            }
            case "thunder" -> {
                level.setThundering(true);
                level.setRainTime(seconds * 20);
                level.setThunderTime(seconds * 20);
                log.addSuccess("commands.weather.thunder").output(true, true);
                return 1;
            }
            default -> {
                sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
                return 0;
            }
        }
    }
}
