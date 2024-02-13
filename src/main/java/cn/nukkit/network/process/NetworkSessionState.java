package cn.nukkit.network.process;

public enum NetworkSessionState {
    START(),

    LOGIN(),

    RESOURCE_PACK(),

    ENCRYPTION(),

    SPAWN_SEQUENCE(),

    SPAWN(),

    IN_GAME(),

    DEATH()
}
