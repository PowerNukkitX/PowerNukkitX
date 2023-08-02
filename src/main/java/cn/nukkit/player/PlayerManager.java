package cn.nukkit.player;

import cn.nukkit.Server;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.server.PlayerDataSerializeEvent;
import cn.nukkit.level.Position;
import cn.nukkit.metadata.PlayerMetadataStore;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.PlayerListPacket;
import cn.nukkit.player.serializer.PlayerDataSerializer;
import cn.nukkit.scheduler.Task;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.iq80.leveldb.DB;

@Log4j2
public class PlayerManager {
    private final Server server;

    public PlayerManager(Server server) {
        this.server = server;
    }

    @Getter
    @Setter
    private int maxPlayers;

    @Getter
    @Setter
    private DB nameLookup;

    @Getter
    private PlayerMetadataStore playerMetadata = new PlayerMetadataStore();

    private final Set<UUID> uniquePlayers = new HashSet<>();

    @Getter
    private final Map<InetSocketAddress, Player> players = new HashMap<>();

    private final Map<UUID, Player> playerList = new HashMap<>();

    private PlayerDataSerializer playerDataSerializer;

    public void onPlayerCompleteLoginSequence(Player player) {
        this.sendFullPlayerListData(player);
    }

    public void onPlayerLogin(Player player) {
        if (server.sendUsageTicker > 0) {
            this.uniquePlayers.add(player.getUniqueId());
        }
    }

    /**
     * Add new Player
     */
    public void addPlayer(InetSocketAddress socketAddress, Player player) {
        this.players.put(socketAddress, player);
    }

    /**
     * Add online Player
     */
    public void addOnlinePlayer(Player player) {
        this.playerList.put(player.getUniqueId(), player);
        this.updatePlayerListData(
                player.getUniqueId(),
                player.getId(),
                player.getDisplayName(),
                player.getSkin(),
                player.getPlayerInfo().getXuid());
    }

    /**
     * Remove player
     */
    public void removePlayer(Player player) {
        Player toRemove = players.remove(player.getPlayerConnection().getRawSocketAddress());
        if (toRemove != null) {
            return;
        }
        for (InetSocketAddress socketAddress : new ArrayList<>(players.keySet())) {
            Player p = players.get(socketAddress);
            if (player == p) {
                players.remove(socketAddress);
                break;
            }
        }
    }

    /**
     * Remove online Player
     */
    public void removeOnlinePlayer(Player player) {
        if (this.playerList.containsKey(player.getUniqueId())) {
            this.playerList.remove(player.getUniqueId());

            PlayerListPacket pk = new PlayerListPacket();
            pk.type = PlayerListPacket.TYPE_REMOVE;
            pk.entries = new PlayerListPacket.Entry[] {new PlayerListPacket.Entry(player.getUniqueId())};

            Server.broadcastPacket(this.playerList.values(), pk);
        }
    }

    /**
     * Get the player instance from the specified UUID.
     *
     * @param uuid uuid
     * @return Player example, can be empty
     */
    public Optional<Player> getPlayer(UUID uuid) {
        Preconditions.checkNotNull(uuid, "uuid");
        return Optional.ofNullable(playerList.get(uuid));
    }

    /**
     * Get an online player from the player name, this method is a fuzzy match and will be returned as long as the player name has the name prefix.
     *
     * @param name Player name
     * @return Player instance object,failed to get null
     */
    public Player getPlayer(String name) {
        Player found = null;
        String loweredName = name.toLowerCase();
        int delta = Integer.MAX_VALUE;
        for (Player player : this.getOnlinePlayers().values()) {
            if (player.getName().toLowerCase().startsWith(loweredName)) {
                int curDelta = player.getName().length() - loweredName.length();
                if (curDelta < delta) {
                    found = player;
                    delta = curDelta;
                }
                if (curDelta == 0) {
                    break;
                }
            }
        }
        return found;
    }

