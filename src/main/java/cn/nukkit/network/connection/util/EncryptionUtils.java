package cn.nukkit.network.connection.util;

import cn.nukkit.utils.JSONUtils;
import lombok.experimental.UtilityClass;
import org.jose4j.json.JsonUtil;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwx.HeaderParameterNames;
import org.jose4j.lang.JoseException;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@UtilityClass
public class EncryptionUtils {
    private static final ECPublicKey MOJANG_PUBLIC_KEY;
    private static final ECPublicKey OLD_MOJANG_PUBLIC_KEY;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String MOJANG_PUBLIC_KEY_BASE64 =
            "MHYwEAYHKoZIzj0CAQYFK4EEACIDYgAECRXueJeTDqNRRgJi/vlRufByu/2G0i2Ebt6YMar5QX/R0DIIyrJMcUpruK4QveTfJSTp3Shlq4Gk34cD/4GUWwkv0DVuzeuB+tXija7HBxii03NHDbPAD0AKnLr2wdAp";
    private static final String OLD_MOJANG_PUBLIC_KEY_BASE64 =
            "MHYwEAYHKoZIzj0CAQYFK4EEACIDYgAE8ELkixyLcwlZryUQcu1TvPOmI2B7vX83ndnWRUaXm74wFfa5f/lwQNTfrLVHa2PmenpGI6JhIMUJaWZrjmMj90NoKNFSNBuKdm8rYiXsfaz3K36x/1U26HpG0ZxK/V1V";
    private static final KeyPairGenerator KEY_PAIR_GEN;

    public static final String ALGORITHM_TYPE = AlgorithmIdentifiers.ECDSA_USING_P384_CURVE_AND_SHA384;
    private static final AlgorithmConstraints ALGORITHM_CONSTRAINTS =
            new AlgorithmConstraints(ConstraintType.PERMIT, ALGORITHM_TYPE);

    static {
        // DO NOT REMOVE THIS
        // Since Java 8u231, secp384r1 is deprecated and will throw an exception.
        String namedGroups = System.getProperty("jdk.tls.namedGroups");
        System.setProperty("jdk.tls.namedGroups", namedGroups == null || namedGroups.isEmpty() ? "secp384r1" : ", secp384r1");

        try {
            KEY_PAIR_GEN = KeyPairGenerator.getInstance("EC");
            KEY_PAIR_GEN.initialize(new ECGenParameterSpec("secp384r1"));
            MOJANG_PUBLIC_KEY = parseKey(MOJANG_PUBLIC_KEY_BASE64);
            OLD_MOJANG_PUBLIC_KEY = parseKey(OLD_MOJANG_PUBLIC_KEY_BASE64);
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeySpecException e) {
            throw new AssertionError("Unable to initialize required encryption", e);
        }
    }

