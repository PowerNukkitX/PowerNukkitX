package cn.nukkit.player;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.block.customblock.CustomBlock;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.dialog.window.FormWindowDialog;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.PlayerUIInventory;
import cn.nukkit.inventory.transaction.*;
import cn.nukkit.item.Item;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.Vector3WithRuntimeId;
import cn.nukkit.level.particle.PunchBlockParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.network.SourceInterface;
import cn.nukkit.network.protocol.*;
import cn.nukkit.network.protocol.types.PlayerBlockActionData;
import cn.nukkit.network.session.NetworkPlayerSession;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.DummyBossBar;
import cn.nukkit.utils.Identifier;
import cn.nukkit.utils.TextFormat;
import com.github.benmanes.caffeine.cache.Cache;
import com.google.common.collect.BiMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.io.File;
import java.net.InetSocketAddress;
import java.util.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

/**
 * A PlayerHandle is used to access a player's protected data.
 */
@Getter
@Setter
@Log4j2
public final class PlayerHandle {

    @NotNull private final Player player;

    private boolean shouldLogin = false;
    private boolean inventoryOpen;
    private double lastRightClickTime = 0.0;
    private Vector3 lastRightClickPos = null;
    private BlockFace breakingBlockFace = null;
    private PlayerBlockActionData lastBlockAction;
    private AsyncTask preLoginEventTask = null;

    private Block breakingBlock = null;
    private long breakingBlockTime = 0;
    private double blockBreakProgress = 0;

    private boolean verified = false;

    private final Server server;
    private final NetworkPlayerSession networkSession;
    private final SourceInterface sourceInterface;
    private final PlayerConnection playerConnection;

    public PlayerHandle(@NotNull Player player) {
        this.player = player;
        this.server = player.getServer();
        this.networkSession = player.getNetworkSession();
        this.sourceInterface = player.getSourceInterface();
        this.playerConnection = player.getPlayerConnection();
    }

    public void setPlayerInfo(PlayerInfo playerInfo) {
        player.playerInfo = playerInfo;
    }

    public PlayerInfo getPlayerInfo() {
        return player.getPlayerInfo();
    }

    public void sendPlayStatus(int status) {
        player.sendPlayStatus(status);
    }

    public void sendPlayStatus(int status, boolean immediate) {
        player.sendPlayStatus(status, immediate);
    }

    public void removeWindow(Inventory inventory, boolean isResponse) {
        player.removeWindow(inventory, isResponse);
    }

    public void addDefaultWindows() {
        player.addDefaultWindows();
    }

    public void onBlock(Entity entity, EntityDamageEvent e, boolean animate) {
        player.onBlock(entity, e, animate);
    }

    public BiMap<Inventory, Integer> getWindows() {
        return player.windows;
    }

    public BiMap<Integer, Inventory> getWindowIndex() {
        return player.windowIndex;
    }

    public Set<Integer> getPermanentWindows() {
        return player.permanentWindows;
    }

    public Long2ObjectLinkedOpenHashMap<Boolean> getLoadQueue() {
        return player.loadQueue;
    }

    public Map<UUID, Player> getHiddenPlayers() {
        return player.hiddenPlayers;
    }

    public int getChunksPerTick() {
        return player.chunksPerTick;
    }

    public int getSpawnThreshold() {
        return player.spawnThreshold;
    }

    public int getWindowCnt() {
        return player.windowCnt;
    }

    public void setWindowCnt(int windowCnt) {
        player.windowCnt = windowCnt;
    }

    public int getClosingWindowId() {
        return player.closingWindowId;
    }

    public void setClosingWindowId(int closingWindowId) {
        player.closingWindowId = closingWindowId;
    }

    public int getMessageCounter() {
        return player.messageCounter;
    }

    public void setMessageCounter(int messageCounter) {
        player.messageCounter = messageCounter;
    }

    public PlayerUIInventory getPlayerUIInventory() {
        return player.getUIInventory();
    }

    public void setPlayerUIInventory(PlayerUIInventory playerUIInventory) {
        player.UIInventory = playerUIInventory;
    }

    public CraftingTransaction getCraftingTransaction() {
        return player.craftingTransaction;
    }

    public void setCraftingTransaction(CraftingTransaction craftingTransaction) {
        player.craftingTransaction = craftingTransaction;
    }

    public EnchantTransaction getEnchantTransaction() {
        return player.enchantTransaction;
    }

    public void setEnchantTransaction(EnchantTransaction enchantTransaction) {
        player.enchantTransaction = enchantTransaction;
    }

    public RepairItemTransaction getRepairItemTransaction() {
        return player.repairItemTransaction;
    }

    public void setRepairItemTransaction(RepairItemTransaction repairItemTransaction) {
        player.repairItemTransaction = repairItemTransaction;
    }

