package cn.nukkit.utils;

import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.leveldb.LevelDBProvider;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.nbt.NbtUtils;
import org.iq80.leveldb.DB;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BedrockMigrationService {

    private final Server server;

    public BedrockMigrationService(Server server) {
        this.server = server;
    }

    public NbtMap migrate(UUID uuid) {

        server.getLogger().debug("Started Migration for " + uuid);

        Level level = server.getDefaultLevel();

        if (!(level.getProvider() instanceof LevelDBProvider provider)) {
            server.getLogger().warning("Level is not LevelDB");
            return null;
        }

        try {
            DB db = provider.getStorage().getDb();

            String identityKey = "player_" + uuid;

            byte[] identityBytes = db.get(identityKey.getBytes(StandardCharsets.UTF_8));

            if (identityBytes == null) {
                return null;
            }

            NbtMap identity = (NbtMap) NbtUtils.createReaderLE(new ByteArrayInputStream(identityBytes)).readTag();

            String serverId = identity.getString("ServerId");

            if (serverId == null || serverId.isEmpty()) {
                return null;
            }

            byte[] gameplayBytes = db.get(serverId.getBytes(StandardCharsets.UTF_8));

            if (gameplayBytes == null) {
                return null;
            }

            NbtMap gameplay = (NbtMap) NbtUtils.createReaderLE(new ByteArrayInputStream(gameplayBytes)).readTag();

            NbtMap converted = convertBedrockNBT(gameplay, level);

            server.getLogger().debug("Migration Successful for " + uuid);

            return converted;

        } catch (Exception e) {
            server.getLogger().error("Migration failed for " + uuid, e);
        }

        return null;
    }

    private NbtMap convertBedrockNBT(NbtMap tag, Level level) {

        // Position
        List<Double> posRaw = tag.getList("Pos", NbtType.DOUBLE);

        final List<Double> motion = Arrays.asList(0.0, 0.0, 0.0);
        final List<Float> rotation = Arrays.asList(0f, 0f);

        // Inventory
        List<NbtMap> inventoryRaw = tag.getList("Inventory", NbtType.COMPOUND);
        List<NbtMap> inventory = new ObjectArrayList<>();

        if (inventoryRaw != null) {
            for (int i = 0; i < inventoryRaw.size(); i++) {
                NbtMap converted = convertItem(inventoryRaw.get(i), i);
                if (converted != null) {
                    inventory.add(converted);
                }
            }
        }

        // Armor -> Inventory
        List<NbtMap> armorRaw = tag.getList("Armor", NbtType.COMPOUND);

        if (armorRaw != null) {
            for (int i = 0; i < armorRaw.size(); i++) {

                int slot = switch (i) {
                    case 0 -> 36; // helmet
                    case 1 -> 37; // chestplate
                    case 2 -> 38; // leggings
                    case 3 -> 39; // boots
                    default -> -1;
                };

                if (slot == -1) continue;

                NbtMap converted = convertItem(armorRaw.get(i), slot);
                if (converted != null) {
                    inventory.add(converted);
                }
            }
        }

        // Offhand
        List<NbtMap> offhandRaw = tag.getList("Offhand", NbtType.COMPOUND);

        if (offhandRaw != null && offhandRaw.size() > 0) {
            NbtMap converted = convertItem(offhandRaw.get(0), 40);
            if (converted != null) {
                inventory.add(converted);
            }
        }

        // Ender Chest
        List<NbtMap> enderRaw = tag.getList("EnderChestInventory", NbtType.COMPOUND);
        List<NbtMap> ender = new ObjectArrayList<>();

        if (enderRaw != null) {
            for (int i = 0; i < enderRaw.size(); i++) {

                NbtMap item = enderRaw.get(i);

                // remove Bedrock-only block data
                if (item.containsKey("Block")) {
                    item = NbtHelper.remove(item, "Block");
                }

                NbtMap converted = convertItem(item, i);
                if (converted != null) {
                    ender.add(converted);
                }
            }
        }

        // Stats
        float health = tag.getFloat("Health", 20f);
        int food = tag.getInt("foodLevel", 20);
        float saturation = tag.getFloat("foodSaturationLevel", 20f);

        int exp = tag.getInt("EXP");
        int expLevel = tag.getInt("expLevel");
        float expProgress = tag.getFloat("EXPProgress");

        int gamemode = tag.getInt("playerGameType");
        int selectedSlot = tag.getInt("SelectedInventorySlot");

        short air = tag.getShort("Air", (short) 300);
        short fire = tag.getShort("Fire");
        float fallDistance = tag.getFloat("FallDistance");
        boolean onGround = tag.getBoolean("OnGround");

        // Active Effects
        List<NbtMap> effects = tag.getList("ActiveEffects", NbtType.COMPOUND);
        if (effects == null) {
            effects = new ObjectArrayList<>();
        }

        // Convert BDS -> PNX
        NbtMapBuilder pnx = NbtMap.builder();

        pnx.putList("Pos", NbtType.DOUBLE, posRaw);
        pnx.putList("Motion", NbtType.DOUBLE, motion);
        pnx.putList("Rotation", NbtType.FLOAT, rotation);
        pnx.putString("Level", level.getName());
        pnx.putInt("DimensionId", tag.getInt("DimensionId"));
        pnx.putString("SpawnLevel", level.getName());
        if (tag.containsKey("SpawnX") && tag.containsKey("SpawnY") && tag.containsKey("SpawnZ")) {

            int sx = tag.getInt("SpawnX");
            int sy = tag.getInt("SpawnY");
            int sz = tag.getInt("SpawnZ");

            if (!(sx == 0 && sy == 0 && sz == 0) && sy > 0) {
                pnx.putInt("SpawnX", sx);
                pnx.putInt("SpawnY", sy);
                pnx.putInt("SpawnZ", sz);
            }
        }

        pnx.putList("Inventory", NbtType.COMPOUND, inventory);
        pnx.putList("EnderItems", NbtType.COMPOUND, ender);

        pnx.putFloat("Health", health);
        pnx.putInt("foodLevel", food);
        pnx.putFloat("foodSaturationLevel", saturation);
        pnx.putFloat("AbsorptionAmount", tag.getFloat("AbsorptionAmount"));

        pnx.putInt("EXP", exp);
        pnx.putInt("expLevel", expLevel);
        pnx.putFloat("EXPProgress", expProgress);
        pnx.putList("ActiveEffects", NbtType.COMPOUND, effects);

        pnx.putInt("permissionsLevel", 1);
        pnx.putInt("playerPermissionsLevel", 1);
        pnx.putInt("playerGameType", gamemode);
        pnx.putInt("SelectedInventorySlot", selectedSlot);

        pnx.putShort("Air", air);
        pnx.putShort("Fire", fire);
        pnx.putFloat("FallDistance", fallDistance);
        pnx.putBoolean("OnGround", onGround);

        long now = System.currentTimeMillis() / 1000;
        pnx.putLong("firstPlayed", now);
        pnx.putLong("lastPlayed", now);

        pnx.putBoolean("BedrockMigrated", true);

        return pnx.build();
    }

    private NbtMap convertItem(NbtMap item, int slot) {

        if (!item.containsKey("Name")) return null;

        String name = item.getString("Name");

        if (name.startsWith("minecraft:")) {
            name = name.substring(10);
        }

        Item nukkitItem = Item.get(name);

        if (nukkitItem == null) {
            if (!item.containsKey("Slot")) {
                item = item.toBuilder().putByte("Slot", (byte) slot).build();
            }
            return item;
        }

        nukkitItem.setCount(item.getByte("Count"));

        if (item.containsKey("tag")) {
            nukkitItem.setCompoundTag(item.getCompound("tag"));
        }

        return ItemHelper.write(nukkitItem, slot);
    }

    public boolean hasBedrockData(UUID uuid) {
        try {
            Level level = server.getDefaultLevel();

            if (!(level.getProvider() instanceof LevelDBProvider provider)) {
                return false;
            }

            DB db = provider.getStorage().getDb();

            String identityKey = "player_" + uuid;
            byte[] identityBytes = db.get(identityKey.getBytes(StandardCharsets.UTF_8));

            if (identityBytes == null) {
                return false;
            }

            // Deeper Validation
            NbtMap identity = (NbtMap) NbtUtils.createReaderLE(new ByteArrayInputStream(identityBytes)).readTag();

            String serverId = identity.getString("ServerId");

            if (serverId == null || serverId.isEmpty()) {
                return false;
            }

            byte[] gameplayBytes = db.get(serverId.getBytes(StandardCharsets.UTF_8));

            return gameplayBytes != null && gameplayBytes.length > 0;

        } catch (Exception e) {
            return false;
        }
    }
}
