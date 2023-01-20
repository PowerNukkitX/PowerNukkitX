package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.permission.BanEntry;
import cn.nukkit.permission.BanList;
import cn.nukkit.utils.TextFormat;

import java.util.Iterator;
import java.util.Map;

/**
 * @author xtypr
 * @since 2015/11/11
 */
public class BanListCommand extends VanillaCommand {
    public BanListCommand(String name) {
        super(name, "list all the banned players or IPs");
        this.setPermission("nukkit.command.ban.list");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newEnum("type", true, new CommandEnum("BanListType", "ips", "players"))
        });
        this.paramTree = new ParamTree(this);
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var paramList = result.getValue();
        BanList list;
        boolean ips = false;

        if (paramList.hasResult(0)) {
            String type = paramList.getResult(0);
            switch (type.toLowerCase()) {
                case "ips" -> {
                    list = sender.getServer().getIPBans();
                    ips = true;
                }
                case "players" -> list = sender.getServer().getNameBans();
                default -> {
                    log.addSyntaxErrors(0).output();
                    return 0;
                }
            }
        } else {
            list = sender.getServer().getNameBans();
        }

        StringBuilder builder = new StringBuilder();
        Iterator<BanEntry> itr = list.getEntires().values().iterator();
        while (itr.hasNext()) {
            builder.append(itr.next().getName());
            if (itr.hasNext()) {
                builder.append(", ");
            }
        }
        int size = list.getEntires().size();
        if (ips) {
            log.addSuccess("commands.banlist.ips", String.valueOf(size));
        } else {
            log.addSuccess("commands.banlist.players", String.valueOf(size));
        }
        log.addSuccess(TextFormat.GREEN + builder.toString()).successCount(size).output();
        return size;
    }
}
