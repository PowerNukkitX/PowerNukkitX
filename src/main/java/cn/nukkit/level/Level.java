package cn.nukkit.level;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.NonComputationAtomic;
import cn.nukkit.block.*;
import cn.nukkit.block.customblock.CustomBlock;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityAsyncPrepare;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.item.EntityFireworksRocket;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.item.EntityPainting;
import cn.nukkit.entity.item.EntityXpOrb;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.entity.weather.EntityLightningBolt;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.block.BlockUpdateEvent;
import cn.nukkit.event.level.ChunkLoadEvent;
import cn.nukkit.event.level.ChunkUnloadEvent;
import cn.nukkit.event.level.LevelSaveEvent;
import cn.nukkit.event.level.LevelUnloadEvent;
import cn.nukkit.event.level.SpawnChangeEvent;
import cn.nukkit.event.level.ThunderChangeEvent;
import cn.nukkit.event.level.WeatherChangeEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.event.weather.LightningStrikeEvent;
import cn.nukkit.inventory.BlockInventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBucket;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.format.LevelConfig;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.level.tickingarea.TickingArea;
import cn.nukkit.level.util.SimpleTickCachedBlockStore;
import cn.nukkit.level.util.TickCachedBlockStore;
import cn.nukkit.level.vibration.SimpleVibrationManager;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationManager;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.*;
import cn.nukkit.math.BlockFace.Plane;
import cn.nukkit.metadata.BlockMetadataStore;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.metadata.Metadatable;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.*;
import cn.nukkit.network.protocol.types.PlayerAbility;
import cn.nukkit.plugin.InternalPlugin;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.registry.Registries;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.scheduler.BlockUpdateScheduler;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.BlockUpdateEntry;
import cn.nukkit.utils.Hash;
import cn.nukkit.utils.LevelException;
import cn.nukkit.utils.RedstoneComponent;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.utils.collection.nb.Int2ObjectNonBlockingMap;
import cn.nukkit.utils.collection.nb.Long2ObjectNonBlockingMap;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static cn.nukkit.utils.Utils.dynamic;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Slf4j
public class Level implements Metadatable {
    // region finals - number finals
    public static final Level[] EMPTY_ARRAY = new Level[0];
    public static final int BLOCK_UPDATE_NORMAL = 1;
    public static final int BLOCK_UPDATE_RANDOM = 2;
    public static final int BLOCK_UPDATE_SCHEDULED = 3;
    public static final int BLOCK_UPDATE_WEAK = 4;
    public static final int BLOCK_UPDATE_TOUCH = 5;
    public static final int BLOCK_UPDATE_REDSTONE = 6;
    public static final int BLOCK_UPDATE_TICK = 7;
    public static final int BLOCK_UPDATE_MOVED = dynamic(1_000_000);
    public static final int TIME_DAY = 0;
    public static final int TIME_NOON = 6000;
    public static final int TIME_SUNSET = 12000;
    public static final int TIME_NIGHT = 14000;
    public static final int TIME_MIDNIGHT = 18000;
    public static final int TIME_SUNRISE = 23000;
    public static final int TIME_FULL = 24000;
    public static final int DIMENSION_OVERWORLD = 0;
    public static final int DIMENSION_NETHER = 1;
    public static final int DIMENSION_THE_END = 2;
    public static final int MAX_BLOCK_CACHE = 512; // Lower values use less memory
    private static final int LCG_CONSTANT = 1013904223;
    public static int COMPRESSION_LEVEL = 8;
    private static int levelIdCounter = 1;
    private static int chunkLoaderCounter = 1;
    // endregion finals - number finals
    private static final Set<String> randomTickBlocks = new HashSet<>(64);  // The blocks that can randomly tick
    private static final Entity[] ENTITY_BUFFER = new Entity[512];

    static {
        randomTickBlocks.add(BlockID.GRASS);
        randomTickBlocks.add(BlockID.FARMLAND);
        randomTickBlocks.add(BlockID.MYCELIUM);
        randomTickBlocks.add(BlockID.SAPLING);
        randomTickBlocks.add(BlockID.LEAVES);
        randomTickBlocks.add(BlockID.LEAVES2);
        randomTickBlocks.add(BlockID.SNOW_LAYER);
        randomTickBlocks.add(BlockID.ICE);
        randomTickBlocks.add(BlockID.FLOWING_LAVA);
        randomTickBlocks.add(BlockID.LAVA);
        randomTickBlocks.add(BlockID.CACTUS);
        randomTickBlocks.add(BlockID.BEETROOT);
        randomTickBlocks.add(BlockID.CARROTS);
        randomTickBlocks.add(BlockID.POTATOES);
        randomTickBlocks.add(BlockID.MELON_STEM);
        randomTickBlocks.add(BlockID.PUMPKIN_STEM);
        randomTickBlocks.add(BlockID.WHEAT);
        randomTickBlocks.add(BlockID.REEDS);
        randomTickBlocks.add(BlockID.RED_MUSHROOM);
        randomTickBlocks.add(BlockID.BROWN_MUSHROOM);
        randomTickBlocks.add(BlockID.NETHER_WART_BLOCK);
        randomTickBlocks.add(BlockID.FIRE);
        randomTickBlocks.add(BlockID.LIT_REDSTONE_ORE);
        randomTickBlocks.add(BlockID.COCOA);
        randomTickBlocks.add(BlockID.VINE);
        randomTickBlocks.add(BlockID.CORAL_FAN);
        randomTickBlocks.add(BlockID.CORAL_FAN_DEAD);
        randomTickBlocks.add(BlockID.KELP);
        randomTickBlocks.add(BlockID.SWEET_BERRY_BUSH);
        randomTickBlocks.add(BlockID.TURTLE_EGG);
        randomTickBlocks.add(BlockID.BAMBOO);
        randomTickBlocks.add(BlockID.BAMBOO_SAPLING);
        randomTickBlocks.add(BlockID.CRIMSON_NYLIUM);
        randomTickBlocks.add(BlockID.WARPED_NYLIUM);
        randomTickBlocks.add(BlockID.TWISTING_VINES);
        randomTickBlocks.add(BlockID.CHORUS_FLOWER);
        randomTickBlocks.add(BlockID.COPPER_BLOCK);
        randomTickBlocks.add(BlockID.EXPOSED_COPPER);
        randomTickBlocks.add(BlockID.WEATHERED_COPPER);
        randomTickBlocks.add(BlockID.WAXED_COPPER);
        randomTickBlocks.add(BlockID.CUT_COPPER);
        randomTickBlocks.add(BlockID.EXPOSED_CUT_COPPER);
        randomTickBlocks.add(BlockID.WEATHERED_CUT_COPPER);
        randomTickBlocks.add(BlockID.CUT_COPPER_STAIRS);
        randomTickBlocks.add(BlockID.EXPOSED_CUT_COPPER_STAIRS);
        randomTickBlocks.add(BlockID.WEATHERED_CUT_COPPER_STAIRS);
        randomTickBlocks.add(BlockID.CUT_COPPER_SLAB);
        randomTickBlocks.add(BlockID.EXPOSED_CUT_COPPER_SLAB);
        randomTickBlocks.add(BlockID.WEATHERED_CUT_COPPER_SLAB);
        randomTickBlocks.add(BlockID.DOUBLE_CUT_COPPER_SLAB);
        randomTickBlocks.add(BlockID.EXPOSED_DOUBLE_CUT_COPPER_SLAB);
        randomTickBlocks.add(BlockID.WEATHERED_DOUBLE_CUT_COPPER_SLAB);
        randomTickBlocks.add(BlockID.BUDDING_AMETHYST);
        randomTickBlocks.add(BlockID.POINTED_DRIPSTONE);
        randomTickBlocks.add(BlockID.CAVE_VINES);
        randomTickBlocks.add(BlockID.CAVE_VINES_BODY_WITH_BERRIES);
        randomTickBlocks.add(BlockID.CAVE_VINES_HEAD_WITH_BERRIES);
        randomTickBlocks.add(BlockID.AZALEA_LEAVES);
        randomTickBlocks.add(BlockID.AZALEA_LEAVES_FLOWERED);
        randomTickBlocks.add(BlockID.MANGROVE_LEAVES);
        randomTickBlocks.add(BlockID.CHERRY_SAPLING);
        randomTickBlocks.add(BlockID.CHERRY_LEAVES);
        randomTickBlocks.add(BlockID.TORCHFLOWER_CROP);
    }

    @NonComputationAtomic
    public final Long2ObjectNonBlockingMap<Entity> updateEntities = new Long2ObjectNonBlockingMap<>();
    @NonComputationAtomic
    private final Long2ObjectNonBlockingMap<BlockEntity> blockEntities = new Long2ObjectNonBlockingMap<>();
    @NonComputationAtomic
    private final Long2ObjectNonBlockingMap<Player> players = new Long2ObjectNonBlockingMap<>();
    @NonComputationAtomic
    private final Long2ObjectNonBlockingMap<Entity> entities = new Long2ObjectNonBlockingMap<>();
    private final ConcurrentLinkedQueue<BlockEntity> updateBlockEntities = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<Long, Boolean> chunkGenerationQueue = new ConcurrentHashMap<>();
    private int chunkGenerationQueueSize = 8;
    private final Server server;
    private final int levelId;
    // Loaders still remain single-threaded
    private final Int2ObjectOpenHashMap<ChunkLoader> loaders = new Int2ObjectOpenHashMap<>();
    private final Int2IntMap loaderCounter = new Int2IntOpenHashMap();
    /*
     * <ChunkIndex,<ChunkLoader ID,ChunkLoader>>
     */
    private final Long2ObjectOpenHashMap<Map<Integer, ChunkLoader>> chunkLoaders = new Long2ObjectOpenHashMap<>();
    // Computation atomicity may be required in addChunkPacket(int, int, DataPacket)
    private final ConcurrentHashMap<Long, Deque<DataPacket>> chunkPackets = new ConcurrentHashMap<>();
    @NonComputationAtomic
    private final Long2ObjectNonBlockingMap<Long> unloadQueue = new Long2ObjectNonBlockingMap<>();
    private final ConcurrentHashMap<Long, TickCachedBlockStore> tickCachedBlocks = new ConcurrentHashMap<>();
    private final LongSet highLightChunks = new LongOpenHashSet();
    // Avoid OOM, gc'd references result in whole chunk being sent (possibly higher cpu)
    private final Long2ObjectOpenHashMap<SoftReference<Int2ObjectOpenHashMap<Object>>> changedBlocks = new Long2ObjectOpenHashMap<>();
    // Storing the vector is redundant
    private final Object changeBlocksPresent = new Object();
    // Storing extra blocks past 512 is redundant
    private final Int2ObjectOpenHashMap<Object> changeBlocksFullMap = new Int2ObjectOpenHashMap<>() {
        @Override
        public int size() {
            return Character.MAX_VALUE;
        }
    };
    private final BlockUpdateScheduler updateQueue;
    private final Queue<QueuedUpdate> normalUpdateQueue = new ConcurrentLinkedDeque<>();
    @NonComputationAtomic
    private final Long2ObjectNonBlockingMap<Int2ObjectNonBlockingMap<Player>> chunkSendQueue = new Long2ObjectNonBlockingMap<>();
    private final Long2IntMap chunkTickList = new Long2IntOpenHashMap();
    private final VibrationManager vibrationManager = new SimpleVibrationManager(this);
    public boolean stopTime;
    public float skyLightSubtracted;
    public int sleepTicks = 0;
    public int tickRateTime = 0;
    public int tickRateCounter = 0;
    /**
     * 当tps过低的时候，tps优化延迟会上升，计算密集型任务应当每隔此tick才运行一次
     */
    public int tickRateOptDelay = 1;
    public GameRules gameRules;
    private LevelProvider provider;
    private float time;
    private int nextTimeSendTick;
    private final String name;
    private final String folderPath;
    private Vector3 mutableBlock;
    private boolean autoSave;
    private BlockMetadataStore blockMetadata;
    private final Vector3 temporalVector;
    private final int chunkTickRadius;
    private final int chunksPerTicks;
    private final boolean clearChunksOnTick;
    private final Generator generator;
    private final Class<? extends Generator> generatorClass;
    private int updateLCG = ThreadLocalRandom.current().nextInt();
    private int tickRate;
    private boolean raining = false;
    private int rainTime = 0;
    private boolean thundering = false;
    private int thunderTime = 0;
    private long levelCurrentTick = 0;
    private final Map<Long, Map<Integer, Object>> lightQueue = new ConcurrentHashMap<>(8, 0.9f, 1);
    private Iterator<cn.nukkit.utils.collection.nb.LongObjectEntry<Long>> lastUsingUnloadingIter;
    private final int dimensionCount;

    public Level(Server server, String name, String path, int dimSum, Class<? extends LevelProvider> provider, LevelConfig.GeneratorConfig generatorConfig) {
        this.levelId = levelIdCounter++;
        this.dimensionCount = dimSum;
        this.blockMetadata = new BlockMetadataStore(this);
        this.server = server;
        this.autoSave = server.getAutoSave();
        this.generatorClass = Registries.GENERATOR.get(generatorConfig.name());
        try {
            this.generator = generatorClass.getConstructor(DimensionData.class, Map.class).newInstance(
                    generatorConfig.dimensionData(),
                    generatorConfig.preset()
            );
            this.generator.setLevel(Level.this);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        try {
            this.provider = provider.getConstructor(Level.class, String.class).newInstance(this, path);
        } catch (ReflectiveOperationException e) {
            throw new LevelException("Constructor of " + provider + " failed", e);
        }
        LevelProvider levelProvider = requireProvider();
        levelProvider.updateLevelName(name);
        log.info(this.server.getLanguage().tr("nukkit.level.preparing", TextFormat.GREEN + levelProvider.getName() + TextFormat.WHITE));

        this.name = name;
        this.folderPath = path;
        this.time = levelProvider.getTime();

        this.raining = levelProvider.isRaining();
        this.rainTime = this.requireProvider().getRainTime();
        if (this.rainTime <= 0) {
            setRainTime(ThreadLocalRandom.current().nextInt(168000) + 12000);
        }

        this.thundering = levelProvider.isThundering();
        this.thunderTime = levelProvider.getThunderTime();
        if (this.thunderTime <= 0) {
            setThunderTime(ThreadLocalRandom.current().nextInt(168000) + 12000);
        }

        this.levelCurrentTick = levelProvider.getCurrentTick();
        this.updateQueue = new BlockUpdateScheduler(this, levelCurrentTick);

        this.chunkTickRadius = Math.min(this.server.getViewDistance(),
                Math.max(1, this.server.getConfig("chunk-ticking.tick-radius", 4)));
        this.chunkGenerationQueueSize = this.server.getConfig("chunk-generation.queue-size", 8);
        this.chunksPerTicks = this.server.getConfig("chunk-ticking.per-tick", 40);
        this.chunkTickList.clear();
        this.clearChunksOnTick = this.server.getConfig("chunk-ticking.clear-tick-list", true);
        this.temporalVector = new Vector3(0, 0, 0);
        this.tickRate = 1;

        this.skyLightSubtracted = this.calculateSkylightSubtracted(1);
    }

    public static boolean canRandomTick(String blockId) {
        return randomTickBlocks.contains(blockId);
    }

    public static void setCanRandomTick(String blockId, boolean newValue) {
        if (newValue) {
            randomTickBlocks.add(blockId);
        } else {
            randomTickBlocks.remove(blockId);
        }
    }

    public static long chunkHash(int x, int z) {
        return (((long) x) << 32) | (z & 0xffffffffL);
    }

    public static long blockHash(int x, int y, int z, Level level) {
        if (!level.isYInRange(y)) {
            throw new IllegalArgumentException("Y coordinate y is out of range!");
        }
        return (((long) x & (long) 0b111111111111111111111111111) << 37) | ((long) (level.ensureY(y) + 64) << 28) | ((long) z & (long) 0xFFFFFFF);
    }

    public static int localBlockHash(double x, double y, double z, Level level) {
        byte hi = (byte) (((int) x & 15) + (((int) z & 15) << 4));
        short lo = (short) (level.ensureY((int) y) + 64);
        return (hi & 0xFF) << 16 | lo;
    }

    public static int localBlockHash(int x, int y, int z, int layer, Level level) {
        byte hi = (byte) ((x & 15) + ((z & 15) << 4));
        short lo = (short) (level.ensureY(y) + 64);
        return ((layer & 127) << 24) | (hi & 0xFF) << 16 | lo;
    }

    public static Vector3 getBlockXYZ(long chunkHash, int blockHash, Level level) {
        int hi = (byte) (blockHash >>> 16);
        int lo = (short) blockHash;
        int y = level.ensureY(lo - 64);
        int x = (hi & 0xF) + (getHashX(chunkHash) << 4);
        int z = ((hi >> 4) & 0xF) + (getHashZ(chunkHash) << 4);
        return new Vector3(x, y, z);
    }

    public static int chunkBlockHash(int x, int y, int z) {
        return (x << 13) | (z << 9) | (y + 64); // 为适配384世界，y需要额外的1bit来存储
    }

    /**
     * 获取chunkX从chunk hash
     * <p>
     * Get chunkX from chunk hash
     *
     * @param hash the hash
     * @return the hash x
     */
    public static int getHashX(long hash) {
        return (int) (hash >> 32);
    }

    /**
     * 获取chunkZ从chunk hash
     * <p>
     * Get chunkZ from chunk hash
     *
     * @param hash the hash
     * @return the hash x
     */
    public static int getHashZ(long hash) {
        return (int) hash;
    }

    public static Vector3 getBlockXYZ(BlockVector3 hash) {
        return new Vector3(hash.x, hash.y, hash.z);
    }

    public static int generateChunkLoaderId(ChunkLoader loader) {
        if (loader.getLoaderId() == 0) {
            return chunkLoaderCounter++;
        } else {
            throw new IllegalStateException("ChunkLoader has a loader id already assigned: " + loader.getLoaderId());
        }
    }

    public int getTickRate() {
        return tickRate;
    }

    public void setTickRate(int tickRate) {
        this.tickRate = tickRate;
    }

    public int getTickRateTime() {
        return tickRateTime;
    }

    public int recalcTickOptDelay() {
        if (tickRateTime > 40) {
            return Math.min(tickRateOptDelay << 1, 8);
        } else if (tickRateOptDelay == 1) {
            return 1;
        } else {
            return tickRateOptDelay >> 1;
        }
    }

    public boolean isHighLightChunk(int chunkX, int chunkZ) {
        return highLightChunks.contains(Level.chunkHash(chunkX, chunkZ));
    }

    public void initLevel() {
        this.gameRules = this.requireProvider().getGamerules();
        log.info("Preparing start region for level \"{}\"", this.getName());
        Position spawn = this.getSpawnLocation();
        this.generateChunk(spawn.getChunkX(), spawn.getChunkZ());
    }

    public Generator getGenerator() {
        return generator;
    }

    public BlockMetadataStore getBlockMetadata() {
        return this.blockMetadata;
    }

    public Server getServer() {
        return server;
    }

    public final LevelProvider getProvider() {
        return provider;
    }

    /**
     * Returns the level provider if it exists. Tries to close and unregister the level and then throw an exception if it doesn't.
     *
     * @throws LevelException If the level is already closed
     */
    @NotNull
    public final LevelProvider requireProvider() {
        LevelProvider levelProvider = getProvider();
        if (levelProvider == null) {
            LevelException levelException = new LevelException("The level \"" + getFolderPath() + "\" is already closed (have no providers)");
            try {
                close();
            } catch (Exception e) {
                levelException.addSuppressed(e);
            }
            throw levelException;
        }
        return levelProvider;
    }

    public final int getId() {
        return this.levelId;
    }

    public void close() {
        LevelProvider levelProvider = this.provider;
        if (levelProvider != null) {
            if (this.getAutoSave()) {
                this.save(true);
            }
            levelProvider.close();
        }

        this.provider = null;
        this.blockMetadata = null;
        this.server.getLevels().remove(this.levelId);
    }

    public void addSound(Vector3 pos, Sound sound) {
        this.addSound(pos, sound, 1, 1, (Player[]) null);
    }

    public void addSound(Vector3 pos, Sound sound, float volume, float pitch) {
        this.addSound(pos, sound, volume, pitch, (Player[]) null);
    }

    public void addSound(Vector3 pos, Sound sound, float volume, float pitch, Collection<Player> players) {
        this.addSound(pos, sound, volume, pitch, players.toArray(Player.EMPTY_ARRAY));
    }

    public void addSound(Vector3 pos, Sound sound, float volume, float pitch, Player... players) {
        Preconditions.checkArgument(volume >= 0 && volume <= 1, "Sound volume must be between 0 and 1");
        Preconditions.checkArgument(pitch >= 0, "Sound pitch must be higher than 0");

        PlaySoundPacket packet = new PlaySoundPacket();
        packet.name = sound.getSound();
        packet.volume = volume;
        packet.pitch = pitch;
        packet.x = pos.getFloorX();
        packet.y = pos.getFloorY();
        packet.z = pos.getFloorZ();

        if (players == null || players.length == 0) {
            addChunkPacket(pos.getFloorX() >> 4, pos.getFloorZ() >> 4, packet);
        } else {
            Server.broadcastPacket(players, packet);
        }
    }

    public void addLevelEvent(int type, int data) {
        addLevelEvent(type, data, null);
    }

    public void addLevelEvent(int type, int data, Vector3 pos) {
        if (pos == null) {
            addLevelEvent(type, data, 0, 0, 0);
        } else {
            addLevelEvent(type, data, (float) pos.x, (float) pos.y, (float) pos.z);
        }
    }

    public void addLevelEvent(int type, int data, float x, float y, float z) {
        LevelEventPacket packet = new LevelEventPacket();
        packet.evid = type;
        packet.x = x;
        packet.y = y;
        packet.z = z;
        packet.data = data;

        this.addChunkPacket(NukkitMath.floorFloat(x) >> 4, NukkitMath.floorFloat(z) >> 4, packet);
    }

    public void addLevelEvent(Vector3 pos, int event) {
        this.addLevelEvent(pos, event, 0);
    }

    public void addLevelEvent(Vector3 pos, int event, int data) {
        LevelEventPacket pk = new LevelEventPacket();
        pk.evid = event;
        pk.x = (float) pos.x;
        pk.y = (float) pos.y;
        pk.z = (float) pos.z;
        pk.data = data;

        addChunkPacket(pos.getFloorX() >> 4, pos.getFloorZ() >> 4, pk);
    }

    public void addLevelEvent(Vector3 pos, int event, CompoundTag data) {
        LevelEventGenericPacket pk = new LevelEventGenericPacket();
        pk.eventId = event;
        pk.tag = data;

        this.addChunkPacket(pos.getChunkX(), pos.getChunkZ(), pk);
    }

    public void addLevelSoundEvent(Vector3 pos, int type, int data, int entityType) {
        addLevelSoundEvent(pos, type, data, entityType, false, false);
    }

    public void addLevelSoundEvent(Vector3 pos, int type, int data, int entityType, boolean isBaby, boolean isGlobal) {
        String identifier = Registries.ENTITY.getEntityIdentifier(entityType);
        if (identifier == null) identifier = ":";
        addLevelSoundEvent(pos, type, data, identifier, isBaby, isGlobal);
    }

    public void addLevelSoundEvent(Vector3 pos, int type) {
        this.addLevelSoundEvent(pos, type, -1);
    }

    /**
     * Broadcasts sound to players
     *
     * @param pos  position where sound should be played
     * @param type ID of the sound from {@link cn.nukkit.network.protocol.LevelSoundEventPacket}
     * @param data generic data that can affect sound
     */
    public void addLevelSoundEvent(Vector3 pos, int type, int data) {
        this.addLevelSoundEvent(pos, type, data, ":", false, false);
    }

    /**
     * Broadcasts a LevelSound to players,use LevelSoundEventPacket
     *
     * @param pos        the pos
     * @param type       the sound type id,get from{@link LevelSoundEventPacket}
     * @param data       the extra data,default -1
     * @param identifier the identifier,default ":"
     * @param isBaby     the is baby,default false
     * @param isGlobal   the is global,default false
     */
    public void addLevelSoundEvent(Vector3 pos, int type, int data, String identifier, boolean isBaby, boolean isGlobal) {
        LevelSoundEventPacket pk = new LevelSoundEventPacket();
        pk.sound = type;
        pk.extraData = data;
        pk.entityIdentifier = identifier;
        pk.x = (float) pos.x;
        pk.y = (float) pos.y;
        pk.z = (float) pos.z;
        pk.isBabyMob = isBaby;
        pk.isGlobal = isGlobal;

        this.addChunkPacket(pos.getFloorX() >> 4, pos.getFloorZ() >> 4, pk);
    }

    public void addParticle(Particle particle) {
        this.addParticle(particle, (Player[]) null);
    }

    public void addParticle(Particle particle, Player player) {
        this.addParticle(particle, new Player[]{player});
    }

    public void addParticle(Particle particle, Player[] players) {
        DataPacket[] packets = particle.encode();

        if (players == null) {
            if (packets != null) {
                for (DataPacket packet : packets) {
                    this.addChunkPacket((int) particle.x >> 4, (int) particle.z >> 4, packet);
                }
            }
        } else {
            if (packets != null) {
                for (var p : packets) {
                    Server.broadcastPacket(players, p);
                }
            }
        }
    }

    public void addParticle(Particle particle, Collection<Player> players) {
        this.addParticle(particle, players.toArray(Player.EMPTY_ARRAY));
    }

    public void addParticleEffect(Vector3 pos, ParticleEffect particleEffect) {
        this.addParticleEffect(pos, particleEffect, -1, this.getDimension(), (Player[]) null);
    }

    public void addParticleEffect(Vector3 pos, ParticleEffect particleEffect, long uniqueEntityId) {
        this.addParticleEffect(pos, particleEffect, uniqueEntityId, this.getDimension(), (Player[]) null);
    }

    public void addParticleEffect(Vector3 pos, ParticleEffect particleEffect, long uniqueEntityId, int dimensionId) {
        this.addParticleEffect(pos, particleEffect, uniqueEntityId, dimensionId, (Player[]) null);
    }

    public void addParticleEffect(Vector3 pos, ParticleEffect particleEffect, long uniqueEntityId, int dimensionId, Collection<Player> players) {
        this.addParticleEffect(pos, particleEffect, uniqueEntityId, dimensionId, players.toArray(Player.EMPTY_ARRAY));
    }

    public void addParticleEffect(Vector3 pos, ParticleEffect particleEffect, long uniqueEntityId, int dimensionId, Player... players) {
        this.addParticleEffect(pos.asVector3f(), particleEffect.getIdentifier(), uniqueEntityId, dimensionId, players);
    }

    public void addParticleEffect(Vector3f pos, String identifier, long uniqueEntityId, int dimensionId, Player... players) {
        SpawnParticleEffectPacket pk = new SpawnParticleEffectPacket();
        pk.identifier = identifier;
        pk.uniqueEntityId = uniqueEntityId;
        pk.dimensionId = dimensionId;
        pk.position = pos;

        if (players == null || players.length == 0) {
            addChunkPacket(pos.getFloorX() >> 4, pos.getFloorZ() >> 4, pk);
        } else {
            Server.broadcastPacket(players, pk);
        }
    }

    public boolean getAutoSave() {
        return this.autoSave;
    }

    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
    }

