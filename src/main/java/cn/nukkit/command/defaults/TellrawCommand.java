package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.utils.CommandParser;
import cn.nukkit.command.utils.RawText;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;
import com.google.gson.JsonSyntaxException;

import java.util.List;

public class TellrawCommand extends VanillaCommand{

    public TellrawCommand(String name){
        super(name, "commands.tellraw.description");
        this.setPermission("nukkit.command.tellraw");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newType("rawtext", CommandParamType.RAWTEXT)
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
            String rawText = parser.parseString();
            RawText rawTextObject = RawText.fromRawText(rawText);
            rawTextObject.preParse(sender);
            for (Player player : players) {
                player.sendRawTextMessage(rawTextObject);
            }
        } catch (CommandSyntaxException e) {
            return false;
        } catch (JsonSyntaxException e) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.tellraw.jsonStringException"));
            return false;
        }
        return true;
    }
}
