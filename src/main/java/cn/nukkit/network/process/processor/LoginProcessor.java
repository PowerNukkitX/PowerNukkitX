package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.entity.data.StringEntityData;
import cn.nukkit.event.player.PlayerAsyncPreLoginEvent;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.event.player.PlayerPreLoginEvent;
import cn.nukkit.network.encryption.PrepareEncryptionTask;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.LoginPacket;
import cn.nukkit.network.protocol.PlayStatusPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.ServerToClientHandshakePacket;
import cn.nukkit.plugin.InternalPlugin;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.ClientChainData;
import cn.nukkit.utils.TextFormat;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginProcessor extends DataPacketProcessor<LoginPacket> {
    /**
     * Regular expression for validating player name. Allows only: Number nicknames, letter nicknames, number and letters nicknames, nicknames with underscores, nicknames with space in the middle
     */
    private static final Pattern playerNamePattern = Pattern.compile("^(?! )([a-zA-Z0-9_ ]{2,15}[a-zA-Z0-9_])(?<! )$");

    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull LoginPacket pk) {
        Server server = playerHandle.player.getServer();
        if (playerHandle.player.loggedIn) {
            return;
        }

        //check the player login time
        if (pk.issueUnixTime != -1 && Server.getInstance().checkLoginTime && System.currentTimeMillis() - pk.issueUnixTime > 20000) {
            var message = "disconnectionScreen.noReason";
            playerHandle.sendPlayStatus(PlayStatusPacket.LOGIN_FAILED_SERVER, true);
            playerHandle.player.close("", message, false);
            return;
        }

        //set user name
        playerHandle.setUsername(TextFormat.clean(pk.username));
        playerHandle.setDisplayName(playerHandle.getUsername());
        playerHandle.setIusername(playerHandle.getUsername().toLowerCase());

        //set user name data flag
        playerHandle.player.setDataProperty(new StringEntityData(Entity.DATA_NAMETAG, playerHandle.getUsername()), false);

        //set login chain data of player
        playerHandle.setLoginChainData(ClientChainData.read(pk));

        //verify the player if enable the xbox-auth
        if (!playerHandle.getLoginChainData().isXboxAuthed() && server.getPropertyBoolean("xbox-auth")) {
            playerHandle.player.close("", "disconnectionScreen.notAuthenticated");
            return;
        }

        //Verify the number of server player
        if (server.getOnlinePlayers().size() >= server.getMaxPlayers()
                && playerHandle.player.kick(PlayerKickEvent.Reason.SERVER_FULL, "disconnectionScreen.serverFull", false)) {
            return;
        }

        //set proxy ip
        if (server.isWaterdogCapable() && playerHandle.getLoginChainData().getWaterdogIP() != null) {
            playerHandle.setSocketAddress(new InetSocketAddress(playerHandle.getLoginChainData().getWaterdogIP(), playerHandle.player.getRawPort()));
        }

        playerHandle.setRandomClientId(pk.clientId);
        playerHandle.player.setUniqueId(pk.clientUUID);
        playerHandle.player.setRawUniqueId(Binary.writeUUID(playerHandle.player.getUniqueId()));

        boolean valid = true;
        Matcher usernameMatcher = playerNamePattern.matcher(pk.username);
        if (!usernameMatcher.matches()) {
            valid = false;
        }

        if (!valid || Objects.equals(playerHandle.getIusername(), "rcon")
                || Objects.equals(playerHandle.getIusername(), "console")) {
            playerHandle.player.close("", "disconnectionScreen.invalidName");
            return;
        }

        if (!pk.skin.isValid()) {
            playerHandle.player.close("", "disconnectionScreen.invalidSkin");
            return;
        } else {
            Skin skin = pk.skin;
            if (server.isForceSkinTrusted()) {
                skin.setTrusted(true);
            }
            playerHandle.player.setSkin(skin);
        }

        PlayerPreLoginEvent playerPreLoginEvent;
        server.getPluginManager().callEvent(playerPreLoginEvent = new PlayerPreLoginEvent(playerHandle.player, "Plugin reason"));
        if (playerPreLoginEvent.isCancelled()) {
            playerHandle.player.close("", playerPreLoginEvent.getKickMessage());
            return;
        }
        playerHandle.setVerified(true);
        playerHandle.setPreLoginEventTask(new AsyncTask() {
            private PlayerAsyncPreLoginEvent event;

            @Override
            public void onRun() {
                event = new PlayerAsyncPreLoginEvent(playerHandle.getUsername(), playerHandle.player.getUniqueId(), playerHandle.getLoginChainData(), playerHandle.player.getSkin(), playerHandle.player.getRawAddress(), playerHandle.player.getRawPort());
                server.getPluginManager().callEvent(event);
            }

            @Override
            public void onCompletion(Server server) {
                if (playerHandle.player.closed) {
                    return;
                }

                if (event.getLoginResult() == PlayerAsyncPreLoginEvent.LoginResult.KICK) {
                    playerHandle.player.close(event.getKickMessage(), event.getKickMessage());
                } else if (playerHandle.isShouldLogin()) {
                    playerHandle.player.setSkin(event.getSkin());
                    playerHandle.completeLoginSequence();
                    for (Consumer<Server> action : event.getScheduledActions()) {
                        action.accept(server);
                    }
                }
            }
        });

        server.getScheduler().scheduleAsyncTask(InternalPlugin.INSTANCE, playerHandle.getPreLoginEventTask());
        if (server.enabledNetworkEncryption && playerHandle.getLoginChainData().isXboxAuthed()) {
            server.getScheduler().scheduleAsyncTask(InternalPlugin.INSTANCE, new PrepareEncryptionTask(playerHandle.player) {
                @Override
                public void onCompletion(Server server) {
                    if (!playerHandle.player.isConnected()) {
                        return;
                    }
                    if (this.getHandshakeJwt() == null || this.getEncryptionKey() == null || this.getEncryptionCipher() == null || this.getDecryptionCipher() == null) {
                        playerHandle.player.close("", "Network Encryption error");
                        return;
                    }
                    ServerToClientHandshakePacket pk = new ServerToClientHandshakePacket();
                    pk.setJwt(this.getHandshakeJwt());
                    playerHandle.player.forceDataPacket(pk, () -> {
                        playerHandle.getNetworkSession().setEncryption(this.getEncryptionKey(), this.getEncryptionCipher(), this.getDecryptionCipher());
                    });
                }
            });
        } else {
            playerHandle.processLogin();
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.toNewProtocolID(ProtocolInfo.LOGIN_PACKET);
    }
}
