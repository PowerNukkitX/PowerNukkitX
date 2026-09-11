package org.powernukkitx.command.defaults;

import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;
import org.powernukkitx.Server;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandEnum;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.command.tree.ParamList;
import org.powernukkitx.command.utils.CommandLogger;
import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.DimensionEnum;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.LevelConfig;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.utils.TextFormat;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
public class WorldCommand extends VanillaCommand {
    public static final CommandEnum WORLD_NAME_ENUM = new CommandEnum("world", () -> Server.getInstance().getLevels().values().stream().map(Level::getName).toList());
    public static final CommandEnum WORLD_FOLDER_ENUM = new CommandEnum("worldFolder", WorldCommand::listUnloadedWorldFolders);
    public static final CommandEnum GENERATOR_ENUM = new CommandEnum("generator", () -> new ArrayList<>(Registries.GENERATOR.getGeneratorList()));

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
        this.commandParameters.put("create",
                new CommandParameter[]{
                        CommandParameter.newEnum("create", new String[]{"create"}),
                        CommandParameter.newType("world", CommandParamType.ID),
                        CommandParameter.newType("seed", true, CommandParamType.ID),
                        CommandParameter.newEnum("generator", true, GENERATOR_ENUM)
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
            case "create" -> {
                String folderName = result.getValue().getResult(1);
                if (folderName.isBlank() || folderName.contains("/") || folderName.contains("\\")
                        || folderName.contains("..")) {
                    log.addMessage("nukkit.command.world.invalidName", folderName).output();
                    return 0;
                }
                if (Server.getInstance().getLevelByName(folderName) != null
                        || new File(Server.getInstance().getDataPath(), "worlds/" + folderName).exists()) {
                    log.addMessage("nukkit.command.world.alreadyExists", folderName).output();
                    return 0;
                }
                String seedArg = result.getValue().getResult(2, "");
                long seed = seedArg.isBlank() ? LevelConfig.GeneratorConfig.randomSeed() : parseSeed(seedArg);
                String generator = result.getValue().getResult(3, "normal");
                if (Registries.GENERATOR.get(generator) == null) {
                    log.addMessage("nukkit.command.world.unknownGenerator", generator).output();
                    return 0;
                }

                Map<Integer, LevelConfig.GeneratorConfig> generators = new HashMap<>(1);
                generators.put(0, new LevelConfig.GeneratorConfig(generator, seed, false,
                        LevelConfig.AntiXrayMode.LOW, true, dimensionOf(generator), Map.of()));
                if (!Server.getInstance().generateLevel(folderName, new LevelConfig("leveldb", true, generators))) {
                    log.addMessage("nukkit.command.world.createError", folderName).output();
                    return 0;
                }
                WORLD_NAME_ENUM.updateSoftEnum();
                WORLD_FOLDER_ENUM.updateSoftEnum();
                log.addMessage(TextFormat.WHITE + "%nukkit.command.world.successCreate", folderName,
                        String.valueOf(seed), generator).output();
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

    /**
     * Parses a seed the way vanilla does: a plain number is used as-is, anything else is hashed.
     */
    private static long parseSeed(String seed) {
        try {
            return Long.parseLong(seed.trim());
        } catch (NumberFormatException e) {
            return seed.hashCode();
        }
    }

    private static DimensionData dimensionOf(String generator) {
        return switch (generator.toLowerCase(Locale.ENGLISH)) {
            case "nether" -> DimensionEnum.NETHER.getDimensionData();
            case "the_end" -> DimensionEnum.THE_END.getDimensionData();
            default -> DimensionEnum.OVERWORLD.getDimensionData();
        };
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
