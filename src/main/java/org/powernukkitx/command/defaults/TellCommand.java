package org.powernukkitx.command.defaults;

import org.powernukkitx.Player;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.command.tree.ParamList;
import org.powernukkitx.command.tree.node.PlayersNode;
import org.powernukkitx.command.utils.CommandLogger;
import org.powernukkitx.lang.TranslationContainer;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author xtypr
 * @since 2015/11/12
 */
public class TellCommand extends VanillaCommand {

    public TellCommand(String name) {
        super(name, "commands.tell.description", "", new String[]{"w", "msg"});
        this.setPermission("nukkit.command.tell");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.SELECTION, new PlayersNode()),
                CommandParameter.newType("message", CommandParamType.MESSAGE)
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        List<Player> players = list.getResult(0);
        players = players.stream().filter(Objects::nonNull).toList();
        if (players.isEmpty()) {
            log.addNoTargetMatch().output();
            return 0;
        }
        String msg = list.getResult(1);
        for (Player player : players) {
            if (player == sender) {
                log.addError("commands.message.sameTarget").output();
                continue;
            }
            log.addSuccess("commands.message.display.outgoing", player.getViewableName(sender), msg);
            player.sendMessage(new TranslationContainer("commands.message.display.incoming",
                    sender.getViewableName(player), msg));
        }
        log.output();
        return 1;
    }
}
