package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.InstantEffect;

import java.util.List;
import java.util.Map;

/**
 * @author Snake1999 and Pub4Game
 * @since 2016/1/23
 */
public class EffectCommand extends VanillaCommand {
    public EffectCommand(String name) {
        super(name, "commands.effect.description", "nukkit.command.effect.usage");
        this.setPermission("nukkit.command.effect");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("effect", CommandEnum.ENUM_EFFECT),
                CommandParameter.newType("seconds", true, CommandParamType.INT),
                CommandParameter.newType("amplifier", true, CommandParamType.INT),
                CommandParameter.newEnum("hideParticle", true, CommandEnum.ENUM_BOOLEAN)
        });
        this.commandParameters.put("clear", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("clear", new CommandEnum("ClearEffects", "clear"))
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        List<Entity> entities = list.getResult(0);
        entities = entities.stream().filter(e -> !(e instanceof EntityItem)).toList();
        if (entities.isEmpty()) {
            log.addNoTargetMatch().output();
            return 0;
        }
        switch (result.getKey()) {
            case "default" -> {
                Effect effect;
                String str = list.getResult(1);
                try {
                    effect = Effect.get(str);
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
                    effect.setVisible(!v);
                }

                int success = 0;
                for (Entity entity : entities) {
                    if (duration == 0) {
                        if (!entity.hasEffect(effect.getType())) {
                            log.addError("commands.effect.failure.notActive", effect.getName(), entity.getName()).output();
                            continue;
                        }
                        entity.removeEffect(effect.getType());
                        log.addSuccess("commands.effect.success.removed", effect.getName(), entity.getName()).output();
                    } else {
                        effect.setDuration(duration).setAmplifier(amplification);
                        entity.addEffect(effect.clone());
                        log.addSuccess("%commands.effect.success", effect.getName(), String.valueOf(effect.getAmplifier()), entity.getName(), String.valueOf(effect.getDuration() / 20))
                                .output(true);
                    }
                    success++;
                }
                return success;
            }
            case "clear" -> {
                int success = 0;
                for (Entity entity : entities) {
                    if (entity.getEffects().isEmpty()) {
                        log.addError("commands.effect.failure.notActive.all", entity.getName());
                        continue;
                    }
                    for (Effect effect : entity.getEffects().values()) {
                        entity.removeEffect(effect.getType());
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
