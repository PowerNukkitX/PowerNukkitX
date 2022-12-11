package cn.nukkit.command.defaults;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.utils.CommandParser;
import cn.nukkit.entity.Entity;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.network.protocol.AnimateEntityPacket;

@PowerNukkitXOnly
@Since("1.19.50-r3")
public class PlayAnimationCommand extends VanillaCommand{
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
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        if (parser.matchCommandForm() == null) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }

        try {
            var entities = parser.parseTargets();
            var animationBuilder = AnimateEntityPacket.Animation.builder();
            animationBuilder.animation(parser.parseString());

            //optional
            if (parser.hasNext()) animationBuilder.nextState(parser.parseString());
            if (parser.hasNext()) animationBuilder.blendOutTime((float) parser.parseDouble());
            if (parser.hasNext()) animationBuilder.stopExpression(parser.parseString());
            if (parser.hasNext()) animationBuilder.controller(parser.parseString());

            //send animation
            Entity.playAnimationOnEntities(animationBuilder.build(), entities);

            sender.sendMessage(new TranslationContainer("commands.playanimation.success"));
            return true;
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }

        return false;
    }
}
