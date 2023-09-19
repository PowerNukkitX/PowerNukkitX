package cn.nukkit.event.player;

import cn.nukkit.event.Cancellable;
import cn.nukkit.player.Player;
import lombok.Getter;
import lombok.Setter;

/**
 * Called when a player attempts to be transferred to another server
 */
@Getter
@Setter
public class PlayerTransferEvent extends PlayerEvent implements Cancellable {

    private String address;
    private int port;

    public PlayerTransferEvent(Player player, String address, int port) {
        this.player = player;
        this.address = address;
        this.port = port;
    }
}
