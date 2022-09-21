package cn.nukkit.network.session;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.network.CompressionProvider;
import cn.nukkit.network.protocol.DataPacket;

@Since("1.19.30-r1")
@PowerNukkitXOnly
public interface NetworkPlayerSession {
    void sendPacket(DataPacket packet);
    void sendImmediatePacket(DataPacket packet, Runnable callback);

    void disconnect(String reason);

    Player getPlayer();

    void setCompression(CompressionProvider compression);
    CompressionProvider getCompression();
}
