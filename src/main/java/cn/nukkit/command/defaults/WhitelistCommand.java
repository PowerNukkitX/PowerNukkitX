package cn.nukkit.command.defaults;

import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.tree.node.StringNode;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.utils.TextFormat;

import java.util.Map;

/**
 * @author xtypr
 * @since 2015/11/12
 */
public class WhitelistCommand extends VanillaCommand {

    public WhitelistCommand(String name) {
        super(name, "nukkit.command.whitelist.description", "nukkit.command.allowlist.usage", new String[]{"allowlist"}); // In Minecraft Bedrock v1.18.10 the whitelist was renamed to allowlist
        this.setPermission(
                "nukkit.command.whitelist.reload;" +
                        "nukkit.command.whitelist.enable;" +
                        "nukkit.command.whitelist.disable;" +
                        "nukkit.command.whitelist.list;" +
                        "nukkit.command.whitelist.add;" +
                        "nukkit.command.whitelist.remove;" +
                        //v1.18.10+
                        "nukkit.command.allowlist.reload;" +
                        "nukkit.command.allowlist.enable;" +
                        "nukkit.command.allowlist.disable;" +
                        "nukkit.command.allowlist.list;" +
                        "nukkit.command.allowlist.add;" +
                        "nukkit.command.allowlist.remove"
        );
        this.commandParameters.clear();
        this.commandParameters.put("1arg", new CommandParameter[]{
                CommandParameter.newEnum("action", new CommandEnum("AllowlistAction", "on", "off", "list", "reload"))
        });
        this.commandParameters.put("2args", new CommandParameter[]{
                CommandParameter.newEnum("action", new CommandEnum("AllowlistPlayerAction", "add", "remove")),
                CommandParameter.newType("player", CommandParamType.TARGET, new StringNode())
        });
        this.paramTree = new ParamTree(this);
    }

    @Since("1.19.50-r4")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        switch (result.getKey()) {
            case "1arg" -> {
                String action = list.getResult(0);
                if (this.badPerm(log, sender, action.toLowerCase())) {
                    return 0;
                }
                switch (action.toLowerCase()) {
                    case "reload" -> {
                        sender.getServer().reloadWhitelist();
                        log.addSuccess("commands.allowlist.reloaded").output(true, true);
                        return 1;
                    }
                    case "on" -> {
                        sender.getServer().setPropertyBoolean("white-list", true);
                        log.addSuccess("commands.allowlist.enabled").output(true, true);
                        return 1;
                    }
                    case "off" -> {
                        sender.getServer().setPropertyBoolean("white-list", false);
                        log.addSuccess("commands.allowlist.disabled").output(true, true);
                        return 1;
                    }
                    case "list" -> {
                        StringBuilder re = new StringBuilder();
                        int count = 0;
                        for (String player : sender.getServer().getWhitelist().getAll().keySet()) {
                            re.append(player).append(", ");
                            ++count;
                        }
                        log.addSuccess("commands.allowlist.list", String.valueOf(count), String.valueOf(count));
                        log.addSuccess(re.length() > 0 ? re.substring(0, re.length() - 2) : "").output();
                        return 1;
                    }
                }
            }
            case "2args" -> {
                String action = list.getResult(0);
                String name = list.getResult(1);
                if (this.badPerm(log, sender, action.toLowerCase())) {
                    return 0;
                }
                switch (action.toLowerCase()) {
                    case "add" -> {
                        sender.getServer().getOfflinePlayer(name).setWhitelisted(true);
                        log.addSuccess("commands.allowlist.add.success", name).output(true, true);
                        return 1;
                    }
                    case "remove" -> {
                        sender.getServer().getOfflinePlayer(name).setWhitelisted(false);
                        log.addSuccess("commands.allowlist.remove.success", name).output(true, true);
                        return 1;
                    }
                }
            }
            default -> {
                return 0;
            }
        }
        return 1;
    }

    private boolean badPerm(CommandLogger log, CommandSender sender, String perm) {
        if (!sender.hasPermission("nukkit.command.whitelist." + perm) && !sender.hasPermission("nukkit.command.allowlist." + perm)) {
            log.addMessage(TextFormat.RED + "%nukkit.command.generic.permission").output();
            return true;
        }
        return false;
    }
}
