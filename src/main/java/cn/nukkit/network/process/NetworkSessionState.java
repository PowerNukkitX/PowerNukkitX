package cn.nukkit.network.process;

public enum NetworkSessionState {
    START(),

    LOGIN(),

    RESOURCE_PACK(),

    ENCRYPTION(),

    PRE_SPAWN(),

    IN_GAME(),

    DEATH()
}
