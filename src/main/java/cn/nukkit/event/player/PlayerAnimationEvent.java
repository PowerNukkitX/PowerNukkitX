package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import org.cloudburstmc.protocol.bedrock.data.ActorSwingSource;
import org.cloudburstmc.protocol.bedrock.packet.AnimatePacket;

public class PlayerAnimationEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final AnimatePacket.Action animationType;
    private final float data;
    private final ActorSwingSource swingSource;

    public PlayerAnimationEvent(Player player, AnimatePacket animatePacket) {
        this.player = player;
        animationType = animatePacket.getAction();
        data = animatePacket.getData();
        swingSource = animatePacket.getSwingSource();
    }

    public PlayerAnimationEvent(Player player) {
        this(player, AnimatePacket.Action.SWING);
    }

    public PlayerAnimationEvent(Player player, AnimatePacket.Action animation) {
        this.player = player;
        this.animationType = animation;
        this.swingSource = ActorSwingSource.NONE;
        data = 0;
    }

    public AnimatePacket.Action getAnimationType() {
        return this.animationType;
    }

    public float getData() {
        return data;
    }

    public ActorSwingSource getSwingSource() {
        return swingSource;
    }
}
