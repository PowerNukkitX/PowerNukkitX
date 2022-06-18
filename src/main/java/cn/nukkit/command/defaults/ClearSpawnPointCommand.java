package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.utils.CommandParser;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Position;
import cn.nukkit.utils.TextFormat;

import java.util.List;
import java.util.stream.Collectors;

public class ClearSpawnPointCommand extends VanillaCommand {

    public ClearSpawnPointCommand(String name){
        super(name,"commands.clearspawnpoint.description");
        this.setPermission("nukkit.command.clearspawnpoint");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newType("player",true, CommandParamType.TARGET),
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
            List<Player> players;
            if (parser.hasNext()) {
                players = parser.parseTargetPlayers();
            }else if (sender.isPlayer()){
                players = List.of(sender.asPlayer());
            }else{
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.noTargetMatch"));
                return false;
            }
            Position spawn = Server.getInstance().getDefaultLevel().getSpawnLocation();
            for (Player player : players){
                player.setSpawn(spawn);
            }
            String players_str = players.stream().map(p -> p.getName()).collect(Collectors.joining(" "));
            if (players.size() > 1){
                sender.sendMessage(new TranslationContainer("commands.clearspawnpoint.success.multiple", players_str));
            }else{
                sender.sendMessage(new TranslationContainer("commands.clearspawnpoint.success.single", players_str));
            }
            return true;
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }
}
