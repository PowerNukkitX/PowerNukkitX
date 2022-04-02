package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.entity.Entity;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Position;
import cn.nukkit.utils.EntitySelector;
import cn.nukkit.utils.TextFormat;

import java.util.List;

/**
 * @author xtypr
 * @since 2015/11/12
 */
public class SayCommand extends VanillaCommand {

    public SayCommand(String name) {
        super(name, "commands.say.description", "commands.say.usage");
        this.setPermission("nukkit.command.say");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("message", CommandParamType.MESSAGE)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }

        String senderString = sender.getName();

        StringBuilder msg = new StringBuilder();
        for (String arg : args) {
            if (EntitySelector.hasArguments(arg)){
                List<Entity> entities = EntitySelector.matchEntities(sender, arg);
                for(Entity entity : entities){
                    msg.append(entity.getName()).append(" ");
                }
            }else {
                msg.append(arg).append(" ");
            }
        }
        if (msg.length() > 0) {
            msg = new StringBuilder(msg.substring(0, msg.length() - 1));
        }


        sender.getServer().broadcastMessage(new TranslationContainer(
                TextFormat.LIGHT_PURPLE + "%chat.type.announcement",
                senderString, TextFormat.LIGHT_PURPLE + msg.toString()));
        return true;
    }
}
