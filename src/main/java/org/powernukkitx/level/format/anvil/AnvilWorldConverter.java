package org.powernukkitx.level.format.anvil;

import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;
import org.powernukkitx.Server;
import org.powernukkitx.level.DimensionEnum;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.ChunkState;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.format.LevelConfig;
import org.powernukkitx.level.format.LevelProvider;
import org.powernukkitx.math.Vector3;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author iYozem
 */
@Slf4j
public final class AnvilWorldConverter {

    private static final Pattern REGION_FILE = Pattern.compile("r\\.(-?\\d+)\\.(-?\\d+)\\.mca");

    private final Server server;
    private final AnvilChunkConverter chunkConverter;

    public AnvilWorldConverter(Server server, JavaBlockMapper blockMapper, JavaItemMapper itemMapper) {
        this.server = server;
        this.chunkConverter = new AnvilChunkConverter(blockMapper, itemMapper);
    }

    public record Result(int chunks, int failed) {}

    public Result convert(File javaWorldDir, String outputName) throws IOException {
        File regionDir = new File(javaWorldDir, "region");
        if (!regionDir.isDirectory()) {
            throw new IllegalArgumentException("No 'region' folder found in " + javaWorldDir.getAbsolutePath() + " (is this a Java Edition world?)");
        }
        if (this.server.getLevelByName(outputName) != null) {
            throw new IllegalArgumentException("A world named '" + outputName + "' is already loaded");
        }

        File[] regionFiles = regionDir.listFiles((dir, name) -> REGION_FILE.matcher(name).matches());
        if (regionFiles == null || regionFiles.length == 0) {
            throw new IllegalArgumentException("No .mca region files found in " + regionDir.getAbsolutePath());
        }

        Vector3 javaSpawn = readJavaSpawn(javaWorldDir);

        Level level = createVoidWorld(outputName);
        if (level == null) {
            throw new IOException("Failed to create the output world '" + outputName + "'");
        }
        LevelProvider provider = level.getProvider();

        int converted = 0;
        int failed = 0;
        log.info("[AnvilConverter] importing {} region files from {}", regionFiles.length, regionDir.getAbsolutePath());

        for (File regionFile : regionFiles) {
            Matcher m = REGION_FILE.matcher(regionFile.getName());
            if (!m.matches()) {
                continue;
            }
            int regionX = Integer.parseInt(m.group(1));
            int regionZ = Integer.parseInt(m.group(2));

            try (RegionReader reader = new RegionReader(regionFile)) {
                for (int lx = 0; lx < 32; lx++) {
                    for (int lz = 0; lz < 32; lz++) {
                        if (!reader.hasChunk(lx, lz)) {
                            continue;
                        }
                        int cx = (regionX << 5) + lx;
                        int cz = (regionZ << 5) + lz;
                        try {
                            NbtMap root = reader.readChunk(lx, lz);
                            if (root == null) {
                                continue;
                            }
                            IChunk chunk = level.getChunk(cx, cz, true);
                            this.chunkConverter.convert(root, chunk);
                            chunk.recalculateHeightMap();
                            chunk.setChunkState(ChunkState.FINISHED);
                            chunk.setChanged();
                            provider.saveChunk(cx, cz, chunk);
                            provider.unloadChunk(cx, cz, false);
                            converted++;
                            if ((converted & 0x3FF) == 0) {
                                log.info("[AnvilConverter] {} chunks converted...", converted);
                            }
                        } catch (Exception e) {
                            failed++;
                            log.warn("[AnvilConverter] failed to convert chunk {},{}: {}", cx, cz, e.toString());
                        }
                    }
                }
            }
        }

        if (javaSpawn != null) {
            level.setSpawnLocation(javaSpawn);
            provider.setSpawn(javaSpawn);
        }

        provider.saveChunks();
        provider.saveLevelData();
        log.info("[AnvilConverter] import complete: {} chunks converted, {} failed", converted, failed);
        return new Result(converted, failed);
    }

    private static Vector3 readJavaSpawn(File javaWorldDir) {
        File levelDat = new File(javaWorldDir, "level.dat");
        if (!levelDat.isFile()) {
            return null;
        }
        try (NBTInputStream in = NbtUtils.createGZIPReader(new FileInputStream(levelDat))) {
            NbtMap root = (NbtMap) in.readTag();
            NbtMap data = root.getCompound("Data");
            if (data == null || !data.containsKey("SpawnX")) {
                return null;
            }
            return new Vector3(data.getInt("SpawnX"), data.getInt("SpawnY"), data.getInt("SpawnZ"));
        } catch (Exception e) {
            log.warn("[AnvilConverter] could not read Java spawn from level.dat: {}", e.toString());
            return null;
        }
    }

    private Level createVoidWorld(String outputName) {
        Map<Integer, LevelConfig.GeneratorConfig> generators = new HashMap<>();
        long seed = LevelConfig.GeneratorConfig.randomSeed();
        generators.put(0, new LevelConfig.GeneratorConfig("void", seed, false, LevelConfig.AntiXrayMode.LOW, true,
            DimensionEnum.OVERWORLD.getDimensionData(), Collections.emptyMap()));
        LevelConfig config = new LevelConfig("leveldb", true, generators);

        if (!this.server.generateLevel(outputName, config)) {
            return null;
        }
        return this.server.getLevelByName(outputName);
    }
}
