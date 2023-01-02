package cn.nukkit.command.defaults;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.tree.node.BooleanNode;
import cn.nukkit.command.tree.node.PlayersNode;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.utils.TextFormat;

import java.util.List;
import java.util.Map;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class AbilityCommand extends VanillaCommand {

    public AbilityCommand(String name) {
        super(name, "commands.ability.description", "%commands.ability.usage");
        this.setPermission("nukkit.command.ability");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", false, CommandParamType.TARGET, new PlayersNode()),
                CommandParameter.newEnum("ability", false, new String[]{"mayfly", "mute", "worldbuilder"}),
                CommandParameter.newEnum("value", true, CommandEnum.ENUM_BOOLEAN, new BooleanNode())
        });
        this.paramTree = new ParamTree(this);
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        if (!this.testPermission(sender)) {
            return 0;
        }
        if (result.getKey().equals("default")) {
            var list = result.getValue();
            List<Player> players = list.getResult(0);
            if (players.size() == 0) {
                log.outputNoTargetMatch();
                return 0;
            }
            String ability_str;
            AdventureSettings.Type type = switch (ability_str = list.getResult(1)) {
                case "mayfly" -> AdventureSettings.Type.ALLOW_FLIGHT;
                case "mute" -> AdventureSettings.Type.MUTED;
                case "worldbuilder" -> AdventureSettings.Type.WORLD_BUILDER;
                default -> null;
            };

            if (list.hasResult(2)) {
                boolean value = list.getResult(2);
                for (Player player : players) {
                    player.getAdventureSettings().set(type, value);
                    player.getAdventureSettings().update();
                    if (value)
                        log.addMessage(TextFormat.WHITE + "%commands.ability.granted", ability_str);
                    else
                        log.addMessage(TextFormat.WHITE + "%commands.ability.revoked", ability_str);
                }
                log.addMessage(TextFormat.WHITE + "%commands.ability.success").successCount(1).output();
                return 1;
            } else {
                if (!sender.isPlayer()) {
                    return 0;
                }
                boolean value = sender.asPlayer().getAdventureSettings().get(type);
                log.outputSuccess(TextFormat.WHITE + ability_str + " = " + value);
                return 1;
            }
        }
        return 0;
    }
}
