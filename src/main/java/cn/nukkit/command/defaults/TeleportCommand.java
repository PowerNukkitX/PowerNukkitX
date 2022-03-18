package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityTeleportEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.CommandParser;
import cn.nukkit.utils.CommandSyntaxException;
import cn.nukkit.utils.EntitySelector;
import cn.nukkit.utils.TextFormat;

import java.util.List;

/**
 * @author Pub4Game and milkice
 * @since 2015/11/12
 */
public class TeleportCommand extends VanillaCommand {
    public TeleportCommand(String name) {
        super(name, "%nukkit.command.tp.description", "%commands.tp.usage");
        this.setPermission("nukkit.command.teleport");
        this.commandParameters.clear();
        this.commandParameters.put("->Player", new CommandParameter[]{
                CommandParameter.newType("destination", CommandParamType.TARGET),
        });
        this.commandParameters.put("Player->Player", new CommandParameter[]{
                CommandParameter.newType("victim", CommandParamType.TARGET),
                CommandParameter.newType("destination", CommandParamType.TARGET)
        });
        this.commandParameters.put("Player->Pos", new CommandParameter[]{
                CommandParameter.newType("victim", CommandParamType.TARGET),
                CommandParameter.newType("destination", CommandParamType.POSITION),
                CommandParameter.newType("yRot", true, CommandParamType.VALUE),
                CommandParameter.newType("xRot", true, CommandParamType.VALUE)
        });
        this.commandParameters.put("->Pos", new CommandParameter[]{
                CommandParameter.newType("destination", CommandParamType.POSITION),
                CommandParameter.newType("yRot", true, CommandParamType.VALUE),
                CommandParameter.newType("xRot", true, CommandParamType.VALUE)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }
        if (args.length < 1 || args.length > 6) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }
        List <Entity> target;
        List<Entity> origin = List.of();
        if (args.length == 1 || args.length == 3 || args.length == 5) {
            if (sender.isEntity()) {
                target = List.of(sender.asEntity());
            } else {
                sender.sendMessage(new TranslationContainer("commands.generic.ingame"));
                return false;
            }
            if (args.length == 1) {
                origin = target;
                List<Entity> targetEntities = List.of();
                if (EntitySelector.hasArguments(args[0])) {
                    if (sender.isPlayer())
                        targetEntities = EntitySelector.matchEntities(sender, args[0]);
                    else
                        targetEntities = EntitySelector.matchEntities(sender, args[0]);
                } else if (sender.getServer().getPlayer(args[0]) != null){
                    targetEntities = List.of(sender.getServer().getPlayer(args[0]));
                }
                if (targetEntities.size() == 0) {
                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
                    return false;
                }
                target = targetEntities;
            }
        } else {
            List<Entity> entities = List.of();
            if (EntitySelector.hasArguments(args[0])) {
                if (sender.isPlayer())
                    entities = EntitySelector.matchEntities(sender, args[0]);
                else
                    entities = EntitySelector.matchEntities(sender, args[0]);
            } else if (sender.getServer().getPlayer(args[0]) != null) {
                entities = List.of(sender.getServer().getPlayer(args[0]));
            }
            if (entities.size() == 0) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
                return false;
            }
            target = entities;

            if (args.length == 2) {
                origin = entities;
                List<Entity> targetEntities = List.of();
                if (EntitySelector.hasArguments(args[1])) {
                    if (sender.isPlayer())
                        targetEntities = EntitySelector.matchEntities(sender, args[1]);
                    else
                        targetEntities = EntitySelector.matchEntities(sender, args[1]);
                } else if(sender.getServer().getPlayer(args[1]) != null){
                    targetEntities = List.of(sender.getServer().getPlayer(args[1]));
                }
                if (targetEntities.size() == 0) {
                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
                    return false;
                }
                target = targetEntities;
            }
        }

        if (args.length < 3) {
            boolean successExecute = false;
            if (origin.size() == 0) {
                sender.sendMessage(TextFormat.RED + "Can't find player " + args[1]);
                return false;
            }
            for (Entity originEntity : origin) {
                EntityTeleportEvent event = new EntityTeleportEvent(originEntity, originEntity, target.get(0));
                if (!event.isCancelled()) {
                    originEntity.teleport(target.get(0));
                    successExecute = true;
                }
            }
            if (!successExecute) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
                return false;
            }

            Command.broadcastCommandMessage(sender, new TranslationContainer("commands.tp.success", target.get(0).getName()));
            return true;

        } else {
            int pos;
            if (args.length == 4 || args.length == 6) {
                pos = 1;
            } else {
                pos = 0;
            }
            double yaw;
            double pitch;
            Position position;
            CommandParser parser = new CommandParser(this, sender, args);
            try {
                position = parser.parsePosition();
                pos = pos + 3;

                if (args.length > pos) {
                    yaw = Integer.parseInt(args[pos++]);
                    pitch = Integer.parseInt(args[pos]);
                } else {
                    yaw = 0;
                    pitch = 0;
                }
            } catch (NumberFormatException | CommandSyntaxException e1) {
                sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
                return false;
            }
            boolean successExecute = false;
            for (Entity targetEntity : target) {
                Location location = new Location(position.x, position.y, position.z, yaw, pitch, targetEntity.getLevel());
                EntityTeleportEvent event = new EntityTeleportEvent(targetEntity, targetEntity, location);
                if (!event.isCancelled()) {
                    targetEntity.teleport(location, PlayerTeleportEvent.TeleportCause.COMMAND);
                    successExecute = true;
                }
            }
            if (!successExecute) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
                return false;
            }

            Command.broadcastCommandMessage(sender, new TranslationContainer("commands.tp.success.coordinates", target.size() + " entities", String.valueOf(NukkitMath.round(position.x, 2)), String.valueOf(NukkitMath.round(position.y, 2)), String.valueOf(NukkitMath.round(position.z, 2))));
            return true;
        }
    }
}