    public boolean unload() {
        return this.unload(false);
    }

    public boolean unload(boolean force) {
        LevelUnloadEvent ev = new LevelUnloadEvent(this);

        if (this == this.server.getDefaultLevel() && !force) {
            ev.setCancelled();
        }

        this.server.getPluginManager().callEvent(ev);

        if (!force && ev.isCancelled()) {
            return false;
        }

        log.info(this.server.getLanguage().tr("nukkit.level.unloading",
                TextFormat.GREEN + this.getName() + TextFormat.WHITE));
        Level defaultLevel = this.server.getDefaultLevel();

        for (Player player : this.getPlayers().values().toArray(Player.EMPTY_ARRAY)) {
            if (this == defaultLevel || defaultLevel == null) {
                player.close(player.getLeaveMessage(), "Forced default level unload");
            } else {
                player.teleport(this.server.getDefaultLevel().getSafeSpawn());
            }
        }

        if (this == defaultLevel) {
            this.server.setDefaultLevel(null);
        }

        this.close();

        return true;
    }

    public Map<Integer, Player> getChunkPlayers(int chunkX, int chunkZ) {
        long index = Level.chunkHash(chunkX, chunkZ);
        if (this.chunkLoaders.containsKey(index)) {
            return this.chunkLoaders.get(index).entrySet()
                    .stream()
                    .filter(e -> e.getValue() instanceof Player)
                    .collect(HashMap::new, (m, e) -> {
                        m.put(e.getKey(), (Player) e.getValue());
                    }, HashMap::putAll);
        }
        return Collections.emptyMap();
    }

    public ChunkLoader[] getChunkLoaders(int chunkX, int chunkZ) {
        long index = Level.chunkHash(chunkX, chunkZ);
        if (this.chunkLoaders.containsKey(index)) {
            return this.chunkLoaders.get(index).values().toArray(ChunkLoader.EMPTY_ARRAY);
        } else {
            return ChunkLoader.EMPTY_ARRAY;
        }
    }

    public void addChunkPacket(int chunkX, int chunkZ, DataPacket packet) {
        long index = Level.chunkHash(chunkX, chunkZ);
        Deque<DataPacket> packets = chunkPackets.computeIfAbsent(index, i -> new ConcurrentLinkedDeque<>());
        packets.add(packet);
    }

    public void registerChunkLoader(ChunkLoader loader, int chunkX, int chunkZ) {
        this.registerChunkLoader(loader, chunkX, chunkZ, true);
    }

    public void registerChunkLoader(ChunkLoader loader, int chunkX, int chunkZ, boolean autoLoad) {
        int hash = loader.getLoaderId();
        long index = Level.chunkHash(chunkX, chunkZ);
        if (!this.chunkLoaders.containsKey(index)) {
            this.chunkLoaders.put(index, new HashMap<>());
        } else if (this.chunkLoaders.get(index).containsKey(hash)) {
            return;
        }

        this.chunkLoaders.get(index).put(hash, loader);

        if (!this.loaders.containsKey(hash)) {
            this.loaderCounter.put(hash, 1);
            this.loaders.put(hash, loader);
        } else {
            this.loaderCounter.put(hash, this.loaderCounter.get(hash) + 1);
        }

        this.cancelUnloadChunkRequest(hash);

        if (autoLoad) {
            this.loadChunk(chunkX, chunkZ);
        }
    }

    public boolean unregisterChunkLoader(ChunkLoader loader, int chunkX, int chunkZ) {
        int loaderId = loader.getLoaderId();
        long chunkHash = Level.chunkHash(chunkX, chunkZ);
        Map<Integer, ChunkLoader> chunkLoadersIndex = this.chunkLoaders.get(chunkHash);
        if (chunkLoadersIndex != null) {
            ChunkLoader oldLoader = chunkLoadersIndex.remove(loaderId);
            if (oldLoader != null) {
                if (chunkLoadersIndex.isEmpty()) {
                    this.chunkLoaders.remove(chunkHash);
                    return this.unloadChunkRequest(chunkX, chunkZ, true);
                }

                int count = this.loaderCounter.get(loaderId);
                if (--count == 0) {
                    this.loaderCounter.remove(loaderId);
                    this.loaders.remove(loaderId);
                } else {
                    this.loaderCounter.put(loaderId, count);
                }
                return true;
            }
            return false;
        }
        return false;
    }

    public void checkTime() {
        if (!this.stopTime && this.gameRules.getBoolean(GameRule.DO_DAYLIGHT_CYCLE)) {
            this.time += tickRate;
        }
    }

    public void sendTime(Player... players) {
        SetTimePacket pk = new SetTimePacket();
        pk.time = (int) this.time;

        Server.broadcastPacket(players, pk);
    }

    public void sendTime() {
        this.sendTime(this.players.values().toArray(Player.EMPTY_ARRAY));
    }

    public GameRules getGameRules() {
        return gameRules;
    }

    public void releaseTickCachedBlocks() {
        synchronized (this.tickCachedBlocks) {
            for (var each : tickCachedBlocks.values()) {
                each.clearCachedStore();
            }
        }
    }

    public void doTick(int currentTick) {
        requireProvider();

        try {
            updateBlockLight(lightQueue);
            this.checkTime();
            if (currentTick >= nextTimeSendTick) { // Send time to client every 30 seconds to make sure it
                this.sendTime();
                nextTimeSendTick = currentTick + 30 * 20;
            }

            // 检查突出区块（玩家附近3x3区块）
            if ((currentTick & 127) == 0) { // 每127刻检查一次是比较合理的
                highLightChunks.clear();
                for (var player : this.players.values()) {
                    if (player.isOnline()) {
                        int chunkX = player.getChunkX();
                        int chunkZ = player.getChunkZ();
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dz = -1; dz <= 1; dz++) {
                                highLightChunks.add(Level.chunkHash(chunkX + dx, chunkZ + dz));
                            }
                        }
                    }
                }
            }

            // Tick Weather
            if (this.getDimension() != DIMENSION_NETHER && this.getDimension() != DIMENSION_THE_END && gameRules.getBoolean(GameRule.DO_WEATHER_CYCLE)) {
                this.rainTime--;
                if (this.rainTime <= 0) {
                    if (!this.setRaining(!this.raining)) {
                        if (this.raining) {
                            setRainTime(ThreadLocalRandom.current().nextInt(12000) + 12000);
                        } else {
                            setRainTime(ThreadLocalRandom.current().nextInt(168000) + 12000);
                        }
                    }
                }

                this.thunderTime--;
                if (this.thunderTime <= 0) {
                    if (!this.setThundering(!this.thundering)) {
                        if (this.thundering) {
                            setThunderTime(ThreadLocalRandom.current().nextInt(12000) + 3600);
                        } else {
                            setThunderTime(ThreadLocalRandom.current().nextInt(168000) + 12000);
                        }
                    }
                }

                if (this.isThundering()) {
                    Map<Long, IChunk> chunks = getChunks();
                    if (chunks instanceof Long2ObjectOpenHashMap<IChunk> fastChunks) {
                        ObjectIterator<? extends Long2ObjectMap.Entry<IChunk>> iter = fastChunks.long2ObjectEntrySet().fastIterator();
                        while (iter.hasNext()) {
                            Long2ObjectMap.Entry<IChunk> entry = iter.next();
                            performThunder(entry.getLongKey(), entry.getValue());
                        }
                    } else {
                        for (Map.Entry<Long, IChunk> entry : getChunks().entrySet()) {
                            performThunder(entry.getKey(), entry.getValue());
                        }
                    }
                }
            }

            this.skyLightSubtracted = this.calculateSkylightSubtracted(1);

            this.levelCurrentTick++;

            this.updateQueue.tick(this.getCurrentTick());

