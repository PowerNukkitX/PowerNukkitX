package cn.nukkit.utils;

import cn.nukkit.network.connection.util.ChainValidationResult;
import cn.nukkit.network.process.login.*;
import com.google.common.reflect.TypeToken;
import lombok.experimental.UtilityClass;
import org.jose4j.json.JsonUtil;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jwk.HttpsJwks;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jwt.consumer.JwtContext;
import org.jose4j.jwx.HeaderParameterNames;
import org.jose4j.keys.resolvers.HttpsJwksVerificationKeyResolver;
import org.jose4j.lang.JoseException;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

/**
 * Took from CloudburstMC Protocol.
 * <a href="https://github.com/CloudburstMC/Protocol/blob/3.0/bedrock-connection/src/main/java/org/cloudburstmc/protocol/bedrock/util/EncryptionUtils.java">...</a>
 */

@UtilityClass
public class EncryptionUtils {
    private static final ECPublicKey MOJANG_PUBLIC_KEY;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String MOJANG_PUBLIC_KEY_BASE64 =
            "MHYwEAYHKoZIzj0CAQYFK4EEACIDYgAECRXueJeTDqNRRgJi/vlRufByu/2G0i2Ebt6YMar5QX/R0DIIyrJMcUpruK4QveTfJSTp3Shlq4Gk34cD/4GUWwkv0DVuzeuB+tXija7HBxii03NHDbPAD0AKnLr2wdAp";
    private static final KeyPairGenerator KEY_PAIR_GEN;

    public static final String ALGORITHM_TYPE = AlgorithmIdentifiers.ECDSA_USING_P384_CURVE_AND_SHA384;
    private static final AlgorithmConstraints ALGORITHM_CONSTRAINTS =
            new AlgorithmConstraints(ConstraintType.PERMIT, ALGORITHM_TYPE);

    private static final String DISCOVERY_ENDPOINT =
            "https://client.discovery.minecraft-services.net/api/v1.0/discovery/MinecraftPE/builds/1.0.0.0";

    private static final Type MAP_PARSE_TYPE = new TypeToken<Map<String, Object>>(){}.getType();
    private static final Map<String, Object> DISCOVERY_DATA = getDiscoveryData();
    private static final Map<String, Object> OPENID_CONFIGURATION = getOpenIdConfiguration();
    private static final String JWKS_URL = getJwksUrl();
    private static final String ISSUER = getIssuer();
    private static final HttpsJwks JWKS = new HttpsJwks(JWKS_URL);
    private static final HttpsJwksVerificationKeyResolver RESOLVER = new HttpsJwksVerificationKeyResolver(JWKS);
    private static final JwtConsumer MOJANG_CONSUMER = new JwtConsumerBuilder()
            .setVerificationKeyResolver(RESOLVER)
            .setRequireExpirationTime()
            .setRequireSubject()
            .setExpectedAudience(true, "api://auth-minecraft-services/multiplayer")
            .setExpectedIssuer(ISSUER)
            .build();

    private static final JwtConsumer OFFLINE_CONSUMER = new JwtConsumerBuilder()
            .setSkipAllValidators()
            .setSkipSignatureVerification()
            .setRequireExpirationTime()
            .setSkipDefaultAudienceValidation()
            .build();

    static {
        // DO NOT REMOVE THIS
        // Since Java 8u231, secp384r1 is deprecated and will throw an exception.
        String namedGroups = System.getProperty("jdk.tls.namedGroups");
        System.setProperty("jdk.tls.namedGroups", namedGroups == null || namedGroups.isEmpty() ? "secp384r1" : namedGroups + ", secp384r1");

        try {
            KEY_PAIR_GEN = KeyPairGenerator.getInstance("EC");
            KEY_PAIR_GEN.initialize(new ECGenParameterSpec("secp384r1"));
            MOJANG_PUBLIC_KEY = parseKey(MOJANG_PUBLIC_KEY_BASE64);
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeySpecException e) {
            throw new AssertionError("Unable to initialize required encryption", e);
        }
    }

