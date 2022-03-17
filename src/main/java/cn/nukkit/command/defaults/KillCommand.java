package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.EntitySelector;
import cn.nukkit.utils.TextFormat;

import java.util.List;
import java.util.StringJoiner;

/**
 * @author Pub4Game
 * @since 2015/12/08
 */
public class KillCommand extends VanillaCommand {

    public KillCommand(String name) {
        super(name, "%nukkit.command.kill.description", "%nukkit.command.kill.usage", new String[]{"suicide"});
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

            List<Entity> entities = null;
            if (EntitySelector.hasArguments(args[0])) {
                if (sender.isPlayer())
                    entities = EntitySelector.matchEntities((Player)sender, args[0]);
                else
                    entities = EntitySelector.matchEntities(new Position(0,0,0, Server.getInstance().getDefaultLevel()), args[0]);
            } else if(sender.getServer().getPlayer(args[0]) != null){
                entities.set(0, sender.getServer().getPlayer(args[0]));
            }

            if (entities.size() == 0 ) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
                return false;
            }

            for (Entity entity : entities) {
                if (!(entity instanceof Player player)) {
                    entity.close();
                } else {
                    player.kill();
                }
            }
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
