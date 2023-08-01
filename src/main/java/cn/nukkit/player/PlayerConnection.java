package cn.nukkit.player;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.player.*;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.item.Item;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.network.protocol.*;
import cn.nukkit.network.session.NetworkPlayerSession;
import cn.nukkit.utils.Identifier;
import cn.nukkit.utils.TextFormat;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.File;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Getter
@Log4j2
public class PlayerConnection {

    private boolean playedBefore;

    protected boolean connected = true;

    @Setter
    private boolean spawned = false;

    @Setter
    private boolean loggedIn = false;

    @Setter
    private boolean locallyInitialized = false;

    protected final InetSocketAddress rawSocketAddress;
    protected InetSocketAddress socketAddress;

    @Getter
    private final Player player;

    @Getter
    private final NetworkPlayerSession networkSession;

    @Getter
    private final Server server;

    public PlayerConnection(Player player, NetworkPlayerSession networkSession, InetSocketAddress socketAddress) {
        this.player = player;
        this.networkSession = networkSession;
        this.rawSocketAddress = socketAddress;
        this.socketAddress = socketAddress;
        this.server = player.getServer();
    }

    /**
     * Processing LoginPacket
     */
    protected void processLogin() {
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
        } else if (this.server.getIPBans().isBanned(getAddress())) {
            String reason =
                    this.server.getIPBans().getEntires().get(getAddress()).getReason();
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

        if (player.getLoginChainData().isXboxAuthed() && server.getPropertyBoolean("xbox-auth")
                || !server.getPropertyBoolean("xbox-auth")) {
            server.getPlayerManager().updateName(player.getUniqueId(), player.getName());
        }

        this.playedBefore = (nbt.getLong("lastPlayed") - nbt.getLong("firstPlayed")) > 1;

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
        this.sendPacket(infoPacket);
    }

    protected void doFirstSpawn() {
        this.spawned = true;

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
        this.sendPacket(packet);

        player.sendFogStack();
        player.sendCameraPresets();
        if (player.getHealth() < 1) {
            player.setHealth(0);
        }
    }

    /**
     * Executed after successful completion of the asynchronous login task
     */
    protected void onCompleteLoginSequence() {
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
            worldSpawnPoint = this.server.getDefaultLevel().getSafeSpawn();
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
        this.sendPacketImmediately(startGamePacket);
        this.loggedIn = true;

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
        this.sendPacket(itemComponentPacket);

        this.sendPacket(new BiomeDefinitionListPacket());
        this.sendPacket(new AvailableEntityIdentifiersPacket());
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
                        this.getAddress(),
                        String.valueOf(this.getPort()),
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
    protected void onPlayerLocallyInitialized() {
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

    /**
     * Get the original address
     *
     * @return {@link String}
     */
    public String getRawAddress() {
        return rawSocketAddress.getAddress().getHostAddress();
    }

    /**
     * Get the original port
     *
     * @return int
     */
    public int getRawPort() {
        return rawSocketAddress.getPort();
    }

    /**
     * Get the original socket address
     *
     * @return {@link InetSocketAddress}
     */
    public InetSocketAddress getRawSocketAddress() {
        return rawSocketAddress;
    }

    /**
     * If waterdogpe compatibility is enabled, the address is modified to be waterdogpe compatible,
     * otherwise it is the same as {@link #rawSocketAddress}
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
     * If waterdogpe compatibility is enabled, the address is modified to be waterdogpe compatible,
     * otherwise it is the same as {@link #rawSocketAddress}
     *
     * @return {@link InetSocketAddress}
     */
    public InetSocketAddress getSocketAddress() {
        return this.socketAddress;
    }

    public int getPing() {
        return player.getSourceInterface().getNetworkLatency(player);
    }

    /**
     * Transfers a player to another server
     *
     * @param address the address
     */
    public void transfer(String address, int port) {
        TransferPacket packet = new TransferPacket();
        packet.address = address;
        packet.port = port;
        this.sendPacket(packet);
    }

    /**
     * Send data packet
     *
     * @param packet Packet to send
     * @return Packet successfully sent
     */
    public boolean sendPacket(DataPacket packet) {
        if (!this.isConnected()) {
            return false;
        }
        DataPacketSendEvent event = new DataPacketSendEvent(player, packet);
        event.call();
        if (event.isCancelled()) {
            return false;
        }
        if (log.isTraceEnabled() && !server.isIgnoredPacket(packet.getClass())) {
            log.trace("Outbound {}: {}", player.getName(), packet);
        }
        this.getNetworkSession().sendPacket(packet);
        return true;
    }

    public void sendPacketImmediately(DataPacket packet, Runnable callback) {
        this.getNetworkSession().sendImmediatePacket(packet, (callback == null ? () -> {} : callback));
    }

    public boolean sendPacketImmediately(DataPacket packet) {
        if (!this.isConnected()) {
            return false;
        }
        DataPacketSendEvent event = new DataPacketSendEvent(player, packet);
        event.call();
        if (event.isCancelled()) {
            return false;
        }
        if (log.isTraceEnabled() && !server.isIgnoredPacket(packet.getClass())) {
            log.trace("Immediate Outbound {}: {}", player.getName(), packet);
        }
        this.getNetworkSession().sendImmediatePacket(packet);
        return true;
    }

    public boolean sendResourcePacket(DataPacket packet) {
        if (!this.isConnected()) {
            return false;
        }
        DataPacketSendEvent event = new DataPacketSendEvent(player, packet);
        event.call();
        if (event.isCancelled()) {
            return false;
        }
        if (log.isTraceEnabled() && !server.isIgnoredPacket(packet.getClass())) {
            log.trace("Resource Outbound {}: {}", player.getName(), packet);
        }
        player.getSourceInterface().putResourcePacket(player, packet);
        return true;
    }
}
