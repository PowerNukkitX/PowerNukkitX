package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

public class PlayerCreationEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    @Setter
    private Class<? extends Player> playerClass;

    @Getter
    @Nullable
    private final Player.PlayerInfo playerInfo;

    public PlayerCreationEvent(Class<? extends Player> playerClass) {
        this(playerClass, null);
    }

    public PlayerCreationEvent(Class<? extends Player> playerClass, @Nullable Player.PlayerInfo playerInfo) {
        this.playerClass = playerClass;
        this.playerInfo = playerInfo;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }
}
