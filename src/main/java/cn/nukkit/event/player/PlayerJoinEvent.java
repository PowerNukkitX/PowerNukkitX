package cn.nukkit.event.player;

import cn.nukkit.lang.TextContainer;
import cn.nukkit.player.Player;

public class PlayerJoinEvent extends PlayerEvent {

    protected TextContainer joinMessage;

    public PlayerJoinEvent(Player player, TextContainer joinMessage) {
        this.player = player;
        this.joinMessage = joinMessage;
    }

    public PlayerJoinEvent(Player player, String joinMessage) {
        this.player = player;
        this.joinMessage = new TextContainer(joinMessage);
    }

    public TextContainer getJoinMessage() {
        return joinMessage;
    }

    public void setJoinMessage(TextContainer joinMessage) {
        this.joinMessage = joinMessage;
    }

    public void setJoinMessage(String joinMessage) {
        this.setJoinMessage(new TextContainer(joinMessage));
    }
}
