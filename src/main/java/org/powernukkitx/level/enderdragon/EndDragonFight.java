package org.powernukkitx.level.enderdragon;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockBedrock;
import org.powernukkitx.block.BlockEndGateway;
import org.powernukkitx.blockentity.BlockEntityEndGateway;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.item.EntityEnderCrystal;
import org.powernukkitx.entity.mob.EntityEnderDragon;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.format.ChunkFinalizationState;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.format.leveldb.LevelDBStorage;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.ObjectEndGateway;
import org.powernukkitx.level.generator.object.ObjectExitPortal;
import org.powernukkitx.level.generator.object.ObjectObsidianPillar;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.IntTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Coordinates Ender Dragon fight state for an End level. It tracks dragon life-cycle, exit portal and gateway
 * generation, respawn crystals, boss event updates, and persistent fight data.
 *
 * @author Curse
 */
public final class EndDragonFight {
    private final Level level;
    private final LevelDBStorage storage;
    private CompoundTag root = new CompoundTag();
    private CompoundTag data = new CompoundTag();
    private CompoundTag fight = new CompoundTag();

    private boolean storedState;
    private byte dragonFightVersion;
    private boolean dragonKilled;
    private boolean dragonSpawned;
    private long dragonUniqueId;
    private boolean hasExitPortalLocation;
    private BlockVector3 exitPortalLocation = new BlockVector3(-1, -1, -1);
    private static final int RESPAWN_STAGE_NONE = 0;
    private static final int RESPAWN_STAGE_START = 1;
    private static final int RESPAWN_STAGE_PREPARING = 2;
    private static final int RESPAWN_STAGE_PILLARS = 3;
    private static final int RESPAWN_STAGE_DRAGON = 4;
    private static final BlockVector3 DRAGON_SPAWN_POSITION = new BlockVector3(0, 128, 0);

    private boolean hasGateways;
    private final List<Integer> gateways = new ArrayList<>(20);
    private final List<EntityEnderCrystal> respawnCrystals = new ArrayList<>(4);
    private int respawnStage;
    private int respawnTime;
    private boolean respawnRecoveryPending;
    private boolean previouslyKilled;
    private boolean initialized;

    /**
     * Creates a new EndDragonFight instance.
     *
     * @param level value for this API
     * @param storage value for this API
     */
    public EndDragonFight(Level level, LevelDBStorage storage) {
        this.level = level;
        this.storage = storage;
    }

    /**
     * Initializes the managed state.
     */
    public void initialize() {
        if (initialized) return;
        initialized = true;
        load();
    }

    private void load() {
        CompoundTag stored = storage.readTheEndData();
        if (stored == null) {
            return;
        }

        this.root = stored.copy();
        if (!root.containsCompound("data")) {
            return;
        }

        this.data = root.getCompound("data").copy();
        if (!data.containsCompound("DragonFight")) {
            return;
        }

        this.fight = data.getCompound("DragonFight").copy();
        this.storedState = true;
        this.dragonFightVersion = fight.getByte("DragonFightVersion");
        this.dragonKilled = fight.getByte("DragonKilled") != 0;
        this.dragonSpawned = fight.getByte("DragonSpawned") != 0;
        this.dragonUniqueId = fight.getLong("DragonUUID");
        this.respawnStage = fight.getByte("IsRespawning") != 0 ? RESPAWN_STAGE_START : RESPAWN_STAGE_NONE;
        this.respawnRecoveryPending = this.respawnStage != RESPAWN_STAGE_NONE;
        this.previouslyKilled = fight.getByte("PreviouslyKilled") != 0;

        if (fight.containsList("ExitPortalLocation")) {
            ListTag<IntTag> exitPortal = fight.getList("ExitPortalLocation", IntTag.class);
            if (exitPortal.size() == 3) {
                this.exitPortalLocation = new BlockVector3(exitPortal.get(0).getData(), exitPortal.get(1).getData(), exitPortal.get(2).getData());
                this.hasExitPortalLocation = true;
            }
        }

        if (fight.containsList("Gateways")) {
            ListTag<IntTag> storedGateways = fight.getList("Gateways", IntTag.class);
            for (int i = 0; i < storedGateways.size(); i++) {
                gateways.add(storedGateways.get(i).getData());
            }
            this.hasGateways = true;
        }
    }

