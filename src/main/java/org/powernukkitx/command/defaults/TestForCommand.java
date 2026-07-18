package org.powernukkitx.command.defaults;

import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.command.tree.ParamList;
import org.powernukkitx.command.utils.CommandLogger;
import org.powernukkitx.entity.Entity;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TestForCommand extends VanillaCommand {

    public TestForCommand(String name) {
        super(name, "commands.testfor.description");
        this.setPermission("nukkit.command.testfor");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newType("victim", false, CommandParamType.SELECTION)
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        List<Entity> targets = result.getValue().getResult(0);

        if (targets.isEmpty()) {
            log.addNoTargetMatch().output();
            return 0;
        }

        List<String> names = targets.stream().map(entity -> {
            String name = entity.getName();
            if (name.isBlank()) name = entity.getOriginalName();
            return name;
        }).collect(Collectors.toList());

        log.addSuccess("commands.testfor.success", names).output();
        return targets.size();
    }

}
