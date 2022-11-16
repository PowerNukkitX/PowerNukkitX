package cn.nukkit.command.defaults;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.command.utils.CommandParser;
import cn.nukkit.command.utils.EntitySelector;
import cn.nukkit.lang.TranslationContainer;

import java.util.List;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class AbilityCommand extends VanillaCommand {

    public AbilityCommand(String name) {
        super(name, "commands.ability.description", "%commands.ability.usage");
        this.setPermission("nukkit.command.ability");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", false, CommandParamType.TARGET),
                CommandParameter.newEnum("ability", false, new String[]{"mayfly", "mute", "worldbuilder"}),
                CommandParameter.newEnum("value", true, CommandEnum.ENUM_BOOLEAN)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        CommandLogger log = new CommandLogger(this, sender, args);
        if (args.length == 0) {
            log.outputSyntaxErrors(-1);
        }

        if (args.length == 1) {
            log.outputSyntaxErrors(1);
            return false;
        }

        List<Player> players = EntitySelector.matchPlayers(sender, args[0]);
        if (players.size() == 0) {
            log.outputNoTargetMatch();
            return false;
        }

        String ability_str;
        AdventureSettings.Type type;
        try {
            ability_str = parser.parseString();
            type = switch (ability_str) {
                case "mayfly" -> AdventureSettings.Type.ALLOW_FLIGHT;
                case "mute" -> AdventureSettings.Type.MUTED;
                case "worldbuilder" -> AdventureSettings.Type.WORLD_BUILDER;
                default -> null;
            };
        } catch (CommandSyntaxException e) {
            log.outputSyntaxErrors(1);
            return false;
        }

        if (type == null || ability_str.isBlank()) log.outputSyntaxErrors(1);

        if (parser.hasNext()) {
            boolean value;
            try {
                value = parser.parseBoolean();
            } catch (CommandSyntaxException e) {
                log.outputSyntaxErrors(2);
                return false;
            }

            for (Player player : players) {
                player.getAdventureSettings().set(type, value);
                player.getAdventureSettings().update();
                if (value)
                    log.outputInfo(null, null, "commands.ability.granted", new String[]{ability_str});
                else
                    log.outputInfo(null, null, "commands.ability.revoked", new String[]{ability_str});
            }
            log.outputInfo(null, null, "commands.ability.success", TranslationContainer.EMPTY_STRING_ARRAY);
        } else {
            if (!sender.isPlayer()) {
                return false;
            }
            boolean value = sender.asPlayer().getAdventureSettings().get(type);
            log.outputInfo(null, null, "ability_str = " + value, TranslationContainer.EMPTY_STRING_ARRAY);
        }
        return true;
    }
}
