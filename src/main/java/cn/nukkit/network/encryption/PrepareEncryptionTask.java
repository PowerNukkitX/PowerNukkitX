package cn.nukkit.network.encryption;

import cn.nukkit.Server;
import cn.nukkit.player.Player;
import cn.nukkit.scheduler.AsyncTask;
import com.nimbusds.jose.jwk.Curve;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class PrepareEncryptionTask extends AsyncTask {

    private final Player player;

    private String handshakeJwt;
    private SecretKey encryptionKey;
    private Cipher encryptionCipher;
    private Cipher decryptionCipher;

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

            this.encryptionKey = EncryptionUtils.getSecretKey(
                    privateKeyPair.getPrivate(),
                    EncryptionUtils.generateKey(player.getPlayerInfo().getIdentityPublicKey()),
                    token);
            this.handshakeJwt =
                    EncryptionUtils.createHandshakeJwt(privateKeyPair, token).serialize();

            this.encryptionCipher = EncryptionUtils.createCipher(true, true, this.encryptionKey);
            this.decryptionCipher = EncryptionUtils.createCipher(true, false, this.encryptionKey);
        } catch (Exception e) {
            log.error("Failed to prepare encryption", e);
        }
    }

    @Override
    public void onCompletion(Server server) {}

    public String getHandshakeJwt() {
        return this.handshakeJwt;
    }

    public SecretKey getEncryptionKey() {
        return this.encryptionKey;
    }

    public Cipher getEncryptionCipher() {
        return this.encryptionCipher;
    }

    public Cipher getDecryptionCipher() {
        return this.decryptionCipher;
    }
}
