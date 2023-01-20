package cn.nukkit.command.defaults;

import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.utils.CommandLogger;
import co.aikar.timings.Timings;
import co.aikar.timings.TimingsExport;

import java.util.Map;

/**
 * @author fromgate
 * @author Pub4Game
 */
public class TimingsCommand extends TestCommand {

    public TimingsCommand(String name) {
        super(name, "%nukkit.command.timings.description", "%nukkit.command.timings.usage");
        this.setPermission("nukkit.command.timings");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newEnum("action", new CommandEnum("TimingsAction", "on", "off", "paste", "verbon", "verboff", "reset", "report"))
        });
        this.paramTree = new ParamTree(this);
    }

    @Since("1.19.50-r4")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        String mode = list.getResult(0);

        if (mode.equals("on")) {
            Timings.setTimingsEnabled(true);
            Timings.reset();
            log.addMessage("nukkit.command.timings.enable").output();
            return 1;
        } else if (mode.equals("off")) {
            Timings.setTimingsEnabled(false);
            log.addMessage("nukkit.command.timings.disable").output();
            return 1;
        }

        if (!Timings.isTimingsEnabled()) {
            log.addMessage("nukkit.command.timings.timingsDisabled").output();
            return 1;
        }

        switch (mode) {
            case "verbon":
                log.addMessage("nukkit.command.timings.verboseEnable").output();
                Timings.setVerboseEnabled(true);
                break;
            case "verboff":
                log.addMessage("nukkit.command.timings.verboseDisable").output();
                Timings.setVerboseEnabled(true);
                break;
            case "reset":
                Timings.reset();
                log.addMessage("nukkit.command.timings.reset").output();
                break;
            case "report":
            case "paste":
                TimingsExport.reportTimings(sender);
                break;
        }
        return 1;
    }
}
