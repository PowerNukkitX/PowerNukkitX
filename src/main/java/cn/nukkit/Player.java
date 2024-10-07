package cn.nukkit;

import cn.nukkit.AdventureSettings.Type;
import cn.nukkit.api.UsedByReflection;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockBed;
import cn.nukkit.block.BlockEndPortal;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.block.BlockRespawnAnchor;
import cn.nukkit.block.BlockWood;
import cn.nukkit.block.BlockWool;
import cn.nukkit.block.customblock.CustomBlock;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.camera.data.CameraPreset;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.utils.RawText;
import cn.nukkit.config.ServerPropertiesKeys;
import cn.nukkit.dialog.window.FormWindowDialog;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.EntityInteractable;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.EntityRideable;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.data.PlayerFlag;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.entity.item.EntityFishingHook;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.item.EntityXpOrb;
import cn.nukkit.entity.passive.EntityHorse;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.entity.projectile.EntityThrownTrident;
import cn.nukkit.event.block.WaterFrostEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityPortalEnterEvent;
import cn.nukkit.event.entity.EntityPortalEnterEvent.PortalType;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.event.inventory.InventoryPickupArrowEvent;
import cn.nukkit.event.inventory.InventoryPickupItemEvent;
import cn.nukkit.event.inventory.InventoryPickupTridentEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.event.player.PlayerTeleportEvent.TeleportCause;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.inventory.CraftTypeInventory;
import cn.nukkit.inventory.CraftingGridInventory;
import cn.nukkit.inventory.CreativeOutputInventory;
import cn.nukkit.inventory.HumanInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.PlayerCursorInventory;
import cn.nukkit.inventory.SpecialWindowId;
import cn.nukkit.inventory.fake.FakeInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemArmor;
import cn.nukkit.item.ItemArrow;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemShield;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.lang.CommandOutputContainer;
import cn.nukkit.lang.LangCode;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.ChunkLoader;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.PlayerChunkManager;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.particle.PunchBlockParticle;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.nbt.tag.ByteTag;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.network.connection.BedrockDisconnectReasons;
import cn.nukkit.network.connection.BedrockSession;
import cn.nukkit.network.process.SessionState;
import cn.nukkit.network.protocol.*;
import cn.nukkit.network.protocol.types.CommandOriginData;
import cn.nukkit.network.protocol.types.CommandOutputType;
import cn.nukkit.network.protocol.types.GameType;
import cn.nukkit.network.protocol.types.PlayerBlockActionData;
import cn.nukkit.network.protocol.types.PlayerInfo;
import cn.nukkit.network.protocol.types.SpawnPointType;
import cn.nukkit.permission.PermissibleBase;
import cn.nukkit.permission.Permission;
import cn.nukkit.permission.PermissionAttachment;
import cn.nukkit.permission.PermissionAttachmentInfo;
import cn.nukkit.plugin.InternalPlugin;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.positiontracking.PositionTrackingService;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.scheduler.Task;
import cn.nukkit.scheduler.TaskHandler;
import cn.nukkit.scoreboard.IScoreboard;
import cn.nukkit.scoreboard.IScoreboardLine;
import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.data.SortOrder;
import cn.nukkit.scoreboard.displayer.IScoreboardViewer;
import cn.nukkit.scoreboard.scorer.PlayerScorer;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.BlockIterator;
import cn.nukkit.utils.BossBarColor;
import cn.nukkit.utils.DummyBossBar;
import cn.nukkit.utils.Identifier;
import cn.nukkit.utils.LoginChainData;
import cn.nukkit.utils.PortalHelper;
import cn.nukkit.utils.TextFormat;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.PlatformDependent;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Game player object, representing the controlled character.
 * <p>
 * This class represents a player in the game, including their state, actions, and interactions.
 * </p>
 *
 * @see EntityHuman
 * @see CommandSender
 * @see ChunkLoader
 * @see IPlayer
 * @see IScoreboardViewer
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
@Slf4j
public class Player extends EntityHuman implements CommandSender, ChunkLoader, IPlayer, IScoreboardViewer {
    /**
     * An empty array of static constants that host the player.
     */
    public static final Player[] EMPTY_ARRAY = new Player[0];
    public static final int SURVIVAL = 0;
    public static final int CREATIVE = 1;
    public static final int ADVENTURE = 2;
    public static final int SPECTATOR = 3;
    public static final int VIEW = SPECTATOR;
    public static final float DEFAULT_SPEED = 0.1f;
    public static final float DEFAULT_FLY_SPEED = 0.05f;
    public static final float MAXIMUM_SPEED = 0.5f;
    public static final int PERMISSION_CUSTOM = 3;
    public static final int PERMISSION_OPERATOR = 2;
    public static final int PERMISSION_MEMBER = 1;
    public static final int PERMISSION_VISITOR = 0;
    /// static fields

    public boolean playedBefore;
    public boolean spawned = false;
    public volatile boolean locallyInitialized = false;
    public boolean loggedIn = false;
    public final HashSet<String> achievements = new HashSet<>();
    public int gamemode;
    public long lastBreak;
    public Vector3 speed = null;
    public long creationTime = 0;
    /**
     * Block being dug.
     */
    public Block breakingBlock = null;
    /**
     * Direction of the dig.
     */
    public BlockFace breakingBlockFace = null;
    public int pickedXPOrb = 0;
    public EntityFishingHook fishing = null;
    public long lastSkinChange;
    protected long breakingBlockTime = 0;
    protected double blockBreakProgress = 0;
    protected final BedrockSession session;
    protected final InetSocketAddress rawSocketAddress;
    protected final Map<UUID, Player> hiddenPlayers = new HashMap<>();
    protected final int chunksPerTick;
    protected final int spawnThreshold;
    protected int messageLimitCounter = 2;
    protected AtomicBoolean connected = new AtomicBoolean(true);
    protected InetSocketAddress socketAddress;
    /**
     * Whether to remove the color character in the chat of the changed player as §c §1.
     */
    protected boolean removeFormat = true;
    protected String displayName;
    protected static final int RESOURCE_PACK_CHUNK_SIZE = 8 * 1024; // 8KB
    protected Vector3 sleeping = null;
    protected int chunkLoadCount = 0;
    protected int nextChunkOrderRun = 1;
    protected Vector3 newPosition = null;
    protected int chunkRadius;
    protected int viewDistance;
    protected Position spawnPoint;
    protected SpawnPointType spawnPointType;
    /**
     * Represents the number of ticks the player has passed through the air.
     */
    protected int inAirTicks = 0;
    protected int startAirTicks = 5;
    protected AdventureSettings adventureSettings;
    protected boolean checkMovement = true;
    protected PlayerFood foodData = null;
    protected boolean enableClientCommand = true;
    protected int formWindowCount = 0;
    protected Map<Integer, FormWindow> formWindows = new Int2ObjectOpenHashMap<>();
    protected Map<Integer, FormWindow> serverSettings = new Int2ObjectOpenHashMap<>();
    /**
     * We use Google's cache to store NPC dialogs to send messages.
     * The reason is that there is a chance that the client will not respond to the dialogs sent, and in certain cases we cannot clear these dialogs, which can lead to memory leaks.
     * Unresponsive dialogs will be cleared after 5 minutes.
     */
    protected Cache<String, FormWindowDialog> dialogWindows = Caffeine.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();
    protected Map<Long, DummyBossBar> dummyBossBars = new Long2ObjectLinkedOpenHashMap<>();
    protected double lastRightClickTime = 0.0;
    protected Vector3 lastRightClickPos = null;
    protected int lastInAirTick = 0;
    private static final float ROTATION_UPDATE_THRESHOLD = 1;
    private static final float MOVEMENT_DISTANCE_THRESHOLD = 0.1f;
    private final Queue<Location> clientMovements = PlatformDependent.newMpscQueue(4);
    private final AtomicReference<Locale> locale = new AtomicReference<>(null);
    private int timeSinceRest;
    private String buttonText = "Button";
    private PermissibleBase perm = null;
    private int hash;
    private int exp = 0;
    private int expLevel = 0;
    private int enchSeed;
    private final int loaderId;
    private BlockVector3 lastBreakPosition = new BlockVector3();
    private boolean hasSeenCredits;
    private boolean wasInSoulSandCompatible;
    private float soulSpeedMultiplier = 1;
    private Entity killer = null;
    private TaskHandler delayedPosTrackingUpdate;
    protected boolean showingCredits;
    protected static final int NO_SHIELD_DELAY = 10;
    protected PlayerBlockActionData lastBlockAction;
    protected AsyncTask preLoginEventTask = null;
    protected LoginChainData loginChainData;
    /**
     * Time to play sound when player upgrades.
     */
    protected int lastPlayerdLevelUpSoundTime = 0;
    /**
     * The entity that the player attacked last.
     */
    protected Entity lastAttackEntity = null;

    /**
     * Player Fog Settings.
     */
    protected List<PlayerFogPacket.Fog> fogStack = new ArrayList<>();
    /**
     * The entity that the player is attacked last.
     */
    protected Entity lastBeAttackEntity = null;
    private final @NotNull PlayerHandle playerHandle = new PlayerHandle(this);
    protected final PlayerChunkManager playerChunkManager;
    private boolean needDimensionChangeACK = false;
    private Boolean openSignFront = null;
    protected Boolean flySneaking = false;
    /// lastUseItem System and item cooldown
    protected final HashMap<String, Integer> cooldownTickMap = new HashMap<>();
    protected final HashMap<String, Integer> lastUseItemMap = new HashMap<>(1);

    /// Inventory system
    protected int windowsCnt = 1;
    protected int closingWindowId = Integer.MIN_VALUE;
    protected final BiMap<Inventory, Integer> windows = HashBiMap.create();
    protected final BiMap<Integer, Inventory> windowIndex = windows.inverse();
    protected final Set<Integer> permanentWindows = new IntOpenHashSet();
    protected CraftingGridInventory craftingGridInventory;
    protected PlayerCursorInventory playerCursorInventory;
    protected CreativeOutputInventory creativeOutputInventory;
    /**
     * Player opens its own inventory.
     */
    protected boolean inventoryOpen;
    /**
     * Player opens its own ender chest inventory.
     */
    protected boolean enderChestOpen;
    /**
     * Player opens a fake Inventory.
     */
    protected boolean fakeInventoryOpen;

    //todo A hack to handle receiving an erroneous position after teleportation
    private Pair<Location, Long> lastTeleportMessage;

    private final @NotNull PlayerInfo info;

    @UsedByReflection
    public Player(@NotNull BedrockSession session, @NotNull PlayerInfo info) {
        super(null, new CompoundTag());
        this.info = info;
        this.session = session;
        this.perm = new PermissibleBase(this);
        this.server = Server.getInstance();
        this.lastBreak = -1;
        this.socketAddress = this.getSession().getAddress();
        this.rawSocketAddress = socketAddress;
        this.loaderId = Level.generateChunkLoaderId(this);
        this.chunksPerTick = this.server.getSettings().chunkSettings().perTickSend();
        this.spawnThreshold = this.server.getSettings().chunkSettings().spawnThreshold();
        this.spawnPoint = null;
        this.gamemode = this.server.getGamemode();
        this.setLevel(this.server.getDefaultLevel());
        this.viewDistance = this.server.getViewDistance();
        this.chunkRadius = viewDistance;
        this.boundingBox = new SimpleAxisAlignedBB(0, 0, 0, 0, 0, 0);
        this.lastSkinChange = -1;
        this.playerChunkManager = new PlayerChunkManager(this);
        this.creationTime = System.currentTimeMillis();
        this.displayName = info.getUsername();
        this.loginChainData = info.getData();
        this.uuid = info.getUniqueId();
        this.rawUUID = Binary.writeUUID(info.getUniqueId());
        this.setSkin(info.getSkin());
    }

    /**
     * Retrieves the network session for the player.
     *
     * @return the network session
     */
    @NotNull
    public BedrockSession getSession() {
        return this.session;
    }

    /**
     * Converts the server-side game mode to the corresponding network packet game mode ID.
     * This method addresses the issue where the NK spectator mode ID is 3, but the original ID is 6.
     *
     * @param gamemode the server-side game mode
     * @return the network layer game mode ID
     */
    public static int toNetworkGamemode(int gamemode) {
        return gamemode != SPECTATOR ? gamemode : GameType.SPECTATOR.ordinal();
    }

    /**
     * Continues the block breaking process.
     *
     * @param pos  the position of the block being broken
     * @param face the face of the block being broken
     */
    protected void onBlockBreakContinue(Vector3 pos, BlockFace face) {
        if (this.isBreakingBlock()) {
            var time = System.currentTimeMillis();
            Block block = this.level.getBlock(pos, false);

            double miningTimeRequired;
            if (this.breakingBlock instanceof CustomBlock customBlock) {
                miningTimeRequired = customBlock.breakTime(this.inventory.getItemInHand(), this);
            } else miningTimeRequired = this.breakingBlock.calculateBreakTime(this.inventory.getItemInHand(), this);

            if (miningTimeRequired > 0) {
                int breakTick = (int) Math.ceil(miningTimeRequired * 20);
                LevelEventPacket pk = new LevelEventPacket();
                pk.evid = LevelEventPacket.EVENT_BLOCK_UPDATE_BREAK;
                pk.x = (float) this.breakingBlock.x;
                pk.y = (float) this.breakingBlock.y;
                pk.z = (float) this.breakingBlock.z;
                pk.data = 65535 / breakTick;
                this.getLevel().addChunkPacket(this.breakingBlock.getFloorX() >> 4, this.breakingBlock.getFloorZ() >> 4, pk);
                this.level.addParticle(new PunchBlockParticle(pos, block));
                if (this.breakingBlock instanceof CustomBlock) {
                    var timeDiff = time - breakingBlockTime;
                    blockBreakProgress += timeDiff / (miningTimeRequired * 1000);
                    if (blockBreakProgress > 0.99) {
                        this.onBlockBreakAbort(pos);
                        this.onBlockBreakComplete(pos.asBlockVector3(), face);
                    }
                    breakingBlockTime = time;
                }
            }
        }
    }

    /**
     * Starts the block breaking process.
     *
     * @param pos  the position of the block being broken
     * @param face the face of the block being broken
     */
    protected void onBlockBreakStart(Vector3 pos, BlockFace face) {
        BlockVector3 blockPos = pos.asBlockVector3();
        long currentBreak = System.currentTimeMillis();
        // HACK: Client spams multiple left clicks so we need to skip them.
        if ((this.lastBreakPosition.equals(blockPos) && (currentBreak - this.lastBreak) < 10) || pos.distanceSquared(this) > 1000) {
            return;
        }

        Block target = this.level.getBlock(pos);
        PlayerInteractEvent playerInteractEvent = new PlayerInteractEvent(this, this.inventory.getItemInHand(), target, face,
                target.isAir() ? Action.LEFT_CLICK_AIR : Action.LEFT_CLICK_BLOCK);
        this.getServer().getPluginManager().callEvent(playerInteractEvent);
        if (playerInteractEvent.isCancelled()) {
            this.inventory.sendHeldItem(this);
            this.getLevel().sendBlocks(new Player[]{this}, new Block[]{target}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 0);
            if (target.getLevelBlockAtLayer(1) instanceof BlockLiquid) {
                this.getLevel().sendBlocks(new Player[]{this}, new Block[]{target.getLevelBlockAtLayer(1)}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 1);
            }
            return;
        }

        target.onTouch(pos, this.getInventory().getItemInHand(), face, 0, 0, 0, this, playerInteractEvent.getAction());

        Block block = target.getSide(face);
        if (block.getId().equals(Block.FIRE) || block.getId().equals(BlockID.SOUL_FIRE)) {
            this.level.setBlock(block, Block.get(BlockID.AIR), true);
            this.level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_EXTINGUISH_FIRE);
            return;
        }

        if (block.getId().equals(BlockID.SWEET_BERRY_BUSH) && block.isDefaultState()) {
            Item oldItem = playerInteractEvent.getItem();
            Item i = this.level.useBreakOn(block, oldItem, this, true);
            if (this.isSurvival() || this.isAdventure()) {
                this.getFoodData().exhaust(0.005);
                if (!i.equals(oldItem) || i.getCount() != oldItem.getCount()) {
                    inventory.setItemInHand(i);
                    inventory.sendHeldItem(this.getViewers().values());
                }
            }
            return;
        }

        if (!block.isBlockChangeAllowed(this)) {
            return;
        }

        if (this.isSurvival()) {
            this.breakingBlockTime = currentBreak;
            double miningTimeRequired;
            if (target instanceof CustomBlock customBlock) {
                miningTimeRequired = customBlock.breakTime(this.inventory.getItemInHand(), this);
            } else miningTimeRequired = target.calculateBreakTime(this.inventory.getItemInHand(), this);
            int breakTime = (int) Math.ceil(miningTimeRequired * 20);
            if (breakTime > 0) {
                LevelEventPacket pk = new LevelEventPacket();
                pk.evid = LevelEventPacket.EVENT_BLOCK_START_BREAK;
                pk.x = (float) pos.x;
                pk.y = (float) pos.y;
                pk.z = (float) pos.z;
                pk.data = 65535 / breakTime;
                this.getLevel().addChunkPacket(pos.getFloorX() >> 4, pos.getFloorZ() >> 4, pk);

                if (this.getLevel().isAntiXrayEnabled() && this.getLevel().getAntiXraySystem().isPreDeObfuscate()) {
                    this.getLevel().getAntiXraySystem().deObfuscateBlock(this, face, target);
                }
            }
        }

