package org.powernukkitx.command.defaults;

import org.powernukkitx.Player;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.command.tree.ParamList;
import org.powernukkitx.command.tree.node.PlayersNode;
import org.powernukkitx.command.utils.CommandLogger;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author iYozem
 */
public class TransferCommand extends VanillaCommand {

    public TransferCommand(String name) {
        super(name, "commands.transferserver.description");
        this.setPermission("nukkit.command.transfer.self;"
            + "nukkit.command.transfer.other");
        this.commandParameters.clear();
        this.commandParameters.put("self", new CommandParameter[]{
            CommandParameter.newType("ip", CommandParamType.ID),
            CommandParameter.newType("port", CommandParamType.INT)
        });
        this.commandParameters.put("target", new CommandParameter[]{
            CommandParameter.newType("player", CommandParamType.SELECTION, new PlayersNode()),
            CommandParameter.newType("ip", CommandParamType.ID),
            CommandParameter.newType("port", CommandParamType.INT)
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        String overload = result.getKey();
        ParamList list = result.getValue();

        switch (overload) {
            case "self" -> {
                if (!(sender instanceof Player player)) {
                    log.addMessage("nukkit.command.generic.ingame").output();
                    return 0;
                }
                if (!sender.hasPermission("nukkit.command.transfer.self")) {
                    log.addMessage("nukkit.command.generic.permission").output();
                    return 0;
                }
                String ip = list.getResult(0);
                int port = list.getResult(1);
                if (!isValidPort(port)) {
                    log.addError("commands.transferserver.invalid.port").output();
                    return 0;
                }
                player.transfer(new InetSocketAddress(ip, port));
                log.addSuccess("%commands.transferserver.successful (" + ip + ":" + port + ")").output(true);
                return 1;
            }
            case "target" -> {
                if (!sender.hasPermission("nukkit.command.transfer.other")) {
                    log.addMessage("%nukkit.command.generic.permission").output();
                    return 0;
                }
                List<Player> targets = list.getResult(0);
                if (targets.isEmpty()) {
                    log.addMessage("commands.generic.player.notFound").output();
                    return 0;
                }
                String ip = list.getResult(1);
                int port = list.getResult(2);
                if (!isValidPort(port)) {
                    log.addError("commands.transferserver.invalid.port").output();
                    return 0;
                }
                int success = 0;
                for (Player target : targets) {
                    target.transfer(new InetSocketAddress(ip, port));
                    success++;
                }
                log.addSuccess("%commands.transferserver.successful: " + targets.stream()
                    .map(t -> t.getViewableName(sender)).collect(Collectors.joining(", ")) + " (" + ip + ":" + port + ")")
                    .output(true);
                return success;
            }
            default -> {
                return 0;
            }
        }
    }

    private boolean isValidPort(int port) {
        return port >= 0 && port <= 65535;
    }
}
