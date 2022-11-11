package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.utils.CommandParser;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.level.Position;

import java.util.ArrayList;

/**
 * @author xtypr
 * @since 2015/11/12
 */
public class ParticleCommand extends VanillaCommand {
    public ParticleCommand(String name) {
        super(name, "commands.particle.description");
        this.setPermission("nukkit.command.particle");
        this.commandParameters.clear();
        ArrayList<String> particles = new ArrayList<>();
        for (ParticleEffect particle : ParticleEffect.values()) {
            particles.add(particle.getIdentifier());
        }
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newEnum("effect", new CommandEnum("particle", particles, false)),
                CommandParameter.newType("position", CommandParamType.POSITION),
                CommandParameter.newType("count", true, CommandParamType.INT)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        if (parser.matchCommandForm() == null) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }

        Position defaultPosition = sender.getPosition();

        try {
            String name = parser.parseString();

            Position position = parser.parsePosition();

            int count = 1;
            if (parser.hasNext())
                count = parser.parseInt();
            count = Math.max(1, count);

            for (int i = 0; i < count; i++) {
                position.level.addParticleEffect(position.asVector3f(), name, -1, position.level.getDimension(), (Player[]) null);
            }

            sender.sendMessage(new TranslationContainer("commands.particle.success", name, String.valueOf(count)));
            return true;
        } catch (CommandSyntaxException e) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }
    }
}
