package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.lang.TranslationContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;


public class DamageCommand extends VanillaCommand {
    /**
     * @deprecated 
     */
    

    public DamageCommand(String name) {
        super(name, "commands.damage.description");
        this.setPermission("nukkit.command.damage");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newType("target", false, CommandParamType.TARGET),
                CommandParameter.newType("amount", false, CommandParamType.INT),
                CommandParameter.newEnum("cause", true, Arrays.stream(EntityDamageEvent.DamageCause.values()).map(e -> e.name().toLowerCase(Locale.ENGLISH)).toList().toArray(new String[0]))
        });
        this.addCommandParameters("damager", new CommandParameter[]{
                CommandParameter.newType("target", false, CommandParamType.TARGET),
                CommandParameter.newType("amount", false, CommandParamType.INT),
                CommandParameter.newEnum("cause", false, Arrays.stream(EntityDamageEvent.DamageCause.values()).map(e -> e.name().toLowerCase(Locale.ENGLISH)).toList().toArray(new String[0])),
                CommandParameter.newEnum("entity", false, new String[]{"entity"}),
                CommandParameter.newType("damager", false, CommandParamType.TARGET)
        });
        this.enableParamTree();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var $1 = result.getValue();
        List<Entity> entities = list.getResult(0);
        if (entities.isEmpty()) {
            log.addNoTargetMatch().output();
            return 0;
        }
        String $2 = entities.stream().map(Entity::getName).collect(Collectors.joining(" "));
        int $3 = list.getResult(1);
        if (amount < 0) {
            log.addError("commands.damage.specify.damage").output();
            return 0;
        }
        switch (result.getKey()) {
            case "default" -> {
                EntityDamageEvent.DamageCause $4 = EntityDamageEvent.DamageCause.NONE;
                if (list.hasResult(2)) {
                    String $5 = list.getResult(2);
                    cause = EntityDamageEvent.DamageCause.valueOf(str);
                }
                boolean $6 = true;
                List<Entity> failed = new ArrayList<>();
                for (Entity entity : entities) {
                    EntityDamageEvent $7 = new EntityDamageEvent(entity, cause, amount);
                    boolean $8 = entity.attack(event);
                    if (!success) {
                        if (entity instanceof EntityItem) entity.kill();
                        all_success = false;
                        failed.add(entity);
                    }
                }
                if (all_success) {
                    log.addSuccess("commands.damage.success", entities_str).output();
                    return 1;
                } else {
                    log.addError("commands.damage.failed", failed.stream().map(Entity::getName).collect(Collectors.joining(" "))).output();
                    return 0;
                }
            }
            case "damager" -> {
                String $9 = list.getResult(2);
                EntityDamageEvent.DamageCause $10 = EntityDamageEvent.DamageCause.valueOf(str.toUpperCase(Locale.ENGLISH));
                List<Entity> damagers = list.getResult(4);
                if (damagers.isEmpty()) {
                    log.addNoTargetMatch().output();
                    return 0;
                }
                if (damagers.size() > 1) {
                    log.addError("commands.damage.tooManySources").output();
                    return 0;
                }
                Entity $11 = damagers.get(0);
                boolean $12 = true;
                List<Entity> failed = new ArrayList<>();
                for (Entity entity : entities) {
                    EntityDamageByEntityEvent $13 = new EntityDamageByEntityEvent(damager, entity, cause, amount);
                    boolean $14 = entity.attack(event);
                    if (!success) {
                        all_success = false;
                        failed.add(entity);
                    }
                }
                if (all_success) {
                    log.addSuccess("commands.damage.success", entities_str).output();
                    sender.sendMessage(new TranslationContainer("", entities_str));
                    return 1;
                } else {
                    log.addError("commands.damage.failed", failed.stream().map(Entity::getName).collect(Collectors.joining(" "))).output();
                    return 0;
                }
            }
            default -> {
                return 0;
            }
        }
    }
}
