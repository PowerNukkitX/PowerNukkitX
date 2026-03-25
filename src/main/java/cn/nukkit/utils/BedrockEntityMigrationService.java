package cn.nukkit.utils;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.level.ChunkLoadEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.leveldb.LevelDBProvider;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.*;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BedrockEntityMigrationService implements Listener {

    private final Server server;

    public static final Map<Long, List<CompoundTag>> MIGRATION_MAP = new ConcurrentHashMap<>();
    private static final Map<String, Integer> DEBUG_COUNTS = new ConcurrentHashMap<>();

    private int lastLoggedRemaining = -1;

    public BedrockEntityMigrationService(Server server) {
        this.server = server;
    }

    private void debugEntity(String id) {
        DEBUG_COUNTS.merge(id, 1, Integer::sum);
    }

    public void loadMigrationFile() {
        File file = new File(server.getDataPath() + "entity_migration.dat");
        if (!file.exists()) return;

        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {

            int size = in.readInt();
            Map<Long, List<CompoundTag>> map = new HashMap<>();

            for (int i = 0; i < size; i++) {
                long key = in.readLong();

                int listSize = in.readInt();
                List<CompoundTag> list = new ArrayList<>();

                for (int j = 0; j < listSize; j++) {
                    int len = in.readInt();
                    byte[] data = new byte[len];
                    in.readFully(data);

                    CompoundTag tag = NBTIO.read(data, ByteOrder.LITTLE_ENDIAN);
                    list.add(tag);
                }

                map.put(key, list);
            }

            MIGRATION_MAP.clear();
            MIGRATION_MAP.putAll(map);

            if (!map.isEmpty()) {
                server.getLogger().info("Loaded Entity Migration Data (" + map.size() + " chunks left to migrate)");
            }

        } catch (Exception e) {
            server.getLogger().error("Failed to load migration data", e);
        }
    }

    public void saveMigrationFile(Map<Long, List<CompoundTag>> map) {
        File file = new File(server.getDataPath() + "entity_migration.dat");

        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {

            out.writeInt(map.size());

            for (Map.Entry<Long, List<CompoundTag>> entry : map.entrySet()) {
                out.writeLong(entry.getKey());

                List<CompoundTag> list = entry.getValue();
                out.writeInt(list.size());

                for (CompoundTag tag : list) {
                    byte[] bytes = NBTIO.write(tag, ByteOrder.LITTLE_ENDIAN);
                    out.writeInt(bytes.length);
                    out.write(bytes);
                }
            }

            server.getLogger().info("Saved migration data (" + map.size() + " chunks left to migrate)");

        } catch (Exception e) {
            server.getLogger().error("Failed to save migration data", e);
        }
    }

    public void saveProgress() {
        saveMigrationFile(new HashMap<>(MIGRATION_MAP));
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        var chunk = event.getChunk();

        int cx = chunk.getX();
        int cz = chunk.getZ();
        long key = (((long) cx) << 32) | (cz & 0xffffffffL);

        List<CompoundTag> list = MIGRATION_MAP.remove(key);
        if (list == null) return;

        int remaining = MIGRATION_MAP.size();

        if (remaining == 0 || remaining / 10000 != lastLoggedRemaining / 10000) {
            server.getLogger().info("Entity Migration progress: " + remaining + " Chunks Remaining");
            lastLoggedRemaining = remaining;
        }

        int injected = 0;

        for (CompoundTag clean : list) {
            try {
                String identifier = clean.getString("identifier");

                ListTag<?> pos = clean.getList("Pos");

                double x = 0, y = 0, z = 0;
                if (pos != null && pos.size() >= 3) {
                    x = ((NumberTag<?>) pos.get(0)).getData().doubleValue();
                    y = ((NumberTag<?>) pos.get(1)).getData().doubleValue();
                    z = ((NumberTag<?>) pos.get(2)).getData().doubleValue();
                }

                final double fx = x;
                final double fy = y;
                final double fz = z;
                final String name = clean.contains("CustomName") ? clean.getString("CustomName") : null;

                server.getScheduler().scheduleTask(() -> {
                    try {
                        Entity entity = Entity.createEntity(identifier, chunk, clean);

                        if (entity == null) {
                            debugEntity(identifier);
                            return;
                        }

                        entity.setPosition(new Vector3(fx, fy, fz));
                        chunk.addEntity(entity);

                        if (name != null) {
                            entity.setNameTag(name);
                            entity.setNameTagVisible(true);
                        }

                        entity.setPersistent(true);

                        entity.setMotion(new Vector3(0, -0.2, 0));
                        entity.onGround = false;

                        entity.spawnToAll();
                        entity.scheduleUpdate();

                        chunk.setChanged();

                        if (entity instanceof cn.nukkit.entity.item.EntityArmorStand stand) {
                            if (clean.contains("Armor")) {
                                ListTag<?> armor = clean.getList("Armor");

                                try {
                                    if (armor.size() >= 4) {
                                        stand.setBoots(parseItem(armor.get(0)));
                                        stand.setLeggings(parseItem(armor.get(1)));
                                        stand.setChestplate(parseItem(armor.get(2)));
                                        stand.setHelmet(parseItem(armor.get(3)));
                                    }
                                } catch (Exception ignored) {}
                            }
                        }

                    } catch (Exception e) {
                        debugEntity(identifier);
                        server.getLogger().debug("Entity injection error: " + identifier, e);
                    }
                });

                injected++;

            } catch (Exception e) {
                debugEntity(clean.getString("identifier"));
                server.getLogger().debug("Entity scheduling error", e);
            }
        }

        if (!DEBUG_COUNTS.isEmpty()) {
            server.getLogger().warning("Failed to Spawn Entity Summary:");
            DEBUG_COUNTS.forEach((k, v) -> server.getLogger().warning(k + " -> " + v));
            DEBUG_COUNTS.clear();
        }

        if (MIGRATION_MAP.size() % 100 == 0) {
            saveProgress();
        }
    }

    private DB getDB(LevelDBProvider provider) {
        try {
            Field storageField = LevelDBProvider.class.getDeclaredField("storage");
            storageField.setAccessible(true);

            Object storage = storageField.get(provider);

            Field dbField = storage.getClass().getDeclaredField("db");
            dbField.setAccessible(true);

            return (DB) dbField.get(storage);

        } catch (Exception e) {
            server.getLogger().error("DB access failed", e);
            return null;
        }
    }

    private boolean isOverworld(CompoundTag nbt) {
        return !nbt.contains("Dimension") || nbt.getInt("Dimension") == 0;
    }

    public void migrateAll(Level level) {
        LevelProvider provider = level.getProvider();

        if (!(provider instanceof LevelDBProvider levelDBProvider)) {
            server.getLogger().warning("Not LevelDB");
            return;
        }

        DB db = getDB(levelDBProvider);
        if (db == null) return;

        server.getLogger().info("Starting BDS Entity Scan");

        int scanned = 0;
        int stored = 0;

        Map<Long, List<CompoundTag>> chunkMap = new HashMap<>();

        try (DBIterator it = db.iterator()) {
            it.seekToFirst();

            boolean inActorSection = false;

            while (it.hasNext()) {
                var entry = it.next();
                String key = new String(entry.getKey(), StandardCharsets.UTF_8);

                if (!key.startsWith("actorprefix")) {
                    if (inActorSection) break;
                    continue;
                }

                inActorSection = true;
                scanned++;

                try {
                    CompoundTag nbt = NBTIO.read(entry.getValue(), ByteOrder.LITTLE_ENDIAN);

                    if (!isOverworld(nbt)) continue;

                    CompoundTag clean = convert(nbt);
                    if (clean == null) continue;

                    ListTag<?> pos = clean.getList("Pos");
                    if (pos == null || pos.size() < 3) {
                        debugEntity(clean.getString("identifier"));
                        continue;
                    }

                    double x = ((NumberTag<?>) pos.get(0)).getData().doubleValue();
                    double z = ((NumberTag<?>) pos.get(2)).getData().doubleValue();

                    int cx = (int) Math.floor(x) >> 4;
                    int cz = (int) Math.floor(z) >> 4;

                    long chunkKey = (((long) cx) << 32) | (cz & 0xffffffffL);

                    chunkMap.computeIfAbsent(chunkKey, k -> new ArrayList<>()).add(clean);
                    stored++;

                } catch (Exception e) {
                    server.getLogger().debug("Entity conversion error", e);
                }
            }

        } catch (Exception e) {
            server.getLogger().error("Scan failed", e);
        }

        server.getLogger().info("BDS Entity scan complete");
        server.getLogger().info("Total Scanned: " + scanned);
        server.getLogger().info("Entities Stored: " + stored);
        server.getLogger().info("Chunks to Migrate: " + chunkMap.size());

        saveMigrationFile(chunkMap);
    }

    private CompoundTag convert(CompoundTag nbt) {
        if (!nbt.contains("identifier")) return null;

        String id = nbt.getString("identifier").toLowerCase();
        if (id.isEmpty()) return null;

        if (id.equals("minecraft:item") ||
                id.contains("arrow") ||
                id.contains("xp_orb") ||
                id.contains("wither_skull") ||
                id.contains("fireball") ||
                id.contains("enderman") ||
                id.contains("piglin") ||
                id.contains("zombified") ||
                id.contains("zombie_pigman") ||
                id.contains("drowned") ||
                id.contains("strider") ||
                id.contains("painting") ||
                id.contains("falling_block")) {
            return null;
        }

        if (id.equals("minecraft:zombie_villager_v2")) id = "minecraft:zombie_villager";
        if (id.equals("minecraft:villager_v2")) id = "minecraft:villager";

        ListTag<?> posList = nbt.getList("Pos");
        if (posList == null || posList.size() < 3) return null;

        double x = ((NumberTag<?>) posList.get(0)).getData().doubleValue();
        double y = ((NumberTag<?>) posList.get(1)).getData().doubleValue();
        double z = ((NumberTag<?>) posList.get(2)).getData().doubleValue();

        CompoundTag clean = Entity.getDefaultNBT(new Vector3(x, y + 1, z));

        clean.putString("identifier", id);

        float health = 20f;

        if (nbt.contains("Attributes")) {
            ListTag<?> attributes = nbt.getList("Attributes");

            for (Object obj : attributes.getAll()) {
                if (obj instanceof CompoundTag attr &&
                        "minecraft:health".equals(attr.getString("Name"))) {

                    health = attr.getFloat("Current");
                    if (health <= 0) health = attr.getFloat("Base");
                    break;
                }
            }
        } else if (nbt.contains("Health")) {
            health = nbt.getFloat("Health");
        }

        if (health <= 0 || health > 1000) {
            health = 20f;
        }

        clean.putFloat("Health", health);
        clean.putFloat("MaxHealth", health);

        if (nbt.contains("CustomName")) {
            clean.putString("CustomName", nbt.getString("CustomName"));
        }

        if (nbt.contains("Armor")) {
            clean.putList("Armor", nbt.getList("Armor"));
        }

        clean.putBoolean("NoAI", false);
        clean.putBoolean("OnGround", true);
        clean.putShort("Fire", (short) 0);
        clean.putShort("Air", (short) 300);
        clean.putFloat("FallDistance", 0f);

        return clean;
    }

    private Item parseItem(Object obj) {
        if (!(obj instanceof CompoundTag tag)) return Item.get("minecraft:air");

        if (!tag.contains("Name")) return Item.get("minecraft:air");

        Item item = Item.get(tag.getString("Name"));

        item.setCount(tag.getByte("Count", (byte) 1));
        item.setDamage(tag.getInt("Damage", 0));

        if (tag.contains("tag")) item.setNamedTag(tag.getCompound("tag"));

        return item;
    }

    public void cleanupMigrationFileIfDone() {
        File file = new File(server.getDataPath() + "entity_migration.dat");

        if (!file.exists()) return;

        if (MIGRATION_MAP.isEmpty()) {
            if (file.delete()) {
                server.getLogger().info("Entity migration complete. Cleaned up migration file.");
            } else {
                server.getLogger().warning("Failed to delete migration file.");
            }
        }
    }
}