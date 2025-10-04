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
import cn.nukkit.level.structure.JeStructure;
import cn.nukkit.level.structure.Structure;
import cn.nukkit.level.structure.StructureAPI;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.plugin.InternalPlugin;
import cn.nukkit.registry.Registries;
import cn.nukkit.scheduler.AsyncTask;

import java.lang.reflect.Field;
import java.nio.ByteOrder;
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
                CommandParameter.newEnum("values", new String[]{"nbt", "bundle", "meta", "data"})
        });
        this.commandParameters.put("str", new CommandParameter[]{
                CommandParameter.newEnum("str", new String[]{"str"}),
                CommandParameter.newEnum("values", new String[]{"placejava", "place"}),
                CommandParameter.newType("file", CommandParamType.STRING)
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        switch (result.getKey()) {
            case "str" -> {
                if (!sender.isPlayer())
                    return 0;
                Player player = sender.asPlayer();
                switch (list.getResult(1).toString()) {
                    case "placejava" -> {
                        String structureName = list.getResult(2);
                        Location loc = player.getLocation();

                        try (var stream = DebugCommand.class.getClassLoader().getResourceAsStream("structures/" + structureName + ".nbt")) {
                            CompoundTag root = NBTIO.readCompressed(stream);

                            JeStructure structure = JeStructure.fromNbt(root);
                            structure.place(loc);
                            log.addSuccess("Placed structure " + structureName + " at " + loc).output();

                        } catch (Exception e) {
                            sender.sendMessage(e.getMessage());
                            e.printStackTrace();
                        }

                        return 1;
                    }

                    case "place" -> {
                        String structureName = list.getResult(2);
                        Location loc = player.getLocation();

                        Structure structure = StructureAPI.load(structureName);
                        if (structure == null) {
                            log.addError("Structure " + structureName + " not found").output();
                            return 0;
                        }

                        structure.place(loc);
                        log.addSuccess("Placed structure " + structureName + " at " + loc).output();

                        return 1;
                    }
                }
                return 0;
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
                var biome = Registries.BIOME.get(loc.level.getBiomeId(loc.getFloorX(), loc.getFloorY(), loc.getFloorZ()));
                sender.sendMessage(biome.getName() + " " + Arrays.toString(biome.getTags().toArray(String[]::new)));
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
            default -> {
                return 0;
            }
        }
    }
}
