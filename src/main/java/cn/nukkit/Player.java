package cn.nukkit;

import cn.nukkit.AdventureSettings.Type;
import cn.nukkit.api.*;
import cn.nukkit.block.*;
import cn.nukkit.blockentity.*;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandDataVersions;
import cn.nukkit.command.utils.RawText;
import cn.nukkit.dialog.handler.FormDialogHandler;
import cn.nukkit.dialog.response.FormResponseDialog;
import cn.nukkit.dialog.window.FormWindowDialog;
import cn.nukkit.entity.*;
import cn.nukkit.entity.custom.CustomEntity;
import cn.nukkit.entity.data.*;
import cn.nukkit.entity.item.*;
import cn.nukkit.entity.passive.EntityNPCEntity;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.entity.projectile.EntityThrownTrident;
import cn.nukkit.event.block.LecternPageChangeEvent;
import cn.nukkit.event.block.WaterFrostEvent;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityDamageEvent.DamageModifier;
import cn.nukkit.event.entity.EntityPortalEnterEvent.PortalType;
import cn.nukkit.event.inventory.*;
import cn.nukkit.event.player.*;
import cn.nukkit.event.player.PlayerAsyncPreLoginEvent.LoginResult;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.event.player.PlayerTeleportEvent.TeleportCause;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.event.vehicle.VehicleMoveEvent;
import cn.nukkit.form.handler.FormResponseHandler;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.inventory.*;
import cn.nukkit.inventory.transaction.*;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.data.ReleaseItemData;
import cn.nukkit.inventory.transaction.data.UseItemData;
import cn.nukkit.inventory.transaction.data.UseItemOnEntityData;
import cn.nukkit.item.*;
import cn.nukkit.item.customitem.ItemCustom;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.sideeffect.SideEffect;
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
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.network.CompressionProvider;
import cn.nukkit.network.Network;
import cn.nukkit.network.SourceInterface;
import cn.nukkit.network.protocol.*;
import cn.nukkit.network.protocol.types.*;
import cn.nukkit.network.session.NetworkPlayerSession;
import cn.nukkit.permission.PermissibleBase;
import cn.nukkit.permission.Permission;
import cn.nukkit.permission.PermissionAttachment;
import cn.nukkit.permission.PermissionAttachmentInfo;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.positiontracking.PositionTracking;
import cn.nukkit.positiontracking.PositionTrackingService;
import cn.nukkit.potion.Effect;
import cn.nukkit.resourcepacks.ResourcePack;
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
import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
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
import org.powernukkit.version.Version;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.List;
import java.util.*;
import java.util.Queue;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static cn.nukkit.utils.Utils.dynamic;

/**
 * @author MagicDroidX &amp; Box (Nukkit Project)
 */
@Log4j2
public class Player extends EntityHuman implements CommandSender, InventoryHolder, ChunkLoader, IPlayer, IScoreboardViewer {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final Player[] EMPTY_ARRAY = new Player[0];

    private static final int NO_SHIELD_DELAY = 10;

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
    public static final @PowerNukkitXOnly
    @Since("1.19.21-r1") int TRADE_WINDOW_ID = 500;

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
    public static final @Since("1.4.0.0-PN")
    @PowerNukkitOnly int SMITHING_WINDOW_ID = dynamic(6);

    @Since("FUTURE")
    protected static final int RESOURCE_PACK_CHUNK_SIZE = 8 * 1024; // 8KB

    protected final SourceInterface interfaz;
    @Since("1.19.30-r1")
    @PowerNukkitXOnly
    protected final NetworkPlayerSession networkSession;

    public boolean playedBefore;
    public boolean spawned = false;
    public boolean loggedIn = false;
    @Since("1.4.0.0-PN")
    public boolean locallyInitialized = false;
    private boolean verified = false;
    private int unverifiedPackets;
    public int gamemode;
    public long lastBreak;
    private BlockVector3 lastBreakPosition = new BlockVector3();

    protected int windowCnt = 4;

    protected final BiMap<Inventory, Integer> windows = HashBiMap.create();

    protected final BiMap<Integer, Inventory> windowIndex = windows.inverse();
    protected final Set<Integer> permanentWindows = new IntOpenHashSet();
    private boolean inventoryOpen;
    @Since("1.4.0.0-PN")
    protected int closingWindowId = Integer.MIN_VALUE;

    protected int messageCounter = 2;

    private String clientSecret;

    public Vector3 speed = null;

    private final Queue<Vector3> clientMovements = PlatformDependent.newMpscQueue(4);

    public final HashSet<String> achievements = new HashSet<>();

    public int craftingType = CRAFTING_SMALL;

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

    public long creationTime = 0;

    protected long randomClientId;

    protected Vector3 forceMovement = null;

    protected Vector3 teleportPosition = null;

    protected boolean connected = true;
    protected final InetSocketAddress rawSocketAddress;
    protected InetSocketAddress socketAddress;
    protected boolean removeFormat = true;

    protected String username;
    protected String iusername;
    protected String displayName;

    protected int startAction = -1;

    protected Vector3 sleeping = null;
    protected Long clientID = null;

    private int loaderId;

    public final Map<Long, Boolean> usedChunks = new Long2ObjectOpenHashMap<>();

    protected int chunkLoadCount = 0;
    protected final Long2ObjectLinkedOpenHashMap<Boolean> loadQueue = new Long2ObjectLinkedOpenHashMap<>();
    protected int nextChunkOrderRun = 1;

    protected final Map<UUID, Player> hiddenPlayers = new HashMap<>();

    protected Vector3 newPosition = null;

    protected int chunkRadius;
    protected int viewDistance;
    protected final int chunksPerTick;
    protected final int spawnThreshold;

    protected Position spawnPosition;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected Position spawnBlockPosition;

    protected int inAirTicks = 0;
    protected int startAirTicks = 5;

    private int noShieldTicks;

    protected AdventureSettings adventureSettings;

    protected boolean checkMovement = true;

    private PermissibleBase perm = null;

    private int exp = 0;
    private int expLevel = 0;

    protected PlayerFood foodData = null;

    private Entity killer = null;

    private final AtomicReference<Locale> locale = new AtomicReference<>(null);

    private int hash;

    private String buttonText = "Button";

    protected boolean enableClientCommand = true;

    private BlockEnderChest viewingEnderChest = null;

    protected int lastEnderPearl = 20;
    protected int lastChorusFruitTeleport = 20;

    private LoginChainData loginChainData;
    public Block breakingBlock = null;
    private PlayerBlockActionData lastBlockAction;

    public int pickedXPOrb = 0;

    protected int formWindowCount = 0;
    protected Map<Integer, FormWindow> formWindows = new Int2ObjectOpenHashMap<>();
    protected Map<Integer, FormWindow> serverSettings = new Int2ObjectOpenHashMap<>();

    /**
     * 我们使用google的cache来存储NPC对话框发送信息
     * 原因是发送过去的对话框客户端有几率不响应，在特定情况下我们无法清除这些对话框，这会导致内存泄漏
     * 5分钟后未响应的对话框会被清除
     */
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    protected Cache<String, FormWindowDialog> dialogWindows = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();

    protected Map<Long, DummyBossBar> dummyBossBars = new Long2ObjectLinkedOpenHashMap<>();

    private AsyncTask preLoginEventTask = null;
    protected boolean shouldLogin = false;

    public EntityFishingHook fishing = null;

    public long lastSkinChange;

    protected double lastRightClickTime = 0.0;
    protected Vector3 lastRightClickPos = null;

    private int timeSinceRest;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected int lastPlayerdLevelUpSoundTime = 0;

    private TaskHandler delayedPosTrackingUpdate;

    private float soulSpeedMultiplier = 1;
    private boolean wasInSoulSandCompatible;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    private boolean showingCredits;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    private boolean hasSeenCredits;
    /**
     * 玩家最后攻击的实体.
     * <p>
     * The entity that the player attacked last.
     */
    @PowerNukkitXOnly
    @Since("1.19.30-r1")
    protected Entity lastAttackEntity = null;
    /**
     * 最后攻击玩家的实体.
     * <p>
     * The entity that the player is attacked last.
     */
    @PowerNukkitXOnly
    @Since("1.19.30-r1")
    protected Entity lastBeAttackEntity = null;
    private boolean foodEnabled = true;
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
        this.chunksPerTick = this.server.getConfig("chunk-sending.per-tick", 4);
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
     * Returns a client-friendly gamemode of the specified real gamemode
     * This function takes care of handling gamemodes known to MCPE (as of 1.1.0.3, that includes Survival, Creative and Adventure)
     * <p>
     * TODO: remove this when Spectator Mode gets added properly to MCPE
     */
    private static int getClientFriendlyGamemode(int gamemode) {
        gamemode &= 0x03;
        if (gamemode == Player.SPECTATOR) {
            return Player.CREATIVE;
        }
        return gamemode;
    }

    private void logTriedToSetButHadInHand(Item tried, Item had) {
        log.debug("Tried to set item {} but {} had item {} in their hand slot", tried.getId(), this.username, had.getId());
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
        return this.server.getNetwork().unpackBatchedPackets(packet, CompressionProvider.ZLIB);
    }

    private void onBlockBreakContinue(Vector3 pos, BlockFace face) {
        if (this.isBreakingBlock()) {
            Block block = this.level.getBlock(pos, false);
            this.level.addParticle(new PunchBlockParticle(pos, block, face));
        }
    }

    private void onBlockBreakStart(Vector3 pos, BlockFace face) {
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
            return;
        }
        //todo 需要检查这里的更改是否有必要
        switch (target.getId()) {
            case Block.NOTEBLOCK:
                ((BlockNoteblock) target).emitSound();
                return;
            case Block.DRAGON_EGG:
                if (!this.isCreative()) {
                    ((BlockDragonEgg) target).teleport();
                    return;
                }
                break;
            case Block.ITEM_FRAME_BLOCK:
                BlockEntity itemFrame = this.level.getBlockEntity(pos);
                if (itemFrame instanceof BlockEntityItemFrame && ((BlockEntityItemFrame) itemFrame).dropItem(this)) {
                    return;
                }
                break;
        }

        target.onTouch(this, playerInteractEvent.getAction());
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

