package cn.nukkit.command.defaults;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.level.Level;
import cn.nukkit.utils.TextFormat;

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
            case "tp" -> {
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
                sender.asEntity().teleport(level.getSafeSpawn());
                log.addMessage(TextFormat.WHITE + "%nukkit.command.world.successTp", levelName).output();
                return 1;
            }
            default -> {
                return 0;
            }
        }
    }
}
