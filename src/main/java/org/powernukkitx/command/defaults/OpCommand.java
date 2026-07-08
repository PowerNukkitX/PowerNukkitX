package org.powernukkitx.command.defaults;

import org.powernukkitx.IPlayer;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.command.tree.ParamList;
import org.powernukkitx.command.tree.node.IPlayersNode;
import org.powernukkitx.command.utils.CommandLogger;
import org.powernukkitx.utils.TextFormat;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;

import java.util.List;
import java.util.Map;

/**
 * @author xtypr
 * @since 2015/11/12
 */
public class OpCommand extends VanillaCommand {

    public OpCommand(String name) {
        super(name, "commands.op.description");
        this.setPermission("nukkit.command.op.give");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.SELECTION, new IPlayersNode())
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        List<IPlayer> IPlayers = result.getValue().getResult(0);
        if (IPlayers.isEmpty()) {
            log.addNoTargetMatch().output();
            return 0;
        }

        for (IPlayer player : IPlayers) {
            if (player.isOp()) {
                log.addError("commands.op.failed", player.getName()).output();
            } else {
                player.setOp(true);
                if (player.isOnline()) {
                    log.outputObjectWhisper(player.getPlayer(), TextFormat.GRAY + "%commands.op.message");
                }
                log.addSuccess("commands.op.success", player.getName()).output(true);
            }
        }
        return IPlayers.size();
    }
}
