package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;
import lombok.Getter;

public class PlayerJumpEvent extends PlayerEvent {
    @Getter
    private static final HandlerList handlers = new HandlerList();

    public PlayerJumpEvent(Player player){
        this.player = player;
    }
}
