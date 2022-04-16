package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.EntitySelector;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.entity.Entity;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.TextFormat;

import java.text.DecimalFormat;
import java.util.List;

/**
 * @author xtypr
 * @since 2015/12/13
 */
public class SpawnpointCommand extends VanillaCommand {
    public SpawnpointCommand(String name) {
        super(name, "commands.spawnpoint.description", "commands.spawnpoint.usage");
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
        List<Entity> entities = List.of();
        if (args.length == 0) {
            if (sender.isPlayer()) {
                entities = List.of(sender.asPlayer());
            } else {
                sender.sendMessage(new TranslationContainer("commands.generic.ingame"));
                return false;
            }
        } else {

            if (EntitySelector.hasArguments(args[0])) {
                entities = EntitySelector.matchEntities(sender, args[0]);
            } else if (sender.getServer().getPlayer(args[0]) != null) {
                entities = List.of(sender.getServer().getPlayer(args[0]));
            }

            List<Entity> players = entities.stream().filter(entity -> entity instanceof Player).toList();
            if (players.size() == 0) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
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
                    position = parser.parsePosition();
                } catch (CommandSyntaxException e1) {
                    sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
                    return false;
                }
                if (level.isOverWorld()) {
                    if (position.y < -64) position.y = -64;
                    if (position.y > 320) position.y = 320;
                } else {
                    if (position.y < 0) position.y = 0;
                    if (position.y > 255) position.y = 255;
                }
                for (Entity entity : entities) {
                    Player target = (Player) entity;
                    target.setSpawn(position);
                    Command.broadcastCommandMessage(sender, new TranslationContainer("commands.spawnpoint.success", target.getName(),
                            round2.format(position.x),
                            round2.format(position.y),
                            round2.format(position.z)));
                }
                return true;
            }
        } else if (args.length <= 1) {
            if (sender instanceof Player) {
                Position pos = sender.getPosition();
                sender.asPlayer().setSpawn(pos);
                Command.broadcastCommandMessage(sender, new TranslationContainer("commands.spawnpoint.success", entities.size() + "players",
                        round2.format(pos.x),
                        round2.format(pos.y),
                        round2.format(pos.z)));
                return true;
            } else {
                sender.sendMessage(new TranslationContainer("commands.generic.ingame"));
                return false;
            }
        }
        sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
        return false;
    }
}
