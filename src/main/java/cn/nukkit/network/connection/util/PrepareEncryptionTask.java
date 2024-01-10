package cn.nukkit.network.connection.util;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.scheduler.AsyncTask;
import com.nimbusds.jose.jwk.Curve;
import lombok.extern.log4j.Log4j2;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

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
            KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
            generator.initialize(Curve.P_384.toECParameterSpec());
            KeyPair privateKeyPair = generator.generateKeyPair();

            byte[] token = EncryptionUtils.generateRandomToken();


            this.encryptionKey = EncryptionUtils.getSecretKey(privateKeyPair.getPrivate(), EncryptionUtils.parseKey(this.player.getLoginChainData().getIdentityPublicKey()), token);
            this.handshakeJwt = EncryptionUtils.createHandshakeJwt(privateKeyPair, token);
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
