package cn.nukkit;

import cn.nukkit.AdventureSettings.Type;
import cn.nukkit.api.*;
import cn.nukkit.block.*;
import cn.nukkit.block.customblock.CustomBlock;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.blockentity.BlockEntitySpawnable;
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
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.event.player.PlayerTeleportEvent.TeleportCause;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.event.server.DataPacketSendEvent;
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
import cn.nukkit.level.particle.PunchBlockParticle;
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
import cn.nukkit.plugin.Plugin;
import cn.nukkit.positiontracking.PositionTrackingService;
import cn.nukkit.potion.Effect;
import cn.nukkit.scheduler.AsyncTask;
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
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static cn.nukkit.utils.Utils.dynamic;

/**
 * 游戏玩家对象，代表操控的角色
 * <p>
 * Game player object, representing the controlled character
 *
 * @author MagicDroidX &amp; Box (Nukkit Project)
 */
@Log4j2
public class Player extends EntityHuman implements CommandSender, InventoryHolder, ChunkLoader, IPlayer, IScoreboardViewer {
    /**
     * 一个承载玩家的空数组静态常量
     * <p>
     * A empty array of static constants that host the player
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final Player[] EMPTY_ARRAY = new Player[0];
    public static final int SURVIVAL = 0;
    public static final int CREATIVE = 1;
    public static final int ADVENTURE = 2;
    public static final int SPECTATOR = 3;
    public static final int VIEW = SPECTATOR;
    public static final int SURVIVAL_SLOTS = 36;
    public static final int CREATIVE_SLOTS = 112;
    public static final int CRAFTING_SMALL = 0;
    public static final int CRAFTING_BIG = 1;
    public static final int CRAFTING_ANVIL = 2;
    public static final int CRAFTING_ENCHANT = 3;
    public static final int CRAFTING_BEACON = 4;
    public static final @PowerNukkitOnly int CRAFTING_GRINDSTONE = 1000;
    public static final @PowerNukkitOnly int CRAFTING_STONECUTTER = 1001;
    public static final @PowerNukkitOnly int CRAFTING_CARTOGRAPHY = 1002;
    public static final @PowerNukkitOnly int CRAFTING_SMITHING = 1003;

    /**
     * 村民交易window id
     * <p>
     * Villager trading window id
     */
    @PowerNukkitXOnly
    @Since("1.19.21-r1")
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
    public static final @PowerNukkitOnly int GRINDSTONE_WINDOW_ID = dynamic(5);
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final int SMITHING_WINDOW_ID = dynamic(6);
    public final HashSet<String> achievements = new HashSet<>();
    public final Map<Long, Boolean> usedChunks = new Long2ObjectOpenHashMap<>();
    public boolean playedBefore;
    public boolean spawned = false;
    public boolean loggedIn = false;
    @Since("1.4.0.0-PN")
    public boolean locallyInitialized = false;
    public int gamemode;
    public long lastBreak;
    /**
     * 每tick 当前位置与移动目标位置向量之差
     * <p>
     * The difference between the current position and the moving target position vector per tick
     */
    public Vector3 speed = null;
    public int craftingType = CRAFTING_SMALL;
    public long creationTime = 0;
    /**
     * 正在挖掘的方块
     * <p>
     * block being dig
     */
    public Block breakingBlock = null;
    /**
     * 正在挖掘的方向
     * <p>
     * direction of dig
     */
    @Since("1.19.60-r1")
    @PowerNukkitXOnly
    public BlockFace breakingBlockFace = null;
    public int pickedXPOrb = 0;
    public EntityFishingHook fishing = null;
    public long lastSkinChange;
    @Since("1.19.63-r1")
    @PowerNukkitXOnly
    protected long breakingBlockTime = 0;
    @Since("1.19.63-r1")
    @PowerNukkitXOnly
    protected double blockBreakProgress = 0;
    protected final SourceInterface interfaz;
    @Since("1.19.30-r1")
    @PowerNukkitXOnly
    protected final NetworkPlayerSession networkSession;
    protected final BiMap<Inventory, Integer> windows = HashBiMap.create();
    protected final BiMap<Integer, Inventory> windowIndex = windows.inverse();
    protected final Set<Integer> permanentWindows = new IntOpenHashSet();
    protected final InetSocketAddress rawSocketAddress;
    protected final Long2ObjectLinkedOpenHashMap<Boolean> loadQueue = new Long2ObjectLinkedOpenHashMap<>();
    protected final Map<UUID, Player> hiddenPlayers = new HashMap<>();
    protected final int chunksPerTick;
    protected final int spawnThreshold;
    protected int windowCnt = 4;
    @Since("1.4.0.0-PN")
    protected int closingWindowId = Integer.MIN_VALUE;
    protected int messageCounter = 2;
    protected PlayerUIInventory playerUIInventory;
    protected CraftingGrid craftingGrid;
    protected CraftingTransaction craftingTransaction;
    @Since("1.3.1.0-PN")
    protected EnchantTransaction enchantTransaction;
    @Since("1.4.0.0-PN")
    protected RepairItemTransaction repairItemTransaction;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    protected GrindstoneTransaction grindstoneTransaction;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    protected SmithingTransaction smithingTransaction;
    @PowerNukkitXOnly
    @Since("1.19.21-r1")
    protected TradingTransaction tradingTransaction;
    protected long randomClientId;
    @Deprecated
    @DeprecationDetails(since = "1.19.60-r1", reason = "Useless, use teleport directly")
    protected Vector3 forceMovement = null;
    @Deprecated
    @DeprecationDetails(since = "1.19.60-r1", reason = "Useless, use teleport directly")
    protected Vector3 teleportPosition = null;
    protected boolean connected = true;
    protected InetSocketAddress socketAddress;
    /**
     * 是否移除改玩家聊天中的颜色字符如 §c §1
     * <p>
     * Whether to remove the color character in the chat of the changed player as §c §1
     */
    protected boolean removeFormat = true;
    protected String username;
    protected String iusername;
    protected String displayName;
    protected static final int RESOURCE_PACK_CHUNK_SIZE = 8 * 1024; // 8KB

    /**
     * 这个值代表玩家是否正在使用物品(长按右键)，-1时玩家未使用物品，当玩家使用物品时该值为{@link Server#getTick() getTick()}的值.
     * <p>
     * This value represents whether the player is using the item or not (long right click), -1 means the player is not using the item, when the player is using the item this value is the value of {@link Server#getTick() getTick()}.
     */
    protected int startAction = -1;
    protected Vector3 sleeping = null;
    protected Long clientID = null;
    protected int chunkLoadCount = 0;
    protected int nextChunkOrderRun = 1;
    protected Vector3 newPosition = null;
    protected int chunkRadius;
    protected int viewDistance;
    protected Position spawnPosition;
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @PowerNukkitXDifference(info = "change as Position")
    protected Position spawnBlockPosition;

    /**
     * 代表玩家悬浮空中所经过的tick数.
     * <p>
     * Represents the number of ticks the player has passed through the air.
     */
    protected int inAirTicks = 0;
    protected int startAirTicks = 5;
    protected AdventureSettings adventureSettings;
    protected boolean checkMovement = true;
    protected PlayerFood foodData = null;
    protected boolean enableClientCommand = true;

    /**
     * 返回上次投掷末影珍珠时的{@link Server#getTick() getTick()}，这个值用于控制末影珍珠的冷却时间.
     * <p>
     * Returns the {@link Server#getTick() getTick()} from the last time the pearl was cast, which is used to control the cooldown time of the pearl.
     */
    protected int lastEnderPearl = 20;

    /**
     * 返回上次吃紫颂果时的{@link Server#getTick() getTick()}，这个值用于控制吃紫颂果的冷却时间.
     * <p>
     * Returns the {@link Server#getTick() getTick()} of the last time you ate a chorus fruit, which is used to control the cooldown time for eating chorus fruit.
     */
    protected int lastChorusFruitTeleport = 20;

    protected int formWindowCount = 0;
    protected Map<Integer, FormWindow> formWindows = new Int2ObjectOpenHashMap<>();
    protected Map<Integer, FormWindow> serverSettings = new Int2ObjectOpenHashMap<>();
    /**
     * 我们使用google的cache来存储NPC对话框发送信息
     * 原因是发送过去的对话框客户端有几率不响应，在特定情况下我们无法清除这些对话框，这会导致内存泄漏
     * 5分钟后未响应的对话框会被清除
     * <p>
     * We use Google's cache to store NPC dialogs to send messages
     * The reason is that there is a chance that the client will not respond to the dialogs sent, and in certain cases we cannot clear these dialogs, which can lead to memory leaks
     * Unresponsive dialogs will be cleared after 5 minutes
     */
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    protected Cache<String, FormWindowDialog> dialogWindows = Caffeine.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();
    protected Map<Long, DummyBossBar> dummyBossBars = new Long2ObjectLinkedOpenHashMap<>();
    protected boolean shouldLogin = false;
    protected double lastRightClickTime = 0.0;
    protected Vector3 lastRightClickPos = null;
    protected int lastInAirTick = 0;
    private static final float ROTATION_UPDATE_THRESHOLD = 1;
    private static final float MOVEMENT_DISTANCE_THRESHOLD = 0.1f;
    private final Queue<Location> clientMovements = PlatformDependent.newMpscQueue(4);
    private final AtomicReference<Locale> locale = new AtomicReference<>(null);
    private int unverifiedPackets;
    private String clientSecret;
    private int timeSinceRest;
    private String buttonText = "Button";
    private PermissibleBase perm = null;
    private int hash;
    private int exp = 0;
    private int expLevel = 0;
    private final int loaderId;
    private BlockVector3 lastBreakPosition = new BlockVector3();
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    private boolean hasSeenCredits;
    private boolean wasInSoulSandCompatible;
    private float soulSpeedMultiplier = 1;
    private Entity killer = null;
    /**
     * 用来暂存放玩家打开的末影箱实例对象，当玩家打开末影箱时该值为指定为那个末影箱，当玩家关闭末影箱后重新设置回null.
     * <p>
     * This is used to temporarily store the player's open EnderChest instance object, when the player opens the EnderChest the value is specified as that EnderChest, when the player closes the EnderChest reset back to null.
     */
    private BlockEnderChest viewingEnderChest = null;
    private TaskHandler delayedPosTrackingUpdate;
    private int noShieldTicks;
    @PowerNukkitXDifference(since = "1.19.80-r3", info = "change to protected")
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected boolean showingCredits;
    @PowerNukkitXDifference(since = "1.19.80-r3", info = "change to protected")
    protected static final int NO_SHIELD_DELAY = 10;
    @PowerNukkitXDifference(since = "1.19.80-r3", info = "change to protected")
    protected boolean inventoryOpen;
    @PowerNukkitXDifference(since = "1.19.80-r3", info = "change to protected")
    protected PlayerBlockActionData lastBlockAction;
    protected AsyncTask preLoginEventTask = null;
    protected boolean verified = false;
    protected LoginChainData loginChainData;
    /**
     * 玩家升级时播放音乐的时间
     * <p>
     * Time to play sound when player upgrades
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected int lastPlayerdLevelUpSoundTime = 0;
    /**
     * 玩家最后攻击的实体.
     * <p>
     * The entity that the player attacked last.
     */
    @PowerNukkitXOnly
    @Since("1.19.30-r1")
    protected Entity lastAttackEntity = null;
    /**
     * 玩家迷雾设置
     * <p>
     * Player Fog Settings
     */
    @PowerNukkitXOnly
    @Since("1.19.50-r3")
    @Getter
    @Setter
    protected List<PlayerFogPacket.Fog> fogStack = new ArrayList<>();
    /**
     * 最后攻击玩家的实体.
     * <p>
     * The entity that the player is attacked last.
     */
    @PowerNukkitXOnly
    @Since("1.19.30-r1")
    protected Entity lastBeAttackEntity = null;

    private boolean foodEnabled = true;

    @Since("1.19.80-r1")
    @PowerNukkitXOnly
    private final @NotNull PlayerHandle playerHandle = new PlayerHandle(this);

    @Since("1.19.80-r3")
    @PowerNukkitXOnly
    private boolean needDimensionChangeACK = false;
    private Boolean openSignFront = null;


    /**
     * 单元测试用的构造函数
     * <p>
     * Constructor for unit testing
     *
     * @param interfaz interfaz
     * @param clientID clientID
     * @param ip       IP地址
     * @param port     端口
     */
    @PowerNukkitOnly
    public Player(SourceInterface interfaz, Long clientID, String ip, int port) {
        this(interfaz, clientID, uncheckedNewInetSocketAddress(ip, port));
    }

    public Player(SourceInterface interfaz, Long clientID, InetSocketAddress socketAddress) {
        super(null, new CompoundTag());
        this.interfaz = interfaz;
        this.networkSession = interfaz.getSession(socketAddress);
        this.perm = new PermissibleBase(this);
        this.server = Server.getInstance();
        this.lastBreak = -1;
        this.rawSocketAddress = socketAddress;
        this.socketAddress = socketAddress;
        this.clientID = clientID;
        this.loaderId = Level.generateChunkLoaderId(this);
        this.chunksPerTick = this.server.getConfig("chunk-sending.per-tick", 8);
        this.spawnThreshold = this.server.getConfig("chunk-sending.spawn-threshold", 56);
        this.spawnPosition = null;
        this.gamemode = this.server.getGamemode();
        this.setLevel(this.server.getDefaultLevel());
        this.viewDistance = this.server.getViewDistance();
        this.chunkRadius = viewDistance;
        //this.newPosition = new Vector3(0, 0, 0);
        this.boundingBox = new SimpleAxisAlignedBB(0, 0, 0, 0, 0, 0);
        this.lastSkinChange = -1;

        this.uuid = null;
        this.rawUUID = null;

        this.creationTime = System.currentTimeMillis();
    }

    private static InetSocketAddress uncheckedNewInetSocketAddress(String ip, int port) {
        try {
            return new InetSocketAddress(InetAddress.getByName(ip), port);
        } catch (UnknownHostException exception) {
            throw new IllegalArgumentException(exception);
        }
    }

    /**
     * 将服务端侧游戏模式转换为网络包适用的游戏模式ID
     * 此方法是为了解决NK观察者模式ID为3而原版ID为6的问题
     *
     * @param gamemode 服务端侧游戏模式
     * @return 网络层游戏模式ID
     */
    @PowerNukkitXOnly
    @Since("1.19.50-r3")
    private static int toNetworkGamemode(int gamemode) {
        return gamemode != SPECTATOR ? gamemode : GameType.SPECTATOR.ordinal();
    }

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

    @SneakyThrows
    private List<DataPacket> unpackBatchedPackets(BatchPacket packet) {
        return this.server.getNetwork().unpackBatchedPackets(packet, this.server.isEnableSnappy() ? CompressionProvider.SNAPPY : CompressionProvider.ZLIB);
    }

