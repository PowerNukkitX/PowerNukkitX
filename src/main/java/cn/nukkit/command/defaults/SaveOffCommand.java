package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;

import java.util.Map;

/**
 * @author xtypr
 * @since 2015/11/13
 */
public class SaveOffCommand extends VanillaCommand {

    public SaveOffCommand(String name) {
        super(name, "Disable auto saving");//no translation in client
        this.setPermission("nukkit.command.save.disable");
        this.commandParameters.clear();
        this.commandParameters.put("default", CommandParameter.EMPTY_ARRAY);
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        sender.getServer().setAutoSave(false);
        log.addSuccess("commands.save.disabled").output(true);
        return 1;
    }
}
