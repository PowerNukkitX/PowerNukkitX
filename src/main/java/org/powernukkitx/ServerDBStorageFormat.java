package org.powernukkitx;

import java.nio.charset.StandardCharsets;

/**
 * Defines the LevelDB key format used by PowerNukkitX server-owned persistent data.
 * <p>
 * Server-global data is stored in the {@code server_data} LevelDB database and separated
 * into logical namespaces by key prefixes. The storage version identifies the PNX version
 * that introduced the current format and is used to determine which migration layers must
 * be applied when loading older data.
 * <p>
 * The server data key space is organized as follows:
 * <pre>
 * server_data
 * ├── storage_version
 * ├── player
 * │   ├── data
 * │   ├── pnx_extra
 * │   ├── custom
 * │   └── name
 * ├── scoreboard
 * │   └── storage_version
 * ├── dynamic_properties
 * │   ├── data
 * │   └── uuid
 * ├── position_tracking
 * │   ├── records
 * │   ├── last_id
 * │   └── storage_version
 * └── actor_unique_id
 *     └── next_epoch
 * </pre>
 *
 * @author Curse
 */
public final class ServerDBStorageFormat {

    public static final String LEVELDB_VERSION_KEY = "storageVersion";
    public static final String LEVELDB_CURRENT_VERSION = "3.1.0";
    public static final String PLAYERDB_CURRENT_VERSION = "3.1.0";
    public static final byte[] PLAYERDB_STORAGE_VERSION_KEY = "\0pnx_player_storage_version".getBytes(StandardCharsets.UTF_8);
    public static final byte[] SERVER_NEXT_EPOCH_KEY = "\0pnx_actor_unique_id_epoch".getBytes(StandardCharsets.UTF_8);

    /**
     * First PNX version using the unified server data LevelDB format.
     */
    public static final String SERVER_DATA_INITIAL_VERSION = "3.1.0";

    /**
     * Stores the PNX version corresponding to the current server data format.
     */
    public static final byte[] SERVER_DATA_STORAGE_VERSION_KEY = "\0pnx_server_data:storage_version".getBytes(StandardCharsets.UTF_8);

    /**
     * Prefix for player NBT records, followed by the player UUID.
     */
    public static final byte[] SERVER_DATA_PLAYER_PREFIX = "\0pnx_server_data:player:data:".getBytes(StandardCharsets.UTF_8);

    /**
     * Prefix for PNX-owned player NBT records, followed by the player UUID.
     */
    public static final byte[] SERVER_DATA_PLAYER_PNX_EXTRA_PREFIX = "\0pnx_server_data:player:pnx_extra:".getBytes(StandardCharsets.UTF_8);

    /**
     * Prefix for plugin-owned custom player NBT records, followed by the player UUID.
     */
    public static final byte[] SERVER_DATA_PLAYER_CUSTOM_PREFIX = "\0pnx_server_data:player:custom:".getBytes(StandardCharsets.UTF_8);

    /**
     * Prefix for lowercase player name to UUID mappings, followed by the player name.
     */
    public static final byte[] SERVER_DATA_PLAYER_NAME_PREFIX = "\0pnx_server_data:player:name:".getBytes(StandardCharsets.UTF_8);

    /**
     * Stores the BDS scoreboard NBT record.
     */
    public static final byte[] SERVER_DATA_SCOREBOARD_KEY = "scoreboard".getBytes(StandardCharsets.UTF_8);

    /**
     * Stores the current scoreboard migration version.
     */
    public static final byte[] SERVER_DATA_SCOREBOARD_STORAGE_VERSION_KEY = "\0pnx_server_data:scoreboard:storage_version".getBytes(StandardCharsets.UTF_8);

    /**
     * Stores server-global Dynamic Properties.
     */
    public static final byte[] SERVER_DATA_DYNAMIC_PROPERTIES_KEY = "DynamicProperties".getBytes(StandardCharsets.UTF_8);

    /**
     * Stores the server-global Dynamic Properties namespace UUID.
     */
    public static final byte[] SERVER_DATA_DYNAMIC_PROPERTIES_UUID_KEY = "\0pnx_server_data:dynamic_properties:uuid".getBytes(StandardCharsets.UTF_8);

    /**
     * Prefix for BDS position-tracking records.
     */
    public static final byte[] SERVER_DATA_POSITION_TRACKING_PREFIX = "PosTrackDB-".getBytes(StandardCharsets.UTF_8);

    /**
     * Stores the last globally allocated position-tracking ID.
     */
    public static final byte[] SERVER_DATA_POSITION_TRACKING_LAST_ID_KEY = "PositionTrackDB-LastId".getBytes(StandardCharsets.UTF_8);

    /**
     * Stores the current position-tracking migration version.
     */
    public static final byte[] SERVER_DATA_POSITION_TRACKING_STORAGE_VERSION_KEY = "\0pnx_server_data:position_tracking:storage_version".getBytes(StandardCharsets.UTF_8);

    /**
     * Prefix for server-global ActorUniqueID state.
     */
    public static final byte[] SERVER_DATA_ACTOR_UNIQUE_ID_PREFIX = "\0pnx_server_data:actor_unique_id:".getBytes(StandardCharsets.UTF_8);

    /**
     * Stores the next server-global ActorUniqueID epoch.
     */
    public static final byte[] SERVER_DATA_ACTOR_UNIQUE_ID_NEXT_EPOCH_KEY = "\0pnx_server_data:actor_unique_id:next_epoch".getBytes(StandardCharsets.UTF_8);

    private ServerDBStorageFormat() {
    }
}
