package org.powernukkitx.command.defaults;

import org.powernukkitx.Server;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandEnum;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.command.tree.ParamList;
import org.powernukkitx.command.utils.CommandLogger;
import org.powernukkitx.level.Level;
import org.powernukkitx.utils.TextFormat;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;

import java.util.Map;


public class WorldCommand extends VanillaCommand {
    public static final CommandEnum WORLD_NAME_ENUM = new CommandEnum("world", () -> Server.getInstance().getLevels().values().stream().map(Level::getName).toList());

    public WorldCommand(String name) {
        super(name, "nukkit.command.world.description");
        this.setPermission("nukkit.command.world");
        this.commandParameters.clear();
        this.commandParameters.put("tp",
                new CommandParameter[]{
                        CommandParameter.newEnum("tp", new String[]{"tp"}),
                        CommandParameter.newEnum("world", false, WORLD_NAME_ENUM)
                });
        this.commandParameters.put("list",
                new CommandParameter[]{
                        CommandParameter.newEnum("list", new String[]{"list"})
                });
        this.commandParameters.put("load",
                new CommandParameter[]{
                        CommandParameter.newEnum("load", new String[]{"load"}),
                        CommandParameter.newType("world", false, CommandParamType.MESSAGE)
                });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        switch (result.getKey()) {
            case "list" -> {
                var strBuilder = new StringBuilder();
                Server.getInstance().getLevels().values().forEach(level -> {
                    strBuilder.append(level.getName());
                    strBuilder.append(", ");
                });
                log.addMessage(TextFormat.WHITE + "%nukkit.command.world.availableLevels", strBuilder.toString()).output();
                return 1;
            }
            case "load" -> {
                String levelName = result.getValue().getResult(1);
                if (Server.getInstance().getLevelByName(levelName) != null) {
                    sender.sendMessage(TextFormat.YELLOW + "World '" + levelName + "' is already loaded");
                    return 1;
                }
                if (Server.getInstance().loadLevel(levelName)) {
                    sender.sendMessage(TextFormat.GREEN + "World '" + levelName + "' loaded");
                    return 1;
                }
                log.addMessage("nukkit.command.world.levelNotFound", levelName).output();
                return 0;
            }
            case "tp" -> {
                if (!sender.isPlayer()) {
                    sender.sendMessage(TextFormat.RED + "Only a player can teleport between worlds");
                    return 0;
                }
                String levelName = result.getValue().getResult(1);
                var level = Server.getInstance().getLevelByName(levelName);
                if (level == null) {
                    if (Server.getInstance().loadLevel(levelName)) {
                        level = Server.getInstance().getLevelByName(levelName);
                    } else {
                        log.addMessage("nukkit.command.world.levelNotFound", levelName).output();
                        return 0;
                    }
                }
                if (level == null) {
                    log.addMessage("nukkit.command.world.levelNotFound", levelName).output();
                    return 0;
                }
                sender.asPlayer().teleport(level.getSafeSpawn());
                log.addMessage(TextFormat.WHITE + "%nukkit.command.world.successTp", levelName).output();
                return 1;
            }
            default -> {
                return 0;
            }
        }
    }
}
