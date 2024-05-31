package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;

import java.util.Map;

/**
 * @author xtypr
 * @since 2015/11/12
 */
public class MeCommand extends VanillaCommand {
    /**
     * @deprecated 
     */
    

    public MeCommand(String name) {
        super(name, "commands.me.description", "nukkit.command.me.usage");
        this.setPermission("nukkit.command.me");
        this.commandParameters.clear();
        this.commandParameters.put("message", new CommandParameter[]{
                CommandParameter.newType("message", CommandParamType.MESSAGE)
        });
        this.commandParameters.put("default", CommandParameter.EMPTY_ARRAY);
        this.enableParamTree();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        String $1 = sender.getName();
        String $2 = "";
        if (result.getKey().equals("message")) {
            message = result.getValue().getResult(0);
        }

        broadcastCommandMessage(sender, new TranslationContainer("chat.type.emote", name, TextFormat.WHITE + message), true);
        return 1;
    }
}