    /**
     * Generates the exit portal.
     *
     * @param active value for this API
     * @return the requested value
     */
    public BlockVector3 spawnExitPortal(boolean active) {
        BlockVector3 portalLocation = resolveExitPortalLocation();
        BlockManager manager = new BlockManager(level);
        new ObjectExitPortal(active).generate(manager, null, portalLocation.asVector3());
        manager.applySubChunkUpdate();
        return portalLocation;
    }

    /**
     * Records the spawned dragon.
     *
     * @param dragon value for this API
     */
    public void onDragonSpawned(EntityEnderDragon dragon) {
        ensureStoredState();
        if (!hasGateways) initializeGateways();

        dragonUniqueId = dragon.uniqueIdLong();
        dragonSpawned = true;
        dragonKilled = false;
        setRespawnStage(RESPAWN_STAGE_NONE);
        respawnCrystals.clear();
        save();
    }

    /**
     * Records the generated exit portal location.
     *
     * @param portalLocation value for this API
     */
    public void onExitPortalGenerated(BlockVector3 portalLocation) {
        ensureStoredState();
        if (!hasGateways) initializeGateways();

        this.exitPortalLocation = portalLocation.clone();
        this.hasExitPortalLocation = true;
        save();
    }

    /**
     * Handles dragon death.
     *
     * @param dragon value for this API
     */
    public void onDragonKilled(EntityEnderDragon dragon) {
        if (dragonUniqueId != 0 && dragonUniqueId != dragon.uniqueIdLong()) return;

        ensureStoredState();
        BlockVector3 portalLocation = spawnExitPortal(true);

        if (!previouslyKilled) {
            level.setBlock(portalLocation.add(0, 4, 0).asVector3(), Block.get(Block.DRAGON_EGG));
        }

        if (!hasGateways) initializeGateways();

        if (gateways.size() > 0) {
            int gateway = gateways.get(gateways.size() - 1);
            if (spawnGateway(gateway)) {
                gateways.remove(gateways.size() - 1);
            }
        }

        dragonUniqueId = dragon.uniqueIdLong();
        dragonKilled = true;
        previouslyKilled = true;
        setRespawnStage(RESPAWN_STAGE_NONE);
        respawnCrystals.clear();
        save();
    }

    /**
     * Attempts to start a respawn sequence.
     * @return the requested value
     */
    public boolean tryRespawn() {
        if (!dragonKilled || respawnStage != RESPAWN_STAGE_NONE) {
            return false;
        }

        BlockVector3 portalLocation = resolveExitPortalLocation();
        int y = portalLocation.y + 1;
        int[][] offsets = {
                {0, 3},
                {-3, 0},
                {0, -3},
                {3, 0}
        };

        List<EntityEnderCrystal> crystals = new ArrayList<>(4);
        for (int[] offset : offsets) {
            EntityEnderCrystal crystal = findRespawnCrystal(portalLocation.x + offset[0], y, portalLocation.z + offset[1]);
            if (crystal == null) return false;
            crystals.add(crystal);
        }

        respawnCrystals.clear();
        respawnCrystals.addAll(crystals);
        setRespawnStage(RESPAWN_STAGE_START);
        spawnExitPortal(false);
        save();
        return true;
    }

    private void loadRespawnCrystalChunks() {
        BlockVector3 portalLocation = resolveExitPortalLocation();
        int[][] offsets = {
                {0, 3},
                {-3, 0},
                {0, -3},
                {3, 0}
        };
        for (int[] offset : offsets) {
            level.loadChunk((portalLocation.x + offset[0]) >> 4, (portalLocation.z + offset[1]) >> 4, false);
        }
    }

