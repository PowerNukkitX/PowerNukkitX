package org.powernukkitx.network.process;

public enum SessionState {

    INITIAL,
    REQUESTED_NETWORK_SETTINGS,
    LOGIN,
    AUTHENTICATING,
    ENCRYPTION,
    RESOURCE_PACK,
    /**
     * The player is being created off the network thread, see {@link PlayerSessionHolder#beginSpawn}.
     */
    SPAWNING,
    BEFORE_SPAWN,
    CHUNKS
}
