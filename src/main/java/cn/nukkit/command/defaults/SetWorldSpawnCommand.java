package cn.nukkit.command.defaults;

import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * @author xtypr
 * @since 2015/12/13
 */
public class SetWorldSpawnCommand extends VanillaCommand {
    public SetWorldSpawnCommand(String name) {
        super(name, "commands.setworldspawn.description");
        this.setPermission("nukkit.command.setworldspawn");
        this.commandParameters.clear();
        this.commandParameters.put("default", CommandParameter.EMPTY_ARRAY);
        this.commandParameters.put("spawnPoint", new CommandParameter[]{
                CommandParameter.newType("spawnPoint", true, CommandParamType.POSITION)
        });
        this.paramTree = new ParamTree(this);
    }

    @Since("1.19.50-r4")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        Level level;
        Vector3 pos;
        if (!result.getValue().hasResult(0)) {
            level = sender.getPosition().level;
            pos = sender.getPosition().round();
        } else {
            level = sender.getServer().getDefaultLevel();
            pos = result.getValue().getResult(0);
        }
        level.setSpawnLocation(pos);
        DecimalFormat round2 = new DecimalFormat("##0.00");
        log.addSuccess("commands.setworldspawn.success", round2.format(pos.x),
                round2.format(pos.y),
                round2.format(pos.z)).output(true, true);
        return 1;
    }
}
