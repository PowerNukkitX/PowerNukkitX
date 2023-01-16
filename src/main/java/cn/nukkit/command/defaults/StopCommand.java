package cn.nukkit.command.defaults;

import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.utils.CommandLogger;

import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class StopCommand extends VanillaCommand {

    public StopCommand(String name) {
        super(name, "commands.stop.description");
        this.setPermission("nukkit.command.stop");
        this.commandParameters.clear();
        this.paramTree = new ParamTree(this);
    }

    @Since("1.19.50-r4")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        log.addSuccess("commands.stop.start").output(true, true);
        sender.getServer().shutdown();
        return 1;
    }
}