    /**
     * Get an online player from a player name, this method is an exact match and returns when the player name string is identical.
     *
     * @param name Player name
     * @return Player instance object,failed to get null
     */
    public Player getPlayerExact(String name) {
        for (Player player : this.getOnlinePlayers().values()) {
            if (player.getName().toLowerCase().equals(name.toLowerCase())) {
                return player;
            }
        }
        return null;
    }

    /**
     * Specify a partial player name and return all players with or equal to that name.
     *
     * @param partialName Partial name
     * @return All players matched
     */
    public List<Player> getMatchingPlayers(String partialName) {
        String loweredName = partialName.toLowerCase();
        List<Player> matchedPlayers = new ArrayList<>();
        for (Player player : this.getOnlinePlayers().values()) {
            if (player.getName().toLowerCase().equals(loweredName)) {
                matchedPlayers.add(player);
                return Collections.singletonList(player);
            } else if (player.getName().toLowerCase().contains(loweredName)) {
                matchedPlayers.add(player);
            }
        }
        return matchedPlayers;
    }

    /**
     * @see #updatePlayerListData(UUID, long, String, Skin, String, Player[])
     */
    public void updatePlayerListData(UUID uuid, long entityId, String name, Skin skin) {
        this.updatePlayerListData(uuid, entityId, name, skin, "", this.playerList.values());
    }

    /**
     * @see #updatePlayerListData(UUID, long, String, Skin, String, Player[])
     */
    public void updatePlayerListData(UUID uuid, long entityId, String name, Skin skin, String xboxUserId) {
        this.updatePlayerListData(uuid, entityId, name, skin, xboxUserId, this.playerList.values());
    }

    /**
     * @see #updatePlayerListData(UUID, long, String, Skin, String, Player[])
     */
    public void updatePlayerListData(UUID uuid, long entityId, String name, Skin skin, Player[] players) {
        this.updatePlayerListData(uuid, entityId, name, skin, "", players);
    }

    /**
     * Update {@link PlayerListPacket} data packets (i.e. player list data) for specified players
     *
     * @param uuid       Uuid
     * @param entityId   Entity id
     * @param name       Name
     * @param skin       Skin
     * @param xboxUserId Xbox user id
     * @param players    Specify the player receiving the packet
     */
    public void updatePlayerListData(
            UUID uuid, long entityId, String name, Skin skin, String xboxUserId, Player[] players) {
        PlayerListPacket pk = new PlayerListPacket();
        pk.type = PlayerListPacket.TYPE_ADD;
        pk.entries = new PlayerListPacket.Entry[] {new PlayerListPacket.Entry(uuid, entityId, name, skin, xboxUserId)};
        Server.broadcastPacket(players, pk);
    }

    /**
     * @see #updatePlayerListData(UUID, long, String, Skin, String, Player[])
     */
    public void updatePlayerListData(
            UUID uuid, long entityId, String name, Skin skin, String xboxUserId, Collection<Player> players) {
        this.updatePlayerListData(uuid, entityId, name, skin, xboxUserId, players.toArray(Player.EMPTY_ARRAY));
    }

    public void removePlayerListData(UUID uuid) {
        this.removePlayerListData(uuid, this.playerList.values());
    }

    /**
     * Remove player list data for all players in the array.
     *
     * @param players Player array
     */
    public void removePlayerListData(UUID uuid, Player[] players) {
        PlayerListPacket pk = new PlayerListPacket();
        pk.type = PlayerListPacket.TYPE_REMOVE;
        pk.entries = new PlayerListPacket.Entry[] {new PlayerListPacket.Entry(uuid)};
        Server.broadcastPacket(players, pk);
    }

    /**
     * Remove this player's player list data.
     *
     * @param player Player
     */
    public void removePlayerListData(UUID uuid, Player player) {
        PlayerListPacket pk = new PlayerListPacket();
        pk.type = PlayerListPacket.TYPE_REMOVE;
        pk.entries = new PlayerListPacket.Entry[] {new PlayerListPacket.Entry(uuid)};
        player.sendPacket(pk);
    }

