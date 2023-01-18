package cn.nukkit.command.defaults;

import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.command.utils.EntitySelector;
import cn.nukkit.entity.Entity;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;

import java.util.Map;

/**
 * @author xtypr
 * @since 2015/11/12
 */
public class MeCommand extends VanillaCommand {

    public MeCommand(String name) {
        super(name, "commands.me.description", "nukkit.command.me.usage");
        this.setPermission("nukkit.command.me");
        this.commandParameters.clear();
        this.commandParameters.put("message", new CommandParameter[]{
                CommandParameter.newType("message", CommandParamType.MESSAGE)
        });
        this.commandParameters.put("default", CommandParameter.EMPTY_ARRAY);
        this.paramTree = new ParamTree(this);
    }

    @Since("1.19.50-r4")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        String name = sender.getName();
        String[] args;
        if (result.getKey().equals("message")) {
            args = result.getValue().getResult(0);
        } else args = new String[]{""};
        StringBuilder msg = new StringBuilder();
        for (String arg : args) {
            if (EntitySelector.hasArguments(arg)) {
                for (Entity entity : EntitySelector.matchEntities(sender, arg)) {
                    msg.append(entity.getName()).append(" ");
                }
            } else {
                msg.append(arg).append(" ");
            }
        }

        if (msg.length() > 0) {
            msg = new StringBuilder(msg.substring(0, msg.length() - 1));
        }
        broadcastCommandMessage(sender, new TranslationContainer("chat.type.emote", name, TextFormat.WHITE + msg.toString()), true);
        return 1;
    }
}
