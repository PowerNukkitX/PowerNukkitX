package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.permission.BanEntry;
import cn.nukkit.permission.BanList;

import java.util.Iterator;

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
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandLogger log = new CommandLogger(this, sender, args);

        BanList list;
        boolean ips = false;
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "ips" -> {
                    list = sender.getServer().getIPBans();
                    ips = true;
                }
                case "players" -> list = sender.getServer().getNameBans();
                default -> {
                    log.outputSyntaxErrors(0);
                    return false;
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

        if (ips) {
            log.outputInfo(null, null, "commands.banlist.ips", new String[]{String.valueOf(list.getEntires().size())});
        } else {
            log.outputInfo(null, null, "commands.banlist.players", new String[]{String.valueOf(list.getEntires().size())});
        }
        log.outputInfo(null, null, builder.toString(), TranslationContainer.EMPTY_STRING_ARRAY);
        return true;
    }
}