    private EntityEnderCrystal findRespawnCrystal(int x, int y, int z) {
        SimpleAxisAlignedBB box = new SimpleAxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
        for (Entity entity : level.getNearbyEntities(box)) {
            if (entity instanceof EntityEnderCrystal crystal && !crystal.isClosed()) return crystal;
        }
        return null;
    }

    /**
     * Advances this object by one server tick.
     */
    public void tick() {
        if (respawnStage == RESPAWN_STAGE_NONE) return;

        if (respawnCrystals.size() == 0) {
            if (respawnRecoveryPending) {
                loadRespawnCrystalChunks();
                respawnRecoveryPending = false;
            }
            setRespawnStage(RESPAWN_STAGE_NONE);
            if (!tryRespawn()) {
                save();
                return;
            }
        }

        for (EntityEnderCrystal crystal : respawnCrystals) {
            if (crystal.isClosed()) {
                abortRespawn();
                return;
            }
        }

        int time = respawnTime++;
        switch (respawnStage) {
            case RESPAWN_STAGE_START -> tickRespawnStart();
            case RESPAWN_STAGE_PREPARING -> tickRespawnPreparing(time);
            case RESPAWN_STAGE_PILLARS -> tickRespawnPillars(time);
            case RESPAWN_STAGE_DRAGON -> tickRespawnDragon(time);
            default -> setRespawnStage(RESPAWN_STAGE_NONE);
        }
    }

    private void tickRespawnStart() {
        setRespawnCrystalBeam(DRAGON_SPAWN_POSITION);
        setRespawnStage(RESPAWN_STAGE_PREPARING);
    }

    private void tickRespawnPreparing(int time) {
        if (time == 0 || (time >= 50 && time <= 52) || (time >= 95 && time <= 99)) {
            playRespawnGrowl();
        }
        if (time >= 100) {
            setRespawnStage(RESPAWN_STAGE_PILLARS);
        }
    }

    private void tickRespawnPillars(int time) {
        int index = time / 40;
        int step = time % 40;

        if (index >= 10) {
            if (step == 0) {
                setRespawnStage(RESPAWN_STAGE_DRAGON);
            }
            return;
        }

        ObjectObsidianPillar pillar = ObjectObsidianPillar.fromSeed(level.getSeed(), index);
        if (step == 0) {
            setRespawnCrystalBeam(new BlockVector3(pillar.getX(), pillar.getHeight() + 1, pillar.getZ()));
        } else if (step == 38) {
            clearSpike(pillar);
        } else if (step == 39) {
            regenerateSpike(pillar);
        }
    }

    private void tickRespawnDragon(int time) {
        if (time == 0) {
            setRespawnCrystalBeam(DRAGON_SPAWN_POSITION);
        } else if (time <= 4 || (time >= 80 && time < 100)) {
            playRespawnGrowl();
        }

        if (time < 100) return;

        resetSpikeCrystals();
        setRespawnStage(RESPAWN_STAGE_NONE);
        dragonKilled = false;

        List<EntityEnderCrystal> crystals = new ArrayList<>(respawnCrystals);
        respawnCrystals.clear();
        for (EntityEnderCrystal crystal : crystals) {
            if (!crystal.isClosed()) {
                crystal.clearBeamTarget();
                crystal.explode();
            }
        }

        Entity entity = Entity.createEntity(Entity.ENDER_DRAGON, new Position(0.5, 128, 0.5, level));
        if (entity instanceof EntityEnderDragon dragon) {
            dragon.getNbt().putBoolean("Revived", true);
            dragon.spawnToAll();
            onDragonSpawned(dragon);
        } else {
            save();
        }
    }

    /**
     * Handles a destroyed End crystal.
     *
     * @param crystal value for this API
     */
    public void onCrystalDestroyed(EntityEnderCrystal crystal) {
        if (respawnStage == RESPAWN_STAGE_NONE || !respawnCrystals.contains(crystal)) return;
        abortRespawn();
    }