    public GrindstoneTransaction getGrindstoneTransaction() {
        return player.grindstoneTransaction;
    }

    public void setGrindstoneTransaction(GrindstoneTransaction grindstoneTransaction) {
        player.grindstoneTransaction = grindstoneTransaction;
    }

    public SmithingTransaction getSmithingTransaction() {
        return player.smithingTransaction;
    }

    public void setSmithingTransaction(SmithingTransaction smithingTransaction) {
        player.smithingTransaction = smithingTransaction;
    }

    public TradingTransaction getTradingTransaction() {
        return player.tradingTransaction;
    }

    public void setTradingTransaction(TradingTransaction tradingTransaction) {
        player.tradingTransaction = tradingTransaction;
    }

    public void setConnected(boolean connected) {
        player.getPlayerConnection().connected = connected;
    }

    public void setSocketAddress(InetSocketAddress socketAddress) {
        player.getPlayerConnection().socketAddress = socketAddress;
    }

    public String getUsername() {
        return player.username;
    }

    public void setUsername(String username) {
        player.username = username;
    }

    public String getDisplayName() {
        return player.displayName;
    }

    public void setDisplayName(String displayName) {
        player.displayName = displayName;
    }

    public int getStartAction() {
        return player.startActionTick;
    }

    public void setStartAction(int startAction) {
        player.startActionTick = startAction;
    }

    public Vector3 getSleeping() {
        return player.sleeping;
    }

    public void setSleeping(Vector3 sleeping) {
        player.sleeping = sleeping;
    }

    public int getChunkLoadCount() {
        return player.chunkLoadCount;
    }

    public void setChunkLoadCount(int chunkLoadCount) {
        player.chunkLoadCount = chunkLoadCount;
    }

    public int getNextChunkOrderRun() {
        return player.nextChunkOrderRun;
    }

    public void setNextChunkOrderRun(int nextChunkOrderRun) {
        player.nextChunkOrderRun = nextChunkOrderRun;
    }

    public Vector3 getNewPosition() {
        return player.newPosition;
    }

    public void setNewPosition(Vector3 newPosition) {
        player.newPosition = newPosition;
    }

    public int getChunkRadius() {
        return player.chunkRadius;
    }

    public void setChunkRadius(int chunkRadius) {
        player.chunkRadius = chunkRadius;
    }

    public Position getSpawnPosition() {
        return player.spawnPosition;
    }

    public void setSpawnPosition(Position spawnPosition) {
        player.spawnPosition = spawnPosition;
    }

    public Position getSpawnBlockPosition() {
        return player.spawnBlockPosition;
    }

    public void setSpawnBlockPosition(Position spawnBlockPosition) {
        player.spawnBlockPosition = spawnBlockPosition;
    }

    public void setFoodData(PlayerFood foodData) {
        player.foodData = foodData;
    }

    public void setLastEnderPearl(int lastEnderPearl) {
        player.lastEnderPearl = lastEnderPearl;
    }

    public void setLastChorusFruitTeleport(int lastChorusFruitTeleport) {
        player.lastChorusFruitTeleport = lastChorusFruitTeleport;
    }

    public int getFormWindowCount() {
        return player.formWindowCount;
    }

    public void setFormWindowCount(int formWindowCount) {
        player.formWindowCount = formWindowCount;
    }

    public Map<Integer, FormWindow> getFormWindows() {
        return player.formWindows;
    }

    public void setFormWindows(Map<Integer, FormWindow> formWindows) {
        player.formWindows = formWindows;
    }

    public Map<Integer, FormWindow> getServerSettings() {
        return player.serverSettings;
    }

    public void setServerSettings(Map<Integer, FormWindow> serverSettings) {
        player.serverSettings = serverSettings;
    }

    public Cache<String, FormWindowDialog> getDialogWindows() {
        return player.dialogWindows;
    }

    public void setDialogWindows(Cache<String, FormWindowDialog> dialogWindows) {
        player.dialogWindows = dialogWindows;
    }

    public void setDummyBossBars(Map<Long, DummyBossBar> dummyBossBars) {
        player.dummyBossBars = dummyBossBars;
    }

    public int getLastPlayerdLevelUpSoundTime() {
        return player.lastPlayerdLevelUpSoundTime;
    }

    public void setLastPlayerdLevelUpSoundTime(int lastPlayerdLevelUpSoundTime) {
        player.lastPlayerdLevelUpSoundTime = lastPlayerdLevelUpSoundTime;
    }

    public void setLastAttackEntity(Entity lastAttackEntity) {
        player.lastAttackEntity = lastAttackEntity;
    }

    public List<PlayerFogPacket.Fog> getFogStack() {
        return player.fogStack;
    }

    public void setFogStack(List<PlayerFogPacket.Fog> fogStack) {
        player.fogStack = fogStack;
    }

