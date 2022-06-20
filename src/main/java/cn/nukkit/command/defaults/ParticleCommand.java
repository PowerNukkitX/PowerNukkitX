package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
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
            particles.add(particle.getIdentifier().substring(10));
        }
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newEnum("effect", new CommandEnum("particle",particles,false)),
                CommandParameter.newType("position", CommandParamType.POSITION),
                CommandParameter.newType("count", true, CommandParamType.INT)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        if (args.length < 4) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));

            return false;
        }

        Position defaultPosition;

        defaultPosition = sender.getPosition();


        String name = args[0].toLowerCase();

        CommandParser parser = new CommandParser(this,sender,args);

        Position position = null;
        try {
            parser.parseString();
            position = parser.parsePosition();
        } catch (Exception e) {
            return false;
        }

        int count = 1;
        if (args.length > 4) {
            try {
                double c = Double.parseDouble(args[4]);
                count = (int) c;
            } catch (Exception e) {
                sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
                return false;
            }
        }
        count = Math.max(1, count);

        for (int i = 0; i < count; i++) {
            position.level.addParticleEffect(position.asVector3f(),name,-1,position.level.getDimension(),(Player[]) null);
        }

        sender.sendMessage(new TranslationContainer("commands.particle.success", name, String.valueOf(count)));

        return true;
    }
}
