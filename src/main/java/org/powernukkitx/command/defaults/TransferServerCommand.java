package org.powernukkitx.command.defaults;

import org.powernukkitx.Player;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.ConsoleCommandSender;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.command.tree.ParamList;
import org.powernukkitx.command.utils.CommandLogger;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

/**
 * @author iYozem
 */
public class TransferServerCommand extends VanillaCommand {

    public TransferServerCommand(String name) {
        super(name, "commands.transferserver.description");
        this.setPermission("nukkit.command.transferserver");

        this.commandParameters.clear();

        this.commandParameters.put("self", new CommandParameter[]{
            CommandParameter.newType("ip", CommandParamType.ID),
            CommandParameter.newType("port", CommandParamType.INT)
        });

        this.commandParameters.put("target", new CommandParameter[]{
            CommandParameter.newType("player", CommandParamType.SELECTION),
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
                    log.addMessage("commands.generic.ingame");
                    log.output();
                    return 0;
                }

                String ip = list.getResult(0);
                int port = list.getResult(1);

                InetSocketAddress address = resolveAddress(ip, port);
                if (address == null) {
                    log.addMessage("commands.transferserver.invalid.port");
                    log.output();
                    return 0;
                }

                player.transferring = true;
                player.transfer(address);
                log.addSuccess("commands.transferserver.successful");
                log.output();
                return 1;
            }
            case "target" -> {
                List<Player> targets = list.getResult(0);
                if (targets.isEmpty()) {
                    log.addMessage("commands.generic.player.notFound");
                    log.output();
                    return 0;
                }
                Player target = targets.getFirst();

                String ip = list.getResult(1);
                int port = list.getResult(2);

                InetSocketAddress address = resolveAddress(ip, port);
                if (address == null) {
                    log.addMessage("commands.transferserver.invalid.port");
                    log.output();
                    return 0;
                }

                target.transferring = true;
                target.transfer(address);
                log.addSuccess("commands.transferserver.successful");
                log.output();
                return 1;
            }
            default -> {
                return 0;
            }
        }
    }

    private InetSocketAddress resolveAddress(String ip, int port) {
        try {
            InetSocketAddress address = new InetSocketAddress(ip, port);
            return address.isUnresolved() ? null : address;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
