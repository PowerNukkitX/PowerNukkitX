package cn.nukkit.command.defaults;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.utils.CommandParser;
import cn.nukkit.lang.TranslationContainer;

public class SetMaxPlayersCommand extends VanillaCommand {

    public SetMaxPlayersCommand(String name) {
        super(name, "commands.setmaxplayers.description");
        this.setPermission("nukkit.command.setmaxplayers");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newType("maxPlayers",false, CommandParamType.INT)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        try {
            int maxPlayers = parser.parseInt();
            boolean lowerBound = false;

            if (maxPlayers < Server.getInstance().getOnlinePlayers().size()) {
                maxPlayers = Server.getInstance().getOnlinePlayers().size();
                lowerBound = true;
            }

            sender.getServer().setMaxPlayers(maxPlayers);

            sender.sendMessage(new TranslationContainer("commands.setmaxplayers.success", String.valueOf(maxPlayers)));

            if (lowerBound) {
                sender.sendMessage(new TranslationContainer("commands.setmaxplayers.success.lowerbound"));
            }
        } catch (CommandSyntaxException e) {
             sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }

        return true;
    }
}
