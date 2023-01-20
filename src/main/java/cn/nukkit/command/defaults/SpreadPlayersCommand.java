package cn.nukkit.command.defaults;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.entity.Entity;
import cn.nukkit.math.Vector3;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class SpreadPlayersCommand extends VanillaCommand {

    private final ThreadLocalRandom random;

    public SpreadPlayersCommand(String name) {
        super(name, "commands.spreadplayers.description");
        this.setPermission("nukkit.command.spreadplayers");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newType("x", false, CommandParamType.VALUE),
                CommandParameter.newType("z", false, CommandParamType.VALUE),
                CommandParameter.newType("spreadDistance", false, CommandParamType.FLOAT),
                CommandParameter.newType("maxRange", false, CommandParamType.FLOAT),
                CommandParameter.newType("victim", false, CommandParamType.TARGET)
        });
        this.random = ThreadLocalRandom.current();
        this.paramTree = new ParamTree(this);
    }

    @Since("1.19.50-r4")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        double x = list.getResult(0);
        double z = list.getResult(1);
        float spreadDistance = list.getResult(2);
        float maxRange = list.getResult(3);
        List<Entity> targets = list.getResult(4);

        if (spreadDistance < 0) {
            log.addDoubleTooSmall(3, 0).output();
            return 0;
        } else if (maxRange < spreadDistance) {
            log.addDoubleTooSmall(4, spreadDistance + 1).output();
            return 0;
        }
        for (Entity target : targets) {
            Vector3 vec3 = this.nextXZ(x, z, (int) maxRange);
            vec3.y = target.getLevel().getHighestBlockAt(vec3.getFloorX(), vec3.getFloorZ()) + 1;
            target.teleport(vec3);
        }
        log.addSuccess("commands.spreadplayers.success.players",
                String.valueOf(targets.size()), String.valueOf(Math.floor(x)), String.valueOf(Math.floor(z))).output();
        return 1;
    }

    private Vector3 nextXZ(double centerX, double centerZ, int maxRange) {
        Vector3 vec3 = new Vector3(centerX, 0, centerZ);
        vec3.x = Math.round(vec3.x) + this.random.nextInt(-maxRange, maxRange) + 0.5;
        vec3.z = Math.round(vec3.z) + this.random.nextInt(-maxRange, maxRange) + 0.5;
        return vec3;
    }
}
