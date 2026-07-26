package org.powernukkitx.command.defaults;

import org.powernukkitx.Player;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.command.tree.ParamList;
import org.powernukkitx.command.tree.node.PlayersNode;
import org.powernukkitx.command.utils.CommandLogger;
import org.powernukkitx.command.utils.RawText;
import org.powernukkitx.lang.TranslationContainer;
import org.powernukkitx.utils.TextFormat;
import com.google.gson.JsonSyntaxException;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;

import java.util.List;
import java.util.Map;
import java.util.Objects;


public class TellrawCommand extends VanillaCommand {

    public TellrawCommand(String name) {
        super(name, "commands.tellraw.description");
        this.setPermission("nukkit.command.tellraw");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.SELECTION, new PlayersNode()),
                CommandParameter.newType("rawtext", CommandParamType.RAW_TEXT)
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        try {
            List<Player> players = list.getResult(0);
            players = players.stream().filter(Objects::nonNull).toList();
            if (players.isEmpty()) {
                log.addNoTargetMatch().output();
                return 0;
            }
            RawText rawTextObject = list.getResult(1);
            rawTextObject.preParse(sender);
            for (Player player : players) {
                player.sendRawTextMessage(rawTextObject);
            }
            return 1;
        } catch (JsonSyntaxException e) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.tellraw.jsonStringException"));
            return 0;
        }
    }
}
