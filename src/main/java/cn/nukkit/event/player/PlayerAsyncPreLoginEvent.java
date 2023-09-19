package cn.nukkit.event.player;

import cn.nukkit.Server;
import cn.nukkit.api.Since;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.player.Player;
import cn.nukkit.player.PlayerInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * This event is called asynchronously
 *
 * @author CreeperFace
 */
public class PlayerAsyncPreLoginEvent extends PlayerEvent {

    private final String name;
    private final UUID uuid;
    private final PlayerInfo playerInfo;
    private Skin skin;
    private final String address;
    private final int port;

    private LoginResult loginResult = LoginResult.SUCCESS;
    private String kickMessage = "Plugin Reason";

    private final List<Consumer<Server>> scheduledActions = new ArrayList<>();

    public PlayerAsyncPreLoginEvent(String name, UUID uuid, PlayerInfo chainData, Skin skin, String address, int port) {
        this.name = name;
        this.uuid = uuid;
        this.playerInfo = chainData;
        this.skin = skin;
        this.address = address;
        this.port = port;
    }

    @Override
    public Player getPlayer() {
        throw new UnsupportedOperationException("Could not get player instance in an async event");
    }

    public String getName() {
        return this.name;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    @Since("1.4.0.0-PN")
    public PlayerInfo getPlayerInfo() {
        return this.playerInfo;
    }

    @Since("1.4.0.0-PN")
    public String getXuid() {
        return playerInfo.getXuid();
    }

    public Skin getSkin() {
        return this.skin;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
    }

    public String getAddress() {
        return this.address;
    }

    public int getPort() {
        return this.port;
    }

    public LoginResult getLoginResult() {
        return this.loginResult;
    }

    public void setLoginResult(LoginResult loginResult) {
        this.loginResult = loginResult;
    }

    public String getKickMessage() {
        return kickMessage;
    }

    public void setKickMessage(String kickMessage) {
        this.kickMessage = kickMessage;
    }

    public void scheduleSyncAction(Consumer<Server> action) {
        this.scheduledActions.add(action);
    }

    public List<Consumer<Server>> getScheduledActions() {
        return new ArrayList<>(scheduledActions);
    }

    public void allow() {
        this.loginResult = LoginResult.SUCCESS;
    }

    public void disAllow(String message) {
        this.loginResult = LoginResult.KICK;
        this.kickMessage = message;
    }

    public enum LoginResult {
        SUCCESS,
        KICK
    }
}
