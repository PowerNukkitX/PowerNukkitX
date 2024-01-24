package cn.nukkit.network.connection.util;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.scheduler.AsyncTask;
import lombok.extern.log4j.Log4j2;

import javax.crypto.SecretKey;

@Log4j2
public class PrepareEncryptionTask extends AsyncTask {

    private final Player player;

    private String handshakeJwt;
    private SecretKey encryptionKey;

    public PrepareEncryptionTask(Player player) {
        this.player = player;
    }

    @Override
    public void onRun() {
        try {
            var clientKey = EncryptionUtils.parseKey(player.getLoginChainData().getIdentityPublicKey());
            var encryptionKeyPair = EncryptionUtils.createKeyPair();
            var encryptionToken = EncryptionUtils.generateRandomToken();
            encryptionKey = EncryptionUtils.getSecretKey(
                    encryptionKeyPair.getPrivate(), clientKey,
                    encryptionToken
            );
            this.handshakeJwt = EncryptionUtils.createHandshakeJwt(encryptionKeyPair, encryptionToken);
        } catch (Exception e) {
            log.error("Failed to prepare encryption", e);
        }
    }

    @Override
    public void onCompletion(Server server) {

    }

    public String getHandshakeJwt() {
        return this.handshakeJwt;
    }

    public SecretKey getEncryptionKey() {
        return this.encryptionKey;
    }
}
