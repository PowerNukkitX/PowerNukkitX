package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.NukkitMath;
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
        Entity target;
        Entity origin = null;
        if (args.length == 1 || args.length == 3 || args.length == 5) {
            if (sender instanceof Entity) {
                target = (Entity) sender;
            } else {
                sender.sendMessage(new TranslationContainer("commands.generic.ingame"));
                return false;
            }
            if (args.length == 1) {
                origin = target;
                List<Entity> targetEntities = null;
                if (EntitySelector.hasArguments(args[0])) {
                    if (sender.isPlayer())
                        targetEntities = EntitySelector.matchEntities((Player)sender, args[0]);
                    else
                        targetEntities = EntitySelector.matchEntities(new Position(0,0,0, Server.getInstance().getDefaultLevel()), args[0]);
                } else if (sender.getServer().getPlayer(args[0]) != null){
                    targetEntities.set(0,sender.getServer().getPlayer(args[1]));
                }
                if (targetEntities.size() == 0) {
                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
                    return false;
                } else if (targetEntities.size() > 1) {
                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
                    return false;
                }
                target = targetEntities.get(0);
                if (target == null) {
                    sender.sendMessage(TextFormat.RED + "Can't find player " + args[0]);
                    return false;
                }
            }
        } else {
            List<Entity> entities = null;
            if (EntitySelector.hasArguments(args[0])) {
                if (sender.isPlayer())
                    entities = EntitySelector.matchEntities((Player)sender, args[0]);
                else
                    entities = EntitySelector.matchEntities(new Position(0,0,0, Server.getInstance().getDefaultLevel()), args[0]);
            } else if (sender.getServer().getPlayer(args[0]) != null) {
                entities.set(0,sender.getServer().getPlayer(args[0]));
            }
            if (entities.size() == 0) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
                return false;
            } else if (entities.size() > 1) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
                return false;
            }
            target = entities.get(0);

            if (args.length == 2) {

                origin = target;
                List<Entity> targetEntities = null;
                if (EntitySelector.hasArguments(args[1])) {
                    if (sender.isPlayer())
                        targetEntities = EntitySelector.matchEntities((Player)sender, args[1]);
                    else
                        targetEntities = EntitySelector.matchEntities(new Position(0,0,0, Server.getInstance().getDefaultLevel()), args[1]);
                } else if(sender.getServer().getPlayer(args[1]) != null){
                    targetEntities.set(0,sender.getServer().getPlayer(args[1]));
                }
                if (targetEntities.size() == 0) {
                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
                    return false;
                } else if (targetEntities.size() > 1) {
                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
                    return false;
                }
                target = targetEntities.get(0);
            }
        }

        if (origin == null) {
            sender.sendMessage(TextFormat.RED + "Can't find player " + args[1]);
            return false;
        }
        if (args.length < 3) {
            origin.teleport(target, PlayerTeleportEvent.TeleportCause.COMMAND);
            Command.broadcastCommandMessage(sender, new TranslationContainer("commands.tp.success", origin.getName(), target.getName()));
            return true;
        } else if (target.getLevel() != null) {
            int pos;
            if (args.length == 4 || args.length == 6) {
                pos = 1;
            } else {
                pos = 0;
            }
            double x;
            double y;
            double z;
            double yaw;
            double pitch;
            try {
                x = parseTilde(args[pos++], target.x);
                y = parseTilde(args[pos++], target.y);
                z = parseTilde(args[pos++], target.z);
                if (args.length > pos) {
                    yaw = Integer.parseInt(args[pos++]);
                    pitch = Integer.parseInt(args[pos]);
                } else {
                    yaw = target.getYaw();
                    pitch = target.getPitch();
                }
            } catch (NumberFormatException e1) {
                sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
                return true;
            }
            target.teleport(new Location(x, y, z, yaw, pitch, target.getLevel()), PlayerTeleportEvent.TeleportCause.COMMAND);
            Command.broadcastCommandMessage(sender, new TranslationContainer("commands.tp.success.coordinates", target.getName(), String.valueOf(NukkitMath.round(x, 2)), String.valueOf(NukkitMath.round(y, 2)), String.valueOf(NukkitMath.round(z, 2))));
            return true;
        }
        sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
        return true;
    }
}
