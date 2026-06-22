package cn.nukkit.network.process;

public enum SessionState {

    INITIAL,
    REQUESTED_NETWORK_SETTINGS,
    LOGIN,
    ENCRYPTION,
    RESOURCE_PACK,
    BEFORE_SPAWN,
    CHUNKS
}