    @PowerNukkitXDifference(since = "1.19.60-r1", info = "Auto-break custom blocks if client doesn't send the break data-pack.")
    @PowerNukkitXDifference(since = "1.19.80-r3", info = "change to protected")
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
                this.level.addParticle(new PunchBlockParticle(pos, block, face));
                //miningTimeRequired * 1000-101这个算法最匹配原版计算速度，我们并不想任何方块破坏处理都由服务端执行，只处理自定义方块以绕过原版固定挖掘时间的限制
                if (this.breakingBlock instanceof CustomBlock) {
                    var timeDiff = time - breakingBlockTime;
                    blockBreakProgress += timeDiff / (miningTimeRequired * 1000 - 101);
                    if (blockBreakProgress > 0.99) {
                        this.onBlockBreakAbort(pos, face);
                        this.onBlockBreakComplete(pos.asBlockVector3(), face);
                    }
                    breakingBlockTime = time;
                }
            }
        }
    }

    @PowerNukkitXDifference(since = "1.19.80-r3", info = "change to protected")
    protected void onBlockBreakStart(Vector3 pos, BlockFace face) {
        BlockVector3 blockPos = pos.asBlockVector3();
        long currentBreak = System.currentTimeMillis();
        // HACK: Client spams multiple left clicks so we need to skip them.
        if ((this.lastBreakPosition.equals(blockPos) && (currentBreak - this.lastBreak) < 10) || pos.distanceSquared(this) > 100) {
            return;
        }

        Block target = this.level.getBlock(pos);
        PlayerInteractEvent playerInteractEvent = new PlayerInteractEvent(this, this.inventory.getItemInHand(), target, face,
                target.getId() == 0 ? Action.LEFT_CLICK_AIR : Action.LEFT_CLICK_BLOCK);
        this.getServer().getPluginManager().callEvent(playerInteractEvent);
        if (playerInteractEvent.isCancelled()) {
            this.inventory.sendHeldItem(this);
            this.getLevel().sendBlocks(new Player[]{this}, new Block[]{target}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 0);
            if (target.getLevelBlockAtLayer(1) instanceof BlockLiquid) {
                this.getLevel().sendBlocks(new Player[]{this}, new Block[]{target.getLevelBlockAtLayer(1)}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 1);
            }
            return;
        }

        if (target.onTouch(this, playerInteractEvent.getAction()) != 0) return;

        Block block = target.getSide(face);
        if (block.getId() == Block.FIRE || block.getId() == BlockID.SOUL_FIRE) {
            this.level.setBlock(block, Block.get(BlockID.AIR), true);
            this.level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_EXTINGUISH_FIRE);
            return;
        }

        if (block.getId() == BlockID.SWEET_BERRY_BUSH && block.getDamage() == 0) {
            Item oldItem = playerInteractEvent.getItem();
            Item i = this.level.useBreakOn(block, oldItem, this, true);
            if (this.isSurvival() || this.isAdventure()) {
                this.getFoodData().updateFoodExpLevel(0.005);
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
                // 优化反矿透时玩家的挖掘体验
                if (this.getLevel().isAntiXrayEnabled() && this.getLevel().isPreDeObfuscate()) {
                    var vecList = new ArrayList<Vector3WithRuntimeId>(5);
                    Vector3WithRuntimeId tmpVec;
                    for (var each : BlockFace.values()) {
                        if (each == face) continue;
                        var tmpX = target.getFloorX() + each.getXOffset();
                        var tmpY = target.getFloorY() + each.getYOffset();
                        var tmpZ = target.getFloorZ() + each.getZOffset();
                        try {
                            tmpVec = new Vector3WithRuntimeId(tmpX, tmpY, tmpZ, getLevel().getBlockRuntimeId(tmpX, tmpY, tmpZ, 0), getLevel().getBlockRuntimeId(tmpX, tmpY, tmpZ, 1));
                            if (getLevel().getRawFakeOreToPutRuntimeIdMap().containsKey(tmpVec.getRuntimeIdLayer0())) {
                                vecList.add(tmpVec);
                            }
                        } catch (Exception ignore) {
                        }
                    }
                    this.getLevel().sendBlocks(new Player[]{this}, vecList.toArray(Vector3[]::new), UpdateBlockPacket.FLAG_ALL);
                }
            }
        }

        this.breakingBlock = target;
        this.breakingBlockFace = face;
        this.lastBreak = currentBreak;
        this.lastBreakPosition = blockPos;
    }

    @PowerNukkitXDifference(since = "1.19.80-r3", info = "change to protected")
    protected void onBlockBreakAbort(Vector3 pos, BlockFace face) {
        if (pos.distanceSquared(this) < 100) {// same as with ACTION_START_BREAK
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

    @PowerNukkitXDifference(since = "1.19.80-r3", info = "change to protected")
    protected void onBlockBreakComplete(BlockVector3 blockPos, BlockFace face) {
        if (!this.spawned || !this.isAlive()) {
            return;
        }

        this.resetCraftingGridType();

        Item handItem = this.getInventory().getItemInHand();
        Item clone = handItem.clone();

        boolean canInteract = this.canInteract(blockPos.add(0.5, 0.5, 0.5), this.isCreative() ? 13 : 7);
        if (canInteract) {
            handItem = this.level.useBreakOn(blockPos.asVector3(), face, handItem, this, true);
            if (handItem != null && this.isSurvival()) {
                this.getFoodData().updateFoodExpLevel(0.005);
                if (handItem.equals(clone) && handItem.getCount() == clone.getCount()) {
                    return;
                }

                if (clone.getId() == handItem.getId() || handItem.getId() == 0) {
                    inventory.setItemInHand(handItem);
                } else {
                    server.getLogger().debug("Tried to set item " + handItem.getId() + " but " + this.username + " had item " + clone.getId() + " in their hand slot");
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

    private void setTitle(String text) {
        SetTitlePacket packet = new SetTitlePacket();
        packet.text = text;
        packet.type = SetTitlePacket.TYPE_TITLE;
        this.dataPacket(packet);
    }

    //todo a lot on dimension
    private void setDimension(int dimension) {
        ChangeDimensionPacket pk = new ChangeDimensionPacket();
        pk.dimension = dimension;
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        this.dataPacket(pk);

        this.needDimensionChangeACK = true;
    }

    private void updateBlockingFlag() {
        boolean shouldBlock = getNoShieldTicks() == 0
                && (this.isSneaking() || getRiding() != null)
                && (this.getInventory().getItemInHand() instanceof ItemShield || this.getOffhandInventory().getItem(0) instanceof ItemShield);

        if (isBlocking() != shouldBlock) {
            this.setBlocking(shouldBlock);
        }
    }

    protected void sendNextChunk() {
        if (!this.connected) {
            return;
        }

        if (!loadQueue.isEmpty()) {
            int count = 0;
            ObjectIterator<Long2ObjectMap.Entry<Boolean>> iter = loadQueue.long2ObjectEntrySet().fastIterator();
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
                this.level.registerChunkLoader(this, chunkX, chunkZ, false);

                if (!this.level.populateChunk(chunkX, chunkZ)) {
                    if (this.spawned) {
                        continue;
                    } else {
                        break;
                    }
                }

                iter.remove();

                PlayerChunkRequestEvent ev = new PlayerChunkRequestEvent(this, chunkX, chunkZ);
                this.server.getPluginManager().callEvent(ev);
                this.level.requestChunk(chunkX, chunkZ, this);
            }
        }
        if (this.chunkLoadCount >= this.spawnThreshold && !this.spawned && loggedIn) {
            this.doFirstSpawn();
        }
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.addDefaultWindows();
    }

    /**
     * 完成completeLoginSequence后执行
     */
    protected void doFirstSpawn() {
        this.spawned = true;

        this.inventory.sendContents(this);
        this.inventory.sendHeldItem(this);
        this.inventory.sendArmorContents(this);
        this.offhandInventory.sendContents(this);
        this.setEnableClientCommand(true);

        SetTimePacket setTimePacket = new SetTimePacket();
        setTimePacket.time = this.level.getTime();
        this.dataPacket(setTimePacket);

        this.sendPlayStatus(PlayStatusPacket.PLAYER_SPAWN);

        PlayerJoinEvent playerJoinEvent = new PlayerJoinEvent(this,
                new TranslationContainer(TextFormat.YELLOW + "%multiplayer.player.joined", new String[]{
                        this.getDisplayName()
                })
        );

        this.server.getPluginManager().callEvent(playerJoinEvent);

        if (playerJoinEvent.getJoinMessage().toString().trim().length() > 0) {
            this.server.broadcastMessage(playerJoinEvent.getJoinMessage());
        }

        this.noDamageTicks = 60;

        this.getServer().sendRecipeList(this);


        for (long index : this.usedChunks.keySet()) {
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

        //todo Updater

        //Weather
        this.getLevel().sendWeather(this);

        //FoodLevel
        PlayerFood food = this.getFoodData();
        if (food.getLevel() != food.getMaxLevel()) {
            food.sendFoodLevel();
        }

        var scoreboardManager = this.getServer().getScoreboardManager();
        if (scoreboardManager != null) {//in test environment sometimes the scoreboard manager is null
            scoreboardManager.onPlayerJoin(this);
        }

        //update compass
        SetSpawnPositionPacket pk = new SetSpawnPositionPacket();
        pk.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN;
        pk.x = this.level.getSpawnLocation().getFloorX();
        pk.y = this.level.getSpawnLocation().getFloorY();
        pk.z = this.level.getSpawnLocation().getFloorZ();
        pk.dimension = this.level.getDimension();
        this.dataPacket(pk);

        this.sendFogStack();
        this.sendCameraPresets();
        if (this.getHealth() < 1) {
            this.setHealth(0);
        }
    }

    protected boolean orderChunks() {
        if (!this.connected) {
            return false;
        }

        this.nextChunkOrderRun = 200;

        loadQueue.clear();
        Long2ObjectOpenHashMap<Boolean> lastChunk = new Long2ObjectOpenHashMap<>(this.usedChunks);

        int centerX = (int) this.x >> 4;
        int centerZ = (int) this.z >> 4;

        int radius = spawned ? this.chunkRadius : (int) Math.ceil(Math.sqrt(spawnThreshold));
        int radiusSqr = radius * radius;
        long index;

        //player center chunk
        if (this.usedChunks.get(index = Level.chunkHash(centerX, centerZ)) != Boolean.TRUE) {
            this.loadQueue.put(index, Boolean.TRUE);
        }
        lastChunk.remove(index);
        for (int r = 1; r <= radius; r++) {
            int rr = r * r;
            for (int i = 0; i <= r; i++) {
                int distanceSqr = rr + i * i;
                if (distanceSqr > radiusSqr) continue;
                //right includes upper right corner
                if (this.usedChunks.get(index = Level.chunkHash(centerX + r, centerZ + i)) != Boolean.TRUE) {
                    this.loadQueue.put(index, Boolean.TRUE);
                }
                lastChunk.remove(index);

                //right includes lower right corner
                if (this.usedChunks.get(index = Level.chunkHash(centerX + r, centerZ - i)) != Boolean.TRUE) {
                    this.loadQueue.put(index, Boolean.TRUE);
                }
                lastChunk.remove(index);

                //left includes upper left corner
                if (this.usedChunks.get(index = Level.chunkHash(centerX - r, centerZ + i)) != Boolean.TRUE) {
                    this.loadQueue.put(index, Boolean.TRUE);
                }
                lastChunk.remove(index);

                //left includes lower left corner
                if (this.usedChunks.get(index = Level.chunkHash(centerX - r, centerZ - i)) != Boolean.TRUE) {
                    this.loadQueue.put(index, Boolean.TRUE);
                }
                lastChunk.remove(index);

                //Exclude duplicate corners
                if (i != r) {
                    //top
                    if (this.usedChunks.get(index = Level.chunkHash(centerX + i, centerZ + r)) != Boolean.TRUE) {
                        this.loadQueue.put(index, Boolean.TRUE);
                    }
                    lastChunk.remove(index);

                    if (this.usedChunks.get(index = Level.chunkHash(centerX - i, centerZ + r)) != Boolean.TRUE) {
                        this.loadQueue.put(index, Boolean.TRUE);
                    }
                    lastChunk.remove(index);

                    //end
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
            this.dataPacket(packet);
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
                        Block block = this.level.getBlock(this.temporalVector.setComponents(x, y, z));

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
        Block[] scaffoldingUnder = level.getCollisionBlocks(
                scanBoundingBox,
                true, true,
                b -> b.getId() == BlockID.SCAFFOLDING
        );

        setDataFlag(DATA_FLAGS_EXTENDED, DATA_FLAG_OVER_DESCENDABLE_BLOCK, scaffoldingUnder.length > 0);
        setDataFlag(DATA_FLAGS_EXTENDED, DATA_FLAG_IN_SCAFFOLDING, scaffolding);
        setDataFlag(DATA_FLAGS_EXTENDED, DATA_FLAG_OVER_SCAFFOLDING, scaffoldingUnder.length > 0);
        setDataFlag(DATA_FLAGS_EXTENDED, DATA_FLAG_IN_ASCENDABLE_BLOCK, scaffolding);

        if (endPortal) {
            if (!inEndPortal) {
                inEndPortal = true;
                if (this.getRiding() == null && this.getPassengers().isEmpty()) {
                    EntityPortalEnterEvent ev = new EntityPortalEnterEvent(this, PortalType.END);
                    getServer().getPluginManager().callEvent(ev);

                    if (!ev.isCancelled()) {
                        final Position newPos = EnumLevel.moveToTheEnd(this);
                        if (newPos != null) {
                            if (newPos.getLevel().getDimension() == Level.DIMENSION_THE_END) {
                                if (teleport(newPos, PlayerTeleportEvent.TeleportCause.END_PORTAL)) {
                                    server.getScheduler().scheduleDelayedTask(new Task() {
                                        @Override
                                        public void onRun(int currentTick) {
                                            // dirty hack to make sure chunks are loaded and generated before spawning player
                                            teleport(newPos, PlayerTeleportEvent.TeleportCause.END_PORTAL);
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

    protected void checkNearEntities() {
        for (Entity entity : this.level.getNearbyEntities(this.boundingBox.grow(1, 0.5, 1), this)) {
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
        //before check
        if (distance > 128) {
            invalidMotion = true;
        } else if (this.chunk == null || !this.chunk.isGenerated()) {
            BaseFullChunk chunk = this.level.getChunk(clientPos.getChunkX() >> 4, clientPos.getChunkX() >> 4, false);
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

        //update server-side position and rotation and aabb
        double diffX = clientPos.getX() - this.x;
        double diffY = clientPos.getY() - this.y;
        double diffZ = clientPos.getZ() - this.z;
        this.fastMove(diffX, diffY, diffZ);
        this.setRotation(clientPos.getYaw(), clientPos.getPitch(), clientPos.getHeadYaw());

        //after check
        double corrX = this.x - clientPos.getX();
        double corrY = this.y - clientPos.getY();
        double corrZ = this.z - clientPos.getZ();
        if (this.checkMovement && (Math.abs(corrX) > 0.5 || Math.abs(corrY) > 0.5 || Math.abs(corrZ) > 0.5) && this.riding == null && !this.hasEffect(Effect.LEVITATION) && !this.hasEffect(Effect.SLOW_FALLING) && !server.getAllowFlight()) {
            double diff = corrX * corrX + corrZ * corrZ;
            //这里放宽了判断，否则对角穿过脚手架会判断非法移动。
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

        //update server-side position and rotation and aabb
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

            if (!(invalidMotion = ev.isCancelled())) { //Yes, this is intended
                if (!now.equals(ev.getTo()) && this.riding == null) { //If plugins modify the destination
                    if (this.getGamemode() != Player.SPECTATOR)
                        this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, ev.getTo().clone(), VibrationType.TELEPORT));
                    this.teleport(ev.getTo(), null);
                } else {
                    if (this.getGamemode() != Player.SPECTATOR && (last.x != now.x || last.y != now.y || last.z != now.z)) {
                        if (this.isOnGround() && this.isGliding()) {
                            this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.clone(), VibrationType.ELYTRA_GLIDE));
                        } else if (this.isOnGround() && this.getSide(BlockFace.DOWN).getLevelBlock().getId() != BlockID.WOOL && !this.isSneaking()) {
                            this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.clone(), VibrationType.STEP));
                        } else if (this.isTouchingWater()) {
                            this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.clone(), VibrationType.SWIM));
                        }
                    }
                    this.broadcastMovement();
                }
            } else {
                this.blocksAround = blocksAround;
                this.collisionBlocks = collidingBlocks;
            }
        }

        //update speed
        if (this.speed == null) {
            this.speed = new Vector3(last.x - now.x, last.y - now.y, last.z - now.z);
        } else {
            this.speed.setComponents(last.x - now.x, last.y - now.y, last.z - now.z);
        }

        handleLogicInMove(invalidMotion, distance);

        //if plugin cancel move
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

    protected void offerMovementTask(Location newPosition) {
        var distance = newPosition.distanceSquared(this);
        var updatePosition = (float) Math.sqrt(distance) > MOVEMENT_DISTANCE_THRESHOLD;//sqrt distance
        var updateRotation = (float) Math.abs(this.getPitch() - newPosition.pitch) > ROTATION_UPDATE_THRESHOLD
                || (float) Math.abs(this.getYaw() - newPosition.yaw) > ROTATION_UPDATE_THRESHOLD
                || (float) Math.abs(this.getHeadYaw() - newPosition.headYaw) > ROTATION_UPDATE_THRESHOLD;
        var isHandle = this.isAlive() && this.spawned && !this.isSleeping() && (updatePosition || updateRotation);
        if (isHandle) {
            this.newPosition = newPosition;
            this.clientMovements.offer(newPosition);
        }
    }


    //NK原始处理移动的方法
    @Deprecated
    @DeprecationDetails(since = "1.19.60-r1", reason = "use handleMovement")
    protected void processMovement(int tickDiff) {
    }

    protected void handleLogicInMove(boolean invalidMotion, double distance) {
        if (!invalidMotion) {
            //处理饱食度更新
            if (this.isFoodEnabled() && this.getServer().getDifficulty() > 0) {
                //UpdateFoodExpLevel
                if (distance >= 0.05) {
                    double jump = 0;
                    double swimming = this.isInsideOfWater() ? 0.01 * distance : 0;
                    double distance2 = distance;
                    if (swimming != 0) distance2 = 0;
                    if (this.isSprinting()) {  //Running
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

            //处理冰霜行者附魔
            Enchantment frostWalker = inventory.getBoots().getEnchantment(Enchantment.ID_FROST_WALKER);
            if (frostWalker != null && frostWalker.getLevel() > 0 && !this.isSpectator() && this.y >= 1 && this.y <= 255) {
                int radius = 2 + frostWalker.getLevel();
                for (int coordX = this.getFloorX() - radius; coordX < this.getFloorX() + radius + 1; coordX++) {
                    for (int coordZ = this.getFloorZ() - radius; coordZ < this.getFloorZ() + radius + 1; coordZ++) {
                        Block block = level.getBlock(coordX, this.getFloorY() - 1, coordZ);
                        int layer = 0;
                        if ((block.getId() != Block.STILL_WATER && (block.getId() != Block.FLOWING_WATER || block.getDamage() != 0)) || block.up().getId() != Block.AIR) {
                            block = block.getLevelBlockAtLayer(1);
                            layer = 1;
                            if ((block.getId() != Block.STILL_WATER && (block.getId() != Block.FLOWING_WATER || block.getDamage() != 0)) || block.up().getId() != Block.AIR) {
                                continue;
                            }
                        }
                        WaterFrostEvent ev = new WaterFrostEvent(block, this);
                        server.getPluginManager().callEvent(ev);
                        if (!ev.isCancelled()) {
                            level.setBlock(block, layer, Block.get(Block.ICE_FROSTED), true, false);
                            level.scheduleUpdate(level.getBlock(block, layer), ThreadLocalRandom.current().nextInt(20, 40));
                        }
                    }
                }
            }

            //处理灵魂急行附魔
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
     * 处理LOGIN_PACKET中执行
     */
    protected void processLogin() {
        if (!this.server.isWhitelisted((this.getName()).toLowerCase())) {
            this.kick(PlayerKickEvent.Reason.NOT_WHITELISTED, "Server is white-listed");

            return;
        } else if (this.isBanned()) {
            String reason = this.server.getNameBans().getEntires().get(this.getName().toLowerCase()).getReason();
            this.kick(PlayerKickEvent.Reason.NAME_BANNED, !reason.isEmpty() ? "You are banned. Reason: " + reason : "You are banned");
            return;
        } else if (this.server.getIPBans().isBanned(this.getAddress())) {
            String reason = this.server.getIPBans().getEntires().get(this.getAddress()).getReason();
            this.kick(PlayerKickEvent.Reason.IP_BANNED, !reason.isEmpty() ? "You are banned. Reason: " + reason : "You are banned");
            return;
        }

        if (this.hasPermission(Server.BROADCAST_CHANNEL_USERS)) {
            this.server.getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_USERS, this);
        }
        if (this.hasPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE)) {
            this.server.getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE, this);
        }

        Player oldPlayer = null;
        for (Player p : new ArrayList<>(this.server.getOnlinePlayers().values())) {
            if (p != this && p.getName() != null && p.getName().equalsIgnoreCase(this.getName()) ||
                    this.getUniqueId().equals(p.getUniqueId())) {
                oldPlayer = p;
                break;
            }
        }
        CompoundTag nbt;
        if (oldPlayer != null) {
            oldPlayer.saveNBT();
            nbt = oldPlayer.namedTag;
            oldPlayer.close("", "disconnectionScreen.loggedinOtherLocation");
        } else {
            File legacyDataFile = new File(server.getDataPath() + "players/" + this.username.toLowerCase() + ".dat");
            File dataFile = new File(server.getDataPath() + "players/" + this.uuid.toString() + ".dat");
            if (legacyDataFile.exists() && !dataFile.exists()) {
                nbt = this.server.getOfflinePlayerData(this.username, false);

                if (!legacyDataFile.delete()) {
                    log.warn("Could not delete legacy player data for {}", this.username);
                }
            } else {
                nbt = this.server.getOfflinePlayerData(this.uuid, true);
            }
        }

        if (nbt == null) {
            this.close(this.getLeaveMessage(), "Invalid data");
            return;
        }

        if (loginChainData.isXboxAuthed() && server.getPropertyBoolean("xbox-auth") || !server.getPropertyBoolean("xbox-auth")) {
            server.updateName(this.uuid, this.username);
        }

        this.playedBefore = (nbt.getLong("lastPlayed") - nbt.getLong("firstPlayed")) > 1;


        nbt.putString("NameTag", this.username);

        int exp = nbt.getInt("EXP");
        int expLevel = nbt.getInt("expLevel");
        this.setExperience(exp, expLevel);

        this.gamemode = nbt.getInt("playerGameType") & 0x03;
        if (this.server.getForceGamemode()) {
            this.gamemode = this.server.getGamemode();
            nbt.putInt("playerGameType", this.gamemode);
        }

        this.adventureSettings = new AdventureSettings(this, nbt);

        Level level;
        if ((level = this.server.getLevelByName(nbt.getString("Level"))) == null) {
            this.setLevel(this.server.getDefaultLevel());
            nbt.putString("Level", this.level.getName());
            Position spawnLocation = this.level.getSafeSpawn();
            nbt.getList("Pos", DoubleTag.class)
                    .add(new DoubleTag("0", spawnLocation.x))
                    .add(new DoubleTag("1", spawnLocation.y))
                    .add(new DoubleTag("2", spawnLocation.z));
        } else {
            this.setLevel(level);
        }

        for (Tag achievement : nbt.getCompound("Achievements").getAllTags()) {
            if (!(achievement instanceof ByteTag)) {
                continue;
            }

            if (((ByteTag) achievement).getData() > 0) {
                this.achievements.add(achievement.getName());
            }
        }

        nbt.putLong("lastPlayed", System.currentTimeMillis() / 1000);

        UUID uuid = getUniqueId();
        nbt.putLong("UUIDLeast", uuid.getLeastSignificantBits());
        nbt.putLong("UUIDMost", uuid.getMostSignificantBits());

        if (this.server.getAutoSave()) {
            this.server.saveOfflinePlayerData(this.uuid, nbt, true);
        }

        this.sendPlayStatus(PlayStatusPacket.LOGIN_SUCCESS);
        this.server.onPlayerLogin(this);

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

        if (!this.namedTag.contains("TimeSinceRest")) {
            this.namedTag.putInt("TimeSinceRest", 0);
        }
        this.timeSinceRest = this.namedTag.getInt("TimeSinceRest");

        if (!this.namedTag.contains("HasSeenCredits")) {
            this.namedTag.putBoolean("HasSeenCredits", false);
        }
        this.hasSeenCredits = this.namedTag.getBoolean("HasSeenCredits");

        //以下两个List的元素一一对应
        if (!this.namedTag.contains("fogIdentifiers")) {
            this.namedTag.putList(new ListTag<StringTag>("fogIdentifiers"));
        }
        if (!this.namedTag.contains("userProvidedFogIds")) {
            this.namedTag.putList(new ListTag<StringTag>("userProvidedFogIds"));
        }
        var fogIdentifiers = this.namedTag.getList("fogIdentifiers", StringTag.class);
        var userProvidedFogIds = this.namedTag.getList("userProvidedFogIds", StringTag.class);
        for (int i = 0; i < fogIdentifiers.size(); i++) {
            this.fogStack.add(i, new PlayerFogPacket.Fog(Identifier.tryParse(fogIdentifiers.get(i).data), userProvidedFogIds.get(i).data));
        }

        if (!this.server.isCheckMovement()) {
            this.checkMovement = false;
        }

        ResourcePacksInfoPacket infoPacket = new ResourcePacksInfoPacket();
        infoPacket.resourcePackEntries = this.server.getResourcePackManager().getResourceStack();
        infoPacket.mustAccept = this.server.getForceResources();
        this.dataPacket(infoPacket);
    }

    /**
     * 异步登录任务成功完成后执行
     */
    protected void completeLoginSequence() {
        PlayerLoginEvent ev;
        this.server.getPluginManager().callEvent(ev = new PlayerLoginEvent(this, "Plugin reason"));
        if (ev.isCancelled()) {
            this.close(this.getLeaveMessage(), ev.getKickMessage());
            return;
        }

        Level level = null;
        if (this.namedTag.containsString("SpawnLevel")) {
            level = this.server.getLevelByName(this.namedTag.getString("SpawnLevel"));
        }
        if (this.namedTag.containsString("SpawnBlockLevel")) {
            level = this.server.getLevelByName(this.namedTag.getString("SpawnBlockLevel"));
        }
        if (level != null) {
            if (this.namedTag.containsInt("SpawnX") && this.namedTag.containsInt("SpawnY") && this.namedTag.containsInt("SpawnZ")) {
                this.spawnPosition = new Position(this.namedTag.getInt("SpawnX"), this.namedTag.getInt("SpawnY"), this.namedTag.getInt("SpawnZ"), level);
            } else {
                this.spawnPosition = null;
            }
            if (this.namedTag.containsInt("SpawnBlockPositionX") && this.namedTag.containsInt("SpawnBlockPositionY")
                    && this.namedTag.containsInt("SpawnBlockPositionZ")) {
                this.spawnBlockPosition = new Position(namedTag.getInt("SpawnBlockPositionX"), namedTag.getInt("SpawnBlockPositionY"), namedTag.getInt("SpawnBlockPositionZ"), level);
            } else {
                this.spawnBlockPosition = null;
            }
        }
        Vector3 worldSpawnPoint;
        if (this.spawnPosition == null) worldSpawnPoint = this.server.getDefaultLevel().getSafeSpawn();
        else worldSpawnPoint = spawnPosition;

        StartGamePacket startGamePacket = new StartGamePacket();
        startGamePacket.entityUniqueId = this.id;
        startGamePacket.entityRuntimeId = this.id;
        startGamePacket.playerGamemode = toNetworkGamemode(this.gamemode);
        startGamePacket.x = (float) this.x;
        startGamePacket.y = (float) (isOnGround() ? this.y + this.getEyeHeight() : this.y);//防止在地上生成容易陷进地里
        startGamePacket.z = (float) this.z;
        startGamePacket.yaw = (float) this.yaw;
        startGamePacket.pitch = (float) this.pitch;
        startGamePacket.seed = -1L;
        startGamePacket.dimension = (byte) (this.level.getDimension() & 0xff);
        startGamePacket.worldGamemode = toNetworkGamemode(getServer().getDefaultGamemode());
        startGamePacket.difficulty = this.server.getDifficulty();
        startGamePacket.spawnX = worldSpawnPoint.getFloorX();
        startGamePacket.spawnY = worldSpawnPoint.getFloorY();
        startGamePacket.spawnZ = worldSpawnPoint.getFloorZ();
        startGamePacket.hasAchievementsDisabled = true;
        startGamePacket.dayCycleStopTime = -1;
        startGamePacket.rainLevel = 0;
        startGamePacket.lightningLevel = 0;
        startGamePacket.commandsEnabled = this.isEnableClientCommand();
        startGamePacket.gameRules = getLevel().getGameRules();
        startGamePacket.levelId = "";
        startGamePacket.worldName = this.getServer().getNetwork().getName();
        startGamePacket.generator = (byte) ((this.level.getDimension() + 1) & 0xff); //0 旧世界, 1 主世界, 2 下界, 3末地
        startGamePacket.serverAuthoritativeMovement = getServer().getServerAuthoritativeMovement();
        //写入自定义方块数据
        startGamePacket.blockProperties.addAll(Block.getCustomBlockDefinitionList());
        this.dataPacketImmediately(startGamePacket);
        this.loggedIn = true;

        //写入自定义物品数据
        ItemComponentPacket itemComponentPacket = new ItemComponentPacket();
        if (this.getServer().isEnableExperimentMode() && !Item.getCustomItemDefinition().isEmpty()) {
            Int2ObjectOpenHashMap<ItemComponentPacket.Entry> entries = new Int2ObjectOpenHashMap<>();
            int i = 0;
            for (var entry : Item.getCustomItemDefinition().entrySet()) {
                try {
                    CompoundTag data = entry.getValue().nbt();
                    data.putShort("minecraft:identifier", i);
                    entries.put(i, new ItemComponentPacket.Entry(entry.getKey(), data));
                    i++;
                } catch (Exception e) {
                    log.error("ItemComponentPacket encoding error", e);
                }
            }
            itemComponentPacket.setEntries(entries.values().toArray(ItemComponentPacket.Entry.EMPTY_ARRAY));
        }
        this.dataPacket(itemComponentPacket);

        this.dataPacket(new BiomeDefinitionListPacket());
        this.dataPacket(new AvailableEntityIdentifiersPacket());
        this.inventory.sendCreativeContents();
        //发送玩家权限列表
        server.getOnlinePlayers().values().forEach(player -> {
            if (player != this) {
                player.adventureSettings.sendAbilities(Collections.singleton(this));
            }
        });

        this.sendAttributes();
        this.sendPotionEffects(this);
        this.sendData(this);
        this.sendAttributes();
        this.setNameTagVisible(true);
        this.setNameTagAlwaysVisible(true);
        this.setCanClimb(true);

        log.info(this.getServer().getLanguage().tr("nukkit.player.logIn",
                TextFormat.AQUA + this.username + TextFormat.WHITE,
                this.getAddress(),
                String.valueOf(this.getPort()),
                String.valueOf(this.id),
                this.level.getName(),
                String.valueOf(NukkitMath.round(this.x, 4)),
                String.valueOf(NukkitMath.round(this.y, 4)),
                String.valueOf(NukkitMath.round(this.z, 4))));

        if (this.isOp() || this.hasPermission("nukkit.textcolor")) {
            this.setRemoveFormat(false);
        }

        this.server.addOnlinePlayer(this);
        this.server.onPlayerCompleteLoginSequence(this);
    }

    /**
     * 玩家客户端初始化完成后调用
     */
    @PowerNukkitXOnly
    @Since("1.19.50-r3")
    protected void onPlayerLocallyInitialized() {
        /*
          我们在玩家客户端初始化后才发送游戏模式，以解决观察者模式疾跑速度不正确的问题
          只有在玩家客户端进入游戏显示后再设置观察者模式，疾跑速度才正常
          强制更新游戏模式以确保客户端会收到模式更新包
         */
        this.setGamemode(this.gamemode, false, null, true);
        //客户端初始化完毕再传送玩家，避免下落
        Location pos;
        if (this.server.isSafeSpawn() && (this.gamemode & 0x01) == 0) {
            pos = this.level.getSafeSpawn(this).getLocation();
            pos.yaw = this.yaw;
            pos.pitch = this.pitch;
        } else {
            pos = new Location(this.x, this.y, this.z, this.yaw, this.pitch, this.level);
        }
        PlayerRespawnEvent respawnEvent = new PlayerRespawnEvent(this, pos, true);
        this.server.getPluginManager().callEvent(respawnEvent);
        Position fromEvent = respawnEvent.getRespawnPosition();
        if (fromEvent instanceof Location) {
            pos = fromEvent.getLocation();
        } else {
            pos = fromEvent.getLocation();
            pos.yaw = this.yaw;
            pos.pitch = this.pitch;
        }
        this.teleport(pos, TeleportCause.PLAYER_SPAWN);
        this.spawnToAll();
    }

    /**
     * 判断重生锚是否有效如果重生锚有效则在重生锚上重生或者在床上重生
     * 如果玩家以上2种都没有则在服务器重生点重生
     * <p>
     * Determine if the respawn anchor is valid if the respawn anchor is valid then the anchor is respawned at the respawn anchor or reborn in bed
     * If the player has none of the above 2 then respawn at the server respawn point
     *
     * @param block
     * @return
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
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
        //the player cant respawn if the server is hardcore
        if (this.server.isHardcore()) {
            this.setBanned(true);
            return;
        }

        this.craftingType = CRAFTING_SMALL;
        this.resetCraftingGridType();

        //level spawn point < block spawn = self spawn
        PlayerRespawnEvent playerRespawnEvent = new PlayerRespawnEvent(this, this.getSpawn());
        if (spawnPosition != null && spawnBlockPosition == null) {//self spawn
            playerRespawnEvent.setRespawnPosition(spawnPosition);
        } else if (spawnBlockPosition != null && spawnPosition == null) {//block spawn
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
            } else {//block not available
                playerRespawnEvent.setRespawnPosition(this.getServer().getDefaultLevel().getSafeSpawn());
                // handle spawn point change when block spawn not available
                this.spawnPosition = null;
                this.spawnBlockPosition = null;
                sendMessage(new TranslationContainer(TextFormat.GRAY +
                        "%tile." + (this.getLevel().getDimension() == Level.DIMENSION_OVERWORLD ? "bed" : "respawn_anchor") + ".notValid"));
            }
        } else {//level spawn point
            playerRespawnEvent.setRespawnPosition(this.getServer().getDefaultLevel().getSafeSpawn());
        }

        this.server.getPluginManager().callEvent(playerRespawnEvent);
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
        this.teleport(Location.fromObject(respawnPos.add(0, this.getEyeHeight(), 0), respawnPos.level), TeleportCause.PLAYER_SPAWN);
        this.spawnToAll();
        this.scheduleUpdate();
    }

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

                //List<Player> reload = new ArrayList<>();
                for (Player player : new ArrayList<>(this.hasSpawned.values())) {
                    if (!newChunk.containsKey(player.getLoaderId())) {
                        this.despawnFrom(player);
                    } else {
                        newChunk.remove(player.getLoaderId());
                        //reload.add(player);
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
            this.dataPacketImmediately(pk);
        } else {
            this.dataPacket(pk);
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
                this.dataPacket(chunk);
            }
        }
    }

    @Since("1.4.0.0-PN")
    protected void removeWindow(Inventory inventory, boolean isResponse) {
        inventory.close(this);
        if (isResponse && !this.permanentWindows.contains(this.getWindowId(inventory))) {
            this.windows.remove(inventory);
            updateTrackingPositions(true);
        }
    }

    protected void addDefaultWindows() {
        this.addWindow(this.getInventory(), ContainerIds.INVENTORY, true, true);

        this.playerUIInventory = new PlayerUIInventory(this);
        this.addWindow(this.playerUIInventory, ContainerIds.UI, true);
        this.addWindow(this.offhandInventory, ContainerIds.OFFHAND, true, true);

        this.craftingGrid = this.playerUIInventory.getCraftingGrid();
        this.addWindow(this.craftingGrid, ContainerIds.NONE, true);

        //TODO: more windows
    }

    @Override
    protected float getBaseOffset() {
        return super.getBaseOffset();
    }

    @PowerNukkitOnly
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
     * @return {@link #lastAttackEntity}
     */
    @PowerNukkitXOnly
    @Since("1.19.30-r1")
    public Entity getLastAttackEntity() {
        return lastAttackEntity;
    }

    /**
     * @return {@link #lastBeAttackEntity}
     */
    @PowerNukkitXOnly
    @Since("1.19.30-r1")
    public Entity getLastBeAttackEntity() {
        return lastBeAttackEntity;
    }

    /**
     * 返回灵魂急行带来的速度增加倍速
     * <p>
     * Return to the speed increase multiplier brought by SOUL_SPEED Enchantment
     */
    public float getSoulSpeedMultiplier() {
        return this.soulSpeedMultiplier;
    }


    /**
     * 返回{@link Player#startAction}的值
     * <p>
     * Returns the value of {@link Player#startAction}
     *
     * @return int
     */
    public int getStartActionTick() {
        return startAction;
    }

    /**
     * 设置{@link Player#startAction}值为{@link Server#getTick() getTick()}
     * <p>
     * Set the {@link Player#startAction} value to {@link Server#getTick() getTick()}
     */
    public void startAction() {
        this.startAction = this.server.getTick();
    }


    /**
     * 设置{@link Player#startAction}值为-1
     * <p>
     * Set the {@link Player#startAction} value to -1
     */
    public void stopAction() {
        this.startAction = -1;
    }

    /**
     * 返回{@link Player#lastEnderPearl}的值
     * <p>
     * Returns the value of {@link Player#lastEnderPearl}
     *
     * @return int
     */
    public int getLastEnderPearlThrowingTick() {
        return lastEnderPearl;
    }

    /**
     * 设置{@link Player#lastEnderPearl}值为{@link Server#getTick() getTick()}
     * <p>
     * Set {@link Player#lastEnderPearl} value to {@link Server#getTick() getTick()}
     */
    public void onThrowEnderPearl() {
        this.lastEnderPearl = this.server.getTick();
    }

    /**
     * 返回{@link Player#lastChorusFruitTeleport}的值
     * <p>
     * Returns the value of {@link Player#lastChorusFruitTeleport}
     *
     * @return int
     */
    public int getLastChorusFruitTeleport() {
        return lastChorusFruitTeleport;
    }

    /**
     * 返回{@link Player#lastInAirTick}的值,代表玩家上次在空中的server tick
     * <p>
     * Returns the value of {@link Player#lastInAirTick},represent the last server tick the player was in the air
     *
     * @return int
     */
    @PowerNukkitXOnly
    @Since("1.19.63-r1")
    public int getLastInAirTick() {
        return this.lastInAirTick;
    }

    /**
     * 设置{@link Player#lastChorusFruitTeleport}值为{@link Server#getTick() getTick()}
     * <p>
     * Set {@link Player#lastChorusFruitTeleport} value to {@link Server#getTick() getTick()}
     */
    public void onChorusFruitTeleport() {
        this.lastChorusFruitTeleport = this.server.getTick();
    }

    /**
     * 返回{@link Player#viewingEnderChest}的值，只在玩家打开末影箱时有效.
     * <p>
     * Returns the value of {@link Player#viewingEnderChest}, which is only valid when the player opens the Ender Chest.
     */
    public BlockEnderChest getViewingEnderChest() {
        return viewingEnderChest;
    }


    /**
     * 设置{@link Player#viewingEnderChest}值为chest
     * <p>
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
     * 获取玩家离开的消息
     *
     * @return {@link TranslationContainer}
     */
    public TranslationContainer getLeaveMessage() {
        return new TranslationContainer(TextFormat.YELLOW + "%multiplayer.player.left", this.getDisplayName());
    }

    public String getClientSecret() {
        return clientSecret;
    }

    /**
     * This might disappear in the future.
     * Please use getUniqueId() instead (IP + clientId + name combo, in the future it'll change to real UUID for online auth)
     *
     * @return random client id
     */
    @Deprecated
    public Long getClientId() {
        return randomClientId;
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
    public boolean hasPlayedBefore() {
        return this.playedBefore;
    }

    /**
     * 获得玩家权限设置.
     * <p>
     * Get player permission settings.
     */
    public AdventureSettings getAdventureSettings() {
        return adventureSettings;
    }

    /**
     * 用于设置玩家权限，对应游戏中的玩家权限设置.
     * <p>
     * Used to set player permissions, corresponding to the game's player permissions settings.
     *
     * @param adventureSettings 玩家权限设置<br>player permissions settings
     */
    public void setAdventureSettings(AdventureSettings adventureSettings) {
        this.adventureSettings = adventureSettings.clone(this);
        this.adventureSettings.update();
    }


    /**
     * 设置{@link #inAirTicks}为0
     * <p>
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
     * 设置允许修改世界(未知原因设置完成之后，玩家不允许挖掘方块，但是可以放置方块)
     * <p>
     * Set allow to modify the world (after the unknown reason setting is completed, the player is not allowed to dig the blocks, but can place them)
     *
     * @param value 是否允许修改世界<br>Whether to allow modification of the world
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
     * 设置允许交互世界/容器
     *
     * @param value      是否允许交互世界
     * @param containers 是否允许交互容器
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
        if (this.spawned && player.spawned && this.isAlive() && player.getLevel() == this.level && player.canSee(this)/* && !this.isSpectator()*/) {
            super.spawnTo(player);

            if (this.isSpectator()) {
                //发送旁观者的游戏模式给对方，使得对方客户端正确渲染玩家实体
                var pk = new UpdatePlayerGameTypePacket();
                pk.gameType = GameType.SPECTATOR;
                pk.entityId = this.getId();
                player.dataPacket(pk);
            }
        }
    }

    @Override
    public Server getServer() {
        return this.server;
    }

    /**
     * 得到{@link #removeFormat}
     * <p>
     * get {@link #removeFormat}
     *
     * @return boolean
     */
    public boolean getRemoveFormat() {
        return removeFormat;
    }

    /**
     * 设置{@link #removeFormat}为指定值
     *
     * @param remove 是否清楚格式化字符<br>Whether remove the formatting character
     */
    public void setRemoveFormat(boolean remove) {
        this.removeFormat = remove;
    }

    public void setRemoveFormat() {
        this.setRemoveFormat(true);
    }

    /**
     * @param player 玩家
     * @return 是否可以看到该玩家<br>Whether the player can be seen
     */
    public boolean canSee(Player player) {
        return !this.hiddenPlayers.containsKey(player.getUniqueId());
    }

    /**
     * 从当前玩家实例的视角中隐藏指定玩家player
     * <p>
     * Hide the specified player from the view of the current player instance
     *
     * @param player 要隐藏的玩家<br>Players who want to hide
     */
    public void hidePlayer(Player player) {
        if (this == player) {
            return;
        }
        this.hiddenPlayers.put(player.getUniqueId(), player);
        player.despawnFrom(this);
    }

    /**
     * 从当前玩家实例的视角中显示指定玩家player
     * <p>
     * Show the specified player from the view of the current player instance
     *
     * @param player 要显示的玩家<br>Players who want to show
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
        return gamemode != SPECTATOR;
    }

    @Override
    public void resetFallDistance() {
        super.resetFallDistance();
        if (this.inAirTicks != 0) {
            this.startAirTicks = 5;
        }
        this.inAirTicks = 0;
        this.highestPosition = this.y;
    }

    @Override
    public boolean isOnline() {
        return this.connected && this.loggedIn;
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

        if (this.isEnableClientCommand() && spawned) this.sendCommandData();
    }

    public boolean isEnableClientCommand() {
        return this.enableClientCommand;
    }

    public void setEnableClientCommand(boolean enable) {
        this.enableClientCommand = enable;
        SetCommandsEnabledPacket pk = new SetCommandsEnabledPacket();
        pk.enabled = enable;
        this.dataPacket(pk);
        if (enable) this.sendCommandData();
    }

    public void sendCommandData() {
        if (!spawned) {
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
            //TODO: structure checking
            pk.commands = data;
            this.dataPacket(pk);
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

    public boolean isConnected() {
        return connected;
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
        if (this.spawned) {
            this.server.updatePlayerListData(this.getUniqueId(), this.getId(), this.getDisplayName(), this.getSkin(), this.getLoginChainData().getXUID());
        }
    }

    @Override
    public void setSkin(Skin skin) {
        super.setSkin(skin);
        if (this.spawned) {
//            this.server.updatePlayerListData(this.getUniqueId(), this.getId(), this.getDisplayName(), skin, this.getLoginChainData().getXUID());
            var skinPacket = new PlayerSkinPacket();
            skinPacket.uuid = this.getUniqueId();
            skinPacket.skin = this.getSkin();
            skinPacket.newSkinName = this.getSkin().getSkinId();
            skinPacket.oldSkinName = "";
            Server.broadcastPacket(Server.getInstance().getOnlinePlayers().values(), skinPacket);
        }
    }

    /**
     * 得到原始地址
     *
     * @return {@link String}
     */
    public String getRawAddress() {
        return this.rawSocketAddress.getAddress().getHostAddress();
    }

    /**
     * 得到原始端口
     *
     * @return int
     */
    public int getRawPort() {
        return this.rawSocketAddress.getPort();
    }

    /**
     * 得到原始套接字地址
     *
     * @return {@link InetSocketAddress}
     */
    public InetSocketAddress getRawSocketAddress() {
        return this.rawSocketAddress;
    }

    /**
     * 得到地址,如果开启waterdogpe兼容，该地址是被修改为兼容waterdogpe型的，反之则与{@link #rawSocketAddress} 一样
     * <p>
     * If waterdogpe compatibility is enabled, the address is modified to be waterdogpe compatible, otherwise it is the same as {@link #rawSocketAddress}
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
     * 得到套接字地址,如果开启waterdogpe兼容，该套接字地址是被修改为兼容waterdogpe型的，反正则与{@link #rawSocketAddress} 一样
     * <p>
     * If waterdogpe compatibility is enabled, the address is modified to be waterdogpe compatible, otherwise it is the same as {@link #rawSocketAddress}
     *
     * @return {@link InetSocketAddress}
     */
    public InetSocketAddress getSocketAddress() {
        return this.socketAddress;
    }

    /**
     * 获得下一个tick客户端玩家将要移动的位置
     * <p>
     * Get the position where the next tick client player will move
     *
     * @return the next position
     */
    public Position getNextPosition() {
        return this.newPosition != null ? new Position(this.newPosition.x, this.newPosition.y, this.newPosition.z, this.level) : this.getPosition();
    }

    /**
     * 玩家是否在睡觉
     * <p>
     * Whether the player is sleeping
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
     * 返回玩家当前是否正在使用某项物品（右击并按住）。
     * <p>
     * Returns whether the player is currently using an item (right-click and hold).
     *
     * @return {@link #startAction}
     */
    public boolean isUsingItem() {
        return this.getDataFlag(DATA_FLAGS, DATA_FLAG_ACTION) && this.startAction > -1;
    }

    /**
     * 设置玩家当前是否正在使用某项物品 {@link #startAction}（右击并按住）。
     * <p>
     * Set whether the player is currently using an item {@link #startAction} (right-click and hold).
     *
     * @param value 玩家当前是否正在使用某项物品<br>whether the player is currently using an item.
     */
    public void setUsingItem(boolean value) {
        this.startAction = value ? this.server.getTick() : -1;
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_ACTION, value);
    }

    /**
     * 获得移动设备玩家面对载具时出现的交互按钮的语言硬编码。
     * <p>
     * Get the language hardcoded for the interaction buttons that appear when mobile device players face the carrier.
     */
    public String getButtonText() {
        return this.buttonText;
    }

    /**
     * 设置移动设备玩家面对载具时出现的交互按钮的语言硬编码。
     * <p>
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
        level = level == null ? this.level : level;
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
     * @return 玩家是否在主世界(维度为0)<br>Is the player in the world(Dimension equal 0)
     */
    public boolean isInOverWorld() {
        return this.getLevel().getDimension() == 0;
    }

    /**
     * 获取该玩家的可用重生点,
     * <p>
     * Get the player's Spawn point
     *
     * @return {@link Position}
     */
    public Position getSpawn() {
        //level spawn point < block spawn = self spawn
        if (spawnPosition != null && spawnBlockPosition == null) {//self spawn
            return spawnPosition;
        } else if (spawnBlockPosition != null && spawnPosition == null) {//block spawn
            return spawnBlockPosition;
        } else {//level spawn point
            return this.getServer().getDefaultLevel().getSafeSpawn();
        }
    }

    /**
     * 保存玩家重生位置的方块的位置。当未知时可能为空。
     * <p>
     * The block that holds the player respawn position. May be null when unknown.
     * <p>
     * 保存着玩家重生位置的方块。当未知时可能为空。
     *
     * @return 床、重生锚的位置，或在未知时为空。<br>The position of a bed, respawn anchor, or null when unknown.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Deprecated
    @DeprecationDetails(since = "1.19.60-r1", reason = "same #getSpawn")
    public Position getSpawnBlock() {
        return this.getSpawn();
    }

    /**
     * 设置玩家的出生点/复活点。
     * <p>
     * Set the player's birth point.
     *
     * @param pos 出生点位置
     */
    @PowerNukkitDifference(info = "pos can be null now and if it is null,the player's spawn will use the level's default spawn")
    public void setSpawn(@Nullable Vector3 pos) {
        if (pos != null) {
            Level level;
            if (pos instanceof Position position && position.isValid()) {
                level = position.getLevel();
            } else {
                level = this.level;
            }
            this.spawnPosition = new Position(pos.x, pos.y, pos.z, level);
            this.spawnBlockPosition = null;
            SetSpawnPositionPacket pk = new SetSpawnPositionPacket();
            pk.spawnType = SetSpawnPositionPacket.TYPE_PLAYER_SPAWN;
            pk.x = (int) this.spawnPosition.x;
            pk.y = (int) this.spawnPosition.y;
            pk.z = (int) this.spawnPosition.z;
            pk.dimension = this.spawnPosition.level.getDimension();
            this.dataPacket(pk);
        } else {
            this.spawnPosition = null;
        }
    }


    /**
     * 设置保存玩家重生位置的方块的位置。当未知时可能为空。
     * <p>
     * Sets the position of the block that holds the player respawn position. May be null when unknown.
     * <p>
     * 设置保存着玩家重生位置的方块的位置。可以设置为空。
     *
     * @param spawnBlock 床位或重生锚的位置<br>The position of a bed or respawn anchor
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setSpawnBlock(@Nullable Vector3 spawnBlock) {
        if (spawnBlock == null) {
            this.spawnBlockPosition = null;
        } else {
            Level level;
            if (spawnBlock instanceof Position position && position.isValid()) {
                level = position.level;
            } else level = this.level;
            this.spawnBlockPosition = new Position(spawnBlock.x, spawnBlock.y, spawnBlock.z, level);
            this.spawnPosition = null;
            SetSpawnPositionPacket pk = new SetSpawnPositionPacket();
            pk.spawnType = SetSpawnPositionPacket.TYPE_PLAYER_SPAWN;
            pk.x = this.spawnBlockPosition.getFloorX();
            pk.y = this.spawnBlockPosition.getFloorY();
            pk.z = this.spawnBlockPosition.getFloorZ();
            pk.dimension = this.spawnBlockPosition.level.getDimension();
            this.dataPacket(pk);
        }
    }

    public void sendChunk(int x, int z, DataPacket packet) {
        if (!this.connected) {
            return;
        }

        this.usedChunks.put(Level.chunkHash(x, z), Boolean.TRUE);
        this.chunkLoadCount++;

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

    public void sendChunk(int x, int z, int subChunkCount, byte[] payload) {
        LevelChunkPacket pk = new LevelChunkPacket();
        pk.chunkX = x;
        pk.chunkZ = z;
        pk.subChunkCount = subChunkCount;
        pk.data = payload;

        this.sendChunk(x, z, pk);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void updateTrackingPositions() {
        updateTrackingPositions(false);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
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

    @DeprecationDetails(by = "Cloudburst Nukkit", since = "1.4.0.0-PN", replaceWith = "dataPacket(DataPacket)",
            reason = "Batching packet is now handled near the RakNet layer")
    @Deprecated
    public boolean batchDataPacket(DataPacket packet) {
        return this.dataPacket(packet);
    }

    /**
     * 0 is true
     * -1 is false
     * other is identifer
     *
     * @param packet 发送的数据包<br>packet to send
     * @return 数据包是否成功发送<br>packet successfully sent
     */
    public boolean dataPacket(DataPacket packet) {
        if (!this.connected) {
            return false;
        }
        DataPacketSendEvent ev = new DataPacketSendEvent(this, packet);
        this.server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return false;
        }
        if (log.isTraceEnabled() && !server.isIgnoredPacket(packet.getClass())) {
            log.trace("Outbound {}: {}", this.getName(), packet);
        }
        this.getNetworkSession().sendPacket(packet);
        return true;
    }

    @DeprecationDetails(by = "Cloudburst Nukkit", since = "2019-05-08", replaceWith = "dataPacket(DataPacket)",
            reason = "ACKs are handled by the RakNet layer only")
    @PowerNukkitDifference(since = "1.4.0.0-PN",
            info = "Cloudburst changed the return values from 0/-1 to 1/0, breaking backward compatibility for no reason, " +
                    "we reversed that.")
    @Deprecated
    public int dataPacket(DataPacket packet, boolean needACK) {
        return dataPacket(packet) ? 0 : -1;
    }

    /**
     * 0 is true
     * -1 is false
     * other is identifer
     *
     * @param packet packet to send
     * @return packet successfully sent
     */
    @Deprecated
    @DeprecationDetails(by = "Cloudburst Nukkit", since = "1.4.0.0-PN", replaceWith = "dataPacket(DataPacket)",
            reason = "Direct packets are no longer allowed")
    public boolean directDataPacket(DataPacket packet) {
        return this.dataPacket(packet);
    }

    @DeprecationDetails(by = "Cloudburst Nukkit", since = "2019-05-08", replaceWith = "dataPacket(DataPacket)",
            reason = "ACK are handled by the RakNet layer and direct packets are no longer allowed")
    @PowerNukkitDifference(since = "1.4.0.0-PN",
            info = "Cloudburst changed the return values from 0/-1 to 1/0, breaking backward compatibility for no reason, " +
                    "we reversed that.")
    @Deprecated
    public int directDataPacket(DataPacket packet, boolean needACK) {
        return this.dataPacket(packet) ? 0 : -1;
    }

    @Since("1.19.30-r1")
    @PowerNukkitXOnly
    public void forceDataPacket(DataPacket packet, Runnable callback) {
        this.networkSession.sendImmediatePacket(packet, (callback == null ? () -> {
        } : callback));
    }

    /**
     * 得到该玩家的网络延迟。
     * <p>
     * Get the network latency of the player.
     *
     * @return int
     */
    public int getPing() {
        return this.interfaz.getNetworkLatency(this);
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

        this.setDataProperty(new IntPositionEntityData(DATA_PLAYER_BED_POSITION, (int) pos.x, (int) pos.y, (int) pos.z));
        this.setDataFlag(DATA_PLAYER_FLAGS, DATA_PLAYER_FLAG_SLEEP, true);
        this.setSpawnBlock(Position.fromObject(pos, getLevel()));
        this.level.sleepTicks = 60;

        this.timeSinceRest = 0;

        return true;
    }

    public void stopSleep() {
        if (this.sleeping != null) {
            this.server.getPluginManager().callEvent(new PlayerBedLeaveEvent(this, this.level.getBlock(this.sleeping)));

            this.sleeping = null;
            this.setDataProperty(new IntPositionEntityData(DATA_PLAYER_BED_POSITION, 0, 0, 0));
            this.setDataFlag(DATA_PLAYER_FLAGS, DATA_PLAYER_FLAG_SLEEP, false);


            this.level.sleepTicks = 0;

            AnimatePacket pk = new AnimatePacket();
            pk.eid = this.id;
            pk.action = AnimatePacket.Action.WAKE_UP;
            this.dataPacket(pk);
        }
    }

    public boolean awardAchievement(String achievementId) {
        if (!Server.getInstance().getPropertyBoolean("achievements", true)) {
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
     * 得到gamemode。
     * <p>
     * Get gamemode.
     *
     * @return int
     */
    public int getGamemode() {
        return gamemode;
    }

    public boolean setGamemode(int gamemode) {
        return this.setGamemode(gamemode, false, null);
    }

    /**
     * AdventureSettings=null
     *
     * @see #setGamemode(int, boolean, AdventureSettings)
     */
    public boolean setGamemode(int gamemode, boolean serverSide) {
        return this.setGamemode(gamemode, serverSide, null);
    }

    public boolean setGamemode(int gamemode, boolean serverSide, AdventureSettings newSettings) {
        return this.setGamemode(gamemode, serverSide, newSettings, false);
    }

    /**
     * 设置gamemode
     *
     * @param gamemode    要设置的玩家游戏模式
     * @param serverSide  是否只更新服务端侧玩家游戏模式。若为true，则不会向客户端发送游戏模式更新包
     * @param newSettings 新的AdventureSettings
     * @param forceUpdate 是否强制更新。若为true，将取消对形参'gamemode'的检查
     * @return gamemode
     */
    public boolean setGamemode(int gamemode, boolean serverSide, AdventureSettings newSettings, boolean forceUpdate) {
        if (!forceUpdate && (gamemode < 0 || gamemode > 3 || this.gamemode == gamemode)) {
            return false;
        }

        if (newSettings == null) {
            newSettings = this.getAdventureSettings().clone(this);
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
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_HAS_COLLISION, false);
        } else {
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_HAS_COLLISION, true);
        }

        this.namedTag.putInt("playerGameType", this.gamemode);

        this.setAdventureSettings(ev.getNewAdventureSettings());

        if (!serverSide) {
            var pk = new UpdatePlayerGameTypePacket();
            var networkGamemode = toNetworkGamemode(gamemode);
            pk.gameType = GameType.from(networkGamemode);
            pk.entityId = this.getId();
            var players = Sets.newHashSet(Server.getInstance().getOnlinePlayers().values());
            //不向自身发送UpdatePlayerGameTypePacket，我们将使用SetPlayerGameTypePacket
            players.remove(this);
            //我们需要给所有玩家发送此包，来使玩家客户端能正确渲染玩家实体
            //eg: 观察者模式玩家对于gm 0 1 2的玩家不可见
            Server.broadcastPacket(players, pk);
            //对于自身，我们使用SetPlayerGameTypePacket来确保与WaterDog的兼容
            var pk2 = new SetPlayerGameTypePacket();
            pk2.gamemode = networkGamemode;
            this.dataPacket(pk2);
        }

        this.resetFallDistance();

        return true;
    }

    public void sendSettings() {
        this.getAdventureSettings().update();
    }

    /**
     * 该玩家是否为生存模式。
     * <p>
     * Whether the player is in survival mode?
     *
     * @return boolean
     */
    public boolean isSurvival() {
        return this.gamemode == SURVIVAL;
    }

    /**
     * 该玩家是否为创造模式。
     * <p>
     * Whether the player is in creative mode?
     *
     * @return boolean
     */
    public boolean isCreative() {
        return this.gamemode == CREATIVE;
    }

    /**
     * 该玩家是否为观察者模式。
     * <p>
     * Whether the player is in spectator mode?
     *
     * @return boolean
     */
    public boolean isSpectator() {
        return this.gamemode == SPECTATOR;
    }

    /**
     * 该玩家是否为冒险模式。
     * <p>
     * Whether the player is in adventure mode?
     *
     * @return boolean
     */
    public boolean isAdventure() {
        return this.gamemode == ADVENTURE;
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

        this.x += dx;
        this.y += dy;
        this.z += dz;
        this.recalculateBoundingBox();

        this.checkChunks();

        if (!this.isSpectator()) {
            if (!this.onGround || dy != 0) {
                AxisAlignedBB bb = this.boundingBox.clone();
                bb.setMinY(bb.getMinY() - 0.75);
                this.onGround = this.level.getCollisionBlocks(bb).length > 0;
            }
            this.isCollided = this.onGround;
            this.updateFallState(this.onGround);
        }
        return true;
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public AxisAlignedBB reCalcOffsetBoundingBox() {
        float dx = this.getWidth() / 2;
        float dz = this.getWidth() / 2;
        return this.offsetBoundingBox.setBounds(
                this.x - dx, this.y, this.z - dz,
                this.x + dx, this.y + this.getHeight(), this.z + dz
        );
    }

    @Override
    public void addMovement(double x, double y, double z, double yaw, double pitch, double headYaw) {
        this.sendPosition(new Vector3(x, y, z), yaw, pitch, MovePlayerPacket.MODE_NORMAL, this.getViewers().values().toArray(EMPTY_ARRAY));
    }

    /**
     * 每次调用此方法都会向客户端发送motion包。若多次调用，motion将在客户端叠加<p/>
     *
     * @param motion 运动向量<br>a motion vector
     * @return 调用是否成功
     */
    @Override
    public boolean setMotion(Vector3 motion) {
        if (super.setMotion(motion)) {
            if (this.chunk != null) {
                this.addMotion(this.motionX, this.motionY, this.motionZ);  //Send to others
                SetEntityMotionPacket pk = new SetEntityMotionPacket();
                pk.eid = this.id;
                pk.motionX = (float) motion.x;
                pk.motionY = (float) motion.y;
                pk.motionZ = (float) motion.z;
                this.dataPacket(pk);  //Send to self
            }

            if (this.motionY > 0) {
                //todo: check this
                this.startAirTicks = (int) ((-(Math.log(this.getGravity() / (this.getGravity() + this.getDrag() * this.motionY))) / this.getDrag()) * 2 + 5);
            }

            return true;
        }

        return false;
    }

    public void sendAttributes() {
        UpdateAttributesPacket pk = new UpdateAttributesPacket();
        pk.entityId = this.getId();
        pk.entries = new Attribute[]{
                Attribute.getAttribute(Attribute.MAX_HEALTH).setMaxValue(this.getMaxHealth()).setValue(health > 0 ? (health < getMaxHealth() ? health : getMaxHealth()) : 0),
                Attribute.getAttribute(Attribute.MAX_HUNGER).setValue(this.getFoodData().getLevel()),
                Attribute.getAttribute(Attribute.MOVEMENT_SPEED).setValue(this.getMovementSpeed()),
                Attribute.getAttribute(Attribute.EXPERIENCE_LEVEL).setValue(this.getExperienceLevel()),
                Attribute.getAttribute(Attribute.EXPERIENCE).setValue(((float) this.getExperience()) / calculateRequireExperience(this.getExperienceLevel()))
        };
        this.dataPacket(pk);
    }

    /**
     * 将迷雾设定发送到客户端
     */
    @PowerNukkitXOnly
    @Since("1.19.50-r3")
    public void sendFogStack() {
        var pk = new PlayerFogPacket();
        pk.setFogStack(this.fogStack);
        pk.encode();
        this.dataPacket(pk);
    }

    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    public void sendCameraPresets() {
        var presetListTag = new ListTag<CompoundTag>("presets");
        for (var preset : CameraPreset.getPresets().values()) {
            presetListTag.add(preset.serialize());
        }
        var pk = new CameraPresetsPacket();
        pk.setData(new CompoundTag().putList("presets", presetListTag));
        dataPacket(pk);
    }

    @Override
    @PowerNukkitXDifference(info = "Calculate fall distance when wearing elytra", since = "1.19.60-r1")
    public boolean onUpdate(int currentTick) {
        if (!this.loggedIn) {
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

        if (!this.isAlive() && this.spawned) {
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
                    this.highestPosition = this.y;
                } else {
                    this.lastInAirTick = server.getTick();
                    //检测玩家是否异常飞行
                    if (this.checkMovement && !this.isGliding() && !server.getAllowFlight() && !this.getAdventureSettings().get(Type.ALLOW_FLIGHT) && this.inAirTicks > 20 && !this.isSleeping() && !this.isImmobile() && !this.isSwimming() && this.riding == null && !this.hasEffect(Effect.LEVITATION) && !this.hasEffect(Effect.SLOW_FALLING)) {
                        double expectedVelocity = (-this.getGravity()) / ((double) this.getDrag()) - ((-this.getGravity()) / ((double) this.getDrag())) * Math.exp(-((double) this.getDrag()) * ((double) (this.inAirTicks - this.startAirTicks)));
                        double diff = (this.speed.y - expectedVelocity) * (this.speed.y - expectedVelocity);

                        Block block = level.getBlock(this);
                        int blockId = block.getId();
                        boolean ignore = blockId == Block.LADDER || blockId == Block.VINE || blockId == Block.COBWEB
                                || blockId == Block.SCAFFOLDING;// || (blockId == Block.SWEET_BERRY_BUSH && block.getDamage() > 0);

                        if (!this.hasEffect(Effect.JUMP_BOOST) && diff > 0.6 && expectedVelocity < this.speed.y && !ignore) {
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

                    // Wiki: 使用鞘翅滑翔时在垂直高度下降率低于每刻 0.5 格的情况下，摔落高度被重置为 1 格。
                    // Wiki: 玩家在较小的角度和足够低的速度上着陆不会受到坠落伤害。着陆时临界伤害角度为50°，伤害值等同于玩家从滑行的最高点直接摔落到着陆点受到的伤害。
                    if (this.isGliding() && Math.abs(this.speed.y) < 0.5 && this.getPitch() <= 40) {
                        this.resetFallDistance();
                    }

                    ++this.inAirTicks;

                }

                if (this.getFoodData() != null) this.getFoodData().update(tickDiff);
            }

            if (!this.isSleeping()) {
                this.timeSinceRest++;
            }

            if (this.server.getServerAuthoritativeMovement() > 0) {//仅服务端权威使用，因为客户端权威continue break是正常的
                onBlockBreakContinue(breakingBlock, breakingBlockFace);
            }

            //reset move status
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

        if (this.spawned && this.dummyBossBars.size() > 0 && currentTick % 100 == 0) {
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
     * 检查附近可交互的实体(插件一般不使用)
     * <p>
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
     * 返回玩家目前正在看的实体。
     * <p>
     * Returns the Entity the player is looking at currently
     *
     * @param maxDistance 检查实体的最大距离<br>the maximum distance to check for entities
     * @return Entity|null    如果没有找到实体，则为NULL，或者是实体的一个实例。<br>either NULL if no entity is found or an instance of the entity
     */
    public EntityInteractable getEntityPlayerLookingAt(int maxDistance) {
        EntityInteractable entity = null;

        // just a fix because player MAY not be fully initialized
        if (temporalVector != null) {
            Entity[] nearbyEntities = level.getNearbyEntities(boundingBox.grow(maxDistance, maxDistance, maxDistance), this);

            // get all blocks in looking direction until the max interact distance is reached (it's possible that startblock isn't found!)
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

        if (!this.loadQueue.isEmpty() || !this.spawned) {
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
        double dot = dV.dot(new Vector2(this.x, this.z));
        double dot1 = dV.dot(new Vector2(pos.x, pos.z));
        return (dot1 - dot) >= -maxDiff;
    }

    @PowerNukkitXDifference(since = "1.19.70-r1", info = "Use new packet id system.")
    public void handleDataPacket(DataPacket packet) {
        if (!connected) {
            return;
        }

        if (!verified && packet.packetId() != ProtocolInfo.toNewProtocolID(ProtocolInfo.LOGIN_PACKET)
                && packet.packetId() != ProtocolInfo.toNewProtocolID(ProtocolInfo.BATCH_PACKET)
                && packet.packetId() != ProtocolInfo.toNewProtocolID(ProtocolInfo.REQUEST_NETWORK_SETTINGS_PACKET)) {
            log.warn("Ignoring {} from {} due to player not verified yet", packet.getClass().getSimpleName(), getAddress());
            if (unverifiedPackets++ > 100) {
                this.close("", "Too many failed login attempts");
            }
            return;
        }


        DataPacketReceiveEvent ev = new DataPacketReceiveEvent(this, packet);
        this.server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
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
     * 以该玩家的身份发送一条聊天信息。如果消息以/（正斜杠）开头，它将被视为一个命令。
     * <p>
     * Sends a chat message as this player. If the message begins with a / (forward-slash) it will be treated
     * as a command.
     *
     * @param message 发送的信息<br>message to send
     * @return successful
     */
    public boolean chat(String message) {
        if (!this.spawned || !this.isAlive()) {
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
                this.server.getPluginManager().callEvent(chatEvent);
                if (!chatEvent.isCancelled()) {
                    this.server.broadcastMessage(this.getServer().getLanguage().tr(chatEvent.getFormat(), new String[]{chatEvent.getPlayer().getDisplayName(), chatEvent.getMessage()}), chatEvent.getRecipients());
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
     * 踢出该玩家
     * <p>
     * Kick out the player
     *
     * @param reason       原因枚举<br>Cause Enumeration
     * @param reasonString 原因字符串<br>Reason String
     * @param isAdmin      是否来自管理员踢出<br>Whether from the administrator kicked out
     * @return boolean
     */
    public boolean kick(PlayerKickEvent.Reason reason, String reasonString, boolean isAdmin) {
        PlayerKickEvent ev;
        this.server.getPluginManager().callEvent(ev = new PlayerKickEvent(this, reason, this.getLeaveMessage()));
        if (!ev.isCancelled()) {
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

            this.close(ev.getQuitMessage(), message);

            return true;
        }

        return false;
    }

    /**
     * 设置玩家的可视距离(范围 0--{@link Server#getViewDistance})
     * <p>
     * Set the player's viewing distance (range 0--{@link Server#getViewDistance})
     *
     * @param distance 可视距离
     */
    public void setViewDistance(int distance) {
        this.chunkRadius = distance;

        ChunkRadiusUpdatedPacket pk = new ChunkRadiusUpdatedPacket();
        pk.radius = distance;

        this.dataPacket(pk);
    }

    /**
     * 得到玩家的可视距离
     * <p>
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

    @PowerNukkitXOnly
    @Since("1.19.60-r1")
    public void sendCommandOutput(CommandOutputContainer container) {
        if (this.level.getGameRules().getBoolean(GameRule.SEND_COMMAND_FEEDBACK)) {
            var pk = new CommandOutputPacket();
            pk.messages.addAll(container.getMessages());
            pk.commandOriginData = new CommandOriginData(CommandOriginData.Origin.PLAYER, this.getUniqueId(), "", null);//Only players can effect
            pk.type = CommandOutputType.ALL_OUTPUT;//Useless
            pk.successCount = container.getSuccessCount();//Useless,maybe used for server-client interaction
            this.dataPacket(pk);
        }
    }

    /**
     * 在玩家聊天栏发送一个JSON文本
     * <p>
     * Send a JSON text in the player chat bar
     *
     * @param text JSON文本<br>Json text
     */
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public void sendRawTextMessage(RawText text) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_OBJECT;
        pk.message = text.toRawText();
        this.dataPacket(pk);
    }

    /**
     * @see #sendTranslation(String, String[])
     */
    public void sendTranslation(String message) {
        this.sendTranslation(message, EmptyArrays.EMPTY_STRINGS);
    }

    /**
     * 在玩家聊天栏发送一个多语言翻译文本，示例:<br>{@code message="Test Message {%0} {%1}" parameters=["Hello","World"]}<br>
     * 实际消息内容{@code "Test Message Hello World"}
     * <p>
     * Send a multilingual translated text in the player chat bar, example:<br> {@code message="Test Message {%0} {%1}" parameters=["Hello", "World"]}<br>
     * actual message content {@code "Test Message Hello World"}
     *
     * @param message    消息
     * @param parameters 参数
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
        this.dataPacket(pk);
    }

    /**
     * @see #sendChat(String, String)
     */
    public void sendChat(String message) {
        this.sendChat("", message);
    }

    /**
     * 在玩家聊天栏发送一个文本
     * <p>
     * Send a text in the player chat bar
     *
     * @param message 消息
     */
    public void sendChat(String source, String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_CHAT;
        pk.source = source;
        pk.message = this.server.getLanguage().tr(message);
        this.dataPacket(pk);
    }

    /**
     * @see #sendPopup(String, String)
     */
    public void sendPopup(String message) {
        this.sendPopup(message, "");
    }

    /**
     * 在玩家物品栏上方发送一个弹出式的文本
     * <p>
     * Send a pop-up text above the player's item bar
     *
     * @param message 消息
     */
    // TODO: Support Translation Parameters
    public void sendPopup(String message, String subtitle) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_POPUP;
        pk.message = message;
        this.dataPacket(pk);
    }

    /**
     * 在玩家物品栏上方发送一个提示文本
     * <p>
     * Send a tip text above the player's item bar
     *
     * @param message 消息
     */
    public void sendTip(String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_TIP;
        pk.message = message;
        this.dataPacket(pk);
    }

    /**
     * 清除掉玩家身上正在显示的标题信息。
     * <p>
     * Clears away the title info being displayed on the player.
     */
    public void clearTitle() {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_CLEAR;
        this.dataPacket(pk);
    }

    /**
     * 为下一个显示的标题重新设置标题动画时间和副标题。
     * <p>
     * Resets both title animation times and subtitle for the next shown title.
     */
    public void resetTitleSettings() {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_RESET;
        this.dataPacket(pk);
    }

    /**
     * 设置副标题，在主标题下方显示。
     * <p>
     * Set subtitle to be displayed below the main title.
     *
     * @param subtitle 副标题
     */
    public void setSubtitle(String subtitle) {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_SUBTITLE;
        pk.text = subtitle;
        this.dataPacket(pk);
    }

    /**
     * 设置一个JSON文本副标题。
     * <p>
     * Set a JSON text subtitle.
     *
     * @param text JSON文本<br>JSON text
     */
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public void setRawTextSubTitle(RawText text) {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_SUBTITLE_JSON;
        pk.text = text.toRawText();
        this.dataPacket(pk);
    }

    /**
     * 设置标题动画时间
     * <p>
     * Set title animation time
     *
     * @param fadein   淡入时间
     * @param duration 持续时间
     * @param fadeout  淡出时间
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
     * 设置一个JSON文本标题。
     * <p>
     * Set a JSON text title.
     *
     * @param text JSON文本<br>JSON text
     */
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public void setRawTextTitle(RawText text) {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_TITLE_JSON;
        pk.text = text.toRawText();
        this.dataPacket(pk);
    }


    /**
     * {@code subtitle=null,fadeIn=20,stay=20,fadeOut=5}
     *
     * @see #sendTitle(String, String, int, int, int)
     */
    public void sendTitle(String title) {
        this.sendTitle(title, null, 20, 20, 5);
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
     * 在玩家视角正中央发送一个标题文本。
     * <p>
     * Send a title text in the center of the player's view.
     *
     * @param title    标题
     * @param subtitle 副标题
     * @param fadeIn   淡入时间<br>fadeIn time(tick)
     * @param stay     持续时间<br>stay time
     * @param fadeOut  淡出时间<br>fadeOut time
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
     * 在玩家物品栏上方发送一个ActionBar消息。
     * <p>
     * Send a ActionBar text above the player's item bar.
     *
     * @param title    消息
     * @param fadein   淡入时间
     * @param duration 持续时间
     * @param fadeout  淡出时间
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
     * fadein=1,duration=0,fadeout=1
     *
     * @see #setRawTextActionBar(RawText, int, int, int)
     */
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public void setRawTextActionBar(RawText text) {
        this.setRawTextActionBar(text, 1, 0, 1);
    }

    /**
     * 设置一个JSON ActionBar消息。
     * <p>
     * Set a JSON ActionBar text.
     *
     * @param text     JSON文本<br>JSON text
     * @param fadein   淡入时间
     * @param duration 持续时间
     * @param fadeout  淡出时间
     */
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
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
     * 关闭该玩家的连接及其一切活动，和{@link #kick}差不多效果，区别在于{@link #kick}是基于{@code close}实现的。
     * <p>
     * Closing the player's connection and all its activities is almost as function as {@link #kick}, the difference is that {@link #kick} is implemented based on {@code close}.
     *
     * @param message PlayerQuitEvent事件消息<br>PlayerQuitEvent message
     * @param reason  登出原因<br>Reason for logout
     * @param notify  是否显示登出画面通知<br>Whether to display the logout screen notification
     */
    public void close(TextContainer message, String reason, boolean notify) {
        if (this.connected && !this.closed) {
            //这里必须在玩家离线之前调用，否则无法将包发过去
            var scoreboardManager = this.getServer().getScoreboardManager();
            //in test environment sometimes the scoreboard manager is null
            if (scoreboardManager != null) {
                scoreboardManager.beforePlayerQuit(this);
            }

            if (notify && reason.length() > 0) {
                DisconnectPacket pk = new DisconnectPacket();
                pk.message = reason;
                this.dataPacketImmediately(pk);
            }

            this.connected = false;
            PlayerQuitEvent ev = null;
            if (this.getName() != null && this.getName().length() > 0) {
                this.server.getPluginManager().callEvent(ev = new PlayerQuitEvent(this, message, true, reason));
                if (this.fishing != null) {
                    this.stopFishing(false);
                }
            }

            // Close the temporary windows first, so they have chance to change all inventories before being disposed
            this.removeAllWindows(false);
            resetCraftingGridType();

            if (ev != null && this.loggedIn && ev.getAutoSave()) {
                this.save();
            }

            for (Player player : new ArrayList<>(this.server.getOnlinePlayers().values())) {
                if (!player.canSee(this)) {
                    player.showPlayer(this);
                }
            }

            this.hiddenPlayers.clear();

            this.removeAllWindows(true);

            for (long index : new ArrayList<>(this.usedChunks.keySet())) {
                int chunkX = Level.getHashX(index);
                int chunkZ = Level.getHashZ(index);
                this.level.unregisterChunkLoader(this, chunkX, chunkZ);
                this.usedChunks.remove(index);

                for (Entity entity : level.getChunkEntities(chunkX, chunkZ).values()) {
                    if (entity != this) {
                        entity.getViewers().remove(getLoaderId());
                    }
                }
            }

            super.close();

            this.interfaz.close(this, notify ? reason : "");

            if (this.loggedIn) {
                this.server.removeOnlinePlayer(this);
            }

            this.loggedIn = false;

            if (ev != null && !Objects.equals(this.username, "") && this.spawned && !Objects.equals(ev.getQuitMessage().toString(), "")) {
                this.server.broadcastMessage(ev.getQuitMessage());
            }

            this.server.getPluginManager().unsubscribeFromPermission(Server.BROADCAST_CHANNEL_USERS, this);
            this.spawned = false;
            log.info(this.getServer().getLanguage().tr("nukkit.player.logOut",
                    TextFormat.AQUA + (this.getName() == null ? "" : this.getName()) + TextFormat.WHITE,
                    this.getAddress(),
                    String.valueOf(this.getPort()),
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

        this.server.removePlayer(this);
    }

    public void save() {
        this.save(false);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        if (spawnBlockPosition == null) {
            namedTag.remove("SpawnBlockPositionX").remove("SpawnBlockPositionY").remove("SpawnBlockPositionZ").remove("SpawnBlockLevel");
        } else {
            namedTag.putInt("SpawnBlockPositionX", spawnBlockPosition.getFloorX())
                    .putInt("SpawnBlockPositionY", spawnBlockPosition.getFloorY())
                    .putInt("SpawnBlockPositionZ", spawnBlockPosition.getFloorZ())
                    .putString("SpawnBlockLevel", this.spawnBlockPosition.getLevel().getFolderName());
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
                this.namedTag.putString("SpawnLevel", this.spawnPosition.getLevel().getFolderName());
                this.namedTag.putInt("SpawnDimension", this.spawnPosition.getLevel().getDimension());
            } else {
                this.namedTag.putString("SpawnLevel", this.server.getDefaultLevel().getFolderName());
                this.namedTag.putInt("SpawnDimension", this.server.getDefaultLevel().getDimension());
            }
        }

        this.adventureSettings.saveNBT();
    }

    public void save(boolean async) {
        if (this.closed) {
            throw new IllegalStateException("Tried to save closed player");
        }

        saveNBT();

        if (this.level != null) {
            this.namedTag.putString("Level", this.level.getFolderName());

            CompoundTag achievements = new CompoundTag();
            for (String achievement : this.achievements) {
                achievements.putByte(achievement, 1);
            }

            this.namedTag.putCompound("Achievements", achievements);

            this.namedTag.putInt("playerGameType", this.gamemode);
            this.namedTag.putLong("lastPlayed", System.currentTimeMillis() / 1000);

            this.namedTag.putString("lastIP", this.getAddress());

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

            if (!this.username.isEmpty() && this.namedTag != null) {
                this.server.saveOfflinePlayerData(this.uuid, this.namedTag, async);
            }
        }
    }

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return "Player";
    }

    @NotNull
    @Override
    public String getName() {
        return this.username;
    }

    @PowerNukkitXOnly
    @Since("1.19.60-r1")
    public LangCode getLanguageCode() {
        return LangCode.valueOf(this.getLoginChainData().getLanguageCode());
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

            RespawnPacket pk = new RespawnPacket();
            Position pos = this.getSpawn();
            pk.x = (float) pos.x;
            pk.y = (float) pos.y;
            pk.z = (float) pos.z;
            pk.respawnState = RespawnPacket.STATE_SEARCHING_FOR_SPAWN;

            this.dataPacket(pk);
        }
    }

    @Override
    public void setHealth(float health) {
        if (health < 1) {
            health = 0;
        }
        super.setHealth(health);
        //TODO: Remove it in future! This a hack to solve the client-side absorption bug! WFT Mojang (Half a yellow heart cannot be shown, we can test it in local gaming)
        Attribute attr = Attribute.getAttribute(Attribute.MAX_HEALTH).setMaxValue(this.getAbsorption() % 2 != 0 ? this.getMaxHealth() + 1 : this.getMaxHealth()).setValue(health > 0 ? (health < getMaxHealth() ? health : getMaxHealth()) : 0);
        if (this.spawned) {
            UpdateAttributesPacket pk = new UpdateAttributesPacket();
            pk.entries = new Attribute[]{attr};
            pk.entityId = this.id;
            this.dataPacket(pk);
        }
    }

    @Override
    public void setMaxHealth(int maxHealth) {
        super.setMaxHealth(maxHealth);

        Attribute attr = Attribute.getAttribute(Attribute.MAX_HEALTH).setMaxValue(this.getAbsorption() % 2 != 0 ? this.getMaxHealth() + 1 : this.getMaxHealth()).setValue(health > 0 ? (health < getMaxHealth() ? health : getMaxHealth()) : 0);
        if (this.spawned) {
            UpdateAttributesPacket pk = new UpdateAttributesPacket();
            pk.entries = new Attribute[]{attr};
            pk.entityId = this.id;
            this.dataPacket(pk);
        }
    }

    /**
     * 得到该玩家的经验值(并不会显示玩家从的经验值总数，而仅仅显示当前等级所在的经验值，即经验条)。
     * <p>
     * Get the experience value of the player (it does not show the total experience value of the player from, but only the experience value where the current level is, i.e. the experience bar).
     *
     * @return int
     */
    public int getExperience() {
        return this.exp;
    }

    /**
     * 得到该玩家的等级。
     * <p>
     * Get the level of the player.
     *
     * @return int
     */
    public int getExperienceLevel() {
        return this.expLevel;
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
     * 增加该玩家的经验值
     * <p>
     * Increase the experience value of the player
     *
     * @param add              经验值的数量
     * @param playLevelUpSound 有无升级声音
     */
    @PowerNukkitOnly
    public void addExperience(int add, boolean playLevelUpSound) {
        if (add == 0) return;
        int now = this.getExperience();
        int added = now + add;
        int level = this.getExperienceLevel();
        int most = calculateRequireExperience(level);
        while (added >= most) {  //Level Up!
            added = added - most;
            level++;
            most = calculateRequireExperience(level);
        }
        this.setExperience(added, level, playLevelUpSound);
    }

    /**
     * 计算玩家到达某等级所需要的经验值
     * <p>
     * Calculates the amount of experience a player needs to reach a certain level
     *
     * @param level 等级
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
     * 设置该玩家的经验值和等级
     * <p>
     * set the experience value and level of the player
     *
     * @param playLevelUpSound 有无升级声音
     * @param exp              经验值
     * @param level            等级
     */
    //todo something on performance, lots of exp orbs then lots of packets, could crash client
    @PowerNukkitOnly
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
     * @see #sendExperience(int)
     */
    public void sendExperience() {
        sendExperience(this.getExperience());
    }

    /**
     * setExperience的实现部分，用来设置当前等级所对应的经验值，即经验条
     * <p>
     * The implementation of setExperience is used to set the experience value corresponding to the current level, i.e. the experience bar
     *
     * @param exp 经验值
     */
    public void sendExperience(int exp) {
        if (this.spawned) {
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
     * setExperience的实现部分，用来设置当前等级
     * <p>
     * The implementation of setExperience is used to set the level
     *
     * @param level 等级
     */
    public void sendExperienceLevel(int level) {
        if (this.spawned) {
            this.setAttribute(Attribute.getAttribute(Attribute.EXPERIENCE_LEVEL).setValue(level));
        }
    }

    /**
     * 以指定{@link Attribute}发送UpdateAttributesPacket数据包到该玩家。
     * <p>
     * Send UpdateAttributesPacket packets to this player with the specified {@link Attribute}.
     *
     * @param attribute the attribute
     */
    public void setAttribute(Attribute attribute) {
        UpdateAttributesPacket pk = new UpdateAttributesPacket();
        pk.entries = new Attribute[]{attribute};
        pk.entityId = this.id;
        this.dataPacket(pk);
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
     * 设置该玩家的移动速度
     * <p>
     * Set the movement speed of this player.
     *
     * @param speed 速度大小，注意默认移动速度为{@link #DEFAULT_SPEED}<br>Speed value, note that the default movement speed is {@link #DEFAULT_SPEED}
     * @param send  是否发送数据包{@link UpdateAttributesPacket}到客户端<br>Whether to send {@link UpdateAttributesPacket} to the client
     */
    public void setMovementSpeed(float speed, boolean send) {
        super.setMovementSpeed(speed);
        if (this.spawned && send) {
            this.sendMovementSpeed(speed);
        }
    }


    /**
     * 发送{@link Attribute#MOVEMENT_SPEED}属性到客户端
     * <p>
     * Send {@link Attribute#MOVEMENT_SPEED} Attribute to Client.
     *
     * @param speed 属性值<br>the speed value
     */
    @Since("1.4.0.0-PN")
    public void sendMovementSpeed(float speed) {
        Attribute attribute = Attribute.getAttribute(Attribute.MOVEMENT_SPEED).setValue(speed);
        this.setAttribute(attribute);
    }

    /**
     * 获取击杀该玩家的实体
     * <p>
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
            //source.setCancelled();
            return false;
        } else if (this.getAdventureSettings().get(Type.ALLOW_FLIGHT) && source.getCause() == DamageCause.FALL) {
            //source.setCancelled();
            return false;
        } else if (source.getCause() == DamageCause.FALL) {
            if (this.getLevel().getBlock(this.getPosition().floor().add(0.5, -1, 0.5)).getId() == Block.SLIME_BLOCK) {
                if (!this.isSneaking()) {
                    //source.setCancelled();
                    this.resetFallDistance();
                    return false;
                }
            }
        }

        if (super.attack(source)) { //!source.isCancelled()
            if (this.getLastDamageCause() == source && this.spawned) {
                if (source instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) {
                    Entity damager = entityDamageByEntityEvent.getDamager();
                    if (damager instanceof Player) {
                        ((Player) damager).getFoodData().updateFoodExpLevel(0.1);
                    }
                    //保存攻击玩家的实体在lastBeAttackEntity
                    this.lastBeAttackEntity = entityDamageByEntityEvent.getDamager();
                }
                EntityEventPacket pk = new EntityEventPacket();
                pk.eid = this.id;
                pk.event = EntityEventPacket.HURT_ANIMATION;
                this.dataPacket(pk);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 在玩家面前的地面上掉落一个物品。如果物品投放成功，则返回。
     * <p>
     * Drops an item on the ground in front of the player. Returns if the item drop was successful.
     *
     * @param item 掉落的物品<br>to drop
     * @return 一个bool值，丢弃物品成功或该物品为空<br>bool if the item was dropped or if the item was null
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

        this.setDataFlag(DATA_FLAGS, DATA_FLAG_ACTION, false);
        return true;
    }

    /**
     * 在玩家面前的地面上扔下一个物品。返回值为该掉落的物品。
     * <p>
     * Drops an item on the ground in front of the player. Returns the dropped item.
     *
     * @param item 掉落的物品<br>to drop
     * @return 如果物品被丢弃成功，则返回EntityItem；如果物品为空，则为null<br>EntityItem if the item was dropped or null if the item was null
     */
    @Since("1.4.0.0-PN")
    @Nullable
    public EntityItem dropAndGetItem(@NotNull Item item) {
        if (!this.spawned || !this.isAlive()) {
            return null;
        }

        if (item.isNull()) {
            log.debug("{} attempted to drop a null item ({})", this.getName(), item);
            return null;
        }

        Vector3 motion = this.getDirectionVector().multiply(0.4);

        this.setDataFlag(DATA_FLAGS, DATA_FLAG_ACTION, false);

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
     * {@link Player#addMovement}的实现,仅发送{@link MovePlayerPacket}数据包到客户端
     *
     * @param pos     the pos of MovePlayerPacket
     * @param yaw     the yaw of MovePlayerPacket
     * @param pitch   the pitch of MovePlayerPacket
     * @param mode    the mode of MovePlayerPacket
     * @param targets 接受数据包的玩家们<br>players of receive the packet
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
        Location to = location;
        //event
        if (cause != null) {
            PlayerTeleportEvent event = new PlayerTeleportEvent(this, from, to, cause);
            this.server.getPluginManager().callEvent(event);
            if (event.isCancelled()) return false;
            to = event.getTo();
        }
        //remove inventory
        for (Inventory window : new ArrayList<>(this.windows.keySet())) {
            if (window == this.inventory) {
                continue;
            }
            this.removeWindow(window);
        }
        //remove ride
        final Entity currentRide = getRiding();
        if (currentRide != null && !currentRide.dismountEntity(this)) {
            return false;
        }
        this.setMotion(this.temporalVector.setComponents(0, 0, 0));
        //switch level, update pos and rotation, update aabb
        if (setPositionAndRotation(to, to.getYaw(), to.getPitch(), to.getHeadYaw())) {
            this.resetFallDistance();
            this.onGround = !this.noClip;
            //send to client
            this.sendPosition(to, to.yaw, to.pitch, MovePlayerPacket.MODE_TELEPORT);
            this.newPosition = to;
        } else {
            this.sendPosition(this, to.yaw, to.pitch, MovePlayerPacket.MODE_TELEPORT);
            this.newPosition = this;
        }
        //state update
        this.positionChanged = true;
        this.nextChunkOrderRun = 0;
        //DummyBossBar
        this.getDummyBossBars().values().forEach(DummyBossBar::reshow);
        //Weather
        this.getLevel().sendWeather(this);
        //Update time
        this.getLevel().sendTime(this);
        updateTrackingPositions(true);
        //Update gamemode
        if (isSpectator()) {
            this.setGamemode(this.gamemode, false, null, true);
        }
        return true;
    }

    @Deprecated
    @DeprecationDetails(since = "1.19.60-r1", reason = "same teleport")
    public void teleportImmediate(Location location) {
        this.teleportImmediate(location, TeleportCause.PLUGIN);
    }

    @Deprecated
    @DeprecationDetails(since = "1.19.60-r1", reason = "same teleport")
    public void teleportImmediate(Location location, TeleportCause cause) {
        this.teleport(location, cause);
    }

    /**
     * Automatic id assignment
     *
     * @see #showFormWindow(FormWindow, int)
     */
    public int showFormWindow(FormWindow window) {
        return showFormWindow(window, this.formWindowCount++);
    }

    /**
     * 向玩家显示一个新的FormWindow。
     * 你可以通过监听PlayerFormRespondedEvent来了解FormWindow的结果。
     * <p>
     * Shows a new FormWindow to the player
     * You can find out FormWindow result by listening to PlayerFormRespondedEvent
     *
     * @param window to show
     * @param id     form id
     * @return form id to use in {@link PlayerFormRespondedEvent}
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
     * book=true
     *
     * @see #showDialogWindow(FormWindowDialog, boolean)
     */
    public void showDialogWindow(FormWindowDialog dialog) {
        showDialogWindow(dialog, true);
    }

    /**
     * 向玩家展示一个NPC对话框.
     * <p>
     * Show dialog window to the player.
     *
     * @param dialog NPC对话框<br>the dialog
     * @param book   如果为true,将会立即更新该{@link FormWindowDialog#getSceneName()}<br>If true, the {@link FormWindowDialog#getSceneName()} will be updated immediately.
     */
    public void showDialogWindow(FormWindowDialog dialog, boolean book) {
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
        this.dataPacket(packet);
    }

    /**
     * 在游戏设置中显示一个新的设置页面。
     * 你可以通过监听PlayerFormRespondedEvent来了解设置结果。
     * <p>
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
     * 创建并发送一个BossBar给玩家。
     * <p>
     * Creates and sends a BossBar to the player
     *
     * @param text   BossBar信息<br>The BossBar message
     * @param length BossBar百分比<br>The BossBar percentage
     * @return bossBarId BossBar的ID，如果你想以后删除或更新BossBar，你应该存储它。<br>bossBarId The BossBar ID, you should store it if you want to remove or update the BossBar later
     */
    @Deprecated
    public long createBossBar(String text, int length) {
        DummyBossBar bossBar = new DummyBossBar.Builder(this).text(text).length(length).build();
        return this.createBossBar(bossBar);
    }

    /**
     * 创建并发送一个BossBar给玩家。
     * <p>
     * Creates and sends a BossBar to the player
     *
     * @param dummyBossBar DummyBossBar对象（通过{@link DummyBossBar.Builder}实例化）。<br>DummyBossBar Object (Instantiate it by the Class Builder)
     * @return bossBarId BossBar的ID，如果你想以后删除或更新BossBar，你应该储存它。<br>bossBarId  The BossBar ID, you should store it if you want to remove or update the BossBar later
     * @see DummyBossBar.Builder
     */
    public long createBossBar(DummyBossBar dummyBossBar) {
        this.dummyBossBars.put(dummyBossBar.getBossBarId(), dummyBossBar);
        dummyBossBar.create();
        return dummyBossBar.getBossBarId();
    }

    /**
     * 获取一个DummyBossBar对象
     * <p>
     * Get a DummyBossBar object
     *
     * @param bossBarId 要查找的BossBar ID<br>The BossBar ID
     * @return DummyBossBar对象<br>DummyBossBar object
     * @see DummyBossBar#setText(String) Set BossBar text
     * @see DummyBossBar#setLength(float) Set BossBar length
     * @see DummyBossBar#setColor(BossBarColor) Set BossBar color
     */
    public DummyBossBar getDummyBossBar(long bossBarId) {
        return this.dummyBossBars.getOrDefault(bossBarId, null);
    }

    /**
     * 获取所有DummyBossBar对象
     * <p>
     * Get all DummyBossBar objects
     *
     * @return DummyBossBars Map
     */
    public Map<Long, DummyBossBar> getDummyBossBars() {
        return dummyBossBars;
    }

    /**
     * 更新一个BossBar
     * <p>
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
     * 移除一个BossBar
     * <p>
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
     * 获取id从指定{@link Inventory}
     * <p>
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
     * 获取{@link Inventory}从指定id
     * <p>
     * Get {@link Inventory} from the specified id
     *
     * @param id 窗口id<br>the window id
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
        return this.addWindow(inventory, null);
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
     * 添加一个{@link Inventory}窗口显示到该玩家
     *
     * @param inventory   这个库存窗口<br>the inventory
     * @param forceId     强制指定window id,若和现有window重复将会删除它并替换,为null则自动分配<br>Force the window id to be specified, if it is duplicated with an existing window, it will be deleted and replaced,if is null is automatically assigned.
     * @param isPermanent 如果为true将会把Inventory存放到{@link #permanentWindows}<br>If true it will store the Inventory in {@link #permanentWindows}
     * @param alwaysOpen  如果为true即使玩家未{@link #spawned}也会添加改玩家为指定inventory的viewer<br>If true, even if the player is not {@link #spawned}, it will add the player as viewer to the specified inventory.
     * @return 返回窗口id，可以利用id通过{@link #windowIndex}重新获取该Inventory<br>Return the window id, you can use the id to retrieve the Inventory via {@link #windowIndex}
     */
    @Since("1.4.0.0-PN")
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

        if (this.spawned && inventory.open(this)) {
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
     * 移除该玩家身上的指定Inventory
     * <p>
     * Remove the specified Inventory from the player
     *
     * @param inventory the inventory
     */
    public void removeWindow(Inventory inventory) {
        this.removeWindow(inventory, false);
    }

    /**
     * 常用于刷新。
     * <p>
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
     * 获取该玩家的{@link PlayerUIInventory}
     * <p>
     * Gets ui inventory of the player.
     */
    public PlayerUIInventory getUIInventory() {
        return playerUIInventory;
    }

    /**
     * 获取该玩家的{@link PlayerCursorInventory}
     * <p>
     * Gets cursor inventory of the player.
     */
    public PlayerCursorInventory getCursorInventory() {
        return this.playerUIInventory.getCursorInventory();
    }

    /**
     * 获取该玩家的{@link CraftingGrid}
     * <p>
     * Gets crafting grid of the player.
     */
    public CraftingGrid getCraftingGrid() {
        return this.craftingGrid;
    }

    /**
     * 设置该玩家的{@link CraftingGrid}
     * <p>
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
            Item[] drops = this.inventory.addItem(this.craftingGrid.getContents().values().toArray(Item.EMPTY_ARRAY));

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

            this.playerUIInventory.clearAll();

            if (this.craftingGrid instanceof BigCraftingGrid) {
                this.craftingGrid = this.playerUIInventory.getCraftingGrid();
                this.addWindow(this.craftingGrid, ContainerIds.NONE);
//
//                ContainerClosePacket pk = new ContainerClosePacket(); //be sure, big crafting is really closed
//                pk.windowId = ContainerIds.NONE;
//                this.dataPacket(pk);
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
     * 清空{@link #windows}
     * <p>
     * Remove all windows.
     *
     * @param permanent 如果为false则会跳过删除{@link #permanentWindows}里面对应的window<br>If false, it will skip deleting the corresponding window in {@link #permanentWindows}
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
     * 获取上一个关闭窗口对应的id
     * <p>
     * Get the id corresponding to the last closed window
     */
    @Since("1.4.0.0-PN")
    public int getClosingWindowId() {
        return this.closingWindowId;
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
    public void onChunkLoaded(FullChunk chunk) {

    }

    @Override
    public void onChunkPopulated(FullChunk chunk) {

    }

    @Override
    public void onChunkUnloaded(FullChunk chunk) {

    }

    @Override
    public void onBlockChanged(Vector3 block) {

    }

    @Override
    public int getLoaderId() {
        return this.loaderId;
    }

    @Override
    public boolean isLoaderActive() {
        return this.isConnected();
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
     * @return 该玩家是否开启饮食系统<br>Whether the player is on the food system
     */
    public boolean isFoodEnabled() {
        return !(this.isCreative() || this.isSpectator()) && this.foodEnabled;
    }

    /**
     * 设置该玩家是否开启饮食系统
     * <p>
     * Set whether the player is on the food system
     *
     * @param foodEnabled 如果为false,则关闭玩家的饮食系统<br>If false, turn off the player's food system
     */
    public void setFoodEnabled(boolean foodEnabled) {
        this.foodEnabled = foodEnabled;
    }

    /**
     * 获取玩家的{@link PlayerFood}
     * <p>
     * Get the player's {@link PlayerFood}
     *
     * @return the food data
     */
    public PlayerFood getFoodData() {
        return this.foodData;
    }

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
            for (long index : new ArrayList<>(this.usedChunks.keySet())) {
                int chunkX = Level.getHashX(index);
                int chunkZ = Level.getHashZ(index);
                this.unloadChunk(chunkX, chunkZ, oldLevel);
            }
            this.usedChunks.clear();

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
     * 设置是否检查该玩家移动
     * <p>
     * Set whether to check for this player movement
     *
     * @param checkMovement the check movement
     */
    public void setCheckMovement(boolean checkMovement) {
        this.checkMovement = checkMovement;
    }

    /**
     * @since 1.2.1.0-PN
     */
    public boolean isCheckingMovement() {
        return this.checkMovement;
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
     * 传送该玩家到另一个服务器
     * <p>
     * Teleport the player to another server
     *
     * @param address the address
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
     * 获取该玩家的登录链数据
     * <p>
     * Get the login chain data of this player
     *
     * @return the login chain data
     */
    public LoginChainData getLoginChainData() {
        return this.loginChainData;
    }

    public boolean pickupEntity(Entity entity, boolean near) {
        if (!this.spawned || !this.isAlive() || !this.isOnline() || this.isSpectator() || entity.isClosed()) {
            return false;
        }

        if (near) {
            Inventory inventory = this.inventory;
            if (entity instanceof EntityArrow && ((EntityArrow) entity).hadCollision) {
                ItemArrow item = new ItemArrow();
                if (!this.isCreative()) {
                    // Should only collect to the offhand slot if the item matches what is already there
                    if (this.offhandInventory.getItem(0).getId() == item.getId() && this.offhandInventory.canAddItem(item)) {
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
                    if (inventory.getItem(((EntityThrownTrident) entity).getFavoredSlot()).getId() == Item.AIR) {
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

                        InventoryPickupItemEvent ev;
                        this.server.getPluginManager().callEvent(ev = new InventoryPickupItemEvent(inventory, (EntityItem) entity));
                        if (ev.isCancelled()) {
                            return false;
                        }

                        switch (item.getId()) {
                            case Item.WOOD:
                            case Item.WOOD2:
                                this.awardAchievement("mineWood");
                                break;
                            case Item.DIAMOND:
                                this.awardAchievement("diamond");
                                break;
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
        if (pickedXPOrb < tick && entity instanceof EntityXPOrb xpOrb && this.boundingBox.isVectorInside(entity)) {
            if (xpOrb.getPickupDelay() <= 0) {
                int exp = xpOrb.getExp();
                entity.kill();
                this.getLevel().addLevelEvent(LevelEventPacket.EVENT_SOUND_EXPERIENCE_ORB, 0, this);
                pickedXPOrb = tick;

                //Mending
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
     * 玩家是否在挖掘方块
     * <p>
     * Whether the player is digging block
     *
     * @return the boolean
     */
    public boolean isBreakingBlock() {
        return this.breakingBlock != null;
    }

    /**
     * 显示一个XBOX账户的资料窗口
     * <p>
     * Show a window of a XBOX account's profile
     *
     * @param xuid XUID
     */
    public void showXboxProfile(String xuid) {
        ShowProfilePacket pk = new ShowProfilePacket();
        pk.xuid = xuid;
        this.dataPacket(pk);
    }

    /**
     * Start fishing
     *
     * @param fishingRod fishing rod item
     */
    public void startFishing(Item fishingRod) {
        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", x))
                        .add(new DoubleTag("", y + this.getEyeHeight()))
                        .add(new DoubleTag("", z)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", -Math.sin(yaw / 180 + Math.PI) * Math.cos(pitch / 180 * Math.PI)))
                        .add(new DoubleTag("", -Math.sin(pitch / 180 * Math.PI)))
                        .add(new DoubleTag("", Math.cos(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI))))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", (float) yaw))
                        .add(new FloatTag("", (float) pitch)));
        double f = 1.1;
        EntityFishingHook fishingHook = new EntityFishingHook(chunk, nbt, this);
        fishingHook.setMotion(new Vector3(-Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * f * f, -Math.sin(Math.toRadians(pitch)) * f * f,
                Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * f * f));
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
        return this.gamemode != SPECTATOR;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getNoShieldTicks() {
        return noShieldTicks;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setNoShieldTicks(int noShieldTicks) {
        this.noShieldTicks = noShieldTicks;
    }

    @Override
    public String toString() {
        return "Player(name='" + getName() +
                "', location=" + super.toString() +
                ')';
    }

    /**
     * 将物品添加到玩家的主要库存中，并将任何多余的物品丢在地上。
     * <p>
     * Adds the items to the main player inventory and drops on the floor any excess.
     *
     * @param items The items to give to the player.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void giveItem(Item... items) {
        for (Item failed : getInventory().addItem(items)) {
            getLevel().dropItem(this, failed);
        }
    }

    @Since("1.4.0.0-PN")
    public int getTimeSinceRest() {
        return timeSinceRest;
    }

    @Since("1.4.0.0-PN")
    public void setTimeSinceRest(int timeSinceRest) {
        this.timeSinceRest = timeSinceRest;
    }

    @Since("1.19.30-r1")
    @PowerNukkitXOnly
    public NetworkPlayerSession getNetworkSession() {
        return this.networkSession;
    }

    // TODO: Support Translation Parameters
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void sendPopupJukebox(String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_JUKEBOX_POPUP;
        pk.message = message;
        this.dataPacket(pk);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void sendSystem(String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_SYSTEM;
        pk.message = message;
        this.dataPacket(pk);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void sendWhisper(String message) {
        this.sendWhisper("", message);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void sendWhisper(String source, String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_WHISPER;
        pk.source = source;
        pk.message = message;
        this.dataPacket(pk);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void sendAnnouncement(String message) {
        this.sendAnnouncement("", message);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void sendAnnouncement(String source, String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_ANNOUNCEMENT;
        pk.source = source;
        pk.message = message;
        this.dataPacket(pk);
    }

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public void completeUsingItem(int itemId, int action) {
        CompletedUsingItemPacket pk = new CompletedUsingItemPacket();
        pk.itemId = itemId;
        pk.action = action;
        this.dataPacket(pk);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isShowingCredits() {
        return showingCredits;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setShowingCredits(boolean showingCredits) {
        this.showingCredits = showingCredits;
        if (showingCredits) {
            ShowCreditsPacket pk = new ShowCreditsPacket();
            pk.eid = this.getId();
            pk.status = ShowCreditsPacket.STATUS_START_CREDITS;
            this.dataPacket(pk);
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void showCredits() {
        this.setShowingCredits(true);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean hasSeenCredits() {
        return showingCredits;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setHasSeenCredits(boolean hasSeenCredits) {
        this.hasSeenCredits = hasSeenCredits;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean dataPacketImmediately(DataPacket packet) {
        if (!this.connected) {
            return false;
        }
        DataPacketSendEvent ev = new DataPacketSendEvent(this, packet);
        this.server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return false;
        }
        if (log.isTraceEnabled() && !server.isIgnoredPacket(packet.getClass())) {
            log.trace("Immediate Outbound {}: {}", this.getName(), packet);
        }
        this.getNetworkSession().sendImmediatePacket(packet);
        return true;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public boolean dataResourcePacket(DataPacket packet) {
        if (!this.connected) {
            return false;
        }
        DataPacketSendEvent ev = new DataPacketSendEvent(this, packet);
        this.server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return false;
        }
        if (log.isTraceEnabled() && !server.isIgnoredPacket(packet.getClass())) {
            log.trace("Resource Outbound {}: {}", this.getName(), packet);
        }
        this.interfaz.putResourcePacket(this, packet);
        return true;
    }

    /**
     * 玩家屏幕振动效果
     * <p>
     * Player screen shake effect
     *
     * @param intensity   the intensity
     * @param duration    the duration
     * @param shakeType   the shake type
     * @param shakeAction the shake action
     */
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public void shakeCamera(float intensity, float duration, CameraShakePacket.CameraShakeType shakeType, CameraShakePacket.CameraShakeAction shakeAction) {
        CameraShakePacket packet = new CameraShakePacket();
        packet.intensity = intensity;
        packet.duration = duration;
        packet.shakeType = shakeType;
        packet.shakeAction = shakeAction;
        this.dataPacket(packet);
    }

    /**
     * 设置指定itemCategory物品的冷却显示效果，注意该方法仅为客户端显示效果，冷却逻辑实现仍需自己实现
     * <p>
     * Set the cooling display effect of the specified itemCategory items, note that this method is only for client-side display effect, cooling logic implementation still needs to be implemented by itself
     *
     * @param coolDown     the cool down
     * @param itemCategory the item category
     */
    @PowerNukkitXOnly
    @Since("1.19.21-r4")
    public void setItemCoolDown(int coolDown, String itemCategory) {
        var pk = new PlayerStartItemCoolDownPacket();
        pk.setCoolDownDuration(coolDown);
        pk.setItemCategory(itemCategory);
        this.dataPacket(pk);
    }

    /**
     * 发送一个下弹消息框给玩家
     * <p>
     * Send a Toast message box to the player
     *
     * @param title   the title
     * @param content the content
     */
    public void sendToast(String title, String content) {
        ToastRequestPacket pk = new ToastRequestPacket();
        pk.title = title;
        pk.content = content;
        this.dataPacket(pk);
    }

    @PowerNukkitXOnly
    @Since("1.19.30-r1")
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

    @PowerNukkitXOnly
    @Since("1.19.30-r1")
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

    @PowerNukkitXOnly
    @Since("1.19.30-r1")
    @Override
    public void display(IScoreboard scoreboard, DisplaySlot slot) {
        SetDisplayObjectivePacket pk = new SetDisplayObjectivePacket();
        pk.displaySlot = slot;
        pk.objectiveName = scoreboard.getObjectiveName();
        pk.displayName = scoreboard.getDisplayName();
        pk.criteriaName = scoreboard.getCriteriaName();
        pk.sortOrder = scoreboard.getSortOrder();
        this.dataPacket(pk);

        //client won't storage the score of a scoreboard,so we should send the score to client
        SetScorePacket pk2 = new SetScorePacket();
        pk2.infos = scoreboard.getLines().values().stream().map(line -> line.toNetworkInfo()).filter(line -> line != null).collect(Collectors.toList());
        pk2.action = SetScorePacket.Action.SET;
        this.dataPacket(pk2);

        var scorer = new PlayerScorer(this);
        var line = scoreboard.getLine(scorer);
        if (slot == DisplaySlot.BELOW_NAME && line != null) {
            this.setScoreTag(line.getScore() + " " + scoreboard.getDisplayName());
        }
    }

    @PowerNukkitXOnly
    @Since("1.19.30-r1")
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

    @Override
    public void removeScoreboard(IScoreboard scoreboard) {
        RemoveObjectivePacket pk = new RemoveObjectivePacket();
        pk.objectiveName = scoreboard.getObjectiveName();

        this.dataPacket(pk);
    }

    @PowerNukkitXOnly
    @Since("1.20.0-r1")
    public Boolean isOpenSignFront() {
        return openSignFront;
    }

    @PowerNukkitXOnly
    @Since("1.20.0-r1")
    public void setOpenSignFront(Boolean frontSide) {
        openSignFront = frontSide;
    }

    /**
     * Opens the player's sign editor GUI for the sign at the given position.
     */
    @PowerNukkitXOnly
    @Since("1.20.0-r1")
    public void openSignEditor(Vector3 position, boolean frontSide) {
        if (openSignFront == null) {
            BlockEntity blockEntity = this.getLevel().getBlockEntity(position);
            if (blockEntity instanceof BlockEntitySign blockEntitySign) {
                if (blockEntitySign.getEditorEntityRuntimeId() == -1) {
                    blockEntitySign.setEditorEntityRuntimeId(this.getId());
                    OpenSignPacket openSignPacket = new OpenSignPacket();
                    openSignPacket.setPosition(position.asBlockVector3());
                    openSignPacket.setFrontSide(frontSide);
                    this.dataPacket(openSignPacket);
                    setOpenSignFront(frontSide);
                }
            } else {
                throw new IllegalArgumentException("Block at this position is not a sign");
            }
        }
    }
}
