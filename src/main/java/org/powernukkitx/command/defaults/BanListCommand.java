package org.powernukkitx.command.defaults;

import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandEnum;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.command.tree.ParamList;
import org.powernukkitx.command.utils.CommandLogger;
import org.powernukkitx.permission.BanEntry;
import org.powernukkitx.permission.BanList;
import org.powernukkitx.utils.TextFormat;

import java.util.Iterator;
import java.util.Locale;
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
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var paramList = result.getValue();
        BanList list;
        boolean ips = false;

        if (paramList.hasResult(0)) {
            String type = paramList.getResult(0);
            switch (type.toLowerCase(Locale.ENGLISH)) {
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
