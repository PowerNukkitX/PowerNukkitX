package jmh;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NBTOutputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtUtils;
import org.openjdk.jmh.annotations.*;
import org.powernukkitx.level.format.palette.PaletteEntryHashCache;
import org.powernukkitx.utils.HashUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * Reading the persistent palette of one section, before and after {@link PaletteEntryHashCache}:
 * {@code realWorld} repeats common states so the cache is warm, {@code allUnique} never repeats and
 * runs with the budget spent, so every read misses.
 * <p>
 * Run with {@code ./gradlew jmh -Pbenchmark="PaletteEntryHashCacheBenchmark -prof gc"}.
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 8, time = 1)
@Fork(3)
public class PaletteEntryHashCacheBenchmark {
    private static final int SECTIONS = 1024;
    private static final int STORED_VERSION = 18168865;

    @Param({"realWorld", "allUnique"})
    public String workload;

    @Param({"before", "after"})
    public String version;

    private ByteBuf[] sections;
    private int[] entryCounts;
    private int cursor;
    private boolean after;

    @Setup(Level.Iteration)
    public void setup() throws IOException {
        after = version.equals("after");
        PaletteEntryHashCache.clear();
        List<NbtMap> all = loadBlockStates();
        String[] common = {"minecraft:stone", "minecraft:air", "minecraft:dirt", "minecraft:deepslate",
                "minecraft:grass_block", "minecraft:water", "minecraft:gravel", "minecraft:andesite",
                "minecraft:granite", "minecraft:diorite", "minecraft:coal_ore", "minecraft:iron_ore",
                "minecraft:copper_ore", "minecraft:tuff", "minecraft:bedrock"};
        List<NbtMap> commonTags = new ArrayList<>();
        for (String name : common) commonTags.add(byName(all, name));

        SplittableRandom random = new SplittableRandom(3);
        sections = new ByteBuf[SECTIONS];
        entryCounts = new int[SECTIONS];
        int unique = 0;
        for (int s = 0; s < SECTIONS; s++) {
            ByteBuf buf = Unpooled.buffer();
            int count = 4 + random.nextInt(11);
            for (int e = 0; e < count; e++) {
                NbtMap tag;
                if (workload.equals("allUnique")) {
                    tag = all.get(random.nextInt(all.size())).toBuilder().putString("unique", "u" + unique++).build();
                } else {
                    tag = random.nextInt(10) < 8 ? commonTags.get(random.nextInt(commonTags.size())) : all.get(random.nextInt(400));
                }
                buf.writeBytes(stored(tag));
            }
            sections[s] = buf;
            entryCounts[s] = count;
        }

        if (workload.equals("allUnique")) {
            // Fill the budget with unrelated entries so every read below misses.
            for (int i = 0; PaletteEntryHashCache.usedBytes() + 512 < PaletteEntryHashCache.MAX_BYTES; i++) {
                readAfter(Unpooled.wrappedBuffer(stored(all.get(i % all.size()).toBuilder().putInt("filler", i).build())));
            }
        }
    }

    @Benchmark
    public int readSectionPalette() throws IOException {
        int s = cursor;
        cursor = (s + 1) % SECTIONS;
        ByteBuf buf = sections[s];
        buf.readerIndex(0);
        int result = 0;
        for (int e = 0; e < entryCounts[s]; e++) {
            result += after ? readAfter(buf) : readBefore(buf);
        }
        return result;
    }

    /** The head of addBlockPalette before the cache. */
    private static int readBefore(ByteBuf byteBuf) throws IOException {
        byteBuf.markReaderIndex();
        try (ByteBufInputStream inputStream = new ByteBufInputStream(byteBuf);
             NBTInputStream nbtInputStream = NbtUtils.createReaderLE(inputStream)) {
            NbtMap blockTag = (NbtMap) nbtInputStream.readTag();
            int storedVersion = blockTag.getInt("version");
            NbtMapBuilder builder = blockTag.toBuilder();
            builder.remove("version");
            blockTag = builder.build();
            return storedVersion ^ HashUtils.fnv1a_32_nbt(blockTag);
        }
    }

    /** The head of addBlockPalette with the cache. */
    private static int readAfter(ByteBuf byteBuf) throws IOException {
        byteBuf.markReaderIndex();
        PaletteEntryHashCache.Probe cacheProbe = PaletteEntryHashCache.probe(byteBuf);
        PaletteEntryHashCache.Entry cached = cacheProbe == null ? null : PaletteEntryHashCache.lookup(cacheProbe, byteBuf);
        if (cached != null) {
            return cached.storedVersion() ^ cached.blockRuntimeId();
        }
        int storedVersion;
        int blockRuntimeId;
        try (ByteBufInputStream inputStream = new ByteBufInputStream(byteBuf);
             NBTInputStream nbtInputStream = NbtUtils.createReaderLE(inputStream)) {
            NbtMap blockTag = (NbtMap) nbtInputStream.readTag();
            storedVersion = blockTag.getInt("version");
            NbtMapBuilder builder = blockTag.toBuilder();
            builder.remove("version");
            blockRuntimeId = HashUtils.fnv1a_32_nbt(builder.build());
        }
        if (cacheProbe != null) {
            PaletteEntryHashCache.remember(cacheProbe, byteBuf.readerIndex(), storedVersion, blockRuntimeId);
        }
        return storedVersion ^ blockRuntimeId;
    }

    private static List<NbtMap> loadBlockStates() throws IOException {
        try (InputStream stream = PaletteEntryHashCacheBenchmark.class.getClassLoader().getResourceAsStream("mappings/blocks.json");
             Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            List<NbtMap> states = new ArrayList<>();
            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                JsonObject mapping = entry.getValue().getAsJsonObject();
                NbtMapBuilder blockStates = NbtMap.builder();
                if (mapping.has("bedrock_states")) {
                    for (Map.Entry<String, JsonElement> state : mapping.getAsJsonObject("bedrock_states").entrySet()) {
                        JsonPrimitive value = state.getValue().getAsJsonPrimitive();
                        if (value.isBoolean()) blockStates.putByte(state.getKey(), (byte) (value.getAsBoolean() ? 1 : 0));
                        else if (value.isNumber()) blockStates.putInt(state.getKey(), value.getAsInt());
                        else blockStates.putString(state.getKey(), value.getAsString());
                    }
                }
                states.add(NbtMap.builder()
                        .putString("name", mapping.get("bedrock_identifier").getAsString())
                        .putCompound("states", NbtMap.fromMap(new TreeMap<>(blockStates.build())))
                        .build());
            }
            return states;
        }
    }

    private static NbtMap byName(List<NbtMap> states, String name) {
        for (NbtMap state : states) {
            if (state.getString("name").equals(name)) return state;
        }
        throw new IllegalArgumentException(name);
    }

    private static byte[] stored(NbtMap tag) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (NBTOutputStream output = NbtUtils.createWriterLE(bytes)) {
            output.writeTag(tag.toBuilder().putInt("version", STORED_VERSION).build());
        }
        return bytes.toByteArray();
    }
}
