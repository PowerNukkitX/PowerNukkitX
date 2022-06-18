package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.utils.CommandParser;
import cn.nukkit.entity.Entity;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SpreadPlayersCommand extends VanillaCommand {

    private final ThreadLocalRandom random;

    public SpreadPlayersCommand(String name) {
        super(name, "commands.spreadplayers.description");
        this.setPermission("nukkit.command.spreadplayers");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newType("x",false, CommandParamType.VALUE),
                CommandParameter.newType("z",false, CommandParamType.VALUE),
                CommandParameter.newType("spreadDistance",false, CommandParamType.FLOAT),
                CommandParameter.newType("maxRange",false, CommandParamType.FLOAT),
                CommandParameter.newType("victim",false, CommandParamType.TARGET)
        });
        this.random = ThreadLocalRandom.current();
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        try {
            Vector2 vec2 = parser.parseVector2();
            float spreadDistance = (float) parser.parseDouble(); //TODO
            float maxRange = (float) parser.parseDouble();
            List<Entity> targets = parser.parseTargets();

            if (spreadDistance < 0) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.double.tooSmall", String.valueOf(spreadDistance), "0"));
                return false;
            } else if (maxRange < spreadDistance) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.double.tooSmall", String.valueOf(maxRange), String.valueOf(spreadDistance + 1)));
                return false;
            }

            if (targets.size() == 0) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.noTargetMatch"));
                return false;
            }

            for (Entity target : targets) {
                Vector3 vec3 = this.nextXZ(vec2.getX(), vec2.getY(), (int) maxRange);
                vec3.y = target.getLevel().getHighestBlockAt(vec3.getFloorX(), vec3.getFloorZ()) + 1;
                target.teleport(vec3);
            }

            sender.sendMessage(new TranslationContainer("commands.spreadplayers.success.players",String.valueOf(targets.size()), String.valueOf(vec2.getFloorX()), String.valueOf(vec2.getFloorY())));
        } catch (CommandSyntaxException e) {
             sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }

        return true;
    }

    private Vector3 nextXZ(double centerX, double centerZ, int maxRange) {
        Vector3 vec3 = new Vector3(centerX, 0, centerZ);
        vec3.x = Math.round(vec3.x) + this.random.nextInt(-maxRange, maxRange) + 0.5;
        vec3.z = Math.round(vec3.z) + this.random.nextInt(-maxRange, maxRange) + 0.5;
        return vec3;
    }
}