        this.breakingBlock = target;
        this.breakingBlockFace = face;
        this.lastBreak = currentBreak;
        this.lastBreakPosition = blockPos;
    }

    /**
     * Aborts the block breaking process.
     *
     * @param pos the position of the block being broken
     */
    protected void onBlockBreakAbort(Vector3 pos) {
        if (pos.distanceSquared(this) < 1000) { // same as with ACTION_START_BREAK
            LevelEventPacket pk = new LevelEventPacket();
            pk.evid = LevelEventPacket.EVENT_BLOCK_STOP_BREAK;
            pk.x = (float) pos.x;
            pk.y = (float) pos.y;
            pk.z = (float) pos.z;
            pk.data = 0;
            this.getLevel().addChunkPacket(pos.getFloorX() >> 4, pos.getFloorZ() >> 4, pk);
        }
        this.blockBreakProgress = 0;
        this.breakingBlock = null;
        this.breakingBlockFace = null;
    }

    /**
     * Completes the block breaking process.
     *
     * @param blockPos the position of the block being broken
     * @param face     the face of the block being broken
     */
    protected void onBlockBreakComplete(BlockVector3 blockPos, BlockFace face) {
        if (!this.spawned || !this.isAlive()) {
            return;
        }

        Item handItem = this.getInventory().getItemInHand();
        Item clone = handItem.clone();

        boolean canInteract = this.canInteract(blockPos.add(0.5, 0.5, 0.5), this.isCreative() ? 13 : 7);
        if (canInteract) {
            handItem = this.level.useBreakOn(blockPos.asVector3(), face, handItem, this, true);
            if (handItem != null && this.isSurvival()) {
                this.getFoodData().exhaust(0.005);
                if (handItem.equals(clone) && handItem.getCount() == clone.getCount()) {
                    return;
                }

                if (Objects.equals(clone.getId(), handItem.getId()) || handItem.isNull()) {
                    inventory.setItemInHand(handItem, false);
                } else {
                    log.debug("Tried to set item " + handItem.getId() + " but " + this.getName() + " had item " + clone.getId() + " in their hand slot");
                }
                inventory.sendHeldItem(this.getViewers().values());
            } else if (handItem == null)
                this.level.sendBlocks(new Player[]{this}, new Block[]{this.level.getBlock(blockPos.asVector3())}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 0);
            return;
        }

        inventory.sendContents(this);
        inventory.sendHeldItem(this);

        if (blockPos.distanceSquared(this) < 100) {
            Block target = this.level.getBlock(blockPos.asVector3());
            this.level.sendBlocks(new Player[]{this}, new Block[]{target}, UpdateBlockPacket.FLAG_ALL_PRIORITY);

            BlockEntity blockEntity = this.level.getBlockEntity(blockPos.asVector3());
            if (blockEntity instanceof BlockEntitySpawnable) {
                ((BlockEntitySpawnable) blockEntity).spawnTo(this);
            }
        }
    }

    /**
     * Sets the title text for the player.
     *
     * @param text the title text
     */
    private void setTitle(String text) {
        SetTitlePacket packet = new SetTitlePacket();
        packet.text = text;
        packet.type = SetTitlePacket.TYPE_TITLE;
        this.dataPacket(packet);
    }

    /**
     * Changes the player's dimension.
     *
     * @param dimension the new dimension ID
     */
    private void setDimension(int dimension) {
        ChangeDimensionPacket pk = new ChangeDimensionPacket();
        pk.dimension = dimension;
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        this.dataPacket(pk);

        this.needDimensionChangeACK = true;
    }

    /**
     * Updates the blocking flag based on the player's state.
     */
    private void updateBlockingFlag() {
        boolean shouldBlock = this.isItemCoolDownEnd("shield")
                && (this.isSneaking() || getRiding() != null)
                && (this.getInventory().getItemInHand() instanceof ItemShield || this.getOffhandInventory().getItem(0) instanceof ItemShield);

        if (isBlocking() != shouldBlock) {
            this.setBlocking(shouldBlock);
        }
    }

    /**
     * Initializes the player entity.
     */
    @Override
    protected void initEntity() {
        super.initEntity();
        Level level = null;
        if (this.namedTag.containsString("SpawnLevel")) {
            level = this.server.getLevelByName(this.namedTag.getString("SpawnLevel"));
        } else level = Server.getInstance().getDefaultLevel();
        if (this.namedTag.containsInt("SpawnX") && this.namedTag.containsInt("SpawnY") && this.namedTag.containsInt("SpawnZ")) {
            this.spawnPoint = new Position(this.namedTag.getInt("SpawnX"), this.namedTag.getInt("SpawnY"), this.namedTag.getInt("SpawnZ"), level);
        } else {
            this.spawnPoint = level.getSafeSpawn();
            log.info("Player {} cannot find the saved spawnpoint, reset the spawnpoint to {} {} {} / {}", this.getName(), this.spawnPoint.x, this.spawnPoint.y, this.spawnPoint.z, this.spawnPoint.getLevel().getName());
        }
        setDataFlag(EntityFlag.HIDDEN_WHEN_INVISIBLE);
        setDataFlag(EntityFlag.PUSH_TOWARDS_CLOSEST_SPACE);
        this.addDefaultWindows();
        this.loggedIn = true;

        // Remove deprecated spawn block level data
        if (this.namedTag.containsString("SpawnBlockLevel")) {
            this.namedTag.remove("SpawnBlockLevel");
        }
        if (this.namedTag.containsInt("SpawnBlockPositionX") && this.namedTag.containsInt("SpawnBlockPositionY") && this.namedTag.containsInt("SpawnBlockPositionZ")) {
            this.namedTag.remove("SpawnBlockPositionX");
            this.namedTag.remove("SpawnBlockPositionY");
            this.namedTag.remove("SpawnBlockPositionZ");
        }
    }

    /**
     * Executes after completing the completeLoginSequence.
     */
    protected void doFirstSpawn() {
        this.spawned = true;

        this.getSession().syncInventory();
        this.resetInventory();

        this.setEnableClientCommand(true);

        SetTimePacket setTimePacket = new SetTimePacket();
        setTimePacket.time = this.level.getTime();
        this.dataPacket(setTimePacket);

        this.noDamageTicks = 60;

        for (long index : playerChunkManager.getUsedChunks()) {
            int chunkX = Level.getHashX(index);
            int chunkZ = Level.getHashZ(index);
            for (Entity entity : this.level.getChunkEntities(chunkX, chunkZ).values()) {
                if (this != entity && !entity.closed && entity.isAlive()) {
                    entity.spawnTo(this);
                }
            }
        }

        int experience = this.getExperience();
        if (experience != 0) {
            this.sendExperience(experience);
        }

        int level = this.getExperienceLevel();
        if (level != 0) {
            this.sendExperienceLevel(this.getExperienceLevel());
        }

        // Update weather
        this.getLevel().sendWeather(this);

        // Update food level
        PlayerFood food = this.getFoodData();
        if (food.isHungry()) {
            food.sendFood();
        }

        var scoreboardManager = this.getServer().getScoreboardManager();
        if (scoreboardManager != null) { // In test environment sometimes the scoreboard level is null
            scoreboardManager.onPlayerJoin(this);
        }

        if (this.getSpawn().second() == null || this.getSpawn().second() == SpawnPointType.WORLD) {
            this.setSpawn(this.level.getSafeSpawn(), SpawnPointType.WORLD);
        } else {
            // Update compass
            SetSpawnPositionPacket pk = new SetSpawnPositionPacket();
            pk.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN;
            pk.x = this.getSpawn().first().getFloorX();
            pk.y = this.getSpawn().first().getFloorY();
            pk.z = this.getSpawn().first().getFloorZ();
            pk.dimension = this.getSpawn().first().getLevel().getDimension();
            this.dataPacket(pk);
        }

        this.sendFogStack();
        this.sendCameraPresets();

        log.debug("Send Player Spawn Status Packet to {}, wait init packet", getName());
        this.sendPlayStatus(PlayStatusPacket.PLAYER_SPAWN);

        // Teleport player after client initialization to avoid falling (x)
        // Already set immobile so no need to worry about falling
        Location pos;
        if (this.server.getSettings().baseSettings().safeSpawn() && (this.gamemode & 0x01) == 0) {
            pos = this.level.getSafeSpawn(this).getLocation();
            pos.yaw = this.yaw;
            pos.pitch = this.pitch;
        } else {
            pos = new Location(this.x, this.y, this.z, this.yaw, this.pitch, this.level);
        }
        this.teleport(pos, TeleportCause.PLAYER_SPAWN);

        if (this.getHealth() < 1) {
            this.setHealth(0);
        }

        Server.getInstance().getScheduler().scheduleDelayedTask(InternalPlugin.INSTANCE, () -> {
            this.session.getMachine().fire(SessionState.IN_GAME);
        }, 5);
    }

    /**
     * Checks the ground state of the player based on movement and updates the collision status.
     *
     * @param movX Movement in the X direction.
     * @param movY Movement in the Y direction.
     * @param movZ Movement in the Z direction.
     * @param dx   Delta X.
     * @param dy   Delta Y.
     * @param dz   Delta Z.
     */
    @Override
    protected void checkGroundState(double movX, double movY, double movZ, double dx, double dy, double dz) {
        if (!this.onGround || movX != 0 || movY != 0 || movZ != 0) {
            boolean onGround = false;

            AxisAlignedBB realBB = this.boundingBox.clone();
            realBB.setMaxY(realBB.getMinY());
            realBB.setMinY(realBB.getMinY() - 0.5);

            Block b1 = level.getTickCachedBlock(getFloorX(), getFloorY() - 1, getFloorZ());
            Block b2 = level.getTickCachedBlock(getFloorX(), getFloorY(), getFloorZ());
            Block[] blocks = {
                    level.getTickCachedBlock(getFloorX() - 1, getFloorY() - 1, getFloorZ()),
                    level.getTickCachedBlock(getFloorX() + 1, getFloorY() - 1, getFloorZ()),
                    level.getTickCachedBlock(getFloorX(), getFloorY() - 1, getFloorZ() + 1),
                    level.getTickCachedBlock(getFloorX(), getFloorY() - 1, getFloorZ() - 1),
                    level.getTickCachedBlock(getFloorX() - 1, getFloorY() - 1, getFloorZ() - 1),
                    level.getTickCachedBlock(getFloorX() + 1, getFloorY() - 1, getFloorZ() - 1),
                    level.getTickCachedBlock(getFloorX() + 1, getFloorY() - 1, getFloorZ() + 1),
                    level.getTickCachedBlock(getFloorX() - 1, getFloorY() - 1, getFloorZ() + 1)
            };
            if ((!b1.canPassThrough() && b1.collidesWithBB(realBB)) || (!b2.canPassThrough() && b2.collidesWithBB(realBB))) {
                onGround = true;
            }
            for (Block block : blocks) {
                if (!block.canPassThrough() && block.collidesWithBB(realBB)) {
                    onGround = true;
                }
            }
            this.onGround = onGround;
        }

        this.isCollided = this.onGround;
    }

    /**
     * Checks for block collisions and handles special block interactions such as portals and scaffolding.
     */
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
                case BlockID.PORTAL -> portal = true;
                case BlockID.SCAFFOLDING -> scaffolding = true;
                case BlockID.END_PORTAL -> endPortal = true;
            }

            block.onEntityCollide(this);
            block.getLevelBlockAtLayer(1).onEntityCollide(this);
        }
        AxisAlignedBB scanBoundingBox = boundingBox.getOffsetBoundingBox(0, -0.125, 0);
        scanBoundingBox.setMaxY(boundingBox.getMinY());
        Block[] scaffoldingUnder = level.getCollisionBlocks(
                scanBoundingBox,
                true, true,
                b -> b.getId().equals(BlockID.SCAFFOLDING)
        );

        setDataFlagExtend(EntityFlag.IN_SCAFFOLDING, scaffolding);
        setDataFlagExtend(EntityFlag.OVER_SCAFFOLDING, scaffoldingUnder.length > 0);
        setDataFlagExtend(EntityFlag.IN_ASCENDABLE_BLOCK, scaffolding);
        setDataFlagExtend(EntityFlag.OVER_DESCENDABLE_BLOCK, scaffoldingUnder.length > 0);

        if (endPortal) { // Handle endPortal teleport
            if (!inEndPortal) {
                inEndPortal = true;
                if (this.getRiding() == null && this.getPassengers().isEmpty()) {
                    EntityPortalEnterEvent ev = new EntityPortalEnterEvent(this, PortalType.END);
                    getServer().getPluginManager().callEvent(ev);

                    if (!ev.isCancelled()) {
                        final Position newPos = PortalHelper.moveToTheEnd(this);
                        if (newPos != null) {
                            if (newPos.getLevel().getDimension() == Level.DIMENSION_THE_END) {
                                if (teleport(newPos, TeleportCause.END_PORTAL)) {
                                    server.getScheduler().scheduleDelayedTask(new Task() {
                                        @Override
                                        public void onRun(int currentTick) {
                                            // Dirty hack to make sure chunks are loaded and generated before spawning player
                                            teleport(newPos, TeleportCause.END_PORTAL);
                                            BlockEndPortal.spawnObsidianPlatform(newPos);
                                        }
                                    }, 5);
                                }
                            } else {
                                if (!this.hasSeenCredits && !this.showingCredits) {
                                    PlayerShowCreditsEvent playerShowCreditsEvent = new PlayerShowCreditsEvent(this);
                                    this.getServer().getPluginManager().callEvent(playerShowCreditsEvent);
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

    /**
     * Checks for nearby entities and updates their state.
     */
    protected void checkNearEntities() {
        for (Entity entity : this.level.getNearbyEntities(this.boundingBox.grow(1, 0.5, 1), this)) {
            entity.scheduleUpdate();

            if (!entity.isAlive() || !this.isAlive()) {
                continue;
            }

            this.pickupEntity(entity, true);
        }
    }

    /**
     * Handles player movement and checks for invalid motion.
     *
     * @param clientPos The new position of the player.
     */
    protected void handleMovement(Location clientPos) {
        if (this.firstMove) this.firstMove = false;
        boolean invalidMotion = false;
        var revertPos = this.getLocation().clone();
        double distance = clientPos.distanceSquared(this);
        // Before check
        if (distance > 128) {
            invalidMotion = true;
        } else if (this.chunk == null || !chunk.getChunkState().canSend()) {
            IChunk chunk = this.level.getChunk(clientPos.getChunkX(), clientPos.getChunkZ(), false);
            this.chunk = chunk;
            if (this.chunk == null || !chunk.getChunkState().canSend()) {
                invalidMotion = true;
                this.nextChunkOrderRun = 0;
                if (this.chunk != null) {
                    this.chunk.removeEntity(this);
                }
            }
        }

        if (invalidMotion) {
            this.revertClientMotion(revertPos);
            this.resetClientMovement();
            return;
        }

        // Update server-side position, rotation, and bounding box
        double diffX = clientPos.getX() - this.x;
        double diffY = clientPos.getY() - this.y;
        double diffZ = clientPos.getZ() - this.z;
        this.setRotation(clientPos.getYaw(), clientPos.getPitch(), clientPos.getHeadYaw());
        this.fastMove(diffX, diffY, diffZ);

        // After check
        double corrX = this.x - clientPos.getX();
        double corrY = this.y - clientPos.getY();
        double corrZ = this.z - clientPos.getZ();
        if (this.checkMovement && (Math.abs(corrX) > 0.5 || Math.abs(corrY) > 0.5 || Math.abs(corrZ) > 0.5) && this.riding == null && !this.hasEffect(EffectType.LEVITATION) && !this.hasEffect(EffectType.SLOW_FALLING) && !server.getAllowFlight()) {
            double diff = corrX * corrX + corrZ * corrZ;
            // Relaxed judgment to avoid illegal movement detection when crossing scaffolding diagonally.
            if (diff > 1.2) {
                PlayerInvalidMoveEvent event = new PlayerInvalidMoveEvent(this, true);
                this.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled() && (invalidMotion = event.isRevert())) {
                    log.warn(this.getServer().getLanguage().tr("nukkit.player.invalidMove", this.getName()));
                }
            }
            if (invalidMotion) {
                this.setPositionAndRotation(revertPos.asVector3f().asVector3(), revertPos.getYaw(), revertPos.getPitch(), revertPos.getHeadYaw());
                this.revertClientMotion(revertPos);
                this.resetClientMovement();
                return;
            }
        }

        // Update server-side position, rotation, and bounding box
        Location last = new Location(this.lastX, this.lastY, this.lastZ, this.lastYaw, this.lastPitch, this.lastHeadYaw, this.level);
        Location now = this.getLocation();
        this.lastX = now.x;
        this.lastY = now.y;
        this.lastZ = now.z;
        this.lastYaw = now.yaw;
        this.lastPitch = now.pitch;
        this.lastHeadYaw = now.headYaw;

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
            PlayerMoveEvent ev = new PlayerMoveEvent(this, last, now);
            this.server.getPluginManager().callEvent(ev);

            if (!(invalidMotion = ev.isCancelled())) { // Yes, this is intended
                if (!now.equals(ev.getTo()) && this.riding == null) { // If plugins modify the destination
                    if (this.getGamemode() != Player.SPECTATOR)
                        this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, ev.getTo().clone(), VibrationType.TELEPORT));
                    this.teleport(ev.getTo(), null);
                } else {
                    if (this.getGamemode() != Player.SPECTATOR && (last.x != now.x || last.y != now.y || last.z != now.z)) {
                        if (this.isOnGround() && this.isGliding()) {
                            this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.clone(), VibrationType.ELYTRA_GLIDE));
                        } else if (this.isOnGround() && !(this.getSide(BlockFace.DOWN).getLevelBlock() instanceof BlockWool) && !this.isSneaking()) {
                            this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.clone(), VibrationType.STEP));
                        } else if (this.isTouchingWater()) {
                            this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.clone(), VibrationType.SWIM));
                        }
                    }
                    this.broadcastMovement(false);
                }
            } else {
                this.blocksAround = blocksAround;
                this.collisionBlocks = collidingBlocks;
            }
        }

        // Update speed
        if (this.speed == null) {
            this.speed = new Vector3(last.x - now.x, last.y - now.y, last.z - now.z);
        } else {
            this.speed.setComponents(last.x - now.x, last.y - now.y, last.z - now.z);
        }

        handleLogicInMove(invalidMotion, distance);

        // If plugin cancels move
        if (invalidMotion) {
            this.setPositionAndRotation(revertPos.asVector3f().asVector3(), revertPos.getYaw(), revertPos.getPitch(), revertPos.getHeadYaw());
            this.revertClientMotion(revertPos);
            this.resetClientMovement();
        } else {
            if (distance != 0 && this.nextChunkOrderRun > 20) {
                this.nextChunkOrderRun = 20;
            }
        }
    }

    /**
     * Offers a movement task based on the new position.
     *
     * @param newPosition the new position of the player
     */
    protected void offerMovementTask(Location newPosition) {
        var distance = newPosition.distance(this);
        var updatePosition = distance > MOVEMENT_DISTANCE_THRESHOLD; // sqrt distance
        var updateRotation = (float) Math.abs(this.getPitch() - newPosition.pitch) > ROTATION_UPDATE_THRESHOLD
                || (float) Math.abs(this.getYaw() - newPosition.yaw) > ROTATION_UPDATE_THRESHOLD
                || (float) Math.abs(this.getHeadYaw() - newPosition.headYaw) > ROTATION_UPDATE_THRESHOLD;
        var isHandle = this.isAlive() && this.spawned && !this.isSleeping() && (updatePosition || updateRotation);
        if (isHandle) {
            // TODO: Hack for receiving an error position after teleport
            long now = System.currentTimeMillis();
            if (lastTeleportMessage != null && (now - lastTeleportMessage.right()) < 200) {
                var dis = newPosition.distance(lastTeleportMessage.left());
                if (dis < MOVEMENT_DISTANCE_THRESHOLD) return;
            }
            this.newPosition = newPosition;
            this.clientMovements.offer(newPosition);
        }
    }

    /**
     * Handles logic during movement, including hunger and enchantment effects.
     *
     * @param invalidMotion whether the motion is invalid
     * @param distance      the distance moved
     */
    protected void handleLogicInMove(boolean invalidMotion, double distance) {
        if (!invalidMotion) {
            // Handle hunger update
            if (this.getFoodData().isEnabled() && this.getServer().getDifficulty() > 0) {
                // Update food exhaustion level
                if (distance >= 0.05) {
                    double jump = 0;
                    double swimming = this.isInsideOfWater() ? 0.01 * distance : 0;
                    double distance2 = distance;
                    if (swimming != 0) distance2 = 0;
                    if (this.isSprinting()) {  // Running
                        if (this.inAirTicks == 3 && swimming == 0) {
                            jump = 0.2;
                        }
                        this.getFoodData().exhaust(0.1 * distance2 + jump + swimming);
                    } else {
                        if (this.inAirTicks == 3 && swimming == 0) {
                            jump = 0.05;
                        }
                        this.getFoodData().exhaust(jump + swimming);
                    }
                }
            }

            // Handle Frost Walker enchantment
            Enchantment frostWalker = inventory.getBoots().getEnchantment(Enchantment.ID_FROST_WALKER);
            if (frostWalker != null && frostWalker.getLevel() > 0 && !this.isSpectator() && this.y >= 1 && this.y <= 255) {
                int radius = 2 + frostWalker.getLevel();
                for (int coordX = this.getFloorX() - radius; coordX < this.getFloorX() + radius + 1; coordX++) {
                    for (int coordZ = this.getFloorZ() - radius; coordZ < this.getFloorZ() + radius + 1; coordZ++) {
                        Block block = level.getBlock(coordX, this.getFloorY() - 1, coordZ);
                        int layer = 0;
                        if ((!block.getId().equals(Block.WATER) && (!block.getId().equals(Block.FLOWING_WATER) ||
                                block.getPropertyValue(CommonBlockProperties.LIQUID_DEPTH) != 0)) || !block.up().isAir()) {
                            block = block.getLevelBlockAtLayer(1);
                            layer = 1;
                            if ((!block.getId().equals(Block.WATER) && (!block.getId().equals(Block.FLOWING_WATER) ||
                                    block.getPropertyValue(CommonBlockProperties.LIQUID_DEPTH) != 0)) || !block.up().isAir()) {
                                continue;
                            }
                        }
                        WaterFrostEvent ev = new WaterFrostEvent(block, this);
                        server.getPluginManager().callEvent(ev);
                        if (!ev.isCancelled()) {
                            level.setBlock(block, layer, Block.get(Block.FROSTED_ICE), true, false);
                            level.scheduleUpdate(level.getBlock(block, layer), ThreadLocalRandom.current().nextInt(20, 40));
                        }
                    }
                }
            }

            // Handle Soul Speed enchantment
            int soulSpeedLevel = this.getInventory().getBoots().getEnchantmentLevel(Enchantment.ID_SOUL_SPEED);
            if (soulSpeedLevel > 0) {
                Block levelBlock = this.getLevelBlock();
                this.soulSpeedMultiplier = (soulSpeedLevel * 0.105f) + 1.3f;

                // Level block check is required because of soul sand being 1 pixel shorter than normal blocks
                boolean isSoulSandCompatible = levelBlock.isSoulSpeedCompatible() || levelBlock.down().isSoulSpeedCompatible();

                if (this.wasInSoulSandCompatible && !isSoulSandCompatible) {
                    this.wasInSoulSandCompatible = false;
                    this.setMovementSpeed(this.getMovementSpeed() / this.soulSpeedMultiplier);
                } else if (!this.wasInSoulSandCompatible && isSoulSandCompatible) {
                    this.wasInSoulSandCompatible = true;
                    this.setMovementSpeed(this.getMovementSpeed() * this.soulSpeedMultiplier);
                }
            }
        }
    }

    /**
     * Resets the client's movement state.
     */
    protected void resetClientMovement() {
        this.newPosition = null;
        this.positionChanged = false;
    }

    /**
     * Reverts the client's motion to the original position.
     *
     * @param originalPos the original position to revert to
     */
    protected void revertClientMotion(Location originalPos) {
        this.lastX = originalPos.getX();
        this.lastY = originalPos.getY();
        this.lastZ = originalPos.getZ();
        this.lastYaw = originalPos.getYaw();
        this.lastPitch = originalPos.getPitch();

        Vector3 syncPos = originalPos.add(0, 0.00001, 0);
        this.sendPosition(syncPos, originalPos.getYaw(), originalPos.getPitch(), MovePlayerPacket.MODE_RESET);

        if (this.speed == null) {
            this.speed = new Vector3(0, 0, 0);
        } else {
            this.speed.setComponents(0, 0, 0);
        }
    }

    /**
     * Processes the LOGIN\_PACKET.
     */
    public void processLogin() {
        if (this.hasPermission(Server.BROADCAST_CHANNEL_USERS)) {
            this.server.getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_USERS, this);
        }
        if (this.hasPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE)) {
            this.server.getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE, this);
        }

        Player oldPlayer = null;
        for (Player p : new ArrayList<>(this.server.getOnlinePlayers().values())) {
            if (p != this && p.getName().equalsIgnoreCase(this.getName()) || this.getUniqueId().equals(p.getUniqueId())) {
                oldPlayer = p;
                break;
            }
        }
        CompoundTag nbt;
        if (oldPlayer != null) {
            oldPlayer.saveNBT();
            nbt = oldPlayer.namedTag;
            oldPlayer.close("disconnectionScreen.loggedinOtherLocation");
        } else {
            boolean existData = Server.getInstance().hasOfflinePlayerData(this.getName());
            if (existData) {
                nbt = this.server.getOfflinePlayerData(this.getName(), false);
            } else {
                nbt = this.server.getOfflinePlayerData(this.uuid, true);
            }
        }

        if (nbt == null) {
            this.close(this.getLeaveMessage(), "Invalid data");
            return;
        }

        server.updateName(this.info);

        this.playedBefore = (nbt.getLong("lastPlayed") - nbt.getLong("firstPlayed")) > 1;

        nbt.putString("NameTag", this.getName());

        int exp = nbt.getInt("EXP");
        int expLevel = nbt.getInt("expLevel");
        this.setExperience(exp, expLevel);

        this.gamemode = nbt.getInt("playerGameType") & 0x03;
        if (this.server.getForceGamemode()) {
            this.gamemode = this.server.getGamemode();
            nbt.putInt("playerGameType", this.gamemode);
        }

        this.adventureSettings = new AdventureSettings(this);
        this.adventureSettings.init(nbt);

        Level level;
        if ((level = this.server.getLevelByName(nbt.getString("Level"))) == null) {
            this.setLevel(this.server.getDefaultLevel());
            nbt.putString("Level", this.level.getName());
            Position spawnLocation = this.level.getSafeSpawn();
            nbt.getList("Pos", DoubleTag.class)
                    .add(new DoubleTag(spawnLocation.x))
                    .add(new DoubleTag(spawnLocation.y))
                    .add(new DoubleTag(spawnLocation.z));
        } else {
            this.setLevel(level);
        }

        for (var e : nbt.getCompound("Achievements").getEntrySet()) {
            if (!(e.getValue() instanceof ByteTag)) {
                continue;
            }

            if (((ByteTag) e.getValue()).getData() > 0) {
                this.achievements.add(e.getKey());
            }
        }

        nbt.putLong("lastPlayed", System.currentTimeMillis() / 1000);

        UUID uuid = getUniqueId();
        nbt.putLong("UUIDLeast", uuid.getLeastSignificantBits());
        nbt.putLong("UUIDMost", uuid.getMostSignificantBits());

        if (this.server.getAutoSave()) {
            this.server.saveOfflinePlayerData(this.uuid, nbt, true);
        }

        ListTag<DoubleTag> posList = nbt.getList("Pos", DoubleTag.class);

        super.init(this.level.getChunk((int) posList.get(0).data >> 4, (int) posList.get(2).data >> 4, true), nbt);

        if (!this.namedTag.contains("foodLevel")) {
            this.namedTag.putInt("foodLevel", 20);
        }
        int foodLevel = this.namedTag.getInt("foodLevel");
        if (!this.namedTag.contains("foodSaturationLevel")) {
            this.namedTag.putFloat("foodSaturationLevel", 20);
        }
        float foodSaturationLevel = this.namedTag.getFloat("foodSaturationLevel");
        this.foodData = new PlayerFood(this, foodLevel, foodSaturationLevel);

        if (this.isSpectator()) {
            this.onGround = false;
        }

        if (this.namedTag.contains("enchSeed")) {
            this.enchSeed = this.namedTag.getInt("enchSeed");
        } else {
            this.regenerateEnchantmentSeed();
            this.namedTag.putInt("enchSeed", this.enchSeed);
        }

        if (!this.namedTag.contains("TimeSinceRest")) {
            this.namedTag.putInt("TimeSinceRest", 0);
        }
        this.timeSinceRest = this.namedTag.getInt("TimeSinceRest");

        if (!this.namedTag.contains("HasSeenCredits")) {
            this.namedTag.putBoolean("HasSeenCredits", false);
        }
        this.hasSeenCredits = this.namedTag.getBoolean("HasSeenCredits");

        // The following two lists correspond to each other
        if (!this.namedTag.contains("fogIdentifiers")) {
            this.namedTag.putList("fogIdentifiers", new ListTag<StringTag>());
        }
        if (!this.namedTag.contains("userProvidedFogIds")) {
            this.namedTag.putList("userProvidedFogIds", new ListTag<StringTag>());
        }
        var fogIdentifiers = this.namedTag.getList("fogIdentifiers", StringTag.class);
        var userProvidedFogIds = this.namedTag.getList("userProvidedFogIds", StringTag.class);
        for (int i = 0; i < fogIdentifiers.size(); i++) {
            this.fogStack.add(i, new PlayerFogPacket.Fog(Identifier.tryParse(fogIdentifiers.get(i).data), userProvidedFogIds.get(i).data));
        }

        if (!this.server.getSettings().playerSettings().checkMovement()) {
            this.checkMovement = false;
        }

        log.info(server.getLanguage().tr("nukkit.player.logIn",
                TextFormat.AQUA + this.getName() + TextFormat.WHITE,
                this.getAddress(),
                String.valueOf(this.getPort()),
                String.valueOf(this.getId()),
                this.level.getName(),
                String.valueOf(NukkitMath.round(this.x, 4)),
                String.valueOf(NukkitMath.round(this.y, 4)),
                String.valueOf(NukkitMath.round(this.z, 4))));
    }

    /**
     * Gets the safe spawn location for the player.
     *
     * @return the safe spawn location
     */
    public Vector3 getSafeSpawn() {
        Vector3 worldSpawnPoint;
        if (this.spawnPoint == null) {
            worldSpawnPoint = this.server.getDefaultLevel().getSafeSpawn();
        } else {
            worldSpawnPoint = spawnPoint;
        }
        return worldSpawnPoint;
    }

    /**
     * Called after the player client has been locally initialized.
     */
    protected void onPlayerLocallyInitialized() {
        if (locallyInitialized) return;
        locallyInitialized = true;

        // Initialize entity data properties
        this.setDataProperty(NAME, info.getUsername(), false);
        this.setDataProperty(NAMETAG_ALWAYS_SHOW, 1, false);

        PlayerJoinEvent playerJoinEvent = new PlayerJoinEvent(this,
                new TranslationContainer(TextFormat.YELLOW + "%multiplayer.player.joined", new String[]{
                        this.getDisplayName()
                })
        );

        this.server.getPluginManager().callEvent(playerJoinEvent);

        if (!playerJoinEvent.getJoinMessage().toString().trim().isEmpty()) {
            this.server.broadcastMessage(playerJoinEvent.getJoinMessage());
        }

    /*
      After initializing the player client, we send the game mode to address the issue of incorrect
      sprint speed in spectator mode. Only after the player client enters the game display is spectator mode set,
      and the sprint speed behaves normally. We force an update of the game mode to ensure that the client receives the
      mode update packet.
     */
        this.setGamemode(this.gamemode, false, null, true);
        this.sendData(this.hasSpawned.values().toArray(Player.EMPTY_ARRAY), entityDataMap);
        this.spawnToAll();
        this.refreshBlockEntity(1);
    }

    /**
     * Determines if the respawn block is valid. If the respawn anchor is valid, respawn at the anchor or in bed.
     * If the player has neither, respawn at the server spawn point.
     *
     * @param block the block to check
     * @return true if the block is a valid respawn block, false otherwise
     */
    protected boolean isValidRespawnBlock(Block block) {
        if (block.getId().equals(BlockID.RESPAWN_ANCHOR) && block.getLevel().getDimension() == Level.DIMENSION_NETHER) {
            BlockRespawnAnchor anchor = (BlockRespawnAnchor) block;
            return anchor.getCharge() > 0;
        }
        if (block.getId().equals(BlockID.BED) && block.getLevel().getDimension() == Level.DIMENSION_OVERWORLD) {
            BlockBed bed = (BlockBed) block;
            return bed.isBedValid();
        }

        return false;
    }

    /**
     * Checks if the player is banned.
     *
     * @return true if the player is banned, false otherwise
     */
    @Override
    public boolean isBanned() {
        return this.server.getNameBans().isBanned(this.getName());
    }

    /**
     * Respawns the player. If the server is in hardcore mode, the player is banned.
     */
    protected void respawn() {
        // The player can't respawn if the server is hardcore
        if (this.server.isHardcore()) {
            this.setBanned(true);
            return;
        }

        this.resetInventory();

        // Determine the spawn point
        Pair<Position, SpawnPointType> spawnPair = this.getSpawn();
        PlayerRespawnEvent playerRespawnEvent = new PlayerRespawnEvent(this, spawnPair);
        if (spawnPair.right() == SpawnPointType.BLOCK) { // Block spawn
            Block spawnBlock = playerRespawnEvent.getRespawnPosition().first().getLevelBlock();
            if (spawnBlock != null && isValidRespawnBlock(spawnBlock)) {
                // Handle RESPAWN_ANCHOR state change when consume charge is true
                if (spawnBlock.getId().equals(BlockID.RESPAWN_ANCHOR)) {
                    BlockRespawnAnchor respawnAnchor = (BlockRespawnAnchor) spawnBlock;
                    int charge = respawnAnchor.getCharge();
                    if (charge > 0) {
                        respawnAnchor.setCharge(charge - 1);
                        respawnAnchor.getLevel().setBlock(respawnAnchor, spawnBlock);
                        respawnAnchor.getLevel().scheduleUpdate(respawnAnchor, 10);
                        respawnAnchor.getLevel().addSound(this, Sound.RESPAWN_ANCHOR_DEPLETE, 1, 1, this);
                    }
                }
            } else { // Block not available
                Position defaultSpawn = this.getServer().getDefaultLevel().getSpawnLocation();
                this.setSpawn(defaultSpawn, SpawnPointType.WORLD);
                playerRespawnEvent.setRespawnPosition(Pair.of(defaultSpawn, SpawnPointType.WORLD));
                // Notify player that the spawn point is not valid
                sendMessage(new TranslationContainer(TextFormat.GRAY + "%tile." + (this.getLevel().getDimension() == Level.DIMENSION_OVERWORLD ? "bed" : "respawn_anchor") + ".notValid"));
            }
        }

        this.server.getPluginManager().callEvent(playerRespawnEvent);
        Position respawnPos = playerRespawnEvent.getRespawnPosition().first();

        this.sendExperience();
        this.sendExperienceLevel();

        this.setSprinting(false);
        this.setSneaking(false);

        this.setDataProperty(Player.AIR_SUPPLY, 400, false);
        this.fireTicks = 0;
        this.collisionBlocks = null;
        this.noDamageTicks = 60;

        this.removeAllEffects();
        this.setHealth(this.getMaxHealth());
        this.getFoodData().setFood(20, 20);

        this.sendData(this);

        this.setMovementSpeed(DEFAULT_SPEED);

        this.getAdventureSettings().update();
        this.inventory.sendContents(this);
        this.inventory.sendArmorContents(this);
        this.offhandInventory.sendContents(this);
        this.teleport(Location.fromObject(respawnPos.add(0, this.getEyeHeight(), 0), respawnPos.level), TeleportCause.PLAYER_SPAWN);
        this.spawnToAll();
    }

    /**
     * Checks and updates the player's chunk.
     */
    @Override
    protected void checkChunks() {
        if (this.chunk == null || (this.chunk.getX() != ((int) this.x >> 4) || this.chunk.getZ() != ((int) this.z >> 4))) {
            if (this.chunk != null) {
                this.chunk.removeEntity(this);
            }
            this.chunk = this.level.getChunk((int) this.x >> 4, (int) this.z >> 4, true);

            if (!this.justCreated) {
                Map<Integer, Player> newChunk = this.level.getChunkPlayers((int) this.x >> 4, (int) this.z >> 4);
                newChunk.remove(this.getLoaderId());

                for (Player player : new ArrayList<>(this.hasSpawned.values())) {
                    if (!newChunk.containsKey(player.getLoaderId())) {
                        this.despawnFrom(player);
                    } else {
                        newChunk.remove(player.getLoaderId());
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

    /**
     * Sends the play status to the client.
     *
     * @param status the play status
     */
    protected void sendPlayStatus(int status) {
        sendPlayStatus(status, false);
    }

    /**
     * Sends the play status to the client, with an option for immediate sending.
     *
     * @param status    the play status
     * @param immediate whether to send immediately
     */
    protected void sendPlayStatus(int status, boolean immediate) {
        PlayStatusPacket pk = new PlayStatusPacket();
        pk.status = status;

        if (immediate) {
            this.dataPacketImmediately(pk);
        } else {
            this.dataPacket(pk);
        }
    }

    /**
     * Forces the client to send empty chunks.
     */
    protected void forceSendEmptyChunks() {
        int chunkPositionX = this.getFloorX() >> 4;
        int chunkPositionZ = this.getFloorZ() >> 4;
        for (int x = -chunkRadius; x < chunkRadius; x++) {
            for (int z = -chunkRadius; z < chunkRadius; z++) {
                LevelChunkPacket chunk = new LevelChunkPacket();
                chunk.chunkX = chunkPositionX + x;
                chunk.chunkZ = chunkPositionZ + z;
                chunk.data = EmptyArrays.EMPTY_BYTES;
                this.dataPacket(chunk);
            }
        }
    }

    /**
     * Adds default windows to the player's inventory.
     */
    protected void addDefaultWindows() {
        this.craftingGridInventory = new CraftingGridInventory(this);
        this.playerCursorInventory = new PlayerCursorInventory(this);
        this.creativeOutputInventory = new CreativeOutputInventory(this);

        this.addWindow(this.getInventory(), SpecialWindowId.PLAYER.getId());
        this.getInventory().open(this);
        this.permanentWindows.add(SpecialWindowId.PLAYER.getId());

        this.addWindow(this.getCreativeOutputInventory(), SpecialWindowId.CREATIVE.getId());
        this.getCreativeOutputInventory().open(this);
        this.permanentWindows.add(SpecialWindowId.CREATIVE.getId());

        this.addWindow(this.getOffhandInventory(), SpecialWindowId.OFFHAND.getId());
        this.getOffhandInventory().open(this);
        this.permanentWindows.add(SpecialWindowId.OFFHAND.getId());

        this.addWindow(this.getCraftingGrid(), SpecialWindowId.NONE.getId());
        this.getCraftingGrid().open(this);
        this.permanentWindows.add(SpecialWindowId.NONE.getId());

        this.addWindow(this.getCursorInventory(), SpecialWindowId.CURSOR.getId());
        this.getCursorInventory().open(this);
        this.permanentWindows.add(SpecialWindowId.CURSOR.getId());

        this.addWindow(this.getEnderChestInventory(), SpecialWindowId.ENDER_CHEST.getId());
        this.permanentWindows.add(SpecialWindowId.ENDER_CHEST.getId());
    }

    /**
     * Gets the base offset for the player.
     *
     * @return the base offset
     */
    @Override
    protected float getBaseOffset() {
        return super.getBaseOffset();
    }

    /**
     * Handles blocking an entity's attack.
     *
     * @param entity  the attacking entity
     * @param e       the damage event
     * @param animate whether to animate the block
     */
    @Override
    protected void onBlock(Entity entity, EntityDamageEvent e, boolean animate) {
        super.onBlock(entity, e, animate);
        if (e.isBreakShield()) {
            this.setItemCoolDown(e.getShieldBreakCoolDown(), "shield");
        }
        if (animate) {
            this.setDataFlag(EntityFlag.BLOCKED_USING_DAMAGED_SHIELD, true);
            this.getServer().getScheduler().scheduleTask(InternalPlugin.INSTANCE, () -> {
                if (this.isOnline()) {
                    this.setDataFlag(EntityFlag.BLOCKED_USING_DAMAGED_SHIELD, false);
                }
            });
        }
    }

    /**
     * Gets the step height for the player.
     *
     * @return the step height
     */
    @Override
    public double getStepHeight() {
        return 0.6f;
    }

    /**
     * Gets the last entity that attacked the player.
     *
     * @return the last attacking entity
     */
    public Entity getLastAttackEntity() {
        return lastAttackEntity;
    }

    /**
     * Gets the last entity that the player attacked.
     *
     * @return the last attacked entity
     */
    public Entity getLastBeAttackEntity() {
        return lastBeAttackEntity;
    }

    /**
     * Returns the speed increase multiplier brought by the SOUL_SPEED Enchantment.
     *
     * @return the speed increase multiplier
     */
    public float getSoulSpeedMultiplier() {
        return this.soulSpeedMultiplier;
    }

    /**
     * Returns the value of {@link Player#lastInAirTick}, representing the last server tick the player was in the air.
     *
     * @return the last server tick the player was in the air
     */
    public int getLastInAirTick() {
        return this.lastInAirTick;
    }

    /**
     * Gets the player's leave message.
     *
     * @return the leave message
     */
    public TranslationContainer getLeaveMessage() {
        return new TranslationContainer(TextFormat.YELLOW + "%multiplayer.player.left", this.getDisplayName());
    }

    /**
     * Sets the banned status of the player.
     *
     * @param value True to ban the player, false to unban.
     */
    public void setBanned(boolean value) {
        if (value) {
            this.server.getNameBans().addBan(this.getName(), null, null, null);
            this.kick(PlayerKickEvent.Reason.NAME_BANNED, "Banned by admin");
        } else {
            this.server.getNameBans().remove(this.getName());
        }
    }

    /**
     * Checks if the player is whitelisted.
     *
     * @return True if the player is whitelisted, otherwise false.
     */
    @Override
    public boolean isWhitelisted() {
        return this.server.isWhitelisted(this.getName().toLowerCase(Locale.ENGLISH));
    }

    /**
     * Sets the whitelisted status of the player.
     *
     * @param value True to whitelist the player, false to remove from whitelist.
     */
    @Override
    public void setWhitelisted(boolean value) {
        if (value) {
            this.server.addWhitelist(this.getName().toLowerCase(Locale.ENGLISH));
        } else {
            this.server.removeWhitelist(this.getName().toLowerCase(Locale.ENGLISH));
        }
    }

    /**
     * Gets the player instance.
     *
     * @return The player instance.
     */
    @Override
    public Player getPlayer() {
        return this;
    }

    /**
     * Gets the timestamp of the first time the player joined the server.
     *
     * @return The timestamp of the first time the player joined, or null if not available.
     */
    @Override
    public Long getFirstPlayed() {
        return this.namedTag != null ? this.namedTag.getLong("firstPlayed") : null;
    }

    /**
     * Gets the timestamp of the last time the player joined the server.
     *
     * @return The timestamp of the last time the player joined, or null if not available.
     */
    @Override
    public Long getLastPlayed() {
        return this.namedTag != null ? this.namedTag.getLong("lastPlayed") : null;
    }

    /**
     * Checks if the player has played before.
     *
     * @return True if the player has played before, otherwise false.
     */
    @Override
    public boolean hasPlayedBefore() {
        return this.playedBefore;
    }

    /**
     * Gets the player's permission settings.
     *
     * @return the player's permission settings
     */
    public AdventureSettings getAdventureSettings() {
        return adventureSettings;
    }

    /**
     * Sets the player's permission settings, corresponding to the game's player permissions settings.
     *
     * @param adventureSettings the player's permission settings
     */
    public void setAdventureSettings(AdventureSettings adventureSettings) {
        this.adventureSettings = adventureSettings.clone();
        this.adventureSettings.update();
    }

    /**
     * Sets {@link #inAirTicks} to 0.
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
     * Sets whether the player is allowed to modify the world. After setting, the player is not allowed to dig blocks but can place them.
     *
     * @param value whether to allow modification of the world
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
     * Sets whether the player is allowed to interact with the world/containers.
     *
     * @param value      whether to allow interaction with the world
     * @param containers whether to allow interaction with containers
     */
    public void setAllowInteract(boolean value, boolean containers) {
        this.getAdventureSettings().set(Type.WORLD_IMMUTABLE, !value);
        this.getAdventureSettings().set(Type.DOORS_AND_SWITCHED, value);
        this.getAdventureSettings().set(Type.OPEN_CONTAINERS, containers);
        this.getAdventureSettings().update();
    }

    /**
     * Sets the auto-jump setting for the player.
     *
     * @param value True to enable auto-jump, false to disable.
     */
    public void setAutoJump(boolean value) {
        this.getAdventureSettings().set(Type.AUTO_JUMP, value);
        this.getAdventureSettings().update();
    }

    /**
     * Checks if the auto-jump setting is enabled for the player.
     *
     * @return True if auto-jump is enabled, otherwise false.
     */
    public boolean hasAutoJump() {
        return this.getAdventureSettings().get(Type.AUTO_JUMP);
    }

    /**
     * Spawns the player to another player.
     *
     * @param player The player to spawn to.
     */
    @Override
    public void spawnTo(Player player) {
        if (player.spawned && this.isAlive() && player.getLevel() == this.level && player.canSee(this)/* && !this.isSpectator()*/) {
            super.spawnTo(player);

            if (this.isSpectator()) {
                // Send the spectator game mode to the other player so that the client correctly renders the player entity
                var pk = new UpdatePlayerGameTypePacket();
                pk.gameType = GameType.SPECTATOR;
                pk.entityId = this.getId();
                player.dataPacket(pk);
            }
        }
    }

    /**
     * Gets the server instance.
     *
     * @return The server instance.
     */
    @Override
    public Server getServer() {
        return this.server;
    }

    /**
     * Gets {@link #removeFormat}.
     *
     * @return whether to remove the formatting character
     */
    public boolean getRemoveFormat() {
        return removeFormat;
    }

    /**
     * Sets {@link #removeFormat} to the specified value.
     *
     * @param remove whether to remove the formatting character
     */
    public void setRemoveFormat(boolean remove) {
        this.removeFormat = remove;
    }

    public void setRemoveFormat() {
        this.setRemoveFormat(true);
    }

    /**
     * Checks whether the player can see the specified player.
     *
     * @param player the player to check
     * @return whether the player can see the specified player
     */
    public boolean canSee(Player player) {
        return !this.hiddenPlayers.containsKey(player.getUniqueId());
    }

    /**
     * Hides the specified player from the view of the current player instance.
     *
     * @param player The player to hide.
     */
    public void hidePlayer(Player player) {
        if (this == player) {
            return;
        }
        this.hiddenPlayers.put(player.getUniqueId(), player);
        player.despawnFrom(this);
    }

    /**
     * Shows the specified player from the view of the current player instance.
     *
     * @param player The player to show.
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

    /**
     * Determines if the player can collide with a specific entity.
     *
     * @param entity The entity to check collision with.
     * @return False, as players cannot collide with entities.
     */
    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    /**
     * Determines if the player can collide with other entities or blocks.
     *
     * @return True if the player is not in spectator mode, otherwise false.
     */
    @Override
    public boolean canCollide() {
        return gamemode != SPECTATOR;
    }

    /**
     * Resets the fall distance of the player.
     */
    @Override
    public void resetFallDistance() {
        super.resetFallDistance();
        if (this.inAirTicks != 0) {
            this.startAirTicks = 5;
        }
        this.inAirTicks = 0;
        this.highestPosition = this.y;
    }

    /**
     * Checks if the player is currently online.
     *
     * @return True if the player is connected and logged in, otherwise false.
     */
    @Override
    public boolean isOnline() {
        return this.connected.get() && this.loggedIn;
    }

    /**
     * Checks if the player has operator status.
     *
     * @return True if the player is an operator, otherwise false.
     */
    @Override
    public boolean isOp() {
        return this.server.isOp(this.getName());
    }

    /**
     * Sets the operator status of the player.
     *
     * @param value True to grant operator status, false to revoke it.
     */
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

    /**
     * Checks if a specific permission is set for the player.
     *
     * @param name The name of the permission.
     * @return True if the permission is set, otherwise false.
     */
    @Override
    public boolean isPermissionSet(String name) {
        return this.perm.isPermissionSet(name);
    }

    /**
     * Checks if a specific permission is set for the player.
     *
     * @param permission The permission to check.
     * @return True if the permission is set, otherwise false.
     */
    @Override
    public boolean isPermissionSet(Permission permission) {
        return this.perm.isPermissionSet(permission);
    }

    /**
     * Checks if the player has a specific permission.
     *
     * @param name The name of the permission.
     * @return True if the player has the permission, otherwise false.
     */
    @Override
    public boolean hasPermission(String name) {
        return this.perm != null && this.perm.hasPermission(name);
    }

    /**
     * Checks if the player has a specific permission.
     *
     * @param permission The permission to check.
     * @return True if the player has the permission, otherwise false.
     */
    @Override
    public boolean hasPermission(Permission permission) {
        return this.perm.hasPermission(permission);
    }

    /**
     * Adds a permission attachment to the player.
     *
     * @param plugin The plugin to associate with the attachment.
     * @return The created permission attachment.
     */
    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return this.addAttachment(plugin, null);
    }

    /**
     * Adds a permission attachment to the player with a specific name.
     *
     * @param plugin The plugin to associate with the attachment.
     * @param name   The name of the permission.
     * @return The created permission attachment.
     */
    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name) {
        return this.addAttachment(plugin, name, null);
    }

    /**
     * Adds a permission attachment to the player with a specific name and value.
     *
     * @param plugin The plugin to associate with the attachment.
     * @param name   The name of the permission.
     * @param value  The value of the permission.
     * @return The created permission attachment.
     */
    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, Boolean value) {
        return this.perm.addAttachment(plugin, name, value);
    }

    /**
     * Removes a permission attachment from the player.
     *
     * @param attachment The permission attachment to remove.
     */
    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        this.perm.removeAttachment(attachment);
    }

    /**
     * Recalculates the player's permissions.
     */
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

        if (this.isEnableClientCommand() && spawned) {
            this.getSession().syncAvailableCommands();
        }
    }

    /**
     * Checks if client commands are enabled for the player.
     *
     * @return True if client commands are enabled, otherwise false.
     */
    public boolean isEnableClientCommand() {
        return this.enableClientCommand;
    }

    /**
     * Sets whether client commands are enabled for the player.
     *
     * @param enable True to enable client commands, false to disable.
     */
    public void setEnableClientCommand(boolean enable) {
        this.enableClientCommand = enable;
        this.getSession().setEnableClientCommand(enable);
    }

    /**
     * Gets the effective permissions of the player.
     *
     * @return A map of the player's effective permissions.
     */
    @Override
    public Map<String, PermissionAttachmentInfo> getEffectivePermissions() {
        return this.perm.getEffectivePermissions();
    }

    /**
     * Checks if the entity is a player.
     *
     * @return True, as this entity is a player.
     */
    @Override
    public boolean isPlayer() {
        return true;
    }

    /**
     * Casts the entity to a player.
     *
     * @return The player instance.
     */
    @Override
    public Player asPlayer() {
        return this;
    }

    /**
     * Checks if the entity is an entity.
     *
     * @return True, as this entity is an entity.
     */
    @Override
    public boolean isEntity() {
        return true;
    }

    /**
     * Casts the entity to an entity.
     *
     * @return The entity instance.
     */
    @Override
    public Entity asEntity() {
        return this;
    }

    /**
     * Removes an achievement from the player.
     *
     * @param achievementId The ID of the achievement to remove.
     */
    public void removeAchievement(String achievementId) {
        achievements.remove(achievementId);
    }

    /**
     * Checks if the player has a specific achievement.
     *
     * @param achievementId The ID of the achievement to check.
     * @return True if the player has the achievement, otherwise false.
     */
    public boolean hasAchievement(String achievementId) {
        return achievements.contains(achievementId);
    }

    /**
     * Checks if the player is connected.
     *
     * @return True if the player is connected, otherwise false.
     */
    public boolean isConnected() {
        return connected.get();
    }

    /**
     * Gets the display name of the player.
     *
     * @return The display name.
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Changes the name displayed during player chat and in the server player list.
     * Does not affect the player parameter name of the command, nor the player header display name.
     *
     * @param displayName The display name.
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        if (this.spawned) {
            this.server.updatePlayerListData(this.getUniqueId(), this.getId(), this.getDisplayName(), this.getSkin(), this.getLoginChainData().getXUID());
        }
    }

    @Override
    public void setSkin(Skin skin) {
        super.setSkin(skin);
        if (this.spawned) {
            var skinPacket = new PlayerSkinPacket();
            skinPacket.uuid = this.getUniqueId();
            skinPacket.skin = this.getSkin();
            skinPacket.newSkinName = this.getSkin().getSkinId();
            skinPacket.oldSkinName = "";
            Server.broadcastPacket(Server.getInstance().getOnlinePlayers().values(), skinPacket);
        }
    }

    /**
     * Gets the raw address.
     *
     * @return The raw address.
     */
    public String getRawAddress() {
        return this.rawSocketAddress.getAddress().getHostAddress();
    }

    /**
     * Gets the raw port.
     *
     * @return The raw port.
     */
    public int getRawPort() {
        return this.rawSocketAddress.getPort();
    }

    /**
     * Closes all form windows.
     */
    public void closeFormWindows() {
        this.formWindows.clear();
        this.dataPacket(new ClientboundCloseFormPacket());
    }

    /**
     * Gets the raw socket address.
     *
     * @return {@link InetSocketAddress}
     */
    public InetSocketAddress getRawSocketAddress() {
        return this.rawSocketAddress;
    }

    /**
     * Gets the address. If WaterdogPE compatibility is enabled, the address is modified to be WaterdogPE compatible; otherwise, it is the same as {@link #rawSocketAddress}.
     *
     * @return {@link String}
     */
    public String getAddress() {
        return this.socketAddress.getAddress().getHostAddress();
    }

    /**
     * @see #getRawPort
     */
    public int getPort() {
        return this.socketAddress.getPort();
    }

    /**
     * Gets the socket address. If WaterdogPE compatibility is enabled, the socket address is modified to be WaterdogPE compatible; otherwise, it is the same as {@link #rawSocketAddress}.
     *
     * @return {@link InetSocketAddress}
     */
    public InetSocketAddress getSocketAddress() {
        return this.socketAddress;
    }

    /**
     * Gets the position where the client player will move in the next tick.
     *
     * @return the next position
     */
    public Position getNextPosition() {
        return this.newPosition != null ? new Position(this.newPosition.x, this.newPosition.y, this.newPosition.z, this.level) : this.getPosition();
    }

    /**
     * Checks whether the player is sleeping.
     *
     * @return boolean
     */
    public boolean isSleeping() {
        return this.sleeping != null;
    }

    /**
     * @return {@link #inAirTicks}
     */
    public int getInAirTicks() {
        return this.inAirTicks;
    }

    /**
     * Returns whether the player is currently using an item (right-click and hold).
     *
     * @param itemId the item ID
     * @return boolean
     */
    public boolean isUsingItem(String itemId) {
        return getLastUseTick(itemId) != -1 && this.getDataFlag(EntityFlag.USING_ITEM);
    }

    /**
     * Sets the cooldown time for the specified item to use.
     *
     * @param coolDownTick the cooldown tick
     * @param itemId       the item ID
     */
    public void setItemCoolDown(int coolDownTick, Identifier itemId) {
        var pk = new PlayerStartItemCoolDownPacket();
        pk.setCoolDownDuration(coolDownTick);
        pk.setItemCategory(itemId.toString());
        this.cooldownTickMap.put(itemId.toString(), this.server.getTick() + coolDownTick);
        this.dataPacket(pk);
    }

    /**
     * Checks if the cooldown of the specified item has ended.
     *
     * @param itemId the item ID
     * @return boolean
     */
    public boolean isItemCoolDownEnd(Identifier itemId) {
        Integer tick = this.cooldownTickMap.getOrDefault(itemId.toString(), 0);
        boolean result = this.getServer().getTick() - tick > 0;
        if (result) {
            cooldownTickMap.remove(itemId.toString());
        }
        return result;
    }

    /**
     * Sets the cooldown time for the specified item category.
     *
     * @param coolDown The cooldown duration in ticks.
     * @param category The item category.
     */
    public void setItemCoolDown(int coolDown, String category) {
        var pk = new PlayerStartItemCoolDownPacket();
        pk.setCoolDownDuration(coolDown);
        pk.setItemCategory(category);
        this.cooldownTickMap.put(category, this.server.getTick() + coolDown);
        this.dataPacket(pk);
    }

    /**
     * Checks if the cooldown for the specified item category has ended.
     *
     * @param category The item category.
     * @return True if the cooldown has ended, otherwise false.
     */
    public boolean isItemCoolDownEnd(String category) {
        Integer tick = this.cooldownTickMap.getOrDefault(category, 0);
        boolean result = this.getServer().getTick() - tick > 0;
        if (result) {
            cooldownTickMap.remove(category);
        }
        return result;
    }

    /**
     * Starts the last use tick for an item (right-click).
     *
     * @param itemId the item ID
     * @param tick   the tick
     */
    public void setLastUseTick(@NotNull String itemId, int tick) {
        lastUseItemMap.put(itemId, tick);
        this.setDataFlag(EntityFlag.USING_ITEM, true);
    }

    /**
     * Removes the last use tick for a specified item.
     *
     * @param itemId The ID of the item.
     */
    public void removeLastUseTick(@NotNull String itemId) {
        lastUseItemMap.remove(itemId);
        this.setDataFlag(EntityFlag.USING_ITEM, false);
    }

    /**
     * Gets the last use tick for a specified item.
     *
     * @param itemId The ID of the item.
     * @return The last use tick, or -1 if not found.
     */
    public int getLastUseTick(String itemId) {
        return lastUseItemMap.getOrDefault(itemId, -1);
    }

    /**
     * Gets the language hardcoded for the interaction buttons that appear when mobile device players face the carrier.
     *
     * @return the button text
     */
    public String getButtonText() {
        return this.buttonText;
    }

    /**
     * Sets the language hardcoded for the interaction buttons that appear when mobile device players face the carrier.
     *
     * @param text the button text
     */
    public void setButtonText(String text) {
        this.buttonText = text;
        this.setDataProperty(INTERACT_TEXT, this.buttonText);
    }

    /**
     * Unloads a chunk at the specified coordinates.
     *
     * @param x The X coordinate of the chunk.
     * @param z The Z coordinate of the chunk.
     */
    public void unloadChunk(int x, int z) {
        this.unloadChunk(x, z, null);
    }

    /**
     * Unloads a chunk at the specified coordinates in the given level.
     *
     * @param x     The X coordinate of the chunk.
     * @param z     The Z coordinate of the chunk.
     * @param level The level in which the chunk is located. If null, the player's current level is used.
     */
    public void unloadChunk(int x, int z, Level level) {
        level = level == null ? this.level : level;
        long index = Level.chunkHash(x, z);
        if (level.unregisterChunkLoader(this, x, z, false)) {
            if (playerChunkManager.getUsedChunks().contains(index)) {
                for (Entity entity : level.getChunkEntities(x, z).values()) {
                    if (entity != this) {
                        entity.despawnFrom(this);
                    }
                }
                playerChunkManager.getUsedChunks().remove(index);
            }
        }
    }

    /**
     * Checks if the player is in the Overworld (dimension 0).
     *
     * @return boolean
     */
    public boolean isInOverWorld() {
        return this.getLevel().getDimension() == 0;
    }

    /**
     * Gets the player's spawn point.
     *
     * @return {@link Position}
     */
    public Pair<Position, SpawnPointType> getSpawn() {
        return Pair.of(spawnPoint, spawnPointType);
    }

    /**
     * Sets the player's spawn point.
     *
     * @param pos            the spawn point position
     * @param spawnPointType the spawn point type
     */
    public void setSpawn(Position pos, SpawnPointType spawnPointType) {
        Preconditions.checkNotNull(pos);
        Preconditions.checkNotNull(pos.level);
        this.spawnPoint = new Position(pos.x, pos.y, pos.z, level);
        this.spawnPointType = spawnPointType;
        SetSpawnPositionPacket pk = new SetSpawnPositionPacket();
        pk.spawnType = SetSpawnPositionPacket.TYPE_PLAYER_SPAWN;
        pk.x = (int) this.spawnPoint.x;
        pk.y = (int) this.spawnPoint.y;
        pk.z = (int) this.spawnPoint.z;
        pk.dimension = this.spawnPoint.level.getDimension();
        this.dataPacket(pk);
    }

    /**
     * Sends a chunk to the player.
     *
     * @param x      The X coordinate of the chunk.
     * @param z      The Z coordinate of the chunk.
     * @param packet The data packet containing the chunk information.
     */
    public void sendChunk(int x, int z, DataPacket packet) {
        if (!this.isConnected()) {
            return;
        }

        this.chunkLoadCount++;
        this.playerChunkManager.getUsedChunks().add(Level.chunkHash(x, z));
        this.dataPacket(packet);

        if (this.spawned) {
            for (Entity entity : this.level.getChunkEntities(x, z).values()) {
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
            this.dataPacket(playerActionPacket);
        }
    }

    /**
     * Updates the tracking positions of the player.
     */
    public void updateTrackingPositions() {
        updateTrackingPositions(false);
    }

    /**
     * Updates the tracking positions of the player, with an option to delay the update.
     *
     * @param delayed True to delay the update, otherwise false.
     */
    public void updateTrackingPositions(boolean delayed) {
        Server server = getServer();
        if (delayed) {
            if (delayedPosTrackingUpdate != null) {
                delayedPosTrackingUpdate.cancel();
            }
            delayedPosTrackingUpdate = server.getScheduler().scheduleDelayedTask(null, this::updateTrackingPositions, 10);
            return;
        }
        PositionTrackingService positionTrackingService = server.getPositionTrackingService();
        positionTrackingService.forceRecheck(this);
    }

    /**
     * Sends a data packet to the player.
     *
     * @param packet the packet to send
     */
    public void dataPacket(DataPacket packet) {
        this.getSession().sendPacket(packet);
    }

    /**
     * Gets the network latency of the player.
     *
     * @return int
     */
    public int getPing() {
        return (int) this.getSession().getPing();
    }

    public boolean sleepOn(Vector3 pos) {
        if (!this.isOnline()) {
            return false;
        }

        for (Entity p : this.level.getNearbyEntities(this.boundingBox.grow(2, 1, 2), this)) {
            if (p instanceof Player) {
                if (((Player) p).sleeping != null && pos.distance(((Player) p).sleeping) <= 0.1) {
                    return false;
                }
            }
        }

        PlayerBedEnterEvent ev;
        this.server.getPluginManager().callEvent(ev = new PlayerBedEnterEvent(this, this.level.getBlock(pos)));
        if (ev.isCancelled()) {
            return false;
        }

        this.sleeping = pos.clone();
        this.teleport(new Location(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, this.yaw, this.pitch, this.level), null);

        this.setDataProperty(BED_POSITION, new BlockVector3((int) pos.x, (int) pos.y, (int) pos.z));
        this.setPlayerFlag(PlayerFlag.SLEEP);
        this.setSpawn(Position.fromObject(pos, getLevel()), SpawnPointType.BLOCK);
        this.level.sleepTicks = 75;

        this.timeSinceRest = 0;

        return true;
    }

    /**
     * Stops the player from sleeping.
     */
    public void stopSleep() {
        if (this.sleeping != null) {
            this.server.getPluginManager().callEvent(new PlayerBedLeaveEvent(this, this.level.getBlock(this.sleeping)));

            this.sleeping = null;
            this.setDataProperty(BED_POSITION, new BlockVector3(0, 0, 0));
            this.setPlayerFlag(PlayerFlag.SLEEP);

            this.level.sleepTicks = 0;

            AnimatePacket pk = new AnimatePacket();
            pk.eid = this.getId();
            pk.action = AnimatePacket.Action.WAKE_UP;
            this.dataPacket(pk);
        }
    }

    /**
     * Awards an achievement to the player.
     *
     * @param achievementId The ID of the achievement to award.
     * @return true if the achievement was awarded, false otherwise.
     */
    public boolean awardAchievement(String achievementId) {
        if (!Server.getInstance().getProperties().get(ServerPropertiesKeys.ACHIEVEMENTS, true)) {
            return false;
        }

        Achievement achievement = Achievement.achievements.get(achievementId);

        if (achievement == null || hasAchievement(achievementId)) {
            return false;
        }

        for (String id : achievement.requires) {
            if (!this.hasAchievement(id)) {
                return false;
            }
        }
        PlayerAchievementAwardedEvent event = new PlayerAchievementAwardedEvent(this, achievementId);
        this.server.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        this.achievements.add(achievementId);
        achievement.broadcast(this);
        return true;
    }

    /**
     * Gets the player's game mode.
     *
     * @return The game mode as an integer.
     */
    public int getGamemode() {
        return gamemode;
    }

    /**
     * Sets the player's game mode.
     *
     * @param gamemode The game mode to set.
     * @return true if the game mode was set, false otherwise.
     */
    public boolean setGamemode(int gamemode) {
        return this.setGamemode(gamemode, false, null);
    }

    /**
     * Sets the player's game mode with server-side option.
     *
     * @param gamemode   The game mode to set.
     * @param serverSide Whether to update only the server-side game mode.
     * @return true if the game mode was set, false otherwise.
     */
    public boolean setGamemode(int gamemode, boolean serverSide) {
        return this.setGamemode(gamemode, serverSide, null);
    }

    /**
     * Sets the player's game mode with server-side option and new adventure settings.
     *
     * @param gamemode    The game mode to set.
     * @param serverSide  Whether to update only the server-side game mode.
     * @param newSettings The new adventure settings.
     * @return true if the game mode was set, false otherwise.
     */
    public boolean setGamemode(int gamemode, boolean serverSide, AdventureSettings newSettings) {
        return this.setGamemode(gamemode, serverSide, newSettings, false);
    }

    /**
     * Sets the player's game mode with additional options.
     *
     * @param gamemode    The game mode to set.
     * @param serverSide  Whether to update only the server-side game mode.
     * @param newSettings The new adventure settings.
     * @param forceUpdate Whether to force the update.
     * @return true if the game mode was set, false otherwise.
     */
    public boolean setGamemode(int gamemode, boolean serverSide, AdventureSettings newSettings, boolean forceUpdate) {
        if (!forceUpdate && (gamemode < 0 || gamemode > 3 || this.gamemode == gamemode)) {
            return false;
        }

        if (newSettings == null) {
            newSettings = this.getAdventureSettings().clone();
            newSettings.set(Type.WORLD_IMMUTABLE, (gamemode & 0x02) > 0);
            newSettings.set(Type.BUILD, (gamemode & 0x02) <= 0);
            newSettings.set(Type.WORLD_BUILDER, (gamemode & 0x02) <= 0);
            newSettings.set(Type.ALLOW_FLIGHT, (gamemode & 0x01) > 0);
            newSettings.set(Type.NO_CLIP, gamemode == SPECTATOR);
            newSettings.set(Type.FLYING, switch (gamemode) {
                case SURVIVAL -> false;
                case CREATIVE -> newSettings.get(Type.FLYING);
                case ADVENTURE -> false;
                case SPECTATOR -> true;
                default -> throw new IllegalStateException("Unexpected game mode: " + gamemode);
            });
        }

        PlayerGameModeChangeEvent ev;
        this.server.getPluginManager().callEvent(ev = new PlayerGameModeChangeEvent(this, gamemode, newSettings));

        if (ev.isCancelled()) {
            return false;
        }

        this.gamemode = gamemode;

        if (this.isSpectator()) {
            this.onGround = false;
            this.setDataFlag(EntityFlag.HAS_COLLISION, false);
        } else {
            this.setDataFlag(EntityFlag.HAS_COLLISION, true);
        }

        this.namedTag.putInt("playerGameType", this.gamemode);

        this.setAdventureSettings(ev.getNewAdventureSettings());

        if (!serverSide) {
            var pk = new UpdatePlayerGameTypePacket();
            var networkGamemode = toNetworkGamemode(gamemode);
            pk.gameType = GameType.from(networkGamemode);
            pk.entityId = this.getId();
            var players = Sets.newHashSet(Server.getInstance().getOnlinePlayers().values());
            // Do not send UpdatePlayerGameTypePacket to self, use SetPlayerGameTypePacket
            players.remove(this);
            // We need to send this packet to all players to correctly render the player entity
            // e.g., Spectator mode players are invisible to players in gm 0, 1, 2
            Server.broadcastPacket(players, pk);
            // For self, use SetPlayerGameTypePacket to ensure compatibility with WaterDog
            var pk2 = new SetPlayerGameTypePacket();
            pk2.gamemode = networkGamemode;
            this.dataPacket(pk2);
        }

        this.resetFallDistance();

        return true;
    }

    /**
     * Sends the player's adventure settings.
     */
    public void sendSettings() {
        this.getAdventureSettings().update();
    }

    /**
     * Checks if the player is in survival mode.
     *
     * @return true if the player is in survival mode, false otherwise.
     */
    public boolean isSurvival() {
        return this.gamemode == SURVIVAL;
    }

    /**
     * Checks if the player is in creative mode.
     *
     * @return true if the player is in creative mode, false otherwise.
     */
    public boolean isCreative() {
        return this.gamemode == CREATIVE;
    }

    /**
     * Checks if the player is in spectator mode.
     *
     * @return true if the player is in spectator mode, false otherwise.
     */
    public boolean isSpectator() {
        return this.gamemode == SPECTATOR;
    }

    /**
     * Checks if the player is in adventure mode.
     *
     * @return true if the player is in adventure mode, false otherwise.
     */
    public boolean isAdventure() {
        return this.gamemode == ADVENTURE;
    }

    @Override
    public Item[] getDrops() {
        // Return the drops if the player is not in creative or spectator mode
        if (!this.isCreative() && !this.isSpectator()) {
            return super.getDrops();
        }

        // Return an empty array if the player is in creative or spectator mode
        return Item.EMPTY_ARRAY;
    }

    @ApiStatus.Internal
    public boolean fastMove(double dx, double dy, double dz) {
        // Update the player's position
        this.x += dx;
        this.y += dy;
        this.z += dz;
        this.recalculateBoundingBox(true);

        // Check the chunks the player is in
        this.checkChunks();

        // Update ground state and fall state if the player is not in spectator mode
        if (!this.isSpectator()) {
            this.checkGroundState(dx, dy, dz, dx, dy, dz);
            this.updateFallState(this.onGround);
        }
        return true;
    }

    @ApiStatus.Internal
    public AxisAlignedBB reCalcOffsetBoundingBox() {
        // Recalculate the offset bounding box based on the player's dimensions
        float dx = this.getWidth() / 2;
        float dz = this.getWidth() / 2;
        return this.offsetBoundingBox.setBounds(
                this.x - dx, this.y, this.z - dz,
                this.x + dx, this.y + this.getHeight(), this.z + dz
        );
    }

    @Override
    public void addMovement(double x, double y, double z, double yaw, double pitch, double headYaw) {
        // Send the player's position to viewers
        this.sendPosition(new Vector3(x, y, z), yaw, pitch, MovePlayerPacket.MODE_NORMAL, this.getViewers().values().toArray(EMPTY_ARRAY));
    }

    /**
     * Sends a motion packet to the client each time this method is called. If called multiple times, the motion will stack on the client.
     *
     * @param motion A motion vector
     * @return Whether the call was successful
     */
    @Override
    public boolean setMotion(Vector3 motion) {
        if (super.setMotion(motion)) {
            if (this.chunk != null) {
                this.addMotion(this.motionX, this.motionY, this.motionZ);  // Send to others
                SetEntityMotionPacket pk = new SetEntityMotionPacket();
                pk.eid = this.getId();
                pk.motionX = (float) motion.x;
                pk.motionY = (float) motion.y;
                pk.motionZ = (float) motion.z;
                this.dataPacket(pk);  // Send to self
            }
            if (this.motionY > 0) {
                // TODO: Check this
                this.startAirTicks = (int) ((-(Math.log(this.getGravity() / (this.getGravity() + this.getDrag() * this.motionY))) / this.getDrag()) * 2 + 5);
            }

            return true;
        }

        return false;
    }

    /**
     * Sends the player's attributes to the client.
     */
    public void sendAttributes() {
        UpdateAttributesPacket pk = new UpdateAttributesPacket();
        pk.entityId = this.getId();
        pk.entries = new Attribute[]{
                Attribute.getAttribute(Attribute.MAX_HEALTH).setMaxValue(this.getMaxHealth()).setValue(health > 0 ? (health < getMaxHealth() ? health : getMaxHealth()) : 0),
                Attribute.getAttribute(Attribute.MAX_HUNGER).setValue(this.getFoodData().getFood()),
                Attribute.getAttribute(Attribute.MOVEMENT_SPEED).setValue(this.getMovementSpeed()),
                Attribute.getAttribute(Attribute.EXPERIENCE_LEVEL).setValue(this.getExperienceLevel()),
                Attribute.getAttribute(Attribute.EXPERIENCE).setValue(((float) this.getExperience()) / calculateRequireExperience(this.getExperienceLevel()))
        };
        this.dataPacket(pk);
    }

    /**
     * Sends the fog settings to the client.
     */
    public void sendFogStack() {
        var pk = new PlayerFogPacket();
        pk.fogStack = this.fogStack;
        this.dataPacket(pk);
    }

    /**
     * Sends camera presets to the client.
     */
    public void sendCameraPresets() {
        var pk = new CameraPresetsPacket();
        pk.presets.addAll(CameraPreset.getPresets().values());
        dataPacket(pk);
    }

    @Override
    public float getHeight() {
        // Return a different height if the player is riding a horse
        if (this.riding instanceof EntityHorse) {
            return 1.1f;
        }
        return super.getHeight();
    }

    @Override
    public void setSwimming(boolean value) {
        // Skip the action if stopping a swim at a height of 1 block
        if (!value && level.getBlock(up()).isSolid() && level.getBlock(down()).isSolid()) {
            return;
        }
        super.setSwimming(value);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (!this.loggedIn) {
            return false;
        }

        int tickDiff = currentTick - this.lastUpdate;

        if (tickDiff <= 0) {
            return true;
        }

        this.messageLimitCounter = 2;

        this.lastUpdate = currentTick;

        if (this.fishing != null && this.server.getTick() % 20 == 0) {
            if (this.distance(fishing) > 33) {
                this.stopFishing(false);
            }
        }

        if (!this.isAlive() && this.spawned) {
            if (this.getLevel().getGameRules().getBoolean(GameRule.DO_IMMEDIATE_RESPAWN)) {
                this.despawnFromAll();
                return true;
            }
            server.getScheduler().scheduleDelayedTask(InternalPlugin.INSTANCE, this::despawnFromAll, 10);
            return true;
        }

        if (this.spawned) {
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

            if (this.getServer().getDifficulty() == 0 && this.level.getGameRules().getBoolean(GameRule.NATURAL_REGENERATION)) {
                if (this.getHealth() < this.getMaxHealth() && this.ticksLived % 20 == 0) {
                    this.heal(1);
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
                    this.highestPosition = this.y;
                    if (this.isGliding()) {
                        this.setGliding(false);
                    }
                } else {
                    this.lastInAirTick = server.getTick();
                    // Check if the player is flying
                    if (this.checkMovement && !this.isGliding() && !server.getAllowFlight() &&
                            !this.getAdventureSettings().get(Type.ALLOW_FLIGHT) &&
                            this.inAirTicks > 20 && !this.isSleeping() && !this.isImmobile() && !this.isSwimming() &&
                            this.riding == null && !this.hasEffect(EffectType.LEVITATION) && !this.hasEffect(EffectType.SLOW_FALLING)) {
                        double expectedVelocity = (-this.getGravity()) / ((double) this.getDrag()) - ((-this.getGravity()) / ((double) this.getDrag())) * Math.exp(-((double) this.getDrag()) * ((double) (this.inAirTicks - this.startAirTicks)));
                        double diff = (this.speed.y - expectedVelocity) * (this.speed.y - expectedVelocity);

                        Block block = level.getBlock(this);
                        String blockId = block.getId();
                        boolean ignore = blockId.equals(Block.LADDER) || blockId.equals(Block.VINE) || blockId.equals(Block.WEB)
                                || blockId.equals(Block.SCAFFOLDING);// || (blockId == Block.SWEET_BERRY_BUSH && block.getDamage() > 0);

                        if (!this.hasEffect(EffectType.JUMP_BOOST) && diff > 0.6 && expectedVelocity < this.speed.y && !ignore) {
                            if (this.inAirTicks < 150) {
                                this.setMotion(new Vector3(0, expectedVelocity, 0));
                            } else if (this.kick(PlayerKickEvent.Reason.FLYING_DISABLED, "Flying is not enabled on this server")) {
                                return false;
                            }
                        }
                        if (ignore) {
                            this.resetFallDistance();
                        }
                    }

                    if (this.y > highestPosition) {
                        this.highestPosition = this.y;
                    }

                    // Wiki: When gliding with an elytra, the fall height is reset to 1 block if the vertical descent rate is less than 0.5 blocks per tick.
                    // Wiki: Players will not take fall damage if they land at a small angle and at a sufficiently low speed. The critical damage angle when landing is 50°, and the damage value is equivalent to the damage the player would take if they fell directly from the highest point of the glide to the landing point.
                    if (this.isGliding() && Math.abs(this.speed.y) < 0.5 && this.getPitch() <= 40) {
                        this.resetFallDistance();
                    }

                    ++this.inAirTicks;
                }

                if (this.getFoodData() != null) {
                    this.getFoodData().tick(tickDiff);
                }

                // Elytra check and durability calculation
                if (this.isGliding()) {
                    HumanInventory playerInventory = this.getInventory();
                    if (playerInventory != null) {
                        Item chestplate = playerInventory.getChestplate();
                        if ((chestplate == null || !chestplate.getId().equals(ItemID.ELYTRA))) {
                            this.setGliding(false);
                        } else if (this.age % (20 * (chestplate.getEnchantmentLevel(Enchantment.ID_DURABILITY) + 1)) == 0 && !isCreative()) {
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

            if (this.server.getServerAuthoritativeMovement() > 0) { // Only used for server authoritative movement, as client authoritative continue break is normal
                onBlockBreakContinue(breakingBlock, breakingBlockFace);
            }

            // Reset move status
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

        if (this.spawned && !this.dummyBossBars.isEmpty() && currentTick % 100 == 0) {
            this.dummyBossBars.values().forEach(DummyBossBar::updateBossEntityPosition);
        }

        updateBlockingFlag();

        PlayerFood foodData = getFoodData();
        if (this.ticksLived % 40 == 0 && foodData != null) {
            foodData.sendFood();
        }

        return true;
    }

    /**
     * Checks for nearby interactable entities (not generally used by plugins).
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
     * Returns the interactable entity at the specified position.
     *
     * @param nearbyEntities An array of nearby entities.
     * @param x              The x-coordinate of the position.
     * @param y              The y-coordinate of the position.
     * @param z              The z-coordinate of the position.
     * @return The interactable entity at the specified position, or null if none is found.
     */
    private EntityInteractable getEntityAtPosition(Entity[] nearbyEntities, int x, int y, int z) {
        for (Entity nearestEntity : nearbyEntities) {
            if (nearestEntity.getFloorX() == x && nearestEntity.getFloorY() == y && nearestEntity.getFloorZ() == z
                    && nearestEntity instanceof EntityInteractable
                    && ((EntityInteractable) nearestEntity).canDoInteraction()) {
                return (EntityInteractable) nearestEntity;
            }
        }
        return null;
    }

    /**
     * Returns the entity the player is currently looking at.
     *
     * @param maxDistance The maximum distance to check for entities.
     * @return The entity the player is looking at, or null if none is found.
     */
    public EntityInteractable getEntityPlayerLookingAt(int maxDistance) {
        EntityInteractable entity = null;

        // Ensure the player is fully initialized
        if (temporalVector != null) {
            Entity[] nearbyEntities = level.getNearbyEntities(boundingBox.grow(maxDistance, maxDistance, maxDistance), this);

            // Iterate through blocks in the looking direction until the max interact distance is reached
            try {
                BlockIterator itr = new BlockIterator(level, getPosition(), getDirectionVector(), getEyeHeight(), maxDistance);
                if (itr.hasNext()) {
                    Block block;
                    while (itr.hasNext()) {
                        block = itr.next();
                        entity = getEntityAtPosition(nearbyEntities, block.getFloorX(), block.getFloorY(), block.getFloorZ());
                        if (entity != null) {
                            break;
                        }
                    }
                }
            } catch (Exception ex) {
                // No logging needed
            }
        }
        return entity;
    }

    /**
     * Gets the enchantment seed.
     *
     * @return The enchantment seed.
     */
    public int getEnchantmentSeed() {
        return this.enchSeed;
    }

    /**
     * Sets the enchantment seed.
     *
     * @param seed The enchantment seed to set.
     */
    public void setEnchantmentSeed(int seed) {
        this.enchSeed = seed;
    }

    /**
     * Regenerates the enchantment seed with a new random value.
     */
    public void regenerateEnchantmentSeed() {
        this.enchSeed = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
    }

    /**
     * Checks the network status and updates the player's chunk loading state.
     */
    public void checkNetwork() {
        if (!this.isOnline()) {
            return;
        }

        if (this.nextChunkOrderRun-- <= 0 || this.chunk == null) {
            playerChunkManager.tick();
        }

        if (this.chunkLoadCount >= this.spawnThreshold && !this.spawned && loggedIn) {
            this.getSession().notifyTerrainReady();
        }
    }

    /**
     * Checks if the player can interact with a position within a maximum distance.
     *
     * @param pos         The position to check.
     * @param maxDistance The maximum distance to check.
     * @return true if the player can interact, false otherwise.
     */
    public boolean canInteract(Vector3 pos, double maxDistance) {
        return this.canInteract(pos, maxDistance, 6.0);
    }

    /**
     * Checks if the player can interact with a position within a maximum distance and difference.
     *
     * @param pos         The position to check.
     * @param maxDistance The maximum distance to check.
     * @param maxDiff     The maximum difference allowed.
     * @return true if the player can interact, false otherwise.
     */
    public boolean canInteract(Vector3 pos, double maxDistance, double maxDiff) {
        if (this.distanceSquared(pos) > maxDistance * maxDistance) {
            return false;
        }

        Vector2 dV = this.getDirectionPlane();
        double dot = dV.dot(new Vector2(this.x, this.z));
        double dot1 = dV.dot(new Vector2(pos.x, pos.z));
        return (dot1 - dot) >= -maxDiff;
    }

    /**
     * Sends a chat message as this player. If the message begins with a / (forward-slash) it will be treated as a command.
     *
     * @param message The message to send.
     * @return true if the message was sent successfully, false otherwise.
     */
    public boolean chat(String message) {
        if (!this.spawned || !this.isAlive()) {
            return false;
        }

        this.resetInventory();

        if (this.removeFormat) {
            message = TextFormat.clean(message, true);
        }

        for (String msg : message.split("\n")) {
            if (!msg.trim().isEmpty() && msg.length() <= 512 && this.messageLimitCounter-- > 0) {
                PlayerChatEvent chatEvent = new PlayerChatEvent(this, msg);
                this.server.getPluginManager().callEvent(chatEvent);
                if (!chatEvent.isCancelled()) {
                    this.server.broadcastMessage(this.getServer().getLanguage().tr(chatEvent.getFormat(), new String[]{chatEvent.getPlayer().getDisplayName(), chatEvent.getMessage()}), chatEvent.getRecipients());
                }
            }
        }

        return true;
    }

    /**
     * Kicks the player with an empty reason.
     *
     * @return true if the player was kicked, false otherwise.
     * @see #kick(PlayerKickEvent.Reason, String, boolean)
     */
    public boolean kick() {
        return this.kick("");
    }

    /**
     * Kicks the player with a specified reason and admin flag.
     *
     * @param reason  The reason for kicking.
     * @param isAdmin Whether the kick is from an admin.
     * @return true if the player was kicked, false otherwise.
     * @see #kick(PlayerKickEvent.Reason, String, boolean)
     */
    public boolean kick(String reason, boolean isAdmin) {
        return this.kick(PlayerKickEvent.Reason.UNKNOWN, reason, isAdmin);
    }

    /**
     * Kicks the player with a specified reason.
     *
     * @param reason The reason for kicking.
     * @return true if the player was kicked, false otherwise.
     * @see #kick(PlayerKickEvent.Reason, String, boolean)
     */
    public boolean kick(String reason) {
        return kick(PlayerKickEvent.Reason.UNKNOWN, reason);
    }

    /**
     * Kicks the player with a specified reason.
     *
     * @param reason The reason for kicking.
     * @return true if the player was kicked, false otherwise.
     * @see #kick(PlayerKickEvent.Reason, String, boolean)
     */
    public boolean kick(PlayerKickEvent.Reason reason) {
        return this.kick(reason, true);
    }

    /**
     * Kicks the player with a specified reason and reason string.
     *
     * @param reason       The reason for kicking.
     * @param reasonString The reason string.
     * @return true if the player was kicked, false otherwise.
     * @see #kick(PlayerKickEvent.Reason, String, boolean)
     */
    public boolean kick(PlayerKickEvent.Reason reason, String reasonString) {
        return this.kick(reason, reasonString, true);
    }

    /**
     * Kicks the player with a specified reason and admin flag.
     *
     * @param reason  The reason for kicking.
     * @param isAdmin Whether the kick is from an admin.
     * @return true if the player was kicked, false otherwise.
     * @see #kick(PlayerKickEvent.Reason, String, boolean)
     */
    public boolean kick(PlayerKickEvent.Reason reason, boolean isAdmin) {
        return this.kick(reason, reason.toString(), isAdmin);
    }

    /**
     * Kicks the player with a specified reason, reason string, and admin flag.
     *
     * @param reason       The reason for kicking.
     * @param reasonString The reason string.
     * @param isAdmin      Whether the kick is from an admin.
     * @return true if the player was kicked, false otherwise.
     */
    public boolean kick(PlayerKickEvent.Reason reason, String reasonString, boolean isAdmin) {
        PlayerKickEvent ev;
        this.server.getPluginManager().callEvent(ev = new PlayerKickEvent(this, reason, this.getLeaveMessage()));
        if (!ev.isCancelled()) {
            String message;
            if (isAdmin) {
                if (!Server.getInstance().getNameBans().isBanned(getName())) {
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

            this.close(ev.getQuitMessage(), message);

            return true;
        }

        return false;
    }

    /**
     * Sets the player's viewing distance (range 0--{@link Server#getViewDistance}).
     *
     * @param distance The viewing distance.
     */
    public void setViewDistance(int distance) {
        this.chunkRadius = distance;

        ChunkRadiusUpdatedPacket pk = new ChunkRadiusUpdatedPacket();
        pk.radius = distance;

        this.dataPacket(pk);
    }

    /**
     * Gets the player's viewing distance.
     *
     * @return The player's viewing distance.
     */
    public int getViewDistance() {
        return this.chunkRadius;
    }

    @Override
    public void sendMessage(String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_RAW;
        pk.message = this.server.getLanguage().tr(message);
        this.dataPacket(pk);
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
     * Sends the command output to the player.
     *
     * @param container The command output container.
     */
    public void sendCommandOutput(CommandOutputContainer container) {
        if (this.level.getGameRules().getBoolean(GameRule.SEND_COMMAND_FEEDBACK)) {
            var pk = new CommandOutputPacket();
            pk.messages.addAll(container.getMessages());
            pk.commandOriginData = new CommandOriginData(CommandOriginData.Origin.PLAYER, this.getUniqueId(), "", null);
            pk.type = CommandOutputType.ALL_OUTPUT;
            pk.successCount = container.getSuccessCount();
            this.dataPacket(pk);
        }
    }

    /**
     * Sends a JSON text message in the player's chat bar.
     *
     * @param text The JSON text.
     */
    public void sendRawTextMessage(RawText text) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_OBJECT;
        pk.message = text.toRawText();
        this.dataPacket(pk);
    }

    /**
     * Sends a translation message to the player.
     *
     * @param message The message to translate.
     */
    public void sendTranslation(String message) {
        this.sendTranslation(message, EmptyArrays.EMPTY_STRINGS);
    }

    /**
     * Sends a multilingual translated text in the player's chat bar.
     *
     * @param message    The message.
     * @param parameters The parameters for the translation.
     */
    public void sendTranslation(String message, String[] parameters) {
        TextPacket pk = new TextPacket();
        if (this.server.getSettings().baseSettings().forceServerTranslate()) {
            pk.type = TextPacket.TYPE_RAW;
            pk.message = this.server.getLanguage().tr(message, parameters);
        } else {
            pk.type = TextPacket.TYPE_TRANSLATION;
            pk.message = this.server.getLanguage().tr(message, parameters, "nukkit.", true);
            for (int i = 0; i < parameters.length; i++) {
                parameters[i] = this.server.getLanguage().tr(parameters[i], parameters, "nukkit.", true);
            }
            pk.parameters = parameters;
        }
        this.dataPacket(pk);
    }

    /**
     * Sends a chat message in the player's chat bar.
     *
     * @param message The message.
     */
    public void sendChat(String message) {
        this.sendChat("", message);
    }

    /**
     * Sends a chat message in the player's chat bar.
     *
     * @param source  The source of the message.
     * @param message The message.
     */
    public void sendChat(String source, String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_CHAT;
        pk.source = source;
        pk.message = this.server.getLanguage().tr(message);
        this.dataPacket(pk);
    }

    /**
     * Sends a popup message above the player's item bar.
     *
     * @param message The message.
     */
    public void sendPopup(String message) {
        this.sendPopup(message, "");
    }

    /**
     * Sends a popup message above the player's item bar.
     *
     * @param message  The message.
     * @param subtitle The subtitle.
     */
    public void sendPopup(String message, String subtitle) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_POPUP;
        pk.message = message;
        this.dataPacket(pk);
    }

    /**
     * Sends a tip message above the player's item bar.
     *
     * @param message The message.
     */
    public void sendTip(String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_TIP;
        pk.message = message;
        this.dataPacket(pk);
    }

    /**
     * Clears the title info being displayed on the player.
     */
    public void clearTitle() {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_CLEAR;
        this.dataPacket(pk);
    }

    /**
     * Resets both title animation times and subtitle for the next shown title.
     */
    public void resetTitleSettings() {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_RESET;
        this.dataPacket(pk);
    }

    /**
     * Sets the subtitle to be displayed below the main title.
     *
     * @param subtitle The subtitle text.
     */
    public void setSubtitle(String subtitle) {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_SUBTITLE;
        pk.text = subtitle;
        this.dataPacket(pk);
    }

    /**
     * Sets a JSON text subtitle.
     *
     * @param text The JSON text.
     */
    public void setRawTextSubTitle(RawText text) {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_SUBTITLE_JSON;
        pk.text = text.toRawText();
        this.dataPacket(pk);
    }

    /**
     * Sets the title animation time.
     *
     * @param fadein   The fade-in time.
     * @param duration The duration time.
     * @param fadeout  The fade-out time.
     */
    public void setTitleAnimationTimes(int fadein, int duration, int fadeout) {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_ANIMATION_TIMES;
        pk.fadeInTime = fadein;
        pk.stayTime = duration;
        pk.fadeOutTime = fadeout;
        this.dataPacket(pk);
    }

    /**
     * Sets a JSON text title.
     *
     * @param text The JSON text.
     */
    public void setRawTextTitle(RawText text) {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_TITLE_JSON;
        pk.text = text.toRawText();
        this.dataPacket(pk);
    }

    /**
     * Sends a title with default animation times.
     *
     * @param title The title text.
     * @see #sendTitle(String, String, int, int, int)
     */
    public void sendTitle(String title) {
        this.sendTitle(title, null, 20, 20, 5);
    }

    /**
     * Sends a title and subtitle with default animation times.
     *
     * @param title    The title text.
     * @param subtitle The subtitle text.
     * @see #sendTitle(String, String, int, int, int)
     */
    public void sendTitle(String title, String subtitle) {
        this.sendTitle(title, subtitle, 20, 20, 5);
    }

    /**
     * Sends a title text in the center of the player's view.
     *
     * @param title    The title text.
     * @param subtitle The subtitle text.
     * @param fadeIn   The fade-in time (in ticks).
     * @param stay     The stay time.
     * @param fadeOut  The fade-out time.
     */
    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        this.setTitleAnimationTimes(fadeIn, stay, fadeOut);
        if (!Strings.isNullOrEmpty(subtitle)) {
            this.setSubtitle(subtitle);
        }
        // Title won't send if an empty string is used.
        this.setTitle(Strings.isNullOrEmpty(title) ? " " : title);
    }

    /**
     * Sends an ActionBar message with default animation times.
     *
     * @param title The message text.
     * @see #sendActionBar(String, int, int, int)
     */
    public void sendActionBar(String title) {
        this.sendActionBar(title, 1, 0, 1);
    }

    /**
     * Sends an ActionBar message above the player's item bar.
     *
     * @param title    The message text.
     * @param fadein   The fade-in time.
     * @param duration The duration time.
     * @param fadeout  The fade-out time.
     */
    public void sendActionBar(String title, int fadein, int duration, int fadeout) {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_ACTION_BAR;
        pk.text = title;
        pk.fadeInTime = fadein;
        pk.stayTime = duration;
        pk.fadeOutTime = fadeout;
        this.dataPacket(pk);
    }

    /**
     * Sets a JSON ActionBar message with default animation times.
     *
     * @param text The JSON text.
     * @see #setRawTextActionBar(RawText, int, int, int)
     */
    public void setRawTextActionBar(RawText text) {
        this.setRawTextActionBar(text, 1, 0, 1);
    }

    /**
     * Sets a JSON ActionBar message.
     *
     * @param text     The JSON text.
     * @param fadein   The fade-in time.
     * @param duration The duration time.
     * @param fadeout  The fade-out time.
     */
    public void setRawTextActionBar(RawText text, int fadein, int duration, int fadeout) {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_ACTIONBAR_JSON;
        pk.text = text.toRawText();
        pk.fadeInTime = fadein;
        pk.stayTime = duration;
        pk.fadeOutTime = fadeout;
        this.dataPacket(pk);
    }

    @Override
    public void close() {
        this.close("");
    }

    /**
     * Closes the player's connection with a reason.
     *
     * @param reason The reason for closing.
     * @see #close(TextContainer, String)
     */
    public void close(String reason) {
        this.close(this.getLeaveMessage(), reason);
    }

    public void close(String message, String reason) {
        this.close(new TextContainer(message), reason);
    }

    /**
     * Closes the player's connection with a message and a reason.
     *
     * @param message The message to display.
     * @param reason  The reason for closing.
     * @see #close(TextContainer, String)
     */
    public void close(TextContainer message) {
        this.close(message, "generic");
    }

    /**
     * Closes the player's connection and all its activities.
     * Similar to {@link #kick}, but {@link #kick} is based on {@code close}.
     *
     * @param message The PlayerQuitEvent message.
     * @param reason  The reason for logout.
     */
    public void close(TextContainer message, String reason) {
        if (!this.connected.compareAndSet(true, false) && this.closed) {
            return;
        }
        // Output logout information
        log.info(this.getServer().getLanguage().tr("nukkit.player.logOut",
                TextFormat.AQUA + this.getName() + TextFormat.WHITE,
                this.getAddress(),
                String.valueOf(this.getPort()),
                this.getServer().getLanguage().tr(reason)));

        resetInventory();
        for (var inv : this.windows.keySet()) {
            if (this.permanentWindows.contains(windows.get(inv))) {
                int windowId = this.getWindowId(inv);
                playerHandle.setClosingWindowId(windowId);
                inv.close(this);
                updateTrackingPositions(true);
            }
        }

        // Handle scoreboardManager#beforePlayerQuit
        var scoreboardManager = this.getServer().getScoreboardManager();
        if (scoreboardManager != null) {
            scoreboardManager.beforePlayerQuit(this);
        }

        // Dismount horse
        if (this.riding instanceof EntityRideable entityRideable) {
            entityRideable.dismountEntity(this);
        }

        unloadAllUsedChunk();

        // Send disconnection packet
        DisconnectPacket packet = new DisconnectPacket();
        if (reason == null || reason.isBlank()) {
            packet.hideDisconnectionScreen = true;
            reason = BedrockDisconnectReasons.DISCONNECTED;
        }
        packet.message = reason;
        this.getSession().sendPacketSync(packet);

        // Call quit event
        PlayerQuitEvent ev = null;
        if (this.getName() != null && !this.getName().isEmpty()) {
            this.server.getPluginManager().callEvent(ev = new PlayerQuitEvent(this, message, true, reason));
            if (this.fishing != null) {
                this.stopFishing(false);
            }
        }
        // Close the temporary windows first, so they have a chance to change all inventories before being disposed
        if (ev != null && ev.getAutoSave() && namedTag != null) {
            this.save();
        }
        super.close();

        this.windows.clear();
        this.hiddenPlayers.clear();
        // Remove player from player list
        this.server.removeOnlinePlayer(this);
        // Remove player from players map
        this.server.removePlayer(this);

        this.server.getPluginManager().unsubscribeFromPermission(Server.BROADCAST_CHANNEL_USERS, this);
        this.server.getPluginManager().unsubscribeFromPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE, this);
        // Broadcast disconnection message
        if (ev != null && !Objects.equals(this.getName(), "") && this.spawned && !Objects.equals(ev.getQuitMessage().toString(), "")) {
            this.server.broadcastMessage(ev.getQuitMessage());
        }

        this.hasSpawned.clear();
        this.loggedIn = false;
        this.spawned = false;
        this.spawnPoint = null;
        this.riding = null;
        this.chunk = null;

        if (this.perm != null) {
            this.perm.clearPermissions();
            this.perm = null;
        }
        if (this.inventory != null) {
            this.inventory = null;
        }
        if (this.offhandInventory != null) {
            this.offhandInventory = null;
        }
        if (this.enderChestInventory != null) {
            this.enderChestInventory = null;
        }
        if (this.creativeOutputInventory != null) {
            this.creativeOutputInventory = null;
        }
        if (this.playerCursorInventory != null) {
            this.playerCursorInventory = null;
        }

        // Close player network session
        log.debug("Closing player network session");
        log.debug(reason);
        assert this.session != null;
        this.session.close(null);
    }

    /**
     * Unloads all chunks used by the player.
     */
    public synchronized void unloadAllUsedChunk() {
        // Save player data
        // Unload chunk for the player
        LongIterator iterator = this.playerChunkManager.getUsedChunks().iterator();
        while (iterator.hasNext()) {
            long l = iterator.nextLong();
            int chunkX = Level.getHashX(l);
            int chunkZ = Level.getHashZ(l);
            if (level.unregisterChunkLoader(this, chunkX, chunkZ, false)) {
                for (Entity entity : level.getChunkEntities(chunkX, chunkZ).values()) {
                    if (entity != this) {
                        entity.despawnFrom(this);
                    }
                }
                iterator.remove();
            }
        }
        this.playerChunkManager.getUsedChunks().clear();
    }

    /**
     * Saves the player's data.
     */
    public void save() {
        this.save(false);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        if (spawnPoint == null) {
            // Remove spawn point tags if spawnPoint is null
            namedTag.remove("SpawnX")
                    .remove("SpawnY")
                    .remove("SpawnZ")
                    .remove("SpawnLevel")
                    .remove("SpawnDimension");
        } else {
            // Save spawn point coordinates
            namedTag.putInt("SpawnX", this.spawnPoint.getFloorX())
                    .putInt("SpawnY", this.spawnPoint.getFloorY())
                    .putInt("SpawnZ", this.spawnPoint.getFloorZ());
            if (this.spawnPoint.getLevel() != null) {
                // Save spawn level name and dimension if available
                this.namedTag.putString("SpawnLevel", this.spawnPoint.getLevel().getName());
                this.namedTag.putInt("SpawnDimension", this.spawnPoint.getLevel().getDimension());
            } else {
                // Save default level name and dimension if spawn level is null
                this.namedTag.putString("SpawnLevel", this.server.getDefaultLevel().getName());
                this.namedTag.putInt("SpawnDimension", this.server.getDefaultLevel().getDimension());
            }
        }

        // Save adventure settings
        this.adventureSettings.saveNBT();
    }

    public void save(boolean async) {
        if (this.closed) {
            throw new IllegalStateException("Tried to save closed player");
        }

        saveNBT();

        if (this.level != null) {
            // Save current level name
            this.namedTag.putString("Level", this.level.getName());

            // Save achievements
            CompoundTag achievements = new CompoundTag();
            for (String achievement : this.achievements) {
                achievements.putByte(achievement, 1);
            }
            this.namedTag.putCompound("Achievements", achievements);

            // Save player data
            this.namedTag.putInt("playerGameType", this.gamemode);
            this.namedTag.putLong("lastPlayed", System.currentTimeMillis() / 1000);
            this.namedTag.putString("lastIP", this.getAddress());
            this.namedTag.putInt("EXP", this.getExperience());
            this.namedTag.putInt("expLevel", this.getExperienceLevel());
            this.namedTag.putInt("foodLevel", this.getFoodData().getFood());
            this.namedTag.putFloat("foodSaturationLevel", this.getFoodData().getSaturation());
            this.namedTag.putInt("enchSeed", this.enchSeed);

            // Save fog identifiers
            var fogIdentifiers = new ListTag<StringTag>();
            var userProvidedFogIds = new ListTag<StringTag>();
            this.fogStack.forEach(fog -> {
                fogIdentifiers.add(new StringTag(fog.identifier().toString()));
                userProvidedFogIds.add(new StringTag(fog.userProvidedId()));
            });
            this.namedTag.putList("fogIdentifiers", fogIdentifiers);
            this.namedTag.putList("userProvidedFogIds", userProvidedFogIds);

            // Save time since last rest
            this.namedTag.putInt("TimeSinceRest", this.timeSinceRest);

            // Save offline player data if name is not blank and namedTag is not null
            if (!this.getName().isBlank() && this.namedTag != null) {
                this.server.saveOfflinePlayerData(this.uuid, this.namedTag, async);
            }
        }
    }

    @Override
    public String getOriginalName() {
        return "Player";
    }

    @NotNull
    @Override
    public String getName() {
        return this.info.getUsername();
    }

    public LangCode getLanguageCode() {
        LangCode code = LangCode.from(this.getLoginChainData().getLanguageCode());
        return code == null ? LangCode.en_US : code;
    }

    @Override
    public void kill() {
        if (!this.spawned) {
            return;
        }

        boolean showMessages = this.level.getGameRules().getBoolean(GameRule.SHOW_DEATH_MESSAGES);
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
                            params.add(!Objects.equals(shooter.getNameTag(), "") ? shooter.getNameTag() : shooter.getName());
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
                        String id = ((EntityDamageByBlockEvent) cause).getDamager().getId();
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

        PlayerDeathEvent ev = new PlayerDeathEvent(this, this.getDrops(), new TranslationContainer(message, params.toArray(EmptyArrays.EMPTY_STRINGS)), this.expLevel);
        ev.setKeepExperience(this.level.gameRules.getBoolean(GameRule.KEEP_INVENTORY));
        ev.setKeepInventory(ev.getKeepExperience());

        this.server.getPluginManager().callEvent(ev);

        if (!ev.isCancelled()) {
            if (this.fishing != null) {
                this.stopFishing(false);
            }

            this.health = 0;
            this.extinguish();
            this.scheduleUpdate();
            if (!ev.getKeepInventory() && this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
                for (Item item : ev.getDrops()) {
                    if (!item.hasEnchantment(Enchantment.ID_VANISHING_CURSE) && item.applyEnchantments()) {
                        this.level.dropItem(this, item, null, true, 40);
                    }
                }

                if (this.inventory != null) {
                    new HashMap<>(this.inventory.getContents()).forEach((slot, item) -> {
                        if (!item.keepOnDeath()) {
                            this.inventory.clear(slot);
                        }
                    });
                }
                if (this.offhandInventory != null) {
                    new HashMap<>(this.offhandInventory.getContents()).forEach((slot, item) -> {
                        if (!item.keepOnDeath()) {
                            this.offhandInventory.clear(slot);
                        }
                    });
                }
            }

            if (!ev.getKeepExperience() && this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
                if (this.isSurvival() || this.isAdventure()) {
                    int exp = ev.getExperience() * 7;
                    if (exp > 100) exp = 100;
                    this.getLevel().dropExpOrb(this, exp);
                }
                this.setExperience(0, 0);
            }

            this.timeSinceRest = 0;

            DeathInfoPacket deathInfo = new DeathInfoPacket();
            deathInfo.translation = ev.getTranslationDeathMessage();
            this.dataPacket(deathInfo);

            if (showMessages && !ev.getDeathMessage().toString().isEmpty()) {
                this.server.broadcast(ev.getDeathMessage(), Server.BROADCAST_CHANNEL_USERS);
            }
            this.setDataProperty(PLAYER_LAST_DEATH_POS, new BlockVector3(this.getFloorX(), this.getFloorY(), this.getFloorZ()));

            RespawnPacket pk = new RespawnPacket();
            Position pos = this.getSpawn().left();
            pk.x = (float) pos.x;
            pk.y = (float) pos.y;
            pk.z = (float) pos.z;
            pk.respawnState = RespawnPacket.STATE_SEARCHING_FOR_SPAWN;
            pk.runtimeEntityId = this.getId();
            this.dataPacket(pk);
        }
    }

    @Override
    public void setHealth(float health) {
        // Ensure health is not less than 0
        if (health < 1) {
            health = 0;
        }
        super.setHealth(health);

        // Update the MAX_HEALTH attribute
        Attribute attribute = this.attributes.computeIfAbsent(Attribute.MAX_HEALTH, Attribute::getAttribute);
        attribute.setMaxValue(this.getAbsorption() % 2 != 0 ? this.getMaxHealth() + 1 : this.getMaxHealth())
                .setValue(health > 0 ? (health < getMaxHealth() ? health : getMaxHealth()) : 0);

        // Send updated attributes to the client if the player is spawned
        if (this.spawned) {
            UpdateAttributesPacket pk = new UpdateAttributesPacket();
            pk.entries = new Attribute[]{attribute};
            pk.entityId = this.getId();
            this.dataPacket(pk);
        }
    }

    @Override
    public void setMaxHealth(int maxHealth) {
        super.setMaxHealth(maxHealth);

        // Update the MAX_HEALTH attribute
        Attribute attribute = this.attributes.computeIfAbsent(Attribute.MAX_HEALTH, Attribute::getAttribute);
        attribute.setMaxValue(this.getAbsorption() % 2 != 0 ? this.getMaxHealth() + 1 : this.getMaxHealth())
                .setValue(health > 0 ? (health < getMaxHealth() ? health : getMaxHealth()) : 0);

        // Send updated attributes to the client if the player is spawned
        if (this.spawned) {
            UpdateAttributesPacket pk = new UpdateAttributesPacket();
            pk.entries = new Attribute[]{attribute};
            pk.entityId = this.getId();
            this.dataPacket(pk);
        }
    }

    /**
     * Get the experience value of the player (it does not show the total experience value of the player, but only the experience value where the current level is, i.e., the experience bar).
     *
     * @return int
     */
    public int getExperience() {
        return this.exp;
    }

    /**
     * Get the level of the player.
     *
     * @return int
     */
    public int getExperienceLevel() {
        return this.expLevel;
    }

    /**
     * Add experience to the player.
     *
     * @param add Amount of experience to add
     */
    public void addExperience(int add) {
        addExperience(add, false);
    }

    /**
     * Add experience to the player.
     *
     * @param add              Amount of experience to add
     * @param playLevelUpSound Whether to play the level-up sound
     */
    public void addExperience(int add, boolean playLevelUpSound) {
        if (add == 0) return;
        int now = this.getExperience();
        int added = now + add;
        int level = this.getExperienceLevel();
        int most = calculateRequireExperience(level);

        // Level up if the added experience exceeds the required experience for the current level
        while (added >= most) {
            added = added - most;
            level++;
            most = calculateRequireExperience(level);
        }
        this.setExperience(added, level, playLevelUpSound);
    }

    /**
     * Calculate the amount of experience a player needs to reach a certain level.
     *
     * @param level The level to reach
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
     * Set the experience value of the player.
     *
     * @param exp The experience value
     */
    public void setExperience(int exp) {
        setExperience(exp, this.getExperienceLevel());
    }

    /**
     * Set the experience value and level of the player.
     *
     * @param exp   The experience value
     * @param level The level
     */
    public void setExperience(int exp, int level) {
        setExperience(exp, level, false);
    }

    /**
     * Set the experience value and level of the player.
     *
     * @param exp              The experience value
     * @param level            The level
     * @param playLevelUpSound Whether to play the level-up sound
     */
    public void setExperience(int exp, int level, boolean playLevelUpSound) {
        var expEvent = new PlayerExperienceChangeEvent(this, this.getExperience(), this.getExperienceLevel(), exp, level);
        this.server.getPluginManager().callEvent(expEvent);
        if (expEvent.isCancelled()) {
            return;
        }
        exp = expEvent.getNewExperience();
        level = expEvent.getNewExperienceLevel();

        int levelBefore = this.expLevel;
        this.exp = exp;
        this.expLevel = level;

        this.sendExperienceLevel(level);
        this.sendExperience(exp);

        // Play level-up sound if applicable
        if (playLevelUpSound && levelBefore < level && levelBefore / 5 != level / 5 && this.lastPlayerdLevelUpSoundTime < this.age - 100) {
            this.lastPlayerdLevelUpSoundTime = this.age;
            this.level.addLevelSoundEvent(
                    this,
                    LevelSoundEventPacketV2.SOUND_LEVELUP,
                    Math.min(7, level / 5) << 28,
                    "",
                    false, false
            );
        }
    }

    /**
     * Send the current experience value to the client.
     */
    public void sendExperience() {
        sendExperience(this.getExperience());
    }

    /**
     * Implementation of setExperience, used to set the experience value corresponding to the current level, i.e., the experience bar.
     *
     * @param exp The experience value
     */
    public void sendExperience(int exp) {
        if (this.spawned) {
            float percent = ((float) exp) / calculateRequireExperience(this.getExperienceLevel());
            percent = Math.max(0f, Math.min(1f, percent));
            Attribute attribute = this.attributes.computeIfAbsent(Attribute.EXPERIENCE, Attribute::getAttribute);
            attribute.setValue(percent);
            this.syncAttribute(attribute);
        }
    }

    /**
     * @see #sendExperienceLevel(int)
     */
    public void sendExperienceLevel() {
        sendExperienceLevel(this.getExperienceLevel());
    }

    /**
     * The implementation of setExperience is used to set the level.
     *
     * @param level The level to set
     */
    public void sendExperienceLevel(int level) {
        if (this.spawned) {
            Attribute attribute = this.attributes.computeIfAbsent(Attribute.EXPERIENCE_LEVEL, Attribute::getAttribute);
            attribute.setValue(level);
            this.syncAttribute(attribute);
        }
    }

    /**
     * Send UpdateAttributesPacket packets to this player with the specified {@link Attribute}.
     *
     * @param attribute The attribute to sync
     */
    public void syncAttribute(Attribute attribute) {
        UpdateAttributesPacket pk = new UpdateAttributesPacket();
        pk.entries = new Attribute[]{attribute};
        pk.entityId = this.getId();
        this.dataPacket(pk);
    }

    /**
     * Sync all attributes that are marked as syncable.
     */
    public void syncAttributes() {
        UpdateAttributesPacket pk = new UpdateAttributesPacket();
        pk.entries = this.attributes.values().stream().filter(Attribute::isSyncable).toArray(Attribute[]::new);
        pk.entityId = this.getId();
        this.dataPacket(pk);
    }

    @Override
    public void setAbsorption(float absorption) {
        if (absorption != this.absorption) {
            this.absorption = absorption;
            Attribute attribute = this.attributes.computeIfAbsent(Attribute.ABSORPTION, Attribute::getAttribute);
            attribute.setValue(absorption);
            this.syncAttribute(attribute);
        }
    }

    /**
     * @see #setMovementSpeed(float, boolean)
     */
    @Override
    public void setMovementSpeed(float speed) {
        setMovementSpeed(speed, true);
    }

    /**
     * Set the movement speed of this player.
     *
     * @param speed The speed value, note that the default movement speed is {@link #DEFAULT_SPEED}
     * @param send  Whether to send {@link UpdateAttributesPacket} to the client
     */
    public void setMovementSpeed(float speed, boolean send) {
        super.setMovementSpeed(speed);
        if (this.spawned && send) {
            this.sendMovementSpeed(speed);
        }
    }

    /**
     * Send {@link Attribute#MOVEMENT_SPEED} attribute to the client.
     *
     * @param speed The speed value
     */
    public void sendMovementSpeed(float speed) {
        Attribute attribute = this.attributes.computeIfAbsent(Attribute.MOVEMENT_SPEED, Attribute::getAttribute);
        attribute.setValue(speed);
        this.syncAttribute(attribute);
    }

    /**
     * Get the entity that killed this player.
     *
     * @return Entity | null
     */
    public Entity getKiller() {
        return killer;
    }

    /**
     * Handles the attack event on the player.
     * <p>
     * This method processes the damage event for the player, checking various conditions such as
     * whether the player is alive, in spectator or creative mode, or has fall damage immunity.
     * It also handles specific block interactions like slime blocks to reset fall distance.
     * If the attack is valid, it triggers the hurt animation and updates the last entity that attacked the player.
     * </p>
     *
     * @param source The source of the damage event.
     * @return True if the attack was successful, otherwise false.
     */
    @Override
    public boolean attack(EntityDamageEvent source) {
        if (!this.isAlive()) {
            return false;
        }

        if (this.isSpectator() || this.isCreative()) {
            // Cancel the damage if the player is in spectator or creative mode
            return false;
        } else if (this.getAdventureSettings().get(Type.ALLOW_FLIGHT) && source.getCause() == DamageCause.FALL) {
            // Cancel fall damage if the player has flight enabled
            return false;
        } else if (source.getCause() == DamageCause.FALL) {
            if (this.getLevel().getBlock(this.getPosition().floor().add(0.5, -1, 0.5)).getId().equals(Block.SLIME)) {
                if (!this.isSneaking()) {
                    // Reset fall distance if the player lands on a slime block and is not sneaking
                    this.resetFallDistance();
                    return false;
                }
            }
        }

        if (super.attack(source)) { // Check if the attack was not cancelled
            if (this.getLastDamageCause() == source && this.spawned) {
                if (source instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) {
                    Entity damager = entityDamageByEntityEvent.getDamager();
                    if (damager instanceof Player) {
                        ((Player) damager).getFoodData().exhaust(0.1);
                    }
                    // Save the entity that attacked the player in lastBeAttackEntity
                    this.lastBeAttackEntity = entityDamageByEntityEvent.getDamager();
                }
                EntityEventPacket pk = new EntityEventPacket();
                pk.eid = this.getId();
                pk.event = EntityEventPacket.HURT_ANIMATION;
                this.dataPacket(pk);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Drops an item on the ground in front of the player. Returns if the item drop was successful.
     *
     * @param item the item to drop
     * @return boolean if the item was dropped or if the item was null
     */
    public boolean dropItem(Item item) {
        if (!this.spawned || !this.isAlive()) {
            return false;
        }

        if (item.isNull()) {
            log.debug("{} attempted to drop a null item ({})", this.getName(), item);
            return true;
        }

        Vector3 motion = this.getDirectionVector().multiply(0.4);

        this.level.dropItem(this.add(0, 1.3, 0), item, motion, 40);

        this.setDataFlag(EntityFlag.USING_ITEM, false);
        return true;
    }

    /**
     * Drops an item on the ground in front of the player. Returns the dropped item.
     *
     * @param item the item to drop
     * @return EntityItem if the item was dropped or null if the item was null
     */
    public @Nullable EntityItem dropAndGetItem(@NotNull Item item) {
        if (!this.spawned || !this.isAlive()) {
            return null;
        }

        if (item.isNull()) {
            log.debug("{} attempted to drop a null item ({})", this.getName(), item);
            return null;
        }

        Vector3 motion = this.getDirectionVector().multiply(0.4);

        this.setDataFlag(EntityFlag.USING_ITEM, false);

        return this.level.dropAndGetItem(this.add(0, 1.3, 0), item, motion, 40);
    }

    /**
     * @see #sendPosition(Vector3, double, double, int, Player[])
     */
    public void sendPosition(Vector3 pos) {
        this.sendPosition(pos, this.yaw);
    }

    /**
     * @see #sendPosition(Vector3, double, double, int, Player[])
     */
    public void sendPosition(Vector3 pos, double yaw) {
        this.sendPosition(pos, yaw, this.pitch);
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
     * Implementation of {@link Player#addMovement}, only sends {@link MovePlayerPacket} to the client
     *
     * @param pos     the position of MovePlayerPacket
     * @param yaw     the yaw of MovePlayerPacket
     * @param pitch   the pitch of MovePlayerPacket
     * @param mode    the mode of MovePlayerPacket
     * @param targets players who receive the packet
     */
    public void sendPosition(Vector3 pos, double yaw, double pitch, int mode, Player[] targets) {
        MovePlayerPacket pk = new MovePlayerPacket();
        pk.eid = this.getId();
        pk.x = (float) pos.x;
        pk.y = (float) (pos.y + this.getEyeHeight());
        pk.z = (float) pos.z;
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
            this.dataPacket(pk);
        }
    }

    @Override
    public boolean teleport(Location location, TeleportCause cause) {
        if (!this.isOnline()) {
            return false;
        }
        Location from = this.getLocation();
        this.lastTeleportMessage = Pair.of(from, System.currentTimeMillis());

        Location to = location;
        // event
        if (cause != null) {
            PlayerTeleportEvent event = new PlayerTeleportEvent(this, from, to, cause);
            this.server.getPluginManager().callEvent(event);
            if (event.isCancelled()) return false;
            to = event.getTo();
        }

        // remove inventory, ride, sign editor
        for (Inventory window : new ArrayList<>(this.windows.keySet())) {
            if (window == this.inventory) {
                continue;
            }
            this.removeWindow(window);
        }
        final Entity currentRide = getRiding();
        if (currentRide != null && !currentRide.dismountEntity(this)) {
            return false;
        }
        setOpenSignFront(null);

        this.setMotion(this.temporalVector.setComponents(0, 0, 0));

        boolean switchLevel = false;
        if (!to.getLevel().equals(from.getLevel())) {
            switchLevel = true;
            unloadAllUsedChunk();
            // unload entities for old level
            Arrays.stream(from.getLevel().getEntities()).forEach(e -> e.despawnFrom(this));
        }

        clientMovements.clear();
        // switch level, update pos and rotation, update aabb
        if (setPositionAndRotation(to, to.getYaw(), to.getPitch(), to.getHeadYaw())) {
            // if switch level or the distance teleported is too far
            if (switchLevel) {
                this.playerChunkManager.handleTeleport();
                // set nextChunkOrderRun is zero means that the next tick immediately execute the playerChunkManager#tick
                this.nextChunkOrderRun = 0;
            } else if ((Math.abs(from.getChunkX() - to.getChunkX()) >= this.getViewDistance())
                    || (Math.abs(from.getChunkZ() - to.getChunkZ()) >= this.getViewDistance())) {
                this.playerChunkManager.handleTeleport();
                this.nextChunkOrderRun = 0;
            }
            // send to client
            this.sendPosition(to, to.yaw, to.pitch, MovePlayerPacket.MODE_TELEPORT);
            this.newPosition = to;
        } else {
            this.sendPosition(this, to.yaw, to.pitch, MovePlayerPacket.MODE_TELEPORT);
            this.newPosition = this;
        }
        // state update
        this.positionChanged = true;

        if (switchLevel) {
            refreshBlockEntity(10);
            refreshChunkRender();
        }
        this.resetFallDistance();
        // DummyBossBar
        this.getDummyBossBars().values().forEach(DummyBossBar::reshow);
        // Weather
        this.getLevel().sendWeather(this);
        // Update time
        this.getLevel().sendTime(this);
        updateTrackingPositions(true);
        // Update gamemode
        if (isSpectator()) {
            this.setGamemode(this.gamemode, false, null, true);
        }
        this.scheduleUpdate();
        return true;
    }

    public void refreshChunkRender() {
        final int origin = getViewDistance();
        this.setViewDistance(1);
        this.setViewDistance(32);
        this.setViewDistance(origin);
    }

    /**
     * Refreshes block entities after a specified delay.
     *
     * @param delay The delay in ticks before refreshing block entities.
     */
    public void refreshBlockEntity(int delay) {
        Server.getInstance().getScheduler().scheduleDelayedTask(InternalPlugin.INSTANCE, () -> {
            for (var b : this.level.getBlockEntities().values()) {
                if (b instanceof BlockEntitySpawnable blockEntitySpawnable) {
                    UpdateBlockPacket setAir = new UpdateBlockPacket();
                    setAir.blockRuntimeId = BlockAir.STATE.blockStateHash();
                    setAir.flags = UpdateBlockPacket.FLAG_NETWORK;
                    setAir.x = b.getFloorX();
                    setAir.y = b.getFloorY();
                    setAir.z = b.getFloorZ();
                    this.dataPacket(setAir);

                    UpdateBlockPacket revertAir = new UpdateBlockPacket();
                    revertAir.blockRuntimeId = b.getBlock().getRuntimeId();
                    revertAir.flags = UpdateBlockPacket.FLAG_NETWORK;
                    revertAir.x = b.getFloorX();
                    revertAir.y = b.getFloorY();
                    revertAir.z = b.getFloorZ();
                    this.dataPacket(revertAir);
                    blockEntitySpawnable.spawnTo(this);
                }
            }
        }, delay, true);
    }

    /**
     * Shows a new FormWindow to the player.
     * You can find out FormWindow result by listening to PlayerFormRespondedEvent.
     *
     * @param window The FormWindow to show.
     * @return The form id to use in PlayerFormRespondedEvent.
     * @see #showFormWindow(FormWindow, int)
     */
    public int showFormWindow(FormWindow window) {
        return showFormWindow(window, this.formWindowCount++);
    }

    /**
     * Shows a new FormWindow to the player.
     * You can find out FormWindow result by listening to PlayerFormRespondedEvent.
     *
     * @param window The FormWindow to show.
     * @param id     The form id.
     * @return The form id to use in PlayerFormRespondedEvent.
     */
    public int showFormWindow(FormWindow window, int id) {
        if (this.formWindows.size() > 100) {
            this.kick("Possible DoS vulnerability: More Than 10 FormWindow sent to client already.");
            return id;
        }
        ModalFormRequestPacket packet = new ModalFormRequestPacket();
        packet.formId = id;
        packet.data = window.getJSONData();
        this.formWindows.put(packet.formId, window);

        this.dataPacket(packet);
        return id;
    }

    /**
     * Shows a dialog window to the player.
     *
     * @param dialog The dialog window to show.
     * @see #showDialogWindow(FormWindowDialog, boolean)
     */
    public void showDialogWindow(FormWindowDialog dialog) {
        showDialogWindow(dialog, true);
    }

    /**
     * Shows a dialog window to the player.
     *
     * @param dialog The dialog window to show.
     * @param book   If true, the FormWindowDialog scene name will be updated immediately.
     */
    public void showDialogWindow(FormWindowDialog dialog, boolean book) {
        String actionJson = dialog.getButtonJSONData();

        if (book && dialogWindows.getIfPresent(dialog.getSceneName()) != null) dialog.updateSceneName();
        dialog.getBindEntity().setDataProperty(HAS_NPC, 1);
        dialog.getBindEntity().setDataProperty(NPC_DATA, dialog.getSkinData());
        dialog.getBindEntity().setDataProperty(ACTIONS, actionJson);
        dialog.getBindEntity().setDataProperty(INTERACT_TEXT, dialog.getContent());

        NPCDialoguePacket packet = new NPCDialoguePacket();
        packet.runtimeEntityId = dialog.getEntityId();
        packet.action = NPCDialoguePacket.NPCDialogAction.OPEN;
        packet.dialogue = dialog.getContent();
        packet.npcName = dialog.getTitle();
        if (book) packet.sceneName = dialog.getSceneName();
        packet.actionJson = dialog.getButtonJSONData();
        if (book) this.dialogWindows.put(dialog.getSceneName(), dialog);
        this.dataPacket(packet);
    }

    /**
     * Shows a new setting page in game settings.
     * You can find out settings result by listening to PlayerFormRespondedEvent.
     *
     * @param window The FormWindow to show on settings page.
     * @return The form id to use in PlayerFormRespondedEvent.
     */
    public int addServerSettings(FormWindow window) {
        int id = this.formWindowCount++;

        this.serverSettings.put(id, window);
        return id;
    }

    /**
     * Creates and sends a BossBar to the player.
     *
     * @param text   The BossBar message.
     * @param length The BossBar percentage.
     * @return The BossBar ID. You should store it if you want to remove or update the BossBar later.
     */
    public long createBossBar(String text, int length) {
        DummyBossBar bossBar = new DummyBossBar.Builder(this).text(text).length(length).build();
        return this.createBossBar(bossBar);
    }

    /**
     * Creates and sends a BossBar to the player.
     *
     * @param dummyBossBar The DummyBossBar object (Instantiate it by the Class Builder).
     * @return The BossBar ID. You should store it if you want to remove or update the BossBar later.
     * @see DummyBossBar.Builder
     */
    public long createBossBar(DummyBossBar dummyBossBar) {
        this.dummyBossBars.put(dummyBossBar.getBossBarId(), dummyBossBar);
        dummyBossBar.create();
        return dummyBossBar.getBossBarId();
    }

    /**
     * Gets a DummyBossBar object.
     *
     * @param bossBarId The BossBar ID.
     * @return The DummyBossBar object.
     * @see DummyBossBar#setText(String) Set BossBar text.
     * @see DummyBossBar#setLength(float) Set BossBar length.
     * @see DummyBossBar#setColor(BossBarColor) Set BossBar color.
     */
    public DummyBossBar getDummyBossBar(long bossBarId) {
        return this.dummyBossBars.getOrDefault(bossBarId, null);
    }

    /**
     * Gets all DummyBossBar objects.
     *
     * @return A map of DummyBossBars.
     */
    public Map<Long, DummyBossBar> getDummyBossBars() {
        return dummyBossBars;
    }

    /**
     * Updates a BossBar.
     *
     * @param text      The new BossBar message.
     * @param length    The new BossBar length.
     * @param bossBarId The BossBar ID.
     */
    public void updateBossBar(String text, int length, long bossBarId) {
        if (this.dummyBossBars.containsKey(bossBarId)) {
            DummyBossBar bossBar = this.dummyBossBars.get(bossBarId);
            bossBar.setText(text);
            bossBar.setLength(length);
        }
    }

    /**
     * Removes a BossBar.
     *
     * @param bossBarId The BossBar ID.
     */
    public void removeBossBar(long bossBarId) {
        if (this.dummyBossBars.containsKey(bossBarId)) {
            this.dummyBossBars.get(bossBarId).destroy();
            this.dummyBossBars.remove(bossBarId);
        }
    }

    /**
     * Gets the window id from the specified Inventory.
     *
     * @param inventory The inventory.
     * @return The window id.
     */
    public int getWindowId(@NotNull Inventory inventory) {
        Preconditions.checkNotNull(inventory);
        if (this.windows.containsKey(inventory)) {
            return this.windows.get(inventory);
        }

        return -1;
    }

    /**
     * Gets the Inventory from the specified id.
     *
     * @param id The window id.
     * @return The Inventory.
     */
    public Inventory getWindowById(int id) {
        return this.windowIndex.get(id);
    }

    /**
     * Adds an inventory to the current player.
     *
     * @param inventory The Inventory object representing the window, must not be null.
     * @return The unique identifier assigned to the window if successfully added and opened; -1 if the window fails to be added.
     */
    public int addWindow(@NotNull Inventory inventory) {
        Preconditions.checkNotNull(inventory);
        if (this.windows.containsKey(inventory)) {
            return this.windows.get(inventory);
        }
        int cnt;
        this.windowsCnt = cnt = Math.max(1, ++this.windowsCnt % 100);
        if (this.windowIndex.containsKey(cnt)) {
            this.windowIndex.get(cnt).close(this);
        }
        this.windows.forcePut(inventory, cnt);

        if (this.spawned && inventory.open(this)) {
            updateTrackingPositions(true);
            return cnt;
        } else {
            this.removeWindow(inventory);
            return -1;
        }
    }

    /**
     * Adds an inventory to the current player with a specified id.
     *
     * @param inventory The Inventory object representing the window, must not be null.
     * @param forceId   The id to force.
     * @return The unique identifier assigned to the window if successfully added and opened; -1 if the window fails to be added.
     */
    public int addWindow(@NotNull Inventory inventory, Integer forceId) {
        Preconditions.checkNotNull(inventory);
        if (this.windows.containsKey(inventory)) {
            return this.windows.get(inventory);
        }
        int cnt;
        if (forceId == null) {
            this.windowsCnt = cnt = Math.max(1, ++this.windowsCnt % 101); // 1-100
        } else {
            cnt = forceId;
        }
        if (this.windowIndex.containsKey(cnt)) {
            this.windowIndex.get(cnt).close(this);
        }
        this.windows.forcePut(inventory, cnt);

        if (this.spawned) {
            if (inventory.open(this)) {
                updateTrackingPositions(true);
                return cnt;
            } else {
                this.removeWindow(inventory);
                return -1;
            }
        }
        return cnt;
    }

    /**
     * Gets the top window that is not permanent.
     *
     * @return An Optional containing the top Inventory if present, otherwise an empty Optional.
     */
    public Optional<Inventory> getTopWindow() {
        for (Entry<Inventory, Integer> entry : this.windows.entrySet()) {
            if (!this.permanentWindows.contains(entry.getValue())) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

    /**
     * Removes the specified Inventory from the player.
     *
     * @param inventory The inventory to be removed.
     */
    public void removeWindow(Inventory inventory) {
        Preconditions.checkNotNull(inventory);
        if (!this.permanentWindows.contains(windows.get(inventory))) {
            int windowId = this.getWindowId(inventory);
            playerHandle.setClosingWindowId(windowId);
            inventory.close(this);
            this.windows.remove(inventory);
            updateTrackingPositions(true);
        }
    }

    /**
     * Commonly used for refreshing all inventories.
     */
    public void sendAllInventories() {
        for (Inventory inv : this.windows.keySet()) {
            inv.sendContents(this);
            if (inv instanceof HumanInventory humanInventory) {
                humanInventory.sendArmorContents(this);
            }
        }
    }

    /**
     * Gets the cursor inventory of the player.
     *
     * @return The PlayerCursorInventory object.
     */
    public PlayerCursorInventory getCursorInventory() {
        return playerCursorInventory;
    }

    /**
     * Gets the crafting grid of the player.
     *
     * @return The CraftingGridInventory object.
     */
    public CraftingGridInventory getCraftingGrid() {
        return this.craftingGridInventory;
    }

    /**
     * Gets the creative output inventory of the player.
     *
     * @return The CreativeOutputInventory object.
     */
    public CreativeOutputInventory getCreativeOutputInventory() {
        return this.creativeOutputInventory;
    }

    /**
     * Resets the player's inventory.
     */
    @ApiStatus.Internal
    public void resetInventory() {
        if (spawned) {
            Map<Integer, Item> contents = this.getCraftingGrid().getContents();
            this.getCraftingGrid().clearAll();
            List<Item> puts = new ArrayList<>(contents.values());

            Map<Integer, Item> contents2 = this.getCursorInventory().getContents();
            this.getCursorInventory().clearAll();
            puts.addAll(contents2.values());

            Optional<Inventory> topWindow = getTopWindow();
            Inventory value;
            if (topWindow.isPresent()) {
                value = topWindow.get();
                if (value instanceof CraftTypeInventory || (value instanceof FakeInventory fakeInventory && fakeInventory.getFakeInventoryType().isCraftType())) {
                    puts.addAll(value.getContents().values());
                    value.clearAll();
                }
                removeWindow(value);
            }
            Item[] drops = getInventory().addItem(puts.toArray(Item.EMPTY_ARRAY));
            for (Item drop : drops) {
                this.dropItem(drop);
            }
        }
    }

    /**
     * Removes all windows.
     */
    public void removeAllWindows() {
        removeAllWindows(false);
    }

    /**
     * Removes all windows.
     *
     * @param permanent If false, it will skip deleting the corresponding window in permanentWindows.
     */
    public void removeAllWindows(boolean permanent) {
        for (Entry<Integer, Inventory> entry : new ArrayList<>(this.windowIndex.entrySet())) {
            if (!permanent && this.permanentWindows.contains(entry.getKey())) {
                continue;
            }
            this.removeWindow(entry.getValue());
        }
    }

    /**
     * Gets the id corresponding to the last closed window.
     *
     * @return The id of the last closed window.
     */
    public int getClosingWindowId() {
        return this.closingWindowId;
    }

    /**
     * Sets metadata for the player.
     *
     * @param metadataKey      The key for the metadata.
     * @param newMetadataValue The value for the metadata.
     */
    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        this.server.getPlayerMetadata().setMetadata(this, metadataKey, newMetadataValue);
    }

    /**
     * Gets metadata for the player.
     *
     * @param metadataKey The key for the metadata.
     * @return A list of MetadataValue objects.
     */
    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        return this.server.getPlayerMetadata().getMetadata(this, metadataKey);
    }

    /**
     * Checks if the player has metadata.
     *
     * @param metadataKey The key for the metadata.
     * @return True if the player has the metadata, otherwise false.
     */
    @Override
    public boolean hasMetadata(String metadataKey) {
        return this.server.getPlayerMetadata().hasMetadata(this, metadataKey);
    }

    /**
     * Removes metadata for the player.
     *
     * @param metadataKey  The key for the metadata.
     * @param owningPlugin The plugin that owns the metadata.
     */
    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        this.server.getPlayerMetadata().removeMetadata(this, metadataKey, owningPlugin);
    }

    /**
     * Called when a chunk changes.
     *
     * @param chunk The chunk that changed.
     */
    @Override
    public void onChunkChanged(IChunk chunk) {
        this.playerChunkManager.addSendChunk(chunk.getX(), chunk.getZ());
    }

    /**
     * Called when a chunk is loaded.
     *
     * @param chunk The chunk that was loaded.
     */
    @Override
    public void onChunkLoaded(IChunk chunk) {
    }

    /**
     * Called when a chunk is unloaded.
     *
     * @param chunk The chunk that was unloaded.
     */
    @Override
    public void onChunkUnloaded(IChunk chunk) {
        this.unloadChunk(chunk.getX(), chunk.getZ(), chunk.getProvider().getLevel());
    }

    /**
     * Gets the loader id.
     *
     * @return The loader id.
     */
    @Override
    public int getLoaderId() {
        return this.loaderId;
    }

    /**
     * Checks if the loader is active.
     *
     * @return True if the loader is active, otherwise false.
     */
    @Override
    public boolean isLoaderActive() {
        return this.isConnected();
    }

    /**
     * Gets the player's food data.
     *
     * @return The PlayerFood object.
     */
    public PlayerFood getFoodData() {
        return this.foodData;
    }

    /**
     * Switches the player's level.
     *
     * @param level The new level.
     * @return True if the level switch was successful, otherwise false.
     */
    @Override
    public boolean switchLevel(Level level) {
        Level oldLevel = this.level;
        if (super.switchLevel(level)) {
            SetSpawnPositionPacket spawnPosition = new SetSpawnPositionPacket();
            spawnPosition.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN;
            Position spawn = level.getSpawnLocation();
            spawnPosition.x = spawn.getFloorX();
            spawnPosition.y = spawn.getFloorY();
            spawnPosition.z = spawn.getFloorZ();
            spawnPosition.dimension = spawn.getLevel().getDimension();
            this.dataPacket(spawnPosition);

            // Remove old chunks
            for (long index : new ArrayList<>(playerChunkManager.getUsedChunks())) {
                int chunkX = Level.getHashX(index);
                int chunkZ = Level.getHashZ(index);
                this.unloadChunk(chunkX, chunkZ, oldLevel);
            }
            playerChunkManager.getUsedChunks().clear();

            SetTimePacket setTime = new SetTimePacket();
            setTime.time = level.getTime();
            this.dataPacket(setTime);

            GameRulesChangedPacket gameRulesChanged = new GameRulesChangedPacket();
            gameRulesChanged.gameRules = level.getGameRules();
            this.dataPacket(gameRulesChanged);

            if (oldLevel.getDimension() != level.getDimension()) {
                this.setDimension(level.getDimension());
            }
            updateTrackingPositions(true);
            return true;
        }

        return false;
    }

    /**
     * Sets whether to check for this player's movement.
     *
     * @param checkMovement True to check movement, otherwise false.
     */
    public void setCheckMovement(boolean checkMovement) {
        this.checkMovement = checkMovement;
    }

    /**
     * Checks if the player's movement is being checked.
     *
     * @return True if the player's movement is being checked, otherwise false.
     * @since 1.2.1.0-PN
     */
    public boolean isCheckingMovement() {
        return this.checkMovement;
    }

    /**
     * Gets the locale of the player.
     *
     * @return The locale of the player.
     */
    public synchronized Locale getLocale() {
        return this.locale.get();
    }

    /**
     * Sets the locale of the player.
     *
     * @param locale The new locale to set.
     */
    public synchronized void setLocale(Locale locale) {
        this.locale.set(locale);
    }

    /**
     * Gets the set of used chunks.
     *
     * @return An unmodifiable set of used chunks.
     */
    @UnmodifiableView
    public Set<Long> getUsedChunks() {
        return Collections.unmodifiableSet(playerChunkManager.getUsedChunks());
    }

    /**
     * Sets the sprinting state of the player.
     *
     * @param value True to set the player sprinting, otherwise false.
     */
    @Override
    public void setSprinting(boolean value) {
        if (value && this.getFreezingTicks() > 0) return;
        if (isSprinting() != value) {
            super.setSprinting(value);
            this.setMovementSpeed(value ? getMovementSpeed() * 1.3f : getMovementSpeed() / 1.3f);

            if (this.hasEffect(EffectType.SPEED)) {
                float movementSpeed = this.getMovementSpeed();
                this.sendMovementSpeed(value ? movementSpeed * 1.3f : movementSpeed);
            }
        }
    }

    /**
     * Teleports the player to another server.
     *
     * @param address The address of the server to teleport to.
     */
    public void transfer(InetSocketAddress address) {
        String hostName = address.getAddress().getHostAddress();
        int port = address.getPort();
        TransferPacket pk = new TransferPacket();
        pk.address = hostName;
        pk.port = port;
        this.dataPacket(pk);
    }

    /**
     * Gets the login chain data of the player.
     *
     * @return The login chain data of the player.
     */
    public LoginChainData getLoginChainData() {
        return this.loginChainData;
    }

    /**
     * Handles the pickup of an entity by the player.
     *
     * @param entity The entity to pick up.
     * @param near   True if the entity is near, otherwise false.
     * @return True if the entity was successfully picked up, otherwise false.
     */
    @ApiStatus.Internal
    public boolean pickupEntity(Entity entity, boolean near) {
        if (!this.spawned || !this.isAlive() || !this.isOnline() || this.isSpectator() || entity.isClosed()) {
            return false;
        }

        if (near) {
            Inventory inventory = this.inventory;
            if (entity instanceof EntityArrow entityArrow && entityArrow.hadCollision) {
                ItemArrow item = new ItemArrow();
                if (!this.isCreative()) {
                    // Should only collect to the offhand slot if the item matches what is already there
                    if (Objects.equals(this.offhandInventory.getItem(0).getId(), item.getId()) && this.offhandInventory.canAddItem(item)) {
                        inventory = this.offhandInventory;
                    } else if (!inventory.canAddItem(item)) {
                        return false;
                    }
                }

                InventoryPickupArrowEvent ev = new InventoryPickupArrowEvent(inventory, (EntityArrow) entity);

                int pickupMode = ((EntityArrow) entity).getPickupMode();
                if (pickupMode == EntityProjectile.PICKUP_NONE || (pickupMode == EntityProjectile.PICKUP_CREATIVE && !this.isCreative())) {
                    ev.setCancelled();
                }

                this.server.getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    return false;
                }

                TakeItemEntityPacket pk = new TakeItemEntityPacket();
                pk.entityId = this.getId();
                pk.target = entity.getId();
                Server.broadcastPacket(entity.getViewers().values(), pk);
                this.dataPacket(pk);

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

                InventoryPickupTridentEvent ev = new InventoryPickupTridentEvent(this.inventory, (EntityThrownTrident) entity);

                int pickupMode = ((EntityThrownTrident) entity).getPickupMode();
                if (pickupMode == EntityProjectile.PICKUP_NONE || (pickupMode == EntityProjectile.PICKUP_CREATIVE && !this.isCreative())) {
                    ev.setCancelled();
                }

                this.server.getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    return false;
                }

                TakeItemEntityPacket pk = new TakeItemEntityPacket();
                pk.entityId = this.getId();
                pk.target = entity.getId();
                Server.broadcastPacket(entity.getViewers().values(), pk);
                this.dataPacket(pk);

                if (!((EntityThrownTrident) entity).isCreative()) {
                    if (inventory.getItem(((EntityThrownTrident) entity).getFavoredSlot()).isNull()) {
                        inventory.setItem(((EntityThrownTrident) entity).getFavoredSlot(), item.clone());
                    } else {
                        inventory.addItem(item.clone());
                    }
                }
                entity.close();
                return true;
            } else if (entity instanceof EntityItem entityItem) {
                if (entityItem.getPickupDelay() <= 0) {
                    Item item = entityItem.getItem();

                    if (item != null) {
                        if (!this.isCreative() && !this.inventory.canAddItem(item)) {
                            return false;
                        }

                        InventoryPickupItemEvent ev;
                        this.server.getPluginManager().callEvent(ev = new InventoryPickupItemEvent(inventory, entityItem));
                        if (ev.isCancelled()) {
                            return false;
                        }

                        if (item.getBlockUnsafe() instanceof BlockWood) {
                            this.awardAchievement("mineWood");
                        } else if (Objects.equals(item.getId(), Item.DIAMOND)) {
                            this.awardAchievement("diamond");
                        }

                        TakeItemEntityPacket pk = new TakeItemEntityPacket();
                        pk.entityId = this.getId();
                        pk.target = entity.getId();
                        Server.broadcastPacket(entity.getViewers().values(), pk);
                        this.dataPacket(pk);

                        this.inventory.addItem(item.clone());
                        entity.close();
                        return true;
                    }
                }
            }
        }

        int tick = this.getServer().getTick();
        if (pickedXPOrb < tick && entity instanceof EntityXpOrb xpOrb && this.boundingBox.isVectorInside(entity)) {
            if (xpOrb.getPickupDelay() <= 0) {
                int exp = xpOrb.getExp();
                entity.kill();
                this.getLevel().addLevelEvent(LevelEventPacket.EVENT_SOUND_EXPERIENCE_ORB_PICKUP, 0, this);
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
     * Checks if the player is digging a block.
     *
     * @return True if the player is digging a block, otherwise false.
     */
    public boolean isBreakingBlock() {
        return this.breakingBlock != null;
    }

    /**
     * Shows a window of an Xbox account's profile.
     *
     * @param xuid The Xbox User ID.
     */
    public void showXboxProfile(String xuid) {
        ShowProfilePacket pk = new ShowProfilePacket();
        pk.xuid = xuid;
        this.dataPacket(pk);
    }

    /**
     * Starts fishing with the given fishing rod item.
     *
     * @param fishingRod The fishing rod item.
     */
    public void startFishing(Item fishingRod) {
        CompoundTag nbt = new CompoundTag()
                .putList("Pos", new ListTag<DoubleTag>()
                        .add(new DoubleTag(x))
                        .add(new DoubleTag(y + this.getEyeHeight()))
                        .add(new DoubleTag(z)))
                .putList("Motion", new ListTag<DoubleTag>()
                        .add(new DoubleTag(-Math.sin(yaw / 180 + Math.PI) * Math.cos(pitch / 180 * Math.PI)))
                        .add(new DoubleTag(-Math.sin(pitch / 180 * Math.PI)))
                        .add(new DoubleTag(Math.cos(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI))))
                .putList("Rotation", new ListTag<FloatTag>()
                        .add(new FloatTag((float) yaw))
                        .add(new FloatTag((float) pitch)));
        double f = 1.1;
        EntityFishingHook fishingHook = new EntityFishingHook(chunk, nbt, this);
        fishingHook.setMotion(new Vector3(-Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * f * f,
                -Math.sin(Math.toRadians(pitch)) * f * f,
                Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * f * f)
        );
        ProjectileLaunchEvent ev = new ProjectileLaunchEvent(fishingHook, this);
        this.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            fishingHook.close();
        } else {
            this.fishing = fishingHook;
            fishingHook.rod = fishingRod;
            fishingHook.checkLure();
            fishingHook.spawnToAll();
        }
    }

    /**
     * Stops fishing.
     *
     * @param click True if the stop was triggered by a click, otherwise false.
     */
    public void stopFishing(boolean click) {
        if (this.fishing != null && click) {
            fishing.reelLine();
        } else if (this.fishing != null) {
            this.fishing.close();
        }
        this.fishing = null;
    }

    /**
     * Checks if the player triggers pressure plates.
     * <p>
     * This method determines whether the player can trigger pressure plates based on their game mode.
     * Players in spectator mode do not trigger pressure plates.
     * </p>
     *
     * @return True if the player can trigger pressure plates, otherwise false.
     */
    @Override
    public boolean doesTriggerPressurePlate() {
        return this.gamemode != SPECTATOR;
    }

    /**
     * Returns a string representation of the player.
     * <p>
     * This method provides a string that includes the player's name and location.
     * </p>
     *
     * @return A string representation of the player.
     */
    @Override
    public String toString() {
        return "Player(name='" + getName() +
                "', location=" + super.toString() +
                ')';
    }

    /**
     * Adds the items to the main player inventory and drops any excess on the floor.
     *
     * @param items The items to give to the player.
     */
    public void giveItem(Item... items) {
        for (Item failed : getInventory().addItem(items)) {
            getLevel().dropItem(this, failed);
        }
    }

    /**
     * Gets the number of ticks since the player last slept. 1 tick = 0.05 seconds.
     *
     * @return The number of ticks since the player last slept.
     */
    public int getTimeSinceRest() {
        return timeSinceRest;
    }

    /**
     * Sets the number of ticks since the player last slept.
     *
     * @param timeSinceRest The number of ticks since the player last slept.
     * @see #getTimeSinceRest()
     */
    public void setTimeSinceRest(int timeSinceRest) {
        this.timeSinceRest = timeSinceRest;
    }

    // TODO: Support Translation Parameters
    public void sendPopupJukebox(String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_JUKEBOX_POPUP;
        pk.message = message;
        this.dataPacket(pk);
    }

    public void sendSystem(String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_SYSTEM;
        pk.message = message;
        this.dataPacket(pk);
    }

    public void sendWhisper(String message) {
        this.sendWhisper("", message);
    }

    public void sendWhisper(String source, String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_WHISPER;
        pk.source = source;
        pk.message = message;
        this.dataPacket(pk);
    }

    public void sendAnnouncement(String message) {
        this.sendAnnouncement("", message);
    }

    public void sendAnnouncement(String source, String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_ANNOUNCEMENT;
        pk.source = source;
        pk.message = message;
        this.dataPacket(pk);
    }

    public void completeUsingItem(int itemId, int action) {
        CompletedUsingItemPacket pk = new CompletedUsingItemPacket();
        pk.itemId = itemId;
        pk.action = action;
        this.dataPacket(pk);
    }

    /**
     * Checks if the player is showing credits.
     *
     * @return True if the player is showing credits, otherwise false.
     */
    public boolean isShowingCredits() {
        return showingCredits;
    }

    /**
     * Sets whether the player is showing credits.
     *
     * @param showingCredits True to show credits, otherwise false.
     */
    public void setShowingCredits(boolean showingCredits) {
        this.showingCredits = showingCredits;
        if (showingCredits) {
            ShowCreditsPacket pk = new ShowCreditsPacket();
            pk.eid = this.getId();
            pk.status = ShowCreditsPacket.STATUS_START_CREDITS;
            this.dataPacket(pk);
        }
    }

    /**
     * Shows the credits to the player.
     */
    public void showCredits() {
        this.setShowingCredits(true);
    }

    /**
     * Checks if the player has seen the credits.
     *
     * @return True if the player has seen the credits, otherwise false.
     */
    public boolean hasSeenCredits() {
        return showingCredits;
    }

    /**
     * Sets whether the player has seen the credits.
     *
     * @param hasSeenCredits True if the player has seen the credits, otherwise false.
     */
    public void setHasSeenCredits(boolean hasSeenCredits) {
        this.hasSeenCredits = hasSeenCredits;
    }

    /**
     * Sends a data packet immediately to the player.
     *
     * @param packet The data packet to send.
     * @return True if the packet was sent, otherwise false.
     */
    public boolean dataPacketImmediately(DataPacket packet) {
        if (!this.isConnected()) {
            return false;
        }
        DataPacketSendEvent ev = new DataPacketSendEvent(this, packet);
        this.server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return false;
        }
        this.getSession().sendPacketImmediately(packet);
        return true;
    }

    /**
     * Applies a screen shake effect to the player.
     *
     * @param intensity   The intensity of the shake.
     * @param duration    The duration of the shake.
     * @param shakeType   The type of shake.
     * @param shakeAction The action of the shake.
     */
    public void shakeCamera(float intensity, float duration, CameraShakePacket.CameraShakeType shakeType, CameraShakePacket.CameraShakeAction shakeAction) {
        CameraShakePacket packet = new CameraShakePacket();
        packet.intensity = intensity;
        packet.duration = duration;
        packet.shakeType = shakeType;
        packet.shakeAction = shakeAction;
        this.dataPacket(packet);
    }

    /**
     * Sends a Toast message box to the player.
     *
     * @param title   The title of the toast.
     * @param content The content of the toast.
     */
    public void sendToast(String title, String content) {
        ToastRequestPacket pk = new ToastRequestPacket();
        pk.title = title;
        pk.content = content;
        this.dataPacket(pk);
    }

    /**
     * Removes a line from the scoreboard.
     * <p>
     * This method sends a packet to remove a specific line from the player's scoreboard.
     * It checks if the line's scorer matches the player's scorer and updates the score tag accordingly.
     * </p>
     *
     * @param line The scoreboard line to remove.
     */
    @Override
    public void removeLine(IScoreboardLine line) {
        SetScorePacket packet = new SetScorePacket();
        packet.action = SetScorePacket.Action.REMOVE;
        var networkInfo = line.toNetworkInfo();
        if (networkInfo != null)
            packet.infos.add(networkInfo);
        this.dataPacket(packet);

        var scorer = new PlayerScorer(this);
        if (line.getScorer().equals(scorer) && line.getScoreboard().getViewers(DisplaySlot.BELOW_NAME).contains(this)) {
            this.setScoreTag("");
        }
    }

    /**
     * Updates a line on the scoreboard.
     * <p>
     * This method sends a packet to update a specific line on the player's scoreboard.
     * It checks if the line's scorer matches the player's scorer and updates the score tag accordingly.
     * </p>
     *
     * @param line The scoreboard line to update.
     */
    @Override
    public void updateScore(IScoreboardLine line) {
        SetScorePacket packet = new SetScorePacket();
        packet.action = SetScorePacket.Action.SET;
        var networkInfo = line.toNetworkInfo();
        if (networkInfo != null)
            packet.infos.add(networkInfo);
        this.dataPacket(packet);

        var scorer = new PlayerScorer(this);
        if (line.getScorer().equals(scorer) && line.getScoreboard().getViewers(DisplaySlot.BELOW_NAME).contains(this)) {
            this.setScoreTag(line.getScore() + " " + line.getScoreboard().getDisplayName());
        }
    }

    /**
     * Displays the scoreboard to the player in the specified slot.
     *
     * @param scoreboard The scoreboard to display.
     * @param slot       The display slot for the scoreboard.
     */
    @Override
    public void display(IScoreboard scoreboard, DisplaySlot slot) {
        SetDisplayObjectivePacket pk = new SetDisplayObjectivePacket();
        pk.displaySlot = slot;
        pk.objectiveName = scoreboard.getObjectiveName();
        pk.displayName = scoreboard.getDisplayName();
        pk.criteriaName = scoreboard.getCriteriaName();
        pk.sortOrder = scoreboard.getSortOrder();
        this.dataPacket(pk);

        // Client won't store the score of a scoreboard, so we should send the score to the client
        SetScorePacket pk2 = new SetScorePacket();
        pk2.infos = scoreboard.getLines().values().stream().map(IScoreboardLine::toNetworkInfo).filter(Objects::nonNull).collect(Collectors.toList());
        pk2.action = SetScorePacket.Action.SET;
        this.dataPacket(pk2);

        var scorer = new PlayerScorer(this);
        var line = scoreboard.getLine(scorer);
        if (slot == DisplaySlot.BELOW_NAME && line != null) {
            this.setScoreTag(line.getScore() + " " + scoreboard.getDisplayName());
        }
    }

    /**
     * Hides the scoreboard from the specified display slot.
     *
     * @param slot The display slot to hide the scoreboard from.
     */
    @Override
    public void hide(DisplaySlot slot) {
        SetDisplayObjectivePacket pk = new SetDisplayObjectivePacket();
        pk.displaySlot = slot;
        pk.objectiveName = "";
        pk.displayName = "";
        pk.criteriaName = "";
        pk.sortOrder = SortOrder.ASCENDING;
        this.dataPacket(pk);

        if (slot == DisplaySlot.BELOW_NAME) {
            this.setScoreTag("");
        }
    }

    /**
     * Removes the specified scoreboard from the player.
     *
     * @param scoreboard The scoreboard to remove.
     */
    @Override
    public void removeScoreboard(IScoreboard scoreboard) {
        RemoveObjectivePacket pk = new RemoveObjectivePacket();
        pk.objectiveName = scoreboard.getObjectiveName();
        this.dataPacket(pk);
    }

    /**
     * Checks if the player is opening the front side of a sign.
     *
     * @return True if the player is opening the front side of a sign, otherwise false.
     */
    public Boolean isOpenSignFront() {
        return openSignFront;
    }

    /**
     * Sets the status of the current player opening a sign.
     *
     * @param frontSide True means open sign front, vice versa. If it is null, it means that the player has not opened a sign.
     */
    public void setOpenSignFront(Boolean frontSide) {
        openSignFront = frontSide;
    }

    /**
     * Opens the player's sign editor GUI for the sign at the given position.
     *
     * @param position  The position of the sign.
     * @param frontSide True if the front side of the sign should be opened.
     */
    public void openSignEditor(Vector3 position, boolean frontSide) {
        if (openSignFront == null) {
            BlockEntity blockEntity = this.getLevel().getBlockEntity(position);
            if (blockEntity instanceof BlockEntitySign blockEntitySign) {
                if (blockEntitySign.getEditorEntityRuntimeId() == -1) {
                    blockEntitySign.setEditorEntityRuntimeId(this.getId());
                    OpenSignPacket openSignPacket = new OpenSignPacket();
                    openSignPacket.position = position.asBlockVector3();
                    openSignPacket.frontSide = frontSide;
                    this.dataPacket(openSignPacket);
                    setOpenSignFront(frontSide);
                }
            } else {
                throw new IllegalArgumentException("Block at this position is not a sign");
            }
        }
    }

    /**
     * Sets the fly sneaking status of the player.
     *
     * @param sneaking True if the player is fly sneaking.
     */
    public void setFlySneaking(boolean sneaking) {
        this.flySneaking = sneaking;
    }

    /**
     * Checks if the player is fly sneaking.
     *
     * @return True if the player is fly sneaking, otherwise false.
     */
    public boolean isFlySneaking() {
        return this.flySneaking;
    }

    /**
     * Gets the number of chunks sent per tick.
     *
     * @return The number of chunks sent per tick.
     */
    public int getChunkSendCountPerTick() {
        return chunksPerTick;
    }

    /**
     * Sets the ender chest open status.
     *
     * @param v True if the ender chest is open.
     */
    public void setEnderChestOpen(boolean v) {
        this.enderChestOpen = v;
    }

    /**
     * Checks if the ender chest is open.
     *
     * @return True if the ender chest is open, otherwise false.
     */
    public boolean getEnderChestOpen() {
        return this.enderChestOpen;
    }

    /**
     * Checks if the fake inventory is open.
     *
     * @return True if the fake inventory is open, otherwise false.
     */
    public boolean getFakeInventoryOpen() {
        return fakeInventoryOpen;
    }

    /**
     * Sets the fake inventory open status.
     *
     * @param fakeInventoryOpen True if the fake inventory is open.
     */
    public void setFakeInventoryOpen(boolean fakeInventoryOpen) {
        this.fakeInventoryOpen = fakeInventoryOpen;
    }

    /**
     * Gets the fog stack.
     *
     * @return The fog stack.
     */
    public List<PlayerFogPacket.Fog> getFogStack() {
        return fogStack;
    }

    /**
     * Sets the fog stack. To apply the effect to the client, you need to call {@link #sendFogStack}.
     *
     * @param fogStack The fog stack to set.
     */
    public void setFogStack(List<PlayerFogPacket.Fog> fogStack) {
        this.fogStack = fogStack;
    }

    /**
     * Gets the player info.
     *
     * @return The player info.
     */
    @NotNull
    public PlayerInfo getPlayerInfo() {
        return this.info;
    }
}
