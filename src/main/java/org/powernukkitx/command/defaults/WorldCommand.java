package org.powernukkitx.command.defaults;

import lombok.extern.slf4j.Slf4j;
import org.powernukkitx.Server;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandEnum;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.command.tree.ParamList;
import org.powernukkitx.command.utils.CommandLogger;
import org.powernukkitx.level.Level;
import org.powernukkitx.utils.TextFormat;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
public class WorldCommand extends VanillaCommand {
    public static final CommandEnum WORLD_NAME_ENUM = new CommandEnum("world", () -> Server.getInstance().getLevels().values().stream().map(Level::getName).toList());
    public static final CommandEnum WORLD_FOLDER_ENUM = new CommandEnum("worldFolder", WorldCommand::listUnloadedWorldFolders);

    public WorldCommand(String name) {
        super(name, "nukkit.command.world.description");
        this.setPermission("nukkit.command.world");
        this.commandParameters.clear();
        this.commandParameters.put("tp",
                new CommandParameter[]{
                        CommandParameter.newEnum("tp", new String[]{"tp"}),
                        CommandParameter.newEnum("world", false, WORLD_NAME_ENUM)
                });
        this.commandParameters.put("load",
                new CommandParameter[]{
                        CommandParameter.newEnum("load", new String[]{"load"}),
                        CommandParameter.newEnum("world", false, WORLD_FOLDER_ENUM)
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
                String levels = Server.getInstance().getLevels().values().stream()
                        .map(Level::getName)
                        .collect(Collectors.joining(", "));
                log.addMessage(TextFormat.WHITE + "%nukkit.command.world.availableLevels", levels).output();
                return 1;
            }
            case "load" -> {
                String folderName = result.getValue().getResult(1);
                if (Server.getInstance().getLevelByName(folderName) != null) {
                    log.addMessage("nukkit.command.world.alreadyLoaded", folderName).output();
                    return 0;
                }
                if (!loadWorld(folderName)) {
                    log.addMessage("nukkit.command.world.levelNotFound", folderName).output();
                    return 0;
                }
                log.addMessage(TextFormat.WHITE + "%nukkit.command.world.successLoad", folderName).output();
                return 1;
            }
            case "tp" -> {
                if (!sender.isPlayer()) {
                    log.addMessage("nukkit.command.generic.ingame").output();
                    return 0;
                }
                String levelName = result.getValue().getResult(1);
                var level = Server.getInstance().getLevelByName(levelName);
                if (level == null) {
                    if (loadWorld(levelName)) {
                        level = Server.getInstance().getLevelByName(levelName);
                    }
                    if (level == null) {
                        log.addMessage("nukkit.command.world.levelNotFound", levelName).output();
                        return 0;
                    }
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

    private static boolean loadWorld(String folderName) {
        boolean loaded;
        try {
            loaded = Server.getInstance().loadLevel(folderName);
        } catch (Exception e) {
            log.error("Failed to load the level {}", folderName, e);
            return false;
        }
        if (loaded) {
            WORLD_FOLDER_ENUM.updateSoftEnum();
        }
        return loaded;
    }

    private static Collection<String> listUnloadedWorldFolders() {
        Server server = Server.getInstance();
        File[] folders = new File(server.getDataPath(), "worlds").listFiles(File::isDirectory);
        if (folders == null) {
            return List.of();
        }
        List<String> unloaded = new ArrayList<>(folders.length);
        for (File folder : folders) {
            if (server.getLevelByName(folder.getName()) == null) {
                unloaded.add(folder.getName());
            }
        }
        return unloaded;
    }
}
