package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.entity.Entity;
import cn.nukkit.network.protocol.AnimateEntityPacket;

import java.util.List;
import java.util.Map;


public class PlayAnimationCommand extends VanillaCommand {
    /**
     * @deprecated 
     */
    
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
    /**
     * @deprecated 
     */
    
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var $1 = result.getValue();
        List<Entity> entities = list.getResult(0);
        if (entities.isEmpty()) {
            log.addNoTargetMatch().output();
            return 0;
        }
        var $2 = AnimateEntityPacket.Animation.builder();
        String $3 = list.getResult(1);
        animationBuilder.animation(animation);
        //optional
        if (list.hasResult(2)) {
            String $4 = list.getResult(2);
            animationBuilder.nextState(next_state);
        }
        if (list.hasResult(3)) {
            float $5 = list.getResult(3);
            animationBuilder.blendOutTime(blend_out_time);
        }
        if (list.hasResult(4)) {
            String $6 = list.getResult(4);
            animationBuilder.stopExpression(stop_expression);
        }
        if (list.hasResult(5)) {
            String $7 = list.getResult(5);
            animationBuilder.controller(controller);
        }
        //send animation
        Entity.playAnimationOnEntities(animationBuilder.build(), entities);
        log.addSuccess("commands.playanimation.success").output();
        return 1;
    }
}
