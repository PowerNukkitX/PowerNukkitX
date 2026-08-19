package org.powernukkitx.network.process.handler;

import io.netty.channel.EventLoop;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.DisconnectFailReason;
import org.cloudburstmc.protocol.bedrock.data.PlayStatus;
import org.cloudburstmc.protocol.bedrock.data.auth.PlayerAuthenticationType;
import org.cloudburstmc.protocol.bedrock.data.skin.Skin;
import org.cloudburstmc.protocol.bedrock.packet.LoginPacket;
import org.cloudburstmc.protocol.bedrock.packet.ServerToClientHandshakePacket;
import org.cloudburstmc.protocol.bedrock.util.ChainValidationResult;
import org.cloudburstmc.protocol.bedrock.util.EncryptionUtils;
import org.jetbrains.annotations.Nullable;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jwt.consumer.JwtContext;
import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.event.player.PlayerPreLoginEvent;
import org.powernukkitx.event.player.PlayerLoginFailEvent;
import org.powernukkitx.network.NetworkConstants;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.powernukkitx.network.process.SessionState;
import org.powernukkitx.network.process.auth.ClientChainData;
import org.powernukkitx.network.process.auth.ClientSkinData;
import org.powernukkitx.utils.SkinUtils;

import javax.crypto.SecretKey;
import java.security.PublicKey;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Handles the client's {@code LoginPacket}.
 * <p>
 * Validating a login costs several milliseconds of cryptography: the identity chain, the client
 * JWT, and the encryption key exchange. RakNet pins a session to one Netty event loop that also
 * serves every other session on that loop, so doing this inline stalls unrelated players whenever
 * a batch of logins arrives.
 * <p>
 * The work therefore runs on the compute pool in two steps, with the checks that read server
 * state - the pre-login event, player count, whitelist and bans - on the event loop between them.
 * That ordering is deliberate: it matches the sequence used when this ran inline, so failures are
 * still reported in the same order, and a login the server is going to refuse anyway never pays
 * for the client JWT or the key exchange.
 *
 * @author Kaooot
 */
@Slf4j
public class LoginHandler implements PacketHandler<LoginPacket> {

    @Override
    public void handle(LoginPacket packet, PlayerSessionHolder holder, Server server) {
        if (!holder.getState().equals(SessionState.LOGIN)) {
            failLogin(holder, server, DisconnectFailReason.UNEXPECTED_PACKET, null);
            return;
        }

        final int clientNetworkVersion = packet.getClientNetworkVersion();
        final int serverNetworkVersion = NetworkConstants.CODEC.getProtocolVersion();

        if (clientNetworkVersion != serverNetworkVersion) {
            final boolean serverOutdated = clientNetworkVersion > serverNetworkVersion;
            holder.sendPlayStatus(
                serverOutdated ?
                    PlayStatus.LOGIN_FAILED_SERVER_OLD : PlayStatus.LOGIN_FAILED_CLIENT_OLD
            );
            failLogin(holder, server, serverOutdated ? DisconnectFailReason.OUTDATED_SERVER : DisconnectFailReason.OUTDATED_CLIENT, null);
            return;
        }

        final PlayerAuthenticationType type = packet.getAuthenticationType();
        if (type.equals(PlayerAuthenticationType.UNKNOWN)) {
            failLogin(holder, server, DisconnectFailReason.NOT_AUTHENTICATED, null);
            return;
        }

        final boolean hasToken = packet.getToken() != null && !packet.getToken().isEmpty();
        final List<String> chain = packet.getChain() != null ? List.copyOf(packet.getChain()) : List.of();
        if (!hasToken && chain.isEmpty()) {
            failLogin(holder, server, DisconnectFailReason.NOT_AUTHENTICATED, null);
            return;
        }

        final boolean xboxAuthRequired = server.getSettings().baseSettings().xboxAuth();
        final Credentials credentials = new Credentials(type, packet.getToken(), chain, packet.getClientJwt());

        holder.setState(SessionState.AUTHENTICATING);

        offLoop(holder, server,
            () -> validateChain(credentials, server, xboxAuthRequired),
            outcome -> applyChain(outcome, credentials, holder, server));
    }

    /**
     * Verifies the identity chain. First off-loop step, and the only one a login must pass before
     * the server decides whether it wants the player at all.
     * <p>
     * A client that is signed in sends a Mojang-issued token, while one that is not sends a
     * self-signed certificate chain instead, so both forms have to be accepted here. Whether the
     * identity ended up signed is what the xbox-auth check below reads.
     */
    private ChainOutcome validateChain(Credentials credentials, Server server, boolean xboxAuthRequired) throws Exception {
        final ChainValidationResult result = credentials.token() == null || credentials.token().isEmpty()
            ? EncryptionUtils.validateChain(credentials.chain())
            : EncryptionUtils.validateToken(credentials.authenticationType(), credentials.token());
        final boolean unsignedAllowed = server.getProxyAuthProvider() != null
            && server.getProxyAuthProvider().isUnsignedLoginAllowed();
        if (xboxAuthRequired && !result.signed() && !unsignedAllowed) {
            return new ChainOutcome(DisconnectFailReason.NOT_AUTHENTICATED, null, false);
        }
        return new ChainOutcome(null, result.identityClaims(), result.signed());
    }

