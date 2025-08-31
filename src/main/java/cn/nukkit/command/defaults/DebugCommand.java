package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
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
import cn.nukkit.level.generator.biome.BiomePicker;
import cn.nukkit.level.generator.biome.OverworldBiomePicker;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.plugin.InternalPlugin;
import cn.nukkit.registry.Registries;
import cn.nukkit.scheduler.AsyncTask;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
                CommandParameter.newEnum("biome", new String[]{"biome"})
        });
        this.commandParameters.put("light", new CommandParameter[]{
                CommandParameter.newEnum("light", new String[]{"light"})
        });
        this.commandParameters.put("chunk", new CommandParameter[]{
                CommandParameter.newEnum("chunk", new String[]{"chunk"}),
                CommandParameter.newEnum("options", new String[]{"info", "regenerate", "resend", "queue"})
        });
        this.commandParameters.put("item", new CommandParameter[]{
                CommandParameter.newEnum("item", new String[]{"item"}),
                CommandParameter.newEnum("values", new String[]{"nbt", "bundle"})
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        switch (result.getKey()) {
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
                var biome = Registries.BIOME.get(loc.level.getBiomeId(loc.getFloorX(), loc.getFloorY(), loc.getFloorZ()));
                sender.sendMessage(biome.getName() + " " + Arrays.toString(biome.getTags().toArray(String[]::new)));
                BiomePicker picker = loc.getLevel().getBiomePicker();
                if(picker instanceof OverworldBiomePicker p) {
                    float continental = NukkitMath.remap(p.getContinentalNoise().getValue(loc.getFloorX(), loc.getFloorY(), loc.getFloorZ()), -1, 1, -1.2f, 1);
                    float temperature = p.getTemperatureNoise().getValue(loc.getFloorX(), loc.getFloorY(), loc.getFloorZ());
                    float humidity = p.getHumidityNoise().getValue(loc.getFloorX(), loc.getFloorY(), loc.getFloorZ());
                    float erosion = p.getErosionNoise().getValue(loc.getFloorX(), loc.getFloorY(), loc.getFloorZ());
                    float weirdness = p.getWeirdnessNoise().getValue(loc.getFloorX(), loc.getFloorY(), loc.getFloorZ());
                    float pv = NukkitMath.remapNormalized(1f-Math.abs(3*Math.abs(weirdness))-2f, -4, 0);
                    int continentalLevel = continental < -1.05f ? 0 : (continental < -0.455f ? 1 : (continental < -0.19 ? 2 : (continental < -0.11 ? 3 : (continental < 0.03 ? 4 : (continental < 0.3 ? 5 : 6)))));
                    int temperatureLevel = temperature < -0.45f ? 0 : (temperature < -0.15f ? 1 : (temperature < 0.2f ? 2 : (temperature < 0.55f ? 3 : 4)));
                    int humidityLevel = humidity < -0.35f ? 0 : (humidity < -0.1f ? 1 : (humidity < 0.1f ? 2 : (humidity < 0.3f ? 3 : 4)));
                    int erosionLevel = erosion < -0.78f ? 0 : (erosion < -0.375f ? 1 : (erosion < -0.2225f ? 2 : (erosion < 0.05f ? 3 : (erosion < 0.45f ? 4 : (erosion < 0.55f ? 5 : 6)))));
                    int pvLevel = pv < -0.85f ? 0 : (pv < -0.2f ? 1 : (pv < 0.2f ? 2 : (pv < 0.7f ? 3 : 4)));
                    Server.getInstance().broadcastMessage("Continental: " + continental + " " + continentalLevel);
                    Server.getInstance().broadcastMessage("Temperature: " + temperature + " " + temperatureLevel);
                    Server.getInstance().broadcastMessage("Humidity: " + humidity + " " + humidityLevel);
                    Server.getInstance().broadcastMessage("Erosion: " + erosion + " " + erosionLevel);
                    Server.getInstance().broadcastMessage("Weirdness: " + weirdness + "(PV: " + pv + " " + pvLevel + ")");
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
                        int blocks = 0;
                        for(int x = 0; x < 16; x++)
                            for(int z = 0; z < 16; z++)
                                for(int y = level.getMinHeight(); y < level.getMaxHeight(); y++)
                                    if(chunk.getBlockState(x,y,z, 0).getIdentifier().equals(BlockAir.STATE.getIdentifier())) blocks++;
                        player.sendMessage("Blocks: " + blocks);
                        return 0;
                    }
                    case "regenerate" -> {
                        level.syncRegenerateChunk(chunk.getX(), chunk.getZ());
                        return 0;
                    }
                    case "resend" -> {
                        level.requestChunk(chunk.getX(), chunk.getZ(), player);
                        return 0;
                    }
                    case "queue" -> {
                        try {
                            Field field = Level.class.getDeclaredField("chunkGenerationQueue");
                            field.setAccessible(true);
                            player.sendMessage("Queue: " + ((ConcurrentHashMap<Long, Boolean>) field.get(level)).size() + " / " + level.getServer().getSettings().chunkSettings().generationQueueSize());
                            field.setAccessible(false);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
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
                }
                return 0;
            }
            default -> {
                return 0;
            }
        }
    }
}
