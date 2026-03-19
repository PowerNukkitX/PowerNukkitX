package cn.nukkit.utils;

import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.leveldb.LevelDBProvider;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.*;
import org.iq80.leveldb.DB;

import java.io.ByteArrayInputStream;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class BedrockMigrationService {

    private final Server server;

    public BedrockMigrationService(Server server) {
        this.server = server;
    }

    public CompoundTag migrate(UUID uuid) {

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

            CompoundTag identity = NBTIO.read(
                    new ByteArrayInputStream(identityBytes),
                    ByteOrder.LITTLE_ENDIAN
            );

            String serverId = identity.getString("ServerId");

            if (serverId == null || serverId.isEmpty()) {
                return null;
            }

            byte[] gameplayBytes = db.get(serverId.getBytes(StandardCharsets.UTF_8));

            if (gameplayBytes == null) {
                return null;
            }

            CompoundTag gameplay = NBTIO.read(
                    new ByteArrayInputStream(gameplayBytes),
                    ByteOrder.LITTLE_ENDIAN
            );

            CompoundTag converted = convertBedrockNBT(gameplay, level);

            server.getLogger().debug("Migration Successful for " + uuid);

            return converted;

        } catch (Exception e) {
            server.getLogger().error("Migration failed for " + uuid, e);
        }

        return null;
    }

    private CompoundTag convertBedrockNBT(CompoundTag tag, Level level) {

        // Position
        ListTag<?> posRaw = tag.getList("Pos");

        double x = 0, y = 100, z = 0;
        if (posRaw != null && posRaw.size() >= 3) {
            x = ((Number) posRaw.get(0).parseValue()).doubleValue();
            y = ((Number) posRaw.get(1).parseValue()).doubleValue();
            z = ((Number) posRaw.get(2).parseValue()).doubleValue();
        }

        ListTag<DoubleTag> pos = new ListTag<>();
        pos.add(new DoubleTag(x));
        pos.add(new DoubleTag(y));
        pos.add(new DoubleTag(z));

        ListTag<DoubleTag> motion = new ListTag<>();
        motion.add(new DoubleTag(0));
        motion.add(new DoubleTag(0));
        motion.add(new DoubleTag(0));

        ListTag<FloatTag> rotation = new ListTag<>();
        rotation.add(new FloatTag(0));
        rotation.add(new FloatTag(0));

        // Inventory
        ListTag<CompoundTag> inventoryRaw = tag.getList("Inventory", CompoundTag.class);
        ListTag<CompoundTag> inventory = new ListTag<>();

        if (inventoryRaw != null) {
            for (int i = 0; i < inventoryRaw.size(); i++) {
                CompoundTag converted = convertItem(inventoryRaw.get(i), i);
                if (converted != null) {
                    inventory.add(converted);
                }
            }
        }

        // Armor -> Inventory
        ListTag<CompoundTag> armorRaw = tag.getList("Armor", CompoundTag.class);

        if (armorRaw != null) {
            for (int i = 0; i < armorRaw.size(); i++) {

                int slot;
                switch (i) {
                    case 0: slot = 36; break; // helmet
                    case 1: slot = 37; break; // chestplate / elytra
                    case 2: slot = 38; break; // leggings
                    case 3: slot = 39; break; // boots
                    default: continue;
                }

                CompoundTag converted = convertItem(armorRaw.get(i), slot);
                if (converted != null) {
                    inventory.add(converted);
                }
            }
        }

        // Offhand
        ListTag<CompoundTag> offhandRaw = tag.getList("Offhand", CompoundTag.class);

        if (offhandRaw != null && offhandRaw.size() > 0) {
            CompoundTag converted = convertItem(offhandRaw.get(0), 40);
            if (converted != null) {
                inventory.add(converted);
            }
        }

        // Ender Chest
        ListTag<CompoundTag> enderRaw = tag.getList("EnderChestInventory", CompoundTag.class);
        ListTag<CompoundTag> ender = new ListTag<>();

        if (enderRaw != null) {
            for (int i = 0; i < enderRaw.size(); i++) {

                CompoundTag item = enderRaw.get(i);

                // remove Bedrock-only block data
                if (item.contains("Block")) {
                    item.remove("Block");
                }

                CompoundTag converted = convertItem(item, i);
                if (converted != null) {
                    ender.add(converted);
                }
            }
        }

        // Stats
        float health = tag.contains("Health") ? tag.getFloat("Health") : 20f;
        int food = tag.contains("foodLevel") ? tag.getInt("foodLevel") : 20;
        float saturation = tag.contains("foodSaturationLevel") ? tag.getFloat("foodSaturationLevel") : 20f;

        int exp = tag.contains("EXP") ? tag.getInt("EXP") : 0;
        int expLevel = tag.contains("expLevel") ? tag.getInt("expLevel") : 0;
        float expProgress = tag.contains("EXPProgress") ? tag.getFloat("EXPProgress") : 0f;

        int gamemode = tag.contains("playerGameType") ? tag.getInt("playerGameType") : 0;
        int selectedSlot = tag.contains("SelectedInventorySlot") ? tag.getInt("SelectedInventorySlot") : 0;

        short air = tag.contains("Air") ? tag.getShort("Air") : 300;
        short fire = tag.contains("Fire") ? tag.getShort("Fire") : 0;
        float fallDistance = tag.contains("FallDistance") ? tag.getFloat("FallDistance") : 0f;
        boolean onGround = tag.contains("OnGround") && tag.getBoolean("OnGround");

        // Active Effects
        ListTag<CompoundTag> effects = tag.contains("ActiveEffects")
                ? tag.getList("ActiveEffects", CompoundTag.class)
                : new ListTag<>();

        // Convert BDS -> PNX
        CompoundTag pnx = new CompoundTag();

        pnx.putList("Pos", pos);
        pnx.putList("Motion", motion);
        pnx.putList("Rotation", rotation);
        pnx.putString("Level", level.getName());
        pnx.putInt("DimensionId", tag.contains("DimensionId") ? tag.getInt("DimensionId") : 0);
        pnx.putString("SpawnLevel", level.getName());
        if (tag.contains("SpawnX") && tag.contains("SpawnY") && tag.contains("SpawnZ")) {

            int sx = tag.getInt("SpawnX");
            int sy = tag.getInt("SpawnY");
            int sz = tag.getInt("SpawnZ");

            if (!(sx == 0 && sy == 0 && sz == 0) && sy > 0) {
                pnx.putInt("SpawnX", sx);
                pnx.putInt("SpawnY", sy);
                pnx.putInt("SpawnZ", sz);
            }
        }

        pnx.putList("Inventory", inventory);
        pnx.putList("EnderItems", ender);

        pnx.putFloat("Health", health);
        pnx.putInt("foodLevel", food);
        pnx.putFloat("foodSaturationLevel", saturation);
        pnx.putFloat("AbsorptionAmount",
                tag.contains("AbsorptionAmount") ? tag.getFloat("AbsorptionAmount") : 0f);

        pnx.putInt("EXP", exp);
        pnx.putInt("expLevel", expLevel);
        pnx.putFloat("EXPProgress", expProgress);
        pnx.putList("ActiveEffects", effects);

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

        return pnx;
    }
    private CompoundTag convertItem(CompoundTag item, int slot) {

        if (!item.contains("Name")) return null;

        String name = item.getString("Name");

        if (name.startsWith("minecraft:")) {
            name = name.substring(10);
        }

        Item nukkitItem = Item.get(name);

        if (nukkitItem == null) {
            if (!item.contains("Slot")) {
                item.putByte("Slot", (byte) slot);
            }
            return item;
        }

        nukkitItem.setCount(item.getByte("Count"));

        if (item.contains("tag")) {
            nukkitItem.setCompoundTag(item.getCompound("tag"));
        }

        return NBTIO.putItemHelper(nukkitItem, slot);
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
            CompoundTag identity = NBTIO.read(
                    new ByteArrayInputStream(identityBytes),
                    ByteOrder.LITTLE_ENDIAN
            );

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
