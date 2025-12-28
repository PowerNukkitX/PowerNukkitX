package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.block.BlockAir;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.entity.ai.EntityAI;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBundle;
import cn.nukkit.item.ItemFilledMap;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.structure.AbstractStructure;
import cn.nukkit.level.structure.JeStructure;
import cn.nukkit.level.structure.StructureAPI;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.level.generator.biome.BiomePicker;
import cn.nukkit.level.generator.biome.OverworldBiomePicker;
import cn.nukkit.level.generator.biome.result.OverworldBiomeResult;
import cn.nukkit.nbt.tag.LongTag;
import cn.nukkit.network.protocol.types.biome.BiomeConsolidatedFeatureData;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.network.protocol.types.biome.BiomeDefinitionChunkGenData;
import cn.nukkit.network.protocol.types.biome.BiomeDefinitionData;
import cn.nukkit.plugin.InternalPlugin;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.registry.Registries;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.OptionalValue;
import cn.nukkit.utils.TextFormat;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import static cn.nukkit.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;

public class DebugCommand extends TestCommand implements CoreCommand {
    public DebugCommand(String name) {
        super(name, "commands.debug.description");
        this.setPermission("nukkit.command.debug");
        this.commandParameters.clear();
        //生物AI debug模式开关
        this.commandParameters.put("entity", new CommandParameter[]{
                CommandParameter.newEnum("entity", new String[]{"entity"}),
                CommandParameter.newEnum("option", Arrays.stream(EntityAI.DebugOption.values()).map(option -> option.name().toLowerCase(Locale.ENGLISH)).toList().toArray(new String[0])),
                CommandParameter.newEnum("value", false, CommandEnum.ENUM_BOOLEAN)
        });
        this.commandParameters.put("rendermap", new CommandParameter[]{
                CommandParameter.newEnum("rendermap", new String[]{"rendermap"}),
                CommandParameter.newType("zoom", CommandParamType.INT)
        });
        this.commandParameters.put("biome", new CommandParameter[]{
                CommandParameter.newEnum("biome", new String[]{"biome"}),
                CommandParameter.newEnum("features", true, new String[]{"pick", "features", "parameter"})
        });
        this.commandParameters.put("light", new CommandParameter[]{
                CommandParameter.newEnum("light", new String[]{"light"})
        });
        this.commandParameters.put("chunk", new CommandParameter[]{
                CommandParameter.newEnum("chunk", new String[]{"chunk"}),
                CommandParameter.newEnum("options", new String[]{"info", "regenerate", "resend", "queue", "extras"})
        });
        this.commandParameters.put("item", new CommandParameter[]{
                CommandParameter.newEnum("item", new String[]{"item"}),
                CommandParameter.newEnum("values", new String[]{"nbt", "bundle", "meta", "data"})
        });
        this.commandParameters.put("structure", new CommandParameter[]{
                CommandParameter.newEnum("structure", new String[]{"structure"}),
                CommandParameter.newEnum("type", new String[]{"placejava", "place", "registry"}),
                CommandParameter.newType("file", CommandParamType.STRING)
        });
        this.commandParameters.put("reload", new CommandParameter[]{
                CommandParameter.newEnum("reload", new String[]{"reload"}),
                CommandParameter.newEnum("reloadType", true, new String[]{"function", "plugin"}),
                CommandParameter.newType("plugin", true, CommandParamType.STRING)
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        switch (result.getKey()) {
            case "structure" -> {
                if (!sender.isPlayer())
                    return 0;
                String structureName = list.getResult(2);
                Player player = sender.asPlayer();
                Location loc = player.getLocation();

                AbstractStructure structure = null;
                switch (list.getResult(1).toString()) {
                    case "placejava" -> {
                        try (var stream = DebugCommand.class.getClassLoader().getResourceAsStream("structures/" + structureName + ".nbt")) {
                            CompoundTag root = NBTIO.readCompressed(stream);
                            structure = JeStructure.fromNbt(root);
                        } catch (Exception e) {
                            sender.sendMessage(e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    case "place" -> structure = StructureAPI.load(structureName);
                    case "registry" -> structure = Registries.STRUCTURE.get(structureName);
                }
                if (structure == null) {
                    log.addError("Structure " + structureName + " not found").output();
                    return 0;
                }
                structure.place(loc);
                log.addSuccess("Placed structure " + structureName + " at " + loc).output();
                return 1;
            }
            case "entity" -> {
                String str = list.getResult(1);
                var option = EntityAI.DebugOption.valueOf(str.toUpperCase(Locale.ENGLISH));
                boolean value = list.getResult(2);
                EntityAI.setDebugOption(option, value);
                log.addSuccess("Entity AI framework " + option.name() + " debug mode have been set to: " + EntityAI.checkDebugOption(option)).output();
                return 1;
            }
            case "rendermap" -> {
                if (!sender.isPlayer())
                    return 0;
                int zoom = list.getResult(1);
                if (zoom < 1) {
                    log.addError("Zoom must bigger than one").output();
                    return 0;
                }
                var player = sender.asPlayer();
                if (player.getInventory().getItemInHand() instanceof ItemFilledMap itemFilledMap) {
                    player.getLevel().getScheduler().scheduleAsyncTask(InternalPlugin.INSTANCE, new AsyncTask() {
                        @Override
                        public void onRun() {
                            itemFilledMap.renderMap(player.getLevel(), player.getFloorX() - 64, player.getFloorZ() - 64, zoom);
                            player.getInventory().setItemInHand(itemFilledMap);
                            itemFilledMap.sendImage(player);
                            player.sendMessage("Successfully rendered the map in your hand");
                        }
                    });
                    log.addSuccess("Start rendering the map in your hand. Zoom: " + zoom).output();
                    return 1;
                }
                return 0;
            }
            case "biome" -> {
                if (!sender.isPlayer())
                    return 0;
                Location loc = sender.getLocation();
                if(!list.hasResult(1)) {
                    var biome = Registries.BIOME.get(loc.level.getBiomeId(loc.getFloorX(), loc.getFloorY(), loc.getFloorZ()));
                    sender.sendMessage(biome.getName() + " " + Arrays.toString(biome.getTags().toArray(String[]::new)));
                } else {
                    switch (list.getResult(1).toString()) {
                        case "parameter" -> {
                            BiomeDefinition biome = Registries.BIOME.get(loc.level.getBiomeId(loc.getFloorX(), loc.getFloorY(), loc.getFloorZ()));
                            sender.sendMessage("Scale: " + biome.data.scale);
                            sender.sendMessage("Depth: " + biome.data.depth);
                        }
                        case "pick" -> {
                            BiomePicker picker = loc.getLevel().getBiomePicker();
                            if(picker instanceof OverworldBiomePicker p) {
                                OverworldBiomeResult res = p.pick(loc.getFloorX(), SEA_LEVEL, loc.getFloorZ());
                                sender.sendMessage("Continental: " + res.getContinental());
                                sender.sendMessage("Temperature: " + res.getTemperature());
                                sender.sendMessage("Humidity: " + res.getHumidity());
                                sender.sendMessage("Erosion: " + res.getErosion());
                                sender.sendMessage("Weirdness: " + res.getWeirdness());
                                sender.sendMessage("Peaks: " + res.getPv());
                                sender.sendMessage("§ePicked biome: " + Registries.BIOME.get(res.getBiomeId()).getName());
                            }
                        }
                        case "features" -> {
                            BiomeDefinition definition = Registries.BIOME.get(loc.getLevel().getBiomeId(loc.getFloorX(), SEA_LEVEL, loc.getFloorZ()));
                            BiomeDefinitionData biome = definition.data;
                            OptionalValue<BiomeDefinitionChunkGenData> chunkGenDataOptional = biome.chunkGenData;
                            if(chunkGenDataOptional.isPresent()) {
                                BiomeDefinitionChunkGenData chunkGenData = chunkGenDataOptional.get();
                                OptionalValue<BiomeConsolidatedFeatureData[]> consolidatedFeatureDataOptional = chunkGenData.consolidatedFeatures;
                                if(consolidatedFeatureDataOptional.isPresent()) {
                                    BiomeConsolidatedFeatureData[] consolidatedFeatureDataArray = consolidatedFeatureDataOptional.get();
                                    sender.sendMessage("§eFeatures of " + definition.getName() + " [" + consolidatedFeatureDataArray.length + "]");
                                    for(BiomeConsolidatedFeatureData consolidatedFeatureData : consolidatedFeatureDataArray) {
                                        String featureIdentifier = Registries.BIOME.getFromBiomeStringList(consolidatedFeatureData.identifier);
                                        String featureName = Registries.BIOME.getFromBiomeStringList(consolidatedFeatureData.feature);
                                        int evalOrder = consolidatedFeatureData.scatter.evalOrder;
                                        boolean registered = Registries.GENERATE_FEATURE.has(featureName) || Registries.GENERATE_FEATURE.has(featureIdentifier);
                                        sender.sendMessage((registered ? "§a" : "§c") + featureName + " (" + featureIdentifier + ") §e[" + evalOrder + "]");
                                    }
                                }
                            }
                        }
                        default -> {
                            return 0;
                        }
                    }
                }
                return 0;
            }
            case "light" -> {
                if (!sender.isPlayer())
                    return 0;
                Location loc = sender.getLocation();
                sender.sendMessage("light level: " + loc.getLevel().getFullLight(loc));
                return 0;
            }
            case "chunk" -> {
                if (!sender.isPlayer())
                    return 0;
                Player player = sender.asPlayer();
                IChunk chunk = player.getChunk();
                Level level = chunk.getProvider().getLevel();
                switch (list.getResult(1).toString()) {
                    case "info" -> {
                        player.sendMessage("Chunk: X: " + chunk.getX() + ", Z: " + chunk.getZ());
                        player.sendMessage("Stage: " + chunk.getChunkState().name());
                        player.sendMessage("Loaded: " + chunk.isLoaded());
                        player.sendMessage("Current Block: " + player.getLevelBlock().getId());
                        player.sendMessage("Pending block updates: " + level.getPendingBlockUpdates(chunk).size());
                        player.sendMessage("Changes: " + chunk.getChanges());
                        int blocks = 0;
                        for(int x = 0; x < 16; x++)
                            for(int z = 0; z < 16; z++)
                                for(int y = level.getMinHeight(); y < level.getMaxHeight(); y++)
                                    if(chunk.getBlockState(x,y,z, 0).getIdentifier().equals(BlockAir.STATE.getIdentifier())) blocks++;
                        player.sendMessage("Blocks: " + blocks);
                        return 0;
                    }
                    case "regenerate" -> {
                        level.regenerateChunk(chunk.getX(), chunk.getZ());
                        return 0;
                    }
                    case "resend" -> {
                        level.requestChunk(chunk.getX(), chunk.getZ(), player);
                        return 0;
                    }
                    case "queue" -> {
                        CompoundTag chunkExtra = chunk.getExtraData();
                        if(chunkExtra.containsList("structureAnchor")) {
                            var chunks = chunkExtra.getList("structureAnchor", LongTag.class);
                            for (LongTag longTag : chunks.getAll()) {
                                long hash = longTag.getData();
                                IChunk target = level.getChunk(Level.getHashX(hash), Level.getHashZ(hash));
                                if (target != null && target != chunk) {
                                    player.sendMessage(target.getX() + " " + target.getZ());
                                }
                            }
                        }
                        return 0;
                    }
                    case "extras" -> {
                        player.sendMessage(chunk.getExtraData().toSNBT().replace("[[", "§e[[§r").replace("]]", "§e]]§r"));
                        return 0;
                    }
                    default -> {
                        return 0;
                    }
                }
            }
            case "item" -> {
                if (!sender.isPlayer())
                    return 0;
                Player player = sender.asPlayer();
                switch (list.getResult(1).toString()) {
                    case "nbt" -> {
                        player.sendMessage(player.getInventory().getItemInHand().getNamedTag().toSNBT());
                        return 0;
                    }
                    case "bundle" -> {
                        Item item = player.getInventory().getItemInHand();
                        if(item instanceof ItemBundle bundle) {
                            for(Item item1 : bundle.getInventory().getContents().values()) player.sendMessage(item1.toString());
                        }
                        return 0;
                    }
                    case "meta" -> {
                        Item item = player.getInventory().getItemInHand();
                        player.sendMessage(item.getId() + "#" + item.getDamage());
                        return 0;
                    }
                    case "data" -> {
                        Item item = player.getInventory().getItemInHand();
                        CompoundTag nbt = NBTIO.putItemHelper(item);
                        player.sendMessage(nbt.toSNBT(2));
                        return 0;
                    }
                }
                return 0;
            }
            case "reload" -> {
                var server = sender.getServer();
                if (!list.hasResult(1)) {
                    log.addMessage(TextFormat.YELLOW + "%nukkit.command.debug.reloading" + TextFormat.WHITE).output(true);
                    server.reload();
                } else {
                    switch (list.getResult(1).toString()) {
                        case "function" -> {
                            log.addSuccess("§eReloading functions...").output(true);
                            server.getFunctionManager().reload();
                        }
                        case "plugin" -> {
                            if (!list.hasResult(2)) {
                                log.addError("Plugin name required").output(true);
                                return 0;
                            }

                            String pluginName = list.getResult(2);

                            PluginManager pluginManager = server.getPluginManager();
                            Plugin plugin = pluginManager.getPlugin(pluginName);

                            if (plugin == null) {
                                log.addError("Plugin not found: " + pluginName).output(true);
                                return 0;
                            }

                            log.addSuccess("§eReloading plugin...").output(true);
                            pluginManager.reloadPlugin(plugin);
                        }
                    }
                }

                return 1;
            }
            default -> {
                return 0;
            }
        }
    }
}
