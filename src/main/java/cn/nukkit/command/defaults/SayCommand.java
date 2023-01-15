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

import java.util.List;
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
        this.paramTree = new ParamTree(this);
    }

    @Since("1.19.50-r4")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        String senderString = sender.getName();
        String[] message = result.getValue().getResult(0);
        StringBuilder msg = new StringBuilder();
        for (String arg : message) {
            if (EntitySelector.hasArguments(arg)) {
                List<Entity> entities = EntitySelector.matchEntities(sender, arg);
                for (Entity entity : entities) {
                    msg.append(entity.getName()).append(" ");
                }
            } else {
                msg.append(arg).append(" ");
            }
        }
        if (msg.length() > 0) {
            msg = new StringBuilder(msg.substring(0, msg.length() - 1));
        }
        sender.getServer().broadcastMessage(new TranslationContainer("%chat.type.announcement", senderString, msg.toString()));
        return 1;
    }
}
