package org.powernukkitx.network.process;

public enum SessionState {

    INITIAL,
    REQUESTED_NETWORK_SETTINGS,
    LOGIN,
    AUTHENTICATING,
    ENCRYPTION,
    RESOURCE_PACK,
    BEFORE_SPAWN,
    CHUNKS
}