    /**
     * Generate EC public key from base 64 encoded string
     *
     * @param b64 base 64 encoded key
     * @return key generated
     * @throws NoSuchAlgorithmException runtime does not support the EC key spec
     * @throws InvalidKeySpecException  input does not conform with EC key spec
     */
    public static ECPublicKey parseKey(String b64) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return (ECPublicKey) KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(b64)));
    }

    /**
     * Create EC key pair to be used for handshake and encryption
     *
     * @return EC KeyPair
     */
    public static KeyPair createKeyPair() {
        return KEY_PAIR_GEN.generateKeyPair();
    }

    public static byte[] verifyClientData(String clientDataJwt, String identityPublicKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException, JoseException {
        return verifyClientData(clientDataJwt, parseKey(identityPublicKey));
    }

    public static byte[] verifyClientData(String clientDataJwt, PublicKey identityPublicKey) throws JoseException {
        JsonWebSignature clientData = new JsonWebSignature();
        clientData.setCompactSerialization(clientDataJwt);
        clientData.setKey(identityPublicKey);
        if (!clientData.verifySignature()) {
            return null;
        }
        return clientData.getUnverifiedPayloadBytes();
    }

    public static ChainValidationResult validateChain(List<String> chain)
            throws JoseException, NoSuchAlgorithmException, InvalidKeySpecException {
        switch (chain.size()) {
            case 1:
                // offline / proxied
                JsonWebSignature identity = new JsonWebSignature();
                identity.setCompactSerialization(chain.get(0));
                return new ChainValidationResult(false, identity.getUnverifiedPayload());
            case 3:
                ECPublicKey currentKey = null;
                Map<String, Object> parsedPayload = null;
                for (int i = 0; i < 3; i++) {
                    JsonWebSignature signature = new JsonWebSignature();
                    signature.setCompactSerialization(chain.get(i));

                    ECPublicKey expectedKey = parseKey(signature.getHeader(HeaderParameterNames.X509_URL));

                    if (currentKey == null) {
                        currentKey = expectedKey;
                    } else if (!currentKey.equals(expectedKey)) {
                        throw new IllegalStateException("Received broken chain");
                    }

                    signature.setAlgorithmConstraints(ALGORITHM_CONSTRAINTS);
                    signature.setKey(currentKey);
                    if (!signature.verifySignature()) {
                        throw new IllegalStateException("Chain signature doesn't match content");
                    }

                    // the second chain entry has to be signed by Mojang
                    if (i == 1 && (!currentKey.equals(MOJANG_PUBLIC_KEY) && !currentKey.equals(OLD_MOJANG_PUBLIC_KEY))) {
                        throw new IllegalStateException("The chain isn't signed by Mojang!");
                    }

                    parsedPayload = JsonUtil.parseJson(signature.getUnverifiedPayload());
                    String identityPublicKey = JSONUtils.childAsType(parsedPayload, "identityPublicKey", String.class);
                    currentKey = parseKey(identityPublicKey);
                }
                return new ChainValidationResult(true, parsedPayload);
            default:
                throw new IllegalStateException("Unexpected login chain length");
        }
    }

    /**
     * Generate the secret key used to encrypt the connection
     *
     * @param localPrivateKey local private key
     * @param remotePublicKey remote public key
     * @param token           token generated or received from the server
     * @return secret key used to encrypt connection
     * @throws InvalidKeyException keys provided are not EC spec
     */
    public static SecretKey getSecretKey(PrivateKey localPrivateKey, PublicKey remotePublicKey, byte[] token) throws InvalidKeyException {
        byte[] sharedSecret = getEcdhSecret(localPrivateKey, remotePublicKey);

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }

        digest.update(token);
        digest.update(sharedSecret);
        byte[] secretKeyBytes = digest.digest();
        return new SecretKeySpec(secretKeyBytes, "AES");
    }

    private static byte[] getEcdhSecret(PrivateKey localPrivateKey, PublicKey remotePublicKey) throws InvalidKeyException {
        KeyAgreement agreement;
        try {
            agreement = KeyAgreement.getInstance("ECDH");
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }

        agreement.init(localPrivateKey);
        agreement.doPhase(remotePublicKey, true);
        return agreement.generateSecret();
    }

    /**
     * Create handshake JWS used in the {@link cn.nukkit.network.connection.packet.ServerToClientHandshakePacket}
     * which completes the encryption handshake.
     *
     * @param serverKeyPair used to sign the JWT
     * @param token         salt for the encryption handshake
     * @return signed JWS object
     * @throws JoseException invalid key pair provided
     */
    public static String createHandshakeJwt(KeyPair serverKeyPair, byte[] token) throws JoseException {
        JsonWebSignature signature = new JsonWebSignature();
        signature.setAlgorithmHeaderValue(ALGORITHM_TYPE);
        signature.setHeader(
                HeaderParameterNames.X509_URL,
                Base64.getEncoder().encodeToString(serverKeyPair.getPublic().getEncoded())
        );
        signature.setKey(serverKeyPair.getPrivate());

        JwtClaims claims = new JwtClaims();
        claims.setClaim("salt", Base64.getEncoder().encodeToString(token));
        signature.setPayload(claims.toJson());

        return signature.getCompactSerialization();
    }

    /**
     * Generate 16 bytes of random data for the handshake token using a {@link SecureRandom}
     *
     * @return 16 byte token
     */
    public static byte[] generateRandomToken() {
        byte[] token = new byte[16];
        SECURE_RANDOM.nextBytes(token);
        return token;
    }

    /**
     * Mojang's public key used to verify the JWT during login.
     *
     * @return Mojang's public EC key
     */
    public static ECPublicKey getMojangPublicKey() {
        return MOJANG_PUBLIC_KEY;
    }

    public static ECPublicKey getOldMojangPublicKey() {
        return OLD_MOJANG_PUBLIC_KEY;
    }

    public static Cipher createCipher(boolean gcm, boolean encrypt, SecretKey key) {
        try {
            byte[] iv;
            String transformation;
            if (gcm) {
                iv = new byte[16];
                System.arraycopy(key.getEncoded(), 0, iv, 0, 12);
                iv[15] = 2;
                transformation = "AES/CTR/NoPadding";
            } else {
                iv = Arrays.copyOf(key.getEncoded(), 16);
                transformation = "AES/CFB8/NoPadding";
            }
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(encrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException e) {
            throw new AssertionError("Unable to initialize required encryption", e);
        }
    }
}
