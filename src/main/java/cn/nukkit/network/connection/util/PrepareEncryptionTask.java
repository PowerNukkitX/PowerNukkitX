package cn.nukkit.network.connection.util;

import cn.nukkit.Server;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.ClientChainData;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;

@Slf4j
public class PrepareEncryptionTask extends AsyncTask {

    private final ClientChainData data;

    private String handshakeJwt;
    private SecretKey encryptionKey;

    public PrepareEncryptionTask(ClientChainData player) {
        this.data = player;
    }

    @Override
    public void onRun() {
        try {
            var clientKey = EncryptionUtils.parseKey(data.getIdentityPublicKey());
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
