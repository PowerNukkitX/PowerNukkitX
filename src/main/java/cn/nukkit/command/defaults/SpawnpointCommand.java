package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.utils.CommandParser;
import cn.nukkit.command.utils.EntitySelector;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.TextFormat;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xtypr
 * @since 2015/12/13
 */
public class SpawnpointCommand extends VanillaCommand {
    public SpawnpointCommand(String name) {
        super(name, "commands.spawnpoint.description");
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
            return false;
        }
        List<Player> players = List.of();
        if (args.length == 0) {
            if (sender.isPlayer()) {
                players = List.of(sender.asPlayer());
            } else {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.noTargetMatch"));
                return false;
            }
        } else {
            if (EntitySelector.hasArguments(args[0])) {
                players = EntitySelector.matchEntities(sender, args[0]).stream().filter(e -> e instanceof Player).map(e -> (Player)e).toList();
            } else if (sender.getServer().getPlayer(args[0]) != null) {
                players = List.of(sender.getServer().getPlayer(args[0]));
            }
            if (players.size() == 0) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.noTargetMatch"));
                return false;
            }
        }

        Level level = sender.getPosition().getLevel();
        DecimalFormat round2 = new DecimalFormat("##0.00");
        if (args.length == 4) {
            if (level != null) {
                Position position;
                CommandParser parser = new CommandParser(this, sender, args);
                try {
                    parser.parseString();//jump over target
                    position = parser.parsePosition();
                } catch (CommandSyntaxException e1) {
                    sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
                    return false;
                }
                if(level.isOverWorld()) {
                    if (position.y < -64) position.y = -64;
                    if (position.y > 320) position.y = 320;
                } else {
                    if (position.y < 0) position.y = 0;
                    if (position.y > 255) position.y = 255;
                }
                for (Player player : players) {
                    player.setSpawn(position);
                }
                Command.broadcastCommandMessage(sender, new TranslationContainer("commands.spawnpoint.success.multiple.specific", players.stream().map(Player::getName).collect(Collectors.joining(" ")),
                        round2.format(position.x),
                        round2.format(position.y),
                        round2.format(position.z)));
                return true;
            }
        } else if (args.length <= 1) {
            if (sender.isPlayer()) {
                Position pos = sender.getPosition();
                sender.asPlayer().setSpawn(pos);
                Command.broadcastCommandMessage(sender, new TranslationContainer("commands.spawnpoint.success.single", sender.getName(),
                        round2.format(pos.x),
                        round2.format(pos.y),
                        round2.format(pos.z)));
                return true;
            } else {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.noTargetMatch"));
                return false;
            }
        }
        sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
        return false;
    }
}
