package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.tree.node.CommandNode;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.event.player.PlayerKickEvent;

import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BanCommand extends VanillaCommand {

    public BanCommand(String name) {
        super(name, "commands.ban.description", "%commands.ban.usage");
        this.setPermission("nukkit.command.ban.player");
        this.commandParameters.clear();
        this.commandParameters.put("default",
                new CommandParameter[]{
                        CommandParameter.newType("player", CommandParamType.STRING),
                        CommandParameter.newType("reason", true, CommandParamType.STRING, new CommandNode())
                });
        this.paramTree = new ParamTree(this);
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        if (result.getKey().equals("default")) {
            var list = result.getValue();
            String name = list.getResult(0);
            String reason = list.getResult(1);
            sender.getServer().getNameBans().addBan(name, reason, null, sender.getName());

            Player player = sender.getServer().getPlayerExact(name);
            if (player != null) {
                player.kick(PlayerKickEvent.Reason.NAME_BANNED, (reason.length() > 0) ? "Banned by admin. Reason: " + reason : "Banned by admin");
            }
            log.addSuccess("commands.ban.success", player != null ? player.getName() : name).output(true, true);
            return 1;
        }
        return 0;
    }
}
