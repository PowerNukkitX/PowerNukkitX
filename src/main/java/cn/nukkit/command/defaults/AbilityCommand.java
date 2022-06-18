package cn.nukkit.command.defaults;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.utils.CommandParser;
import cn.nukkit.lang.TranslationContainer;

import java.util.List;

public class AbilityCommand extends VanillaCommand {

    public AbilityCommand(String name) {
        super(name, "commands.ability.description", "%commands.ability.usage");
        this.setPermission("nukkit.command.ability");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", false, CommandParamType.TARGET),
                CommandParameter.newEnum("ability",false, new String[]{"mayfly","mute","worldbuilder"}),
                CommandParameter.newEnum("value", true,CommandEnum.ENUM_BOOLEAN)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this,sender,args);
        if(parser.matchCommandForm() == null){
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }

        try {
            List<Player> players = parser.parseTargetPlayers();
            if(players.size() == 0){
                sender.sendMessage(new TranslationContainer("commands.generic.noTargetMatch"));
                return false;
            }

            String ability_str;

            AdventureSettings.Type type = switch(ability_str = parser.parseString()){
                case "mayfly" -> AdventureSettings.Type.ALLOW_FLIGHT;
                case "mute" -> AdventureSettings.Type.MUTED;
                case "worldbuilder" -> AdventureSettings.Type.WORLD_BUILDER;
                default -> null;
            };

            if (parser.hasNext()) {
                boolean value = parser.parseBoolean();
                for (Player player : players) {
                    player.getAdventureSettings().set(type, value);
                    player.getAdventureSettings().update();
                    if (value)
                        player.sendMessage(new TranslationContainer("commands.ability.granted",ability_str));
                    else
                        player.sendMessage(new TranslationContainer("commands.ability.revoked",ability_str));
                }
                sender.sendMessage(new TranslationContainer("commands.ability.success"));
                return true;
            }else{
                if (!sender.isPlayer()){
                    return false;
                }
                boolean value = sender.asPlayer().getAdventureSettings().get(type);
                sender.sendMessage(ability_str + " = " + value);
                return true;
            }
        } catch (CommandSyntaxException e) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }
    }
}
