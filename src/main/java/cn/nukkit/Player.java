package cn.nukkit;

import cn.nukkit.AdventureSettings.Type;
import cn.nukkit.api.UnintendedClientBehaviour;
import cn.nukkit.api.UsedByReflection;
import cn.nukkit.block.Block;
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
import cn.nukkit.dialog.window.FormWindowDialog;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.EntityInteractable;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.EntityRideable;
import cn.nukkit.entity.custom.CustomEntityComponents;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.data.PlayerFlag;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.entity.item.EntityFishingHook;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.item.EntityXpOrb;
import cn.nukkit.entity.mob.EntityBoss;
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
import cn.nukkit.form.window.Form;
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
import cn.nukkit.item.ItemBundle;
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
import cn.nukkit.network.protocol.types.*;
import cn.nukkit.permission.PermissibleBase;
import cn.nukkit.permission.Permission;
import cn.nukkit.permission.PermissionAttachment;
import cn.nukkit.permission.PermissionAttachmentInfo;
import cn.nukkit.plugin.InternalPlugin;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.positiontracking.PositionTrackingService;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.scheduler.ServerScheduler;
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
import cn.nukkit.utils.Utils;
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
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.awt.*;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 游戏玩家对象，代表操控的角色
 * <p>
 * Game player object, representing the controlled character
 *
 * @author MagicDroidX &amp; Box (Nukkit Project)
 */
@Slf4j
public class Player extends EntityHuman implements CommandSender, ChunkLoader, IPlayer, IScoreboardViewer {
    /// static fields
    /**
     * 一个承载玩家的空数组静态常量
     * <p>
     * An empty array of static constants that host the player
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
     * 是否移除改玩家聊天中的颜色字符如 §c §1
     * <p>
     * Whether to remove the color character in the chat of the changed player as §c §1
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
     * 代表玩家悬浮空中所经过的tick数.
     * <p>
     * Represents the number of ticks the player has passed through the air.
     */
    protected int inAirTicks = 0;
    protected int startAirTicks = 5;
    protected float horizontalFlySpeed = DEFAULT_FLY_SPEED;
    protected float verticalFlySpeed = 1F;
    protected AdventureSettings adventureSettings;
    protected boolean checkMovement = true;
    protected PlayerFood foodData = null;
    protected boolean enableClientCommand = true;
    protected int formWindowCount = 0;
    protected Map<Integer, Form<?>> formWindows = new Int2ObjectOpenHashMap<>();
    protected Map<Integer, Form<?>> serverSettings = new Int2ObjectOpenHashMap<>();
    /**
     * 我们使用google的cache来存储NPC对话框发送信息
     * 原因是发送过去的对话框客户端有几率不响应，在特定情况下我们无法清除这些对话框，这会导致内存泄漏
     * 5分钟后未响应的对话框会被清除
     * <p>
     * We use Google's cache to store NPC dialogs to send messages
     * The reason is that there is a chance that the client will not respond to the dialogs sent, and in certain cases we cannot clear these dialogs, which can lead to memory leaks
     * Unresponsive dialogs will be cleared after 5 minutes
     */
    protected Cache<String, FormWindowDialog> dialogWindows = Caffeine.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();
    protected Map<Long, DummyBossBar> dummyBossBars = new Long2ObjectLinkedOpenHashMap<>();
    protected int lastInAirTick = 0;
    protected int previousInteractTick = 0;
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
     * 玩家升级时播放音乐的时间
     * <p>
     * Time to play sound when player upgrades
     */
    protected int lastPlayerdLevelUpSoundTime = 0;
    /**
     * 玩家最后攻击的实体.
     * <p>
     * The entity that the player attacked last.
     */
    protected Entity lastAttackEntity = null;

    /**
     * 玩家迷雾设置
     * <p>
     * Player Fog Settings
     */
    protected List<PlayerFogPacket.Fog> fogStack = new ArrayList<>();
    /**
     * 最后攻击玩家的实体.
     * <p>
     * The entity that the player is attacked last.
     */
    protected Entity lastBeAttackEntity = null;
    private final @NotNull PlayerHandle playerHandle = new PlayerHandle(this);
    @Getter
    protected final PlayerChunkManager playerChunkManager;
    private boolean needDimensionChangeACK = false;
    private Boolean openSignFront = null;
    protected Boolean flySneaking = false;
    /// lastUseItem System and item cooldown
    protected final HashMap<String, Integer> cooldownTickMap = new HashMap<>();
    protected final HashMap<String, Integer> lastUseItemMap = new HashMap<>(1);
    ///

    /// inventory system
    protected int windowsCnt = 1;
    protected int closingWindowId = Integer.MIN_VALUE;
    protected final BiMap<Inventory, Integer> windows = HashBiMap.create();
    protected final BiMap<Integer, Inventory> windowIndex = windows.inverse();
    protected final Set<Integer> permanentWindows = new IntOpenHashSet();
    protected CraftingGridInventory craftingGridInventory;
    protected PlayerCursorInventory playerCursorInventory;
    protected CreativeOutputInventory creativeOutputInventory;
    /**
     * Player opens it own inventory
     */
    protected boolean inventoryOpen;
    /**
     * Player open it own ender chest inventory
     */
    protected boolean enderChestOpen;
    /**
     * Player open a fake Inventory
     */
    protected boolean fakeInventoryOpen;
    ///

    /// todo hack for receive a error position after teleport
    private Pair<Location, Long> lastTeleportMessage;
    ///