    /**
     * Runs the checks that read server state, then starts the second off-loop step for a login
     * that passed them.
     */
    private void applyChain(ChainOutcome chain, Credentials credentials, PlayerSessionHolder holder, Server server) {
        if (chain.failure() != null) {
            failLogin(holder, server, chain.failure(), null);
            return;
        }
        final ChainValidationResult.IdentityClaims identityClaims = Objects.requireNonNull(
            chain.identityClaims(), "a chain outcome without a failure always carries identity claims");

        final PlayerPreLoginEvent event = new PlayerPreLoginEvent(identityClaims);
        server.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            failLogin(holder, server, DisconnectFailReason.UNKNOWN, event.getKickMessage());
            return;
        }
        if (server.getOnlinePlayers().size() >= server.getMaxPlayers()) {
            failLogin(holder, server, DisconnectFailReason.SERVER_FULL, null);
            return;
        }

        final String displayName = identityClaims.extraData.displayName.toLowerCase(Locale.ENGLISH);
        if (!server.isWhitelisted(displayName)) {
            failLogin(holder, server, DisconnectFailReason.NOT_ALLOWED, null);
            return;
        }

        final var entry = server.getNameBans().getEntires().get(displayName);
        if (entry != null) {
            final String reason = entry.getReason();
            failLogin(holder, server, DisconnectFailReason.UNKNOWN,
                !reason.isEmpty() ? "You are banned. Reason: " + reason : "You are banned");
            return;
        }

