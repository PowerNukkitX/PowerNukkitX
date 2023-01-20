package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.level.Level;

import java.util.Map;

/**
 * @author xtypr
 * @since 2015/11/13
 */
public class SaveCommand extends VanillaCommand {

    public SaveCommand(String name) {
        super(name, "Save the server (levels and players)");
        this.setPermission("nukkit.command.save.perform");
        this.commandParameters.clear();
        this.commandParameters.put("default", CommandParameter.EMPTY_ARRAY);
        this.paramTree = new ParamTree(this);
    }

    @Since("1.19.50-r4")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        log.addSuccess("commands.save.start").output(true, true);
        for (Player player : sender.getServer().getOnlinePlayers().values()) {
            player.save();
        }
        for (Level level : sender.getServer().getLevels().values()) {
            level.save(true);
        }
        log.addSuccess("commands.save.success").output(true, true);
        return 1;
    }
}
