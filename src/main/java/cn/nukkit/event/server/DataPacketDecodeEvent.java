package cn.nukkit.event.server;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.network.connection.netty.BedrockPacketWrapper;


/**
 * @author MagicDroidX (Nukkit Project)
 */
public class DataPacketDecodeEvent extends ServerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final BedrockPacketWrapper wrapper;
    private final Player player;

    public DataPacketDecodeEvent(Player player, BedrockPacketWrapper wrapper) {
        this.wrapper = wrapper;
        this.player = player;
    }


    public Player getPlayer() {
        return player;
    }

    public int getPacketId()
    {
        return wrapper.getPacketId();
    }

    public String getPacketBuffer()
    {
        return wrapper.getPacketBuffer().toString();
    }

    public BedrockPacketWrapper getPacketWrapper()
    {
        return wrapper;
    }
}