        offLoop(holder, server,
            () -> validateClient(credentials, server, identityClaims),
            client -> completeLogin(client, identityClaims, chain.signed(), holder, server));
    }

    /**
     * Verifies the client JWT and prepares the key exchange. Second off-loop step, reached only
     * once the server has accepted the identity.
     */
    private ClientOutcome validateClient(Credentials credentials, Server server, ChainValidationResult.IdentityClaims identityClaims) throws Exception {
        final ClientJwtValidationResult clientJwt = this.validateClientJwt(credentials.clientJwt(), identityClaims.parsedIdentityPublicKey());
        if (!clientJwt.isValid()) {
            return new ClientOutcome(null, null, null, false);
        }
        EncryptionHandshake handshake = null;
        boolean encryptionFailed = false;
        if (server.enabledNetworkEncryption) {
            try {
                final PublicKey clientKey = EncryptionUtils.parseKey(identityClaims.identityPublicKey);
                final var encryptionKeyPair = EncryptionUtils.createKeyPair();
                final byte[] encryptionToken = EncryptionUtils.generateRandomToken();
                handshake = new EncryptionHandshake(
                    EncryptionUtils.getSecretKey(encryptionKeyPair.getPrivate(), clientKey, encryptionToken),
                    EncryptionUtils.createHandshakeJwt(encryptionKeyPair, encryptionToken)
                );
            } catch (Exception e) {
                log.debug("Failed to prepare the encryption handshake", e);
                encryptionFailed = true;
            }
        }
        return new ClientOutcome(clientJwt.getClientChainData(), clientJwt.getSkin(), handshake, encryptionFailed);
    }

    /**
     * Answers the client: the remaining rejections, then either the encryption handshake or the
     * resource pack stage.
     */
    private void completeLogin(ClientOutcome client, ChainValidationResult.IdentityClaims identityClaims,
                               boolean signed, PlayerSessionHolder holder, Server server) {
        final ClientChainData clientChainData = client.clientChainData();
        if (clientChainData == null) {
            failLogin(holder, server, DisconnectFailReason.INVALID_PLATFORM_SKIN, null);
            return;
        }
        if (clientChainData.isEduMode()) {
            holder.sendPlayStatus(PlayStatus.LOGIN_FAILED_EDITION_MISMATCH_EDU_TO_VANILLA);
            failLogin(holder, server, DisconnectFailReason.EDITION_MISMATCH_EDU_TO_VANILLA, null);
            return;
        }

        holder.getSession().setCodec(NetworkConstants.codecForGameVersion(clientChainData.getGameVersion()));

        holder.setPlayerInfo(new Player.PlayerInfo(identityClaims, clientChainData, client.skin(), signed));

        if (client.encryptionFailed()) {
            holder.disconnect(DisconnectFailReason.UNKNOWN, "encryption error");
            return;
        }
        if (client.encryption() != null) {
            try {
                final ServerToClientHandshakePacket pk = new ServerToClientHandshakePacket();
                pk.setHandshakeWebToken(client.encryption().handshakeJwt());
                holder.getSession().sendPacketImmediately(pk);
                holder.getSession().enableEncryption(client.encryption().secretKey());
            } catch (Exception e) {
                log.debug("Failed to start the encrypted session", e);
                holder.disconnect(DisconnectFailReason.UNKNOWN, "encryption error");
                return;
            }
            holder.setState(SessionState.ENCRYPTION);
        } else {
            holder.sendPlayStatus(PlayStatus.LOGIN_SUCCESS);
            holder.setState(SessionState.RESOURCE_PACK);
            holder.sendResourcePacksInfo(server);
        }
    }

    /**
     * Runs {@code work} on the compute pool and hands its result to {@code then} back on the
     * session's event loop, so everything touching session or server state stays on one thread.
     * A session that went away in the meantime is dropped, and a failing step disconnects rather
     * than leaving the login stuck in {@link SessionState#AUTHENTICATING}.
     */
    private <T> void offLoop(PlayerSessionHolder holder, Server server, ThrowingSupplier<T> work, Consumer<T> then) {
        final EventLoop eventLoop = holder.getSession().getPeer().getChannel().eventLoop();
        server.getComputeThreadPool().execute(() -> {
            final T result;
            try {
                result = work.get();
            } catch (Exception e) {
                log.debug("Error while validating login", e);
                eventLoop.execute(() -> {
                    if (holder.getSession().isConnected()) {
                        failLogin(holder, server, DisconnectFailReason.NOT_AUTHENTICATED, null);
                    }
                });
                return;
            }
            eventLoop.execute(() -> {
                if (holder.getSession().isConnected()) {
                    then.accept(result);
                }
            });
        });
    }

    /**
     * Fires {@link PlayerLoginFailEvent} so plugins may adjust the reason, then disconnects with
     * whatever they left in place.
     *
     * @param message an explicit disconnect message, or {@code null} to use the reason's default
     */
    private void failLogin(PlayerSessionHolder holder, Server server, DisconnectFailReason reason, @Nullable String message) {
        final PlayerLoginFailEvent sessionFailEvent = new PlayerLoginFailEvent(holder, reason);
        if (message != null) {
            sessionFailEvent.setDisconnectMessage(message);
        }
        server.getPluginManager().callEvent(sessionFailEvent);

        if (sessionFailEvent.getDisconnectMessage() != null) {
            holder.disconnect(sessionFailEvent.getDisconnectFailReason(), sessionFailEvent.getDisconnectMessage());
        } else {
            holder.disconnect(sessionFailEvent.getDisconnectFailReason());
        }
    }

    private ClientJwtValidationResult validateClientJwt(String clientJwt, PublicKey identityPublicKey) {
        if (clientJwt == null || clientJwt.isEmpty()) {
            return ClientJwtValidationResult.INVALID;
        }
        try {
            final JwtContext ctx = new JwtConsumerBuilder()
                .setVerificationKey(identityPublicKey)
                .build()
                .process(clientJwt);
            final JwtClaims claims = ctx.getJwtClaims();
            final ClientChainData clientChainData = ClientChainData.from(claims);
            if (clientChainData == null) {
                return ClientJwtValidationResult.INVALID;
            }
            final Skin skin = ClientSkinData.readSkin(claims);
            if (skin == null || !SkinUtils.isValid(skin)) {
                return ClientJwtValidationResult.INVALID;
            }
            return new ClientJwtValidationResult(
                true,
                clientChainData,
                skin
            );
        } catch (InvalidJwtException ignored) {}
        return ClientJwtValidationResult.INVALID;
    }

    @Value
    private static class ClientJwtValidationResult {
        private static final ClientJwtValidationResult INVALID = new ClientJwtValidationResult(false, null, null);

        boolean valid;
        ClientChainData clientChainData;
        Skin skin;
    }

    @FunctionalInterface
    private interface ThrowingSupplier<T> {
        T get() throws Exception;
    }

    /**
     * The parts of the login packet the off-loop steps need, copied out on the network thread so
     * that nothing reads the packet once the pipeline has moved on from it.
     */
    private record Credentials(PlayerAuthenticationType authenticationType, String token, List<String> chain,
                               String clientJwt) {
    }

    /**
     * Result of {@link #validateChain}: either a {@code failure} with the other fields unset, or
     * the verified identity.
     */
    private record ChainOutcome(
        @Nullable DisconnectFailReason failure,
        @Nullable ChainValidationResult.IdentityClaims identityClaims,
        boolean signed
    ) {
    }

    /**
     * Result of {@link #validateClient}.
     *
     * @param clientChainData  {@code null} when the client JWT was rejected
     * @param encryption       the prepared key exchange, or {@code null} when encryption is
     *                         disabled, the JWT was rejected, or preparation failed
     * @param encryptionFailed whether preparing the key exchange threw
     */
    private record ClientOutcome(
        @Nullable ClientChainData clientChainData,
        @Nullable Skin skin,
        @Nullable EncryptionHandshake encryption,
        boolean encryptionFailed
    ) {
    }

    /** Encryption material derived off the event loop, applied once back on it. */
    private record EncryptionHandshake(SecretKey secretKey, String handshakeJwt) {
    }

    @Override
    public boolean requiresPlayer() {
        return false;
    }
}
