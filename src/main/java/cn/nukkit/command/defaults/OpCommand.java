package cn.nukkit.command.defaults;

import cn.nukkit.IPlayer;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.utils.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xtypr
 * @since 2015/11/12
 */
public class OpCommand extends VanillaCommand {

    public OpCommand(String name) {
        super(name, "commands.op.description");
        this.setPermission("nukkit.command.op.give");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }
        if (args.length == 0) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }

        CommandParser parser = new CommandParser(this,sender,args);
        try {
            List<IPlayer> players = parser.parseTargetPlayers().stream().map(p -> (IPlayer)p).collect(Collectors.toList());
            if (players.size() == 0) {
                players.add(sender.getServer().getOfflinePlayer(args[0]));
            }

            for (IPlayer player : players) {
                if (player.isOp()){
                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.op.failed", player.getName()));
                    return false;
                }
                Command.broadcastCommandMessage(sender, new TranslationContainer("commands.op.success", player.getName()));
                if (player instanceof Player) {
                    ((Player) player).sendMessage(new TranslationContainer(TextFormat.GRAY + "%commands.op.message"));
                }

                player.setOp(true);
            }
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
