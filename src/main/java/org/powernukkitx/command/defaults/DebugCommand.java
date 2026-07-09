package org.powernukkitx.command.defaults;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.block.BlockAir;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandEnum;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.command.tree.ParamList;
import org.powernukkitx.command.utils.CommandLogger;
import org.powernukkitx.ddui.CustomForm;
import org.powernukkitx.ddui.Observable;
import org.powernukkitx.ddui.element.options.SliderElementOptions;
import org.powernukkitx.ddui.element.options.TextFieldOptions;
import org.powernukkitx.entity.ai.EntityAI;
import org.powernukkitx.inventory.fake.FakeInventory;
import org.powernukkitx.inventory.fake.FakeInventoryType;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBundle;
import org.powernukkitx.item.ItemFilledMap;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Location;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.NukkitMath;
import org.powernukkitx.level.generator.biome.BiomePicker;
import org.powernukkitx.level.generator.biome.OverworldBiomePicker;
import org.powernukkitx.level.generator.biome.result.OverworldBiomeResult;
import org.powernukkitx.level.structure.AbstractStructure;
import org.powernukkitx.level.structure.JeStructure;
import org.powernukkitx.level.structure.StructureAPI;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.LongTag;
import org.powernukkitx.plugin.InternalPlugin;
import org.powernukkitx.plugin.Plugin;
import org.powernukkitx.plugin.PluginManager;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.scheduler.AsyncTask;
import org.powernukkitx.scheduler.TaskHandler;
import org.powernukkitx.utils.GameLoop;
import org.powernukkitx.utils.ItemHelper;
import org.powernukkitx.utils.TextFormat;
import it.unimi.dsi.fastutil.Pair;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeConsolidatedFeatureData;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeDefinitionChunkGenData;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeDefinitionData;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DebugCommand extends TestCommand implements CoreCommand {
    public DebugCommand(String name) {
        super(name, "commands.debug.description");
        this.setPermission("nukkit.command.debug");
        this.commandParameters.clear();
        // Toggle for AI debug mode
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
                CommandParameter.newEnum("options", new String[]{"info", "regenerate", "resend", "queue", "extras", "reload"})
        });
        this.commandParameters.put("item", new CommandParameter[]{
                CommandParameter.newEnum("item", new String[]{"item"}),
                CommandParameter.newEnum("values", new String[]{"nbt", "bundle", "meta", "data"})
        });
        this.commandParameters.put("structure", new CommandParameter[]{
                CommandParameter.newEnum("structure", new String[]{"structure"}),
                CommandParameter.newEnum("type", new String[]{"placejava", "place", "registry"}),
                CommandParameter.newType("file", CommandParamType.ID)
        });
        this.commandParameters.put("reload", new CommandParameter[]{
                CommandParameter.newEnum("reload", new String[]{"reload"}),
                CommandParameter.newEnum("reloadType", true, new String[]{"function", "plugin"}),
                CommandParameter.newType("plugin", true, CommandParamType.ID)
        });
        this.commandParameters.put("ddui", new CommandParameter[]{
                CommandParameter.newEnum("ddui", new String[]{"ddui"})
        });
        this.commandParameters.put("toggle", new CommandParameter[]{
                CommandParameter.newEnum("toggle", new String[]{"toggle"}),
                CommandParameter.newEnum("type", new String[]{"invulnerable"}),
        });
        this.commandParameters.put("fakeinv", new CommandParameter[]{
                CommandParameter.newEnum("fakeinv", new String[]{"fakeinv"})
        });
        this.commandParameters.put("tps", new CommandParameter[]{
                CommandParameter.newEnum("tps", new String[]{"tps"}),
                CommandParameter.newType("value", true, CommandParamType.INT)
        });
        this.commandParameters.put("genrate", new CommandParameter[]{
                CommandParameter.newEnum("genrate", new String[]{"genrate"})
        });
        this.commandParameters.put("mspt", new CommandParameter[]{
                CommandParameter.newEnum("mspt", new String[]{"mspt"}),
                CommandParameter.newEnum("off", true, new String[]{"off"})
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String label, Map.Entry<String, ParamList> result, CommandLogger log) {
        return switch (result.getKey()) {
            case "fakeinv" -> handleFakeInv(sender);
            case "structure" -> handleStructure(sender, result.getValue(), log);
            case "entity" -> handleEntity(result.getValue(), log);
            case "rendermap" -> handleRenderMap(sender, result.getValue(), log);
            case "biome" -> handleBiome(sender, result.getValue());
            case "light" -> handleLight(sender);
            case "chunk" -> handleChunk(sender, result.getValue());
            case "item" -> handleItem(sender, result.getValue());
            case "reload" -> handleReload(sender, result.getValue(), log);
            case "ddui" -> exampleDDUI(sender);
            case "toggle" -> handleToggle(sender, result.getValue(), log);
            case "tps" -> handleTps(sender, result.getValue(), log);
            case "genrate" -> handleGenRate(sender);
            case "mspt" -> handleMspt(sender, result.getValue());
            default -> 0;
        };
    }

    private int handleFakeInv(CommandSender sender) {
        final FakeInventory fakeInventory = new FakeInventory(FakeInventoryType.CHEST, "Test");
        fakeInventory.setItem(0, Item.get(Item.DIAMOND));
        ((Player) sender).addWindow(fakeInventory);
        return 1;
    }

    private int handleToggle(CommandSender sender, ParamList value, CommandLogger log) {
        switch (value.getResult(1).toString()) {
            case "invulnerable" -> {
                if (!sender.isPlayer()) return 0;
                Player player = sender.asPlayer();
                boolean newValue = !player.isInvulnerable();
                player.setInvulnerable(newValue);
                log.addSuccess("Set invulnerable to " + newValue).output();
                return 1;
            }
        }
        return 0;
    }

    private int handleStructure(CommandSender sender, ParamList list, CommandLogger log) {
        if (!sender.isPlayer()) return 0;
        Player player = sender.asPlayer();
        String structureName = list.getResult(2);
        Location loc = player.getLocation();

        AbstractStructure structure = switch (list.getResult(1).toString()) {
            case "placejava" -> loadJavaStructure(structureName, sender);
            case "place" -> StructureAPI.load(structureName);
            case "registry" -> Registries.STRUCTURE.get(structureName);
            default -> null;
        };

        if (structure == null) {
            log.addError("Structure " + structureName + " not found").output();
            return 0;
        }

        structure.place(loc);
        log.addSuccess("Placed structure " + structureName + " at " + loc).output();
        return 1;
    }

    private AbstractStructure loadJavaStructure(String name, CommandSender sender) {
        try (var stream = getClass().getClassLoader().getResourceAsStream("structures/" + name + ".nbt")) {
            if (stream == null) return null;
            try (final NBTInputStream inputStream = NbtUtils.createReader(stream)) {
                return JeStructure.fromNbt(CompoundTag.fromNetwork((NbtMap) inputStream.readTag()));
            }
        } catch (Exception e) {
            sender.sendMessage(e.getMessage());
            return null;
        }
    }

    private int handleEntity(ParamList list, CommandLogger log) {
        String str = list.getResult(1);
        var option = EntityAI.DebugOption.valueOf(str.toUpperCase(Locale.ENGLISH));
        boolean value = list.getResult(2);
        EntityAI.setDebugOption(option, value);
        log.addSuccess("Entity AI framework " + option.name() + " debug mode have been set to: " + EntityAI.checkDebugOption(option)).output();
        return 1;
    }

    private int handleRenderMap(CommandSender sender, ParamList list, CommandLogger log) {
        if (!sender.isPlayer()) return 0;
        int zoom = list.getResult(1);
        if (zoom < 1) {
            log.addError("Zoom must bigger than one").output();
            return 0;
        }

        Player player = sender.asPlayer();
        if (player.getInventory().getItemInMainHand() instanceof ItemFilledMap map) {
            player.getLevel().getScheduler().scheduleAsyncTask(InternalPlugin.INSTANCE, new AsyncTask() {
                @Override
                public void onRun() {
                    map.renderMap(player.getLevel(), player.getFloorX() - 64, player.getFloorZ() - 64, zoom);
                    player.getInventory().setItemInMainHand(map);
                    map.sendImage(player);
                    player.sendMessage("Successfully rendered the map in your hand");
                }
            });
            log.addSuccess("Start rendering the map in your hand. Zoom: " + zoom).output();
            return 1;
        }
        return 0;
    }

    private int handleBiome(CommandSender sender, ParamList list) {
        if (!sender.isPlayer()) return 0;
        Location loc = sender.getLocation();

        if (!list.hasResult(1)) {
            var biome = Registries.BIOME.get(loc.level.getBiomeId(loc.getFloorX(), loc.getFloorY(), loc.getFloorZ()));
            var name = Registries.BIOME.getFromBiomeStringList(biome.key());
            sender.sendMessage(name + " " + Arrays.toString(Registries.BIOME.getTags(name).toArray(String[]::new)));
            return 1;
        }

        switch (list.getResult(1).toString()) {
            case "parameter" -> {
                BiomeDefinitionData biome = Registries.BIOME.get(loc.level.getBiomeId(loc.getFloorX(), loc.getFloorY(), loc.getFloorZ())).second();
                sender.sendMessage("Scale: " + biome.getScale());
                sender.sendMessage("Depth: " + biome.getDepth());
            }
            case "pick" -> {
                BiomePicker picker = loc.getLevel().getBiomePicker();
                if (picker instanceof OverworldBiomePicker p) {
                    Player player = sender.asPlayer();
                    OverworldBiomeResult res = p.pick(loc.getFloorX(), loc.getFloorY(), loc.getFloorZ());
                    sender.sendMessage("Continental: " + res.getContinental());
                    sender.sendMessage("Temperature: " + res.getTemperature());
                    sender.sendMessage("Humidity: " + res.getHumidity());
                    sender.sendMessage("Erosion: " + res.getErosion());
                    sender.sendMessage("Weirdness: " + res.getWeirdness());
                    sender.sendMessage("Peaks: " + res.getPv());
                    sender.sendMessage("Depths: " + ((loc.getFloorY() - sender.getLocation().getChunk().getHeightMap(player.getFloorX() - (player.getChunkX() << 4), player.getFloorZ() - (player.getChunkZ() << 4))) / 128f));
                    sender.sendMessage("§ePicked biome: " + Registries.BIOME.getFromBiomeStringList(Registries.BIOME.get(res.getBiomeId()).key()));
                }
            }
            case "features" -> {
                Pair<Short, BiomeDefinitionData> definition = Registries.BIOME.get(loc.getLevel().getBiomeId(loc.getFloorX(), loc.getFloorY(), loc.getFloorZ()));
                BiomeDefinitionData biome = definition.second();
                final String biomeName = Registries.BIOME.getFromBiomeStringList(definition.key());
                BiomeDefinitionChunkGenData chunkGenData = biome.getChunkGenData();
                if (chunkGenData != null) {
                    final List<BiomeConsolidatedFeatureData> features = chunkGenData.getConsolidatedFeatures();
                    if (features != null) {
                        sender.sendMessage("§eFeatures of " + biomeName + " [" + features.size() + "]");
                        for (BiomeConsolidatedFeatureData f : features) {
                            String id = Registries.BIOME.getFromBiomeStringList(f.getIdentifier());
                            String name = Registries.BIOME.getFromBiomeStringList(f.getFeature());
                            int order = f.getScatter().getEvalOrder().ordinal();
                            boolean registered = Registries.GENERATE_FEATURE.has(name) || Registries.GENERATE_FEATURE.has(id);
                            sender.sendMessage((registered ? "§a" : "§c") + name + " (" + id + ") §e[" + order + "]");
                        }
                    }
                }
            }
        }
        return 1;
    }

    private int handleLight(CommandSender sender) {
        if (!sender.isPlayer()) return 0;
        Location loc = sender.getLocation();
        sender.sendMessage("light level: " + loc.getLevel().getFullLight(loc));
        return 1;
    }

    private static TaskHandler genRateTask;
    private static long genRateLastCount;
    private static long genRateLastNanos;

    private int handleGenRate(CommandSender sender) {
        if (genRateTask != null) {
            genRateTask.cancel();
            genRateTask = null;
            sender.sendMessage("§eChunk generation rate monitor stopped.");
            return 1;
        }
        genRateLastCount = Level.GENERATED_CHUNK_COUNT.get();
        genRateLastNanos = System.nanoTime();
        final CommandSender target = sender;
        genRateTask = sender.getServer().getScheduler().scheduleRepeatingTask(() -> {
            if (target instanceof Player p && !p.isOnline()) {
                if (genRateTask != null) {
                    genRateTask.cancel();
                    genRateTask = null;
                }
                return;
            }
            long now = System.nanoTime();
            long count = Level.GENERATED_CHUNK_COUNT.get();
            double seconds = (now - genRateLastNanos) / 1_000_000_000.0;
            long delta = count - genRateLastCount;
            double rate = seconds > 0 ? delta / seconds : 0.0;
            genRateLastCount = count;
            genRateLastNanos = now;
            target.sendMessage(String.format("§aChunk gen: %.1f/s §7(%d in %.2fs, %d total)", rate, delta, seconds, count));
        }, 20);
        sender.sendMessage("§eChunk generation rate monitor started (~1s interval). Run /debug genrate again to stop.");
        return 1;
    }

    private int handleChunk(CommandSender sender, ParamList list) {
        if (!sender.isPlayer()) return 0;
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
                for (int x = 0; x < 16; x++)
                    for (int z = 0; z < 16; z++)
                        for (int y = level.getMinHeight(); y < level.getMaxHeight(); y++)
                            if (chunk.getBlockState(x, y, z, 0).getIdentifier().equals(BlockAir.STATE.getIdentifier()))
                                blocks++;
                player.sendMessage("Blocks: " + blocks);
            }
            case "regenerate" -> level.regenerateChunk(chunk.getX(), chunk.getZ());
            case "resend" -> level.requestChunk(chunk.getX(), chunk.getZ(), player);
            case "queue" -> {
                CompoundTag extra = chunk.getExtraData();
                if (extra.contains("structureAnchor")) {
                    for (LongTag tag : extra.getList("structureAnchor", LongTag.class).getAll()) {
                        long hash = tag.getData();
                        IChunk target = level.getChunk(Level.getHashX(hash), Level.getHashZ(hash));
                        if (target != null && target != chunk)
                            player.sendMessage(target.getX() + " " + target.getZ());
                    }
                }
            }
            case "extras" ->
                    player.sendMessage(chunk.getExtraData().toString().replace("[[", "§e[[§r").replace("]]", "§e]]§r"));
            case "reload" -> {
                player.sendMessage("§eReloading chunk...");
                int cx = player.getChunkX();
                int cz = player.getChunkZ();
                int radius = 1;
                for (int x = cx - radius; x <= cx + radius; x++)
                    for (int z = cz - radius; z <= cz + radius; z++) {
                        IChunk c = level.getChunk(x, z);
                        if (c != null) {
                            level.unloadChunk(x, z, true);
                            level.loadChunk(x, z, true);
                            level.requestChunk(x, z, player);
                        }
                    }
            }
        }
        return 1;
    }

    private int handleItem(CommandSender sender, ParamList list) {
        if (!sender.isPlayer()) return 0;
        Player player = sender.asPlayer();

        switch (list.getResult(1).toString()) {
            case "nbt" -> player.sendMessage(player.getInventory().getItemInMainHand().getNbt().toString());
            case "bundle" -> {
                Item item = player.getInventory().getItemInMainHand();
                if (item instanceof ItemBundle bundle)
                    for (Item it : bundle.getInventory().getContents().values())
                        player.sendMessage(it.toString());
            }
            case "meta" -> {
                Item item = player.getInventory().getItemInMainHand();
                player.sendMessage(item.getId() + "#" + item.getDamage());
            }
            case "data" -> {
                Item item = player.getInventory().getItemInMainHand();
                CompoundTag nbt = ItemHelper.write(item);
                player.sendMessage(nbt.toSNBT());
            }
        }
        return 1;
    }

    private int handleReload(CommandSender sender, ParamList list, CommandLogger log) {
        var server = sender.getServer();

        if (!list.hasResult(1)) {
            log.addMessage(TextFormat.YELLOW + "%nukkit.command.debug.reloading" + TextFormat.WHITE).output(true);
            server.reload();
            return 1;
        }

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
                String name = list.getResult(2);
                PluginManager pm = server.getPluginManager();
                Plugin plugin = pm.getPlugin(name);
                if (plugin == null) {
                    log.addError("Plugin not found: " + name).output(true);
                    return 0;
                }
                log.addSuccess("§eReloading plugin...").output(true);
                pm.reloadPlugin(plugin);
            }
        }
        return 1;
    }

    private int exampleDDUI(CommandSender sender) {
        if (!sender.isPlayer()) return 0;

        var server = sender.getServer();

        Observable<String> name = new Observable<>("");
        Observable<String> bio = new Observable<>("");
        Observable<Long> age = new Observable<>(18L);
        Observable<String> ageGroup = new Observable<>("Adult");
        Observable<Long> difficulty = new Observable<>(3L);

        CustomForm form = new CustomForm("My Form")
                .label("Personal informations")
                .textField("Name", name, TextFieldOptions.builder()
                        .description("Max 10 characters")
                        .build())
                .textField("Biography", bio, TextFieldOptions.builder()
                        .description("This is your biography. You can write anything you want here.")
                        .build())
                .slider("Age", 1L, 100L, age)
                .textField("Age Group", ageGroup, TextFieldOptions.builder()
                        .description("Automatically set based on age")
                        .disabled(true)
                        .build())
                .slider("Difficulty",
                        1L, 5L,
                        difficulty,
                        SliderElementOptions.builder()
                                .description("1 = Peaceful - 5 = Hardcore")
                                .build()
                )
                .button("Reset", player -> CompletableFuture.runAsync(() -> {
                    name.setValue("");
                    bio.setValue("");
                    age.setValue(18L);
                    difficulty.setValue(3L);
                }));
        form.button("Confirm", player -> {
                    player.sendMessage("Confirmed successfully!");
                    String _name = name.getValue();
                    String _bio = bio.getValue();
                    long _age = age.getValue();
                    long _difficulty = difficulty.getValue();
                    player.sendMessage("Name: " + _name);
                    player.sendMessage("Biography: " + _bio);
                    player.sendMessage("Age: " + _age + " (" + ageGroup.getValue() + ")");
                    player.sendMessage("Difficulty: " + _difficulty);

                    form.close(player);
                })
                .closeButton();

        name.subscribe(value -> {
            String normalized = value.length() > 10 ? value.substring(0, 10) : value;
            server.getScheduler().scheduleTask(InternalPlugin.INSTANCE, () -> {
                if (!normalized.equals(value)) {
                    name.setValue(normalized);
                }
            });

            return null;
        });

        age.subscribe(value -> {
            CompletableFuture.runAsync(() -> {
                //Kid: 1 - 12
                //Teen: 13 - 17
                //Adult: 18 - 64
                //Senior: 65+

                if (value <= 12) {
                    ageGroup.setValue("Kid");
                } else if (value <= 17) {
                    ageGroup.setValue("Teen");
                } else if (value <= 64) {
                    ageGroup.setValue("Adult");
                } else {
                    ageGroup.setValue("Senior");
                }
            });

            return null;
        });


        form.show(sender.asPlayer());
        return 1;
    }

    private int handleMspt(CommandSender sender, ParamList value) {
        Server server = sender.getServer();

        if (value.hasResult(1)) { // "off": disarm phase profiling
            for (Level level : server.getLevels().values()) {
                level.setTickPhaseProfiling(false);
            }
            sender.sendMessage("§eLevel tick phase profiling disabled.");
            return 1;
        }

        boolean armedNow = false;
        for (Level level : server.getLevels().values()) {
            if (!level.isTickPhaseProfiling()) {
                level.setTickPhaseProfiling(true);
                level.snapshotTickPhaseAvgNanos(true);
                armedNow = true;
            }
        }
        if (armedNow) {
            sender.sendMessage("§ePhase profiling armed (adds ~11 clock reads per level tick). Run /debug mspt again in a few seconds for the breakdown; /debug mspt off to disarm.");
        }

        long[] durations = server.getTickDurationsNanos();
        // Drop unfilled (zero) slots
        int n = 0;
        for (long d : durations) {
            if (d > 0) durations[n++] = d;
        }
        if (n == 0) {
            sender.sendMessage("§cNo tick samples recorded yet.");
            return 1;
        }
        Arrays.sort(durations, 0, n);
        long p50 = durations[(int) (n * 0.50)];
        long p90 = durations[Math.min(n - 1, (int) (n * 0.90))];
        long p99 = durations[Math.min(n - 1, (int) (n * 0.99))];
        long max = durations[n - 1];
        long target = server.getNanosPerTick();

        sender.sendMessage("§eServer tick durations (" + n + " samples):");
        sender.sendMessage("§7  p50: §f" + formatNanos(p50) + " §7 p90: §f" + formatNanos(p90)
                + " §7 p99: §f" + formatNanos(p99) + " §7 max: §f" + formatNanos(max));
        sender.sendMessage("§7  target: §f" + formatNanos(target) + "/tick (" + server.getBaseTps() + " TPS)"
                + " §7 measured TPS: §f" + NukkitMath.round(server.getTicksPerSecond(), 2));
        boolean levelThread = server.getSettings().levelSettings().levelThread();
        for (Level level : server.getLevels().values()) {
            if (levelThread) {
                sender.sendMessage("§7  level " + level.getName() + ": §f"
                        + NukkitMath.round(level.getBaseTickGameLoop().getMSPT(), 3) + " ms avg, TPS "
                        + NukkitMath.round(level.getBaseTickGameLoop().getTps(), 2));
            }
            long[] phases = level.snapshotTickPhaseAvgNanos(true);
            StringBuilder sb = new StringBuilder("§7    phases: §f");
            boolean any = false;
            for (int i = 0; i < phases.length; i++) {
                if (phases[i] <= 0) continue;
                if (any) sb.append("§7, §f");
                sb.append(Level.TICK_PHASE_NAMES[i]).append(' ').append(formatNanos(phases[i]));
                any = true;
            }
            if (any) {
                sender.sendMessage((levelThread ? "" : "§7  level " + level.getName() + ":\n") + sb);
            }
        }
        return 1;
    }

    private static String formatNanos(long nanos) {
        if (nanos >= 1_000_000L) {
            return NukkitMath.round(nanos / 1_000_000d, 3) + " ms";
        }
        if (nanos >= 1_000L) {
            return NukkitMath.round(nanos / 1_000d, 2) + " µs";
        }
        return nanos + " ns";
    }

    private int handleTps(CommandSender sender, ParamList value, CommandLogger log) {
        if (value.hasResult(1)) {
            Server server = sender.getServer();
            server.getSettings().performanceSettings().baseTps(value.get(1).get());
            int baseTps = server.getBaseTps();
            if (server.getSettings().levelSettings().levelThread()) {
                for (Level level : server.getLevels().values()) {
                    level.getBaseTickGameLoop().setLoopCountPerSec(baseTps);
                }
            }
            log.addSuccess("Set base TPS to " + baseTps);
            if (server.getNanosPerTick() < GameLoop.SPIN_ACTIVATION_NANOS) {
                log.addSuccess("§eHigh-TPS mode: tick threads will busy-wait for sub-millisecond precision (one CPU core per tick loop).");
            }
        } else log.addSuccess("Current base TPS: " + sender.getServer().getBaseTps());
        log.output();
        return 1;
    }
}