    private void abortRespawn() {
        setRespawnStage(RESPAWN_STAGE_NONE);
        resetSpikeCrystals();
        respawnCrystals.clear();
        spawnExitPortal(true);
        save();
    }

    private void resetSpikeCrystals() {
        for (int i = 0; i < 10; i++) {
            ObjectObsidianPillar pillar = ObjectObsidianPillar.fromSeed(level.getSeed(), i);
            int x = pillar.getX();
            int z = pillar.getZ();
            int radius = pillar.getRadius();

            SimpleAxisAlignedBB box = new SimpleAxisAlignedBB(x - radius, 0, z - radius, x + radius, 128, z + radius);
            for (Entity entity : level.getNearbyEntities(box)) {
                if (entity instanceof EntityEnderCrystal crystal && !crystal.isClosed()) {
                    crystal.setInvulnerable(false);
                    crystal.clearBeamTarget();
                }
            }
        }
    }

    private void playRespawnGrowl() {
        level.addLevelSoundEvent(DRAGON_SPAWN_POSITION.asVector3(), SoundEvent.MAD, -1, Entity.ENDER_DRAGON, false, false);
    }

    private void setRespawnCrystalBeam(BlockVector3 target) {
        for (EntityEnderCrystal crystal : respawnCrystals) {
            if (!crystal.isClosed()) crystal.setBeamTarget(target);
        }
    }

