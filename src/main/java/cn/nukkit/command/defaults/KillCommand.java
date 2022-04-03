package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.EntitySelector;
import cn.nukkit.utils.TextFormat;

import java.util.List;

/**
 * @author Pub4Game
 * @since 2015/12/08
 */
public class KillCommand extends VanillaCommand {

    public KillCommand(String name) {
        super(name, "commands.kill.description", "commands.kill.usage", new String[]{"suicide"});
        this.setPermission("nukkit.command.kill.self;"
                + "nukkit.command.kill.other");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", true, CommandParamType.TARGET)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        if (args.length >= 2) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }
        if (args.length == 1) {
            if (!sender.hasPermission("nukkit.command.kill.other")) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));
                return true;
            }
            List<Entity> entities = EntitySelector.hasArguments(args[0]) ? EntitySelector.matchEntities(sender,args[0]) : null;
            if (entities == null) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
                return false;
            }
            StringBuilder message = new StringBuilder();
            boolean successExecute = true;
            for (Entity entity : entities) {
                if (entity.getName().equals(sender.getName())) {
                    if (!sender.hasPermission("nukkit.command.kill.self")) {
                        continue;
                    }
                }
                if (entity instanceof Player player) {
                    EntityDamageEvent ev = new EntityDamageEvent(player, DamageCause.SUICIDE, 1000);
                    sender.getServer().getPluginManager().callEvent(ev);
                    if (ev.isCancelled()) {
                        continue;
                    }
                    player.setLastDamageCause(ev);
                    player.setHealth(0);
                } else {
                    entity.kill();
                }
                message.append(entity.getName()).append(", ");
            }
            Command.broadcastCommandMessage(sender, new TranslationContainer("commands.kill.successful", message.toString()));
            return true;
        }
        if (sender instanceof Player) {
            if (!sender.hasPermission("nukkit.command.kill.self")) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));
                return true;
            }
            EntityDamageEvent ev = new EntityDamageEvent((Player) sender, DamageCause.SUICIDE, 1000);
            sender.getServer().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return true;
            }
            ((Player) sender).setLastDamageCause(ev);
            ((Player) sender).setHealth(0);
            sender.sendMessage(new TranslationContainer("commands.kill.successful", sender.getName()));
        } else {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }
        return true;
    }
}
