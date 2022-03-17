package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.CommandParser;
import cn.nukkit.utils.CommandSyntaxException;

public class SetMaxPlayersCommand extends VanillaCommand {

    public SetMaxPlayersCommand(String name) {
        super(name, "Sets the maximum number of players for this game session.", "/setmaxplayers [maxPlayers: int]");
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

            if (maxPlayers < 1) {
                maxPlayers = 1;
                lowerBound = true;
            }

            sender.getServer().setMaxPlayers(maxPlayers);

            sender.sendMessage(String.format("Set max players to %1$d.", maxPlayers));

            if (lowerBound) {
                sender.sendMessage("(Bound to minimum allowed connections)");
            }
        } catch (CommandSyntaxException e) {
            sender.sendMessage(parser.getErrorMessage());
            return false;
        }

        return true;
    }
}
