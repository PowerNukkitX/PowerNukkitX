package cn.nukkit.player;

import static cn.nukkit.utils.Utils.dynamic;

import cn.nukkit.Server;
import cn.nukkit.block.*;
import cn.nukkit.block.impl.BlockBed;
import cn.nukkit.block.impl.BlockEndPortal;
import cn.nukkit.block.impl.BlockEnderChest;
import cn.nukkit.block.impl.BlockRespawnAnchor;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.camera.data.CameraPreset;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandDataVersions;
import cn.nukkit.command.utils.RawText;
import cn.nukkit.dialog.window.FormWindowDialog;
import cn.nukkit.entity.*;
import cn.nukkit.entity.data.*;
import cn.nukkit.entity.item.EntityFishingHook;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.item.EntityXPOrb;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.entity.projectile.EntityThrownTrident;
import cn.nukkit.event.block.WaterFrostEvent;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityPortalEnterEvent.PortalType;
import cn.nukkit.event.inventory.InventoryPickupArrowEvent;
import cn.nukkit.event.inventory.InventoryPickupItemEvent;
import cn.nukkit.event.inventory.InventoryPickupTridentEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.event.player.PlayerTeleportEvent.TeleportCause;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.inventory.*;
import cn.nukkit.inventory.transaction.*;
import cn.nukkit.item.*;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.lang.CommandOutputContainer;
import cn.nukkit.lang.LangCode;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.*;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.*;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.network.CompressionProvider;
import cn.nukkit.network.Network;
import cn.nukkit.network.SourceInterface;
import cn.nukkit.network.process.DataPacketManager;
import cn.nukkit.network.protocol.*;
import cn.nukkit.network.protocol.types.*;
import cn.nukkit.network.session.NetworkPlayerSession;
import cn.nukkit.permission.PermissibleBase;
import cn.nukkit.permission.Permission;
import cn.nukkit.permission.PermissionAttachment;
import cn.nukkit.permission.PermissionAttachmentInfo;
import cn.nukkit.player.AdventureSettings.Type;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.positiontracking.PositionTrackingService;
import cn.nukkit.potion.Effect;
import cn.nukkit.scheduler.Task;
import cn.nukkit.scheduler.TaskHandler;
import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.data.SortOrder;
import cn.nukkit.scoreboard.displayer.IScoreboardViewer;
import cn.nukkit.scoreboard.scoreboard.IScoreboard;
import cn.nukkit.scoreboard.scoreboard.IScoreboardLine;
import cn.nukkit.scoreboard.scorer.PlayerScorer;
import cn.nukkit.utils.*;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.PlatformDependent;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

/**
 * Game player object, representing the controlled character
 *
 * @author MagicDroidX &amp; Box (Nukkit Project)
 */