        if (!this.isCreative()) {
            double breakTime = Math.ceil(target.getBreakTime(this.inventory.getItemInHand(), this) * 20);
            if (breakTime > 0) {
                LevelEventPacket pk = new LevelEventPacket();
                pk.evid = LevelEventPacket.EVENT_BLOCK_START_BREAK;
                pk.x = (float) pos.x;
                pk.y = (float) pos.y;
                pk.z = (float) pos.z;
                pk.data = (int) (65535 / breakTime);
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
        this.lastBreak = currentBreak;
        this.lastBreakPosition = blockPos;
    }

    private void onBlockBreakAbort(Vector3 pos, BlockFace face) {
        if (pos.distanceSquared(this) < 100) {// same as with ACTION_START_BREAK
            LevelEventPacket pk = new LevelEventPacket();
            pk.evid = LevelEventPacket.EVENT_BLOCK_STOP_BREAK;
            pk.x = (float) pos.x;
            pk.y = (float) pos.y;
            pk.z = (float) pos.z;
            pk.data = 0;
            this.getLevel().addChunkPacket(pos.getFloorX() >> 4, pos.getFloorZ() >> 4, pk);
        }
        this.breakingBlock = null;
    }

    private void onBlockBreakComplete(BlockVector3 blockPos, BlockFace face) {
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
    }

    private void updateBlockingFlag() {
        boolean shouldBlock = getNoShieldTicks() == 0
                && (this.isSneaking() || getRiding() != null)
                && (this.getInventory().getItemInHand().getId() == ItemID.SHIELD || this.getOffhandInventory().getItem(0).getId() == ItemID.SHIELD);

        if (isBlocking() != shouldBlock) {
            this.setBlocking(shouldBlock);
        }
    }

    protected void sendNextChunk() {
        if (!this.connected) {
            return;
        }

        Timings.playerChunkSendTimer.startTiming();

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
                    if (this.spawned && this.teleportPosition == null) {
                        continue;
                    } else {
                        break;
                    }
                }

                iter.remove();

                PlayerChunkRequestEvent ev = new PlayerChunkRequestEvent(this, chunkX, chunkZ);
                this.server.getPluginManager().callEvent(ev);
                if (!ev.isCancelled()) {
                    this.level.requestChunk(chunkX, chunkZ, this);
                }
            }
        }
        if (this.chunkLoadCount >= this.spawnThreshold && !this.spawned && this.teleportPosition == null) {
            this.doFirstSpawn();
        }
        Timings.playerChunkSendTimer.stopTiming();
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.addDefaultWindows();
    }

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

        Location pos;
        if (this.server.isSafeSpawn()) {
            pos = this.level.getSafeSpawn(this).getLocation();
            pos.yaw = this.yaw;
            pos.pitch = this.pitch;
        } else {
            pos = new Location(this.forceMovement.x, this.forceMovement.y, this.forceMovement.z, this.yaw, this.pitch, this.level);
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
        this.teleport(pos.getFloorY() == pos.getY() ? pos.add(0, 0.5) : pos.setY(Math.ceil(pos.getY())), TeleportCause.PLAYER_SPAWN);
        lastYaw = yaw;
        lastPitch = pitch;

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

        if (!this.isSpectator()) {
            this.spawnToAll();
        }

        //todo Updater

        //Weather
        this.getLevel().sendWeather(this);

        //FoodLevel
        PlayerFood food = this.getFoodData();
        if (food.getLevel() != food.getMaxLevel()) {
            food.sendFoodLevel();
        }

        if (this.getHealth() < 1) {
            this.respawn();
        } else {
            updateTrackingPositions(false);
        }

        var scoreboardManager = this.getServer().getScoreboardManager();
        if (scoreboardManager != null) {//in test environment sometimes the scoreboard manager is null
            scoreboardManager.onPlayerJoin(this);
        }

        this.sendFogStack();
    }

    protected boolean orderChunks() {
        if (!this.connected) {
            return false;
        }

        Timings.playerChunkOrderTimer.startTiming();

        this.nextChunkOrderRun = 200;

        loadQueue.clear();
        Long2ObjectOpenHashMap<Boolean> lastChunk = new Long2ObjectOpenHashMap<>(this.usedChunks);

        int centerX = (int) this.x >> 4;
        int centerZ = (int) this.z >> 4;

        int radius = spawned ? this.chunkRadius : (int) Math.ceil(Math.sqrt(spawnThreshold));
        int radiusSqr = radius * radius;


        long index;
        for (int x = 0; x <= radius; x++) {
            int xx = x * x;
            for (int z = 0; z <= x; z++) {
                int distanceSqr = xx + z * z;
                if (distanceSqr > radiusSqr) continue;

                /* Top right quadrant */
                if (this.usedChunks.get(index = Level.chunkHash(centerX + x, centerZ + z)) != Boolean.TRUE) {
                    this.loadQueue.put(index, Boolean.TRUE);
                }
                lastChunk.remove(index);
                /* Top left quadrant */
                if (this.usedChunks.get(index = Level.chunkHash(centerX - x - 1, centerZ + z)) != Boolean.TRUE) {
                    this.loadQueue.put(index, Boolean.TRUE);
                }
                lastChunk.remove(index);
                /* Bottom right quadrant */
                if (this.usedChunks.get(index = Level.chunkHash(centerX + x, centerZ - z - 1)) != Boolean.TRUE) {
                    this.loadQueue.put(index, Boolean.TRUE);
                }
                lastChunk.remove(index);
                /* Bottom left quadrant */
                if (this.usedChunks.get(index = Level.chunkHash(centerX - x - 1, centerZ - z - 1)) != Boolean.TRUE) {
                    this.loadQueue.put(index, Boolean.TRUE);
                }
                lastChunk.remove(index);
                if (x != z) {
                    /* Top right quadrant mirror */
                    if (this.usedChunks.get(index = Level.chunkHash(centerX + z, centerZ + x)) != Boolean.TRUE) {
                        this.loadQueue.put(index, Boolean.TRUE);
                    }
                    lastChunk.remove(index);
                    /* Top left quadrant mirror */
                    if (this.usedChunks.get(index = Level.chunkHash(centerX - z - 1, centerZ + x)) != Boolean.TRUE) {
                        this.loadQueue.put(index, Boolean.TRUE);
                    }
                    lastChunk.remove(index);
                    /* Bottom right quadrant mirror */
                    if (this.usedChunks.get(index = Level.chunkHash(centerX + z, centerZ - x - 1)) != Boolean.TRUE) {
                        this.loadQueue.put(index, Boolean.TRUE);
                    }
                    lastChunk.remove(index);
                    /* Bottom left quadrant mirror */
                    if (this.usedChunks.get(index = Level.chunkHash(centerX - z - 1, centerZ - x - 1)) != Boolean.TRUE) {
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

        Timings.playerChunkOrderTimer.stopTiming();
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

                    if (!ev.isCancelled() && (this.getLevel() == EnumLevel.OVERWORLD.getLevel() || this.getLevel() == EnumLevel.THE_END.getLevel())) {
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

    //服务端权威移动的处理移动方法(和原来的大差不差)
    protected void handleMovement(Vector3 clientPos) {
        if (!this.isAlive() || !this.spawned || this.teleportPosition != null || this.isSleeping()) {
            this.positionChanged = false;
            return;
        }

        boolean invalidMotion = false;
        double distance = clientPos.distanceSquared(this);
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
            this.positionChanged = false;
            this.revertClientMotion(this.getLocation());
            return;
        }

        double diffX = clientPos.getX() - this.x;
        double diffY = clientPos.getY() - this.y;
        double diffZ = clientPos.getZ() - this.z;

        // Client likes to clip into few blocks like stairs or slabs
        // This should help reduce the server mis-prediction at least a bit
        diffY += this.ySize * (1 - 0.4D);

        this.fastMove(diffX, diffY, diffZ);

        double corrX = this.x - clientPos.getX();
        double corrY = this.y - clientPos.getY();
        double corrZ = this.z - clientPos.getZ();

        double yS = this.getStepHeight() + this.ySize;
        if (corrY >= -yS || corrY <= yS) {
            corrY = 0;
        }

        if (this.checkMovement && (Math.abs(corrX) > 0.5 || Math.abs(corrY) > 0.5 || Math.abs(corrZ) > 0.5) &&
                this.riding == null && !this.hasEffect(Effect.LEVITATION) && !this.hasEffect(Effect.SLOW_FALLING) && !server.getAllowFlight()) {
            double diff = corrX * corrX + corrZ * corrZ;
            //这里放宽了判断，否则对角穿过脚手架会判断非法移动。
            if (diff > 1) {
                PlayerInvalidMoveEvent event = new PlayerInvalidMoveEvent(this, true);
                this.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled() && (invalidMotion = event.isRevert())) {
                    this.server.getLogger().warning(this.getServer().getLanguage().translateString("nukkit.player.invalidMove", this.getName()));
                }
            }

            if (!invalidMotion) {
                this.x = clientPos.getX();
                this.y = clientPos.getY();
                this.z = clientPos.getZ();
                double radius = this.getWidth() / 2;
                this.boundingBox.setBounds(this.x - radius, this.y, this.z - radius, this.x + radius, this.y + this.getCurrentHeight(), this.z + radius);
            }
        }

        Location source = new Location(this.lastX, this.lastY, this.lastZ, this.lastYaw, this.lastPitch, this.level);
        Location target = this.getLocation();
        double delta = Math.pow(this.lastX - target.getX(), 2) + Math.pow(this.lastY - target.getY(), 2) + Math.pow(this.z - target.getZ(), 2);
        double deltaAngle = Math.abs(this.lastYaw - target.getYaw()) + Math.abs(this.lastPitch - target.getPitch());

        if (delta > 0.0005 || deltaAngle > 1) {
            boolean isFirst = this.firstMove;
            this.firstMove = false;

            this.lastX = target.x;
            this.lastY = target.y;
            this.lastZ = target.z;
            this.lastYaw = target.yaw;
            this.lastPitch = target.pitch;

            if (!isFirst) {
                List<Block> blocksAround = new ArrayList<>(this.blocksAround);
                List<Block> collidingBlocks = new ArrayList<>(this.collisionBlocks);

                PlayerMoveEvent ev = new PlayerMoveEvent(this, source, target);

                if (this.clientMovements.isEmpty()) {
                    this.blocksAround = null;
                    this.collisionBlocks = null;
                }

                this.server.getPluginManager().callEvent(ev);

                if (!(invalidMotion = ev.isCancelled())) { //Yes, this is intended
                    if (!target.equals(ev.getTo()) && this.riding == null) { //If plugins modify the destination
                        if (delta > 0.0001d && this.getGamemode() != Player.SPECTATOR)
                            this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, ev.getTo().clone(), VibrationType.TELEPORT));
                        this.teleport(ev.getTo(), null);
                    } else {
                        if (delta > 0.0001d && this.getGamemode() != Player.SPECTATOR) {
                            if (this.isOnGround() && this.isGliding()) {
                                this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.clone(), VibrationType.ELYTRA_GLIDE));
                            } else if (this.isOnGround() && this.getSide(BlockFace.DOWN).getLevelBlock().getId() != BlockID.WOOL && !this.isSneaking()) {
                                this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.clone(), VibrationType.STEP));
                            } else if (this.isTouchingWater()) {
                                this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.clone(), VibrationType.SWIM));
                            }
                        }
                        //this.addMovement(this.x, this.y, this.z, this.yaw, this.pitch, this.yaw);
                        this.broadcastMovement(false);
                    }
                } else {
                    this.blocksAround = blocksAround;
                    this.collisionBlocks = collidingBlocks;
                }
            }

            if (this.speed == null) {
                this.speed = new Vector3(source.x - target.x, source.y - target.y, source.z - target.z);
            } else {
                this.speed.setComponents(source.x - target.x, source.y - target.y, source.z - target.z);
            }
        } else {
            if (this.speed == null) speed = new Vector3(0, 0, 0);
            else this.speed.setComponents(0, 0, 0);
        }

        handleLogicInMove(invalidMotion, distance, delta);

        if (invalidMotion) {
            this.revertClientMotion(this.getLocation());
        } else {
            this.forceMovement = null;
            if (distance != 0 && this.nextChunkOrderRun > 20) {
                this.nextChunkOrderRun = 20;
            }
        }
        this.resetClientMovement();
    }

    //非服务端权威移动的处理移动方法(原始方法)
    protected void processMovement(int tickDiff) {
        if (!this.isAlive() || !this.spawned || this.newPosition == null || this.teleportPosition != null || this.isSleeping()) {
            this.positionChanged = false;
            return;
        }
        Vector3 newPos = this.newPosition;
        double distanceSquared = newPos.distanceSquared(this);
        boolean revert = false;
        if ((distanceSquared / ((double) (tickDiff * tickDiff))) > 100 && (newPos.y - this.y) > -5) {
            revert = true;
        } else if (this.chunk == null || !this.chunk.isGenerated()) {
            BaseFullChunk chunk = this.level.getChunk((int) newPos.x >> 4, (int) newPos.z >> 4, false);
            if (chunk == null || !chunk.isGenerated()) {
                revert = true;
                this.nextChunkOrderRun = 0;
            } else {
                if (this.chunk != null) {
                    this.chunk.removeEntity(this);
                }
                this.chunk = chunk;
            }
        }


        double tdx = newPos.x - this.x;
        double tdz = newPos.z - this.z;
        double distance = Math.sqrt(tdx * tdx + tdz * tdz);

        if (!revert && distanceSquared != 0) {
            double dx = newPos.x - this.x;
            double dy = newPos.y - this.y;
            double dz = newPos.z - this.z;

            this.fastMove(dx, dy, dz);
            if (this.newPosition == null) {
                return; //maybe solve that in better way
            }

            double diffX = this.x - newPos.x;
            double diffY = this.y - newPos.y;
            double diffZ = this.z - newPos.z;

            double yS = 0.5 + this.ySize;
            if (diffY >= -yS || diffY <= yS) {
                diffY = 0;
            }

            if (diffX != 0 || diffY != 0 || diffZ != 0) {
                if (this.checkMovement && !isOp() && !server.getAllowFlight() && (this.isSurvival() || this.isAdventure())) {
                    // Some say: I cant move my head when riding because the server
                    // blocked my movement
                    if (!this.isSleeping() && this.riding == null && !this.hasEffect(Effect.LEVITATION) && !this.hasEffect(Effect.SLOW_FALLING)) {
                        double diffHorizontalSqr = (diffX * diffX + diffZ * diffZ) / ((double) (tickDiff * tickDiff));
                        if (diffHorizontalSqr > 0.5) {
                            PlayerInvalidMoveEvent ev;
                            this.getServer().getPluginManager().callEvent(ev = new PlayerInvalidMoveEvent(this, true));
                            if (!ev.isCancelled()) {
                                revert = ev.isRevert();

                                if (revert) {
                                    log.warn(this.getServer().getLanguage().translateString("nukkit.player.invalidMove", this.getName()));
                                }
                            }
                        }
                    }
                }


                this.x = newPos.x;
                this.y = newPos.y;
                this.z = newPos.z;
                double radius = this.getWidth() / 2;
                this.boundingBox.setBounds(
                        this.x - radius,
                        this.y,
                        this.z - radius,
                        this.x + radius,
                        this.y + this.getCurrentHeight(),
                        this.z + radius
                );
            }
        }

        Location from = new Location(this.lastX, this.lastY, this.lastZ, this.lastYaw, this.lastPitch, this.level);
        Location to = this.getLocation();

        double delta = Math.pow(this.lastX - to.x, 2) + Math.pow(this.lastY - to.y, 2) + Math.pow(this.z - to.z, 2);
        double deltaAngle = Math.abs(this.lastYaw - to.yaw) + Math.abs(this.lastPitch - to.pitch);

        if (!revert && (delta > 0.0001d || deltaAngle > 1d)) {
            boolean isFirst = this.firstMove;

            this.firstMove = false;
            this.lastX = to.x;
            this.lastY = to.y;
            this.lastZ = to.z;

            this.lastYaw = to.yaw;
            this.lastPitch = to.pitch;

            if (!isFirst) {
                List<Block> blocksAround = new ArrayList<>(this.blocksAround);
                List<Block> collidingBlocks = new ArrayList<>(this.collisionBlocks);

                PlayerMoveEvent ev = new PlayerMoveEvent(this, from, to);

                this.blocksAround = null;
                this.collisionBlocks = null;

                this.server.getPluginManager().callEvent(ev);

                if (!(revert = ev.isCancelled())) { //Yes, this is intended
                    if (!to.equals(ev.getTo()) && this.riding == null) { //If plugins modify the destination
                        if (delta > 0.0001d && this.getGamemode() != Player.SPECTATOR)
                            this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, ev.getTo().clone(), VibrationType.TELEPORT));
                        this.teleport(ev.getTo(), null);
                    } else {
                        if (delta > 0.0001d && this.getGamemode() != Player.SPECTATOR) {
                            if (this.isOnGround() && this.isGliding()) {
                                this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.clone(), VibrationType.ELYTRA_GLIDE));
                            } else if (this.isOnGround() && this.getSide(BlockFace.DOWN).getLevelBlock().getId() != BlockID.WOOL && !this.isSneaking()) {
                                this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.clone(), VibrationType.STEP));
                            } else if (this.isTouchingWater()) {
                                this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.clone(), VibrationType.SWIM));
                            }
                        }
                        //this.addMovement(this.x, this.y, this.z, this.yaw, this.pitch, this.yaw);
                        this.broadcastMovement(false);
                    }
                } else {
                    this.blocksAround = blocksAround;
                    this.collisionBlocks = collidingBlocks;
                }
            }

            if (this.speed == null) speed = new Vector3(from.x - to.x, from.y - to.y, from.z - to.z);
            else this.speed.setComponents(from.x - to.x, from.y - to.y, from.z - to.z);
        } else {
            if (this.speed == null) speed = new Vector3(0, 0, 0);
            else this.speed.setComponents(0, 0, 0);
        }

        handleLogicInMove(revert, distance, delta);

        if (revert) {
            this.revertClientMotion(this.getLocation());
        } else {
            this.forceMovement = null;
            if (distance != 0 && this.nextChunkOrderRun > 20) {
                this.nextChunkOrderRun = 20;
            }
        }
        this.resetClientMovement();
    }

    protected void handleLogicInMove(boolean invalidMotion, double distance, double delta) {
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
            if (delta > 0.0001d) {
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
            }
            //处理灵魂急行附魔
            {
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
    }

    protected void resetClientMovement() {
        this.newPosition = null;
    }

    protected void revertClientMotion(Location originalPos) {
        this.lastX = originalPos.getX();
        this.lastY = originalPos.getY();
        this.lastZ = originalPos.getZ();
        this.lastYaw = originalPos.getYaw();
        this.lastPitch = originalPos.getPitch();

        Vector3 syncPos = originalPos.add(0, 0.00001, 0);
        this.sendPosition(syncPos, originalPos.getYaw(), originalPos.getPitch(), MovePlayerPacket.MODE_RESET);
        this.forceMovement = syncPos;

        if (this.speed == null) {
            this.speed = new Vector3(0, 0, 0);
        } else {
            this.speed.setComponents(0, 0, 0);
        }
    }

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

        this.adventureSettings = new AdventureSettings(this, nbt);/*
                .set(Type.WORLD_IMMUTABLE, isAdventure() || isSpectator())
                .set(Type.WORLD_BUILDER, !isAdventure() && !isSpectator())
                .set(Type.AUTO_JUMP, true)
                .set(Type.ALLOW_FLIGHT, isCreative() || isSpectator())
                .set(Type.NO_CLIP, isSpectator())
                .set(Type.FLYING, isSpectator());*/

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
        if (!this.namedTag.contains("FoodSaturationLevel")) {
            this.namedTag.putFloat("FoodSaturationLevel", 20);
        }
        float foodSaturationLevel = this.namedTag.getFloat("foodSaturationLevel");
        this.foodData = new PlayerFood(this, foodLevel, foodSaturationLevel);

        if (this.isSpectator()) {
            this.keepMovement = true;
            this.onGround = false;
        }

        this.forceMovement = this.teleportPosition = this.getPosition();

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
        startGamePacket.playerGamemode = getClientFriendlyGamemode(this.gamemode);
        startGamePacket.x = (float) this.x;
        startGamePacket.y = (float) this.y;
        startGamePacket.z = (float) this.z;
        startGamePacket.yaw = (float) this.yaw;
        startGamePacket.pitch = (float) this.pitch;
        startGamePacket.seed = -1L;
        startGamePacket.dimension = (byte) (this.level.getDimension() & 0xff);
        startGamePacket.worldGamemode = getClientFriendlyGamemode(this.gamemode);
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
        //写入自定义物品数据
        ItemComponentPacket itemComponentPacket = new ItemComponentPacket();
        if (this.getServer().isEnableExperimentMode() && !Item.getCustomItems().isEmpty()) {
            Int2ObjectOpenHashMap<ItemComponentPacket.Entry> entries = new Int2ObjectOpenHashMap<>();
            int i = 0;
            for (String id : Item.getCustomItems().keySet()) {
                try {
                    Item item = Item.fromString(id);
                    if (item instanceof ItemCustom itemCustom) {
                        CompoundTag data = Item.getCustomItemDefinition().get(itemCustom.getNamespaceId()).nbt();
                        data.putShort("minecraft:identifier", i);

                        entries.put(i, new ItemComponentPacket.Entry(item.getNamespaceId(), data));

                        i++;
                    }
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
        this.adventureSettings.update();
        //发送玩家权限列表
        server.getOnlinePlayers().values().forEach(player -> {
            if (player != this) {
                player.adventureSettings.sendAbilities(Collections.singleton(this));
            }
        });

        this.sendAttributes();

        this.sendPotionEffects(this);

        if (this.isSpectator()) {
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_SILENT, true);
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_HAS_COLLISION, false);
        }

        this.sendData(this);

        this.loggedIn = true;

        this.level.sendTime(this);

        this.sendAttributes();
        this.setNameTagVisible(true);
        this.setNameTagAlwaysVisible(true);
        this.setCanClimb(true);

        log.info(this.getServer().getLanguage().translateString("nukkit.player.logIn",
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

    @PowerNukkitDifference(info = "will force using the spawnposition if the value spawnBlock is null,to fix the bug of command /spawnpoint", since = "1.6.0.0-PNX")
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

        this.teleport(respawnPos.getFloorY() == respawnPos.getY() ? respawnPos.add(0, 0.5) : respawnPos.setY(Math.ceil(respawnPos.getY())), TeleportCause.PLAYER_SPAWN);
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

    protected boolean checkTeleportPosition() {
        if (this.teleportPosition != null) {
            int chunkX = (int) this.teleportPosition.x >> 4;
            int chunkZ = (int) this.teleportPosition.z >> 4;

            for (int X = -1; X <= 1; ++X) {
                for (int Z = -1; Z <= 1; ++Z) {
                    long index = Level.chunkHash(chunkX + X, chunkZ + Z);
                    if (!this.usedChunks.containsKey(index) || !this.usedChunks.get(index)) {
                        return false;
                    }
                }
            }

            this.spawnToAll();
            this.forceMovement = this.teleportPosition;
            this.teleportPosition = null;
            return true;
        }

        return false;
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

    public float getSoulSpeedMultiplier() {
        return this.soulSpeedMultiplier;
    }

    public int getStartActionTick() {
        return startAction;
    }

    public void startAction() {
        this.startAction = this.server.getTick();
    }

    public void stopAction() {
        this.startAction = -1;
    }

    public int getLastEnderPearlThrowingTick() {
        return lastEnderPearl;
    }

    public void onThrowEnderPearl() {
        this.lastEnderPearl = this.server.getTick();
    }

    public int getLastChorusFruitTeleport() {
        return lastChorusFruitTeleport;
    }

    public void onChorusFruitTeleport() {
        this.lastChorusFruitTeleport = this.server.getTick();
    }

    public BlockEnderChest getViewingEnderChest() {
        return viewingEnderChest;
    }

    public void setViewingEnderChest(BlockEnderChest chest) {
        if (chest == null && this.viewingEnderChest != null) {
            this.viewingEnderChest.getViewers().remove(this);
        } else if (chest != null) {
            chest.getViewers().add(this);
        }
        this.viewingEnderChest = chest;
    }

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

    public AdventureSettings getAdventureSettings() {
        return adventureSettings;
    }

    public void setAdventureSettings(AdventureSettings adventureSettings) {
        this.adventureSettings = adventureSettings.clone(this);
        this.adventureSettings.update();
    }

    public void resetInAirTicks() {
        this.inAirTicks = 0;
    }

    @Deprecated
    public void setAllowFlight(boolean value) {
        this.getAdventureSettings().set(Type.ALLOW_FLIGHT, value);
        this.getAdventureSettings().update();
    }

    @Deprecated
    public boolean getAllowFlight() {
        return this.getAdventureSettings().get(Type.ALLOW_FLIGHT);
    }

    public void setAllowModifyWorld(boolean value) {
        this.getAdventureSettings().set(Type.WORLD_IMMUTABLE, !value);
        this.getAdventureSettings().set(Type.BUILD, value);
        this.getAdventureSettings().set(Type.WORLD_BUILDER, value);
        this.getAdventureSettings().update();
    }

    public void setAllowInteract(boolean value) {
        setAllowInteract(value, value);
    }

    public void setAllowInteract(boolean value, boolean containers) {
        this.getAdventureSettings().set(Type.WORLD_IMMUTABLE, !value);
        this.getAdventureSettings().set(Type.DOORS_AND_SWITCHED, value);
        this.getAdventureSettings().set(Type.OPEN_CONTAINERS, containers);
        this.getAdventureSettings().update();
    }

    @Deprecated
    public void setAutoJump(boolean value) {
        this.getAdventureSettings().set(Type.AUTO_JUMP, value);
        this.getAdventureSettings().update();
    }

    @Deprecated
    public boolean hasAutoJump() {
        return this.getAdventureSettings().get(Type.AUTO_JUMP);
    }

    @Override
    public void spawnTo(Player player) {
        if (this.spawned && player.spawned && this.isAlive() && player.getLevel() == this.level && player.canSee(this) && !this.isSpectator()) {
            super.spawnTo(player);
        }
    }

    @Override
    public Server getServer() {
        return this.server;
    }

    public boolean getRemoveFormat() {
        return removeFormat;
    }

    public void setRemoveFormat() {
        this.setRemoveFormat(true);
    }

    public void setRemoveFormat(boolean remove) {
        this.removeFormat = remove;
    }

    public boolean canSee(Player player) {
        return !this.hiddenPlayers.containsKey(player.getUniqueId());
    }

    public void hidePlayer(Player player) {
        if (this == player) {
            return;
        }
        this.hiddenPlayers.put(player.getUniqueId(), player);
        player.despawnFrom(this);
    }

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
            if (!command.testPermissionSilent(this) || !command.isRegistered()) {
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

    public String getDisplayName() {
        return this.displayName;
    }

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
            this.server.updatePlayerListData(this.getUniqueId(), this.getId(), this.getDisplayName(), skin, this.getLoginChainData().getXUID());
        }
    }

    public String getRawAddress() {
        return this.rawSocketAddress.getAddress().getHostAddress();
    }

    public int getRawPort() {
        return this.rawSocketAddress.getPort();
    }

    public InetSocketAddress getRawSocketAddress() {
        return this.rawSocketAddress;
    }

    public String getAddress() {
        return this.socketAddress.getAddress().getHostAddress();
    }

    public int getPort() {
        return this.socketAddress.getPort();
    }

    public InetSocketAddress getSocketAddress() {
        return this.socketAddress;
    }

    public Position getNextPosition() {
        return this.newPosition != null ? new Position(this.newPosition.x, this.newPosition.y, this.newPosition.z, this.level) : this.getPosition();
    }

    public boolean isSleeping() {
        return this.sleeping != null;
    }

    public int getInAirTicks() {
        return this.inAirTicks;
    }

    /**
     * Returns whether the player is currently using an item (right-click and hold).
     *
     * @return bool
     */
    public boolean isUsingItem() {
        return this.getDataFlag(DATA_FLAGS, DATA_FLAG_ACTION) && this.startAction > -1;
    }

    public void setUsingItem(boolean value) {
        this.startAction = value ? this.server.getTick() : -1;
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_ACTION, value);
    }

    public String getButtonText() {
        return this.buttonText;
    }

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

    public boolean isInOverWorld() {
        return this.getLevel().getDimension() == 0;
    }

    public Position getSpawn() {
        if (this.spawnPosition != null) {
            return this.spawnPosition;
        } else {
            return this.server.getDefaultLevel().getSafeSpawn();
        }
    }

    /**
     * The block that holds the player respawn position. May be null when unknown.
     * <p>
     * 保存着玩家重生位置的方块。当未知时可能为空。
     *
     * @return The position of a bed, respawn anchor, or null when unknown.<br>床、重生锚的位置，如果未知，则为空。
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    public Position getSpawnBlock() {
        return spawnBlockPosition;
    }

    /**
     * Sets the position of the block that holds the player respawn position. May be null when unknown.
     * <p>
     * 设置保存着玩家重生位置的方块的位置。可以设置为空。
     *
     * @param spawnBlock The position of a bed or respawn anchor<br>床或重生锚的位置
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setSpawnBlock(@Nullable Vector3 spawnBlock) {
        if (spawnBlock == null) {
            this.setSpawnBlock(null);
        } else {
            this.setSpawnBlock(Position.fromObject(spawnBlock, this.getLevel()));
        }
    }

    @PowerNukkitXOnly
    @Since("1.19.50-r3")
    public void setSpawnBlock(@Nullable Position spawnBlock) {
        if (spawnBlock == null) {
            this.spawnBlockPosition = null;
        } else {
            this.spawnBlockPosition = spawnBlock.clone();
            this.spawnPosition = null;
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
    }

    public void sendChunk(int x, int z, int subChunkCount, byte[] payload) {
        if (!this.connected) {
            return;
        }

        this.usedChunks.put(Level.chunkHash(x, z), true);
        this.chunkLoadCount++;

        LevelChunkPacket pk = new LevelChunkPacket();
        pk.chunkX = x;
        pk.chunkZ = z;
        pk.subChunkCount = subChunkCount;
        pk.data = payload;

        this.dataPacket(pk);

        if (this.spawned) {
            for (Entity entity : this.level.getChunkEntities(x, z).values()) {
                if (this != entity && !entity.closed && entity.isAlive()) {
                    entity.spawnTo(this);
                }
            }
        }
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
     * @param packet packet to send
     * @return packet successfully sent
     */
    public boolean dataPacket(DataPacket packet) {
        if (!this.connected) {
            return false;
        }

        try (Timing ignored = Timings.getSendDataPacketTiming(packet)) {
            DataPacketSendEvent ev = new DataPacketSendEvent(this, packet);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            }

            if (log.isTraceEnabled() && !server.isIgnoredPacket(packet.getClass())) {
                log.trace("Outbound {}: {}", this.getName(), packet);
            }
            this.interfaz.putPacket(this, packet, false, false);
        }
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

    @PowerNukkitDifference(info = "pos can be null now and if it is null,the player's spawn will use the level's default spawn")
    public void setSpawn(@Nullable Vector3 pos) {
        if (pos != null) {
            Level level;
            if (!(pos instanceof Position)) {
                level = this.level;
            } else {
                level = ((Position) pos).getLevel();
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

    public int getGamemode() {
        return gamemode;
    }

    public boolean setGamemode(int gamemode) {
        return this.setGamemode(gamemode, false, null);
    }

    public boolean setGamemode(int gamemode, boolean clientSide) {
        return this.setGamemode(gamemode, clientSide, null);
    }

    public boolean setGamemode(int gamemode, boolean clientSide, AdventureSettings newSettings) {
        if (gamemode < 0 || gamemode > 3 || this.gamemode == gamemode) {
            return false;
        }

        if (newSettings == null) {
            newSettings = this.getAdventureSettings().clone(this);
            newSettings.set(Type.WORLD_IMMUTABLE, (gamemode & 0x02) > 0);
            newSettings.set(Type.BUILD, (gamemode & 0x02) <= 0);
            newSettings.set(Type.WORLD_BUILDER, (gamemode & 0x02) <= 0);
            newSettings.set(Type.ALLOW_FLIGHT, (gamemode & 0x01) > 0);
            newSettings.set(Type.NO_CLIP, gamemode == SPECTATOR);
            if (gamemode == SPECTATOR) {
                newSettings.set(Type.FLYING, true);
            } else if ((gamemode & 0x1) == 0) {
                newSettings.set(Type.FLYING, false);
            }
        }

        PlayerGameModeChangeEvent ev;
        this.server.getPluginManager().callEvent(ev = new PlayerGameModeChangeEvent(this, gamemode, newSettings));

        if (ev.isCancelled()) {
            return false;
        }

        this.gamemode = gamemode;

        if (this.isSpectator()) {
            this.keepMovement = true;
            this.onGround = false;
            this.despawnFromAll();
        } else {
            this.keepMovement = false;
            this.spawnToAll();
        }

        this.namedTag.putInt("playerGameType", this.gamemode);

        if (!clientSide) {
            SetPlayerGameTypePacket pk = new SetPlayerGameTypePacket();
            pk.gamemode = getClientFriendlyGamemode(gamemode);
            this.dataPacket(pk);
        }

        this.setAdventureSettings(ev.getNewAdventureSettings());

        if (this.isSpectator()) {
            this.teleport(this, null);
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_SILENT, true);
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_HAS_COLLISION, false);

            /*InventoryContentPacket inventoryContentPacket = new InventoryContentPacket();
            inventoryContentPacket.inventoryId = InventoryContentPacket.SPECIAL_CREATIVE;
            this.dataPacket(inventoryContentPacket);*/
        } else {
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_SILENT, false);
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_HAS_COLLISION, true);
            /*InventoryContentPacket inventoryContentPacket = new InventoryContentPacket();
            inventoryContentPacket.inventoryId = InventoryContentPacket.SPECIAL_CREATIVE;
            inventoryContentPacket.slots = Item.getCreativeItems().toArray(new Item[0]);
            this.dataPacket(inventoryContentPacket);*/
        }

        this.resetFallDistance();

        this.inventory.sendContents(this);
        this.inventory.sendHeldItem(this.hasSpawned.values());
        this.offhandInventory.sendContents(this);
        this.offhandInventory.sendContents(this.getViewers().values());

        this.inventory.sendCreativeContents();
        return true;
    }

    @Deprecated
    public void sendSettings() {
        this.getAdventureSettings().update();
    }

    public boolean isSurvival() {
        return this.gamemode == SURVIVAL;
    }

    public boolean isCreative() {
        return this.gamemode == CREATIVE;
    }

    public boolean isSpectator() {
        return this.gamemode == SPECTATOR;
    }

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
        if (dx == 0 && dy == 0 && dz == 0) {
            return true;
        }

        Timings.entityMoveTimer.startTiming();

        AxisAlignedBB newBB = this.boundingBox.getOffsetBoundingBox(dx, dy, dz);

        if (this.isSpectator() || server.getAllowFlight() || !this.level.hasCollision(this, newBB.shrink(0, this.getStepHeight(), 0), false)) {
            this.boundingBox = newBB;
        }

        this.x = (this.boundingBox.getMinX() + this.boundingBox.getMaxX()) / 2;
        this.y = this.boundingBox.getMinY() - this.ySize;
        this.z = (this.boundingBox.getMinZ() + this.boundingBox.getMaxZ()) / 2;

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

        Timings.entityMoveTimer.stopTiming();
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

    @Override
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
            ++this.deadTicks;
            if (this.deadTicks >= 10) {
                this.despawnFromAll();
            }
            return true;
        }

        if (this.spawned) {
            if (getServer().getServerAuthoritativeMovement() == 0) {
                if (this.motionX != 0 || this.motionY != 0 || this.motionZ != 0) {
                    this.setMotion(new Vector3(motionX, motionY, motionZ));
                    motionX = motionY = motionZ = 0;
                }
                this.processMovement(tickDiff);
            } else {
                while (!this.clientMovements.isEmpty()) {
                    this.handleMovement(this.clientMovements.poll());
                }
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
                    if (this.checkMovement && !this.isGliding() && !server.getAllowFlight() && !this.getAdventureSettings().get(Type.ALLOW_FLIGHT) && this.inAirTicks > 20 && !this.isSleeping() && !this.isImmobile() && !this.isSwimming() && this.riding == null && !this.hasEffect(Effect.LEVITATION) && !this.hasEffect(Effect.SLOW_FALLING)) {
                        double expectedVelocity = (-this.getGravity()) / ((double) this.getDrag()) - ((-this.getGravity()) / ((double) this.getDrag())) * Math.exp(-((double) this.getDrag()) * ((double) (this.inAirTicks - this.startAirTicks)));
                        double diff = (this.speed.y - expectedVelocity) * (this.speed.y - expectedVelocity);

                        Block block = level.getBlock(this);
                        int blockId = block.getId();
                        boolean ignore = blockId == Block.LADDER || blockId == Block.VINES || blockId == Block.COBWEB
                                || blockId == Block.SCAFFOLDING;// || (blockId == Block.SWEET_BERRY_BUSH && block.getDamage() > 0);

                        if (!this.hasEffect(Effect.JUMP) && diff > 0.6 && expectedVelocity < this.speed.y && !ignore) {
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

                    if (this.isGliding()) this.resetFallDistance();

                    ++this.inAirTicks;

                }

                if (this.getFoodData() != null) this.getFoodData().update(tickDiff);
            }

            if (!this.isSleeping()) {
                this.timeSinceRest++;
            }
        }

        this.checkTeleportPosition();

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

        if (this.getTickCachedLevelBlock() instanceof BlockBigDripleaf block) {
            if (block.isHead())
                block.onUpdate(Level.BLOCK_UPDATE_NORMAL);
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
     * @param maxDistance the maximum distance to check for entities
     * @return Entity|null    either NULL if no entity is found or an instance of the entity
     */
    public EntityInteractable getEntityPlayerLookingAt(int maxDistance) {
        timing.startTiming();

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

        timing.stopTiming();

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


    public void handleDataPacket(DataPacket packet) {
        if (!connected) {
            return;
        }

        if (!verified && packet.pid() != ProtocolInfo.LOGIN_PACKET && packet.pid() != ProtocolInfo.BATCH_PACKET && packet.pid() != ProtocolInfo.REQUEST_NETWORK_SETTINGS_PACKET) {
            log.warn("Ignoring {} from {} due to player not verified yet", packet.getClass().getSimpleName(), getAddress());
            if (unverifiedPackets++ > 100) {
                this.close("", "Too many failed login attempts");
            }
            return;
        }

        try (Timing ignored = Timings.getReceiveDataPacketTiming(packet)) {
            DataPacketReceiveEvent ev = new DataPacketReceiveEvent(this, packet);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return;
            }

            if (packet.pid() == ProtocolInfo.BATCH_PACKET) {
                List<DataPacket> dataPackets = unpackBatchedPackets((BatchPacket) packet);
                dataPackets.forEach(this::handleDataPacket);
                return;
            }

            if (log.isTraceEnabled() && !server.isIgnoredPacket(packet.getClass())) {
                log.trace("Inbound {}: {}", this.getName(), packet);
            }

            packetswitch:
            switch (packet.pid()) {
                case ProtocolInfo.REQUEST_NETWORK_SETTINGS_PACKET:
                    if (this.loggedIn) {
                        break;
                    }

                    int protocolVersion = ((RequestNetworkSettingsPacket) packet).protocolVersion;

                    String message;
                    if (!ProtocolInfo.SUPPORTED_PROTOCOLS.contains(protocolVersion)) {
                        if (protocolVersion < ProtocolInfo.CURRENT_PROTOCOL) {
                            message = "disconnectionScreen.outdatedClient";

                            this.sendPlayStatus(PlayStatusPacket.LOGIN_FAILED_CLIENT, true);
                        } else {
                            message = "disconnectionScreen.outdatedServer";

                            this.sendPlayStatus(PlayStatusPacket.LOGIN_FAILED_SERVER, true);
                        }
                        this.close("", message, false);
                        break;
                    }
                    NetworkSettingsPacket settingsPacket = new NetworkSettingsPacket();
                    settingsPacket.compressionAlgorithm = PacketCompressionAlgorithm.ZLIB;
                    settingsPacket.compressionThreshold = 1; // compress everything
                    this.forceDataPacket(settingsPacket, () -> {
                        this.networkSession.setCompression(CompressionProvider.ZLIB);
                    });
                    break;
                case ProtocolInfo.LOGIN_PACKET:
                    if (this.loggedIn) {
                        break;
                    }

                    LoginPacket loginPacket = (LoginPacket) packet;

                    if (loginPacket.issueUnixTime != -1 && Server.getInstance().checkLoginTime && System.currentTimeMillis() - loginPacket.issueUnixTime > 20000) {
                        message = "disconnectionScreen.noReason";
                        this.sendPlayStatus(PlayStatusPacket.LOGIN_FAILED_SERVER, true);
                        this.close("", message, false);
                        break;
                    }

                    this.username = TextFormat.clean(loginPacket.username);
                    this.displayName = this.username;
                    this.iusername = this.username.toLowerCase();
                    this.setDataProperty(new StringEntityData(DATA_NAMETAG, this.username), false);

                    this.loginChainData = ClientChainData.read(loginPacket);

                    if (!loginChainData.isXboxAuthed() && server.getPropertyBoolean("xbox-auth")) {
                        this.close("", "disconnectionScreen.notAuthenticated");
                        break;
                    }

                    if (this.server.getOnlinePlayers().size() >= this.server.getMaxPlayers() && this.kick(PlayerKickEvent.Reason.SERVER_FULL, "disconnectionScreen.serverFull", false)) {
                        break;
                    }

                    if (this.server.isWaterdogCapable() && loginChainData.getWaterdogIP() != null) {
                        this.socketAddress = new InetSocketAddress(this.loginChainData.getWaterdogIP(), this.getRawPort());
                    }

                    this.randomClientId = loginPacket.clientId;

                    this.uuid = loginPacket.clientUUID;
                    this.rawUUID = Binary.writeUUID(this.uuid);

                    boolean valid = true;
                    int len = loginPacket.username.length();
                    if (len > 16 || len < 3) {
                        valid = false;
                    }

                    for (int i = 0; i < len && valid; i++) {
                        char c = loginPacket.username.charAt(i);
                        if ((c >= 'a' && c <= 'z') ||
                                (c >= 'A' && c <= 'Z') ||
                                (c >= '0' && c <= '9') ||
                                c == '_' || c == ' '
                        ) {
                            continue;
                        }

                        valid = false;
                        break;
                    }

                    if (!valid || Objects.equals(this.iusername, "rcon") || Objects.equals(this.iusername, "console")) {
                        this.close("", "disconnectionScreen.invalidName");

                        break;
                    }

                    if (!loginPacket.skin.isValid()) {
                        this.close("", "disconnectionScreen.invalidSkin");
                        break;
                    } else {
                        Skin skin = loginPacket.skin;
                        if (this.server.isForceSkinTrusted()) {
                            skin.setTrusted(true);
                        }
                        this.setSkin(skin);
                    }

                    PlayerPreLoginEvent playerPreLoginEvent;
                    this.server.getPluginManager().callEvent(playerPreLoginEvent = new PlayerPreLoginEvent(this, "Plugin reason"));
                    if (playerPreLoginEvent.isCancelled()) {
                        this.close("", playerPreLoginEvent.getKickMessage());

                        break;
                    }

                    Player playerInstance = this;
                    this.verified = true;

                    this.preLoginEventTask = new AsyncTask() {
                        private PlayerAsyncPreLoginEvent event;

                        @Override
                        public void onRun() {
                            this.event = new PlayerAsyncPreLoginEvent(username, uuid, loginChainData, playerInstance.getSkin(), playerInstance.getAddress(), playerInstance.getPort());
                            server.getPluginManager().callEvent(this.event);
                        }

                        @Override
                        public void onCompletion(Server server) {
                            if (playerInstance.closed) {
                                return;
                            }

                            if (this.event.getLoginResult() == LoginResult.KICK) {
                                playerInstance.close(this.event.getKickMessage(), this.event.getKickMessage());
                            } else if (playerInstance.shouldLogin) {
                                playerInstance.setSkin(this.event.getSkin());
                                playerInstance.completeLoginSequence();
                                for (Consumer<Server> action : this.event.getScheduledActions()) {
                                    action.accept(server);
                                }
                            }
                        }
                    };

                    this.server.getScheduler().scheduleAsyncTask(this.preLoginEventTask);
                    this.processLogin();
                    break;
                case ProtocolInfo.RESOURCE_PACK_CLIENT_RESPONSE_PACKET:
                    ResourcePackClientResponsePacket responsePacket = (ResourcePackClientResponsePacket) packet;
                    switch (responsePacket.responseStatus) {
                        case ResourcePackClientResponsePacket.STATUS_REFUSED:
                            this.close("", "disconnectionScreen.noReason");
                            break;
                        case ResourcePackClientResponsePacket.STATUS_SEND_PACKS:
                            for (ResourcePackClientResponsePacket.Entry entry : responsePacket.packEntries) {
                                ResourcePack resourcePack = this.server.getResourcePackManager().getPackById(entry.uuid);
                                if (resourcePack == null) {
                                    this.close("", "disconnectionScreen.resourcePack");
                                    break;
                                }

                                ResourcePackDataInfoPacket dataInfoPacket = new ResourcePackDataInfoPacket();
                                dataInfoPacket.packId = resourcePack.getPackId();
                                dataInfoPacket.setPackVersion(new Version(resourcePack.getPackVersion()));
                                dataInfoPacket.maxChunkSize = server.getResourcePackManager().getMaxChunkSize(); // 102400 is default
                                dataInfoPacket.chunkCount = (int) Math.ceil(resourcePack.getPackSize() / (double) dataInfoPacket.maxChunkSize);
                                dataInfoPacket.compressedPackSize = resourcePack.getPackSize();
                                dataInfoPacket.sha256 = resourcePack.getSha256();
                                this.dataResourcePacket(dataInfoPacket);
                            }
                            break;
                        case ResourcePackClientResponsePacket.STATUS_HAVE_ALL_PACKS:
                            ResourcePackStackPacket stackPacket = new ResourcePackStackPacket();
                            stackPacket.mustAccept = this.server.getForceResources();
                            stackPacket.resourcePackStack = this.server.getResourcePackManager().getResourceStack();

                            if (this.getServer().isEnableExperimentMode() && !this.getServer().getConfig("settings.waterdogpe", false)) {
//                                stackPacket.experiments.add(
//                                        new ResourcePackStackPacket.ExperimentData("spectator_mode", true)
//                                );
                                stackPacket.experiments.add(
                                        new ResourcePackStackPacket.ExperimentData("data_driven_items", true)
                                );
//                                stackPacket.experiments.add(
//                                        new ResourcePackStackPacket.ExperimentData("data_driven_biomes", true)
//                                );
                                stackPacket.experiments.add(
                                        new ResourcePackStackPacket.ExperimentData("upcoming_creator_features", true)
                                );
//                                stackPacket.experiments.add(
//                                        new ResourcePackStackPacket.ExperimentData("gametest", true)
//                                );
                                stackPacket.experiments.add(
                                        new ResourcePackStackPacket.ExperimentData("experimental_molang_features", true)
                                );
                            }

                            this.dataResourcePacket(stackPacket);
                            break;
                        case ResourcePackClientResponsePacket.STATUS_COMPLETED:
                            this.shouldLogin = true;

                            if (this.preLoginEventTask.isFinished()) {
                                this.preLoginEventTask.onCompletion(server);
                            }
                            break;
                    }
                    break;
                case ProtocolInfo.RESOURCE_PACK_CHUNK_REQUEST_PACKET: {
                    ResourcePackChunkRequestPacket requestPacket = (ResourcePackChunkRequestPacket) packet;
                    ResourcePack resourcePack = this.server.getResourcePackManager().getPackById(requestPacket.getPackId()); // TODO: Pack version check
                    if (resourcePack == null) {
                        this.close("", "disconnectionScreen.resourcePack");
                        break;
                    }

                    int maxChunkSize = server.getResourcePackManager().getMaxChunkSize();
                    ResourcePackChunkDataPacket dataPacket = new ResourcePackChunkDataPacket();
                    dataPacket.setPackId(resourcePack.getPackId());
                    dataPacket.setPackVersion(new Version(resourcePack.getPackVersion()));
                    dataPacket.chunkIndex = requestPacket.chunkIndex;
                    dataPacket.data = resourcePack.getPackChunk(maxChunkSize * requestPacket.chunkIndex, maxChunkSize);
                    dataPacket.progress = maxChunkSize * (long) requestPacket.chunkIndex;
                    this.dataResourcePacket(dataPacket);
                    break;
                }
                case ProtocolInfo.SET_LOCAL_PLAYER_AS_INITIALIZED_PACKET:
                    if (this.locallyInitialized) {
                        break;
                    }
                    this.locallyInitialized = true;
                    PlayerLocallyInitializedEvent locallyInitializedEvent = new PlayerLocallyInitializedEvent(this);
                    this.server.getPluginManager().callEvent(locallyInitializedEvent);
                    break;
                case ProtocolInfo.PLAYER_SKIN_PACKET:
                    PlayerSkinPacket skinPacket = (PlayerSkinPacket) packet;
                    Skin skin = skinPacket.skin;

                    if (!skin.isValid()) {
                        this.getServer().getLogger().debug(username + ": PlayerSkinPacket with invalid skin");
                        break;
                    }

                    if (this.server.isForceSkinTrusted()) {
                        skin.setTrusted(true);
                    }

                    PlayerChangeSkinEvent playerChangeSkinEvent = new PlayerChangeSkinEvent(this, skin);
                    playerChangeSkinEvent.setCancelled(TimeUnit.SECONDS.toMillis(this.server.getPlayerSkinChangeCooldown()) > System.currentTimeMillis() - this.lastSkinChange);
                    this.server.getPluginManager().callEvent(playerChangeSkinEvent);
                    if (!playerChangeSkinEvent.isCancelled()) {
                        this.lastSkinChange = System.currentTimeMillis();
                        this.setSkin(skin);
                    }

                    break;
                case ProtocolInfo.PACKET_VIOLATION_WARNING_PACKET:
                    Optional<String> packetName = Arrays.stream(ProtocolInfo.class.getDeclaredFields())
                            .filter(field -> field.getType() == Byte.TYPE)
                            .filter(field -> {
                                try {
                                    return field.getByte(null) == ((PacketViolationWarningPacket) packet).packetId;
                                } catch (IllegalAccessException e) {
                                    return false;
                                }
                            }).map(Field::getName).findFirst();
                    log.warn("Violation warning from {}{}", this.getName(), packetName.map(name -> " for packet " + name).orElse("") + ": " + packet);
                    break;
                case ProtocolInfo.EMOTE_PACKET:
                    if (!this.spawned) {
                        return;
                    }
                    EmotePacket emotePacket = (EmotePacket) packet;
                    if (emotePacket.runtimeId != this.id) {
                        log.warn("{} sent EmotePacket with invalid entity id: {} != {}", this.username, emotePacket.runtimeId, this.id);
                        return;
                    }
                    for (Player viewer : this.getViewers().values()) {
                        viewer.dataPacket(emotePacket);
                    }
                    return;
                case ProtocolInfo.PLAYER_INPUT_PACKET:
                    if (!this.isAlive() || !this.spawned) {
                        break;
                    }
                    PlayerInputPacket ipk = (PlayerInputPacket) packet;
                    if (riding instanceof EntityMinecartAbstract) {
                        ((EntityMinecartAbstract) riding).setCurrentSpeed(ipk.motionY);
                    }
                    break;
                case ProtocolInfo.MOVE_PLAYER_PACKET:
                    if (this.teleportPosition != null) {
                        break;
                    }

                    MovePlayerPacket movePlayerPacket = (MovePlayerPacket) packet;
                    Vector3 newPos = new Vector3(movePlayerPacket.x, movePlayerPacket.y - this.getBaseOffset(), movePlayerPacket.z);

                    double dis = newPos.distanceSquared(this);
                    if (dis == 0 && movePlayerPacket.yaw % 360 == this.yaw && movePlayerPacket.pitch % 360 == this.pitch) {
                        break;
                    }

                    if (dis > 100) {
                        this.sendPosition(this, movePlayerPacket.yaw, movePlayerPacket.pitch, MovePlayerPacket.MODE_RESET);
                        break;
                    }

                    boolean revert = false;
                    if (!this.isAlive() || !this.spawned) {
                        revert = true;
                        this.forceMovement = new Vector3(this.x, this.y, this.z);
                    }

                    if (this.forceMovement != null && (newPos.distanceSquared(this.forceMovement) > 0.1 || revert)) {
                        this.sendPosition(this.forceMovement, movePlayerPacket.yaw, movePlayerPacket.pitch, MovePlayerPacket.MODE_RESET);
                    } else {

                        movePlayerPacket.yaw %= 360;
                        movePlayerPacket.pitch %= 360;

                        if (movePlayerPacket.yaw < 0) {
                            movePlayerPacket.yaw += 360;
                        }

                        this.setRotation(movePlayerPacket.yaw, movePlayerPacket.pitch);
                        this.newPosition = newPos;
                        this.positionChanged = true;
                        this.forceMovement = null;
                    }
                    break;
                case ProtocolInfo.PLAYER_AUTH_INPUT_PACKET:
                    PlayerAuthInputPacket authPacket = (PlayerAuthInputPacket) packet;

                    if (!authPacket.getBlockActionData().isEmpty()) {
                        for (PlayerBlockActionData action : authPacket.getBlockActionData().values()) {
                            BlockVector3 blockPos = action.getPosition();
                            BlockFace blockFace = BlockFace.fromIndex(action.getFacing());
                            if (this.lastBlockAction != null && this.lastBlockAction.getAction() == PlayerActionType.PREDICT_DESTROY_BLOCK &&
                                    action.getAction() == PlayerActionType.CONTINUE_DESTROY_BLOCK) {
                                this.onBlockBreakStart(blockPos.asVector3(), blockFace);
                            }

                            BlockVector3 lastBreakPos = this.lastBlockAction == null ? null : this.lastBlockAction.getPosition();
                            if (lastBreakPos != null && (lastBreakPos.getX() != blockPos.getX() ||
                                    lastBreakPos.getY() != blockPos.getY() || lastBreakPos.getZ() != blockPos.getZ())) {
                                this.onBlockBreakAbort(lastBreakPos.asVector3(), BlockFace.DOWN);
                                this.onBlockBreakStart(blockPos.asVector3(), blockFace);
                            }

                            switch (action.getAction()) {
                                case START_DESTROY_BLOCK:
                                    this.onBlockBreakStart(blockPos.asVector3(), blockFace);
                                    break;
                                case ABORT_DESTROY_BLOCK:
                                case STOP_DESTROY_BLOCK:
                                    this.onBlockBreakAbort(blockPos.asVector3(), blockFace);
                                    break;
                                case CONTINUE_DESTROY_BLOCK:
                                    this.onBlockBreakContinue(blockPos.asVector3(), blockFace);
                                    break;
                                case PREDICT_DESTROY_BLOCK:
                                    this.onBlockBreakAbort(blockPos.asVector3(), blockFace);
                                    this.onBlockBreakComplete(blockPos, blockFace);
                                    break;
                            }
                            this.lastBlockAction = action;
                        }
                    }

                    if (this.teleportPosition != null) {
                        break;
                    }

                    // Proper player.isPassenger() check may be needed
                    if (this.riding instanceof EntityMinecartAbstract) {
                        ((EntityMinecartAbstract) riding).setCurrentSpeed(authPacket.getMotion().getY());
                        break;
                    }

                    if (authPacket.getInputData().contains(AuthInputAction.START_SPRINTING)) {
                        PlayerToggleSprintEvent event = new PlayerToggleSprintEvent(this, true);
                        this.server.getPluginManager().callEvent(event);
                        if (event.isCancelled()) {
                            this.sendData(this);
                        } else {
                            this.setSprinting(true);
                        }
                    }
                    if (authPacket.getInputData().contains(AuthInputAction.STOP_SPRINTING)) {
                        PlayerToggleSprintEvent event = new PlayerToggleSprintEvent(this, false);
                        this.server.getPluginManager().callEvent(event);
                        if (event.isCancelled()) {
                            this.sendData(this);
                        } else {
                            this.setSprinting(false);
                        }
                    }
                    if (authPacket.getInputData().contains(AuthInputAction.START_SNEAKING)) {
                        PlayerToggleSneakEvent event = new PlayerToggleSneakEvent(this, true);
                        this.server.getPluginManager().callEvent(event);
                        if (event.isCancelled()) {
                            this.sendData(this);
                        } else {
                            this.setSneaking(true);
                        }
                    }
                    if (authPacket.getInputData().contains(AuthInputAction.STOP_SNEAKING)) {
                        PlayerToggleSneakEvent event = new PlayerToggleSneakEvent(this, false);
                        this.server.getPluginManager().callEvent(event);
                        if (event.isCancelled()) {
                            this.sendData(this);
                        } else {
                            this.setSneaking(false);
                        }
                    }
                    if (authPacket.getInputData().contains(AuthInputAction.START_JUMPING)) {
                        PlayerJumpEvent playerJumpEvent = new PlayerJumpEvent(this);
                        this.server.getPluginManager().callEvent(playerJumpEvent);
                    }
                    if (authPacket.getInputData().contains(AuthInputAction.START_SWIMMING)) {
                        var playerSwimmingEvent = new PlayerToggleSwimEvent(this, true);
                        this.server.getPluginManager().callEvent(playerSwimmingEvent);
                        if (playerSwimmingEvent.isCancelled()) {
                            this.sendData(this);
                        } else {
                            this.setSwimming(true);
                        }
                    }
                    if (authPacket.getInputData().contains(AuthInputAction.STOP_SWIMMING)) {
                        var playerSwimmingEvent = new PlayerToggleSwimEvent(this, false);
                        this.server.getPluginManager().callEvent(playerSwimmingEvent);
                        if (playerSwimmingEvent.isCancelled()) {
                            this.sendData(this);
                        } else {
                            this.setSwimming(false);
                        }
                    }
                    if (authPacket.getInputData().contains(AuthInputAction.START_GLIDING)) {
                        var playerToggleGlideEvent = new PlayerToggleGlideEvent(this, true);
                        this.server.getPluginManager().callEvent(playerToggleGlideEvent);
                        if (playerToggleGlideEvent.isCancelled()) {
                            this.sendData(this);
                        } else {
                            this.setGliding(true);
                        }
                    }
                    if (authPacket.getInputData().contains(AuthInputAction.STOP_GLIDING)) {
                        var playerToggleGlideEvent = new PlayerToggleGlideEvent(this, false);
                        this.server.getPluginManager().callEvent(playerToggleGlideEvent);
                        if (playerToggleGlideEvent.isCancelled()) {
                            this.sendData(this);
                        } else {
                            this.setGliding(false);
                        }
                    }

                    Vector3 clientPosition = authPacket.getPosition().asVector3().subtract(0, this.getEyeHeight(), 0);

                    double distSqrt = clientPosition.distanceSquared(this);
                    if (distSqrt == 0.0 && authPacket.getYaw() % 360 == this.yaw && authPacket.getPitch() % 360 == this.pitch) {
                        break;
                    }

                    if (distSqrt > 100) {
                        this.sendPosition(this, authPacket.getYaw(), authPacket.getPitch(), MovePlayerPacket.MODE_RESET);
                        break;
                    }

                    boolean revertMotion = false;
                    if (!this.isAlive() || !this.spawned) {
                        revertMotion = true;
                        this.forceMovement = new Vector3(this.x, this.y, this.z);
                    }

                    if (this.forceMovement != null && (clientPosition.distanceSquared(this.forceMovement) > 0.1 || revertMotion)) {
                        this.sendPosition(this.forceMovement, authPacket.getYaw(), authPacket.getPitch(), MovePlayerPacket.MODE_RESET);
                    } else {
                        float yaw = authPacket.getYaw() % 360;
                        float pitch = authPacket.getPitch() % 360;
                        if (yaw < 0) {
                            yaw += 360;
                        }

                        this.setRotation(yaw, pitch);
                        this.newPosition = clientPosition;
                        this.clientMovements.offer(clientPosition);
                        this.forceMovement = null;
                    }
                    break;
                /* PowerNukkit disabled to use our own boat implementation
                case ProtocolInfo.MOVE_ENTITY_ABSOLUTE_PACKET:
                    MoveEntityAbsolutePacket moveEntityAbsolutePacket = (MoveEntityAbsolutePacket) packet;
                    if (this.riding == null || this.riding.getId() != moveEntityAbsolutePacket.eid || !this.riding.isControlling(this)) {
                        break;
                    }
                    if (this.riding instanceof EntityBoat) {
                        if (this.temporalVector.setComponents(moveEntityAbsolutePacket.x, moveEntityAbsolutePacket.y, moveEntityAbsolutePacket.z).distanceSquared(this.riding) < 1000) {
                            ((EntityBoat) this.riding).onInput(moveEntityAbsolutePacket.x, moveEntityAbsolutePacket.y, moveEntityAbsolutePacket.z, moveEntityAbsolutePacket.headYaw);
                        }
                    }
                    break;
                 */
                case ProtocolInfo.MOVE_ENTITY_ABSOLUTE_PACKET: {
                    if (!this.isAlive() || !this.spawned || this.getRiding() == null) {
                        break;
                    }
                    MoveEntityAbsolutePacket movePacket = (MoveEntityAbsolutePacket) packet;
                    Entity movedEntity = getLevel().getEntity(movePacket.eid);
                    if (!(movedEntity instanceof EntityBoat)) {
                        break;
                    }

                    temporalVector.setComponents(movePacket.x, movePacket.y - ((EntityBoat) movedEntity).getBaseOffset(), movePacket.z);
                    if (!movedEntity.equals(getRiding()) || !movedEntity.isControlling(this)
                            || temporalVector.distanceSquared(movedEntity) > 10 * 10) {
                        movedEntity.addMovement(movedEntity.x, movedEntity.y, movedEntity.z, movedEntity.yaw, movedEntity.pitch, movedEntity.yaw);
                        break;
                    }

                    Location from = movedEntity.getLocation();
                    movedEntity.setPositionAndRotation(temporalVector, movePacket.headYaw, 0);
                    Location to = movedEntity.getLocation();
                    if (!from.equals(to)) {
                        this.getServer().getPluginManager().callEvent(new VehicleMoveEvent(this, from, to));
                    }
                    break;
                }
                case ProtocolInfo.REQUEST_ABILITY_PACKET:
                    RequestAbilityPacket abilityPacket = (RequestAbilityPacket) packet;

                    PlayerAbility ability = abilityPacket.ability;
                    if (ability != PlayerAbility.FLYING) {
                        this.server.getLogger().info("[" + this.getName() + "] has tried to trigger " + ability + " ability " + (abilityPacket.boolValue ? "on" : "off"));
                        return;
                    }

                    if (!server.getAllowFlight() && abilityPacket.boolValue && !this.getAdventureSettings().get(Type.ALLOW_FLIGHT)) {
                        this.kick(PlayerKickEvent.Reason.FLYING_DISABLED, "Flying is not enabled on this server");
                        break;
                    }

                    PlayerToggleFlightEvent playerToggleFlightEvent = new PlayerToggleFlightEvent(this, abilityPacket.boolValue);
                    this.server.getPluginManager().callEvent(playerToggleFlightEvent);
                    if (playerToggleFlightEvent.isCancelled()) {
                        this.getAdventureSettings().update();
                    } else {
                        this.getAdventureSettings().set(Type.FLYING, playerToggleFlightEvent.isFlying());
                    }
                    break;
                case ProtocolInfo.MOB_EQUIPMENT_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    MobEquipmentPacket mobEquipmentPacket = (MobEquipmentPacket) packet;

                    Inventory inv = this.getWindowById(mobEquipmentPacket.windowId);

                    if (inv == null) {
                        log.debug("Player {} has no open container with window ID {}", this.getName(), mobEquipmentPacket.windowId);
                        return;
                    }

                    Item item = inv.getItem(mobEquipmentPacket.hotbarSlot);

                    if (!item.equals(mobEquipmentPacket.item)) {
                        log.debug("Tried to equip {} but have {} in target slot", mobEquipmentPacket.item, item);
                        inv.sendContents(this);
                        return;
                    }

                    if (inv instanceof PlayerInventory) {
                        ((PlayerInventory) inv).equipItem(mobEquipmentPacket.hotbarSlot);
                    }

                    this.setDataFlag(Player.DATA_FLAGS, Player.DATA_FLAG_ACTION, false);

                    break;
                case ProtocolInfo.PLAYER_ACTION_PACKET:
                    PlayerActionPacket playerActionPacket = (PlayerActionPacket) packet;
                    if (!this.spawned || (!this.isAlive() && playerActionPacket.action != PlayerActionPacket.ACTION_RESPAWN && playerActionPacket.action != PlayerActionPacket.ACTION_DIMENSION_CHANGE_ACK)) {
                        break;
                    }

                    playerActionPacket.entityId = this.id;
                    Vector3 pos = new Vector3(playerActionPacket.x, playerActionPacket.y, playerActionPacket.z);
                    BlockFace face = BlockFace.fromIndex(playerActionPacket.face);

                    actionswitch:
                    switch (playerActionPacket.action) {
                        case PlayerActionPacket.ACTION_START_BREAK:
                            this.onBlockBreakStart(pos, face);
                            break;
                        case PlayerActionPacket.ACTION_ABORT_BREAK:
                        case PlayerActionPacket.ACTION_STOP_BREAK:
                            this.onBlockBreakAbort(pos, face);
                            break;
                        case PlayerActionPacket.ACTION_GET_UPDATED_BLOCK:
                            break; //TODO
                        case PlayerActionPacket.ACTION_DROP_ITEM:
                            break; //TODO
                        case PlayerActionPacket.ACTION_STOP_SLEEPING:
                            this.stopSleep();
                            break;
                        case PlayerActionPacket.ACTION_RESPAWN:
                            if (!this.spawned || this.isAlive() || !this.isOnline()) {
                                break;
                            }

                            this.respawn();
                            break;
                        case PlayerActionPacket.ACTION_JUMP:
                            PlayerJumpEvent playerJumpEvent = new PlayerJumpEvent(this);
                            this.server.getPluginManager().callEvent(playerJumpEvent);
                            break packetswitch;
                        case PlayerActionPacket.ACTION_START_SPRINT:
                            PlayerToggleSprintEvent playerToggleSprintEvent = new PlayerToggleSprintEvent(this, true);
                            this.server.getPluginManager().callEvent(playerToggleSprintEvent);
                            if (playerToggleSprintEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setSprinting(true);
                            }
                            break packetswitch;
                        case PlayerActionPacket.ACTION_STOP_SPRINT:
                            playerToggleSprintEvent = new PlayerToggleSprintEvent(this, false);
                            this.server.getPluginManager().callEvent(playerToggleSprintEvent);
                            if (playerToggleSprintEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setSprinting(false);
                            }
                            break packetswitch;
                        case PlayerActionPacket.ACTION_START_SNEAK:
                            PlayerToggleSneakEvent playerToggleSneakEvent = new PlayerToggleSneakEvent(this, true);
                            this.server.getPluginManager().callEvent(playerToggleSneakEvent);
                            if (playerToggleSneakEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setSneaking(true);
                            }
                            break packetswitch;
                        case PlayerActionPacket.ACTION_STOP_SNEAK:
                            playerToggleSneakEvent = new PlayerToggleSneakEvent(this, false);
                            this.server.getPluginManager().callEvent(playerToggleSneakEvent);
                            if (playerToggleSneakEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setSneaking(false);
                            }
                            break packetswitch;
                        case PlayerActionPacket.ACTION_CREATIVE_PLAYER_DESTROY_BLOCK:
                            this.onBlockBreakComplete(new BlockVector3(playerActionPacket.x, playerActionPacket.y, playerActionPacket.z), face);
                            break;
                        case PlayerActionPacket.ACTION_DIMENSION_CHANGE_ACK:
                            this.sendPosition(this, this.yaw, this.pitch, MovePlayerPacket.MODE_NORMAL);
                            break; //TODO
                        case PlayerActionPacket.ACTION_START_GLIDE:
                            PlayerToggleGlideEvent playerToggleGlideEvent = new PlayerToggleGlideEvent(this, true);
                            this.server.getPluginManager().callEvent(playerToggleGlideEvent);
                            if (playerToggleGlideEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setGliding(true);
                            }
                            break packetswitch;
                        case PlayerActionPacket.ACTION_STOP_GLIDE:
                            playerToggleGlideEvent = new PlayerToggleGlideEvent(this, false);
                            this.server.getPluginManager().callEvent(playerToggleGlideEvent);
                            if (playerToggleGlideEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setGliding(false);
                            }
                            break packetswitch;
                        case PlayerActionPacket.ACTION_CONTINUE_BREAK:
                            this.onBlockBreakContinue(pos, face);
                            break;
                        case PlayerActionPacket.ACTION_START_SWIMMING:
                            PlayerToggleSwimEvent ptse = new PlayerToggleSwimEvent(this, true);
                            this.server.getPluginManager().callEvent(ptse);

                            if (ptse.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setSwimming(true);
                            }
                            break;
                        case PlayerActionPacket.ACTION_STOP_SWIMMING:
                            ptse = new PlayerToggleSwimEvent(this, false);
                            this.server.getPluginManager().callEvent(ptse);

                            if (ptse.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setSwimming(false);
                            }
                            break;
                        case PlayerActionPacket.ACTION_START_SPIN_ATTACK:
                            if (this.inventory.getItemInHand().getId() != ItemID.TRIDENT) {
                                this.sendPosition(this, this.yaw, this.pitch, MovePlayerPacket.MODE_RESET);
                                break;
                            }

                            int riptideLevel = this.inventory.getItemInHand().getEnchantmentLevel(Enchantment.ID_TRIDENT_RIPTIDE);
                            if (riptideLevel < 1) {
                                this.sendPosition(this, this.yaw, this.pitch, MovePlayerPacket.MODE_RESET);
                                break;
                            }

                            if (!(this.isTouchingWater() || (this.getLevel().isRaining() && this.getLevel().canBlockSeeSky(this)))) {
                                this.sendPosition(this, this.yaw, this.pitch, MovePlayerPacket.MODE_RESET);
                                break;
                            }

                            PlayerToggleSpinAttackEvent playerToggleSpinAttackEvent = new PlayerToggleSpinAttackEvent(this, true);
                            this.server.getPluginManager().callEvent(playerToggleSpinAttackEvent);

                            if (playerToggleSpinAttackEvent.isCancelled()) {
                                this.sendPosition(this, this.yaw, this.pitch, MovePlayerPacket.MODE_RESET);
                            } else {
                                this.setSpinAttacking(true);

                                Sound riptideSound;
                                if (riptideLevel >= 3) {
                                    riptideSound = Sound.ITEM_TRIDENT_RIPTIDE_3;
                                } else if (riptideLevel == 2) {
                                    riptideSound = Sound.ITEM_TRIDENT_RIPTIDE_2;
                                } else {
                                    riptideSound = Sound.ITEM_TRIDENT_RIPTIDE_1;
                                }
                                this.level.addSound(this, riptideSound);
                            }
                            break packetswitch;
                        case PlayerActionPacket.ACTION_STOP_SPIN_ATTACK:
                            playerToggleSpinAttackEvent = new PlayerToggleSpinAttackEvent(this, false);
                            this.server.getPluginManager().callEvent(playerToggleSpinAttackEvent);

                            if (playerToggleSpinAttackEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setSpinAttacking(false);
                            }
                            break;
                    }

                    this.setUsingItem(false);
                    break;
                case ProtocolInfo.MOB_ARMOR_EQUIPMENT_PACKET:
                    break;

                case ProtocolInfo.MODAL_FORM_RESPONSE_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    ModalFormResponsePacket modalFormPacket = (ModalFormResponsePacket) packet;

                    if (formWindows.containsKey(modalFormPacket.formId)) {
                        FormWindow window = formWindows.remove(modalFormPacket.formId);
                        window.setResponse(modalFormPacket.data.trim());

                        for (FormResponseHandler handler : window.getHandlers()) {
                            handler.handle(this, modalFormPacket.formId);
                        }

                        PlayerFormRespondedEvent event = new PlayerFormRespondedEvent(this, modalFormPacket.formId, window);
                        getServer().getPluginManager().callEvent(event);
                    } else if (serverSettings.containsKey(modalFormPacket.formId)) {
                        FormWindow window = serverSettings.get(modalFormPacket.formId);
                        window.setResponse(modalFormPacket.data.trim());

                        for (FormResponseHandler handler : window.getHandlers()) {
                            handler.handle(this, modalFormPacket.formId);
                        }

                        PlayerSettingsRespondedEvent event = new PlayerSettingsRespondedEvent(this, modalFormPacket.formId, window);
                        getServer().getPluginManager().callEvent(event);

                        //Set back new settings if not been cancelled
                        if (!event.isCancelled() && window instanceof FormWindowCustom)
                            ((FormWindowCustom) window).setElementsFromResponse();
                    }

                    break;
                case ProtocolInfo.NPC_REQUEST_PACKET:
                    NPCRequestPacket npcRequestPacket = (NPCRequestPacket) packet;
                    //若sceneName字段为空，则为玩家在编辑NPC，我们并不需要记录对话框，直接通过entityRuntimeId获取实体即可
                    if (npcRequestPacket.getSceneName().isEmpty() && this.level.getEntity(npcRequestPacket.getRequestedEntityRuntimeId()) instanceof EntityNPCEntity npcEntity) {
                        FormWindowDialog dialog = npcEntity.getDialog();

                        FormResponseDialog response = new FormResponseDialog(npcRequestPacket, dialog);
                        for (FormDialogHandler handler : dialog.getHandlers()) {
                            handler.handle(this, response);
                        }

                        PlayerDialogRespondedEvent event = new PlayerDialogRespondedEvent(this, dialog, response);
                        getServer().getPluginManager().callEvent(event);
                        break;
                    }
                    if (dialogWindows.getIfPresent(npcRequestPacket.getSceneName()) != null) {
                        //remove the window from the map only if the requestType is EXECUTE_CLOSING_COMMANDS
                        FormWindowDialog dialog = null;
                        if (npcRequestPacket.getRequestType() == NPCRequestPacket.RequestType.EXECUTE_CLOSING_COMMANDS) {
                            dialog = dialogWindows.getIfPresent(npcRequestPacket.getSceneName());
                            dialogWindows.invalidate(npcRequestPacket.getSceneName());
                        } else {
                            dialog = dialogWindows.getIfPresent(npcRequestPacket.getSceneName());
                        }

                        FormResponseDialog response = new FormResponseDialog(npcRequestPacket, dialog);
                        for (FormDialogHandler handler : dialog.getHandlers()) {
                            handler.handle(this, response);
                        }

                        PlayerDialogRespondedEvent event = new PlayerDialogRespondedEvent(this, dialog, response);
                        getServer().getPluginManager().callEvent(event);

                        //close dialog after clicked button (otherwise the client will not be able to close the window)
                        if (response.getClickedButton() != null && npcRequestPacket.getRequestType() == NPCRequestPacket.RequestType.EXECUTE_ACTION) {
                            NPCDialoguePacket closeWindowPacket = new NPCDialoguePacket();
                            closeWindowPacket.setRuntimeEntityId(npcRequestPacket.getRequestedEntityRuntimeId());
                            closeWindowPacket.setSceneName(response.getSceneName());
                            closeWindowPacket.setAction(NPCDialoguePacket.NPCDialogAction.CLOSE);
                            this.dataPacket(closeWindowPacket);
                        }
                        if (response.getClickedButton() != null && response.getRequestType() == NPCRequestPacket.RequestType.EXECUTE_ACTION && response.getClickedButton().getNextDialog() != null) {
                            response.getClickedButton().getNextDialog().send(this);
                        }
                    }
                    break;
                case ProtocolInfo.INTERACT_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    InteractPacket interactPacket = (InteractPacket) packet;

                    if (interactPacket.action != InteractPacket.ACTION_MOUSEOVER || interactPacket.target != 0) {
                        this.craftingType = CRAFTING_SMALL;
                        //this.resetCraftingGridType();
                    }


                    Entity targetEntity = this.level.getEntity(interactPacket.target);

                    if (targetEntity == null || !this.isAlive() || !targetEntity.isAlive()) {
                        break;
                    }

                    if (targetEntity instanceof EntityItem || targetEntity instanceof EntityArrow || targetEntity instanceof EntityXPOrb) {
                        // 自定义实体在客户端中可以互动, 所以不踢出玩家
                        if (targetEntity instanceof CustomEntity) {
                            break;
                        }
                        this.kick(PlayerKickEvent.Reason.INVALID_PVE, "Attempting to interact with an invalid entity");
                        log.warn(this.getServer().getLanguage().translateString("nukkit.player.invalidEntity", this.getName()));
                        break;
                    }

                    item = this.inventory.getItemInHand();

                    switch (interactPacket.action) {
                        case InteractPacket.ACTION_MOUSEOVER:
                            if (interactPacket.target == 0) {
                                break packetswitch;
                            }
                            this.getServer().getPluginManager().callEvent(new PlayerMouseOverEntityEvent(this, targetEntity));
                            break;
                        case InteractPacket.ACTION_VEHICLE_EXIT:
                            if (!(targetEntity instanceof EntityRideable) || this.riding == null) {
                                break;
                            }

                            ((EntityRideable) riding).dismountEntity(this);
                            break;
                        case InteractPacket.ACTION_OPEN_INVENTORY:
                            if (targetEntity instanceof EntityRideable) {
                                if (targetEntity instanceof EntityChestBoat chestBoat) {
                                    this.addWindow(chestBoat.getInventory());
                                    break;
                                }
                                if (!(targetEntity instanceof EntityBoat || targetEntity instanceof EntityMinecartEmpty)) {
                                    break;
                                }
                            } else if (targetEntity.getId() != this.getId()) {
                                break;
                            }

                            if (!this.inventoryOpen) {
                                this.inventory.open(this);
                                this.inventoryOpen = true;
                            }

                            break;
                    }
                    break;
                case ProtocolInfo.BLOCK_PICK_REQUEST_PACKET:
                    BlockPickRequestPacket pickRequestPacket = (BlockPickRequestPacket) packet;
                    Block block = this.level.getBlock(pickRequestPacket.x, pickRequestPacket.y, pickRequestPacket.z, false);
                    if (block.distanceSquared(this) > 1000) {
                        this.getServer().getLogger().debug(username + ": Block pick request for a block too far away");
                        return;
                    }
                    item = block.toItem();

                    if (pickRequestPacket.addUserData) {
                        BlockEntity blockEntity = this.getLevel().getBlockEntity(new Vector3(pickRequestPacket.x, pickRequestPacket.y, pickRequestPacket.z));
                        if (blockEntity != null) {
                            CompoundTag nbt = blockEntity.getCleanedNBT();
                            if (nbt != null) {
                                item.setCustomBlockData(nbt);
                                item.setLore("+(DATA)");
                            }
                        }
                    }

                    PlayerBlockPickEvent pickEvent = new PlayerBlockPickEvent(this, block, item);
                    if (this.isSpectator()) {
                        log.debug("Got block-pick request from {} when in spectator mode", this.getName());
                        pickEvent.setCancelled();
                    }

                    this.server.getPluginManager().callEvent(pickEvent);

                    if (!pickEvent.isCancelled()) {
                        boolean itemExists = false;
                        int itemSlot = -1;
                        for (int slot = 0; slot < this.inventory.getSize(); slot++) {
                            if (this.inventory.getItem(slot).equals(pickEvent.getItem())) {
                                if (slot < this.inventory.getHotbarSize()) {
                                    this.inventory.setHeldItemSlot(slot);
                                } else {
                                    itemSlot = slot;
                                }
                                itemExists = true;
                                break;
                            }
                        }

                        for (int slot = 0; slot < this.inventory.getHotbarSize(); slot++) {
                            if (this.inventory.getItem(slot).isNull()) {
                                if (!itemExists && this.isCreative()) {
                                    this.inventory.setHeldItemSlot(slot);
                                    this.inventory.setItemInHand(pickEvent.getItem());
                                    break packetswitch;
                                } else if (itemSlot > -1) {
                                    this.inventory.setHeldItemSlot(slot);
                                    this.inventory.setItemInHand(this.inventory.getItem(itemSlot));
                                    this.inventory.clear(itemSlot, true);
                                    break packetswitch;
                                }
                            }
                        }

                        if (!itemExists && this.isCreative()) {
                            Item itemInHand = this.inventory.getItemInHand();
                            this.inventory.setItemInHand(pickEvent.getItem());
                            if (!this.inventory.isFull()) {
                                for (int slot = 0; slot < this.inventory.getSize(); slot++) {
                                    if (this.inventory.getItem(slot).isNull()) {
                                        this.inventory.setItem(slot, itemInHand);
                                        break;
                                    }
                                }
                            }
                        } else if (itemSlot > -1) {
                            Item itemInHand = this.inventory.getItemInHand();
                            this.inventory.setItemInHand(this.inventory.getItem(itemSlot));
                            this.inventory.setItem(itemSlot, itemInHand);
                        }
                    }
                    break;
                case ProtocolInfo.ANIMATE_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    AnimatePacket animatePacket = (AnimatePacket) packet;
                    AnimatePacket.Action animation = animatePacket.action;

                    // prevent client send illegal packet to server and broadcast to other client and make other client crash
                    if (animation == null // illegal action id
                            || animation == AnimatePacket.Action.WAKE_UP // these actions are only for server to client
                            || animation == AnimatePacket.Action.CRITICAL_HIT
                            || animation == AnimatePacket.Action.MAGIC_CRITICAL_HIT) {
                        break;
                    }

                    PlayerAnimationEvent animationEvent = new PlayerAnimationEvent(this, animatePacket);
                    this.server.getPluginManager().callEvent(animationEvent);
                    if (animationEvent.isCancelled()) {
                        break;
                    }
                    animation = animationEvent.getAnimationType();

                    switch (animation) {
                        case ROW_RIGHT:
                        case ROW_LEFT:
                            if (this.riding instanceof EntityBoat) {
                                ((EntityBoat) this.riding).onPaddle(animation, ((AnimatePacket) packet).rowingTime);
                            }
                            break;
                    }

                    if (animationEvent.getAnimationType() == AnimatePacket.Action.SWING_ARM) {
                        setNoShieldTicks(NO_SHIELD_DELAY);
                    }

                    animatePacket = new AnimatePacket();
                    animatePacket.eid = this.getId();
                    animatePacket.action = animationEvent.getAnimationType();
                    animatePacket.rowingTime = animationEvent.getRowingTime();
                    Server.broadcastPacket(this.getViewers().values(), animatePacket);
                    break;
                case ProtocolInfo.SET_HEALTH_PACKET:
                    //use UpdateAttributePacket instead
                    break;

                case ProtocolInfo.ENTITY_EVENT_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }
                    EntityEventPacket entityEventPacket = (EntityEventPacket) packet;
                    if (craftingType != CRAFTING_ANVIL && entityEventPacket.event != EntityEventPacket.ENCHANT) {
                        this.craftingType = CRAFTING_SMALL;
                        //this.resetCraftingGridType();
                    }


                    if (entityEventPacket.event == EntityEventPacket.EATING_ITEM) {
                        if (entityEventPacket.data == 0 || entityEventPacket.eid != this.id) {
                            break;
                        }

                        entityEventPacket.eid = this.id;
                        entityEventPacket.isEncoded = false;

                        this.dataPacket(entityEventPacket);
                        Server.broadcastPacket(this.getViewers().values(), entityEventPacket);
                    } else if (entityEventPacket.event == EntityEventPacket.ENCHANT) {
                        if (entityEventPacket.eid != this.id) {
                            break;
                        }

                        Inventory inventory = this.getWindowById(ANVIL_WINDOW_ID);
                        if (inventory instanceof AnvilInventory) {
                            ((AnvilInventory) inventory).setCost(-entityEventPacket.data);
                        }
                    }
                    break;
                case ProtocolInfo.COMMAND_REQUEST_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }
                    this.craftingType = CRAFTING_SMALL;
                    CommandRequestPacket commandRequestPacket = (CommandRequestPacket) packet;
                    PlayerCommandPreprocessEvent playerCommandPreprocessEvent = new PlayerCommandPreprocessEvent(this, commandRequestPacket.command);
                    this.server.getPluginManager().callEvent(playerCommandPreprocessEvent);
                    if (playerCommandPreprocessEvent.isCancelled()) {
                        break;
                    }

                    Timings.playerCommandTimer.startTiming();
                    this.server.dispatchCommand(playerCommandPreprocessEvent.getPlayer(), playerCommandPreprocessEvent.getMessage().substring(1));
                    Timings.playerCommandTimer.stopTiming();
                    break;
                case ProtocolInfo.COMMAND_BLOCK_UPDATE_PACKET:
                    CommandBlockUpdatePacket cmdpk = (CommandBlockUpdatePacket) packet;
                    if (this.isOp() && this.isCreative()) {
                        if (cmdpk.isBlock) {
                            BlockEntity blockEntity = this.level.getBlockEntity(new Vector3(cmdpk.x, cmdpk.y, cmdpk.z));
                            if (blockEntity instanceof BlockEntityCommandBlock) {
                                BlockEntityCommandBlock commandBlock = (BlockEntityCommandBlock) blockEntity;
                                Block cmdBlock = commandBlock.getLevelBlock();

                                //change commandblock type
                                switch (cmdpk.commandBlockMode) {
                                    case ICommandBlock.MODE_REPEATING:
                                        if (cmdBlock.getId() != BlockID.REPEATING_COMMAND_BLOCK) {
                                            cmdBlock = Block.get(BlockID.REPEATING_COMMAND_BLOCK, cmdBlock.getDamage());
                                            commandBlock.scheduleUpdate();
                                        }
                                        break;
                                    case ICommandBlock.MODE_CHAIN:
                                        if (cmdBlock.getId() != BlockID.CHAIN_COMMAND_BLOCK) {
                                            cmdBlock = Block.get(BlockID.CHAIN_COMMAND_BLOCK, cmdBlock.getDamage());
                                        }
                                        break;
                                    case ICommandBlock.MODE_NORMAL:
                                    default:
                                        if (cmdBlock.getId() != BlockID.COMMAND_BLOCK) {
                                            cmdBlock = Block.get(BlockID.COMMAND_BLOCK, cmdBlock.getDamage());
                                        }
                                        break;
                                }

                                boolean conditional = cmdpk.isConditional;
                                cmdBlock.setPropertyValue(BlockCommandBlock.CONDITIONAL_BIT, conditional);

                                this.level.setBlock(commandBlock, cmdBlock, true);

                                commandBlock.setCommand(cmdpk.command);
                                commandBlock.setName(cmdpk.name);
                                commandBlock.setTrackOutput(cmdpk.shouldTrackOutput);
                                commandBlock.setConditional(conditional);
                                commandBlock.setTickDelay(cmdpk.tickDelay);
                                commandBlock.setExecutingOnFirstTick(cmdpk.executingOnFirstTick);

                                //redstone mode / auto
                                boolean isRedstoneMode = cmdpk.isRedstoneMode;
                                commandBlock.setAuto(!isRedstoneMode);
                                if (!isRedstoneMode && cmdpk.commandBlockMode == ICommandBlock.MODE_NORMAL) {
                                    commandBlock.trigger();
                                }
                            }
                        }
                    }
                    break;
                case ProtocolInfo.TEXT_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    TextPacket textPacket = (TextPacket) packet;

                    if (textPacket.type == TextPacket.TYPE_CHAT) {
                        String chatMessage = textPacket.message;
                        int breakLine = chatMessage.indexOf('\n');
                        // Chat messages shouldn't contain break lines so ignore text afterwards
                        if (breakLine != -1) {
                            chatMessage = chatMessage.substring(0, breakLine);
                        }
                        this.chat(chatMessage);
                    }
                    break;
                case ProtocolInfo.CONTAINER_CLOSE_PACKET:
                    ContainerClosePacket containerClosePacket = (ContainerClosePacket) packet;
                    if (!this.spawned || containerClosePacket.windowId == ContainerIds.INVENTORY && !inventoryOpen) {
                        break;
                    }

                    if (this.windowIndex.containsKey(containerClosePacket.windowId)) {
                        this.server.getPluginManager().callEvent(new InventoryCloseEvent(this.windowIndex.get(containerClosePacket.windowId), this));
                        if (containerClosePacket.windowId == ContainerIds.INVENTORY) this.inventoryOpen = false;
                        this.closingWindowId = containerClosePacket.windowId;
                        this.removeWindow(this.windowIndex.get(containerClosePacket.windowId), true);
                        this.closingWindowId = Integer.MIN_VALUE;
                    }
                    if (containerClosePacket.windowId == -1) {
                        this.craftingType = CRAFTING_SMALL;
                        this.resetCraftingGridType();
                        this.addWindow(this.craftingGrid, ContainerIds.NONE);
                        ContainerClosePacket pk = new ContainerClosePacket();
                        pk.wasServerInitiated = false;
                        pk.windowId = -1;
                        this.dataPacket(pk);
                    }
                    break;
                case ProtocolInfo.CRAFTING_EVENT_PACKET:
                    CraftingEventPacket craftingEventPacket = (CraftingEventPacket) packet;
                    if (craftingType == CRAFTING_BIG && craftingEventPacket.type == CraftingEventPacket.TYPE_WORKBENCH
                            || craftingType == CRAFTING_SMALL && craftingEventPacket.type == CraftingEventPacket.TYPE_INVENTORY) {
                        if (craftingTransaction != null) {
                            craftingTransaction.setReadyToExecute(true);
                            if (craftingTransaction.getPrimaryOutput() == null) {
                                craftingTransaction.setPrimaryOutput(craftingEventPacket.output[0]);
                            }
                        }
                    }
                    break;
                case ProtocolInfo.BLOCK_ENTITY_DATA_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    BlockEntityDataPacket blockEntityDataPacket = (BlockEntityDataPacket) packet;
                    this.craftingType = CRAFTING_SMALL;
                    this.resetCraftingGridType();

                    pos = new Vector3(blockEntityDataPacket.x, blockEntityDataPacket.y, blockEntityDataPacket.z);
                    if (pos.distanceSquared(this) > 10000) {
                        break;
                    }

                    BlockEntity t = this.level.getBlockEntity(pos);
                    if (t instanceof BlockEntitySpawnable) {
                        CompoundTag nbt;
                        try {
                            nbt = NBTIO.read(blockEntityDataPacket.namedTag, ByteOrder.LITTLE_ENDIAN, true);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        if (!((BlockEntitySpawnable) t).updateCompoundTag(nbt, this)) {
                            ((BlockEntitySpawnable) t).spawnTo(this);
                        }
                    }
                    break;
                case ProtocolInfo.REQUEST_CHUNK_RADIUS_PACKET:
                    RequestChunkRadiusPacket requestChunkRadiusPacket = (RequestChunkRadiusPacket) packet;
                    ChunkRadiusUpdatedPacket chunkRadiusUpdatePacket = new ChunkRadiusUpdatedPacket();
                    this.chunkRadius = Math.max(3, Math.min(requestChunkRadiusPacket.radius, this.viewDistance));
                    chunkRadiusUpdatePacket.radius = this.chunkRadius;
                    this.dataPacket(chunkRadiusUpdatePacket);
                    break;
                case ProtocolInfo.SET_PLAYER_GAME_TYPE_PACKET:
                    SetPlayerGameTypePacket setPlayerGameTypePacket = (SetPlayerGameTypePacket) packet;
                    if (setPlayerGameTypePacket.gamemode != this.gamemode) {
                        if (!this.hasPermission("nukkit.command.gamemode")) {
                            SetPlayerGameTypePacket setPlayerGameTypePacket1 = new SetPlayerGameTypePacket();
                            setPlayerGameTypePacket1.gamemode = this.gamemode & 0x01;
                            this.dataPacket(setPlayerGameTypePacket1);
                            this.getAdventureSettings().update();
                            break;
                        }
                        this.setGamemode(setPlayerGameTypePacket.gamemode, true);
                        Command.broadcastCommandMessage(this, new TranslationContainer("commands.gamemode.success.self", Server.getGamemodeString(this.gamemode)));
                    }
                    break;

                // PowerNukkit Note: This packed is not being sent anymore since 1.16.210
                case ProtocolInfo.ITEM_FRAME_DROP_ITEM_PACKET:
                    ItemFrameDropItemPacket itemFrameDropItemPacket = (ItemFrameDropItemPacket) packet;
                    Vector3 vector3 = this.temporalVector.setComponents(itemFrameDropItemPacket.x, itemFrameDropItemPacket.y, itemFrameDropItemPacket.z);
                    if (vector3.distanceSquared(this) < 1000) {
                        BlockEntity itemFrame = this.level.getBlockEntity(vector3);
                        if (itemFrame instanceof BlockEntityItemFrame) {
                            ((BlockEntityItemFrame) itemFrame).dropItem(this);
                        }
                    }
                    break;


                case ProtocolInfo.LECTERN_UPDATE_PACKET:
                    LecternUpdatePacket lecternUpdatePacket = (LecternUpdatePacket) packet;
                    BlockVector3 blockPosition = lecternUpdatePacket.blockPosition;
                    this.temporalVector.setComponents(blockPosition.x, blockPosition.y, blockPosition.z);
                    if (lecternUpdatePacket.dropBook) {
                        Block blockLectern = this.getLevel().getBlock(temporalVector);
                        if (blockLectern instanceof BlockLectern) {
                            ((BlockLectern) blockLectern).dropBook(this);
                        }
                    } else {
                        BlockEntity blockEntityLectern = this.level.getBlockEntity(this.temporalVector);
                        if (blockEntityLectern instanceof BlockEntityLectern) {
                            BlockEntityLectern lectern = (BlockEntityLectern) blockEntityLectern;
                            LecternPageChangeEvent lecternPageChangeEvent = new LecternPageChangeEvent(this, lectern, lecternUpdatePacket.page);
                            this.server.getPluginManager().callEvent(lecternPageChangeEvent);
                            if (!lecternPageChangeEvent.isCancelled()) {
                                lectern.setRawPage(lecternPageChangeEvent.getNewRawPage());
                                lectern.spawnToAll();
                                Block blockLectern = lectern.getBlock();
                                if (blockLectern instanceof BlockLectern) {
                                    ((BlockLectern) blockLectern).executeRedstonePulse();
                                }
                            }
                        }
                    }
                    break;
                case ProtocolInfo.MAP_INFO_REQUEST_PACKET:
                    MapInfoRequestPacket pk = (MapInfoRequestPacket) packet;
                    Item mapItem = null;

                    for (Item item1 : this.offhandInventory.getContents().values()) {
                        if (item1 instanceof ItemMap && ((ItemMap) item1).getMapId() == pk.mapId) {
                            mapItem = item1;
                        }
                    }

                    if (mapItem == null) {
                        for (Item item1 : this.inventory.getContents().values()) {
                            if (item1 instanceof ItemMap && ((ItemMap) item1).getMapId() == pk.mapId) {
                                mapItem = item1;
                            }
                        }
                    }

                    if (mapItem == null) {
                        for (BlockEntity be : this.level.getBlockEntities().values()) {
                            if (be instanceof BlockEntityItemFrame) {
                                BlockEntityItemFrame itemFrame1 = (BlockEntityItemFrame) be;

                                if (itemFrame1.getItem() instanceof ItemMap && ((ItemMap) itemFrame1.getItem()).getMapId() == pk.mapId) {
                                    ((ItemMap) itemFrame1.getItem()).sendImage(this);
                                    break;
                                }
                            }
                        }
                    }

                    if (mapItem != null) {
                        PlayerMapInfoRequestEvent event;
                        getServer().getPluginManager().callEvent(event = new PlayerMapInfoRequestEvent(this, mapItem));

                        if (!event.isCancelled()) {
                            ItemMap map = (ItemMap) mapItem;
                            if (map.trySendImage(this)) {
                                return;
                            }
                            try {
                                BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
                                Graphics2D graphics = image.createGraphics();

                                int worldX = (this.getFloorX() / 128) << 7;
                                int worldZ = (this.getFloorZ() / 128) << 7;
                                for (int x = 0; x < 128; x++) {
                                    for (int y = 0; y < 128; y++) {
                                        graphics.setColor(new Color(this.getLevel().getMapColorAt(worldX + x, worldZ + y).getRGB()));
                                        graphics.fillRect(x, y, x + 1, y + 1);
                                    }
                                }

                                map.setImage(image);
                                map.sendImage(this);
                            } catch (Exception ex) {
                                this.getServer().getLogger().debug("There was an error while generating map image", ex);
                            }
                        }
                    }

                    break;
                case ProtocolInfo.LEVEL_SOUND_EVENT_PACKET_V1:
                case ProtocolInfo.LEVEL_SOUND_EVENT_PACKET_V2:
                case ProtocolInfo.LEVEL_SOUND_EVENT_PACKET:
                    if (!this.isSpectator()) {
                        this.level.addChunkPacket(this.getChunkX(), this.getChunkZ(), packet);
                    }
                    break;
                case ProtocolInfo.INVENTORY_TRANSACTION_PACKET:
                    if (this.isSpectator()) {
                        this.sendAllInventories();
                        break;
                    }

                    InventoryTransactionPacket transactionPacket = (InventoryTransactionPacket) packet;

                    // Nasty hack because the client won't change the right packet in survival when creating netherite stuff
                    // so we are emulating what Mojang should be sending
                    if (transactionPacket.transactionType == InventoryTransactionPacket.TYPE_MISMATCH
                            && !isCreative()
                            && (inv = getWindowById(SMITHING_WINDOW_ID)) instanceof SmithingInventory) {
                        SmithingInventory smithingInventory = (SmithingInventory) inv;
                        if (!smithingInventory.getResult().isNull()) {
                            InventoryTransactionPacket fixedPacket = new InventoryTransactionPacket();
                            fixedPacket.isRepairItemPart = true;
                            fixedPacket.actions = new NetworkInventoryAction[6];

                            Item fromIngredient = smithingInventory.getIngredient().clone();
                            Item toIngredient = fromIngredient.decrement(1);

                            Item fromEquipment = smithingInventory.getEquipment().clone();
                            Item toEquipment = fromEquipment.decrement(1);

                            Item fromResult = Item.getBlock(BlockID.AIR);
                            Item toResult = smithingInventory.getResult().clone();

                            NetworkInventoryAction action = new NetworkInventoryAction();
                            action.windowId = ContainerIds.UI;
                            action.inventorySlot = SmithingInventory.SMITHING_INGREDIENT_UI_SLOT;
                            action.oldItem = fromIngredient.clone();
                            action.newItem = toIngredient.clone();
                            fixedPacket.actions[0] = action;

                            action = new NetworkInventoryAction();
                            action.windowId = ContainerIds.UI;
                            action.inventorySlot = SmithingInventory.SMITHING_EQUIPMENT_UI_SLOT;
                            action.oldItem = fromEquipment.clone();
                            action.newItem = toEquipment.clone();
                            fixedPacket.actions[1] = action;

                            int emptyPlayerSlot = -1;
                            for (int slot = 0; slot < inventory.getSize(); slot++) {
                                if (inventory.getItem(slot).isNull()) {
                                    emptyPlayerSlot = slot;
                                    break;
                                }
                            }
                            if (emptyPlayerSlot == -1) {
                                sendAllInventories();
                                getCursorInventory().sendContents(this);
                            } else {
                                action = new NetworkInventoryAction();
                                action.windowId = ContainerIds.INVENTORY;
                                action.inventorySlot = emptyPlayerSlot; // Cursor
                                action.oldItem = Item.getBlock(BlockID.AIR);
                                action.newItem = toResult.clone();
                                fixedPacket.actions[2] = action;

                                action = new NetworkInventoryAction();
                                action.sourceType = NetworkInventoryAction.SOURCE_TODO;
                                action.windowId = NetworkInventoryAction.SOURCE_TYPE_ANVIL_RESULT;
                                action.inventorySlot = 2; // result
                                action.oldItem = toResult.clone();
                                action.newItem = fromResult.clone();
                                fixedPacket.actions[3] = action;

                                action = new NetworkInventoryAction();
                                action.sourceType = NetworkInventoryAction.SOURCE_TODO;
                                action.windowId = NetworkInventoryAction.SOURCE_TYPE_ANVIL_INPUT;
                                action.inventorySlot = 0; // equipment
                                action.oldItem = toEquipment.clone();
                                action.newItem = fromEquipment.clone();
                                fixedPacket.actions[4] = action;

                                action = new NetworkInventoryAction();
                                action.sourceType = NetworkInventoryAction.SOURCE_TODO;
                                action.windowId = NetworkInventoryAction.SOURCE_TYPE_ANVIL_MATERIAL;
                                action.inventorySlot = 1; // material
                                action.oldItem = toIngredient.clone();
                                action.newItem = fromIngredient.clone();
                                fixedPacket.actions[5] = action;

                                transactionPacket = fixedPacket;
                            }
                        }
                    }

                    List<InventoryAction> actions = new ArrayList<>();
                    for (NetworkInventoryAction networkInventoryAction : transactionPacket.actions) {
                        if (craftingType == CRAFTING_STONECUTTER && craftingTransaction != null
                                && networkInventoryAction.sourceType == NetworkInventoryAction.SOURCE_TODO) {
                            networkInventoryAction.windowId = NetworkInventoryAction.SOURCE_TYPE_CRAFTING_RESULT;
                        } else if (craftingType == CRAFTING_CARTOGRAPHY && craftingTransaction != null
                                && transactionPacket.actions.length == 2 && transactionPacket.actions[1].windowId == ContainerIds.UI
                                && networkInventoryAction.inventorySlot == 0) {
                            int slot = transactionPacket.actions[1].inventorySlot;
                            if (slot == 50) {
                                networkInventoryAction.windowId = NetworkInventoryAction.SOURCE_TYPE_CRAFTING_RESULT;
                            } else {
                                networkInventoryAction.inventorySlot = slot - 12;
                            }
                        }
                        InventoryAction a = networkInventoryAction.createInventoryAction(this);

                        if (a == null) {
                            log.debug("Unmatched inventory action from {}: {}", this.getName(), networkInventoryAction);
                            this.sendAllInventories();
                            break packetswitch;
                        }

                        actions.add(a);
                    }

                    if (transactionPacket.isCraftingPart) {
                        if (this.craftingTransaction == null) {
                            this.craftingTransaction = new CraftingTransaction(this, actions);
                        } else {
                            for (InventoryAction action : actions) {
                                this.craftingTransaction.addAction(action);
                            }
                        }

                        if (this.craftingTransaction.getPrimaryOutput() != null && (this.craftingTransaction.isReadyToExecute() || this.craftingTransaction.canExecute())) {
                            //we get the actions for this in several packets, so we can't execute it until we get the result

                            if (this.craftingTransaction.execute()) {
                                Sound sound = null;
                                switch (craftingType) {
                                    case CRAFTING_STONECUTTER:
                                        sound = Sound.BLOCK_STONECUTTER_USE;
                                        break;
                                    case CRAFTING_CARTOGRAPHY:
                                        sound = Sound.BLOCK_CARTOGRAPHY_TABLE_USE;
                                        break;
                                }

                                if (sound != null) {
                                    Collection<Player> players = level.getChunkPlayers(getChunkX(), getChunkZ()).values();
                                    players.remove(this);
                                    if (!players.isEmpty()) {
                                        level.addSound(this, sound, 1f, 1f, players);
                                    }
                                }
                            }
                            this.craftingTransaction = null;
                        }

                        return;
                    } else if (transactionPacket.isEnchantingPart) {
                        if (this.enchantTransaction == null) {
                            this.enchantTransaction = new EnchantTransaction(this, actions);
                        } else {
                            for (InventoryAction action : actions) {
                                this.enchantTransaction.addAction(action);
                            }
                        }
                        if (this.enchantTransaction.canExecute()) {
                            this.enchantTransaction.execute();
                            this.enchantTransaction = null;
                        }
                        return;
                    } else if (transactionPacket.isRepairItemPart) {
                        Sound sound = null;
                        if (GrindstoneTransaction.checkForItemPart(actions)) {
                            if (this.grindstoneTransaction == null) {
                                this.grindstoneTransaction = new GrindstoneTransaction(this, actions);
                            } else {
                                for (InventoryAction action : actions) {
                                    this.grindstoneTransaction.addAction(action);
                                }
                            }
                            if (this.grindstoneTransaction.canExecute()) {
                                try {
                                    if (this.grindstoneTransaction.execute()) {
                                        sound = Sound.BLOCK_GRINDSTONE_USE;
                                    }
                                } finally {
                                    this.grindstoneTransaction = null;
                                }
                            }
                        } else if (SmithingTransaction.checkForItemPart(actions)) {
                            if (this.smithingTransaction == null) {
                                this.smithingTransaction = new SmithingTransaction(this, actions);
                            } else {
                                for (InventoryAction action : actions) {
                                    this.smithingTransaction.addAction(action);
                                }
                            }
                            if (this.smithingTransaction.canExecute()) {
                                try {
                                    if (this.smithingTransaction.execute()) {
                                        sound = Sound.SMITHING_TABLE_USE;
                                    }
                                } finally {
                                    this.smithingTransaction = null;
                                }
                            }
                        } else {
                            if (this.repairItemTransaction == null) {
                                this.repairItemTransaction = new RepairItemTransaction(this, actions);
                            } else {
                                for (InventoryAction action : actions) {
                                    this.repairItemTransaction.addAction(action);
                                }
                            }
                            if (this.repairItemTransaction.canExecute()) {
                                try {
                                    this.repairItemTransaction.execute();
                                } finally {
                                    this.repairItemTransaction = null;
                                }
                            }
                        }

                        if (sound != null) {
                            Collection<Player> players = level.getChunkPlayers(getChunkX(), getChunkZ()).values();
                            players.remove(this);
                            if (!players.isEmpty()) {
                                level.addSound(this, sound, 1f, 1f, players);
                            }
                        }
                        return;
                    } else if (transactionPacket.isTradeItemPart) {
                        if (this.tradingTransaction == null) {
                            this.tradingTransaction = new TradingTransaction(this, actions);
                        } else {
                            for (InventoryAction action : actions) {
                                this.tradingTransaction.addAction(action);
                            }
                        }
                        if (this.tradingTransaction.canExecute()) {
                            this.tradingTransaction.execute();
                            this.tradingTransaction = null;
                        }
                        return;
                    } else if (this.craftingTransaction != null) {
                        if (craftingTransaction.checkForCraftingPart(actions)) {
                            for (InventoryAction action : actions) {
                                craftingTransaction.addAction(action);
                            }
                            return;
                        } else {
                            log.debug("Got unexpected normal inventory action with incomplete crafting transaction from {}, refusing to execute crafting", this.getName());
                            this.removeAllWindows(false);
                            this.sendAllInventories();
                            this.craftingTransaction = null;
                        }
                    } else if (this.enchantTransaction != null) {
                        if (enchantTransaction.checkForEnchantPart(actions)) {
                            for (InventoryAction action : actions) {
                                enchantTransaction.addAction(action);
                            }
                            return;
                        } else {
                            log.debug("Got unexpected normal inventory action with incomplete enchanting transaction from {}, refusing to execute enchant {}", this.getName(), transactionPacket.toString());
                            this.removeAllWindows(false);
                            this.sendAllInventories();
                            this.enchantTransaction = null;
                        }
                    } else if (this.repairItemTransaction != null) {
                        if (RepairItemTransaction.checkForRepairItemPart(actions)) {
                            for (InventoryAction action : actions) {
                                this.repairItemTransaction.addAction(action);
                            }
                            return;
                        } else {
                            log.debug("Got unexpected normal inventory action with incomplete repair item transaction from " + this.getName() + ", refusing to execute repair item " + transactionPacket.toString());
                            this.removeAllWindows(false);
                            this.sendAllInventories();
                            this.repairItemTransaction = null;
                        }
                    } else if (this.grindstoneTransaction != null) {
                        if (GrindstoneTransaction.checkForItemPart(actions)) {
                            for (InventoryAction action : actions) {
                                this.grindstoneTransaction.addAction(action);
                            }
                            return;
                        } else {
                            log.debug("Got unexpected normal inventory action with incomplete grindstone transaction from {}, refusing to execute use the grindstone {}", this.getName(), transactionPacket.toString());
                            this.removeAllWindows(false);
                            this.sendAllInventories();
                            this.grindstoneTransaction = null;
                        }
                    } else if (this.smithingTransaction != null) {
                        if (SmithingTransaction.checkForItemPart(actions)) {
                            for (InventoryAction action : actions) {
                                this.smithingTransaction.addAction(action);
                            }
                            return;
                        } else {
                            log.debug("Got unexpected normal inventory action with incomplete smithing table transaction from {}, refusing to execute use the smithing table {}", this.getName(), transactionPacket.toString());
                            this.removeAllWindows(false);
                            this.sendAllInventories();
                            this.smithingTransaction = null;
                        }
                    }

                    switch (transactionPacket.transactionType) {
                        case InventoryTransactionPacket.TYPE_NORMAL:
                            InventoryTransaction transaction = new InventoryTransaction(this, actions);

                            if (!transaction.execute()) {
                                log.debug("Failed to execute inventory transaction from {} with actions: {}", this.getName(), Arrays.toString(transactionPacket.actions));
                                break packetswitch; //oops!
                            }

                            //TODO: fix achievement for getting iron from furnace

                            break packetswitch;
                        case InventoryTransactionPacket.TYPE_MISMATCH:
                            if (transactionPacket.actions.length > 0) {
                                log.debug("Expected 0 actions for mismatch, got {}, {}", transactionPacket.actions.length, Arrays.toString(transactionPacket.actions));
                            }
                            this.sendAllInventories();

                            break packetswitch;
                        case InventoryTransactionPacket.TYPE_USE_ITEM:
                            UseItemData useItemData = (UseItemData) transactionPacket.transactionData;

                            BlockVector3 blockVector = useItemData.blockPos;
                            face = useItemData.face;

                            int type = useItemData.actionType;
                            switch (type) {
                                case InventoryTransactionPacket.USE_ITEM_ACTION_CLICK_BLOCK:
                                    // Remove if client bug is ever fixed
                                    boolean spamBug = (lastRightClickPos != null && System.currentTimeMillis() - lastRightClickTime < 100.0 && blockVector.distanceSquared(lastRightClickPos) < 0.00001);
                                    lastRightClickPos = blockVector.asVector3();
                                    lastRightClickTime = System.currentTimeMillis();
                                    if (spamBug && this.getInventory().getItemInHand().getBlockId() == BlockID.AIR) {
                                        return;
                                    }

                                    this.setDataFlag(DATA_FLAGS, DATA_FLAG_ACTION, false);

                                    if (this.canInteract(blockVector.add(0.5, 0.5, 0.5), this.isCreative() ? 13 : 7)) {
                                        if (this.isCreative()) {
                                            Item i = inventory.getItemInHand();
                                            if (this.level.useItemOn(blockVector.asVector3(), i, face, useItemData.clickPos.x, useItemData.clickPos.y, useItemData.clickPos.z, this) != null) {
                                                break packetswitch;
                                            }
                                        } else if (inventory.getItemInHand().equals(useItemData.itemInHand)) {
                                            Item i = inventory.getItemInHand();
                                            Item oldItem = i.clone();
                                            //TODO: Implement adventure mode checks
                                            if ((i = this.level.useItemOn(blockVector.asVector3(), i, face, useItemData.clickPos.x, useItemData.clickPos.y, useItemData.clickPos.z, this)) != null) {
                                                if (!i.equals(oldItem) || i.getCount() != oldItem.getCount()) {
                                                    if (oldItem.getId() == i.getId() || i.getId() == 0) {
                                                        inventory.setItemInHand(i);
                                                    } else {
                                                        logTriedToSetButHadInHand(i, oldItem);
                                                    }
                                                    inventory.sendHeldItem(this.getViewers().values());
                                                }
                                                break packetswitch;
                                            }
                                        }
                                    }

                                    inventory.sendHeldItem(this);

                                    if (blockVector.distanceSquared(this) > 10000) {
                                        break packetswitch;
                                    }

                                    Block target = this.level.getBlock(blockVector.asVector3());
                                    block = target.getSide(face);

                                    this.level.sendBlocks(new Player[]{this}, new Block[]{target, block}, UpdateBlockPacket.FLAG_NOGRAPHIC);
                                    this.level.sendBlocks(new Player[]{this}, new Block[]{target.getLevelBlockAtLayer(1), block.getLevelBlockAtLayer(1)}, UpdateBlockPacket.FLAG_NOGRAPHIC, 1);
                                    break packetswitch;
                                case InventoryTransactionPacket.USE_ITEM_ACTION_BREAK_BLOCK:
                                    if (!this.spawned || !this.isAlive()) {
                                        break packetswitch;
                                    }

                                    this.resetCraftingGridType();

                                    Item i = this.getInventory().getItemInHand();

                                    Item oldItem = i.clone();

                                    if (this.canInteract(blockVector.add(0.5, 0.5, 0.5), this.isCreative() ? 13 : 7) && (i = this.level.useBreakOn(blockVector.asVector3(), face, i, this, true)) != null) {
                                        if (this.isSurvival() || this.isAdventure()) {
                                            this.getFoodData().updateFoodExpLevel(0.005);
                                            if (!i.equals(oldItem) || i.getCount() != oldItem.getCount()) {
                                                if (oldItem.getId() == i.getId() || i.getId() == 0) {
                                                    inventory.setItemInHand(i);
                                                } else {
                                                    logTriedToSetButHadInHand(i, oldItem);
                                                }
                                                inventory.sendHeldItem(this.getViewers().values());
                                            }
                                        }
                                        break packetswitch;
                                    }

                                    inventory.sendContents(this);
                                    inventory.sendHeldItem(this);

                                    if (blockVector.distanceSquared(this) < 10000) {
                                        target = this.level.getBlock(blockVector.asVector3());
                                        this.level.sendBlocks(new Player[]{this}, new Block[]{target}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 0);

                                        BlockEntity blockEntity = this.level.getBlockEntity(blockVector.asVector3());
                                        if (blockEntity instanceof BlockEntitySpawnable) {
                                            ((BlockEntitySpawnable) blockEntity).spawnTo(this);
                                        }
                                    }

                                    break packetswitch;
                                case InventoryTransactionPacket.USE_ITEM_ACTION_CLICK_AIR:
                                    Vector3 directionVector = this.getDirectionVector();

                                    if (this.isCreative()) {
                                        item = this.inventory.getItemInHand();
                                    } else if (!this.inventory.getItemInHand().equals(useItemData.itemInHand)) {
                                        this.inventory.sendHeldItem(this);
                                        break packetswitch;
                                    } else {
                                        item = this.inventory.getItemInHand();
                                    }

                                    PlayerInteractEvent interactEvent = new PlayerInteractEvent(this, item, directionVector, face, Action.RIGHT_CLICK_AIR);

                                    this.server.getPluginManager().callEvent(interactEvent);

                                    if (interactEvent.isCancelled()) {
                                        this.inventory.sendHeldItem(this);
                                        break packetswitch;
                                    }

                                    if (item.onClickAir(this, directionVector)) {
                                        if (!this.isCreative()) {
                                            if (item.getId() == 0 || this.inventory.getItemInHand().getId() == item.getId()) {
                                                this.inventory.setItemInHand(item);
                                            } else {
                                                logTriedToSetButHadInHand(item, this.inventory.getItemInHand());
                                            }
                                        }

                                        if (!this.isUsingItem()) {
                                            this.setUsingItem(true);
                                            break packetswitch;
                                        }

                                        // Used item
                                        int ticksUsed = this.server.getTick() - this.startAction;
                                        this.setUsingItem(false);

                                        if (!item.onUse(this, ticksUsed)) {
                                            this.inventory.sendContents(this);
                                        }
                                    }

                                    break packetswitch;
                                default:
                                    //unknown
                                    break;
                            }
                            break;
                        case InventoryTransactionPacket.TYPE_USE_ITEM_ON_ENTITY:
                            UseItemOnEntityData useItemOnEntityData = (UseItemOnEntityData) transactionPacket.transactionData;

                            Entity target = this.level.getEntity(useItemOnEntityData.entityRuntimeId);
                            if (target == null) {
                                return;
                            }

                            type = useItemOnEntityData.actionType;

                            if (!useItemOnEntityData.itemInHand.equalsExact(this.inventory.getItemInHand())) {
                                this.inventory.sendHeldItem(this);
                            }

                            item = this.inventory.getItemInHand();

                            switch (type) {
                                case InventoryTransactionPacket.USE_ITEM_ON_ENTITY_ACTION_INTERACT:
                                    PlayerInteractEntityEvent playerInteractEntityEvent = new PlayerInteractEntityEvent(this, target, item, useItemOnEntityData.clickPos);
                                    if (this.isSpectator()) playerInteractEntityEvent.setCancelled();
                                    getServer().getPluginManager().callEvent(playerInteractEntityEvent);

                                    if (playerInteractEntityEvent.isCancelled()) {
                                        break;
                                    }
                                    if (!(target instanceof EntityArmorStand)) {
                                        this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(target, target.clone(), VibrationType.ENTITY_INTERACT));
                                    } else {
                                        this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(target, target.clone(), VibrationType.EQUIP));
                                    }
                                    if (target.onInteract(this, item, useItemOnEntityData.clickPos) && (this.isSurvival() || this.isAdventure())) {
                                        if (item.isTool()) {
                                            if (item.useOn(target) && item.getDamage() >= item.getMaxDurability()) {
                                                level.addSound(this, Sound.RANDOM_BREAK);
                                                item = new ItemBlock(Block.get(BlockID.AIR));
                                            }
                                        } else {
                                            if (item.count > 1) {
                                                item.count--;
                                            } else {
                                                item = new ItemBlock(Block.get(BlockID.AIR));
                                            }
                                        }

                                        if (item.getId() == 0 || this.inventory.getItemInHand().getId() == item.getId()) {
                                            this.inventory.setItemInHand(item);
                                        } else {
                                            logTriedToSetButHadInHand(item, this.inventory.getItemInHand());
                                        }
                                    }
                                    break;
                                case InventoryTransactionPacket.USE_ITEM_ON_ENTITY_ACTION_ATTACK:
                                    if (target.getId() == this.getId()) {
                                        this.kick(PlayerKickEvent.Reason.INVALID_PVP, "Attempting to attack yourself");
                                        log.warn(this.getName() + " tried to attack oneself");
                                        break;
                                    }

                                    if (!this.canInteract(target, isCreative() ? 8 : 5)) {
                                        break;
                                    } else if (target instanceof Player) {
                                        if ((((Player) target).getGamemode() & 0x01) > 0) {
                                            break;
                                        } else if (!this.server.getPropertyBoolean("pvp") || this.server.getDifficulty() == 0) {
                                            break;
                                        }
                                    }

                                    float itemDamage = item.getAttackDamage();
                                    Enchantment[] enchantments = item.getEnchantments();
                                    if (item.applyEnchantments()) {
                                        for (Enchantment enchantment : enchantments) {
                                            itemDamage += enchantment.getDamageBonus(target);
                                        }
                                    }

                                    Map<DamageModifier, Float> damage = new EnumMap<>(DamageModifier.class);
                                    damage.put(DamageModifier.BASE, itemDamage);

                                    float knockBack = 0.3f;
                                    if (item.applyEnchantments()) {
                                        Enchantment knockBackEnchantment = item.getEnchantment(Enchantment.ID_KNOCKBACK);
                                        if (knockBackEnchantment != null) {
                                            knockBack += knockBackEnchantment.getLevel() * 0.1f;
                                        }
                                    }

                                    EntityDamageByEntityEvent entityDamageByEntityEvent = new EntityDamageByEntityEvent(this, target, DamageCause.ENTITY_ATTACK, damage, knockBack, item.applyEnchantments() ? enchantments : null);

                                    entityDamageByEntityEvent.setBreakShield(item.canBreakShield());


                                    if (this.isSpectator()) entityDamageByEntityEvent.setCancelled();
                                    if ((target instanceof Player) && !this.level.getGameRules().getBoolean(GameRule.PVP)) {
                                        entityDamageByEntityEvent.setCancelled();
                                    }

                                    //保存攻击的目标在lastAttackEntity
                                    if (!entityDamageByEntityEvent.isCancelled()) {
                                        this.lastAttackEntity = entityDamageByEntityEvent.getEntity();
                                    }


                                    if (target instanceof EntityLiving) {
                                        ((EntityLiving) target).preAttack(this);
                                    }

                                    try {
                                        if (!target.attack(entityDamageByEntityEvent)) {
                                            if (item.isTool() && this.isSurvival()) {
                                                this.inventory.sendContents(this);
                                            }
                                            break;
                                        }
                                    } finally {
                                        if (target instanceof EntityLiving) {
                                            ((EntityLiving) target).postAttack(this);
                                        }
                                    }

                                    for (SideEffect sideEffect : entityDamageByEntityEvent.getSideEffects()) {
                                        sideEffect.doPostAttack(this, entityDamageByEntityEvent, target);
                                    }

                                    if (item.applyEnchantments()) {
                                        for (Enchantment enchantment : item.getEnchantments()) {
                                            enchantment.doPostAttack(this, target);
                                        }
                                    }

                                    if (item.isTool() && (this.isSurvival() || this.isAdventure())) {
                                        if (item.useOn(target) && item.getDamage() >= item.getMaxDurability()) {
                                            level.addSound(this, Sound.RANDOM_BREAK);
                                            this.inventory.setItemInHand(Item.get(0));
                                        } else {
                                            if (item.getId() == 0 || this.inventory.getItemInHand().getId() == item.getId()) {
                                                this.inventory.setItemInHand(item);
                                            } else {
                                                logTriedToSetButHadInHand(item, this.inventory.getItemInHand());
                                            }
                                        }
                                    }
                                    return;
                                default:
                                    break; //unknown
                            }

                            break;
                        case InventoryTransactionPacket.TYPE_RELEASE_ITEM:
                            if (this.isSpectator()) {
                                this.sendAllInventories();
                                break packetswitch;
                            }
                            ReleaseItemData releaseItemData = (ReleaseItemData) transactionPacket.transactionData;

                            try {
                                type = releaseItemData.actionType;
                                switch (type) {
                                    case InventoryTransactionPacket.RELEASE_ITEM_ACTION_RELEASE:
                                        if (this.isUsingItem()) {
                                            item = this.inventory.getItemInHand();

                                            int ticksUsed = this.server.getTick() - this.startAction;
                                            if (!item.onRelease(this, ticksUsed)) {
                                                this.inventory.sendContents(this);
                                            }

                                            this.setUsingItem(false);
                                        } else {
                                            this.inventory.sendContents(this);
                                        }
                                        return;
                                    case InventoryTransactionPacket.RELEASE_ITEM_ACTION_CONSUME:
                                        log.debug("Unexpected release item action consume from {}", this::getName);
                                        return;
                                    default:
                                        break;
                                }
                            } finally {
                                this.setUsingItem(false);
                            }
                            break;
                        default:
                            this.inventory.sendContents(this);
                            break;
                    }
                    break;
                case ProtocolInfo.PLAYER_HOTBAR_PACKET:
                    PlayerHotbarPacket hotbarPacket = (PlayerHotbarPacket) packet;

                    if (hotbarPacket.windowId != ContainerIds.INVENTORY) {
                        return; //In PE this should never happen
                    }

                    this.inventory.equipItem(hotbarPacket.selectedHotbarSlot);
                    break;
                case ProtocolInfo.SERVER_SETTINGS_REQUEST_PACKET:
                    PlayerServerSettingsRequestEvent settingsRequestEvent = new PlayerServerSettingsRequestEvent(this, new HashMap<>(this.serverSettings));
                    this.getServer().getPluginManager().callEvent(settingsRequestEvent);

                    if (!settingsRequestEvent.isCancelled()) {
                        settingsRequestEvent.getSettings().forEach((id, window) -> {
                            ServerSettingsResponsePacket re = new ServerSettingsResponsePacket();
                            re.formId = id;
                            re.data = window.getJSONData();
                            this.dataPacket(re);
                        });
                    }
                    break;
                case ProtocolInfo.RESPAWN_PACKET:
                    if (this.isAlive()) {
                        break;
                    }
                    RespawnPacket respawnPacket = (RespawnPacket) packet;
                    if (respawnPacket.respawnState == RespawnPacket.STATE_CLIENT_READY_TO_SPAWN) {
                        RespawnPacket respawn1 = new RespawnPacket();
                        respawn1.x = (float) this.getX();
                        respawn1.y = (float) this.getY();
                        respawn1.z = (float) this.getZ();
                        respawn1.respawnState = RespawnPacket.STATE_READY_TO_SPAWN;
                        this.dataPacket(respawn1);
                    }
                    break;
                case ProtocolInfo.BOOK_EDIT_PACKET:
                    BookEditPacket bookEditPacket = (BookEditPacket) packet;
                    Item oldBook = this.inventory.getItem(bookEditPacket.inventorySlot);
                    if (oldBook.getId() != Item.BOOK_AND_QUILL) {
                        return;
                    }

                    if (bookEditPacket.text == null || bookEditPacket.text.length() > 512) {
                        return;
                    }

                    Item newBook = oldBook.clone();
                    boolean success;
                    switch (bookEditPacket.action) {
                        case REPLACE_PAGE:
                            success = ((ItemBookAndQuill) newBook).setPageText(bookEditPacket.pageNumber, bookEditPacket.text);
                            break;
                        case ADD_PAGE:
                            success = ((ItemBookAndQuill) newBook).insertPage(bookEditPacket.pageNumber, bookEditPacket.text);
                            break;
                        case DELETE_PAGE:
                            success = ((ItemBookAndQuill) newBook).deletePage(bookEditPacket.pageNumber);
                            break;
                        case SWAP_PAGES:
                            success = ((ItemBookAndQuill) newBook).swapPages(bookEditPacket.pageNumber, bookEditPacket.secondaryPageNumber);
                            break;
                        case SIGN_BOOK:
                            if (bookEditPacket.title == null || bookEditPacket.author == null || bookEditPacket.xuid == null || bookEditPacket.title.length() > 64 || bookEditPacket.author.length() > 64 || bookEditPacket.xuid.length() > 64) {
                                this.getServer().getLogger().debug(username + ": Invalid BookEditPacket action SIGN_BOOK: title/author/xuid is too long");
                                return;
                            }
                            newBook = Item.get(Item.WRITTEN_BOOK, 0, 1, oldBook.getCompoundTag());
                            success = ((ItemBookWritten) newBook).signBook(bookEditPacket.title, bookEditPacket.author, bookEditPacket.xuid, ItemBookWritten.GENERATION_ORIGINAL);
                            break;
                        default:
                            return;
                    }

                    if (success) {
                        PlayerEditBookEvent editBookEvent = new PlayerEditBookEvent(this, oldBook, newBook, bookEditPacket.action);
                        this.server.getPluginManager().callEvent(editBookEvent);
                        if (!editBookEvent.isCancelled()) {
                            this.inventory.setItem(bookEditPacket.inventorySlot, editBookEvent.getNewBook());
                        }
                    }
                    break;
                case ProtocolInfo.FILTER_TEXT_PACKET:
                    FilterTextPacket filterTextPacket = (FilterTextPacket) packet;
                    if (filterTextPacket.text == null || filterTextPacket.text.length() > 64) {
                        this.getServer().getLogger().debug(username + ": FilterTextPacket with too long text");
                        return;
                    }
                    FilterTextPacket textResponsePacket = new FilterTextPacket();

                    if (craftingType == CRAFTING_ANVIL) {
                        AnvilInventory anvilInventory = (AnvilInventory) getWindowById(ANVIL_WINDOW_ID);
                        if (anvilInventory != null) {
                            PlayerTypingAnvilInventoryEvent playerTypingAnvilInventoryEvent = new PlayerTypingAnvilInventoryEvent(
                                    this, anvilInventory, anvilInventory.getNewItemName(), filterTextPacket.getText()
                            );
                            getServer().getPluginManager().callEvent(playerTypingAnvilInventoryEvent);
                            anvilInventory.setNewItemName(playerTypingAnvilInventoryEvent.getTypedName());
                        }
                    }

                    textResponsePacket.text = filterTextPacket.text;
                    textResponsePacket.fromServer = true;
                    this.dataPacket(textResponsePacket);
                    break;
                case ProtocolInfo.SET_DIFFICULTY_PACKET:
                    if (!this.spawned || !this.hasPermission("nukkit.command.difficulty")) {
                        return;
                    }
                    server.setDifficulty(((SetDifficultyPacket) packet).difficulty);
                    SetDifficultyPacket difficultyPacket = new SetDifficultyPacket();
                    difficultyPacket.difficulty = server.getDifficulty();
                    Server.broadcastPacket(server.getOnlinePlayers().values(), difficultyPacket);
                    Command.broadcastCommandMessage(this, new TranslationContainer("commands.difficulty.success", String.valueOf(server.getDifficulty())));
                    break;
                case ProtocolInfo.POS_TRACKING_CLIENT_REQUEST_PACKET:
                    PositionTrackingDBClientRequestPacket posTrackReq = (PositionTrackingDBClientRequestPacket) packet;
                    try {
                        PositionTracking positionTracking = this.server.getPositionTrackingService().startTracking(this, posTrackReq.getTrackingId(), true);
                        if (positionTracking != null) {
                            break;
                        }
                    } catch (IOException e) {
                        log.warn("Failed to track the trackingHandler {}", posTrackReq.getTrackingId(), e);
                    }
                    PositionTrackingDBServerBroadcastPacket notFound = new PositionTrackingDBServerBroadcastPacket();
                    notFound.setAction(PositionTrackingDBServerBroadcastPacket.Action.NOT_FOUND);
                    notFound.setTrackingId(posTrackReq.getTrackingId());
                    dataPacket(notFound);
                    break;
                case ProtocolInfo.SHOW_CREDITS_PACKET:
                    ShowCreditsPacket showCreditsPacket = (ShowCreditsPacket) packet;
                    if (showCreditsPacket.status == ShowCreditsPacket.STATUS_END_CREDITS) {
                        if (this.showingCredits) {
                            this.setShowingCredits(false);
                            this.teleport(this.getSpawn(), PlayerTeleportEvent.TeleportCause.END_PORTAL);
                        }
                    }
                    break;
                case ProtocolInfo.TICK_SYNC_PACKET:
                    TickSyncPacket tickSyncPacket = (TickSyncPacket) packet;

                    TickSyncPacket tickSyncPacketToClient = new TickSyncPacket();
                    tickSyncPacketToClient.setRequestTimestamp(tickSyncPacket.getRequestTimestamp());
                    tickSyncPacketToClient.setResponseTimestamp(this.getServer().getTick());
                    this.dataPacketImmediately(tickSyncPacketToClient);
                    break;
                case ProtocolInfo.REQUEST_PERMISSIONS_PACKET:
                    RequestPermissionsPacket requestPermissionsPacket = (RequestPermissionsPacket) packet;
                    var customPermissions = requestPermissionsPacket.parseCustomPermissions();
                    for (PlayerAbility controllableAbility : RequestPermissionsPacket.CONTROLLABLE_ABILITIES) {
                        this.adventureSettings.set(controllableAbility, customPermissions.contains(controllableAbility));
                    }
                    this.adventureSettings.setPlayerPermission(requestPermissionsPacket.permissions);
                    this.adventureSettings.update();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Sends a chat message as this player. If the message begins with a / (forward-slash) it will be treated
     * as a command.
     *
     * @param message message to send
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
                    this.server.broadcastMessage(this.getServer().getLanguage().translateString(chatEvent.getFormat(), new String[]{chatEvent.getPlayer().getDisplayName(), chatEvent.getMessage()}), chatEvent.getRecipients());
                }
            }
        }

        return true;
    }

    public boolean kick() {
        return this.kick("");
    }

    public boolean kick(String reason, boolean isAdmin) {
        return this.kick(PlayerKickEvent.Reason.UNKNOWN, reason, isAdmin);
    }

    public boolean kick(String reason) {
        return kick(PlayerKickEvent.Reason.UNKNOWN, reason);
    }

    public boolean kick(PlayerKickEvent.Reason reason) {
        return this.kick(reason, true);
    }

    public boolean kick(PlayerKickEvent.Reason reason, String reasonString) {
        return this.kick(reason, reasonString, true);
    }

    public boolean kick(PlayerKickEvent.Reason reason, boolean isAdmin) {
        return this.kick(reason, reason.toString(), isAdmin);
    }

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

    public void setViewDistance(int distance) {
        this.chunkRadius = distance;

        ChunkRadiusUpdatedPacket pk = new ChunkRadiusUpdatedPacket();
        pk.radius = distance;

        this.dataPacket(pk);
    }

    public int getViewDistance() {
        return this.chunkRadius;
    }

    @Override
    public void sendMessage(String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_RAW;
        pk.message = this.server.getLanguage().translateString(message);
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
    @Since("1.6.0.0-PNX")
    public void sendRawTextMessage(RawText text) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_OBJECT;
        pk.message = text.toRawText();
        this.dataPacket(pk);
    }

    public void sendTranslation(String message) {
        this.sendTranslation(message, EmptyArrays.EMPTY_STRINGS);
    }

    public void sendTranslation(String message, String[] parameters) {
        TextPacket pk = new TextPacket();
        if (!this.server.isLanguageForced()) {
            pk.type = TextPacket.TYPE_TRANSLATION;
            pk.message = this.server.getLanguage().translateString(message, parameters, "nukkit.");
            for (int i = 0; i < parameters.length; i++) {
                parameters[i] = this.server.getLanguage().translateString(parameters[i], parameters, "nukkit.");

            }
            pk.parameters = parameters;
        } else {
            pk.type = TextPacket.TYPE_RAW;
            pk.message = this.server.getLanguage().translateString(message, parameters);
        }
        this.dataPacket(pk);
    }

    public void sendChat(String message) {
        this.sendChat("", message);
    }

    public void sendChat(String source, String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_CHAT;
        pk.source = source;
        pk.message = this.server.getLanguage().translateString(message);
        this.dataPacket(pk);
    }

    public void sendPopup(String message) {
        this.sendPopup(message, "");
    }

    // TODO: Support Translation Parameters
    public void sendPopup(String message, String subtitle) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_POPUP;
        pk.message = message;
        this.dataPacket(pk);
    }

    public void sendTip(String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_TIP;
        pk.message = message;
        this.dataPacket(pk);
    }

    public void clearTitle() {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_CLEAR;
        this.dataPacket(pk);
    }

    /**
     * Resets both title animation times and subtitle for the next shown title
     */
    public void resetTitleSettings() {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_RESET;
        this.dataPacket(pk);
    }

    public void setSubtitle(String subtitle) {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_SUBTITLE;
        pk.text = subtitle;
        this.dataPacket(pk);
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public void setRawTextSubTitle(RawText text) {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_SUBTITLE_JSON;
        pk.text = text.toRawText();
        this.dataPacket(pk);
    }

    public void setTitleAnimationTimes(int fadein, int duration, int fadeout) {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_ANIMATION_TIMES;
        pk.fadeInTime = fadein;
        pk.stayTime = duration;
        pk.fadeOutTime = fadeout;
        this.dataPacket(pk);
    }


    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public void setRawTextTitle(RawText text) {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_TITLE_JSON;
        pk.text = text.toRawText();
        this.dataPacket(pk);
    }

    public void sendTitle(String title) {
        this.sendTitle(title, null, 20, 20, 5);
    }

    public void sendTitle(String title, String subtitle) {
        this.sendTitle(title, subtitle, 20, 20, 5);
    }

    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        this.setTitleAnimationTimes(fadeIn, stay, fadeOut);
        if (!Strings.isNullOrEmpty(subtitle)) {
            this.setSubtitle(subtitle);
        }
        // title won't send if an empty string is used.
        this.setTitle(Strings.isNullOrEmpty(title) ? " " : title);
    }

    public void sendActionBar(String title) {
        this.sendActionBar(title, 1, 0, 1);
    }

    public void sendActionBar(String title, int fadein, int duration, int fadeout) {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_ACTION_BAR;
        pk.text = title;
        pk.fadeInTime = fadein;
        pk.stayTime = duration;
        pk.fadeOutTime = fadeout;
        this.dataPacket(pk);
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public void setRawTextActionBar(RawText text) {
        this.setRawTextActionBar(text, 1, 0, 1);
    }

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

    public void close(String message) {
        this.close(message, "generic");
    }

    public void close(String message, String reason) {
        this.close(message, reason, true);
    }

    public void close(String message, String reason, boolean notify) {
        this.close(new TextContainer(message), reason, notify);
    }

    public void close(TextContainer message) {
        this.close(message, "generic");
    }

    public void close(TextContainer message, String reason) {
        this.close(message, reason, true);
    }

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
            log.info(this.getServer().getLanguage().translateString("nukkit.player.logOut",
                    TextFormat.AQUA + (this.getName() == null ? "" : this.getName()) + TextFormat.WHITE,
                    this.getAddress(),
                    String.valueOf(this.getPort()),
                    this.getServer().getLanguage().translateString(reason)));
            this.windows.clear();
            this.usedChunks.clear();
            this.loadQueue.clear();
            this.hasSpawned.clear();
            this.spawnPosition = null;

            if (this.riding instanceof EntityRideable) {
                this.riding.passengers.remove(this);
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

    @Override
    public String getName() {
        return this.username;
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

    public int getExperience() {
        return this.exp;
    }

    public int getExperienceLevel() {
        return this.expLevel;
    }

    public void addExperience(int add) {
        addExperience(add, false);
    }

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

    public static int calculateRequireExperience(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        } else if (level >= 15) {
            return 37 + (level - 15) * 5;
        } else {
            return 7 + level * 2;
        }
    }

    public void setExperience(int exp) {
        setExperience(exp, this.getExperienceLevel());
    }

    public void setExperience(int exp, int level) {
        setExperience(exp, level, false);
    }

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

    public void sendExperience() {
        sendExperience(this.getExperience());
    }

    public void sendExperience(int exp) {
        if (this.spawned) {
            float percent = ((float) exp) / calculateRequireExperience(this.getExperienceLevel());
            percent = Math.max(0f, Math.min(1f, percent));
            this.setAttribute(Attribute.getAttribute(Attribute.EXPERIENCE).setValue(percent));
        }
    }

    public void sendExperienceLevel() {
        sendExperienceLevel(this.getExperienceLevel());
    }

    public void sendExperienceLevel(int level) {
        if (this.spawned) {
            this.setAttribute(Attribute.getAttribute(Attribute.EXPERIENCE_LEVEL).setValue(level));
        }
    }

    public void setAttribute(Attribute attribute) {
        UpdateAttributesPacket pk = new UpdateAttributesPacket();
        pk.entries = new Attribute[]{attribute};
        pk.entityId = this.id;
        this.dataPacket(pk);
    }

    @Override
    public void setMovementSpeed(float speed) {
        setMovementSpeed(speed, true);
    }

    public void setMovementSpeed(float speed, boolean send) {
        super.setMovementSpeed(speed);
        if (this.spawned && send) {
            this.sendMovementSpeed(speed);
        }
    }

    @Since("1.4.0.0-PN")
    public void sendMovementSpeed(float speed) {
        Attribute attribute = Attribute.getAttribute(Attribute.MOVEMENT_SPEED).setValue(speed);
        this.setAttribute(attribute);
    }

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
     * Drops an item on the ground in front of the player. Returns if the item drop was successful.
     *
     * @param item to drop
     * @return bool if the item was dropped or if the item was null
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
     * Drops an item on the ground in front of the player. Returns the dropped item.
     *
     * @param item to drop
     * @return EntityItem if the item was dropped or null if the item was null
     */
    @Since("1.4.0.0-PN")
    @Nullable
    public EntityItem dropAndGetItem(@Nonnull Item item) {
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

    public void sendPosition(Vector3 pos) {
        this.sendPosition(pos, this.yaw);
    }

    public void sendPosition(Vector3 pos, double yaw) {
        this.sendPosition(pos, yaw, this.pitch);
    }

    public void sendPosition(Vector3 pos, double yaw, double pitch) {
        this.sendPosition(pos, yaw, pitch, MovePlayerPacket.MODE_NORMAL);
    }

    public void sendPosition(Vector3 pos, double yaw, double pitch, int mode) {
        this.sendPosition(pos, yaw, pitch, mode, null);
    }

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

        if (cause != null) {
            PlayerTeleportEvent event = new PlayerTeleportEvent(this, from, to, cause);
            this.server.getPluginManager().callEvent(event);
            if (event.isCancelled()) return false;
            to = event.getTo();
        }
        if (super.teleport(to, null)) { // null to prevent fire of duplicate EntityTeleportEvent
            this.removeAllWindows();

            this.teleportPosition = new Vector3(this.x, this.y, this.z);
            this.forceMovement = this.teleportPosition;
            this.yaw = to.yaw;
            this.pitch = to.pitch;
            this.sendPosition(this, to.yaw, to.pitch, MovePlayerPacket.MODE_TELEPORT);
            this.broadcastMovement(true);

            this.checkTeleportPosition();

            this.resetFallDistance();
            this.nextChunkOrderRun = 0;
            resetClientMovement();

            //DummyBossBar
            this.getDummyBossBars().values().forEach(DummyBossBar::reshow);
            //Weather
            this.getLevel().sendWeather(this);
            //Update time
            this.getLevel().sendTime(this);
            updateTrackingPositions(true);
            return true;
        }

        return false;
    }

    public void teleportImmediate(Location location) {
        this.teleportImmediate(location, TeleportCause.PLUGIN);
    }

    public void teleportImmediate(Location location, TeleportCause cause) {
        Location from = this.getLocation();
        if (super.teleport(location, cause)) {

            for (Inventory window : new ArrayList<>(this.windows.keySet())) {
                if (window == this.inventory) {
                    continue;
                }
                this.removeWindow(window);
            }

            if (from.getLevel().getId() != location.getLevel().getId()) { //Different level, update compass position
                SetSpawnPositionPacket pk = new SetSpawnPositionPacket();
                pk.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN;
                Position spawn = location.getLevel().getSpawnLocation();
                pk.x = spawn.getFloorX();
                pk.y = spawn.getFloorY();
                pk.z = spawn.getFloorZ();
                pk.dimension = spawn.getLevel().getDimension();
                dataPacket(pk);
            }

            this.forceMovement = new Vector3(this.x, this.y, this.z);
            this.sendPosition(this, this.yaw, this.pitch, MovePlayerPacket.MODE_RESET);

            this.resetFallDistance();
            this.orderChunks();
            this.nextChunkOrderRun = 0;
            resetClientMovement();

            //Weather
            this.getLevel().sendWeather(this);
            //Update time
            this.getLevel().sendTime(this);
            updateTrackingPositions(true);
        }
    }

    /**
     * Shows a new FormWindow to the player
     * You can find out FormWindow result by listening to PlayerFormRespondedEvent
     *
     * @param window to show
     * @return form id to use in {@link PlayerFormRespondedEvent}
     */
    public int showFormWindow(FormWindow window) {
        return showFormWindow(window, this.formWindowCount++);
    }

    /**
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

    public void showDialogWindow(FormWindowDialog dialog) {
        showDialogWindow(dialog, true);
    }

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
     * Shows a new setting page in game settings
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
     * @param text   The BossBar message
     * @param length The BossBar percentage
     * @return bossBarId  The BossBar ID, you should store it if you want to remove or update the BossBar later
     */
    @Deprecated
    public long createBossBar(String text, int length) {
        DummyBossBar bossBar = new DummyBossBar.Builder(this).text(text).length(length).build();
        return this.createBossBar(bossBar);
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

    public int getWindowId(Inventory inventory) {
        if (this.windows.containsKey(inventory)) {
            return this.windows.get(inventory);
        }

        return -1;
    }

    public Inventory getWindowById(int id) {
        return this.windowIndex.get(id);
    }

    public int addWindow(Inventory inventory) {
        return this.addWindow(inventory, null);
    }

    public int addWindow(Inventory inventory, Integer forceId) {
        return addWindow(inventory, forceId, false);
    }

    public int addWindow(Inventory inventory, Integer forceId, boolean isPermanent) {
        return addWindow(inventory, forceId, isPermanent, false);
    }

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

    public void removeWindow(Inventory inventory) {
        this.removeWindow(inventory, false);
    }

    public void sendAllInventories() {
        getCursorInventory().sendContents(this);
        for (Inventory inv : this.windows.keySet()) {
            inv.sendContents(this);

            if (inv instanceof PlayerInventory) {
                ((PlayerInventory) inv).sendArmorContents(this);
            }
        }
    }

    public PlayerUIInventory getUIInventory() {
        return playerUIInventory;
    }

    public PlayerCursorInventory getCursorInventory() {
        return this.playerUIInventory.getCursorInventory();
    }

    public CraftingGrid getCraftingGrid() {
        return this.craftingGrid;
    }

    public void setCraftingGrid(CraftingGrid grid) {
        this.craftingGrid = grid;
        this.addWindow(grid, ContainerIds.NONE);
    }

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

    public void removeAllWindows() {
        removeAllWindows(false);
    }

    public void removeAllWindows(boolean permanent) {
        for (Entry<Integer, Inventory> entry : new ArrayList<>(this.windowIndex.entrySet())) {
            if (!permanent && this.permanentWindows.contains(entry.getKey())) {
                continue;
            }
            this.removeWindow(entry.getValue());
        }
    }

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
            batch.payload = Network.deflateRaw(data, Server.getInstance().networkCompressionLevel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return batch;
    }

    public boolean isFoodEnabled() {
        return !(this.isCreative() || this.isSpectator()) && this.foodEnabled;
    }

    public void setFoodEnabled(boolean foodEnabled) {
        this.foodEnabled = foodEnabled;
    }

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

    public void setCheckMovement(boolean checkMovement) {
        this.checkMovement = checkMovement;
    }

    /**
     * @since 1.2.1.0-PN
     */
    public boolean isCheckingMovement() {
        return this.checkMovement;
    }

    public synchronized void setLocale(Locale locale) {
        this.locale.set(locale);
    }

    public synchronized Locale getLocale() {
        return this.locale.get();
    }

    @Override
    public void setSprinting(boolean value) {
        if (isSprinting() != value) {
            super.setSprinting(value);
            this.setMovementSpeed(value ? getMovementSpeed() * 1.3f : getMovementSpeed() / 1.3f);

            if (this.hasEffect(Effect.SPEED)) {
                float movementSpeed = this.getMovementSpeed();
                this.sendMovementSpeed(value ? movementSpeed * 1.3f : movementSpeed);
            }
        }
    }

    public void transfer(InetSocketAddress address) {
        String hostName = address.getAddress().getHostAddress();
        int port = address.getPort();
        TransferPacket pk = new TransferPacket();
        pk.address = hostName;
        pk.port = port;
        this.dataPacket(pk);
    }

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
        if (pickedXPOrb < tick && entity instanceof EntityXPOrb && this.boundingBox.isVectorInside(entity)) {
            EntityXPOrb xpOrb = (EntityXPOrb) entity;
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
        if (!(obj instanceof Player)) {
            return false;
        }
        Player other = (Player) obj;
        return Objects.equals(this.getUniqueId(), other.getUniqueId()) && this.getId() == other.getId();
    }

    public boolean isBreakingBlock() {
        return this.breakingBlock != null;
    }

    /**
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

        try (Timing ignored = Timings.getSendDataPacketTiming(packet)) {
            DataPacketSendEvent ev = new DataPacketSendEvent(this, packet);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            }

            if (log.isTraceEnabled() && !server.isIgnoredPacket(packet.getClass())) {
                log.trace("Immediate Outbound {}: {}", this.getName(), packet);
            }

            this.interfaz.putPacket(this, packet, false, true);
        }

        return true;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public boolean dataResourcePacket(DataPacket packet) {
        if (!this.connected) {
            return false;
        }

        try (Timing ignored = Timings.getSendDataPacketTiming(packet)) {
            DataPacketSendEvent ev = new DataPacketSendEvent(this, packet);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            }

            if (log.isTraceEnabled() && !server.isIgnoredPacket(packet.getClass())) {
                log.trace("Resource Outbound {}: {}", this.getName(), packet);
            }

            this.interfaz.putResourcePacket(this, packet);
        }

        return true;
    }

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

    @PowerNukkitXOnly
    @Since("1.19.21-r4")
    public void setItemCoolDown(int coolDown, String itemCategory) {
        var pk = new PlayerStartItemCoolDownPacket();
        pk.setCoolDownDuration(coolDown);
        pk.setItemCategory(itemCategory);
        this.dataPacket(pk);
    }

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
}
