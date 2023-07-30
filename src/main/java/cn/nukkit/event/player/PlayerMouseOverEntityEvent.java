package cn.nukkit.event.player;

import cn.nukkit.entity.Entity;
import cn.nukkit.player.Player;

public class PlayerMouseOverEntityEvent extends PlayerEvent {

    private final Entity entity;

    public PlayerMouseOverEntityEvent(Player player, Entity entity) {
        this.player = player;
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}
