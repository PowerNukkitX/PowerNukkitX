package cn.nukkit.network.process;

import cn.nukkit.PlayerHandle;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import org.jetbrains.annotations.NotNull;

/**
 * A DataPacketProcessor is used to handle a specific type of DataPacket. <br/>
 * DataPacketProcessor must be <strong>thread-safe</strong>. <br/>
 * <hr/>
 * Why not interfaces? Hotspot C2 JIT cannot handle so many classes that impl the same interface, it makes the
 * performance lower.
 */
@Since("1.19.80-r2")
@PowerNukkitXOnly
public abstract class DataPacketProcessor {

    public abstract void handle(@NotNull PlayerHandle playerHandle, @NotNull DataPacket packet);

    public abstract int getPacketId();

    public int getProtocol() {
        return ProtocolInfo.CURRENT_PROTOCOL;
    }
}