            while (!this.normalUpdateQueue.isEmpty()) {
                QueuedUpdate queuedUpdate = this.normalUpdateQueue.poll();
                Block block = getBlock(queuedUpdate.block, queuedUpdate.block.layer);
                BlockUpdateEvent event = new BlockUpdateEvent(block);
                this.server.getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    block.onUpdate(BLOCK_UPDATE_NORMAL);
                    if (queuedUpdate.neighbor != null) {
                        block.onNeighborChange(queuedUpdate.neighbor.getOpposite());
                    }
                }
            }

            if (!this.updateEntities.isEmpty()) {
                CompletableFuture.runAsync(() -> updateEntities.keySet()
                        .longParallelStream().mapToObj(id -> {
                            var entity = this.updateEntities.get(id);
                            if (entity instanceof EntityAsyncPrepare entityAsyncPrepare) {
                                return entityAsyncPrepare;
                            } else {
                                return null;
                            }
                        }).forEach(entityAsyncPrepare -> {
                            if (entityAsyncPrepare != null)
                                entityAsyncPrepare.asyncPrepare(currentTick);
                        }), Server.getInstance().computeThreadPool).join();
                for (long id : this.updateEntities.keySetLong()) {
                    Entity entity = this.updateEntities.get(id);
                    if (entity instanceof EntityIntelligent intelligent) {
                        if (intelligent.getBehaviorGroup() == null) {
                            this.updateEntities.remove(id);
                            continue;
                        }
                    }
                    if (entity == null) {
                        this.updateEntities.remove(id);
                        continue;
                    }
                    if (entity.closed || !entity.onUpdate(currentTick)) {
                        this.updateEntities.remove(id);
                    }
                }
            }

            this.updateBlockEntities.removeIf(blockEntity -> !blockEntity.isValid() || !blockEntity.onUpdate());

            this.tickChunks();

            synchronized (changedBlocks) {
                if (!this.changedBlocks.isEmpty()) {
                    if (!this.players.isEmpty()) {
                        var iter = changedBlocks.long2ObjectEntrySet().fastIterator();
                        while (iter.hasNext()) {
                            var entry = iter.next();
                            long index = entry.getLongKey();
                            var blocks = entry.getValue().get();
                            int chunkX = Level.getHashX(index);
                            int chunkZ = Level.getHashZ(index);
                            if (blocks == null || blocks.size() > MAX_BLOCK_CACHE) {
                                IChunk chunk = this.getChunk(chunkX, chunkZ);
                                chunk.reObfuscateChunk();
                                for (Player p : this.getChunkPlayers(chunkX, chunkZ).values()) {
                                    p.onChunkChanged(chunk);
                                }
                            } else {
                                Collection<Player> toSend = this.getChunkPlayers(chunkX, chunkZ).values();
                                Player[] playerArray = toSend.toArray(Player.EMPTY_ARRAY);
                                var size = blocks.size();
                                var blocksArray = new Vector3[size];
                                int i = 0;
                                for (int blockHash : blocks.keySet()) {
                                    Vector3 hash = getBlockXYZ(index, blockHash, this);
                                    blocksArray[i++] = hash;
                                }
                                this.sendBlocks(playerArray, blocksArray, UpdateBlockPacket.FLAG_ALL);
                            }
                        }
                    }

                    this.changedBlocks.clear();
                }
            }

            this.processChunkRequest();

            if (this.sleepTicks > 0 && --this.sleepTicks <= 0) {
                this.checkSleep();
            }

            for (long index : this.chunkPackets.keySet()) {
                int chunkX = Level.getHashX(index);
                int chunkZ = Level.getHashZ(index);
                Player[] chunkPlayers = this.getChunkPlayers(chunkX, chunkZ).values().toArray(Player.EMPTY_ARRAY);
                if (chunkPlayers.length > 0) {
                    for (var pk : this.chunkPackets.get(index)) {
                        Server.broadcastPacket(chunkPlayers, pk);
                    }
                }
            }
            this.chunkPackets.clear();

            if (gameRules.isStale()) {
                GameRulesChangedPacket packet = new GameRulesChangedPacket();
                packet.gameRules = gameRules;
                Server.broadcastPacket(players.values().toArray(Player.EMPTY_ARRAY), packet);
                gameRules.refresh();
            }
        } finally {
            // 清除所有tick缓存的方块
            releaseTickCachedBlocks();
        }
    }

    private void performThunder(long index, IChunk chunk) {
        if (areNeighboringChunksLoaded(index)) return;
        if (ThreadLocalRandom.current().nextInt(100000) == 0) {
            int LCG = this.getUpdateLCG() >> 2;

            int chunkX = chunk.getX() * 16;
            int chunkZ = chunk.getZ() * 16;
            Vector3 vector = this.adjustPosToNearbyEntity(new Vector3(chunkX + (LCG & 0xf), 0, chunkZ + (LCG >> 8 & 0xf)));

            int biome = this.getBiomeId(vector.getFloorX(), 70, vector.getFloorZ());
            if (Registries.BIOME.get(biome).rain() <= 0) {
                return;
            }

            var b = this.getBlock(vector.getFloorX(), vector.getFloorY(), vector.getFloorZ());
            if (b.getProperties() != BlockTallgrass.PROPERTIES && b.getProperties() != BlockWater.PROPERTIES)
                vector.y += 1;
            CompoundTag nbt = new CompoundTag()
                    .putList("Pos", new ListTag<DoubleTag>().add(new DoubleTag(vector.x))
                            .add(new DoubleTag(vector.y)).add(new DoubleTag(vector.z)))
                    .putList("Motion", new ListTag<DoubleTag>().add(new DoubleTag(0))
                            .add(new DoubleTag(0)).add(new DoubleTag(0)))
                    .putList("Rotation", new ListTag<FloatTag>().add(new FloatTag(0))
                            .add(new FloatTag(0)));

            EntityLightningBolt bolt = new EntityLightningBolt(chunk, nbt);
            LightningStrikeEvent ev = new LightningStrikeEvent(this, bolt);
            getServer().getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                bolt.spawnToAll();
            } else {
                bolt.setEffect(false);
            }

            this.addLevelSoundEvent(vector, LevelSoundEventPacket.SOUND_THUNDER, -1, Registries.ENTITY.getEntityNetworkId(EntityID.LIGHTNING_BOLT));
            this.addLevelSoundEvent(vector, LevelSoundEventPacket.SOUND_EXPLODE, -1, Registries.ENTITY.getEntityNetworkId(EntityID.LIGHTNING_BOLT));
        }
    }

    public Vector3 adjustPosToNearbyEntity(Vector3 pos) {
        pos.y = this.getHighestBlockAt(pos.getFloorX(), pos.getFloorZ());
        AxisAlignedBB axisalignedbb = new SimpleAxisAlignedBB(pos.x, pos.y, pos.z, pos.getX(), isOverWorld() ? 320 : 255, pos.getZ()).expand(3, 3, 3);
        List<Entity> list = new ArrayList<>();

        for (Entity entity : this.getCollidingEntities(axisalignedbb)) {
            if (entity.isAlive() && canBlockSeeSky(entity)) {
                list.add(entity);
            }
        }

        if (!list.isEmpty()) {
            return list.get(ThreadLocalRandom.current().nextInt(list.size())).getPosition();
        } else {
            if (pos.getY() == -1) {
                pos = pos.up(2);
            }

            return pos;
        }
    }

    public void checkSleep() {
        if (this.players.isEmpty()) {
            return;
        }

        int playerCount = 0;
        int sleepingPlayerCount = 0;
        for (Player p : this.getPlayers().values()) {
            playerCount++;
            if (p.isSleeping()) {
                sleepingPlayerCount++;
            }
        }

        if (playerCount > 0 && sleepingPlayerCount / playerCount * 100 >= this.gameRules.getInteger(GameRule.PLAYERS_SLEEPING_PERCENTAGE)) {
            int time = this.getTime() % Level.TIME_FULL;

            if (time >= Level.TIME_NIGHT && time < Level.TIME_SUNRISE) {
                this.setTime(this.getTime() + Level.TIME_FULL - time);

                for (Player p : this.getPlayers().values()) {
                    p.stopSleep();
                }
            }
        }
    }

    public void sendBlockExtraData(int x, int y, int z, int id, int data) {
        this.sendBlockExtraData(x, y, z, id, data, this.getChunkPlayers(x >> 4, z >> 4).values());
    }

    public void sendBlockExtraData(int x, int y, int z, int id, int data, Collection<Player> players) {
        sendBlockExtraData(x, y, z, id, data, players.toArray(Player.EMPTY_ARRAY));
    }

    public void sendBlockExtraData(int x, int y, int z, int id, int data, Player[] players) {
        LevelEventPacket pk = new LevelEventPacket();
        pk.evid = LevelEventPacket.EVENT_SET_DATA;
        pk.x = x + 0.5f;
        pk.y = y + 0.5f;
        pk.z = z + 0.5f;
        pk.data = (data << 8) | id;

        Server.broadcastPacket(players, pk);
    }

    public void sendBlocks(Player[] target, Vector3[] blocks) {
        this.sendBlocks(target, blocks, UpdateBlockPacket.FLAG_NONE, 0);
        this.sendBlocks(target, blocks, UpdateBlockPacket.FLAG_NONE, 1);
    }

    public void sendBlocks(Player[] target, Vector3[] blocks, int flags) {
        this.sendBlocks(target, blocks, flags, 0);
        this.sendBlocks(target, blocks, flags, 1);
    }

    public void sendBlocks(Player[] target, Vector3[] blocks, int flags, boolean optimizeRebuilds) {
        this.sendBlocks(target, blocks, flags, 0, optimizeRebuilds);
        this.sendBlocks(target, blocks, flags, 1, optimizeRebuilds);
    }

    public void sendBlocks(Player[] target, Vector3[] blocks, int flags, int dataLayer) {
        this.sendBlocks(target, blocks, flags, dataLayer, false);
    }

    public void sendBlocks(Player[] target, Vector3[] blocks, int flags, int dataLayer, boolean optimizeRebuilds) {
        int size = 0;
        for (Vector3 block : blocks) {
            if (block != null) size++;
        }
        var packets = new ArrayList<UpdateBlockPacket>(size);
        LongSet chunks = null;
        if (optimizeRebuilds) {
            chunks = new LongOpenHashSet();
        }
        for (Vector3 b : blocks) {
            if (b == null) {
                continue;
            }
            boolean first = !optimizeRebuilds;

            if (optimizeRebuilds) {
                long index = Level.chunkHash((int) b.x >> 4, (int) b.z >> 4);
                if (!chunks.contains(index)) {
                    chunks.add(index);
                    first = true;
                }
            }

            UpdateBlockPacket updateBlockPacket = new UpdateBlockPacket();
            updateBlockPacket.x = (int) b.x;
            updateBlockPacket.y = (int) b.y;
            updateBlockPacket.z = (int) b.z;
            updateBlockPacket.flags = first ? flags : UpdateBlockPacket.FLAG_NONE;
            updateBlockPacket.dataLayer = dataLayer;
            int runtimeId;
            if (b instanceof Block block) {
                runtimeId = block.getRuntimeId();
            } else if (b instanceof Vector3WithRuntimeId vRid) {
                if (dataLayer == 0) {
                    runtimeId = vRid.getRuntimeIdLayer0();
                } else {
                    runtimeId = vRid.getRuntimeIdLayer1();
                }
            } else {
                int hash = getBlockRuntimeId((int) b.x, (int) b.y, (int) b.z, dataLayer);
                if (hash == Integer.MIN_VALUE) {
                    continue;
                }
                runtimeId = hash;
            }
            try {
                updateBlockPacket.blockRuntimeId = runtimeId;
            } catch (NoSuchElementException e) {
                throw new IllegalStateException("Unable to create BlockUpdatePacket at (" +
                        b.x + ", " + b.y + ", " + b.z + ") in " + getName(), e);
            }
            packets.add(updateBlockPacket);
        }
        for (var p : packets) {
            Server.broadcastPacket(target, p);
        }
    }

    private void tickChunks() {
        if (this.chunksPerTicks <= 0 || this.loaders.isEmpty()) {
            this.chunkTickList.clear();
            return;
        }

        int chunksPerLoader = Math.min(200, Math.max(1, (int) (((double) (this.chunksPerTicks - this.loaders.size()) / this.loaders.size() + 0.5))));
        int randRange = 3 + chunksPerLoader / 30;
        randRange = Math.min(randRange, this.chunkTickRadius);

        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (!this.loaders.isEmpty()) {
            for (ChunkLoader loader : this.loaders.values()) {
                int chunkX = (int) loader.getX() >> 4;
                int chunkZ = (int) loader.getZ() >> 4;

                long index = Level.chunkHash(chunkX, chunkZ);
                int existingLoaders = Math.max(0, this.chunkTickList.getOrDefault(index, 0));
                this.chunkTickList.put(index, existingLoaders + 1);
                for (int chunk = 0; chunk < chunksPerLoader; ++chunk) {
                    int dx = random.nextInt(2 * randRange) - randRange;
                    int dz = random.nextInt(2 * randRange) - randRange;
                    long hash = Level.chunkHash(dx + chunkX, dz + chunkZ);
                    if (!this.chunkTickList.containsKey(hash) && requireProvider().isChunkLoaded(hash)) {
                        this.chunkTickList.put(hash, -1);
                    }
                }
            }
        }

        if (!chunkTickList.isEmpty()) {
            ObjectIterator<Long2IntMap.Entry> iter = chunkTickList.long2IntEntrySet().iterator();
            while (iter.hasNext()) {
                Long2IntMap.Entry entry = iter.next();
                long index = entry.getLongKey();
                if (!areNeighboringChunksLoaded(index)) {
                    iter.remove();
                    continue;
                }

                int loaders = entry.getIntValue();

                int chunkX = getHashX(index);
                int chunkZ = getHashZ(index);

                IChunk chunk;
                if ((chunk = this.getChunk(chunkX, chunkZ, false)) == null) {
                    iter.remove();
                    continue;
                } else if (loaders <= 0) {
                    iter.remove();
                }

                for (Entity entity : chunk.getEntities().values()) {
                    entity.scheduleUpdate();
                }
                int tickSpeed = gameRules.getInteger(GameRule.RANDOM_TICK_SPEED);
                if (tickSpeed <= 0) {
                    continue;
                }

                for (ChunkSection section : chunk.getSections()) {
                    if (section == null || section.isEmpty()) {
                        continue;
                    }
                    for (int i = 0; i < tickSpeed; ++i) {
                        int lcg = this.getUpdateLCG();
                        int x = lcg & 0x0f;
                        int y = lcg >>> 8 & 0x0f;
                        int z = lcg >>> 16 & 0x0f;
                        BlockState state = section.getBlockState(x, y, z);
                        if (randomTickBlocks.contains(state.getIdentifier())) {
                            Block block = Block.get(state, this, x, y, z);
                            block.setLevel(this);
                            block.onUpdate(BLOCK_UPDATE_RANDOM);
                        }
                    }
                }
            }
        }

        if (this.clearChunksOnTick) {
            this.chunkTickList.clear();
        }
    }

    public boolean save() {
        return this.save(false);
    }

    public boolean save(boolean force) {
        if (!this.getAutoSave() && !force) {
            return false;
        }

        this.server.getPluginManager().callEvent(new LevelSaveEvent(this));

        LevelProvider levelProvider = this.requireProvider();
        levelProvider.setTime((int) this.time);
        levelProvider.setRaining(this.raining);
        levelProvider.setRainTime(this.rainTime);
        levelProvider.setThundering(this.thundering);
        levelProvider.setThunderTime(this.thunderTime);
        levelProvider.setCurrentTick(this.levelCurrentTick);
        levelProvider.setGameRules(this.gameRules);
        this.saveChunks();
        levelProvider.saveLevelData();
        return true;
    }

    public void saveChunks() {
        requireProvider().saveChunks();
    }

    @Deprecated
    @DeprecationDetails(reason = "Was moved to RedstoneComponent", since = "1.4.0.0-PN",
            replaceWith = "RedstoneComponent#updateAroundRedstone", by = "PowerNukkit")
    public void updateAroundRedstone(Vector3 pos, BlockFace face) {
        Location loc = new Location(pos.x, pos.y, pos.z, this);
        RedstoneComponent.updateAroundRedstone(loc, face);
    }

    public void updateComparatorOutputLevel(Vector3 v) {
        updateComparatorOutputLevelSelective(v, true);
    }

    public void updateComparatorOutputLevelSelective(Vector3 v, boolean observer) {
        for (BlockFace face : Plane.HORIZONTAL) {
            temporalVector.setComponentsAdding(v, face);

            if (!this.isChunkLoaded((int) temporalVector.x >> 4, (int) temporalVector.z >> 4)) {
                continue;
            }
            Block block1 = this.getBlock(temporalVector);

            if (BlockID.OBSERVER.equals(block1.getId())) {
                if (observer) {
                    block1.onNeighborChange(face.getOpposite());
                }
            } else if (BlockRedstoneDiode.isDiode(block1)) {
                block1.onUpdate(BLOCK_UPDATE_REDSTONE);
            } else if (block1.isNormalBlock()) {
                block1 = this.getBlock(temporalVector.setComponentsAdding(temporalVector, face));

                if (BlockRedstoneDiode.isDiode(block1)) {
                    block1.onUpdate(BLOCK_UPDATE_REDSTONE);
                }
            }
        }

        if (!observer) {
            return;
        }

        for (BlockFace face : Plane.VERTICAL) {
            Block block1 = this.getBlock(temporalVector.setComponentsAdding(v, face));

            if (BlockID.OBSERVER.equals(block1.getId())) {
                block1.onNeighborChange(face.getOpposite());
            }
        }
    }

    public void updateAround(Vector3 pos) {
        Block block = getBlock(pos);
        for (BlockFace face : BlockFace.values()) {
            final Block side = block.getSideAtLayer(0, face);
            normalUpdateQueue.add(new QueuedUpdate(side, face));
            normalUpdateQueue.add(new QueuedUpdate(side.getLevelBlockAtLayer(1), face));
        }
    }

    public void neighborChangeAroundImmediately(int x, int y, int z) {
        neighborChangeAroundImmediately(new Vector3(x, y, z));
    }

    /**
     * 立即对围绕指定位置的方块发送neighborChange更新
     *
     * @param pos 指定位置
     */
    public void neighborChangeAroundImmediately(Vector3 pos) {
        for (var face : BlockFace.values()) {
            var neighborBlock = getBlock(pos.getSide(face));
            neighborBlock.onNeighborChange(face.getOpposite());
        }
    }

    public void updateAroundObserver(Vector3 pos) {
        for (var face : BlockFace.values()) {
            var neighborBlock = getBlock(pos.getSide(face));
            if (neighborBlock.getId() == BlockID.OBSERVER)
                neighborBlock.onNeighborChange(face.getOpposite());
        }
    }

    public void updateAround(int x, int y, int z) {
        updateAround(new Vector3(x, y, z));
    }

    public void scheduleUpdate(Block pos, int delay) {
        this.scheduleUpdate(pos, pos, delay, 0, true);
    }

    public void scheduleUpdate(Block pos, int delay, boolean checkBlockWhenUpdate) {
        this.scheduleUpdate(pos, pos, delay, 0, true, checkBlockWhenUpdate);
    }

    public void scheduleUpdate(Block block, Vector3 pos, int delay) {
        this.scheduleUpdate(block, pos, delay, 0, true);
    }

    public void scheduleUpdate(Block block, Vector3 pos, int delay, int priority) {
        this.scheduleUpdate(block, pos, delay, priority, true);
    }

    public void scheduleUpdate(Block block, Vector3 pos, int delay, int priority, boolean checkArea) {
        this.scheduleUpdate(block, pos, delay, priority, checkArea, true);
    }

    public void scheduleUpdate(Block block, Vector3 pos, int delay, int priority, boolean checkArea, boolean checkBlockWhenUpdate) {
        if (block.getId().equals(BlockID.AIR) || (checkArea && !this.isChunkLoaded(block.getFloorX() >> 4, block.getFloorZ() >> 4))) {
            return;
        }

        BlockUpdateEntry entry = new BlockUpdateEntry(pos.floor(), block, delay + getCurrentTick(), priority, checkBlockWhenUpdate);

        if (!this.updateQueue.contains(entry)) {
            this.updateQueue.add(entry);
        }
    }

    public boolean cancelSheduledUpdate(Vector3 pos, Block block) {
        return this.updateQueue.remove(new BlockUpdateEntry(pos, block));
    }

    public boolean isUpdateScheduled(Vector3 pos, Block block) {
        return this.updateQueue.contains(new BlockUpdateEntry(pos, block));
    }

    public boolean isBlockTickPending(Vector3 pos, Block block) {
        return this.updateQueue.isBlockTickPending(pos, block);
    }

    public Set<BlockUpdateEntry> getPendingBlockUpdates(IChunk chunk) {
        int minX = (chunk.getX() << 4) - 2;
        int maxX = minX + 16 + 2;
        int minZ = (chunk.getZ() << 4) - 2;
        int maxZ = minZ + 16 + 2;

        return this.getPendingBlockUpdates(new SimpleAxisAlignedBB(minX, isOverWorld() ? -64 : 0, minZ, maxX, isOverWorld() ? 320 : 256, maxZ));
    }

    public Set<BlockUpdateEntry> getPendingBlockUpdates(AxisAlignedBB boundingBox) {
        return updateQueue.getPendingBlockUpdates(boundingBox);
    }

    public List<Block> scanBlocks(@NotNull AxisAlignedBB bb, @NotNull BiPredicate<BlockVector3, BlockState> condition) {
        BlockVector3 min = new BlockVector3(NukkitMath.floorDouble(bb.getMinX()), NukkitMath.floorDouble(bb.getMinY()), NukkitMath.floorDouble(bb.getMinZ()));
        BlockVector3 max = new BlockVector3(NukkitMath.floorDouble(bb.getMaxX()), NukkitMath.floorDouble(bb.getMaxY()), NukkitMath.floorDouble(bb.getMaxZ()));
        ChunkVector2 minChunk = min.getChunkVector();
        ChunkVector2 maxChunk = max.getChunkVector();
        return IntStream.rangeClosed(minChunk.getX(), maxChunk.getX())
                .mapToObj(x -> IntStream.rangeClosed(minChunk.getZ(), maxChunk.getZ()).mapToObj(z -> new ChunkVector2(x, z)))
                .flatMap(Function.identity()).parallel()
                .map(this::getChunk).filter(Objects::nonNull)
                .flatMap(chunk -> chunk.scanBlocks(min, max, condition))
                .collect(Collectors.toList());
    }

    public Block[] getCollisionBlocks(AxisAlignedBB bb) {
        return this.getCollisionBlocks(bb, false);
    }

    public Block[] getCollisionBlocks(AxisAlignedBB bb, boolean targetFirst) {
        return getCollisionBlocks(bb, targetFirst, false);
    }

    public Block[] getCollisionBlocks(AxisAlignedBB bb, boolean targetFirst, boolean ignoreCollidesCheck) {
        return getCollisionBlocks(bb, targetFirst, ignoreCollidesCheck, block -> !block.getId().equals(BlockID.AIR));
    }

    public Block[] getCollisionBlocks(AxisAlignedBB bb, boolean targetFirst, boolean ignoreCollidesCheck, Predicate<Block> condition) {
        int minX = NukkitMath.floorDouble(bb.getMinX());
        int minY = NukkitMath.floorDouble(bb.getMinY());
        int minZ = NukkitMath.floorDouble(bb.getMinZ());
        int maxX = NukkitMath.ceilDouble(bb.getMaxX());
        int maxY = NukkitMath.ceilDouble(bb.getMaxY());
        int maxZ = NukkitMath.ceilDouble(bb.getMaxZ());

        List<Block> collides = new ArrayList<>();

        if (targetFirst) {
            for (int z = minZ; z <= maxZ; ++z) {
                for (int x = minX; x <= maxX; ++x) {
                    for (int y = minY; y <= maxY; ++y) {
                        Block block = this.getBlock(this.temporalVector.setComponents(x, y, z), false);
                        if (block != null && condition.test(block) && (ignoreCollidesCheck || block.collidesWithBB(bb))) {
                            return new Block[]{block};
                        }
                    }
                }
            }
        } else {
            for (int z = minZ; z <= maxZ; ++z) {
                for (int x = minX; x <= maxX; ++x) {
                    for (int y = minY; y <= maxY; ++y) {
                        Block block = this.getBlock(this.temporalVector.setComponents(x, y, z), false);
                        if (block != null && condition.test(block) && (ignoreCollidesCheck || block.collidesWithBB(bb))) {
                            collides.add(block);
                        }
                    }
                }
            }
        }

        return collides.toArray(Block.EMPTY_ARRAY);
    }

    public boolean isFullBlock(Vector3 pos) {
        AxisAlignedBB bb;
        if (pos instanceof Block) {
            if (((Block) pos).isSolid()) {
                return true;
            }
            bb = ((Block) pos).getBoundingBox();
        } else {
            bb = this.getBlock(pos).getBoundingBox();
        }

        return bb != null && bb.getAverageEdgeLength() >= 1;
    }

    public Block[] getTickCachedCollisionBlocks(AxisAlignedBB bb) {
        return this.getTickCachedCollisionBlocks(bb, false);
    }

    public Block[] getTickCachedCollisionBlocks(AxisAlignedBB bb, boolean targetFirst) {
        return getTickCachedCollisionBlocks(bb, targetFirst, false);
    }

    public Block[] getTickCachedCollisionBlocks(AxisAlignedBB bb, boolean targetFirst, boolean ignoreCollidesCheck) {
        return getTickCachedCollisionBlocks(bb, targetFirst, ignoreCollidesCheck, block -> !block.getId().equals(BlockID.AIR));
    }

    public Block[] getTickCachedCollisionBlocks(AxisAlignedBB bb, boolean targetFirst, boolean ignoreCollidesCheck, Predicate<Block> condition) {
        int minX = NukkitMath.floorDouble(bb.getMinX());
        int minY = NukkitMath.floorDouble(bb.getMinY());
        int minZ = NukkitMath.floorDouble(bb.getMinZ());
        int maxX = NukkitMath.ceilDouble(bb.getMaxX());
        int maxY = NukkitMath.ceilDouble(bb.getMaxY());
        int maxZ = NukkitMath.ceilDouble(bb.getMaxZ());

        List<Block> collides = new ArrayList<>();

        if (targetFirst) {
            for (int z = minZ; z <= maxZ; ++z) {
                for (int x = minX; x <= maxX; ++x) {
                    for (int y = minY; y <= maxY; ++y) {
                        Block block = this.getTickCachedBlock(this.temporalVector.setComponents(x, y, z), false);
                        if (block != null && condition.test(block) && (ignoreCollidesCheck || block.collidesWithBB(bb))) {
                            return new Block[]{block};
                        }
                    }
                }
            }
        } else {
            for (int z = minZ; z <= maxZ; ++z) {
                for (int x = minX; x <= maxX; ++x) {
                    for (int y = minY; y <= maxY; ++y) {
                        Block block = this.getTickCachedBlock(this.temporalVector.setComponents(x, y, z), false);
                        if (block != null && condition.test(block) && (ignoreCollidesCheck || block.collidesWithBB(bb))) {
                            collides.add(block);
                        }
                    }
                }
            }
        }

        return collides.toArray(Block.EMPTY_ARRAY);
    }

    public AxisAlignedBB[] getCollisionCubes(Entity entity, AxisAlignedBB bb) {
        return this.getCollisionCubes(entity, bb, true);
    }

    public AxisAlignedBB[] getCollisionCubes(Entity entity, AxisAlignedBB bb, boolean entities) {
        return getCollisionCubes(entity, bb, entities, false);
    }

    public AxisAlignedBB[] getCollisionCubes(Entity entity, AxisAlignedBB bb, boolean entities, boolean solidEntities) {
        int minX = NukkitMath.floorDouble(bb.getMinX());
        int minY = NukkitMath.floorDouble(bb.getMinY());
        int minZ = NukkitMath.floorDouble(bb.getMinZ());
        int maxX = NukkitMath.ceilDouble(bb.getMaxX());
        int maxY = NukkitMath.ceilDouble(bb.getMaxY());
        int maxZ = NukkitMath.ceilDouble(bb.getMaxZ());

        List<AxisAlignedBB> collides = new ArrayList<>();

        for (int z = minZ; z <= maxZ; ++z) {
            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    Block block = this.getBlock(this.temporalVector.setComponents(x, y, z), false);
                    if (!block.canPassThrough() && block.collidesWithBB(bb)) {
                        collides.add(block.getBoundingBox());
                    }
                }
            }
        }

        if (entities || solidEntities) {
            for (Entity ent : this.getCollidingEntities(bb.grow(0.25f, 0.25f, 0.25f), entity)) {
                if (solidEntities && !ent.canPassThrough()) {
                    collides.add(ent.boundingBox.clone());
                }
            }
        }

        return collides.toArray(AxisAlignedBB.EMPTY_ARRAY);
    }

    public List<AxisAlignedBB> fastCollisionCubes(Entity entity, AxisAlignedBB bb) {
        return this.fastCollisionCubes(entity, bb, true);
    }

    public List<AxisAlignedBB> fastCollisionCubes(Entity entity, AxisAlignedBB bb, boolean entities) {
        return fastCollisionCubes(entity, bb, entities, false);
    }

    public List<AxisAlignedBB> fastCollisionCubes(Entity entity, AxisAlignedBB bb, boolean entities, boolean solidEntities) {
        int minX = NukkitMath.floorDouble(bb.getMinX());
        int minY = NukkitMath.floorDouble(bb.getMinY());
        int minZ = NukkitMath.floorDouble(bb.getMinZ());
        int maxX = NukkitMath.ceilDouble(bb.getMaxX());
        int maxY = NukkitMath.ceilDouble(bb.getMaxY());
        int maxZ = NukkitMath.ceilDouble(bb.getMaxZ());

        List<AxisAlignedBB> collides = new ArrayList<>();

        for (int z = minZ; z <= maxZ; ++z) {
            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    Block block = this.getBlock(this.temporalVector.setComponents(x, y, z), false);
                    if (!block.canPassThrough() && block.collidesWithBB(bb)) {
                        collides.add(block.getBoundingBox());
                    }
                }
            }
        }

        if (entities || solidEntities) {
            var grownBB = bb.grow(0.25f, 0.25f, 0.25f);
            collides.addAll(this.streamCollidingEntities(grownBB, entity)
                    .filter(ent -> solidEntities && !ent.canPassThrough()).map(ent -> ent.boundingBox.clone()).toList());
        }

        return collides;
    }

    public boolean hasCollision(Entity entity, AxisAlignedBB bb, boolean entities) {
        int minX = NukkitMath.floorDouble(NukkitMath.round(bb.getMinX(), 4));
        int minY = NukkitMath.floorDouble(NukkitMath.round(bb.getMinY(), 4));
        int minZ = NukkitMath.floorDouble(NukkitMath.round(bb.getMinZ(), 4));
        int maxX = NukkitMath.ceilDouble(NukkitMath.round(bb.getMaxX(), 4) - 0.00001);
        int maxY = NukkitMath.ceilDouble(NukkitMath.round(bb.getMaxY(), 4) - 0.00001);
        int maxZ = NukkitMath.ceilDouble(NukkitMath.round(bb.getMaxZ(), 4) - 0.00001);

        for (int z = minZ; z <= maxZ; ++z) {
            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    Block block = this.getBlock(this.temporalVector.setComponents(x, y, z));
                    if (!block.canPassThrough() && block.collidesWithBB(bb)) {
                        return true;
                    }
                }
            }
        }

        if (entities) {
            return this.fastCollidingEntities(bb.grow(0.25f, 0.25f, 0.25f), entity).size() > 0;
        }
        return false;
    }

    public int getFullLight(Vector3 pos) {
        IChunk chunk = this.getChunk((int) pos.x >> 4, (int) pos.z >> 4, false);
        int level = 0;
        if (chunk != null) {
            level = chunk.getBlockSkyLight((int) pos.x & 0x0f, ensureY((int) pos.y), (int) pos.z & 0x0f);
            level -= this.skyLightSubtracted;

            if (level < 15) {
                level = Math.max(chunk.getBlockLight((int) pos.x & 0x0f, ensureY((int) pos.y), (int) pos.z & 0x0f),
                        level);
            }
        }

        return level;
    }

    public int calculateSkylightSubtracted(float tickDiff) {
        float d = 1.0F - (this.getRainStrength(tickDiff) * 5.0F) / 16.0F;
        float e = 1.0F - (this.getThunderStrength(tickDiff) * 5.0F) / 16.0F;
        float f = 0.5F + 2.0F * MathHelper.clamp(MathHelper.cos(this.getCelestialAngle(tickDiff) * 6.2831855F), -0.25F, 0.25F);
        return (int) ((1.0F - f * d * e) * 11.0F);
        /* Old NukkitX Code
        float angle = this.getCelestialAngle(tickDiff);
        float light = 1.0F - (MathHelper.cos(angle * ((float) Math.PI * 2F)) * 2.0F + 0.5F);
        light = MathHelper.clamp(light, 0.0F, 1.0F);
        light = 1.0F - light;
        light = (float) ((double) light * (1.0D - (double) (this.getRainStrength(tickDiff) * 5.0F) / 16.0D));
        light = (float) ((double) light * (1.0D - (double) (this.getThunderStrength(tickDiff) * 5.0F) / 16.0D));
        light = 1.0F - light;
        return (int) (light * 11.0F);
         */
    }

    public float getRainStrength(float tickDiff) {
        return isRaining() ? 1 : 0; // TODO: real implementation
    }

    public float getThunderStrength(float tickDiff) {
        return isThundering() ? 1 : 0; // TODO: real implementation
    }

    public float getCelestialAngle(float tickDiff) {
        return calculateCelestialAngle(getTime(), tickDiff);
    }

    public float calculateCelestialAngle(int time, float tickDiff) {
        int i = (int) (time % 24000L);
        float angle = ((float) i + tickDiff) / 24000.0F - 0.25F;

        if (angle < 0.0F) {
            ++angle;
        }

        if (angle > 1.0F) {
            --angle;
        }

        float f1 = 1.0F - (float) ((Math.cos((double) angle * Math.PI) + 1.0D) / 2.0D);
        angle = angle + (f1 - angle) / 3.0F;
        return angle;
    }

    public int getMoonPhase(long worldTime) {
        return (int) (worldTime / 24000 % 8 + 8) % 8;
    }

    public int getBlockRuntimeId(int x, int y, int z) {
        return getBlockRuntimeId(x, y, z, 0);
    }

    public int getBlockRuntimeId(int x, int y, int z, int layer) {
        final var tmp = this.getChunk(x >> 4, z >> 4, false);
        if (tmp == null) {
            return Integer.MIN_VALUE;
        }
        return tmp.getBlockState(x & 0x0f, ensureY(y), z & 0x0f, layer).blockStateHash();
    }

    public Set<Block> getBlockAround(Vector3 pos) {
        Set<Block> around = new HashSet<>();
        Block block = getBlock(pos);
        for (BlockFace face : BlockFace.values()) {
            Block side = block.getSideAtLayer(0, face);
            around.add(side);
        }
        return around;
    }

    public Block getTickCachedBlock(Vector3 pos) {
        return getTickCachedBlock(pos, 0);
    }

    public Block getTickCachedBlock(Vector3 pos, int layer) {
        return this.getTickCachedBlock(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), layer);
    }

    public Block getTickCachedBlock(Vector3 pos, boolean load) {
        return getTickCachedBlock(pos, 0, load);
    }

    public Block getTickCachedBlock(Vector3 pos, int layer, boolean load) {
        return this.getTickCachedBlock(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), layer, load);
    }

    public Block getTickCachedBlock(int x, int y, int z) {
        return getTickCachedBlock(x, y, z, 0);
    }

    public Block getTickCachedBlock(int x, int y, int z, int layer) {
        return getTickCachedBlock(x, y, z, layer, true);
    }

    public Block getTickCachedBlock(int x, int y, int z, boolean load) {
        return getTickCachedBlock(x, y, z, 0, load);
    }

    public Block getTickCachedBlock(int x, int y, int z, int layer, boolean load) {
        return tickCachedBlocks.computeIfAbsent(Level.chunkHash(x >> 4, z >> 4), key -> new SimpleTickCachedBlockStore(this))
                .computeFromCachedStore(x, y, z, layer, () -> getBlock(x, y, z, layer, load));
    }

    public Block getBlock(Vector3 pos) {
        return getBlock(pos, 0);
    }

    public Block getBlock(Vector3 pos, int layer) {
        return this.getBlock(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), layer);
    }

    public Block getBlock(Vector3 pos, boolean load) {
        return getBlock(pos, 0, load);
    }

    public Block getBlock(Vector3 pos, int layer, boolean load) {
        return this.getBlock(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), layer, load);
    }

    public Block getBlock(int x, int y, int z) {
        return getBlock(x, y, z, 0);
    }

    public Block getBlock(int x, int y, int z, int layer) {
        return getBlock(x, y, z, layer, true);
    }

    public Block getBlock(int x, int y, int z, boolean load) {
        return getBlock(x, y, z, 0, load);
    }

    public Block getBlock(int x, int y, int z, int layer, boolean load) {
        BlockState fullState = BlockAir.PROPERTIES.getDefaultState();
        if (isYInRange(y)) {
            int cx = x >> 4;
            int cz = z >> 4;
            IChunk chunk;
            if (load) {
                chunk = getChunk(cx, cz);
            } else {
                chunk = getChunkIfLoaded(cx, cz);
            }
            if (chunk != null) {
                fullState = chunk.getBlockState(x & 0xF, y, z & 0xF, layer);
            }
        }
        return Registries.BLOCK.get(fullState, x, y, z, layer, this);
    }

    public String getBlockIdAt(int x, int y, int z) {
        return getBlockIdAt(x, y, z, 0);
    }

    public String getBlockIdAt(int x, int y, int z, int layer) {
        return this.getChunk(x >> 4, z >> 4, true).getBlockState(x & 0x0f, ensureY(y), z & 0x0f, layer).getIdentifier();
    }

    public synchronized void updateAllLight(Vector3 pos) {
        this.updateBlockSkyLight((int) pos.x, (int) pos.y, (int) pos.z);
        this.addLightUpdate((int) pos.x, (int) pos.y, (int) pos.z);
    }

    public void updateBlockSkyLight(int x, int y, int z) {
        IChunk chunk = getChunkIfLoaded(x >> 4, z >> 4);

        if (chunk == null) return;

        int oldHeightMap = chunk.getHeightMap(x & 0xf, z & 0xf);
        Block sourceBlock = getBlock(x, y, z);

        int yPlusOne = y + 1;

        int newHeightMap;
        if (yPlusOne == oldHeightMap) { // Block changed directly beneath the heightmap. Check if a block was removed or changed to a different light-filter
            newHeightMap = chunk.recalculateHeightMapColumn(x & 0x0f, z & 0x0f);
        } else if (yPlusOne > oldHeightMap) { // Block changed above the heightmap
            if (sourceBlock.getLightFilter() > 1 || sourceBlock.diffusesSkyLight()) {
                chunk.setHeightMap(x & 0xf, z & 0xf, yPlusOne);
                newHeightMap = yPlusOne;
            } else { // Block changed which has no effect on direct sky light, for example placing or removing glass.
                return;
            }
        } else { // Block changed below heightmap
            newHeightMap = oldHeightMap;
        }

        if (newHeightMap > oldHeightMap) { // Heightmap increase, block placed, remove sky light
            for (int i = y; i >= oldHeightMap; --i) {
                setBlockSkyLightAt(x, i, z, 0);
            }
        } else if (newHeightMap < oldHeightMap) { // Heightmap decrease, block changed or removed, add sky light
            for (int i = y; i >= newHeightMap; --i) {
                setBlockSkyLightAt(x, i, z, 15);
            }
        } else { // No heightmap change, block changed "underground"
            setBlockSkyLightAt(x, y, z, Math.max(0, getHighestAdjacentBlockSkyLight(x, y, z) - sourceBlock.getLightFilter()));
        }
    }

    /**
     * Returns the highest block skylight level available in the positions adjacent to the specified block coordinates.
     */

    public int getHighestAdjacentBlockSkyLight(int x, int y, int z) {
        int[] lightLevels = new int[]{
                getBlockSkyLightAt(x + 1, y, z),
                getBlockSkyLightAt(x - 1, y, z),
                getBlockSkyLightAt(x, y + 1, z),
                getBlockSkyLightAt(x, y - 1, z),
                getBlockSkyLightAt(x, y, z + 1),
                getBlockSkyLightAt(x, y, z - 1),
        };

        int maxValue = lightLevels[0];
        for (int i = 1; i < lightLevels.length; i++) {
            if (lightLevels[i] > maxValue) {
                maxValue = lightLevels[i];
            }
        }

        return maxValue;
    }

    public void updateBlockLight(Map<Long, Map<Integer, Object>> map) {
        int size = map.size();
        if (size == 0) {
            return;
        }
        Queue<Long> lightPropagationQueue = new ConcurrentLinkedQueue<>();
        Queue<Object[]> lightRemovalQueue = new ConcurrentLinkedQueue<>();
        Long2ObjectOpenHashMap<Object> visited = new Long2ObjectOpenHashMap<>();
        Long2ObjectOpenHashMap<Object> removalVisited = new Long2ObjectOpenHashMap<>();

        var iter = map.entrySet().iterator();
        while (iter.hasNext() && size-- > 0) {
            var entry = iter.next();
            iter.remove();
            long index = entry.getKey();
            var blocks = entry.getValue();
            int chunkX = Level.getHashX(index);
            int chunkZ = Level.getHashZ(index);
            int bx = chunkX << 4;
            int bz = chunkZ << 4;
            for (int blockHash : blocks.keySet()) {
                int hi = (byte) (blockHash >>> 16);
                int lo = (short) blockHash;
                int y = ensureY(lo - 64);
                int x = (hi & 0xF) + bx;
                int z = ((hi >> 4) & 0xF) + bz;
                IChunk chunk = getChunk(x >> 4, z >> 4, false);
                if (chunk != null) {
                    int lcx = x & 0xF;
                    int lcz = z & 0xF;
                    int oldLevel = chunk.getBlockLight(lcx, y, lcz);
                    int newLevel = Registries.BLOCK.get(chunk.getBlockState(lcx, y, lcz), x, y, z, this).getLightLevel();
                    if (oldLevel != newLevel) {
                        this.setBlockLightAt(x, y, z, newLevel);
                        if (newLevel < oldLevel) {
                            removalVisited.put(Hash.hashBlock(x, y, z), changeBlocksPresent);
                            lightRemovalQueue.add(new Object[]{Hash.hashBlock(x, y, z), oldLevel});
                        } else {
                            visited.put(Hash.hashBlock(x, y, z), changeBlocksPresent);
                            lightPropagationQueue.add(Hash.hashBlock(x, y, z));
                        }
                    }
                }
            }
        }

        while (!lightRemovalQueue.isEmpty()) {
            Object[] val = lightRemovalQueue.poll();
            long node = (long) val[0];
            int x = Hash.hashBlockX(node);
            int y = Hash.hashBlockY(node);
            int z = Hash.hashBlockZ(node);

            int lightLevel = (int) val[1];

            this.computeRemoveBlockLight(x - 1, y, z, lightLevel, lightRemovalQueue, lightPropagationQueue,
                    removalVisited, visited);
            this.computeRemoveBlockLight(x + 1, y, z, lightLevel, lightRemovalQueue, lightPropagationQueue,
                    removalVisited, visited);
            this.computeRemoveBlockLight(x, y - 1, z, lightLevel, lightRemovalQueue, lightPropagationQueue,
                    removalVisited, visited);
            this.computeRemoveBlockLight(x, y + 1, z, lightLevel, lightRemovalQueue, lightPropagationQueue,
                    removalVisited, visited);
            this.computeRemoveBlockLight(x, y, z - 1, lightLevel, lightRemovalQueue, lightPropagationQueue,
                    removalVisited, visited);
            this.computeRemoveBlockLight(x, y, z + 1, lightLevel, lightRemovalQueue, lightPropagationQueue,
                    removalVisited, visited);
        }

        while (!lightPropagationQueue.isEmpty()) {
            long node = lightPropagationQueue.poll();

            int x = Hash.hashBlockX(node);
            int y = Hash.hashBlockY(node);
            int z = Hash.hashBlockZ(node);
            int lightLevel = this.getBlockLightAt(x, y, z) - getBlock(x, y, z).getLightFilter();

            if (lightLevel >= 1) {
                this.computeSpreadBlockLight(x - 1, y, z, lightLevel, lightPropagationQueue, visited);
                this.computeSpreadBlockLight(x + 1, y, z, lightLevel, lightPropagationQueue, visited);
                this.computeSpreadBlockLight(x, y - 1, z, lightLevel, lightPropagationQueue, visited);
                this.computeSpreadBlockLight(x, y + 1, z, lightLevel, lightPropagationQueue, visited);
                this.computeSpreadBlockLight(x, y, z - 1, lightLevel, lightPropagationQueue, visited);
                this.computeSpreadBlockLight(x, y, z + 1, lightLevel, lightPropagationQueue, visited);
            }
        }
    }

    private void computeRemoveBlockLight(int x, int y, int z, int currentLight, Queue<Object[]> queue,
                                         Queue<Long> spreadQueue, Map<Long, Object> visited, Map<Long, Object> spreadVisited) {
        int current = this.getBlockLightAt(x, y, z);
        long index = Hash.hashBlock(x, y, z);
        if (current != 0 && current < currentLight) {
            this.setBlockLightAt(x, y, z, 0);
            if (current > 1) {
                if (!visited.containsKey(index)) {
                    visited.put(index, changeBlocksPresent);
                    queue.add(new Object[]{Hash.hashBlock(x, y, z), current});
                }
            }
        } else if (current >= currentLight) {
            if (!spreadVisited.containsKey(index)) {
                spreadVisited.put(index, changeBlocksPresent);
                spreadQueue.add(Hash.hashBlock(x, y, z));
            }
        }
    }

    private void computeSpreadBlockLight(int x, int y, int z, int currentLight, Queue<Long> queue,
                                         Map<Long, Object> visited) {
        int current = this.getBlockLightAt(x, y, z);
        long index = Hash.hashBlock(x, y, z);

        if (current < currentLight - 1) {
            this.setBlockLightAt(x, y, z, currentLight);

            if (!visited.containsKey(index)) {
                visited.put(index, changeBlocksPresent);
                if (currentLight > 1) {
                    queue.add(Hash.hashBlock(x, y, z));
                }
            }
        }
    }

    public void addLightUpdate(int x, int y, int z) {
        long index = chunkHash(x >> 4, z >> 4);
        var currentMap = lightQueue.get(index);
        if (currentMap == null) {
            currentMap = new ConcurrentHashMap<>(8, 0.9f, 1);
            this.lightQueue.put(index, currentMap);
        }
        currentMap.put(Level.localBlockHash(x, y, z, this), changeBlocksPresent);
    }

    public boolean setBlock(Vector3 pos, Block block) {
        return setBlock(pos, 0, block);
    }

    public boolean setBlock(Vector3 pos, int layer, Block block) {
        return this.setBlock(pos, layer, block, false);
    }

    public boolean setBlock(Vector3 pos, Block block, boolean direct) {
        return this.setBlock(pos, 0, block, direct);
    }

    public boolean setBlock(Vector3 pos, int layer, Block block, boolean direct) {
        return this.setBlock(pos, layer, block, direct, true);
    }

    public boolean setBlock(Vector3 pos, Block block, boolean direct, boolean update) {
        return setBlock(pos, 0, block, direct, update);
    }

    public boolean setBlock(Vector3 pos, int layer, Block block, boolean direct, boolean update) {
        return setBlock(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), layer, block, direct, update);
    }

    public boolean setBlock(int x, int y, int z, Block block, boolean direct, boolean update) {
        return setBlock(x, y, z, 0, block, direct, update);
    }

    /**
     * 设置一个方块
     *
     * @param x      方块的x坐标
     * @param y      方块的y坐标
     * @param z      方块的z坐标
     * @param layer  设置的方块层级。例如含水方块位置上的layer1对应的方块为水
     * @param block  方块
     * @param direct 是否立即同步方块变更到客户端。
     * @param update 是否进行方块更新
     * @return 是否设置成功
     */
    public boolean setBlock(int x, int y, int z, int layer, Block block, boolean direct, boolean update) {
        if (!isYInRange(y) || layer < 0 || layer > this.requireProvider().getMaximumLayer()) {
            return false;
        }
        BlockState state = block.getBlockState();
        IChunk chunk = this.getChunk(x >> 4, z >> 4, true);
        BlockState statePrevious = chunk.getAndSetBlockState(x & 0xF, y, z & 0xF, state, layer);
        if (state == statePrevious) {
            return false;
        }
        block.x = x;
        block.y = y;
        block.z = z;
        block.level = this;
        block.layer = layer;

        Block blockPrevious = statePrevious.toBlock();
        blockPrevious.x = x;
        blockPrevious.y = y;
        blockPrevious.z = z;
        blockPrevious.level = this;
        blockPrevious.layer = layer;

        int cx = x >> 4;
        int cz = z >> 4;
        long index = Level.chunkHash(cx, cz);
        if (direct) {
            this.sendBlocks(this.getChunkPlayers(cx, cz).values().toArray(Player.EMPTY_ARRAY), new Block[]{block}, UpdateBlockPacket.FLAG_ALL_PRIORITY, block.layer);
        } else {
            addBlockChange(index, x, y, z);
        }
        for (ChunkLoader loader : this.getChunkLoaders(cx, cz)) {
            loader.onBlockChanged(block);
        }
        if (update) {
            updateAllLight(block);
            BlockUpdateEvent ev = new BlockUpdateEvent(block);
            this.server.getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                for (Entity entity : this.getNearbyEntities(new SimpleAxisAlignedBB(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1))) {
                    entity.scheduleUpdate();
                }
                block = ev.getBlock();
                block.onUpdate(BLOCK_UPDATE_NORMAL);
                block.getLevelBlockAtLayer(layer == 0 ? 1 : 0).onUpdate(BLOCK_UPDATE_NORMAL);
                this.updateAround(x, y, z);

                if (block.hasComparatorInputOverride()) {
                    this.updateComparatorOutputLevel(block);
                }
            }
        }
        blockPrevious.afterRemoval(block, update);
        return true;
    }

    private void addBlockChange(int x, int y, int z) {
        long index = Level.chunkHash(x >> 4, z >> 4);
        addBlockChange(index, x, y, z);
    }

    private void addBlockChange(long index, int x, int y, int z) {
        synchronized (changedBlocks) {
            SoftReference<Int2ObjectOpenHashMap<Object>> current = changedBlocks.computeIfAbsent(index, k -> new SoftReference<>(new Int2ObjectOpenHashMap<>()));
            var currentMap = current.get();
            if (currentMap != changeBlocksFullMap && currentMap != null) {
                if (currentMap.size() > MAX_BLOCK_CACHE) {
                    this.changedBlocks.put(index, new SoftReference<>(changeBlocksFullMap));
                } else {
                    currentMap.put(Level.localBlockHash(x, y, z, this), changeBlocksPresent);
                }
            }
        }
    }

    public void dropItem(Vector3 source, Item item) {
        this.dropItem(source, item, null);
    }

    public void dropItem(Vector3 source, Item item, Vector3 motion) {
        this.dropItem(source, item, motion, 10);
    }

    public void dropItem(Vector3 source, Item item, Vector3 motion, int delay) {
        this.dropItem(source, item, motion, false, delay);
    }

    public void dropItem(Vector3 source, Item item, Vector3 motion, boolean dropAround, int delay) {
        if (motion == null) {
            if (dropAround) {
                float f = ThreadLocalRandom.current().nextFloat() * 0.5f;
                float f1 = ThreadLocalRandom.current().nextFloat() * ((float) Math.PI * 2);

                motion = new Vector3(-MathHelper.sin(f1) * f, 0.20000000298023224, MathHelper.cos(f1) * f);
            } else {
                motion = new Vector3(new java.util.Random().nextDouble() * 0.2 - 0.1, 0.2,
                        new java.util.Random().nextDouble() * 0.2 - 0.1);
            }
        }

        if (item.isNull()) {
            return;
        }
        EntityItem itemEntity = (EntityItem) Entity.createEntity(Entity.ITEM,
                this.getChunk((int) source.getX() >> 4, (int) source.getZ() >> 4, true),
                Entity.getDefaultNBT(source, motion, new Random().nextFloat() * 360, 0)
                        .putShort("Health", 5)
                        .putCompound("Item", NBTIO.putItemHelper(item))
                        .putShort("PickupDelay", delay));

        if (itemEntity != null) {
            itemEntity.spawnToAll();
        }
    }

    public @Nullable EntityItem dropAndGetItem(@NotNull Vector3 source, @NotNull Item item) {
        return this.dropAndGetItem(source, item, null);
    }

    public @Nullable EntityItem dropAndGetItem(@NotNull Vector3 source, @NotNull Item item, @Nullable Vector3 motion) {
        return this.dropAndGetItem(source, item, motion, 10);
    }

    public @Nullable EntityItem dropAndGetItem(@NotNull Vector3 source, @NotNull Item item, @Nullable Vector3 motion, int delay) {
        return this.dropAndGetItem(source, item, motion, false, delay);
    }

    public @Nullable EntityItem dropAndGetItem(@NotNull Vector3 source, @NotNull Item item, @Nullable Vector3 motion, boolean dropAround, int delay) {
        if (item.isNull()) {
            return null;
        }
        if (motion == null) {
            if (dropAround) {
                float f = ThreadLocalRandom.current().nextFloat() * 0.5f;
                float f1 = ThreadLocalRandom.current().nextFloat() * ((float) Math.PI * 2);

                motion = new Vector3(-MathHelper.sin(f1) * f, 0.20000000298023224, MathHelper.cos(f1) * f);
            } else {
                motion = new Vector3(new java.util.Random().nextDouble() * 0.2 - 0.1, 0.2,
                        new java.util.Random().nextDouble() * 0.2 - 0.1);
            }
        }

        CompoundTag itemTag = NBTIO.putItemHelper(item);

        EntityItem itemEntity = (EntityItem) Entity.createEntity(Entity.ITEM,
                this.getChunk((int) source.getX() >> 4, (int) source.getZ() >> 4, true),
                new CompoundTag().putList("Pos", new ListTag<DoubleTag>().add(new DoubleTag(source.getX()))
                                .add(new DoubleTag(source.getY())).add(new DoubleTag(source.getZ())))

                        .putList("Motion", new ListTag<DoubleTag>().add(new DoubleTag(motion.x))
                                .add(new DoubleTag(motion.y)).add(new DoubleTag(motion.z)))

                        .putList("Rotation", new ListTag<FloatTag>()
                                .add(new FloatTag(ThreadLocalRandom.current().nextFloat() * 360))
                                .add(new FloatTag(0)))

                        .putShort("Health", 5).putCompound("Item", itemTag).putShort("PickupDelay", delay));

        if (itemEntity != null) {
            itemEntity.spawnToAll();
        }

        return itemEntity;
    }

    public Item useBreakOn(@NotNull Vector3 vector) {
        return this.useBreakOn(vector, null);
    }

    public Item useBreakOn(@NotNull Vector3 vector, @Nullable Item item) {
        return this.useBreakOn(vector, item, null);
    }

    public Item useBreakOn(@NotNull Vector3 vector, @Nullable Item item, @Nullable Player player) {
        return this.useBreakOn(vector, item, player, false);
    }

    public Item useBreakOn(@NotNull Vector3 vector, @Nullable Item item, @Nullable Player player, boolean createParticles) {
        return useBreakOn(vector, null, item, player, createParticles);
    }

    public Item useBreakOn(@NotNull Vector3 vector, @Nullable BlockFace face, @Nullable Item item, @Nullable Player player, boolean createParticles) {
        return useBreakOn(vector, face, item, player, createParticles, false);
    }

    public Item useBreakOn(@NotNull Vector3 vector, @Nullable BlockFace face, @Nullable Item item, @Nullable Player player, boolean createParticles, boolean immediateDestroy) {
        if (vector instanceof Block b) {
            return useBreakOn(b, b.layer, face, item, player, createParticles, immediateDestroy);
        } else {
            return useBreakOn(vector, 0, face, item, player, createParticles, immediateDestroy);
        }
    }

    public Item useBreakOn(@NotNull Vector3 vector, int layer, @Nullable BlockFace face, @Nullable Item item, @Nullable Player player, boolean createParticles, boolean immediateDestroy) {
        if (player != null && player.getGamemode() > 2) {
            return null;
        }

        Block target = this.getBlock(vector, layer);

        if (player != null && !target.isBlockChangeAllowed(player)) {
            return null;
        }

        Item[] drops;
        int dropExp = target.getDropExp();

        if (item == null) {
            item = Item.AIR;
        }

        if (!target.isBreakable(vector, layer, face, item, player)) {
            return null;
        }

        boolean isSilkTouch = target.isSilkTouch(vector, layer, face, item, player) ||
                (item.getEnchantment(Enchantment.ID_SILK_TOUCH) != null && item.applyEnchantments());

        if (player != null) {
            if (player.getGamemode() == 2) {
                Tag tag = item.getNamedTagEntry("CanDestroy");
                boolean canBreak = false;
                if (tag instanceof ListTag) {
                    for (Tag v : ((ListTag<? extends Tag>) tag).getAll()) {
                        if (!(v instanceof StringTag stringTag)) {
                            continue;
                        }
                        Item entry = Item.get(stringTag.data);
                        if (!entry.isNull() &&
                                entry.getBlock().getId().equals(target.getId())) {
                            canBreak = true;
                            break;
                        }
                    }
                }
                if (!canBreak) {
                    return null;
                }
            }

            Item[] eventDrops;
            if (immediateDestroy || player.isCreative()) {
                eventDrops = Item.EMPTY_ARRAY;
            } else if (isSilkTouch && target.canSilkTouch()) {
                eventDrops = new Item[]{target.toItem()};
            } else {
                eventDrops = target.getDrops(item);
            }

            if (immediateDestroy) {
                drops = eventDrops;
            } else {
                if (!player.getAdventureSettings().get(PlayerAbility.MINE))
                    return null;

                //使用calculateBreakTimeNotInAir目的是获取玩家在陆地上的挖掘时间，如果挖掘时间小于这个时间才认为玩家作弊。
                double breakTime = target.calculateBreakTimeNotInAir(item, player);
                //对于自定义方块，由于用户可以自由设置客户端侧的挖掘时间，拿服务端硬度计算出来的挖掘时间来判断是否为fastBreak是不准确的。
                if (target instanceof CustomBlock customBlock) {
                    var comp = customBlock.getDefinition().nbt().getCompound("components");
                    if (comp.containsCompound("minecraft:destructible_by_mining")) {
                        var clientBreakTime = comp.getCompound("minecraft:destructible_by_mining").getFloat("value");
                        breakTime = Math.min(breakTime, clientBreakTime);
                    }
                }
                if (player.isCreative() && breakTime > 0.15) {
                    breakTime = 0.15;
                }
                breakTime -= 0.15;
                //thisBreak-lastBreak < breakTime-1000ms = the player is hacker (fastBreak)
                boolean fastBreak = Long.sum(player.lastBreak, (long) breakTime * 1000) > Long.sum(System.currentTimeMillis(), 1000);
                BlockBreakEvent ev = new BlockBreakEvent(player, target, face, item, eventDrops, player.isCreative(), fastBreak);
                if (!player.isOp() && isInSpawnRadius(target)) {
                    ev.setCancelled();
                } else if (!ev.getInstaBreak() && ev.isFastBreak()) {
                    ev.setCancelled();
                }

                this.server.getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    return null;
                }

                if (!ev.getInstaBreak() && ev.isFastBreak()) {
                    return null;
                }

                player.lastBreak = System.currentTimeMillis();

                drops = ev.getDrops();
                dropExp = ev.getDropExp();
            }
        } else if (isSilkTouch) {
            drops = new Item[]{target.toItem()};
        } else {
            drops = target.getDrops(item);
        }

        Block above = this.getBlock(new Vector3(target.x, target.y + 1, target.z), 0);
        if (above != null) {
            if (above.getId().equals(BlockID.FIRE)) {
                this.setBlock(above, Block.get(BlockID.AIR), true);
            }
        }

        if (createParticles) {
            Map<Integer, Player> players = this.getChunkPlayers((int) target.x >> 4, (int) target.z >> 4);
            if (player != null && immediateDestroy) {
                players.remove(player.getLoaderId());
            }
            this.addParticle(new DestroyBlockParticle(target.add(0.5), target), players.values());
        }

        // Close BlockEntity before we check onBreak
        if (layer == 0) {
            BlockEntity blockEntity = this.getBlockEntity(target);
            if (blockEntity != null) {
                blockEntity.onBreak(isSilkTouch);
                blockEntity.close();

                this.updateComparatorOutputLevel(target);
            }
        }

        target.onBreak(item);

        this.getVibrationManager().callVibrationEvent(new VibrationEvent(player, target.add(0.5, 0.5, 0.5), VibrationType.BLOCK_DESTROY));

        item.useOn(target);
        if (item.isTool() && item.getDamage() >= item.getMaxDurability()) {
            if (player != null) {
                addSound(player, Sound.RANDOM_BREAK);
            }
            item = Item.AIR;
        }

        if (this.gameRules.getBoolean(GameRule.DO_TILE_DROPS)) {

            if (!isSilkTouch && (player != null && ((player.isSurvival() || player.isAdventure()) || immediateDestroy)) && dropExp > 0 && drops.length != 0) {
                this.dropExpOrb(vector.add(0.5, 0.5, 0.5), dropExp);
            }

            for (Item drop : drops) {
                if (drop.getCount() > 0) {
                    this.dropItem(vector.add(0.5, 0.5, 0.5), drop);
                }
            }
        }
        return item;
    }

    public void dropExpOrb(Vector3 source, int exp) {
        dropExpOrb(source, exp, null);
    }

    public void dropExpOrb(Vector3 source, int exp, Vector3 motion) {
        dropExpOrb(source, exp, motion, 10);
    }

    public void dropExpOrb(Vector3 source, int exp, Vector3 motion, int delay) {
        dropExpOrbAndGetEntities(source, exp, motion, delay);
    }

    public List<EntityXpOrb> dropExpOrbAndGetEntities(Vector3 source, int exp) {
        return dropExpOrbAndGetEntities(source, exp, null);
    }

    public List<EntityXpOrb> dropExpOrbAndGetEntities(Vector3 source, int exp, Vector3 motion) {
        return dropExpOrbAndGetEntities(source, exp, motion, 10);
    }

    public List<EntityXpOrb> dropExpOrbAndGetEntities(Vector3 source, int exp, Vector3 motion, int delay) {
        Random rand = ThreadLocalRandom.current();
        List<Integer> drops = EntityXpOrb.splitIntoOrbSizes(exp);
        List<EntityXpOrb> entities = new ArrayList<>(drops.size());
        for (int split : drops) {
            CompoundTag nbt = Entity.getDefaultNBT(source, motion == null ? new Vector3(
                            (rand.nextDouble() * 0.2 - 0.1) * 2,
                            rand.nextDouble() * 0.4,
                            (rand.nextDouble() * 0.2 - 0.1) * 2) : motion,
                    rand.nextFloat() * 360f, 0);

            nbt.putShort("Value", split);
            nbt.putShort("PickupDelay", delay);

            EntityXpOrb entity = (EntityXpOrb) Entity.createEntity(Entity.XP_ORB, this.getChunk(source.getChunkX(), source.getChunkZ()), nbt);
            if (entity != null) {
                entities.add(entity);
                entity.spawnToAll();
            }
        }
        return entities;
    }

    public Item useItemOn(Vector3 vector, Item item, BlockFace face, float fx, float fy, float fz) {
        return this.useItemOn(vector, item, face, fx, fy, fz, null);
    }

    public Item useItemOn(Vector3 vector, Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        return this.useItemOn(vector, item, face, fx, fy, fz, player, true);
    }

    public Item useItemOn(Vector3 vector, Item item, BlockFace face, float fx, float fy, float fz, Player player, boolean playSound) {
        Block target = this.getBlock(vector);
        Block block = target.getSide(face);

        if (item.getBlock() instanceof BlockScaffolding && face == BlockFace.UP && block.getId().equals(BlockID.SCAFFOLDING)) {
            while (block instanceof BlockScaffolding) {
                block = block.up();
            }
        }
        //handle height limit
        if (!isYInRange((int) block.y)) {
            return null;
        }
        //handle height limit in nether
        if (block.y > 127 && this.getDimension() == DIMENSION_NETHER) {
            return null;
        }

        if (target.isAir()) {
            return null;
        }
        if (player != null) {
            PlayerInteractEvent ev = new PlayerInteractEvent(player, item, target, face, target.isAir() ? Action.RIGHT_CLICK_AIR : Action.RIGHT_CLICK_BLOCK);
            //                                handle spawn protect
            if (player.getGamemode() > 2 || (!player.isOp() && isInSpawnRadius(target))) {
                ev.setCancelled();
            }

            this.server.getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                target.onTouch(vector, item, face, fx, fy, fz, player, ev.getAction());
                if (ev.getAction() == Action.RIGHT_CLICK_BLOCK && target.canBeActivated() && target.onActivate(item, player, face, fx, fy, fz)) {
                    if (item.isTool() && item.getDamage() >= item.getMaxDurability()) {
                        addSound(player, Sound.RANDOM_BREAK);
                        item = Item.AIR;
                    }
                    return item;
                }

                if (item.canBeActivated() && item.onActivate(this, player, block, target, face, fx, fy, fz)) {
                    if (item.getCount() <= 0) {
                        item = Item.AIR;
                        return item;
                    }
                }
            } else {
                if ((item instanceof ItemBucket itemBucket) && itemBucket.isWater()) {
                    player.getLevel().sendBlocks(new Player[]{player}, new Block[]{Block.get(BlockID.AIR, target)}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 1);
                }
                return null;
            }

            if ((item instanceof ItemBucket itemBucket) && itemBucket.isWater()) {
                player.getLevel().sendBlocks(new Player[]{player}, new Block[]{target.getLevelBlockAtLayer(1)}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 1);
            }
        } else if (!target.isAir() && target.canBeActivated() && target.onActivate(item, null, face, fx, fy, fz)) {
            if (item.isTool() && item.getDamage() >= item.getMaxDurability()) {
                item = Item.AIR;
            }
            return item;
        }

        return placeBlock(item, face, fx, fy, fz, player, playSound, block, target);
    }

    @Nullable
    private Item placeBlock(Item item, BlockFace face, float fx, float fy, float fz, Player player, boolean playSound, Block block, Block target) {
        Block hand;
        if (item.canBePlaced()) {
            hand = item.getBlock();
            hand.position(block);
        } else {
            return null;
        }

        if (!(block.canBeReplaced() || (hand instanceof BlockSlab && hand.getId().equals(block.getId())))) {
            return null;
        }

        //处理放置梯子,我们应该提前给hand设置方向,这样后面计算是否碰撞实体才准确
        if (hand instanceof BlockLadder) {
            if (target instanceof BlockLadder) {
                hand.setPropertyValue(CommonBlockProperties.FACING_DIRECTION, face.getOpposite().getIndex());
            } else hand.setPropertyValue(CommonBlockProperties.FACING_DIRECTION, face.getIndex());
        }

        //cause bug (eg: frog_spawn) (and I don't know what this is for)
        if (!(hand instanceof BlockFrogSpawn) && target.canBeReplaced()) {
            block = target;
            hand.position(block);
        }

        if (!hand.canPassThrough() && hand.getBoundingBox() != null) {
            int realCount = 0;
            Entity[] entities = this.getCollidingEntities(hand.getBoundingBox());
            for (Entity e : entities) {
                if (e instanceof EntityProjectile || e instanceof EntityItem || e instanceof EntityXpOrb ||
                        e instanceof EntityFireworksRocket || e instanceof EntityPainting || e == player ||
                        (e instanceof Player p && p.isSpectator())) {
                    continue;
                }
                ++realCount;
            }
            if (player != null) {
                var diff = player.getNextPosition().subtract(player.getPosition());
                var aabb = player.getBoundingBox().getOffsetBoundingBox(diff.x, diff.y, diff.z);
                if (aabb.intersectsWith(hand.getBoundingBox().shrink(0.02, 0.02, 0.02))) {
                    ++realCount;
                }
            }
            if (realCount > 0) {
                // Entity in block
                return null;
            }
        }

        if (player != null) {
            if (!player.getAdventureSettings().get(PlayerAbility.BUILD) || !block.isBlockChangeAllowed(player))
                return null;

            BlockPlaceEvent event = new BlockPlaceEvent(player, hand, block, target, item);
            if (player.getGamemode() == Player.ADVENTURE) {
                Tag tag = item.getNamedTagEntry("CanPlaceOn");
                boolean canPlace = false;
                if (tag instanceof ListTag) {
                    for (Tag v : ((ListTag<Tag>) tag).getAll()) {
                        if (!(v instanceof StringTag stringTag)) {
                            continue;
                        }
                        Item entry = Item.get(stringTag.data);
                        if (!entry.isNull() && entry.getBlock().getId().equals(target.getId())) {
                            canPlace = true;
                            break;
                        }
                    }
                }
                if (!canPlace) {
                    event.setCancelled();
                }
            }
            if (!player.isOp() && isInSpawnRadius(target)) {
                event.setCancelled();
            }

            this.server.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return null;
            }
        }

        if (hand.getWaterloggingLevel() == 0 && hand.canBeFlowedInto() && (block instanceof BlockLiquid || block.getLevelBlockAtLayer(1) instanceof BlockLiquid)) {
            return null;
        }

        if ((block instanceof BlockLiquid) && ((BlockLiquid) block).usesWaterLogging()) {
            this.setBlock(block, 1, block, false, false);
            this.setBlock(block, 0, Block.get(BlockID.AIR), false, false);
            this.scheduleUpdate(block, 1);
        }

        if (!hand.place(item, block, target, face, fx, fy, fz, player)) {
            this.setBlock(block, 0, block, true, false);
            this.setBlock(block, 1, Block.get(BlockID.AIR), true, false);
            return null;
        }

        if (player != null) {
            if (!player.isCreative()) {
                item.setCount(item.getCount() - 1);
            }
        }

        if (playSound) {
            this.addLevelSoundEvent(hand, LevelSoundEventPacket.SOUND_PLACE, hand.getRuntimeId());
        }

        if (item.getCount() <= 0) {
            item = Item.AIR;
        }

        this.getVibrationManager().callVibrationEvent(new VibrationEvent(player, block.add(0.5, 0.5, 0.5), VibrationType.BLOCK_PLACE));
        return item;
    }

    public boolean isInSpawnRadius(Vector3 vector3) {
        int distance = this.server.getSpawnRadius();
        if (distance > -1) {
            Vector2 t = new Vector2(vector3.x, vector3.z);
            Vector2 s = new Vector2(this.getSpawnLocation().x, this.getSpawnLocation().z);
            return t.distance(s) <= distance;
        }
        return false;
    }

    public Entity getEntity(long entityId) {
        return this.entities.containsKey(entityId) ? this.entities.get(entityId) : null;
    }

    public Entity[] getEntities() {
        return entities.values().toArray(Entity.EMPTY_ARRAY);
    }

    public Entity[] getCollidingEntities(AxisAlignedBB bb) {
        return this.getCollidingEntities(bb, null);
    }

    public Entity[] getCollidingEntities(AxisAlignedBB bb, Entity entity) {
        int index = 0;

        ArrayList<Entity> overflow = null;

        if (entity == null || entity.canCollide()) {
            int minX = NukkitMath.floorDouble((bb.getMinX() - 2) / 16);
            int maxX = NukkitMath.ceilDouble((bb.getMaxX() + 2) / 16);
            int minZ = NukkitMath.floorDouble((bb.getMinZ() - 2) / 16);
            int maxZ = NukkitMath.ceilDouble((bb.getMaxZ() + 2) / 16);

            for (int x = minX; x <= maxX; ++x) {
                for (int z = minZ; z <= maxZ; ++z) {
                    for (Entity ent : this.getChunkEntities(x, z, false).values()) {
                        if ((entity == null || (ent != entity && entity.canCollideWith(ent)))
                                && ent.boundingBox.intersectsWith(bb)) {
                            overflow = addEntityToBuffer(index, overflow, ent);
                            index++;
                        }
                    }
                }
            }
        }

        return getEntitiesFromBuffer(index, overflow);
    }

    public List<Entity> fastCollidingEntities(AxisAlignedBB bb) {
        return this.fastCollidingEntities(bb, null);
    }

    public List<Entity> fastCollidingEntities(AxisAlignedBB bb, Entity entity) {
        var result = new ArrayList<Entity>();

        if (entity == null || entity.canCollide()) {
            int minX = NukkitMath.floorDouble((bb.getMinX() - 2) / 16);
            int maxX = NukkitMath.ceilDouble((bb.getMaxX() + 2) / 16);
            int minZ = NukkitMath.floorDouble((bb.getMinZ() - 2) / 16);
            int maxZ = NukkitMath.ceilDouble((bb.getMaxZ() + 2) / 16);

            for (int x = minX; x <= maxX; ++x) {
                for (int z = minZ; z <= maxZ; ++z) {
                    for (var each : this.getChunkEntities(x, z, false).values()) {
                        if ((entity == null || (each != entity && entity.canCollideWith(each)))
                                && each.boundingBox.intersectsWith(bb)) {
                            result.add(each);
                        }
                    }
                }
            }
        }

        return result;
    }

    public Stream<Entity> streamCollidingEntities(AxisAlignedBB bb, Entity entity) {
        if (entity == null || entity.canCollide()) {
            int minX = NukkitMath.floorDouble((bb.getMinX() - 2) / 16);
            int maxX = NukkitMath.ceilDouble((bb.getMaxX() + 2) / 16);
            int minZ = NukkitMath.floorDouble((bb.getMinZ() - 2) / 16);
            int maxZ = NukkitMath.ceilDouble((bb.getMaxZ() + 2) / 16);

            var allEntities = new ArrayList<Entity>();

            for (int x = minX; x <= maxX; ++x) {
                for (int z = minZ; z <= maxZ; ++z) {
                    allEntities.addAll(this.getChunkEntities(x, z, false).values());
                }
            }

            return allEntities.stream().filter(each -> (entity == null || (each != entity && entity.canCollideWith(each)))
                    && each.boundingBox.intersectsWith(bb));
        } else {
            return Stream.empty();
        }
    }

    public Entity[] getNearbyEntities(AxisAlignedBB bb) {
        return this.getNearbyEntities(bb, null);
    }

    public Entity[] getNearbyEntities(AxisAlignedBB bb, Entity entity) {
        return getNearbyEntities(bb, entity, false);
    }

    public Entity[] getNearbyEntities(AxisAlignedBB bb, Entity entity, boolean loadChunks) {
        int index = 0;

        int minX = NukkitMath.floorDouble((bb.getMinX() - 2) * 0.0625);
        int maxX = NukkitMath.ceilDouble((bb.getMaxX() + 2) * 0.0625);
        int minZ = NukkitMath.floorDouble((bb.getMinZ() - 2) * 0.0625);
        int maxZ = NukkitMath.ceilDouble((bb.getMaxZ() + 2) * 0.0625);

        ArrayList<Entity> overflow = null;

        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                for (Entity ent : this.getChunkEntities(x, z, loadChunks).values()) {
                    if (ent != entity && ent.boundingBox.intersectsWith(bb)) {
                        overflow = addEntityToBuffer(index, overflow, ent);
                        index++;
                    }
                }
            }
        }

        return getEntitiesFromBuffer(index, overflow);
    }

    public List<Entity> fastNearbyEntities(AxisAlignedBB bb) {
        return this.fastNearbyEntities(bb, null);
    }

    public List<Entity> fastNearbyEntities(AxisAlignedBB bb, Entity entity) {
        return fastNearbyEntities(bb, entity, false);
    }

    public List<Entity> fastNearbyEntities(AxisAlignedBB bb, Entity entity, boolean loadChunks) {
        int minX = NukkitMath.floorDouble((bb.getMinX() - 2) * 0.0625);
        int maxX = NukkitMath.ceilDouble((bb.getMaxX() + 2) * 0.0625);
        int minZ = NukkitMath.floorDouble((bb.getMinZ() - 2) * 0.0625);
        int maxZ = NukkitMath.ceilDouble((bb.getMaxZ() + 2) * 0.0625);

        var result = new ArrayList<Entity>();

        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                for (var ent : this.getChunkEntities(x, z, loadChunks).values()) {
                    if (ent != entity && ent.boundingBox.intersectsWith(bb)) {
                        result.add(ent);
                    }
                }
            }
        }

        return result;
    }

    private ArrayList<Entity> addEntityToBuffer(int index, ArrayList<Entity> overflow, Entity ent) {
        if (index < ENTITY_BUFFER.length) {
            ENTITY_BUFFER[index] = ent;
        } else {
            if (overflow == null) overflow = new ArrayList<>(1024);
            overflow.add(ent);
        }
        return overflow;
    }

    private Entity[] getEntitiesFromBuffer(int index, ArrayList<Entity> overflow) {
        if (index == 0) return Entity.EMPTY_ARRAY;
        Entity[] copy;
        if (overflow == null) {
            copy = Arrays.copyOfRange(ENTITY_BUFFER, 0, index);
            Arrays.fill(ENTITY_BUFFER, 0, index, null);
        } else {
            copy = new Entity[ENTITY_BUFFER.length + overflow.size()];
            System.arraycopy(ENTITY_BUFFER, 0, copy, 0, ENTITY_BUFFER.length);
            for (int i = 0; i < overflow.size(); i++) {
                copy[ENTITY_BUFFER.length + i] = overflow.get(i);
            }
        }
        return copy;
    }

    @NonComputationAtomic
    public Map<Long, BlockEntity> getBlockEntities() {
        return blockEntities;
    }

    public BlockEntity getBlockEntityById(long blockEntityId) {
        return this.blockEntities.containsKey(blockEntityId) ? this.blockEntities.get(blockEntityId) : null;
    }

    @NonComputationAtomic
    public Map<Long, Player> getPlayers() {
        return players;
    }

    public Map<Integer, ChunkLoader> getLoaders() {
        return loaders;
    }

    public BlockEntity getBlockEntity(Vector3 pos) {
        return getBlockEntity(pos.asBlockVector3());
    }

    public BlockEntity getBlockEntity(BlockVector3 pos) {
        IChunk chunk = this.getChunk(pos.x >> 4, pos.z >> 4, false);

        if (chunk != null) {
            return chunk.getTile(pos.x & 0x0f, ensureY(pos.y), pos.z & 0x0f);
        }

        return null;
    }

    public BlockEntity getBlockEntityIfLoaded(Vector3 pos) {
        IChunk chunk = this.getChunkIfLoaded((int) pos.x >> 4, (int) pos.z >> 4);

        if (chunk != null) {
            return chunk.getTile((int) pos.x & 0x0f, ensureY((int) pos.y), (int) pos.z & 0x0f);
        }

        return null;
    }

    public Map<Long, Entity> getChunkEntities(int X, int Z) {
        return getChunkEntities(X, Z, true);
    }

    public Map<Long, Entity> getChunkEntities(int X, int Z, boolean loadChunks) {
        IChunk chunk = loadChunks ? this.getChunk(X, Z) : this.getChunkIfLoaded(X, Z);
        return chunk != null ? chunk.getEntities() : Collections.emptyMap();
    }

    public Map<Long, BlockEntity> getChunkBlockEntities(int X, int Z) {
        IChunk chunk;
        return (chunk = this.getChunk(X, Z)) != null ? chunk.getBlockEntities() : Collections.emptyMap();
    }

    public void setBlockStateAt(int x, int y, int z, BlockState state) {
        setBlockStateAt(x, y, z, 0, state);
    }

    public void setBlockStateAt(int x, int y, int z, int layer, BlockState state) {
        IChunk chunk = this.getChunk(x >> 4, z >> 4, true);
        chunk.setBlockState(x & 0x0f, ensureY(y), z & 0x0f, state, layer);
        addBlockChange(x, y, z);
        temporalVector.setComponents(x, y, z);
        for (ChunkLoader loader : this.getChunkLoaders(x >> 4, z >> 4)) {
            loader.onBlockChanged(temporalVector);
        }
    }

    public BlockState getBlockStateAt(int x, int y, int z, int layer) {
        return this.getChunk(x >> 4, z >> 4, true).getBlockState(x & 0x0f, ensureY(y), z & 0x0f, layer);
    }

    public BlockState getBlockStateAt(int x, int y, int z) {
        return getBlockStateAt(x, y, z, 0);
    }

    public synchronized int getBlockSkyLightAt(int x, int y, int z) {
        return this.getChunk(x >> 4, z >> 4, true).getBlockSkyLight(x & 0x0f, ensureY(y), z & 0x0f);
    }

    public synchronized void setBlockSkyLightAt(int x, int y, int z, int level) {
        this.getChunk(x >> 4, z >> 4, true).setBlockSkyLight(x & 0x0f, ensureY(y), z & 0x0f, level & 0x0f);
    }

    public synchronized int getBlockLightAt(int x, int y, int z) {
        return this.getChunk(x >> 4, z >> 4, true).getBlockLight(x & 0x0f, ensureY(y), z & 0x0f);
    }

    public synchronized void setBlockLightAt(int x, int y, int z, int level) {
        this.getChunk(x >> 4, z >> 4, true).setBlockLight(x & 0x0f, ensureY(y), z & 0x0f, level & 0x0f);
    }

    public int getBiomeId(int x, int y, int z) {
        return this.getChunk(x >> 4, z >> 4, true).getBiomeId(x & 0x0f, y, z & 0x0f);
    }

    public void setBiomeId(int x, int y, int z, byte biomeId) {
        this.getChunk(x >> 4, z >> 4, true).setBiomeId(x & 0x0f, y, z & 0x0f, biomeId);
    }

    public int getHeightMap(int x, int z) {
        return this.getChunk(x >> 4, z >> 4, true).getHeightMap(x & 0x0f, z & 0x0f);
    }

    public void setHeightMap(int x, int z, int value) {
        this.getChunk(x >> 4, z >> 4, true).setHeightMap(x & 0x0f, z & 0x0f, value & 0x0f);
    }

    public Map<Long, IChunk> getChunks() {
        return requireProvider().getLoadedChunks();
    }

    public IChunk getChunkIfLoaded(int chunkX, int chunkZ) {
        long index = Level.chunkHash(chunkX, chunkZ);
        return this.requireProvider().getLoadedChunk(index);
    }

    public void setChunk(int chunkX, int chunkZ) {
        this.setChunk(chunkX, chunkZ, null);
    }

    public void setChunk(int chunkX, int chunkZ, IChunk chunk) {
        this.setChunk(chunkX, chunkZ, chunk, true);
    }

    /**
     * Set chunk to the level provider
     *
     * @param unload Whether to unload old chunk on the current chunk pos
     */
    public void setChunk(int chunkX, int chunkZ, IChunk chunk, boolean unload) {
        if (chunk == null) {
            return;
        }

        long index = Level.chunkHash(chunkX, chunkZ);
        IChunk oldChunk = this.getChunk(chunkX, chunkZ, false);

        if (oldChunk != chunk) {
            if (unload && oldChunk != null) {
                this.unloadChunk(chunkX, chunkZ, false, false);

                this.requireProvider().setChunk(chunkX, chunkZ, chunk);
            } else {
                Map<Long, Entity> oldEntities = oldChunk != null ? oldChunk.getEntities() : Collections.emptyMap();

                Map<Long, BlockEntity> oldBlockEntities = oldChunk != null ? oldChunk.getBlockEntities() : Collections.emptyMap();

                if (!oldEntities.isEmpty()) {
                    Iterator<Map.Entry<Long, Entity>> iter = oldEntities.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry<Long, Entity> entry = iter.next();
                        Entity entity = entry.getValue();
                        chunk.addEntity(entity);
                        iter.remove();
                        oldChunk.removeEntity(entity);
                        entity.chunk = chunk;
                    }
                }

                if (!oldBlockEntities.isEmpty()) {
                    Iterator<Map.Entry<Long, BlockEntity>> iter = oldBlockEntities.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry<Long, BlockEntity> entry = iter.next();
                        BlockEntity blockEntity = entry.getValue();
                        chunk.addBlockEntity(blockEntity);
                        iter.remove();
                        oldChunk.removeBlockEntity(blockEntity);
                        blockEntity.chunk = chunk;
                    }
                }

                this.requireProvider().setChunk(chunkX, chunkZ, chunk);
            }
        }

        chunk.setChanged();

        if (!this.isChunkInUse(index)) {
            this.unloadChunkRequest(chunkX, chunkZ);
        } else {
            for (ChunkLoader loader : this.getChunkLoaders(chunkX, chunkZ)) {
                loader.onChunkChanged(chunk);
            }
        }
    }

    public int getHighestBlockAt(int x, int z) {
        return this.getChunk(x >> 4, z >> 4, true).getHighestBlockAt(x & 0x0f, z & 0x0f);
    }

    protected static final BlockColor VOID_BLOCK_COLOR = BlockColor.VOID_BLOCK_COLOR;
    protected static final BlockColor WATER_BLOCK_COLOR = BlockColor.WATER_BLOCK_COLOR;

    public BlockColor getMapColorAt(int x, int z) {
        var color = VOID_BLOCK_COLOR.toAwtColor();

        var block = getMapColoredBlockAt(x, z);
        if (block == null)
            return VOID_BLOCK_COLOR;

        //在z轴存在高度差的地方，颜色变深或变浅
        var nzy = getMapColoredBlockAt(x, z - 1);
        if (nzy == null)
            return block.getColor();
        color = block.getColor().toAwtColor();
        if (nzy.getFloorY() > block.getFloorY()) {
            color = darker(color, 0.875 - Math.min(5, nzy.getFloorY() - block.getFloorY()) * 0.05);
        } else if (nzy.getFloorY() < block.getFloorY()) {
            color = brighter(color, 0.875 - Math.min(5, block.getFloorY() - nzy.getFloorY()) * 0.05);
        }

        //效果不好，暂时禁用
//        var deltaY = block.y - 128;
//        if (deltaY > 0) {
//            color = brighter(color, 1 - deltaY / (192 * 3));
//        } else if (deltaY < 0) {
//            color = darker(color, 1 - (-deltaY) / (192 * 3));
//        }

        var up = block.getSide(BlockFace.UP);
        var up1 = block.getSideAtLayer(1, BlockFace.UP);
        if (up instanceof BlockFlowingWater || up1 instanceof BlockFlowingWater) {
            var r1 = color.getRed();
            var g1 = color.getGreen();
            var b1 = color.getBlue();
            //在水下
            if (block.y < 62) {
                //在海平面下
                //海平面为62格。离海平面越远颜色越接近海洋颜色
                var depth = 62 - block.y;
                if (depth > 96) return WATER_BLOCK_COLOR;
                b1 = WATER_BLOCK_COLOR.getBlue();
                var radio = (depth / 96.0);
                if (radio < 0.5) radio = 0.5;
                r1 += (WATER_BLOCK_COLOR.getRed() - r1) * radio;
                g1 += (WATER_BLOCK_COLOR.getGreen() - g1) * radio;
            } else {
                //湖泊 or 河流
                b1 = WATER_BLOCK_COLOR.getBlue();
                r1 += (WATER_BLOCK_COLOR.getRed() - r1) * 0.5;
                g1 += (WATER_BLOCK_COLOR.getGreen() - g1) * 0.5;
            }
            color = new Color(r1, g1, b1);
        }

        return new BlockColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    protected Color brighter(Color source, double factor) {
        int r = source.getRed();
        int g = source.getGreen();
        int b = source.getBlue();
        int alpha = source.getAlpha();

        int i = (int) (1.0 / (1.0 - factor));
        if (r == 0 && g == 0 && b == 0) {
            return new Color(i, i, i, alpha);
        }
        if (r > 0 && r < i) r = i;
        if (g > 0 && g < i) g = i;
        if (b > 0 && b < i) b = i;

        return new Color(Math.min((int) (r / factor), 255),
                Math.min((int) (g / factor), 255),
                Math.min((int) (b / factor), 255),
                alpha);
    }

    protected Color darker(Color source, double factor) {
        return new Color(Math.max((int) (source.getRed() * factor), 0),
                Math.max((int) (source.getGreen() * factor), 0),
                Math.max((int) (source.getBlue() * factor), 0),
                source.getAlpha());
    }

    protected Block getMapColoredBlockAt(int x, int z) {
        var chunk = getChunk(x >> 4, z >> 4);
        if (chunk == null) return null;
        var chunkX = x & 0xF;
        var chunkZ = z & 0xF;
        int y = chunk.getHighestBlockAt(chunkX, chunkZ, false);
        while (y > getMinHeight()) {
            Block block = getBlock(x, y, z);
            if (block.getColor() == null) return null;
            if (block.getColor().getAlpha() == 0/* || block instanceof BlockFlowingWater*/) {
                y--;
            } else {
                return block;
            }
        }
        return null;
    }

    public boolean isChunkLoaded(int x, int z) {
        return this.requireProvider().isChunkLoaded(x, z);
    }

    private boolean areNeighboringChunksLoaded(long hash) {
        LevelProvider levelProvider = this.requireProvider();
        return levelProvider.isChunkLoaded(hash + 1) &&
                levelProvider.isChunkLoaded(hash - 1) &&
                levelProvider.isChunkLoaded(hash + (1L << 32)) &&
                levelProvider.isChunkLoaded(hash - (1L << 32));
    }

    public boolean isChunkGenerated(int x, int z) {
        IChunk chunk = this.getChunk(x, z);
        return chunk != null && chunk.isGenerated();
    }

    public boolean isChunkPopulated(int x, int z) {
        IChunk chunk = this.getChunk(x, z);
        return chunk != null && chunk.isPopulated();
    }

    public Position getSpawnLocation() {
        return Position.fromObject(this.requireProvider().getSpawn(), this);
    }

    public void setSpawnLocation(Vector3 pos) {
        Position previousSpawn = this.getSpawnLocation();
        this.requireProvider().setSpawn(pos);
        this.server.getPluginManager().callEvent(new SpawnChangeEvent(this, previousSpawn));
        SetSpawnPositionPacket pk = new SetSpawnPositionPacket();
        pk.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN;
        pk.x = pos.getFloorX();
        pk.y = pos.getFloorY();
        pk.z = pos.getFloorZ();
        pk.dimension = getDimension();
        for (Player p : getPlayers().values()) {
            p.dataPacket(pk);
        }
    }

    public Position getFuzzySpawnLocation() {
        Position spawn = getSpawnLocation();
        int radius = gameRules.getInteger(GameRule.SPAWN_RADIUS);
        if (radius > 0) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int negativeFlags = random.nextInt(4);
            spawn = spawn.add(
                    radius * random.nextDouble() * ((negativeFlags & 1) > 0 ? -1 : 1),
                    0,
                    radius * random.nextDouble() * ((negativeFlags & 2) > 0 ? -1 : 1)
            );
        }
        return spawn;
    }

    @SuppressWarnings("deprecation")
    public void requestChunk(int x, int z, Player player) {
        Preconditions.checkArgument(player.getLoaderId() > 0, player.getName() + " has no chunk loader");
        long index = Level.chunkHash(x, z);
        var casLock = new AtomicBoolean(false);
        Int2ObjectNonBlockingMap<Player> playerInt2ObjectMap = this.chunkSendQueue.computeIfAbsent(index, (key) -> {
            if (casLock.weakCompareAndSetVolatile(false, true)) {
                return new Int2ObjectNonBlockingMap<>();
            } else {
                return null;
            }
        });
        Objects.requireNonNull(playerInt2ObjectMap).put(player.getLoaderId(), player);
    }

    private void sendChunk(int x, int z, long index, DataPacket packet) {
        for (Player player : this.chunkSendQueue.get(index).values()) {
            if (player.isConnected() && player.getUsedChunks().contains(index)) {
                player.sendChunk(x, z, packet);
            }
        }
        this.chunkSendQueue.remove(index);
    }

    private final ArrayList<CompletableFuture<?>> allChunkRequestTask = new ArrayList<>(
            Server.getInstance().getConfig("chunk-sending.per-tick", 8) * Server.getInstance().getMaxPlayers()
    );

    private void processChunkRequest() {
        for (long index : this.chunkSendQueue.keySet()) {
            int x = getHashX(index);
            int z = getHashZ(index);
            AsyncTask task = this.requireProvider().requestChunkTask(x, z);
            if (task != null) {
                allChunkRequestTask.add(CompletableFuture.runAsync(task, server.getComputeThreadPool()));
            }
        }
        if (!allChunkRequestTask.isEmpty()) {
            CompletableFuture.allOf(allChunkRequestTask.toArray(CompletableFuture<?>[]::new)).join();
            allChunkRequestTask.clear();
        }
    }

    public void chunkRequestCallback(long timestamp, int x, int z, int subChunkCount, byte[] payload) {
        long index = Level.chunkHash(x, z);

        for (Player player : this.chunkSendQueue.get(index).values()) {
            if (player.isConnected() && player.getUsedChunks().contains(index)) {
                player.sendChunk(x, z, getDimension(), subChunkCount, payload);
            }
        }

        this.chunkSendQueue.remove(index);
    }

    public void removeEntity(Entity entity) {
        if (entity.getLevel() != this) {
            throw new LevelException("Invalid Entity level");
        }

        if (entity instanceof Player) {
            this.players.remove(entity.getId());
            this.checkSleep();
        } else {
            entity.close();
        }

        this.entities.remove(entity.getId());
        this.updateEntities.remove(entity.getId());
    }

    public void addEntity(Entity entity) {
        if (entity.getLevel() != this) {
            throw new LevelException("Invalid Entity level");
        }

        if (entity instanceof Player) {
            this.players.put(entity.getId(), (Player) entity);
        }
        this.entities.put(entity.getId(), entity);
    }

    public void addBlockEntity(BlockEntity blockEntity) {
        if (blockEntity.getLevel() != this) {
            throw new LevelException("Invalid Block Entity level");
        }
        blockEntities.put(blockEntity.getId(), blockEntity);
    }

    public void scheduleBlockEntityUpdate(BlockEntity entity) {
        Preconditions.checkNotNull(entity, "entity");
        Preconditions.checkArgument(entity.getLevel() == this, "BlockEntity is not in this level");
        if (!updateBlockEntities.contains(entity)) {
            updateBlockEntities.add(entity);
        }
    }

    public void removeBlockEntity(BlockEntity entity) {
        Preconditions.checkNotNull(entity, "entity");
        Preconditions.checkArgument(entity.getLevel() == this, "BlockEntity is not in this level");
        blockEntities.remove(entity.getId());
        updateBlockEntities.remove(entity);
    }

    /**
     * 该区块是否在使用中，出生点区块，tick区域中的区块，以及存在{@link ChunkLoader}的区块都被看做正在使用
     * <p>
     * Whether the chunk is in use, spawn chunks, chunks in the tick area, and chunks with {@link ChunkLoader} are considered in use
     *
     * @param x the chunk x
     * @param z the chunk z
     * @return the boolean
     */
    public boolean isChunkInUse(int x, int z) {
        return isChunkInUse(Level.chunkHash(x, z));
    }

    /**
     * 该区块是否在使用中，出生点区块，tick区域中的区块，以及存在{@link ChunkLoader}的区块都被看做正在使用
     * <p>
     * Whether the chunk is in use, spawn chunks, chunks in the tick area, and chunks with {@link ChunkLoader} are considered in use
     *
     * @param hash chunk hash value from {@link #chunkHash(int, int)}
     * @return the boolean
     */
    public boolean isChunkInUse(long hash) {
        if (isSpawnChunk(getHashX(hash), getHashZ(hash))) {
            return true;
        }

        var tickingAreaManager = getServer().getTickingAreaManager();
        if (tickingAreaManager != null && tickingAreaManager.getTickingAreaByChunk(this.getName(), new TickingArea.ChunkPos(getHashX(hash), getHashZ(hash))) != null) {
            return true;
        }
        Map<Integer, ChunkLoader> integerChunkLoaderMap = this.chunkLoaders.get(hash);
        if (integerChunkLoaderMap != null) {
            return this.chunkLoaders.containsKey(hash) && !integerChunkLoaderMap.isEmpty();
        } else return false;
    }

    public IChunk getChunk(int chunkX, int chunkZ) {
        return this.getChunk(chunkX, chunkZ, false);
    }

    public IChunk getChunk(@NotNull ChunkVector2 pos) {
        return getChunk(pos.getX(), pos.getZ(), false);
    }

    public IChunk getChunk(int chunkX, int chunkZ, boolean create) {
        long index = Level.chunkHash(chunkX, chunkZ);
        IChunk chunk = this.requireProvider().getLoadedChunk(index);
        if (chunk == null) {
            chunk = this.forceLoadChunk(index, chunkX, chunkZ, create);
        }
        return chunk;
    }

    public CompletableFuture<IChunk> getChunkAsync(int chunkX, int chunkZ) {
        return this.getChunkAsync(chunkX, chunkZ, false);
    }

    public CompletableFuture<IChunk> getChunkAsync(@NotNull ChunkVector2 pos) {
        return getChunkAsync(pos.getX(), pos.getZ(), false);
    }

    public CompletableFuture<IChunk> getChunkAsync(int chunkX, int chunkZ, boolean create) {
        return CompletableFuture.supplyAsync(() -> {
            long index = Level.chunkHash(chunkX, chunkZ);
            IChunk chunk = this.requireProvider().getLoadedChunk(index);
            if (chunk == null) {
                chunk = this.forceLoadChunk(index, chunkX, chunkZ, create);
            }
            return chunk;
        }, Server.getInstance().getScheduler().getAsyncPool());
    }


    public boolean loadChunk(int x, int z) {
        return this.loadChunk(x, z, true);
    }

    public boolean loadChunk(int x, int z, boolean generate) {
        long index = Level.chunkHash(x, z);
        if (this.requireProvider().isChunkLoaded(index)) {
            return true;
        }
        return forceLoadChunk(index, x, z, generate) != null;
    }

    private IChunk forceLoadChunk(long index, int x, int z, boolean generate) {
        IChunk chunk = this.requireProvider().getChunk(x, z, generate);
        if (chunk == null) {
            if (generate) {
                throw new IllegalStateException("Could not create new Chunk");
            }
            return null;
        }

        if (chunk.getProvider() != null) {
            this.server.getPluginManager().callEvent(new ChunkLoadEvent(chunk, !chunk.isGenerated()));
        } else {
            this.unloadChunk(x, z, false);
            return chunk;
        }

        chunk.initChunk();

        if (this.isChunkInUse(index)) {
            this.unloadQueue.remove(index);
            for (ChunkLoader loader : this.getChunkLoaders(x, z)) {
                loader.onChunkLoaded(chunk);
            }
        } else {
            this.unloadQueue.put(index, (Long) System.currentTimeMillis());
        }
        return chunk;
    }

    private void queueUnloadChunk(int x, int z) {
        long index = Level.chunkHash(x, z);
        this.unloadQueue.put(index, (Long) System.currentTimeMillis());
    }

    public boolean unloadChunkRequest(int x, int z) {
        return this.unloadChunkRequest(x, z, true);
    }

    public boolean unloadChunkRequest(int x, int z, boolean safe) {
        if ((safe && this.isChunkInUse(x, z))) {
            return false;
        }

        this.queueUnloadChunk(x, z);

        return true;
    }

    public void cancelUnloadChunkRequest(int x, int z) {
        this.cancelUnloadChunkRequest(Level.chunkHash(x, z));
    }

    public void cancelUnloadChunkRequest(long hash) {
        this.unloadQueue.remove(hash);
    }

    public boolean unloadChunk(int x, int z) {
        return this.unloadChunk(x, z, true);
    }

    public boolean unloadChunk(int x, int z, boolean safe) {
        return this.unloadChunk(x, z, safe, true);
    }

    /**
     * Unload chunk from memory
     *
     * @param safe    check the chunk if is used
     * @param trySave Whether to try to save the chunk
     */
    public synchronized boolean unloadChunk(int x, int z, boolean safe, boolean trySave) {
        if (safe && this.isChunkInUse(x, z)) {
            return false;
        }

        if (!this.isChunkLoaded(x, z)) {
            return true;
        }

        IChunk chunk = this.getChunk(x, z);

        if (chunk != null && chunk.getProvider() != null) {
            ChunkUnloadEvent ev = new ChunkUnloadEvent(chunk);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            }
        }

        try {
            LevelProvider levelProvider = this.requireProvider();
            if (chunk != null) {
                if (trySave && this.getAutoSave()) {
                    int entities = 0;
                    for (Entity e : chunk.getEntities().values()) {
                        if (e instanceof Player) {
                            continue;
                        }
                        ++entities;
                    }

                    if (chunk.hasChanged() || !chunk.getBlockEntities().isEmpty() || entities > 0) {
                        levelProvider.setChunk(x, z, chunk);
                        levelProvider.saveChunk(x, z);
                    }
                }
                for (ChunkLoader loader : this.getChunkLoaders(x, z)) {
                    loader.onChunkUnloaded(chunk);
                }
            }
            levelProvider.unloadChunk(x, z, safe);
        } catch (Exception e) {
            log.error(this.server.getLanguage().tr("nukkit.level.chunkUnloadError", e.toString()), e);
        }

        return true;
    }

    public boolean isSpawnChunk(int x, int z) {
        Vector3 spawn = this.requireProvider().getSpawn();
        int spawnCX = spawn.getFloorX() >> 4;
        int spawnCZ = spawn.getFloorZ() >> 4;
        return x == spawnCX && z == spawnCZ;
    }

    public Position getSafeSpawn() {
        return getSafeSpawn(null);
    }

    public Position getSafeSpawn(Vector3 spawn) {
        return getSafeSpawn(spawn, 16);
    }

    public Position getSafeSpawn(Vector3 spawn, int horizontalMaxOffset) {
        return getSafeSpawn(spawn, horizontalMaxOffset, true);
    }

    public Position getSafeSpawn(Vector3 spawn, int horizontalMaxOffset, boolean allowWaterUnder) {
        if (spawn == null)
            spawn = this.getFuzzySpawnLocation();
        if (spawn == null)
            return null;
        if (standable(spawn, true))
            return Position.fromObject(spawn, this);

        int maxY = isNether() ? 127 : (isOverWorld() ? 319 : 255);
        int minY = isOverWorld() ? -64 : 0;

        for (int horizontalOffset = 0; horizontalOffset <= horizontalMaxOffset; horizontalOffset++) {
            for (int y = maxY; y > minY; y--) {
                Position pos = Position.fromObject(spawn, this);
                pos.setY(y);
                Position newSpawn;
                if (standable(newSpawn = pos.add(horizontalOffset, 0, horizontalOffset), allowWaterUnder))
                    return newSpawn;
                if (standable(newSpawn = pos.add(horizontalOffset, 0, -horizontalOffset), allowWaterUnder))
                    return newSpawn;
                if (standable(newSpawn = pos.add(-horizontalOffset, 0, horizontalOffset), allowWaterUnder))
                    return newSpawn;
                if (standable(newSpawn = pos.add(-horizontalOffset, 0, -horizontalOffset), allowWaterUnder))
                    return newSpawn;
            }
        }

        log.warn("cannot find a safe spawn around " + spawn.asBlockVector3() + "!");
        return Position.fromObject(spawn, this);
    }

    public boolean standable(Vector3 vec) {
        return standable(vec, false);
    }

    public boolean standable(Vector3 vec, boolean allowWaterUnder) {
        Position pos = Position.fromObject(vec, this);
        Block blockUnder = pos.add(0, -1, 0).getLevelBlock(0, true);
        Block block = pos.getLevelBlock(0, true);
        Block blockUpper = pos.add(0, 1, 0).getLevelBlock(0, true);
        if (!allowWaterUnder)
            return !blockUnder.canPassThrough()
                    && (block.isAir() || block.canPassThrough())
                    && (blockUpper.isAir() || block.canPassThrough());
        else
            return (!blockUnder.canPassThrough() || blockUnder.getId().equals(BlockID.FLOWING_WATER) || blockUnder.getId().equals(BlockID.WATERLILY) || blockUnder.getId().equals(BlockID.WATER))
                    && (block.isAir() || block.canPassThrough())
                    && (blockUpper.isAir() || block.canPassThrough());
    }

    /**
     * 获取这个地图经历的时间(一直会累加)
     * <p>
     * Get the elapsed time for this level
     */
    public int getTime() {
        return (int) time;
    }

    /**
     * 设置这个地图经历的时间
     * <p>
     * Set the elapsed time for this level
     */
    public void setTime(int time) {
        this.time = time;
        this.sendTime();
    }

    public boolean isDaytime() {
        return this.skyLightSubtracted < 4;
    }

    public long getCurrentTick() {
        return this.levelCurrentTick;
    }

    public String getName() {
        return this.requireProvider().getName();
    }

    public String getFolderPath() {
        return this.folderPath;
    }

    public void stopTime() {
        this.stopTime = true;
        this.sendTime();
    }

    public void startTime() {
        this.stopTime = false;
        this.sendTime();
    }

    public long getSeed() {
        return this.requireProvider().getSeed();
    }

    public void setSeed(int seed) {
        this.requireProvider().setSeed(seed);
    }


    public void regenerateChunk(int x, int z) {
        this.unloadChunk(x, z, false);

        this.cancelUnloadChunkRequest(x, z);

        LevelProvider levelProvider = requireProvider();
        IChunk chunk = levelProvider.getEmptyChunk(x, z);
        levelProvider.setChunk(x, z, chunk);

        this.generateChunk(x, z);
    }

    public void generateChunk(int x, int z) {
        generateChunk(x, z, false);
    }

    public void generateChunk(int x, int z, boolean force) {
        if (this.chunkGenerationQueue.size() >= this.chunkGenerationQueueSize && !force) {
            return;
        }
        IChunk chunk = this.getChunk(x, z, true);
        long index = Level.chunkHash(x, z);
        if (this.chunkGenerationQueue.putIfAbsent(index, Boolean.TRUE) == null) {
            this.generator.asyncGenerate(chunk, (c) -> chunkGenerationQueue.remove(c.getChunk().getIndex()));//async
        }
    }

    /**
     * 异步执行服务器内存垃圾收集
     * <p>
     * Run server memory garbage collection asynchronously
     *
     * @return the list
     */
    public List<CompletableFuture<Void>> asyncChunkGarbageCollection() {
        var gcBlockInventoryMetaData = CompletableFuture.runAsync(() -> {
            for (var entry : this.getBlockMetadata().getBlockMetadataMap().entrySet()) {
                String key = entry.getKey();
                String[] split = key.split(":");
                Map<Plugin, MetadataValue> value = entry.getValue();
                if (split[3].equals("inventory") && value.containsKey(InternalPlugin.INSTANCE)) {
                    Block block = getBlock(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                    if (!(block instanceof BlockInventoryHolder)) {
                        this.getBlockMetadata().removeMetadata(block, key, InternalPlugin.INSTANCE);
                    }
                }
            }
        });
        var gcBlockEntities = CompletableFuture.runAsync(() -> {
            // remove all invaild block entities.
            if (!blockEntities.isEmpty()) {
                var iter = blockEntities.values().iterator();
                while (iter.hasNext()) {
                    BlockEntity blockEntity = iter.next();
                    if (blockEntity != null) {
                        if (!blockEntity.isValid()) {
                            iter.remove();
                            blockEntity.close();
                        }
                    } else {
                        iter.remove();
                    }
                }
            }
        });
        var gcDeadChunks = CompletableFuture.runAsync(() -> {
            for (Map.Entry<Long, ? extends IChunk> entry : requireProvider().getLoadedChunks().entrySet()) {
                long index = entry.getKey();
                if (!this.unloadQueue.containsKey(index)) {
                    IChunk chunk = entry.getValue();
                    int X = chunk.getX();
                    int Z = chunk.getZ();
                    this.unloadChunkRequest(X, Z, true);
                }
            }
            this.unloadChunks();
        });
        var gcSuper = CompletableFuture.runAsync(() -> this.requireProvider().doGarbageCollection());
        return List.of(gcBlockInventoryMetaData, gcBlockEntities, gcDeadChunks, gcSuper);
    }

    /**
     * 异步执行服务器内存垃圾收集
     * <p>
     * Run server memory garbage collection synchronously
     */

    public void doChunkGarbageCollection() {
        doChunkGarbageCollection(false);
    }

    /**
     * 同步执行服务器内存垃圾收集
     * <p>
     * Run server memory garbage collection synchronously
     *
     * @param force the force
     */
    public void doChunkGarbageCollection(boolean force) {
        // remove all invaild block entities.
        if (!blockEntities.isEmpty()) {
            var iter = blockEntities.values().iterator();
            while (iter.hasNext()) {
                BlockEntity blockEntity = iter.next();
                if (blockEntity != null) {
                    if (!blockEntity.isValid()) {
                        iter.remove();
                        blockEntity.close();
                    }
                } else {
                    iter.remove();
                }
            }
        }

        for (Map.Entry<Long, ? extends IChunk> entry : requireProvider().getLoadedChunks().entrySet()) {
            long index = entry.getKey();
            if (!this.unloadQueue.containsKey(index)) {
                IChunk chunk = entry.getValue();
                int X = chunk.getX();
                int Z = chunk.getZ();
                this.unloadChunkRequest(X, Z, true);
            }
        }
        this.unloadChunks(force);

        this.requireProvider().doGarbageCollection();
    }

    /**
     * 在一些空闲的时间片对Level进行内存垃圾收集
     * <p>
     * Run memory garbage collection on Level in some free time slices
     *
     * @param allocatedTime free time slices
     */
    public void doGarbageCollection(long allocatedTime) {
        long start = System.currentTimeMillis();
        if (unloadChunks(start, allocatedTime, false)) {
            allocatedTime = allocatedTime - (System.currentTimeMillis() - start);
            requireProvider().doGarbageCollection(allocatedTime);
        }
    }

    public void unloadChunks() {
        this.unloadChunks(false);
    }

    public void unloadChunks(boolean force) {
        unloadChunks(96, force);
    }

    public void unloadChunks(int maxUnload, boolean force) {
        if (!this.unloadQueue.isEmpty()) {
            long now = System.currentTimeMillis();

            LongList toRemove = null;
            for (var entry : unloadQueue.fastEntrySet()) {
                long index = entry.getLongKey();
                if (isChunkInUse(index)) {
                    continue;
                }
                int hashX = Level.getHashX(index);
                int hashZ = Level.getHashZ(index);
                if (!force) {
                    long time = entry.getValue();
                    if (maxUnload <= 0) {
                        break;
                    } else if (time > (now - Server.getInstance().getChunkUnloadDelay())) {
                        continue;
                    }
                }

                if (toRemove == null) toRemove = new LongArrayList();
                toRemove.add(index);
            }

            if (toRemove != null) {
                int size = toRemove.size();
                for (int i = 0; i < size; i++) {
                    long index = toRemove.getLong(i);
                    int X = getHashX(index);
                    int Z = getHashZ(index);

                    if (this.unloadChunk(X, Z, true)) {
                        this.unloadQueue.remove(index);
                        --maxUnload;
                    }
                }
            }
        }
    }

    /**
     * @param now
     * @param allocatedTime
     * @param force
     * @return true if there is allocated time remaining
     */
    private boolean unloadChunks(long now, long allocatedTime, boolean force) {
        if (!this.unloadQueue.isEmpty()) {
            boolean result = true;
            int maxIterations = this.unloadQueue.size();

            if (lastUsingUnloadingIter == null)
                lastUsingUnloadingIter = this.unloadQueue.fastEntrySet().iterator();

            var iter = lastUsingUnloadingIter;

            for (int i = 0; i < maxIterations; i++) {
                if (!iter.hasNext()) {
                    iter = this.unloadQueue.fastEntrySet().iterator();
                }
                if (!iter.hasNext()) {
                    break;
                }
                var entry = iter.next();

                long index = entry.getLongKey();

                if (isChunkInUse(index)) {
                    continue;
                }

                if (!force) {
                    long time = entry.getValue();
                    if (time > (now - Server.getInstance().getChunkUnloadDelay())) {
                        continue;
                    }
                }

                int X = getHashX(index);
                int Z = getHashZ(index);
                if (this.unloadChunk(X, Z, true)) {
                    iter.remove();
                    if (System.currentTimeMillis() - now >= allocatedTime) {
                        result = false;
                        break;
                    }
                }
            }
            return result;
        } else {
            return true;
        }
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        this.server.getLevelMetadata().setMetadata(this, metadataKey, newMetadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        return this.server.getLevelMetadata().getMetadata(this, metadataKey);
    }

    @Override
    public MetadataValue getMetadata(String metadataKey, Plugin plugin) {
        return this.server.getLevelMetadata().getMetadata(this, metadataKey, plugin);
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        return this.server.getLevelMetadata().hasMetadata(this, metadataKey);
    }

    @Override
    public boolean hasMetadata(String metadataKey, Plugin plugin) {
        return this.server.getLevelMetadata().hasMetadata(this, metadataKey, plugin);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        this.server.getLevelMetadata().removeMetadata(this, metadataKey, owningPlugin);
    }

    public void addPlayerMovement(Entity entity, double x, double y, double z, double yaw, double pitch, double headYaw) {
        MovePlayerPacket pk = new MovePlayerPacket();
        pk.eid = entity.getId();
        pk.x = (float) x;
        pk.y = (float) y;
        pk.z = (float) z;
        pk.yaw = (float) yaw;
        pk.headYaw = (float) headYaw;
        pk.pitch = (float) pitch;
        if (entity.riding != null) {
            pk.ridingEid = entity.riding.getId();
            pk.mode = MovePlayerPacket.MODE_PITCH;
        }

        Server.broadcastPacket(entity.getViewers().values(), pk);
    }

    public void addEntityMovement(Entity entity, double x, double y, double z, double yaw, double pitch, double headYaw) {
//        MoveEntityAbsolutePacket pk = new MoveEntityAbsolutePacket();
//        pk.eid = entity.getId();
//        pk.x = (float) x;
//        pk.y = (float) y;
//        pk.z = (float) z;
//        pk.yaw = (float) yaw;
//        pk.headYaw = (float) headYaw;
//        pk.pitch = (float) pitch;
//        pk.onGround = entity.onGround;
        MoveEntityDeltaPacket pk = new MoveEntityDeltaPacket();
        pk.runtimeEntityId = entity.getId();
        if (entity.lastX != x) {
            pk.x = (float) x;
            pk.flags |= MoveEntityDeltaPacket.FLAG_HAS_X;
        }
        if (entity.lastY != y) {
            pk.y = (float) y;
            pk.flags |= MoveEntityDeltaPacket.FLAG_HAS_Y;
        }
        if (entity.lastZ != z) {
            pk.z = (float) z;
            pk.flags |= MoveEntityDeltaPacket.FLAG_HAS_Z;
        }
        if (entity.lastPitch != pitch) {
            pk.pitch = (float) pitch;
            pk.flags |= MoveEntityDeltaPacket.FLAG_HAS_PITCH;
        }
        if (entity.lastYaw != yaw) {
            pk.yaw = (float) yaw;
            pk.flags |= MoveEntityDeltaPacket.FLAG_HAS_YAW;
        }
        if (entity.lastHeadYaw != headYaw) {
            pk.headYaw = (float) headYaw;
            pk.flags |= MoveEntityDeltaPacket.FLAG_HAS_HEAD_YAW;
        }
        if (entity.onGround) {
            pk.flags |= MoveEntityDeltaPacket.FLAG_ON_GROUND;
        }
        Server.broadcastPacket(entity.getViewers().values(), pk);
    }

    public boolean isRaining() {
        return this.raining;
    }

    public boolean setRaining(boolean raining) {
        WeatherChangeEvent ev = new WeatherChangeEvent(this, raining);
        this.getServer().getPluginManager().callEvent(ev);

        if (ev.isCancelled()) {
            return false;
        }

        this.raining = raining;

        LevelEventPacket pk = new LevelEventPacket();
        // These numbers are from Minecraft

        if (raining) {
            pk.evid = LevelEventPacket.EVENT_START_RAIN;
            int time = ThreadLocalRandom.current().nextInt(12000) + 12000;
            pk.data = time;
            setRainTime(time);
        } else {
            pk.evid = LevelEventPacket.EVENT_STOP_RAIN;
            setRainTime(ThreadLocalRandom.current().nextInt(168000) + 12000);
        }

        Server.broadcastPacket(this.getPlayers().values(), pk);

        return true;
    }

    public int getRainTime() {
        return this.rainTime;
    }

    public void setRainTime(int rainTime) {
        this.rainTime = rainTime;
    }

    public boolean isThundering() {
        return isRaining() && this.thundering;
    }

    public boolean setThundering(boolean thundering) {
        ThunderChangeEvent ev = new ThunderChangeEvent(this, thundering);
        this.getServer().getPluginManager().callEvent(ev);

        if (ev.isCancelled()) {
            return false;
        }

        if (thundering && !isRaining()) {
            setRaining(true);
        }

        this.thundering = thundering;

        LevelEventPacket pk = new LevelEventPacket();
        // These numbers are from Minecraft
        if (thundering) {
            pk.evid = LevelEventPacket.EVENT_START_THUNDER;
            int time = ThreadLocalRandom.current().nextInt(12000) + 3600;
            pk.data = time;
            setThunderTime(time);
        } else {
            pk.evid = LevelEventPacket.EVENT_STOP_THUNDER;
            setThunderTime(ThreadLocalRandom.current().nextInt(168000) + 12000);
        }

        Server.broadcastPacket(this.getPlayers().values(), pk);

        return true;
    }

    public int getThunderTime() {
        return this.thunderTime;
    }

    public void setThunderTime(int thunderTime) {
        this.thunderTime = thunderTime;
    }

    public void sendWeather(Player[] players) {
        if (players == null) {
            players = this.getPlayers().values().toArray(Player.EMPTY_ARRAY);
        }

        LevelEventPacket pk = new LevelEventPacket();

        if (this.isRaining()) {
            pk.evid = LevelEventPacket.EVENT_START_RAIN;
            pk.data = this.rainTime;
        } else {
            pk.evid = LevelEventPacket.EVENT_STOP_RAIN;
        }

        Server.broadcastPacket(players, pk);

        if (this.isThundering()) {
            pk.evid = LevelEventPacket.EVENT_START_THUNDER;
            pk.data = this.thunderTime;
        } else {
            pk.evid = LevelEventPacket.EVENT_STOP_THUNDER;
        }

        Server.broadcastPacket(players, pk);
    }

    public void sendWeather(Player player) {
        if (player != null) {
            this.sendWeather(new Player[]{player});
        }
    }

    public void sendWeather(Collection<Player> players) {
        if (players == null) {
            players = this.getPlayers().values();
        }
        this.sendWeather(players.toArray(Player.EMPTY_ARRAY));
    }

    public final DimensionData getDimensionData() {
        return generator.getDimensionData();
    }

    public int getDimension() {
        return getDimensionData().getDimensionId();
    }

    public int getDimensionCount() {
        return dimensionCount;
    }

    public final boolean isOverWorld() {
        return getDimension() == 0;
    }

    public final boolean isNether() {
        return getDimension() == 1;
    }

    public final boolean isTheEnd() {
        return getDimension() == 2;
    }

    public final boolean isYInRange(int y) {
        return y >= getMinHeight() && y <= getMaxHeight();
    }

    public boolean canBlockSeeSky(Vector3 pos) {
        return this.getHighestBlockAt(pos.getFloorX(), pos.getFloorZ()) < pos.getY();
    }

    /**
     * Minimum height where blocks can be placed
     */
    public int getMinHeight() {
        return getDimensionData().getMinHeight();
    }

    /**
     * The maximum height at which blocks can be placed
     */
    public int getMaxHeight() {
        return getDimensionData().getMaxHeight();
    }

    public int getStrongPower(Vector3 pos, BlockFace direction) {
        return this.getBlock(pos).getStrongPower(direction);
    }

    public int getStrongPower(Vector3 pos) {
        if (pos instanceof BlockPistonBase || this.getBlock(pos) instanceof BlockPistonBase) return 0;

        int i = 0;
        for (BlockFace face : BlockFace.values()) {
            i = Math.max(i, this.getStrongPower(temporalVector.setComponentsAdding(pos, face), face));

            if (i >= 15) {
                return i;
            }
        }

        return i;
    }

    public boolean isSidePowered(Vector3 pos, BlockFace face) {
        return this.getRedstonePower(pos, face) > 0;
    }

    /**
     * Get the block redstone power can output.
     *
     * @param pos  the block pos
     * @param face Only be used on block with not isNormalBlock, such as redstone torch
     */
    public int getRedstonePower(Vector3 pos, BlockFace face) {
        Block block;

        if (pos instanceof Block b) {
            block = b;
        } else {
            block = this.getBlock(pos);
        }

        return block.isNormalBlock() ? this.getStrongPower(pos) : block.getWeakPower(face);
    }

    public boolean isBlockPowered(Vector3 pos) {
        for (BlockFace face : BlockFace.values()) {
            if (this.getRedstonePower(temporalVector.setComponentsAdding(pos, face), face) > 0) {
                return true;
            }
        }

        return false;
    }

    public int isBlockIndirectlyGettingPowered(Vector3 pos) {
        int power = 0;

        for (BlockFace face : BlockFace.values()) {
            int blockPower = this.getRedstonePower(temporalVector.setComponentsAdding(pos, face), face);

            if (blockPower >= 15) {
                return 15;
            }

            if (blockPower > power) {
                power = blockPower;
            }
        }

        return power;
    }

    public boolean isAreaLoaded(AxisAlignedBB bb) {
        if (bb.getMaxY() < (isOverWorld() ? -64 : 0) || bb.getMinY() >= (isOverWorld() ? 320 : 256)) {
            return false;
        }
        int minX = NukkitMath.floorDouble(bb.getMinX()) >> 4;
        int minZ = NukkitMath.floorDouble(bb.getMinZ()) >> 4;
        int maxX = NukkitMath.floorDouble(bb.getMaxX()) >> 4;
        int maxZ = NukkitMath.floorDouble(bb.getMaxZ()) >> 4;

        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                if (!this.isChunkLoaded(x, z)) {
                    return false;
                }
            }
        }

        return true;
    }

    public int getUpdateLCG() {
        return (this.updateLCG = (this.updateLCG * 3) ^ LCG_CONSTANT);
    }

    public boolean createPortal(Block target) {
        if (this.getDimension() == DIMENSION_THE_END) return false;
        int maxPortalSize = 23;
        final int targX = target.getFloorX();
        final int targY = target.getFloorY();
        final int targZ = target.getFloorZ();
        //check if there's air above (at least 3 blocks)
        for (int i = 1; i < 4; i++) {
            if (!this.getBlock(targX, targY + i, targZ).isAir()) {
                return false;
            }
        }
        int sizePosX = 0;
        int sizeNegX = 0;
        int sizePosZ = 0;
        int sizeNegZ = 0;
        for (int i = 1; i < maxPortalSize; i++) {
            if (this.getBlock(targX + i, targY, targZ).getId().equals(BlockID.OBSIDIAN)) {
                sizePosX++;
            } else {
                break;
            }
        }
        for (int i = 1; i < maxPortalSize; i++) {
            if (this.getBlock(targX - i, targY, targZ).getId().equals(BlockID.OBSIDIAN)) {
                sizeNegX++;
            } else {
                break;
            }
        }
        for (int i = 1; i < maxPortalSize; i++) {
            if (this.getBlock(targX, targY, targZ + i).getId().equals(BlockID.OBSIDIAN)) {
                sizePosZ++;
            } else {
                break;
            }
        }
        for (int i = 1; i < maxPortalSize; i++) {
            if (this.getBlock(targX, targY, targZ - i).getId().equals(BlockID.OBSIDIAN)) {
                sizeNegZ++;
            } else {
                break;
            }
        }
        //plus one for target block
        int sizeX = sizePosX + sizeNegX + 1;
        int sizeZ = sizePosZ + sizeNegZ + 1;
        if (sizeX >= 2 && sizeX <= maxPortalSize) {
            //start scan from 1 block above base
            //find pillar or end of portal to start scan
            int scanX = targX;
            int scanY = targY + 1;
            int scanZ = targZ;
            for (int i = 0; i < sizePosX + 1; i++) {
                //this must be air
                if (!this.getBlock(scanX + i, scanY, scanZ).isAir()) {
                    return false;
                }
                if (this.getBlock(scanX + i + 1, scanY, scanZ).getId().equals(BlockID.OBSIDIAN)) {
                    scanX += i;
                    break;
                }
            }
            //make sure that the above loop finished
            if (!this.getBlock(scanX + 1, scanY, scanZ).getId().equals(BlockID.OBSIDIAN)) {
                return false;
            }

            int innerWidth = 0;
            LOOP:
            for (int i = 0; i < maxPortalSize - 2; i++) {
                String id = this.getBlock(scanX - i, scanY, scanZ).getId();
                switch (id) {
                    case BlockID.AIR:
                        innerWidth++;
                        break;
                    case BlockID.OBSIDIAN:
                        break LOOP;
                    default:
                        return false;
                }
            }
            int innerHeight = 0;
            LOOP:
            for (int i = 0; i < maxPortalSize - 2; i++) {
                String id = this.getBlock(scanX, scanY + i, scanZ).getId();
                switch (id) {
                    case BlockID.AIR:
                        innerHeight++;
                        break;
                    case BlockID.OBSIDIAN:
                        break LOOP;
                    default:
                        return false;
                }
            }
            if (!(innerWidth <= maxPortalSize - 2
                    && innerWidth >= 2
                    && innerHeight <= maxPortalSize - 2
                    && innerHeight >= 3)) {
                return false;
            }

            for (int height = 0; height < innerHeight + 1; height++) {
                if (height == innerHeight) {
                    for (int width = 0; width < innerWidth; width++) {
                        if (!this.getBlock(scanX - width, scanY + height, scanZ).getId().equals(BlockID.OBSIDIAN)) {
                            return false;
                        }
                    }
                } else {
                    if (!this.getBlock(scanX + 1, scanY + height, scanZ).getId().equals(BlockID.OBSIDIAN)
                            || !this.getBlock(scanX - innerWidth, scanY + height, scanZ).getId().equals(BlockID.OBSIDIAN)) {
                        return false;
                    }

                    for (int width = 0; width < innerWidth; width++) {
                        if (!this.getBlock(scanX - width, scanY + height, scanZ).isAir()) {
                            return false;
                        }
                    }
                }
            }

            for (int height = 0; height < innerHeight; height++) {
                for (int width = 0; width < innerWidth; width++) {
                    this.setBlock(new Vector3(scanX - width, scanY + height, scanZ), Block.get(BlockID.PORTAL));
                }
            }

            this.addSound(target, Sound.FIRE_IGNITE);
            return true;
        } else if (sizeZ >= 2 && sizeZ <= maxPortalSize) {
            //start scan from 1 block above base
            //find pillar or end of portal to start scan
            int scanX = targX;
            int scanY = targY + 1;
            int scanZ = targZ;
            for (int i = 0; i < sizePosZ + 1; i++) {
                //this must be air
                if (!this.getBlock(scanX, scanY, scanZ + i).isAir()) {
                    return false;
                }
                if (this.getBlock(scanX, scanY, scanZ + i + 1).getId().equals(BlockID.OBSIDIAN)) {
                    scanZ += i;
                    break;
                }
            }
            //make sure that the above loop finished
            if (!this.getBlock(scanX, scanY, scanZ + 1).getId().equals(BlockID.OBSIDIAN)) {
                return false;
            }

            int innerWidth = 0;
            LOOP:
            for (int i = 0; i < maxPortalSize - 2; i++) {
                String id = this.getBlock(scanX, scanY, scanZ - i).getId();
                switch (id) {
                    case BlockID.AIR:
                        innerWidth++;
                        break;
                    case BlockID.OBSIDIAN:
                        break LOOP;
                    default:
                        return false;
                }
            }
            int innerHeight = 0;
            LOOP:
            for (int i = 0; i < maxPortalSize - 2; i++) {
                String id = this.getBlock(scanX, scanY + i, scanZ).getId();
                switch (id) {
                    case BlockID.AIR:
                        innerHeight++;
                        break;
                    case BlockID.OBSIDIAN:
                        break LOOP;
                    default:
                        return false;
                }
            }
            if (!(innerWidth <= maxPortalSize - 2
                    && innerWidth >= 2
                    && innerHeight <= maxPortalSize - 2
                    && innerHeight >= 3)) {
                return false;
            }

            for (int height = 0; height < innerHeight + 1; height++) {
                if (height == innerHeight) {
                    for (int width = 0; width < innerWidth; width++) {
                        if (!this.getBlock(scanX, scanY + height, scanZ - width).getId().equals(BlockID.OBSIDIAN)) {
                            return false;
                        }
                    }
                } else {
                    if (!this.getBlock(scanX, scanY + height, scanZ + 1).getId().equals(BlockID.OBSIDIAN)
                            || !this.getBlock(scanX, scanY + height, scanZ - innerWidth).getId().equals(BlockID.OBSIDIAN)) {
                        return false;
                    }

                    for (int width = 0; width < innerWidth; width++) {
                        if (!this.getBlock(scanX, scanY + height, scanZ - width).isAir()) {
                            return false;
                        }
                    }
                }
            }

            for (int height = 0; height < innerHeight; height++) {
                for (int width = 0; width < innerWidth; width++) {
                    this.setBlock(new Vector3(scanX, scanY + height, scanZ - width), Block.get(BlockID.PORTAL));
                }
            }

            this.addSound(target, Sound.FIRE_IGNITE);
            return true;
        }

        return false;
    }

    public boolean isRayCollidingWithBlocks(double srcX, double srcY, double srcZ, double dstX, double dstY, double dstZ, double stepSize) {
        Vector3 direction = new Vector3(dstX - srcX, dstY - srcY, dstZ - srcZ);
        double length = direction.length();
        Vector3 normalizedDirection = direction.divide(length);

        for (double t = 0.0; t < length; t += stepSize) {
            int x = (int) Math.round(srcX + normalizedDirection.x * t);
            int y = (int) Math.round(srcY + normalizedDirection.y * t);
            int z = (int) Math.round(srcZ + normalizedDirection.z * t);

            Block block = getBlock(x, y, z);
            if (block != null && block.getCollisionBoundingBox() != null) {
                AxisAlignedBB bb = block.getCollisionBoundingBox();
                if (bb.isVectorInside(x, y, z)) {
                    return true;
                }
            }
        }

        return false; // No collision with any blocks
    }

    public float getBlockDensity(Vector3 source, AxisAlignedBB boundingBox) {
        double xInterval = 1 / ((boundingBox.getMaxX() - boundingBox.getMinX()) * 2 + 1);
        double yInterval = 1 / ((boundingBox.getMaxY() - boundingBox.getMinY()) * 2 + 1);
        double zInterval = 1 / ((boundingBox.getMaxZ() - boundingBox.getMinZ()) * 2 + 1);
        double xOffset = (1 - Math.floor(1 / xInterval) * xInterval) / 2;
        double zOffset = (1 - Math.floor(1 / zInterval) * zInterval) / 2;

        if (xInterval >= 0 && yInterval >= 0 && zInterval >= 0) {
            int visibleBlocks = 0;
            int totalBlocks = 0;

            for (float x = 0; x <= 1; x = (float) ((double) x + xInterval)) {
                for (float y = 0; y <= 1; y = (float) ((double) y + yInterval)) {
                    for (float z = 0; z <= 1; z = (float) ((double) z + zInterval)) {
                        double blockX = boundingBox.getMinX() + (boundingBox.getMaxX() - boundingBox.getMinX()) * (double) x;
                        double blockY = boundingBox.getMinY() + (boundingBox.getMaxY() - boundingBox.getMinY()) * (double) y;
                        double blockZ = boundingBox.getMinZ() + (boundingBox.getMaxZ() - boundingBox.getMinZ()) * (double) z;

                        if (this.isRayCollidingWithBlocks(source.x, source.y, source.z, blockX + xOffset, blockY, blockZ + zOffset, 0.3)) {
                            visibleBlocks++;
                        }

                        totalBlocks++;
                    }
                }
            }

            return (float) visibleBlocks / (float) totalBlocks;
        } else {
            return 0;
        }
    }

    public VibrationManager getVibrationManager() {
        return this.vibrationManager;
    }

    private int ensureY(final int y) {
        return Math.max(Math.min(y, getDimensionData().getMaxHeight()), getDimensionData().getMinHeight());
    }

    @Override
    public String toString() {
        return "Level{" +
                "name='" + name + '\'' +
                ", dimension=" + getDimension() +
                '}';
    }

    @AllArgsConstructor
    @Data
    private static class QueuedUpdate {
        @NotNull
        private Block block;
        private BlockFace neighbor;
    }
}