@Log4j2
public class Player extends EntityHuman
        implements CommandSender, InventoryHolder, ChunkLoader, IPlayer, IScoreboardViewer {
    /**
     * A empty array of static constants that host the player
     */
    public static final Player[] EMPTY_ARRAY = new Player[0];

    public static final int SURVIVAL_SLOTS = 36;
    public static final int CREATIVE_SLOTS = 112;
    public static final int CRAFTING_SMALL = 0;
    public static final int CRAFTING_BIG = 1;
    public static final int CRAFTING_ANVIL = 2;
    public static final int CRAFTING_ENCHANT = 3;
    public static final int CRAFTING_BEACON = 4;
    public static final int CRAFTING_GRINDSTONE = 1000;
    public static final int CRAFTING_STONECUTTER = 1001;
    public static final int CRAFTING_CARTOGRAPHY = 1002;
    public static final int CRAFTING_SMITHING = 1003;

    /**
     * Villager trading window id
     */
    public static final int TRADE_WINDOW_ID = 500;

    public static final float DEFAULT_SPEED = 0.1f;
    public static final float DEFAULT_FLY_SPEED = 0.05f;
    public static final float MAXIMUM_SPEED = 0.5f;
    public static final int PERMISSION_CUSTOM = 3;
    public static final int PERMISSION_OPERATOR = 2;
    public static final int PERMISSION_MEMBER = 1;
    public static final int PERMISSION_VISITOR = 0;

    public static final int ANVIL_WINDOW_ID = 2;
    public static final int ENCHANT_WINDOW_ID = 3;
    public static final int BEACON_WINDOW_ID = 4;
    public static final int GRINDSTONE_WINDOW_ID = dynamic(5);
    public static final int SMITHING_WINDOW_ID = dynamic(6);

    private static final float ROTATION_UPDATE_THRESHOLD = 1;
    private static final float MOVEMENT_DISTANCE_THRESHOLD = 0.1f;

    protected static final int RESOURCE_PACK_CHUNK_SIZE = 8 * 1024; // 8KB
    protected static final int NO_SHIELD_DELAY = 10;

    public final HashSet<String> achievements = new HashSet<>();
    public final Map<Long, Boolean> usedChunks = new Long2ObjectOpenHashMap<>();

    @Getter
    protected GameMode gamemode;

    @Getter
    private int experience = 0;

    @Getter
    private int experienceLevel = 0;

    /**
     * The difference between the current position and the moving target position vector per tick
     */
    public Vector3 speed = null;

    public int craftingType = CRAFTING_SMALL;
    public long creationTime = 0;

    public int pickedXPOrb = 0;
    public EntityFishingHook fishing = null;

    /**
     * Network
     */
    @Getter
    protected final SourceInterface sourceInterface;

    @Getter
    protected final NetworkPlayerSession networkSession;

    @Getter
    protected final PlayerConnection playerConnection;

    /**
     * Windows
     */
    protected final BiMap<Inventory, Integer> windows = HashBiMap.create();

    protected final BiMap<Integer, Inventory> windowIndex = windows.inverse();
    protected final Set<Integer> permanentWindows = new IntOpenHashSet();

    @Getter
    protected int closingWindowId = Integer.MIN_VALUE;

    protected int windowCnt = 4;

    protected final Long2ObjectLinkedOpenHashMap<Boolean> loadQueue = new Long2ObjectLinkedOpenHashMap<>();
    protected final Map<UUID, Player> hiddenPlayers = new HashMap<>();
    protected final int chunksPerTick;
    protected final int spawnThreshold;

    protected int messageCounter = 2;

    @Getter
    protected PlayerUIInventory UIInventory;

    @Getter
    protected CraftingGrid craftingGrid;

    /**
     * Transactions
     */
    protected CraftingTransaction craftingTransaction;

    protected EnchantTransaction enchantTransaction;
    protected RepairItemTransaction repairItemTransaction;
    protected GrindstoneTransaction grindstoneTransaction;
    protected SmithingTransaction smithingTransaction;
    protected TradingTransaction tradingTransaction;

    /**
     * Whether to remove the color character in the chat of the changed player as §c §1
     */
    @Getter
    @Setter
    protected boolean removeFormat = true;

    protected String username;
    protected String displayName;

    /**
     * This value represents whether the player is using the item or not (long right click), -1 means the player is not using the item, when the player is using the item this value is the value of {@link Server#getTick() getTick()}.
     */
    @Getter
    protected int startActionTick = -1;

    protected Vector3 sleeping = null;
    protected int chunkLoadCount = 0;
    protected int nextChunkOrderRun = 1;
    protected Vector3 newPosition = null;
    protected int chunkRadius;
    protected int viewDistance;

    protected Position spawnPosition;
    protected Position spawnBlockPosition;

    /**
     * Represents the number of ticks the player has passed through the air.
     */
    @Getter
    protected int inAirTicks = 0;

    protected int startAirTicks = 5;

    protected AdventureSettings adventureSettings;

    @Getter
    protected PlayerFood foodData = null;

    @Getter
    @Setter
    protected boolean checkMovement = true;

    protected boolean enableClientCommand = true;

    protected int formWindowCount = 0;
    protected Map<Integer, FormWindow> formWindows = new Int2ObjectOpenHashMap<>();
    protected Map<Integer, FormWindow> serverSettings = new Int2ObjectOpenHashMap<>();
    /**
     * We use Google's cache to store NPC dialogs to send messages
     * The reason is that there is a chance that the client will not respond to the dialogs sent, and in certain cases we cannot clear these dialogs, which can lead to memory leaks
     * Unresponsive dialogs will be cleared after 5 minutes
     */
    protected Cache<String, FormWindowDialog> dialogWindows =
            Caffeine.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();

    protected Map<Long, DummyBossBar> dummyBossBars = new Long2ObjectLinkedOpenHashMap<>();

    @Getter
    protected int lastInAirTick = 0;

    protected BlockVector3 lastBreakPosition = new BlockVector3();
    public long lastBreak;
    public long lastSkinChange;
    protected int lastEnderPearl = 20;
    protected int lastChorusFruitTeleport = 20;

    private final Queue<Location> clientMovements = PlatformDependent.newMpscQueue(4);
    private final AtomicReference<Locale> locale = new AtomicReference<>(null);
    private int unverifiedPackets;

    @Getter
    @Setter
    private int timeSinceRest;

    private String buttonText = "Button";
    protected PermissibleBase perm;
    private int hash;

    @Getter
    private final int loaderId;

    @Setter
    private boolean hasSeenCredits;

    private boolean wasInSoulSandCompatible;

    @Getter
    private float soulSpeedMultiplier = 1;

    private Entity killer = null;
    /**
     * This is used to temporarily store the player's open EnderChest instance object, when the player opens the EnderChest the value is specified as that EnderChest, when the player closes the EnderChest reset back to null.
     */
    private BlockEnderChest viewingEnderChest = null;

    private TaskHandler delayedPosTrackingUpdate;

    @Getter
    private int noShieldTicks;

    @Getter
    protected boolean showingCredits;

    @Getter
    protected PlayerInfo playerInfo;
    /**
     * Time to play sound when player upgrades
     */
    protected int lastPlayerdLevelUpSoundTime = 0;
    /**
     * The entity that the player attacked last.
     */
    @Getter
    protected Entity lastAttackEntity = null;
    /**
     * The entity that the player is attacked last.
     */
    @Getter
    protected Entity lastBeAttackEntity = null;
    /**
     * Player Fog Settings
     */
    @Getter
    @Setter
    protected List<PlayerFogPacket.Fog> fogStack = new ArrayList<>();

    @Setter
    private boolean foodEnabled = true;

    @Getter(value = AccessLevel.PACKAGE)
    private final @NotNull PlayerHandle playerHandle;

    private boolean needDimensionChangeACK = false;

    @Setter
    private Boolean openSignFront = null;

    private Boolean flySneaking = false;

    /**
     * Constructor for unit testing
     *
     * @param sourceInterface Interfaz
     * @param ip       IP address
     * @param port     Port
     */
    public Player(SourceInterface sourceInterface, String ip, int port) {
        this(sourceInterface, uncheckedNewInetSocketAddress(ip, port));
    }

    public Player(SourceInterface sourceInterface, InetSocketAddress socketAddress) {
        super(null, new CompoundTag());
        this.sourceInterface = sourceInterface;
        this.networkSession = sourceInterface.getSession(socketAddress);
        this.server = Server.getInstance();
        this.perm = new PermissibleBase(this);
        this.lastBreak = -1;
        this.loaderId = Level.generateChunkLoaderId(this);
        this.chunksPerTick = this.server.getConfig("chunk-sending.per-tick", 8);
        this.spawnThreshold = this.server.getConfig("chunk-sending.spawn-threshold", 56);
        this.spawnPosition = null;
        this.gamemode = server.getGamemode();
        this.setLevel(this.server.getDefaultLevel());
        this.viewDistance = this.server.getViewDistance();
        this.chunkRadius = viewDistance;
        this.boundingBox = new SimpleAxisAlignedBB(0, 0, 0, 0, 0, 0);
        this.lastSkinChange = -1;
        this.playerConnection = new PlayerConnection(this, networkSession, socketAddress);
        this.playerHandle = new PlayerHandle(this);

        this.uuid = null;
        this.rawUUID = null;

        this.creationTime = System.currentTimeMillis();
    }

    public boolean isConnected() {
        return playerConnection != null && playerConnection.isConnected();
    }

    public boolean isLoggedIn() {
        return playerConnection != null && playerConnection.isLoggedIn();
    }

    @Override
    public boolean isOnline() {
        return isConnected() && isLoggedIn();
    }

    public boolean isSpawned() {
        return playerConnection != null && playerConnection.isSpawned();
    }

    public boolean isLocallyInitialized() {
        return playerConnection != null && playerConnection.isLocallyInitialized();
    }

    private static InetSocketAddress uncheckedNewInetSocketAddress(String ip, int port) {
        try {
            return new InetSocketAddress(InetAddress.getByName(ip), port);
        } catch (UnknownHostException exception) {
            throw new IllegalArgumentException(exception);
        }
    }

    private EntityInteractable getEntityAtPosition(Entity[] nearbyEntities, int x, int y, int z) {
        for (Entity nearestEntity : nearbyEntities) {
            if (nearestEntity.getFloorX() == x
                    && nearestEntity.getFloorY() == y
                    && nearestEntity.getFloorZ() == z
                    && nearestEntity instanceof EntityInteractable
                    && ((EntityInteractable) nearestEntity).canDoInteraction()) {
                return (EntityInteractable) nearestEntity;
            }
        }
        return null;
    }

    @SneakyThrows
    private List<DataPacket> unpackBatchedPackets(BatchPacket packet) {
        return this.server
                .getNetwork()
                .unpackBatchedPackets(
                        packet, this.server.isEnableSnappy() ? CompressionProvider.SNAPPY : CompressionProvider.ZLIB);
    }

    // todo a lot on dimension
    private void setDimension(int dimension) {
        ChangeDimensionPacket pk = new ChangeDimensionPacket();
        pk.dimension = dimension;
        pk.x = (float) this.x();
        pk.y = (float) this.y();
        pk.z = (float) this.z();
        this.sendPacket(pk);

        this.needDimensionChangeACK = true;
    }

    private void updateBlockingFlag() {
        boolean shouldBlock = getNoShieldTicks() == 0
                && (this.isSneaking() || getRiding() != null)
                && (this.getInventory().getItemInHand() instanceof ItemShield
                        || this.getOffhandInventory().getItem(0) instanceof ItemShield);

        if (isBlocking() != shouldBlock) {
            this.setBlocking(shouldBlock);
        }
    }

    protected void sendNextChunk() {
        if (!this.isConnected()) {
            return;
        }

        if (!loadQueue.isEmpty()) {
            int count = 0;
            ObjectIterator<Long2ObjectMap.Entry<Boolean>> iter =
                    loadQueue.long2ObjectEntrySet().fastIterator();
            while (iter.hasNext()) {
                Long2ObjectMap.Entry<Boolean> entry = iter.next();
                long index = entry.getLongKey();

                if (count >= this.chunksPerTick) {
                    break;
                }
                int chunkX = Level.getHashX(index);
                int chunkZ = Level.getHashZ(index);

                ++count;

                this.usedChunks.put(index, false);
                this.getLevel().registerChunkLoader(this, chunkX, chunkZ, false);

                if (!this.getLevel().populateChunk(chunkX, chunkZ)) {
                    if (this.isSpawned()) {
                        continue;
                    } else {
                        break;
                    }
                }

                iter.remove();

                PlayerChunkRequestEvent event = new PlayerChunkRequestEvent(this, chunkX, chunkZ);
                event.call();
                this.getLevel().requestChunk(chunkX, chunkZ, this);
            }
        }
        if (this.chunkLoadCount >= this.spawnThreshold && !this.isSpawned() && isConnected()) {
            playerHandle.handleSpawn();
        }
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.addDefaultWindows();
    }

    protected boolean orderChunks() {
        if (!this.isConnected()) {
            return false;
        }

        this.nextChunkOrderRun = 200;

        loadQueue.clear();
        Long2ObjectOpenHashMap<Boolean> lastChunk = new Long2ObjectOpenHashMap<>(this.usedChunks);

        int centerX = (int) this.x() >> 4;
        int centerZ = (int) this.z() >> 4;

        int radius = playerConnection.isSpawned() ? this.chunkRadius : (int) Math.ceil(Math.sqrt(spawnThreshold));
        int radiusSqr = radius * radius;
        long index;

        // player center chunk
        if (this.usedChunks.get(index = Level.chunkHash(centerX, centerZ)) != Boolean.TRUE) {
            this.loadQueue.put(index, Boolean.TRUE);
        }
        lastChunk.remove(index);
        for (int r = 1; r <= radius; r++) {
            int rr = r * r;
            for (int i = 0; i <= r; i++) {
                int distanceSqr = rr + i * i;
                if (distanceSqr > radiusSqr) continue;
                // right includes upper right corner
                if (this.usedChunks.get(index = Level.chunkHash(centerX + r, centerZ + i)) != Boolean.TRUE) {
                    this.loadQueue.put(index, Boolean.TRUE);
                }
                lastChunk.remove(index);

                // right includes lower right corner
                if (this.usedChunks.get(index = Level.chunkHash(centerX + r, centerZ - i)) != Boolean.TRUE) {
                    this.loadQueue.put(index, Boolean.TRUE);
                }
                lastChunk.remove(index);

                // left includes upper left corner
                if (this.usedChunks.get(index = Level.chunkHash(centerX - r, centerZ + i)) != Boolean.TRUE) {
                    this.loadQueue.put(index, Boolean.TRUE);
                }
                lastChunk.remove(index);

                // left includes lower left corner
                if (this.usedChunks.get(index = Level.chunkHash(centerX - r, centerZ - i)) != Boolean.TRUE) {
                    this.loadQueue.put(index, Boolean.TRUE);
                }
                lastChunk.remove(index);

                // Exclude duplicate corners
                if (i != r) {
                    // top
                    if (this.usedChunks.get(index = Level.chunkHash(centerX + i, centerZ + r)) != Boolean.TRUE) {
                        this.loadQueue.put(index, Boolean.TRUE);
                    }
                    lastChunk.remove(index);

                    if (this.usedChunks.get(index = Level.chunkHash(centerX - i, centerZ + r)) != Boolean.TRUE) {
                        this.loadQueue.put(index, Boolean.TRUE);
                    }
                    lastChunk.remove(index);

                    // end
                    if (this.usedChunks.get(index = Level.chunkHash(centerX + i, centerZ - r)) != Boolean.TRUE) {
                        this.loadQueue.put(index, Boolean.TRUE);
                    }
                    lastChunk.remove(index);
                    if (this.usedChunks.get(index = Level.chunkHash(centerX - i, centerZ - r)) != Boolean.TRUE) {
                        this.loadQueue.put(index, Boolean.TRUE);
                    }
                    lastChunk.remove(index);
                }
            }
        }

        LongIterator keys = lastChunk.keySet().iterator();
        while (keys.hasNext()) {
            index = keys.nextLong();
            this.unloadChunk(Level.getHashX(index), Level.getHashZ(index));
        }

        if (!loadQueue.isEmpty()) {
            NetworkChunkPublisherUpdatePacket packet = new NetworkChunkPublisherUpdatePacket();
            packet.position = this.asBlockVector3();
            packet.radius = viewDistance << 4;
            this.sendPacket(packet);
        }
        return true;
    }

    @Override
    protected void checkGroundState(double movX, double movY, double movZ, double dx, double dy, double dz) {
        if (!this.onGround || movX != 0 || movY != 0 || movZ != 0) {
            boolean onGround = false;

            AxisAlignedBB bb = this.boundingBox.clone();
            bb.setMaxY(bb.getMinY() + 0.5);
            bb.setMinY(bb.getMinY() - 1);

            AxisAlignedBB realBB = this.boundingBox.clone();
            realBB.setMaxY(realBB.getMinY() + 0.1);
            realBB.setMinY(realBB.getMinY() - 0.2);

            int minX = NukkitMath.floorDouble(bb.getMinX());
            int minY = NukkitMath.floorDouble(bb.getMinY());
            int minZ = NukkitMath.floorDouble(bb.getMinZ());
            int maxX = NukkitMath.ceilDouble(bb.getMaxX());
            int maxY = NukkitMath.ceilDouble(bb.getMaxY());
            int maxZ = NukkitMath.ceilDouble(bb.getMaxZ());

            for (int z = minZ; z <= maxZ; ++z) {
                for (int x = minX; x <= maxX; ++x) {
                    for (int y = minY; y <= maxY; ++y) {
                        Block block = this.getLevel().getBlock(this.temporalVector.setComponents(x, y, z));

                        if (!block.canPassThrough() && block.collidesWithBB(realBB)) {
                            onGround = true;
                            break;
                        }
                    }
                }
            }

            this.onGround = onGround;
        }

        this.isCollided = this.onGround;
    }

    @Override
    protected void checkBlockCollision() {
        boolean portal = false;
        boolean scaffolding = false;
        boolean endPortal = false;
        for (Block block : this.getCollisionBlocks()) {
            if (this.isSpectator()) {
                continue;
            }

            switch (block.getId()) {
                case BlockID.NETHER_PORTAL -> portal = true;
                case BlockID.SCAFFOLDING -> scaffolding = true;
                case BlockID.END_PORTAL -> endPortal = true;
            }

            block.onEntityCollide(this);
            block.getLevelBlockAtLayer(1).onEntityCollide(this);
        }
        AxisAlignedBB scanBoundingBox = boundingBox.getOffsetBoundingBox(0, -0.125, 0);
        scanBoundingBox.setMaxY(boundingBox.getMinY());
        Block[] scaffoldingUnder =
                getLevel().getCollisionBlocks(scanBoundingBox, true, true, b -> b.getId() == BlockID.SCAFFOLDING);

        setDataFlag(DATA_FLAGS_EXTENDED, DATA_FLAG_OVER_DESCENDABLE_BLOCK, scaffoldingUnder.length > 0);
        setDataFlag(DATA_FLAGS_EXTENDED, DATA_FLAG_IN_SCAFFOLDING, scaffolding);
        setDataFlag(DATA_FLAGS_EXTENDED, DATA_FLAG_OVER_SCAFFOLDING, scaffoldingUnder.length > 0);
        setDataFlag(DATA_FLAGS_EXTENDED, DATA_FLAG_IN_ASCENDABLE_BLOCK, scaffolding);

        if (endPortal) {
            if (!inEndPortal) {
                inEndPortal = true;
                if (this.getRiding() == null && this.getPassengers().isEmpty()) {
                    EntityPortalEnterEvent event = new EntityPortalEnterEvent(this, PortalType.END);
                    event.call();

                    if (!event.isCancelled()) {
                        final Position newPos = EnumLevel.moveToTheEnd(this);
                        if (newPos != null) {
                            if (newPos.getLevel().getDimension() == Level.DIMENSION_THE_END) {
                                if (teleport(newPos, PlayerTeleportEvent.TeleportCause.END_PORTAL)) {
                                    server.getScheduler()
                                            .scheduleDelayedTask(
                                                    new Task() {
                                                        @Override
                                                        public void onRun(int currentTick) {
                                                            // dirty hack to make sure chunks are loaded and generated
                                                            // before spawning player
                                                            teleport(
                                                                    newPos,
                                                                    PlayerTeleportEvent.TeleportCause.END_PORTAL);
                                                            BlockEndPortal.spawnObsidianPlatform(newPos);
                                                        }
                                                    },
                                                    5);
                                }
                            } else {
                                if (!this.hasSeenCredits && !this.showingCredits) {
                                    PlayerShowCreditsEvent playerShowCreditsEvent = new PlayerShowCreditsEvent(this);
                                    playerShowCreditsEvent.call();
                                    if (!playerShowCreditsEvent.isCancelled()) {
                                        this.showCredits();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            inEndPortal = false;
        }

        if (portal) {
            if ((this.isCreative() || this.isSpectator()) && this.inPortalTicks < 80) {
                this.inPortalTicks = 80;
            } else {
                this.inPortalTicks++;
            }
        } else {
            this.inPortalTicks = 0;
        }
    }

    protected void checkNearEntities() {
        for (Entity entity : this.getLevel().getNearbyEntities(this.boundingBox.grow(1, 0.5, 1), this)) {
            entity.scheduleUpdate();

            if (!entity.isAlive() || !this.isAlive()) {
                continue;
            }

            this.pickupEntity(entity, true);
        }
    }

    protected void handleMovement(Location clientPos) {
        if (this.firstMove) this.firstMove = false;
        boolean invalidMotion = false;
        var revertPos = this.getLocation().clone();
        double distance = clientPos.distanceSquared(this);
        // before check
        if (distance > 128) {
            invalidMotion = true;
        } else if (this.chunk == null || !this.chunk.isGenerated()) {
            BaseFullChunk chunk =
                    this.getLevel().getChunk(clientPos.getChunkX() >> 4, clientPos.getChunkX() >> 4, false);
            if (chunk == null || !chunk.isGenerated()) {
                invalidMotion = true;
                this.nextChunkOrderRun = 0;
            } else {
                if (this.chunk != null) {
                    this.chunk.removeEntity(this);
                }
                this.chunk = chunk;
            }
        }

        if (invalidMotion) {
            this.revertClientMotion(revertPos);
            this.resetClientMovement();
            return;
        }

        // update server-side position and rotation and aabb
        double diffX = clientPos.x() - this.x();
        double diffY = clientPos.y() - this.y();
        double diffZ = clientPos.z() - this.z();
        this.fastMove(diffX, diffY, diffZ);
        this.setRotation(clientPos.yaw(), clientPos.pitch(), clientPos.headYaw());

        // after check
        double corrX = this.x() - clientPos.x();
        double corrY = this.y() - clientPos.y();
        double corrZ = this.z() - clientPos.z();
        if (this.checkMovement
                && (Math.abs(corrX) > 0.5 || Math.abs(corrY) > 0.5 || Math.abs(corrZ) > 0.5)
                && this.riding == null
                && !this.hasEffect(Effect.LEVITATION)
                && !this.hasEffect(Effect.SLOW_FALLING)
                && !server.getAllowFlight()) {
            double diff = corrX * corrX + corrZ * corrZ;
            // 这里放宽了判断，否则对角穿过脚手架会判断非法移动。
            if (diff > 1.2) {
                PlayerInvalidMoveEvent event = new PlayerInvalidMoveEvent(this, true);
                event.call();
                if (!event.isCancelled() && (invalidMotion = event.isRevert())) {
                    log.warn(this.getServer().getLanguage().tr("nukkit.player.invalidMove", this.getName()));
                }
            }
            if (invalidMotion) {
                this.setPositionAndRotation(
                        revertPos.asVector3f().asVector3(), revertPos.yaw(), revertPos.pitch(), revertPos.headYaw());
                this.revertClientMotion(revertPos);
                this.resetClientMovement();
                return;
            }
        }

        // update server-side position and rotation and aabb
        Location last = new Location(
                this.lastX, this.lastY, this.lastZ, this.lastYaw, this.lastPitch, this.lastHeadYaw, this.getLevel());
        Location now = this.getLocation();
        this.lastX = now.x();
        this.lastY = now.y();
        this.lastZ = now.z();
        this.lastYaw = now.yaw();
        this.lastPitch = now.pitch();
        this.lastHeadYaw = now.headYaw();

        List<Block> blocksAround = null;
        List<Block> collidingBlocks = null;
        if (this.blocksAround != null && this.collisionBlocks != null) {
            blocksAround = new ArrayList<>(this.blocksAround);
            collidingBlocks = new ArrayList<>(this.collisionBlocks);
        }

        if (!this.firstMove) {
            if (this.clientMovements.isEmpty()) {
                this.blocksAround = null;
                this.collisionBlocks = null;
            }
            PlayerMoveEvent event = new PlayerMoveEvent(this, last, now);
            event.call();

            if (!(invalidMotion = event.isCancelled())) { // Yes, this is intended
                if (!now.equals(event.getTo()) && this.riding == null) { // If plugins modify the destination
                    if (!this.isSpectator())
                        this.getLevel()
                                .getVibrationManager()
                                .callVibrationEvent(
                                        new VibrationEvent(this, event.getTo().clone(), VibrationType.TELEPORT));
                    this.teleport(event.getTo(), null);
                } else {
                    if (!this.isSpectator() && (last.x() != now.x() || last.y() != now.y() || last.z() != now.z())) {
                        if (this.isOnGround() && this.isGliding()) {
                            this.getLevel()
                                    .getVibrationManager()
                                    .callVibrationEvent(
                                            new VibrationEvent(this, this.clone(), VibrationType.ELYTRA_GLIDE));
                        } else if (this.isOnGround()
                                && this.getSide(BlockFace.DOWN).getLevelBlock().getId() != BlockID.WOOL
                                && !this.isSneaking()) {
                            this.getLevel()
                                    .getVibrationManager()
                                    .callVibrationEvent(new VibrationEvent(this, this.clone(), VibrationType.STEP));
                        } else if (this.isTouchingWater()) {
                            this.getLevel()
                                    .getVibrationManager()
                                    .callVibrationEvent(new VibrationEvent(this, this.clone(), VibrationType.SWIM));
                        }
                    }
                    this.broadcastMovement();
                }
            } else {
                this.blocksAround = blocksAround;
                this.collisionBlocks = collidingBlocks;
            }
        }

        // update speed
        if (this.speed == null) {
            this.speed = new Vector3(last.x() - now.x(), last.y() - now.y(), last.z() - now.z());
        } else {
            this.speed.setComponents(last.x() - now.x(), last.y() - now.y(), last.z() - now.z());
        }

        handleLogicInMove(invalidMotion, distance);

        // if plugin cancel move
        if (invalidMotion) {
            this.setPositionAndRotation(
                    revertPos.asVector3f().asVector3(), revertPos.yaw(), revertPos.pitch(), revertPos.headYaw());
            this.revertClientMotion(revertPos);
            this.resetClientMovement();
        } else {
            if (distance != 0 && this.nextChunkOrderRun > 20) {
                this.nextChunkOrderRun = 20;
            }
        }
    }

    protected void offerMovementTask(Location newPosition) {
        var distance = newPosition.distanceSquared(this);
        var updatePosition = (float) Math.sqrt(distance) > MOVEMENT_DISTANCE_THRESHOLD; // sqrt distance
        var updateRotation = (float) Math.abs(this.pitch() - newPosition.pitch()) > ROTATION_UPDATE_THRESHOLD
                || (float) Math.abs(this.yaw() - newPosition.yaw()) > ROTATION_UPDATE_THRESHOLD
                || (float) Math.abs(this.headYaw() - newPosition.headYaw()) > ROTATION_UPDATE_THRESHOLD;
        var isHandle = this.isAlive() && this.isSpawned() && !this.isSleeping() && (updatePosition || updateRotation);
        if (isHandle) {
            this.newPosition = newPosition;
            this.clientMovements.offer(newPosition);
        }
    }

    protected void handleLogicInMove(boolean invalidMotion, double distance) {
        if (!invalidMotion) {
            // 处理饱食度更新
            if (this.isFoodEnabled() && this.getServer().getDifficulty() > 0) {
                // UpdateFoodExpLevel
                if (distance >= 0.05) {
                    double jump = 0;
                    double swimming = this.isInsideOfWater() ? 0.01 * distance : 0;
                    double distance2 = distance;
                    if (swimming != 0) distance2 = 0;
                    if (this.isSprinting()) { // Running
                        if (this.inAirTicks == 3 && swimming == 0) {
                            jump = 0.2;
                        }
                        this.getFoodData().updateFoodExpLevel(0.1 * distance2 + jump + swimming);
                    } else {
                        if (this.inAirTicks == 3 && swimming == 0) {
                            jump = 0.05;
                        }
                        this.getFoodData().updateFoodExpLevel(jump + swimming);
                    }
                }
            }

            // 处理冰霜行者附魔
            Enchantment frostWalker = inventory.getBoots().getEnchantment(Enchantment.ID_FROST_WALKER);
            if (frostWalker != null
                    && frostWalker.getLevel() > 0
                    && !this.isSpectator()
                    && this.y() >= 1
                    && this.y() <= 255) {
                int radius = 2 + frostWalker.getLevel();
                for (int coordX = this.getFloorX() - radius; coordX < this.getFloorX() + radius + 1; coordX++) {
                    for (int coordZ = this.getFloorZ() - radius; coordZ < this.getFloorZ() + radius + 1; coordZ++) {
                        Block block = getLevel().getBlock(coordX, this.getFloorY() - 1, coordZ);
                        int layer = 0;
                        if ((block.getId() != Block.STILL_WATER
                                        && (block.getId() != Block.FLOWING_WATER || block.getDamage() != 0))
                                || block.up().getId() != Block.AIR) {
                            block = block.getLevelBlockAtLayer(1);
                            layer = 1;
                            if ((block.getId() != Block.STILL_WATER
                                            && (block.getId() != Block.FLOWING_WATER || block.getDamage() != 0))
                                    || block.up().getId() != Block.AIR) {
                                continue;
                            }
                        }
                        WaterFrostEvent event = new WaterFrostEvent(block, this);
                        event.call();
                        if (!event.isCancelled()) {
                            getLevel().setBlock(block, layer, Block.get(Block.ICE_FROSTED), true, false);
                            getLevel()
                                    .scheduleUpdate(
                                            getLevel().getBlock(block, layer),
                                            ThreadLocalRandom.current().nextInt(20, 40));
                        }
                    }
                }
            }

            // 处理灵魂急行附魔
            int soulSpeedLevel = this.getInventory().getBoots().getEnchantmentLevel(Enchantment.ID_SOUL_SPEED);
            if (soulSpeedLevel > 0) {
                Block downBlock = this.getLevelBlock().down();

                if (this.wasInSoulSandCompatible && !downBlock.isSoulSpeedCompatible()) {
                    this.wasInSoulSandCompatible = false;
                    this.soulSpeedMultiplier = 1;
                    this.sendMovementSpeed(this.movementSpeed);
                } else if (!this.wasInSoulSandCompatible && downBlock.isSoulSpeedCompatible()) {
                    this.wasInSoulSandCompatible = true;
                    this.soulSpeedMultiplier = (soulSpeedLevel * 0.105f) + 1.3f;
                    this.sendMovementSpeed(this.movementSpeed * this.soulSpeedMultiplier);
                }
            }
        }
    }

    protected void resetClientMovement() {
        this.newPosition = null;
        this.positionChanged = false;
    }

    protected final void initialize(FullChunk chunk, CompoundTag nbt) {
        super.init(chunk, nbt);
    }

    protected void revertClientMotion(Location originalPos) {
        this.lastX = originalPos.x();
        this.lastY = originalPos.y();
        this.lastZ = originalPos.z();
        this.lastYaw = originalPos.yaw();
        this.lastPitch = originalPos.pitch();

        Vector3 syncPos = originalPos.add(0, 0.00001, 0);
        this.sendPosition(syncPos, originalPos.yaw(), originalPos.pitch(), MovePlayerPacket.MODE_RESET);

        if (this.speed == null) {
            this.speed = new Vector3(0, 0, 0);
        } else {
            this.speed.setComponents(0, 0, 0);
        }
    }

    /**
     * Determine if the respawn anchor is valid if the respawn anchor is valid then the anchor is respawned at the respawn anchor or reborn in bed
     * If the player has none of the above 2 then respawn at the server respawn point
     *
     * @param block - Block
     */
    protected boolean isValidRespawnBlock(Block block) {
        if (block.getId() == BlockID.RESPAWN_ANCHOR && block.getLevel().getDimension() == Level.DIMENSION_NETHER) {
            BlockRespawnAnchor anchor = (BlockRespawnAnchor) block;
            return anchor.getCharge() > 0;
        }
        if (block.getId() == BlockID.BED_BLOCK && block.getLevel().getDimension() == Level.DIMENSION_OVERWORLD) {
            BlockBed bed = (BlockBed) block;
            return bed.isBedValid();
        }

        return false;
    }

    protected void respawn() {
        // the player cant respawn if the server is hardcore
        if (this.server.isHardcore()) {
            this.setBanned(true);
            return;
        }

        this.craftingType = CRAFTING_SMALL;
        this.resetCraftingGridType();

        // level spawn point < block spawn = self spawn
        PlayerRespawnEvent playerRespawnEvent = new PlayerRespawnEvent(this, this.getSpawn());
        if (spawnPosition != null && spawnBlockPosition == null) { // self spawn
            playerRespawnEvent.setRespawnPosition(spawnPosition);
        } else if (spawnBlockPosition != null && spawnPosition == null) { // block spawn
            Block spawnBlock = spawnBlockPosition.getLevelBlock();
            if (spawnBlock != null && isValidRespawnBlock(spawnBlock)) {
                playerRespawnEvent.setRespawnPosition(spawnBlock);
                // handle RESPAWN_ANCHOR state change when consume charge is true
                if (spawnBlock.getId() == BlockID.RESPAWN_ANCHOR) {
                    BlockRespawnAnchor respawnAnchor = (BlockRespawnAnchor) spawnBlock;
                    int charge = respawnAnchor.getCharge();
                    if (charge > 0) {
                        respawnAnchor.setCharge(charge - 1);
                        respawnAnchor.getLevel().setBlock(respawnAnchor, spawnBlock);
                        respawnAnchor.getLevel().scheduleUpdate(respawnAnchor, 10);
                        respawnAnchor.getLevel().addSound(this, Sound.RESPAWN_ANCHOR_DEPLETE, 1, 1, this);
                    }
                }
            } else { // block not available
                playerRespawnEvent.setRespawnPosition(
                        this.getServer().getDefaultLevel().getSafeSpawn());
                // handle spawn point change when block spawn not available
                this.spawnPosition = null;
                this.spawnBlockPosition = null;
                sendMessage(new TranslationContainer(TextFormat.GRAY + "%tile."
                        + (this.getLevel().getDimension() == Level.DIMENSION_OVERWORLD ? "bed" : "respawn_anchor")
                        + ".notValid"));
            }
        } else { // level spawn point
            playerRespawnEvent.setRespawnPosition(
                    this.getServer().getDefaultLevel().getSafeSpawn());
        }

        playerRespawnEvent.call();
        Position respawnPos = playerRespawnEvent.getRespawnPosition();

        this.sendExperience();
        this.sendExperienceLevel();

        this.setSprinting(false);
        this.setSneaking(false);

        this.setDataProperty(new ShortEntityData(Player.DATA_AIR, 400), false);
        this.fireTicks = 0;
        this.collisionBlocks = null;
        this.deadTicks = 0;
        this.noDamageTicks = 60;

        this.removeAllEffects();
        this.setHealth(this.getMaxHealth());
        this.getFoodData().setLevel(20, 20);

        this.sendData(this);

        this.setMovementSpeed(DEFAULT_SPEED);

        this.getAdventureSettings().update();
        this.inventory.sendContents(this);
        this.inventory.sendArmorContents(this);
        this.offhandInventory.sendContents(this);
        this.teleport(
                Location.fromObject(respawnPos.add(0, this.getEyeHeight(), 0), respawnPos.getLevel()),
                TeleportCause.PLAYER_SPAWN);
        this.spawnToAll();
        this.scheduleUpdate();
    }

    @Override
    protected void checkChunks() {
        if (this.chunk == null
                || (this.chunk.getX() != ((int) this.x() >> 4) || this.chunk.getZ() != ((int) this.z() >> 4))) {
            if (this.chunk != null) {
                this.chunk.removeEntity(this);
            }
            this.chunk = this.getLevel().getChunk((int) this.x() >> 4, (int) this.z() >> 4, true);

            if (!this.justCreated) {
                Map<Integer, Player> newChunk =
                        this.getLevel().getChunkPlayers((int) this.x() >> 4, (int) this.z() >> 4);
                newChunk.remove(this.getLoaderId());

                // List<Player> reload = new ArrayList<>();
                for (Player player : new ArrayList<>(this.hasSpawned.values())) {
                    if (!newChunk.containsKey(player.getLoaderId())) {
                        this.despawnFrom(player);
                    } else {
                        newChunk.remove(player.getLoaderId());
                        // reload.add(player);
                    }
                }

                for (Player player : newChunk.values()) {
                    this.spawnTo(player);
                }
            }

            if (this.chunk == null) {
                return;
            }

            this.chunk.addEntity(this);
        }
    }

    protected void sendPlayStatus(int status) {
        sendPlayStatus(status, false);
    }

    protected void sendPlayStatus(int status, boolean immediate) {
        PlayStatusPacket pk = new PlayStatusPacket();
        pk.status = status;

        if (immediate) {
            this.sendPacketImmediately(pk);
        } else {
            this.sendPacket(pk);
        }
    }

    protected void forceSendEmptyChunks() {
        int chunkPositionX = this.getFloorX() >> 4;
        int chunkPositionZ = this.getFloorZ() >> 4;
        for (int x = -chunkRadius; x < chunkRadius; x++) {
            for (int z = -chunkRadius; z < chunkRadius; z++) {
                LevelChunkPacket chunk = new LevelChunkPacket();
                chunk.chunkX = chunkPositionX + x;
                chunk.chunkZ = chunkPositionZ + z;
                chunk.data = EmptyArrays.EMPTY_BYTES;
                this.sendPacket(chunk);
            }
        }
    }

    protected void removeWindow(Inventory inventory, boolean isResponse) {
        inventory.close(this);
        if (isResponse && !this.permanentWindows.contains(this.getWindowId(inventory))) {
            this.windows.remove(inventory);
            updateTrackingPositions(true);
        }
    }

    protected void addDefaultWindows() {
        this.addWindow(this.getInventory(), ContainerIds.INVENTORY, true, true);

        this.UIInventory = new PlayerUIInventory(this);
        this.addWindow(this.UIInventory, ContainerIds.UI, true);
        this.addWindow(this.offhandInventory, ContainerIds.OFFHAND, true, true);

        this.craftingGrid = this.UIInventory.getCraftingGrid();
        this.addWindow(this.craftingGrid, ContainerIds.NONE, true);

        // TODO: more windows
    }

    @Override
    protected float getBaseOffset() {
        return super.getBaseOffset();
    }

    @Override
    protected void onBlock(Entity entity, EntityDamageEvent e, boolean animate) {
        super.onBlock(entity, e, animate);
        if (e.isBreakShield()) {
            this.setNoShieldTicks(e.getShieldBreakCoolDown());
            this.setItemCoolDown(e.getShieldBreakCoolDown(), "shield");
        }
        if (animate) {
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_BLOCKED_USING_DAMAGED_SHIELD, true);
            this.getServer().getScheduler().scheduleTask(null, () -> {
                if (this.isOnline()) {
                    this.setDataFlag(DATA_FLAGS, DATA_FLAG_BLOCKED_USING_DAMAGED_SHIELD, false);
                }
            });
        }
    }

    @Override
    public double getStepHeight() {
        return 0.6f;
    }

    /**
     * 设置{@link Player#startActionTick}值为{@link Server#getTick() getTick()}
     * <p>
     * Set the {@link Player#startActionTick} value to {@link Server#getTick() getTick()}
     */
    public void startAction() {
        this.startActionTick = this.server.getTick();
    }

    /**
     * 设置{@link Player#startActionTick}值为-1
     * <p>
     * Set the {@link Player#startActionTick} value to -1
     */
    public void stopAction() {
        this.startActionTick = -1;
    }

    /**
     * Returns the value of {@link Player#lastEnderPearl}
     *
     * @return int
     */
    public int getLastEnderPearlThrowingTick() {
        return lastEnderPearl;
    }

    /**
     * Set {@link Player#lastEnderPearl} value to {@link Server#getTick() getTick()}
     */
    public void onThrowEnderPearl() {
        this.lastEnderPearl = server.getTick();
    }

    /**
     * Returns the value of {@link Player#lastChorusFruitTeleport}
     *
     * @return int
     */
    public int getLastChorusFruitTeleport() {
        return lastChorusFruitTeleport;
    }

    /**
     * Set {@link Player#lastChorusFruitTeleport} value to {@link Server#getTick() getTick()}
     */
    public void onChorusFruitTeleport() {
        this.lastChorusFruitTeleport = server.getTick();
    }

    /**
     * Returns the value of {@link Player#viewingEnderChest}, which is only valid when the player opens the Ender Chest.
     */
    public BlockEnderChest getViewingEnderChest() {
        return viewingEnderChest;
    }

    /**
     * Set the {@link Player#viewingEnderChest} value to chest
     *
     * @param chest BlockEnderChest
     */
    public void setViewingEnderChest(BlockEnderChest chest) {
        if (chest == null && this.viewingEnderChest != null) {
            this.viewingEnderChest.getViewers().remove(this);
        } else if (chest != null) {
            chest.getViewers().add(this);
        }
        this.viewingEnderChest = chest;
    }

    /**
     * Getting the message that a player has left
     *
     * @return {@link TranslationContainer}
     */
    public TranslationContainer getLeaveMessage() {
        return new TranslationContainer(TextFormat.YELLOW + "%multiplayer.player.left", this.getDisplayName());
    }

    @Override
    public boolean isBanned() {
        return this.server.getNameBans().isBanned(this.getName());
    }

    @Override
    public void setBanned(boolean value) {
        if (value) {
            this.server.getNameBans().addBan(this.getName(), null, null, null);
            this.kick(PlayerKickEvent.Reason.NAME_BANNED, "Banned by admin");
        } else {
            this.server.getNameBans().remove(this.getName());
        }
    }

    @Override
    public boolean isWhitelisted() {
        return this.server.isWhitelisted(this.getName().toLowerCase());
    }

    @Override
    public void setWhitelisted(boolean value) {
        if (value) {
            this.server.addWhitelist(this.getName().toLowerCase());
        } else {
            this.server.removeWhitelist(this.getName().toLowerCase());
        }
    }

    @Override
    public Player getPlayer() {
        return this;
    }

    @Override
    public Long getFirstPlayed() {
        return this.namedTag != null ? this.namedTag.getLong("firstPlayed") : null;
    }

    @Override
    public Long getLastPlayed() {
        return this.namedTag != null ? this.namedTag.getLong("lastPlayed") : null;
    }

    @Override
    public boolean isPlayedBefore() {
        return playerConnection.isPlayedBefore();
    }

    /**
     * Get player permission settings.
     */
    public AdventureSettings getAdventureSettings() {
        return adventureSettings;
    }

    /**
     * Used to set player permissions, corresponding to the game's player permissions settings.
     *
     * @param adventureSettings player permissions settings
     */
    public void setAdventureSettings(AdventureSettings adventureSettings) {
        this.adventureSettings = adventureSettings.clone(this);
        this.adventureSettings.update();
    }

    /**
     * Set {@link #inAirTicks} to 0
     */
    public void resetInAirTicks() {
        this.inAirTicks = 0;
    }

    public void setAllowFlight(boolean value) {
        this.getAdventureSettings().set(Type.ALLOW_FLIGHT, value);
        this.getAdventureSettings().update();
    }

    public boolean getAllowFlight() {
        return this.getAdventureSettings().get(Type.ALLOW_FLIGHT);
    }

    /**
     * Set allow to modify the world (after the unknown reason setting is completed,
     * the player is not allowed to dig the blocks, but can place them)
     *
     * @param value Whether to allow modification of the world
     */
    public void setAllowModifyWorld(boolean value) {
        this.getAdventureSettings().set(Type.WORLD_IMMUTABLE, !value);
        this.getAdventureSettings().set(Type.BUILD, value);
        this.getAdventureSettings().set(Type.WORLD_BUILDER, value);
        this.getAdventureSettings().update();
    }

    public void setAllowInteract(boolean value) {
        setAllowInteract(value, value);
    }

    /**
     * Setting up the allowed interaction world/container
     *
     * @param value      Whether to allow interactive worlds
     * @param containers Whether to allow interaction containers
     */
    public void setAllowInteract(boolean value, boolean containers) {
        this.getAdventureSettings().set(Type.WORLD_IMMUTABLE, !value);
        this.getAdventureSettings().set(Type.DOORS_AND_SWITCHED, value);
        this.getAdventureSettings().set(Type.OPEN_CONTAINERS, containers);
        this.getAdventureSettings().update();
    }

    public void setAutoJump(boolean value) {
        this.getAdventureSettings().set(Type.AUTO_JUMP, value);
        this.getAdventureSettings().update();
    }

    public boolean hasAutoJump() {
        return this.getAdventureSettings().get(Type.AUTO_JUMP);
    }

    @Override
    public void spawnTo(Player player) {
        if (this.isSpawned()
                && player.isSpawned()
                && this.isAlive()
                && player.getLevel() == this.getLevel()
                && player.canSee(this) /* && !this.isSpectator()*/) {
            super.spawnTo(player);

            if (this.isSpectator()) {
                // Sends the spectator's game mode to the other side so that the other
                // client renders the player entity correctly
                var pk = new UpdatePlayerGameTypePacket();
                pk.gameType = GameType.SPECTATOR;
                pk.entityId = this.getId();
                player.sendPacket(pk);
            }
        }
    }

    @Override
    public Server getServer() {
        return this.server;
    }

    /**
     * @param player Player
     * @return Whether the player can be seen
     */
    public boolean canSee(Player player) {
        return !this.hiddenPlayers.containsKey(player.getUniqueId());
    }

    /**
     * Hide the specified player from the view of the current player instance
     *
     * @param player Players who want to hide
     */
    public void hidePlayer(Player player) {
        if (this == player) {
            return;
        }
        this.hiddenPlayers.put(player.getUniqueId(), player);
        player.despawnFrom(this);
    }

    /**
     * Show the specified player from the view of the current player instance
     *
     * @param player Players who want to show
     */
    public void showPlayer(Player player) {
        if (this == player) {
            return;
        }
        this.hiddenPlayers.remove(player.getUniqueId());
        if (player.isOnline()) {
            player.spawnTo(this);
        }
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    @Override
    public boolean canCollide() {
        return !this.isSpectator();
    }

    @Override
    public void resetFallDistance() {
        super.resetFallDistance();
        if (this.inAirTicks != 0) {
            this.startAirTicks = 5;
        }
        this.inAirTicks = 0;
        this.highestPosition = this.y();
    }

    @Override
    public boolean isOp() {
        return this.server.isOp(this.getName());
    }

    @Override
    public void setOp(boolean value) {
        if (value == this.isOp()) {
            return;
        }

        if (value) {
            this.server.addOp(this.getName());
        } else {
            this.server.removeOp(this.getName());
        }
    }

    @Override
    public boolean isPermissionSet(String name) {
        return this.perm.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        return this.perm.isPermissionSet(permission);
    }

    @Override
    public boolean hasPermission(String name) {
        return this.perm != null && this.perm.hasPermission(name);
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return this.perm.hasPermission(permission);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return this.addAttachment(plugin, null);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name) {
        return this.addAttachment(plugin, name, null);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, Boolean value) {
        return this.perm.addAttachment(plugin, name, value);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        this.perm.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        this.server.getPluginManager().unsubscribeFromPermission(Server.BROADCAST_CHANNEL_USERS, this);
        this.server.getPluginManager().unsubscribeFromPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE, this);

        if (this.perm == null) {
            return;
        }

        this.perm.recalculatePermissions();

        if (this.hasPermission(Server.BROADCAST_CHANNEL_USERS)) {
            this.server.getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_USERS, this);
        }

        if (this.hasPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE)) {
            this.server.getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE, this);
        }

        if (this.isEnableClientCommand() && isSpawned()) this.sendCommandData();
    }

    public boolean isEnableClientCommand() {
        return this.enableClientCommand;
    }

    public void setEnableClientCommand(boolean enable) {
        this.enableClientCommand = enable;
        SetCommandsEnabledPacket pk = new SetCommandsEnabledPacket();
        pk.enabled = enable;
        this.sendPacket(pk);
        if (enable) this.sendCommandData();
    }

    public void sendCommandData() {
        if (!isSpawned()) {
            return;
        }
        AvailableCommandsPacket pk = new AvailableCommandsPacket();
        Map<String, CommandDataVersions> data = new HashMap<>();
        int count = 0;
        for (Command command : this.server.getCommandMap().getCommands().values()) {
            if (!command.testPermissionSilent(this) || !command.isRegistered() || command.isServerSideOnly()) {
                continue;
            }
            ++count;
            CommandDataVersions data0 = command.generateCustomCommandData(this);
            data.put(command.getName(), data0);
        }
        if (count > 0) {
            // TODO: structure checking
            pk.commands = data;
            this.sendPacket(pk);
        }
    }

    @Override
    public Map<String, PermissionAttachmentInfo> getEffectivePermissions() {
        return this.perm.getEffectivePermissions();
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    @Override
    public Player asPlayer() {
        return this;
    }

    @Override
    public boolean isEntity() {
        return true;
    }

    @Override
    public Entity asEntity() {
        return this;
    }

    public void removeAchievement(String achievementId) {
        achievements.remove(achievementId);
    }

    public boolean hasAchievement(String achievementId) {
        return achievements.contains(achievementId);
    }

    /**
     * 得到该玩家的显示名称
     * <p>
     * Get the display name of the player
     *
     * @return {@link #displayName}
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * 只是改变玩家聊天时和在服务器玩家列表中的显示名(不影响命令的玩家参数名,也不影响玩家头顶显示名称)
     * <p>
     * Just change the name displayed during player chat and in the server player list  (Does not affect the player parameter name of the command, nor does it affect the player header display name)
     *
     * @param displayName 显示名称
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        if (this.isSpawned()) {
            server.getPlayerManager()
                    .updatePlayerListData(
                            this.getUniqueId(),
                            this.getId(),
                            this.getDisplayName(),
                            this.getSkin(),
                            this.getPlayerInfo().getXuid());
        }
    }

    @Override
    public void setSkin(Skin skin) {
        super.setSkin(skin);
        if (this.isSpawned()) {
            var skinPacket = new PlayerSkinPacket();
            skinPacket.uuid = this.getUniqueId();
            skinPacket.skin = this.getSkin();
            skinPacket.newSkinName = this.getSkin().getSkinId();
            skinPacket.oldSkinName = "";
            Server.broadcastPacket(server.getPlayerManager().getOnlinePlayers().values(), skinPacket);
        }
    }

    /**
     * Get the position where the next tick client player will move
     *
     * @return the next position
     */
    public Position getNextPosition() {
        return this.newPosition != null
                ? new Position(this.newPosition.x(), this.newPosition.y(), this.newPosition.z(), this.getLevel())
                : this.getPosition();
    }

    /**
     * Returns whether the player is currently using an item (right-click and hold).
     *
     * @return {@link #startActionTick}
     */
    public boolean isUsingItem() {
        return this.getDataFlag(DATA_FLAGS, DATA_FLAG_ACTION) && this.startActionTick > -1;
    }

    /**
     * Set whether the player is currently using an item {@link #startActionTick} (right-click and hold).
     *
     * @param value Whether the player is currently using an item.
     */
    public void setUsingItem(boolean value) {
        this.startActionTick = value ? this.server.getTick() : -1;
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_ACTION, value);
    }

    /**
     * Get the language hardcoded for the interaction buttons that appear when mobile device players face the carrier.
     */
    public String getButtonText() {
        return this.buttonText;
    }

    /**
     * Set the language hardcoded for the interaction buttons that appear when mobile device players face the carrier.
     */
    public void setButtonText(String text) {
        this.buttonText = text;
        this.setDataProperty(new StringEntityData(Entity.DATA_INTERACTIVE_TAG, this.buttonText));
    }

    public void unloadChunk(int x, int z) {
        this.unloadChunk(x, z, null);
    }

    public void unloadChunk(int x, int z, Level level) {
        level = level == null ? this.getLevel() : level;
        long index = Level.chunkHash(x, z);
        if (this.usedChunks.containsKey(index)) {
            for (Entity entity : level.getChunkEntities(x, z).values()) {
                if (entity != this) {
                    entity.despawnFrom(this);
                }
            }

            this.usedChunks.remove(index);
        }
        level.unregisterChunkLoader(this, x, z);
        this.loadQueue.remove(index);
    }

    /**
     * @return Is the player in the world (Dimension equal 0)
     */
    public boolean isInOverWorld() {
        return this.getLevel().getDimension() == 0;
    }

    /**
     * Get the player's Spawn point
     *
     * @return {@link Position}
     */
    public Position getSpawn() {
        // level spawn point < block spawn = self spawn
        if (spawnPosition != null && spawnBlockPosition == null) { // self spawn
            return spawnPosition;
        } else if (spawnBlockPosition != null && spawnPosition == null) { // block spawn
            return spawnBlockPosition;
        } else { // level spawn point
            return this.getServer().getDefaultLevel().getSafeSpawn();
        }
    }

    /**
     * Set the player's birth point.
     *
     * @param pos Location of the birth point
     */
    public void setSpawn(@Nullable Vector3 pos) {
        if (pos != null) {
            Level level;
            if (pos instanceof Position position && position.isValid()) {
                level = position.getLevel();
            } else {
                level = this.getLevel();
            }
            this.spawnPosition = new Position(pos.x(), pos.y(), pos.z(), level);
            this.spawnBlockPosition = null;
            SetSpawnPositionPacket pk = new SetSpawnPositionPacket();
            pk.spawnType = SetSpawnPositionPacket.TYPE_PLAYER_SPAWN;
            pk.x = (int) this.spawnPosition.x();
            pk.y = (int) this.spawnPosition.y();
            pk.z = (int) this.spawnPosition.z();
            pk.dimension = this.spawnPosition.getLevel().getDimension();
            this.sendPacket(pk);
        } else {
            this.spawnPosition = null;
        }
    }

    /**
     * Sets the position of the block that holds the player respawn position. May be null when unknown.
     *
     * @param spawnBlock The position of a bed or respawn anchor
     */
    public void setSpawnBlock(@Nullable Vector3 spawnBlock) {
        if (spawnBlock == null) {
            this.spawnBlockPosition = null;
        } else {
            Level level;
            if (spawnBlock instanceof Position position && position.isValid()) {
                level = position.getLevel();
            } else {
                level = this.getLevel();
            }
            this.spawnBlockPosition = new Position(spawnBlock.x(), spawnBlock.y(), spawnBlock.z(), level);
            this.spawnPosition = null;
            SetSpawnPositionPacket pk = new SetSpawnPositionPacket();
            pk.spawnType = SetSpawnPositionPacket.TYPE_PLAYER_SPAWN;
            pk.x = this.spawnBlockPosition.getFloorX();
            pk.y = this.spawnBlockPosition.getFloorY();
            pk.z = this.spawnBlockPosition.getFloorZ();
            pk.dimension = this.spawnBlockPosition.getLevel().getDimension();
            this.sendPacket(pk);
        }
    }

    public void sendChunk(int x, int z, DataPacket packet) {
        if (!this.isConnected()) {
            return;
        }

        this.usedChunks.put(Level.chunkHash(x, z), Boolean.TRUE);
        this.chunkLoadCount++;

        this.sendPacket(packet);

        if (this.isSpawned()) {
            for (Entity entity : this.getLevel().getChunkEntities(x, z).values()) {
                if (this != entity && !entity.closed && entity.isAlive()) {
                    entity.spawnTo(this);
                }
            }
        }

        if (this.needDimensionChangeACK) {
            this.needDimensionChangeACK = false;

            PlayerActionPacket playerActionPacket = new PlayerActionPacket();
            playerActionPacket.action = PlayerActionPacket.ACTION_DIMENSION_CHANGE_ACK;
            playerActionPacket.entityId = this.getId();
            this.sendPacket(playerActionPacket);
        }
    }

    public void sendChunk(int x, int z, int subChunkCount, byte[] payload) {
        LevelChunkPacket pk = new LevelChunkPacket();
        pk.chunkX = x;
        pk.chunkZ = z;
        pk.subChunkCount = subChunkCount;
        pk.data = payload;

        this.sendChunk(x, z, pk);
    }

    public void updateTrackingPositions() {
        updateTrackingPositions(false);
    }

    public void updateTrackingPositions(boolean delayed) {
        Server server = getServer();
        if (delayed) {
            if (delayedPosTrackingUpdate != null) {
                delayedPosTrackingUpdate.cancel();
            }
            delayedPosTrackingUpdate =
                    server.getScheduler().scheduleDelayedTask(null, this::updateTrackingPositions, 10);
            return;
        }
        PositionTrackingService positionTrackingService = server.getPositionTrackingService();
        positionTrackingService.forceRecheck(this);
    }

    /**
     * Send data packet
     *
     * @param packet Packet to send
     * @return Packet successfully sent
     */
    public boolean sendPacket(DataPacket packet) {
        return playerConnection != null && playerConnection.sendPacket(packet);
    }

    public void sendPacketImmediately(DataPacket packet, Runnable callback) {
        if (playerConnection != null) playerConnection.sendPacketImmediately(packet, callback);
    }

    public boolean sendPacketImmediately(DataPacket packet) {
        return playerConnection != null && playerConnection.sendPacketImmediately(packet);
    }

    public boolean sendResourcePacket(DataPacket packet) {
        return playerConnection != null && playerConnection.sendResourcePacket(packet);
    }

    public boolean sleepOn(Vector3 pos) {
        if (!this.isOnline()) {
            return false;
        }

        for (Entity p : this.getLevel().getNearbyEntities(this.boundingBox.grow(2, 1, 2), this)) {
            if (p instanceof Player) {
                if (((Player) p).sleeping != null && pos.distance(((Player) p).sleeping) <= 0.1) {
                    return false;
                }
            }
        }

        PlayerBedEnterEvent event =
                new PlayerBedEnterEvent(this, this.getLevel().getBlock(pos));
        event.call();

        if (event.isCancelled()) {
            return false;
        }

        this.sleeping = pos.clone();
        this.teleport(
                new Location(pos.x() + 0.5, pos.y() + 0.5, pos.z() + 0.5, this.yaw(), this.pitch(), this.getLevel()),
                null);

        this.setDataProperty(
                new IntPositionEntityData(DATA_PLAYER_BED_POSITION, (int) pos.x(), (int) pos.y(), (int) pos.z()));
        this.setDataFlag(DATA_PLAYER_FLAGS, DATA_PLAYER_FLAG_SLEEP, true);
        this.setSpawnBlock(Position.fromObject(pos, getLevel()));
        this.getLevel().sleepTicks = 60;

        this.timeSinceRest = 0;

        return true;
    }

    public void stopSleep() {
        if (this.sleeping != null) {
            PlayerBedLeaveEvent event =
                    new PlayerBedLeaveEvent(this, this.getLevel().getBlock(this.sleeping));
            event.call();

            this.sleeping = null;
            this.setDataProperty(new IntPositionEntityData(DATA_PLAYER_BED_POSITION, 0, 0, 0));
            this.setDataFlag(DATA_PLAYER_FLAGS, DATA_PLAYER_FLAG_SLEEP, false);

            this.getLevel().sleepTicks = 0;

            AnimatePacket pk = new AnimatePacket();
            pk.eid = this.id;
            pk.action = AnimatePacket.Action.WAKE_UP;
            this.sendPacket(pk);
        }
    }

    /**
     * Whether the player is sleeping
     *
     * @return boolean
     */
    public boolean isSleeping() {
        return this.sleeping != null;
    }

    /**
     * Sets the provided gamemode
     */
    public boolean setGamemode(GameMode gamemode) {
        return this.setGamemode(gamemode, false, false);
    }

    /**
     * Sets the provided gamemode
     *
     * @param gamemode    Player game mode to be set
     * @param serverSide  Whether to update the server-side player game mode only. If true, no game mode update packet will be sent to the client side
     * @param forceUpdate Whether to force an update. If true, will uncheck the formal parameter 'gamemode'
     */
    public boolean setGamemode(GameMode gamemode, boolean serverSide, boolean forceUpdate) {
        if (!forceUpdate && this.gamemode.equals(gamemode)) {
            return false;
        }

        AdventureSettings newSettings = this.getAdventureSettings().clone(this);
        newSettings.set(Type.WORLD_IMMUTABLE, (gamemode.ordinal() & 0x02) > 0);
        newSettings.set(Type.BUILD, (gamemode.ordinal() & 0x02) <= 0);
        newSettings.set(Type.WORLD_BUILDER, (gamemode.ordinal() & 0x02) <= 0);
        newSettings.set(Type.ALLOW_FLIGHT, (gamemode.ordinal() & 0x01) > 0);
        newSettings.set(Type.NO_CLIP, gamemode == GameMode.SPECTATOR);
        newSettings.set(
                Type.FLYING,
                switch (gamemode) {
                    case SURVIVAL -> false;
                    case CREATIVE -> newSettings.get(Type.FLYING);
                    case ADVENTURE -> false;
                    case SPECTATOR -> true;
                });

        PlayerGameModeChangeEvent event = new PlayerGameModeChangeEvent(this, gamemode, newSettings);
        event.call();

        if (event.isCancelled()) {
            return false;
        }

        this.gamemode = gamemode;

        if (this.isSpectator()) {
            this.onGround = false;
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_HAS_COLLISION, false);
        } else {
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_HAS_COLLISION, true);
        }

        this.namedTag.putInt("playerGameType", gamemode.ordinal());

        this.setAdventureSettings(event.getNewAdventureSettings());

        if (!serverSide) {
            UpdatePlayerGameTypePacket packet = new UpdatePlayerGameTypePacket();
            packet.gameType = GameType.from(gamemode.getNetworkGamemode());
            packet.entityId = this.getId();
            Set<Player> players =
                    Sets.newHashSet(server.getPlayerManager().getOnlinePlayers().values());
            // Instead of sending an UpdatePlayerGameTypePacket to itself, we'll use the SetPlayerGameTypePacket
            players.remove(this);
            // We need to send this packet to all players to enable the player client to render the player entity
            // correctly
            // eg: observer mode players are not visible to players with gm 0 1 2
            Server.broadcastPacket(players, packet);
            // We use SetPlayerGameTypePacket to ensure compatibility with WaterDog!
            SetPlayerGameTypePacket gameTypePacket = new SetPlayerGameTypePacket();
            gameTypePacket.gamemode = gamemode.getNetworkGamemode();
            this.sendPacket(gameTypePacket);
        }

        this.resetFallDistance();
        return true;
    }

    public void sendSettings() {
        this.getAdventureSettings().update();
    }

    /**
     * Whether the player is in survival mode?
     */
    public boolean isSurvival() {
        return gamemode.equals(GameMode.SURVIVAL);
    }

    /**
     * Whether the player is in creative mode?
     */
    public boolean isCreative() {
        return gamemode.equals(GameMode.CREATIVE);
    }

    /**
     * Whether the player is in adventure mode?
     */
    public boolean isAdventure() {
        return gamemode.equals(GameMode.ADVENTURE);
    }

    /**
     * Whether the player is in spectator mode?
     */
    public boolean isSpectator() {
        return gamemode.equals(GameMode.SPECTATOR);
    }

    @Override
    public Item[] getDrops() {
        if (!this.isCreative() && !this.isSpectator()) {
            return super.getDrops();
        }
        return Item.EMPTY_ARRAY;
    }

    @Override
    public boolean fastMove(double dx, double dy, double dz) {
        this.setX(x() + dx);
        this.setY(y() + dy);
        this.setZ(z() + dz);
        this.recalculateBoundingBox();

        this.checkChunks();

        if (!this.isSpectator()) {
            if (!this.onGround || dy != 0) {
                AxisAlignedBB bb = this.boundingBox.clone();
                bb.setMinY(bb.getMinY() - 0.75);
                this.onGround = this.getLevel().getCollisionBlocks(bb).length > 0;
            }
            this.isCollided = this.onGround;
            this.updateFallState(this.onGround);
        }
        return true;
    }

    public AxisAlignedBB reCalcOffsetBoundingBox() {
        float dx = this.getWidth() / 2;
        float dz = this.getWidth() / 2;
        return this.offsetBoundingBox.setBounds(
                this.x() - dx, this.y(), this.z() - dz, this.x() + dx, this.y() + this.getHeight(), this.z() + dz);
    }

    @Override
    public void addMovement(double x, double y, double z, double yaw, double pitch, double headYaw) {
        this.sendPosition(
                new Vector3(x, y, z),
                yaw,
                pitch,
                MovePlayerPacket.MODE_NORMAL,
                this.getViewers().values().toArray(EMPTY_ARRAY));
    }

    /**
     * Each call to this method sends a motion packet to the client. If called multiple times,
     * motion will be overlaid on the client side
     *
     * @param motion A motion vector
     * @return Whether the call was successful or not
     */
    @Override
    public boolean setMotion(Vector3 motion) {
        if (super.setMotion(motion)) {
            if (this.chunk != null) {
                this.addMotion(this.motionX, this.motionY, this.motionZ); // Send to others
                SetEntityMotionPacket pk = new SetEntityMotionPacket();
                pk.eid = this.id;
                pk.motionX = (float) motion.x();
                pk.motionY = (float) motion.y();
                pk.motionZ = (float) motion.z();
                this.sendPacket(pk); // Send to self
            }

            if (this.motionY > 0) {
                // todo: check this
                this.startAirTicks =
                        (int) ((-(Math.log(this.getGravity() / (this.getGravity() + this.getDrag() * this.motionY)))
                                                / this.getDrag())
                                        * 2
                                + 5);
            }

            return true;
        }

        return false;
    }

    public void sendDefaultAttributes() {
        UpdateAttributesPacket pk = new UpdateAttributesPacket();
        pk.entityId = this.getId();
        pk.entries = new Attribute[] {
            Attribute.getAttribute(Attribute.MAX_HEALTH)
                    .setMaxValue(this.getMaxHealth())
                    .setValue(health > 0 ? (health < getMaxHealth() ? health : getMaxHealth()) : 0),
            Attribute.getAttribute(Attribute.MAX_HUNGER)
                    .setValue(this.getFoodData().getLevel()),
            Attribute.getAttribute(Attribute.MOVEMENT_SPEED).setValue(this.getMovementSpeed()),
            Attribute.getAttribute(Attribute.EXPERIENCE_LEVEL).setValue(this.getExperienceLevel()),
            Attribute.getAttribute(Attribute.EXPERIENCE)
                    .setValue(((float) this.getExperience()) / calculateRequireExperience(this.getExperienceLevel()))
        };
        this.sendPacket(pk);
    }

    /**
     * Sending Mist Settings to the Client
     */
    public void sendFogStack() {
        var pk = new PlayerFogPacket();
        pk.setFogStack(this.fogStack);
        pk.encode();
        this.sendPacket(pk);
    }

    public void sendCameraPresets() {
        var pk = new CameraPresetsPacket();
        pk.getPresets().addAll(CameraPreset.getPresets().values());
        sendPacket(pk);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (!this.isLoggedIn()) {
            return false;
        }

        int tickDiff = currentTick - this.lastUpdate;

        if (tickDiff <= 0) {
            return true;
        }

        this.messageCounter = 2;

        this.lastUpdate = currentTick;

        if (this.fishing != null && this.server.getTick() % 20 == 0) {
            if (this.distance(fishing) > 33) {
                this.stopFishing(false);
            }
        }

        if (!this.isAlive() && this.isSpawned()) {
            if (this.getLevel().getGameRules().getBoolean(GameRule.DO_IMMEDIATE_RESPAWN)) {
                this.despawnFromAll();
                return true;
            }
            ++this.deadTicks;
            if (this.deadTicks >= 10) {
                this.despawnFromAll();
            }
            return true;
        }

        if (this.isSpawned()) {
            if (this.motionX != 0 || this.motionY != 0 || this.motionZ != 0) {
                this.setMotion(new Vector3(motionX, motionY, motionZ));
                motionX = motionY = motionZ = 0;
            }

            while (!this.clientMovements.isEmpty()) {
                this.positionChanged = true;
                this.handleMovement(this.clientMovements.poll());
            }

            if (!this.isSpectator()) {
                this.checkNearEntities();
            }

            this.entityBaseTick(tickDiff);

            if (this.getServer().getDifficulty() == 0
                    && this.getLevel().getGameRules().getBoolean(GameRule.NATURAL_REGENERATION)) {
                if (this.getHealth() < this.getMaxHealth() && this.ticksLived % 20 == 0) {
                    this.heal(1);
                }

                PlayerFood foodData = this.getFoodData();

                if (foodData.getLevel() < 20 && this.ticksLived % 10 == 0) {
                    foodData.addFoodLevel(1, 0);
                }
            }

            if (this.isOnFire() && this.lastUpdate % 10 == 0) {
                if (this.isCreative() && !this.isInsideOfFire()) {
                    this.extinguish();
                } else if (this.getLevel().isRaining()) {
                    if (this.getLevel().canBlockSeeSky(this)) {
                        this.extinguish();
                    }
                }
            }

            if (!this.isSpectator() && this.speed != null) {
                if (this.onGround) {
                    if (this.inAirTicks != 0) {
                        this.startAirTicks = 5;
                    }
                    this.inAirTicks = 0;
                    this.highestPosition = this.y();
                    if (this.isGliding()) {
                        this.setGliding(false);
                    }
                } else {
                    this.lastInAirTick = server.getTick();
                    // 检测玩家是否异常飞行
                    if (this.checkMovement
                            && !this.isGliding()
                            && !server.getAllowFlight()
                            && !this.getAdventureSettings().get(Type.ALLOW_FLIGHT)
                            && this.inAirTicks > 20
                            && !this.isSleeping()
                            && !this.isImmobile()
                            && !this.isSwimming()
                            && this.riding == null
                            && !this.hasEffect(Effect.LEVITATION)
                            && !this.hasEffect(Effect.SLOW_FALLING)) {
                        double expectedVelocity = (-this.getGravity()) / ((double) this.getDrag())
                                - ((-this.getGravity()) / ((double) this.getDrag()))
                                        * Math.exp(-((double) this.getDrag())
                                                * ((double) (this.inAirTicks - this.startAirTicks)));
                        double diff = (this.speed.y() - expectedVelocity) * (this.speed.y() - expectedVelocity);

                        Block block = getLevel().getBlock(this);
                        int blockId = block.getId();
                        boolean ignore = blockId == Block.LADDER
                                || blockId == Block.VINE
                                || blockId == Block.COBWEB
                                || blockId == Block.SCAFFOLDING; // || (blockId == Block.SWEET_BERRY_BUSH &&
                        // block.getDamage() > 0);

                        if (!this.hasEffect(Effect.JUMP_BOOST)
                                && diff > 0.6
                                && expectedVelocity < this.speed.y()
                                && !ignore) {
                            if (this.inAirTicks < 150) {
                                this.setMotion(new Vector3(0, expectedVelocity, 0));
                            } else if (this.kick(
                                    PlayerKickEvent.Reason.FLYING_DISABLED, "Flying is not enabled on this server")) {
                                return false;
                            }
                        }
                        if (ignore) {
                            this.resetFallDistance();
                        }
                    }

                    if (this.y() > highestPosition) {
                        this.highestPosition = this.y();
                    }

                    // Wiki: 使用鞘翅滑翔时在垂直高度下降率低于每刻 0.5 格的情况下，摔落高度被重置为 1 格。
                    // Wiki: 玩家在较小的角度和足够低的速度上着陆不会受到坠落伤害。着陆时临界伤害角度为50°，伤害值等同于玩家从滑行的最高点直接摔落到着陆点受到的伤害。
                    if (this.isGliding() && Math.abs(this.speed.y()) < 0.5 && this.pitch() <= 40) {
                        this.resetFallDistance();
                    }

                    ++this.inAirTicks;
                }

                if (this.getFoodData() != null) this.getFoodData().update(tickDiff);

                // Elytra check and durability calculation
                if (this.isGliding()) {
                    PlayerInventory playerInventory = this.getInventory();
                    if (playerInventory != null) {
                        Item chestplate = playerInventory.getChestplate();
                        if ((chestplate == null || chestplate.getId() != ItemID.ELYTRA)) {
                            this.setGliding(false);
                        } else if (this.age % (20 * (chestplate.getEnchantmentLevel(Enchantment.ID_DURABILITY) + 1))
                                == 0) {
                            int newDamage = chestplate.getDamage() + 1;
                            if (newDamage < chestplate.getMaxDurability()) {
                                chestplate.setDamage(newDamage);
                                playerInventory.setChestplate(chestplate);
                            } else {
                                this.setGliding(false);
                            }
                        }
                    }
                }
            }

            if (!this.isSleeping()) {
                this.timeSinceRest++;
            }

            // Only used by server-side authorities, since client-side authorities continue break is normal.
            if (server.getServerAuthoritativeMovement() > 0) {
                playerHandle.handleBlockBreakContinue(
                        playerHandle.getBreakingBlock(), playerHandle.getBreakingBlockFace());
            }

            // reset move status
            this.newPosition = null;
            this.positionChanged = false;
            if (this.speed == null) {
                this.speed = new Vector3(0, 0, 0);
            } else {
                this.speed.setComponents(0, 0, 0);
            }
        }

        if (currentTick % 10 == 0) {
            this.checkInteractNearby();
        }

        if (this.isSpawned() && this.dummyBossBars.size() > 0 && currentTick % 100 == 0) {
            this.dummyBossBars.values().forEach(DummyBossBar::updateBossEntityPosition);
        }

        updateBlockingFlag();

        PlayerFood foodData = getFoodData();
        if (this.ticksLived % 40 == 0 && foodData != null) {
            foodData.sendFoodLevel();
        }

        return true;
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        boolean hasUpdated = false;
        if (isUsingItem()) {
            if (noShieldTicks < NO_SHIELD_DELAY) {
                noShieldTicks = NO_SHIELD_DELAY;
                hasUpdated = true;
            }
        } else {
            if (noShieldTicks > 0) {
                noShieldTicks -= tickDiff;
                hasUpdated = true;
            }
            if (noShieldTicks < 0) {
                noShieldTicks = 0;
                hasUpdated = true;
            }
        }
        return super.entityBaseTick(tickDiff) || hasUpdated;
    }

    /**
     * Check for nearby interactable entities (not generally used by plugins)
     */
    public void checkInteractNearby() {
        int interactDistance = isCreative() ? 5 : 3;
        if (canInteract(this, interactDistance)) {
            if (getEntityPlayerLookingAt(interactDistance) != null) {
                EntityInteractable onInteract = getEntityPlayerLookingAt(interactDistance);
                setButtonText(onInteract.getInteractButtonText(this));
            } else {
                setButtonText("");
            }
        } else {
            setButtonText("");
        }
    }

    /**
     * Returns the Entity the player is looking at currently
     *
     * @param maxDistance  The maximum distance to check for entities
     * @return Entity|null Either NULL if no entity is found or an instance of the entity
     */
    public EntityInteractable getEntityPlayerLookingAt(int maxDistance) {
        EntityInteractable entity = null;

        // just a fix because player MAY not be fully initialized
        if (temporalVector != null) {
            Entity[] nearbyEntities =
                    getLevel().getNearbyEntities(boundingBox.grow(maxDistance, maxDistance, maxDistance), this);

            // get all blocks in looking direction until the max interact distance is reached (it's possible that
            // startblock isn't found!)
            try {
                BlockIterator itr =
                        new BlockIterator(getLevel(), getPosition(), getDirectionVector(), getEyeHeight(), maxDistance);
                if (itr.hasNext()) {
                    Block block;
                    while (itr.hasNext()) {
                        block = itr.next();
                        entity = getEntityAtPosition(
                                nearbyEntities, block.getFloorX(), block.getFloorY(), block.getFloorZ());
                        if (entity != null) {
                            break;
                        }
                    }
                }
            } catch (Exception ex) {
                // nothing to log here!
            }
        }

        return entity;
    }

    public void checkNetwork() {
        if (!this.isOnline()) {
            return;
        }

        if (this.nextChunkOrderRun-- <= 0 || this.chunk == null) {
            this.orderChunks();
        }

        if (!this.loadQueue.isEmpty() || !this.isSpawned()) {
            this.sendNextChunk();
        }
    }

    public boolean canInteract(Vector3 pos, double maxDistance) {
        return this.canInteract(pos, maxDistance, 6.0);
    }

    public boolean canInteract(Vector3 pos, double maxDistance, double maxDiff) {
        if (this.distanceSquared(pos) > maxDistance * maxDistance) {
            return false;
        }

        Vector2 dV = this.getDirectionPlane();
        double dot = dV.dot(new Vector2(this.x(), this.z()));
        double dot1 = dV.dot(new Vector2(pos.x(), pos.z()));
        return (dot1 - dot) >= -maxDiff;
    }

    public void handleDataPacket(DataPacket packet) {
        if (!isConnected()) {
            return;
        }

        if (!playerHandle.isVerified()
                && packet.packetId() != ProtocolInfo.toNewProtocolID(ProtocolInfo.LOGIN_PACKET)
                && packet.packetId() != ProtocolInfo.toNewProtocolID(ProtocolInfo.BATCH_PACKET)
                && packet.packetId() != ProtocolInfo.toNewProtocolID(ProtocolInfo.REQUEST_NETWORK_SETTINGS_PACKET)) {
            log.warn(
                    "Ignoring {} from {} due to player not verified yet",
                    packet.getClass().getSimpleName(),
                    playerConnection.getAddress());
            if (unverifiedPackets++ > 100) {
                this.close("", "Too many failed login attempts");
            }
            return;
        }

        DataPacketReceiveEvent event = new DataPacketReceiveEvent(this, packet);
        event.call();
        if (event.isCancelled()) {
            return;
        }
        if (packet.packetId() == ProtocolInfo.toNewProtocolID(ProtocolInfo.BATCH_PACKET)) {
            List<DataPacket> dataPackets = unpackBatchedPackets((BatchPacket) packet);
            dataPackets.forEach(this::handleDataPacket);
            return;
        }
        if (log.isTraceEnabled() && !server.isIgnoredPacket(packet.getClass())) {
            log.trace("Inbound {}: {}", this.getName(), packet);
        }
        if (DataPacketManager.canProcess(packet.getProtocolUsed(), packet.packetId())) {
            DataPacketManager.processPacket(this.playerHandle, packet);
        }
    }

    /**
     * Sends a chat message as this player. If the message begins with a / (forward-slash) it will be treated
     * as a command.
     *
     * @param message Message to send
     * @return successful
     */
    public boolean chat(String message) {
        if (!this.isSpawned() || !this.isAlive()) {
            return false;
        }

        this.resetCraftingGridType();
        this.craftingType = CRAFTING_SMALL;

        if (this.removeFormat) {
            message = TextFormat.clean(message, true);
        }

        for (String msg : message.split("\n")) {
            if (!msg.trim().isEmpty() && msg.length() <= 512 && this.messageCounter-- > 0) {
                PlayerChatEvent chatEvent = new PlayerChatEvent(this, msg);
                chatEvent.call();
                if (!chatEvent.isCancelled()) {
                    this.server.broadcastMessage(
                            this.getServer()
                                    .getLanguage()
                                    .tr(
                                            chatEvent.getFormat(),
                                            chatEvent.getPlayer().getDisplayName(),
                                            chatEvent.getMessage()),
                            chatEvent.getRecipients());
                }
            }
        }

        return true;
    }

    /**
     * reason=empty string
     *
     * @see #kick(PlayerKickEvent.Reason, String, boolean)
     */
    public boolean kick() {
        return this.kick("");
    }

    /**
     * {@link PlayerKickEvent.Reason} = {@link PlayerKickEvent.Reason#UNKNOWN}
     *
     * @see #kick(PlayerKickEvent.Reason, String, boolean)
     */
    public boolean kick(String reason, boolean isAdmin) {
        return this.kick(PlayerKickEvent.Reason.UNKNOWN, reason, isAdmin);
    }

    /**
     * {@link PlayerKickEvent.Reason} = {@link PlayerKickEvent.Reason#UNKNOWN}
     *
     * @see #kick(PlayerKickEvent.Reason, String, boolean)
     */
    public boolean kick(String reason) {
        return kick(PlayerKickEvent.Reason.UNKNOWN, reason);
    }

    /**
     * @see #kick(PlayerKickEvent.Reason, String, boolean)
     */
    public boolean kick(PlayerKickEvent.Reason reason) {
        return this.kick(reason, true);
    }

    /**
     * @see #kick(PlayerKickEvent.Reason, String, boolean)
     */
    public boolean kick(PlayerKickEvent.Reason reason, String reasonString) {
        return this.kick(reason, reasonString, true);
    }

    /**
     * @see #kick(PlayerKickEvent.Reason, String, boolean)
     */
    public boolean kick(PlayerKickEvent.Reason reason, boolean isAdmin) {
        return this.kick(reason, reason.toString(), isAdmin);
    }

    /**
     * Kick out the player
     *
     * @param reason       Cause Enumeration
     * @param reasonString Reason String
     * @param isAdmin      Whether from the administrator kicked out
     * @return boolean
     */
    public boolean kick(PlayerKickEvent.Reason reason, String reasonString, boolean isAdmin) {
        PlayerKickEvent event = new PlayerKickEvent(this, reason, this.getLeaveMessage());
        event.call();

        if (!event.isCancelled()) {
            String message;
            if (isAdmin) {
                if (!this.isBanned()) {
                    message = "Kicked by admin." + (!reasonString.isEmpty() ? " Reason: " + reasonString : "");
                } else {
                    message = reasonString;
                }
            } else {
                if (reasonString.isEmpty()) {
                    message = "disconnectionScreen.noReason";
                } else {
                    message = reasonString;
                }
            }

            this.close(event.getQuitMessage(), message);

            return true;
        }

        return false;
    }

    /**
     * Set the player's viewing distance (range 0--{@link Server#getViewDistance})
     *
     * @param distance Viewing distance
     */
    public void setViewDistance(int distance) {
        this.chunkRadius = distance;

        ChunkRadiusUpdatedPacket pk = new ChunkRadiusUpdatedPacket();
        pk.radius = distance;

        this.sendPacket(pk);
    }

    /**
     * Get the player's viewing distance
     *
     * @return int
     */
    public int getViewDistance() {
        return this.chunkRadius;
    }

    @Override
    public void sendMessage(String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_RAW;
        pk.message = this.server.getLanguage().tr(message);
        this.sendPacket(pk);
    }

    public void sendCommandOutput(CommandOutputContainer container) {
        if (this.getLevel().getGameRules().getBoolean(GameRule.SEND_COMMAND_FEEDBACK)) {
            var pk = new CommandOutputPacket();
            pk.messages.addAll(container.getMessages());
            pk.commandOriginData = new CommandOriginData(
                    CommandOriginData.Origin.PLAYER, this.getUniqueId(), "", null); // Only players can effect
            pk.type = CommandOutputType.ALL_OUTPUT; // Useless
            pk.successCount = container.getSuccessCount(); // Useless,maybe used for server-client interaction
            this.sendPacket(pk);
        }
    }

    /**
     * Send a JSON text in the player chat bar
     *
     * @param text Json text
     */
    public void sendRawTextMessage(RawText text) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_OBJECT;
        pk.message = text.toRawText();
        this.sendPacket(pk);
    }

    /**
     * @see #sendTranslation(String, String[])
     */
    public void sendTranslation(String message) {
        this.sendTranslation(message, EmptyArrays.EMPTY_STRINGS);
    }

    /**
     * Send a multilingual translated text in the player chat bar, example:<br> {@code message="Test Message {%0} {%1}" parameters=["Hello", "World"]}<br>
     * actual message content {@code "Test Message Hello World"}
     *
     * @param message    Message
     * @param parameters Parameters
     */
    public void sendTranslation(String message, String[] parameters) {
        TextPacket pk = new TextPacket();
        if (!this.server.isLanguageForced()) {
            pk.type = TextPacket.TYPE_TRANSLATION;
            pk.message = this.server.getLanguage().tr(message, parameters, "nukkit.", true);
            for (int i = 0; i < parameters.length; i++) {
                parameters[i] = this.server.getLanguage().tr(parameters[i], parameters, "nukkit.", true);
            }
            pk.parameters = parameters;
        } else {
            pk.type = TextPacket.TYPE_RAW;
            pk.message = this.server.getLanguage().tr(message, parameters);
        }
        this.sendPacket(pk);
    }

    @Override
    public void sendMessage(TextContainer message) {
        if (message instanceof TranslationContainer) {
            this.sendTranslation(message.getText(), ((TranslationContainer) message).getParameters());
            return;
        }
        this.sendMessage(message.getText());
    }

    /**
     * Send a pop-up text above the player's item bar
     *
     * @param message Message
     */
    public void sendPopup(String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_POPUP;
        pk.message = message;
        this.sendPacket(pk);
    }

    /**
     * Send a tip text above the player's item bar
     *
     * @param message Message
     */
    public void sendTip(String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_TIP;
        pk.message = message;
        this.sendPacket(pk);
    }

    /**
     * Clears away the title info being displayed on the player.
     */
    public void clearTitle() {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_CLEAR;
        this.sendPacket(pk);
    }

    /**
     * Resets both title animation times and subtitle for the next shown title.
     */
    public void resetTitleSettings() {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_RESET;
        this.sendPacket(pk);
    }

    /**
     * Set subtitle to be displayed below the main title.
     *
     * @param subtitle Subtitle
     */
    public void setSubtitle(String subtitle) {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_SUBTITLE;
        pk.text = subtitle;
        this.sendPacket(pk);
    }

    /**
     * Set a JSON text subtitle.
     *
     * @param text JSON text
     */
    public void setRawTextSubTitle(RawText text) {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_SUBTITLE_JSON;
        pk.text = text.toRawText();
        this.sendPacket(pk);
    }

    /**
     * Set title animation time
     *
     * @param fadein   fade-in time
     * @param duration duration
     * @param fadeout  fade-out time
     */
    public void setTitleAnimationTimes(int fadein, int duration, int fadeout) {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_ANIMATION_TIMES;
        pk.fadeInTime = fadein;
        pk.stayTime = duration;
        pk.fadeOutTime = fadeout;
        this.sendPacket(pk);
    }

    /**
     * Set a JSON text title.
     *
     * @param text JSON text
     */
    public void setRawTextTitle(RawText text) {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_TITLE_JSON;
        pk.text = text.toRawText();
        this.sendPacket(pk);
    }

    private void setTitle(String text) {
        SetTitlePacket packet = new SetTitlePacket();
        packet.text = text;
        packet.type = SetTitlePacket.TYPE_TITLE;
        this.sendPacket(packet);
    }

    /**
     * {@code subtitle=null,fadeIn=20,stay=20,fadeOut=5}
     *
     * @see #sendTitle(String, String, int, int, int)
     */
    public void sendTitle(String title) {
        this.sendTitle(title, null);
    }

    /**
     * {@code fadeIn=20,stay=20,fadeOut=5}
     *
     * @see #sendTitle(String, String, int, int, int)
     */
    public void sendTitle(String title, String subtitle) {
        this.sendTitle(title, subtitle, 20, 20, 5);
    }

    /**
     * Send a title text in the center of the player's view.
     *
     * @param title    title
     * @param subtitle subtitle
     * @param fadeIn   fade-in time(tick)
     * @param stay     stay time
     * @param fadeOut  fade-out time
     */
    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        this.setTitleAnimationTimes(fadeIn, stay, fadeOut);
        if (!Strings.isNullOrEmpty(subtitle)) {
            this.setSubtitle(subtitle);
        }
        // title won't send if an empty string is used.
        this.setTitle(Strings.isNullOrEmpty(title) ? " " : title);
    }

    /**
     * fadein=1,duration=0,fadeout=1
     *
     * @see #sendActionBar(String, int, int, int)
     */
    public void sendActionBar(String title) {
        this.sendActionBar(title, 1, 0, 1);
    }

    /**
     * Send a ActionBar text above the player's item bar.
     *
     * @param title    title
     * @param fadein   fade-in time
     * @param duration duration
     * @param fadeout  fade-out time
     */
    public void sendActionBar(String title, int fadein, int duration, int fadeout) {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_ACTION_BAR;
        pk.text = title;
        pk.fadeInTime = fadein;
        pk.stayTime = duration;
        pk.fadeOutTime = fadeout;
        this.sendPacket(pk);
    }

    /**
     * fadein=1,duration=0,fadeout=1
     *
     * @see #setRawTextActionBar(RawText, int, int, int)
     */
    public void setRawTextActionBar(RawText text) {
        this.setRawTextActionBar(text, 1, 0, 1);
    }

    /**
     * Set a JSON ActionBar text.
     *
     * @param text     JSON text
     * @param fadein   fade-in time
     * @param duration duration
     * @param fadeout  fade-out time
     */
    public void setRawTextActionBar(RawText text, int fadein, int duration, int fadeout) {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_ACTIONBAR_JSON;
        pk.text = text.toRawText();
        pk.fadeInTime = fadein;
        pk.stayTime = duration;
        pk.fadeOutTime = fadeout;
        this.sendPacket(pk);
    }

    public void sendJukeboxPopup(String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_JUKEBOX_POPUP;
        pk.message = message;
        this.sendPacket(pk);
    }

    public void sendWhisper(String message) {
        this.sendWhisper("", message);
    }

    public void sendWhisper(String source, String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_WHISPER;
        pk.source = source;
        pk.message = message;
        this.sendPacket(pk);
    }

    public void sendAnnouncement(String message) {
        this.sendAnnouncement("", message);
    }

    public void sendAnnouncement(String source, String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_ANNOUNCEMENT;
        pk.source = source;
        pk.message = message;
        this.sendPacket(pk);
    }

    /**
     * Sends a toast message to the player, or queue to send it if a toast message is already shown.
     */
    public void sendToastNotification(String title, String body) {
        ToastRequestPacket pk = new ToastRequestPacket();
        pk.title = title;
        pk.content = body;
        this.sendPacket(pk);
    }

    @Override
    public void close() {
        this.close("");
    }

    /**
     * {@code notify=true}
     *
     * @see #close(TextContainer, String, boolean)
     */
    public void close(String message) {
        this.close(message, "generic");
    }

    /**
     * {@code notify=true}
     *
     * @see #close(TextContainer, String, boolean)
     */
    public void close(String message, String reason) {
        this.close(message, reason, true);
    }

    /**
     * @see #close(TextContainer, String, boolean)
     */
    public void close(String message, String reason, boolean notify) {
        this.close(new TextContainer(message), reason, notify);
    }

    /**
     * {@code reason="generic",notify=true}
     *
     * @see #close(TextContainer, String, boolean)
     */
    public void close(TextContainer message) {
        this.close(message, "generic");
    }

    /**
     * notify=true
     *
     * @see #close(TextContainer, String, boolean)
     */
    public void close(TextContainer message, String reason) {
        this.close(message, reason, true);
    }

    /**
     * Closing the player's connection and all its activities is almost as function as {@link #kick}, the difference is that {@link #kick} is implemented based on {@code close}.
     *
     * @param message PlayerQuitEvent message
     * @param reason  Reason for logout
     * @param notify  Whether to display the logout screen notification
     */
    public void close(TextContainer message, String reason, boolean notify) {
        if (this.isConnected() && !this.closed) {
            // This must be called here before the player goes offline, otherwise the packet cannot be sent over
            var scoreboardManager = this.getServer().getScoreboardManager();
            // in test environment sometimes the scoreboard manager is null
            if (scoreboardManager != null) {
                scoreboardManager.beforePlayerQuit(this);
            }

            if (notify && reason.length() > 0) {
                DisconnectPacket pk = new DisconnectPacket();
                pk.message = reason;
                this.sendPacketImmediately(pk);
            }

            playerConnection.connected = false;
            PlayerQuitEvent event = null;
            if (this.getName() != null && this.getName().length() > 0) {
                event = new PlayerQuitEvent(this, message, true, reason);
                event.call();
                if (this.fishing != null) {
                    this.stopFishing(false);
                }
            }

            // Close the temporary windows first, so they have chance to change all inventories before being disposed
            this.removeAllWindows(false);
            this.resetCraftingGridType();

            if (event != null && this.isLoggedIn() && event.getAutoSave()) {
                this.save();
            }

            for (Player player :
                    new ArrayList<>(server.getPlayerManager().getOnlinePlayers().values())) {
                if (!player.canSee(this)) {
                    player.showPlayer(this);
                }
            }

            this.hiddenPlayers.clear();

            this.removeAllWindows(true);

            for (long index : new ArrayList<>(this.usedChunks.keySet())) {
                int chunkX = Level.getHashX(index);
                int chunkZ = Level.getHashZ(index);
                this.getLevel().unregisterChunkLoader(this, chunkX, chunkZ);
                this.usedChunks.remove(index);

                for (Entity entity : getLevel().getChunkEntities(chunkX, chunkZ).values()) {
                    if (entity != this) {
                        entity.getViewers().remove(getLoaderId());
                    }
                }
            }

            super.close();

            this.sourceInterface.close(this, notify ? reason : "");

            if (this.isLoggedIn()) {
                server.getPlayerManager().removeOnlinePlayer(this);
            }

            playerConnection.setLoggedIn(false);

            if (event != null
                    && !Objects.equals(this.username, "")
                    && this.isSpawned()
                    && !Objects.equals(event.getQuitMessage().toString(), "")) {
                this.server.broadcastMessage(event.getQuitMessage());
            }

            this.server.getPluginManager().unsubscribeFromPermission(Server.BROADCAST_CHANNEL_USERS, this);
            playerConnection.setSpawned(false);
            ;
            log.info(this.getServer()
                    .getLanguage()
                    .tr(
                            "nukkit.player.logOut",
                            TextFormat.AQUA + (this.getName() == null ? "" : this.getName()) + TextFormat.WHITE,
                            playerConnection.getAddress(),
                            String.valueOf(playerConnection.getPort()),
                            this.getServer().getLanguage().tr(reason)));
            this.windows.clear();
            this.usedChunks.clear();
            this.loadQueue.clear();
            this.hasSpawned.clear();
            this.spawnPosition = null;

            if (this.riding instanceof EntityRideable entityRideable) {
                entityRideable.dismountEntity(this);
            }

            this.riding = null;
        }

        if (this.perm != null) {
            this.perm.clearPermissions();
            this.perm = null;
        }

        if (this.inventory != null) {
            this.inventory = null;
        }

        this.chunk = null;

        server.getPlayerManager().removePlayer(this);
    }

    public void save() {
        this.save(false);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        if (spawnBlockPosition == null) {
            namedTag.remove("SpawnBlockPositionX")
                    .remove("SpawnBlockPositionY")
                    .remove("SpawnBlockPositionZ")
                    .remove("SpawnBlockLevel");
        } else {
            namedTag.putInt("SpawnBlockPositionX", spawnBlockPosition.getFloorX())
                    .putInt("SpawnBlockPositionY", spawnBlockPosition.getFloorY())
                    .putInt("SpawnBlockPositionZ", spawnBlockPosition.getFloorZ())
                    .putString(
                            "SpawnBlockLevel",
                            this.spawnBlockPosition.getLevel().getFolderName());
        }

        if (spawnPosition == null) {
            namedTag.remove("SpawnX")
                    .remove("SpawnY")
                    .remove("SpawnZ")
                    .remove("SpawnLevel")
                    .remove("SpawnDimension");
        } else {
            namedTag.putInt("SpawnX", this.spawnPosition.getFloorX())
                    .putInt("SpawnY", this.spawnPosition.getFloorY())
                    .putInt("SpawnZ", this.spawnPosition.getFloorZ());
            if (this.spawnPosition.getLevel() != null) {
                this.namedTag.putString(
                        "SpawnLevel", this.spawnPosition.getLevel().getFolderName());
                this.namedTag.putInt(
                        "SpawnDimension", this.spawnPosition.getLevel().getDimension());
            } else {
                this.namedTag.putString(
                        "SpawnLevel", this.server.getDefaultLevel().getFolderName());
                this.namedTag.putInt(
                        "SpawnDimension", this.server.getDefaultLevel().getDimension());
            }
        }

        this.adventureSettings.saveNBT();
    }

    public void save(boolean async) {
        if (this.closed) {
            throw new IllegalStateException("Tried to save closed player");
        }

        saveNBT();

        if (this.getLevel() != null) {
            this.namedTag.putString("Level", this.getLevel().getFolderName());

            CompoundTag achievements = new CompoundTag();
            for (String achievement : this.achievements) {
                achievements.putByte(achievement, 1);
            }

            this.namedTag.putCompound("Achievements", achievements);

            this.namedTag.putInt("playerGameType", gamemode.ordinal());
            this.namedTag.putLong("lastPlayed", System.currentTimeMillis() / 1000);

            this.namedTag.putString("lastIP", playerConnection.getAddress());

            this.namedTag.putInt("EXP", this.getExperience());
            this.namedTag.putInt("expLevel", this.getExperienceLevel());

            this.namedTag.putInt("foodLevel", this.getFoodData().getLevel());
            this.namedTag.putFloat("foodSaturationLevel", this.getFoodData().getFoodSaturationLevel());

            var fogIdentifiers = new ListTag<StringTag>("fogIdentifiers");
            var userProvidedFogIds = new ListTag<StringTag>("userProvidedFogIds");
            this.fogStack.forEach(fog -> {
                fogIdentifiers.add(new StringTag("", fog.identifier().toString()));
                userProvidedFogIds.add(new StringTag("", fog.userProvidedId()));
            });
            this.namedTag.putList(fogIdentifiers);
            this.namedTag.putList(userProvidedFogIds);

            this.namedTag.putInt("TimeSinceRest", this.timeSinceRest);

            if (!username.isEmpty() && this.namedTag != null) {
                server.getPlayerManager().saveOfflinePlayerData(String.valueOf(this.uuid), this.namedTag, async);
            }
        }
    }

    @Override
    public String getOriginalName() {
        return "Player";
    }

    @NotNull @Override
    public String getName() {
        return this.username;
    }

    public LangCode getLanguageCode() {
        return playerInfo.getLanguageCode();
    }

    @Override
    public void kill() {
        if (!this.isSpawned()) {
            return;
        }

        boolean showMessages = this.getLevel().getGameRules().getBoolean(GameRule.SHOW_DEATH_MESSAGES);
        String message = "";
        List<String> params = new ArrayList<>();
        EntityDamageEvent cause = this.getLastDamageCause();

        if (showMessages) {
            params.add(this.getDisplayName());

            switch (cause == null ? DamageCause.CUSTOM : cause.getCause()) {
                case ENTITY_ATTACK:
                    if (cause instanceof EntityDamageByEntityEvent) {
                        Entity e = ((EntityDamageByEntityEvent) cause).getDamager();
                        killer = e;
                        if (e instanceof Player) {
                            message = "death.attack.player";
                            params.add(((Player) e).getDisplayName());
                            break;
                        } else if (e instanceof EntityLiving) {
                            message = "death.attack.mob";
                            params.add(!Objects.equals(e.getNameTag(), "") ? e.getNameTag() : e.getName());
                            break;
                        } else {
                            params.add("Unknown");
                        }
                    }
                    break;
                case PROJECTILE:
                    if (cause instanceof EntityDamageByEntityEvent) {
                        Entity e = ((EntityDamageByEntityEvent) cause).getDamager();
                        killer = e;
                        if (e instanceof Player) {
                            message = "death.attack.arrow";
                            params.add(((Player) e).getDisplayName());
                        } else if (e instanceof EntityLiving) {
                            message = "death.attack.arrow";
                            params.add(!Objects.equals(e.getNameTag(), "") ? e.getNameTag() : e.getName());
                            break;
                        } else {
                            params.add("Unknown");
                        }
                    }
                    break;
                case VOID:
                    message = "death.attack.outOfWorld";
                    break;
                case FALL:
                    if (cause.getFinalDamage() > 2) {
                        message = "death.fell.accident.generic";
                        break;
                    }
                    message = "death.attack.fall";
                    break;

                case SUFFOCATION:
                    message = "death.attack.inWall";
                    break;

                case LAVA:
                    message = "death.attack.lava";

                    if (killer instanceof EntityProjectile) {
                        Entity shooter = ((EntityProjectile) killer).shootingEntity;
                        if (shooter != null) {
                            killer = shooter;
                        }
                        if (killer instanceof EntityHuman) {
                            message += ".player";
                            params.add(
                                    !Objects.equals(shooter.getNameTag(), "")
                                            ? shooter.getNameTag()
                                            : shooter.getName());
                        }
                    }
                    break;

                case FIRE:
                    message = "death.attack.onFire";
                    break;

                case FIRE_TICK:
                    message = "death.attack.inFire";
                    break;

                case DROWNING:
                    message = "death.attack.drown";
                    break;

                case CONTACT:
                    if (cause instanceof EntityDamageByBlockEvent) {
                        int id = ((EntityDamageByBlockEvent) cause).getDamager().getId();
                        if (id == BlockID.CACTUS) {
                            message = "death.attack.cactus";
                        } else if (id == BlockID.ANVIL) {
                            message = "death.attack.anvil";
                        }
                    }
                    break;

                case BLOCK_EXPLOSION:
                case ENTITY_EXPLOSION:
                    if (cause instanceof EntityDamageByEntityEvent) {
                        Entity e = ((EntityDamageByEntityEvent) cause).getDamager();
                        killer = e;
                        if (e instanceof Player) {
                            message = "death.attack.explosion.player";
                            params.add(((Player) e).getDisplayName());
                        } else if (e instanceof EntityLiving) {
                            message = "death.attack.explosion.player";
                            params.add(!Objects.equals(e.getNameTag(), "") ? e.getNameTag() : e.getName());
                            break;
                        } else {
                            message = "death.attack.explosion";
                        }
                    } else {
                        message = "death.attack.explosion";
                    }
                    break;
                case MAGIC:
                    message = "death.attack.magic";
                    break;
                case LIGHTNING:
                    message = "death.attack.lightningBolt";
                    break;
                case HUNGER:
                    message = "death.attack.starve";
                    break;
                case HOT_FLOOR:
                    message = "death.attack.magma";
                    break;
                default:
                    message = "death.attack.generic";
                    break;
            }
        }

        PlayerDeathEvent event = new PlayerDeathEvent(
                this,
                this.getDrops(),
                new TranslationContainer(message, params.toArray(EmptyArrays.EMPTY_STRINGS)),
                this.experienceLevel);
        event.setKeepExperience(this.getLevel().gameRules.getBoolean(GameRule.KEEP_INVENTORY));
        event.setKeepInventory(event.getKeepExperience());

        event.call();

        if (!event.isCancelled()) {
            if (this.fishing != null) {
                this.stopFishing(false);
            }

            this.health = 0;
            this.extinguish();
            this.scheduleUpdate();
            if (!event.getKeepInventory() && this.getLevel().getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
                for (Item item : event.getDrops()) {
                    if (!item.hasEnchantment(Enchantment.ID_VANISHING_CURSE) && item.applyEnchantments()) {
                        this.getLevel().dropItem(this, item, null, true, 40);
                    }
                }

                if (this.inventory != null) {
                    new HashMap<>(this.inventory.slots).forEach((slot, item) -> {
                        if (!item.keepOnDeath()) {
                            this.inventory.clear(slot);
                        }
                    });
                }
                if (this.offhandInventory != null) {
                    new HashMap<>(this.offhandInventory.slots).forEach((slot, item) -> {
                        if (!item.keepOnDeath()) {
                            this.offhandInventory.clear(slot);
                        }
                    });
                }
            }

            if (!event.getKeepExperience() && this.getLevel().getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
                if (this.isSurvival() || this.isAdventure()) {
                    int exp = event.getExperience() * 7;
                    if (exp > 100) exp = 100;
                    this.getLevel().dropExpOrb(this, exp);
                }
                this.setExperience(0, 0);
            }

            this.timeSinceRest = 0;

            DeathInfoPacket deathInfo = new DeathInfoPacket();
            deathInfo.translation = event.getTranslationDeathMessage();
            this.sendPacket(deathInfo);

            if (showMessages && !event.getDeathMessage().toString().isEmpty()) {
                this.server.broadcast(event.getDeathMessage(), Server.BROADCAST_CHANNEL_USERS);
            }

            RespawnPacket pk = new RespawnPacket();
            Position pos = this.getSpawn();
            pk.x = (float) pos.x();
            pk.y = (float) pos.y();
            pk.z = (float) pos.z();
            pk.respawnState = RespawnPacket.STATE_SEARCHING_FOR_SPAWN;

            this.sendPacket(pk);
        }
    }

    @Override
    public void setHealth(float health) {
        if (health < 1) {
            health = 0;
        }
        super.setHealth(health);
        // TODO: Remove it in future! This a hack to solve the client-side absorption bug! WFT Mojang (Half a yellow
        // heart cannot be shown, we can test it in local gaming)
        Attribute attr = Attribute.getAttribute(Attribute.MAX_HEALTH)
                .setMaxValue(this.getAbsorption() % 2 != 0 ? this.getMaxHealth() + 1 : this.getMaxHealth())
                .setValue(health > 0 ? (health < getMaxHealth() ? health : getMaxHealth()) : 0);
        if (this.isSpawned()) {
            UpdateAttributesPacket pk = new UpdateAttributesPacket();
            pk.entries = new Attribute[] {attr};
            pk.entityId = this.id;
            this.sendPacket(pk);
        }
    }

    @Override
    public void setMaxHealth(int maxHealth) {
        super.setMaxHealth(maxHealth);

        Attribute attr = Attribute.getAttribute(Attribute.MAX_HEALTH)
                .setMaxValue(this.getAbsorption() % 2 != 0 ? this.getMaxHealth() + 1 : this.getMaxHealth())
                .setValue(health > 0 ? (health < getMaxHealth() ? health : getMaxHealth()) : 0);
        if (this.isSpawned()) {
            UpdateAttributesPacket pk = new UpdateAttributesPacket();
            pk.entries = new Attribute[] {attr};
            pk.entityId = this.id;
            this.sendPacket(pk);
        }
    }

    /**
     * playLevelUpSound=false
     *
     * @see #addExperience(int, boolean)
     */
    public void addExperience(int add) {
        addExperience(add, false);
    }

    /**
     * Increase the experience value of the player
     *
     * @param add              Number of experience values
     * @param playLevelUpSound With or without upgrade sound
     */
    public void addExperience(int add, boolean playLevelUpSound) {
        if (add == 0) return;
        int now = this.getExperience();
        int added = now + add;
        int level = this.getExperienceLevel();
        int most = calculateRequireExperience(level);
        while (added >= most) { // Level Up!
            added = added - most;
            level++;
            most = calculateRequireExperience(level);
        }
        this.setExperience(added, level, playLevelUpSound);
    }

    /**
     * Calculates the amount of experience a player needs to reach a certain level
     *
     * @param level level
     * @return int
     */
    public static int calculateRequireExperience(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        } else if (level >= 15) {
            return 37 + (level - 15) * 5;
        } else {
            return 7 + level * 2;
        }
    }

    /**
     * {@code level = this.getExperienceLevel(),playLevelUpSound=false}
     *
     * @see #setExperience(int, int, boolean)
     */
    public void setExperience(int exp) {
        setExperience(exp, this.getExperienceLevel());
    }

    /**
     * playLevelUpSound=false
     *
     * @see #setExperience(int, int, boolean)
     */
    public void setExperience(int exp, int level) {
        setExperience(exp, level, false);
    }

    /**
     * set the experience value and level of the player
     *
     * @param playLevelUpSound With or without upgrade sound
     * @param exp              Experience value
     * @param level            Level
     */
    // todo something on performance, lots of exp orbs then lots of packets, could crash client
    public void setExperience(int exp, int level, boolean playLevelUpSound) {
        PlayerExperienceChangeEvent expEvent =
                new PlayerExperienceChangeEvent(this, this.getExperience(), this.getExperienceLevel(), exp, level);
        expEvent.call();
        if (expEvent.isCancelled()) {
            return;
        }
        exp = expEvent.getNewExperience();
        level = expEvent.getNewExperienceLevel();

        int levelBefore = this.experienceLevel;
        this.experience = exp;
        this.experienceLevel = level;

        this.sendExperienceLevel(level);
        this.sendExperience(exp);
        if (playLevelUpSound
                && levelBefore < level
                && levelBefore / 5 != level / 5
                && this.lastPlayerdLevelUpSoundTime < this.age - 100) {
            this.lastPlayerdLevelUpSoundTime = this.age;
            this.getLevel()
                    .addLevelSoundEvent(
                            this,
                            LevelSoundEventPacketV2.SOUND_LEVELUP,
                            Math.min(7, level / 5) << 28,
                            "",
                            false,
                            false);
        }
    }

    /**
     * @see #sendExperience(int)
     */
    public void sendExperience() {
        sendExperience(this.getExperience());
    }

    /**
     * The implementation of setExperience is used to set the experience value corresponding to the current level, i.e. the experience bar
     *
     * @param exp Experience value
     */
    public void sendExperience(int exp) {
        if (this.isSpawned()) {
            float percent = ((float) exp) / calculateRequireExperience(this.getExperienceLevel());
            percent = Math.max(0f, Math.min(1f, percent));
            this.setAttribute(Attribute.getAttribute(Attribute.EXPERIENCE).setValue(percent));
        }
    }

    /**
     * @see #sendExperienceLevel(int)
     */
    public void sendExperienceLevel() {
        sendExperienceLevel(this.getExperienceLevel());
    }

    /**
     * The implementation of setExperience is used to set the level
     *
     * @param level Level
     */
    public void sendExperienceLevel(int level) {
        if (this.isSpawned()) {
            this.setAttribute(Attribute.getAttribute(Attribute.EXPERIENCE_LEVEL).setValue(level));
        }
    }

    /**
     * Send UpdateAttributesPacket packets to this player with the specified {@link Attribute}.
     *
     * @param attribute the attribute
     */
    public void setAttribute(Attribute attribute) {
        UpdateAttributesPacket pk = new UpdateAttributesPacket();
        pk.entries = new Attribute[] {attribute};
        pk.entityId = this.id;
        this.sendPacket(pk);
    }

    /**
     * send=true
     *
     * @see #setMovementSpeed(float, boolean)
     */
    @Override
    public void setMovementSpeed(float speed) {
        setMovementSpeed(speed, true);
    }

    /**
     * Set the movement speed of this player.
     *
     * @param speed Speed value, note that the default movement speed is {@link #DEFAULT_SPEED}
     * @param send  Whether to send {@link UpdateAttributesPacket} to the client
     */
    public void setMovementSpeed(float speed, boolean send) {
        super.setMovementSpeed(speed);
        if (this.isSpawned() && send) {
            this.sendMovementSpeed(speed);
        }
    }

    /**
     * Send {@link Attribute#MOVEMENT_SPEED} Attribute to Client.
     *
     * @param speed The speed value
     */
    public void sendMovementSpeed(float speed) {
        Attribute attribute = Attribute.getAttribute(Attribute.MOVEMENT_SPEED).setValue(speed);
        this.setAttribute(attribute);
    }

    /**
     * Get the entity that killed this player
     *
     * @return Entity | null
     */
    public Entity getKiller() {
        return killer;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (!this.isAlive()) {
            return false;
        }

        if (this.isSpectator() || this.isCreative()) {
            // source.setCancelled();
            return false;
        } else if (this.getAdventureSettings().get(Type.ALLOW_FLIGHT) && source.getCause() == DamageCause.FALL) {
            // source.setCancelled();
            return false;
        } else if (source.getCause() == DamageCause.FALL) {
            if (this.getLevel()
                            .getBlock(this.getPosition().floor().add(0.5, -1, 0.5))
                            .getId()
                    == Block.SLIME_BLOCK) {
                if (!this.isSneaking()) {
                    // source.setCancelled();
                    this.resetFallDistance();
                    return false;
                }
            }
        }

        if (super.attack(source)) { // !source.isCancelled()
            if (this.getLastDamageCause() == source && this.isSpawned()) {
                if (source instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) {
                    Entity damager = entityDamageByEntityEvent.getDamager();
                    if (damager instanceof Player) {
                        ((Player) damager).getFoodData().updateFoodExpLevel(0.1);
                    }
                    // Save the entity that attacked the player in the lastBeAttackEntity
                    this.lastBeAttackEntity = entityDamageByEntityEvent.getDamager();
                }
                EntityEventPacket pk = new EntityEventPacket();
                pk.eid = this.id;
                pk.event = EntityEventPacket.HURT_ANIMATION;
                this.sendPacket(pk);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Drops an item on the ground in front of the player. Returns if the item drop was successful.
     *
     * @param item To drop
     * @return Bool if the item was dropped or if the item was null
     */
    public boolean dropItem(Item item) {
        if (!this.isSpawned() || !this.isAlive()) {
            return false;
        }

        if (item.isNull()) {
            log.debug("{} attempted to drop a null item ({})", this.getName(), item);
            return true;
        }

        Vector3 motion = this.getDirectionVector().multiply(0.4);

        this.getLevel().dropItem(this.add(0, 1.3, 0), item, motion, 40);

        this.setDataFlag(DATA_FLAGS, DATA_FLAG_ACTION, false);
        return true;
    }

    /**
     * Drops an item on the ground in front of the player. Returns the dropped item.
     *
     * @param item To drop
     * @return EntityItem if the item was dropped or null if the item was null
     */
    @Nullable public EntityItem dropAndGetItem(@NotNull Item item) {
        if (!this.isSpawned() || !this.isAlive()) {
            return null;
        }

        if (item.isNull()) {
            log.debug("{} attempted to drop a null item ({})", this.getName(), item);
            return null;
        }

        Vector3 motion = this.getDirectionVector().multiply(0.4);

        this.setDataFlag(DATA_FLAGS, DATA_FLAG_ACTION, false);

        return this.getLevel().dropAndGetItem(this.add(0, 1.3, 0), item, motion, 40);
    }

    /**
     * @see #sendPosition(Vector3, double, double, int, Player[])
     */
    public void sendPosition(Vector3 pos) {
        this.sendPosition(pos, this.yaw());
    }

    /**
     * @see #sendPosition(Vector3, double, double, int, Player[])
     */
    public void sendPosition(Vector3 pos, double yaw) {
        this.sendPosition(pos, yaw, this.pitch());
    }

    /**
     * @see #sendPosition(Vector3, double, double, int, Player[])
     */
    public void sendPosition(Vector3 pos, double yaw, double pitch) {
        this.sendPosition(pos, yaw, pitch, MovePlayerPacket.MODE_NORMAL);
    }

    /**
     * @see #sendPosition(Vector3, double, double, int, Player[])
     */
    public void sendPosition(Vector3 pos, double yaw, double pitch, int mode) {
        this.sendPosition(pos, yaw, pitch, mode, null);
    }

    /**
     * Implementation of {@link Player#addMovement}, only sends {@link MovePlayerPacket} packets to the client.
     *
     * @param pos     The pos of MovePlayerPacket
     * @param yaw     The yaw of MovePlayerPacket
     * @param pitch   The pitch of MovePlayerPacket
     * @param mode    The mode of MovePlayerPacket
     * @param targets Players of receive the packet
     */
    public void sendPosition(Vector3 pos, double yaw, double pitch, int mode, Player[] targets) {
        MovePlayerPacket pk = new MovePlayerPacket();
        pk.eid = this.getId();
        pk.x = (float) pos.x();
        pk.y = (float) (pos.y() + this.getEyeHeight());
        pk.z = (float) pos.z();
        pk.headYaw = (float) yaw;
        pk.pitch = (float) pitch;
        pk.yaw = (float) yaw;
        pk.mode = mode;
        pk.onGround = this.onGround;
        if (this.riding != null) {
            pk.ridingEid = this.riding.getId();
            pk.mode = MovePlayerPacket.MODE_PITCH;
        }
        if (targets != null) {
            Server.broadcastPacket(targets, pk);
        } else {
            this.sendPacket(pk);
        }
    }

    @Override
    public boolean teleport(Location to, TeleportCause cause) {
        if (!this.isOnline()) {
            return false;
        }
        Location from = this.getLocation();
        // event
        if (cause != null) {
            PlayerTeleportEvent event = new PlayerTeleportEvent(this, from, to, cause);
            event.call();
            if (event.isCancelled()) {
                return false;
            }
            to = event.getTo();
        }
        // remove inventory
        for (Inventory window : new ArrayList<>(this.windows.keySet())) {
            if (window == this.inventory) {
                continue;
            }
            this.removeWindow(window);
        }
        // remove ride
        final Entity currentRide = getRiding();
        if (currentRide != null && !currentRide.dismountEntity(this)) {
            return false;
        }
        this.setMotion(this.temporalVector.setComponents(0, 0, 0));
        // switch level, update pos and rotation, update aabb
        if (this.setPositionAndRotation(to, to.yaw(), to.pitch(), to.headYaw())) {
            this.resetFallDistance();
            this.onGround = !this.noClip;
            // send to client
            this.sendPosition(to, to.yaw(), to.pitch(), MovePlayerPacket.MODE_TELEPORT);
            this.newPosition = to;
        } else {
            this.sendPosition(this, to.yaw(), to.pitch(), MovePlayerPacket.MODE_TELEPORT);
            this.newPosition = this;
        }
        // state update
        this.positionChanged = true;
        this.nextChunkOrderRun = 0;
        // DummyBossBar
        this.getDummyBossBars().values().forEach(DummyBossBar::reshow);
        // Weather
        this.getLevel().sendWeather(this);
        // Update time
        this.getLevel().sendTime(this);
        this.updateTrackingPositions(true);
        // Update gamemode
        if (this.isSpectator()) {
            this.setGamemode(gamemode, false, true);
        }
        return true;
    }

    /**
     * Automatic id assignment
     *
     * @see #sendForm(FormWindow, int)
     */
    public int sendForm(FormWindow window) {
        return sendForm(window, this.formWindowCount++);
    }

    /**
     * Shows a new FormWindow to the player
     * You can find out FormWindow result by listening to PlayerFormRespondedEvent
     *
     * @param window to show
     * @param id     form id
     * @return form id to use in {@link PlayerFormRespondedEvent}
     */
    public int sendForm(FormWindow window, int id) {
        if (this.formWindows.size() > 100) {
            this.kick("Possible DoS vulnerability: More Than 10 FormWindow sent to client already.");
            return id;
        }
        ModalFormRequestPacket packet = new ModalFormRequestPacket();
        packet.formId = id;
        packet.data = window.getJSONData();
        this.formWindows.put(packet.formId, window);

        this.sendPacket(packet);
        return id;
    }

    /**
     * book=true
     *
     * @see #sendDialog(FormWindowDialog, boolean)
     */
    public void sendDialog(FormWindowDialog dialog) {
        sendDialog(dialog, true);
    }

    /**
     * Show dialog window to the player.
     *
     * @param dialog The dialog
     * @param book   If true, the {@link FormWindowDialog#getSceneName()} will be updated immediately.
     */
    public void sendDialog(FormWindowDialog dialog, boolean book) {
        String actionJson = dialog.getButtonJSONData();

        if (book && dialogWindows.getIfPresent(dialog.getSceneName()) != null) dialog.updateSceneName();
        dialog.getBindEntity().setDataProperty(new ByteEntityData(Entity.DATA_HAS_NPC_COMPONENT, 1));
        dialog.getBindEntity().setDataProperty(new StringEntityData(Entity.DATA_NPC_SKIN_DATA, dialog.getSkinData()));
        dialog.getBindEntity().setDataProperty(new StringEntityData(Entity.DATA_NPC_ACTIONS, actionJson));
        dialog.getBindEntity().setDataProperty(new StringEntityData(Entity.DATA_INTERACTIVE_TAG, dialog.getContent()));

        NPCDialoguePacket packet = new NPCDialoguePacket();
        packet.setRuntimeEntityId(dialog.getEntityId());
        packet.setAction(NPCDialoguePacket.NPCDialogAction.OPEN);
        packet.setDialogue(dialog.getContent());
        packet.setNpcName(dialog.getTitle());
        if (book) packet.setSceneName(dialog.getSceneName());
        packet.setActionJson(dialog.getButtonJSONData());
        if (book) this.dialogWindows.put(dialog.getSceneName(), dialog);
        this.sendPacket(packet);
    }

    /**
     * Shows a new setting page in game settings.
     * You can find out settings result by listening to PlayerFormRespondedEvent
     *
     * @param window to show on settings page
     * @return form id to use in {@link PlayerFormRespondedEvent}
     */
    public int addServerSettings(FormWindow window) {
        int id = this.formWindowCount++;

        this.serverSettings.put(id, window);
        return id;
    }

    /**
     * Creates and sends a BossBar to the player
     *
     * @param dummyBossBar DummyBossBar Object (Instantiate it by the Class Builder)
     * @return bossBarId  The BossBar ID, you should store it if you want to remove or update the BossBar later
     * @see DummyBossBar.Builder
     */
    public long createBossBar(DummyBossBar dummyBossBar) {
        this.dummyBossBars.put(dummyBossBar.getBossBarId(), dummyBossBar);
        dummyBossBar.create();
        return dummyBossBar.getBossBarId();
    }

    /**
     * Get a DummyBossBar object
     *
     * @param bossBarId The BossBar ID
     * @return DummyBossBar object
     * @see DummyBossBar#setText(String) Set BossBar text
     * @see DummyBossBar#setLength(float) Set BossBar length
     * @see DummyBossBar#setColor(BossBarColor) Set BossBar color
     */
    public DummyBossBar getDummyBossBar(long bossBarId) {
        return this.dummyBossBars.getOrDefault(bossBarId, null);
    }

    /**
     * Get all DummyBossBar objects
     *
     * @return DummyBossBars Map
     */
    public Map<Long, DummyBossBar> getDummyBossBars() {
        return dummyBossBars;
    }

    /**
     * Updates a BossBar
     *
     * @param text      The new BossBar message
     * @param length    The new BossBar length
     * @param bossBarId The BossBar ID
     */
    @Deprecated
    public void updateBossBar(String text, int length, long bossBarId) {
        if (this.dummyBossBars.containsKey(bossBarId)) {
            DummyBossBar bossBar = this.dummyBossBars.get(bossBarId);
            bossBar.setText(text);
            bossBar.setLength(length);
        }
    }

    /**
     * Removes a BossBar
     *
     * @param bossBarId The BossBar ID
     */
    public void removeBossBar(long bossBarId) {
        if (this.dummyBossBars.containsKey(bossBarId)) {
            this.dummyBossBars.get(bossBarId).destroy();
            this.dummyBossBars.remove(bossBarId);
        }
    }

    /**
     * Get id from the specified {@link Inventory}
     *
     * @param inventory the inventory
     * @return the window id
     */
    public int getWindowId(Inventory inventory) {
        if (this.windows.containsKey(inventory)) {
            return this.windows.get(inventory);
        }

        return -1;
    }

    /**
     * Get {@link Inventory} from the specified id
     *
     * @param id The window id
     */
    public Inventory getWindowById(int id) {
        return this.windowIndex.get(id);
    }

    /**
     * {@code forceId=null isPermanent=false alwaysOpen = false}
     *
     * @see #addWindow(Inventory, Integer, boolean, boolean)
     */
    public int addWindow(Inventory inventory) {
        return addWindow(inventory, null);
    }

    /**
     * {@code isPermanent=false alwaysOpen = false}
     *
     * @see #addWindow(Inventory, Integer, boolean, boolean)
     */
    public int addWindow(Inventory inventory, Integer forceId) {
        return addWindow(inventory, forceId, false);
    }

    /**
     * alwaysOpen = false
     *
     * @see #addWindow(Inventory, Integer, boolean, boolean)
     */
    public int addWindow(Inventory inventory, Integer forceId, boolean isPermanent) {
        return addWindow(inventory, forceId, isPermanent, false);
    }

    /**
     * Add a {@link Inventory} window to display to this player
     *
     * @param inventory   The inventory
     * @param forceId     Force the window id to be specified, if it is duplicated with an existing window, it will be deleted and replaced,if is null is automatically assigned.
     * @param isPermanent If true it will store the Inventory in {@link #permanentWindows}
     * @param alwaysOpen  If true, even if the player is not {@link #isSpawned}, it will add the player as viewer to the specified inventory.
     * @return Return the window id, you can use the id to retrieve the Inventory via {@link #windowIndex}
     */
    public int addWindow(Inventory inventory, Integer forceId, boolean isPermanent, boolean alwaysOpen) {
        if (this.windows.containsKey(inventory)) {
            return this.windows.get(inventory);
        }
        int cnt;
        if (forceId == null) {
            this.windowCnt = cnt = Math.max(4, ++this.windowCnt % 99);
        } else {
            cnt = forceId;
        }
        this.windows.forcePut(inventory, cnt);

        if (isPermanent) {
            this.permanentWindows.add(cnt);
        }

        if (this.isSpawned() && inventory.open(this)) {
            if (!isPermanent) {
                updateTrackingPositions(true);
            }
            return cnt;
        } else if (!alwaysOpen) {
            this.removeWindow(inventory);

            return -1;
        } else {
            inventory.getViewers().add(this);
        }

        if (!isPermanent) {
            updateTrackingPositions(true);
        }

        return cnt;
    }

    public Optional<Inventory> getTopWindow() {
        for (Entry<Inventory, Integer> entry : this.windows.entrySet()) {
            if (!this.permanentWindows.contains(entry.getValue())) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

    /**
     * Remove the specified Inventory from the player
     *
     * @param inventory the inventory
     */
    public void removeWindow(Inventory inventory) {
        this.removeWindow(inventory, false);
    }

    /**
     * Commonly used for refreshing.
     */
    public void sendAllInventories() {
        getCursorInventory().sendContents(this);
        for (Inventory inv : this.windows.keySet()) {
            inv.sendContents(this);

            if (inv instanceof PlayerInventory) {
                ((PlayerInventory) inv).sendArmorContents(this);
            }
        }
    }

    /**
     * Gets cursor inventory of the player.
     */
    public PlayerCursorInventory getCursorInventory() {
        return this.getUIInventory().getCursorInventory();
    }

    /**
     * Sets crafting grid.
     *
     * @param grid {@link CraftingGrid}
     */
    public void setCraftingGrid(CraftingGrid grid) {
        this.craftingGrid = grid;
        this.addWindow(grid, ContainerIds.NONE);
    }

    /**
     * Reset crafting grid type.
     */
    public void resetCraftingGridType() {
        if (this.craftingGrid != null) {
            Item[] drops = this.inventory.addItem(
                    this.craftingGrid.getContents().values().toArray(Item.EMPTY_ARRAY));

            if (drops.length > 0) {
                for (Item drop : drops) {
                    this.dropItem(drop);
                }
            }

            drops = this.inventory.addItem(this.getCursorInventory().getItem(0));
            if (drops.length > 0) {
                for (Item drop : drops) {
                    this.dropItem(drop);
                }
            }

            this.getUIInventory().clearAll();

            if (this.craftingGrid instanceof BigCraftingGrid) {
                this.craftingGrid = this.getUIInventory().getCraftingGrid();
                this.addWindow(this.craftingGrid, ContainerIds.NONE);
            }

            this.craftingType = CRAFTING_SMALL;
        }
    }

    /**
     * permanent=false
     *
     * @see #removeAllWindows(boolean)
     */
    public void removeAllWindows() {
        removeAllWindows(false);
    }

    /**
     * Remove all windows.
     *
     * @param permanent If false, it will skip deleting the corresponding window in {@link #permanentWindows}
     */
    public void removeAllWindows(boolean permanent) {
        for (Entry<Integer, Inventory> entry : new ArrayList<>(this.windowIndex.entrySet())) {
            if (!permanent && this.permanentWindows.contains(entry.getKey())) {
                continue;
            }
            this.removeWindow(entry.getValue());
        }
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        this.server.getPlayerMetadata().setMetadata(this, metadataKey, newMetadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        return this.server.getPlayerMetadata().getMetadata(this, metadataKey);
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        return this.server.getPlayerMetadata().hasMetadata(this, metadataKey);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        this.server.getPlayerMetadata().removeMetadata(this, metadataKey, owningPlugin);
    }

    @Override
    public void onChunkChanged(FullChunk chunk) {
        this.usedChunks.remove(Level.chunkHash(chunk.getX(), chunk.getZ()));
    }

    @Override
    public void onChunkLoaded(FullChunk chunk) {}

    @Override
    public void onChunkPopulated(FullChunk chunk) {}

    @Override
    public void onChunkUnloaded(FullChunk chunk) {}

    @Override
    public void onBlockChanged(Vector3 block) {}

    @Override
    public boolean isLoaderActive() {
        return this.isConnected();
    }

    @Override
    public double getX() {
        return x();
    }

    @Override
    public double getZ() {
        return z();
    }

    public static BatchPacket getChunkCacheFromData(int chunkX, int chunkZ, int subChunkCount, byte[] payload) {
        LevelChunkPacket pk = new LevelChunkPacket();
        pk.chunkX = chunkX;
        pk.chunkZ = chunkZ;
        pk.subChunkCount = subChunkCount;
        pk.data = payload;
        pk.encode();

        BatchPacket batch = new BatchPacket();
        byte[][] batchPayload = new byte[2][];
        byte[] buf = pk.getBuffer();
        batchPayload[0] = Binary.writeUnsignedVarInt(buf.length);
        batchPayload[1] = buf;
        byte[] data = Binary.appendBytes(batchPayload);
        try {
            if (Server.getInstance().isEnableSnappy()) {
                batch.payload = SnappyCompression.compress(data);
            } else {
                batch.payload = Network.deflateRaw(data, Server.getInstance().networkCompressionLevel);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return batch;
    }

    /**
     * @return Whether the player is on the food system
     */
    public boolean isFoodEnabled() {
        return !(this.isCreative() || this.isSpectator()) && this.foodEnabled;
    }

    @Override
    public boolean switchLevel(Level level) {
        Level oldLevel = this.getLevel();
        if (super.switchLevel(level)) {
            SetSpawnPositionPacket spawnPosition = new SetSpawnPositionPacket();
            spawnPosition.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN;
            Position spawn = level.getSpawnLocation();
            spawnPosition.x = spawn.getFloorX();
            spawnPosition.y = spawn.getFloorY();
            spawnPosition.z = spawn.getFloorZ();
            spawnPosition.dimension = spawn.getLevel().getDimension();
            this.sendPacket(spawnPosition);

            // Remove old chunks
            for (long index : new ArrayList<>(this.usedChunks.keySet())) {
                int chunkX = Level.getHashX(index);
                int chunkZ = Level.getHashZ(index);
                this.unloadChunk(chunkX, chunkZ, oldLevel);
            }
            this.usedChunks.clear();

            SetTimePacket setTime = new SetTimePacket();
            setTime.time = level.getTime();
            this.sendPacket(setTime);

            GameRulesChangedPacket gameRulesChanged = new GameRulesChangedPacket();
            gameRulesChanged.gameRules = level.getGameRules();
            this.sendPacket(gameRulesChanged);

            if (oldLevel.getDimension() != level.getDimension()) {
                this.setDimension(level.getDimension());
            }
            updateTrackingPositions(true);
            return true;
        }

        return false;
    }

    public synchronized Locale getLocale() {
        return this.locale.get();
    }

    public synchronized void setLocale(Locale locale) {
        this.locale.set(locale);
    }

    @Override
    public void setSprinting(boolean value) {
        if (value && this.getFreezingTicks() > 0) return;
        if (isSprinting() != value) {
            super.setSprinting(value);
            this.setMovementSpeed(value ? getMovementSpeed() * 1.3f : getMovementSpeed() / 1.3f);

            if (this.hasEffect(Effect.SPEED)) {
                float movementSpeed = this.getMovementSpeed();
                this.sendMovementSpeed(value ? movementSpeed * 1.3f : movementSpeed);
            }
        }
    }

    /**
     * Transfers a player to another server
     *
     * @param address The address
     * @param port The port
     */
    public void transfer(String address, int port) {
        PlayerTransferEvent event = new PlayerTransferEvent(this, address, port);
        event.call();
        if (!event.isCancelled()) {
            playerConnection.transfer(event.getAddress(), event.getPort());
        }
    }

    public boolean pickupEntity(Entity entity, boolean near) {
        if (!this.isSpawned() || !this.isAlive() || !this.isOnline() || this.isSpectator() || entity.isClosed()) {
            return false;
        }

        if (near) {
            Inventory inventory = this.inventory;
            if (entity instanceof EntityArrow && ((EntityArrow) entity).hadCollision) {
                ItemArrow item = new ItemArrow();
                if (!this.isCreative()) {
                    // Should only collect to the offhand slot if the item matches what is already there
                    if (this.offhandInventory.getItem(0).getId() == item.getId()
                            && this.offhandInventory.canAddItem(item)) {
                        inventory = this.offhandInventory;
                    } else if (!inventory.canAddItem(item)) {
                        return false;
                    }
                }

                InventoryPickupArrowEvent event = new InventoryPickupArrowEvent(inventory, (EntityArrow) entity);

                int pickupMode = ((EntityArrow) entity).getPickupMode();
                if (pickupMode == EntityProjectile.PICKUP_NONE
                        || (pickupMode == EntityProjectile.PICKUP_CREATIVE && !this.isCreative())) {
                    event.cancel();
                }

                event.call();
                if (event.isCancelled()) {
                    return false;
                }

                TakeItemEntityPacket pk = new TakeItemEntityPacket();
                pk.entityId = this.getId();
                pk.target = entity.getId();
                Server.broadcastPacket(entity.getViewers().values(), pk);
                this.sendPacket(pk);

                if (!this.isCreative()) {
                    inventory.addItem(item.clone());
                }
                entity.close();
                return true;
            } else if (entity instanceof EntityThrownTrident) {
                // Check Trident is returning to shooter
                if (!((EntityThrownTrident) entity).hadCollision) {
                    if (entity.isNoClip()) {
                        if (!((EntityProjectile) entity).shootingEntity.equals(this)) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }

                if (!((EntityThrownTrident) entity).isPlayer()) {
                    return false;
                }

                Item item = ((EntityThrownTrident) entity).getItem();
                if (!this.isCreative() && !this.inventory.canAddItem(item)) {
                    return false;
                }

                InventoryPickupTridentEvent event =
                        new InventoryPickupTridentEvent(this.inventory, (EntityThrownTrident) entity);

                int pickupMode = ((EntityThrownTrident) entity).getPickupMode();
                if (pickupMode == EntityProjectile.PICKUP_NONE
                        || (pickupMode == EntityProjectile.PICKUP_CREATIVE && !this.isCreative())) {
                    event.cancel();
                }

                event.call();
                if (event.isCancelled()) {
                    return false;
                }

                TakeItemEntityPacket pk = new TakeItemEntityPacket();
                pk.entityId = this.getId();
                pk.target = entity.getId();
                Server.broadcastPacket(entity.getViewers().values(), pk);
                this.sendPacket(pk);

                if (!((EntityThrownTrident) entity).isCreative()) {
                    if (inventory
                                    .getItem(((EntityThrownTrident) entity).getFavoredSlot())
                                    .getId()
                            == Item.AIR) {
                        inventory.setItem(((EntityThrownTrident) entity).getFavoredSlot(), item.clone());
                    } else {
                        inventory.addItem(item.clone());
                    }
                }
                entity.close();
                return true;
            } else if (entity instanceof EntityItem) {
                if (((EntityItem) entity).getPickupDelay() <= 0) {
                    Item item = ((EntityItem) entity).getItem();

                    if (item != null) {
                        if (!this.isCreative() && !this.inventory.canAddItem(item)) {
                            return false;
                        }

                        InventoryPickupItemEvent event = new InventoryPickupItemEvent(inventory, (EntityItem) entity);
                        event.call();
                        if (event.isCancelled()) {
                            return false;
                        }

                        TakeItemEntityPacket pk = new TakeItemEntityPacket();
                        pk.entityId = this.getId();
                        pk.target = entity.getId();
                        Server.broadcastPacket(entity.getViewers().values(), pk);
                        this.sendPacket(pk);

                        this.inventory.addItem(item.clone());
                        entity.close();
                        return true;
                    }
                }
            }
        }

        int tick = this.getServer().getTick();
        if (pickedXPOrb < tick && entity instanceof EntityXPOrb xpOrb && this.boundingBox.isVectorInside(entity)) {
            if (xpOrb.getPickupDelay() <= 0) {
                int exp = xpOrb.getExp();
                entity.kill();
                this.getLevel().addLevelEvent(LevelEventPacket.EVENT_SOUND_EXPERIENCE_ORB, 0, this);
                pickedXPOrb = tick;

                // Mending
                ArrayList<Integer> itemsWithMending = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    if (inventory.getArmorItem(i).getEnchantment((short) Enchantment.ID_MENDING) != null) {
                        itemsWithMending.add(inventory.getSize() + i);
                    }
                }
                if (inventory.getItemInHand().getEnchantment((short) Enchantment.ID_MENDING) != null) {
                    itemsWithMending.add(inventory.getHeldItemIndex());
                }
                if (itemsWithMending.size() > 0) {
                    Random rand = new Random();
                    Integer itemToRepair = itemsWithMending.get(rand.nextInt(itemsWithMending.size()));
                    Item toRepair = inventory.getItem(itemToRepair);
                    if (toRepair instanceof ItemTool || toRepair instanceof ItemArmor) {
                        if (toRepair.getDamage() > 0) {
                            int dmg = toRepair.getDamage() - 2;
                            if (dmg < 0) {
                                dmg = 0;
                            }
                            toRepair.setDamage(dmg);
                            inventory.setItem(itemToRepair, toRepair);
                            return true;
                        }
                    }
                }

                this.addExperience(exp, true);
                return true;
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        if ((this.hash == 0) || (this.hash == 485)) {
            this.hash = (485 + (getUniqueId() != null ? getUniqueId().hashCode() : 0));
        }

        return this.hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Player other)) {
            return false;
        }
        return Objects.equals(this.getUniqueId(), other.getUniqueId()) && this.getId() == other.getId();
    }

    /**
     * Whether the player is digging block
     *
     * @return the boolean
     */
    public boolean isBreakingBlock() {
        return playerHandle.isBreakingBlock();
    }

    /**
     * Show a window of a XBOX account's profile
     *
     * @param xuid XUID
     */
    public void showXboxProfile(String xuid) {
        ShowProfilePacket pk = new ShowProfilePacket();
        pk.xuid = xuid;
        this.sendPacket(pk);
    }

    /**
     * Start fishing
     *
     * @param fishingRod fishing rod item
     */
    public void startFishing(Item fishingRod) {
        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", x()))
                        .add(new DoubleTag("", y() + this.getEyeHeight()))
                        .add(new DoubleTag("", z())))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", -Math.sin(yaw() / 180 + Math.PI) * Math.cos(pitch() / 180 * Math.PI)))
                        .add(new DoubleTag("", -Math.sin(pitch() / 180 * Math.PI)))
                        .add(new DoubleTag("", Math.cos(yaw() / 180 * Math.PI) * Math.cos(pitch() / 180 * Math.PI))))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", (float) yaw()))
                        .add(new FloatTag("", (float) pitch())));
        double f = 1.1;
        EntityFishingHook fishingHook = new EntityFishingHook(chunk, nbt, this);
        fishingHook.setMotion(new Vector3(
                -Math.sin(Math.toRadians(yaw())) * Math.cos(Math.toRadians(pitch())) * f * f,
                -Math.sin(Math.toRadians(pitch())) * f * f,
                Math.cos(Math.toRadians(yaw())) * Math.cos(Math.toRadians(pitch())) * f * f));
        ProjectileLaunchEvent event = new ProjectileLaunchEvent(fishingHook, this);
        event.call();
        if (event.isCancelled()) {
            fishingHook.close();
        } else {
            this.fishing = fishingHook;
            fishingHook.rod = fishingRod;
            fishingHook.checkLure();
            fishingHook.spawnToAll();
        }
    }

    /**
     * Stop fishing
     *
     * @param click clicked or forced
     */
    public void stopFishing(boolean click) {
        if (this.fishing != null && click) {
            fishing.reelLine();
        } else if (this.fishing != null) {
            this.fishing.close();
        }

        this.fishing = null;
    }

    @Override
    public boolean doesTriggerPressurePlate() {
        return !this.isSpectator();
    }

    public void setNoShieldTicks(int noShieldTicks) {
        this.noShieldTicks = noShieldTicks;
    }

    @Override
    public String toString() {
        return "Player(name='" + getName() + "', location=" + super.toString() + ')';
    }

    /**
     * Adds the items to the main player inventory and drops on the floor any excess.
     *
     * @param items The items to give to the player.
     */
    public void giveItem(Item... items) {
        for (Item failed : getInventory().addItem(items)) {
            getLevel().dropItem(this, failed);
        }
    }

    public void completeUsingItem(int itemId, int action) {
        CompletedUsingItemPacket pk = new CompletedUsingItemPacket();
        pk.itemId = itemId;
        pk.action = action;
        this.sendPacket(pk);
    }

    public void setShowingCredits(boolean showingCredits) {
        this.showingCredits = showingCredits;
        if (showingCredits) {
            ShowCreditsPacket pk = new ShowCreditsPacket();
            pk.eid = this.getId();
            pk.status = ShowCreditsPacket.STATUS_START_CREDITS;
            this.sendPacket(pk);
        }
    }

    public void showCredits() {
        this.setShowingCredits(true);
    }

    /**
     * Player screen shake effect
     *
     * @param intensity   the intensity
     * @param duration    the duration
     * @param shakeType   the shake type
     * @param shakeAction the shake action
     */
    public void shakeCamera(
            float intensity,
            float duration,
            CameraShakePacket.CameraShakeType shakeType,
            CameraShakePacket.CameraShakeAction shakeAction) {
        CameraShakePacket packet = new CameraShakePacket();
        packet.intensity = intensity;
        packet.duration = duration;
        packet.shakeType = shakeType;
        packet.shakeAction = shakeAction;
        this.sendPacket(packet);
    }

    /**
     * Set the cooling display effect of the specified itemCategory items, note that this method is only for client-side display effect, cooling logic implementation still needs to be implemented by itself
     *
     * @param coolDown     the cool down
     * @param itemCategory the item category
     */
    public void setItemCoolDown(int coolDown, String itemCategory) {
        var pk = new PlayerStartItemCoolDownPacket();
        pk.setCoolDownDuration(coolDown);
        pk.setItemCategory(itemCategory);
        this.sendPacket(pk);
    }

    @Override
    public void removeLine(IScoreboardLine line) {
        SetScorePacket packet = new SetScorePacket();
        packet.action = SetScorePacket.Action.REMOVE;
        var networkInfo = line.toNetworkInfo();
        if (networkInfo != null) packet.infos.add(networkInfo);
        this.sendPacket(packet);

        var scorer = new PlayerScorer(this);
        if (line.getScorer().equals(scorer)
                && line.getScoreboard().getViewers(DisplaySlot.BELOW_NAME).contains(this)) {
            this.setScoreTag("");
        }
    }

    @Override
    public void updateScore(IScoreboardLine line) {
        SetScorePacket packet = new SetScorePacket();
        packet.action = SetScorePacket.Action.SET;
        var networkInfo = line.toNetworkInfo();
        if (networkInfo != null) packet.infos.add(networkInfo);
        this.sendPacket(packet);

        var scorer = new PlayerScorer(this);
        if (line.getScorer().equals(scorer)
                && line.getScoreboard().getViewers(DisplaySlot.BELOW_NAME).contains(this)) {
            this.setScoreTag(line.getScore() + " " + line.getScoreboard().getDisplayName());
        }
    }

    @Override
    public void display(IScoreboard scoreboard, DisplaySlot slot) {
        SetDisplayObjectivePacket pk = new SetDisplayObjectivePacket();
        pk.displaySlot = slot;
        pk.objectiveName = scoreboard.getObjectiveName();
        pk.displayName = scoreboard.getDisplayName();
        pk.criteriaName = scoreboard.getCriteriaName();
        pk.sortOrder = scoreboard.getSortOrder();
        this.sendPacket(pk);

        // client won't storage the score of a scoreboard,so we should send the score to client
        SetScorePacket pk2 = new SetScorePacket();
        pk2.infos = scoreboard.getLines().values().stream()
                .map(line -> line.toNetworkInfo())
                .filter(line -> line != null)
                .collect(Collectors.toList());
        pk2.action = SetScorePacket.Action.SET;
        this.sendPacket(pk2);

        var scorer = new PlayerScorer(this);
        var line = scoreboard.getLine(scorer);
        if (slot == DisplaySlot.BELOW_NAME && line != null) {
            this.setScoreTag(line.getScore() + " " + scoreboard.getDisplayName());
        }
    }

    @Override
    public void hide(DisplaySlot slot) {
        SetDisplayObjectivePacket pk = new SetDisplayObjectivePacket();
        pk.displaySlot = slot;
        pk.objectiveName = "";
        pk.displayName = "";
        pk.criteriaName = "";
        pk.sortOrder = SortOrder.ASCENDING;
        this.sendPacket(pk);

        if (slot == DisplaySlot.BELOW_NAME) {
            this.setScoreTag("");
        }
    }

    @Override
    public void removeScoreboard(IScoreboard scoreboard) {
        RemoveObjectivePacket pk = new RemoveObjectivePacket();
        pk.objectiveName = scoreboard.getObjectiveName();

        this.sendPacket(pk);
    }

    public Boolean isOpenSignFront() {
        return openSignFront;
    }

    /**
     * Opens the player's sign editor GUI for the sign at the given position.
     */
    public void openSignEditor(Vector3 position, boolean frontSide) {
        if (openSignFront == null) {
            BlockEntity blockEntity = this.getLevel().getBlockEntity(position);
            if (blockEntity instanceof BlockEntitySign blockEntitySign) {
                if (blockEntitySign.getEditorEntityRuntimeId() == -1) {
                    blockEntitySign.setEditorEntityRuntimeId(this.getId());
                    OpenSignPacket openSignPacket = new OpenSignPacket();
                    openSignPacket.setPosition(position.asBlockVector3());
                    openSignPacket.setFrontSide(frontSide);
                    this.sendPacket(openSignPacket);
                    setOpenSignFront(frontSide);
                }
            } else {
                throw new IllegalArgumentException("Block at this position is not a sign");
            }
        }
    }

    public void setFlySneaking(boolean sneaking) {
        this.flySneaking = sneaking;
    }

    public boolean isFlySneaking() {
        return this.flySneaking;
    }
}
