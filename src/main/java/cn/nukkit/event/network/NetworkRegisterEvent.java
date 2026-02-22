package cn.nukkit.event.network;

import cn.nukkit.event.HandlerList;
import cn.nukkit.network.NetworkInterface;
import lombok.Getter;
import lombok.Setter;

/**
 * Triggers before the network class is registered.
 * @author xRookieFight
 * @since 20/02/2026
 */
@Getter
@Setter
public class NetworkRegisterEvent extends NetworkEvent {
    private static final HandlerList handlers = new HandlerList();
    public static HandlerList getHandlers() {
        return handlers;
    }

    private NetworkInterface networkInterface;

    public NetworkRegisterEvent(NetworkInterface networkInterface) {
        this.networkInterface = networkInterface;
    }
}
