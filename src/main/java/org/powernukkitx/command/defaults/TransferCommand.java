package org.powernukkitx.command.defaults;

import org.powernukkitx.Player;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.command.tree.ParamList;
import org.powernukkitx.command.utils.CommandLogger;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;

import java.util.List;
import java.util.Map;

/**
 * @author iYozem
 */
public class TransferCommand extends VanillaCommand {

    public TransferCommand(String name) {
        super(name, "commands.transfer.description");
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
                    log.addMessage("nukkit.command.generic.ingame");
                    log.output();
                    return 0;
                }

                String ip = list.getResult(0);
                int port = list.getResult(1);

                if (!isValidPort(port)) {
                    log.addMessage("commands.transfer.invalid.port");
                    log.output();
                    return 0;
                }

                player.transfer(ip, port);
                log.addSuccess("commands.transfer.successful");
                log.output();
                return 1;
            }
            case "target" -> {
                List<?> rawTargets = list.getResult(0);
                if (rawTargets.isEmpty()) {
                    log.addMessage("commands.generic.player.notFound");
                    log.output();
                    return 0;
                }

                Player target = null;
                for (Object obj : rawTargets) {
                    if (obj instanceof Player p) {
                        target = p;
                        break;
                    }
                }

                if (target == null) {
                    log.addMessage("commands.generic.player.notFound");
                    log.output();
                    return 0;
                }

                String ip = list.getResult(1);
                int port = list.getResult(2);

                if (!isValidPort(port)) {
                    log.addMessage("commands.transfer.invalid.port");
                    log.output();
                    return 0;
                }

                target.transfer(ip, port);
                log.addSuccess("commands.transfer.successful");
                log.output();
                return 1;
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
