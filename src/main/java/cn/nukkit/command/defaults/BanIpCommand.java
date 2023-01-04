package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.tree.node.CommandNode;
import cn.nukkit.command.tree.node.IPStringNode;
import cn.nukkit.command.tree.node.StringNode;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BanIpCommand extends VanillaCommand {

    public BanIpCommand(String name) {
        super(name, "commands.banip.description", "%commands.banip.usage");
        this.setPermission("nukkit.command.ban.ip");
        this.setAliases(new String[]{"banip"});
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET, new StringNode()),
                CommandParameter.newType("reason", true, CommandParamType.STRING, new CommandNode())
        });
        this.commandParameters.put("byIp", new CommandParameter[]{
                CommandParameter.newType("ip", CommandParamType.STRING, new IPStringNode()),
                CommandParameter.newType("reason", true, CommandParamType.STRING, new CommandNode())
        });
        this.paramTree = new ParamTree(this);
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed resource leak")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        String reason = "";
        var list = result.getValue();
        switch (result.getKey()) {
            case "default" -> {
                String value = list.getResult(0);
                if (list.hasResult(1)) reason = list.getResult(1);
                Player player = sender.getServer().getPlayer(value);
                if (player != null) {
                    this.processIPBan(player.getAddress(), sender, reason);
                    log.outputSuccess(true, true, "commands.banip.success.players", player.getAddress(), player.getName());
                    return 1;
                } else {
                    String name = value.toLowerCase();
                    String path = sender.getServer().getDataPath() + "players/";
                    File file = new File(path + name + ".dat");
                    CompoundTag nbt = null;
                    if (file.exists()) {
                        try (FileInputStream inputStream = new FileInputStream(file)) {
                            nbt = NBTIO.readCompressed(inputStream);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    }

                    if (nbt != null && nbt.contains("lastIP") && Pattern.matches("^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$", (value = nbt.getString("lastIP")))) {
                        this.processIPBan(value, sender, reason);
                        log.outputSuccess(true, true, "commands.banip.success", value);
                        return 1;
                    } else {
                        log.outputError("commands.banip.invalid");
                        return 0;
                    }
                }
            }
            case "byIp" -> {
                String ip = list.getResult(0);
                if (list.hasResult(1)) reason = list.getResult(1);
                this.processIPBan(ip, sender, reason);
                log.outputSuccess(true, true, "commands.banip.success", ip);
            }
            default -> {
                return 0;
            }
        }
        return 0;
    }

    private void processIPBan(String ip, CommandSender sender, String reason) {
        sender.getServer().getIPBans().addBan(ip, reason, null, sender.getName());

        for (Player player : new ArrayList<>(sender.getServer().getOnlinePlayers().values())) {
            if (player.getAddress().equals(ip)) {
                player.kick(PlayerKickEvent.Reason.IP_BANNED, !reason.isEmpty() ? reason : "IP banned");
            }
        }

        try {
            sender.getServer().getNetwork().blockAddress(InetAddress.getByName(ip), -1);
        } catch (UnknownHostException e) {
            // ignore
        }
    }
}
