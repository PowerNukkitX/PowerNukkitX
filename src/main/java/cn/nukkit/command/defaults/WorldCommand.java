package cn.nukkit.command.defaults;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.level.Dimension;
import cn.nukkit.level.Level;
import cn.nukkit.utils.TextFormat;

import java.util.Map;


public class WorldCommand extends VanillaCommand {
    public static final CommandEnum WORLD_NAME_ENUM = new CommandEnum("world", () -> Server.getInstance().getLevels().values().stream()
            .flatMap(level -> level.getDimensions().stream())
            .map(Dimension::getName)
            .distinct()
            .toList());

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
                Server.getInstance().getLevels().values().forEach(level -> level.getDimensions().forEach(dimension -> {
                    strBuilder.append(dimension.getName());
                    strBuilder.append(", ");
                }));
                log.addMessage(TextFormat.WHITE + "%nukkit.command.world.availableLevels", strBuilder.toString()).output();
                return 1;
            }
            case "tp" -> {
                String levelName = result.getValue().getResult(1);
                var server = Server.getInstance();
                var level = server.getLevelByName(levelName);
                Dimension dimension = null;

                if (level != null) {
                    dimension = level.getOverworld();
                } else {
                    dimension = server.getDimensionByName(levelName);
                }

                if (dimension == null) {
                    String baseName = baseWorldName(levelName);
                    if (baseName != null && server.loadLevel(baseName)) {
                        dimension = server.getDimensionByName(levelName);
                    } else if (server.loadLevel(levelName)) {
                        level = server.getLevelByName(levelName);
                        dimension = level != null ? level.getOverworld() : null;
                    }
                }

                if (dimension == null) {
                    log.addMessage("nukkit.command.world.levelNotFound", levelName).output();
                    return 0;
                }

                sender.asEntity().teleport(dimension.getSafeSpawn());
                log.addMessage(TextFormat.WHITE + "%nukkit.command.world.successTp", dimension.getName()).output();
                return 1;
            }
            default -> {
                return 0;
            }
        }
    }

    private static String baseWorldName(String levelName) {
        if (levelName.endsWith("_the_nether")) {
            return levelName.substring(0, levelName.length() - "_the_nether".length());
        }
        if (levelName.endsWith("_the_end")) {
            return levelName.substring(0, levelName.length() - "_the_end".length());
        }
        return null;
    }
}
