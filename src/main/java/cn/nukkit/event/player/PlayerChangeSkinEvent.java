package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import org.cloudburstmc.protocol.bedrock.data.skin.SerializedSkin;

/**
 * @author KCodeYT (Nukkit Project)
 */
public class PlayerChangeSkinEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final SerializedSkin skin;

    public PlayerChangeSkinEvent(Player player, SerializedSkin skin) {
        this.player = player;
        this.skin = skin;
    }

    public SerializedSkin getSkin() {
        return this.skin;
    }

}
