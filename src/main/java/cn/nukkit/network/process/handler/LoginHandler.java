package cn.nukkit.network.process.handler;

import cn.nukkit.Server;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.network.connection.BedrockSession;
import cn.nukkit.network.connection.util.EncryptionUtils;
import cn.nukkit.network.process.login.LoginData;
import cn.nukkit.network.process.SessionState;
import cn.nukkit.network.protocol.LoginPacket;
import cn.nukkit.network.protocol.ServerToClientHandshakePacket;
import cn.nukkit.network.protocol.types.InputMode;
import cn.nukkit.network.protocol.types.PlayerInfo;
import cn.nukkit.network.protocol.types.XboxLivePlayerInfo;
import cn.nukkit.utils.ClientChainData;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
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

        LoginData loginDataRaw = LoginData.processHandshake(pk, Server.getInstance().getSettings().baseSettings().xboxAuth());

        ClientChainData loginData = ClientChainData.of(loginDataRaw);

        //TODO: re-implement if possible
        //check the player login time
        /*if (pk.issueUnixTime != -1 && Server.getInstance().checkLoginTime && System.currentTimeMillis() - pk.issueUnixTime > 20000) {
            var message = "disconnectionScreen.noReason";
            log.debug("disconnection due to noReason");
            session.sendPlayStatus(PlayStatusPacket.LOGIN_FAILED_CLIENT, true);
            session.close(message);
            return;
        }*/

        if (!loginData.isXboxAuthed() && server.getSettings().baseSettings().xboxAuth()) {
            log.debug("disconnection due to notAuthenticated");
            session.close("disconnectionScreen.notAuthenticated");
            return;
        }

        if (server.getOnlinePlayers().size() >= server.getMaxPlayers()) {
            log.debug("disconnection due to serverFull");
            session.close("disconnectionScreen.serverFull");
            return;
        }

        //set proxy ip
        if (server.getSettings().baseSettings().waterdogpe() && loginData.getWaterdogIP() != null) {
            InetSocketAddress oldAddress = session.getAddress();
            session.setAddress(new InetSocketAddress(loginData.getWaterdogIP(), session.getAddress().getPort()));
            Server.getInstance().getNetwork().replaceSessionAddress(oldAddress, session.getAddress(), session);
        }

        //Verify if the language is valid
        if(!isValidLanguage(loginData.getLanguageCode())) {
            session.close("§cPacket handling error: lang check failed");
            return;
        }

        //Verify if the GameVersion has valid format
        if(loginData.getGameVersion().split("\\.").length != 3 && !Server.getInstance().getSettings().gameplaySettings().allowBeta()) {
            session.close("§cPacket handling error: no beta allowed");
            return;
        }

        //Verify if the CurrentInputMode is valid
        int CurrentInputMode = loginData.getCurrentInputMode();
        if(
                CurrentInputMode <= InputMode.UNDEFINED.getOrdinal() ||
                        CurrentInputMode >= InputMode.COUNT.getOrdinal()
        ) {
            log.debug("disconnection due to invalid input mode");
            session.close("§cPacket handling error: invalid input mode");
            return;
        }

        //Verify if the DefaultInputMode is valid
        int DefaultInputMode = loginData.getDefaultInputMode();
        if(
                DefaultInputMode <= InputMode.UNDEFINED.getOrdinal() ||
                        DefaultInputMode >= InputMode.COUNT.getOrdinal()
        ) {
            log.debug("disconnection due to invalid input mode");
            session.close("§cPacket handling error: invalid input mode");
            return;
        }

        var uniqueId = loginData.getClientUUID();
        var username = loginData.getUsername();
        Matcher usernameMatcher = playerNamePattern.matcher(username);

        if (
                !usernameMatcher.matches() ||
                        username.equalsIgnoreCase("rcon") ||
                        username.equalsIgnoreCase("console")
        ) {
            log.debug("disconnection due to invalidName");
            session.close("disconnectionScreen.invalidName");
            return;
        }

        if (!loginDataRaw.skin().isValid()) {
            log.debug("disconnection due to invalidSkin");
            session.close("disconnectionScreen.invalidSkin");
            return;
        }

        Skin skin = loginDataRaw.skin();
        if (server.getSettings().playerSettings().forceSkinTrusted()) {
            skin.setTrusted(true);
        }

        var info = new PlayerInfo(
                username,
                uniqueId,
                skin,
                loginData
        );

        if (loginData.isXboxAuthed()) {
            info = new XboxLivePlayerInfo(
                    username,
                    uniqueId,
                    skin,
                    loginData,
                    loginData.getXUID()
            );
        }

        this.consumer.accept(info);
        session.setAuthenticated();

        if (!server.isWhitelisted((info.getUsername()).toLowerCase(Locale.ENGLISH))) {
            log.debug("disconnection due to white-listed");
            session.close("Server is white-listed");
            return;
        }

        var entry = server.getNameBans().getEntires().get(info.getUsername().toLowerCase(Locale.ENGLISH));
        if (entry != null) {
            String reason = entry.getReason();
            log.debug("disconnection due to named ban");
            session.close(!reason.isEmpty() ? "You are banned. Reason: " + reason : "You are banned");
            return;
        }

        if (server.enabledNetworkEncryption) {
            this.enableEncryption(loginData);
        } else {
            session.getMachine().fire(SessionState.RESOURCE_PACK);
        }
    }

    private boolean isValidLanguage(String language) {
        Set<String> languagesCode = new HashSet<>();
        Collections.addAll(languagesCode,
                "fr_CA", "fr_FR",
                "bg_BG", "cs_CZ", "da_DK",
                "de_DE", "el_GR", "en_GB",
                "en_US", "es_ES", "es_MX",
                "fi_FI", "hu_HU", "id_ID",
                "it_IT", "ja_JP", "ko_KR",
                "nb_NO", "nl_NL", "pl_PL",
                "pt_BR", "pt_PT", "ru_RU",
                "sk_SK", "sv_SE", "tr_TR", "uk_UA",
                "zh_CN", "zh_TW"
        );
        return languagesCode.contains(language);
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
