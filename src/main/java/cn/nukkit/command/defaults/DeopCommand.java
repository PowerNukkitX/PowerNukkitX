package cn.nukkit.command.defaults;

import cn.nukkit.IPlayer;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.utils.CommandParser;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.network.protocol.types.PlayerAbility;
import cn.nukkit.utils.TextFormat;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xtypr
 * @since 2015/11/12
 */
public class DeopCommand extends VanillaCommand {
    public DeopCommand(String name) {
        super(name, "commands.deop.description");
        this.setPermission("nukkit.command.op.take");
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

        CommandParser parser = new CommandParser(this, sender, args);
        try {
            List<IPlayer> players = parser.parseTargetPlayers().stream().map(p -> (IPlayer) p).collect(Collectors.toList());
            if (players.size() == 0) {
                players.add(sender.getServer().getOfflinePlayer(args[0]));
            }
            for (IPlayer player : players) {
                if (!player.isOp()) {
                    sender.sendMessage(TextFormat.RED + "Privileges cannot be revoked (revoked or with higher privileges)");//no translation in client
                    return false;
                }
                player.setOp(false);

                if (player instanceof Player) {
                    ((Player) player).sendMessage(new TranslationContainer(TextFormat.GRAY + "%commands.deop.message"));
                }

                Command.broadcastCommandMessage(sender, new TranslationContainer("commands.deop.success", new String[]{player.getName()}));
            }
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
