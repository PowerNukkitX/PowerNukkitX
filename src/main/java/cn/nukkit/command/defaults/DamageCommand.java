package cn.nukkit.command.defaults;

import cn.nukkit.command.utils.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DamageCommand extends VanillaCommand {

    public DamageCommand(String name) {
        super(name, "commands.damage.description");
        this.setPermission("nukkit.command.damage");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newType("target",false, CommandParamType.TARGET),
                CommandParameter.newType("amount", false,CommandParamType.INT),
                CommandParameter.newEnum("cause",true, Arrays.stream(EntityDamageEvent.DamageCause.values()).map(e -> e.name().toLowerCase()).collect(Collectors.toList()).toArray(new String[0]))
        });
        this.addCommandParameters("damager", new CommandParameter[]{
                CommandParameter.newType("target",false, CommandParamType.TARGET),
                CommandParameter.newType("amount", false,CommandParamType.INT),
                CommandParameter.newEnum("cause",false, Arrays.stream(EntityDamageEvent.DamageCause.values()).map(e -> e.name().toLowerCase()).collect(Collectors.toList()).toArray(new String[0])),
                CommandParameter.newEnum("entity",false, new String[]{"entity"}),
                CommandParameter.newType("damager",false, CommandParamType.TARGET)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender) || !sender.isEntity()) {
            return false;
        }

        CommandParser parser = new CommandParser(this, sender, args);

        String form = parser.matchCommandForm();
        if (form == null) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }

        try {
            List<Entity> entities = parser.parseTargets();
            String entities_str = entities.stream().map(e -> e.getName()).collect(Collectors.joining(" "));
            int amount = parser.parseInt();
            if (amount < 0){
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.damage.specify.damage"));
                return false;
            }
            switch (form) {
                case "default" ->{
                    EntityDamageEvent.DamageCause cause = EntityDamageEvent.DamageCause.NONE;
                    if (parser.hasNext())
                        cause = parser.parseEnum(EntityDamageEvent.DamageCause.class);
                    boolean all_success = true;
                    List<Entity> failed = new ArrayList<>();
                    for (Entity entity : entities) {
                        EntityDamageEvent event = new EntityDamageEvent(entity,cause,amount);
                        boolean success = entity.attack(event);
                        if (!success) {
                            all_success = false;
                            failed.add(entity);
                        }
                    }
                    if (all_success) {
                        sender.sendMessage(new TranslationContainer("commands.damage.success", entities_str));
                        return true;
                    }else{
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.damage.failed", failed.stream().map(e -> e.getName()).collect(Collectors.joining(" "))));
                        return false;
                    }
                }
                case "damager" -> {
                    EntityDamageEvent.DamageCause cause = EntityDamageEvent.DamageCause.NONE;
                    cause = parser.parseEnum(EntityDamageEvent.DamageCause.class);
                    parser.parseString();
                    List<Entity> damagers = parser.parseTargets();
                    if (damagers.size() == 0) {
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.noTargetMatch"));
                        return false;
                    }else if (damagers.size() > 1){
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.damage.tooManySources"));
                        return false;
                    }
                    Entity damager = damagers.get(0);
                    boolean all_success = true;
                    List<Entity> failed = new ArrayList<>();
                    for (Entity entity : entities) {
                        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(damager,entity,cause,amount);
                        boolean success = entity.attack(event);
                        if (!success) {
                            all_success = false;
                            failed.add(entity);
                        }
                    }
                    if (all_success) {
                        sender.sendMessage(new TranslationContainer("commands.damage.success", entities_str));
                        return true;
                    }else{
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.damage.failed", failed.stream().map(e -> e.getName()).collect(Collectors.joining(" "))));
                        return false;
                    }
                }
                default -> {}
            }
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }
}
