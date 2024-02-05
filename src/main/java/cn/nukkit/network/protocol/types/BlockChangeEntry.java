package cn.nukkit.network.protocol.types;

import cn.nukkit.math.BlockVector3;


public record BlockChangeEntry(BlockVector3 blockPos, long runtimeID, int updateFlags, long messageEntityID, MessageType messageType) {
    public enum MessageType {
        NONE,
        CREATE,
        DESTROY
    }
}
