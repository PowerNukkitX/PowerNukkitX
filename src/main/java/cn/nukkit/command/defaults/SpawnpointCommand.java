package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.CommandParser;
import cn.nukkit.utils.CommandSyntaxException;
import cn.nukkit.utils.TextFormat;

import java.text.DecimalFormat;

/**
 * @author xtypr
 * @since 2015/12/13
 */
public class SpawnpointCommand extends VanillaCommand {
    public SpawnpointCommand(String name) {
        super(name, "%nukkit.command.spawnpoint.description", "%commands.spawnpoint.usage");
        this.setPermission("nukkit.command.spawnpoint");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", true, CommandParamType.TARGET),
                CommandParameter.newType("spawnPos", true, CommandParamType.POSITION),
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        Player target;
        if (args.length == 0) {
            if (sender instanceof Player) {
                target = (Player) sender;
            } else {
                sender.sendMessage(new TranslationContainer("commands.generic.ingame"));
                return true;
            }
        } else {
            target = sender.getServer().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
                return true;
            }
        }
        Level level = target.getLevel();
        DecimalFormat round2 = new DecimalFormat("##0.00");
        if (args.length == 4) {
            if (level != null) {
                Position position;
                CommandParser parser = new CommandParser(this, sender, args);
                try {
                    position = parser.parsePosition();
                } catch (CommandSyntaxException e1) {
                    sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
                    return false;
                }
                if(level.isOverWorld()) {
                    if (position.y < -64) position.y = -64;
                    if (position.y > 320) position.y = 320;
                } else {
                    if (position.y < 0) position.y = 0;
                    if (position.y > 255) position.y = 255;
                }
                target.setSpawn(position);
                Command.broadcastCommandMessage(sender, new TranslationContainer("commands.spawnpoint.success", target.getName(),
                        round2.format(position.x),
                        round2.format(position.y),
                        round2.format(position.z)));
                return true;
            }
        } else if (args.length <= 1) {
            if (sender instanceof Player) {
                Position pos = (Position) sender;
                target.setSpawn(pos);
                Command.broadcastCommandMessage(sender, new TranslationContainer("commands.spawnpoint.success", target.getName(),
                        round2.format(pos.x),
                        round2.format(pos.y),
                        round2.format(pos.z)));
                return true;
            } else {
                sender.sendMessage(new TranslationContainer("commands.generic.ingame"));
                return true;
            }
        }
        sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
        return true;
    }
}
