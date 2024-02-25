package cn.nukkit.network.process.handler;

import cn.nukkit.Server;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.network.connection.BedrockSession;
import cn.nukkit.network.connection.util.EncryptionUtils;
import cn.nukkit.network.process.SessionState;
import cn.nukkit.network.protocol.LoginPacket;
import cn.nukkit.network.protocol.PlayStatusPacket;
import cn.nukkit.network.protocol.ServerToClientHandshakePacket;
import cn.nukkit.player.info.PlayerInfo;
import cn.nukkit.player.info.XboxLivePlayerInfo;
import cn.nukkit.utils.ClientChainData;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class LoginHandler extends BedrockSessionPacketHandler {

    private final Consumer<PlayerInfo> consumer;

    public LoginHandler(BedrockSession session, Consumer<PlayerInfo> consumer) {
        super(session);
        this.consumer = consumer;
    }

    private static final Pattern playerNamePattern = Pattern.compile("^(?! )([a-zA-Z0-9_ ]{2,15}[a-zA-Z0-9_])(?<! )$");

    @Override
    @SneakyThrows
    public void handle(LoginPacket pk) {
        var server = this.session.getServer();

        //check the player login time
        if (pk.issueUnixTime != -1 && Server.getInstance().checkLoginTime && System.currentTimeMillis() - pk.issueUnixTime > 20000) {
            var message = "disconnectionScreen.noReason";
            log.debug("disconnection due to noReason");
            session.sendPlayStatus(PlayStatusPacket.LOGIN_FAILED_CLIENT, true);
            session.close(message);
            return;
        }

        var chainData = ClientChainData.read(pk);

        //verify the player if enable the xbox-auth
        if (!chainData.isXboxAuthed() && server.getPropertyBoolean("xbox-auth")) {
            log.debug("disconnection due to notAuthenticated");
            session.close("disconnectionScreen.notAuthenticated");
            return;
        }

        //Verify the number of server player
        if (server.getOnlinePlayers().size() >= server.getMaxPlayers()) {
            log.debug("disconnection due to serverFull");
            session.close("disconnectionScreen.serverFull");
            return;
        }

        //set proxy ip
        if (server.isWaterdogCapable() && chainData.getWaterdogIP() != null) {
            session.setAddress(new InetSocketAddress(chainData.getWaterdogIP(), session.getAddress().getPort()));
        }

        var uniqueId = pk.clientUUID;

        var username = pk.username;
        Matcher usernameMatcher = playerNamePattern.matcher(username);

        if (!usernameMatcher.matches() || Objects.equals(username, "rcon")
                || Objects.equals(username, "console")) {
            log.debug("disconnection due to invalidName");
            session.close("disconnectionScreen.invalidName");
            return;
        }

        if (!pk.skin.isValid()) {
            log.debug("disconnection due to invalidSkin");
            session.close("disconnectionScreen.invalidSkin");
            return;
        }

        Skin skin = pk.skin;
        if (server.isForceSkinTrusted()) {
            skin.setTrusted(true);
        }

        var info = new PlayerInfo(
                username,
                uniqueId,
                skin,
                chainData
        );

        if (chainData.isXboxAuthed()) {
            info = new XboxLivePlayerInfo(
                    username,
                    uniqueId,
                    skin,
                    chainData,
                    chainData.getXUID()
            );
        }

        this.consumer.accept(info);

        if (!server.isWhitelisted((info.getUsername()).toLowerCase())) {
            log.debug("disconnection due to white-listed");
            session.close("Server is white-listed");
            return;
        }

        var entry = server.getNameBans().getEntires().get(info.getUsername().toLowerCase());
        if (entry != null) {
            String reason = entry.getReason();
            log.debug("disconnection due to named ban");
            session.close(!reason.isEmpty() ? "You are banned. Reason: " + reason : "You are banned");
            return;
        }

        if (server.enabledNetworkEncryption) {
            this.enableEncryption(chainData);
        } else {
            session.getMachine().fire(SessionState.RESOURCE_PACK);
        }
    }

    private void enableEncryption(ClientChainData data) {
        try {
            var clientKey = EncryptionUtils.parseKey(data.getIdentityPublicKey());
            var encryptionKeyPair = EncryptionUtils.createKeyPair();
            var encryptionToken = EncryptionUtils.generateRandomToken();
            var encryptionKey = EncryptionUtils.getSecretKey(
                    encryptionKeyPair.getPrivate(), clientKey,
                    encryptionToken
            );
            var handshakeJwt = EncryptionUtils.createHandshakeJwt(encryptionKeyPair, encryptionToken);
            // WTF
            if (session.isDisconnected()) {
                return;
            }
            var pk = new ServerToClientHandshakePacket();
            pk.setJwt(handshakeJwt);
            session.sendPacketImmediately(pk);
            session.enableEncryption(encryptionKey);

            session.getMachine().fire(SessionState.ENCRYPTION);
        } catch (Exception e) {
            log.error("Failed to prepare encryption", e);
            session.close("encryption error");
        }
    }
}
