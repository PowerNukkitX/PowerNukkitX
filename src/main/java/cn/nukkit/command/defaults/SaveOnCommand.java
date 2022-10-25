package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.lang.TranslationContainer;

/**
 * @author xtypr
 * @since 2015/11/13
 */
public class SaveOnCommand extends VanillaCommand {

    public SaveOnCommand(String name) {
        super(name, "Enable auto saving");//no translation in client
        this.setPermission("nukkit.command.save.enable");
        this.commandParameters.clear();
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args, Boolean sendCommandFeedback) {
        if (!this.testPermission(sender)) {
            return true;
        }
        sender.getServer().setAutoSave(true);
        Command.broadcastCommandMessage(sender, new TranslationContainer("commands.save.enabled"));
        return true;
    }
}
