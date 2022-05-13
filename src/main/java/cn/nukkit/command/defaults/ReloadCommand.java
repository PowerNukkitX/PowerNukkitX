package cn.nukkit.command.defaults;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ReloadCommand extends VanillaCommand {

    public ReloadCommand(String name) {
        super(name, "Reload the server ");
        this.setPermission("nukkit.command.reload");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[0]);
        this.commandParameters.put("function", new CommandParameter[]{
            CommandParameter.newEnum("type",true,new String[]{"function"})
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        Command.broadcastCommandMessage(sender, new TranslationContainer(TextFormat.YELLOW + "%nukkit.command.reload.reloading" + TextFormat.WHITE));
        sender.getServer().reload();
        Command.broadcastCommandMessage(sender, new TranslationContainer(TextFormat.YELLOW + "%nukkit.command.reload.reloaded" + TextFormat.WHITE));

        return true;
    }
}
