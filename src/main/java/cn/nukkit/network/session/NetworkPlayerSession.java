package cn.nukkit.network.session;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.network.CompressionProvider;
import cn.nukkit.network.protocol.DataPacket;
import org.jetbrains.annotations.Nullable;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.util.concurrent.Executor;

@Since("1.19.30-r1")
@PowerNukkitXOnly
public interface NetworkPlayerSession {
    void sendPacket(DataPacket packet);

    void sendImmediatePacket(DataPacket packet, Runnable callback);

    void sendImmediatePacket(DataPacket packet);

    void disconnect(String reason);

    Player getPlayer();

    void setCompression(CompressionProvider compression);

    CompressionProvider getCompression();

    default void setEncryption(SecretKey agreedKey, Cipher encryptionCipher, Cipher decryptionCipher) {

    }

    @Since("1.20.0-r3")
    @PowerNukkitXOnly
    default @Nullable Executor getPacketProcessingExecutor() {
        return null;
    }
}
