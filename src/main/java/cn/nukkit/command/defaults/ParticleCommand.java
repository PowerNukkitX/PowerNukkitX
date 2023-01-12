package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
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
        this.paramTree = new ParamTree(this);
    }

    @Since("1.19.50-r4")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        Position defaultPosition = sender.getPosition();
        String name = result.getValue().getResult(0);
        Position position = result.getValue().getResult(1);
        int count = 1;
        if (result.getValue().hasResult(3)) count = result.getValue().getResult(3);
        if (count < 1) {
            log.addNumTooSmall(3, 1).output();
            return 0;
        }
        for (int i = 0; i < count; i++) {
            position.level.addParticleEffect(position.asVector3f(), name, -1, position.level.getDimension(), (Player[]) null);
        }
        log.addSuccess("commands.particle.success", name, String.valueOf(count));
        return 1;
    }
}
