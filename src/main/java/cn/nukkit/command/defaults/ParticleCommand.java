package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.level.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xtypr
 * @since 2015/11/12
 */
public class ParticleCommand extends VanillaCommand {
    public ParticleCommand(String name) {
        super(name, "commands.particle.description");
        this.setPermission("nukkit.command.particle");
        this.commandParameters.clear();
        List<String> particles = new ArrayList<>();
        for (ParticleEffect particle : ParticleEffect.values()) {
            particles.add(particle.getIdentifier());
        }
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newEnum("effect", new CommandEnum("particle", particles, true)),
                CommandParameter.newType("position", CommandParamType.POSITION),
                CommandParameter.newType("count", true, CommandParamType.INT)
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        String name = result.getValue().getResult(0);
        Position position = result.getValue().getResult(1);
        int count = 1;
        if (result.getValue().hasResult(2)) count = result.getValue().getResult(2);
        if (count < 1) {
            log.addNumTooSmall(2, 1).output();
            return 0;
        }
        for (int i = 0; i < count; i++) {
            position.level.addParticleEffect(position, name);
        }
        log.addSuccess("commands.particle.success", name, String.valueOf(count));
        return 1;
    }
}
