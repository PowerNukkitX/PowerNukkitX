package org.powernukkitx.level.enderdragon;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.item.EntityEnderCrystal;
import org.powernukkitx.entity.mob.EntityEnderDragon;
import org.powernukkitx.block.BlockBedrock;
import org.powernukkitx.block.BlockEndPortal;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.leveldb.LevelDBProvider;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.ObjectEndGateway;
import org.powernukkitx.level.generator.object.ObjectExitPortal;
import org.powernukkitx.level.generator.object.ObjectObsidianPillar;
import org.powernukkitx.level.Position;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.StringTag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class EnderDragonFight {
    private static final String TAG = "EnderDragonFight";
    private static final int GATEWAY_COUNT = 20;
    private static final int DRAGON_SPAWN_Y = 128;
    private static final int PLAYER_SCAN_INTERVAL = 20;
    private static final int CRYSTAL_SCAN_INTERVAL = 100;
    private static final int DRAGON_SCAN_INTERVAL = 1200;
    private static final int PREPARING_RESPAWN_TICKS = 100;
    private static final int PILLAR_RESPAWN_TICKS = 40;
    private static final int SUMMONING_DRAGON_TICKS = 100;
    private static final int RESPAWN_DURATION = PREPARING_RESPAWN_TICKS + PILLAR_RESPAWN_TICKS * 10 + SUMMONING_DRAGON_TICKS;
    private static final int[][] RESPAWN_CRYSTAL_OFFSETS = {{3, 0}, {-3, 0}, {0, 3}, {0, -3}};

    private final Level level;
    private final List<Integer> gateways = new ArrayList<>(GATEWAY_COUNT);
    private boolean dragonKilled;
    private boolean previouslyKilled;
    private boolean needsStateScanning;
    private String dragonUniqueId;
    private boolean nearbyPlayer;
    private int ticksSincePlayerScan = PLAYER_SCAN_INTERVAL + 1;
    private int ticksSinceCrystalScan;
    private int ticksSinceDragonSeen;
    private int ticksSinceRespawnCrystalRestore = CRYSTAL_SCAN_INTERVAL;
    private int respawnTicks = -1;
    private final List<EntityEnderCrystal> respawnCrystals = new ArrayList<>(4);
    private final List<String> respawnCrystalUniqueIds = new ArrayList<>(4);

    /**
     * Creates the End dragon fight controller for a level.
     *
     * @param level the End level that owns this controller
     */
    public EnderDragonFight(Level level) {
        this.level = level;
        load();
        if (gateways.isEmpty()) {
            for (int i = 0; i < GATEWAY_COUNT; i++) {
                gateways.add(i);
            }
            Collections.shuffle(gateways, new Random(level.getSeed()));
            save();
        }
    }

    /**
     * Updates the fight state from the owning level tick.
     */
    public void tick() {
        if (++ticksSincePlayerScan >= PLAYER_SCAN_INTERVAL) {
            nearbyPlayer = hasNearbyPlayer();
            ticksSincePlayerScan = 0;
        }
        if (!nearbyPlayer) {
            return;
        }
        if (needsStateScanning) {
            scanState();
            needsStateScanning = false;
            save();
        }
        if (respawnTicks >= 0) {
            if (respawnCrystals.isEmpty() && !restoreRespawnCrystals()) {
                return;
            }
            tickRespawn();
            return;
        }
        if (!dragonKilled) {
            if (dragonUniqueId == null || ++ticksSinceDragonSeen >= DRAGON_SCAN_INTERVAL) {
                if (findDragon() == null) {
                    createDragon();
                }
                ticksSinceDragonSeen = 0;
            }
        } else if (++ticksSinceCrystalScan >= CRYSTAL_SCAN_INTERVAL) {
            tryRespawn();
            ticksSinceCrystalScan = 0;
        }
    }

    /**
     * Records the death of the dragon managed by this fight.
     *
     * @param dragon the dragon that completed its death animation
     */
    public void setDragonKilled(EntityEnderDragon dragon) {
        if (dragonUniqueId != null && !dragonUniqueId.equals(dragon.getUniqueId().toString())) {
            return;
        }
        dragonKilled = true;
        dragonUniqueId = dragon.getUniqueId().toString();
        respawnTicks = -1;
        ticksSinceCrystalScan = 0;
        spawnPodium(true);
        if (!previouslyKilled) {
            int y = level.getHighestBlockAt(0, 0);
            level.setBlock(new Vector3(0, y + 1, 0), org.powernukkitx.block.Block.get(org.powernukkitx.block.Block.DRAGON_EGG));
        }
        spawnGateway();
        previouslyKilled = true;
        save();
    }

    private void tryRespawn() {
        int podiumY = getPodiumY();
        List<EntityEnderCrystal> crystals = new ArrayList<>(4);
        for (int[] offset : RESPAWN_CRYSTAL_OFFSETS) {
            EntityEnderCrystal crystal = null;
            for (Entity entity : level.getNearbyEntities(new SimpleAxisAlignedBB(offset[0] - 1, podiumY, offset[1] - 1, offset[0] + 2, podiumY + 4, offset[1] + 2))) {
                if (entity instanceof EntityEnderCrystal enderCrystal) {
                    crystal = enderCrystal;
                    break;
                }
            }
            if (crystal == null) {
                return;
            }
            crystals.add(crystal);
        }
        respawnCrystals.clear();
        respawnCrystalUniqueIds.clear();
        for (EntityEnderCrystal crystal : crystals) {
            crystal.setRespawning(true);
            crystal.setBeamTarget(new BlockVector3(0, podiumY + 1, 0));
            respawnCrystals.add(crystal);
            respawnCrystalUniqueIds.add(crystal.getUniqueId().toString());
        }
        respawnTicks = 0;
        spawnPodium(false);
        save();
    }

    private void tickRespawn() {
        int tick = respawnTicks++;
        if (respawnCrystals.stream().anyMatch(Entity::isClosed)) {
            abortRespawn();
            return;
        }
        if (tick >= PREPARING_RESPAWN_TICKS && tick < PREPARING_RESPAWN_TICKS + PILLAR_RESPAWN_TICKS * 10
                && (tick - PREPARING_RESPAWN_TICKS) % PILLAR_RESPAWN_TICKS == 0) {
            resetSpike((tick - PREPARING_RESPAWN_TICKS) / PILLAR_RESPAWN_TICKS);
        }
        if (tick < RESPAWN_DURATION) {
            return;
        }
        for (EntityEnderCrystal crystal : respawnCrystals) {
            crystal.setRespawning(false);
            crystal.close();
        }
        respawnCrystals.clear();
        respawnCrystalUniqueIds.clear();
        dragonKilled = false;
        respawnTicks = -1;
        ticksSinceDragonSeen = 0;
        createDragon();
        save();
    }

    private void resetSpike(int index) {
        Random random = new Random(level.getSeed());
        random.setSeed(random.nextLong() & 65535L);
        List<Integer> values = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            values.add(i);
        }
        Collections.shuffle(values, random);
        int value = values.get(index);
        int x = (int) Math.floor(42d * Math.cos(2d * (-Math.PI + Math.PI / 10d * index)));
        int z = (int) Math.floor(42d * Math.sin(2d * (-Math.PI + Math.PI / 10d * index)));
        int height = 76 + value * 3;
        BlockVector3 beamTarget = new BlockVector3(x, height + 1, z);
        for (EntityEnderCrystal crystal : respawnCrystals) {
            crystal.setBeamTarget(beamTarget);
        }
        for (Entity entity : level.getNearbyEntities(new SimpleAxisAlignedBB(x - 5, height - 1, z - 5, x + 6, height + 5, z + 6))) {
            if (entity instanceof EntityEnderCrystal) {
                entity.close();
            }
        }
        BlockManager manager = new BlockManager(level);
        new ObjectObsidianPillar(2 + value / 3, height, value == 1 || value == 2)
                .generate(manager, null, new Vector3(x, level.getHeightMap(x, z), z));
        manager.applySubChunkUpdate();
    }

    private void abortRespawn() {
        for (EntityEnderCrystal crystal : respawnCrystals) {
            crystal.setRespawning(false);
            crystal.clearBeamTarget();
        }
        respawnCrystals.clear();
        respawnCrystalUniqueIds.clear();
        respawnTicks = -1;
        spawnPodium(true);
        save();
    }

    private void spawnPodium(boolean active) {
        BlockManager manager = new BlockManager(level);
        new ObjectExitPortal(active).generate(manager, null, new Vector3(0, getPodiumY(), 0));
        manager.applySubChunkUpdate();
    }

    private void spawnGateway() {
        if (gateways.isEmpty()) {
            return;
        }
        int gateway = gateways.removeLast();
        int x = (int) Math.floor(96d * Math.cos(2d * (-Math.PI + Math.PI / 20d * gateway)));
        int z = (int) Math.floor(96d * Math.sin(2d * (-Math.PI + Math.PI / 20d * gateway)));
        BlockManager manager = new BlockManager(level);
        new ObjectEndGateway().generate(manager, null, new Vector3(x, 75, z));
        manager.applySubChunkUpdate();
    }

    private EntityEnderDragon findDragon() {
        EntityEnderDragon firstDragon = null;
        for (Entity entity : level.getEntities()) {
            if (entity instanceof EntityEnderDragon dragon && dragon.isAlive()) {
                if (dragonUniqueId == null || dragonUniqueId.equals(dragon.getUniqueId().toString())) {
                    dragonUniqueId = dragon.getUniqueId().toString();
                    return dragon;
                }
                firstDragon = dragon;
            }
        }
        if (firstDragon != null) {
            dragonUniqueId = firstDragon.getUniqueId().toString();
        }
        return firstDragon;
    }

    private void createDragon() {
        Entity entity = Entity.createEntity(Entity.ENDER_DRAGON, new Position(0.5, DRAGON_SPAWN_Y, 0.5, level));
        if (entity instanceof EntityEnderDragon dragon) {
            level.addEntity(dragon);
            dragon.spawnToAll();
            dragonUniqueId = dragon.getUniqueId().toString();
            save();
        }
    }

    private int getPodiumY() {
        int highest = level.getHighestBlockAt(0, 0);
        for (int y = highest; y >= level.getMinHeight() + 4; y--) {
            if (level.getBlock(0, y, 0) instanceof BlockBedrock
                    && level.getBlock(0, y - 1, 0) instanceof BlockBedrock
                    && level.getBlock(0, y - 2, 0) instanceof BlockBedrock
                    && level.getBlock(0, y - 3, 0) instanceof BlockBedrock) {
                return y - 3;
            }
        }
        return Math.max(level.getMinHeight() + 1, highest - 3);
    }

    private boolean restoreRespawnCrystals() {
        if (++ticksSinceRespawnCrystalRestore < CRYSTAL_SCAN_INTERVAL || respawnCrystalUniqueIds.isEmpty()) {
            return false;
        }
        ticksSinceRespawnCrystalRestore = 0;
        List<EntityEnderCrystal> crystals = new ArrayList<>(respawnCrystalUniqueIds.size());
        for (String uniqueId : respawnCrystalUniqueIds) {
            EntityEnderCrystal crystal = null;
            for (Entity entity : level.getEntities()) {
                if (entity instanceof EntityEnderCrystal enderCrystal && uniqueId.equals(enderCrystal.getUniqueId().toString())) {
                    crystal = enderCrystal;
                    break;
                }
            }
            if (crystal == null) {
                return false;
            }
            crystals.add(crystal);
        }
        respawnCrystals.addAll(crystals);
        return true;
    }

    private void scanState() {
        boolean activePortal = false;
        int highest = level.getHighestBlockAt(0, 0);
        for (int y = highest; y >= level.getMinHeight(); y--) {
            if (level.getBlock(0, y, 0) instanceof BlockEndPortal) {
                activePortal = true;
                break;
            }
        }
        EntityEnderDragon dragon = findDragon();
        previouslyKilled = activePortal;
        dragonKilled = dragon == null;
        if (!previouslyKilled && dragonKilled) {
            dragonKilled = false;
        }
        if (!activePortal && dragon == null) {
            spawnPodium(false);
        }
    }

    private boolean hasNearbyPlayer() {
        for (var player : level.getPlayers().values()) {
            if (player.distanceSquared(new Vector3(0, 128, 0)) <= 192 * 192) {
                return true;
            }
        }
        return false;
    }

    private void load() {
        if (level.getProvider() instanceof LevelDBProvider provider) {
            CompoundTag tag = provider.getWorldDynamicProperties().getCompound(TAG);
            needsStateScanning = !provider.getWorldDynamicProperties().contains(TAG) || tag.getBoolean("NeedsStateScanning");
            dragonKilled = tag.getBoolean("DragonKilled");
            previouslyKilled = tag.getBoolean("PreviouslyKilled");
            dragonUniqueId = tag.getString("DragonUniqueId");
            if (dragonUniqueId.isEmpty()) {
                dragonUniqueId = null;
            }
            respawnTicks = tag.getInt("RespawnTicks", -1);
            for (StringTag crystalUniqueId : tag.getList("RespawnCrystals", StringTag.class).getAll()) {
                respawnCrystalUniqueIds.add(crystalUniqueId.parseValue());
            }
            for (int gateway : tag.getIntArray("Gateways")) {
                gateways.add(gateway);
            }
        }
    }

    private void save() {
        if (level.getProvider() instanceof LevelDBProvider provider) {
            CompoundTag properties = provider.getWorldDynamicProperties();
            properties.putCompound(TAG, new CompoundTag()
                    .putBoolean("DragonKilled", dragonKilled)
                    .putBoolean("PreviouslyKilled", previouslyKilled)
                    .putBoolean("NeedsStateScanning", needsStateScanning)
                    .putString("DragonUniqueId", dragonUniqueId == null ? "" : dragonUniqueId)
                    .putInt("RespawnTicks", respawnTicks)
                    .putList("RespawnCrystals", new ListTag<StringTag>().addAll(respawnCrystalUniqueIds.stream().map(StringTag::new).toList()))
                    .putIntArray("Gateways", gateways.stream().mapToInt(Integer::intValue).toArray()));
            provider.setWorldDynamicPropertiesDirty(true);
        }
    }
}
