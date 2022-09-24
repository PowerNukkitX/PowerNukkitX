package cn.nukkit.network.protocol.types;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.BlockVector3;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public record BlockChangeEntry(BlockVector3 blockPos, int runtimeID, int updateFlags, long messageEntityID, MessageType messageType) {
    public enum MessageType {
        NONE,
        CREATE,
        DESTROY
    }
}
