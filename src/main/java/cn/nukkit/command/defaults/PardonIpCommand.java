package cn.nukkit.command.defaults;

import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.utils.CommandLogger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class PardonIpCommand extends VanillaCommand {

    public PardonIpCommand(String name) {
        super(name, "unban an IP");
        this.setPermission("nukkit.command.unban.ip");
        this.setAliases(new String[]{"unbanip", "unban-ip", "pardonip"});
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("ip", CommandParamType.STRING)
        });
        this.paramTree = new ParamTree(this);
    }

    @Since("1.19.50-r4")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        String value = result.getValue().getResult(0);
        if (Pattern.matches("^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$", value)) {
            sender.getServer().getIPBans().remove(value);
            try {
                sender.getServer().getNetwork().unblockAddress(InetAddress.getByName(value));
            } catch (UnknownHostException e) {
                log.addError("commands.unbanip.invalid").output();
                return 0;
            }
            log.addSuccess("commands.unbanip.success", value).output(true, true);
            return 1;
        } else {
            log.addError("commands.unbanip.invalid").output();
        }
        return 0;
    }
}