    private Color locatorBarColor;
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
        this.locatorBarColor = new Color(Utils.rand(0, 255), Utils.rand(0, 255), Utils.rand(0, 255));
    }

    /**
     * Get the network Session for the player
     *
     * @return the network Session
     */
    @NotNull
    public BedrockSession getSession() {
        return this.session;
    }

    /**
     * 将服务端侧游戏模式转换为网络包适用的游戏模式ID
     * 此方法是为了解决NK观察者模式ID为3而原版ID为6的问题
     *
     * @param gamemode 服务端侧游戏模式
     * @return 网络层游戏模式ID
     */
    public static int toNetworkGamemode(int gamemode) {
        return gamemode != SPECTATOR ? gamemode : GameType.SPECTATOR.ordinal();
    }

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
        playerHandle.setInteract();
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
            this.level.addLevelSoundEvent(block, LevelSoundEvent.EXTINGUISH_FIRE);
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

        boolean canChangeBlock = target.isBlockChangeAllowed(this);
        if (!canChangeBlock) {
            return;
        }

        if (this.isSurvival() || (this.isAdventure() && canChangeBlock)) {
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

    protected void onBlockBreakAbort(Vector3 pos) {
        if (pos.distanceSquared(this) < 1000) {// same as with ACTION_START_BREAK
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
                    log.debug("Tried to set item {} but {} had item {} in their hand slot", handItem.getId(), this.getName(), clone.getId());
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
        packet.fadeInTime = -1;
        packet.stayTime = -1;
        packet.fadeOutTime = -1;
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

        //todo remove these
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
     * 完成completeLoginSequence后执行
     */
    protected void doFirstSpawn() {
        this.spawned = true;

        this.getSession().syncCraftingData();
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

        //Weather
        this.getLevel().sendWeather(this);

        //FoodLevel
        PlayerFood food = this.getFoodData();
        if (food.isHungry()) {
            food.sendFood();
        }

        var scoreboardManager = this.getServer().getScoreboardManager();
        if (scoreboardManager != null) {//in test environment sometimes the scoreboard level is null
            scoreboardManager.onPlayerJoin(this);
        }

        if (this.getSpawn().second() == null || this.getSpawn().second() == SpawnPointType.WORLD) {
            this.setSpawn(this.level.getSafeSpawn(), SpawnPointType.WORLD);
        } else {
            //update compass
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

        log.debug("Send Player Spawn Status Packet to {},wait init packet", getName());
        this.sendPlayStatus(PlayStatusPacket.PLAYER_SPAWN);

        //客户端初始化完毕再传送玩家，避免下落 (x)
        //已经设置immobile了所以不用管下落了
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
        } else setHealth(getHealth()); //sends health to player

        getLevel().getScheduler().scheduleDelayedTask(InternalPlugin.INSTANCE, () -> {
            this.session.getMachine().fire(SessionState.IN_GAME);
        }, 5);
    }

    @Override
    protected void checkGroundState(double movX, double movY, double movZ, double dx, double dy, double dz) {
        if (!this.onGround || movX != 0 || movY != 0 || movZ != 0) {
            boolean onGround = false;

            double shrinkFaktor = 0.01d;

            AxisAlignedBB realBB = this.boundingBox.shrink(shrinkFaktor, shrinkFaktor, shrinkFaktor).clone();
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
//                level.addParticle(new BlockForceFieldParticle(b1.add(0.5, 0, 0.5)));
                onGround = true;
            }
            for (Block block : blocks) {
//                level.addParticle(new BlockForceFieldParticle(block.add(0.5, 0, 0.5)));
                if (!block.canPassThrough() && block.collidesWithBB(realBB)) {
                    onGround = true;
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

        if (endPortal) {//handle endPortal teleport
            if (!inEndPortal) {
                inEndPortal = true;
                if (this.getRiding() == null && this.getPassengers().isEmpty()) {
                    EntityPortalEnterEvent ev = new EntityPortalEnterEvent(this, PortalType.END);
                    getServer().getPluginManager().callEvent(ev);

                    if (!ev.isCancelled()) {
                        final Position newPos = PortalHelper.convertPosBetweenEndAndOverworld(this);
                        if (newPos != null) {
                            if (newPos.getLevel().getDimension() == Level.DIMENSION_THE_END) {
                                if (teleport(newPos, TeleportCause.END_PORTAL)) {
                                    newPos.getLevel().getScheduler().scheduleDelayedTask(new Task() {
                                        @Override
                                        public void onRun(int currentTick) {
                                            // dirty hack to make sure chunks are loaded and generated before spawning player
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

    protected void checkNearEntities() {
        for (Entity entity : this.level.getNearbyEntities(this.boundingBox.grow(1, 0.5, 1), this)) {
            if (entity == null) continue;
            entity.scheduleUpdate();

            if (!entity.isAlive() || !this.isAlive()) {
                continue;
            }

            this.pickupEntity(entity, true);
        }
    }

    @Override
    public Location clone() {
        return getLocation();
    }

    protected void handleMovement(Location clientPos) {
        if (this.firstMove) this.firstMove = false;
        boolean invalidMotion = false;
        var revertPos = this.getLocation().clone();
        double distance = clientPos.distanceSquared(this);
        //before check
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

        //update server-side position and rotation and aabb
        double diffX = clientPos.getX() - this.x;
        double diffY = clientPos.getY() - this.y;
        double diffZ = clientPos.getZ() - this.z;
        this.setRotation(clientPos.getYaw(), clientPos.getPitch(), clientPos.getHeadYaw());
        this.fastMove(diffX, diffY, diffZ);

        //after check
        double corrX = this.x - clientPos.getX();
        double corrY = this.y - clientPos.getY();
        double corrZ = this.z - clientPos.getZ();

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
                            this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.getVector3(), VibrationType.ELYTRA_GLIDE));
                        } else if (this.isOnGround() && !(this.getSide(BlockFace.DOWN).getLevelBlock() instanceof BlockWool) && !this.isSneaking()) {
                            this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.getVector3(), VibrationType.STEP));
                        } else if (this.isTouchingWater()) {
                            this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.getLocation().clone(), VibrationType.SWIM));
                        }
                    }
                    this.broadcastMovement(false);
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
        var distance = newPosition.distance(this);
        var updatePosition = distance > MOVEMENT_DISTANCE_THRESHOLD;//sqrt distance
        var updateRotation = (float) Math.abs(this.getPitch() - newPosition.pitch) > ROTATION_UPDATE_THRESHOLD
                || (float) Math.abs(this.getYaw() - newPosition.yaw) > ROTATION_UPDATE_THRESHOLD
                || (float) Math.abs(this.getHeadYaw() - newPosition.headYaw) > ROTATION_UPDATE_THRESHOLD;
        var isHandle = this.isAlive() && this.spawned && !this.isSleeping() && (updatePosition || updateRotation);
        if (isHandle) {
            //todo hack for receive a error position after teleport
            long now = System.currentTimeMillis();
            if (lastTeleportMessage != null && (now - lastTeleportMessage.right()) < 200) {
                var dis = newPosition.distance(lastTeleportMessage.left());
                if (dis < MOVEMENT_DISTANCE_THRESHOLD) return;
            }
            this.newPosition = newPosition;
            this.clientMovements.offer(newPosition);
        }
    }


    protected void handleLogicInMove(boolean invalidMotion, double distance) {
        if (!invalidMotion) {
            //处理饱食度更新
            if (this.getFoodData().isEnabled() && this.getServer().getDifficulty() > 0) {
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
                        this.getFoodData().exhaust(0.1 * distance2 + jump + swimming);
                    } else {
                        if (this.inAirTicks == 3 && swimming == 0) {
                            jump = 0.05;
                        }
                        this.getFoodData().exhaust(jump + swimming);
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

            //处理灵魂急行附魔
            //Handling Soul Speed Enchantment
            int soulSpeedLevel = this.getInventory().getBoots().getEnchantmentLevel(Enchantment.ID_SOUL_SPEED);
            if (soulSpeedLevel > 0) {
                Block levelBlock = this.getLevelBlock();
                this.soulSpeedMultiplier = (soulSpeedLevel * 0.105f) + 1.3f;

                // levelBlock check is required because of soul sand being 1 pixel shorter than normal blocks
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

        //以下两个List的元素一一对应
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
     * 玩家客户端初始化完成后调用
     */
    protected void onPlayerLocallyInitialized() {
        if (locallyInitialized) return;
        locallyInitialized = true;

        //init entity data property
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
          我们在玩家客户端初始化后才发送游戏模式，以解决观察者模式疾跑速度不正确的问题
          只有在玩家客户端进入游戏显示后再设置观察者模式，疾跑速度才正常
          强制更新游戏模式以确保客户端会收到模式更新包
          After initializing the player client, we send the game mode to address the issue of incorrect
          sprint speed in spectator mode. Only after the player client enters the game display is spectator mode set,
          and the sprint speed behaves normally. We force an update of the game mode to ensure that the client receives the
          mode update packet.
         */
        this.setGamemode(this.gamemode, false, null, true);
        this.sendData(this.hasSpawned.values().toArray(Player.EMPTY_ARRAY), entityDataMap);
        this.spawnToAll();
        Arrays.stream(this.level.getEntities()).filter(entity -> entity.getViewers().containsKey(this.getLoaderId()) && entity instanceof EntityBoss).forEach(entity -> ((EntityBoss) entity).addBossbar(this));
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

    @Override
    public boolean isBanned() {
        return this.server.getNameBans().isBanned(this.getName());
    }

    protected void respawn() {
        //the player can't respawn if the server is hardcore
        if (this.server.isHardcore()) {
            this.setBanned(true);
            return;
        }

        this.resetInventory();

        //level spawn point < block spawn = self spawn
        Pair<Position, SpawnPointType> spawnPair = this.getSpawn();
        PlayerRespawnEvent playerRespawnEvent = new PlayerRespawnEvent(this, spawnPair);
        if (spawnPair.right() == SpawnPointType.BLOCK) {//block spawn
            Block spawnBlock = playerRespawnEvent.getRespawnPosition().first().getLevelBlock();
            if (spawnBlock != null && isValidRespawnBlock(spawnBlock)) {
                // handle RESPAWN_ANCHOR state change when consume charge is true
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
            } else {//block not available
                Position defaultSpawn = this.getServer().getDefaultLevel().getSpawnLocation();
                this.setSpawn(defaultSpawn, SpawnPointType.WORLD);
                playerRespawnEvent.setRespawnPosition(Pair.of(defaultSpawn, SpawnPointType.WORLD));
                // handle spawn point change when block spawn not available
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

    protected void addDefaultWindows() {
        this.craftingGridInventory = new CraftingGridInventory(this);
        this.playerCursorInventory = new PlayerCursorInventory(this);
        this.creativeOutputInventory = new CreativeOutputInventory(this);

        this.addWindow(this.getInventory(), SpecialWindowId.PLAYER.getId());
        //addDefaultWindows when the player doesn't have a spawn yet,
        // so we need to manually open it to add the player to the viewer
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
    }

    @Override
    protected float getBaseOffset() {
        return super.getBaseOffset();
    }

    @Override
    protected void onBlock(Entity entity, EntityDamageEvent e, boolean animate) {
        super.onBlock(entity, e, animate);
        if (e.isBreakShield()) {
            this.setItemCoolDown(e.getShieldBreakCoolDown(), "shield");
        }
        if (animate) {
            this.setDataFlag(EntityFlag.BLOCKED_USING_DAMAGED_SHIELD, true);
            getLevel().getScheduler().scheduleTask(InternalPlugin.INSTANCE, () -> {
                if (this.isOnline()) {
                    this.setDataFlag(EntityFlag.BLOCKED_USING_DAMAGED_SHIELD, false);
                }
            });
        }
    }

    /**
     * @return {@link #lastAttackEntity}
     */
    public Entity getLastAttackEntity() {
        return lastAttackEntity;
    }

    /**
     * @return {@link #lastBeAttackEntity}
     */
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
     * 返回{@link Player#lastInAirTick}的值,代表玩家上次在空中的server tick
     * <p>
     * Returns the value of {@link Player#lastInAirTick},represent the last server tick the player was in the air
     *
     * @return int
     */
    public int getLastInAirTick() {
        return this.lastInAirTick;
    }

    public int getPreviousInteractTick() {
        return this.previousInteractTick;
    }

    public int getPreviousInteractTickDifference() {
        return getServer().getTick() - getPreviousInteractTick();
    }

    /**
     * 获取玩家离开的消息
     *
     * @return {@link TranslationContainer}
     */
    public TranslationContainer getLeaveMessage() {
        return new TranslationContainer(TextFormat.YELLOW + "%multiplayer.player.left", this.getDisplayName());
    }

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
        return this.server.isWhitelisted(this.getName().toLowerCase(Locale.ENGLISH));
    }

    @Override
    public void setWhitelisted(boolean value) {
        if (value) {
            this.server.addWhitelist(this.getName().toLowerCase(Locale.ENGLISH));
        } else {
            this.server.removeWhitelist(this.getName().toLowerCase(Locale.ENGLISH));
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
        this.adventureSettings = adventureSettings.clone();
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

    public void setHorizontalFlySpeed(float speed) {
        this.horizontalFlySpeed = speed;
    }

    public float getHorizontalFlySpeed() {
        return horizontalFlySpeed;
    }

    public void setVerticalFlySpeed(float speed) {
        this.verticalFlySpeed = speed;
    }

    public float getVerticalFlySpeed() {
        return verticalFlySpeed;
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

    public void broadcastClientSyncedProperties(Player... viewers) {
        PropertySyncData data = this.getClientSyncProperties();
        if (data == null) return;

        SetEntityDataPacket pk = new SetEntityDataPacket();
        pk.eid = this.getId();
        pk.entityData = this.getEntityDataMap();
        pk.syncedProperties = data;
        pk.frame = 0L;

        Player[] targets = (viewers == null || viewers.length == 0)
            ? this.getViewers().values().toArray(Player.EMPTY_ARRAY)
            : viewers;

        for (Player v : targets) {
            if (v != null) v.dataPacket(pk);
        }
    }

    @Override
    public void spawnTo(Player player) {
        if (player.spawned && this.isAlive() && player.getLevel() == this.level && player.canSee(this)/* && !this.isSpectator()*/) {
            super.spawnTo(player);
            this.broadcastClientSyncedProperties(player);

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
        return this.connected.get() && this.loggedIn;
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

        if (this.isEnableClientCommand() && spawned) {
            this.getSession().syncAvailableCommands();
        }
    }

    public boolean isEnableClientCommand() {
        return this.enableClientCommand;
    }

    public void setEnableClientCommand(boolean enable) {
        this.enableClientCommand = enable;
        this.getSession().setEnableClientCommand(enable);
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
        return connected.get();
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
            this.server.updatePlayerListData(this.getUniqueId(), this.getId(), this.getDisplayName(), this.getSkin(), this.getLoginChainData().getXUID(), this.getLocatorBarColor());
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
     * Close all form windows
     */
    public void closeFormWindows() {
        this.formWindows.clear();
        this.dataPacket(new ClientboundCloseFormPacket());
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
     */
    public boolean isUsingItem(String itemId) {
        return getLastUseTick(itemId) != -1 && this.getDataFlag(EntityFlag.USING_ITEM);
    }

    /**
     * the cooldown of specified item is end
     *
     * @param itemId the item identifier
     * @return the boolean
     */
    public boolean isItemCoolDownEnd(Identifier itemId) {
        return isItemCoolDownEnd(itemId.toString());
    }

    /**
     * the cooldown of specified item is end
     *
     * @param category a string category
     * @return the boolean
     */
    public boolean isItemCoolDownEnd(String category) {
        int now  = this.getLevel().getTick();
        int end  = this.cooldownTickMap.getOrDefault(category, 0);
        boolean done = now - end >= 0;
        if (done) this.cooldownTickMap.remove(category);
        return done;
    }

    /**
     * Sets the cooldown time for the specified item to use
     *
     * @param coolDownTick the cool down tick
     * @param itemId       the item id
     */
    public void setItemCoolDown(int coolDown, Identifier itemId) {
        setItemCoolDown(coolDown, itemId.toString());
    }

    /**
     * Sets the cooldown time for the specified item to use
     *
     * @param coolDownTick the cool down tick
     * @param itemId       a string category
     */
    public void setItemCoolDown(int coolDown, String category) {
        var pk = new PlayerStartItemCoolDownPacket();
        pk.setCoolDownDuration(coolDown);
        pk.setItemCategory(category);
        this.cooldownTickMap.put(category, this.getLevel().getTick() + coolDown);
        this.dataPacket(pk);
    }

    /**
     * Start last use tick for an item(right-click).
     *
     * @param itemId the item id
     */
    public void setLastUseTick(@NotNull String itemId, int tick) {
        lastUseItemMap.put(itemId, tick);
        this.setDataFlag(EntityFlag.USING_ITEM, true);
    }

    public void removeLastUseTick(@NotNull String itemId) {
        lastUseItemMap.remove(itemId);
        this.setDataFlag(EntityFlag.USING_ITEM, false);
    }

    public int getLastUseTick(String itemId) {
        return lastUseItemMap.getOrDefault(itemId, -1);
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
        this.setDataProperty(INTERACT_TEXT, this.buttonText);
    }

    public void unloadChunk(int x, int z) {
        this.unloadChunk(x, z, null);
    }

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
    public Pair<Position, SpawnPointType> getSpawn() {
        return Pair.of(spawnPoint, spawnPointType);
    }

    /**
     * 设置玩家的出生点/复活点。
     * <p>
     * Set the player's birth point.
     *
     * @param pos 出生点位置
     */
    public void setSpawn(Position pos, SpawnPointType spawnPointType) {
        Preconditions.checkNotNull(pos);
        Preconditions.checkNotNull(pos.level);
        this.spawnPoint = new Position(pos.x, pos.y, pos.z, pos.level);
        this.spawnPointType = spawnPointType;
        SetSpawnPositionPacket pk = new SetSpawnPositionPacket();
        pk.spawnType = SetSpawnPositionPacket.TYPE_PLAYER_SPAWN;
        pk.x = (int) this.spawnPoint.x;
        pk.y = (int) this.spawnPoint.y;
        pk.z = (int) this.spawnPoint.z;
        pk.dimension = this.spawnPoint.level.getDimension();
        this.dataPacket(pk);
    }

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

        for (BlockEntity entity : this.level.getChunkBlockEntities(x, z).values()) {
            if (entity instanceof BlockEntitySpawnable spawnable) {
                spawnable.spawnTo(this);
            }
        }

        if (this.needDimensionChangeACK) {
            this.needDimensionChangeACK = false;
            PlayerActionPacket pap = new PlayerActionPacket();
            pap.action = PlayerActionPacket.ACTION_DIMENSION_CHANGE_ACK;
            pap.entityId = this.getId();
            this.dataPacket(pap);
        }
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
            ServerScheduler scheduler = level == null ? server.getScheduler() : level.getScheduler();
            delayedPosTrackingUpdate = scheduler.scheduleDelayedTask(null, this::updateTrackingPositions, 10);
            return;
        }
        PositionTrackingService positionTrackingService = server.getPositionTrackingService();
        positionTrackingService.forceRecheck(this);
    }

    /**
     * @param packet 发送的数据包<br>packet to send
     */
    public void dataPacket(DataPacket packet) {
        this.getSession().sendPacket(packet);
    }

    /**
     * 得到该玩家的网络延迟。
     * <p>
     * Get the network latency of the player.
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

    public boolean awardAchievement(String achievementId) {
        if (!Server.getInstance().getSettings().gameplaySettings().achievements()) {
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
     * Set GameMode
     *
     * @param gamemode    The player game mode to set
     * @param serverSide  Whether to update only the game mode of players on the server side. If true, the game mode update package will not be sent to the client
     * @param newSettings New Adventure Settings
     * @param forceUpdate Whether to force an update. If true, the check for the parameter 'gamemode' will be canceled
     * @return gamemode
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

    @ApiStatus.Internal
    public boolean fastMove(double dx, double dy, double dz) {
        this.x += dx;
        this.y += dy;
        this.z += dz;
        this.recalculateBoundingBox(true);

        this.checkChunks();

        if (!this.isSpectator()) {
            this.checkGroundState(dx, dy, dz, dx, dy, dz);
            this.updateFallState(this.onGround);
        }
        return true;
    }


    @ApiStatus.Internal
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
                pk.eid = this.getId();
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

    /**
     * Send attributes to client
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
     * Send the fog settings to the client
     */
    public void sendFogStack() {
        var pk = new PlayerFogPacket();
        pk.fogStack = this.fogStack;
        this.dataPacket(pk);
    }

    /**
     * Send camera presets to cilent
     */
    public void sendCameraPresets() {
        var pk = new CameraPresetsPacket();
        pk.presets.addAll(CameraPreset.getPresets().values());
        dataPacket(pk);
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("player");
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public float getHeight() {
        if (this.riding instanceof EntityHorse) {
            return 1.1f;
        }
        return super.getHeight();
    }

    @Override
    public void setSwimming(boolean value) {
        //Stopping a swim at a height of 1 block will still send a STOPSWIMMING ACTION from the client, but the player will still be swimming height,so skip the action
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

        if (this.fishing != null && this.level.getTick() % 20 == 0) {
            if (this.distance(fishing) > 33) {
                this.stopFishing(false);
            }
        }

        if (!this.isAlive() && this.spawned) {
            if (this.getLevel().getGameRules().getBoolean(GameRule.DO_IMMEDIATE_RESPAWN)) {
                this.despawnFromAll();
                return true;
            }
            getLevel().getScheduler().scheduleDelayedTask(InternalPlugin.INSTANCE, this::despawnFromAll, 10);
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

            if (this.getServer().getDifficulty() == 0 || this.level.getGameRules().getBoolean(GameRule.NATURAL_REGENERATION)) {
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
                    this.lastInAirTick = level.getTick();
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

                if (this.getFoodData() != null) {
                    this.getFoodData().tick(tickDiff);
                }

                //鞘翅检查和耐久计算
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

        if (this.spawned && !this.dummyBossBars.isEmpty() && currentTick % 100 == 0) {
            this.dummyBossBars.values().forEach(DummyBossBar::updateBossEntityPosition);
        }

        PlayerFood foodData = getFoodData();
        if (this.ticksLived % 40 == 0 && foodData != null) {
            foodData.sendFood();
        }

        return true;
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

    public int getEnchantmentSeed() {
        return this.enchSeed;
    }

    public void setEnchantmentSeed(int seed) {
        this.enchSeed = seed;
    }

    public void regenerateEnchantmentSeed() {
        this.enchSeed = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
    }

    public void checkNetwork() {
        if (!this.isOnline()) {
            return;
        }

        if (this.nextChunkOrderRun-- <= 0 || this.chunk == null) {
            CompletableFuture.runAsync(playerChunkManager::tick, this.server.getComputeThreadPool());
        }

        if (this.chunkLoadCount >= this.spawnThreshold && !this.spawned && loggedIn) {
            this.getSession().notifyTerrainReady();
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
        boolean forceTranslate = this.server.getSettings().baseSettings().forceServerTranslate()
                || parameters.length > 4;
        if (forceTranslate) {
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
     * @see #close(TextContainer, String)
     */
    public void close(String reason) {
        this.close(this.getLeaveMessage(), reason);
    }

    public void close(String message, String reason) {
        this.close(new TextContainer(message), reason);
    }

    /**
     * {@code reason="generic",notify=true}
     *
     * @see #close(TextContainer, String)
     */
    public void close(TextContainer message) {
        this.close(message, "generic");
    }

    /**
     * 关闭该玩家的连接及其一切活动，和{@link #kick}差不多效果，区别在于{@link #kick}是基于{@code close}实现的。
     * <p>
     * Closing the player's connection and all its activities is almost as function as {@link #kick}, the difference is that {@link #kick} is implemented based on {@code close}.
     *
     * @param message PlayerQuitEvent事件消息<br>PlayerQuitEvent message
     * @param reason  登出原因<br>Reason for logout
     */
    public void close(TextContainer message, String reason) {
        if (!this.connected.compareAndSet(true, false) && this.closed) {
            return;
        }
        //output logout infomation
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

        //handle scoreboardManager#beforePlayerQuit
        var scoreboardManager = this.getServer().getScoreboardManager();
        if (scoreboardManager != null) {
            scoreboardManager.beforePlayerQuit(this);
        }

        //dismount horse
        if (this.riding instanceof EntityRideable entityRideable) {
            entityRideable.dismountEntity(this);
        }

        unloadAllUsedChunk();

        //send disconnection packet
        DisconnectPacket packet = new DisconnectPacket();
        if (reason == null || reason.isBlank()) {
            packet.hideDisconnectionScreen = true;
            reason = BedrockDisconnectReasons.DISCONNECTED;
        }
        packet.message = reason;
        this.getSession().sendPacketSync(packet);

        //call quit event
        PlayerQuitEvent ev = null;
        if (this.getName() != null && !this.getName().isEmpty()) {
            this.server.getPluginManager().callEvent(ev = new PlayerQuitEvent(this, message, true, reason));
            if (this.fishing != null) {
                this.stopFishing(false);
            }
        }
        // Close the temporary windows first, so they have chance to change all inventories before being disposed
        if (ev != null && ev.getAutoSave() && namedTag != null) {
            this.save();
        }
        super.close();

        this.windows.clear();
        this.hiddenPlayers.clear();
        //remove player from playerlist
        this.server.removeOnlinePlayer(this);
        //remove player from players map
        this.server.removePlayer(this);

        this.server.getPluginManager().unsubscribeFromPermission(Server.BROADCAST_CHANNEL_USERS, this);
        this.server.getPluginManager().unsubscribeFromPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE, this);
        // broadcast disconnection message
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

        //close player network session
        log.debug("Closing player network session");
        log.debug(reason);
        assert this.session != null;
        this.session.close(null);
    }

    public synchronized void unloadAllUsedChunk() {
        //save player data
        //unload chunk for the player
        LongIterator iterator = this.playerChunkManager.getUsedChunks().iterator();
        try {
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
        } catch (Exception e) {
            getServer().getLogger().error("Failed to unload all used chunks.", e);
        } finally {
            this.playerChunkManager.getUsedChunks().clear();
        }
    }

    public void save() {
        this.save(false);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        if (spawnPoint == null) {
            namedTag.remove("SpawnX")
                    .remove("SpawnY")
                    .remove("SpawnZ")
                    .remove("SpawnLevel")
                    .remove("SpawnDimension");
        } else {
            namedTag.putInt("SpawnX", this.spawnPoint.getFloorX())
                    .putInt("SpawnY", this.spawnPoint.getFloorY())
                    .putInt("SpawnZ", this.spawnPoint.getFloorZ());
            if (this.spawnPoint.getLevel() != null) {
                this.namedTag.putString("SpawnLevel", this.spawnPoint.getLevel().getName());
                this.namedTag.putInt("SpawnDimension", this.spawnPoint.getLevel().getDimension());
            } else {
                this.namedTag.putString("SpawnLevel", this.server.getDefaultLevel().getName());
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
            this.namedTag.putString("Level", this.level.getName());

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
            this.namedTag.putInt("foodLevel", this.getFoodData().getFood());
            this.namedTag.putFloat("foodSaturationLevel", this.getFoodData().getSaturation());
            this.namedTag.putInt("enchSeed", this.enchSeed);

            var fogIdentifiers = new ListTag<StringTag>();
            var userProvidedFogIds = new ListTag<StringTag>();
            this.fogStack.forEach(fog -> {
                fogIdentifiers.add(new StringTag(fog.identifier().toString()));
                userProvidedFogIds.add(new StringTag(fog.userProvidedId()));
            });
            this.namedTag.putList("fogIdentifiers", fogIdentifiers);
            this.namedTag.putList("userProvidedFogIds", userProvidedFogIds);

            this.namedTag.putInt("TimeSinceRest", this.timeSinceRest);

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
        if (health < 1) {
            health = 0;
        }
        super.setHealth(health);
        Attribute attribute = this.attributes.computeIfAbsent(Attribute.MAX_HEALTH, Attribute::getAttribute);
        attribute.setMaxValue(this.getAbsorption() % 2 != 0 ? this.getMaxHealth() + 1 : this.getMaxHealth()).setValue(health > 0 ? (health < getMaxHealth() ? health : getMaxHealth()) : 0);
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

        Attribute attribute = this.attributes.computeIfAbsent(Attribute.MAX_HEALTH, Attribute::getAttribute);
        attribute.setMaxValue(this.getAbsorption() % 2 != 0 ? this.getMaxHealth() + 1 : this.getMaxHealth()).setValue(health > 0 ? (health < getMaxHealth() ? health : getMaxHealth()) : 0);
        if (this.spawned) {
            UpdateAttributesPacket pk = new UpdateAttributesPacket();
            pk.entries = new Attribute[]{attribute};
            pk.entityId = this.getId();
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
                    LevelSoundEvent.LEVEL_UP,
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
     * setExperience的实现部分，用来设置当前等级
     * <p>
     * The implementation of setExperience is used to set the level
     *
     * @param level 等级
     */
    public void sendExperienceLevel(int level) {
        if (this.spawned) {
            Attribute attribute = this.attributes.computeIfAbsent(Attribute.EXPERIENCE_LEVEL, Attribute::getAttribute);
            attribute.setValue(level);
            this.syncAttribute(attribute);
        }
    }

    /**
     * 以指定{@link Attribute}发送UpdateAttributesPacket数据包到该玩家。
     * <p>
     * Send UpdateAttributesPacket packets to this player with the specified {@link Attribute}.
     *
     * @param attribute the attribute
     */
    public void syncAttribute(Attribute attribute) {
        UpdateAttributesPacket pk = new UpdateAttributesPacket();
        pk.entries = new Attribute[]{attribute};
        pk.entityId = this.getId();
        this.dataPacket(pk);
    }

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
    public void sendMovementSpeed(float speed) {
        Attribute attribute = this.attributes.computeIfAbsent(Attribute.MOVEMENT_SPEED, Attribute::getAttribute);
        attribute.setValue(speed);
        this.syncAttribute(attribute);
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
            if (this.getLevel().getBlock(this.getPosition().floor().add(0.5, -1, 0.5)).getId().equals(Block.SLIME)) {
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
                        ((Player) damager).getFoodData().exhaust(0.1);
                    }
                    //保存攻击玩家的实体在lastBeAttackEntity
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

        this.setDataFlag(EntityFlag.USING_ITEM, false);
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
        this.lastTeleportMessage = Pair.of(from, System.currentTimeMillis());

        Location to = location;
        //event
        if (cause != null) {
            PlayerTeleportEvent event = new PlayerTeleportEvent(this, from, to, cause);
            this.server.getPluginManager().callEvent(event);
            if (event.isCancelled()) return false;
            to = event.getTo();
        }

        //remove inventory,ride,sign editor
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
            //unload entities for old level
            Arrays.stream(from.getLevel().getEntities()).forEach(e -> e.despawnFrom(this));
        }

        clientMovements.clear();
        //switch level, update pos and rotation, update aabb
        if (setPositionAndRotation(to, to.getYaw(), to.getPitch(), to.getHeadYaw())) {
            //if switch level or the distance teleported is too far
            if (switchLevel) {
                this.playerChunkManager.handleTeleport();
                //set nextChunkOrderRun is zero means that the next tick immediately execute the playerChunkManager#tick
                this.nextChunkOrderRun = 0;
            } else if ((Math.abs(from.getChunkX() - to.getChunkX()) >= this.getViewDistance())
                    || (Math.abs(from.getChunkZ() - to.getChunkZ()) >= this.getViewDistance())) {
                this.playerChunkManager.handleTeleport();
                this.nextChunkOrderRun = 0;
            }
            //send to client
            this.sendPosition(to, to.yaw, to.pitch, MovePlayerPacket.MODE_TELEPORT);
            this.newPosition = to;
        } else {
            this.sendPosition(this, to.yaw, to.pitch, MovePlayerPacket.MODE_TELEPORT);
            this.newPosition = this;
        }
        //state update
        this.positionChanged = true;

        if (switchLevel) {
            refreshChunkRender();
        }
        this.resetFallDistance();
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
     * Sends a form to a player and assigns the next ID to it
     * To open a form safely, please use {@link Form#send(Player)}
     *
     * @param form The form to open
     * @return The id assigned to the form
     */
    public int sendForm(Form<?> form) {
        return this.sendForm(form, this.formWindowCount++);
    }

    /**
     * Sends a form to a player and assigns a given ID to it
     * To open a form safely, please use {@link Form#send(Player)}
     *
     * @param form The form to open
     * @param id   The ID to assign the form to
     * @return The id assigned to the form
     */
    public int sendForm(Form<?> form, int id) {
        if (this.formWindows.size() > 10) {
            this.kick("Server sent to many forms. Please ");
            return id;
        }

        if (!form.isViewer(this)) {
            form.viewers().add(this);
        }

        ModalFormRequestPacket packet = new ModalFormRequestPacket();
        packet.formId = id;
        packet.data = form.toJson();

        this.formWindows.put(packet.formId, form);

        this.dataPacket(packet);
        return id;
    }

    @UnintendedClientBehaviour
    public void updateForm(Form<?> form) {
        if (!form.isViewer(this)) {
            return;
        }

        this.formWindows.entrySet()
                .stream()
                .filter(f -> f.getValue().equals(form))
                .map(Map.Entry::getKey)
                .findFirst()
                .ifPresent(id -> {
                    ServerSettingsResponsePacket packet = new ServerSettingsResponsePacket(); // Exploiting some (probably unintended) protocol features here
                    packet.formId = id;
                    packet.data = form.toJson();

                    this.dataPacket(packet);
                });
    }

    public void checkClosedForms() {
        this.formWindows.entrySet().removeIf(entry -> !entry.getValue().isViewer(this));
    }

    public Map<Integer, Form<?>> getFormWindows() {
        return formWindows;
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
        dialog.getBindEntity().setDataProperty(HAS_NPC, true);
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
     * 在游戏设置中显示一个新的设置页面。
     * 你可以通过监听PlayerFormRespondedEvent来了解设置结果。
     * <p>
     * Shows a new setting page in game settings.
     * You can find out settings result by listening to PlayerFormRespondedEvent
     *
     * @param window to show on settings page
     * @return form id to use in {@link PlayerFormRespondedEvent}
     */
    public int addServerSettings(Form<?> window) {
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
    public int getWindowId(@NotNull Inventory inventory) {
        Preconditions.checkNotNull(inventory);
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
     * Add inventory to the current player.
     *
     * @param inventory The Inventory object representing the window, must not be null.
     * @return The unique identifier assigned to the window if successfully added and opened; -1 if the window fails to be added.
     */
    public int addWindow(@NotNull Inventory inventory) {
        if (getTopWindow().isPresent() || inventoryOpen) return -1;
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
        } else {
            this.removeWindow(inventory);
            return -1;
        }
        for (int index : inventory.getContents().keySet()) {
            Item item = inventory.getUnclonedItem(index);
            if (item instanceof ItemBundle bundle) {
                if (bundle.hasCompoundTag()) {
                    bundle.onChange(inventory);
                    inventory.sendSlot(index, this);
                }
            }
        }
        return cnt;
    }

    public int addWindow(@NotNull Inventory inventory, Integer forceId) {
        Preconditions.checkNotNull(inventory);
        if (this.windows.containsKey(inventory)) {
            return this.windows.get(inventory);
        }
        int cnt;
        if (forceId == null) {
            this.windowsCnt = cnt = Math.max(1, ++this.windowsCnt % 101);//1-100
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
     * 常用于刷新。
     * <p>
     * Commonly used for refreshing.
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
     * 获取该玩家的{@link PlayerCursorInventory}
     * <p>
     * Gets cursor inventory of the player.
     */
    public PlayerCursorInventory getCursorInventory() {
        return playerCursorInventory;
    }

    /**
     * 获取该玩家的{@link CraftingGridInventory}
     * <p>
     * Gets crafting grid of the player.
     */
    public CraftingGridInventory getCraftingGrid() {
        return this.craftingGridInventory;
    }

    public CreativeOutputInventory getCreativeOutputInventory() {
        return this.creativeOutputInventory;
    }

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
     * @Since 1.21.90 (818)
     * The client closes inventores when the SLEEP player tag is set.
     * Even the players inventory, which cannot be closed with the ContainerClosePacket
     * This won't close the inventories on the server side, but the client will send us the ContainerClose which in return will close the inventory on the server side
     * We're setting the flag manually because setPlayerFlag just flips the bit. But we need to set the bits in the correct order.
     */
    @UnintendedClientBehaviour
    public void forceClientCloseInventory() {
        setDataProperty(PLAYER_FLAGS, getDataProperty(PLAYER_FLAGS) | 0x2);
        getLevel().getScheduler().scheduleDelayedTask(() -> setDataProperty(PLAYER_FLAGS, getDataProperty(PLAYER_FLAGS) & 0x1), 2);
    }

    /**
     * 获取上一个关闭窗口对应的id
     * <p>
     * Get the id corresponding to the last closed window
     */
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
    public void onChunkChanged(IChunk chunk) {
        this.playerChunkManager.addSendChunk(chunk.getX(), chunk.getZ());
    }

    @Override
    public void onChunkLoaded(IChunk chunk) {
    }


    @Override
    public void onChunkUnloaded(IChunk chunk) {
        this.unloadChunk(chunk.getX(), chunk.getZ(), chunk.getProvider().getLevel());
    }

    @Override
    public int getLoaderId() {
        return this.loaderId;
    }

    @Override
    public boolean isLoaderActive() {
        return this.isConnected();
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
            this.clientMovements.clear();
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

    @UnmodifiableView
    public Set<Long> getUsedChunks() {
        return Collections.unmodifiableSet(playerChunkManager.getUsedChunks());
    }

    @Override
    public void setSprinting(boolean value) {
        if (value && this.getFreezingTicks() > 0) return;

        if (isSprinting() != value) {
            super.setSprinting(value);
            this.recalcMovementSpeedFromEffects();
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

    @ApiStatus.Internal
    public boolean pickupEntity(Entity entity, boolean near) {
        if (!this.spawned || !this.isAlive() || !this.isOnline() || this.isSpectator() || entity.isClosed()) {
            return false;
        }

        if (near) {
            Inventory inventory = this.inventory;
            if (entity instanceof EntityArrow entityArrow && entityArrow.hadCollision) {
                ItemArrow item = entityArrow.getArrowItem() != null ? entityArrow.getArrowItem() : new ItemArrow();
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
                if (entityItem.getPickupDelay() <= 0 && !entityItem.isDisplayOnly()) {
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

        int tick = this.getLevel().getTick();
        if (pickedXPOrb < tick && entity instanceof EntityXpOrb xpOrb && this.boundingBox.isVectorInside(entity)) {
            if (xpOrb.getPickupDelay() <= 0) {
                int exp = xpOrb.getExp();
                entity.kill();
                this.getLevel().addLevelEvent(LevelEventPacket.EVENT_SOUND_EXPERIENCE_ORB_PICKUP, 0, this);
                pickedXPOrb = tick;

                //Mending
                ArrayList<Integer> itemsWithMending = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    if (inventory.getArmorItem(i).hasEnchantment(Enchantment.ID_MENDING)) {
                        itemsWithMending.add(inventory.getSize() + i);
                    }
                }
                if (inventory.getItemInHand().hasEnchantment(Enchantment.ID_MENDING)) {
                    itemsWithMending.add(inventory.getHeldItemIndex());
                }
                if (itemsWithMending.size() > 0) {
                    Random rand = new Random();
                    Integer itemToRepair = itemsWithMending.get(rand.nextInt(itemsWithMending.size()));
                    Item toRepair = inventory.getItem(itemToRepair);
                    if (toRepair instanceof ItemTool || toRepair instanceof ItemArmor) {
                        if (toRepair.getDamage() > 0) {
                            int dmg = toRepair.getDamage() - exp;
                            if (dmg < 0) {
                                exp = Math.abs(dmg);
                                dmg = 0;
                            }
                            toRepair.setDamage(dmg);
                            inventory.setItem(itemToRepair, toRepair);
                        }
                    }
                }

                if (exp > 0) this.addExperience(exp, true);
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
    public void giveItem(Item... items) {
        for (Item failed : getInventory().addItem(items)) {
            getLevel().dropItem(this, failed);
        }
    }


    /**
     * How many ticks have passed since the player last sleeped, 1 tick = 0.05 s
     *
     * @return the ticks
     */
    public int getTimeSinceRest() {
        return timeSinceRest;
    }


    /**
     * Set the timeSinceRest ticks
     *
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


    public boolean isShowingCredits() {
        return showingCredits;
    }


    public void setShowingCredits(boolean showingCredits) {
        this.showingCredits = showingCredits;
        if (showingCredits) {
            ShowCreditsPacket pk = new ShowCreditsPacket();
            pk.eid = this.getId();
            pk.status = ShowCreditsPacket.STATUS_START_CREDITS;
            this.dataPacket(pk);
        }
    }


    public void showCredits() {
        this.setShowingCredits(true);
    }


    public boolean hasSeenCredits() {
        return showingCredits;
    }


    public void setHasSeenCredits(boolean hasSeenCredits) {
        this.hasSeenCredits = hasSeenCredits;
    }


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
     * 玩家屏幕振动效果
     * <p>
     * Player screen shake effect
     *
     * @param intensity   the intensity
     * @param duration    the duration
     * @param shakeType   the shake type
     * @param shakeAction the shake action
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
        pk2.infos = scoreboard.getLines().values().stream().map(IScoreboardLine::toNetworkInfo).filter(Objects::nonNull).collect(Collectors.toList());
        pk2.action = SetScorePacket.Action.SET;
        this.dataPacket(pk2);

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

    public Boolean isOpenSignFront() {
        return openSignFront;
    }

    /**
     * Set the status of the current player opening sign
     *
     * @param frontSide true means open sign front, vice versa. If it is null, it means that the player has not opened sign
     */
    public void setOpenSignFront(Boolean frontSide) {
        openSignFront = frontSide;
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

    public void setFlySneaking(boolean sneaking) {
        this.flySneaking = sneaking;
    }


    public boolean isFlySneaking() {
        return this.flySneaking;
    }

    public int getChunkSendCountPerTick() {
        return chunksPerTick;
    }

    public void setEnderChestOpen(boolean v) {
        this.enderChestOpen = v;
    }

    public boolean getEnderChestOpen() {
        return this.enderChestOpen;
    }

    public boolean getFakeInventoryOpen() {
        return fakeInventoryOpen;
    }

    public void setFakeInventoryOpen(boolean fakeInventoryOpen) {
        this.fakeInventoryOpen = fakeInventoryOpen;
    }

    public Color getLocatorBarColor() {
        return this.locatorBarColor;
    }

    public void setLocatorBarColor(Color color) {
        this.locatorBarColor = color;
        if (this.spawned) {
            this.server.updatePlayerListData(this.getUniqueId(), this.getId(), this.getDisplayName(), this.getSkin(), this.getLoginChainData().getXUID(), this.getLocatorBarColor());
        }
    }

    /**
     * Gets fog stack.
     */
    public List<PlayerFogPacket.Fog> getFogStack() {
        return fogStack;
    }

    /**
     * Set the fog stack, if you want to client effect,you need {@link #sendFogStack}
     *
     * @param fogStack the fog stack
     */
    public void setFogStack(List<PlayerFogPacket.Fog> fogStack) {
        this.fogStack = fogStack;
    }

    /**
     * Get the player info.
     */
    @NotNull
    public PlayerInfo getPlayerInfo() {
        return this.info;
    }

    public String getXUID() {
        return this.loginChainData.getXUID();
    }

    /**
     * Returns the flight status of player
     */
    public boolean isFlying() {
        return this.getAdventureSettings().get(Type.FLYING);
    }

    /**
     * Sets the flight status of player. If you want to work it properly, you need to use {@link #setAllowFlight}
     *
     * @param value Status value
     */
    public void setFlying(boolean value) {
        boolean isFlying = this.isFlying();

        if (value != isFlying){
            this.resetFallDistance();

            this.getAdventureSettings().set(Type.FLYING, value);
            this.getAdventureSettings().update();
        }
    }
}