    public void removePlayerListData(UUID uuid, Collection<Player> players) {
        this.removePlayerListData(uuid, players.toArray(Player.EMPTY_ARRAY));
    }

    /**
     * Send a player list packet to a player.
     *
     * @param player Player
     */
    public void sendFullPlayerListData(Player player) {
        PlayerListPacket pk = new PlayerListPacket();
        pk.type = PlayerListPacket.TYPE_ADD;
        pk.entries = this.playerList.values().stream()
                .map(p -> new PlayerListPacket.Entry(
                        p.getUniqueId(),
                        p.getId(),
                        p.getDisplayName(),
                        p.getSkin(),
                        p.getPlayerInfo().getXuid()))
                .toArray(PlayerListPacket.Entry[]::new);

        player.sendPacket(pk);
    }

    /**
     * Find the UUID corresponding to the specified player name from the database.
     *
     * @param name Player name
     * @return The player's UUID, which can be empty.
     */
    public Optional<UUID> lookupName(String name) {
        byte[] nameBytes = name.toLowerCase().getBytes(StandardCharsets.UTF_8);
        byte[] uuidBytes = nameLookup.get(nameBytes);
        if (uuidBytes == null) {
            return Optional.empty();
        }

        if (uuidBytes.length != 16) {
            log.warn("Invalid uuid in name lookup database detected! Removing");
            nameLookup.delete(nameBytes);
            return Optional.empty();
        }

        ByteBuffer buffer = ByteBuffer.wrap(uuidBytes);
        return Optional.of(new UUID(buffer.getLong(), buffer.getLong()));
    }

    /**
     * Update the UUID of the specified player name in the database, or add it if it does not exist.
     *
     * @param uuid Uuid
     * @param name Name
     */
    public void updateName(UUID uuid, String name) {
        byte[] nameBytes = name.toLowerCase().getBytes(StandardCharsets.UTF_8);

        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());

