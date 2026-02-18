package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.entity.Entity;

import java.util.List;
import java.util.Map;
import java.util.Objects;


public class PlayAnimationCommand extends VanillaCommand {
    public PlayAnimationCommand(String name) {
        super(name, "commands.playanimation.description");
        this.setPermission("nukkit.command.playanimation");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("entity", CommandParamType.TARGET),
                CommandParameter.newType("animation", CommandParamType.STRING),
                CommandParameter.newType("next_state", true, CommandParamType.STRING),
                CommandParameter.newType("blend_out_time", true, CommandParamType.FLOAT),
                CommandParameter.newType("stop_expression", true, CommandParamType.STRING),
                CommandParameter.newType("controller", true, CommandParamType.STRING),
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        List<Entity> entities = list.getResult(0);
        entities = entities.stream().filter(Objects::nonNull).toList();
        if (entities.isEmpty()) {
            log.addNoTargetMatch().output();
            return 0;
        }
        String animation = list.getResult(1);
        Entity.playAnimationOnEntities(animation, entities);
        log.addSuccess("commands.playanimation.success").output();
        return 1;
    }
}
