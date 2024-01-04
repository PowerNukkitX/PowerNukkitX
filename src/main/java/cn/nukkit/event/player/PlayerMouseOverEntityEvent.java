package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.HandlerList;
import lombok.Getter;

public class PlayerMouseOverEntityEvent extends PlayerEvent {
    @Getter
    private static final HandlerList handlers = new HandlerList();

    private final Entity entity;

    public PlayerMouseOverEntityEvent(Player player, Entity entity) {
        this.player = player;
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}
