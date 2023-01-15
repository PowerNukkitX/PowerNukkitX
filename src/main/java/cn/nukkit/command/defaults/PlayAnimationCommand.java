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
import cn.nukkit.network.protocol.AnimateEntityPacket;

import java.util.List;
import java.util.Map;

@PowerNukkitXOnly
@Since("1.19.50-r3")
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
        this.paramTree = new ParamTree(this);
    }

    @Since("1.19.50-r4")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        List<Entity> entities = list.getResult(0);
        var animationBuilder = AnimateEntityPacket.Animation.builder();
        String animation = list.getResult(1);
        animationBuilder.animation(animation);
        //optional
        if (list.hasResult(2)) {
            String next_state = list.getResult(2);
            animationBuilder.nextState(next_state);
        }
        if (list.hasResult(3)) {
            float blend_out_time = list.getResult(3);
            animationBuilder.blendOutTime(blend_out_time);
        }
        if (list.hasResult(4)) {
            String stop_expression = list.getResult(4);
            animationBuilder.stopExpression(stop_expression);
        }
        if (list.hasResult(5)) {
            String controller = list.getResult(5);
            animationBuilder.controller(controller);
        }
        //send animation
        Entity.playAnimationOnEntities(animationBuilder.build(), entities);
        log.addSuccess("commands.playanimation.success").output();
        return 1;
    }
}