    private void clearSpike(ObjectObsidianPillar pillar) {
        int centerX = pillar.getX();
        int centerY = pillar.getHeight();
        int centerZ = pillar.getZ();

        SimpleAxisAlignedBB box = new SimpleAxisAlignedBB(
                centerX - 10, centerY - 10, centerZ - 10,
                centerX + 11, centerY + 11, centerZ + 11
        );
        for (Entity entity : level.getNearbyEntities(box)) {
            if (entity instanceof EntityEnderCrystal crystal && !respawnCrystals.contains(crystal)) {
                crystal.close();
            }
        }

        BlockManager manager = new BlockManager(level);
        int minY = Math.max(level.getMinHeight(), centerY - 10);
        int maxY = Math.min(level.getMaxHeight(), centerY + 10);
        for (int x = centerX - 10; x <= centerX + 10; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = centerZ - 10; z <= centerZ + 10; z++) {
                    manager.setBlockStateAt(x, y, z, BlockAir.STATE);
                }
            }
        }
        manager.applySubChunkUpdate();
    }

    private void regenerateSpike(ObjectObsidianPillar pillar) {
        BlockManager manager = new BlockManager(level);
        int x = pillar.getX();
        int z = pillar.getZ();
        pillar.generate(manager, null, new Vector3(x, level.getHeightMap(x, z), z), DRAGON_SPAWN_POSITION);
        manager.applySubChunkUpdate();
    }

    private void setRespawnStage(int stage) {
        this.respawnStage = stage;
        this.respawnTime = 0;
    }

    private void ensureStoredState() {
        if (storedState) {
            return;
        }

        storedState = true;
        dragonFightVersion = 0;
        if (root.containsCompound("data")) {
            data = root.getCompound("data").copy();
        }
        if (!data.containsList("LimboEntities")) {
            data.putList("LimboEntities", new ListTag<>());
        }
        fight = new CompoundTag();
    }

    private void initializeGateways() {
        gateways.clear();
        for (int i = 0; i < 20; i++) {
            gateways.add(i);
        }

        BedrockMersenneTwister random = new BedrockMersenneTwister((int) level.getSeed());
        for (int i = 1; i < gateways.size(); i++) {
            int j = Integer.remainderUnsigned(random.nextInt(), i + 1);
            int value = gateways.get(i);
            gateways.set(i, gateways.get(j));
            gateways.set(j, value);
        }
        hasGateways = true;
    }

    private boolean spawnGateway(int gateway) {
        double angle = 2d * (gateway * Math.PI / 20d - Math.PI);
        BlockVector3 entryGateway = new BlockVector3((int) (Math.cos(angle) * 96d), 75, (int) (Math.sin(angle) * 96d));
        ensureAreaGenerated(entryGateway, 16);
        BlockManager manager = new BlockManager(level);
        new ObjectEndGateway().generate(manager, null, entryGateway.asVector3());
        manager.applySubChunkUpdate();

        Block entryBlock = level.getBlock(entryGateway.x, entryGateway.y, entryGateway.z);
        if (!(entryBlock instanceof BlockEndGateway entry)) {
            return false;
        }

        BlockEntityEndGateway entryEntity = entry.getOrCreateBlockEntity();
        if (entryEntity == null) {
            return false;
        }

        entryEntity.setAge(0);
        entryEntity.setExitPortal(new BlockVector3(0, 0, 0));
        entryEntity.saveNBT();
        entryEntity.setDirty();
        return true;
    }

    private void ensureAreaGenerated(BlockVector3 center, int radius) {
        int minChunkX = (center.x - radius) >> 4;
        int maxChunkX = (center.x + radius) >> 4;
        int minChunkZ = (center.z - radius) >> 4;
        int maxChunkZ = (center.z + radius) >> 4;

        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                IChunk chunk = level.getChunk(chunkX, chunkZ, false);
                if (chunk == null || chunk.getFinalizationState() != ChunkFinalizationState.DONE) {
                    level.syncGenerateChunkFully(chunkX, chunkZ);
                }
            }
        }
    }

    private static final class BedrockMersenneTwister {
        private final int[] state = new int[624];
        private int index = 624;

        private BedrockMersenneTwister(int seed) {
            state[0] = seed;
            for (int i = 1; i < state.length; i++) {
                int previous = state[i - 1];
                state[i] = 0x6c078965 * (previous ^ (previous >>> 30)) + i;
            }
        }

        private int nextInt() {
            if (index >= state.length) twist();

            int value = state[index++];
            value ^= value >>> 11;
            value ^= (value << 7) & 0x9d2c5680;
            value ^= (value << 15) & 0xefc60000;
            value ^= value >>> 18;
            return value;
        }

        private void twist() {
            for (int i = 0; i < state.length; i++) {
                int value = (state[i] & 0x80000000) | (state[(i + 1) % state.length] & 0x7fffffff);
                state[i] = state[(i + 397) % state.length] ^ (value >>> 1);
                if ((value & 1) != 0) {
                    state[i] ^= 0x9908b0df;
                }
            }
            index = 0;
        }
    }

    private BlockVector3 resolveExitPortalLocation() {
        if (hasExitPortalLocation) return exitPortalLocation.clone();

        int highest = level.getHighestBlockAt(0, 0);
        for (int y = level.getMinHeight(); y <= highest; y++) {
            if (level.getBlock(0, y, 0) instanceof BlockBedrock) {
                this.exitPortalLocation = new BlockVector3(0, y + 1, 0);
                this.hasExitPortalLocation = true;
                return exitPortalLocation.clone();
            }
        }

        throw new IllegalStateException("Unable to resolve End exit portal location");
    }

    /**
     * Persists the current state.
     */
    public void save() {
        if (!storedState) return;

        synchronized (storage) {
            CompoundTag stored = storage.readTheEndData();
            if (stored != null) {
                root = stored.copy();
                data = root.getCompound("data").copy();
            }

            fight.putByte("DragonFightVersion", dragonFightVersion)
                    .putByte("DragonKilled", dragonKilled ? 1 : 0)
                    .putByte("DragonSpawned", dragonSpawned ? 1 : 0)
                    .putLong("DragonUUID", dragonUniqueId)
                    .putByte("IsRespawning", respawnStage != RESPAWN_STAGE_NONE ? 1 : 0)
                    .putByte("PreviouslyKilled", previouslyKilled ? 1 : 0);

            if (hasExitPortalLocation) {
                fight.putList("ExitPortalLocation", new ListTag<IntTag>()
                        .add(new IntTag(exitPortalLocation.x))
                        .add(new IntTag(exitPortalLocation.y))
                        .add(new IntTag(exitPortalLocation.z)));
            }

            if (hasGateways) {
                ListTag<IntTag> gatewayList = new ListTag<>();
                for (int gateway : gateways) {
                    gatewayList.add(new IntTag(gateway));
                }
                fight.putList("Gateways", gatewayList);
            }

            data.putCompound("DragonFight", fight);
            root.putCompound("data", data);
            storage.writeTheEndData(root);
        }
    }

    /**
     * Returns the owning level.
     * @return the requested value
     */
    public Level getLevel() {
        return level;
    }

    /**
     * Returns whether stored state exists.
     * @return the requested value
     */
    public boolean hasStoredState() {
        return storedState;
    }

    /**
     * Returns the dragon fight version.
     * @return the requested value
     */
    public byte getDragonFightVersion() {
        return dragonFightVersion;
    }

    /**
     * Returns whether the dragon is marked killed.
     * @return the requested value
     */
    public boolean isDragonKilled() {
        return dragonKilled;
    }

    /**
     * Sets whether the dragon is marked killed.
     *
     * @param dragonKilled value for this API
     */
    public void setDragonKilled(boolean dragonKilled) {
        this.dragonKilled = dragonKilled;
    }

    /**
     * Returns whether the dragon is marked spawned.
     * @return the requested value
     */
    public boolean isDragonSpawned() {
        return dragonSpawned;
    }

    /**
     * Sets whether the dragon is marked spawned.
     *
     * @param dragonSpawned value for this API
     */
    public void setDragonSpawned(boolean dragonSpawned) {
        this.dragonSpawned = dragonSpawned;
    }

    /**
     * Returns the tracked dragon unique ID.
     * @return the requested value
     */
    public long getDragonUniqueId() {
        return dragonUniqueId;
    }

    /**
     * Sets the tracked dragon unique ID.
     *
     * @param dragonUniqueId value for this API
     */
    public void setDragonUniqueId(long dragonUniqueId) {
        this.dragonUniqueId = dragonUniqueId;
    }

    /**
     * Returns whether an exit portal location is stored.
     * @return the requested value
     */
    public boolean hasExitPortalLocation() {
        return hasExitPortalLocation;
    }

    /**
     * Returns the exit portal location.
     * @return the requested value
     */
    public BlockVector3 getExitPortalLocation() {
        return exitPortalLocation.clone();
    }

    /**
     * Sets the exit portal location.
     *
     * @param exitPortalLocation value for this API
     */
    public void setExitPortalLocation(BlockVector3 exitPortalLocation) {
        this.exitPortalLocation = exitPortalLocation.clone();
        this.hasExitPortalLocation = true;
    }

    /**
     * Returns whether gateway indexes are stored.
     * @return the requested value
     */
    public boolean hasGateways() {
        return hasGateways;
    }

    /**
     * Returns the remaining gateway indexes.
     * @return the requested value
     */
    public List<Integer> getGateways() {
        return List.copyOf(gateways);
    }

    /**
     * Sets the remaining gateway indexes.
     *
     * @param gateways value for this API
     */
    public void setGateways(List<Integer> gateways) {
        this.gateways.clear();
        this.gateways.addAll(gateways);
        this.hasGateways = true;
    }

    /**
     * Returns whether respawn is active.
     * @return the requested value
     */
    public boolean isRespawning() {
        return respawnStage != RESPAWN_STAGE_NONE;
    }

    /**
     * Sets whether respawn is active.
     *
     * @param respawning value for this API
     */
    public void setRespawning(boolean respawning) {
        setRespawnStage(respawning ? RESPAWN_STAGE_START : RESPAWN_STAGE_NONE);
    }

    /**
     * Returns whether the dragon was previously killed.
     * @return the requested value
     */
    public boolean wasPreviouslyKilled() {
        return previouslyKilled;
    }

    /**
     * Sets whether the dragon was previously killed.
     *
     * @param previouslyKilled value for this API
     */
    public void setPreviouslyKilled(boolean previouslyKilled) {
        this.previouslyKilled = previouslyKilled;
    }
}
