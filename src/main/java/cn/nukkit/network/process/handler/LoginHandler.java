package cn.nukkit.network.process.handler;

import cn.nukkit.Server;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.network.connection.util.PrepareEncryptionTask;
import cn.nukkit.network.process.NetworkSession;
import cn.nukkit.network.process.NetworkSessionState;
import cn.nukkit.network.protocol.LoginPacket;
import cn.nukkit.network.protocol.PlayStatusPacket;
import cn.nukkit.network.protocol.ServerToClientHandshakePacket;
import cn.nukkit.player.info.PlayerInfo;
import cn.nukkit.player.info.XboxLivePlayerInfo;
import cn.nukkit.plugin.InternalPlugin;
import cn.nukkit.utils.ClientChainData;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginHandler extends NetworkSessionPacketHandler {

    private final Consumer<PlayerInfo> consumer;

    public LoginHandler(NetworkSession session, Consumer<PlayerInfo> consumer) {
        super(session);
        this.consumer = consumer;
    }

    private static final Pattern playerNamePattern = Pattern.compile("^(?! )([a-zA-Z0-9_ ]{2,15}[a-zA-Z0-9_])(?<! )$");

    @Override
    public void handle(LoginPacket pk) {
        var server = this.session.getServer();
        /*if (handle.player.loggedIn) {
            return;
        }*/
        //check the player login time
        if (pk.issueUnixTime != -1 && Server.getInstance().checkLoginTime && System.currentTimeMillis() - pk.issueUnixTime > 20000) {
            var message = "disconnectionScreen.noReason";
            session.sendPlayStatus(PlayStatusPacket.LOGIN_FAILED_CLIENT, true);
            session.disconnect(message);
            return;
        }

        var chainData = ClientChainData.read(pk);

        //verify the player if enable the xbox-auth
        if (!chainData.isXboxAuthed() && server.getPropertyBoolean("xbox-auth")) {
            session.disconnect("disconnectionScreen.notAuthenticated");
            return;
        }

        //Verify the number of server player
        if (server.getOnlinePlayers().size() >= server.getMaxPlayers()) {
            session.disconnect("disconnectionScreen.serverFull");
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
            session.disconnect("disconnectionScreen.invalidName");
            return;
        }

        if (!pk.skin.isValid()) {
            session.disconnect("disconnectionScreen.invalidSkin");
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
            session.disconnect("Server is white-listed");
            return;
        }

        var entry = server.getNameBans().getEntires().get(info.getUsername().toLowerCase());
        if (entry != null) {
            String reason = entry.getReason();
            session.disconnect(!reason.isEmpty() ? "You are banned. Reason: " + reason : "You are banned");
            return;
        }

        if (server.enabledNetworkEncryption) {
            this.enableEncryption(chainData);
        } else {
            session.getMachine().fire(NetworkSessionState.RESOURCE_PACK);
        }
    }

    private void enableEncryption(ClientChainData data) {
        Server.getInstance().getScheduler().scheduleAsyncTask(InternalPlugin.INSTANCE, new PrepareEncryptionTask(data) {
            @Override
            public void onCompletion(Server server) {
                if (session.isDisconnected()) {
                    return;
                }
                if (this.getHandshakeJwt() == null || this.getEncryptionKey() == null) {
                    session.disconnect("Network Encryption error");
                    return;
                }
                ServerToClientHandshakePacket pk = new ServerToClientHandshakePacket();
                pk.setJwt(this.getHandshakeJwt());
                session.sendPacketImmediately(pk);
                session.getSession().enableEncryption(this.getEncryptionKey());

                session.getMachine().fire(NetworkSessionState.ENCRYPTION);
            }
        });
    }
}
