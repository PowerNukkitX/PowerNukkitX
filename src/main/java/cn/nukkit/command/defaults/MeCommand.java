package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.entity.Entity;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.command.utils.EntitySelector;
import cn.nukkit.utils.TextFormat;

/**
 * @author xtypr
 * @since 2015/11/12
 */
public class MeCommand extends VanillaCommand {

    public MeCommand(String name) {
        super(name, "commands.me.description");
        this.setPermission("nukkit.command.me");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("message", CommandParamType.MESSAGE)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));

            return false;
        }

        String name = sender.getName();

        StringBuilder msg = new StringBuilder();
        for (String arg : args) {
            if (EntitySelector.hasArguments(arg)){
                for (Entity entity : EntitySelector.matchEntities(sender, arg)){
                    msg.append(entity.getName()).append(" ");
                }
            }else {
                msg.append(arg).append(" ");
            }
        }

        if (msg.length() > 0) {
            msg = new StringBuilder(msg.substring(0, msg.length() - 1));
        }

        sender.getServer().broadcastMessage(new TranslationContainer("chat.type.emote", name, TextFormat.WHITE + msg.toString()));

        return true;
    }
}
