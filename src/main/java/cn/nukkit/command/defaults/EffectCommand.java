package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.utils.CommandParser;
import cn.nukkit.entity.Entity;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.InstantEffect;
import cn.nukkit.utils.ServerException;
import cn.nukkit.utils.TextFormat;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Snake1999 and Pub4Game
 * @since 2016/1/23
 */
public class EffectCommand extends Command {
    public EffectCommand(String name) {
        super(name, "commands.effect.description");
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
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }
        if (args.length < 2) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }
        CommandParser parser = new CommandParser(this, sender, args);
        try {
            List<Entity> entities = parser.parseTargets();

            if (entities.size() == 0) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
                return false;
            }
            if (args[1].equalsIgnoreCase("clear")) {
                for (Entity entity : entities) {
                    for (Effect effect : entity.getEffects().values()) {
                        entity.removeEffect(effect.getId());
                    }
                    sender.sendMessage(new TranslationContainer("commands.effect.success.removed.all", entity.getName()));
                }
                return true;
            }
            Effect effect;
            try {
                effect = Effect.getEffect(Integer.parseInt(args[1]));
            } catch (NumberFormatException | ServerException a) {
                try {
                    effect = Effect.getEffectByName(args[1]);
                } catch (Exception e) {
                    sender.sendMessage(new TranslationContainer("commands.effect.notFound", args[1]));
                    return false;
                }
            }
            int duration = 300;
            int amplification = 0;
            if (args.length >= 3) {
                try {
                    duration = Integer.parseInt(args[2]);
                } catch (NumberFormatException a) {
                    sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
                    return false;
                }
                if (!(effect instanceof InstantEffect)) {
                    duration *= 20;
                }
            } else if (effect instanceof InstantEffect) {
                duration = 1;
            }
            if (args.length >= 4) {
                try {
                    amplification = Integer.parseInt(args[3]);
                } catch (NumberFormatException a) {
                    sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
                    return false;
                }
            }
            if (args.length >= 5) {
                String v = args[4].toLowerCase();
                if (v.matches("(?i)|on|true|t|1")) {
                    effect.setVisible(false);
                }
            }
            boolean successExecute = false;
            for (Entity entity : entities) {
                if (duration == 0) {
                    if (!entity.hasEffect(effect.getId())) {
                        if (entity.getEffects().size() == 0) {
                            sender.sendMessage(new TranslationContainer("commands.effect.failure.notActive.all", entity.getName()));
                        } else {
                            sender.sendMessage(new TranslationContainer("commands.effect.failure.notActive", effect.getName(), entity.getName()));
                        }
                        continue;
                    }
                    successExecute = true;
                    entity.removeEffect(effect.getId());
                    sender.sendMessage(new TranslationContainer("commands.effect.success.removed", effect.getName(), entity.getName()));
                } else {
                    successExecute = true;
                    effect.setDuration(duration).setAmplifier(amplification);
                    entity.addEffect(effect.clone());
                    Command.broadcastCommandMessage(sender, new TranslationContainer("%commands.effect.success", effect.getName(), String.valueOf(effect.getAmplifier()), entity.getName(), String.valueOf(effect.getDuration() / 20)));
                }
            }
            return successExecute;
        } catch (CommandSyntaxException e) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            e.printStackTrace();
            return false;
        }
    }
}
