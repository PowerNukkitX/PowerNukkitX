package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.lang.TranslationContainer;

import java.util.Map;

/**
 * @author xtypr
 * @since 2015/11/12
 */
public class SayCommand extends VanillaCommand {

    public SayCommand(String name) {
        super(name, "commands.say.description");
        this.setPermission("nukkit.command.say");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("message", CommandParamType.MESSAGE)
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        String senderString = sender.getName();
        String message = result.getValue().getResult(0);
        sender.getServer().broadcastMessage(new TranslationContainer("%chat.type.announcement", senderString, message));
        return 1;
    }
}
