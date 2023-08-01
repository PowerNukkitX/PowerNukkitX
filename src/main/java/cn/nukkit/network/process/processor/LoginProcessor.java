package cn.nukkit.network.process.processor;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
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
import cn.nukkit.player.Player;
import cn.nukkit.player.PlayerHandle;
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
import org.jetbrains.annotations.NotNull;

public class LoginProcessor extends DataPacketProcessor<LoginPacket> {

    /**
     * Regular expression for validating player name. Allows only: Number nicknames, letter nicknames, number and letters nicknames, nicknames with underscores, nicknames with space in the middle
     */
    @PowerNukkitXOnly
    @Since("1.19.70-r3")
    private static final Pattern playerNamePattern = Pattern.compile("^(?! )([a-zA-Z0-9_ ]{2,15}[a-zA-Z0-9_])(?<! )$");

    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull LoginPacket pk) {
        Player player = playerHandle.getPlayer();
        Server server = player.getServer();
        if (player.isLoggedIn()) {
            return;
        }

        // check the player login time
        if (pk.issueUnixTime != -1
                && Server.getInstance().checkLoginTime
                && System.currentTimeMillis() - pk.issueUnixTime > 20000) {
            var message = "disconnectionScreen.noReason";
            playerHandle.sendPlayStatus(PlayStatusPacket.LOGIN_FAILED_SERVER, true);
            player.close("", message, false);
            return;
        }

        // set user name
        playerHandle.setUsername(TextFormat.clean(pk.username));
        playerHandle.setDisplayName(playerHandle.getUsername());

        // set user name data flag
        player.setDataProperty(new StringEntityData(Entity.DATA_NAMETAG, playerHandle.getUsername()), false);

        // set login chain data of player
        playerHandle.setLoginChainData(ClientChainData.read(pk));

        // verify the player if enable the xbox-auth
        if (!playerHandle.getLoginChainData().isXboxAuthed() && server.getPropertyBoolean("xbox-auth")) {
            player.close("", "disconnectionScreen.notAuthenticated");
            return;
        }

        // Verify the number of server player
        if (server.getPlayerManager().getOnlinePlayers().size() >= server.getMaxPlayers()
                && player.kick(PlayerKickEvent.Reason.SERVER_FULL, "disconnectionScreen.serverFull", false)) {
            return;
        }

        // set proxy ip
        if (server.isWaterdogCapable() && playerHandle.getLoginChainData().getWaterdogIP() != null) {
            playerHandle.setSocketAddress(new InetSocketAddress(
                    playerHandle.getLoginChainData().getWaterdogIP(),
                    player.getPlayerConnection().getRawPort()));
        }

        player.setUniqueId(pk.clientUUID);
        player.setRawUniqueId(Binary.writeUUID(player.getUniqueId()));

        boolean valid = true;
        Matcher usernameMatcher = playerNamePattern.matcher(pk.username);
        if (!usernameMatcher.matches()) {
            valid = false;
        }

        if (!valid
                || Objects.equals(playerHandle.getUsername().toLowerCase(), "rcon")
                || Objects.equals(playerHandle.getUsername().toLowerCase(), "console")) {
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

        PlayerPreLoginEvent playerPreLoginEvent = new PlayerPreLoginEvent(player, "Plugin reason");
        playerPreLoginEvent.call();

        if (playerPreLoginEvent.isCancelled()) {
            player.close("", playerPreLoginEvent.getKickMessage());
            return;
        }
        playerHandle.setVerified(true);
        playerHandle.setPreLoginEventTask(new AsyncTask() {
            private PlayerAsyncPreLoginEvent event;

            @Override
            public void onRun() {
                event = new PlayerAsyncPreLoginEvent(
                        playerHandle.getUsername(),
                        player.getUniqueId(),
                        playerHandle.getLoginChainData(),
                        player.getSkin(),
                        player.getPlayerConnection().getRawAddress(),
                        player.getPlayerConnection().getRawPort());
                event.call();
            }

            @Override
            public void onCompletion(Server server) {
                if (player.closed) {
                    return;
                }

                if (event.getLoginResult() == PlayerAsyncPreLoginEvent.LoginResult.KICK) {
                    player.close(event.getKickMessage(), event.getKickMessage());
                } else if (playerHandle.isShouldLogin()) {
                    player.setSkin(event.getSkin());
                    playerHandle.onCompleteLoginSequence();
                    for (Consumer<Server> action : event.getScheduledActions()) {
                        action.accept(server);
                    }
                }
            }
        });

        server.getScheduler().scheduleAsyncTask(InternalPlugin.INSTANCE, playerHandle.getPreLoginEventTask());
        if (server.enabledNetworkEncryption && playerHandle.getLoginChainData().isXboxAuthed()) {
            server.getScheduler().scheduleAsyncTask(InternalPlugin.INSTANCE, new PrepareEncryptionTask(player) {
                @Override
                public void onCompletion(Server server) {
                    if (!player.isConnected()) {
                        return;
                    }
                    if (this.getHandshakeJwt() == null
                            || this.getEncryptionKey() == null
                            || this.getEncryptionCipher() == null
                            || this.getDecryptionCipher() == null) {
                        player.close("", "Network Encryption error");
                        return;
                    }
                    ServerToClientHandshakePacket pk = new ServerToClientHandshakePacket();
                    pk.setJwt(this.getHandshakeJwt());
                    player.sendPacketImmediately(pk, () -> {
                        playerHandle
                                .getNetworkSession()
                                .setEncryption(
                                        this.getEncryptionKey(),
                                        this.getEncryptionCipher(),
                                        this.getDecryptionCipher());
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
