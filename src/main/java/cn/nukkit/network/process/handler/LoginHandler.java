package cn.nukkit.network.process.handler;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.entity.data.StringEntityData;
import cn.nukkit.event.player.PlayerAsyncPreLoginEvent;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.event.player.PlayerPreLoginEvent;
import cn.nukkit.network.connection.util.PrepareEncryptionTask;
import cn.nukkit.network.process.NetworkSession;
import cn.nukkit.network.process.NetworkSessionState;
import cn.nukkit.network.protocol.LoginPacket;
import cn.nukkit.network.protocol.PlayStatusPacket;
import cn.nukkit.network.protocol.ServerToClientHandshakePacket;
import cn.nukkit.plugin.InternalPlugin;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.ClientChainData;
import cn.nukkit.utils.TextFormat;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginHandler extends NetworkSessionPacketHandler {
    public LoginHandler(NetworkSession session) {
        super(session);
    }

    private static final Pattern playerNamePattern = Pattern.compile("^(?! )([a-zA-Z0-9_ ]{2,15}[a-zA-Z0-9_])(?<! )$");

    @Override
    public void handle(LoginPacket pk) {
        var server = player.getServer();
        /*if (handle.player.loggedIn) {
            return;
        }*/
        //check the player login time
        if (pk.issueUnixTime != -1 && Server.getInstance().checkLoginTime && System.currentTimeMillis() - pk.issueUnixTime > 20000) {
            var message = "disconnectionScreen.noReason";
            session.sendPlayStatus(PlayStatusPacket.LOGIN_FAILED_CLIENT, true);
            player.close("", message, false);
            return;
        }

        //set user name
        handle.setUsername(TextFormat.clean(pk.username));
        handle.setDisplayName(handle.getUsername());
        handle.setIusername(handle.getUsername().toLowerCase());

        //set user name data flag
        player.setDataProperty(new StringEntityData(Entity.DATA_NAMETAG, handle.getUsername()), false);

        //set login chain data of player
        handle.setLoginChainData(ClientChainData.read(pk));

        //verify the player if enable the xbox-auth
        if (!handle.getLoginChainData().isXboxAuthed() && server.getPropertyBoolean("xbox-auth")) {
            player.close("", "disconnectionScreen.notAuthenticated");
            return;
        }

        //Verify the number of server player
        if (server.getOnlinePlayers().size() >= server.getMaxPlayers()
                && player.kick(PlayerKickEvent.Reason.SERVER_FULL, "disconnectionScreen.serverFull", false)) {
            return;
        }

        //set proxy ip
        if (server.isWaterdogCapable() && handle.getLoginChainData().getWaterdogIP() != null) {
            handle.setSocketAddress(new InetSocketAddress(handle.getLoginChainData().getWaterdogIP(), player.getRawPort()));
        }

        player.setUniqueId(pk.clientUUID);
        player.setRawUniqueId(Binary.writeUUID(player.getUniqueId()));

        boolean valid = true;
        Matcher usernameMatcher = playerNamePattern.matcher(pk.username);
        if (!usernameMatcher.matches()) {
            valid = false;
        }

        if (!valid || Objects.equals(handle.getIusername(), "rcon")
                || Objects.equals(handle.getIusername(), "console")) {
            player.close("", "disconnectionScreen.invalidName");
            return;
        }

        if (!pk.skin.isValid()) {
            player.close("", "disconnectionScreen.invalidSkin");
            return;
        } else {
            Skin skin = pk.skin;
            if (server.isForceSkinTrusted()) {
                skin.setTrusted(true);
            }
            player.setSkin(skin);
        }

        PlayerPreLoginEvent playerPreLoginEvent;
        server.getPluginManager().callEvent(playerPreLoginEvent = new PlayerPreLoginEvent(player, "Plugin reason"));
        if (playerPreLoginEvent.isCancelled()) {
            player.close("", playerPreLoginEvent.getKickMessage());
            return;
        }
        handle.setVerified(true);
        handle.setPreLoginEventTask(new AsyncTask() {
            private PlayerAsyncPreLoginEvent event;

            @Override
            public void onRun() {
                event = new PlayerAsyncPreLoginEvent(handle.getUsername(), player.getUniqueId(), handle.getLoginChainData(), player.getSkin(), player.getRawAddress(), player.getRawPort());
                server.getPluginManager().callEvent(event);
            }

            @Override
            public void onCompletion(Server server) {
                if (player.closed) {
                    return;
                }

                if (event.getLoginResult() == PlayerAsyncPreLoginEvent.LoginResult.KICK) {
                    player.close(event.getKickMessage(), event.getKickMessage());
                } else if (handle.isShouldLogin()) {
                    player.setSkin(event.getSkin());
                    for (Consumer<Server> action : event.getScheduledActions()) {
                        action.accept(server);
                    }
                }
            }
        });

        server.getScheduler().scheduleAsyncTask(InternalPlugin.INSTANCE, handle.getPreLoginEventTask());

        if (server.enabledNetworkEncryption) {
            this.enableEncryption();
        } else {
            session.getMachine().fire(NetworkSessionState.RESOURCE_PACK);
        }
    }

    private void enableEncryption() {
        Server.getInstance().getScheduler().scheduleAsyncTask(InternalPlugin.INSTANCE, new PrepareEncryptionTask(player) {
            @Override
            public void onCompletion(Server server) {
                if (!player.isConnected()) {
                    return;
                }
                if (this.getHandshakeJwt() == null || this.getEncryptionKey() == null) {
                    player.close("", "Network Encryption error");
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
