package org.powernukkitx.command.defaults;

import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.command.tree.ParamList;
import org.powernukkitx.command.utils.CommandLogger;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityAnimation;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;

import java.util.List;
import java.util.Map;
import java.util.Objects;


public class PlayAnimationCommand extends VanillaCommand {
    public PlayAnimationCommand(String name) {
        super(name, "commands.playanimation.description");
        this.setPermission("nukkit.command.playanimation");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("entity", CommandParamType.SELECTION),
                CommandParameter.newType("animation", CommandParamType.ID),
                CommandParameter.newType("next_state", true, CommandParamType.ID),
                CommandParameter.newType("blend_out_time", true, CommandParamType.FLOAT),
                CommandParameter.newType("stop_expression", true, CommandParamType.ID),
                CommandParameter.newType("controller", true, CommandParamType.ID),
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
        var animationBuilder = EntityAnimation.builder();
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