    private static Map<String, Object> getDiscoveryData() {
        try {
            URL url = URI.create(DISCOVERY_ENDPOINT).toURL();
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            if (connection.getResponseCode() != 200) {
                throw new IOException("Failed to fetch discovery data: " + connection.getResponseMessage());
            }
            try(InputStream stream = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                return Utils.GSON.fromJson(reader, MAP_PARSE_TYPE);
            }
        } catch (IOException e) {
            throw new AssertionError("Unable to fetch discovery data from " + DISCOVERY_ENDPOINT, e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getAuthEnvironment() {
        Map<String, Object> result = (Map<String, Object>) DISCOVERY_DATA.get("result");

        if (result == null) {
            throw new AssertionError("Discovery data does not contain 'result' key" + DISCOVERY_DATA);
        }
        Map<String, Object> environments = (Map<String, Object>) result.get("serviceEnvironments");
        if (environments == null) {
            throw new AssertionError("Discovery data does not contain 'serviceEnvironments' key" + result);
        }
        Map<String, Object> authEnv = (Map<String, Object>) environments.get("auth");
        if (authEnv == null) {
            throw new AssertionError("Discovery data does not contain 'auth' environment" + environments);
        }
        Map<String, Object> prodEnv = (Map<String, Object>) authEnv.get("prod");
        if (prodEnv == null) {
            throw new AssertionError("Discovery data does not contain 'prod' environment" + authEnv);
        }
        return prodEnv;
    }

    private static String getServiceUri() {
        String issuer = (String) getAuthEnvironment().get("serviceUri");
        if (issuer == null) {
            throw new AssertionError("Discovery data does not contain 'issuer' key in 'prod' environment");
        }
        return issuer;
    }

    private static Map<String, Object> getOpenIdConfiguration() {
        String serviceUri = getServiceUri();

        String openIdConfigUrl = serviceUri + "/.well-known/openid-configuration";
        try {
            URL url = URI.create(openIdConfigUrl).toURL();
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            if (connection.getResponseCode() != 200) {
                throw new IOException("Failed to fetch OpenID configuration: " + connection.getResponseMessage());
            }
            try (InputStream stream = connection.getInputStream();
                 InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                return Utils.GSON.fromJson(reader, MAP_PARSE_TYPE);
            }
        } catch (IOException e) {
            throw new AssertionError("Unable to fetch OpenID configuration from " + openIdConfigUrl, e);
        }
    }

    private static String getJwksUrl() {
        String jwksUrl = (String) OPENID_CONFIGURATION.get("jwks_uri");
        if (jwksUrl == null || jwksUrl.isEmpty()) {
            throw new AssertionError("OpenID configuration does not contain 'jwks_uri' key: " + OPENID_CONFIGURATION);
        }
        return jwksUrl;
    }

    private static String getIssuer() {
        String issuer = (String) OPENID_CONFIGURATION.get("issuer");
        if (issuer == null || issuer.isEmpty()) {
            throw new AssertionError("OpenID configuration does not contain 'issuer' key: " + OPENID_CONFIGURATION);
        }
        return issuer;
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

    public static ChainValidationResult validatePayload(AuthPayload payload)
            throws JoseException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidJwtException {
        if (payload instanceof CertificateChainPayload) {
            CertificateChainPayload chainPayload = (CertificateChainPayload) payload;
            List<String> chain = chainPayload.getChain();
            if (chain == null || chain.isEmpty()) {
                throw new IllegalStateException("Certificate chain is empty");
            }
            return validateChain(chain);
        } else if (payload instanceof TokenPayload) {
            TokenPayload tokenPayload = (TokenPayload) payload;
            String token = tokenPayload.getToken();
            if (token == null || token.isEmpty()) {
                throw new IllegalStateException("Token is empty");
            }
            return validateToken(payload.getAuthType(), token);
        } else {
            throw new IllegalArgumentException("Unsupported AuthPayload type: " + payload.getClass().getName());
        }
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
                    if (i == 1 && !currentKey.equals(MOJANG_PUBLIC_KEY)) {
                        throw new IllegalStateException("The chain isn't signed by Mojang!");
                    }

                    parsedPayload = JsonUtil.parseJson(signature.getUnverifiedPayload());
                    String identityPublicKey = childAsType(parsedPayload, "identityPublicKey", String.class);
                    currentKey = parseKey(identityPublicKey);
                }
                return new ChainValidationResult(true, parsedPayload);
            default:
                throw new IllegalStateException("Unexpected login chain length");
        }
    }

    public static ChainValidationResult validateToken(AuthType type, String token) throws InvalidJwtException, JoseException {
        if (type == AuthType.FULL || type == AuthType.GUEST) {
            JwtContext context = MOJANG_CONSUMER.process(token);
            return new ChainValidationResult(true, context);
        } else if (type == AuthType.SELF_SIGNED) {
            JwtContext context = OFFLINE_CONSUMER.process(token);
            return new ChainValidationResult(false, context);
        }
        throw new JoseException("Unsupported AuthType: " + type);
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
     * Create handshake JWS used in the {@link cn.nukkit.network.protocol.ServerToClientHandshakePacket}
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
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new AssertionError("Unable to initialize required encryption", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static  <T> T childAsType(Map<?, ?> data, String key, Class<T> asType) {
        Object value = data.get(key);
        if (!(asType.isInstance(value))) {
            throw new IllegalStateException(key + " node is missing");
        }
        return (T) value;
    }
}
