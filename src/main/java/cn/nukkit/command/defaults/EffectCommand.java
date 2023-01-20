package cn.nukkit.command.defaults;

import cn.nukkit.api.Since;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.InstantEffect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Snake1999 and Pub4Game
 * @since 2016/1/23
 */
public class EffectCommand extends Command {
    public EffectCommand(String name) {
        super(name, "commands.effect.description", "nukkit.command.effect.usage");
        this.setPermission("nukkit.command.effect");
        this.commandParameters.clear();

        List<String> effects = new ArrayList<>();
        for (Field field : Effect.class.getDeclaredFields()) {
            if (field.getType() == int.class && field.getModifiers() == (Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL)) {
                effects.add(field.getName().toLowerCase());
            }
        }

        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("effect", new CommandEnum("Effect", effects)),
                CommandParameter.newType("seconds", true, CommandParamType.INT),
                CommandParameter.newType("amplifier", true, CommandParamType.INT),
                CommandParameter.newEnum("hideParticle", true, CommandEnum.ENUM_BOOLEAN)
        });
        this.commandParameters.put("clear", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("clear", new CommandEnum("ClearEffects", "clear"))
        });
        this.paramTree = new ParamTree(this);
    }

    @Since("1.19.50-r4")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        List<Entity> entities = list.getResult(0);
        entities = entities.stream().filter(e -> !(e instanceof EntityItem)).toList();
        switch (result.getKey()) {
            case "default" -> {
                Effect effect;
                String str = list.getResult(1);
                try {
                    effect = Effect.getEffectByName(str);
                } catch (RuntimeException e) {
                    log.addError("commands.effect.notFound", str).output();
                    return 0;
                }
                int duration = 300;
                int amplification = 0;
                if (list.hasResult(2)) {
                    duration = list.getResult(2);
                    if (!(effect instanceof InstantEffect)) {
                        duration *= 20;
                    }
                } else if (effect instanceof InstantEffect) {
                    duration = 1;
                }
                if (duration < 0) {
                    log.addNumTooSmall(2, 0).output();
                    return 0;
                }

                if (list.hasResult(3)) {
                    amplification = list.getResult(3);
                }
                if (amplification < 0) {
                    log.addNumTooSmall(3, 0).output();
                    return 0;
                }

                if (list.hasResult(4)) {
                    boolean v = list.getResult(4);
                    effect.setVisible(v);
                }

                int success = 0;
                for (Entity entity : entities) {
                    if (duration == 0) {
                        if (!entity.hasEffect(effect.getId())) {
                            log.addError("commands.effect.failure.notActive", effect.getName(), entity.getName()).output();
                            continue;
                        }
                        entity.removeEffect(effect.getId());
                        log.addSuccess("commands.effect.success.removed", effect.getName(), entity.getName()).output();
                    } else {
                        effect.setDuration(duration).setAmplifier(amplification);
                        entity.addEffect(effect.clone());
                        log.addSuccess("%commands.effect.success", effect.getName(), String.valueOf(effect.getAmplifier()), entity.getName(), String.valueOf(effect.getDuration() / 20))
                                .output(true, true);
                    }
                    success++;
                }
                return success;
            }
            case "clear" -> {
                int success = 0;
                for (Entity entity : entities) {
                    if (entity.getEffects().size() == 0) {
                        log.addError("commands.effect.failure.notActive.all", entity.getName());
                        continue;
                    }
                    for (Effect effect : entity.getEffects().values()) {
                        entity.removeEffect(effect.getId());
                    }
                    success++;
                    log.addSuccess("commands.effect.success.removed.all", entity.getName());
                }
                log.successCount(success).output();
                return success;
            }
            default -> {
                return 0;
            }
        }
    }
}
