package cn.nukkit.network.process.handler;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.network.NetworkConstants;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import cn.nukkit.network.process.SessionState;
import cn.nukkit.network.process.auth.ClientChainData;
import cn.nukkit.network.process.auth.ClientSkinData;
import lombok.Value;
import org.cloudburstmc.protocol.bedrock.data.DisconnectFailReason;
import org.cloudburstmc.protocol.bedrock.data.PlayStatus;
import org.cloudburstmc.protocol.bedrock.data.auth.PlayerAuthenticationType;
import org.cloudburstmc.protocol.bedrock.data.skin.SerializedSkin;
import org.cloudburstmc.protocol.bedrock.packet.LoginPacket;
import org.cloudburstmc.protocol.bedrock.packet.ServerToClientHandshakePacket;
import org.cloudburstmc.protocol.bedrock.util.ChainValidationResult;
import org.cloudburstmc.protocol.bedrock.util.EncryptionUtils;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jwt.consumer.JwtContext;
import org.jose4j.lang.JoseException;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Locale;

/**
 * @author Kaooot
 */
public class LoginHandler implements PacketHandler<LoginPacket> {

    @Override
    public void handle(LoginPacket packet, PlayerSessionHolder holder, Server server) {
        if (!holder.getState().equals(SessionState.LOGIN)) {
            holder.disconnect(DisconnectFailReason.UNEXPECTED_PACKET);
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
            holder.disconnect(
                    serverOutdated ? DisconnectFailReason.OUTDATED_SERVER : DisconnectFailReason.OUTDATED_CLIENT
            );
            return;
        }

        final PlayerAuthenticationType type = packet.getAuthenticationType();
        final DisconnectFailReason notAuthenticated = DisconnectFailReason.NOT_AUTHENTICATED;
        if (type.equals(PlayerAuthenticationType.UNKNOWN)) {
            holder.disconnect(notAuthenticated);
            return;
        }

        final boolean xboxAuthRequired = server.getSettings().baseSettings().xboxAuth();
        if (xboxAuthRequired && (type.equals(PlayerAuthenticationType.SELF_SIGNED)) ||
                packet.getToken() == null || packet.getToken().isEmpty() || packet.getChain().size() != 1) {
            holder.disconnect(notAuthenticated);
            return;
        }

        try {
            final ChainValidationResult result = EncryptionUtils.validateToken(type, packet.getToken());
            if (xboxAuthRequired && !result.signed()) {
                holder.disconnect(notAuthenticated);
                return;
            }

            if (server.getOnlinePlayers().size() >= server.getMaxPlayers()) {
                holder.disconnect(DisconnectFailReason.SERVER_FULL);
                return;
            }

            final ChainValidationResult.IdentityClaims identityClaims = result.identityClaims();

            if (!server.isWhitelisted(identityClaims.extraData.displayName.toLowerCase(Locale.ENGLISH))) {
                holder.disconnect(DisconnectFailReason.NOT_ALLOWED);
                return;
            }

            var entry = server.getNameBans().getEntires().get(identityClaims.extraData.displayName.toLowerCase(Locale.ENGLISH));
            if (entry != null) {
                String reason = entry.getReason();
                holder.disconnect(DisconnectFailReason.UNKNOWN, !reason.isEmpty() ? "You are banned. Reason: " + reason : "You are banned");
                return;
            }

            final ClientJwtValidationResult clientJwtValidationResult = this.validateClientJwt(packet, holder, server, identityClaims.parsedIdentityPublicKey());
            if (!clientJwtValidationResult.isValid()) {
                holder.disconnect(DisconnectFailReason.INVALID_PLATFORM_SKIN);
                return;
            }

            final ClientChainData clientChainData = clientJwtValidationResult.getClientChainData();
            if (clientChainData.isEduMode()) {
                holder.sendPlayStatus(PlayStatus.LOGIN_FAILED_EDITION_MISMATCH_EDU_TO_VANILLA);
                holder.disconnect(DisconnectFailReason.EDITION_MISMATCH_EDU_TO_VANILLA);
                return;
            }
            if (clientChainData.isEditorMode()) {
                holder.sendPlayStatus(PlayStatus.LOGIN_FAILED_EDITOR_MISMATCH_EDITOR_TO_VANILLA);
                holder.disconnect(DisconnectFailReason.EDITION_MISMATCH_EDITOR_TO_VANILLA);
                return;
            }
            holder.setPlayerInfo(
                    new Player.PlayerInfo(
                            identityClaims,
                            clientChainData,
                            clientJwtValidationResult.getSkin()
                    )
            );

            if (server.enabledNetworkEncryption) {
                this.enableEncryption(identityClaims, holder);
            } else {
                holder.sendPlayStatus(PlayStatus.LOGIN_SUCCESS);
                holder.setState(SessionState.RESOURCE_PACK);
                holder.sendResourcePacksInfo(server);
            }
            // TODO protocol wdpe
        } catch (InvalidJwtException | JoseException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            holder.disconnect(notAuthenticated);
            return;
        }
    }

    private ClientJwtValidationResult validateClientJwt(LoginPacket packet, PlayerSessionHolder holder, Server server, PublicKey identityPublicKey) {
        final String clientJwt = packet.getClientJwt();
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
            final SerializedSkin skin = ClientSkinData.readSkin(claims);
            if (skin == null) {
                return ClientJwtValidationResult.INVALID;
            }
            return new ClientJwtValidationResult(
                    true,
                    clientChainData,
                    skin
            );
        } catch (InvalidJwtException ignored) {

        }
        return ClientJwtValidationResult.INVALID;
    }

    @Value
    private static class ClientJwtValidationResult {
        private static final ClientJwtValidationResult INVALID = new ClientJwtValidationResult(false, null, null);

        boolean valid;
        ClientChainData clientChainData;
        SerializedSkin skin;
    }

    private void enableEncryption(ChainValidationResult.IdentityClaims claims, PlayerSessionHolder holder) {
        try {
            var session = holder.getSession();
            var clientKey = EncryptionUtils.parseKey(claims.identityPublicKey);
            var encryptionKeyPair = EncryptionUtils.createKeyPair();
            var encryptionToken = EncryptionUtils.generateRandomToken();
            var encryptionKey = EncryptionUtils.getSecretKey(
                    encryptionKeyPair.getPrivate(), clientKey,
                    encryptionToken
            );
            var handshakeWebToken = EncryptionUtils.createHandshakeJwt(encryptionKeyPair, encryptionToken);
            if (!holder.getSession().isConnected()) {
                return;
            }
            var pk = new ServerToClientHandshakePacket();
            pk.setHandshakeWebToken(handshakeWebToken);

            session.sendPacketImmediately(pk);
            session.enableEncryption(encryptionKey);

            holder.setState(SessionState.ENCRYPTION);
        } catch (Exception e) {
            holder.disconnect(DisconnectFailReason.UNKNOWN, "encryption error");
        }
    }
}