    public void setLastBeAttackEntity(Entity lastBeAttackEntity) {
        player.lastBeAttackEntity = lastBeAttackEntity;
    }

    public boolean isValidRespawnBlock(Block block) {
        return player.isValidRespawnBlock(block);
    }

    public void respawn() {
        player.respawn();
    }

    public void checkChunks() {
        player.checkChunks();
    }

    public void sendNextChunk() {
        player.sendNextChunk();
    }

    public void initEntity() {
        player.initEntity();
    }

    public boolean orderChunks() {
        return player.orderChunks();
    }

    public void checkGroundState(double movX, double movY, double movZ, double dx, double dy, double dz) {
        player.checkGroundState(movX, movY, movZ, dx, dy, dz);
    }

    public void checkBlockCollision() {
        player.checkBlockCollision();
    }

    public void checkNearEntities() {
        player.checkNearEntities();
    }

    public void handleMovement(Location clientPos) {
        player.handleMovement(clientPos);
    }

    public void offerMovementTask(Location newPosition) {
        player.offerMovementTask(newPosition);
    }

    public void handleLogicInMove(boolean invalidMotion, double distance) {
        player.handleLogicInMove(invalidMotion, distance);
    }

    public void resetClientMovement() {
        player.resetClientMovement();
    }

    public void revertClientMotion(Location originalPos) {
        player.revertClientMotion(originalPos);
    }

    public float getBaseOffset() {
        return player.getBaseOffset();
    }

    public static int getNoShieldDelay() {
        return Player.NO_SHIELD_DELAY;
    }

    public boolean isBreakingBlock() {
        return breakingBlock != null;
    }

