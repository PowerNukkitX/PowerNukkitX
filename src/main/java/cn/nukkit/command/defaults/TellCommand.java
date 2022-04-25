package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.entity.Entity;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.command.EntitySelector;
import cn.nukkit.utils.TextFormat;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author xtypr
 * @since 2015/11/12
 */
public class TellCommand extends VanillaCommand {

    public TellCommand(String name) {
        super(name, "commands.tell.description", "commands.message.usage", new String[]{"w", "msg"});
        this.setPermission("nukkit.command.tell");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newType("message", CommandParamType.MESSAGE)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));

            return false;
        }

        String target = args[0].toLowerCase();

        List<Entity> entities = null;
        if(EntitySelector.hasArguments(target)){
            entities = EntitySelector.matchEntities(sender, target);
        }else {
            entities = Collections.singletonList(sender.getServer().getPlayer(target));
        }
        if (entities.isEmpty()) {
            sender.sendMessage(new TranslationContainer("commands.generic.player.notFound"));
            return true;
        }

        StringBuilder msg = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            msg.append(args[i]).append(" ");
        }
        if (msg.length() > 0) {
            msg = new StringBuilder(msg.substring(0, msg.length() - 1));
        }

        String displayName = (sender instanceof Player ? ((Player) sender).getDisplayName() : sender.getName());

        for (Entity entity : entities) {
            if (entity instanceof Player) {
                if (Objects.equals(entity, sender.asEntity())) {
                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.message.sameTarget"));
                    continue;
                }
                sender.sendMessage("[" + sender.getName() + " -> " + entity.getName() + "] " + msg);
                ((Player)entity).sendMessage("[" + displayName + " -> " + entity.getName() + "] " + msg);
            }
        }
        return true;
    }
}