        nameLookup.put(nameBytes, buffer.array());
    }

    /**
     * create is false
     *
     * @see #getOfflinePlayerData(UUID, boolean)
     */
    public CompoundTag getOfflinePlayerData(UUID uuid) {
        return getOfflinePlayerData(uuid, false);
    }

    /**
     * Get the NBT data of the player specified by UUID
     *
     * @param uuid   UUID of the player to get data from
     * @param create If player data does not exist whether to create.
     * @return {@link CompoundTag}
     */
    public CompoundTag getOfflinePlayerData(UUID uuid, boolean create) {
        return getOfflinePlayerDataInternal(uuid.toString(), true, create);
    }

    @Deprecated
    public CompoundTag getOfflinePlayerData(String name) {
        return getOfflinePlayerData(name, false);
    }

    @Deprecated
    public CompoundTag getOfflinePlayerData(String name, boolean create) {
        Optional<UUID> uuid = lookupName(name);
        return getOfflinePlayerDataInternal(uuid.map(UUID::toString).orElse(name), true, create);
    }

    private CompoundTag getOfflinePlayerDataInternal(String name, boolean runEvent, boolean create) {
        Preconditions.checkNotNull(name, "name");

        PlayerDataSerializeEvent event = new PlayerDataSerializeEvent(name, playerDataSerializer);
        if (runEvent) {
            event.call();
        }

        Optional<InputStream> dataStream = Optional.empty();
        try {
            dataStream = event.getSerializer().read(name, event.getUuid().orElse(null));
            if (dataStream.isPresent()) {
                return NBTIO.readCompressed(dataStream.get());
            }
        } catch (IOException e) {
            log.warn(server.getLanguage().tr("nukkit.data.playerCorrupted", name), e);
        } finally {
            if (dataStream.isPresent()) {
                try {
                    dataStream.get().close();
                } catch (IOException e) {
                    log.catching(e);
                }
            }
        }
        CompoundTag nbt = null;
        if (create) {
            if (server.shouldSavePlayerData()) {
                log.info(server.getLanguage().tr("nukkit.data.playerNotFound", name));
            }
            Position spawn = server.getDefaultLevel().getSafeSpawn();
            nbt = new CompoundTag()
                    .putLong("firstPlayed", System.currentTimeMillis() / 1000)
                    .putLong("lastPlayed", System.currentTimeMillis() / 1000)
                    .putList(new ListTag<DoubleTag>("Pos")
                            .add(new DoubleTag("0", spawn.x()))
                            .add(new DoubleTag("1", spawn.y()))
                            .add(new DoubleTag("2", spawn.z())))
                    .putString("Level", server.getDefaultLevel().getName())
                    .putList(new ListTag<>("Inventory"))
                    .putCompound("Achievements", new CompoundTag())
                    .putInt("playerGameType", server.getGamemode().ordinal())
                    .putList(new ListTag<DoubleTag>("Motion")
                            .add(new DoubleTag("0", 0))
                            .add(new DoubleTag("1", 0))
                            .add(new DoubleTag("2", 0)))
                    .putList(new ListTag<FloatTag>("Rotation")
                            .add(new FloatTag("0", 0))
                            .add(new FloatTag("1", 0)))
                    .putFloat("FallDistance", 0)
                    .putShort("Fire", 0)
                    .putShort("Air", 300)
                    .putBoolean("OnGround", true)
                    .putBoolean("Invulnerable", false);

            this.saveOfflinePlayerData(name, nbt, true, runEvent);
        }
        return nbt;
    }

    /**
     * Save player data, players can be offline.
     *
     * @param name  Player name
     * @param tag   Nbt data
     * @param async Whether to save asynchronously
     */
    public void saveOfflinePlayerData(String name, CompoundTag tag, boolean async) {
        Optional<UUID> uuid = lookupName(name);
        saveOfflinePlayerData(uuid.map(UUID::toString).orElse(name), tag, async, true);
    }

    private void saveOfflinePlayerData(String name, CompoundTag tag, boolean async, boolean runEvent) {
        String nameLower = name.toLowerCase();
        if (server.shouldSavePlayerData()) {
            PlayerDataSerializeEvent event = new PlayerDataSerializeEvent(nameLower, playerDataSerializer);
            if (runEvent) {
                event.call();
            }

            server.getScheduler()
                    .scheduleTask(
                            new Task() {
                                boolean hasRun = false;

                                @Override
                                public void onRun(int currentTick) {
                                    this.onCancel();
                                }

                                // doing it like this ensures that the playerdata will be saved in a server shutdown
                                @Override
                                public void onCancel() {
                                    if (!this.hasRun) {
                                        this.hasRun = true;
                                        saveOfflinePlayerDataInternal(
                                                event.getSerializer(),
                                                tag,
                                                nameLower,
                                                event.getUuid().orElse(null));
                                    }
                                }
                            },
                            async);
        }
    }

    private void saveOfflinePlayerDataInternal(
            PlayerDataSerializer serializer, CompoundTag tag, String name, UUID uuid) {
        try (OutputStream dataStream = serializer.write(name, uuid)) {
            NBTIO.writeGZIPCompressed(tag, dataStream, ByteOrder.BIG_ENDIAN);
        } catch (Exception e) {
            log.error(server.getLanguage().tr("nukkit.data.saveError", name, e));
        }
    }

    public PlayerDataSerializer getPlayerDataSerializer() {
        return playerDataSerializer;
    }

    public void setPlayerDataSerializer(PlayerDataSerializer playerDataSerializer) {
        this.playerDataSerializer = Preconditions.checkNotNull(playerDataSerializer, "playerDataSerializer");
    }

    /**
     * Get all online players Map.
     *
     * @return All online players Map
     */
    public Map<UUID, Player> getOnlinePlayers() {
        return ImmutableMap.copyOf(playerList);
    }

    /**
     * Get all online player List.
     *
     * @return All online player List
     */
    public List<Player> getPlayerList() {
        return ImmutableList.copyOf(playerList.values());
    }
}
