package cn.nukkit.command.defaults;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.entity.Entity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class TestForCommand extends VanillaCommand {

    public TestForCommand(String name) {
        super(name, "commands.testfor.description");
        this.setPermission("nukkit.command.testfor");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newType("victim", false, CommandParamType.TARGET)
        });
        this.enableParamTree();
    }

    @Since("1.19.60-r1")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        List<Entity> targets = result.getValue().getResult(0);
        if (targets.isEmpty()) {
            log.addNoTargetMatch().output();
            return 0;
        } else {
            log.addSuccess("commands.testfor.success", targets.stream().map(entity -> {
                var name = entity.getName();
                if (name.isBlank()) name = entity.getSaveId();
                return name;
            }).collect(Collectors.joining(","))).output();
            return targets.size();
        }
    }
}