    /**
     * Processing LoginPacket
     */
    public void handleLogin() {
        if (!server.isWhitelisted((player.getName()).toLowerCase())) {
            player.kick(PlayerKickEvent.Reason.NOT_WHITELISTED, "Server is white-listed");
            return;
        } else if (player.isBanned()) {
            String reason = this.server
                    .getNameBans()
                    .getEntires()
                    .get(player.getName().toLowerCase())
                    .getReason();
            player.kick(
                    PlayerKickEvent.Reason.NAME_BANNED,
                    !reason.isEmpty() ? "You are banned. Reason: " + reason : "You are banned");
            return;
        } else if (server.getIPBans().isBanned(playerConnection.getAddress())) {
            String reason = server.getIPBans()
                    .getEntires()
                    .get(playerConnection.getAddress())
                    .getReason();
            player.kick(
                    PlayerKickEvent.Reason.IP_BANNED,
                    !reason.isEmpty() ? "You are banned. Reason: " + reason : "You are banned");
            return;
        }

        if (player.hasPermission(Server.BROADCAST_CHANNEL_USERS)) {
            server.getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_USERS, player);
        }
        if (player.hasPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE)) {
            server.getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE, player);
        }

        Player oldPlayer = null;
        for (Player p :
                new ArrayList<>(server.getPlayerManager().getOnlinePlayers().values())) {
            if (p != player && p.getName() != null && p.getName().equalsIgnoreCase(player.getName())
                    || player.getUniqueId().equals(p.getUniqueId())) {
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
            File legacyDataFile = new File(
                    server.getDataPath() + "players/" + player.getName().toLowerCase() + ".dat");
            File dataFile = new File(
                    server.getDataPath() + "players/" + player.getUniqueId().toString() + ".dat");
            if (legacyDataFile.exists() && !dataFile.exists()) {
                nbt = server.getPlayerManager().getOfflinePlayerData(player.getName(), false);

                if (!legacyDataFile.delete()) {
                    log.warn("Could not delete legacy player data for {}", player.getName());
                }
            } else {
                nbt = server.getPlayerManager().getOfflinePlayerData(player.getUniqueId(), true);
            }
        }

        if (nbt == null) {
            player.close(player.getLeaveMessage(), "Invalid data");
            return;
        }

        if (player.getPlayerInfo().isXboxAuthed() && server.getPropertyBoolean("xbox-auth")
                || !server.getPropertyBoolean("xbox-auth")) {
            server.getPlayerManager().updateName(player.getUniqueId(), player.getName());
        }

        playerConnection.setPlayedBefore((nbt.getLong("lastPlayed") - nbt.getLong("firstPlayed")) > 1);

        nbt.putString("NameTag", player.getName());

        int exp = nbt.getInt("EXP");
        int expLevel = nbt.getInt("expLevel");
        player.setExperience(exp, expLevel);

        player.gamemode = GameMode.fromOrdinal(nbt.getInt("playerGameType") & 0x03);
        if (server.getForceGamemode()) {
            player.gamemode = server.getGamemode();
            nbt.putInt("playerGameType", player.getGamemode().ordinal());
        }

        player.adventureSettings = new AdventureSettings(player, nbt);

        Level level;
        if ((level = this.server.getLevelByName(nbt.getString("Level"))) == null) {
            player.setLevel(this.server.getDefaultLevel());
            nbt.putString("Level", player.getLevel().getName());
            Position spawnLocation = player.getLevel().getSafeSpawn();
            nbt.getList("Pos", DoubleTag.class)
                    .add(new DoubleTag("0", spawnLocation.x()))
                    .add(new DoubleTag("1", spawnLocation.y()))
                    .add(new DoubleTag("2", spawnLocation.z()));
        } else {
            player.setLevel(level);
        }

        for (Tag achievement : nbt.getCompound("Achievements").getAllTags()) {
            if (!(achievement instanceof ByteTag)) {
                continue;
            }

            if (((ByteTag) achievement).getData() > 0) {
                player.achievements.add(achievement.getName());
            }
        }

        nbt.putLong("lastPlayed", System.currentTimeMillis() / 1000);

        UUID uuid = player.getUniqueId();
        nbt.putLong("UUIDLeast", uuid.getLeastSignificantBits());
        nbt.putLong("UUIDMost", uuid.getMostSignificantBits());

        if (server.getAutoSave()) {
            server.getPlayerManager().saveOfflinePlayerData(String.valueOf(player.getUniqueId()), nbt, true);
        }

        player.sendPlayStatus(PlayStatusPacket.LOGIN_SUCCESS);
        server.getPlayerManager().onPlayerLogin(player);

        ListTag<DoubleTag> posList = nbt.getList("Pos", DoubleTag.class);

        player.initialize(
                player.getLevel().getChunk((int) posList.get(0).data >> 4, (int) posList.get(2).data >> 4, true), nbt);

        if (!player.namedTag.contains("foodLevel")) {
            player.namedTag.putInt("foodLevel", 20);
        }
        int foodLevel = player.namedTag.getInt("foodLevel");
        if (!player.namedTag.contains("foodSaturationLevel")) {
            player.namedTag.putFloat("foodSaturationLevel", 20);
        }
        float foodSaturationLevel = player.namedTag.getFloat("foodSaturationLevel");
        player.foodData = new PlayerFood(player, foodLevel, foodSaturationLevel);

        if (player.isSpectator()) {
            player.onGround = false;
        }

        if (!player.namedTag.contains("TimeSinceRest")) {
            player.namedTag.putInt("TimeSinceRest", 0);
        }
        player.setTimeSinceRest(player.namedTag.getInt("TimeSinceRest"));

        if (!player.namedTag.contains("HasSeenCredits")) {
            player.namedTag.putBoolean("HasSeenCredits", false);
        }
        player.setHasSeenCredits(player.namedTag.getBoolean("HasSeenCredits"));

        // The elements of the following two Lists correspond one to the other
        if (!player.namedTag.contains("fogIdentifiers")) {
            player.namedTag.putList(new ListTag<StringTag>("fogIdentifiers"));
        }
        if (!player.namedTag.contains("userProvidedFogIds")) {
            player.namedTag.putList(new ListTag<StringTag>("userProvidedFogIds"));
        }
        var fogIdentifiers = player.namedTag.getList("fogIdentifiers", StringTag.class);
        var userProvidedFogIds = player.namedTag.getList("userProvidedFogIds", StringTag.class);
        for (int i = 0; i < fogIdentifiers.size(); i++) {
            player.fogStack.add(
                    i,
                    new PlayerFogPacket.Fog(
                            Identifier.tryParse(fogIdentifiers.get(i).data), userProvidedFogIds.get(i).data));
        }

        if (!server.isCheckMovement()) {
            player.checkMovement = false;
        }

        ResourcePacksInfoPacket infoPacket = new ResourcePacksInfoPacket();
        infoPacket.resourcePackEntries = server.getResourcePackManager().getResourceStack();
        infoPacket.mustAccept = server.getForceResources();
        playerConnection.sendPacket(infoPacket);
    }

    /**
     * Executed after successful completion of the asynchronous login task
     */
    public void handleCompleteLoginSequence() {
        PlayerLoginEvent event = new PlayerLoginEvent(player, "Plugin reason");
        event.call();

        if (event.isCancelled()) {
            player.close(player.getLeaveMessage(), event.getKickMessage());
            return;
        }

        Level level = null;
        if (player.namedTag.containsString("SpawnLevel")) {
            level = server.getLevelByName(player.namedTag.getString("SpawnLevel"));
        }
        if (player.namedTag.containsString("SpawnBlockLevel")) {
            level = server.getLevelByName(player.namedTag.getString("SpawnBlockLevel"));
        }
        if (level != null) {
            if (player.namedTag.containsInt("SpawnX")
                    && player.namedTag.containsInt("SpawnY")
                    && player.namedTag.containsInt("SpawnZ")) {
                player.spawnPosition = new Position(
                        player.namedTag.getInt("SpawnX"),
                        player.namedTag.getInt("SpawnY"),
                        player.namedTag.getInt("SpawnZ"),
                        level);
            } else {
                player.spawnPosition = null;
            }
            if (player.namedTag.containsInt("SpawnBlockPositionX")
                    && player.namedTag.containsInt("SpawnBlockPositionY")
                    && player.namedTag.containsInt("SpawnBlockPositionZ")) {
                player.spawnBlockPosition = new Position(
                        player.namedTag.getInt("SpawnBlockPositionX"),
                        player.namedTag.getInt("SpawnBlockPositionY"),
                        player.namedTag.getInt("SpawnBlockPositionZ"),
                        level);
            } else {
                player.spawnBlockPosition = null;
            }
        }
        Vector3 worldSpawnPoint;
        if (player.spawnPosition == null)
            worldSpawnPoint = server.getDefaultLevel().getSafeSpawn();
        else worldSpawnPoint = player.spawnPosition;

        StartGamePacket startGamePacket = new StartGamePacket();
        startGamePacket.entityUniqueId = player.getId();
        startGamePacket.entityRuntimeId = player.getId();
        startGamePacket.playerGamemode = player.getGamemode().getNetworkGamemode();
        startGamePacket.x = (float) player.x();
        startGamePacket.y = (float)
                (player.isOnGround()
                        ? player.y() + player.getEyeHeight()
                        : player.y()); // Prevent generating on the ground to easily sink into the ground
        startGamePacket.z = (float) player.z();
        startGamePacket.yaw = (float) player.yaw();
        startGamePacket.pitch = (float) player.pitch();
        startGamePacket.seed = -1L;
        startGamePacket.dimension = (byte) (player.getLevel().getDimension() & 0xff);
        startGamePacket.worldGamemode = this.getServer().getGamemode().getNetworkGamemode();
        startGamePacket.difficulty = this.getServer().getDifficulty();
        startGamePacket.spawnX = worldSpawnPoint.getFloorX();
        startGamePacket.spawnY = worldSpawnPoint.getFloorY();
        startGamePacket.spawnZ = worldSpawnPoint.getFloorZ();
        startGamePacket.hasAchievementsDisabled = true;
        startGamePacket.dayCycleStopTime = -1;
        startGamePacket.rainLevel = 0;
        startGamePacket.lightningLevel = 0;
        startGamePacket.commandsEnabled = player.isEnableClientCommand();
        startGamePacket.gameRules = player.getLevel().getGameRules();
        startGamePacket.levelId = "";
        startGamePacket.worldName = this.getServer().getNetwork().getName();
        startGamePacket.generator = (byte) ((player.getLevel().getDimension() + 1)
                & 0xff); // 0 Old World, 1 Main World, 2 Lower World, 3 End Times
        startGamePacket.serverAuthoritativeMovement = getServer().getServerAuthoritativeMovement();
        // Write custom block data
        startGamePacket.blockProperties.addAll(Block.getCustomBlockDefinitionList());
        playerConnection.sendPacketImmediately(startGamePacket);
        playerConnection.setLoggedIn(true);

        // Write custom item data
        ItemComponentPacket itemComponentPacket = new ItemComponentPacket();
        if (this.getServer().isEnableExperimentMode()
                && !Item.getCustomItemDefinition().isEmpty()) {
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
        playerConnection.sendPacket(itemComponentPacket);

        playerConnection.sendPacket(new BiomeDefinitionListPacket());
        playerConnection.sendPacket(new AvailableEntityIdentifiersPacket());
        player.getInventory().sendCreativeContents();
        // Send a list of player permissions
        server.getPlayerManager().getOnlinePlayers().values().forEach(pl -> {
            if (pl != player) {
                pl.adventureSettings.sendAbilities(Collections.singleton(player));
            }
        });

        player.sendDefaultAttributes();
        player.sendPotionEffects(player);
        player.sendData(player);
        player.sendDefaultAttributes();
        player.setNameTagVisible(true);
        player.setNameTagAlwaysVisible(true);
        player.setCanClimb(true);

        log.info(this.getServer()
                .getLanguage()
                .tr(
                        "nukkit.player.logIn",
                        TextFormat.AQUA + player.getName() + TextFormat.WHITE,
                        playerConnection.getAddress(),
                        String.valueOf(playerConnection.getPort()),
                        String.valueOf(player.getId()),
                        player.getLevel().getName(),
                        String.valueOf(NukkitMath.round(player.x(), 4)),
                        String.valueOf(NukkitMath.round(player.y(), 4)),
                        String.valueOf(NukkitMath.round(player.z(), 4))));

        if (player.isOp() || player.hasPermission("nukkit.textcolor")) {
            player.setRemoveFormat(false);
        }

        server.getPlayerManager().addOnlinePlayer(player);
        server.getPlayerManager().onPlayerCompleteLoginSequence(player);
    }

    /**
     * Called after player client initialization is complete
     */
    public void handlePlayerLocallyInitialized() {
        /*
         We send the game mode only after the player client is initialized to solve the problem of
         incorrect sprint speed in observer mode
         Sprint speed is only correct if the observer mode is set after the player client is displayed in the game.
         Force the game mode to update to ensure that the client receives the mode update package.
        */
        player.setGamemode(player.getGamemode(), false, true);
        // The client initializes and then transmits the player to avoid dropping the
        Location pos;
        if (this.server.isSafeSpawn() && player.getGamemode().isSurvival()) {
            pos = player.getLevel().getSafeSpawn(player).getLocation();
            pos.setYaw(player.yaw());
            pos.setPitch(player.pitch());
        } else {
            pos = new Location(player.x(), player.y(), player.z(), player.yaw(), player.pitch(), player.getLevel());
        }
        PlayerRespawnEvent respawnEvent = new PlayerRespawnEvent(player, pos, true);
        respawnEvent.call();
        Position fromEvent = respawnEvent.getRespawnPosition();
        if (fromEvent instanceof Location) {
            pos = fromEvent.getLocation();
        } else {
            pos = fromEvent.getLocation();
            pos.setYaw(player.yaw());
            pos.setPitch(player.pitch());
        }
        player.teleport(pos, PlayerTeleportEvent.TeleportCause.PLAYER_SPAWN);
        player.spawnToAll();
    }

    public void handleSpawn() {
        playerConnection.setSpawned(true);

        player.getInventory().sendContents(player);
        player.getInventory().sendHeldItem(player);
        player.getInventory().sendArmorContents(player);
        player.getOffhandInventory().sendContents(player);
        player.setEnableClientCommand(true);

        SetTimePacket setTimePacket = new SetTimePacket();
        setTimePacket.time = player.getLevel().getTime();
        player.sendPacket(setTimePacket);

        player.sendPlayStatus(PlayStatusPacket.PLAYER_SPAWN);

        PlayerJoinEvent playerJoinEvent = new PlayerJoinEvent(
                player,
                new TranslationContainer(
                        TextFormat.YELLOW + "%multiplayer.player.joined", new String[] {player.getDisplayName()}));
        playerJoinEvent.call();

        if (playerJoinEvent.getJoinMessage().toString().trim().length() > 0) {
            this.server.broadcastMessage(playerJoinEvent.getJoinMessage());
        }

        player.noDamageTicks = 60;

        server.sendRecipeList(player);

        for (long index : player.usedChunks.keySet()) {
            int chunkX = Level.getHashX(index);
            int chunkZ = Level.getHashZ(index);
            for (Entity entity :
                    player.getLevel().getChunkEntities(chunkX, chunkZ).values()) {
                if (player != entity && !entity.closed && entity.isAlive()) {
                    entity.spawnTo(player);
                }
            }
        }

        int experience = player.getExperience();
        if (experience != 0) {
            player.sendExperience(experience);
        }

        int level = player.getExperienceLevel();
        if (level != 0) {
            player.sendExperienceLevel(player.getExperienceLevel());
        }

        // todo Updater

        // Weather
        player.getLevel().sendWeather(player);

        // FoodLevel
        PlayerFood food = player.getFoodData();
        if (food.getLevel() != food.getMaxLevel()) {
            food.sendFoodLevel();
        }

        var scoreboardManager = player.getServer().getScoreboardManager();
        if (scoreboardManager != null) { // in test environment sometimes the scoreboard manager is null
            scoreboardManager.onPlayerJoin(player);
        }

        // Update compass
        SetSpawnPositionPacket packet = new SetSpawnPositionPacket();
        packet.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN;
        packet.x = player.getLevel().getSpawnLocation().getFloorX();
        packet.y = player.getLevel().getSpawnLocation().getFloorY();
        packet.z = player.getLevel().getSpawnLocation().getFloorZ();
        packet.dimension = player.getLevel().getDimension();
        playerConnection.sendPacket(packet);

        player.sendFogStack();
        player.sendCameraPresets();
        if (player.getHealth() < 1) {
            player.setHealth(0);
        }
    }

    /**
     * The player begins to break the block
     */
    public void handleBlockBreakStart(Vector3 pos, BlockFace face) {
        BlockVector3 blockPos = pos.asBlockVector3();
        long currentBreak = System.currentTimeMillis();
        // HACK: Client spams multiple left clicks so we need to skip them.
        if ((player.lastBreakPosition.equals(blockPos) && (currentBreak - player.lastBreak) < 10)
                || pos.distanceSquared(player) > 100) {
            return;
        }

        Block target = player.getLevel().getBlock(pos);
        PlayerInteractEvent playerInteractEvent = new PlayerInteractEvent(
                player,
                player.getInventory().getItemInHand(),
                target,
                face,
                target.getId() == 0
                        ? PlayerInteractEvent.Action.LEFT_CLICK_AIR
                        : PlayerInteractEvent.Action.LEFT_CLICK_BLOCK);
        playerInteractEvent.call();
        if (playerInteractEvent.isCancelled()) {
            player.getInventory().sendHeldItem(player);
            player.getLevel()
                    .sendBlocks(new Player[] {player}, new Block[] {target}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 0);
            if (target.getLevelBlockAtLayer(1) instanceof BlockLiquid) {
                player.getLevel()
                        .sendBlocks(
                                new Player[] {player},
                                new Block[] {target.getLevelBlockAtLayer(1)},
                                UpdateBlockPacket.FLAG_ALL_PRIORITY,
                                1);
            }
            return;
        }

        if (target.onTouch(player, playerInteractEvent.getAction()) != 0) return;

        Block block = target.getSide(face);
        if (block.getId() == Block.FIRE || block.getId() == BlockID.SOUL_FIRE) {
            player.getLevel().setBlock(block, Block.get(BlockID.AIR), true);
            player.getLevel().addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_EXTINGUISH_FIRE);
            return;
        }

        if (block.getId() == BlockID.SWEET_BERRY_BUSH && block.getDamage() == 0) {
            Item oldItem = playerInteractEvent.getItem();
            Item i = player.getLevel().useBreakOn(block, oldItem, player, true);
            if (player.isSurvival() || player.isAdventure()) {
                player.getFoodData().updateFoodExpLevel(0.005);
                if (!i.equals(oldItem) || i.getCount() != oldItem.getCount()) {
                    player.getInventory().setItemInHand(i);
                    player.getInventory().sendHeldItem(player.getViewers().values());
                }
            }
            return;
        }

        if (!block.isBlockChangeAllowed(player)) {
            return;
        }

        if (player.isSurvival()) {
            this.breakingBlockTime = currentBreak;
            double miningTimeRequired;
            if (target instanceof CustomBlock customBlock) {
                miningTimeRequired = customBlock.breakTime(player.getInventory().getItemInHand(), player);
            } else
                miningTimeRequired =
                        target.calculateBreakTime(player.getInventory().getItemInHand(), player);
            int breakTime = (int) Math.ceil(miningTimeRequired * 20);
            if (breakTime > 0) {
                LevelEventPacket pk = new LevelEventPacket();
                pk.evid = LevelEventPacket.EVENT_BLOCK_START_BREAK;
                pk.x = (float) pos.x();
                pk.y = (float) pos.y();
                pk.z = (float) pos.z();
                pk.data = 65535 / breakTime;
                player.getLevel().addChunkPacket(pos.getFloorX() >> 4, pos.getFloorZ() >> 4, pk);
                // Optimize the player's digging experience during anti-mining penetration
                if (player.getLevel().isAntiXrayEnabled() && player.getLevel().isPreDeObfuscate()) {
                    var vecList = new ArrayList<Vector3WithRuntimeId>(5);
                    Vector3WithRuntimeId tmpVec;
                    for (var each : BlockFace.values()) {
                        if (each == face) continue;
                        var tmpX = target.getFloorX() + each.getXOffset();
                        var tmpY = target.getFloorY() + each.getYOffset();
                        var tmpZ = target.getFloorZ() + each.getZOffset();
                        try {
                            tmpVec = new Vector3WithRuntimeId(
                                    tmpX,
                                    tmpY,
                                    tmpZ,
                                    player.getLevel().getBlockRuntimeId(tmpX, tmpY, tmpZ, 0),
                                    player.getLevel().getBlockRuntimeId(tmpX, tmpY, tmpZ, 1));
                            if (player.getLevel()
                                    .getRawFakeOreToPutRuntimeIdMap()
                                    .containsKey(tmpVec.getRuntimeIdLayer0())) {
                                vecList.add(tmpVec);
                            }
                        } catch (Exception ignore) {
                        }
                    }
                    player.getLevel()
                            .sendBlocks(
                                    new Player[] {player}, vecList.toArray(Vector3[]::new), UpdateBlockPacket.FLAG_ALL);
                }
            }
        }

        this.breakingBlock = target;
        this.breakingBlockFace = face;
        player.lastBreak = currentBreak;
        player.lastBreakPosition = blockPos;
    }

    /**
     * The player stopped breaking the block
     */
    public void handleBlockBreakAbort(Vector3 pos, BlockFace face) {
        if (pos.distanceSquared(player) < 100) { // same as with ACTION_START_BREAK
            LevelEventPacket pk = new LevelEventPacket();
            pk.evid = LevelEventPacket.EVENT_BLOCK_STOP_BREAK;
            pk.x = (float) pos.x();
            pk.y = (float) pos.y();
            pk.z = (float) pos.z();
            pk.data = 0;
            player.getLevel().addChunkPacket(pos.getFloorX() >> 4, pos.getFloorZ() >> 4, pk);
        }
        this.blockBreakProgress = 0;
        this.breakingBlock = null;
        this.breakingBlockFace = null;
    }

    /**
     * The player broke the block
     */
    public void handleBlockBreakComplete(BlockVector3 blockPos, BlockFace face) {
        if (!player.isSpawned() || !player.isAlive()) {
            return;
        }

        player.resetCraftingGridType();

        Item handItem = player.getInventory().getItemInHand();
        Item clone = handItem.clone();

        boolean canInteract = player.canInteract(blockPos.add(0.5, 0.5, 0.5), player.isCreative() ? 13 : 7);
        if (canInteract) {
            handItem = player.getLevel().useBreakOn(blockPos.asVector3(), face, handItem, player, true);
            if (handItem != null && player.isSurvival()) {
                player.getFoodData().updateFoodExpLevel(0.005);
                if (handItem.equals(clone) && handItem.getCount() == clone.getCount()) {
                    return;
                }

                if (clone.getId() == handItem.getId() || handItem.getId() == 0) {
                    player.getInventory().setItemInHand(handItem);
                } else {
                    server.getLogger()
                            .debug("Tried to set item " + handItem.getId() + " but " + player.getName() + " had item "
                                    + clone.getId() + " in their hand slot");
                }
                player.getInventory().sendHeldItem(player.getViewers().values());
            } else if (handItem == null)
                player.getLevel()
                        .sendBlocks(
                                new Player[] {player},
                                new Block[] {player.getLevel().getBlock(blockPos.asVector3())},
                                UpdateBlockPacket.FLAG_ALL_PRIORITY,
                                0);
            return;
        }

        player.getInventory().sendContents(player);
        player.getInventory().sendHeldItem(player);

        if (blockPos.distanceSquared(player) < 100) {
            Block target = player.getLevel().getBlock(blockPos.asVector3());
            player.getLevel()
                    .sendBlocks(new Player[] {player}, new Block[] {target}, UpdateBlockPacket.FLAG_ALL_PRIORITY);

            BlockEntity blockEntity = player.getLevel().getBlockEntity(blockPos.asVector3());
            if (blockEntity instanceof BlockEntitySpawnable) {
                ((BlockEntitySpawnable) blockEntity).spawnTo(player);
            }
        }
    }

    /**
     * The player continues to break the block
     */
    public void handleBlockBreakContinue(Vector3 pos, BlockFace face) {
        if (this.isBreakingBlock()) {
            var time = System.currentTimeMillis();
            Block block = player.getLevel().getBlock(pos, false);

            double miningTimeRequired;
            if (breakingBlock instanceof CustomBlock customBlock) {
                miningTimeRequired = customBlock.breakTime(player.getInventory().getItemInHand(), player);
            } else {
                miningTimeRequired =
                        breakingBlock.calculateBreakTime(player.getInventory().getItemInHand(), player);
            }

            if (miningTimeRequired > 0) {
                int breakTick = (int) Math.ceil(miningTimeRequired * 20);
                LevelEventPacket pk = new LevelEventPacket();
                pk.evid = LevelEventPacket.EVENT_BLOCK_UPDATE_BREAK;
                pk.x = (float) breakingBlock.x();
                pk.y = (float) breakingBlock.y();
                pk.z = (float) breakingBlock.z();
                pk.data = 65535 / breakTick;
                player.getLevel().addChunkPacket(breakingBlock.getFloorX() >> 4, breakingBlock.getFloorZ() >> 4, pk);
                player.getLevel().addParticle(new PunchBlockParticle(pos, block, face));
                // miningTimeRequired * 1000-101 This algorithm best matches the original computational speed, we don't
                // want any cube destruction processing
                // to be performed by the server, only custom cubes are processed in order to bypass the original fixed
                // mining time limitation
                if (breakingBlock instanceof CustomBlock) {
                    var timeDiff = time - this.breakingBlockTime;
                    this.blockBreakProgress += timeDiff / (miningTimeRequired * 1000 - 101);
                    if (this.blockBreakProgress > 0.99) {
                        this.handleBlockBreakAbort(pos, face);
                        this.handleBlockBreakComplete(pos.asBlockVector3(), face);
                    }
                    this.breakingBlockTime = time;
                }
            }
        }
    }
}
